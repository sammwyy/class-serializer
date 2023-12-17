package com.sammwy.classserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CachedClass<S> {
    private Class<S> clazz;
    private ClassProcessor processor;
    private Map<String, CachedField> fields;

    public CachedClass(Class<S> clazz, ClassProcessor processor) {
        this.clazz = clazz;
        this.processor = processor;
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

    public void load() {
        Field[] fields = this.getAllFields();

        for (Field field : fields) {
            boolean serialize = this.processor.shouldSerializeField(clazz, field);
            boolean deserialize = this.processor.shouldDeserializeField(clazz, field);

            if (serialize || deserialize) {
                field.setAccessible(true);

                CachedField cachedField = new CachedField(field, processor);
                String key = this.processor.getFieldName(clazz, field);
                cachedField.load();
                this.fields.put(key, cachedField);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public S deserialize(Object obj, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            CachedField cachedField = this.fields.get(key);

            if (cachedField != null && cachedField.shouldDeserialize(value)) {
                cachedField.setValue(obj, value);
            }
        }

        return (S) obj;
    }

    @SuppressWarnings("unchecked")
    public S deserialize(Map<String, Object> values) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            S result = (S) constructor.newInstance();
            return this.deserialize(result, values);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> serialize(Object object) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, CachedField> entry : this.fields.entrySet()) {
            String key = entry.getKey();
            CachedField cachedField = entry.getValue();
            Object value = cachedField.getValue(object);

            if (cachedField.shouldSerialize(value)) {
                Object fixedValue = cachedField.transform(value);
                cachedField.setValue(object, fixedValue);
                result.put(key, fixedValue);
            }
        }

        return result;
    }
}
