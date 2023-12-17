package com.sammwy.classserializer.utils;

public class AbstractMath {
    // Clamp.
    private static int i_clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static long i_clamp(long value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float i_clamp(float value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double i_clamp(double value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Object clamp(Object value, int min, int max) {
        if (value instanceof Integer) {
            return i_clamp((Integer) value, min, max);
        } else if (value instanceof Long) {
            return i_clamp((Long) value, min, max);
        } else if (value instanceof Float) {
            return i_clamp((Float) value, min, max);
        } else if (value instanceof Double) {
            return i_clamp((Double) value, min, max);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }

    // Check math type.
    public static boolean isMathType(Object value) {
        return value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double;
    }
}
