package com.saucedemo.driver;

import com.saucedemo.constants.FrameworkConstants;
import com.saucedemo.exceptions.InvalidBrowserException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * BrowserFactory is a Factory Pattern class responsible for initializing
 * specific WebDriver browser instances with configured ChromeOptions,
 * FirefoxOptions, or EdgeOptions.
 */
public class BrowserFactory {

    private BrowserFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates and returns a WebDriver instance for the specified browser.
     *
     * @param browser Name of browser ("chrome", "firefox", "edge")
     * @param isHeadless Whether to run browser in headless mode
     * @return Initialized WebDriver object
     */
    public static WebDriver createDriver(String browser, boolean isHeadless) {
        WebDriver driver;
        String targetBrowser = (browser != null) ? browser.toLowerCase().trim() : FrameworkConstants.BROWSER_CHROME;

        switch (targetBrowser) {
            case FrameworkConstants.BROWSER_CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-infobars");
                if (isHeadless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--window-size=1920,1080");
                }
                driver = new ChromeDriver(chromeOptions);
                break;

            case FrameworkConstants.BROWSER_FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case FrameworkConstants.BROWSER_EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized");
                if (isHeadless) {
                    edgeOptions.addArguments("--headless=new");
                }
                driver = new EdgeDriver(edgeOptions);
                break;

            default:
                throw new InvalidBrowserException("Unsupported browser type passed: '" + browser
                        + "'. Valid values: " + FrameworkConstants.BROWSER_CHROME + ", "
                        + FrameworkConstants.BROWSER_FIREFOX + ", " + FrameworkConstants.BROWSER_EDGE + ".");
        }

        return driver;
    }
}
