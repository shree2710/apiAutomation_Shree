package ui.driver;

import exceptions.DriverException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import utils.ConfigReader;

/**
 * UI core utility - the counterpart of {@link services.BaseService} for the
 * browser side.
 *
 * Creates and tracks one {@link WebDriver} per thread (so suites can run in
 * parallel), driven entirely by config: {@code ui.browser} and
 * {@code ui.headless}. Selenium 4's built-in Selenium Manager downloads the
 * matching driver binary, so no WebDriverManager dependency is needed.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
        // utility class - no instances
    }

    /** Creates a driver for the configured browser and binds it to this thread. */
    public static WebDriver init() {
        BrowserType browser = BrowserType.from(ConfigReader.get("ui.browser", "chrome"));
        boolean headless = Boolean.parseBoolean(ConfigReader.get("ui.headless", "true"));

        WebDriver driver = create(browser, headless);
        driver.manage().window().maximize();
        DRIVER.set(driver);
        return driver;
    }

    private static WebDriver create(BrowserType browser, boolean headless) {
        switch (browser) {
            case CHROME -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox");
                }
                return new ChromeDriver(options);
            }
            case FIREFOX -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                return new FirefoxDriver(options);
            }
            case EDGE -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new", "--window-size=1920,1080");
                }
                return new EdgeDriver(options);
            }
            default -> throw new DriverException("Unsupported browser: " + browser);
        }
    }

    /** Returns the driver for this thread, or fails fast if {@link #init()} was not called. */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new DriverException("Driver not initialized for this thread; call DriverFactory.init() first");
        }
        return driver;
    }

    /** Quits the driver and clears the thread binding. Safe to call when none exists. */
    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
