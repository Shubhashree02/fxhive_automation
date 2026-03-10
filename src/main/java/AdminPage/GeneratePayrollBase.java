package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base page object for Generate Payroll flow (Full Time, Intern, Contractual, Consultant).
 * Form elements are scoped to the visible dialog so we never interact with a hidden duplicate (e.g. name="lopDays").
 */
public abstract class GeneratePayrollBase {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    private final By payrollMenuButton = By.xpath("//button[.//span[text()='Payroll']]");
    protected final By fullTimeOption = By.cssSelector("a[href='/admin/payroll?type=full-time']");
    protected final By internOption = By.cssSelector("a[href='/admin/payroll?type=intern']");
    protected final By contractualOption = By.cssSelector("a[href='/admin/payroll?type=contractual']");
    protected final By consultantOption = By.cssSelector("a[href='/admin/payroll?type=consultant']");
    private final By generatePayrollButton = By.xpath("//button[contains(., 'Generate Payroll')]");

    private final By comboboxTriggers = By.cssSelector("button[role='combobox']");
    private final By comboboxOptions = By.cssSelector("[role='option']");
    private final By employeeComboboxTrigger = By.xpath("//button[@role='combobox' and contains(., 'Select Employee')]");
    private final By generateSubmitButton = By.xpath("//button[@type='submit' and contains(.,'Generate')]");

    /** Scope form to the visible dialog to avoid interacting with hidden elements (e.g. multiple lopDays in DOM). */
    private final By dialogContainer = By.cssSelector("div[role='dialog']");

    public GeneratePayrollBase(WebDriver driver) {
        this.driver = driver;
        this.wait = PageHelper.newPageWait(driver);
    }

    public void clickPayrollMenu() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(payrollMenuButton));
        PageHelper.scrollAndClick(driver, wait, btn);
    }

    public void clickFullTimeOption() {
        wait.until(ExpectedConditions.elementToBeClickable(fullTimeOption)).click();
    }

    public void clickInternOption() {
        wait.until(ExpectedConditions.elementToBeClickable(internOption)).click();
    }

    public void clickContractualOption() {
        wait.until(ExpectedConditions.elementToBeClickable(contractualOption)).click();
    }

    public void clickConsultantOption() {
        wait.until(ExpectedConditions.elementToBeClickable(consultantOption)).click();
    }

    public void clickGeneratePayrollButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(generatePayrollButton));
        PageHelper.scrollAndClick(driver, wait, btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(comboboxTriggers));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void selectComboboxByIndex(int comboboxIndex, String optionText) {
        List<WebElement> triggers = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(comboboxTriggers));
        if (comboboxIndex < 0 || comboboxIndex >= triggers.size()) {
            throw new IllegalArgumentException("Combobox index " + comboboxIndex + " out of range (0-" + (triggers.size() - 1) + ")");
        }
        WebElement trigger = triggers.get(comboboxIndex);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trigger);
        try {
            trigger.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(comboboxOptions));
        List<WebElement> options = driver.findElements(comboboxOptions);
        for (WebElement opt : options) {
            if (opt.getText().trim().equals(optionText)) {
                try {
                    opt.click();
                } catch (Exception ex) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
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

    public void selectMonth(String monthValue) {
        selectComboboxByIndex(0, monthValue);
    }

    public void selectYear(String yearValue) {
        selectComboboxByIndex(1, yearValue);
    }

    public void selectEmployeeByIndex(int index) {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(employeeComboboxTrigger));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trigger);
        try {
            trigger.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(comboboxOptions));
        List<WebElement> options = driver.findElements(comboboxOptions);
        if (index >= 0 && index < options.size()) {
            WebElement opt = options.get(index);
            try {
                opt.click();
            } catch (Exception ex) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
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

    /**
     * Enter LOP Days scoped to the visible dialog and wait for element to be interactable.
     * Uses JS fallback if clear/sendKeys fails (element not interactable).
     */
    public void enterLopDays(String days) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
        WebElement input = dialog.findElement(By.name("lopDays"));
        wait.until(ExpectedConditions.visibilityOf(input));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
        try {
            input.clear();
            input.sendKeys(days);
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = arguments[0]; el.value = ''; el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true }));",
                input, days);
        }
    }

    /**
     * Enter Remark scoped to the visible dialog.
     */
    public void enterRemark(String text) {
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
        WebElement input = dialog.findElement(By.name("remarks"));
        wait.until(ExpectedConditions.visibilityOf(input));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", input);
        try {
            input.clear();
            input.sendKeys(text);
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = arguments[0]; el.value = ''; el.value = arguments[1]; el.dispatchEvent(new Event('input', { bubbles: true }));",
                input, text);
        }
    }

    public void clickGenerateButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(generateSubmitButton));
        PageHelper.scrollAndClick(driver, wait, btn);
    }
}
