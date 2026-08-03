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

            String customCss = "@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&display=swap'); " +
                    "html, body, .spark-app, .layout, .vcontainer, .main-content { font-family: 'Outfit', -apple-system, sans-serif !important; transition: all 0.3s ease !important; } " +
                    ".nav-logo .logo { background-image: url('logo.png') !important; background-size: contain !important; background-repeat: no-repeat !important; background-position: center !important; filter: grayscale(100%) contrast(1.2) !important; width: 38px !important; height: 38px !important; border-radius: 8px !important; } " +
                    "/* Dark Theme (Default) */ " +
                    "body, body.dark, .dark .spark-app, body.theme-dark { background-color: #000000 !important; color: #ffffff !important; } " +
                    "body.dark .vheader, body.theme-dark .vheader, body.dark .header.navbar { background-color: #0d0d0d !important; border-bottom: 1px solid #222222 !important; } " +
                    "body.dark .vheader *, body.theme-dark .vheader * { color: #ffffff !important; } " +
                    "body.dark .badge-primary, body.theme-dark .badge-primary { background-color: #1a1a1a !important; color: #ffffff !important; border: 1px solid rgba(255,255,255,0.2) !important; border-radius: 20px !important; } " +
                    "body.dark .search-input input, body.theme-dark .search-input input { background-color: #161616 !important; color: #ffffff !important; border: 1px solid #333333 !important; border-radius: 8px !important; } " +
                    "body.dark .side-nav, body.theme-dark .side-nav { background-color: #050505 !important; border-right: 1px solid #1a1a1a !important; } " +
                    "body.dark .side-nav-menu .nav-item a, body.theme-dark .side-nav-menu .nav-item a { color: #888888 !important; } " +
                    "body.dark .side-nav-menu .nav-item.active a, body.theme-dark .side-nav-menu .nav-item.active a, body.dark .side-nav-menu .nav-item:hover a, body.theme-dark .side-nav-menu .nav-item:hover a { color: #ffffff !important; background-color: #1a1a1a !important; } " +
                    "body.dark .test-list, body.theme-dark .test-list { background-color: #0d0d0d !important; border-right: 1px solid #222222 !important; } " +
                    "body.dark .test-list-tools, body.theme-dark .test-list-tools { background-color: #141414 !important; border-bottom: 1px solid #222222 !important; color: #ffffff !important; } " +
                    "body.dark .test-item, body.theme-dark .test-item { background-color: #0d0d0d !important; border-bottom: 1px solid #1e1e1e !important; color: #ffffff !important; } " +
                    "body.dark .test-item:hover, body.theme-dark .test-item:hover, body.dark .test-item.active, body.theme-dark .test-item.active { background-color: #1a1a1a !important; } " +
                    "body.dark .test-detail .name, body.theme-dark .test-detail .name { color: #ffffff !important; font-weight: 600 !important; } " +
                    "body.dark .test-detail .text-sm, body.theme-dark .test-detail .text-sm { color: #aaaaaa !important; } " +
                    "body.dark .test-contents, body.theme-dark .test-contents, body.dark .detail-head, body.theme-dark .detail-head, body.dark .detail-body, body.theme-dark .detail-body { background-color: #000000 !important; color: #ffffff !important; } " +
                    "body.dark .detail-head *, body.theme-dark .detail-head * { color: #ffffff !important; } " +
                    "body.dark .card, body.theme-dark .card { background-color: #0d0d0d !important; border: 1px solid #222222 !important; color: #ffffff !important; border-radius: 12px !important; } " +
                    "body.dark .card-header, body.theme-dark .card-header { background-color: #141414 !important; border-bottom: 1px solid #222222 !important; color: #ffffff !important; } " +
                    "body.dark .card-body, body.theme-dark .card-body, body.dark .card-footer, body.theme-dark .card-footer { background-color: #0d0d0d !important; color: #ffffff !important; } " +
                    "body.dark .table, body.theme-dark .table { background-color: #000000 !important; color: #ffffff !important; } " +
                    "body.dark .table th, body.theme-dark .table th { background-color: #141414 !important; border-color: #222222 !important; color: #888888 !important; } " +
                    "body.dark .table td, body.theme-dark .table td, body.dark .event-row, body.theme-dark .event-row { background-color: #000000 !important; border-color: #1e1e1e !important; color: #dddddd !important; } " +
                    "body.dark .dropdown-menu, body.theme-dark .dropdown-menu { background-color: #0d0d0d !important; border: 1px solid #333333 !important; border-radius: 8px !important; } " +
                    "body.dark .dropdown-item, body.theme-dark .dropdown-item { color: #ffffff !important; } " +
                    "body.dark .dropdown-item:hover, body.theme-dark .dropdown-item:hover { background-color: #222222 !important; } " +
                    "/* Light Theme (100% Complete White Mode Conversion) */ " +
                    "body.theme-light, body.standard, .standard .spark-app, [data-theme='light'] { background-color: #f4f4f6 !important; color: #000000 !important; } " +
                    "body.theme-light .vcontainer, body.standard .vcontainer, body.theme-light .main-content, body.standard .main-content, body.theme-light .test-wrapper, body.standard .test-wrapper { background-color: #f4f4f6 !important; color: #000000 !important; } " +
                    "body.theme-light .vheader, body.standard .vheader, body.standard .header.navbar { background-color: #ffffff !important; border-bottom: 1px solid #e0e0e0 !important; } " +
                    "body.theme-light .vheader *, body.standard .vheader * { color: #000000 !important; } " +
                    "body.theme-light .badge-primary, body.standard .badge-primary { background-color: #e4e4e8 !important; color: #000000 !important; border: 1px solid #cccccc !important; border-radius: 20px !important; } " +
                    "body.theme-light .search-input input, body.standard .search-input input { background-color: #f0f0f4 !important; color: #000000 !important; border: 1px solid #cccccc !important; border-radius: 8px !important; } " +
                    "body.theme-light .side-nav, body.standard .side-nav { background-color: #ffffff !important; border-right: 1px solid #e0e0e0 !important; } " +
                    "body.theme-light .side-nav-menu .nav-item a, body.standard .side-nav-menu .nav-item a { color: #555555 !important; } " +
                    "body.theme-light .side-nav-menu .nav-item.active a, body.standard .side-nav-menu .nav-item.active a, body.theme-light .side-nav-menu .nav-item:hover a, body.standard .side-nav-menu .nav-item:hover a { color: #000000 !important; background-color: #e4e4e8 !important; } " +
                    "body.theme-light .test-list, body.standard .test-list { background-color: #ffffff !important; border-right: 1px solid #e0e0e0 !important; } " +
                    "body.theme-light .test-list-tools, body.standard .test-list-tools { background-color: #f8f9fa !important; border-bottom: 1px solid #e0e0e0 !important; color: #000000 !important; } " +
                    "body.theme-light .test-list-tools *, body.standard .test-list-tools * { color: #000000 !important; } " +
                    "body.theme-light .test-item, body.standard .test-item { background-color: #ffffff !important; border-bottom: 1px solid #f0f0f4 !important; color: #000000 !important; } " +
                    "body.theme-light .test-item:hover, body.standard .test-item:hover, body.theme-light .test-item.active, body.standard .test-item.active { background-color: #f0f0f4 !important; } " +
                    "body.theme-light .test-detail .name, body.standard .test-detail .name { color: #000000 !important; font-weight: 700 !important; } " +
                    "body.theme-light .test-detail .text-sm, body.standard .test-detail .text-sm { color: #666666 !important; } " +
                    "body.theme-light .test-contents, body.standard .test-contents, body.theme-light .detail-head, body.standard .detail-head, body.theme-light .detail-body, body.standard .detail-body { background-color: #ffffff !important; color: #000000 !important; } " +
                    "body.theme-light .detail-head *, body.standard .detail-head * { color: #000000 !important; } " +
                    "body.theme-light .card, body.standard .card { background-color: #ffffff !important; border: 1px solid #e0e0e0 !important; color: #000000 !important; border-radius: 12px !important; box-shadow: 0 2px 8px rgba(0,0,0,0.04) !important; } " +
                    "body.theme-light .card-header, body.standard .card-header { background-color: #f8f9fa !important; border-bottom: 1px solid #e0e0e0 !important; color: #000000 !important; } " +
                    "body.theme-light .card-body, body.standard .card-body, body.theme-light .card-footer, body.standard .card-footer { background-color: #ffffff !important; color: #000000 !important; } " +
                    "body.theme-light .table, body.standard .table { background-color: #ffffff !important; color: #000000 !important; } " +
                    "body.theme-light .table th, body.standard .table th { background-color: #f8f9fa !important; border-color: #e0e0e0 !important; color: #555555 !important; } " +
                    "body.theme-light .table td, body.standard .table td, body.theme-light .event-row, body.standard .event-row { background-color: #ffffff !important; border-color: #f0f0f4 !important; color: #111111 !important; } " +
                    "body.theme-light .dropdown-menu, body.standard .dropdown-menu { background-color: #ffffff !important; border: 1px solid #e0e0e0 !important; box-shadow: 0 4px 16px rgba(0,0,0,0.1) !important; } " +
                    "body.theme-light .dropdown-item, body.standard .dropdown-item { color: #000000 !important; } " +
                    "body.theme-light .dropdown-item:hover, body.standard .dropdown-item:hover { background-color: #f0f0f4 !important; } " +
                    "/* Mobile Responsiveness */ " +
                    "@media (max-width: 992px) { " +
                    "  .vheader { height: auto !important; padding: 8px 12px !important; flex-wrap: wrap !important; } " +
                    "  .vheader .nav-right { margin-top: 6px !important; flex-wrap: wrap !important; } " +
                    "  .test-wrapper { flex-direction: column !important; } " +
                    "  .test-list { width: 100% !important; max-height: 260px !important; border-right: none !important; border-bottom: 1px solid rgba(128,128,128,0.2) !important; } " +
                    "  .test-contents { width: 100% !important; padding: 12px !important; } " +
                    "  .vcontainer { padding: 10px !important; margin-left: 50px !important; } " +
                    "  .side-nav { width: 50px !important; } " +
                    "} " +
                    "@media (max-width: 576px) { " +
                    "  .side-nav { display: none !important; } " +
                    "  .vcontainer { margin-left: 0 !important; } " +
                    "  .card { margin-bottom: 12px !important; } " +
                    "  .table { font-size: 12px !important; } " +
                    "}";

            String customJs = "window.addEventListener('message', function(e) {" +
                    "  if (e.data && e.data.type === 'SET_THEME') {" +
                    "    if (e.data.theme === 'light') {" +
                    "      document.body.classList.remove('dark', 'theme-dark');" +
                    "      document.body.classList.add('standard', 'theme-light');" +
                    "    } else {" +
                    "      document.body.classList.remove('standard', 'theme-light');" +
                    "      document.body.classList.add('dark', 'theme-dark');" +
                    "    }" +
                    "  }" +
                    "});" +
                    "var saved = localStorage.getItem('sa_platform_theme');" +
                    "if (saved === 'light') { document.body.classList.remove('dark'); document.body.classList.add('standard', 'theme-light'); }";

            sparkReporter.config().setCss(customCss);
            sparkReporter.config().setJs(customJs);

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
