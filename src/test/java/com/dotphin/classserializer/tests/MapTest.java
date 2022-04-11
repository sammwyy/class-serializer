package com.dotphin.classserializer.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import com.dotphin.classserializer.ClassSerializer;
import com.dotphin.classserializer.entities.PropUser;
import org.junit.Test;

public class MapTest {
    @Test
    public void test() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Sammwy");
        values.put("age", 20);

        ClassSerializer serializer = new ClassSerializer();
        PropUser user = serializer.deserialize(PropUser.class, values);

        assertEquals(user.name, "Sammwy");
        assertEquals(user.age, 20);
    }
}
