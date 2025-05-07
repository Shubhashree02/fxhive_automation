package Admin;

import org.testng.Assert;
import org.testng.annotations.Test;

import AdminPage.AllEmployeePage;

public class AllEmployeeTest extends BaseTest {
   private AllEmployeePage employeePage;

    @Test
    public void testSearchEmployee() {
        employeePage = new AllEmployeePage(driver);
        employeePage.searchEmployee("Shruti Mehta");
        // Add assertions to verify the search results
        Assert.assertTrue(driver.getPageSource().contains("Shruti Mehta"), "Employee not found");
    }

    @Test
    public void testFilterByEmployeeType() {
        employeePage = new AllEmployeePage(driver);
        employeePage.selectEmployeeType("Full time");
        // Add assertions to verify the filter results
        Assert.assertTrue(driver.getPageSource().contains("Full time"), "Filter not applied correctly");
    }

    @Test
    public void testFilterByDepartment() {
        employeePage = new AllEmployeePage(driver);
        employeePage.selectDepartment("HR");
        // Add assertions to verify the filter results
        Assert.assertTrue(driver.getPageSource().contains("HR"), "Filter not applied correctly");
    }

    @Test
    public void testFilterByStatus() {
        employeePage = new AllEmployeePage(driver);
        employeePage.selectStatus("Active");
        // Add assertions to verify the filter results
        Assert.assertTrue(driver.getPageSource().contains("Active"), "Filter not applied correctly");
    }

    @Test
    public void testResetFilters() {
        employeePage = new AllEmployeePage(driver);
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