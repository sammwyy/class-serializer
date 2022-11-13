package com.dotphin.classserializer.defaults;

import java.lang.reflect.Field;

import com.dotphin.classserializer.ClassProcessor;
import com.dotphin.classserializer.annotations.Prop;
import com.dotphin.classserializer.annotations.Serializable;

public class DefaultClassProcessor implements ClassProcessor {
    @Override
    public boolean shouldDeserializeField(Class<?> clazz, Field field) {
        return clazz.isAnnotationPresent(Serializable.class) || field.isAnnotationPresent(Prop.class);
    }

    @Override
    public boolean shouldDeserializeValue(Class<?> clazz, Field field, Object value) {
        return true;
    }

    @Override
    public boolean shouldSerializeField(Class<?> clazz, Field field) {
        return clazz.isAnnotationPresent(Serializable.class) || field.isAnnotationPresent(Prop.class);
    }

    @Override
    public boolean shouldSerializeValue(Class<?> clazz, Field field, Object value) {
        return true;
    }

    @Override
    public String getFieldName(Class<?> clazz, Field field) {
        String key = field.getName();

        if (field.isAnnotationPresent(Prop.class)) {
            String annotationKey = field.getAnnotation(Prop.class).key();

            if (!annotationKey.isEmpty()) {
                key = annotationKey;
            }
        }

        return key;
    }

}
