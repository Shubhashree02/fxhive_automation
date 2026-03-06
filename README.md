# FxHive HRMS – Selenium Automation

Automated UI tests for the **FxHive HRMS** admin panel using Selenium WebDriver and TestNG. This project runs a **positive regression suite** on **Stage** (employee, salary, and related flows) and includes separate tests for the **login feature** (valid and invalid scenarios).

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
│   ├── StageSuiteSession.java   # Positive regression suite setup: open browser, base URL, login once (valid creds); shared driver & report
│   ├── LoginTest.java           # Login feature tests (valid, invalid username/password, blank) – run separately, not in main suite
│   ├── AddEmployeeTest.java
│   ├── AllEmployeeTest.java
│   └── AddSalaryFullTimeTest.java
├── src/test/resources/
│   └── testng.xml               # TestNG suite definition
└── test-output/                 # Generated reports (gitignored)
```

## Test Suites

### Positive regression suite (default: `mvn clean test`)

The main suite uses **StageSuiteSession** as setup. It does **not** test login; it only:

1. Opens the browser and navigates to the Stage base URL (`https://stage.fxhive.site/`).
2. Logs in **once** with valid credentials so all following tests run in the same authenticated session.

Then the suite runs Add Employee, Add Salary, and All Employee tests using that shared session. Report: `test-output/ExtentReport.html`.

### Login feature tests (`LoginTest`)

**LoginTest** verifies the login page behaviour. It is **not** included in the main suite. Run it separately (e.g. from the IDE) when you want to test login:

| Scenario | What it checks |
|----------|----------------|
| Valid login | Correct credentials → redirect to dashboard |
| Invalid username | Wrong username → error message (e.g. "Invalid credentials") |
| Invalid password | Wrong password → same error |
| Blank credentials | Empty fields → validation message |

Each test uses its own browser and does not share the session with StageSuiteSession.

## What Is Tested (by area)

| Area | Description |
|------|-------------|
| **Add Employee** | Add employee form and required fields (positive regression suite) |
| **All Employee** | Employee list, search, filters (positive regression suite) |
| **Add Salary** | Salary flows (e.g. Full Time) – see `testng.xml` for enabled tests |
| **Login** | Valid/invalid/blank login – run via `LoginTest` separately |

**Environment:** All tests run on **Stage** (`https://stage.fxhive.site/`). After a successful login, the app redirects to `https://stage.fxhive.site/admin/dashboard`.

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

From the IDE you can run a single test class (e.g. `Admin.LoginTest` for login scenarios, or `Admin.AddSalaryFullTimeTest`). To run only the positive regression suite (with shared login), use `mvn clean test` or run `testng.xml`.

## Test Reports

After a run, open the generated HTML report in a browser:

- **Positive regression suite:** `test-output/ExtentReport.html`
- **LoginTest (when run separately):** `test-output/LoginTestReport.html`

The `test-output/` directory is in `.gitignore` and is generated on each test run.

## Configuration & Credentials

Tests use default Stage credentials (e.g. `admin@fx31labs.com` / `Admin@123`). These are for automated testing only. For other environments or shared use, consider moving credentials to environment variables or a config file.

## Build

This is a **Maven** project. If `pom.xml` is not in the repo, add one with dependencies for:

- Selenium Java
- TestNG
- ExtentReports (AventStack)

Then run `mvn clean test` or import the project as a Maven project in your IDE.
