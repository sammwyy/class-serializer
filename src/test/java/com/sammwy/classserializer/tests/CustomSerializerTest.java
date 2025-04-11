package com.sammwy.classserializer.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.tests.annotations.Embedded;
import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.customs.CustomData;
import com.sammwy.classserializer.tests.customs.CustomDataSerializer;
import com.sammwy.classserializer.tests.entity.UserWithCustomData;

@Nested
@DisplayName("Custom Serializers")
public class CustomSerializerTest {
    private ClassSerializer serializer;

    @BeforeEach
    public void setUp() {
        serializer = new ClassSerializer()
                .fieldPredicate((field, obj) -> {
                    if (field.isAnnotationPresent(Prop.class)) {
                        return field.getName();
                    } else if (field.isAnnotationPresent(Embedded.class)) {
                        return field.getName();
                    }
                    return null;
                })
                .addSerializer(CustomData.class, new CustomDataSerializer());
    }

    @Test
    @DisplayName("Use correctly custom serializers")
    public void useCustomSerializers() {
        CustomData customData = new CustomData("ABC123", 1649712000000L);

        Map<String, Object> serialized = serializer.serialize(customData);

        assertTrue(serialized instanceof Map);
        assertEquals("ABC123", ((Map<?, ?>) serialized).get("code"));
        assertEquals(1649712000000L, ((Map<?, ?>) serialized).get("timestamp"));

        CustomData deserialized = serializer.deserialize(CustomData.class, serialized);

        assertEquals("ABC123", deserialized.getCode());
        assertEquals(1649712000000L, deserialized.getTimestamp());
    }

    @Test
    @DisplayName("Use custom serializers in nested fields")
    public void useCustomSerializersInNestedFields() {
        UserWithCustomData user = new UserWithCustomData();

        Map<String, Object> serialized = serializer.serialize(user);

        assertEquals("Alice", serialized.get("name"));
        assertTrue(serialized.get("data") instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) serialized.get("data");
        assertEquals("XYZ789", dataMap.get("code"));
        assertEquals(1649712000000L, dataMap.get("timestamp"));
    }
}