package AdminPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for Salary Structure → Contractual → Add New Salary.
 * Mirrors Generate Payroll POM: one page per type, base holds shared logic.
 */
public class AddSalaryContractual extends AddSalaryBase {

    public AddSalaryContractual(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTypeSegment() {
        return "contractual";
    }
}
