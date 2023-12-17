package com.sammwy.classserializer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CachedClass {
    private Class<?> clazz;
    private Map<String, CachedField> fields;

    public CachedClass(Class<?> clazz) {
        this.clazz = clazz;
        this.fields = new HashMap<>();
    }

    private Field[] getAllFields() {
        Field[] fields = clazz.getFields();
        Field[] declaredFields = clazz.getDeclaredFields();
        Field[] allFields = new Field[fields.length + declaredFields.length];

        System.arraycopy(fields, 0, allFields, 0, fields.length);
        System.arraycopy(declaredFields, 0, allFields, fields.length, declaredFields.length);

        return allFields;
    }

    public void load(ClassProcessor processor) {
        Field[] fields = this.getAllFields();

        for (Field field : fields) {
            boolean serialize = processor.shouldSerializeField(clazz, field);
            boolean deserialize = processor.shouldDeserializeField(clazz, field);

            if (serialize || deserialize) {
                field.setAccessible(true);

                CachedField cachedField = new CachedField(field, processor);
                String key = processor.getFieldName(clazz, field);
                this.fields.put(key, cachedField);
            }
        }
    }
}
