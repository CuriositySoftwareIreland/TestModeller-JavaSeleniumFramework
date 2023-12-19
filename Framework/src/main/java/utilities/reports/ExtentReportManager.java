package utilities.reports;


import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.model.ScreenCapture;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.runresult.dto.TestPathRunCollectionEntity;
import ie.curiositysoftware.runresult.dto.TestPathRunStep;
import ie.curiositysoftware.runresult.dto.TestPathRunStepHTTPRequest;
import ie.curiositysoftware.runresult.dto.TestPathRunStepHTTPResponse;
import ie.curiositysoftware.runresult.services.TestPathRunCollectionService;
import ie.curiositysoftware.runresult.services.TestRunIdGenerator;
import ie.curiositysoftware.runresult.services.TestRunService;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.text.StringEscapeUtils;
import org.sikuli.script.Screen;
import org.sikuli.script.ScreenImage;
import utilities.PropertiesLoader;
import utilities.testmodeller.GetScreenShot;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExtentReportManager {
    public static ExtentReports extentReport;

    private static final ThreadLocal<ExtentTest> reportThreadLocal = new ThreadLocal<ExtentTest>();

    public static final ThreadLocal<String> currentTestGuid = new ThreadLocal<String>();

    public static final ThreadLocal<String> currentTestName = new ThreadLocal<String>();

    public static String extentDir = System.getProperty("user.dir") +"/report";

    public static Boolean runColPosted = false;

    public static void setupReporter()
    {
        ExtentSparkReporter avent = new ExtentSparkReporter(extentDir +  "/index.html");
        if (extentReport == null) {
            System.out.println("Report location - " + extentDir +  "/index.html");

            extentReport = new ExtentReports();
            extentReport.attachReporter(avent);
        }

        runColPosted = false;
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

        if (!runColPosted) {
            runColPosted = true;

            TestPathRunCollectionEntity testRunCol = new TestPathRunCollectionEntity();
            testRunCol.setCreated(Calendar.getInstance().getTime());
            testRunCol.setFileByte(baos.toByteArray());
            testRunCol.setFileName("report.zip");
            testRunCol.setGuid(TestRunIdGenerator.getRunId());
            testRunCol.setName("Selenium framework");

            runColService.saveTestPathRun(testRunCol);
        }
    }

    public static void createNewTest(Method method)
    {
        reportThreadLocal.set(extentReport.createTest(method.getName()));

        currentTestName.set(method.getName());
    }

    public static String getTestName()
    {
        String name = currentTestName.get();

        if (name == null)
            return "";

        return name;
    }

    public static void passStep(WebDriver driver, String stepName)
    {
        reportThreadLocal.get().log(Status.PASS, stepName);
    }

    public static void passStep(RequestSpecification req, Response rsp, String stepName)
    {
        String bodyContent = StringEscapeUtils.escapeHtml4(rsp.getBody().asString());

        String message = "<h3>" + stepName + "</h3>" +
                "<ul>" +
                "<li><b>Status Code:</b> " + rsp.getStatusCode() + "</li>" +
                "<li><b>Status:</b> " + rsp.getStatusLine() + "</li>" +
                "<li><b>Message:</b> " + "<pre>" + bodyContent + "</pre></li>" +
                "</ul>";

        ExtentTest passSection = reportThreadLocal.get().log(Status.PASS, message);

        populateAPITestStep(stepName, passSection, Status.PASS, req, rsp);
    }

    public static void failStep(RequestSpecification req, Response rsp, String stepName)
    {
        String bodyContent = StringEscapeUtils.escapeHtml4(rsp.getBody().asString());

        String message = "<h3>" + stepName + "</h3>" +
                "<ul>" +
                "<li><b>Status Code:</b> " + rsp.getStatusCode() + "</li>" +
                "<li><b>Status:</b> " + rsp.getStatusLine() + "</li>" +
                "<li><b>Message:</b> " + "<pre>" + bodyContent + "</pre></li>" +
                "</ul>";

        ExtentTest failSection = reportThreadLocal.get().log(Status.FAIL, message);

        populateAPITestStep(stepName, failSection, Status.FAIL, req, rsp);
    }

    public static void passStep(RequestSpecification req, Response rsp, String stepName, String message)
    {
        ExtentTest passSection = reportThreadLocal.get().log(Status.PASS, stepName + "<br>" +
                message);

        populateAPITestStep(stepName, passSection,Status.PASS, req, rsp);
    }

    public static void failStep(RequestSpecification req, Response rsp, String stepName, String message)
    {
        ExtentTest failSection = reportThreadLocal.get().log(Status.FAIL, stepName + "<br>" +
                message);

        populateAPITestStep(stepName, failSection, Status.FAIL, req, rsp);
    }

    public static void passStep(Response rsp, String stepName)
    {
        passStep(null, rsp, stepName);
    }

    public static void failStep(Response rsp, String stepName)
    {
        failStep(null, rsp, stepName);
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
    public static void passStepWithScreenshot(Screen screen, String stepName)
    {
        passStepWithScreenshot(screen, stepName, stepName);
    }

    public static void passStepWithScreenshot(Screen screen, String stepName, String details)
    {
        Media m = ScreenCapture.builder().base64(GetScreenShot.captureAsBase64(screen)).title(stepName).build();

        reportThreadLocal.get().log(Status.PASS, stepName + "\n" + details, m);
    }


    public static void passStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        Media m = ScreenCapture.builder().base64(GetScreenShot.captureAsBase64(driver)).title(stepName).build();

        reportThreadLocal.get().log(Status.PASS, stepName + "\n" + details, m);
    }

    public static void failStepWithScreenshot(WebDriver driver, String stepName)
    {
        failStepWithScreenshot(driver, stepName, stepName);
    }

    public static void failStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        Media m = ScreenCapture.builder().base64(GetScreenShot.captureAsBase64(driver)).title(stepName).build();

        reportThreadLocal.get().log(Status.FAIL, stepName + "\n" + details, m);
    }

    public static void failStepWithScreenshot(Screen screen, String stepName)
    {
        failStepWithScreenshot(screen, stepName, stepName);
    }

    public static void failStepWithScreenshot(Screen screen, String stepName, String details)
    {
        Media m = ScreenCapture.builder().base64(GetScreenShot.captureAsBase64(screen)).title(stepName).build();

        reportThreadLocal.get().log(Status.FAIL, stepName + "\n" + details, m);
    }

    private static void populateAPITestStep(String stepName, ExtentTest section, Status status, RequestSpecification rawReq, Response rsp)
    {
        ExtentTest apiDetailsSection = section.createNode("API Details - " + stepName);

        if (rawReq != null) {
            ExtentTest reqDetailsSection = apiDetailsSection.createNode("API Request", "Details of the API Request");

            FilterableRequestSpecification req = (FilterableRequestSpecification) rawReq;

            String markupText = "";
            String reqType = req.getMethod();
            String url = req.getURI();

            // Request URL
            markupText += MarkupHelper.createLabel("Request - " + reqType + " " + url, ExtentColor.GREY).getMarkup() + "<br><br>";

            // Headers
            if (req.getHeaders() != null) {
                ArrayList<List<String>> mainList = new ArrayList<List<String>>();
                mainList.add(Arrays.asList("Header", "Value"));
                for (Header h : req.getHeaders().asList()) {
                    mainList.add(Arrays.asList(h.getName(), h.getValue()));
                }
                String[][] stringArray = mainList.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);

                markupText += MarkupHelper.createLabel("Headers", ExtentColor.GREY).getMarkup() + "<br>";
                markupText += MarkupHelper.createTable(stringArray).getMarkup();
            }

            // Get body
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            if (req.getBody() != null) {
                markupText += MarkupHelper.createLabel("JSON Body", ExtentColor.GREY).getMarkup() + "<br>";
                markupText += MarkupHelper.createCodeBlock(req.getBody().toString()).getMarkup() + "<br><br>";
            }

            // Get form params
            if (req.getFormParams() != null && req.getFormParams().size() > 0) {
                try {
                    markupText += MarkupHelper.createLabel("Form Parameters", ExtentColor.GREY).getMarkup() + "<br>";

                    markupText += MarkupHelper.createCodeBlock(ow.writeValueAsString(req.getFormParams())).getMarkup() + "<br><br>";
                } catch (Exception e) {}
            }

            // Multi part form params
            if (req.getMultiPartParams() != null && req.getMultiPartParams().size() > 0) {
                Map<String, String> multiFormParams = new HashMap<>();
                for (MultiPartSpecification spec : req.getMultiPartParams()) {
                    multiFormParams.put(spec.getControlName(), spec.getContent().toString());
                }

                try {
                    markupText += MarkupHelper.createLabel("Multi-Form Parameters", ExtentColor.GREY).getMarkup() + "<br>";
                    markupText += MarkupHelper.createCodeBlock(ow.writeValueAsString(multiFormParams)).getMarkup();
                } catch (Exception e) {}
            }

            reqDetailsSection.log(status, markupText);

        }

        if (rsp != null) {
            ExtentTest rspDetailsSection = apiDetailsSection.createNode("API Response", "Details of the API Response");

            String markupText = "";
            markupText += MarkupHelper.createLabel("Status Code: " + rsp.getStatusCode(), ExtentColor.GREY).getMarkup() + "<br>";
            markupText += MarkupHelper.createLabel("Status Line: " + rsp.getStatusLine(), ExtentColor.GREY).getMarkup() + "<br>";
            markupText += MarkupHelper.createLabel("Session ID: " + rsp.getStatusLine(), ExtentColor.GREY).getMarkup() + "<br>";
            markupText += MarkupHelper.createLabel("Context Type: " + rsp.getContentType(), ExtentColor.GREY).getMarkup() + "<br>";
            markupText += MarkupHelper.createLabel("Duration: " + rsp.getTime(), ExtentColor.GREY).getMarkup() + "<br><br>";

            // Headers
            if (rsp.getHeaders() != null) {
                ArrayList<List<String>> mainList = new ArrayList<List<String>>();
                mainList.add(Arrays.asList("Header", "Value"));
                for (Header h : rsp.getHeaders().asList()) {
                    mainList.add(Arrays.asList(h.getName(), h.getValue()));
                }
                String[][] stringArray = mainList.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);

                markupText += MarkupHelper.createLabel("Headers", ExtentColor.GREY).getMarkup() + "<br>";
                markupText += MarkupHelper.createTable(stringArray).getMarkup();
            }

            if (rsp.getBody() != null) {
                if (rsp.getContentType().toLowerCase().contains("application/json")) {
                    markupText += MarkupHelper.createLabel("Body", ExtentColor.GREY).getMarkup() + "<br>";
                    markupText += MarkupHelper.createCodeBlock(rsp.getBody().asPrettyString()).getMarkup() + "<br><br>";
                } else if (rsp.getBody().asString() != null && rsp.getBody().asString().length() < 100000) {
                    markupText += MarkupHelper.createLabel("Body", ExtentColor.GREY).getMarkup() + "<br>";
                    markupText += MarkupHelper.createCodeBlock(rsp.getBody().asString()).getMarkup() + "<br><br>";
                }
            }

            rspDetailsSection.log(status, markupText);
        }
    }
}
