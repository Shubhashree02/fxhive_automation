package Admin;

import org.testng.Assert;
import org.testng.annotations.Test;

import AdminPage.AllEmployeePage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;

public class AllEmployeeTest {
   private AllEmployeePage employeePage;
   private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testSearchEmployee() {
        StageSuiteSession.ensureOnDashboard();
        employeePage = new AllEmployeePage(driver);
        employeePage.openEmployeesPage();
        employeePage.searchEmployee("Shruti Mehta");
        // Add assertions to verify the search results
        Assert.assertTrue(driver.getPageSource().contains("Shruti Mehta"), "Employee not found");
    }

    @Test
    public void testFilterByEmployeeType() {
        StageSuiteSession.ensureOnDashboard();
        employeePage = new AllEmployeePage(driver);
        employeePage.openEmployeesPage();
        employeePage.selectEmployeeType("Full time");
        // Add assertions to verify the filter results
        Assert.assertTrue(driver.getPageSource().contains("Full time"), "Filter not applied correctly");
    }

    @Test
    public void testFilterByDepartment() {
        StageSuiteSession.ensureOnDashboard();
        employeePage = new AllEmployeePage(driver);
        employeePage.openEmployeesPage();
        employeePage.selectDepartment("HR");
        // Add assertions to verify the filter results
        Assert.assertTrue(driver.getPageSource().contains("HR"), "Filter not applied correctly");
    }

    @Test
    public void testFilterByStatus() {
        StageSuiteSession.ensureOnDashboard();
        employeePage = new AllEmployeePage(driver);
        employeePage.openEmployeesPage();
        employeePage.selectStatus("Active");
        // Add assertions to verify the filter results
        Assert.assertTrue(driver.getPageSource().contains("Active"), "Filter not applied correctly");
    }

    @Test
    public void testResetFilters() {
        StageSuiteSession.ensureOnDashboard();
        employeePage = new AllEmployeePage(driver);
        employeePage.openEmployeesPage();
        employeePage.selectEmployeeType("Intern");
        employeePage.selectDepartment("Tech");
        employeePage.selectStatus("Inactive");
        employeePage.resetFilters();
        // Add assertions to verify the filters are reset
        Assert.assertFalse(driver.getPageSource().contains("Intern"), "Filters not reset correctly");
        Assert.assertFalse(driver.getPageSource().contains("Tech"), "Filters not reset correctly");
        Assert.assertFalse(driver.getPageSource().contains("Inactive"), "Filters not reset correctly");
    }
}