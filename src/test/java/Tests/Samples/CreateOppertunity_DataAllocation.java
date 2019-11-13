package Tests.Samples;

import Pages.Samples.Splendid.LoginPage;
import Pages.Samples.Splendid.OpportunitiesPage;
import Tests.TestBase;
import Utilities.TestModeller.TestNGListener;
import ie.curiositysoftware.DataAllocation.Engine.DataAllocation;
import ie.curiositysoftware.DataAllocation.Entities.DataAllocationResult;
import ie.curiositysoftware.RunResult.Entities.UseTestModellerId;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestNGListener.class)
public class CreateOppertunity_DataAllocation extends TestBase
{
    @Test(groups= {"9fb5fbbe-b5ad-4af6-b812-3df6c22e2c92","Create Oppertunity","Create Oppertunity - Default Profile"})
    @UseTestModellerId(testModellerGuid = "9fb5fbbe-b5ad-4af6-b812-3df6c22e2c92")
    @DataAllocation(poolName = "SplendidUAT", suiteName = "Create Oppertunity", groups = {"Default Profile_GoToUrl_PositiveName_PositiveAccountName_NegativeAmount_Save1::*"})
    public void DefaultProfileGoToUrlPositiveNamePositiveAccountNameNegativeAmountSave1()
    {
        DataAllocationResult CreateOppertunity_AccountName =  dataAllocationEngine.GetDataResult("SplendidUAT", "Create Oppertunity", "Default Profile_GoToUrl_PositiveName_PositiveAccountName_NegativeAmount_Save1:::Create Oppertunity_AccountName");

        LoginPage _LoginPage = new LoginPage(driver);
        _LoginPage.GoToUrl();

        _LoginPage.Enter_UsernameInput("admin");

        _LoginPage.Enter_PasswordInput("admin");

        _LoginPage.Click_LoginButton();

        OpportunitiesPage _PagesOpportunities = new OpportunitiesPage(driver);
        _PagesOpportunities.GoToUrl();

        _PagesOpportunities.Enter_Name("omnis");

        _PagesOpportunities.Enter_AccountName(CreateOppertunity_AccountName.GetValueByColumnIndex(0).toString());

        _PagesOpportunities.Enter_Amount("100.2.22");

        _PagesOpportunities.Click__Save_();

    }
}
