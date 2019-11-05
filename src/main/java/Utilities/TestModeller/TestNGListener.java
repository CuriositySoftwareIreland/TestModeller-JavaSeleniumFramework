package Utilities.TestModeller;

import Utilities.PropertiesLoader;
import ie.curiositysoftware.JobEngine.Services.ConnectionProfile;
import ie.curiositysoftware.RunResult.Entities.TestPathRunEntity;
import ie.curiositysoftware.RunResult.Entities.TestPathRunStatusEnum;
import ie.curiositysoftware.RunResult.Entities.UseTestModellerId;
import ie.curiositysoftware.RunResult.Services.TestRunIdGenerator;
import ie.curiositysoftware.RunResult.Services.TestRunService;
import org.testng.IClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.Date;

import static java.lang.Math.toIntExact;

public class TestNGListener implements ITestListener {

    @Override
    public void onFinish(ITestContext test) {
        System.out.println("Test Case finished" + test.getName());
    }

    @Override
    public void onStart(ITestContext test) {
        System.out.println("Test Case started" + test.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

    }

    @Override
    public void onTestFailure(ITestResult testResult) {
        // Get guid
        String guid = GetTestModellerGuid(testResult);

        if (guid == null)
            return;

        // Create TestPath run entity
        TestPathRunEntity testPathRun = new TestPathRunEntity();
        testPathRun.setMessage(testResult.getThrowable().getMessage());
        testPathRun.setRunTime(toIntExact(testResult.getEndMillis() - testResult.getStartMillis()));
        testPathRun.setRunTimeStamp(new Date(testResult.getStartMillis()));
        testPathRun.setTestPathGuid(guid);
        testPathRun.setVipRunId(TestRunIdGenerator.GetRunId());
        testPathRun.setTestStatus(TestPathRunStatusEnum.Failed);

        // Post it
        TestRunService runService = new TestRunService(new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey")));
        if (!runService.saveTestPathRun(testPathRun)) {
            System.out.println("Error posting path run " + runService.getErrorMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult arg0) {

    }

    @Override
    public void onTestStart(ITestResult arg0) {
        System.out.println("Test Case started" + arg0);
    }

    @Override
    public void onTestSuccess(ITestResult testResult) {
        System.out.println("Test success");

        // Get guid
        String guid = GetTestModellerGuid(testResult);

        if (guid == null)
            return;

        // Create TestPath run entity
        TestPathRunEntity testPathRun = new TestPathRunEntity();
        testPathRun.setRunTime(toIntExact(testResult.getEndMillis() - testResult.getStartMillis()));
        testPathRun.setRunTimeStamp(new Date(testResult.getStartMillis()));
        testPathRun.setTestPathGuid(guid);
        testPathRun.setVipRunId(TestRunIdGenerator.GetRunId());
        testPathRun.setTestStatus(TestPathRunStatusEnum.Passed);

        // Post it
        TestRunService runService = new TestRunService(new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey")));
        if (!runService.saveTestPathRun(testPathRun)) {
            System.out.println("Error posting path run " + runService.getErrorMessage());
        }
    }

    private String GetTestModellerGuid(ITestResult testResult)
    {
        String TestID = null;

        IClass obj = testResult.getTestClass();

        Class newobj = obj.getRealClass();

        Method testMethod = null;
        try {
            testMethod = newobj.getMethod(testResult.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (testMethod != null && testMethod.isAnnotationPresent(UseTestModellerId.class))
        {
            UseTestModellerId useAsTestName = testMethod.getAnnotation(UseTestModellerId.class);

            TestID = useAsTestName.testModellerGuid();

            System.out.println("Test Modeller ID = " + TestID);
        }

        return TestID;
    }
}