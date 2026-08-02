package com.saucedemo.driver;

import org.openqa.selenium.WebDriver;

/**
 * DriverFactory manages ThreadLocal instances of WebDriver.
 * This guarantees strict thread safety during parallel TestNG test execution.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {
        // Prevent instantiation
    }

    /**
     * Gets the ThreadLocal WebDriver instance for the current thread.
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Sets the ThreadLocal WebDriver instance for the current thread.
     * @param driver Initialized WebDriver instance
     */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    /**
     * Quits the WebDriver instance and removes it from ThreadLocal storage
     * to avoid memory leaks.
     */
    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }
}
