package com.sammwy.classserializer.tests.entity;

import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.customs.CustomData;

public class UserWithCustomData {
    @Prop
    public String name = "Alice";

    @Prop
    public CustomData data = new CustomData("XYZ789", 1649712000000L);
}
