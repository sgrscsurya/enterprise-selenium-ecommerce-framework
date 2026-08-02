package com.saucedemo.utils;

import com.saucedemo.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.Set;

/**
 * WindowUtility encapsulates routines for switching between browser windows,
 * child tabs, opening new windows/tabs, and managing parent window handles.
 */
public class WindowUtility {

    private WindowUtility() {
        // Prevent instantiation
    }

    private static WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /**
     * Gets the window handle string for the current active window.
     */
    public static String getCurrentWindowHandle() {
        return getDriver().getWindowHandle();
    }

    /**
     * Gets all open window handle strings.
     */
    public static Set<String> getAllWindowHandles() {
        return getDriver().getWindowHandles();
    }

    /**
     * Switches to window/tab by matching expected page title string.
     */
    public static boolean switchToWindowByTitle(String expectedTitle) {
        LoggerUtility.info("Switching to window with title containing: " + expectedTitle);
        String currentHandle = getCurrentWindowHandle();
        Set<String> allHandles = getAllWindowHandles();

        for (String handle : allHandles) {
            getDriver().switchTo().window(handle);
            if (getDriver().getTitle().toLowerCase().contains(expectedTitle.toLowerCase())) {
                LoggerUtility.info("Successfully switched to window with title: " + getDriver().getTitle());
                return true;
            }
        }
        LoggerUtility.warn("Could not find window matching title: " + expectedTitle);
        getDriver().switchTo().window(currentHandle);
        return false;
    }

    /**
     * Switches to window/tab by matching partial URL string.
     */
    public static boolean switchToWindowByUrl(String partialUrl) {
        LoggerUtility.info("Switching to window with URL containing: " + partialUrl);
        String currentHandle = getCurrentWindowHandle();
        Set<String> allHandles = getAllWindowHandles();

        for (String handle : allHandles) {
            getDriver().switchTo().window(handle);
            if (getDriver().getCurrentUrl().toLowerCase().contains(partialUrl.toLowerCase())) {
                LoggerUtility.info("Successfully switched to window with URL: " + getDriver().getCurrentUrl());
                return true;
            }
        }
        LoggerUtility.warn("Could not find window matching URL: " + partialUrl);
        getDriver().switchTo().window(currentHandle);
        return false;
    }

    /**
     * Opens a new browser tab and automatically switches focus to it.
     */
    public static void switchToNewTab() {
        LoggerUtility.info("Opening and switching to a new browser tab...");
        getDriver().switchTo().newWindow(WindowType.TAB);
    }

    /**
     * Opens a new browser window and automatically switches focus to it.
     */
    public static void switchToNewWindow() {
        LoggerUtility.info("Opening and switching to a new browser window...");
        getDriver().switchTo().newWindow(WindowType.WINDOW);
    }

    /**
     * Closes the current active window/tab and switches focus back to the parent window handle.
     */
    public static void closeCurrentAndSwitchToParent(String parentWindowHandle) {
        LoggerUtility.info("Closing current window and returning to parent handle...");
        getDriver().close();
        getDriver().switchTo().window(parentWindowHandle);
    }
}
