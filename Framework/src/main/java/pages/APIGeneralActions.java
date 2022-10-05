package pages;

import com.jayway.jsonpath.JsonPath;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;

public class APIGeneralActions extends BasePage {
    @TestModellerIgnore
    public APIGeneralActions(WebDriver driver) {
        super(driver);
    }

    /**
     * @name Extract Value by JSON Path
     */
    public String ExtractValueByJSONPath(Response rsp, String jsonPath)
    {
        return JsonPath.read(rsp.getBody().asString(), jsonPath).toString();
    }

    /**
     * @name Extract Value by XPath
     */
    public String ExtractValueByXMLPath(Response rsp, String xmlPath)
    {
        return rsp.body().xmlPath().get(xmlPath);
    }

    /**
     * @name Assert Status Code
     */
    public void AssertStatusCode(Response rsp, int statusCode)
    {
        if (rsp.getStatusCode() != statusCode) {
            failStep("Status code invalid - expected " + statusCode + " - found " + rsp.getStatusCode());
        }
    }

    /**
     * @name Assert JSON Path
     */
    public void AssertJsonPath(Response rsp, String jsonPath, String value)
    {
        if (!(JsonPath.read(rsp.getBody().asString(), jsonPath).toString().equals(value))) {
            failStep("Body didn't contain value '" + value + "' at path '" + jsonPath + "'");
        }
    }
}
