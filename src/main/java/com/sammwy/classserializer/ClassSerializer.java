package com.sammwy.classserializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * A flexible class serializer that converts Java objects to maps and vice
 * versa.
 * Supports custom serializers, field filtering, and flat/deflate serialization
 * modes.
 */
public class ClassSerializer {
    /**
     * A predicate function that determines if a field should be serialized and
     * provides its serialization name.
     * If the function returns null, the field will not be serialized.
     */
    private BiFunction<Field, Object, String> fieldPredicate = (field, obj) -> field.getName();

    /**
     * A map of custom serializers for specific types.
     */
    private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    /**
     * A predicate that determines if a class can be serialized recursively.
     */
    private Predicate<Class<?>> classPredicate = cls -> true;

    /**
     * The separator used for keys in deflate mode.
     */
    private String deflateSeparator = null;

    /**
     * Initializes a new ClassSerializer instance.
     */
    public ClassSerializer() {
    }

    /**
     * Sets the field predicate function.
     *
     * @param predicate A function that takes a Field and returns the name to
     *                  use for serialization,
     *                  or null if the field should not be serialized.
     * @return This serializer instance for chaining.
     */
    public ClassSerializer fieldPredicate(BiFunction<Field, Object, String> predicate) {
        this.fieldPredicate = predicate;
        return this;
    }

    /**
     * Adds a predicate that determines if a class can be serialized recursively.
     * 
     * @param predicate A predicate that takes a Class and returns true if the
     *                  class should be serialized recursively, false otherwise.
     * @return This serializer instance for chaining.
     */
    public ClassSerializer addClassPredicate(Predicate<Class<?>> predicate) {
        this.classPredicate = predicate;
        return this;
    }

    /**
     * Adds a custom serializer for a specific type.
     *
     * @param clazz      The class to register the serializer for.
     * @param serializer The serializer implementation.
     * @param <T>        The type of objects the serializer handles.
     * @return This serializer instance for chaining.
     */
    public <T> ClassSerializer addSerializer(Class<T> clazz, Serializer<T> serializer) {
        serializers.put(clazz, serializer);
        return this;
    }

    /**
     * Sets the serialization mode to deflate (flat structure with prefixed keys).
     *
     * @param separator The separator string. Must be null to disable deflate mode.
     * @return This serializer instance for chaining.
     */
    public ClassSerializer withDeflate(String separator) {
        this.deflateSeparator = separator;
        return this;
    }

    /**
     * Serializes an object to a map.
     *
     * @param object The object to serialize.
     * @return A map representation of the object.
     */
    public Map<String, Object> serialize(Object object) {
        if (object == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        Class<?> clazz = object.getClass();

        // Check if there's a custom serializer for this class
        Serializer<?> customSerializer = serializers.get(clazz);
        if (customSerializer != null) {
            Object serialized = serializeWithCustomSerializer(object, customSerializer);
            if (serialized instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) serialized;
                result.putAll(map);
            } else {
                result.put("value", serialized);
            }
        } else if (isPrimitiveOrWrapper(clazz) || isCollection(clazz) || Map.class.isAssignableFrom(clazz)) {
            // This is more for consistency, but normally you'd return the primitive
            // directly
            result.put("value", serializePrimitiveOrCollection(object));
        } else if (classPredicate.test(clazz)) {
            // Serialize recursively - process all fields
            for (Field field : getAllFields(clazz)) {
                String name = fieldPredicate.apply(field, object);
                if (name == null) {
                    continue;
                }

                field.setAccessible(true);
                try {
                    Object value = field.get(object);
                    if (value != null) {
                        result.put(name, serializeValue(value));
                    }
                } catch (IllegalAccessException e) {
                    throw new SerializationException("Error accessing field: " + field.getName(), e);
                }
            }
        } else {
            throw new SerializationException("Can't serialize class: " + clazz.getName() +
                    ". No serializer registered and class doesn't satisfy classPredicate.");
        }

        // Apply deflation if configured
        if (deflateSeparator != null) {
            return MapDeflate.deflate(result, deflateSeparator);
        }

        return result;
    }

