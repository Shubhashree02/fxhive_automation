package Admin;

import org.testng.annotations.Test;
import org.testng.Assert;
import com.aventstack.extentreports.Status;
import AdminPage.AddEmployeePage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import java.time.Duration;

public class AddEmployeeTest extends BaseTest {
        @Test(priority = 1)
    public void testAddEmployeePositiveCase() {
        // Debug: Method start print statement
        System.out.println(">>> testAddEmployeePositiveCase() started");

        com.aventstack.extentreports.ExtentTest addEmployeeTest = parentTest
                .createNode("Add Employee Test - Positive Case");
        AddEmployeePage addEmployeePage = new AddEmployeePage(driver);
        try {
            addEmployeeTest.log(Status.INFO, "Test starting: Positive case");

            // Navigate to Add Employee form
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement addEmployeeLink = wait
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-content='addEmployee']")));
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

            // ✅ Generate a unique first name and last name
            String firstName = "John" + System.currentTimeMillis(); // dynamic first name
            String lastName = "Doe" + new Random().nextInt(1000);   // dynamic last name
            String fullName = firstName + " " + lastName;           // used for bank account name

            // Generate a unique email
            String newEmail = "john.doe" + System.currentTimeMillis() + "@example.com";

            // Fill out the form with valid dynamic data
            addEmployeePage.fillEmployeeForm(
                    firstName, lastName, "Software Engineer", "Engineering",
                    "Full time", "9876543210", newEmail, dateOfJoining,
                    dob, "Password123", fullName, "Example Bank",
                    "EXMP0001234", "1234567890", "ABCDE1234F");

            addEmployeeTest.log(Status.INFO, "Filled employee form with valid dynamic data");

            // Submit the form
            addEmployeePage.submitForm();

            // Enhanced debug: Wait for notification element to have non-empty text
            WebDriverWait notificationWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            boolean messageAppeared = notificationWait.until(d -> {
                WebElement el = d.findElement(By.id("notification-container"));
                return el.isDisplayed() && !el.getText().trim().isEmpty();
            });

            WebElement successNotification = driver.findElement(By.id("notification-container"));
            String notificationMessage = successNotification.getText();

            // Debug: Print the notification element's HTML and the actual message
            System.out.println(">>> Notification element HTML: " + successNotification.getAttribute("outerHTML"));
            System.out.println(">>> Actual notification message: '" + notificationMessage + "'");

            // Optional: Print the page source (uncomment if needed)
            // System.out.println(">>> Page source: " + driver.getPageSource());

            addEmployeeTest.log(Status.INFO, "Success notification message: " + notificationMessage);

            // Assert that the success message is correct (robust version)
            Assert.assertTrue(
                notificationMessage != null && notificationMessage.toLowerCase().contains("employee created"),
                "Expected success message not found. Actual: '" + notificationMessage + "'"
            );

            addEmployeeTest.log(Status.PASS, "Employee added successfully with valid data");
        } catch (Exception e) {
            addEmployeeTest.log(Status.FAIL, "Failed to add employee: " + e.getMessage());
            throw new RuntimeException("Failed to add employee: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void testAddEmployeeInvalidEmailFormat() {
        com.aventstack.extentreports.ExtentTest addEmployeeTest = parentTest
                .createNode("Add Employee Test - Invalid Email Format");
        AddEmployeePage addEmployeePage = new AddEmployeePage(driver);
        try {
            addEmployeeTest.log(Status.INFO, "Test starting: Invalid Email Format");

            // Navigate to Add Employee form
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement addEmployeeLink = wait
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-content='addEmployee']")));
            addEmployeeLink.click();
            addEmployeePage.waitForAddEmployeeForm();
            addEmployeeTest.log(Status.INFO, "Navigated to Add Employee form");

            // Calculate dates
            LocalDate today = LocalDate.now();
            LocalDate dobDate = today.minusYears(14);
            LocalDate joiningDate = today.minusDays(30);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
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
            WebElement emailField = waitElement
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            String validationMessage = emailField.getAttribute("validationMessage");

            addEmployeeTest.log(Status.INFO, "Validation Message: " + validationMessage);

            Assert.assertTrue(validationMessage.contains("Please include an '@' in the email address"));

        } catch (Exception e) {
            addEmployeeTest.log(Status.FAIL, "Failed to add employee: " + e.getMessage());
            throw new RuntimeException("Failed to add employee: " + e.getMessage());
        }
    }
}
