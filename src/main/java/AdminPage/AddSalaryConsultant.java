package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for Salary Structure → Consultant → Add New Salary.
 * Mirrors Generate Payroll POM: one page per type; base holds shared logic.
 * Consultant form uses consultancy fee and TDS %, not gross.
 */
public class AddSalaryConsultant extends AddSalaryBase {

    private static final By consultancyFeeInput = By.id("consultancyFee");
    private static final By tdsPercentageInput = By.id("tdsPercentage");

    public AddSalaryConsultant(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTypeSegment() {
        return "consultant";
    }

    public void enterConsultancyFee(String amount) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement field = shortWait.until(ExpectedConditions.presenceOfElementLocated(consultancyFeeInput));
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(consultancyFeeInput));
            field.clear();
            field.sendKeys(amount);
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = arguments[0]; el.removeAttribute('disabled'); el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true }));",
                field, amount);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void enterTdsPercentage(String percentage) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement field = shortWait.until(ExpectedConditions.presenceOfElementLocated(tdsPercentageInput));
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(tdsPercentageInput));
            field.clear();
            field.sendKeys(percentage);
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = arguments[0]; el.removeAttribute('disabled'); el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true }));",
                field, percentage);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
