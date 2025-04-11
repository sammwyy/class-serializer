package com.sammwy.classserializer.tests.entity;

import java.util.Arrays;
import java.util.List;

import com.sammwy.classserializer.tests.annotations.Prop;

public class WithLists {
    @Prop
    public List<String> strings = Arrays.asList("a", "b", "c");
    @Prop
    public List<Integer> numbers = Arrays.asList(1, 2, 3);
    @Prop
    public List<List<String>> nested = Arrays.asList(
            Arrays.asList("x", "y"),
            Arrays.asList("z"));
    @Prop
    public List<TestPreferences> objects = Arrays.asList(
            new TestPreferences(),
            new TestPreferences());
}
