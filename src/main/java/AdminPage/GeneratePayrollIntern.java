package AdminPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for Payroll → Intern → Generate Payroll flow.
 * Mirrors Add Salary POM: one page per payroll type.
 */
public class GeneratePayrollIntern extends GeneratePayrollBase {

    public GeneratePayrollIntern(WebDriver driver) {
        super(driver);
    }
}
