package utilities.reports;


import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.model.ScreenCapture;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.runresult.dto.TestPathRunCollectionEntity;
import ie.curiositysoftware.runresult.services.TestPathRunCollectionService;
import ie.curiositysoftware.runresult.services.TestRunIdGenerator;
import ie.curiositysoftware.runresult.services.TestRunService;
import io.restassured.response.Response;
import utilities.PropertiesLoader;
import utilities.testmodeller.GetScreenShot;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExtentReportManager {
    public static ExtentReports extentReport;

    private static final ThreadLocal<ExtentTest> reportThreadLocal = new ThreadLocal<ExtentTest>();

    public static final ThreadLocal<String> currentTestGuid = new ThreadLocal<String>();

    public static final ThreadLocal<String> currentTestName = new ThreadLocal<String>();

    public static String extentDir = System.getProperty("user.dir") +"/report";

    public static void setupReporter()
    {
        ExtentSparkReporter avent = new ExtentSparkReporter(extentDir +  "/index.html");

        System.out.println("Report location - " + extentDir +  "/index.html");
        if (extentReport == null) {
            extentReport = new ExtentReports();
            extentReport.attachReporter(avent);
        }
    }

    public static ExtentTest getCurrentTest()
    {
        return reportThreadLocal.get();
    }

    public static void closeReporter()
    {
        extentReport.flush();

        // Post to modeller
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zs = new ZipOutputStream(baos)) {
            Path pp = Paths.get(extentDir);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        } catch (Exception e) {

        }

        String apiHost = PropertiesLoader.getProperties().getProperty("testModeller.apiHost");
        String apiKey = PropertiesLoader.getProperties().getProperty("testModeller.apiKey");
        ConnectionProfile profile = new ConnectionProfile(apiHost, apiKey);
        TestPathRunCollectionService runColService = new TestPathRunCollectionService(profile);

        TestPathRunCollectionEntity testRunCol = new TestPathRunCollectionEntity();
        testRunCol.setCreated(Calendar.getInstance().getTime());
        testRunCol.setFileByte(baos.toByteArray());
        testRunCol.setFileName("report.zip");
        testRunCol.setGuid(TestRunIdGenerator.getRunId());
        testRunCol.setName("Selenium framework");

        runColService.saveTestPathRun(testRunCol);
    }

    public static void createNewTest(Method method)
    {
        reportThreadLocal.set(extentReport.createTest(method.getName()));

        currentTestName.set(method.getName());
    }

    public static void passStep(WebDriver driver, String stepName)
    {
        reportThreadLocal.get().log(Status.PASS, stepName);
    }

    public static void passStep(Response rsp, String stepName)
    {
        reportThreadLocal.get().log(Status.PASS, stepName + "\n" +
                                    "Status Code: " + rsp.getStatusCode() + "\n" +
                                    "Status: " + rsp.getStatusLine() + "\n" +
                                    "Message: " + rsp.getBody().asString());
    }

    public static void failStep(Response rsp, String stepName)
    {
        reportThreadLocal.get().log(Status.FAIL, stepName + "\n" +
                                    "Status Code: " + rsp.getStatusCode() + "\n" +
                                    "Status: " + rsp.getStatusLine() + "\n" +
                                    "Message: " + rsp.getBody().asString());
    }

    public static void passStep(String stepName)
    {
        reportThreadLocal.get().log(Status.PASS, stepName);
    }

    public static void failStep(String stepName)
    {
        reportThreadLocal.get().log(Status.FAIL, stepName);
    }

    public static void failStep(WebDriver driver, String stepName)
    {
        reportThreadLocal.get().log(Status.FAIL, stepName);
    }

    public static void passStepWithScreenshot(WebDriver driver, String stepName)
    {
        passStepWithScreenshot(driver, stepName, stepName);
    }

    public static void passStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        Media m = ScreenCapture.builder().base64(GetScreenShot.captureAsBase64(driver)).title(stepName).build();

        reportThreadLocal.get().log(Status.PASS, m);
    }

    public static void failStepWithScreenshot(WebDriver driver, String stepName)
    {
        failStepWithScreenshot(driver, stepName, stepName);
    }

    public static void failStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        Media m = ScreenCapture.builder().base64(GetScreenShot.captureAsBase64(driver)).title(stepName).build();

        reportThreadLocal.get().log(Status.FAIL, m);
    }
}
