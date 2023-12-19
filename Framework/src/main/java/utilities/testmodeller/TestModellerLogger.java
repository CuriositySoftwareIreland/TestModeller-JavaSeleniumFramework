package utilities.testmodeller;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ie.curiositysoftware.runresult.dto.*;
import ie.curiositysoftware.runresult.services.TestRunService;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.Screen;
import utilities.PropertiesLoader;

import java.util.*;

public class TestModellerLogger {
    public static class ModellerContext {
        private String lastNodeGuid;

        private Long moduleColId;

        private Long moduleObjId;

        public Long getModuleColId() {
            return moduleColId;
        }

        public Long getModuleObjId() {
            return moduleObjId;
        }

        public void setModuleColId(Long moduleColId) {
            this.moduleColId = moduleColId;
        }

        public void setModuleObjId(Long moduleObjId) {
            this.moduleObjId = moduleObjId;
        }

        public String getLastNodeGuid() {
            return lastNodeGuid;
        }

        public void setLastNodeGuid(String lastNodeGuid) {
            this.lastNodeGuid = lastNodeGuid;
        }
    }

    public static final ThreadLocal<List<TestPathRunStep>> steps = new ThreadLocal<List<TestPathRunStep>>();

    public static final ThreadLocal<ModellerContext> LastNodeGuid = new ThreadLocal<ModellerContext>();

    public static final ThreadLocal<TestPathRun> CurrentRun = new ThreadLocal<TestPathRun>();

    public static HashMap<String, TestPathRun> TestRunMap = new HashMap<String, TestPathRun>();

    public static void LogMessage(String name, String description, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setTestStatus(status);
        if (LastNodeGuid.get() != null) {
            step.setNodeGuid(LastNodeGuid.get().getLastNodeGuid());
            step.setModuleColId(LastNodeGuid.get().getModuleColId());
            step.setModuleObjId(LastNodeGuid.get().getModuleObjId());
        }

        addStep(step);
    }

    public static void LogMessageWithScreenshot(String name, String description, byte[] image, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setImage(image);
        step.setTestStatus(status);
        if (LastNodeGuid.get() != null) {
            step.setNodeGuid(LastNodeGuid.get().getLastNodeGuid());
            step.setModuleColId(LastNodeGuid.get().getModuleColId());
            step.setModuleObjId(LastNodeGuid.get().getModuleObjId());
        }

        addStep(step);
    }

    public static TestPathRunStep FailStep(String stepName)
    {
        return FailStep(null, stepName);
    }

    public static TestPathRunStep FailStep(String stepName, String details)
    {
        TestPathRunStep step = createFailStep(stepName, details);

        addStep(step);

        return step;
    }

    private static TestPathRunStep createFailStep(String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setTestStatus(TestPathRunStatusEnum.Failed);
        if (LastNodeGuid.get() != null) {
            step.setNodeGuid(LastNodeGuid.get().getLastNodeGuid());
            step.setModuleColId(LastNodeGuid.get().getModuleColId());
            step.setModuleObjId(LastNodeGuid.get().getModuleObjId());
        }

        return step;
    }

    public static TestPathRunStep PassStep(WebDriver driver, String stepName)
    {
        TestPathRunStep step = createPassStep(driver, stepName, null);

        addStep(step);

        return step;
    }

    public static TestPathRunStep PassStep(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = createPassStep(driver, stepName, details);

        addStep(step);

        return step;
    }

    private static TestPathRunStep createPassStep(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setTestStatus(TestPathRunStatusEnum.Passed);
        if (LastNodeGuid.get() != null) {
            step.setNodeGuid(LastNodeGuid.get().getLastNodeGuid());
            step.setModuleColId(LastNodeGuid.get().getModuleColId());
            step.setModuleObjId(LastNodeGuid.get().getModuleObjId());
        }

        if (driver != null) {
            step.setPageSource(driver.getPageSource());
        }

        return step;
    }

    private static TestPathRunStep createPassStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = createPassStep(driver, stepName, details);

        step.setImage(GetScreenShot.captureAsByteArray(driver));

