# FxHive HRMS – Selenium Automation

Automated UI tests for the **FxHive HRMS** admin panel using Selenium WebDriver and TestNG. This project covers login, employee management, and salary flows on the FxHive HRMS platform (dev/stage).

## Tech Stack

- **Java** (Maven project)
- **Selenium WebDriver** (Chrome)
- **TestNG** – test runner and suite configuration
- **ExtentReports** – HTML test reports
- **Page Object Model** – page classes under `AdminPage`, tests under `Admin`

## Prerequisites

- **JDK 8+** (or 11+)
- **Maven** – for building and running tests
- **Chrome** browser
- **ChromeDriver** – must match your Chrome version (on `PATH` or set `webdriver.chrome.driver`)

## Project Structure

```
fxhive_selenium/
├── src/main/java/AdminPage/     # Page objects
│   ├── AddEmployeePage.java
│   ├── AllEmployeePage.java
│   └── AddSalaryFullTime.java
├── src/main/resources/          # Static resources (e.g. js/)
├── src/test/java/Admin/         # Test classes
│   ├── BaseTest.java            # Shared driver, login, ExtentReports
│   ├── LoginTest.java
│   ├── AddEmployeeTest.java
│   ├── AllEmployeeTest.java
│   └── AddSalaryFullTimeTest.java
├── src/test/resources/
│   └── testng.xml               # TestNG suite definition
└── test-output/                 # Generated reports (gitignored)
```

## What Is Tested

| Area | Description |
|------|-------------|
| **Login** | Valid login, invalid username/password, blank credentials |
| **Add Employee** | Add employee form and required fields |
| **All Employee** | Employee list / listing page |
| **Add Salary** | Salary flows (e.g. Full Time, Intern) – see `testng.xml` for enabled tests |

**Environments:** `BaseTest` uses **dev** (`http://dev.fxhive.site/`). `LoginTest` uses **stage** (`https://stage.fxhive.site/`).

## How to Run Tests

### Using Maven

Ensure you have a `pom.xml` with Selenium, TestNG, and ExtentReports dependencies, then:

```bash
mvn clean test
```

### Using TestNG

Run the TestNG suite from your IDE or CLI:

- **Suite file:** `src/test/resources/testng.xml`
- Enable or disable test classes by uncommenting/commenting the `<class>` entries in `testng.xml`.

### Single Test Class

From IDE: run a single test class (e.g. `Admin.LoginTest` or `Admin.AddSalaryFullTimeTest`).

## Test Reports

After a run, open the generated HTML report in a browser:

- **Main suite:** `test-output/ExtentReport.html`
- **Login-only suite:** `test-output/LoginTestReport.html` (when running `LoginTest`)

The `test-output/` directory is in `.gitignore` and is generated on each test run.

## Configuration & Credentials

Tests use default credentials for dev/stage (e.g. `admin@fx31labs.com` / `admin@123`). These are for automated testing only. For other environments or shared use, consider moving credentials to environment variables or a config file.

## Build

This is a **Maven** project. If `pom.xml` is not in the repo, add one with dependencies for:

- Selenium Java
- TestNG
- ExtentReports (AventStack)

Then run `mvn clean test` or import the project as a Maven project in your IDE.
