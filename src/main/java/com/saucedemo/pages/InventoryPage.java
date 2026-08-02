package com.saucedemo.pages;

import com.saucedemo.pages.base.BasePage;
import com.saucedemo.pages.components.HeaderComponent;
import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.LoggerUtility;
import org.openqa.selenium.By;

import java.util.List;

/**
 * InventoryPage encapsulates elements and actions for the Products Inventory Page.
 */
public class InventoryPage extends BasePage {

    private static final String ITEM_ANCESTOR_CLASS = "inventory_item";

    private final HeaderComponent headerComponent;

    // Locators
    private final By productSortDropdown = By.cssSelector(".product_sort_container");
    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By itemPrices = By.cssSelector(".inventory_item_price");

    public InventoryPage() {
        super(PAGE_TITLE);
        this.headerComponent = new HeaderComponent();
    }

    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }

    /**
     * Checks whether InventoryPage is loaded cleanly.
     */
    public boolean isPageLoaded() {
        return getPageTitleText().equalsIgnoreCase("Products");
    }

    /**
     * Adds an item to the cart by matching exact item title string.
     * @param itemName Exact name of product (e.g. "Sauce Labs Backpack")
     */
    public InventoryPage addItemToCartByName(String itemName) {
        LoggerUtility.info("Adding item to cart: " + itemName);
        ElementActions.click(itemAncestorLocator(itemName, ITEM_ANCESTOR_CLASS, "//button"));
        return this;
    }

    /**
     * Removes an item from the cart by matching exact item title string.
     * @param itemName Exact name of product
     */
    public InventoryPage removeItemFromCartByName(String itemName) {
        LoggerUtility.info("Removing item from cart: " + itemName);
        ElementActions.click(itemAncestorLocator(itemName, ITEM_ANCESTOR_CLASS, "//button"));
        return this;
    }

    /**
     * Selects product sorting option from dropdown.
     * Options: "az" (Name A to Z), "za" (Name Z to A), "lohi" (Price low to high), "hilo" (Price high to low).
     */
    public InventoryPage selectSortOptionByValue(String value) {
        LoggerUtility.info("Selecting product sort option value: " + value);
        ElementActions.selectByValue(productSortDropdown, value);
        return this;
    }

    /**
     * Retrieves all product title names displayed on the page.
     */
    public List<String> getItemNamesList() {
        return ElementActions.getTextList(itemNames);
    }

    /**
     * Retrieves all product prices displayed on the page as doubles.
     */
    public List<Double> getItemPricesList() {
        return ElementActions.getTextList(itemPrices).stream()
                .map(text -> text.replace("$", "").trim())
                .map(Double::parseDouble)
                .toList();
    }

    /**
     * Retrieves price of a specific product by name.
     */
    public double getItemPriceByName(String itemName) {
        By priceLocator = itemAncestorLocator(itemName, ITEM_ANCESTOR_CLASS, "//div[@class='inventory_item_price']");
        return ElementActions.getNumericValue(priceLocator);
    }
}
