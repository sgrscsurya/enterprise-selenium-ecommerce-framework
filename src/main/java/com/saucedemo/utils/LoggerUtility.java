package com.saucedemo.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LoggerUtility provides a unified, thread-safe wrapper around Log4j2
 * to standardize framework logging output across tests and utilities.
 */
public class LoggerUtility {

    private LoggerUtility() {
        // Prevent instantiation
    }

    /**
     * Obtains the Log4j Logger instance for the calling class.
     */
    private static Logger getLogger() {
        String callingClassName = Thread.currentThread().getStackTrace()[3].getClassName();
        return LogManager.getLogger(callingClassName);
    }

    public static void info(String message) {
        getLogger().info(message);
    }

    public static void warn(String message) {
        getLogger().warn(message);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

    public static void error(String message, Throwable throwable) {
        getLogger().error(message, throwable);
    }

    public static void debug(String message) {
        getLogger().debug(message);
    }

    public static void startTestCase(String testName) {
        info("======================================================================");
        info("   STARTING TEST CASE: " + testName);
        info("======================================================================");
    }

    public static void endTestCase(String testName) {
        info("======================================================================");
        info("   ENDING TEST CASE: " + testName);
        info("======================================================================");
    }
}
