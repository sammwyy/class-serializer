package com.sammwy.classserializer;

import java.util.HashMap;
import java.util.Map;

import com.sammwy.classserializer.defaults.DefaultClassProcessor;

public class ClassSerializer {
    private ClassProcessor processor;
    private Map<Class<?>, CachedClass<?>> cachedClasses;

    public ClassSerializer(ClassProcessor processor) {
        this.processor = processor;
        this.cachedClasses = new HashMap<>();
    }

    public ClassSerializer() {
        this(new DefaultClassProcessor());

        ClassSerializer.defaultSerializer = this;
    }

    @SuppressWarnings("unchecked")
    public <S> CachedClass<S> wrapClass(Class<S> clazz) {
        CachedClass<S> cachedClass = (CachedClass<S>) this.cachedClasses.get(clazz);

        if (cachedClass == null) {
            cachedClass = new CachedClass<S>(clazz, this.processor);
            cachedClass.load();
            this.cachedClasses.put(clazz, cachedClass);
        }

        return cachedClass;
    }

    @SuppressWarnings("unchecked")
    public <S> S deserialize(Object obj, Map<String, Object> values) {
        CachedClass<S> cached = (CachedClass<S>) this.wrapClass(obj.getClass());
        return cached.deserialize(obj, values);
    }

    @SuppressWarnings("unchecked")
    public <S> S deserialize(Class<?> clazz, Map<String, Object> values) {
        CachedClass<S> cached = (CachedClass<S>) this.wrapClass(clazz);
        return cached.deserialize(values);
    }

    public Map<String, Object> serialize(Object object) {
        CachedClass<?> cached = this.wrapClass(object.getClass());
        return cached.serialize(object);
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