package com.saucedemo.pages.components;

import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.JavaScriptUtility;
import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.WaitUtility;
import org.openqa.selenium.By;

/**
 * HeaderComponent encapsulates the shared navigation header elements present across
 * logged-in pages (Cart icon, badge count, side menu, logout).
 *
 * <p>Intentionally does NOT extend {@link com.saucedemo.pages.base.BasePage}: it is a
 * reusable component composed into full pages, not a page in its own right, so it has
 * no independent "ready indicator" to wait on (its host page already synchronizes on load).
 */
public class HeaderComponent {

    // Locators
    private final By shoppingCartLink = By.cssSelector(".shopping_cart_link");
    private final By shoppingCartBadge = By.cssSelector(".shopping_cart_badge");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutSidebarLink = By.id("logout_sidebar_link");
    private final By allItemsSidebarLink = By.id("inventory_sidebar_link");
    private final By resetAppStateSidebarLink = By.id("reset_sidebar_link");

    public HeaderComponent() {
        // Shared component constructor
    }

    /**
     * Gets the current badge count displayed on the cart icon.
     * @return Integer badge count, or 0 if no badge exists.
     */
    public int getCartBadgeCount() {
        if (!ElementActions.isPresent(shoppingCartBadge)) {
            // Wait up to 2 seconds for badge to appear if item was just added
            if (!WaitUtility.waitForVisibilityWithTimeout(shoppingCartBadge, 2)) {
                LoggerUtility.info("No cart badge present. Item count is 0.");
                return 0;
            }
        }
        String countText = ElementActions.getText(shoppingCartBadge);
        LoggerUtility.info("Cart badge count retrieved: " + countText);
        return Integer.parseInt(countText);
    }

    /**
     * Clicks the shopping cart icon to navigate to the Cart page.
     */
    public void clickShoppingCart() {
        LoggerUtility.info("Clicking Shopping Cart icon...");
        ElementActions.click(shoppingCartLink);
    }

    /**
     * Opens the slide-out hamburger side menu and waits for items to be visible.
     */
    public void openSideMenu() {
        LoggerUtility.info("Opening side menu...");
        ElementActions.click(menuButton);
        WaitUtility.waitForVisibility(logoutSidebarLink);
        WaitUtility.waitForClickability(logoutSidebarLink);
        WaitUtility.sleep(400); // Allow slide-in animation to complete
    }

    /**
     * Performs logout action via the side menu.
     */
    public void clickLogout() {
        openSideMenu();
        LoggerUtility.info("Clicking Logout link in sidebar...");
        JavaScriptUtility.clickElementViaJS(logoutSidebarLink);
        try {
            WaitUtility.waitForVisibility(By.id("login-button"));
        } catch (Exception e) {
            LoggerUtility.warn("JS logout click delayed, retrying standard click...");
            ElementActions.click(logoutSidebarLink);
            WaitUtility.waitForVisibility(By.id("login-button"));
        }
    }

    /**
     * Navigates to all items via sidebar menu.
     */
    public void clickAllItems() {
        openSideMenu();
        LoggerUtility.info("Clicking All Items link in sidebar...");
        ElementActions.click(allItemsSidebarLink);
    }

    /**
     * Resets application state via sidebar menu.
     */
    public void resetAppState() {
        openSideMenu();
        LoggerUtility.info("Resetting application state...");
        ElementActions.click(resetAppStateSidebarLink);
    }
}
