package com.sammwy.classserializer.tests.entity;

import java.util.Objects;

import com.sammwy.classserializer.tests.annotations.Embedded;
import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.annotations.Serializable;

@Serializable
public class TestSettings {
    @Prop
    public boolean darkMode = true;

    @Prop
    public float fontSize = 14.5f;

    @Embedded
    public TestPreferences preferences = new TestPreferences();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TestSettings that = (TestSettings) o;
        return darkMode == that.darkMode &&
                Float.compare(that.fontSize, fontSize) == 0 &&
                Objects.equals(preferences, that.preferences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(darkMode, fontSize, preferences);
    }
}