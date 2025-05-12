package Admin;

import AdminPage.AddSalaryFullTime;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.time.Duration;

public class AddSalaryFullTimeTest extends BaseTest {

    @Test(invocationCount = 4)
    public void testAddSalaryFlow() {
        AddSalaryFullTime addSalaryPage = new AddSalaryFullTime(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click on Salary Structure menu
        addSalaryPage.clickSalaryStructureMenu();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[@data-content='salaryStructure' and @data-employee-type='Full time']")));

        // Step 2: Click on 'Full Time' option
        addSalaryPage.clickFullTimeOption();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("addSalaryBtn")));

        // Step 3: Click on 'Add New Salary' button
        addSalaryPage.clickAddNewSalaryBtn();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("employeeSelect")));

        // Step 4: Select employee from dropdown (selecting 1st employee)
        addSalaryPage.selectEmployeeByIndex(1);

        // Step 5: Enter Gross Salary
        int gross = 100000;
        addSalaryPage.enterGross(String.valueOf(gross));

        // Step 6: Validate calculated components
        int basic = gross * 40 / 100;
        int hra = basic * 40 / 100;
        int special = gross - (basic + hra);

        addSalaryPage.verifySalaryComponents(gross);
        addSalaryPage.verifyTaxCalculation(basic, hra, special);

        // Step 7: Save the Salary Structure (scrolls and clicks robustly)
        addSalaryPage.clickSave();

        // Optional: Pause for manual inspection (remove this in production)
        try {
            Thread.sleep(2000); // 2 seconds to visually check for popup/alert
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Step 8: Try to handle JavaScript alert after saving
        handleAlertIfPresent(wait);

        System.out.println("✅ Salary structure added and validated successfully.");
    }

    /**
     * Handles a JavaScript alert if present, otherwise prints a message.
     */
    private void handleAlertIfPresent(WebDriverWait wait) {
        // Try direct switch to alert first
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            System.out.println("Alert says: " + alertText);
            alert.accept();
            System.out.println("Alert was handled directly.");
            return;
        } catch (NoAlertPresentException e) {
            System.out.println("No alert present (direct switch).");
        }

        // Fallback: try explicit wait for alert
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            System.out.println("Alert says (wait): " + alertText);
            alert.accept();
            System.out.println("Alert was handled (wait).");
        } catch (TimeoutException te) {
            System.out.println("No JavaScript alert appeared after saving (even after wait).");
            System.out.println("If you see a popup but Selenium doesn't, it may be a custom modal or OS-level dialog.");
            System.out.println("Try to inspect the popup or ask your developer for its HTML structure.");
        }
    }
} 