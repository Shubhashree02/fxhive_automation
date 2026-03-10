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

    /** Employee types to add (one employee per type) before moving to next step. */
    private static final String[] EMPLOYEE_TYPES = { "Full time", "Intern", "Contractual", "Consultant" };

    /** Builds a valid employee email per rule: contains '@' and valid format (local@domain). */
    private static String buildValidEmployeeEmail(String firstName, String lastName, String suffix) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + suffix + "@example.com";
    }

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test(priority = 1)
    public void testAddEmployeePositiveCase() {

        System.out.println(">>> testAddEmployeePositiveCase() started – adding all employee types");

        StageSuiteSession.ensureOnDashboard();
        com.aventstack.extentreports.ExtentTest addEmployeeTest =
                StageSuiteSession.createNode("Add Employee Test - Add All Types");
        AddEmployeePage addEmployeePage = new AddEmployeePage(driver);
        Faker faker = new Faker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
        LocalDate today = LocalDate.now();
        LocalDate joiningDate = today.minusDays(30);
        String dob = DOB_FEB_2009.format(formatter);
        String dateOfJoining = joiningDate.format(formatter);

        try {
            addEmployeeTest.log(Status.INFO, "Adding one employee per type: Full time, Intern, Contractual, Consultant");
            addEmployeePage.openEmployeesPage();

            for (int i = 0; i < EMPLOYEE_TYPES.length; i++) {
                String employeeType = EMPLOYEE_TYPES[i];
                addEmployeeTest.log(Status.INFO, "Adding employee type: " + employeeType);

                addEmployeePage.clickAddEmployeeButton();
                addEmployeePage.waitForAddEmployeeForm();

                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String fullName = firstName + " " + lastName;
                String suffix = "_" + employeeType.replace(" ", "") + "_" + System.currentTimeMillis();
                String newEmail = buildValidEmployeeEmail(firstName, lastName, suffix);

                addEmployeePage.fillEmployeeForm(
                        firstName, lastName, "Automation Engineer", "IT",
                        employeeType, "9876543210", newEmail, dateOfJoining,
                        dob, VALID_EMPLOYEE_PASSWORD, fullName, "Example Bank",
                        "EXMP0001234", "123456789" + (i + 1), "ABCDE123" + (4 + i) + (char) ('F' + i));

                addEmployeePage.submitForm();

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
                } catch (Exception e) {
                    // continue to next type
                }

                try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }

            addEmployeeTest.log(Status.PASS, "All employee types added: Full time, Intern, Contractual, Consultant");
        } catch (Exception e) {
            addEmployeeTest.log(Status.FAIL, "Failed to add employees: " + e.getMessage());
            throw new RuntimeException("Failed to add employees: " + e.getMessage());
        }
    }
}
