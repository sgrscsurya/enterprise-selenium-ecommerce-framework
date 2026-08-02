package com.saucedemo.utils;

import org.testng.Assert;

/**
 * AssertUtility wraps TestNG's hard {@link Assert} with framework-standard logging,
 * so every assertion outcome (pass or fail) is recorded via {@link LoggerUtility}
 * before the underlying {@link AssertionError} propagates (hard assertions abort the
 * test method immediately on the first failure, unlike {@link SoftAssertionManager}).
 */
public class AssertUtility {

    private AssertUtility() {
        // Prevent instantiation
    }

    public static void assertEquals(Object actual, Object expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
            LoggerUtility.info("ASSERT PASSED: " + message + " [expected=" + expected + ", actual=" + actual + "]");
        } catch (AssertionError e) {
            LoggerUtility.error("ASSERT FAILED: " + message + " [expected=" + expected + ", actual=" + actual + "]");
            throw e;
        }
    }

    public static void assertTrue(boolean condition, String message) {
        try {
            Assert.assertTrue(condition, message);
            LoggerUtility.info("ASSERT PASSED: " + message);
        } catch (AssertionError e) {
            LoggerUtility.error("ASSERT FAILED: " + message);
            throw e;
        }
    }

    public static void assertFalse(boolean condition, String message) {
        try {
            Assert.assertFalse(condition, message);
            LoggerUtility.info("ASSERT PASSED: " + message);
        } catch (AssertionError e) {
            LoggerUtility.error("ASSERT FAILED: " + message);
            throw e;
        }
    }

    public static void assertNotNull(Object object, String message) {
        try {
            Assert.assertNotNull(object, message);
            LoggerUtility.info("ASSERT PASSED: " + message);
        } catch (AssertionError e) {
            LoggerUtility.error("ASSERT FAILED: " + message);
            throw e;
        }
    }

    public static void fail(String message) {
        LoggerUtility.error("ASSERT FAILED: " + message);
        Assert.fail(message);
    }
}