    /**
     * Deserializes a map back to an object of the specified class.
     *
     * @param <T>   The type of the object to deserialize.
     * @param clazz The target class.
     * @param map   The map containing serialized data.
     * @return An instance of the specified class with data from the map.
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Class<T> clazz, Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        // Apply inflation if configured
        Map<String, Object> workingMap = map;
        if (deflateSeparator != null) {
            workingMap = MapDeflate.inflate(map, deflateSeparator);
        }

        // Check if there's a custom serializer for this class
        Serializer<T> customSerializer = (Serializer<T>) serializers.get(clazz);
        if (customSerializer != null) {
            if (workingMap.size() == 1 && workingMap.containsKey("value")) {
                return customSerializer.deserialize(workingMap.get("value"));
            }
            return customSerializer.deserialize(workingMap);
        }

        if (isPrimitiveOrWrapper(clazz)) {
            Object value = workingMap.get("value");
            if (value == null) {
                return (T) ClassUtils.getDefaultValue(clazz);
            }
            return convertToPrimitiveType(value, clazz);
        }

        if (Map.class.isAssignableFrom(clazz)) {
            return (T) workingMap;
        }

        if (List.class.isAssignableFrom(clazz)) {
            throw new SerializationException(
                    "Cannot deserialize directly to List without type information. Use a custom serializer.");
        }

        // Create a new instance of the class
        T instance;
        try {
            instance = ClassUtils.createInstance(clazz);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException
                | InvocationTargetException e) {
            throw new SerializationException("Error creating instance of class: " + clazz.getName(), e);
        }

        // Fill in all fields
        for (Field field : getAllFields(clazz)) {
            String name = fieldPredicate.apply(field, instance);
            if (name == null) {
                continue;
            }

            field.setAccessible(true);

            if (workingMap.containsKey(name)) {
                Object value = workingMap.get(name);
                try {
                    field.set(instance, deserializeValue(value, field));
                } catch (IllegalAccessException e) {
                    throw new SerializationException("Error setting field: " + name, e);
                }
            }
        }

        return instance;
    }

    /**
     * Serializes a single value based on its type
     */
    private Object serializeValue(Object value) {
        if (value == null) {
            return null;
        }

        Class<?> valueClass = value.getClass();

        // Check if there's a custom serializer
        Serializer<?> customSerializer = serializers.get(valueClass);
        if (customSerializer != null) {
            return serializeWithCustomSerializer(value, customSerializer);
        }

        // Handle primitives and collections
        if (isPrimitiveOrWrapper(valueClass)) {
            return value;
        } else if (value instanceof List) {
            return serializeList((List<?>) value);
        } else if (value instanceof Map) {
            return serializeMap((Map<?, ?>) value);
        } else if (classPredicate.test(valueClass)) {
            // Recursively serialize nested objects
            return serialize(value);
        } else {
            throw new SerializationException("Can't serialize value of type: " + valueClass.getName());
        }
    }

    /**
     * Serializes a list
     */
    private List<Object> serializeList(List<?> list) {
        List<Object> result = new ArrayList<>();
        for (Object item : list) {
            result.add(serializeValue(item));
        }
        return result;
    }

