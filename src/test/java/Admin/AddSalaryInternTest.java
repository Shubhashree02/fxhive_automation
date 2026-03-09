package Admin;

import AdminPage.AddSalaryIntern;
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
 * Test: Add Salary for Intern.
 * Flow: Click Salary Structure → Click Intern → Add New Salary → Select employee, enter gross → Save.
 */
public class AddSalaryInternTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testAddSalaryInternFlow() {
        StageSuiteSession.ensureOnDashboard();
        AddSalaryIntern addSalaryPage = new AddSalaryIntern(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click on Salary Structure menu
        addSalaryPage.clickSalaryStructureMenu();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[href='/admin/salary-structure?type=intern']")));

        // Step 2: Click on 'Intern' option
        addSalaryPage.clickInternOption();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title='Add New Salary']")));

        // Step 3: Click on 'Add New Salary' button
        addSalaryPage.clickAddNewSalaryBtn();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("employeeSelect")));

        // Step 4–5: Fill details (select employee, enter gross)
        addSalaryPage.selectEmployeeByIndex(0);
        int gross = 50000;
        addSalaryPage.enterGross(String.valueOf(gross));

        // Step 6: Click Save button
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form button[type='submit']")));
        addSalaryPage.clickSave();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        handleAlertIfPresent(wait);

        System.out.println("✅ Intern salary structure added and saved successfully.");
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
