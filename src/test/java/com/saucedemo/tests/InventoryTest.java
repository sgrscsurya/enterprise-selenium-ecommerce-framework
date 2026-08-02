package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.LoggerUtility;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * InventoryTest contains test scenarios covering product catalog display,
 * item counts, title/price validations, and sorting options (Name & Price).
 */
public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginToInventory() {
        LoginPage loginPage = new LoginPage();
        inventoryPage = loginPage.login(FrameworkConstants.STANDARD_USER, FrameworkConstants.STANDARD_PASSWORD);
    }

    @Test(description = "Verify product inventory page is loaded and displayed correctly",
            groups = {FrameworkConstants.GROUP_SMOKE, FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testVerifyProductsDisplayed() {
        LoggerUtility.info("Executing testVerifyProductsDisplayed...");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page was not loaded.");
        Assert.assertEquals(inventoryPage.getPageTitleText(), FrameworkConstants.INVENTORY_PAGE_TITLE, "Title text mismatch.");
    }

    @Test(description = "Verify exact product count on inventory page",
            groups = {FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testVerifyProductCount() {
        LoggerUtility.info("Executing testVerifyProductCount...");
        List<String> itemNames = inventoryPage.getItemNamesList();
        LoggerUtility.info("Total products count retrieved: " + itemNames.size());
        Assert.assertEquals(itemNames.size(), 6, "Product catalog count mismatch. Expected 6 items.");
    }

    @Test(description = "Verify product names are valid and non-empty", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testVerifyProductNamesList() {
        LoggerUtility.info("Executing testVerifyProductNamesList...");
        List<String> itemNames = inventoryPage.getItemNamesList();
        Assert.assertFalse(itemNames.isEmpty(), "Item names list is empty.");
        
        for (String name : itemNames) {
            Assert.assertNotNull(name, "Found null product name.");
            Assert.assertFalse(name.trim().isEmpty(), "Found blank product name.");
        }
    }

    @Test(description = "Verify product prices are positive values", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testVerifyProductPricesList() {
        LoggerUtility.info("Executing testVerifyProductPricesList...");
        List<Double> itemPrices = inventoryPage.getItemPricesList();
        Assert.assertEquals(itemPrices.size(), 6, "Prices list count mismatch.");
        
        for (Double price : itemPrices) {
            Assert.assertTrue(price > 0.0, "Found invalid product price: $" + price);
        }
    }

    @Test(description = "Verify sorting products by Name (A to Z)", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testSortNameAZ() {
        LoggerUtility.info("Executing testSortNameAZ...");
        inventoryPage.selectSortOptionByValue("az");
        
        List<String> actualNames = inventoryPage.getItemNamesList();
        List<String> expectedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedNames);
        
        Assert.assertEquals(actualNames, expectedNames, "Product names are not sorted alphabetically ascending (A-Z).");
    }

    @Test(description = "Verify sorting products by Name (Z to A)", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testSortNameZA() {
        LoggerUtility.info("Executing testSortNameZA...");
        inventoryPage.selectSortOptionByValue("za");
        
        List<String> actualNames = inventoryPage.getItemNamesList();
        List<String> expectedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedNames, Collections.reverseOrder());
        
        Assert.assertEquals(actualNames, expectedNames, "Product names are not sorted alphabetically descending (Z-A).");
    }

    @Test(description = "Verify sorting products by Price (Low to High)", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testSortPriceLowToHigh() {
        LoggerUtility.info("Executing testSortPriceLowToHigh...");
        inventoryPage.selectSortOptionByValue("lohi");
        
        List<Double> actualPrices = inventoryPage.getItemPricesList();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices);
        
        Assert.assertEquals(actualPrices, expectedPrices, "Product prices are not sorted from Low to High.");
    }

    @Test(description = "Verify sorting products by Price (High to Low)", groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testSortPriceHighToLow() {
        LoggerUtility.info("Executing testSortPriceHighToLow...");
        inventoryPage.selectSortOptionByValue("hilo");
        
        List<Double> actualPrices = inventoryPage.getItemPricesList();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices, Collections.reverseOrder());
        
        Assert.assertEquals(actualPrices, expectedPrices, "Product prices are not sorted from High to Low.");
    }
}
