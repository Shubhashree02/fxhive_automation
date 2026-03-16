package Admin;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

/**
 * Single shared browser session for the suite. Only one Chrome window is used.
 * Run only one test suite at a time (one "mvn test" or one IDE run); do not start
 * another run until the current one finishes, or you will see a second browser.
 */
public class StageSuiteSession {
    private static WebDriver driver;

    private static ExtentReports extent;
    private static ExtentTest parentTest;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        // Ensure no leftover driver from a previous run in the same JVM (single session only).
        if (driver != null) {
            try {
                driver.quit();
            } catch (Throwable ignored) {}
            driver = null;
        }

        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        parentTest = extent.createTest("FxHive Stage UI Test Suite");

        // Create single driver + login (one Chrome only).
        initOrRecreateDriverAndLogin();

        // Quit browser when JVM exits (e.g. run stopped, or only one test class run without suite).
        // Catch Throwable so no Error from quit() can cause JVM to exit with failure.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Throwable ignored) {}
                driver = null;
            }
        }));
    }

    private static synchronized void initOrRecreateDriverAndLogin() {
        // If we already have a driver, check whether the session is still alive.
        if (driver != null) {
            try {
                driver.getCurrentUrl();
                // If this works, the session is fine.
                return;
            } catch (WebDriverException e) {
                // Session is broken, try to quit and recreate.
                try {
                    driver.quit();
                } catch (Throwable ignored) {
                }
                driver = null;
            }
        }

        driver = TestConfig.createDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get(TestConfig.getBaseUrl());
        loginValid();
    }

    private static void loginValid() {
        ExtentTest loginNode = parentTest.createNode("Suite Login (Stage)");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            WebElement loginButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("form button[type='submit']"))
            );

            usernameField.clear();
            passwordField.clear();
            usernameField.sendKeys(TestConfig.getUsername());
            passwordField.sendKeys(TestConfig.getPassword());
            loginButton.click();

            wait.until(ExpectedConditions.urlContains("dashboard"));
            loginNode.log(Status.PASS, "Logged in successfully on Stage");
        } catch (Exception e) {
            loginNode.log(Status.FAIL, "Suite login failed: " + e.getMessage());
            throw new RuntimeException("Suite login failed: " + e.getMessage(), e);
        }
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized. Ensure StageSuiteSession ran first.");
        }
        return driver;
    }

    public static ExtentTest createNode(String name) {
        if (parentTest == null) {
            throw new IllegalStateException("ExtentReports is not initialized. Ensure StageSuiteSession ran first.");
        }
        return parentTest.createNode(name);
    }

    /**
     * Navigates to dashboard only if not already there. Avoids full-page refresh
     * at the start of every test so it doesn't look like repeated/infinite refresh.
     */
    public static void ensureOnDashboard() {
        try {
            // Make sure we have a live session and are logged in.
            initOrRecreateDriverAndLogin();
            WebDriver d = getDriver();
            String url = d.getCurrentUrl();
            if (url == null || !url.contains("dashboard")) {
                d.get(TestConfig.getDashboardUrl());
            }
        } catch (WebDriverException e) {
            // If session died between calls, recreate once and navigate again.
            initOrRecreateDriverAndLogin();
            WebDriver d = getDriver();
            d.get(TestConfig.getDashboardUrl());
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        // Quit driver first; swallow any exception so teardown never fails the build.
        if (driver != null) {
            try {
                driver.quit();
            } catch (Throwable t) {
                // Session may already be invalid; don't fail build.
            }
            driver = null;
        }
        // Flush reports; swallow any exception so build stays SUCCESS when all tests passed.
        if (extent != null) {
            try {
                extent.flush();
            } catch (Throwable t) {
                // Don't fail build due to report flush.
            }
        }
    }
}

