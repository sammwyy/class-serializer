package com.sammwy.classserializer.tests.entity;

import com.sammwy.classserializer.tests.annotations.Prop;

public class PrimitiveTypes {
    @Prop
    public Integer intValue = 42;
    @Prop
    public Long longValue = 123456789012L;
    @Prop
    public Float floatValue = 3.14f;
    @Prop
    public Double doubleValue = 2.71828;
    @Prop
    public Boolean boolValue = true;
    @Prop
    public Short shortValue = 128;
    @Prop
    public Byte byteValue = 8;
    @Prop
    public Character charValue = 'A';
}