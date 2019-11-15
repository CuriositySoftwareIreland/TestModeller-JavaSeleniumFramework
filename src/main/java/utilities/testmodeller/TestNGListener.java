package utilities.testmodeller;

import utilities.PropertiesLoader;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.runresult.dto.TestPathRun;
import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.services.TestRunIdGenerator;
import ie.curiositysoftware.runresult.services.TestRunService;
import ie.curiositysoftware.testmodeller.TestModellerPath;
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
        String guid = GetTestModellerPathGuid(testResult);

        if (guid == null)
            return;

        // Create TestPath run entity
        TestPathRun testPathRun = new TestPathRun();
        testPathRun.setMessage(testResult.getThrowable().getMessage());
        testPathRun.setRunTime(toIntExact(testResult.getEndMillis() - testResult.getStartMillis()));
        testPathRun.setRunTimeStamp(new Date(testResult.getStartMillis()));
        testPathRun.setTestPathGuid(guid);
        testPathRun.setVipRunId(TestRunIdGenerator.getRunId());
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
        String guid = GetTestModellerPathGuid(testResult);

        if (guid == null)
            return;

        // Create TestPath run entity
        TestPathRun testPathRun = new TestPathRun();
        testPathRun.setRunTime(toIntExact(testResult.getEndMillis() - testResult.getStartMillis()));
        testPathRun.setRunTimeStamp(new Date(testResult.getStartMillis()));
        testPathRun.setTestPathGuid(guid);
        testPathRun.setVipRunId(TestRunIdGenerator.getRunId());
        testPathRun.setTestStatus(TestPathRunStatusEnum.Passed);

        // Post it
        TestRunService runService = new TestRunService(new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey")));
        if (!runService.saveTestPathRun(testPathRun)) {
            System.out.println("Error posting path run " + runService.getErrorMessage());
        }
    }

    private String GetTestModellerPathGuid(ITestResult testResult)
    {
        String pathGuid = null;

        IClass obj = testResult.getTestClass();

        Class newobj = obj.getRealClass();

        Method testMethod = null;
        try {
            testMethod = newobj.getMethod(testResult.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (testMethod != null && testMethod.isAnnotationPresent(TestModellerPath.class))
        {
            TestModellerPath useAsTestName = testMethod.getAnnotation(TestModellerPath.class);

            pathGuid = useAsTestName.guid();

            System.out.println("Test Modeller ID = " + pathGuid);
        }

        return pathGuid;
    }
}