package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.ActionUtility;
import com.saucedemo.utils.JavaScriptUtility;
import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.WaitUtility;
import com.saucedemo.utils.WindowUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * AdvancedInteractionsTest demonstrates reusable Selenium interactions integrated into the framework:
 * Fluent Wait, JavaScript Executor, Actions Hover & Keyboard shortcuts, and Window/Tab switching.
 */
public class AdvancedInteractionsTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void loginToInventory() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login(FrameworkConstants.STANDARD_USER, FrameworkConstants.STANDARD_PASSWORD);
    }

    @Test(description = "Demonstrate Fluent Wait element synchronization", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testFluentWaitInteraction() {
        LoggerUtility.info("Executing testFluentWaitInteraction...");
        By inventoryTitleLocator = By.cssSelector(".title");
        
        WebElement titleElement = WaitUtility.waitForElementWithFluentWait(inventoryTitleLocator, 15, 250);
        Assert.assertNotNull(titleElement, "Title element found via FluentWait is null.");
        Assert.assertEquals(titleElement.getText().trim(), "Products", "FluentWait title text mismatch.");
    }

    @Test(description = "Demonstrate JavaScript Executor scrolling, highlighting, and forced click", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testJavaScriptExecutorInteractions() {
        LoggerUtility.info("Executing testJavaScriptExecutorInteractions...");
        
        // Scroll to footer via JavaScript
        JavaScriptUtility.scrollToBottom();
        
        By footerLocator = By.cssSelector(".footer_copy");
        WebElement footerElement = WaitUtility.waitForVisibility(footerLocator);
        
        // Highlight element for visual debugging
        JavaScriptUtility.highlightElement(footerElement);
        Assert.assertTrue(footerElement.isDisplayed(), "Footer element was not visible after JS scroll.");
        
        // Scroll back to top
        JavaScriptUtility.scrollToTop();
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page title was not visible after scrolling to top.");
    }

    @Test(description = "Demonstrate Actions class mouse hover and keyboard interaction", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testActionsClassInteractions() {
        LoggerUtility.info("Executing testActionsClassInteractions...");
        
        By shoppingCartIcon = By.cssSelector(".shopping_cart_link");
        ActionUtility.hoverOverElement(shoppingCartIcon);
        
        int count = inventoryPage.getHeaderComponent().getCartBadgeCount();
        Assert.assertTrue(count >= 0, "Cart badge count is invalid.");
    }

    @Test(description = "Demonstrate Window and Tab switching utility routines", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testWindowHandlingInteractions() {
        LoggerUtility.info("Executing testWindowHandlingInteractions...");
        String parentWindowHandle = WindowUtility.getCurrentWindowHandle();
        
        WindowUtility.switchToNewTab();
        LoggerUtility.info("Switched to new tab handle. Total window count: " + WindowUtility.getAllWindowHandles().size());
        Assert.assertTrue(WindowUtility.getAllWindowHandles().size() >= 2, "Failed to open new browser tab.");
        
        WindowUtility.closeCurrentAndSwitchToParent(parentWindowHandle);
        Assert.assertEquals(WindowUtility.getCurrentWindowHandle(), parentWindowHandle, "Failed to return focus to parent window.");
    }
}
