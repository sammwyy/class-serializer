package com.dotphin.classserializer.tests;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.dotphin.classserializer.ClassSerializer;
import com.dotphin.classserializer.entities.PropUser;

import org.junit.Test;

public class PropTest {
    @Test
    public void test() {
        PropUser user = new PropUser();
        user.name = "Sammwy";
        user.age = 20;

        Map<String, Object> map = new ClassSerializer().serialize(user);
        PropUser deserializedUser = new ClassSerializer().deserialize(PropUser.class, map);

        assertEquals(user.name, deserializedUser.name);
        assertEquals(user.age, deserializedUser.age);
    }
}
