# Plan: StageSuiteSession for Positive Regression Suite

## Goal

**StageSuiteSession** is the setup for the **positive regression suite** only. It should do exactly:

1. **Open browser** – create one ChromeDriver, maximize, set timeouts.
2. **Open base URL** – navigate to Stage login page (e.g. `https://stage.fxhive.site/`).
3. **Login once with correct credentials** – perform a single valid login so the suite has an authenticated session. All tests that run after (Add Employee, Add Salary, All Employee) reuse this same session.

No other responsibilities. Login is **once**, with **valid** credentials only.

---

## Current State

- `StageSuiteSession` already does this in `@BeforeSuite`:
  - Creates ExtentReport and parent test.
  - Creates ChromeDriver, maximizes, sets implicit wait.
  - `driver.get(BASE_URL)`.
  - `loginValid()` – fills username/password (valid creds), clicks Login, waits for dashboard.
- So behavior is correct; we only need to make the **purpose and steps explicit**.

---

## Implementation (clarify only)

### 1. Document the role of StageSuiteSession

- Add a **class-level Javadoc** (or comment) stating:
  - This class is the setup for the **positive regression suite**.
  - It: (1) opens browser, (2) opens base URL, (3) logs in once with correct credentials. All subsequent tests share this session.
- Optionally rename the ExtentReport parent test to e.g. **"Positive Regression Suite (Stage)"** so the report title matches.

### 2. Keep beforeSuite() structure clear

- **Step 1 – Report:** Create ExtentReport and parent test (for suite reporting).
- **Step 2 – Browser:** Create ChromeDriver, maximize, set timeouts.
- **Step 3 – Base URL:** `driver.get(BASE_URL)` (Stage login page).
- **Step 4 – Login once:** Call `loginValid()` (or rename to e.g. `loginOnceWithValidCredentials()`) so the flow reads clearly.

No change to *what* runs; only separate the steps in code/comments so "open browser + base URL" and "login once with correct credential" are obvious.

### 3. Keep credentials in one place

- Continue using a single place for the valid Stage credentials (e.g. in `StageSuiteSession` only, or constants at top of the class). No hardcoding in multiple classes.

### 4. testng.xml

- Keep `StageSuiteSession` as the **first** class in the suite so its `@BeforeSuite` runs before any test class. No change needed if already ordered that way.

### 5. No change to helpers

- `getDriver()`, `createNode()`, `ensureOnDashboard()`, `@AfterSuite` stay as they are; they support the positive regression tests that run after the one-time login.

---

## Summary

| Responsibility | Where |
|----------------|--------|
| Open browser | `StageSuiteSession.beforeSuite()` – create ChromeDriver, maximize, timeouts |
| Open base URL | `StageSuiteSession.beforeSuite()` – `driver.get(BASE_URL)` |
| Login once with correct credentials | `StageSuiteSession.loginValid()` (or renamed method) – single valid login, wait for dashboard |
| Shared session for all tests | Same `driver` via `getDriver()`; tests run after `@BeforeSuite` |

After implementation, the class will clearly read as: **positive regression suite setup = open browser → open base URL → login once with valid credentials**; then all tests use that session.
