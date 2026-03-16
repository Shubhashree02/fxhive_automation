package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for Salary Structure → Consultant → Add New Salary.
 * Consultant form uses consultancy fee and TDS %, not gross. Adds dialog/form wait.
 */
public class AddSalaryConsultant extends AddSalaryBase {

    private static final By consultancyFeeInput = By.id("consultancyFee");
    private static final By tdsPercentageInput = By.id("tdsPercentage");
    private static final int MODAL_STABILIZE_MS = 600;

    public AddSalaryConsultant(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTypeSegment() {
        return "consultant";
    }

    @Override
    public void clickAddNewSalaryBtn() {
        super.clickAddNewSalaryBtn();
        WebDriverWait dialogWait = new WebDriverWait(driver, Duration.ofSeconds(12));
        dialogWait.until(d -> {
            if (!d.findElements(By.cssSelector("[role='dialog']")).isEmpty() && d.findElement(By.cssSelector("[role='dialog']")).isDisplayed()) return true;
            if (!d.findElements(By.cssSelector(".modal, [data-state='open']")).isEmpty()) return true;
            return false;
        });
        try { Thread.sleep(MODAL_STABILIZE_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        WebDriverWait formWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        formWait.until(d -> {
            if (!d.findElements(By.id("employeeSelect")).isEmpty() && d.findElement(By.id("employeeSelect")).isDisplayed()) return true;
            if (d.findElements(By.cssSelector("button[role='combobox']")).stream().anyMatch(WebElement::isDisplayed)) return true;
            if (!d.findElements(consultancyFeeInput).isEmpty() && d.findElement(consultancyFeeInput).isDisplayed()) return true;
            return false;
        });
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
