package Admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.ITestResult;


public class LoginTest {
     WebDriver driver;
    static ExtentReports extent;
    static ExtentTest test; // Changed to static
    ExtentSparkReporter spark;

    @BeforeSuite
    public void setupReport() {
        spark = new ExtentSparkReporter("test-output/LoginTestReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        
        extent.setSystemInfo("Application", "FxHive");
        extent.setSystemInfo("Environment", "STAGE");
        extent.setSystemInfo("Tester", "Shubhashree");
    }

    @BeforeMethod
    public void setUp() {
        //System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chrome-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        test = extent.createTest(this.getClass().getSimpleName()); // Initialize test here
        test.log(Status.INFO, "Launching browser and navigating to URL");
        driver.get("https://stage.fxhive.site/");
    }

    @Test(priority = 1)
    public void testValidLogin() {
        test.log(Status.INFO, "Starting valid login test");
        driver.findElement(By.id("username")).sendKeys("admin@fx31labs.com");
        driver.findElement(By.id("password")).sendKeys("Admin@123");
        driver.findElement(By.xpath("//button[text()='Login']")).click();


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("dashboard"));

        Assert.assertTrue(driver.getCurrentUrl().contains("stage.fxhive.site") && driver.getCurrentUrl().contains("dashboard"),
                "Expected redirect to dashboard. Actual: " + driver.getCurrentUrl());
        test.log(Status.PASS, "Login successful");
    }

    @Test(priority = 2)
    public void testInvalidUsename() {
        test.log(Status.INFO, "Testing invalid username");
        driver.findElement(By.id("username")).sendKeys("admin@m");
        driver.findElement(By.id("password")).sendKeys("admin@123");
        driver.findElement(By.xpath("//button[text()='Login']")).click();


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMsg = wait.until(driver -> {
            WebElement el = driver.findElement(By.id("loginMessage"));
            String style = el.getAttribute("style");
            if (style != null && style.contains("opacity: 1") && !style.contains("display: none")) {
                return el;
            }
            return null;
        });

        Assert.assertEquals(errorMsg.getText().trim(), "Invalid credentials");
        test.log(Status.PASS, "Correct error message shown for invalid username");
    }

    @Test(priority = 3)
    public void testInvalidPassword() {
        test.log(Status.INFO, "Testing invalid password");
        driver.findElement(By.id("username")).sendKeys("admin@fx31labs.com");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        driver.findElement(By.xpath("//button[text()='Login']")).click();


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMsg = wait.until(driver -> {
            WebElement el = driver.findElement(By.id("loginMessage"));
            String style = el.getAttribute("style");
            if (style != null && style.contains("opacity: 1") && !style.contains("display: none")) {
                return el;
            }
            return null;
        });

        Assert.assertEquals(errorMsg.getText().trim(), "Invalid credentials");
        test.log(Status.PASS, "Correct error message shown for invalid password");
    }

    @Test(priority = 4)
    public void testBlankCredentials() {
        test.log(Status.INFO, "Testing blank credentials");
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        usernameField.clear();
        passwordField.clear();
        driver.findElement(By.xpath("//button[text()='Login']")).click();


        Assert.assertEquals(usernameField.getAttribute("validationMessage"), "Please fill out this field.");
        test.log(Status.PASS, "Validation message shown for blank fields");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test failed: " + result.getThrowable());
        }
        driver.quit();
    }

    @AfterSuite
    public void tearDownReport() {
        extent.flush();
    }

}
