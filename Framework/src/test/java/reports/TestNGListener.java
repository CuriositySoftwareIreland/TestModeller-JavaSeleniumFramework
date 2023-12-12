package reports;

import ie.curiositysoftware.jobengine.dto.job.TestCoverageEnum;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.jobengine.services.job.FailureAnalysisService;
import ie.curiositysoftware.runresult.dto.TestPathRun;
import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.dto.TestPathRunStep;
import ie.curiositysoftware.runresult.services.TestRunIdGenerator;
import ie.curiositysoftware.runresult.services.TestRunService;
import ie.curiositysoftware.testmodeller.TestModellerPath;
import ie.curiositysoftware.testmodeller.TestModellerSuite;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import tests.TestBase;
import utilities.CapabilityLoader;
import utilities.PropertiesLoader;
import utilities.testmodeller.TestModellerLogger;
import utilities.testmodeller.TestModellerMethodExtractor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.toIntExact;

public class TestNGListener implements ITestListener, IClassListener
{
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
        TestModellerLogger.ClearModellerContext();

        Object testClass = testResult.getInstance();

        if (testClass instanceof TestBase) {
            WebDriver webDriver = ((TestBase) testClass).getDriver();

            if (testResult.getThrowable() != null) {
                TestModellerLogger.FailStepWithScreenshot(webDriver, "Test Failed", testResult.getThrowable().getMessage());
            } else {
                TestModellerLogger.FailStepWithScreenshot(webDriver, "Test Failed");
            }
        } else {
            if (testResult.getThrowable() != null) {
                TestModellerLogger.FailStep("Test Failed", testResult.getThrowable().getMessage());
            } else {
                TestModellerLogger.FailStep("Test Failed");
            }
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
        TestModellerLogger.ClearModellerContext();

        Object testClass = testResult.getInstance();

        if (testClass instanceof TestBase) {
            WebDriver webDriver = ((TestBase) testClass).getDriver();

            TestModellerLogger.PassStepWithScreenshot(webDriver, "Test Passed");
        } else {
            TestModellerLogger.PassStep("Test Passed");
        }

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

    public static void StartTestRun(Method method)
    {
        // Set status to running
        TestRunService runService1 = new TestRunService(PropertiesLoader.getConnectionProfile());
        TestPathRun testPathRun = TestModellerLogger.CurrentRun.get();
        if (testPathRun != null) {
            testPathRun.setTestStatus(TestPathRunStatusEnum.Running);

            // Post it
            if (runService1.saveTestPathRun(testPathRun) == null) {
                System.out.println("Error posting path run " + runService1.getErrorMessage());
            }
        }
    }

    public static void StartTestRunInQueue(Method method)
    {
        TestModellerPath path = TestModellerMethodExtractor.getTestModellerPath(method);
        if (path == null)
            return;

        // Create TestPath run entity
        TestPathRun testPathRun = new TestPathRun();
        testPathRun.setRunTimeStamp(new Date());
        testPathRun.setTestPathGuid(path.guid());
        testPathRun.setRunSource("Selenium");
        testPathRun.setVipRunId(TestRunIdGenerator.getRunId());
        testPathRun.setTestStatus(TestPathRunStatusEnum.Queue);

        try {
            testPathRun.setJobId(Long.parseLong(PropertiesLoader.getProperties().getProperty("testModeller.jobId")));
        } catch (Exception e) {}

        // Post it
        TestRunService runService1 = new TestRunService(PropertiesLoader.getConnectionProfile());
        TestPathRun run = runService1.saveTestPathRun(testPathRun);
        if (run == null) {
            System.out.println("Error posting path run " + runService1.getErrorMessage());
        } else {
            TestModellerLogger.CurrentRun.set(run);
        }
    }

    public static void AddStep(TestPathRunStep step)
    {

    }

    private void postRunResult(ITestResult testResult, TestPathRunStatusEnum status) {
        // Get guid
        TestModellerPath path = TestModellerMethodExtractor.getTestModellerPath(testResult);
        if (path == null && TestModellerLogger.CurrentRun.get() != null)
            return;

        TestModellerSuite suite = TestModellerMethodExtractor.getTestModellerSuite((ITestClass) testResult.getTestClass());

        // Create TestPath run entity
        TestPathRun testPathRun = TestModellerLogger.CurrentRun.get();

        if (testPathRun != null) {
            testPathRun.setRunTime(toIntExact(testResult.getEndMillis() - testResult.getStartMillis()));
            testPathRun.setTestStatus(status);
//        testPathRun.setTestPathRunSteps(TestModellerLogger.steps.get());
            if (suite != null) {
                testPathRun.setProfileId(suite.profileId());
                testPathRun.setTestSuiteId(suite.id());
            }

            if (testResult.getThrowable() != null)
                testPathRun.setMessage(testResult.getThrowable().getMessage());

            // Post it
            if (runService.updateTestPathRun(testPathRun) == null) {
                System.out.println("Error posting path run " + runService.getErrorMessage());
            }
        }
    }

    private void postAnalysisJob(ITestClass testClass) {
        TestModellerSuite suite = TestModellerMethodExtractor.getTestModellerSuite(testClass);
        if(suite != null && suite.profileId() > 0) {
            if(failureService.analyseFailures(suite.profileId())) {
                executeNewTests(failureService.getNewTests());
            } else {
                System.out.println(failureService.getErrorMessage());
            }
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