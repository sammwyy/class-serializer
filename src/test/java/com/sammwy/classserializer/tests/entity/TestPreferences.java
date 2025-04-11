package com.sammwy.classserializer.tests.entity;

import java.util.Objects;

import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.annotations.Serializable;

@Serializable
public class TestPreferences {
    @Prop
    public String theme = "default";

    @Prop
    public boolean notifications = true;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TestPreferences that = (TestPreferences) o;
        return notifications == that.notifications &&
                Objects.equals(theme, that.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theme, notifications);
    }
}