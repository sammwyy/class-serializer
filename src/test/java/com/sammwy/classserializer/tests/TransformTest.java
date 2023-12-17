package com.sammwy.classserializer.tests;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.entities.PropUser;

public class TransformTest {
    @Test
    public void testClamp() {
        PropUser user = new PropUser();
        user.clamp = 2000;

        Map<String, Object> map = new ClassSerializer().serialize(user);
        assertEquals(map.get("clamp"), 100);
    }

    @Test
    public void testEmptyIsNull() {
        PropUser user = new PropUser();
        user.emptyIsNull = "";

        Map<String, Object> map = new ClassSerializer().serialize(user);
        assertEquals(map.get("emptyIsNull"), null);
    }

    @Test
    public void testLower() {
        PropUser user = new PropUser();
        user.lower = "SaMMwY";

        Map<String, Object> map = new ClassSerializer().serialize(user);
        assertEquals(map.get("lower"), "sammwy");
    }

    @Test
    public void testUpper() {
        PropUser user = new PropUser();
        user.upper = "SaMMwY";

        Map<String, Object> map = new ClassSerializer().serialize(user);
        assertEquals(map.get("upper"), "SAMMWY");
    }

    @Test
    public void testTrim() {
        PropUser user = new PropUser();
        user.trim = "    SaMMwY        ";

        Map<String, Object> map = new ClassSerializer().serialize(user);
        assertEquals(map.get("trim"), "SaMMwY");
    }
}
