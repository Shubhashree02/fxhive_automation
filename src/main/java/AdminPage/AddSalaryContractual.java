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
 * Page object for Add Salary - Contractual flow.
 * Flow: Salary Structure menu → Contractual → Add New Salary → enter data → Save.
 */
public class AddSalaryContractual {
    WebDriver driver;
    WebDriverWait wait;

    public AddSalaryContractual(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    // Page navigation locators
    private By salaryStructureMenu = By.xpath("//span[text()='Salary Structure']");
    // Contractual: <a class="flex items-center gap-3 ..." href="/admin/salary-structure?type=contractual"><svg>...</svg><span class="truncate">Contractual</span></a>
    private By contractualOption = By.cssSelector("a[href='/admin/salary-structure?type=contractual']");
    private By addNewSalaryBtn = By.cssSelector("button[title='Add New Salary']");

    // Modal locators
    private By employeeDropdownTrigger = By.id("employeeSelect");
    private By employeeDropdownOptions = By.cssSelector("[role='option']");
    private By grossInput = By.id("gross");
    private By saveButton = By.cssSelector("form button[type='submit']");

    public void clickSalaryStructureMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(salaryStructureMenu)).click();
    }

    public void clickContractualOption() {
        wait.until(ExpectedConditions.elementToBeClickable(contractualOption)).click();
    }

    public void clickAddNewSalaryBtn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addNewSalaryBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
    }

    public void selectEmployeeByIndex(int index) {
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(employeeDropdownTrigger));
        trigger.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(employeeDropdownOptions));
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(employeeDropdownOptions));
        if (index >= 0 && index < options.size()) {
            options.get(index).click();
        } else {
            throw new IllegalArgumentException("Employee option index " + index + " out of range (0-" + (options.size() - 1) + ")");
        }
    }

    public void enterGross(String grossAmount) {
        WebElement gross = wait.until(ExpectedConditions.visibilityOfElementLocated(grossInput));
        gross.clear();
        gross.sendKeys(grossAmount);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickSave() {
        try {
            WebElement saveButtonElement = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButtonElement);
            saveButtonElement.click();
            System.out.println("Clicked the Save button (Contractual).");
        } catch (Exception e) {
            System.out.println("Error clicking the Save button: " + e.getMessage());
            try {
                WebElement saveButtonElement = driver.findElement(saveButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButtonElement);
                System.out.println("Clicked the Save button with JavaScript.");
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click Save: " + ex.getMessage());
            }
        }
    }
}
