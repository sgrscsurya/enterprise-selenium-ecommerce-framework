# 🧪 Enterprise Selenium Automation Framework

A production-grade, cross-browser, parallel test automation framework built with **Java 17**, **Selenium 4**, **TestNG**, and **ExtentReports** — designed for the [SauceDemo](https://www.saucedemo.com) e-commerce application.

---

## 📋 Table of Contents

- [Technology Stack](#-technology-stack)
- [Framework Architecture](#-framework-architecture)
- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [Running Tests](#-running-tests)
  - [Maven Commands](#maven-commands)
  - [Maven Profiles](#maven-profiles)
  - [Cross-Browser Execution](#cross-browser-execution)
  - [Parallel Execution](#parallel-execution)
- [CI/CD Integration](#-cicd-integration)
  - [GitHub Actions](#github-actions)
  - [Jenkins](#jenkins)
- [Docker & Selenium Grid](#-docker--selenium-grid)
- [Multi-Environment Support](#-multi-environment-support)
- [Framework Phases](#-framework-phases)
- [Extending for a New Website](#-extending-for-a-new-website)
- [Design Patterns Used](#-design-patterns-used)

---

## 🛠 Technology Stack

| Category | Technology | Version |
|----------|-----------|---------|
| Language | Java | 17 |
| Browser Automation | Selenium WebDriver | 4.28.0 |
| Test Framework | TestNG | 7.10.2 |
| Reporting | ExtentReports | 5.1.2 |
| Logging | Apache Log4j2 | 2.24.3 |
| Data-Driven | Apache POI (Excel) | 5.4.0 |
| Driver Management | WebDriverManager | 5.9.2 |
| Build Tool | Apache Maven | 3.9+ |
| Containerization | Docker + Compose | – |
| CI/CD | GitHub Actions + Jenkins | – |

---

## 🏗 Framework Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                     Test Layer (TestNG)                        │
│   LoginTest  InventoryTest  CartTest  CheckoutTest  ...       │
└────────────────────────┬───────────────────────────────────────┘
                         │ extends
┌────────────────────────▼───────────────────────────────────────┐
│                     BaseTest                                   │
│  @BeforeSuite  @BeforeMethod  @AfterMethod  @AfterSuite       │
│  ThreadLocal<WebDriver> lifecycle management                  │
└──────┬─────────────────┬──────────────────┬────────────────────┘
       │                 │                  │
┌──────▼──────┐  ┌───────▼──────┐  ┌───────▼──────────────────┐
│ Page Object │  │  Utilities   │  │  Listeners & Reporting   │
│   Model     │  │              │  │                          │
│ LoginPage   │  │ WaitUtility  │  │ TestListener             │
│InventoryPage│  │ JSUtility    │  │ AnnotationTransformer    │
│ CartPage    │  │ ActionUtility│  │ RetryAnalyzer            │
│ CheckoutPage│  │ AlertUtility │  │ ReportManager            │
│ ...         │  │ FrameUtility │  │ ScreenshotUtility        │
└──────┬──────┘  │ WindowUtility│  └──────────────────────────┘
       │         └──────────────┘
┌──────▼───────────────────────────────────────────────────────┐
│                   Driver Layer                               │
│  BrowserFactory        RemoteDriverFactory                  │
│  (Chrome/Firefox/Edge) (Selenium Grid)                      │
│           └──────────────────┘                             │
│                 DriverFactory (ThreadLocal<WebDriver>)      │
└──────────────────────────────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────────┐
│                  Configuration Layer                         │
│  ConfigReader ──► EnvironmentManager                        │
│  config.properties | config-staging.properties | config-prod│
└──────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
enterprise-selenium-ecommerce-framework/
├── .github/workflows/selenium-ci.yml      ← GitHub Actions (6 jobs)
├── Dockerfile                              ← Multi-stage Docker image
├── docker-compose.yml                      ← Smoke/Regression/Grid services
├── Jenkinsfile                             ← Declarative Jenkins pipeline
├── Makefile                                ← Developer shortcuts (14 targets)
├── pom.xml                                 ← Maven build + profiles
├── testng.xml                              ← Default full suite
├── testng-smoke.xml                        ← Smoke suite
├── testng-sanity.xml                       ← Sanity suite (3 threads)
├── testng-regression.xml                   ← Regression suite (4 threads)
├── testng-crossbrowser.xml                 ← Chrome + Firefox + Edge (3 threads)
└── src/
    ├── main/java/com/saucedemo/
    │   ├── config/
    │   │   ├── ConfigReader.java           ← Config facade
    │   │   └── EnvironmentManager.java     ← Multi-env resolver
    │   ├── driver/
    │   │   ├── BrowserFactory.java         ← Local Chrome/Firefox/Edge
    │   │   ├── DriverFactory.java          ← ThreadLocal<WebDriver>
    │   │   └── RemoteDriverFactory.java    ← Selenium Grid driver
    │   ├── listeners/
    │   │   ├── AnnotationTransformer.java  ← Suite-wide RetryAnalyzer binding
    │   │   ├── RetryAnalyzer.java          ← Auto-retry on failure (2 retries)
    │   │   └── TestListener.java           ← ExtentReports + screenshot hook
    │   ├── pages/
    │   │   ├── components/HeaderComponent.java
    │   │   ├── LoginPage.java
    │   │   ├── InventoryPage.java
    │   │   ├── CartPage.java
    │   │   ├── CheckoutPage.java
    │   │   ├── CheckoutOverviewPage.java
    │   │   └── CheckoutCompletePage.java
    │   └── utils/
    │       ├── ActionUtility.java          ← Mouse & Keyboard (Actions API)
    │       ├── AlertUtility.java           ← JavaScript alert dialogs
    │       ├── ExcelUtility.java           ← Apache POI Excel reader/writer
    │       ├── FrameUtility.java           ← iframe switching
    │       ├── JavaScriptUtility.java      ← JavascriptExecutor wrapper
    │       ├── LoggerUtility.java          ← Log4j2 wrapper
    │       ├── ReportManager.java          ← ExtentReports manager
    │       ├── ScreenshotUtility.java      ← Base64 inline screenshots
    │       ├── WaitUtility.java            ← Explicit + FluentWait
    │       └── WindowUtility.java          ← Window/tab switching
    ├── main/resources/
    │   ├── config.properties               ← Local environment
    │   ├── config-staging.properties       ← Staging environment
    │   ├── config-prod.properties          ← Production environment
    │   └── log4j2.xml                      ← Logging configuration
    └── test/java/com/saucedemo/
        ├── base/BaseTest.java              ← Test lifecycle management
        ├── dataproviders/TestDataProvider.java
        ├── tests/
        │   ├── LoginTest.java
        │   ├── InventoryTest.java
        │   ├── CartTest.java
        │   ├── CheckoutTest.java
        │   ├── DataDrivenLoginTest.java    ← Excel-driven, SoftAssertions
        │   └── AdvancedInteractionsTest.java
        └── utils/ExcelDataInitializer.java
```

---

## ⚡ Quick Start

### Prerequisites

| Tool | Minimum Version | Install |
|------|----------------|---------|
| Java JDK | 17 | [adoptium.net](https://adoptium.net) |
| Apache Maven | 3.9 | [maven.apache.org](https://maven.apache.org) |
| Google Chrome | Latest | [google.com/chrome](https://www.google.com/chrome) |
| Firefox | Latest | [mozilla.org](https://www.mozilla.org/firefox) |
| Edge | Latest | Pre-installed on Windows |

> **WebDriverManager** automatically downloads the correct browser driver binary — no manual `chromedriver.exe` setup needed.

```bash
# Clone the repo
git clone https://github.com/your-org/enterprise-selenium-ecommerce-framework.git
cd enterprise-selenium-ecommerce-framework

# Compile and verify setup
mvn test-compile -B

# Run smoke tests (headed Chrome, local env)
mvn test -Psmoke
```

---

## 🚀 Running Tests

### Maven Commands

```bash
# ── Basic Execution ─────────────────────────────────────────────────
mvn test                              # Full suite, Chrome, local env

# ── Override any parameter on the fly ───────────────────────────────
mvn test -DsuiteFile=testng-smoke.xml           # Specific suite
mvn test -Dbrowser=firefox                       # Specific browser
mvn test -Denv=staging                           # Specific environment
mvn test -Dheadless=true                         # Headless mode
mvn test -Dgrid=true -DgridUrl=http://localhost:4444  # Via Selenium Grid

# ── Combined overrides ───────────────────────────────────────────────
mvn test -DsuiteFile=testng-regression.xml -Dbrowser=firefox -Dheadless=true
```

### Maven Profiles

Maven profiles are the recommended way to run pre-configured scenarios:

```bash
# ── Single Browser ───────────────────────────────────────────────────
mvn test -Psmoke         # Smoke, Chrome, local, headed
mvn test -Pchrome        # Smoke, Chrome, local, headed
mvn test -Pfirefox       # Smoke, Firefox, local, headed
mvn test -Pedge          # Smoke, Edge, local, headed

# ── Multi-Browser ────────────────────────────────────────────────────
mvn test -Pregression    # Full regression, Chrome, local, headed
mvn test -Pcrossbrowser  # Chrome + Firefox + Edge simultaneously

# ── CI / Headless ────────────────────────────────────────────────────
mvn test -Pci-staging        # Smoke, Chrome, staging, headless
mvn test -Pci-regression     # Regression, Chrome, staging, headless
mvn test -Pci-crossbrowser   # Cross-browser, staging, headless
mvn test -Pci-dev            # Smoke, Chrome, dev, headless
mvn test -Pci-qa             # Smoke, Chrome, qa, headless
mvn test -Pci-uat            # Regression, Chrome, uat, headless
```

| Profile | Suite | Browser | Env | Headless |
|---------|-------|---------|-----|---------|
| `smoke` | testng-smoke.xml | Chrome | local | false |
| `sanity` | testng-sanity.xml | Chrome | local | false |
| `regression` | testng-regression.xml | Chrome | local | false |
| `crossbrowser` | testng-crossbrowser.xml | All 3 | local | false |
| `chrome` | testng-smoke.xml | Chrome | local | false |
| `firefox` | testng-smoke.xml | Firefox | local | false |
| `edge` | testng-smoke.xml | Edge | local | false |
| `ci-staging` | testng-smoke.xml | Chrome | staging | **true** |
| `ci-regression` | testng-regression.xml | Chrome | staging | **true** |
| `ci-crossbrowser` | testng-crossbrowser.xml | All 3 | staging | **true** |
| `ci-dev` | testng-smoke.xml | Chrome | dev | **true** |
| `ci-qa` | testng-smoke.xml | Chrome | qa | **true** |
| `ci-uat` | testng-regression.xml | Chrome | uat | **true** |

### Cross-Browser Execution

The framework supports **Chrome**, **Firefox**, and **Edge** both locally and via Selenium Grid.

**How cross-browser routing works:**

```
testng-crossbrowser.xml
  └── <test name="Chrome - Login"> <parameter name="browser" value="chrome"/>
  └── <test name="Firefox - Login"> <parameter name="browser" value="firefox"/>
  └── <test name="Edge - Login"> <parameter name="browser" value="edge"/>
         │
         ▼
  BaseTest.setUp(@Parameter browser)
         │
         ├── if -Dgrid=true → RemoteDriverFactory.createRemoteDriver(browser)
         └── else           → BrowserFactory.createDriver(browser)
```

```bash
# Local cross-browser (3 browser windows open simultaneously)
mvn test -Pcrossbrowser

# Grid cross-browser (requires docker-compose --profile grid up)
mvn test -DsuiteFile=testng-crossbrowser.xml -Dgrid=true
```

### Parallel Execution

The framework is fully **thread-safe** using `ThreadLocal<WebDriver>` in `DriverFactory`.

| Suite | `parallel` mode | `thread-count` |
|-------|----------------|---------------|
| testng.xml | `classes` | 2 |
| testng-smoke.xml | `classes` | 2 |
| testng-sanity.xml | `classes` | 3 |
| testng-regression.xml | `classes` | 4 |
| testng-crossbrowser.xml | `tests` | 3 |

**TestNG parallel modes explained:**
- `parallel="classes"` — Each test class runs in its own thread (safest for POM-based tests)
- `parallel="tests"` — Each `<test>` element runs in its own thread (used for cross-browser)
- `parallel="methods"` — Each `@Test` method runs in its own thread (most aggressive)

---

## 🔄 CI/CD Integration

### GitHub Actions

**File:** [`.github/workflows/selenium-ci.yml`](.github/workflows/selenium-ci.yml)

**Pipeline Flow:**

```
Push / PR
    │
    ▼
[Job 1] Build & Validate (compile check)
    │
    ▼
[Job 2] Smoke Tests – Chrome (ubuntu-latest, headless)
    │
    ├──────────────────────────────┐
    ▼                              ▼
[Job 3a] Cross-Browser: Chrome   [Job 3b] Cross-Browser: Firefox   [Job 4] Edge (windows-latest)
         (matrix strategy, parallel)
    │
    ▼
[Job 5] Full Regression – Chrome  (main branch only)
```

**Manual Trigger (workflow_dispatch):**

Go to → `GitHub repo → Actions → Selenium CI Pipeline → Run workflow`

Choose:
- **Suite**: smoke / regression / crossbrowser / full
- **Browser**: chrome / firefox / **edge**
- **Environment**: local / staging / prod

**Artifacts uploaded after every run:**
- `extent-report-{type}-{run_number}/` — full HTML report
- `screenshots-{browser}-failure-{run_number}/` — only on failure

### Jenkins

**File:** [`Jenkinsfile`](Jenkinsfile)

**Setup Steps:**

1. Install Jenkins plugins: **Pipeline**, **HTML Publisher**, **AnsiColor**, **TestNG Results**
2. Configure Global Tools:
   - JDK: name = `JDK_17`, auto-install from AdoptOpenJDK
   - Maven: name = `MAVEN_3_9`, auto-install
3. Create a **Pipeline** job → SCM: Git → Script Path: `Jenkinsfile`
4. Click **Build with Parameters** to choose suite/browser/environment

**Stage Breakdown:**

| Stage | Condition | Description |
|-------|-----------|-------------|
| Checkout | Always | Clones source |
| Build & Compile | Always | Compiles main + test |
| Smoke Tests | Always | `testng-smoke.xml` with chosen browser |
| Cross-Browser (parallel) | main / develop / param | Chrome + Firefox + Edge simultaneously |
| Full Regression | `main` branch only | `testng-regression.xml` |

Post-build: HTML reports published, screenshots archived, TestNG trend graph updated.

---

## 🐳 Docker & Selenium Grid

### Run Tests in Docker

```bash
# Build the automation image
docker build -t saucedemo-automation .

# Run smoke tests (headless Chrome, staging env)
docker run --rm \
  -e SUITE=testng-smoke.xml \
  -e ENV=staging \
  -e BROWSER=chrome \
  -e HEADLESS=true \
  -v $(pwd)/reports:/app/reports \
  saucedemo-automation

# Or use docker-compose
docker-compose up smoke        # Smoke suite
docker-compose up regression   # Full regression
docker-compose up crossbrowser # Cross-browser suite
```

### Selenium Grid

```bash
# Start Grid Hub + Chrome Node + Firefox Node
docker-compose --profile grid up -d

# Verify Grid UI
open http://localhost:4444

# Run tests against the Grid
mvn test -DsuiteFile=testng-crossbrowser.xml -Dgrid=true -DgridUrl=http://localhost:4444

# Stop Grid
docker-compose --profile grid down
```

---

## 🌍 Multi-Environment Support

The framework automatically loads the correct config file based on the `-Denv` JVM property:

| `-Denv` value | Config File | Use Case |
|--------------|-------------|---------|
| `local` (default) | `config.properties` | Local developer machine |
| `dev` | `config-dev.properties` | Shared development environment |
| `qa` | `config-qa.properties` | QA test environment |
| `uat` | `config-uat.properties` | User acceptance testing |
| `staging` | `config-staging.properties` | CI/CD pipelines |
| `prod` | `config-prod.properties` | Production smoke tests |

```bash
# Target staging environment
mvn test -Denv=staging

# Target QA
mvn test -Denv=qa -Psanity

# Target production
mvn test -Denv=prod -Psmoke

# Inside CI (environment variable also works)
ENV=staging mvn test
```

`EnvironmentManager` (`com.saucedemo.config.EnvironmentManager`) is a lazily-initialized Singleton — configuration is resolved and parsed exactly once per JVM, then reused for every property lookup via `ConfigReader`.

**Adding a new environment** (e.g., `perf`):
1. Create `src/main/resources/config-perf.properties`
2. Set properties: `url`, `browser`, `headless`, `env.name`, etc.
3. Run: `mvn test -Denv=perf`

No code changes required — `EnvironmentManager` resolves it automatically.

---

## 📖 Framework Phases

| Phase | Focus | Key Deliverables |
|-------|-------|-----------------|
| 1 | Foundation | Maven setup, DriverFactory (ThreadLocal), BaseTest, Log4j2 |
| 2 | Page Object Model | 7 Page classes, HeaderComponent, reusable locators & methods |
| 3 | Test Classes | 4 TestNG suites: Login, Inventory, Cart, Checkout |
| 4 | Data-Driven Testing | ExcelUtility (POI), TestDataProvider, SoftAssertions |
| 5 | Reporting & Resilience | ExtentReports 5, RetryAnalyzer, AnnotationTransformer, TestListener |
| 6 | Advanced Interactions | FluentWait, JavaScriptExecutor, Actions, Alerts, Frames, Windows |
| 7 | CI/CD & Multi-Browser | GitHub Actions, Jenkinsfile, Docker, Selenium Grid, 8 Maven profiles |
| 8 | Enterprise Enhancements | BasePage, ElementActions, FrameworkConstants wired throughout, Singleton EnvironmentManager, FrameworkException/InvalidBrowserException, dev/qa/uat/staging/prod configs, DateUtility, RandomDataGenerator, JSONReader, CSVReader, Smoke/Sanity/Regression groups, AssertUtility & SoftAssertionManager, execution summary, code de-duplication |

---

## 🔌 Extending for a New Website

This framework is website-agnostic by design. To adapt it for a different target application (e.g., `https://www.demoblaze.com`), follow these steps:

### Step 1 — Update Configuration

```properties
# src/main/resources/config.properties
url=https://www.demoblaze.com
browser=chrome
headless=false
```

No other infrastructure changes needed.

---

### Step 2 — Create New Page Objects

Create a page class per unique page/component in the new application.

```java
// src/main/java/com/yourcompany/pages/HomePage.java
package com.yourcompany.pages;

import com.saucedemo.pages.base.BasePage;
import com.saucedemo.utils.ElementActions;
import com.saucedemo.utils.LoggerUtility;
import org.openqa.selenium.By;

/**
 * Page Object for the DemoBlaze Home Page.
 * Extends BasePage (Phase 8) for shared ready-wait/logging behavior.
 */
public class HomePage extends BasePage {

    // ── Locators ────────────────────────────────────────────────────
    private final By loginButton  = By.id("login2");
    private final By productCards = By.cssSelector(".card-title");

    // ── Constructor ─────────────────────────────────────────────────
    public HomePage() {
        super(By.cssSelector(".navbar-brand")); // waits for this element, then logs init
    }

    // ── Methods ──────────────────────────────────────────────────────
    public void clickLogin() {
        LoggerUtility.info("Clicking Login button on Home Page");
        ElementActions.click(loginButton);
    }

    public int getProductCount() {
        return ElementActions.getTextList(productCards).size();
    }
}
```

**Checklist for each new page:**
- [ ] One class per page/component, extending `BasePage`
- [ ] All locators declared as `private final By` fields
- [ ] Constructor calls `super(readyIndicatorLocator)` — no manual `WebDriver` plumbing needed (driver comes from the ThreadLocal `DriverFactory`)
- [ ] All interactions go through `ElementActions` (no raw `findElement`/`WaitUtility` calls)
- [ ] Each method has a `LoggerUtility.info(...)` call for non-trivial actions

---

### Step 3 — Create New Test Classes

```java
// src/test/java/com/yourcompany/tests/HomePageTest.java
package com.yourcompany.tests;

import com.saucedemo.base.BaseTest;        // ← Reuse existing BaseTest
import com.yourcompany.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {  // ← Extends existing BaseTest

    @Test(groups = {"smoke"}, description = "Verify product listing is visible")
    public void verifyProductsVisible() {
        HomePage home = new HomePage(getDriver());  // or DriverFactory.getDriver()
        Assert.assertTrue(home.getProductCount() > 0, "No products found on Home Page");
    }
}
```

**Reuse without modification:**
- `BaseTest` — browser lifecycle (works for any URL via config)
- `DriverFactory` — ThreadLocal WebDriver
- `WaitUtility` — Explicit + Fluent waits
- `ReportManager` / `TestListener` — ExtentReports
- `RetryAnalyzer` — automatic retries
- `ScreenshotUtility` — failure screenshots
- `ExcelUtility` — data-driven testing
- All advanced utilities (`ActionUtility`, `JSUtility`, etc.)

---

### Step 4 — Register Tests in a New Suite

```xml
<!-- testng-demoblaze.xml -->
<suite name="DemoBlaze Suite" parallel="classes" thread-count="2">
    <listeners>
        <listener class-name="com.saucedemo.listeners.TestListener"/>
        <listener class-name="com.saucedemo.listeners.AnnotationTransformer"/>
    </listeners>
    <parameter name="browser" value="chrome"/>
    <test name="Home Page Tests">
        <classes>
            <class name="com.yourcompany.tests.HomePageTest"/>
        </classes>
    </test>
</suite>
```

```bash
mvn test -DsuiteFile=testng-demoblaze.xml -Denv=local
```

---

### Step 5 — Extension Checklist

| Task | Time Estimate |
|------|-------------|
| Update `config.properties` with new URL | 1 min |
| Create Page Objects (1 per page) | 30–60 min / page |
| Create test classes | 30–60 min / class |
| Register in suite XML | 5 min |
| CI/CD: update `workflow_dispatch` suite list | 5 min |
| Update `.env` files if environment URLs differ | 5 min |

> **You do NOT need to change:** `BaseTest`, `DriverFactory`, `BrowserFactory`, `RemoteDriverFactory`, `EnvironmentManager`, any utility class, `TestListener`, `RetryAnalyzer`, `ReportManager`, `Jenkinsfile`, `docker-compose.yml`, or `pom.xml` profiles.

---

## 🎨 Design Patterns Used

| Pattern | Where Applied | Purpose |
|---------|--------------|---------|
| **Page Object Model** | `pages/` package | Decouple test logic from UI selectors |
| **Factory Pattern** | `BrowserFactory`, `RemoteDriverFactory` | Abstract driver creation strategy |
| **Singleton (static)** | `DriverFactory` (ThreadLocal) | One driver per thread, zero leaks |
| **Observer Pattern** | `TestListener` (ITestListener) | React to test lifecycle events |
| **Facade Pattern** | `ConfigReader` → `EnvironmentManager` | Simplify config access API |
| **Strategy Pattern** | Local vs Grid driver routing in `BaseTest` | Swap execution strategy at runtime |
| **Template Method** | `BaseTest` `@Before`/`@After` hooks | Define test skeleton; subclasses fill steps |
| **Data Provider** | `TestDataProvider` + Excel | Parameterize tests from external source |

---

## 📞 Support & Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/new-page-object`
3. Follow existing code patterns (Javadoc, `LoggerUtility`, `WaitUtility`)
4. Submit a Pull Request — CI pipeline runs automatically

---

*Built with ❤️ as an enterprise-grade reference implementation.*
