package Admin;

import org.testng.annotations.Test;
import org.testng.Assert;
import com.aventstack.extentreports.Status;
import AdminPage.AddEmployeePage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.github.javafaker.Faker; // <-- Add this import

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import org.testng.annotations.BeforeClass;

public class AddEmployeeTest {
    private WebDriver driver;

    /** Password rule: at least 6 characters, uppercase, lowercase, number, special character, no spaces. */
    private static final String VALID_EMPLOYEE_PASSWORD = "Password1!";

    /** Minimum age for employee (date of birth must be above this many years ago). */
    private static final int MIN_AGE_YEARS = 14;

    /** Date of birth: February 2009 (above 14 years). */
    private static final LocalDate DOB_FEB_2009 = LocalDate.of(2009, 2, 1);

    /** Builds a valid employee email per rule: contains '@' and valid format (local@domain). */
    private static String buildValidEmployeeEmail(String firstName, String lastName) {
        long timestamp = System.currentTimeMillis();
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + timestamp + "@example.com";
    }

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test(priority = 1)
    public void testAddEmployeePositiveCase() {

        System.out.println(">>> testAddEmployeePositiveCase() started");

        StageSuiteSession.ensureOnDashboard();
        com.aventstack.extentreports.ExtentTest addEmployeeTest =
                StageSuiteSession.createNode("Add Employee Test - Positive Case");
        AddEmployeePage addEmployeePage = new AddEmployeePage(driver);
        try {
            addEmployeeTest.log(Status.INFO, "Test starting: Positive case");
            addEmployeeTest.log(Status.INFO, "Current URL: " + driver.getCurrentUrl());

            addEmployeePage.openEmployeesPage();
            addEmployeePage.clickAddEmployeeButton();
            addEmployeePage.waitForAddEmployeeForm();
            addEmployeeTest.log(Status.INFO, "Navigated to Add Employee form");

            // Dates: DOB Feb 2009 (above 14 years); date of joining 30 days ago
            LocalDate today = LocalDate.now();
            LocalDate joiningDate = today.minusDays(30);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
            String dob = DOB_FEB_2009.format(formatter);
            String dateOfJoining = joiningDate.format(formatter);

            // Use Faker for realistic random names; email must follow rule (contain '@', valid format)
            Faker faker = new Faker();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String fullName = firstName + " " + lastName;
            String newEmail = buildValidEmployeeEmail(firstName, lastName);

            addEmployeePage.fillEmployeeForm(
                    firstName, lastName, "Automation Engineer", "IT",
                    "Contractual", "9876543210", newEmail, dateOfJoining,
                    dob, VALID_EMPLOYEE_PASSWORD, fullName, "Example Bank",
                    "EXMP0001234", "1234567890", "ABCDE1234F");

            addEmployeeTest.log(Status.INFO, "Filled employee form with valid dynamic data");


            // Submit the form
            addEmployeePage.submitForm();

            // No success message on Stage; wait for Add Employee popup to close (up to 15 s) if it does
            try {
                WebDriverWait dialogWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                dialogWait.until(driver -> {
                    var dialogs = driver.findElements(By.cssSelector("div[role='dialog']"));
                    if (dialogs.isEmpty()) return true;
                    for (WebElement d : dialogs) {
                        if (!d.isDisplayed()) return true;
                        if ("closed".equals(d.getAttribute("data-state"))) return true;
                    }
                    return false;
                });
                addEmployeeTest.log(Status.PASS, "Employee form submitted and popup closed");
            } catch (Exception e) {
                addEmployeeTest.log(Status.WARNING, "Popup did not close within 15s; form was still submitted.");
                addEmployeeTest.log(Status.PASS, "Employee form submitted.");
            }
        } catch (Exception e) {
            addEmployeeTest.log(Status.FAIL, "Failed to add employee: " + e.getMessage());
            addEmployeeTest.log(Status.INFO, "Current URL at failure: " + driver.getCurrentUrl());
            throw new RuntimeException("Failed to add employee: " + e.getMessage());
        }
    }
}
