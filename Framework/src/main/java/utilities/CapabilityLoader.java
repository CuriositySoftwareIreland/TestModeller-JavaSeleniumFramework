package utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import utilities.reports.ExtentReportManager;

import java.net.MalformedURLException;
import java.net.URL;

public class CapabilityLoader
{
    private static final ThreadLocal<WebDriver> threadLocal = new ThreadLocal<WebDriver>();

    private static final ThreadLocal<DesiredCapabilities> capabilitiesThreadLocal = new ThreadLocal<DesiredCapabilities>();

    public static WebDriver getDriver() {
        return threadLocal.get();
    }

    public static void setDriver(WebDriver driver) {
        threadLocal.set(driver);
    }

    private static String browserNameEnv = "selenium.browserType";

    public static DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = capabilitiesThreadLocal.get();

        if (desiredCapabilities == null) {
            desiredCapabilities = new DesiredCapabilities();

            capabilitiesThreadLocal.set(desiredCapabilities);
        }

        return desiredCapabilities;
    }

    public static void addCapability(String capabilityName, String value)
    {
        getDesiredCapabilities().setCapability(capabilityName, value);
    }

    public static String getCapability(String capabilityName)
    {
        return getDesiredCapabilities().getCapability(capabilityName).toString();
    }

    public static WebDriver createSauceLabsDriver(String username, String accesskey, String region)
    {
        String sauceUrl;
        if (region.equalsIgnoreCase("eu")) {
            sauceUrl = "@ondemand.eu-central-1.saucelabs.com:443";
        } else {
            sauceUrl = "@ondemand.us-west-1.saucelabs.com:443";
        }

        String SAUCE_REMOTE_URL = "https://" + username + ":" + accesskey + sauceUrl +"/wd/hub";

        try {
            return new RemoteWebDriver(new URL(SAUCE_REMOTE_URL), getDesiredCapabilities());
        } catch (MalformedURLException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static WebDriver createFirefoxDriver()
    {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.merge(getDesiredCapabilities());

        WebDriver driver = new FirefoxDriver(firefoxOptions);

        return driver;
    }

    public static WebDriver createChromeDriver()
    {
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addArguments(new String[]{"--start-maximized", "disable-gpu", "--ignore-certificate-errors", "--ignore-ssl-errors", "--allow-running-insecure-content", "--disable-web-security",  "--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080"});
        options.merge(getDesiredCapabilities());

        WebDriver driver = new ChromeDriver(options);

        return driver;
    }

    public static WebDriver createWebDriver()
    {
        if (PropertiesLoader.getProperties().getProperty(browserNameEnv).toLowerCase().equals("chrome")) {

            ChromeOptions options = new ChromeOptions();
            options.setAcceptInsecureCerts(true);
            options.addArguments(new String[]{"--start-maximized", "disable-gpu", "--ignore-certificate-errors", "--ignore-ssl-errors", "--allow-running-insecure-content", "--disable-web-security",  "--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080"});
            options.merge(getDesiredCapabilities());

            WebDriver driver = new ChromeDriver(options);

            return driver;
        } else {
            WebDriver driver = new FirefoxDriver();

            return driver;
        }
    }
}
