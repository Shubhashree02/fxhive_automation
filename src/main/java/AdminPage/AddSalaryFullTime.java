package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddSalaryFullTime {
    WebDriver driver;
    WebDriverWait wait;

    public AddSalaryFullTime(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // Page navigation locators
    private By salaryStructureMenu = By.xpath("//a[contains(@class, 'submenu-trigger') and .//i[contains(@class, 'fa-wallet')]]");
    private By fullTimeOption = By.xpath("//a[@data-content='salaryStructure' and @data-employee-type='Full time']");
    private By addNewSalaryBtn = By.id("addSalaryBtn");

    // Modal locators
    private By employeeDropdown = By.id("employeeSelect");
    private By grossInput = By.id("grossInput");
    private By basicPayDisplay = By.id("basicPayDisplay");
    private By hraDisplay = By.id("hraDisplay");
    private By specialAllowanceDisplay = By.id("specialAllowanceDisplay");
    private By taxInput = By.id("projectedIncomeTaxInput");

    // Updated: More specific Save button locator using XPath
    private By saveButton = By.xpath("//button[@type='submit' and contains(@class,'btn-primary') and normalize-space()='Save']");

    // Navigation actions
    public void clickSalaryStructureMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(salaryStructureMenu)).click();
    }

    public void clickFullTimeOption() {
        wait.until(ExpectedConditions.elementToBeClickable(fullTimeOption)).click();
    }

    public void clickAddNewSalaryBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(addNewSalaryBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
    }

    // Modal Actions
    public void selectEmployeeByIndex(int index) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeDropdown));
        Select employeeSelect = new Select(driver.findElement(employeeDropdown));
        employeeSelect.selectByIndex(index);
    }

    public void enterGross(String grossAmount) {
        WebElement gross = wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
        gross.clear();
        gross.sendKeys(grossAmount);
    }

    public void verifySalaryComponents(int gross) {
        int basic = gross * 40 / 100;
        int hra = basic * 40 / 100;
        int special = gross - (basic + hra);

        int actualBasic = Integer.parseInt(driver.findElement(basicPayDisplay).getAttribute("value").replace(",", ""));
        int actualHra = Integer.parseInt(driver.findElement(hraDisplay).getAttribute("value").replace(",", ""));
        int actualSpecial = Integer.parseInt(driver.findElement(specialAllowanceDisplay).getAttribute("value").replace(",", ""));

        assert actualBasic == basic : "Basic pay mismatch: expected " + basic + ", found " + actualBasic;
        assert actualHra == hra : "HRA mismatch: expected " + hra + ", found " + actualHra;
        assert actualSpecial == special : "Special allowance mismatch: expected " + special + ", found " + actualSpecial;
    }

    public void verifyTaxCalculation(int basic, int hra, int special) {
        int annualIncome = (basic + hra + special - 75000) * 12;
        int tax = annualIncome > 1200000 ? (annualIncome - 1200000) : 0;

        String taxValue = driver.findElement(taxInput).getAttribute("value").replace(",", "");
        int actualTax = Integer.parseInt(taxValue);

        assert actualTax == tax : "Tax calculation mismatch: expected " + tax + ", found " + actualTax;
    }

    // Robust Save Button Click (with scroll and JS fallback)
    public void clickSave() {
        try {
            List<WebElement> saveButtons = driver.findElements(saveButton);
            System.out.println("Number of Save buttons found: " + saveButtons.size());

            WebElement saveButtonElement = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButtonElement);
            saveButtonElement.click();
            System.out.println("Clicked the Save button.");
        } catch (Exception e) {
            System.out.println("Error clicking the Save button: " + e.getMessage());
            // Fallback: Try JS click if normal click fails
            try {
                WebElement saveButtonElement = driver.findElement(saveButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButtonElement);
                System.out.println("Clicked the Save button with JavaScript.");
            } catch (Exception ex) {
                System.out.println("Failed to click the Save button even with JavaScript: " + ex.getMessage());
            }
        }
    }
}
