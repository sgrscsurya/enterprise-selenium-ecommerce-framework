package com.saucedemo.listeners;

import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.ReportManager;
import com.saucedemo.utils.ScreenshotUtility;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestListener implements TestNG's ITestListener to manage execution event hooks:
 * test logging, ExtentReports creation, category tagging, and automatic Base64 failure screenshot attachment.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        LoggerUtility.info("======================================================================");
        LoggerUtility.info("   SUITE EXECUTION STARTED: " + context.getName());
        LoggerUtility.info("======================================================================");
        ReportManager.initReports();
    }

    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        double passRate = (total == 0) ? 0.0 : (passed * 100.0) / total;

        LoggerUtility.info("======================================================================");
        LoggerUtility.info("   EXECUTION SUMMARY: " + context.getName());
        LoggerUtility.info("   ----------------------------------------------------------------");
        LoggerUtility.info(String.format("   TOTAL: %d | PASSED: %d | FAILED: %d | SKIPPED: %d", total, passed, failed, skipped));
        LoggerUtility.info(String.format("   PASS RATE: %.2f%%", passRate));
        LoggerUtility.info("======================================================================");
        ReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String className = result.getTestClass().getRealClass().getSimpleName();

        LoggerUtility.startTestCase(methodName);
        ReportManager.createTest(methodName, description);
        ReportManager.assignCategory(className);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LoggerUtility.info("TEST PASSED: " + methodName);
        ReportManager.logPass("Test passed successfully: " + methodName);
        LoggerUtility.endTestCase(methodName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        LoggerUtility.error("TEST FAILED: " + methodName, throwable);

        String base64Screenshot = ScreenshotUtility.captureScreenshotAsBase64();
        ScreenshotUtility.captureScreenshot(methodName);
        String errorMessage = (throwable != null) ? throwable.getMessage() : "Unknown Test Failure Error";

        ReportManager.logFail("Test Failed: " + errorMessage, base64Screenshot);
        LoggerUtility.endTestCase(methodName);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LoggerUtility.warn("TEST SKIPPED: " + methodName);
        ReportManager.logSkip("Test Skipped: " + result.getThrowable());
        LoggerUtility.endTestCase(methodName);
    }
}
