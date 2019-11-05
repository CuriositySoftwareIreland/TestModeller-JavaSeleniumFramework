package Tests;

import Utilities.CapabilityLoader;
import Utilities.Reports.ExtentReportManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class TestBase {
    /********** Replace with your own details ***********/
    protected WebDriver driver;

    public WebDriver getDriver()
    {
        return driver;
    }

    @BeforeSuite(alwaysRun = true)
    public void setupReporter()
    {
        ExtentReportManager.setupReporter();
    }

    @BeforeMethod(alwaysRun = true)
    public void initDriver(Method method)
    {
        ExtentReportManager.createNewTest(method.getName());

        driver = CapabilityLoader.createWebDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closerDriver()
    {
        driver.quit();
    }

    @AfterSuite
    public void closeReporter()
    {
        ExtentReportManager.closeReporter();
    }
}
