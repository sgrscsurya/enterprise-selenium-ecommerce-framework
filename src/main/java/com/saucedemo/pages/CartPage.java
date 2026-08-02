package com.saucedemo.pages;

import com.saucedemo.pages.base.BasePage;
import com.saucedemo.pages.components.HeaderComponent;
import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.LoggerUtility;
import org.openqa.selenium.By;

import java.util.List;

/**
 * CartPage encapsulates elements and actions for the Shopping Cart Page.
 */
public class CartPage extends BasePage {

    private static final String ITEM_ANCESTOR_CLASS = "cart_item";

    private final HeaderComponent headerComponent;

    // Locators
    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");

    public CartPage() {
        super(PAGE_TITLE);
        this.headerComponent = new HeaderComponent();
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    /**
     * Retrieves all product names currently in the cart.
     */
    public List<String> getItemNamesList() {
        return ElementActions.getTextList(itemNames);
    }

    /**
     * Removes an item from the cart by product title name.
     */
    public CartPage removeItemByName(String itemName) {
        LoggerUtility.info("Removing item from cart page: " + itemName);
        ElementActions.click(itemAncestorLocator(itemName, ITEM_ANCESTOR_CLASS, "//button"));
        return this;
    }

    /**
     * Clicks 'Continue Shopping' and returns to InventoryPage.
     */
    public InventoryPage clickContinueShopping() {
        LoggerUtility.info("Clicking 'Continue Shopping' button...");
        ElementActions.click(continueShoppingButton);
        return new InventoryPage();
    }

    /**
     * Clicks 'Checkout' and navigates to Step 1 CheckoutPage.
     */
    public CheckoutPage clickCheckout() {
        LoggerUtility.info("Clicking 'Checkout' button...");
        ElementActions.click(checkoutButton);
        return new CheckoutPage();
    }
}
