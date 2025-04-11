package com.sammwy.classserializer.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.tests.annotations.Embedded;
import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.entity.TestUser;

@Nested
@DisplayName("Serialize with Deflate and Inflate")
public class SerializeWithDeflateTest {
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
    @DisplayName("Serialize in Flate Mode")
    public void serializeInFlateMode() {
        TestUser user = new TestUser();

        // Flate mode is enabled by default
        Map<String, Object> serialized = serializer.serialize(user);

        assertTrue(serialized.get("settings") instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) serialized.get("settings");
        assertTrue(settings.get("preferences") instanceof Map);
    }

    @Test
    @DisplayName("Serialize in Deflate Mode")
    public void serializeInDeflateMode() {
        TestUser user = new TestUser();

        // Change the serializer to deflate mode
        serializer.withDeflate(".");
        ;
        Map<String, Object> serialized = serializer.serialize(user);

        // On deflate mode, the serialized object should be a plain map
        assertEquals("John Doe", serialized.get("name"));
        assertEquals(30, serialized.get("age"));
        assertEquals(true, serialized.get("settings.darkMode"));
        assertEquals(14.5f, serialized.get("settings.fontSize"));
        assertEquals("default", serialized.get("settings.preferences.theme"));
        assertEquals(true, serialized.get("settings.preferences.notifications"));

        // Must not contain the settings field
        assertFalse(serialized.containsKey("settings"));
    }

    @Test
    @DisplayName("Deserialize from Deflate Mode")
    public void deserializeFromDeflateMode() {
        // Create a deflate map
        Map<String, Object> deflateMap = new HashMap<>();
        deflateMap.put("name", "Jane Smith");
        deflateMap.put("age", 25);
        deflateMap.put("settings.darkMode", false);
        deflateMap.put("settings.fontSize", 16.0f);
        deflateMap.put("settings.preferences.theme", "light");
        deflateMap.put("settings.preferences.notifications", false);
        deflateMap.put("tags", Arrays.asList("manager", "admin"));

        // Deflate the map
        serializer.withDeflate(".");
        ;
        TestUser user = serializer.deserialize(TestUser.class, deflateMap);

        assertEquals("Jane Smith", user.name);
        assertEquals(25, user.age);
        assertFalse(user.settings.darkMode);
        assertEquals(16.0f, user.settings.fontSize);
        assertEquals("light", user.settings.preferences.theme);
        assertFalse(user.settings.preferences.notifications);
        assertEquals(Arrays.asList("manager", "admin"), user.tags);
    }

    @Test
    @DisplayName("Custom Separator In Deflate Mode")
    public void customSeparatorInDeflateMode() {
        TestUser user = new TestUser();

        // Change the serializer to deflate mode
        serializer.withDeflate("_");
        Map<String, Object> serialized = serializer.serialize(user);

        assertEquals("John Doe", serialized.get("name"));
        assertEquals(true, serialized.get("settings_darkMode"));
        assertEquals("default", serialized.get("settings_preferences_theme"));

        // Deserialize with the same custom separator
        TestUser deserialized = serializer.deserialize(TestUser.class, serialized);

        assertEquals("John Doe", deserialized.name);
        assertTrue(deserialized.settings.darkMode);
        assertEquals("default", deserialized.settings.preferences.theme);
    }
}
