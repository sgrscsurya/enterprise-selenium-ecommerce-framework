package com.saucedemo.pages;

import com.saucedemo.pages.base.BasePage;
import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.LoggerUtility;
import org.openqa.selenium.By;

/**
 * CheckoutCompletePage encapsulates the final confirmation screen displayed after a successful order.
 */
public class CheckoutCompletePage extends BasePage {

    // Locators
    private final By completeHeader = By.cssSelector(".complete-header");
    private final By completeText = By.cssSelector(".complete-text");
    private final By backHomeButton = By.id("back-to-products");

    public CheckoutCompletePage() {
        super(By.id("back-to-products"));
    }

    /**
     * Retrieves header confirmation message (e.g., "Thank you for your order!").
     */
    public String getCompleteHeaderMessage() {
        String header = ElementActions.getText(completeHeader);
        LoggerUtility.info("Retrieved complete header message: " + header);
        return header;
    }

    /**
     * Retrieves body dispatch confirmation text.
     */
    public String getCompleteText() {
        String text = ElementActions.getText(completeText);
        LoggerUtility.info("Retrieved complete body text: " + text);
        return text;
    }

    /**
     * Checks whether the success header is displayed.
     */
    public boolean isOrderCompleteHeaderDisplayed() {
        return ElementActions.isDisplayed(completeHeader);
    }

    /**
     * Clicks 'Back Home' button to reset navigation and return to InventoryPage.
     */
    public InventoryPage clickBackHome() {
        LoggerUtility.info("Clicking Back Home button...");
        ElementActions.click(backHomeButton);
        return new InventoryPage();
    }
}
