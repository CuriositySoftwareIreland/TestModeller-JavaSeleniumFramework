package pages;

import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.reports.ExtentReportManager;
import utilities.testmodeller.TestModellerLogger;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TestModellerIgnore
public class BasePage {
    protected WebDriver m_Driver;

    protected WebDriverWait jsWait;

    protected JavascriptExecutor jsExec;

    @TestModellerIgnore
    public BasePage(WebDriver driver)
    {
        this.m_Driver = driver;

        jsWait = new WebDriverWait(this.m_Driver, 10);

        jsExec = (JavascriptExecutor) this.m_Driver;
    }

    protected void failStep(String msg, String details)
    {
        ExtentReportManager.failStepWithScreenshot(m_Driver, msg, details);

        TestModellerLogger.FailStepWithScreenshot(m_Driver, msg, details);

        Assert.fail(msg);
    }

    protected void failStep(String msg)
    {
        ExtentReportManager.failStepWithScreenshot(m_Driver, msg);
        TestModellerLogger.FailStepWithScreenshot(m_Driver, msg);

        Assert.fail(msg);
    }

    protected void passStep(String msg)
    {
        ExtentReportManager.passStep(msg);
        TestModellerLogger.PassStep(msg);
    }

    protected void passStepWithScreenshot(String msg)
    {
        ExtentReportManager.passStepWithScreenshot(m_Driver, msg);
        TestModellerLogger.PassStepWithScreenshot(m_Driver, msg);
    }

    protected RemoteWebElement expandRootElement(WebElement element) {
        RemoteWebElement ele = (RemoteWebElement) ((JavascriptExecutor) m_Driver).executeScript("return arguments[0].shadowRoot", element);

        return ele;
    }

    protected RemoteWebElement expandShadowRoots(List<By> elems)
    {
        if (elems.isEmpty())
            return null;

        RemoteWebElement element = expandRootElement(getWebElement(elems.get(0)));

        for (int i = 1; i < elems.size(); i++) {
            element = expandRootElement(getWebElement(element, elems.get(i)));
        }

        return element;
    }

    protected WebElement getWebElement(WebElement elem, final By by)
    {
        waitForLoaded(elem, by, 5);
        waitForVisible(elem, by, 5);

        if (m_Driver.getCurrentUrl() == null || m_Driver.getCurrentUrl().isEmpty() || m_Driver.getCurrentUrl().equals("data:,")) {
            Assert.fail("No webpage loaded. Add a 'Go To URL' action prior to trying to interact with a web element.");
        }

        return elem.findElement(by);
    }

    protected List<WebElement> getWebElements(final By by)
    {
        waitForLoaded(by, 2);
        waitForVisible(by, 2);

        if (m_Driver.getCurrentUrl() == null || m_Driver.getCurrentUrl().isEmpty() || m_Driver.getCurrentUrl().equals("data:,")) {
            Assert.fail("No webpage loaded. Add a 'Go To URL' action prior to trying to interact with a web element.");
        }

        try {
            return m_Driver.findElements(by);
        } catch (Exception e) {
            return null;
        }
    }

    protected void waitForLoaded(WebElement elem, final By by, int waitTime) {
        WebDriverWait wait = new WebDriverWait(m_Driver, waitTime);

        for (int attempt = 0; attempt < waitTime; attempt++) {
            try {
                elem.findElement(by);
                break;
            } catch (Exception e) {
                m_Driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            }
        }
    }

    protected void waitForVisible(WebElement selem, final By by, int waitTime)
    {
        try {
            WebElement elem = selem.findElement(by);

            WebDriverWait wait = new WebDriverWait(m_Driver, waitTime);

            wait.until(ExpectedConditions.visibilityOf(elem));
        } catch (Exception e) {

        }

        try {
            WebElement elem = selem.findElement(by);

            WebDriverWait wait = new WebDriverWait(m_Driver, waitTime);

            wait.until(ExpectedConditions.elementToBeClickable(elem));
        } catch (Exception e) {

        }

    }

    protected WebElement getWebElement(final By by)
    {
        waitForLoaded(by, 5);
        waitForVisible(by, 5);

        if (m_Driver.getCurrentUrl() == null || m_Driver.getCurrentUrl().isEmpty() || m_Driver.getCurrentUrl().equals("data:,")) {
            Assert.fail("No webpage loaded. Add a 'Go To URL' action prior to trying to interact with a web element.");
        }

        try {
            return m_Driver.findElement(by);
        } catch (Exception e) {
            return null;
        }
    }

