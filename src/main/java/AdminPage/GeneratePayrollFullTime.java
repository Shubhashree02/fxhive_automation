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
 * Page object for Payroll → Full Time / Intern → Generate Payroll flow.
 * Flow: Click Payroll menu → Full Time / Intern / Contractual / Consultant → Generate Payroll → fill details → Generate/Submit.
 */
public class GeneratePayrollFullTime {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Payroll menu: button with span "Payroll"
    private final By payrollMenuButton = By.xpath("//button[.//span[text()='Payroll']]");
    private final By fullTimeOption = By.cssSelector("a[href='/admin/payroll?type=full-time']");
    private final By internOption = By.cssSelector("a[href='/admin/payroll?type=intern']");
    private final By contractualOption = By.cssSelector("a[href='/admin/payroll?type=contractual']");
    private final By consultantOption = By.cssSelector("a[href='/admin/payroll?type=consultant']");
    private final By generatePayrollButton = By.xpath("//button[contains(., 'Generate Payroll')]");

    // Generate form: custom comboboxes (role="combobox")
    // Typical order: [0]=month, [1]=year, [2]=employee. If only 2 comboboxes, [0]=month, [1]=employee.
    private final By comboboxTriggers = By.cssSelector("button[role='combobox']");
    private final By comboboxOptions = By.cssSelector("[role='option']");
    private final By employeeComboboxTrigger = By.xpath("//button[@role='combobox' and contains(., 'Select Employee')]");
    // LOP Days: input name="lopDays"; Remark: textarea name="remarks" placeholder="Enter remarks..."
    private final By lopDaysInput = By.name("lopDays");
    private final By remarkInput = By.name("remarks");
    // Generate button: type="submit" with text "Generate"
    private final By generateSubmitButton = By.xpath("//button[@type='submit' and contains(.,'Generate')]");

    public GeneratePayrollFullTime(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickPayrollMenu() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(payrollMenuButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
    }

    public void clickFullTimeOption() {
        wait.until(ExpectedConditions.elementToBeClickable(fullTimeOption)).click();
    }

    /** Click Intern in Payroll section. */
    public void clickInternOption() {
        wait.until(ExpectedConditions.elementToBeClickable(internOption)).click();
    }

    /** Click Contractual in Payroll section. */
    public void clickContractualOption() {
        wait.until(ExpectedConditions.elementToBeClickable(contractualOption)).click();
    }

    /** Click Consultant in Payroll section. */
    public void clickConsultantOption() {
        wait.until(ExpectedConditions.elementToBeClickable(consultantOption)).click();
    }

    public void clickGeneratePayrollButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(generatePayrollButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(comboboxTriggers));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Select value from a combobox by its index in the form (0=month, 1=year, 2=employee). */
    public void selectComboboxByIndex(int comboboxIndex, String optionText) {
        List<WebElement> triggers = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(comboboxTriggers));
        if (comboboxIndex < 0 || comboboxIndex >= triggers.size()) {
            throw new IllegalArgumentException("Combobox index " + comboboxIndex + " out of range (0-" + (triggers.size() - 1) + ")");
        }
        WebElement trigger = triggers.get(comboboxIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trigger);
        try {
            trigger.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(comboboxOptions));
        List<WebElement> options = driver.findElements(comboboxOptions);
        for (WebElement opt : options) {
            if (opt.getText().trim().equals(optionText)) {
                try {
                    opt.click();
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
                }
                break;
            }
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Select month from first combobox (e.g. "March"). */
    public void selectMonth(String monthValue) {
        selectComboboxByIndex(0, monthValue);
    }

    /** Select year from second combobox (e.g. "2026"). */
    public void selectYear(String yearValue) {
        selectComboboxByIndex(1, yearValue);
    }

    /** Select employee by option index (0-based) from the "Select Employee" combobox. */
    public void selectEmployeeByIndex(int index) {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(employeeComboboxTrigger));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trigger);
        try {
            trigger.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(comboboxOptions));
        List<WebElement> options = driver.findElements(comboboxOptions);
        if (index >= 0 && index < options.size()) {
            WebElement opt = options.get(index);
            try {
                opt.click();
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
            }
        } else {
            throw new IllegalArgumentException("Employee option index " + index + " out of range (0-" + (options.size() - 1) + ")");
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Enter LOP Days (number). */
    public void enterLopDays(String days) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(lopDaysInput));
        input.clear();
        input.sendKeys(days);
    }

    /** Enter Remark text. */
    public void enterRemark(String text) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(remarkInput));
        input.clear();
        input.sendKeys(text);
    }

    public void clickGenerateButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(generateSubmitButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
    }
}
