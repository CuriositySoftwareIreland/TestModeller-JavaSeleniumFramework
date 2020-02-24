package pages.samples.splendid;

import ie.curiositysoftware.testmodeller.TestModellerModule;
import pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

@TestModellerModule
public class OpportunitiesPage  extends BasePage {

    public OpportunitiesPage(WebDriver driver) {
        super(driver);
    }

    private By NameElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$NAME']");

    private By AccountNameElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$ACCOUNT_NAME']");

    private By OpportunityTypeElem = By.xpath("//SELECT[@name='ctl00$cntBody$ctlEditView$OPPORTUNITY_TYPE']");

    private By AssignedToNameElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$ASSIGNED_TO_NAME']");

    private By LeadSourceElem = By.xpath("//SELECT[@name='ctl00$cntBody$ctlEditView$LEAD_SOURCE']");

    private By DescriptionElem = By.xpath("//TEXTAREA[@name='ctl00$cntBody$ctlEditView$DESCRIPTION']");

    private By CurrencyIDElem = By.xpath("//SELECT[@name='ctl00$cntBody$ctlEditView$CURRENCY_ID']");

    private By AmountElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$AMOUNT']");

    private By DateClosedElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$DATE_CLOSED$txtDATE']");

    private By NextStepElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$NEXT_STEP']");

    private By ProbabilityElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$PROBABILITY']");

    private By SalesStageElem = By.xpath("//SELECT[@name='ctl00$cntBody$ctlEditView$SALES_STAGE']");

    private By CampaignNameElem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$CAMPAIGN_NAME']");

    private By _Save_Elem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$ctlFooterButtons$btnSAVE']");

    private By _Cancel_Elem = By.xpath("//INPUT[@name='ctl00$cntBody$ctlEditView$ctlFooterButtons$btnCANCEL']");

    private String pageURL = "http://localhost:81/SplendidCRM/Opportunities/edit.aspx";

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
     * @param Name [name]
     * @name Name
     */
    public void Enter_Name(String Name) {
        WebElement elem = getWebElement(NameElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + NameElem.toString());
        }
        elem.sendKeys(Name);
    }

    /**
     * @param AccountName [name]
     * @name AccountName
     */
    public void Enter_AccountName(String AccountName) {
        WebElement elem = getWebElement(AccountNameElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + AccountNameElem.toString());
        }
        elem.sendKeys(AccountName);
    }

    /**
     * @param OpportunityType [select(["--None--","Existing business","New business"])]
     * @name OpportunityType
     */
    public void Select_OpportunityType(String OpportunityType) {
        WebElement elem = getWebElement(OpportunityTypeElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + OpportunityTypeElem.toString());
        }
        Select dropdown = new Select(elem);
        dropdown.selectByVisibleText(OpportunityType);
    }

    /**
     * @param AssignedToName [name]
     * @name AssignedToName
     */
    public void Enter_AssignedToName(String AssignedToName) {
        WebElement elem = getWebElement(AssignedToNameElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + AssignedToNameElem.toString());
        }
        elem.sendKeys(AssignedToName);
    }

    /**
     * @param LeadSource [select(["--None--","Cold call","Existing customer","Self generated","Employee","Partner","Public relations","Direct mail","Conference","Trade show","Web site","Word of mouth","Email","Campaign","Other"])]
     * @name LeadSource
     */
    public void Select_LeadSource(String LeadSource) {
        WebElement elem = getWebElement(LeadSourceElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + LeadSourceElem.toString());
        }
        Select dropdown = new Select(elem);
        dropdown.selectByVisibleText(LeadSource);
    }

    /**
     * @param Description [description]
     * @name Description
     */
    public void Enter_Description(String Description) {
        WebElement elem = getWebElement(DescriptionElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + DescriptionElem.toString());
        }
        elem.sendKeys(Description);
    }

    /**
     * @param CurrencyID [select(["--None--","U.S. Dollar: $"])]
     * @name CurrencyID
     */
    public void Select_CurrencyID(String CurrencyID) {
        WebElement elem = getWebElement(CurrencyIDElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + CurrencyIDElem.toString());
        }
        Select dropdown = new Select(elem);
        dropdown.selectByVisibleText(CurrencyID);
    }

    /**
     * @param Amount [amount]
     * @name Amount
     */
    public void Enter_Amount(String Amount) {
        WebElement elem = getWebElement(AmountElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + AmountElem.toString());
        }
        elem.sendKeys(Amount);
    }

    /**
     * @param DateClosed [date]
     * @name DateClosed
     */
    public void Enter_DateClosed(String DateClosed) {
        WebElement elem = getWebElement(DateClosedElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + DateClosedElem.toString());
        }
        elem.sendKeys(DateClosed);
    }

    /**
     * @param NextStep [text]
     * @name NextStep
     */
    public void Enter_NextStep(String NextStep) {
        WebElement elem = getWebElement(NextStepElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + NextStepElem.toString());
        }
        elem.sendKeys(NextStep);
    }

    /**
     * @param Probability [probability]
     * @name Probability
     */
    public void Enter_Probability(String Probability) {
        WebElement elem = getWebElement(ProbabilityElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + ProbabilityElem.toString());
        }
        elem.sendKeys(Probability);
    }

    /**
     * @param SalesStage [select(["--None--","Prospecting","Qualification","Needs analysis","Value proposition","Id. decision makers","Perception analysis","Proposal/price quote","Negotiation/review","Closed won","Closed lost","Other"])]
     * @name SalesStage
     */
    public void Select_SalesStage(String SalesStage) {
        WebElement elem = getWebElement(SalesStageElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + SalesStageElem.toString());
        }
        Select dropdown = new Select(elem);
        dropdown.selectByVisibleText(SalesStage);
    }

    /**
     * @param CampaignName [name]
     * @name CampaignName
     */
    public void Enter_CampaignName(String CampaignName) {
        WebElement elem = getWebElement(CampaignNameElem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + CampaignNameElem.toString());
        }
        elem.sendKeys(CampaignName);
    }

    /**
     * @name Save
     */
    public void Click__Save_() {
        WebElement elem = getWebElement(_Save_Elem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + _Save_Elem.toString());
        }
        elem.click();
    }

    /**
     * @name Cancel
     */
    public void Click__Cancel_() {
        WebElement elem = getWebElement(_Cancel_Elem);
        if (elem == null) {
            Assert.fail("Unable to locate object: " + _Cancel_Elem.toString());
        }
        elem.click();
    }
}
