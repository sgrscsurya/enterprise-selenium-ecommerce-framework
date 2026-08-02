package com.saucedemo.utils;

import com.saucedemo.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * ActionUtility provides reusable mouse hover, double-click, right-click,
 * drag-and-drop, and keyboard interaction routines using Selenium Actions class.
 */
public class ActionUtility {

    private ActionUtility() {
        // Prevent instantiation
    }

    private static Actions getActions() {
        WebDriver driver = DriverFactory.getDriver();
        return new Actions(driver);
    }

    /**
     * Performs mouse hover movement over the target element.
     */
    public static void hoverOverElement(WebElement element) {
        LoggerUtility.info("Hovering mouse over target element...");
        getActions().moveToElement(element).perform();
    }

    public static void hoverOverElement(By locator) {
        WebElement element = DriverFactory.getDriver().findElement(locator);
        hoverOverElement(element);
    }

    /**
     * Performs double-click on element.
     */
    public static void doubleClick(WebElement element) {
        LoggerUtility.info("Double-clicking target element...");
        getActions().doubleClick(element).perform();
    }

    /**
     * Performs right-click / context-click on element.
     */
    public static void rightClick(WebElement element) {
        LoggerUtility.info("Right-clicking target element...");
        getActions().contextClick(element).perform();
    }

    /**
     * Performs drag and drop from source element to target element.
     */
    public static void dragAndDrop(WebElement source, WebElement target) {
        LoggerUtility.info("Dragging source element and dropping onto target...");
        getActions().dragAndDrop(source, target).perform();
    }

    /**
     * Sends ENTER key to active focused element.
     */
    public static void pressEnterKey() {
        LoggerUtility.info("Pressing ENTER key via Actions...");
        getActions().sendKeys(Keys.ENTER).perform();
    }

    /**
     * Sends TAB key to active focused element.
     */
    public static void pressTabKey() {
        LoggerUtility.info("Pressing TAB key via Actions...");
        getActions().sendKeys(Keys.TAB).perform();
    }

    /**
     * Types characters sequentially using Actions.
     */
    public static void sendKeysWithActions(WebElement element, String text) {
        LoggerUtility.info("Sending keys with Actions: " + text);
        getActions().sendKeys(element, text).perform();
    }

    /**
     * Performs keyboard key combination (e.g. CTRL+A, CTRL+C, CTRL+V).
     */
    public static void keyCombination(Keys modifierKey, String keyChar) {
        LoggerUtility.info("Executing key combination: " + modifierKey.name() + " + " + keyChar);
        getActions().keyDown(modifierKey).sendKeys(keyChar).keyUp(modifierKey).perform();
    }
}
