package Admin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.InputStream;
import java.util.Properties;

/**
 * Loads test config from config.properties (or config.example.properties as fallback).
 * Keep config.properties out of Git and copy from config.example.properties with real values.
 */
public final class TestConfig {
    private static final Properties PROPS = load();

    private static Properties load() {
        Properties p = new Properties();
        try {
            InputStream in = TestConfig.class.getClassLoader().getResourceAsStream("config.properties");
            if (in == null) {
                in = TestConfig.class.getClassLoader().getResourceAsStream("config.example.properties");
            }
            if (in != null) {
                p.load(in);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties. Copy config.example.properties to config.properties and set credentials.", e);
        }
        return p;
    }

    public static String getBaseUrl() {
        return PROPS.getProperty("base.url", "https://stage.fxhive.site/");
    }

    public static String getDashboardUrl() {
        return PROPS.getProperty("dashboard.url", "https://stage.fxhive.site/admin/dashboard");
    }

    public static String getUsername() {
        return PROPS.getProperty("username");
    }

    public static String getPassword() {
        return PROPS.getProperty("password");
    }

    /** Browser name from config (e.g. chrome). Defaults to chrome. */
    public static String getBrowser() {
        String b = PROPS.getProperty("browser");
        return (b != null && !b.trim().isEmpty()) ? b.trim().toLowerCase() : "chrome";
    }

    /** Creates a WebDriver for the configured browser. Currently supports: chrome. */
    public static WebDriver createDriver() {
        if ("chrome".equals(getBrowser())) {
            return new ChromeDriver();
        }
        throw new IllegalArgumentException("Unsupported browser in config: " + getBrowser() + ". Use browser=chrome.");
    }

    private TestConfig() {}
}
