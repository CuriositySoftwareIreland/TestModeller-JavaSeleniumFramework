package pages;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.jayway.jsonpath.JsonPath;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.response.Response;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.openqa.selenium.WebDriver;
import utilities.testmodeller.JSONTemplateEvaluator;
import utilities.testmodeller.ModellerCookieFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APIGeneralActions extends BasePage {
    public static ThreadLocal<ModellerCookieFilter> CookieFilterTL = new ThreadLocal<ModellerCookieFilter>();

    public static ThreadLocal<Boolean> CookieStoreEnabledTL = new ThreadLocal<Boolean>();

    @TestModellerIgnore
    public APIGeneralActions(WebDriver driver) {
        super(driver);
    }

    public static ModellerCookieFilter CookieFilter()
    {
        if (CookieFilterTL.get() == null)
            CookieFilterTL.set(new ModellerCookieFilter());

        return CookieFilterTL.get();
    }

    public static Boolean CookieStoreEnabled() {
        if (CookieStoreEnabledTL.get() == null)
            CookieStoreEnabledTL.set(true);

        return CookieStoreEnabledTL.get();
    }

    /**
     * @name Extract Value by JSON Path
     */
    public String ExtractValueByJSONPath(Response rsp, String jsonPath) {
        try {
            String value = JsonPath.read(rsp.getBody().asString(), jsonPath).toString();
            passStep(rsp, "Extract Value by JSON Path", "Extracted value '" + value + "'");

            return value;
        } catch (Exception e) {
            failStep(rsp, "Extract Value by JSON Path", "Unable to extract json path '" + jsonPath + "' from response " + rsp.getBody().asString() + ". " + e.getMessage());
        }

        return "";
    }

    /**
     * @name Extract Value by XPath
     */
    public String ExtractValueByXMLPath(Response rsp, String xmlPath) {
        return rsp.body().xmlPath().get(xmlPath);
    }

    /**
     * @name Extract Cookie Value by Name
     */
    public String ExtractCookieValue(Response rsp, String name) {
        return rsp.getDetailedCookie(name).getValue();
    }

    /**
     * @name Extract Status Code
     */
    public int extractStatusCode(Response rsp) {
        return rsp.getStatusCode();
    }

    /**
     * @name Extract Header Value
     */
    public String extractHeaderValue(Response rsp, String headerName) {
        return rsp.getHeader(headerName);
    }

    /**
     * @name Extract Response Time
     */
    public long extractResponseTime(Response rsp) {
        return rsp.time();
    }

    /**
     * @name Extract Content Type
     */
    public String extractContentType(Response rsp) {
        return rsp.getContentType();
    }

    /**
     * @name Assert Status Code
     */
    public void AssertStatusCode(Response rsp, int statusCode) {
        if (rsp.getStatusCode() != statusCode) {
            failStep(rsp, "Assert Status Code", "Status code invalid - expected " + statusCode + " - found " + rsp.getStatusCode());
        } else {
            passStep(rsp, "Assert Status Code", "Status code '" + statusCode + "'");
        }
    }

    /**
     * @name Assert JSON Path
     */
    public void AssertJsonPath(Response rsp, String jsonPath, String value) {
        try {
            if (!(JsonPath.read(rsp.getBody().asString(), jsonPath).toString().equals(value))) {
                failStep(rsp, "Assert JSON Path", "Body didn't contain value '" + value + "' at path '" + jsonPath + "'");
            } else {
                passStep(rsp, "Assert JSON Path", "Body contained value '" + value + "' at path '" + jsonPath + "'");
            }
        } catch (Exception e) {
            failStep(rsp, "Assert JSON Path", "Unable to extract json path '" + jsonPath + "' from response " + rsp.getBody().asString() + ". " + e.getMessage());
        }
    }

    /**
     * @name Assert Response Contains Header
     */
    public void AssertHeaderContains(Response rsp, String headerKey) {
        if (rsp.getHeaders().get(headerKey) == null) {
            failStep(rsp, "Assert Response Contains Header", "Response does not contain a header '" + headerKey + "'.");
        } else {
            passStep(rsp, "Assert Response Contains Header", "Response contains header '" + headerKey + "'");
        }
    }

    /**
     * @name Assert Response Contains Header with Value
     */
    public void AssertHeaderValue(Response rsp, String headerKey, String headerValue) {
        if (rsp.getHeaders().get(headerKey) == null) {
            failStep(rsp, "Assert Response Contains Header with Value", "Response does not contain a header '" + headerKey + "'.");
        } else {
            String curVal = rsp.getHeaders().get(headerKey).getValue();
            if (curVal.equals(headerValue)) {
                passStep(rsp, "Assert Response Contains Header with Value", "API Request header value for '" + headerKey + "' is '" + curVal + "'.");
            } else {
                failStep(rsp, "Assert Response Contains Header with Value", "The API Request header value for '" + headerKey + "' is '" + curVal + "'. Expected '" + headerValue + "'.");
            }
        }
    }

    /**
     * @name Assert Response JSON Path Exists
     */
    public void AssertJSONPathExists(Response rsp, String jsonPath) {
        try {
            if (!(JsonPath.read(rsp.getBody().asString(), jsonPath) != null)) {
                failStep(rsp, "Assert Response JSON Path Exists", "Body does not contain the JSON path '" + jsonPath + "'.");
            } else {
                passStep(rsp, "Assert Response JSON Path Exists", "Body contains the JSON path '" + jsonPath + "'.");
            }
        } catch (Exception e) {
            failStep(rsp, "Assert Response JSON Path Exists", "Unable to extract json path '" + jsonPath + "' from response " + rsp.getBody().asString() + ". " + e.getMessage());
        }
    }

    /**
     * @name Assert Response JSON Path Not Exists
     */
    public void AssertJSONPathNotExists(Response rsp, String jsonPath) {
        try {
            if ((JsonPath.read(rsp.getBody().asString(), jsonPath) == null)) {
                passStep(rsp, "Assert Response JSON Path Exists", "Body does not contain the JSON path '" + jsonPath + "'.");
            } else {
                failStep(rsp, "Assert Response JSON Path Exists", "Body contains the JSON path '" + jsonPath + "'.");
            }
        } catch (Exception e) {
            passStep(rsp, "Assert Response JSON Path Exists", "Unable to extract json path '" + jsonPath + "' from response " + rsp.getBody().asString() + ". " + e.getMessage());
        }
    }

    /**
     * @name Assert JSON Data
     */
    public void AssertJSONData(Response rsp) {
        try {
            new JSONObject(rsp.getBody().asString());

            passStep(rsp, "Assert JSON Data", "The JSON data is valid.");
        } catch (Exception ex) {
            try {
                new JSONArray(rsp.getBody().asString());

                passStep(rsp, "Assert JSON Data", "The JSON data is valid.");
            } catch (Exception ex1) {
                failStep(rsp, "Assert JSON Data", "An exception occured while parsing the JSON result: " + ex1.getMessage());
            }
        }
    }

    /**
     * @name Enable Cookie Store
     */
    public void EnableCookieStore()
    {
        CookieStoreEnabledTL.set(true);
    }

    /**
     * @name Disable Cookie Store
     */
    public void DisableCookieStore()
    {
        CookieStoreEnabledTL.set(false);
    }

    /**
     * @name Clear Cookie Store
     */
    public void ClearCookieStore()
    {
        CookieFilterTL.set(new ModellerCookieFilter());
    }

    /**
     * @name Evaluate Message Template
     */
    public String EvaluateMessageTemplate(String msg) {
        return JSONTemplateEvaluator.EvaluateMessageTemplate(msg);
    }
}
