package Admin;

import AdminPage.SettingsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Positive regression test: Settings → Change Your Password.
 * Changes to a new random password once, stores it, and updates config.properties so the next run logs in with it.
 */
public class SettingsTest {
    private static final Random RANDOM = new Random();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";

    private WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void init() {
        driver = StageSuiteSession.getDriver();
    }

    @Test
    public void testChangePasswordPositive() throws Exception {
        StageSuiteSession.ensureOnDashboard();
        SettingsPage page = new SettingsPage(driver);
        page.clickSettingsMenu();

        String currentPassword = TestConfig.getPassword();
        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new IllegalStateException("config.properties must set password (e.g. Admin@123) for Settings test.");
        }

        String newPassword = generateNewPassword();
        System.out.println("Settings test: new password (will update config) = " + newPassword);

        page.enterCurrentPassword(currentPassword);
        page.enterNewPassword(newPassword);
        page.enterConfirmPassword(newPassword);
        page.clickChangePassword();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/admin/settings"));

        updateConfigPassword(newPassword);
        System.out.println("Settings test: config.properties updated with new password.");
    }

    /** Generates a password that meets rules: 6+ chars, 1 upper, 1 lower, 1 number, 1 special, no spaces. */
    private static String generateNewPassword() {
        StringBuilder sb = new StringBuilder();
        sb.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        sb.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        sb.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));
        String all = UPPER + LOWER + DIGITS + SPECIAL;
        for (int i = 0; i < 4 + RANDOM.nextInt(5); i++) {
            sb.append(all.charAt(RANDOM.nextInt(all.length())));
        }
        return shuffle(sb.toString());
    }

    private static String shuffle(String s) {
        char[] a = s.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
        return new String(a);
    }

    /** Writes the new password into config.properties (password= line) so the next run uses it. */
    private static void updateConfigPassword(String newPassword) throws Exception {
        Path path = Paths.get("src/test/resources/config.properties");
        if (!Files.exists(path)) {
            path = Paths.get("target/test-classes/config.properties");
        }
        List<String> lines = Files.readAllLines(path);
        List<String> updated = lines.stream()
                .map(line -> line.trim().startsWith("password=") ? "password=" + newPassword : line)
                .collect(Collectors.toList());
        Files.write(path, updated);
    }
}
