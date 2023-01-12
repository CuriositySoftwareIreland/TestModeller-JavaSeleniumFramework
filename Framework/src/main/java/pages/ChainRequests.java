package pages;

import pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ie.curiositysoftware.testmodeller.TestModellerModule;
import utilities.reports.ExtentReportManager;
import utilities.testmodeller.TestModellerLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.JavascriptExecutor;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jayway.jsonpath.JsonPath;

// http://dev.testinsights.io/app/#!/module-collection/guid/4bc14522-6f49-4ef4-a568-bec65b98524e
@TestModellerModule(guid = "4bc14522-6f49-4ef4-a568-bec65b98524e")
public class ChainRequests extends BasePage
{
	public ChainRequests (WebDriver driver)
	{
		super(driver);
	}




	
	public void health(String authToken)
	{
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.contentType(ContentType.JSON);
        
        httpRequest = httpRequest.header("Content-Type", "application/json");
        httpRequest = httpRequest.header("Authorization", "Bearer " + authToken + "");
        
        Response response = httpRequest.get("https://localhost:80/api/health");
        printResponse(response);

        
 		ExtentReportManager.passStep(response, "health");
 		TestModellerLogger.PassResponseStep(response, "health");
	}
	public String login(String email,String password)
	{
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.contentType(ContentType.JSON);
        
        httpRequest = httpRequest.header("Content-Type", "application/json");
        
        
        httpRequest.body("{" + 
        "\t\"email\": \"" + email + "\"," + 
        "\t\"password\": \"" + password + "\"" + 
        "}");
        Response response = httpRequest.post("https://localhost:80/api/login");
        
        if (!(response.getStatusCode() == 200)) {
            failStep("login","Status code invalid - expected 200 - found " + response.getStatusCode());
        }

 		ExtentReportManager.passStep(response, "login");
 		TestModellerLogger.PassResponseStep(response, "login");
        
        return JsonPath.read(response.getBody().asString(), "$.access_string").toString();
	}}