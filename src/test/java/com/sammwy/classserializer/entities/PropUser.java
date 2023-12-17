package com.sammwy.classserializer.entities;

import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.classserializer.transform.Clamp;
import com.sammwy.classserializer.transform.EmptyIsNull;
import com.sammwy.classserializer.transform.LowerCase;
import com.sammwy.classserializer.transform.Trim;
import com.sammwy.classserializer.transform.UpperCase;

public class PropUser {
    @Prop
    public String name;

    @Prop
    public int age;

    @Prop
    @Clamp(min = 0, max = 100)
    public int clamp;

    @Prop
    @EmptyIsNull
    public String emptyIsNull;

    @Prop
    @LowerCase
    public String lower;

    @Prop
    @UpperCase
    public String upper;

    @Prop
    @Trim
    public String trim;
}