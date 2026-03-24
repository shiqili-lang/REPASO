package org.junit.jupiter.api;

import org.junit.jupiter.api.function.Executable;

public final class Assertions {
    private Assertions() {}

    public static void assertTrue(boolean condition) {
        if (!condition) {
            fail("Expected condition to be true");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            fail("Expected condition to be false");
        }
    }

    public static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            fail("Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            fail("Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                return expectedType.cast(t);
            }
            fail("Expected exception <" + expectedType.getName() + "> but was <" + t.getClass().getName() + ">");
        }
        fail("Expected exception <" + expectedType.getName() + "> but nothing was thrown");
        return null;
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }
}
