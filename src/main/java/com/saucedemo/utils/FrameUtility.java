package com.saucedemo.utils;

import com.saucedemo.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * FrameUtility encapsulates switching contexts into iFrames and switching back to parent DOM containers.
 */
public class FrameUtility {

    private FrameUtility() {
        // Prevent instantiation
    }

    private static WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /**
     * Switches to frame by 0-indexed integer.
     */
    public static void switchToFrameByIndex(int index) {
        LoggerUtility.info("Switching to iframe by index: " + index);
        getDriver().switchTo().frame(index);
    }

    /**
     * Switches to frame by string Name or ID attribute.
     */
    public static void switchToFrameByNameOrId(String nameOrId) {
        LoggerUtility.info("Switching to iframe by Name or ID: " + nameOrId);
        getDriver().switchTo().frame(nameOrId);
    }

    /**
     * Switches to frame by WebElement locator.
     */
    public static void switchToFrameByElement(WebElement frameElement) {
        LoggerUtility.info("Switching to iframe by WebElement...");
        getDriver().switchTo().frame(frameElement);
    }

    public static void switchToFrameByLocator(By locator) {
        WebElement frameElement = WaitUtility.waitForPresence(locator);
        switchToFrameByElement(frameElement);
    }

    /**
     * Switches context back to the top-level document default content.
     */
    public static void switchToDefaultContent() {
        LoggerUtility.info("Switching back to default document content...");
        getDriver().switchTo().defaultContent();
    }

    /**
     * Switches context to the immediate parent frame.
     */
    public static void switchToParentFrame() {
        LoggerUtility.info("Switching back to parent frame...");
        getDriver().switchTo().parentFrame();
    }
}
