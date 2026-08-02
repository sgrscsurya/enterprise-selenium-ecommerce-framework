package com.saucedemo.utils;

import com.saucedemo.config.ConfigReader;
import com.saucedemo.driver.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

/**
 * ScreenshotUtility handles capturing element or full page screenshots
 * during test execution failures or assertion checkpoints.
 */
public class ScreenshotUtility {

    private ScreenshotUtility() {
        // Prevent instantiation
    }

    /**
     * Captures a screenshot and saves it to the configured target path on disk.
     *
     * @param testName Name of the test case
     * @return Absolute file path of the saved screenshot
     */
    public static String captureScreenshot(String testName) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            LoggerUtility.error("Cannot capture screenshot because WebDriver instance is null.");
            return "";
        }

        String screenshotName = testName + "_" + DateUtility.getScreenshotTimestamp() + ".png";
        String screenshotDirectory = ConfigReader.getProperty("screenshot.path");

        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File targetFile = new File(screenshotDirectory + screenshotName);

        try {
            FileUtils.copyFile(srcFile, targetFile);
            LoggerUtility.info("Screenshot saved successfully at: " + targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            LoggerUtility.error("Failed to save screenshot for test: " + testName, e);
            return "";
        }
    }

    /**
     * Captures a screenshot as a Base64 string for direct embedding into HTML reports.
     *
     * @return Base64 encoded screenshot image string
     */
    public static String captureScreenshotAsBase64() {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return "";
        }
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }
}
