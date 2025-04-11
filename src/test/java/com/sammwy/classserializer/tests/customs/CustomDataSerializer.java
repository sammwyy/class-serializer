package com.sammwy.classserializer.tests.customs;

import java.util.HashMap;
import java.util.Map;

import com.sammwy.classserializer.SerializationException;
import com.sammwy.classserializer.Serializer;

public class CustomDataSerializer implements Serializer<CustomData> {
    @Override
    public Object serialize(CustomData object) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", object.getCode());
        map.put("timestamp", object.getTimestamp());
        return map;
    }

    @Override
    public CustomData deserialize(Object serialized) {
        if (!(serialized instanceof Map)) {
            throw new SerializationException("Expected a Map for CustomData");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) serialized;
        String code = (String) map.get("code");
        long timestamp = ((Number) map.get("timestamp")).longValue();
        return new CustomData(code, timestamp);
    }
}
