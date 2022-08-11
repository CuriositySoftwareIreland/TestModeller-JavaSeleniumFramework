package tests;

import utilities.CapabilityLoader;
import utilities.PropertiesLoader;
import utilities.reports.ExtentReportManager;
import ie.curiositysoftware.allocation.dto.AllocationType;
import ie.curiositysoftware.allocation.engine.DataAllocation;
import ie.curiositysoftware.allocation.engine.DataAllocationEngine;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestBase {
    protected ConnectionProfile cp = new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey"));

    protected DataAllocationEngine dataAllocationEngine = new DataAllocationEngine(cp);

    public WebDriver getDriver()
    {
        return CapabilityLoader.getDriver();
    }

    @BeforeSuite(alwaysRun = true)
    public void setupReporter()
    {
        ExtentReportManager.setupReporter();
    }

    @BeforeSuite(alwaysRun = true)
    public void allocateData(ITestContext testContext)
    {
        // Create a list of all the pools that need allocating
        List<AllocationType> allocationTypes = new ArrayList<AllocationType>();

        ITestNGMethod[] methods =  testContext.getAllTestMethods();
        try {

            for (int i = 0; i < methods.length; i++) {
                ITestNGMethod method = methods[i];

                Method testMethod = method.getConstructorOrMethod().getMethod();

                if (testMethod != null && testMethod.isAnnotationPresent(DataAllocation.class))
                {
                    DataAllocation dataAllocation = testMethod.getAnnotation(DataAllocation.class);

                    for (String testType : dataAllocation.groups()) {
                        AllocationType allocationType = new AllocationType(dataAllocation.poolName(), dataAllocation.suiteName(), testType);

                        allocationTypes.add(allocationType);
                    }
                }
            }
        } catch (Throwable e) {
            System.err.println(e);
        }

        // Publish and allocate data
        if (!dataAllocationEngine.resolvePools(PropertiesLoader.getProperties().getProperty("testModeller.serverName"), allocationTypes)) {
            System.out.println("Error - " + dataAllocationEngine.getErrorMessage());
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void initDriver(Method method)
    {
        ExtentReportManager.createNewTest(method);

        CapabilityLoader.setDriver(CapabilityLoader.createWebDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void closerDriver()
    {
        try {
            if (CapabilityLoader.getDriver() != null)
                CapabilityLoader.getDriver().quit();
        } catch (Exception e) {}
    }

    @AfterSuite
    public void closeReporter()
    {
        ExtentReportManager.closeReporter();
    }
}
