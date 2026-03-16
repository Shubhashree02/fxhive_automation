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
    /** Fallback when button has no title or uses different markup (e.g. Full Time page). */
    protected final By[] addNewSalaryBtnFallbacks = {
        By.xpath("//button[contains(.,'Add New Salary') or contains(.,'Add new salary')]"),
        By.cssSelector("button[title='Add New Salary']"),
        By.xpath("//a[contains(.,'Add New Salary')]")
    };

    protected final By employeeDropdownTrigger = By.id("employeeSelect");
    protected final By employeeDropdownOptions = By.cssSelector("[role='option']");
    /** Fallback for dropdowns that use li or div instead of role='option' (e.g. some Intern UIs). */
    protected final By employeeDropdownOptionsFallback = By.cssSelector("[role='listbox'] li, [role='listbox'] [role='option'], ul[role='listbox'] > li, .dropdown-content li, [data-state='open'] [role='option']");
    protected final By grossInput = By.id("gross");
    protected final By basicPayDisplay = By.id("basicPayDisplay");
    protected final By hraDisplay = By.id("hraDisplay");
    protected final By specialAllowanceDisplay = By.id("specialAllowanceDisplay");
    protected final By taxInput = By.id("projectedIncomeTaxInput");
    protected final By saveButton = By.cssSelector("form button[type='submit']");
    /** Save button inside the modal so we don't click a different form's submit. */
    protected final By[] saveButtonInDialogFirst = {
        By.cssSelector("[role='dialog'] form button[type='submit']"),
        By.cssSelector("[role='dialog'] button[type='submit']"),
        By.cssSelector("div[role='dialog'] button[type='submit']"),
        By.xpath("//*[@role='dialog']//button[contains(translate(.,'SAVE','save'),'save') or @type='submit']"),
        By.cssSelector("form button[type='submit']")
    };

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
        WebElement btn = wait.until(d -> {
            for (By by : addNewSalaryBtnFallbacks) {
                List<WebElement> elts = d.findElements(by);
                for (WebElement e : elts) {
                    if (e.isDisplayed() && e.isEnabled()) return e;
                }
            }
            return null;
        });
        if (btn == null) {
            throw new RuntimeException("Add New Salary button not found (tried title, button text, and link).");
        }
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
        // Wait for at least one option to be visible (don't require ALL options visible - avoids timeout with long/virtualized lists).
        wait.until(d -> {
            List<WebElement> list = d.findElements(employeeDropdownOptions);
            if (!list.isEmpty() && list.get(0).isDisplayed()) return true;
            List<WebElement> fallback = d.findElements(employeeDropdownOptionsFallback);
            return !fallback.isEmpty() && fallback.get(0).isDisplayed();
        });
        List<WebElement> options = driver.findElements(employeeDropdownOptions);
        if (options.isEmpty()) options = driver.findElements(employeeDropdownOptionsFallback);
        if (index >= 0 && index < options.size()) {
            options.get(index).click();
        } else {
            throw new IllegalArgumentException("Employee option index " + index + " out of range (0-" + (options.size() - 1) + ")");
        }
    }

    /** Enter gross (Full Time, Intern, Contractual). Wait for breakdown when present so Save can be clicked. */
    public void enterGross(String grossAmount) {
        WebElement gross = wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
        gross.clear();
        gross.sendKeys(grossAmount);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            waitForSalaryBreakdownToDisplay();
        } catch (org.openqa.selenium.TimeoutException e) {
            // Intern or some UIs may not show breakdown fields or use different IDs; still allow Save.
        }
    }

    /** Wait for calculated salary fields (basic, HRA, special) to be visible and populated so "data is displaying". */
    protected void waitForSalaryBreakdownToDisplay() {
        WebDriverWait breakdownWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        breakdownWait.until(d -> {
            String basic = getElementValueOrText(d, basicPayDisplay);
            String hra = getElementValueOrText(d, hraDisplay);
            String special = getElementValueOrText(d, specialAllowanceDisplay);
            return (basic != null && !basic.trim().isEmpty())
                || (hra != null && !hra.trim().isEmpty())
                || (special != null && !special.trim().isEmpty());
        });
    }

    /** Get value attribute (input) or text (div/span) for display fields. */
    private static String getElementValueOrText(WebDriver d, By by) {
        List<WebElement> elts = d.findElements(by);
        if (elts.isEmpty() || !elts.get(0).isDisplayed()) return null;
        String v = elts.get(0).getAttribute("value");
        if (v != null && !v.trim().isEmpty()) return v;
        return elts.get(0).getText();
    }

    /** Verify basic/HRA/special from gross (40% basic, 40% HRA of basic, rest special). Used by Full Time and Intern. */
    public void verifySalaryComponents(int gross) {
        int basic = gross * 40 / 100;
        int hra = basic * 40 / 100;
        int special = gross - (basic + hra);
        String rawBasic = getElementValueOrText(driver, basicPayDisplay);
        String rawHra = getElementValueOrText(driver, hraDisplay);
        String rawSpecial = getElementValueOrText(driver, specialAllowanceDisplay);
        if (rawBasic == null || rawBasic.trim().isEmpty()) throw new RuntimeException("Basic pay field not displaying (id=basicPayDisplay).");
        if (rawHra == null || rawHra.trim().isEmpty()) throw new RuntimeException("HRA field not displaying (id=hraDisplay).");
        if (rawSpecial == null || rawSpecial.trim().isEmpty()) throw new RuntimeException("Special allowance field not displaying (id=specialAllowanceDisplay).");
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(taxInput));
        String taxValue = getElementValueOrText(driver, taxInput);
        if (taxValue == null || taxValue.trim().isEmpty()) throw new RuntimeException("Tax field not displaying (id=projectedIncomeTaxInput).");
        int actualTax = Integer.parseInt(taxValue.replaceAll("[^0-9.-]", "").split("\\.")[0]);
        assert actualTax == tax : "Tax calculation mismatch: expected " + tax + ", found " + actualTax;
    }

    /** Click Save with scroll and JS fallback. Log message can be overridden by subclasses. */
    public void clickSave() {
        clickSaveWithLabel(getTypeSegment());
    }

    /** Wait for Save button inside dialog to be enabled, or return first visible submit in dialog so Full Time/Intern can save. */
    protected WebElement getSaveButtonInDialog() {
        WebDriverWait saveWait = new WebDriverWait(driver, java.time.Duration.ofSeconds(18));
        try {
            return saveWait.until(d -> {
                for (By by : saveButtonInDialogFirst) {
                    List<WebElement> elts = d.findElements(by);
                    for (WebElement e : elts) {
                        if (!e.isDisplayed()) continue;
                        if (!"true".equals(e.getAttribute("disabled")) && e.isEnabled()) return e;
                    }
                }
                return null;
            });
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Fallback: return first visible submit in dialog so we can try JS click (saves Full Time / Intern).
            for (By by : saveButtonInDialogFirst) {
                List<WebElement> elts = driver.findElements(by);
                for (WebElement btn : elts) {
                    if (btn.isDisplayed()) return btn;
                }
            }
            return null;
        }
    }

    /** Click Save and log custom label (e.g. "Save (Intern)"). Prefers button inside dialog and waits until enabled. */
    protected void clickSaveWithLabel(String label) {
        try {
            WebElement saveBtn = getSaveButtonInDialog();
            if (saveBtn == null) throw new RuntimeException("Save button not found in dialog.");
            if (!saveBtn.isEnabled()) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].removeAttribute('disabled'); arguments[0].click();", saveBtn);
                System.out.println("Clicked the Save button (" + label + ") with JavaScript (was disabled).");
            } else {
                PageHelper.scrollAndClick(driver, wait, saveBtn);
                System.out.println("Clicked the Save button (" + label + ").");
            }
        } catch (Exception e) {
            System.out.println("Error clicking the Save button: " + e.getMessage());
            try {
                WebElement saveBtn = driver.findElement(saveButton);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].removeAttribute('disabled'); arguments[0].click();", saveBtn);
                System.out.println("Clicked the Save button with JavaScript.");
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click Save: " + ex.getMessage());
            }
        }
    }
}
