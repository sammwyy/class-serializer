package com.sammwy.classserializer.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sammwy.classserializer.MapDeflate;

@Nested
@DisplayName("Map Deflate and Inflate")
public class MapDeflateTest {
    @Test
    @DisplayName("mapDeflate must flatten a nested map correctly")
    public void mapDeflateFlattensCorrectly() {
        Map<String, Object> nestedMap = new HashMap<>();
        Map<String, Object> settingsMap = new HashMap<>();
        Map<String, Object> prefsMap = new HashMap<>();

        prefsMap.put("theme", "light");
        prefsMap.put("notifications", false);

        settingsMap.put("darkMode", false);
        settingsMap.put("preferences", prefsMap);

        nestedMap.put("name", "Test");
        nestedMap.put("settings", settingsMap);

        Map<String, Object> deflated = MapDeflate.deflate(nestedMap, ".");
        assertEquals("Test", deflated.get("name"));
        assertEquals(false, deflated.get("settings.darkMode"));
        assertEquals("light", deflated.get("settings.preferences.theme"));
        assertEquals(false, deflated.get("settings.preferences.notifications"));
    }
}
