package AdminPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for Payroll → Consultant → Generate Payroll flow.
 * Mirrors Add Salary POM: one page per payroll type.
 */
public class GeneratePayrollConsultant extends GeneratePayrollBase {

    public GeneratePayrollConsultant(WebDriver driver) {
        super(driver);
    }
}
