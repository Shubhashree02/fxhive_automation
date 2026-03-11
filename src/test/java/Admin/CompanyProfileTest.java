package Admin;

import AdminPage.CompanyProfilePage;
import com.github.javafaker.Faker;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test: Click Company Profile menu, update company name, address, phone, description, and save.
 */
public class CompanyProfileTest {
    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testUpdateCompanyProfile() {
        StageSuiteSession.ensureOnDashboard();
        CompanyProfilePage page = new CompanyProfilePage(driver);
        page.clickCompanyProfileMenu();

        Faker faker = new Faker();
        page.enterCompanyName(faker.company().name());
        page.enterAddress(faker.address().fullAddress());
        page.enterPhone(faker.phoneNumber().phoneNumber());
        page.enterDescription(faker.company().catchPhrase());

        page.clickSaveCompanyInformation();
    }
}
