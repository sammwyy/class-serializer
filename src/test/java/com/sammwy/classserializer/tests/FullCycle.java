package com.sammwy.classserializer.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.sammwy.classserializer.tests.entity.TestUser;

@Nested
@DisplayName("Full Cycle")
public class FullCycle {
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
    @DisplayName("Serialize and Deserialize Complex Object")
    public void serializeDeserializeComplex() {
        // Create a complex object for testing
        TestUser originalUser = new TestUser();
        originalUser.name = "Alice Cooper";
        originalUser.age = 35;
        originalUser.settings.darkMode = false;
        originalUser.settings.fontSize = 18.0f;
        originalUser.settings.preferences.theme = "contrast";
        originalUser.settings.preferences.notifications = false;
        originalUser.tags = new ArrayList<>(Arrays.asList("musician", "artist"));
        originalUser.metadata.put("verified", true);
        originalUser.metadata.put("followers", 1500000);
        originalUser.metadata.put("custom", new CustomData("MUSICIAN", 1649712000000L));

        // Serialize
        Map<String, Object> serialized = serializer.serialize(originalUser);

        // Deserialize
        TestUser deserializedUser = serializer.deserialize(TestUser.class, serialized);

        // Check integrity
        assertEquals(originalUser.name, deserializedUser.name);
        assertEquals(originalUser.age, deserializedUser.age);
        assertEquals(originalUser.settings.darkMode, deserializedUser.settings.darkMode);
        assertEquals(originalUser.settings.fontSize, deserializedUser.settings.fontSize);
        assertEquals(originalUser.settings.preferences.theme, deserializedUser.settings.preferences.theme);
        assertEquals(originalUser.settings.preferences.notifications,
                deserializedUser.settings.preferences.notifications);
        assertEquals(originalUser.tags, deserializedUser.tags);
        assertEquals(originalUser.metadata.get("verified"), deserializedUser.metadata.get("verified"));
        assertEquals(originalUser.metadata.get("followers"), deserializedUser.metadata.get("followers"));

        // Check for custom serializers
        CustomData originalCustom = (CustomData) originalUser.metadata.get("custom");
        @SuppressWarnings("unchecked")
        Map<String, Object> deserializedCustomMap = (Map<String, Object>) deserializedUser.metadata.get("custom");
        assertEquals(originalCustom.getCode(), deserializedCustomMap.get("code"));
        assertEquals(originalCustom.getTimestamp(), deserializedCustomMap.get("timestamp"));
    }

    @Test
    @DisplayName("Check consistency in Deflate Mode")
    public void consistencyInDeflateMode() {
        TestUser originalUser = new TestUser();
        originalUser.name = "Bob Marley";
        originalUser.settings.preferences.theme = "reggae";

        // Serialize in deflate mode
        serializer.withDeflate(".");
        ;
        Map<String, Object> serialized = serializer.serialize(originalUser);

        // Deserialize in deflate mode
        TestUser deserializedUser = serializer.deserialize(TestUser.class, serialized);

        // Check integrity
        assertEquals(originalUser.name, deserializedUser.name);
        assertEquals(originalUser.age, deserializedUser.age);
        assertEquals(originalUser.settings.darkMode, deserializedUser.settings.darkMode);
        assertEquals(originalUser.settings.fontSize, deserializedUser.settings.fontSize);
        assertEquals(originalUser.settings.preferences.theme, deserializedUser.settings.preferences.theme);
        assertEquals(originalUser.settings.preferences.notifications,
                deserializedUser.settings.preferences.notifications);
    }
}