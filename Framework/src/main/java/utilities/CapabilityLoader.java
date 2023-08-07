package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import utilities.reports.ExtentReportManager;

import java.io.File;
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

            desiredCapabilities.setCapability("browserName", "chrome");

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
            WebDriver d = new RemoteWebDriver(new URL(SAUCE_REMOTE_URL), getDesiredCapabilities());

            setDriver(d);

            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static WebDriver createBrowserStackDriver(String username, String accesskey)
    {
        String browserURL = "https://" + username + ":" + accesskey + "@hub.browserstack.com/wd/hub";


        try {
            WebDriver d = new RemoteWebDriver(new URL(browserURL), getDesiredCapabilities());

            setDriver(d);

            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static WebDriver createLambdaTestDriver(String username, String accesskey)
    {
        getDesiredCapabilities().setCapability("username", username);
        getDesiredCapabilities().setCapability("accessKey", accesskey);

        String browserURL = "https://hub.lambdatest.com/wd/hub";

        try {
            WebDriver d = new RemoteWebDriver(new URL(browserURL), getDesiredCapabilities());

            setDriver(d);

            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static WebDriver createEdgeDriver()
    {
        WebDriverManager.edgedriver().setup();

        EdgeOptions edgeOptions = new EdgeOptions();

        if (PropertiesLoader.isHeadless()) {
            edgeOptions.addArguments("headless");
        }

        edgeOptions.addArguments("--no-sandbox");
        edgeOptions.addArguments("--disable-dev-shm-usage");
        edgeOptions.addArguments("--ignore-certificate-errors");
        edgeOptions.addArguments("--ignore-ssl-errors");
        edgeOptions.addArguments("--allow-running-insecure-content");
        edgeOptions.addArguments("--window-size=1920,1080");
        edgeOptions.addArguments("--remote-allow-origins=*");

        edgeOptions.merge(getDesiredCapabilities());

        WebDriver driver = new EdgeDriver(edgeOptions);

        setDriver(driver);

        return driver;
    }

    public static WebDriver createFirefoxDriver()
    {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions firefoxOptions = new FirefoxOptions();

        if (PropertiesLoader.isHeadless()) {
            firefoxOptions.addArguments("-headless");
        }

        firefoxOptions.setAcceptInsecureCerts(true); // For --ignore-certificate-errors and --ignore-ssl-errors
        firefoxOptions.addArguments("--width=1920");
        firefoxOptions.addArguments("--height=1080");

        firefoxOptions.merge(getDesiredCapabilities());

        WebDriver driver = new FirefoxDriver(firefoxOptions);

        setDriver(driver);

        return driver;
    }

    public static WebDriver createChromeDriver()
    {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);

        if (PropertiesLoader.isHeadless()) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        options.merge(getDesiredCapabilities());

        try {
            options.addExtensions(new File("extension/TestModellerExtension.crx"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebDriver driver = new ChromeDriver(options);

        setDriver(driver);

        return driver;
    }

    public static WebDriver createWebDriver()
    {
        return createChromeDriver();
    }
}
