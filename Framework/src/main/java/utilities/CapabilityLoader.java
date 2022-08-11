package utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class CapabilityLoader
{
    private static final ThreadLocal<WebDriver> threadLocal = new ThreadLocal<WebDriver>();

    public static WebDriver getDriver() {
        return threadLocal.get();
    }

    public static void setDriver(WebDriver driver) {
        threadLocal.set(driver);
    }

    private static String browserNameEnv = "selenium.browserType";
    private static String runScopeEnv = "selenium.runLocation";
    private static String sauceLabsPlatformEnv = "sauceLabs.platform";
    private static String sauceLabsVersionEnv = "sauceLabs.version";
    private static String sauceLabsUsernameEnv = "sauceLabs.username";
    private static String sauceLabsAccessKeyEnv = "sauceLabs.accessKey";

    public static WebDriver createWebDriver() {
        if (PropertiesLoader.getProperties().getProperty(runScopeEnv).toLowerCase().equals("saucelabs")) {
            if (PropertiesLoader.getProperties().getProperty(browserNameEnv).toLowerCase().equals("chrome")) {

                DesiredCapabilities caps = DesiredCapabilities.chrome();
                caps.setCapability("platform", PropertiesLoader.getProperties().getProperty(sauceLabsPlatformEnv));
                caps.setCapability("version", PropertiesLoader.getProperties().getProperty(sauceLabsVersionEnv));
                caps.setCapability("extendedDebugging", true);

                WebDriver driver = null;
                try {
                    driver = new RemoteWebDriver(new URL("http://" + PropertiesLoader.getProperties().getProperty(sauceLabsUsernameEnv) + ":" + PropertiesLoader.getProperties().getProperty(sauceLabsAccessKeyEnv) + "@ondemand.saucelabs.com:80/wd/hub"), caps);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                return driver;
            } else {
                return null;
            }
        } else {
            if (PropertiesLoader.getProperties().getProperty(browserNameEnv).toLowerCase().equals("chrome")) {
                ChromeOptions options = new ChromeOptions();

                options.addArguments(new String[]{"--start-maximized", "disable-gpu", "--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080"});

                WebDriver driver = new ChromeDriver(options);

                return driver;
            } else {
                WebDriver driver = new FirefoxDriver();

                return driver;
            }
        }
    }
}
