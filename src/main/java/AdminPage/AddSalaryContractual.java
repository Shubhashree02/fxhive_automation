package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for Salary Structure → Contractual → Add New Salary.
 * Adds dialog/form wait so the modal is ready before filling.
 */
public class AddSalaryContractual extends AddSalaryBase {

    private static final int MODAL_STABILIZE_MS = 600;

    public AddSalaryContractual(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTypeSegment() {
        return "contractual";
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
            if (!d.findElements(By.id("gross")).isEmpty() && d.findElement(By.id("gross")).isDisplayed()) return true;
            return false;
        });
    }
}