        return step;
    }
    public static TestPathRunStep PassResponseStep(Response rsp, String stepName)
    {
        TestPathRunStep runStep = createPassStep(null, stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                        "Status: " + rsp.getStatusLine());

        populateAPITestStep(runStep, null, rsp);

        addStep(runStep);

        return runStep;
    }

    public static TestPathRunStep CreateAPIStep(RequestSpecification req, Response rsp, String stepName)
    {
        TestPathRunStep runStep = createPassStep(null, stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                        "Status: " + rsp.getStatusLine());

        populateAPITestStep(runStep, req, rsp);

        return runStep;
    }

    public static TestPathRunStep FailResponseStep(Response rsp, String stepName)
    {
        TestPathRunStep runStep = createFailStep(stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                        "Status: " + rsp.getStatusLine());

        // Setup
        populateAPITestStep(runStep, null, rsp);

        addStep(runStep);

        return runStep;
    }

    public static TestPathRunStep PassResponseStep(RequestSpecification req, Response rsp, String stepName)
    {
        TestPathRunStep runStep = createPassStep(null, stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                        "Status: " + rsp.getStatusLine());

        populateAPITestStep(runStep, req, rsp);

        addStep(runStep);

        return runStep;
    }

    public static TestPathRunStep PassResponseStep(RequestSpecification req, Response rsp, String stepName, String desc)
    {
        TestPathRunStep runStep = createPassStep(null, stepName, desc);

        populateAPITestStep(runStep, req, rsp);

        addStep(runStep);

        return runStep;
    }

    public static TestPathRunStep FailResponseStep(RequestSpecification req, Response rsp, String stepName)
    {
        TestPathRunStep runStep = createFailStep(stepName,
                "Status Code: " + rsp.getStatusCode() + "\n" +
                        "Status: " + rsp.getStatusLine());

        // Setup
        populateAPITestStep(runStep, req, rsp);

        addStep(runStep);

        return runStep;
    }

    public static TestPathRunStep FailResponseStep(RequestSpecification req, Response rsp, String stepName, String desc)
    {
        TestPathRunStep runStep = createFailStep(stepName, desc);

        // Setup
        populateAPITestStep(runStep, req, rsp);

        addStep(runStep);

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
        TestPathRunStep step = createPassStepWithScreenshot(driver, stepName, details);

        addStep(step);

        return step;
    }

    public static TestPathRunStep PassStepWithScreenshot(Screen screen, String stepName)
    {
        return PassStepWithScreenshot(screen, stepName, stepName);
    }

    public static TestPathRunStep PassStepWithScreenshot(Screen screen, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setImage(GetScreenShot.captureAsByteArray(screen));
        step.setTestStatus(TestPathRunStatusEnum.Passed);
        if (LastNodeGuid.get() != null) {
            step.setNodeGuid(LastNodeGuid.get().getLastNodeGuid());
            step.setModuleColId(LastNodeGuid.get().getModuleColId());
            step.setModuleObjId(LastNodeGuid.get().getModuleObjId());
        }

        addStep(step);

        return step;
    }

    public static TestPathRunStep FailStepWithScreenshot(WebDriver driver, String stepName)
    {
        return FailStepWithScreenshot(driver, stepName, stepName);
    }

    public static TestPathRunStep FailStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = createFailStepWithScreenshot(driver, stepName, details);

        addStep(step);

        return step;
    }

    private static TestPathRunStep createFailStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setImage(GetScreenShot.captureAsByteArray(driver));
        step.setTestStatus(TestPathRunStatusEnum.Failed);
        if (LastNodeGuid.get() != null) {
            step.setNodeGuid(LastNodeGuid.get().getLastNodeGuid());
            step.setModuleColId(LastNodeGuid.get().getModuleColId());
            step.setModuleObjId(LastNodeGuid.get().getModuleObjId());
        }

        if (driver != null) {
            step.setPageSource(driver.getPageSource());
        }

        return step;
    }

    public static TestPathRunStep FailStepWithScreenshot(Screen screen, String stepName)
    {
        return FailStepWithScreenshot(screen, stepName, stepName);
    }

    public static TestPathRunStep FailStepWithScreenshot(Screen screen, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setImage(GetScreenShot.captureAsByteArray(screen));
        step.setTestStatus(TestPathRunStatusEnum.Failed);
        if (LastNodeGuid.get() != null) {
            step.setNodeGuid(LastNodeGuid.get().getLastNodeGuid());
            step.setModuleColId(LastNodeGuid.get().getModuleColId());
            step.setModuleObjId(LastNodeGuid.get().getModuleObjId());
        }

        addStep(step);

        return step;
    }

    public static void SetLastNodeGuid(String guid)
    {
        ModellerContext moc = new ModellerContext();
        moc.setLastNodeGuid(guid);

        LastNodeGuid.set(moc);
    }

    public static void SetModellerContext(String guid, Integer moduleColId, Integer moduleObjId)
    {
        ModellerContext moc = new ModellerContext();
        moc.setLastNodeGuid(guid);

        if (moduleColId != null)
            moc.setModuleColId(new Long (moduleColId));

        if (moduleObjId != null)
            moc.setModuleObjId(new Long (moduleObjId));

        LastNodeGuid.set(moc);
    }

    public static void ClearModellerContext()
    {
        LastNodeGuid.set(null);
    }

    public static void ClearMessages()
    {
        if (steps.get() != null)
            steps.get().clear();
    }

    public static void addStep(TestPathRunStep step)
    {
        if (steps.get() == null)
            steps.set(new ArrayList<>());

        TestPathRunStep savedStep = SaveStep(step);

        steps.get().add(savedStep);
    }

    private static TestPathRunStep SaveStep(TestPathRunStep step)
    {
        TestPathRun curRun = CurrentRun.get();

        if (curRun != null) {
            TestRunService runService1 = new TestRunService(PropertiesLoader.getConnectionProfile());

            step.setTestPathRun(curRun.getId());
            step.setRunTimeStamp(new Date());

            TestPathRunStep curRunStep = runService1.saveTestPathRunStep(step);

            return curRunStep;
        } else {
            return step;
        }
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
                    if (spec.getControlName().equals("file"))
                        continue;

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

            if (req.getCookies() != null) {
                HashMap<String, String> cookies = new HashMap<>();
                for (Cookie cookie : req.getCookies().asList()) {
                    cookies.put(cookie.getName(), cookie.getValue());
                }
                httpRequest.setCookies(cookies);
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

            if (rsp.getBody() != null) {
                if (rsp.getContentType().toLowerCase().contains("application/json")) {
                    httpResponse.setBody(rsp.getBody().asPrettyString());
                } else if (rsp.getBody().asString() != null && rsp.getBody().asString().length() < 100000) {
                    httpResponse.setBody(rsp.getBody().asString());
                }
            }

            if (rsp.getHeaders() != null) {
                HashMap<String, String> headers = new HashMap<String, String>();

                for (Header h : rsp.getHeaders().asList()) {
                    headers.put(h.getName(), h.getValue());
                }

                httpResponse.setHeaders(headers);
            }

            if (rsp.getCookies() != null) {
                httpResponse.setCookies(new HashMap<>(rsp.getCookies()));
            }


            runStep.setHttpResponse(httpResponse);
        }
    }
}
