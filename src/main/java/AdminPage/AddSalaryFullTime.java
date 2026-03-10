package AdminPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for Salary Structure → Full Time → Add New Salary.
 * Mirrors Generate Payroll POM: one page per type, base holds shared logic.
 */
public class AddSalaryFullTime extends AddSalaryBase {

    public AddSalaryFullTime(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTypeSegment() {
        return "full-time";
    }
}
