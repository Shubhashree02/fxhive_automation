package Admin;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

public class StageSuiteSession {
    private static WebDriver driver;

    private static ExtentReports extent;
    private static ExtentTest parentTest;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        parentTest = extent.createTest("FxHive Stage UI Test Suite");

        driver = TestConfig.createDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get(TestConfig.getBaseUrl());
        loginValid();
    }

    private void loginValid() {
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

    public static void ensureOnDashboard() {
        WebDriver d = getDriver();
        String url = "";
        try {
            url = d.getCurrentUrl();
        } catch (Exception ignored) {
            // ignore
        }
        if (url == null || !url.contains("dashboard")) {
            d.get(TestConfig.getDashboardUrl());
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        if (extent != null) {
            extent.flush();
        }
    }
}

