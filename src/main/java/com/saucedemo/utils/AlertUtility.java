package com.saucedemo.utils;

import com.saucedemo.driver.DriverFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

/**
 * AlertUtility encapsulates routines for interacting with JavaScript Alert,
 * Confirm, and Prompt popups cleanly and safely.
 */
public class AlertUtility {

    private AlertUtility() {
        // Prevent instantiation
    }

    private static Alert waitForAlert() {
        return WaitUtility.waitForAlertPresent();
    }

    /**
     * Accepts (clicks OK on) the active JavaScript alert.
     */
    public static void acceptAlert() {
        LoggerUtility.info("Accepting JavaScript alert dialog...");
        waitForAlert().accept();
    }

    /**
     * Dismisses (clicks Cancel on) the active JavaScript alert.
     */
    public static void dismissAlert() {
        LoggerUtility.info("Dismissing JavaScript alert dialog...");
        waitForAlert().dismiss();
    }

    /**
     * Retrieves text message from active alert dialog.
     */
    public static String getAlertText() {
        String text = waitForAlert().getText();
        LoggerUtility.info("Retrieved alert message text: " + text);
        return text;
    }

    /**
     * Sends input text string into a JavaScript prompt dialog before accepting.
     */
    public static void sendKeysToAlert(String input) {
        LoggerUtility.info("Sending keys to alert prompt dialog: " + input);
        Alert alert = waitForAlert();
        alert.sendKeys(input);
        alert.accept();
    }

    /**
     * Checks if a JavaScript alert is currently present.
     */
    public static boolean isAlertPresent() {
        try {
            DriverFactory.getDriver().switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }
}
