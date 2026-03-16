package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for Salary Structure → Intern → Add New Salary.
 * Adds Intern-specific waits so the modal and form are ready before filling.
 */
public class AddSalaryIntern extends AddSalaryBase {

    private static final int INTERN_MODAL_STABILIZE_MS = 800;

    public AddSalaryIntern(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTypeSegment() {
        return "intern";
    }

    /**
     * Open Add New Salary modal and wait until the Intern form is ready
     * (dialog visible, then employee dropdown or gross field available).
     */
    @Override
    public void clickAddNewSalaryBtn() {
        super.clickAddNewSalaryBtn();

        WebDriverWait dialogWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        dialogWait.until(d -> {
            if (!d.findElements(By.cssSelector("div[role='dialog']")).isEmpty() && d.findElement(By.cssSelector("div[role='dialog']")).isDisplayed()) return true;
            if (!d.findElements(By.cssSelector("[role='dialog']")).isEmpty() && d.findElement(By.cssSelector("[role='dialog']")).isDisplayed()) return true;
            if (!d.findElements(By.cssSelector(".modal, [data-state='open']")).isEmpty()) return true;
            return false;
        });

        try {
            Thread.sleep(INTERN_MODAL_STABILIZE_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebDriverWait formWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        formWait.until(d -> {
            if (!d.findElements(By.id("employeeSelect")).isEmpty() && d.findElement(By.id("employeeSelect")).isDisplayed()) return true;
            if (d.findElements(By.cssSelector("button[role='combobox']")).stream().anyMatch(WebElement::isDisplayed)) return true;
            if (!d.findElements(By.id("gross")).isEmpty() && d.findElement(By.id("gross")).isDisplayed()) return true;
            return false;
        });
    }
}
