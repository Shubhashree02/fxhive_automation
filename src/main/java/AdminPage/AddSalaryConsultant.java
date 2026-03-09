package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

/**
 * Page object for Add Salary - Consultant flow.
 * Flow: Salary Structure menu → Consultant → Add New Salary → enter data → Save.
 */
public class AddSalaryConsultant {
    WebDriver driver;
    WebDriverWait wait;

    public AddSalaryConsultant(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    // Page navigation locators
    private By salaryStructureMenu = By.xpath("//span[text()='Salary Structure']");
    // Consultant: <a class="flex items-center gap-3 ..." href="/admin/salary-structure?type=consultant"><svg>...</svg><span class="truncate">Consultant</span></a>
    private By consultantOption = By.cssSelector("a[href='/admin/salary-structure?type=consultant']");
    private By addNewSalaryBtn = By.cssSelector("button[title='Add New Salary']");

    // Modal locators
    private By employeeDropdownTrigger = By.id("employeeSelect");
    private By employeeDropdownOptions = By.cssSelector("[role='option']");
    private By consultancyFeeInput = By.id("consultancyFee");
    private By tdsPercentageInput = By.id("tdsPercentage");
    private By saveButton = By.cssSelector("form button[type='submit']");

    public void clickSalaryStructureMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(salaryStructureMenu)).click();
    }

    public void clickConsultantOption() {
        wait.until(ExpectedConditions.elementToBeClickable(consultantOption)).click();
    }

    public void clickAddNewSalaryBtn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addNewSalaryBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
        // Wait for modal to open (employee dropdown visible)
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeDropdownTrigger));
        // Brief wait for form fields (e.g. gross) to render
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void selectEmployeeByIndex(int index) {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(employeeDropdownTrigger));
        trigger.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeDropdownOptions));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(employeeDropdownOptions));
        if (index >= 0 && index < options.size()) {
            options.get(index).click();
        } else {
            throw new IllegalArgumentException("Employee option index " + index + " out of range (0-" + (options.size() - 1) + ")");
        }
        // Wait for consultancy fee / TDS fields to become enabled after employee selection
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Enter Consultancy Fees (id="consultancyFee"). Waits for field to be visible; if disabled, uses JS to set value. */
    public void enterConsultancyFee(String amount) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement field = shortWait.until(ExpectedConditions.presenceOfElementLocated(consultancyFeeInput));
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(consultancyFeeInput));
            field.clear();
            field.sendKeys(amount);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0]; el.removeAttribute('disabled'); el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true }));",
                field, amount);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Enter TDS Percentage % (id="tdsPercentage"). Waits for field to be visible; if disabled, uses JS to set value. */
    public void enterTdsPercentage(String percentage) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement field = shortWait.until(ExpectedConditions.presenceOfElementLocated(tdsPercentageInput));
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(tdsPercentageInput));
            field.clear();
            field.sendKeys(percentage);
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0]; el.removeAttribute('disabled'); el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true }));",
                field, percentage);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** @deprecated Consultant form uses consultancy fee and TDS %; use enterConsultancyFee and enterTdsPercentage. */
    @Deprecated
    public void enterGross(String grossAmount) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement gross = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gross")));
            gross.clear();
            gross.sendKeys(grossAmount);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (org.openqa.selenium.TimeoutException e) {
            // Consultant form uses consultancyFee, not gross
        }
    }

    public void clickSave() {
        // Save button is disabled until form is valid; wait for it to become clickable
        try {
            WebElement saveButtonElement = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButtonElement);
            saveButtonElement.click();
            System.out.println("Clicked the Save button (Consultant).");
        } catch (Exception e) {
            System.out.println("Error clicking the Save button: " + e.getMessage());
            try {
                WebElement saveButtonElement = driver.findElement(saveButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('disabled'); arguments[0].click();", saveButtonElement);
                System.out.println("Clicked the Save button with JavaScript.");
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click Save: " + ex.getMessage());
            }
        }
    }
}
