package AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AllEmployeePage {
    private WebDriver driver;

    // Locators
    private By employeeSearchInput = By.id("employeeSearchInput");
    private By searchButton = By.id("searchButton");
    private By employeeTypeFilter = By.id("employeeTypeFilter");
    private By departmentFilter = By.id("departmentFilter");
    private By statusFilter = By.id("statusFilter");
    private By resetFiltersBtn = By.id("resetFiltersBtn");

    public AllEmployeePage(WebDriver driver) {
        this.driver = driver;
    }

    // Methods to interact with the elements
    public void searchEmployee(String keyword) {
        WebElement searchInput = driver.findElement(employeeSearchInput);
        searchInput.clear();
        searchInput.sendKeys(keyword);
        driver.findElement(searchButton).click();
    }

    public void selectEmployeeType(String type) {
        WebElement typeFilter = driver.findElement(employeeTypeFilter);
        typeFilter.sendKeys(type);
    }

    public void selectDepartment(String department) {
        WebElement deptFilter = driver.findElement(departmentFilter);
        deptFilter.sendKeys(department);
    }

    public void selectStatus(String statusValue) {
        WebElement statusFilterElement = driver.findElement(statusFilter);
        statusFilterElement.sendKeys(statusValue);
    }

    public void resetFilters() {
        driver.findElement(resetFiltersBtn).click();
    }
}
