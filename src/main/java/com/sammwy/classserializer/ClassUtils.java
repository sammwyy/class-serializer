package com.sammwy.classserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for working with Java classes, providing methods for type
 * checking,
 * primitive conversion and instance creation.
 */
public class ClassUtils {

    /**
     * Checks if a class is a primitive type or its wrapper equivalent.
     * Includes String in the check as it's commonly treated as a basic type.
     *
     * @param clazz the class to check
     * @return true if the class is a primitive, wrapper or String, false otherwise
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class;
    }

    /**
     * Gets the default value for a primitive type.
     * Returns null for non-primitive types.
     *
     * @param type the primitive type class
     * @return the default value for the primitive type
     * @throws IllegalArgumentException if the type is not a primitive type
     */
    public static Object getDefaultValue(Class<?> type) {
        if (!type.isPrimitive())
            return null;
        if (type == boolean.class)
            return false;
        if (type == char.class)
            return '\0';
        if (type == byte.class)
            return (byte) 0;
        if (type == short.class)
            return (short) 0;
        if (type == int.class)
            return 0;
        if (type == long.class)
            return 0L;
        if (type == float.class)
            return 0f;
        if (type == double.class)
            return 0d;
        throw new IllegalArgumentException("Unsupported primitive type: " + type);
    }

    /**
     * Converts a value to the specified primitive or wrapper type.
     * Handles common type conversions including string parsing.
     *
     * @param <T>        the target type
     * @param value      the value to convert
     * @param targetType the target primitive or wrapper type class
     * @return the converted value
     * @throws IllegalArgumentException if the conversion is not supported
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertValueToPrimitive(Object value, Class<T> targetType) {
        if (value == null) {
            return (T) ClassUtils.getDefaultValue(targetType);
        }

        if (targetType.isInstance(value)) {
            return (T) value;
        }

        // Basic type conversions
        if (targetType == String.class) {
            return (T) value.toString();
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof String) {
                return (T) Boolean.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Boolean.valueOf(((Number) value).intValue() != 0);
            } else if (value instanceof Boolean) {
                return (T) value;
            }
        } else if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof String) {
                return (T) Integer.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Integer.valueOf(((Number) value).intValue());
            }
        } else if (targetType == Long.class || targetType == long.class) {
            if (value instanceof String) {
                return (T) Long.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            }
        } else if (targetType == Double.class || targetType == double.class) {
            if (value instanceof String) {
                return (T) Double.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Double.valueOf(((Number) value).doubleValue());
            }
        } else if (targetType == Float.class || targetType == float.class) {
            if (value instanceof String) {
                return (T) Float.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Float.valueOf(((Number) value).floatValue());
            }
        } else if (targetType == Character.class || targetType == char.class) {
            if (value instanceof String && ((String) value).length() > 0) {
                return (T) Character.valueOf(((String) value).charAt(0));
            }
        } else if (targetType == Byte.class || targetType == byte.class) {
            if (value instanceof String) {
                return (T) Byte.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Byte.valueOf(((Number) value).byteValue());
            }
        } else if (targetType == Short.class || targetType == short.class) {
            if (value instanceof String) {
                return (T) Short.valueOf((String) value);
            } else if (value instanceof Number) {
                return (T) Short.valueOf(((Number) value).shortValue());
            }
        }
        throw new IllegalArgumentException("Unsupported primitive type: " + targetType);
    }

    /**
     * Creates an instance of the specified class, even if it doesn't have a no-args
     * constructor.
     * For classes without no-args constructor, it will use the first available
     * constructor
     * and provide default values for all parameters.
     *
     * @param <T>   the type of class to instantiate
     * @param clazz the class to instantiate
     * @return a new instance of the specified class
     * @throws NoSuchMethodException     if no suitable constructor can be found
     * @throws IllegalAccessException    if the constructor cannot be accessed
     * @throws InstantiationException    if the class is abstract or cannot be
     *                                   instantiated
     * @throws InvocationTargetException if the constructor throws an exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> clazz)
            throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            // Try to find any constructor if no-args isn't available
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length > 0) {
                Constructor<?> constructor = constructors[0];
                constructor.setAccessible(true);
                Object[] args = new Object[constructor.getParameterCount()];
                Class<?>[] paramTypes = constructor.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    args[i] = getDefaultValue(paramTypes[i]);
                }
                return (T) constructor.newInstance(args);
            }
            throw e;
        }
    }
}