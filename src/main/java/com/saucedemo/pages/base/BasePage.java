package com.saucedemo.pages.base;

import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.LoggerUtility;
import org.openqa.selenium.By;

/**
 * BasePage is the abstract superclass for every full-page Page Object in the framework.
 *
 * <p>Phase 8 Enterprise Enhancement: Centralizes the constructor synchronization pattern
 * ("wait for the page's ready indicator, then log initialization") that was previously
 * copy-pasted into every page class constructor, and the shared {@code .title} heading
 * lookup used by five of the seven SauceDemo pages.
 *
 * <p>SOLID – Open/Closed: New pages extend this class without modifying it.
 * SOLID – Liskov Substitution: Any {@code BasePage} subclass can be synchronized on and
 * queried for its title through this common contract.
 */
public abstract class BasePage {

    /** Shared SauceDemo page heading locator, reused by every page that renders a ".title" element. */
    protected static final By PAGE_TITLE = By.cssSelector(".title");

    /**
     * Waits for the page's ready indicator element to become visible, confirming the page
     * has finished loading before any subsequent interaction is attempted.
     *
     * @param readyIndicator locator of an element that only appears once this page is loaded
     */
    protected BasePage(By readyIndicator) {
        ElementActions.waitUntilVisible(readyIndicator);
        LoggerUtility.info(getClass().getSimpleName() + " initialized successfully.");
    }

    /**
     * Returns the trimmed text of the page's ".title" heading. Valid for any page that
     * renders the shared SauceDemo title element (all pages except the login screen).
     */
    public String getPageTitleText() {
        return ElementActions.getText(PAGE_TITLE);
    }

    /**
     * Builds an xpath locator for an action button/element scoped to the ancestor container
     * of a named catalog item (e.g. the "Add to cart" button for "Sauce Labs Backpack").
     *
     * <p>Consolidates the item-by-name xpath pattern previously duplicated across
     * {@code InventoryPage} and {@code CartPage}.
     *
     * @param itemName       exact product title text (e.g. "Sauce Labs Backpack")
     * @param ancestorClass  CSS class of the item's container div (e.g. "inventory_item", "cart_item")
     * @param relativeXPath  xpath fragment relative to the ancestor (e.g. "//button")
     * @return By locator scoped to the named item's ancestor container
     */
    protected static By itemAncestorLocator(String itemName, String ancestorClass, String relativeXPath) {
        return By.xpath("//div[text()='" + itemName + "']/ancestor::div[@class='" + ancestorClass + "']" + relativeXPath);
    }
}
