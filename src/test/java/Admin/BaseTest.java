package Admin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected ExtentReports extent;
    protected ExtentTest parentTest;
    protected ExtentTest loginTest;
    ExtentSparkReporter spark;
    private boolean isLoggedIn = false; // Track login status

    @BeforeClass
    public void setUp() {
        // Setup ExtentReports
        spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        parentTest = extent.createTest("Admin Panel Test Suite");
        loginTest = parentTest.createNode("Login Test");

        // Setup WebDriver
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("httpS://stage.fxhive.site/");
        loginTest.log(Status.INFO, "Navigated to login page");
    }

    @BeforeMethod
    public void checkLogin() {
        if (!isLoggedIn) {
            performLogin("admin@fx31labs.com", "admin@123");
            isLoggedIn = true;
        }
    }

    public void performLogin(String email, String password) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Wait until the username input field is visible
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            WebElement loginButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("form button[type='submit']")));

            // Enter login credentials
            usernameField.sendKeys(email);
            passwordField.sendKeys(password);
            loginButton.click();

            // Wait until URL contains dashboard keyword
            wait.until(ExpectedConditions.urlContains("admin_dashboard"));

            loginTest.log(Status.PASS, "Login successful, redirected to dashboard");
        } catch (TimeoutException te) {
            loginTest.log(Status.FAIL,
                    "Login failed: Element not found or page did not load in time - " + te.getMessage());
            throw new RuntimeException("Login failed: Element not found or timeout - " + te.getMessage());
        } catch (Exception e) {
            loginTest.log(Status.FAIL, "Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            parentTest.log(Status.INFO, "Browser closed");
        }

        if (extent != null) {
            extent.flush();
        }
    }

}
