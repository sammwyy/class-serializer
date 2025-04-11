package com.sammwy.classserializer.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.tests.annotations.Embedded;
import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.entity.TestPreferences;
import com.sammwy.classserializer.tests.entity.TestUser;

@Nested
@DisplayName("Primitive Serialization")
public class PrimitiveSerializerTest {
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
    @DisplayName("Simple Object Serialization")
    public void serializeSimpleObject() {
        TestPreferences preferences = new TestPreferences();

        Map<String, Object> serialized = this.serializer.serialize(preferences);
        assertEquals(2, serialized.size());
        assertEquals("default", serialized.get("theme"));
        assertEquals(true, serialized.get("notifications"));
    }

    @DisplayName("Object With Nested Fields Serialization")
    public void serializeObjectWithNestedFields() {
        TestUser user = new TestUser();

        Map<String, Object> serialized = serializer.serialize(user);
        assertEquals("John Doe", serialized.get("name"));
        assertEquals(30, serialized.get("age"));
        assertTrue(serialized.get("settings") instanceof Map);
        assertTrue(serialized.get("tags") instanceof List);
        assertTrue(serialized.get("metadata") instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> settings = (Map<String, Object>) serialized.get("settings");
        assertEquals(true, settings.get("darkMode"));
        assertEquals(14.5f, settings.get("fontSize"));

        assertFalse(serialized.containsKey("ignored"));
    }

    @Test
    @DisplayName("Handle Null Values In Serialization")
    public void handleNullValues() {
        TestUser user = new TestUser();
        user.settings = null;

        Map<String, Object> serialized = serializer.serialize(user);

        assertFalse(serialized.containsKey("ignored"));
        assertNull(serialized.get("settings"));
    }

    @Test
    @DisplayName("Deserialize Simple Object")
    public void deserializeSimpleObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("theme", "dark");
        map.put("notifications", false);

        TestPreferences preferences = serializer.deserialize(TestPreferences.class, map);

        assertEquals("dark", preferences.theme);
        assertFalse(preferences.notifications);
    }

    @Test
    @DisplayName("Deserialize Object With Nested Fields")
    public void deserializeObjectWithNestedFields() {
        Map<String, Object> settingsMap = new HashMap<>();
        settingsMap.put("darkMode", false);
        settingsMap.put("fontSize", 16.0f);

        Map<String, Object> preferencesMap = new HashMap<>();
        preferencesMap.put("theme", "light");
        preferencesMap.put("notifications", false);

        settingsMap.put("preferences", preferencesMap);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", "Jane Smith");
        userMap.put("age", 25);
        userMap.put("settings", settingsMap);
        userMap.put("tags", Arrays.asList("manager", "admin"));

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("created", 1649712000000L);
        metadata.put("active", false);
        userMap.put("metadata", metadata);

        TestUser user = serializer.deserialize(TestUser.class, userMap);

        assertEquals("Jane Smith", user.name);
        assertEquals(25, user.age);
        assertFalse(user.settings.darkMode);
        assertEquals(16.0f, user.settings.fontSize);
        assertEquals("light", user.settings.preferences.theme);
        assertFalse(user.settings.preferences.notifications);
        assertEquals(Arrays.asList("manager", "admin"), user.tags);
        assertEquals(1649712000000L, user.metadata.get("created"));
        assertEquals(false, user.metadata.get("active"));
    }

    @Test
    @DisplayName("Handle Null Values In Deserialization")
    public void handleNullValuesInDeserialization() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", null);
        userMap.put("settings", null);

        TestUser user = serializer.deserialize(TestUser.class, userMap);

        assertNull(user.name);
        assertNull(user.settings);
    }
}
