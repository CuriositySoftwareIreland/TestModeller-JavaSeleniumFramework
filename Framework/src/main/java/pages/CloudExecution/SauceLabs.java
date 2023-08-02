package pages.CloudExecution;

import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.WebGeneralActions;
import utilities.CapabilityLoader;

public class SauceLabs extends WebGeneralActions {
    @TestModellerIgnore
    public SauceLabs(WebDriver driver) {
        super(driver);
    }

    /**
     * Opens a new connection to given SauceLabs US server.
     * @name Connect SauceLabs (US Server)
     */
    public void ConnectSauceLabsUS(String username, String accessKey)
    {
        quitCurrentBrowser();

        setDriver(CapabilityLoader.createSauceLabsDriver(username, accessKey, "us"));
    }

    /**
     * Opens a new connection to given SauceLabs EU server.
     * @name Connect SauceLabs (EU Server)
     */
    public void ConnectSauceLabsEU(String username, String accessKey)
    {
        quitCurrentBrowser();

        setDriver(CapabilityLoader.createSauceLabsDriver(username, accessKey, "eu"));
    }

    /**
     * Sets the browser type
     * @name Browser Name (chrome, firefox, Safari)
     */
    public void SetWebBrowserType(String platformName)
    {
        AddCapability("browserName", platformName);
    }
    /**
     * Sets the browser version
     * @name Browser Version
     */
    public void SetWebBrowserVersion(String browserVersion)
    {
        AddCapability("browserVersion", browserVersion);
    }
    /**
     * Sets the platform type
     * @name Platform Name
     */
    public void SetWebPlatformName(String platformName)
    {
        AddCapability("platformName", platformName);
    }

    /**
     * Sets custom capability type
     * @name Set Custom Capability
     */
    public void SetCustomCapability(String capName, String capValue)
    {
        AddCapability(capName, capValue);
    }
}
