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
 * Test: Payroll → Consultant → Generate Payroll → select month & year → select employee → enter LOP days & remark → Generate.
 */
public class GeneratePayrollConsultantTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testGeneratePayrollConsultantFlow() {
        StageSuiteSession.ensureOnDashboard();
        GeneratePayrollFullTime payrollPage = new GeneratePayrollFullTime(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click Payroll menu, then Consultant
        payrollPage.clickPayrollMenu();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/admin/payroll?type=consultant']")));
        payrollPage.clickConsultantOption();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Generate Payroll')]")));

        // Step 2: Click Generate Payroll (opens modal)
        payrollPage.clickGeneratePayrollButton();

        // Step 3: Select month
        payrollPage.selectMonth("March");

        // Step 4: Select year
        payrollPage.selectYear("2026");

        // Step 5: Select employee
        payrollPage.selectEmployeeByIndex(0);

        // Step 6: Enter LOP days
        payrollPage.enterLopDays("0");

        // Step 7: Enter remark
        payrollPage.enterRemark("Automated test - Consultant");

        // Step 8: Click Generate button
        payrollPage.clickGenerateButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        handleAlertIfPresent(wait);
        System.out.println("✅ Generate Payroll (Consultant) completed.");
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
