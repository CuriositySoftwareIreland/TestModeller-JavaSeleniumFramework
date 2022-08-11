package reports;

import ie.curiositysoftware.jobengine.dto.job.TestCoverageEnum;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.jobengine.services.job.FailureAnalysisService;
import ie.curiositysoftware.runresult.dto.TestPathRun;
import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.services.TestRunIdGenerator;
import ie.curiositysoftware.runresult.services.TestRunService;
import ie.curiositysoftware.testmodeller.TestModellerPath;
import ie.curiositysoftware.testmodeller.TestModellerSuite;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import tests.TestBase;
import utilities.PropertiesLoader;
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

    private final boolean uploadResults;
    private final boolean analyseFailures;

    private static boolean runningGeneratedTests = false;

    public TestNGListener() {
        uploadResults = Boolean.parseBoolean(PropertiesLoader.getProperties().getProperty("testModeller.uploadResults"));
        analyseFailures = !runningGeneratedTests && Boolean.parseBoolean(PropertiesLoader.getProperties().getProperty("testModeller.analyseFailures"));

        String apiHost = PropertiesLoader.getProperties().getProperty("testModeller.apiHost");
        String apiKey = PropertiesLoader.getProperties().getProperty("testModeller.apiKey");
        ConnectionProfile profile = new ConnectionProfile(apiHost, apiKey);
        runService = new TestRunService(profile);

        String serverName = PropertiesLoader.getProperties().getProperty("testModeller.serverName");
        Long jobTimeout = Long.parseLong(PropertiesLoader.getProperties().getProperty("testModeller.analyser.jobTimeout"));
        Long codeTemplateId = Long.parseLong(PropertiesLoader.getProperties().getProperty("testModeller.analyser.codeTemplateId"));
        Boolean includeOldTests = Boolean.parseBoolean(PropertiesLoader.getProperties().getProperty("testModeller.analyser.includeOldTests"));
        failureService = new FailureAnalysisService(profile, jobTimeout, serverName, codeTemplateId, includeOldTests, TestCoverageEnum.Medium);
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

    @Override
    public void onBeforeClass(ITestClass testClass) {
        failedTestsInClass = 0;
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        if(analyseFailures && failedTestsInClass > 0)
            testsClassesToAnalyse.add(testClass);
    }

    @Override
    public void onFinish(ITestContext context) {
        testsClassesToAnalyse.forEach(this::postAnalysisJob);
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

        try {
            testPathRun.setJobId(Long.parseLong(PropertiesLoader.getProperties().getProperty("testModeller.jobId")));
        } catch (Exception e) {}

        if(testResult.getThrowable() != null)
            testPathRun.setMessage(testResult.getThrowable().getMessage());

        // Post it
        if (!runService.saveTestPathRun(testPathRun)) {
            System.out.println("Error posting path run " + runService.getErrorMessage());
        }
    }

    private void postAnalysisJob(ITestClass testClass) {
        TestModellerSuite suite = getTestModellerSuite(testClass);
        if(suite != null && suite.profileId() > 0) {
            if(failureService.analyseFailures(suite.profileId())) {
                executeNewTests(failureService.getNewTests());
            } else {
                System.out.println(failureService.getErrorMessage());
            }
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

    private TestModellerSuite getTestModellerSuite(ITestClass testClass) {
        Class<?> realClass = testClass.getRealClass();
        if(realClass != null && realClass.isAnnotationPresent(TestModellerSuite.class)) {
            TestModellerSuite suite = realClass.getAnnotation(TestModellerSuite.class);
            System.out.println("Test Modeller Suite ID = " + suite.id());
            return suite;
        } else {
            return null;
        }
    }

    private void executeNewTests(List<Class<?>> newTests) {
        boolean previousValue = runningGeneratedTests;
        runningGeneratedTests = true;

        TestNG testNG = new TestNG();
        testNG.setTestClasses(newTests.toArray(new Class[0]));
        testNG.run();

        runningGeneratedTests = previousValue;
    }
}