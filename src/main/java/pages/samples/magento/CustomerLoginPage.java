package pages.samples.magento;

import ie.curiositysoftware.testmodeller.TestModellerModule;
import pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

@TestModellerModule
public class CustomerLoginPage extends BasePage
{
    // Login page URL
    String loginPageURL = "https://magento.nublue.co.uk/customer/account/login";

    // Email
    By EmailInput = By.xpath("//input[@id='email']");

    // Password
    By PassInput = By.xpath("//input[@title='Password']");

    // Signin button
    By SigninButton = By.xpath("//button[@id=\"send2\"]");

    // Invalid email
    By EmailErrorMessage = By.xpath("//div[@id=\"email-error\"]");

    // Invalid login
    By InvalidLoginMessage = By.xpath("//div[text()=\"The account sign-in was incorrect or your account is disabled temporarily. Please wait and try again later.\"]");

    public CustomerLoginPage(WebDriver driver)
    {
        super(driver);
    }

    public void GoToUrl()
    {
        m_Driver.get(loginPageURL);
    }

    public void EnterEmail(String email)
    {
        WebElement emailInputElemen = getWebElement(EmailInput);

        emailInputElemen.sendKeys(email);
    }

    public void EnterPassword(String password)
    {
        WebElement passwordInputElement = getWebElement(PassInput);

        passwordInputElement.sendKeys(password);
    }

    public void ClickSigninButton()
    {
        WebElement signinButtonElement = getWebElement(SigninButton);

        signinButtonElement.click();
    }

    public void AssertsLoginError()
    {
        WebElement EmailErrorMessageElement = getWebElement(EmailErrorMessage);

        WebElement InvalidLoginMessageElement = getWebElement(InvalidLoginMessage);

        Assert.assertTrue(EmailErrorMessageElement != null || InvalidLoginMessageElement != null);
    }
}
