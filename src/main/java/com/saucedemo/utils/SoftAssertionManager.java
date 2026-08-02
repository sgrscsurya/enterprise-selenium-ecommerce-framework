package com.saucedemo.utils;

import org.testng.asserts.SoftAssert;

/**
 * SoftAssertionManager provides a ThreadLocal-scoped {@link SoftAssert} instance per test
 * thread, mirroring the ThreadLocal isolation model already used by
 * {@link com.saucedemo.driver.DriverFactory} for WebDriver instances.
 *
 * <p>Rationale: a plain {@code new SoftAssert()} local variable works fine for a single
 * test method, but a shared/static {@link SoftAssert} would leak assertion failures across
 * threads during parallel execution (testng.xml runs suites with {@code parallel="classes"}).
 * This class gives callers a consistently-scoped instance without requiring every test
 * method to manage its own local variable.
 */
public class SoftAssertionManager {

    private static final ThreadLocal<SoftAssert> SOFT_ASSERT_THREAD_LOCAL = ThreadLocal.withInitial(SoftAssert::new);

    private SoftAssertionManager() {
        // Prevent instantiation
    }

    /** Returns the current thread's {@link SoftAssert} instance, creating one on first access. */
    public static SoftAssert get() {
        return SOFT_ASSERT_THREAD_LOCAL.get();
    }

    public static void assertEquals(Object actual, Object expected, String message) {
        get().assertEquals(actual, expected, message);
    }

    public static void assertTrue(boolean condition, String message) {
        get().assertTrue(condition, message);
    }

    public static void assertFalse(boolean condition, String message) {
        get().assertFalse(condition, message);
    }

    /**
     * Verifies all accumulated soft assertions for the current thread, then clears the
     * ThreadLocal entry so a subsequent test method on the same thread starts fresh.
     *
     * @throws AssertionError aggregating all failures recorded on this thread, if any occurred
     */
    public static void assertAll() {
        try {
            get().assertAll();
        } finally {
            SOFT_ASSERT_THREAD_LOCAL.remove();
        }
    }
}
