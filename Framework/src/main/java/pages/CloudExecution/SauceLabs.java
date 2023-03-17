package pages.CloudExecution;

import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import utilities.CapabilityLoader;

public class SauceLabs extends BasePage {
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
}
