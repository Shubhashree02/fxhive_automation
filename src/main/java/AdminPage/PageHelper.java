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
 * Small helper for common Selenium patterns used across Admin page objects.
 */
public final class PageHelper {

    private PageHelper() {
        // utility
    }

    /**
     * Scrolls the element into view and clicks it, falling back to JavaScript click when needed.
     */
    public static void scrollAndClick(WebDriver driver, WebDriverWait wait, WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Clicks a combobox trigger and selects an option whose trimmed text equals optionText.
     */
    public static void selectComboboxOptionByText(WebDriver driver,
                                                  WebDriverWait wait,
                                                  By triggerLocator,
                                                  By optionsLocator,
                                                  String optionText) {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(triggerLocator));
        trigger.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionsLocator));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator));
        for (WebElement opt : options) {
            if (opt.getText().trim().equals(optionText)) {
                opt.click();
                break;
            }
        }
    }

    /**
     * Builds a WebDriverWait with the shared page timeout.
     */
    public static WebDriverWait newPageWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(PageConstants.PAGE_WAIT_SECONDS));
    }

    /**
     * Builds a WebDriverWait with the shared short timeout.
     */
    public static WebDriverWait newShortWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(PageConstants.SHORT_WAIT_SECONDS));
    }
}

