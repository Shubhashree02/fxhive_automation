package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Page object for Employee Document flow.
 * Flow: Click Employee Document menu → Select employee → Upload Document → (modal) select document type, document name, file → Upload.
 */
public class EmployeeDocumentPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Sidebar: Employee Document link
    private final By employeeDocumentMenu = By.cssSelector("a[href='/admin/documents']");
    // Select employee combobox (id="employee-document-select")
    private final By employeeSelectTrigger = By.id("employee-document-select");
    private final By optionRole = By.cssSelector("[role='option']");
    // Upload Document button (opens modal; enabled after employee selected). Prefer main-page button (not inside dialog).
    private final By uploadDocumentButton = By.xpath("//button[contains(.,'Upload Document') and not(ancestor::div[@role='dialog'])]");

    // Upload modal: document type combobox, document name, file input, submit
    private final By modalComboboxes = By.cssSelector("div[role='dialog'] button[role='combobox']");
    private final By documentNameInput = By.name("documentName");
    private final By fileInput = By.cssSelector("div[role='dialog'] input[type='file']");
    // Modal submit: <button type="submit">Upload</button>
    private final By uploadSubmitButton = By.xpath("//div[@role='dialog']//button[@type='submit' and contains(.,'Upload')]");

    public EmployeeDocumentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickEmployeeDocumentMenu() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(employeeDocumentMenu));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        link.click();
        wait.until(ExpectedConditions.urlContains("/admin/documents"));
    }

    /** Select employee by option index (0-based). */
    public void selectEmployeeByIndex(int index) {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(employeeSelectTrigger));
        trigger.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionRole));
        List<WebElement> options = driver.findElements(optionRole);
        if (index >= 0 && index < options.size()) {
            clickOption(options.get(index));
        } else {
            throw new IllegalArgumentException("Employee option index " + index + " out of range (0-" + (options.size() - 1) + ")");
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Select a random employee from the dropdown. */
    public void selectRandomEmployee() {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(employeeSelectTrigger));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", trigger);
        try {
            trigger.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", trigger);
        }
    /**
     * Select a random employee from the dropdown (different index each run).
     * Uses index in range [0, optionCount - 1]. If only one option exists, selects it.
     */
    public void selectRandomEmployee() {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(employeeSelectTrigger));
        trigger.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionRole));
        List<WebElement> options = driver.findElements(optionRole);
        if (options.isEmpty()) {
            throw new IllegalStateException("No employee options in dropdown");
        }
        int index = new Random().nextInt(options.size());
        WebElement option = options.get(index);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
        try {
            option.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
        }
        int count = options.size();
        int index = count == 1 ? 0 : ThreadLocalRandom.current().nextInt(count);
        clickOption(options.get(index));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Click the main "Upload Document" button (opens modal). Waits for button to become enabled after employee selection. */
    private void clickOption(WebElement option) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", option);
        try {
            option.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
        }
    }

    /**
     * Click the main "Upload Document" button (opens modal). Waits for button to become enabled after employee selection. */
    public void clickUploadDocumentButton() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(45));
        WebElement btn = longWait.until(ExpectedConditions.elementToBeClickable(uploadDocumentButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(documentNameInput));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Select document type from combobox in modal by option text (e.g. "PAN Card"). */
    public void selectDocumentType(String documentTypeText) {
        List<WebElement> comboboxes = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(modalComboboxes));
        if (comboboxes.isEmpty()) return;
        WebElement first = comboboxes.get(0);
        first.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionRole));
        List<WebElement> options = driver.findElements(optionRole);
        for (WebElement opt : options) {
            if (documentTypeText.equals(opt.getText().trim())) {
                opt.click();
                break;
            }
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void enterDocumentName(String name) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(documentNameInput));
        input.clear();
        input.sendKeys(name);
    }

    /** Send file path to the file input (accepts only .pdf). */
    public void uploadFile(String absoluteFilePath) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(fileInput));
        input.sendKeys(absoluteFilePath);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Click the Upload button in the modal to submit. */
    public void clickUploadSubmit() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(uploadSubmitButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
    }
}
