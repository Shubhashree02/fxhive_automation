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
 * Page object for Add Salary - Intern flow.
 * Flow: Salary Structure menu → Intern → Add New Salary → enter data → Save.
 */
public class AddSalaryIntern {
    WebDriver driver;
    WebDriverWait wait;

    public AddSalaryIntern(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    // Page navigation locators
    private By salaryStructureMenu = By.xpath("//span[text()='Salary Structure']");
    // Intern link: same pattern as Full Time, href for intern
    private By internOption = By.cssSelector("a[href='/admin/salary-structure?type=intern']");
    private By addNewSalaryBtn = By.cssSelector("button[title='Add New Salary']");

    // Modal locators (same as Full Time)
    private By employeeDropdownTrigger = By.id("employeeSelect");
    private By employeeDropdownOptions = By.cssSelector("[role='option']");
    private By grossInput = By.id("gross");
    private By basicPayDisplay = By.id("basicPayDisplay");
    private By hraDisplay = By.id("hraDisplay");
    private By specialAllowanceDisplay = By.id("specialAllowanceDisplay");
    private By taxInput = By.id("projectedIncomeTaxInput");
    // Save: <button type="submit" class="... bg-primary text-primary-foreground ..." disabled="">Save</button> (Cancel is type="button")
    private By saveButton = By.cssSelector("form button[type='submit']");

    public void clickSalaryStructureMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(salaryStructureMenu)).click();
    }

    public void clickInternOption() {
        wait.until(ExpectedConditions.elementToBeClickable(internOption)).click();
    }

    public void clickAddNewSalaryBtn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addNewSalaryBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
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
    }

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

    public void verifyTaxCalculation(int basic, int hra, int special) {
        int annualIncome = (basic + hra + special - 75000) * 12;
        int tax = annualIncome > 1200000 ? (annualIncome - 1200000) : 0;

        String taxValue = driver.findElement(taxInput).getAttribute("value");
        int actualTax = Integer.parseInt(taxValue.replaceAll("[^0-9.-]", "").split("\\.")[0]);

        assert actualTax == tax : "Tax calculation mismatch: expected " + tax + ", found " + actualTax;
    }

    public void clickSave() {
        try {
            WebElement saveButtonElement = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButtonElement);
            saveButtonElement.click();
            System.out.println("Clicked the Save button (Intern).");
        } catch (Exception e) {
            System.out.println("Error clicking the Save button: " + e.getMessage());
            try {
                WebElement saveButtonElement = driver.findElement(saveButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButtonElement);
                System.out.println("Clicked the Save button with JavaScript.");
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click Save: " + ex.getMessage());
            }
        }
    }
}
