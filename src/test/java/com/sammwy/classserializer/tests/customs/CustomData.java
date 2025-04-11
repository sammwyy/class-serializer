package com.sammwy.classserializer.tests.customs;

import java.util.Objects;

import com.sammwy.classserializer.tests.annotations.Prop;

public class CustomData {
    @Prop
    private String code;
    @Prop
    private long timestamp;

    public CustomData(String code, long timestamp) {
        this.code = code;
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomData that = (CustomData) o;
        return timestamp == that.timestamp &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, timestamp);
    }
}