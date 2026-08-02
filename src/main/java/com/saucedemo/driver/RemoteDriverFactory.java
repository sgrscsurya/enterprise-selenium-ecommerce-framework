package com.saucedemo.driver;

import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.exceptions.FrameworkException;
import com.saucedemo.exceptions.InvalidBrowserException;
import com.saucedemo.utils.LoggerUtility;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * RemoteDriverFactory creates RemoteWebDriver instances that connect to a Selenium Grid Hub.
 *
 * <p>Supports all three enterprise browsers: Chrome, Firefox, and Edge.
 *
 * <p>Grid Hub URL Resolution Order:
 * <ol>
 *   <li>JVM System Property: {@code -DgridUrl=http://your-hub:4444}
 *   <li>Environment Variable: {@code SELENIUM_GRID_URL}
 *   <li>Default: {@code http://localhost:4444} (local Docker Compose grid)
 * </ol>
 *
 * <p>Activation: Set JVM property {@code -Dgrid=true} to route all browsers through Grid.
 *
 * <p>Example:
 * <pre>
 *   mvn test -DsuiteFile=testng-crossbrowser.xml -Dgrid=true -DgridUrl=http://localhost:4444
 * </pre>
 */
public class RemoteDriverFactory {

    private static final String DEFAULT_GRID_URL = "http://localhost:4444";
    private static final String GRID_URL_PROPERTY = "gridUrl";
    private static final String GRID_URL_ENV = "SELENIUM_GRID_URL";

    private RemoteDriverFactory() {
        // Utility class – prevent instantiation
    }

    /**
     * Creates a RemoteWebDriver connected to the configured Selenium Grid Hub.
     * Supports chrome, firefox, and edge.
     *
     * @param browser  browser name (chrome | firefox | edge)
     * @param headless whether to run in headless mode
     * @return RemoteWebDriver instance connected to the Grid
     */
    public static WebDriver createRemoteDriver(String browser, boolean headless) {
        String gridUrl = resolveGridUrl();
        LoggerUtility.info(String.format(
                "Connecting to Selenium Grid [url=%s] [browser=%s] [headless=%s]",
                gridUrl, browser, headless));

        try {
            URL hubUrl = new URL(gridUrl + "/wd/hub");
            String targetBrowser = browser != null ? browser.toLowerCase().trim() : FrameworkConstants.BROWSER_CHROME;

            return switch (targetBrowser) {

                case FrameworkConstants.BROWSER_CHROME -> {
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                        chromeOptions.addArguments("--window-size=1920,1080");
                    }
                    LoggerUtility.info("Creating Remote Chrome driver");
                    yield new RemoteWebDriver(hubUrl, chromeOptions);
                }

                case FrameworkConstants.BROWSER_FIREFOX -> {
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (headless) {
                        firefoxOptions.addArguments("-headless");
                    }
                    LoggerUtility.info("Creating Remote Firefox driver");
                    yield new RemoteWebDriver(hubUrl, firefoxOptions);
                }

                case FrameworkConstants.BROWSER_EDGE -> {
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--start-maximized");
                    edgeOptions.addArguments("--remote-allow-origins=*");
                    edgeOptions.addArguments("--disable-notifications");
                    if (headless) {
                        edgeOptions.addArguments("--headless=new");
                        edgeOptions.addArguments("--window-size=1920,1080");
                    }
                    LoggerUtility.info("Creating Remote Edge driver");
                    yield new RemoteWebDriver(hubUrl, edgeOptions);
                }

                default -> throw new InvalidBrowserException("Unsupported browser type passed: '" + browser
                        + "'. Valid values: " + FrameworkConstants.BROWSER_CHROME + ", "
                        + FrameworkConstants.BROWSER_FIREFOX + ", " + FrameworkConstants.BROWSER_EDGE + ".");
            };

        } catch (MalformedURLException e) {
            throw new FrameworkException("Invalid Selenium Grid URL: " + gridUrl, e);
        }
    }

    /**
     * Checks whether Grid mode is enabled via the {@code -Dgrid=true} JVM property.
     *
     * @return true if grid mode is active
     */
    public static boolean isGridEnabled() {
        return Boolean.parseBoolean(System.getProperty("grid", "false"));
    }

    /**
     * Resolves the Selenium Grid Hub URL from system property, env var, or default.
     *
     * @return the fully-qualified hub URL string
     */
    private static String resolveGridUrl() {
        String gridUrl = System.getProperty(GRID_URL_PROPERTY);
        if (gridUrl == null || gridUrl.isBlank()) {
            gridUrl = System.getenv(GRID_URL_ENV);
        }
        if (gridUrl == null || gridUrl.isBlank()) {
            gridUrl = DEFAULT_GRID_URL;
        }
        return gridUrl.trim();
    }
}
