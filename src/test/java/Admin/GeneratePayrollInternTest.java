package Admin;

import AdminPage.GeneratePayrollIntern;
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
 * Test: Payroll → Intern → Generate Payroll → select month & year → select employee → enter LOP days & remark → Generate.
 */
public class GeneratePayrollInternTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testGeneratePayrollInternFlow() {
        StageSuiteSession.ensureOnDashboard();
        GeneratePayrollIntern payrollPage = new GeneratePayrollIntern(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click Payroll menu, then Intern
        payrollPage.clickPayrollMenu();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/admin/payroll?type=intern']")));
        payrollPage.clickInternOption();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Generate Payroll')]")));

        // Step 2: Click Generate Payroll (opens modal)
        payrollPage.clickGeneratePayrollButton();

        // Step 3: Select month and year
        payrollPage.selectMonth("March");
        payrollPage.selectYear("2026");

        // Step 4: Select employee
        payrollPage.selectEmployeeByIndex(0);

        // Step 5: Enter LOP days and remark
        payrollPage.enterLopDays("0");
        payrollPage.enterRemark("Automated test - Intern");

        // Step 6: Click Generate button
        payrollPage.clickGenerateButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        handleAlertIfPresent(wait);
        System.out.println("✅ Generate Payroll (Intern) completed.");
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
