package com.saucedemo.pages;

import com.saucedemo.pages.base.BasePage;
import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.LoggerUtility;
import org.openqa.selenium.By;

/**
 * LoginPage encapsulates elements and actions for the SauceDemo Login Page (https://www.saucedemo.com).
 */
public class LoginPage extends BasePage {

    // Locators
    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessageContainer = By.cssSelector("h3[data-test='error']");

    public LoginPage() {
        super(By.id("login-button"));
    }

    /**
     * Enters username into input field.
     */
    public LoginPage enterUsername(String username) {
        LoggerUtility.info("Entering username: " + username);
        ElementActions.type(usernameInput, username);
        return this;
    }

    /**
     * Enters password into input field.
     */
    public LoginPage enterPassword(String password) {
        LoggerUtility.info("Entering password...");
        ElementActions.type(passwordInput, password);
        return this;
    }

    /**
     * Clicks the login button and returns the next page instance (InventoryPage).
     */
    public InventoryPage clickLoginButton() {
        LoggerUtility.info("Clicking Login button...");
        ElementActions.click(loginButton);
        return new InventoryPage();
    }

    /**
     * Helper method to perform login for invalid attempts where error is expected.
     */
    public LoginPage clickLoginExpectingFailure() {
        LoggerUtility.info("Clicking Login button (expecting error)...");
        ElementActions.click(loginButton);
        return this;
    }

    /**
     * Fluent helper method to perform complete login workflow.
     */
    public InventoryPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }

    /**
     * Retrieves displayed error message string if authentication fails.
     */
    public String getErrorMessage() {
        String errorText = ElementActions.getText(errorMessageContainer);
        LoggerUtility.info("Retrieved login error message: " + errorText);
        return errorText;
    }

    /**
     * Checks if error message container is currently displayed.
     */
    public boolean isErrorMessageDisplayed() {
        return ElementActions.isDisplayed(errorMessageContainer);
    }
}
