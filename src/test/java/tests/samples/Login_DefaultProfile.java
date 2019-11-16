package tests.samples;

import pages.samples.magento.CustomerLoginPage;
import tests.TestBase;
import utilities.testmodeller.TestNGListener;
import ie.curiositysoftware.testmodeller.TestModellerPath;
import ie.curiositysoftware.testmodeller.TestModellerSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestNGListener.class)
@TestModellerSuite(id = 100142, profileId = 100042)
public class Login_DefaultProfile extends TestBase
{
	@Test  (groups= {"6e672af7-7b79-4cde-9a03-975446b64a2e","Login","Login - Default Profile"})
	@TestModellerPath(guid = "6e672af7-7b79-4cde-9a03-975446b64a2e")
	public void DefaultProfileLoginPageUnauthenticated1()
	{
		CustomerLoginPage _PagesCustomerAuthenticationCustomerLoginPage = new CustomerLoginPage(driver);
			_PagesCustomerAuthenticationCustomerLoginPage.GoToUrl();


			_PagesCustomerAuthenticationCustomerLoginPage.EnterEmail("6MC3g81kL3");

			_PagesCustomerAuthenticationCustomerLoginPage.EnterPassword("5qpCcDmP,exk");

			_PagesCustomerAuthenticationCustomerLoginPage.ClickSigninButton();

			_PagesCustomerAuthenticationCustomerLoginPage.AssertsLoginError();
	}

	@Test  (groups= {"3e955b5a-7cff-4397-95ee-e22e1b0aa7f1","Login","Login - Default Profile"})
	@TestModellerPath(guid = "3e955b5a-7cff-4397-95ee-e22e1b0aa7f1")
	public void DefaultProfileLoginPageUnauthenticated2()
	{
		CustomerLoginPage _PagesCustomerAuthenticationCustomerLoginPage = new CustomerLoginPage(driver);
			_PagesCustomerAuthenticationCustomerLoginPage.GoToUrl();


			_PagesCustomerAuthenticationCustomerLoginPage.EnterEmail("U8EzkC1kNl");

			_PagesCustomerAuthenticationCustomerLoginPage.EnterPassword("roni_cost3@example.com");

			_PagesCustomerAuthenticationCustomerLoginPage.ClickSigninButton();

			_PagesCustomerAuthenticationCustomerLoginPage.AssertsLoginError();
	}

	@Test  (groups= {"bab14917-9382-44d2-ba44-b8a8e9c50ca6","Login","Login - Default Profile"})
	@TestModellerPath(guid = "bab14917-9382-44d2-ba44-b8a8e9c50ca6")
	public void DefaultProfileLoginPageUnauthenticated3()
	{
		CustomerLoginPage _PagesCustomerAuthenticationCustomerLoginPage = new CustomerLoginPage(driver);
			_PagesCustomerAuthenticationCustomerLoginPage.GoToUrl();


			_PagesCustomerAuthenticationCustomerLoginPage.EnterEmail("roni_cost@example.com");

			_PagesCustomerAuthenticationCustomerLoginPage.EnterPassword("XUz,wG.@&lt;fDP");

			_PagesCustomerAuthenticationCustomerLoginPage.ClickSigninButton();

			_PagesCustomerAuthenticationCustomerLoginPage.AssertsLoginError();
	}

	@Test  (groups= {"0498f348-5acf-42fa-840b-ab90df7f2986","Login","Login - Default Profile"})
	@TestModellerPath(guid = "0498f348-5acf-42fa-840b-ab90df7f2986")
	public void DefaultProfileLoginPageAuthenticated4()
	{
		CustomerLoginPage _PagesCustomerAuthenticationCustomerLoginPage = new CustomerLoginPage(driver);
			_PagesCustomerAuthenticationCustomerLoginPage.GoToUrl();


			_PagesCustomerAuthenticationCustomerLoginPage.EnterEmail("roni_cost@example.com");

			_PagesCustomerAuthenticationCustomerLoginPage.EnterPassword("roni_cost3@example.com");

			_PagesCustomerAuthenticationCustomerLoginPage.ClickSigninButton();
	}
}
