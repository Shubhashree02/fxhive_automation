package Admin;

import AdminPage.AddSalaryFullTime;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import java.time.Duration;
import java.util.List;

public class AddSalaryFullTimeTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test(invocationCount = 1)
    public void testAddSalaryFlow() {
        StageSuiteSession.ensureOnDashboard();
        AddSalaryFullTime addSalaryPage = new AddSalaryFullTime(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click on Salary Structure menu
        addSalaryPage.clickSalaryStructureMenu();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[href='/admin/salary-structure?type=full-time']")));

        // Step 2: Click on 'Full Time' option
        addSalaryPage.clickFullTimeOption();
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        longWait.until(d -> {
            if (!d.findElements(By.cssSelector("button[title='Add New Salary']")).isEmpty()
                && d.findElement(By.cssSelector("button[title='Add New Salary']")).isDisplayed()
                && d.findElement(By.cssSelector("button[title='Add New Salary']")).isEnabled()) return true;
            List<WebElement> btns = d.findElements(By.xpath("//button[contains(.,'Add New Salary') or contains(.,'Add new salary')]"));
            return btns.stream().anyMatch(WebElement::isDisplayed);
        });

        // Step 3: Click on 'Add New Salary' button (page object uses same fallbacks)
        addSalaryPage.clickAddNewSalaryBtn();
        longWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("employeeSelect")));

        // Step 4–5: Fill details (select employee, enter gross)
        addSalaryPage.selectEmployeeByIndex(0);
        int gross = 100000;
        addSalaryPage.enterGross(String.valueOf(gross));

        // Step 6: Validate calculated components then click Save button
        int basic = gross * 40 / 100;
        int hra = basic * 40 / 100;
        int special = gross - (basic + hra);
        addSalaryPage.verifySalaryComponents(gross);
        addSalaryPage.verifyTaxCalculation(basic, hra, special);
        // Click Save (waits for button in dialog to be enabled)
        longWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='dialog']")));
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