package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for Company Profile.
 * Flow: Click Company Profile menu → fill company name, address, phone, description → Save company information.
 */
public class CompanyProfilePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Sidebar: Company Profile link (href="/admin/company")
    private final By companyProfileMenu = By.cssSelector("a[href='/admin/company']");

    // Company form fields
    private final By companyNameInput = By.id("companyName");
    private final By companyAddressInput = By.id("companyAddress");
    private final By companyPhoneInput = By.id("companyPhone");
    private final By companyDescriptionTextarea = By.id("companyDescription");
    private final By saveCompanyButton = By.xpath("//button[@type='submit' and contains(.,'Save')]");

    public CompanyProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /** Click the Company Profile menu link in the sidebar. */
    public void clickCompanyProfileMenu() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(companyProfileMenu));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        try {
            link.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
        wait.until(ExpectedConditions.urlContains("/admin/company"));
    }

    public void enterCompanyName(String name) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(companyNameInput));
        el.clear();
        el.sendKeys(name);
    }

    public void enterAddress(String address) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(companyAddressInput));
        el.clear();
        el.sendKeys(address);
    }

    public void enterPhone(String phone) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(companyPhoneInput));
        el.clear();
        el.sendKeys(phone);
    }

    public void enterDescription(String description) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(companyDescriptionTextarea));
        el.clear();
        el.sendKeys(description);
    }

    /** Click the Save company information button. */
    public void clickSaveCompanyInformation() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveCompanyButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
