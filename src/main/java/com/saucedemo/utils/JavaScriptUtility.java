package com.saucedemo.utils;

import com.saucedemo.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * JavaScriptUtility provides reusable JavaScript execution operations for custom UI interactions,
 * smooth scrolling, element highlighting, and forced clicks.
 */
public class JavaScriptUtility {

    private JavaScriptUtility() {
        // Prevent instantiation
    }

    private static JavascriptExecutor getJSExecutor() {
        WebDriver driver = DriverFactory.getDriver();
        return (JavascriptExecutor) driver;
    }

    /**
     * Forces a click event on the target web element via JavaScript.
     */
    public static void clickElementViaJS(WebElement element) {
        LoggerUtility.info("Clicking element via JavaScript...");
        getJSExecutor().executeScript("arguments[0].click();", element);
    }

    public static void clickElementViaJS(By locator) {
        WebElement element = DriverFactory.getDriver().findElement(locator);
        clickElementViaJS(element);
    }

    /**
     * Scrolls the viewport until the target element is aligned into view.
     */
    public static void scrollToElement(WebElement element) {
        LoggerUtility.info("Scrolling element into view via JavaScript...");
        getJSExecutor().executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public static void scrollToElement(By locator) {
        WebElement element = DriverFactory.getDriver().findElement(locator);
        scrollToElement(element);
    }

    /**
     * Scrolls down to the bottom of the page.
     */
    public static void scrollToBottom() {
        LoggerUtility.info("Scrolling to page bottom via JavaScript...");
        getJSExecutor().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scrolls up to the top of the page.
     */
    public static void scrollToTop() {
        LoggerUtility.info("Scrolling to page top via JavaScript...");
        getJSExecutor().executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Highlights an element temporarily with a bright yellow background and red border for debugging.
     */
    public static void highlightElement(WebElement element) {
        try {
            getJSExecutor().executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", element);
            Thread.sleep(200);
            getJSExecutor().executeScript("arguments[0].setAttribute('style', 'border: none;');", element);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Directly sets value attribute of an input element via JavaScript.
     */
    public static void setValueViaJS(WebElement element, String value) {
        LoggerUtility.info("Setting input value via JavaScript...");
        getJSExecutor().executeScript("arguments[0].value='" + value + "';", element);
    }

    /**
     * Obtains page title via JavaScript.
     */
    public static String getPageTitleViaJS() {
        return getJSExecutor().executeScript("return document.title;").toString();
    }

    /**
     * Obtains page inner text via JavaScript.
     */
    public static String getInnerTextViaJS() {
        return getJSExecutor().executeScript("return document.documentElement.innerText;").toString();
    }
}
