package Admin;

import org.testng.annotations.Test;
import org.testng.Assert;
import com.aventstack.extentreports.Status;
import AdminPage.AddEmployeePage; // Import the AddEmployeePage class
//import org.testng.annotations.Priority;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import java.time.Duration;


public class AddEmployeeTest extends BaseTest {
    @Test(priority = 1)
    public void testAddEmployeePositiveCase() {
        com.aventstack.extentreports.ExtentTest addEmployeeTest = parentTest.createNode("Add Employee Test - Positive Case");
        AddEmployeePage addEmployeePage = new AddEmployeePage(driver);
        try {
            addEmployeeTest.log(Status.INFO, "Test starting: Positive case");
    
            // Navigate to Add Employee form
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement addEmployeeLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-content='addEmployee']")));
            addEmployeeLink.click();
            addEmployeePage.waitForAddEmployeeForm();
            addEmployeeTest.log(Status.INFO, "Navigated to Add Employee form");
    
            // Calculate dates
            LocalDate today = LocalDate.now();
            LocalDate dobDate = today.minusYears(14); // Exactly 14 years old
            LocalDate joiningDate = today.minusDays(30);
    
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
            String dob = dobDate.format(formatter);
            String dateOfJoining = joiningDate.format(formatter);
    
            // Generate a unique email
            String newEmail = "john.doe" + System.currentTimeMillis() + "@example.com";
    
            // Fill out the form with valid data
            addEmployeePage.fillEmployeeForm(
                "John", "Doe", "Software Engineer", "Engineering",
                "Full time", "9876543210", newEmail, dateOfJoining,
                dob, "Password123", "John Doe", "Example Bank",
                "EXMP0001234", "1234567890", "ABCDE1234F");
            addEmployeeTest.log(Status.INFO, "Filled employee form with valid data");
    
            // Submit the form
            addEmployeePage.submitForm();
    
            // Get the alert text
            String alertText = addEmployeePage.getAlertText();
            addEmployeeTest.log(Status.INFO, "Alert message: " + alertText);
    
            // Assert the alert message
            Assert.assertEquals(alertText, "Employee Created Successfully");
    
            addEmployeeTest.log(Status.PASS, "Employee added successfully with valid data");
    
        } catch (Exception e) {
            addEmployeeTest.log(Status.FAIL, "Failed to add employee: " + e.getMessage());
            throw new RuntimeException("Failed to add employee: " + e.getMessage());
        }
    }
    

    @Test(priority = 2)
    public void testAddEmployeeInvalidEmailFormat() {
        com.aventstack.extentreports.ExtentTest addEmployeeTest = parentTest.createNode("Add Employee Test - Invalid Email Format");
        AddEmployeePage addEmployeePage = new AddEmployeePage(driver);
        try {
            addEmployeeTest.log(Status.INFO, "Test starting: Invalid Email Format");

            // Navigate to Add Employee form
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement addEmployeeLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-content='addEmployee']")));
            addEmployeeLink.click();
            addEmployeePage.waitForAddEmployeeForm();
            addEmployeeTest.log(Status.INFO, "Navigated to Add Employee form");

            // Calculate dates
            LocalDate today = LocalDate.now();
            LocalDate dobDate = today.minusYears(14); // Exactly 14 years old
            LocalDate joiningDate = today.minusDays(30);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
            String dob = dobDate.format(formatter);
            String dateOfJoining = joiningDate.format(formatter);

            // Fill out the form with invalid email format
            addEmployeePage.fillEmployeeForm(
                "John", "Doe", "Software Engineer", "Engineering",
                "Full time", "9876543210", "mjjukik", dateOfJoining,
                dob, "Password123", "John Doe", "Example Bank",
                "EXMP0001234", "1234567890", "ABCDE1234F");
            addEmployeeTest.log(Status.INFO, "Filled employee form with invalid email format (missing @)");

            // Submit Form
            addEmployeePage.submitForm();

            // Wait for the email field to have a validation message
            WebDriverWait waitElement = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement emailField = waitElement.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            String validationMessage = emailField.getAttribute("validationMessage");
            addEmployeeTest.log(Status.INFO, "Validation Message: " + validationMessage);

           Assert.assertTrue(validationMessage.contains("Please include an '@' in the email address"));


        } catch (Exception e) {
            addEmployeeTest.log(Status.FAIL, "Failed to add employee: " + e.getMessage());
            throw new RuntimeException("Failed to add employee: " + e.getMessage());
        }
    }

    
}