    protected void waitForLoaded(final By by, int waitTime) {
        WebDriverWait wait = new WebDriverWait(m_Driver, waitTime);

        for (int attempt = 0; attempt < waitTime; attempt++) {
            try {
                m_Driver.findElement(by);
                break;
            } catch (Exception e) {
                m_Driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            }
        }
    }

    protected void waitForVisible(final By by, int waitTime)
    {
        try {
            WebElement elem = m_Driver.findElement(by);

            WebDriverWait wait = new WebDriverWait(m_Driver, waitTime);

            wait.until(ExpectedConditions.visibilityOf(elem));
        } catch (Exception e) {

        }
    }

    //Wait for JQuery Load
    protected void waitForJQueryLoad() {
        //Wait for jQuery to load
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long)((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
                }
                catch (Exception e) {
                    // no jQuery present
                    return true;
                }
            }
        };

        //Get JQuery is Ready
        boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");

        //Wait JQuery until it is Ready!
        if(!jqueryReady) {
            System.out.println("JQuery is NOT Ready!");
            //Wait for jQuery to load
            jsWait.until(jQueryLoad);
        } else {
            System.out.println("JQuery is Ready!");
        }
    }


    //Wait for Angular Load
    protected void waitForAngularLoad() {
        WebDriverWait wait = new WebDriverWait(m_Driver,15);
        JavascriptExecutor jsExec = (JavascriptExecutor) m_Driver;

        final String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";

        //Wait for ANGULAR to load
        ExpectedCondition<Boolean> angularLoad = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    return Boolean.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString());
                }
                catch (Exception e) {
                    // no jQuery present
                    return true;
                }
            }
        };


        //Get Angular is Ready
        boolean angularReady = Boolean.valueOf(jsExec.executeScript(angularReadyScript).toString());

        //Wait ANGULAR until it is Ready!
        if(!angularReady) {
            System.out.println("ANGULAR is NOT Ready!");
            //Wait for Angular to load
            wait.until(angularLoad);
        } else {
            System.out.println("ANGULAR is Ready!");
        }
    }

    //Wait Until JS Ready
    protected void waitUntilJSReady() {
        WebDriverWait wait = new WebDriverWait(m_Driver,15);
        JavascriptExecutor jsExec = (JavascriptExecutor) m_Driver;

        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                try {
                    return ((JavascriptExecutor) driver).executeScript("return document.readyState\"").toString().equals("complete");
                }
                catch (Exception e) {
                    // no jQuery present
                    return true;
                }
            }
        };

        //Get JS is Ready
        boolean jsReady =  (Boolean) jsExec.executeScript("return document.readyState").toString().equals("complete");

        //Wait Javascript until it is Ready!
        if(!jsReady) {
            System.out.println("JS in NOT Ready!");
            //Wait for Javascript to load
            wait.until(jsLoad);
        } else {
            System.out.println("JS is Ready!");
        }
    }

    //Wait Until JQuery and JS Ready
    protected void waitUntilJQueryReady() {
        JavascriptExecutor jsExec = (JavascriptExecutor) m_Driver;

        //First check that JQuery is defined on the page. If it is, then wait AJAX
        Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
        if (jQueryDefined == true) {
            //Pre Wait for stability (Optional)
            sleep(20);

            //Wait JQuery Load
            waitForJQueryLoad();

            //Wait JS Load
            waitUntilJSReady();

            //Post Wait for stability (Optional)
            sleep(20);
        }  else {
            System.out.println("jQuery is not defined on this site!");
        }
    }

    //Wait Until Angular and JS Ready
    protected void waitUntilAngularReady() {
        JavascriptExecutor jsExec = (JavascriptExecutor) m_Driver;

        //First check that ANGULAR is defined on the page. If it is, then wait ANGULAR
        Boolean angularUnDefined = (Boolean) jsExec.executeScript("return window.angular === undefined");
        if (!angularUnDefined) {
            Boolean angularInjectorUnDefined = (Boolean) jsExec.executeScript("return angular.element(document).injector() === undefined");
            if(!angularInjectorUnDefined) {
                //Pre Wait for stability (Optional)
                sleep(20);

                //Wait Angular Load
                waitForAngularLoad();

                //Wait JS Load
                waitUntilJSReady();

                //Post Wait for stability (Optional)
                sleep(20);
            } else {
                System.out.println("Angular injector is not defined on this site!");
            }
        }  else {
            System.out.println("Angular is not defined on this site!");
        }
    }

    //Wait Until JQuery Angular and JS is ready
    protected void waitJQueryAngular() {
        waitUntilJQueryReady();
        waitUntilAngularReady();
    }

    protected void sleep (Integer milliSeconds) {
        long secondsLong = (long) milliSeconds;
        try {
            Thread.sleep(secondsLong);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
