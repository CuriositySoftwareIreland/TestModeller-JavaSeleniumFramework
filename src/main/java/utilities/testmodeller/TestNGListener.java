package utilities.testmodeller;

import ie.curiositysoftware.jobengine.services.job.TestGenerationService;
import ie.curiositysoftware.jobengine.utils.JobExecutor;
import ie.curiositysoftware.testmodeller.TestModellerSuite;
import org.testng.*;
import utilities.PropertiesLoader;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.runresult.dto.TestPathRun;
import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.services.TestRunIdGenerator;
import ie.curiositysoftware.runresult.services.TestRunService;
import ie.curiositysoftware.testmodeller.TestModellerPath;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.toIntExact;

public class TestNGListener implements ITestListener, IClassListener {

    private TestRunService runService;
    private TestGenerationService generationService;
    private JobExecutor jobExecutor;

    private int failedTestsInClass = 0;
    private List<ITestClass> testsClassesToAnalyse = new ArrayList<>();

    private final boolean uploadResults;
    private final boolean analyseFailures;
    private final long jobTimeout;

    public TestNGListener() {
        uploadResults = Boolean.parseBoolean(PropertiesLoader.getProperties().getProperty("testModeller.uploadResults"));
        analyseFailures = Boolean.parseBoolean(PropertiesLoader.getProperties().getProperty("testModeller.analyseFailures"));
        jobTimeout = Long.parseLong(PropertiesLoader.getProperties().getProperty("testModeller.jobTimeout"));

        String apiHost = PropertiesLoader.getProperties().getProperty("testModeller.apiHost");
        String apiKey = PropertiesLoader.getProperties().getProperty("testModeller.apiKey");
        ConnectionProfile profile = new ConnectionProfile(apiHost, apiKey);

        if (uploadResults) {
            runService = new TestRunService(profile);
        }
        if(analyseFailures) {
            generationService = new TestGenerationService(profile);
            jobExecutor = new JobExecutor(profile);
        }
    }

    @Override
    public void onTestFailure(ITestResult testResult) {
        System.out.println("Test failure");
        failedTestsInClass++;
        if(uploadResults)
            postRunResult(testResult, TestPathRunStatusEnum.Failed);
    }

    @Override
    public void onTestSuccess(ITestResult testResult) {
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
        testPathRun.setVipRunId(TestRunIdGenerator.getRunId());
        testPathRun.setTestStatus(status);
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
            generationService.startAnalysisAndGenerationJob(suite.profileId());
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
}