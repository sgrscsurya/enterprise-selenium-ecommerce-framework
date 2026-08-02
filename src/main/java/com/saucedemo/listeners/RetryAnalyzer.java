package com.saucedemo.listeners;

import com.saucedemo.utils.LoggerUtility;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer implements TestNG's IRetryAnalyzer to automatically re-execute
 * failing test methods up to MAX_RETRY_COUNT times before marking them as FAILED.
 * This prevents transient network or environmental flakiness from breaking CI/CD builds.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            String testName = result.getMethod().getMethodName();
            LoggerUtility.warn("RETRYING TEST: '" + testName + "' | Attempt " + retryCount + " of " + MAX_RETRY_COUNT);
            return true;
        }
        return false;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }
}
