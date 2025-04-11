package com.sammwy.classserializer.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.tests.annotations.Embedded;
import com.sammwy.classserializer.tests.annotations.Prop;
import com.sammwy.classserializer.tests.entity.PrimitiveTypes;
import com.sammwy.classserializer.tests.entity.WithLists;
import com.sammwy.classserializer.tests.entity.WithMaps;

@Nested
@DisplayName("Data types")
public class DataTypesTest {
    private ClassSerializer serializer;

    @BeforeEach
    public void setUp() {
        serializer = new ClassSerializer()
                .fieldPredicate((field, obj) -> {
                    if (field.isAnnotationPresent(Prop.class)) {
                        return field.getName();
                    } else if (field.isAnnotationPresent(Embedded.class)) {
                        return field.getName();
                    }
                    return null;
                });
    }

    @Test
    @DisplayName("Handles primitive types")
    public void handlePrimitiveTypes() {
        PrimitiveTypes obj = new PrimitiveTypes();

        Map<String, Object> serialized = serializer.serialize(obj);

        assertEquals(42, serialized.get("intValue"));
        assertEquals(123456789012L, serialized.get("longValue"));
        assertEquals(3.14f, serialized.get("floatValue"));
        assertEquals(2.71828, serialized.get("doubleValue"));
        assertEquals(true, serialized.get("boolValue"));
        assertEquals((short) 128, serialized.get("shortValue"));
        assertEquals((byte) 8, serialized.get("byteValue"));
        assertEquals('A', serialized.get("charValue"));

        PrimitiveTypes deserialized = serializer.deserialize(PrimitiveTypes.class, serialized);

        assertEquals(42, deserialized.intValue);
        assertEquals(123456789012L, deserialized.longValue);
        assertEquals(3.14f, deserialized.floatValue);
        assertEquals(2.71828, deserialized.doubleValue);
        assertEquals(true, deserialized.boolValue);
        assertEquals((short) 128, deserialized.shortValue);
        assertEquals((byte) 8, deserialized.byteValue);
        assertEquals('A', deserialized.charValue);
    }

    @Test
    @DisplayName("Handle primitive wrappers")
    public void handlePrimitiveWrappers() {
        PrimitiveTypes obj = new PrimitiveTypes();

        Map<String, Object> serialized = serializer.serialize(obj);
        PrimitiveTypes deserialized = serializer.deserialize(PrimitiveTypes.class, serialized);

        assertEquals(Integer.valueOf(42), deserialized.intValue);
        assertEquals(Long.valueOf(123456789012L), deserialized.longValue);
        assertEquals(Float.valueOf(3.14f), deserialized.floatValue);
        assertEquals(Double.valueOf(2.71828), deserialized.doubleValue);
        assertEquals(Boolean.TRUE, deserialized.boolValue);
        assertEquals(Short.valueOf((short) 128), deserialized.shortValue);
        assertEquals(Byte.valueOf((byte) 8), deserialized.byteValue);
        assertEquals(Character.valueOf('A'), deserialized.charValue);
    }

    @Test
    @DisplayName("Handle lists")
    public void handleLists() {
        WithLists obj = new WithLists();
        obj.objects.get(1).theme = "dark";

        Map<String, Object> serialized = serializer.serialize(obj);

        assertTrue(serialized.get("strings") instanceof List);
        assertTrue(serialized.get("numbers") instanceof List);
        assertTrue(serialized.get("nested") instanceof List);
        assertTrue(serialized.get("objects") instanceof List);

        @SuppressWarnings("unchecked")
        List<Object> objects = (List<Object>) serialized.get("objects");
        assertTrue(objects.get(0) instanceof Map);
        assertTrue(objects.get(1) instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> secondObject = (Map<String, Object>) objects.get(1);
        assertEquals("dark", secondObject.get("theme"));

        WithLists deserialized = serializer.deserialize(WithLists.class, serialized);

        assertEquals(Arrays.asList("a", "b", "c"), deserialized.strings);
        assertEquals(Arrays.asList(1, 2, 3), deserialized.numbers);
        assertEquals("default", deserialized.objects.get(0).theme);
        assertEquals("dark", deserialized.objects.get(1).theme);
    }

    @Test
    @DisplayName("Handle maps")
    public void handleMaps() {
        WithMaps obj = new WithMaps();

        Map<String, Object> serialized = serializer.serialize(obj);

        assertTrue(serialized.get("strings") instanceof Map);
        assertTrue(serialized.get("numbers") instanceof Map);
        assertTrue(serialized.get("nested") instanceof Map);
        assertTrue(serialized.get("objects") instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> objects = (Map<String, Object>) serialized.get("objects");
        assertTrue(objects.get("default") instanceof Map);
        assertTrue(objects.get("dark") instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> darkTheme = (Map<String, Object>) objects.get("dark");
        assertEquals("dark", darkTheme.get("theme"));

        WithMaps deserialized = serializer.deserialize(WithMaps.class, serialized);

        assertEquals("value-a", deserialized.strings.get("a"));
        assertEquals("value-b", deserialized.strings.get("b"));
        assertEquals(Integer.valueOf(1), deserialized.numbers.get("one"));
        assertEquals(Integer.valueOf(2), deserialized.numbers.get("two"));
        assertEquals(Arrays.asList("x", "y", "z"), deserialized.nested.get("letters"));
        assertEquals("default", deserialized.objects.get("default").theme);
        assertEquals("dark", deserialized.objects.get("dark").theme);
    }
}