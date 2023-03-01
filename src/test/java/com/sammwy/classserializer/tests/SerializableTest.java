package com.sammwy.classserializer.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.entities.SerializableUser;

import org.junit.Test;

public class SerializableTest {
    @Test
    public void serializeTest() {
        SerializableUser user = new SerializableUser();
        user.name = "Sammwy";
        user.age = 20;

        Map<String, Object> map = new ClassSerializer().serialize(user);
        assertEquals(map.get("name"), user.name);
        assertEquals(map.get("age"), user.age);
    }

    @Test
    public void deserializeTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "sammwy");
        map.put("age", 20);

        SerializableUser user = new ClassSerializer().deserialize(SerializableUser.class, map);
        assertEquals(user.name, map.get("name"));
        assertEquals(user.age, map.get("age"));
    }

    @Test
    public void nullTest() {
        SerializableUser user = new SerializableUser();
        Map<String, Object> map = new ClassSerializer().serialize(user);

        assertTrue(map.containsKey("name"));
        assertTrue(map.containsKey("age"));
        assertNull(map.get("name"));
        assertEquals(map.get("age"), 0);
    }
}
