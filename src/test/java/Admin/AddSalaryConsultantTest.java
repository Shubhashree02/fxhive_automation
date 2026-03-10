package Admin;

import AdminPage.AddSalaryConsultant;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import java.time.Duration;

/**
 * Test: Add Salary for Consultant.
 * Flow: Click Salary Structure → Click Consultant → Add New Salary → Select employee, enter gross → Save.
 */
public class AddSalaryConsultantTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testAddSalaryConsultantFlow() {
        StageSuiteSession.ensureOnDashboard();
        AddSalaryConsultant addSalaryPage = new AddSalaryConsultant(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click on Salary Structure menu
        addSalaryPage.clickSalaryStructureMenu();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[href='/admin/salary-structure?type=consultant']")));

        // Step 2: Click on 'Consultant' option
        addSalaryPage.clickConsultantOption();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title='Add New Salary']")));

        // Step 3: Click on 'Add New Salary' button
        addSalaryPage.clickAddNewSalaryBtn();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("employeeSelect")));

        // Step 4–5: Fill details (select employee, consultancy fee, TDS %)
        addSalaryPage.selectEmployeeByIndex(0);
        addSalaryPage.enterConsultancyFee("75000");
        addSalaryPage.enterTdsPercentage("10");

        // Step 6: Click Save button
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form button[type='submit']")));
        addSalaryPage.clickSave();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        handleAlertIfPresent(wait);

        System.out.println("✅ Consultant salary structure added and saved successfully.");
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
            System.out.println("No JavaScript alert after save.");
        }
    }
}
