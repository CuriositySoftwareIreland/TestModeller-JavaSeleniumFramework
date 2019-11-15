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
        iTestContext.setAttribute("WebDriver", this.driver);
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
        ExtentReportManager.extentTest.log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        //Get driver from BaseTest and assign to local webDriver variable.
        Object testClass = iTestResult.getInstance();
        WebDriver webDriver = ((TestBase) testClass).getDriver();

        String screenShotPath = GetScreenShot.captureAsBase64(webDriver);
        ExtentReportManager.extentTest.addScreenCaptureFromBase64String(screenShotPath);

        //ExtentReports log and screenshot operations for failed tests.
        ExtentReportManager.extentTest.log(Status.FAIL, iTestResult.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }
}