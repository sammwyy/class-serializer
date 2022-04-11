package com.dotphin.classserializer.tests;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.dotphin.classserializer.ClassSerializer;
import com.dotphin.classserializer.entities.SerializableUser;

import org.junit.Test;

public class SerializableTest {
    @Test
    public void test() {
        SerializableUser user = new SerializableUser();
        user.name = "Sammwy";
        user.age = 20;

        Map<String, Object> map = new ClassSerializer().serialize(user);
        SerializableUser deserializedUser = new ClassSerializer().deserialize(SerializableUser.class, map);

        assertEquals(user.name, deserializedUser.name);
        assertEquals(user.age, deserializedUser.age);
    }
}
