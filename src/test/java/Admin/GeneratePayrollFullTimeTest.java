package Admin;

import AdminPage.GeneratePayrollFullTime;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Test: Payroll → Full Time → Generate Payroll → fill details → Generate.
 */
public class GeneratePayrollFullTimeTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testGeneratePayrollFullTimeFlow() {
        StageSuiteSession.ensureOnDashboard();
        GeneratePayrollFullTime payrollPage = new GeneratePayrollFullTime(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click Payroll menu
        payrollPage.clickPayrollMenu();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/admin/payroll?type=full-time']")));

        // Step 2: Click Full Time
        payrollPage.clickFullTimeOption();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Generate Payroll')]")));

        // Step 3: Click Generate Payroll (opens modal)
        payrollPage.clickGeneratePayrollButton();

        // Step 4: Fill details — select year, select employee, LOP days, remark
        payrollPage.selectMonth("March");
        payrollPage.selectYear("2026");
        payrollPage.selectEmployeeByIndex(0);
        payrollPage.enterLopDays("0");
        payrollPage.enterRemark("Automated test");

        // Step 5: Click Generate button
        payrollPage.clickGenerateButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        handleAlertIfPresent(wait);
        System.out.println("✅ Generate Payroll (Full Time) completed.");
    }

    private void handleAlertIfPresent(WebDriverWait wait) {
        try {
            Alert alert = driver.switchTo().alert();
            System.out.println("Alert: " + alert.getText());
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.out.println("No alert present.");
        }
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("Alert (wait): " + alert.getText());
            alert.accept();
        } catch (TimeoutException te) {
            System.out.println("No JavaScript alert after generate.");
        }
    }
}
