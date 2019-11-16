package pages.samples.splendid;

import ie.curiositysoftware.testmodeller.TestModellerModule;
import pages.BasePage;
import utilities.reports.ExtentReportManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

@TestModellerModule
public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    private By UsernameInput = By.xpath("//INPUT[@name='ctl00$cntBody$ctlLoginView$txtUSER_NAME']");

    private By PasswordInput = By.xpath("//INPUT[@name='ctl00$cntBody$ctlLoginView$txtPASSWORD']");

    private By LoginButton = By.xpath("//INPUT[@name='ctl00$cntBody$ctlLoginView$btnLogin']");

    private By Invalid_User_Name_and_Password = By.xpath("//SPAN[text()='Invalid User Name and Password.']");

    private String pageURL = "http://localhost:81/SplendidCRM/Users/Login.aspx";

    public void GoToUrl() {
        m_Driver.get(pageURL);
    }

    public void AssertURL() {
        String currentUrl = m_Driver.getCurrentUrl();
        if (!currentUrl.equals(pageURL)) {
            Assert.fail("Expecting URL - " + pageURL + " Found " + currentUrl);
        }
    }

    /**
     * @name UsernameInput
     * @param value [username]
     */
    public void Enter_UsernameInput(String value) {
        WebElement elem = getWebElement(UsernameInput);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + UsernameInput.toString());
        }
        elem.sendKeys(value);
    }

    /**
     * @name PasswordInput
     * @param value [password]
     */
    public void Enter_PasswordInput(String value) {
        WebElement elem = getWebElement(PasswordInput);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + PasswordInput.toString());
        }
        elem.sendKeys(value);
    }

    /**
     * @name LoginButton
     */
    public void Click_LoginButton() {
        WebElement elem = getWebElement(LoginButton);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + LoginButton.toString());
        }

        ExtentReportManager.passStepWithScreenshot(m_Driver, "Click_LoginButton");

        elem.click();
    }

    /**
     * @name Invalid User Name and Password.
     */
    public void Assert_Invalid_User_Name_and_Password() {
        WebElement elem = getWebElement(Invalid_User_Name_and_Password);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + Invalid_User_Name_and_Password.toString());
        }
    }
}
