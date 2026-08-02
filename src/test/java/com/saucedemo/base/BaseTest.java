package com.saucedemo.base;

import com.saucedemo.config.ConfigReader;
import com.saucedemo.driver.BrowserFactory;
import com.saucedemo.driver.DriverFactory;
import com.saucedemo.driver.RemoteDriverFactory;
import com.saucedemo.utils.LoggerUtility;
import com.saucedemo.utils.ReportManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

/**
 * BaseTest serves as the foundational superclass for all TestNG test cases.
 *
 * <p>It manages the full WebDriver lifecycle: initialization, timeouts, navigation, and teardown.
 *
 * <p>Phase 7 Enhancement: Supports Selenium Grid execution transparently.
 * When {@code -Dgrid=true} is passed, the driver is created via {@link RemoteDriverFactory}
 * and connected to the configured Grid Hub. Otherwise, local {@link BrowserFactory} is used.
 *
 * <p>Phase 7 Enhancement: Reads environment-aware configuration via {@link ConfigReader},
 * which now delegates to {@link com.saucedemo.config.EnvironmentManager} and resolves
 * properties from the correct environment file (local / staging / prod).
 *
 * <p>Execution modes:
 * <pre>
 *   # Local execution (default)
 *   mvn test -Psmoke
 *
 *   # Headless CI execution against staging
 *   mvn test -Pci-staging
 *
 *   # Selenium Grid execution
 *   mvn test -DsuiteFile=testng-crossbrowser.xml -Dgrid=true -DgridUrl=http://localhost:4444
 * </pre>
 */
public class BaseTest {

    @BeforeSuite
    public void setUpSuite() {
        LoggerUtility.info("=================================================================");
        LoggerUtility.info("  Enterprise Selenium Framework – Initializing Test Suite");
        LoggerUtility.info("  Active Environment : " + ConfigReader.getActiveEnvironment().toUpperCase());
        LoggerUtility.info("  Selenium Grid Mode : " + (RemoteDriverFactory.isGridEnabled() ? "ENABLED" : "DISABLED"));
        LoggerUtility.info("=================================================================");
        ReportManager.initReports();
    }

    /**
     * Sets up the WebDriver instance before each test method.
     *
     * <p>Resolution priority for browser:
     * <ol>
     *   <li>TestNG {@code @Parameters("browser")} from testng.xml / suite XML
     *   <li>{@code -Dbrowser} JVM system property (forwarded by maven-surefire-plugin)
     *   <li>{@code browser} key in the active environment config file
     * </ol>
     *
     * @param browserParam browser parameter injected by TestNG; empty string if not provided
     */
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional("") String browserParam) {
        // Resolve browser: TestNG param > JVM system property > config file
        String browser;
        if (!browserParam.isBlank()) {
            browser = browserParam;
        } else {
            String sysBrowser = System.getProperty("browser");
            browser = (sysBrowser != null && !sysBrowser.isBlank())
                    ? sysBrowser
                    : ConfigReader.getProperty("browser");
        }

        // Resolve headless: JVM system property > config file
        String sysHeadless = System.getProperty("headless");
        boolean isHeadless = (sysHeadless != null && !sysHeadless.isBlank())
                ? Boolean.parseBoolean(sysHeadless)
                : ConfigReader.getBooleanProperty("headless");

        LoggerUtility.info(String.format(
                "Launching browser: %s | Headless: %s | Grid: %s",
                browser, isHeadless, RemoteDriverFactory.isGridEnabled()));

        // Create driver: Grid or Local
        WebDriver driver;
        if (RemoteDriverFactory.isGridEnabled()) {
            driver = RemoteDriverFactory.createRemoteDriver(browser, isHeadless);
        } else {
            driver = BrowserFactory.createDriver(browser, isHeadless);
        }

        DriverFactory.setDriver(driver);

        // Apply timeouts
        int implicitWait = ConfigReader.getIntProperty("implicit.wait");
        int pageLoadWait = ConfigReader.getIntProperty("page.load.timeout");

        DriverFactory.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        DriverFactory.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadWait));

        // Navigate to target URL
        String targetUrl = ConfigReader.getProperty("url");
        LoggerUtility.info("Navigating to: " + targetUrl);
        DriverFactory.getDriver().get(targetUrl);
    }

    @AfterMethod
    public void tearDown() {
        LoggerUtility.info("Tearing down WebDriver instance for current thread...");
        DriverFactory.quitDriver();
    }

    @AfterSuite
    public void tearDownSuite() {
        LoggerUtility.info("Flushing Extent Reports after Suite Execution.");
        ReportManager.flushReports();
    }
}
