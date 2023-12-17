package com.sammwy.classserializer;

import java.lang.reflect.Field;

public class CachedField {
    private Field field;
    private ClassProcessor processor;

    public CachedField(Field field, ClassProcessor processor) {
        this.field = field;
        this.processor = processor;
    }

    public Object getValue(Object entity) {
        return processor.getFieldValue(entity, field);
    }

    public void setValue(Object entity, Object value) {
        processor.setFieldValue(entity, field, value);
    }
}
