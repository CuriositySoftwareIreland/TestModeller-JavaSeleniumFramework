package pages.bases;

import com.jayway.jsonpath.JsonPath;
import ie.curiositysoftware.pageobjects.Identifier.ObjectIdentifier;
import ie.curiositysoftware.pageobjects.dto.PageObjectParameterEntity;
import ie.curiositysoftware.runresult.dto.TestPathRunStep;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import io.restassured.response.Response;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

import java.util.HashMap;

@TestModellerIgnore
public class APIGeneralInternalActions extends BasePage {
    public APIGeneralInternalActions(WebDriver driver)
    {
        super(driver);
    }


    /**
     * @name Assert JSON Path
     */
    public void AssertJsonPath(TestPathRunStep step, Response rsp, String jsonPath, String value) {
        try {
            if (!(JsonPath.read(rsp.getBody().asString(), jsonPath).toString().equals(value))) {
                step.addFailStep("Assert JSON Path", "Body didn't contain value '" + value + "' at path '" + jsonPath + "'");
            } else {
                step.addPassStep("Assert JSON Path", "Body contained value '" + value + "' at path '" + jsonPath + "'");
            }
        } catch (Exception e) {
            step.addFailStep("Assert JSON Path", "Unable to extract json path '" + jsonPath + "' from response " + rsp.getBody().asString() + ". " + e.getMessage());
        }
    }

    /**
     * @name Assert Response Contains Header
     */
    public void AssertHeaderContains(TestPathRunStep step, Response rsp, String headerKey) {
        if (rsp.getHeaders().get(headerKey) == null) {
            step.addFailStep("Assert Response Contains Header", "Response does not contain a header '" + headerKey + "'.");
        } else {
            step.addPassStep("Assert Response Contains Header", "Response contains header '" + headerKey + "'");
        }
    }

    /**
     * @name Assert Response Contains Header with Value
     */
    public void AssertHeaderValue(TestPathRunStep step, Response rsp, String headerKey, String headerValue) {
        if (rsp.getHeaders().get(headerKey) == null) {
            step.addFailStep("Assert Response Contains Header with Value", "Response does not contain a header '" + headerKey + "'.");
        } else {
            String curVal = rsp.getHeaders().get(headerKey).getValue();
            if (curVal.equals(headerValue)) {
                step.addPassStep("Assert Response Contains Header with Value", "API Request header value for '" + headerKey + "' is '" + curVal + "'.");
            } else {
                step.addFailStep("Assert Response Contains Header with Value", "The API Request header value for '" + headerKey + "' is '" + curVal + "'. Expected '" + headerValue + "'.");
            }
        }
    }

    /**
     * @name Assert Response JSON Path Exists
     */
    public void AssertJSONPathExists(TestPathRunStep step, Response rsp, String jsonPath) {
        try {
            if (!(JsonPath.read(rsp.getBody().asString(), jsonPath) != null)) {
                step.addFailStep("Assert Response JSON Path Exists", "Body does not contain the JSON path '" + jsonPath + "'.");
            } else {
                step.addPassStep("Assert Response JSON Path Exists", "Body contains the JSON path '" + jsonPath + "'.");
            }
        } catch (Exception e) {
            step.addFailStep( "Assert Response JSON Path Exists", "Unable to extract json path '" + jsonPath + "' from response " + rsp.getBody().asString() + ". " + e.getMessage());
        }
    }

    /**
     * @name Assert Response JSON Path Not Exists
     */
    public void AssertJSONPathNotExists(TestPathRunStep step, Response rsp, String jsonPath) {
        try {
            if ((JsonPath.read(rsp.getBody().asString(), jsonPath) == null)) {
                step.addPassStep("Assert Response JSON Path Exists", "Body does not contain the JSON path '" + jsonPath + "'.");
            } else {
                step.addFailStep("Assert Response JSON Path Exists", "Body contains the JSON path '" + jsonPath + "'.");
            }
        } catch (Exception e) {
            step.addPassStep( "Assert Response JSON Path Exists", "Unable to extract json path '" + jsonPath + "' from response " + rsp.getBody().asString() + ". " + e.getMessage());
        }
    }

    /**
     * @name Assert JSON Data
     */
    public void AssertJSONData(TestPathRunStep step, Response rsp) {
        try {
            new JSONObject(rsp.getBody().asString());

            step.addPassStep( "Assert JSON Data", "The JSON data is valid.");
        } catch (Exception ex) {
            try {
                new JSONArray(rsp.getBody().asString());

                step.addPassStep("Assert JSON Data", "The JSON data is valid.");
            } catch (Exception ex1) {
                step.addFailStep("Assert JSON Data", "An exception occured while parsing the JSON result: " + ex1.getMessage());
            }
        }
    }
}
