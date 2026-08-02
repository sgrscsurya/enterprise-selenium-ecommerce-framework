package com.saucedemo.pages;

import com.saucedemo.pages.base.BasePage;
import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.JavaScriptUtility;
import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.WaitUtility;
import org.openqa.selenium.By;

import java.util.List;

/**
 * CheckoutOverviewPage encapsulates Step 2 of the checkout process: Summary & Total Review.
 */
public class CheckoutOverviewPage extends BasePage {

    // Locators
    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By summarySubtotal = By.cssSelector(".summary_subtotal_label");
    private final By summaryTax = By.cssSelector(".summary_tax_label");
    private final By summaryTotal = By.cssSelector(".summary_total_label");
    private final By finishButton = By.id("finish");
    private final By cancelButton = By.id("cancel");

    public CheckoutOverviewPage() {
        super(By.id("finish"));
    }

    public List<String> getItemNamesList() {
        return ElementActions.getTextList(itemNames);
    }

    /**
     * Extracts numerical subtotal value (e.g. "Item total: $29.99" -> 29.99).
     */
    public double getSubtotal() {
        double value = ElementActions.getNumericValue(summarySubtotal);
        LoggerUtility.info("Retrieved subtotal: $" + value);
        return value;
    }

    /**
     * Extracts numerical tax value (e.g. "Tax: $2.40" -> 2.40).
     */
    public double getTax() {
        double value = ElementActions.getNumericValue(summaryTax);
        LoggerUtility.info("Retrieved tax: $" + value);
        return value;
    }

    /**
     * Extracts numerical final total value (e.g. "Total: $32.39" -> 32.39).
     */
    public double getTotal() {
        double value = ElementActions.getNumericValue(summaryTotal);
        LoggerUtility.info("Retrieved final total: $" + value);
        return value;
    }

    /**
     * Clicks 'Finish' button and proceeds to CheckoutCompletePage.
     */
    public CheckoutCompletePage clickFinish() {
        LoggerUtility.info("Clicking Finish button...");
        JavaScriptUtility.scrollToElement(finishButton);
        ElementActions.click(finishButton);
        WaitUtility.waitForUrlContains("checkout-complete");
        return new CheckoutCompletePage();
    }

    /**
     * Clicks 'Cancel' button and returns to InventoryPage.
     */
    public InventoryPage clickCancel() {
        LoggerUtility.info("Clicking Cancel button on Overview page...");
        ElementActions.click(cancelButton);
        return new InventoryPage();
    }
}
