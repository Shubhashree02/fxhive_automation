package AdminPage;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


public class AddEmployeePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators for form elements
    private By addEmployeeLink = By.cssSelector("a[data-content='addEmployee']");
    private By addEmployeeForm = By.id("addEmployeeForm");
    private By firstNameField = By.id("firstName");
    private By lastNameField = By.id("lastName");
    private By designationField = By.id("designation");
    private By departmentField = By.id("department");
    private By employeeTypeDropdown = By.id("employeeType");
    private By contactField = By.id("contact");
    private By emailField = By.id("email");
    private By dateOfJoiningField = By.id("dateOfJoining");
    private By dobField = By.id("dob");
    private By passwordField = By.id("password");
    private By nameAsPerBankField = By.id("nameAsPerBank");
    private By bankNameField = By.id("bankName");
    private By ifscCodeField = By.id("ifscCode");
    private By bankAccountNumberField = By.id("bankAccountNumber");
    private By panNumberBankField = By.id("panNumberBank");
    private By submitButton = By.cssSelector("#addEmployeeForm button[type='submit']");

    public AddEmployeePage(WebDriver driver) {
        // Constructor to initialize the WebDriver and WebDriverWait
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));

}
    // Action to click on Add Employee link
    public void clickAddEmployeeLink() {
        // Find the Add Employee link and click it
        WebElement addEmployeeLinkElement = wait.until(ExpectedConditions.elementToBeClickable(addEmployeeLink));
        addEmployeeLinkElement.click();
    }
    // Wait for the Add Employee form to be visible
    public void waitForAddEmployeeForm() {
        // Wait until the Add Employee form is visible
        WebElement addEmployeeFormElement = wait.until(ExpectedConditions.visibilityOfElementLocated(addEmployeeForm));
    }
    // Method to enter the first name
    public void enterFirstName(String firstName) {
        // Wait until the first name field is visible and then enter the first name
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField)).sendKeys(firstName);
}
// Method to enter the last name
public void enterLastName(String lastName) {
    // Wait until the last name field is visible and then enter the last name
    wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameField)).sendKeys(lastName);
}
 // Method to enter the designation
 public void enterDesignation(String designation) {
    // Wait until the designation field is visible and then enter the designation
    wait.until(ExpectedConditions.visibilityOfElementLocated(designationField)).sendKeys(designation);
}
// Method to enter the department
public void enterDepartment(String department) {
    // Wait until the department field is visible and then enter the department
    wait.until(ExpectedConditions.visibilityOfElementLocated(departmentField)).sendKeys(department);
}
 // Method to select the employee type
    public void selectEmployeeType(String type) {
        // Wait until the employee type dropdown is visible
        WebElement employeeTypeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeTypeDropdown));
        // Create a Select object to interact with the dropdown
        Select employeeTypeSelect = new Select(employeeTypeElement);
        // Select the employee type by visible text
        employeeTypeSelect.selectByVisibleText(type);
    }

    // Method to enter the contact number
    public void enterContact(String contact) {
        // Wait until the contact field is visible and then enter the contact number
        wait.until(ExpectedConditions.visibilityOfElementLocated(contactField)).sendKeys(contact);
    }

    // Method to enter the email address
    public void enterEmail(String email) {
        // Wait until the email field is visible and then enter the email address
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).sendKeys(email);
    }

    // Method to enter the date of joining
    public void enterDateOfJoining(String date) {
        // Wait until the date of joining field is visible
        WebElement dojElement = wait.until(ExpectedConditions.visibilityOfElementLocated(dateOfJoiningField));
        // Clear the field before entering the date
        dojElement.clear();
        // Enter the date of joining
        dojElement.sendKeys(date);
    }

    // Method to enter the date of birth
    public void enterDateOfBirth(String dob) {
        // Wait until the date of birth field is visible
        WebElement dobElement = wait.until(ExpectedConditions.visibilityOfElementLocated(dobField));
        // Clear the field before entering the date
        dobElement.clear();
        // Enter the date of birth
        dobElement.sendKeys(dob);
    }

    // Method to enter the password
    public void enterPassword(String password) {
        // Wait until the password field is visible and then enter the password
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField)).sendKeys(password);
    }

    // Method to enter the name as per bank
    public void enterNameAsPerBank(String name) {
        // Wait until the name as per bank field is visible and then enter the name
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameAsPerBankField)).sendKeys(name);
    }

    // Method to enter the bank name
    public void enterBankName(String bankName) {
        // Wait until the bank name field is visible and then enter the bank name
        wait.until(ExpectedConditions.visibilityOfElementLocated(bankNameField)).sendKeys(bankName);
    }

    // Method to enter the IFSC code
    public void enterIFSCCode(String ifsc) {
        // Wait until the IFSC code field is visible and then enter the IFSC code
        wait.until(ExpectedConditions.visibilityOfElementLocated(ifscCodeField)).sendKeys(ifsc);
    }

    // Method to enter the bank account number
    public void enterBankAccountNumber(String accountNumber) {
        // Wait until the bank account number field is visible and then enter the account number
        wait.until(ExpectedConditions.visibilityOfElementLocated(bankAccountNumberField)).sendKeys(accountNumber);
    }
    // Method to enter the PAN
    public void enterPAN(String pan) {
        // Wait until the PAN field is visible and then enter the PAN
        wait.until(ExpectedConditions.visibilityOfElementLocated(panNumberBankField)).sendKeys(pan);
    }

    // Method to submit the form
    public void submitForm() {
         // Wait until the submit button is clickable
         WebElement submitButtonElement = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
         // Use JavascriptExecutor to click the button
         JavascriptExecutor executor = (JavascriptExecutor) driver;
         executor.executeScript("arguments[0].click();", submitButtonElement);
    }

    // Method to get the alert text
    public String getAlertText() {
        // Wait until the alert is present
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        // Get the text from the alert
        String alertText = alert.getText();
        // Accept the alert
        alert.accept();
        // Return the alert text
        return alertText;
    }

    // Method to fill the entire employee form
    public void fillEmployeeForm(String firstName, String lastName, String designation, String department,
                                   String employeeType, String contact, String email, String dateOfJoining,
                                   String dob, String password, String nameAsPerBank, String bankName,
                                   String ifscCode, String bankAccountNumber, String panNumberBank) {
        // Enter all the details in the employee form
        enterFirstName(firstName);
        enterLastName(lastName);
        enterDesignation(designation);
        enterDepartment(department);
        selectEmployeeType(employeeType);
        enterContact(contact);
        enterEmail(email);
        enterDateOfJoining(dateOfJoining);
        enterDateOfBirth(dob);
        enterPassword(password);
        enterNameAsPerBank(nameAsPerBank);
        enterBankName(bankName);
        enterIFSCCode(ifscCode);
        enterBankAccountNumber(bankAccountNumber);
        enterPAN(panNumberBank);
    }


}