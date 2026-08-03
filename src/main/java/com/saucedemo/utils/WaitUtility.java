package com.saucedemo.utils;

import com.saucedemo.config.ConfigReader;
import com.saucedemo.driver.DriverFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * WaitUtility provides standardized Explicit and Fluent Wait routines for web element interactions,
 * eliminating hardcoded thread sleeps and flaky execution.
 */
public class WaitUtility {

    private WaitUtility() {
        // Prevent instantiation
    }

    /**
     * Pauses thread execution safely for specified milliseconds (used for UI animations).
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static WebDriverWait getWait() {
        WebDriver driver = DriverFactory.getDriver();
        int timeoutSeconds = ConfigReader.getIntProperty("explicit.wait");
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    private static WebDriverWait getWait(int customTimeoutInSeconds) {
        WebDriver driver = DriverFactory.getDriver();
        return new WebDriverWait(driver, Duration.ofSeconds(customTimeoutInSeconds));
    }

    /**
     * Waits for element visibility with custom timeout, returning boolean status without throwing.
     */
    public static boolean waitForVisibilityWithTimeout(By locator, int timeoutInSeconds) {
        try {
            getWait(timeoutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fluent Wait implementation with custom timeout, polling interval, and ignored exceptions.
     */
    public static WebElement waitForElementWithFluentWait(By locator, int timeoutInSeconds, int pollingInMillis) {
        WebDriver driver = DriverFactory.getDriver();
        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(pollingInMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        return fluentWait.until(d -> d.findElement(locator));
    }

    /**
     * Fluent Wait with default explicit wait timeout and 500ms polling.
     */
    public static WebElement waitForElementWithFluentWait(By locator) {
        int defaultTimeout = ConfigReader.getIntProperty("explicit.wait");
        return waitForElementWithFluentWait(locator, defaultTimeout, 500);
    }

    public static WebElement waitForVisibility(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisibility(WebElement element) {
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickability(By locator) {
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickability(WebElement element) {
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForPresence(By locator) {
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static List<WebElement> waitForAllElementsVisible(By locator) {
        return getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    public static boolean waitForTextPresent(By locator, String text) {
        return getWait().until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForUrlContains(String fraction) {
        return getWait().until(ExpectedConditions.urlContains(fraction));
    }

    public static void waitForFrameAndSwitch(By locator) {
        getWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    /**
     * Waits for a JavaScript alert/confirm/prompt dialog to become present and returns it.
     * Centralizes the wait construction previously duplicated in {@link AlertUtility}.
     */
    public static Alert waitForAlertPresent() {
        return getWait().until(ExpectedConditions.alertIsPresent());
    }
}
