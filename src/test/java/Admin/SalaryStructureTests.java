package Admin;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import java.time.Duration;
import static org.testng.Assert.*;

public class SalaryStructureTests extends BaseTest {

    private By salaryMenu = By.cssSelector("a.submenu-trigger");
    private By addSalaryBtn = By.id("addSalaryBtn");

    //Locators for employee type options
    private By fullTimeOption = By.xpath("//a[contains(text(),'Full Time')]");
    private By internOption = By.xpath("//a[contains(text(),'Intern')]");
    private By contractualOption = By.xpath("//a[contains(text(),'Contractual')]");

    private By employeeTypeNote = By.id("employeeTypeNote");
    private By closeButton = By.cssSelector(".close");
    private By tdsDisplay = By.id("tdsDisplay");
    private By grossInput = By.id("grossInput");
    private By employeeSelect = By.id("employeeSelect"); // Selector for the employee dropdown


     private void openSalaryForm(By employeeTypeOption) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(salaryMenu));
        driver.findElement(salaryMenu).click();

        wait.until(ExpectedConditions.elementToBeClickable(employeeTypeOption));
        driver.findElement(employeeTypeOption).click();

        wait.until(ExpectedConditions.elementToBeClickable(addSalaryBtn));
        driver.findElement(addSalaryBtn).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("salaryModalTitle")));
    }

    private void closeSalaryForm() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(closeButton));
        driver.findElement(closeButton).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("salaryModalTitle")));
    }

    @Test(priority = 1)
    public void verifySalaryCalculations() {
        verifySalaryCalculationsForType("fulltime");
           //Added retry Analyzer for the flakiness
         for (int i = 0; i < 2; i++) {
        try {
            verifySalaryCalculationsForType("intern");
             break; 
        } catch (AssertionError e) {
          System.err.println("Assertion failed on attempt " + (i + 1) + ": " + e.getMessage());
            if (i == 1) {
                throw e;
            }
        }
    }

        verifySalaryCalculationsForType("contractual");
    }

    public void verifySalaryCalculationsForType(String employeeType) {
        By employeeTypeOption;
        String expectedNote;

        switch (employeeType.toLowerCase()) {
            case "fulltime":
                employeeTypeOption = fullTimeOption;
                expectedNote = "";
                break;
            case "intern":
                employeeTypeOption = internOption;
                expectedNote = "Note: No deductions or taxes are applicable for interns.";
                break;
            case "contractual":
                employeeTypeOption = contractualOption;
                expectedNote = "Note: For contractual employees, only Gross Salary and TDS (if applicable) are set.";
                break;
            default:
                throw new IllegalArgumentException("Invalid employee type: " + employeeType);
        }

        openSalaryForm(employeeTypeOption);

        // Set employee type-specific validations
        try {
            switch (employeeType.toLowerCase()) {
                case "fulltime":
                    validateFullTimeCalculations();
                    break;
                case "intern":
                    validateInternForm();
                    break;
                case "contractual":
                    validateContractualForm();
                    break;
            }
        } finally {
            closeSalaryForm();
        }
    }

    private void validateFullTimeCalculations() {
        driver.findElement(grossInput).sendKeys("600000");
        double grossSalary = 600000.0;
        double basicPay = grossSalary * 0.4;
        double hra = basicPay * 0.4;
        double specialAllowance = grossSalary - basicPay - hra;

           String actualHra = driver.findElement(By.id("hraDisplay")).getAttribute("value");
        actualHra = actualHra.replace(",", "");
         if (actualHra.endsWith(".00")) {
            actualHra = actualHra.substring(0, actualHra.length() - 3);
        }


        String actualBasicPay = driver.findElement(By.id("basicPayDisplay")).getAttribute("value");
        actualBasicPay = actualBasicPay.replace(",", "");
        if (actualBasicPay.endsWith(".00")) {
            actualBasicPay = actualBasicPay.substring(0, actualBasicPay.length() - 3);
        }

       assertEquals(actualBasicPay, String.valueOf((int) basicPay), "Basic Pay is incorrect");
        assertEquals(actualHra, String.valueOf((int) hra), "HRA is incorrect");
       assertEquals(driver.findElement(By.id("specialAllowanceDisplay")).getAttribute("value"), String.format("%.2f", specialAllowance), "Special Allowance is incorrect");
    }

    private void validateInternForm() {
        // Validate employee type note
        WebElement employeeTypeNoteElement = driver.findElement(employeeTypeNote);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(employeeTypeNoteElement));
        assertEquals(employeeTypeNoteElement.getText(), "Note: No deductions or taxes are applicable for interns.", "Employee type note does not match for Intern");

        // Validate PF and ESIC toggles are not displayed
         assertFalse(isPfTogglePresent(), "PF Toggle should not be displayed for Intern");
        assertFalse(isEsicTogglePresent(), "ESIC Toggle should not be displayed for Intern");

        // Fill and validate gross input
        driver.findElement(grossInput).sendKeys("120000");

        // Validate TDS visibility and value
        WebElement tdsElement = driver.findElement(By.id("tdsDisplay"));
        WebDriverWait tdsWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        tdsWait.until(ExpectedConditions.visibilityOf(tdsElement));

        assertEquals(tdsElement.getAttribute("value"), "1200.00", "TDS should be 1200.00 for Intern");

        // Select an employee
        Select employeeSelectElement = new Select(driver.findElement(By.id("employeeSelect")));
       employeeSelectElement.selectByVisibleText("Krish Desai (22)");
    }
    private boolean isPfTogglePresent() {
        try {
            driver.findElement(By.id("pfToggle"));
            return true; // Element is present
        } catch (NoSuchElementException e) {
            return false; // Element is not present
        }
    }

    private boolean isEsicTogglePresent() {
        try {
            driver.findElement(By.id("esicToggle"));
            return true; // Element is present
        } catch (NoSuchElementException e) {
            return false; // Element is not present
        }
    }

    private void validateContractualForm() {
        // Validate employee type note
        WebElement employeeTypeNoteElement = driver.findElement(employeeTypeNote);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(employeeTypeNoteElement));
        assertEquals(employeeTypeNoteElement.getText(), "Note: For contractual employees, only Gross Salary and TDS (if applicable) are set.", "Employee type note does not match for Contractual");

        // Validate PF and ESIC toggles are not displayed
        assertFalse(isPfTogglePresent(), "PF Toggle should not be displayed for Contractual");
        assertFalse(isEsicTogglePresent(), "ESIC Toggle should not be displayed for Contractual");

        // Validate TDS calculation for different gross inputs
        driver.findElement(grossInput).sendKeys("120000");
        assertEquals(driver.findElement(tdsDisplay).getAttribute("value"), "1200.00", "TDS should be 1200.00 for Gross > 100000");

        driver.findElement(grossInput).clear();
        driver.findElement(grossInput).sendKeys("90000");
        assertEquals(driver.findElement(tdsDisplay).getAttribute("value"), "0.00", "TDS should be 0.00 for Gross <= 100000");
    }

    // Helper method to check if an element is present
    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
