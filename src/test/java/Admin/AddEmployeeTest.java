package Admin;

import org.testng.annotations.Test;
import org.testng.Assert;
import com.aventstack.extentreports.Status;
import AdminPage.AddEmployeePage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import com.github.javafaker.Faker; // <-- Add this import

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

public class AddEmployeeTest extends BaseTest {
    @Test(priority = 1,invocationCount = 10)
    public void testAddEmployeePositiveCase() {

        System.out.println(">>> testAddEmployeePositiveCase() started");

        com.aventstack.extentreports.ExtentTest addEmployeeTest = parentTest
                .createNode("Add Employee Test - Positive Case");
        AddEmployeePage addEmployeePage = new AddEmployeePage(driver);
        try {
            addEmployeeTest.log(Status.INFO, "Test starting: Positive case");

            // Scroll down a bit before clicking the link
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 200);");
            Thread.sleep(400);

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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
            String dob = dobDate.format(formatter);
            String dateOfJoining = joiningDate.format(formatter);

            // Use Faker for realistic random names and unique email
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            long timestamp = System.currentTimeMillis();
            String fullName = firstName + " " + lastName;
            String newEmail = firstName.toLowerCase() + "." + lastName.toLowerCase() + timestamp + "@example.com";

            addEmployeePage.fillEmployeeForm(
                    firstName, lastName, "Automation Engineer", "IT",
                    "Contractual", "9876543210", newEmail, dateOfJoining,
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

            System.out.println(">>> Notification element HTML: " + successNotification.getAttribute("outerHTML"));
            System.out.println(">>> Actual notification message: '" + notificationMessage + "'");

            addEmployeeTest.log(Status.INFO, "Success notification message: " + notificationMessage);

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

            // Scroll down a bit before clicking the link
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 200);");
            Thread.sleep(400);

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

            // Use Faker for random names but intentionally provide an invalid email
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String invalidEmail = firstName.toLowerCase() + lastName.toLowerCase(); // No '@' intentionally
            String fullName = firstName + " " + lastName;

            addEmployeePage.fillEmployeeForm(
                    firstName, lastName, "Software Engineer", "Engineering",
                    "Full time", "9876543210", invalidEmail, dateOfJoining,
                    dob, "Password123", fullName, "Example Bank",
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