    /**
     * Serializes a map
     */
    private Map<String, Object> serializeMap(Map<?, ?> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            result.put(key, serializeValue(entry.getValue()));
        }
        return result;
    }

    /**
     * Deserializes a single value based on field type information
     */
    @SuppressWarnings("unchecked")
    private Object deserializeValue(Object value, Field field) {
        if (value == null) {
            return ClassUtils.getDefaultValue(field.getType());
        }

        Class<?> fieldType = field.getType();

        // Check if there's a custom serializer
        Serializer<?> customSerializer = serializers.get(fieldType);
        if (customSerializer != null) {
            return customSerializer.deserialize(value);
        }

        if (isPrimitiveOrWrapper(fieldType)) {
            return convertToPrimitiveType(value, fieldType);
        } else if (List.class.isAssignableFrom(fieldType)) {
            return deserializeList(value, field);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            if (value instanceof Map) {
                // Handle map with key mapping
                Map<String, Object> serializedMap = (Map<String, Object>) value;
                Map<Object, Object> resultMap;

                try {
                    resultMap = (Map<Object, Object>) fieldType.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    resultMap = new HashMap<>();
                }

                Type genericType = field.getGenericType();
                Class<?> valueType = Object.class;

                if (genericType instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) genericType;
                    Type[] typeArgs = paramType.getActualTypeArguments();
                    if (typeArgs.length >= 2) {
                        Type valueTypeArg = typeArgs[1];
                        if (valueTypeArg instanceof Class) {
                            valueType = (Class<?>) valueTypeArg;
                        }
                    }
                }

                for (Map.Entry<String, Object> entry : serializedMap.entrySet()) {
                    Object mapValue = entry.getValue();
                    if (mapValue != null && valueType != Object.class) {
                        if (classPredicate.test(valueType) && mapValue instanceof Map) {
                            mapValue = deserialize(valueType, (Map<String, Object>) mapValue);
                        } else if (serializers.containsKey(valueType)) {
                            mapValue = serializers.get(valueType).deserialize(mapValue);
                        }
                    }
                    resultMap.put(entry.getKey(), mapValue);
                }

                return resultMap;
            }
        } else if (classPredicate.test(fieldType) && value instanceof Map) {
            return deserialize(fieldType, (Map<String, Object>) value);
        }

        return value;
    }

    /**
     * Deserializes a list with type information from the field
     */
    @SuppressWarnings("unchecked")
    private List<Object> deserializeList(Object value, Field field) {
        if (!(value instanceof List)) {
            throw new SerializationException("Expected a List but got: " + value.getClass().getName());
        }

        List<?> sourceList = (List<?>) value;
        List<Object> result = new ArrayList<>();

        // Try to extract generic type information
        Type genericType = field.getGenericType();
        Class<?> itemType = Object.class;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length > 0) {
                Type itemTypeArg = typeArgs[0];
                if (itemTypeArg instanceof Class) {
                    itemType = (Class<?>) itemTypeArg;
                }
            }
        }

        for (Object item : sourceList) {
            if (item == null) {
                result.add(null);
            } else if (itemType != Object.class) {
                if (isPrimitiveOrWrapper(itemType)) {
                    result.add(convertToPrimitiveType(item, itemType));
                } else if (classPredicate.test(itemType) && item instanceof Map) {
                    result.add(deserialize(itemType, (Map<String, Object>) item));
                } else if (serializers.containsKey(itemType)) {
                    result.add(serializers.get(itemType).deserialize(item));
                } else {
                    result.add(item);
                }
            } else {
                result.add(item);
            }
        }

        return result;
    }

    /**
     * Serializes a primitive or collection for direct value storage
     */
    private Object serializePrimitiveOrCollection(Object value) {
        if (value instanceof List) {
            return serializeList((List<?>) value);
        } else if (value instanceof Map) {
            return serializeMap((Map<?, ?>) value);
        } else {
            return value;
        }
    }

    /**
     * Helper method to determine if a class is a primitive or wrapper
     */
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz);
    }

    /**
     * Helper method to determine if a class is a collection
     */
    private boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * Helper method to convert an object to the appropriate primitive type
     */
    private <T> T convertToPrimitiveType(Object value, Class<T> targetType) {
        try {
            return ClassUtils.convertValueToPrimitive(value, targetType);
        } catch (Exception e) {
            throw new SerializationException("Cannot convert " + value.getClass() + " to " + targetType, e);
        }
    }

    /**
     * Helper method to get all fields from a class and its superclasses
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            Collections.addAll(fields, currentClass.getDeclaredFields());
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    /**
     * Helper method to use a custom serializer
     */
    @SuppressWarnings("unchecked")
    private <T> Object serializeWithCustomSerializer(Object value, Serializer<?> serializer) {
        Serializer<T> typedSerializer = (Serializer<T>) serializer;
        return typedSerializer.serialize((T) value);
    }
}