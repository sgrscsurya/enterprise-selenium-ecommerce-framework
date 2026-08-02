package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.LoggerUtility;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * CartTest contains test scenarios covering single and multi-item additions,
 * item removal, and header cart badge count synchronization.
 */
public class CartTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod
    public void loginToInventory() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login(FrameworkConstants.STANDARD_USER, FrameworkConstants.STANDARD_PASSWORD);
    }

    @Test(description = "Verify adding a single product to cart updates badge and cart page",
            groups = {FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testAddSingleProductToCart() {
        LoggerUtility.info("Executing testAddSingleProductToCart...");
        String itemToAdd = "Sauce Labs Backpack";
        
        inventoryPage.addItemToCartByName(itemToAdd);
        
        int badgeCount = inventoryPage.getHeaderComponent().getCartBadgeCount();
        Assert.assertEquals(badgeCount, 1, "Cart badge count mismatch after adding single product.");
        
        inventoryPage.getHeaderComponent().clickShoppingCart();
        CartPage cartPage = new CartPage();
        
        List<String> cartItems = cartPage.getItemNamesList();
        Assert.assertTrue(cartItems.contains(itemToAdd), "Item '" + itemToAdd + "' was not found in cart.");
    }

    @Test(description = "Verify adding multiple products to cart", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testAddMultipleProductsToCart() {
        LoggerUtility.info("Executing testAddMultipleProductsToCart...");
        String item1 = "Sauce Labs Backpack";
        String item2 = "Sauce Labs Bike Light";
        String item3 = "Sauce Labs Bolt T-Shirt";
        
        inventoryPage.addItemToCartByName(item1)
                     .addItemToCartByName(item2)
                     .addItemToCartByName(item3);
        
        int badgeCount = inventoryPage.getHeaderComponent().getCartBadgeCount();
        Assert.assertEquals(badgeCount, 3, "Cart badge count mismatch after adding 3 products.");
        
        inventoryPage.getHeaderComponent().clickShoppingCart();
        CartPage cartPage = new CartPage();
        
        List<String> cartItems = cartPage.getItemNamesList();
        Assert.assertEquals(cartItems.size(), 3, "Cart item list count mismatch.");
        Assert.assertTrue(cartItems.contains(item1), "Missing item: " + item1);
        Assert.assertTrue(cartItems.contains(item2), "Missing item: " + item2);
        Assert.assertTrue(cartItems.contains(item3), "Missing item: " + item3);
    }

    @Test(description = "Verify removing product from cart page updates item list and badge count", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testRemoveProductFromCart() {
        LoggerUtility.info("Executing testRemoveProductFromCart...");
        String item1 = "Sauce Labs Backpack";
        String item2 = "Sauce Labs Bike Light";
        
        inventoryPage.addItemToCartByName(item1)
                     .addItemToCartByName(item2);
        
        inventoryPage.getHeaderComponent().clickShoppingCart();
        CartPage cartPage = new CartPage();
        
        cartPage.removeItemByName(item1);
        
        int updatedBadgeCount = cartPage.getHeaderComponent().getCartBadgeCount();
        Assert.assertEquals(updatedBadgeCount, 1, "Badge count mismatch after item removal.");
        
        List<String> remainingItems = cartPage.getItemNamesList();
        Assert.assertFalse(remainingItems.contains(item1), "Removed item '" + item1 + "' is still in cart.");
        Assert.assertTrue(remainingItems.contains(item2), "Remaining item '" + item2 + "' is missing from cart.");
    }

    @Test(description = "Verify cart badge count synchronization during addition and removal", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testVerifyCartCount() {
        LoggerUtility.info("Executing testVerifyCartCount...");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0, "Initial cart count should be 0.");
        
        inventoryPage.addItemToCartByName("Sauce Labs Onesie");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 1, "Cart count should be 1.");
        
        inventoryPage.removeItemFromCartByName("Sauce Labs Onesie");
        Assert.assertEquals(inventoryPage.getHeaderComponent().getCartBadgeCount(), 0, "Cart count should reset to 0.");
    }
}
