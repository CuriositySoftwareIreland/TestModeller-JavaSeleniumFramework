package tests.samples;

import pages.samples.splendid.LoginPage;
import pages.samples.splendid.OpportunitiesPage;
import tests.TestBase;
import utilities.testmodeller.TestNGListener;
import ie.curiositysoftware.allocation.dto.DataAllocationResult;
import ie.curiositysoftware.allocation.engine.DataAllocation;
import ie.curiositysoftware.testmodeller.TestModellerPath;
import ie.curiositysoftware.testmodeller.TestModellerSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestNGListener.class)
@TestModellerSuite(id = 10, profileId = 100043)
public class CreateOppertunity_DataAllocation extends TestBase
{
    @Test(groups= {"9fb5fbbe-b5ad-4af6-b812-3df6c22e2c92","Create Oppertunity","Create Oppertunity - Default Profile"})
    @TestModellerPath(guid = "9fb5fbbe-b5ad-4af6-b812-3df6c22e2c92")
    @DataAllocation(poolName = "SplendidUAT", suiteName = "Create Oppertunity", groups = {"Default Profile_GoToUrl_PositiveName_PositiveAccountName_NegativeAmount_Save1::*"})
    public void DefaultProfileGoToUrlPositiveNamePositiveAccountNameNegativeAmountSave1()
    {
        DataAllocationResult CreateOppertunity_AccountName =  dataAllocationEngine.getDataResult("SplendidUAT", "Create Oppertunity", "Default Profile_GoToUrl_PositiveName_PositiveAccountName_NegativeAmount_Save1:::Create Oppertunity_AccountName");

        LoginPage _LoginPage = new LoginPage(driver);
        _LoginPage.GoToUrl();

        _LoginPage.Enter_UsernameInput("admin");

        _LoginPage.Enter_PasswordInput("admin");

        _LoginPage.Click_LoginButton();

        OpportunitiesPage _PagesOpportunities = new OpportunitiesPage(driver);
        _PagesOpportunities.GoToUrl();

        _PagesOpportunities.Enter_Name("omnis");

        _PagesOpportunities.Enter_AccountName(CreateOppertunity_AccountName.getValueByColumnIndex(0).toString());

        _PagesOpportunities.Enter_Amount("100.2.22");

        _PagesOpportunities.Click__Save_();

    }
}
