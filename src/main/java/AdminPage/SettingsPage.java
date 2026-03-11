package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page object for Settings → Change Your Password.
 * Flow: Click Settings menu → fill current, new, confirm password → Change Password.
 */
public class SettingsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By settingsMenu = By.cssSelector("a[href='/admin/settings']");
    private final By currentPasswordInput = By.name("currentPassword");
    private final By newPasswordInput = By.name("newPassword");
    private final By confirmPasswordInput = By.name("confirmPassword");
    private final By changePasswordButton = By.xpath("//button[@type='submit' and contains(.,'Change Password')]");
    private static final By PASSWORD_INPUTS = By.cssSelector("input[type='password']");

    public SettingsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /** Click the Settings menu link in the sidebar. */
    public void clickSettingsMenu() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(settingsMenu));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        try {
            link.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
        wait.until(ExpectedConditions.urlContains("/admin/settings"));
    }

    public void enterCurrentPassword(String password) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(currentPasswordInput));
        el.clear();
        el.sendKeys(password);
    }

    public void enterNewPassword(String password) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(newPasswordInput));
        el.clear();
        el.sendKeys(password);
    }

    /** Enter confirm new password. Uses name="confirmPassword"; if not present, uses third password input. */
    public void enterConfirmPassword(String password) {
        List<WebElement> byName = driver.findElements(confirmPasswordInput);
        WebElement el;
        if (!byName.isEmpty() && byName.get(0).isDisplayed()) {
            el = byName.get(0);
        } else {
            List<WebElement> allPass = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PASSWORD_INPUTS));
            if (allPass.size() >= 3) {
                el = allPass.get(2);
            } else {
                el = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordInput));
            }
        }
        el.clear();
        el.sendKeys(password);
    }

    /** Click the Change Password submit button. */
    public void clickChangePassword() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(changePasswordButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
