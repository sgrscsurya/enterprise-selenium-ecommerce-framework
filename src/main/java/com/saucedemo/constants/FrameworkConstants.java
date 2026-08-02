package com.saucedemo.constants;

/**
 * FrameworkConstants is the single source of truth for all framework-wide constants.
 *
 * <p>Phase 8 Enterprise Enhancement: Eliminates magic strings, hardcoded timeouts,
 * and repeated literals scattered across page objects, utilities, and test classes.
 *
 * <p>SOLID – Single Responsibility: This class owns ONLY constant definitions.
 * No logic, no state, no instantiation.
 *
 * <p>Usage:
 * <pre>
 *   @Test(groups = {FrameworkConstants.GROUP_SMOKE, FrameworkConstants.GROUP_REGRESSION})
 *   int timeout = FrameworkConstants.TIMEOUT_EXPLICIT;
 *   String env = FrameworkConstants.ENV_QA;
 * </pre>
 */
public final class FrameworkConstants {

    // ── Prevent instantiation ────────────────────────────────────────────
    private FrameworkConstants() {
        throw new UnsupportedOperationException("FrameworkConstants is a constants class and cannot be instantiated.");
    }

    // ========================================================================
    // TEST GROUP NAMES
    // Used in @Test(groups = {...}) annotations and TestNG suite XML group filters.
    // ========================================================================

    /** Quickest CI gate. Core happy-path tests only. */
    public static final String GROUP_SMOKE = "smoke";

    /** Post-deployment sanity check. Core features verified end-to-end. */
    public static final String GROUP_SANITY = "sanity";

    /** Full test coverage. All tests across all features. */
    public static final String GROUP_REGRESSION = "regression";

    // ========================================================================
    // BROWSER IDENTIFIERS
    // Match the values expected by BrowserFactory and TestNG parameter "browser".
    // ========================================================================

    public static final String BROWSER_CHROME  = "chrome";
    public static final String BROWSER_FIREFOX = "firefox";
    public static final String BROWSER_EDGE    = "edge";

    // ========================================================================
    // ENVIRONMENT IDENTIFIERS
    // Match the -Denv JVM property values resolved by EnvironmentManager.
    // ========================================================================

    public static final String ENV_LOCAL   = "local";
    public static final String ENV_DEV     = "dev";
    public static final String ENV_QA      = "qa";
    public static final String ENV_UAT     = "uat";
    public static final String ENV_STAGING = "staging";
    public static final String ENV_PROD    = "prod";

    // ========================================================================
    // TIMEOUTS (seconds)
    // Defaults — config.properties values take precedence at runtime.
    // ========================================================================

    /** Default implicit wait timeout in seconds. */
    public static final int TIMEOUT_IMPLICIT   = 10;

    /** Default explicit / FluentWait timeout in seconds. */
    public static final int TIMEOUT_EXPLICIT   = 15;

    /** Default page-load timeout in seconds. */
    public static final int TIMEOUT_PAGE_LOAD  = 30;

    /** Short wait for fast element checks (e.g., isDisplayed). */
    public static final int TIMEOUT_SHORT      = 5;

    /** Extended wait for heavy async operations. */
    public static final int TIMEOUT_EXTENDED   = 60;

    /** Fluent wait polling interval in milliseconds. */
    public static final int POLLING_INTERVAL_MS = 500;

    // ========================================================================
    // FILE PATHS
    // Relative to project root. Used by utility classes.
    // ========================================================================

    public static final String CONFIG_DIR           = "src/main/resources/";
    public static final String CONFIG_FILE_DEFAULT  = CONFIG_DIR + "config.properties";

    public static final String REPORT_DIR           = "reports/";
    public static final String REPORT_FILE_NAME     = "ExtentReport.html";
    public static final String REPORT_FILE_PATH     = REPORT_DIR + REPORT_FILE_NAME;

    public static final String SCREENSHOT_DIR       = "reports/screenshots/";

    public static final String TEST_RESOURCES_DIR   = "src/test/resources/";
    public static final String EXCEL_FILE_PATH      = TEST_RESOURCES_DIR + "testdata.xlsx";
    public static final String JSON_FILE_PATH       = TEST_RESOURCES_DIR + "testdata.json";
    public static final String CSV_FILE_PATH        = TEST_RESOURCES_DIR + "testdata.csv";

    // ========================================================================
    // SCREENSHOT NAMING
    // Pattern tokens used in ScreenshotUtility.
    // ========================================================================

    /** Timestamp format for screenshot file names. */
    public static final String SCREENSHOT_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss_SSS";

    /** Timestamp format for human-readable report entries. */
    public static final String REPORT_TIMESTAMP_FORMAT     = "yyyy-MM-dd HH:mm:ss";

    /** ISO-8601 datetime format. */
    public static final String ISO_DATETIME_FORMAT         = "yyyy-MM-dd'T'HH:mm:ss";

    // ========================================================================
    // REPORT METADATA
    // ========================================================================

    public static final String REPORT_TITLE   = "SauceDemo Enterprise Test Automation Report";
    public static final String REPORT_NAME    = "SauceDemo Execution Report";
    public static final String FRAMEWORK_NAME = "Enterprise Selenium Framework";
    public static final String APP_NAME       = "SauceDemo E-Commerce";

    // ========================================================================
    // SAUCEDEMO APPLICATION CONSTANTS
    // Specific to the SauceDemo application under test.
    // ========================================================================

    public static final String INVENTORY_PAGE_TITLE = "Products";
    public static final String CART_PAGE_TITLE      = "Your Cart";
    public static final String CHECKOUT_PAGE_TITLE  = "Checkout: Your Information";
    public static final String OVERVIEW_PAGE_TITLE  = "Checkout: Overview";
    public static final String COMPLETE_PAGE_TITLE  = "Checkout: Complete!";
    public static final String COMPLETE_HEADER_TEXT = "Thank you for your order!";

    public static final String STANDARD_USER        = "standard_user";
    public static final String LOCKED_USER          = "locked_out_user";
    public static final String PROBLEM_USER         = "problem_user";
    public static final String STANDARD_PASSWORD    = "secret_sauce";

    public static final String LOCKED_USER_ERROR    = "Epic sadface: Sorry, this user has been locked out.";
    public static final String INVALID_CREDS_ERROR  = "Epic sadface: Username and password do not match any user in this service";
    public static final String EMPTY_CREDS_ERROR    = "Epic sadface: Username is required";
}
