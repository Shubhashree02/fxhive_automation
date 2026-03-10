package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Base page object for Add Salary flows (Full Time, Intern, Contractual, Consultant).
 * Shared: Salary Structure menu, type links, Add New Salary button, modal (employee dropdown, save).
 * Subclasses add type-specific navigation and form fields (gross vs consultancy fee/TDS).
 */
public abstract class AddSalaryBase {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected final By salaryStructureMenu = By.xpath("//span[text()='Salary Structure']");
    protected final By fullTimeOption = By.cssSelector("a[href='/admin/salary-structure?type=full-time']");
    protected final By internOption = By.cssSelector("a[href='/admin/salary-structure?type=intern']");
    protected final By contractualOption = By.cssSelector("a[href='/admin/salary-structure?type=contractual']");
    protected final By consultantOption = By.cssSelector("a[href='/admin/salary-structure?type=consultant']");
    protected final By addNewSalaryBtn = By.cssSelector("button[title='Add New Salary']");

    protected final By employeeDropdownTrigger = By.id("employeeSelect");
    protected final By employeeDropdownOptions = By.cssSelector("[role='option']");
    protected final By grossInput = By.id("gross");
    protected final By basicPayDisplay = By.id("basicPayDisplay");
    protected final By hraDisplay = By.id("hraDisplay");
    protected final By specialAllowanceDisplay = By.id("specialAllowanceDisplay");
    protected final By taxInput = By.id("projectedIncomeTaxInput");
    protected final By saveButton = By.cssSelector("form button[type='submit']");

    public AddSalaryBase(WebDriver driver) {
        this.driver = driver;
        this.wait = PageHelper.newPageWait(driver);
    }

    /**
     * Type for this salary page (Full Time, Intern, Contractual, Consultant).
     */
    protected abstract String getTypeSegment();

    public void clickSalaryStructureMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(salaryStructureMenu)).click();
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

    /** Click Add New Salary and wait for modal (employee dropdown, combobox, or gross input visible). Subclasses may override to add extra wait. */
    public void clickAddNewSalaryBtn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addNewSalaryBtn));
        PageHelper.scrollAndClick(driver, wait, btn);

        WebDriverWait modalWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(PageConstants.MODAL_WAIT_SECONDS));
        modalWait.until(d -> {
            if (!d.findElements(employeeDropdownTrigger).isEmpty() && d.findElement(employeeDropdownTrigger).isDisplayed()) return true;
            if (d.findElements(By.cssSelector("button[role='combobox']")).stream().anyMatch(WebElement::isDisplayed)) return true;
            if (!d.findElements(grossInput).isEmpty() && d.findElement(grossInput).isDisplayed()) return true;
            return false;
        });

        // Type-specific extra waits
        String type = getTypeSegment();
        if ("full-time".equals(type) || "contractual".equals(type)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
        } else if ("intern".equals(type)) {
            WebDriverWait shortWait = PageHelper.newShortWait(driver);
            try {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
            } catch (org.openqa.selenium.TimeoutException ignored) {
                // Intern form may show gross after employee is selected
            }
        } else if ("consultant".equals(type)) {
            try {
                Thread.sleep(PageConstants.DEFAULT_ANIMATION_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** Resolve employee dropdown: #employeeSelect if visible, else button[role='combobox']. */
    protected By getEmployeeTriggerLocator() {
        if (!driver.findElements(employeeDropdownTrigger).isEmpty() && driver.findElement(employeeDropdownTrigger).isDisplayed()) {
            return employeeDropdownTrigger;
        }
        return By.cssSelector("button[role='combobox']");
    }

    public void selectEmployeeByIndex(int index) {
        By triggerBy = getEmployeeTriggerLocator();
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(triggerBy));
        trigger.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeDropdownOptions));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(employeeDropdownOptions));
        if (index >= 0 && index < options.size()) {
            options.get(index).click();
        } else {
            throw new IllegalArgumentException("Employee option index " + index + " out of range (0-" + (options.size() - 1) + ")");
        }
    }

    /** Enter gross (Full Time, Intern, Contractual). Wait for breakdown after. */
    public void enterGross(String grossAmount) {
        WebElement gross = wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
        gross.clear();
        gross.sendKeys(grossAmount);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Verify basic/HRA/special from gross (40% basic, 40% HRA of basic, rest special). Used by Full Time and Intern. */
    public void verifySalaryComponents(int gross) {
        int basic = gross * 40 / 100;
        int hra = basic * 40 / 100;
        int special = gross - (basic + hra);
        String rawBasic = driver.findElement(basicPayDisplay).getAttribute("value");
        String rawHra = driver.findElement(hraDisplay).getAttribute("value");
        String rawSpecial = driver.findElement(specialAllowanceDisplay).getAttribute("value");
        int actualBasic = Integer.parseInt(rawBasic.replaceAll("[^0-9.-]", "").split("\\.")[0]);
        int actualHra = Integer.parseInt(rawHra.replaceAll("[^0-9.-]", "").split("\\.")[0]);
        int actualSpecial = Integer.parseInt(rawSpecial.replaceAll("[^0-9.-]", "").split("\\.")[0]);
        assert actualBasic == basic : "Basic pay mismatch: expected " + basic + ", found " + actualBasic;
        assert actualHra == hra : "HRA mismatch: expected " + hra + ", found " + actualHra;
        assert actualSpecial == special : "Special allowance mismatch: expected " + special + ", found " + actualSpecial;
    }

    /** Verify tax from components. Used by Full Time and Intern. */
    public void verifyTaxCalculation(int basic, int hra, int special) {
        int annualIncome = (basic + hra + special - 75000) * 12;
        int tax = annualIncome > 1200000 ? (annualIncome - 1200000) : 0;
        String taxValue = driver.findElement(taxInput).getAttribute("value");
        int actualTax = Integer.parseInt(taxValue.replaceAll("[^0-9.-]", "").split("\\.")[0]);
        assert actualTax == tax : "Tax calculation mismatch: expected " + tax + ", found " + actualTax;
    }

    /** Click Save with scroll and JS fallback. Log message can be overridden by subclasses. */
    public void clickSave() {
        clickSaveWithLabel(getTypeSegment());
    }

    /** Click Save and log custom label (e.g. "Save (Intern)"). */
    protected void clickSaveWithLabel(String label) {
        try {
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            PageHelper.scrollAndClick(driver, wait, saveBtn);
            System.out.println("Clicked the Save button (" + label + ").");
        } catch (Exception e) {
            System.out.println("Error clicking the Save button: " + e.getMessage());
            try {
                WebElement saveBtn = driver.findElement(saveButton);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('disabled'); arguments[0].click();", saveBtn);
                System.out.println("Clicked the Save button with JavaScript.");
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click Save: " + ex.getMessage());
            }
        }
    }
}
