package com.sammwy.classserializer.tests.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sammwy.classserializer.tests.annotations.Prop;

public class WithMaps {
    @Prop
    public Map<String, String> strings = new HashMap<>();
    @Prop
    public Map<String, Integer> numbers = new HashMap<>();
    @Prop
    public Map<String, List<String>> nested = new HashMap<>();
    @Prop
    public Map<String, TestPreferences> objects = new HashMap<>();

    public WithMaps() {
        strings.put("a", "value-a");
        strings.put("b", "value-b");

        numbers.put("one", 1);
        numbers.put("two", 2);

        nested.put("letters", Arrays.asList("x", "y", "z"));

        objects.put("default", new TestPreferences());
        TestPreferences dark = new TestPreferences();
        dark.theme = "dark";
        objects.put("dark", dark);
    }
}
