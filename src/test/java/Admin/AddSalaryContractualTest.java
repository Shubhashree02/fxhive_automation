package Admin;

import AdminPage.AddSalaryContractual;
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

/**
 * Test: Add Salary for Contractual.
 * Flow: Click Salary Structure → Click Contractual → Add New Salary → Select employee, enter gross → Save.
 */
public class AddSalaryContractualTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testAddSalaryContractualFlow() {
        StageSuiteSession.ensureOnDashboard();
        AddSalaryContractual addSalaryPage = new AddSalaryContractual(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Click on Salary Structure menu
        addSalaryPage.clickSalaryStructureMenu();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[href='/admin/salary-structure?type=contractual']")));

        // Step 2: Click on 'Contractual' option
        addSalaryPage.clickContractualOption();
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        longWait.until(d -> {
            if (!d.findElements(By.cssSelector("button[title='Add New Salary']")).isEmpty()
                && d.findElement(By.cssSelector("button[title='Add New Salary']")).isDisplayed()
                && d.findElement(By.cssSelector("button[title='Add New Salary']")).isEnabled()) return true;
            return d.findElements(By.xpath("//button[contains(.,'Add New Salary') or contains(.,'Add new salary')]")).stream()
                .anyMatch(el -> el.isDisplayed());
        });

        // Step 3: Click on 'Add New Salary' button (page object uses same fallbacks)
        addSalaryPage.clickAddNewSalaryBtn();

        // Step 4–5: Fill details (select employee, enter gross)
        addSalaryPage.selectEmployeeByIndex(1);
        int gross = 60000;
        addSalaryPage.enterGross(String.valueOf(gross));

        // Step 6: Click Save (waits for button in dialog to be enabled)
        longWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='dialog']")));
        addSalaryPage.clickSave();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        handleAlertIfPresent(wait);

        System.out.println("✅ Contractual salary structure added and saved successfully.");
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
