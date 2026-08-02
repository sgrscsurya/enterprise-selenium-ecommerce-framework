package com.saucedemo.utils;

import com.saucedemo.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ElementActions is the single facade page objects use to interact with elements.
 *
 * <p>Phase 8 Enterprise Enhancement: Centralizes the wait-then-act pattern
 * (find + synchronize + interact) that was previously duplicated across every page
 * class (each page independently called {@link WaitUtility} and then manipulated the
 * returned {@link WebElement}). Page objects should prefer these methods over calling
 * {@link WaitUtility} or {@link org.openqa.selenium.WebDriver} directly.
 *
 * <p>SOLID – Single Responsibility: This class owns element interaction only; waiting
 * strategy lives in {@link WaitUtility}, driver lifecycle lives in {@link DriverFactory}.
 */
public class ElementActions {

    private ElementActions() {
        // Prevent instantiation
    }

    /** Waits for clickability, then clicks the element (with JS click fallback). */
    public static void click(By locator) {
        WebElement element = WaitUtility.waitForClickability(locator);
        try {
            element.click();
        } catch (Exception e) {
            LoggerUtility.warn("Standard click failed for locator '" + locator + "', falling back to JavaScript click: " + e.getMessage());
            JavaScriptUtility.clickElementViaJS(element);
        }
    }

    /**
     * Blocks until the element becomes visible, throwing a Selenium timeout exception
     * if it never does. Used by {@link com.saucedemo.pages.base.BasePage} to confirm a
     * page has finished loading before construction completes.
     */
    public static void waitUntilVisible(By locator) {
        WaitUtility.waitForVisibility(locator);
    }

    /** Waits for visibility, clears any existing value, then types the given text. */
    public static void type(By locator, String text) {
        WebElement element = WaitUtility.waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    /** Waits for visibility, then returns the trimmed visible text. */
    public static String getText(By locator) {
        return WaitUtility.waitForVisibility(locator).getText().trim();
    }

    /**
     * Waits for visibility, strips non-numeric characters (e.g. "Total: $32.39" -> 32.39)
     * and parses the result as a double. Used for price/summary label parsing.
     */
    public static double getNumericValue(By locator) {
        String raw = getText(locator);
        return Double.parseDouble(raw.replaceAll("[^0-9.]", ""));
    }

    /** Returns trimmed text for every element matched by the locator. */
    public static List<String> getTextList(By locator) {
        return WaitUtility.waitForAllElementsVisible(locator).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Safely checks whether an element is displayed, returning {@code false}
     * instead of throwing when the element cannot be found in time.
     */
    public static boolean isDisplayed(By locator) {
        try {
            return WaitUtility.waitForVisibility(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Checks for element presence in the DOM without waiting (e.g. optional badges). */
    public static boolean isPresent(By locator) {
        return !DriverFactory.getDriver().findElements(locator).isEmpty();
    }

    /** Waits for visibility, then returns the requested attribute value. */
    public static String getAttribute(By locator, String attributeName) {
        return WaitUtility.waitForVisibility(locator).getAttribute(attributeName);
    }

    /** Waits for visibility of a &lt;select&gt; element, then selects an option by its value attribute. */
    public static void selectByValue(By locator, String value) {
        new Select(WaitUtility.waitForVisibility(locator)).selectByValue(value);
    }

    /** Waits for visibility of a &lt;select&gt; element, then selects an option by its visible text. */
    public static void selectByVisibleText(By locator, String text) {
        new Select(WaitUtility.waitForVisibility(locator)).selectByVisibleText(text);
    }
}
