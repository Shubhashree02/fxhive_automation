package AdminPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for Payroll → Full Time → Generate Payroll flow.
 * Mirrors Add Salary POM: one page per payroll type.
 */
public class GeneratePayrollFullTime extends GeneratePayrollBase {

    public GeneratePayrollFullTime(WebDriver driver) {
        super(driver);
    }
}
