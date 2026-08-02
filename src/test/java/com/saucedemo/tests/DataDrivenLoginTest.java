package com.saucedemo.tests;

import com.saucedemo.base.BaseTest;
import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.dataproviders.TestDataProvider;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.SoftAssertionManager;
import org.testng.annotations.Test;

/**
 * DataDrivenLoginTest demonstrates Data-Driven Testing (DDT) using TestNG @DataProvider,
 * sourced from Excel, JSON, and CSV files, combined with Soft Assertions for multiple
 * user scenarios.
 */
public class DataDrivenLoginTest extends BaseTest {

    @Test(
        dataProvider = "loginDataFromExcel",
        dataProviderClass = TestDataProvider.class,
        description = "Execute data-driven login test scenarios sourced from Excel (testdata.xlsx)",
        groups = {FrameworkConstants.GROUP_REGRESSION}
    )
    public void testDataDrivenLogin(String username, String password, String expectedResult, String expectedErrorMessage) {
        runLoginScenario(username, password, expectedResult, expectedErrorMessage);
    }

    @Test(
        dataProvider = "loginDataFromJSON",
        dataProviderClass = TestDataProvider.class,
        description = "Execute data-driven login test scenarios sourced from JSON (testdata.json)",
        groups = {FrameworkConstants.GROUP_REGRESSION}
    )
    public void testDataDrivenLoginFromJSON(String username, String password, String expectedResult, String expectedErrorMessage) {
        runLoginScenario(username, password, expectedResult, expectedErrorMessage);
    }

    @Test(
        dataProvider = "invalidUsersData",
        dataProviderClass = TestDataProvider.class,
        description = "Execute data-driven invalid login scenarios with soft assertions",
        groups = {FrameworkConstants.GROUP_REGRESSION}
    )
    public void testDataDrivenInvalidLogins(String username, String password, String expectedErrorMessage) {
        runInvalidLoginScenario(username, password, expectedErrorMessage);
    }

    @Test(
        dataProvider = "invalidUsersDataFromCSV",
        dataProviderClass = TestDataProvider.class,
        description = "Execute data-driven invalid login scenarios sourced from CSV (testdata.csv)",
        groups = {FrameworkConstants.GROUP_REGRESSION}
    )
    public void testDataDrivenInvalidLoginsFromCSV(String username, String password, String expectedErrorMessage) {
        runInvalidLoginScenario(username, password, expectedErrorMessage);
    }

    /**
     * Shared scenario runner for success/failure login data rows, used by both the
     * Excel- and JSON-sourced data providers to avoid duplicating assertion logic.
     */
    private void runLoginScenario(String username, String password, String expectedResult, String expectedErrorMessage) {
        LoggerUtility.info("Executing Data-Driven Login Test for user: '" + username + "' (Expected Result: " + expectedResult + ")");

        LoginPage loginPage = new LoginPage();

        if ("success".equalsIgnoreCase(expectedResult)) {
            InventoryPage inventoryPage = loginPage.login(username, password);
            SoftAssertionManager.assertTrue(inventoryPage.isPageLoaded(), "Inventory page title 'Products' was not loaded for user: " + username);
            SoftAssertionManager.assertEquals(inventoryPage.getPageTitleText(), FrameworkConstants.INVENTORY_PAGE_TITLE, "Page title text mismatch for user: " + username);

            // Cleanup: logout back to login screen for next iterations if needed
            inventoryPage.getHeaderComponent().clickLogout();
        } else {
            loginPage.enterUsername(username)
                     .enterPassword(password)
                     .clickLoginExpectingFailure();

            SoftAssertionManager.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message container was not displayed for user: " + username);
            SoftAssertionManager.assertEquals(loginPage.getErrorMessage(), expectedErrorMessage, "Error message text mismatch for user: " + username);
        }

        SoftAssertionManager.assertAll();
    }

    /**
     * Shared scenario runner for invalid-login-only data rows, used by both the
     * in-memory and CSV-sourced data providers to avoid duplicating assertion logic.
     */
    private void runInvalidLoginScenario(String username, String password, String expectedErrorMessage) {
        LoggerUtility.info("Executing Data-Driven Invalid Login Test for user: '" + username + "'");

        LoginPage loginPage = new LoginPage();

        loginPage.enterUsername(username)
                 .enterPassword(password)
                 .clickLoginExpectingFailure();

        SoftAssertionManager.assertTrue(loginPage.isErrorMessageDisplayed(), "Error banner missing for invalid login input.");
        SoftAssertionManager.assertEquals(loginPage.getErrorMessage(), expectedErrorMessage, "Invalid login error text mismatch.");

        SoftAssertionManager.assertAll();
    }
}
