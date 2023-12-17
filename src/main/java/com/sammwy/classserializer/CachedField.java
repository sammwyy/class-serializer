package com.sammwy.classserializer;

import java.lang.reflect.Field;

import com.sammwy.classserializer.transform.Clamp;
import com.sammwy.classserializer.transform.EmptyIsNull;
import com.sammwy.classserializer.transform.LowerCase;
import com.sammwy.classserializer.transform.Trim;
import com.sammwy.classserializer.transform.UpperCase;
import com.sammwy.classserializer.utils.AbstractMath;

public class CachedField {
    private Field field;
    private ClassProcessor processor;
    private boolean modified;
    private boolean hasValueCached;
    private Object cachedValue;

    // Transformers.
    private boolean clamp;
    private int clampMin;
    private int clampMax;
    private boolean emptyIsNull;
    private boolean trim;
    private boolean toLowerCase;
    private boolean toUpperCase;

    public CachedField(Field field, ClassProcessor processor) {
        this.field = field;
        this.processor = processor;
        this.modified = false;
        this.hasValueCached = false;
        this.cachedValue = null;
    }

    protected void load() {
        this.clamp = field.isAnnotationPresent(Clamp.class);

        if (clamp) {
            Clamp clamp = field.getAnnotation(Clamp.class);
            this.clampMin = clamp.min();
            this.clampMax = clamp.max();
        }

        this.emptyIsNull = field.isAnnotationPresent(EmptyIsNull.class);
        this.trim = field.isAnnotationPresent(Trim.class);
        this.toLowerCase = field.isAnnotationPresent(LowerCase.class);
        this.toUpperCase = field.isAnnotationPresent(UpperCase.class);
    }

    public Object transform(Object value) {
        if (clamp && AbstractMath.isMathType(value)) {
            value = AbstractMath.clamp(value, clampMin, clampMax);
        }

        if (emptyIsNull && value instanceof String && ((String) value).isEmpty()) {
            value = null;
        }

        if (trim && value instanceof String) {
            value = ((String) value).trim();
        }

        if (toLowerCase && value instanceof String) {
            value = ((String) value).toLowerCase();
        }

        if (toUpperCase && value instanceof String) {
            value = ((String) value).toUpperCase();
        }

        return value;
    }

    public boolean isModified() {
        return modified;
    }

    public Object getValue(Object entity) {
        Object value = processor.getFieldValue(entity, field);
        this.modified = hasValueCached && !value.equals(cachedValue);
        return value;
    }

    public void setValue(Object entity, Object value) {
        processor.setFieldValue(entity, field, value);
        cachedValue = value;
        hasValueCached = true;
        modified = false;
    }

    public boolean shouldDeserialize(Object value) {
        return processor.shouldDeserializeValue(field.getDeclaringClass(), field, value);
    }

    public boolean shouldSerialize(Object value) {
        return processor.shouldSerializeValue(field.getDeclaringClass(), field, value);
    }
}
