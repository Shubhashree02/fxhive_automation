package AdminPage;

import org.openqa.selenium.WebDriver;

/**
 * Page object for Salary Structure → Intern → Add New Salary.
 * Mirrors Generate Payroll POM: one page per type, base holds shared logic.
 */
public class AddSalaryIntern extends AddSalaryBase {

    public AddSalaryIntern(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTypeSegment() {
        return "intern";
    }
}
