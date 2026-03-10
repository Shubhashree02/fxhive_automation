package AdminPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for Payroll → Contractual → Generate Payroll flow.
 * Mirrors Add Salary POM: one page per payroll type.
 */
public class GeneratePayrollContractual extends GeneratePayrollBase {

    public GeneratePayrollContractual(WebDriver driver) {
        super(driver);
    }
}
