package com.saucedemo.pages;

import com.saucedemo.pages.base.BasePage;
import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.JavaScriptUtility;
import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.WaitUtility;
import org.openqa.selenium.By;

/**
 * CheckoutPage encapsulates Step 1 of the checkout process: Customer Information Form.
 */
public class CheckoutPage extends BasePage {

    // Locators
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessageContainer = By.cssSelector("h3[data-test='error']");

    public CheckoutPage() {
        super(By.id("continue"));
    }

    public CheckoutPage enterFirstName(String firstName) {
        LoggerUtility.info("Entering first name: " + firstName);
        ElementActions.type(firstNameInput, firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        LoggerUtility.info("Entering last name: " + lastName);
        ElementActions.type(lastNameInput, lastName);
        return this;
    }

    public CheckoutPage enterPostalCode(String postalCode) {
        LoggerUtility.info("Entering postal code: " + postalCode);
        ElementActions.type(postalCodeInput, postalCode);
        return this;
    }

    /**
     * Helper method to populate all checkout information inputs at once.
     */
    public CheckoutPage fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        return this;
    }

    /**
     * Clicks 'Continue' button and proceeds to Step 2 CheckoutOverviewPage.
     */
    public CheckoutOverviewPage clickContinue() {
        LoggerUtility.info("Clicking Continue button...");
        JavaScriptUtility.scrollToElement(continueButton);
        WaitUtility.waitForClickability(continueButton);
        JavaScriptUtility.clickElementViaJS(continueButton);
        try {
            WaitUtility.waitForUrlContains("checkout-step-two");
        } catch (Exception e) {
            LoggerUtility.warn("JS click did not trigger URL change immediately, retrying standard click...");
            ElementActions.click(continueButton);
            WaitUtility.waitForUrlContains("checkout-step-two");
        }
        return new CheckoutOverviewPage();
    }

    /**
     * Helper method for validation tests expecting form submission error.
     */
    public CheckoutPage clickContinueExpectingError() {
        LoggerUtility.info("Clicking Continue button (expecting validation error)...");
        ElementActions.click(continueButton);
        return this;
    }

    /**
     * Clicks 'Cancel' button and returns to CartPage.
     */
    public CartPage clickCancel() {
        LoggerUtility.info("Clicking Cancel button...");
        ElementActions.click(cancelButton);
        return new CartPage();
    }

    /**
     * Retrieves displayed error message string if input validation fails.
     */
    public String getErrorMessage() {
        String errorText = ElementActions.getText(errorMessageContainer);
        LoggerUtility.info("Retrieved checkout error message: " + errorText);
        return errorText;
    }
}
