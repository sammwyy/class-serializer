package com.sammwy.classserializer.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.SerializationException;
import com.sammwy.classserializer.Serializer;
import com.sammwy.classserializer.tests.annotations.Embedded;
import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.customs.CustomData;
import com.sammwy.classserializer.tests.entity.TestUser;

@Nested
@DisplayName("Error Boundary")
public class ErrorBoundaryTest {
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
                });
    }

    @Test
    @DisplayName("Handle incompatible types")
    public void handleIncompatibleTypes() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", "not a number"); // String instead of int

        SerializationException exception = assertThrows(SerializationException.class,
                () -> serializer.deserialize(TestUser.class, map));

        assertTrue(exception.getMessage().contains("Cannot convert"));
    }

    @Test
    @DisplayName("Handle in custom serializers")
    public void handleCustomSerializerErrors() {
        Serializer<CustomData> badSerializer = new Serializer<CustomData>() {
            @Override
            public Object serialize(CustomData object) {
                throw new RuntimeException("Serializer error");
            }

            @Override
            public CustomData deserialize(Object serialized) {
                throw new RuntimeException("Deserializer error");
            }
        };

        serializer.addSerializer(CustomData.class, badSerializer);
        CustomData data = new CustomData("TEST", 123);

        assertThrows(RuntimeException.class, () -> serializer.serialize(data));
    }
}
