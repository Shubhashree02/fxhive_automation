package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AllEmployeePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private By employeeSidebarLink = By.cssSelector("a[href='/admin/employees']");
    private By employeeSearchInput = By.id("employeeSearchInput");
    private By searchButton = By.id("searchButton");
    private By employeeTypeFilter = By.id("employeeTypeFilter");
    private By departmentFilter = By.id("departmentFilter");
    private By statusFilter = By.id("statusFilter");
    private By resetFiltersBtn = By.id("resetFiltersBtn");

    public AllEmployeePage(WebDriver driver) {
        this.driver = driver;
        this.wait = PageHelper.newPageWait(driver);
    }

    /** Navigate to the Employees list page (required before search/filter). */
    public void openEmployeesPage() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(employeeSidebarLink));
        link.click();
        wait.until(ExpectedConditions.urlContains("/admin/employees"));
    }

    // Methods to interact with the elements
    public void searchEmployee(String keyword) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeSearchInput));
        searchInput.clear();
        searchInput.sendKeys(keyword);
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        PageHelper.scrollAndClick(driver, wait, btn);
    }

    public void selectEmployeeType(String type) {
        WebElement typeFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeTypeFilter));
        typeFilter.sendKeys(type);
    }

    public void selectDepartment(String department) {
        WebElement deptFilter = wait.until(ExpectedConditions.visibilityOfElementLocated(departmentFilter));
        deptFilter.sendKeys(department);
    }

    public void selectStatus(String statusValue) {
        WebElement statusFilterElement = wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilter));
        statusFilterElement.sendKeys(statusValue);
    }

    public void resetFilters() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(resetFiltersBtn));
        PageHelper.scrollAndClick(driver, wait, btn);
    }
}
