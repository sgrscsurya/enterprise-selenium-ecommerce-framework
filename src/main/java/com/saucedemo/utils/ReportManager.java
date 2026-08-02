package com.saucedemo.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.saucedemo.config.ConfigReader;

import java.io.File;

/**
 * ReportManager manages ExtentReports 5.x thread-safe HTML reporting.
 * Uses ThreadLocal<ExtentTest> to support concurrent parallel test execution.
 */
public class ReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ReportManager() {
        // Prevent instantiation
    }

    /**
     * Initializes ExtentReports instance if not already initialized.
     */
    public synchronized static ExtentReports initReports() {
        if (extent == null) {
            String reportFilePath = ConfigReader.getProperty("report.path");
            File reportFile = new File(reportFilePath);
            if (reportFile.getParentFile() != null && !reportFile.getParentFile().exists()) {
                reportFile.getParentFile().mkdirs();
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
            sparkReporter.config().setReportName("SauceDemo Enterprise Test Execution Report");
            sparkReporter.config().setDocumentTitle("SauceDemo Test Automation Results");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm:ss a '('zzz')'");
            sparkReporter.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Framework Target", "SauceDemo E-Commerce");
            extent.setSystemInfo("Architecture", "Page Object Model (POM)");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Default Browser", ConfigReader.getProperty("browser"));
            extent.setSystemInfo("Execution Mode", ConfigReader.getBooleanProperty("headless") ? "Headless" : "Headed");
        }
        return extent;
    }

    /**
     * Creates an ExtentTest node and binds it to the current thread.
     */
    public static synchronized void createTest(String testName, String description) {
        ExtentTest test = initReports().createTest(testName, description);
        extentTestThreadLocal.set(test);
    }

    /**
     * Retrieves the ExtentTest instance bound to the current execution thread.
     */
    public static ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }

    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().log(Status.INFO, message);
        }
    }

    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().log(Status.PASS, message);
        }
    }

    public static void logWarning(String message) {
        if (getTest() != null) {
            getTest().log(Status.WARNING, message);
        }
    }

    public static void logFail(String message, String base64Screenshot) {
        if (getTest() != null) {
            if (base64Screenshot != null && !base64Screenshot.isEmpty()) {
                getTest().fail(message, MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
            } else {
                getTest().fail(message);
            }
        }
    }

    public static void logSkip(String message) {
        if (getTest() != null) {
            getTest().log(Status.SKIP, message);
        }
    }

    public static void assignCategory(String... categories) {
        if (getTest() != null) {
            for (String category : categories) {
                getTest().assignCategory(category);
            }
        }
    }

    /**
     * Flushes ExtentReports data to disk (reports/ExtentReport.html).
     */
    public synchronized static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}
