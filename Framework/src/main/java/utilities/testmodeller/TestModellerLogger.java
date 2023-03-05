package utilities.testmodeller;

import com.aventstack.extentreports.ExtentTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.dto.TestPathRunStep;
import ie.curiositysoftware.runresult.dto.TestPathRunStepHTTPRequest;
import ie.curiositysoftware.runresult.dto.TestPathRunStepHTTPResponse;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        populateAPITestStep(runStep, null, rsp);

        return runStep;
    }

    public static TestPathRunStep FailResponseStep(Response rsp, String stepName)
    {
        TestPathRunStep runStep = FailStep(stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                "Status: " + rsp.getStatusLine());

        // Setup
        populateAPITestStep(runStep, null, rsp);

        return runStep;
    }

    public static TestPathRunStep PassResponseStep(RequestSpecification req, Response rsp, String stepName)
    {
        TestPathRunStep runStep = PassStep(null, stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                        "Status: " + rsp.getStatusLine());

        populateAPITestStep(runStep, req, rsp);

        return runStep;
    }

    public static TestPathRunStep FailResponseStep(RequestSpecification req, Response rsp, String stepName)
    {
        TestPathRunStep runStep = FailStep(stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                        "Status: " + rsp.getStatusLine());

        // Setup
        populateAPITestStep(runStep, req, rsp);

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

    private static void populateAPITestStep(TestPathRunStep runStep, RequestSpecification rawReq, Response rsp)
    {
        runStep.setStepType(TestPathRunStep.TestPathRunStepType.APIStep);

        if (rawReq != null) {
            FilterableRequestSpecification req = (FilterableRequestSpecification) rawReq;

            TestPathRunStepHTTPRequest httpRequest = new TestPathRunStepHTTPRequest();
            httpRequest.setEndpoint(req.getURI());

            if (req.getMethod() != null) {
                if (req.getMethod().toLowerCase().equals("get")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Get);
                } else if (req.getMethod().toLowerCase().equals("post")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Post);
                } else if (req.getMethod().toLowerCase().equals("put")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Put);
                } else if (req.getMethod().toLowerCase().equals("patch")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Patch);
                } else if (req.getMethod().toLowerCase().equals("delete")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Delete);
                } else if (req.getMethod().toLowerCase().equals("options")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Options);
                } else if (req.getMethod().toLowerCase().equals("head")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Head);
                } else if (req.getMethod().toLowerCase().equals("copy")) {
                    httpRequest.setRequestType(TestPathRunStepHTTPRequest.RequestType.Copy);
                }
            }

            // Get body
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            if (req.getBody() != null) {
                httpRequest.setBodyType(TestPathRunStepHTTPRequest.BodyType.Raw);
                httpRequest.setBody(req.getBody().toString());
            }

            // Get form params
            if (req.getFormParams() != null && req.getFormParams().size() > 0) {
                httpRequest.setBodyType(TestPathRunStepHTTPRequest.BodyType.FormData);

                try {
                    httpRequest.setBody(ow.writeValueAsString(req.getFormParams()));
                } catch (Exception e) {}
            }

            // Multi part form params
            if (req.getMultiPartParams() != null && req.getMultiPartParams().size() > 0) {
                httpRequest.setBodyType(TestPathRunStepHTTPRequest.BodyType.XWWWFormURLEncoded);

                Map<String, String> multiFormParams = new HashMap<>();
                for (MultiPartSpecification spec : req.getMultiPartParams()) {
                    multiFormParams.put(spec.getControlName(), spec.getContent().toString());
                }

                try {
                    httpRequest.setBody(ow.writeValueAsString(multiFormParams));
                } catch (Exception e) {}
            }

            // Headers
            if (req.getHeaders() != null) {
                HashMap<String, String> headerParams = new HashMap<>();
                for (Header header : req.getHeaders().asList()) {
                    headerParams.put(header.getName(), header.getValue());
                }
                httpRequest.setHeaders(headerParams);
            }

            runStep.setHttpRequest(httpRequest);
        }

        if (rsp != null) {
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

            runStep.setHttpResponse(httpResponse);
        }
    }
}
