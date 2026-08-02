package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.AssertUtility;
import com.saucedemo.utils.LoggerUtility;
import org.testng.annotations.Test;

/**
 * LoginTest contains test scenarios covering valid, invalid, locked out user authentication,
 * and user logout functionality on SauceDemo.
 */
public class LoginTest extends BaseTest {

    @Test(description = "Verify successful login with valid credentials",
            groups = {FrameworkConstants.GROUP_SMOKE, FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testValidLogin() {
        LoggerUtility.info("Executing testValidLogin...");
        LoginPage loginPage = new LoginPage();

        InventoryPage inventoryPage = loginPage.login(FrameworkConstants.STANDARD_USER, FrameworkConstants.STANDARD_PASSWORD);

        AssertUtility.assertTrue(inventoryPage.isPageLoaded(), "Inventory page title 'Products' was not loaded.");
        AssertUtility.assertEquals(inventoryPage.getPageTitleText(), FrameworkConstants.INVENTORY_PAGE_TITLE, "Page title text mismatch.");
    }

    @Test(description = "Verify error message when logging in with invalid credentials",
            groups = {FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testInvalidLogin() {
        LoggerUtility.info("Executing testInvalidLogin...");
        LoginPage loginPage = new LoginPage();

        loginPage.enterUsername("invalid_user")
                 .enterPassword("invalid_password")
                 .clickLoginExpectingFailure();

        AssertUtility.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message banner was not displayed.");
        AssertUtility.assertEquals(loginPage.getErrorMessage(), FrameworkConstants.INVALID_CREDS_ERROR, "Login error message text mismatch.");
    }

    @Test(description = "Verify error message when logging in with a locked-out user account",
            groups = {FrameworkConstants.GROUP_REGRESSION})
    public void testLockedUserLogin() {
        LoggerUtility.info("Executing testLockedUserLogin...");
        LoginPage loginPage = new LoginPage();

        loginPage.enterUsername(FrameworkConstants.LOCKED_USER)
                 .enterPassword(FrameworkConstants.STANDARD_PASSWORD)
                 .clickLoginExpectingFailure();

        AssertUtility.assertTrue(loginPage.isErrorMessageDisplayed(), "Locked user error message banner was not displayed.");
        AssertUtility.assertEquals(loginPage.getErrorMessage(), FrameworkConstants.LOCKED_USER_ERROR, "Locked user error message text mismatch.");
    }

    @Test(description = "Verify successful logout via side menu link",
            groups = {FrameworkConstants.GROUP_SANITY, FrameworkConstants.GROUP_REGRESSION})
    public void testLogout() {
        LoggerUtility.info("Executing testLogout...");
        LoginPage loginPage = new LoginPage();

        InventoryPage inventoryPage = loginPage.login(FrameworkConstants.STANDARD_USER, FrameworkConstants.STANDARD_PASSWORD);
        AssertUtility.assertTrue(inventoryPage.isPageLoaded(), "Inventory page failed to load prior to logout.");

        inventoryPage.getHeaderComponent().clickLogout();

        LoginPage newLoginPage = new LoginPage();
        AssertUtility.assertFalse(newLoginPage.isErrorMessageDisplayed(), "Login page error message unexpected on clean logout.");
    }
}
