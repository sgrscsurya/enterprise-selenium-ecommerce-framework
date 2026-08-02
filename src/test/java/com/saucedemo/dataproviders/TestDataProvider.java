package com.saucedemo.dataproviders;

import com.fasterxml.jackson.databind.JsonNode;
import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.utils.CSVReader;
import com.saucedemo.utils.ExcelUtility;
import com.saucedemo.utils.JSONReader;
import org.testng.annotations.DataProvider;

import java.io.File;

/**
 * TestDataProvider supplies dynamic test datasets for TestNG tests
 * by reading from Excel, JSON, or CSV files, with in-memory fallbacks.
 */
public class TestDataProvider {

    private static final String TEST_DATA_EXCEL_PATH = "src/test/resources/testdata.xlsx";

    /**
     * DataProvider to read login test scenarios dynamically from testdata.xlsx.
     */
    @DataProvider(name = "loginDataFromExcel")
    public static Object[][] getLoginDataFromExcel() {
        File file = new File(TEST_DATA_EXCEL_PATH);
        if (file.exists()) {
            return ExcelUtility.getTestData(TEST_DATA_EXCEL_PATH, "LoginData");
        } else {
            return getLoginDataInMemory();
        }
    }

    /**
     * Fallback DataProvider containing multiple login scenarios in-memory.
     * Columns: Username, Password, ExpectedResult, ExpectedErrorMessage
     */
    @DataProvider(name = "loginDataInMemory")
    public static Object[][] getLoginDataInMemory() {
        return new Object[][]{
            {"standard_user", "secret_sauce", "success", ""},
            {"locked_out_user", "secret_sauce", "locked", "Epic sadface: Sorry, this user has been locked out."},
            {"problem_user", "secret_sauce", "success", ""},
            {"performance_glitch_user", "secret_sauce", "success", ""},
            {"invalid_user", "invalid_password", "invalid", "Epic sadface: Username and password do not match any user in this service"},
            {"error_user", "secret_sauce", "success", ""},
            {"visual_user", "secret_sauce", "success", ""}
        };
    }

    /**
     * DataProvider containing invalid login user credentials only.
     */
    @DataProvider(name = "invalidUsersData")
    public static Object[][] getInvalidUsersData() {
        return new Object[][]{
            {"invalid_user1", "wrong_pass", "Epic sadface: Username and password do not match any user in this service"},
            {"user_test", "12345", "Epic sadface: Username and password do not match any user in this service"},
            {"", "secret_sauce", "Epic sadface: Username is required"},
            {"standard_user", "", "Epic sadface: Password is required"}
        };
    }

    /**
     * DataProvider to read invalid-login scenarios dynamically from testdata.csv via {@link CSVReader}.
     * Columns: username, password, expectedErrorMessage (header row skipped automatically).
     */
    @DataProvider(name = "invalidUsersDataFromCSV")
    public static Object[][] getInvalidUsersDataFromCSV() {
        File file = new File(FrameworkConstants.CSV_FILE_PATH);
        if (file.exists()) {
            return CSVReader.getTestData(FrameworkConstants.CSV_FILE_PATH);
        }
        return getInvalidUsersData();
    }

    /**
     * DataProvider to read login test scenarios dynamically from testdata.json via {@link JSONReader}.
     */
    @DataProvider(name = "loginDataFromJSON")
    public static Object[][] getLoginDataFromJSON() {
        File file = new File(FrameworkConstants.JSON_FILE_PATH);
        if (!file.exists()) {
            return getLoginDataInMemory();
        }

        JsonNode root = JSONReader.readAsTree(FrameworkConstants.JSON_FILE_PATH);
        Object[][] data = new Object[root.size()][4];
        for (int i = 0; i < root.size(); i++) {
            JsonNode row = root.get(i);
            data[i][0] = row.get("username").asText();
            data[i][1] = row.get("password").asText();
            data[i][2] = row.get("expectedResult").asText();
            data[i][3] = row.get("expectedErrorMessage").asText();
        }
        return data;
    }
}
