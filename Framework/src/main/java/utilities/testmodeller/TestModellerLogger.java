package utilities.testmodeller;

import com.aventstack.extentreports.ExtentTest;
import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.dto.TestPathRunStep;
import ie.curiositysoftware.runresult.dto.TestPathRunStepHTTPResponse;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestModellerLogger {
    public static final ThreadLocal<List<TestPathRunStep>> steps = new ThreadLocal<List<TestPathRunStep>>();

    public static final ThreadLocal<String> LastNodeGuid = new ThreadLocal<String>();

    public static void LogMessage(String name, String description, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setTestStatus(status);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static void LogMessageWithScreenshot(String name, String description, byte[] image, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setImage(image);
        step.setTestStatus(status);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static TestPathRunStep FailStep(String stepName)
    {
        return FailStep(null, stepName);
    }

    public static TestPathRunStep FailStep(String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setTestStatus(TestPathRunStatusEnum.Failed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);

        return step;
    }

    public static TestPathRunStep PassStep(WebDriver driver, String stepName)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setTestStatus(TestPathRunStatusEnum.Passed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);

        return step;
    }

    public static TestPathRunStep PassStep(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setTestStatus(TestPathRunStatusEnum.Passed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);

        return step;
    }

    public static TestPathRunStep PassResponseStep(Response rsp, String stepName)
    {
        TestPathRunStep runStep = PassStep(null, stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                "Status: " + rsp.getStatusLine());

        populateAPITestStep(runStep, rsp);

        return runStep;
    }

    public static TestPathRunStep FailResponseStep(Response rsp, String stepName)
    {
        TestPathRunStep runStep = FailStep(stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                "Status: " + rsp.getStatusLine());

        // Setup
        populateAPITestStep(runStep, rsp);

        return runStep;
    }

    public static TestPathRunStep PassStep(String stepName)
    {
        return PassStep(null, stepName);
    }

    public static TestPathRunStep PassStepWithScreenshot(WebDriver driver, String stepName)
    {
        return PassStepWithScreenshot(driver, stepName, stepName);
    }

    public static TestPathRunStep PassStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setImage(GetScreenShot.captureAsByteArray(driver));
        step.setTestStatus(TestPathRunStatusEnum.Passed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);

        return step;
    }

    public static TestPathRunStep FailStepWithScreenshot(WebDriver driver, String stepName)
    {
        return FailStepWithScreenshot(driver, stepName, stepName);
    }

    public static TestPathRunStep FailStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setImage(GetScreenShot.captureAsByteArray(driver));
        step.setTestStatus(TestPathRunStatusEnum.Failed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);

        return step;
    }

    public static void SetLastNodeGuid(String guid)
    {
        LastNodeGuid.set(guid);
    }

    public static void ClearMessages()
    {
        if (steps.get() != null)
            steps.get().clear();
    }

    private static void addStep(TestPathRunStep step)
    {
        if (steps.get() == null)
            steps.set(new ArrayList<>());

        steps.get().add(step);
    }

    private static void populateAPITestStep(TestPathRunStep runStep, Response rsp)
    {
        if (rsp == null)
            return;

        runStep.setStepType(TestPathRunStep.TestPathRunStepType.APIStep);

        // Create HTTP Response object
        TestPathRunStepHTTPResponse httpResponse = new TestPathRunStepHTTPResponse();
        httpResponse.setStatusCode(rsp.getStatusCode());
        httpResponse.setStatusText(rsp.getStatusLine());

        httpResponse.setSessionId(rsp.getSessionId());
        httpResponse.setContentType(rsp.getContentType());
        httpResponse.setTime(rsp.getTime());

        if (rsp.getBody() != null)
            httpResponse.setBody(rsp.getBody().prettyPrint());

        if (rsp.getHeaders() != null) {
            HashMap<String, String> headers = new HashMap<String, String>();

            for (Header h : rsp.getHeaders().asList()) {
                headers.put(h.getName(), h.getValue());
            }

            httpResponse.setHeaders(headers);
        }
    }
}
