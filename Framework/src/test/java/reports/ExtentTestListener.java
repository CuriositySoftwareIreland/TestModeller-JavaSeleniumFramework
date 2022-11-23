package reports;

import tests.TestBase;
import utilities.reports.ExtentReportManager;
import utilities.testmodeller.GetScreenShot;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentTestListener extends TestBase implements ITestListener {
    private static String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        iTestContext.setAttribute("WebDriver", getDriver());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        ExtentReportManager.extentReport.flush();
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        Object testClass = iTestResult.getInstance();

        if (testClass instanceof TestBase) {
            WebDriver webDriver = ((TestBase) testClass).getDriver();
            ExtentReportManager.passStepWithScreenshot(webDriver, "Test Passed");
        } else {
            ExtentReportManager.getCurrentTest().log(Status.PASS, "Test Passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        Object testClass = iTestResult.getInstance();

        if (testClass instanceof TestBase) {
            WebDriver webDriver = ((TestBase) testClass).getDriver();
            ExtentReportManager.failStepWithScreenshot(webDriver, "Test Failure");
        } else {
            //ExtentReports log and screenshot operations for failed tests.
            ExtentReportManager.getCurrentTest().log(Status.FAIL, iTestResult.getThrowable());
        }
        ExtentReportManager.extentReport.flush();

    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }
}