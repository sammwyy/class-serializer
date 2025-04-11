package com.sammwy.classserializer.tests.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sammwy.classserializer.tests.annotations.Embedded;
import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.annotations.Serializable;

@Serializable
public class TestUser {
    @Prop
    public String name = "John Doe";

    @Prop
    public int age = 30;

    @Embedded
    public TestSettings settings = new TestSettings();

    public String ignored = "This should be ignored";

    @Prop
    public List<String> tags = new ArrayList<>(Arrays.asList("developer", "tester"));

    @Prop
    public Map<String, Object> metadata = new HashMap<>();

    public TestUser() {
        metadata.put("created", 1649712000000L); // Fixed timestamp for testing
        metadata.put("active", true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TestUser testUser = (TestUser) o;
        return age == testUser.age &&
                Objects.equals(name, testUser.name) &&
                Objects.equals(settings, testUser.settings) &&
                Objects.equals(tags, testUser.tags) &&
                Objects.deepEquals(new ArrayList<>(metadata.entrySet()).toArray(),
                        new ArrayList<>(testUser.metadata.entrySet()).toArray());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, settings, tags, metadata);
    }
}