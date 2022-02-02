package reports;

import ie.curiositysoftware.jobengine.services.ConnectionProfile;

import ie.curiositysoftware.runresult.dto.TestPathRun;
import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.services.TestRunIdGenerator;
import ie.curiositysoftware.runresult.services.TestRunService;

import ie.curiositysoftware.testmodeller.TestModellerPath;
import ie.curiositysoftware.testmodeller.TestModellerSuite;

import org.openqa.selenium.WebDriver;
import org.testng.*;
import tests.TestBase;
import utilities.testmodeller.TestModellerLogger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.toIntExact;

public class TestNGListener implements ITestListener, IClassListener {

    private final TestRunService runService;

    private final FailureAnalysisService failureService;

    private int failedTestsInClass = 0;
    private List<ITestClass> testsClassesToAnalyse = new ArrayList<>();

    private final boolean uploadResults = true;
    private final boolean analyseFailures = false;

    private static boolean runningGeneratedTests = false;

    public TestNGListener() {
		ConnectionProfile profile = new ConnectionProfile("APIHOST", "APIKEY");
        runService = new TestRunService(profile);
    }

    @Override
    public void onTestFailure(ITestResult testResult) {
        Object testClass = testResult.getInstance();
        WebDriver webDriver = ((TestBase) testClass).getDriver();

        if(testResult.getThrowable() != null) {
            TestModellerLogger.FailStepWithScreenshot(webDriver, "Test Failed", testResult.getThrowable().getMessage());
        } else {
            TestModellerLogger.FailStepWithScreenshot(webDriver, "Test Failed");
        }

        System.out.println("Test failure");
        failedTestsInClass++;
        if(uploadResults)
            postRunResult(testResult, TestPathRunStatusEnum.Failed);
    }

    @Override
    public void onTestStart(ITestResult result) {
        TestModellerLogger.ClearMessages();
    }


    @Override
    public void onTestSuccess(ITestResult testResult) {
        Object testClass = testResult.getInstance();

        WebDriver webDriver = ((TestBase) testClass).getDriver();

        TestModellerLogger.PassStepWithScreenshot(webDriver, "Test Passed");

        System.out.println("Test success");
        if(uploadResults)
            postRunResult(testResult, TestPathRunStatusEnum.Passed);
    }

    private void postRunResult(ITestResult testResult, TestPathRunStatusEnum status) {
        // Get guid
        TestModellerPath path = getTestModellerPath(testResult);
        if (path == null)
            return;

        // Create TestPath run entity
        TestPathRun testPathRun = new TestPathRun();
        testPathRun.setRunTime(toIntExact(testResult.getEndMillis() - testResult.getStartMillis()));
        testPathRun.setRunTimeStamp(new Date(testResult.getStartMillis()));
        testPathRun.setTestPathGuid(path.guid());
        testPathRun.setRunSource("Selenium");

        testPathRun.setVipRunId(TestRunIdGenerator.getRunId());
        testPathRun.setTestStatus(status);
        testPathRun.setTestPathRunSteps(TestModellerLogger.steps.get());
        if(testResult.getThrowable() != null)
            testPathRun.setMessage(testResult.getThrowable().getMessage());

        // Post it
        if (!runService.saveTestPathRun(testPathRun)) {
            System.out.println("Error posting path run " + runService.getErrorMessage());
        }
    }

    private TestModellerPath getTestModellerPath(ITestResult testResult) {
        Method testMethod = testResult.getMethod().getConstructorOrMethod().getMethod();
        if (testMethod != null && testMethod.isAnnotationPresent(TestModellerPath.class)) {
            TestModellerPath path = testMethod.getAnnotation(TestModellerPath.class);
            System.out.println("Test Modeller Path GUID = " + path.guid());
            return path;
        } else {
            return null;
        }
    }
}