package AdminPage;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


public class AddEmployeePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators for form elements
    // Stage: Employee sidebar link (navigates to /admin/employees)
    private By employeeSidebarLink = By.cssSelector("a[href='/admin/employees']");
    // Stage: Add Employee button on the employees page
    private By addEmployeeButton = By.xpath("//button[contains(.,'Add Employee')]");
    // Stage: name-based locators (stable; no dynamic ids)
    private By firstNameField = By.cssSelector("input[name='firstName']");
    private By lastNameField = By.cssSelector("input[name='lastName']");
    private By designationField = By.cssSelector("input[name='designation']");
    private By contactField = By.cssSelector("input[name='contact']");
    private By emailField = By.cssSelector("input[name='email']");
    private By dateOfJoiningField = By.cssSelector("input[name='dateOfJoining']");
    private By dobField = By.cssSelector("input[name='dob']");
    private By passwordField = By.cssSelector("input[name='password']");
    private By nameAsPerBankField = By.cssSelector("input[name='bankDetails.nameAsPerBank']");
    private By bankNameField = By.cssSelector("input[name='bankDetails.bankName']");
    private By ifscCodeField = By.cssSelector("input[name='bankDetails.ifscCode']");
    private By bankAccountNumberField = By.cssSelector("input[name='bankDetails.accountNumber']");
    private By panNumberBankField = By.cssSelector("input[name='bankDetails.panNumber']");
    // Stage: comboboxes by label (button that opens list)
    private By departmentCombobox = By.xpath("//div[contains(@class,'space-y-2')]//label[contains(.,'Department')]/following-sibling::button[@role='combobox']");
    private By employeeTypeCombobox = By.xpath("//div[contains(@class,'space-y-2')]//label[contains(.,'Employee Type')]/following-sibling::button[@role='combobox']");
    // Stage: Add Employee submit button (type=submit, text "Add Employee" in dialog form)
    private By submitButton = By.xpath("//div[@role='dialog']//form//button[@type='submit' and contains(.,'Add Employee')]");

    public AddEmployeePage(WebDriver driver) {
        // Constructor to initialize the WebDriver and WebDriverWait
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));

}
    /** Stage: Click Employee sidebar tab and wait for /admin/employees page. */
    public void openEmployeesPage() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(employeeSidebarLink));
        link.click();
        wait.until(ExpectedConditions.urlContains("/admin/employees"));
    }

    /** Stage: Click the Add Employee button on the employees page to open the form. */
    public void clickAddEmployeeButton() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addEmployeeButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // Action to click on Add Employee link (legacy — not used on Stage)
    public void clickAddEmployeeLink() {
        By addEmployeeLink = By.cssSelector("a[data-content='addEmployee']");
        WebElement addEmployeeLinkElement = wait.until(ExpectedConditions.elementToBeClickable(addEmployeeLink));
        addEmployeeLinkElement.click();
    }
    // Wait for the Add Employee form (Stage: dialog — first name field by name)
    public void waitForAddEmployeeForm() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField));
    }

    /** Returns the visible email field (for validation message etc.). */
    public WebElement getEmailField() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
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
// Method to enter the department (Stage: combobox — click button then option by text)
    public void enterDepartment(String department) {
        WebElement combobox = wait.until(ExpectedConditions.elementToBeClickable(departmentCombobox));
        combobox.click();
        By optionLocator = By.xpath("//*[@role='option' and normalize-space()='" + department + "']");
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
        option.click();
    }
// Method to select the employee type (Stage: combobox — click button then option by text)
    public void selectEmployeeType(String type) {
        WebElement combobox = wait.until(ExpectedConditions.elementToBeClickable(employeeTypeCombobox));
        combobox.click();
        By optionLocator = By.xpath("//*[@role='option' and normalize-space()='" + type + "']");
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
        option.click();
    }

    // Method to enter the contact number
    public void enterContact(String contact) {
        // Wait until the contact field is visible and then enter the contact number
        wait.until(ExpectedConditions.visibilityOfElementLocated(contactField)).sendKeys(contact);
    }

    // Method to enter the email (must contain '@' and valid format; UI shows "Please include an '@'..." when invalid)
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

    // Method to enter the date of birth (employee must be above 14 years old per validation)
    public void enterDateOfBirth(String dob) {
        // Wait until the date of birth field is visible
        WebElement dobElement = wait.until(ExpectedConditions.visibilityOfElementLocated(dobField));
        // Clear the field before entering the date
        dobElement.clear();
        // Enter the date of birth
        dobElement.sendKeys(dob);
    }

    // Method to enter the password (rule: 6+ chars, upper, lower, number, special char, no spaces)
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
    // Email must follow the rule: contain '@' and valid format (e.g. local@domain); otherwise validation will fail.
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