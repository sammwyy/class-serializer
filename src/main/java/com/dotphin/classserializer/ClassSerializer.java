package com.dotphin.classserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.dotphin.classserializer.defaults.DefaultClassProcessor;

public class ClassSerializer {
    private ClassProcessor processor;
    
    public ClassSerializer(ClassProcessor processor) {
        this.processor = processor;
    }

    public ClassSerializer() {
        this(new DefaultClassProcessor());

        ClassSerializer.defaultSerializer = this;
    }

    @SuppressWarnings("unchecked")
    public <S> S deserialize(Class<?> clazz, Map<String, Object> values) {
        Constructor<?> constructor;
        S result;

        try {
            constructor = clazz.getConstructor();
            result = (S) constructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        Field[] fields = clazz.getFields();

        for (Field field : fields) {
            String key = this.processor.getFieldName(clazz, field);
            Object value = values.get(key);

            if (this.processor.shouldDeserializeField(clazz, field, value)) {
                field.setAccessible(true);
                
                try {
                    field.set(result, value);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public Map<String, Object> serialize (Object object) {
        Class<?> clazz = object.getClass();
        Map<String, Object> result = new HashMap<>();

        Field[] fields = clazz.getFields();
        
        for (Field field : fields) {
            if (this.processor.shouldSerializeField(clazz, field)) {
                field.setAccessible(true);

                try {
                    String key = this.processor.getFieldName(clazz, field);
                    Object value = field.get(object);
                    result.put(key, value);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Static instance
     */
    private static ClassSerializer defaultSerializer;

    public static ClassSerializer getDefaultSerializer() {
        if (ClassSerializer.defaultSerializer == null) {
            ClassSerializer.defaultSerializer = new ClassSerializer();
        }
        return ClassSerializer.defaultSerializer;
    }
}