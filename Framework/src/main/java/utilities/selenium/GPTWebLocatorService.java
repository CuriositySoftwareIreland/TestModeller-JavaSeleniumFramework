package utilities.selenium;

import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class GPTWebLocatorService
{
    private ConnectionProfile connectionProfile;

    public GPTWebLocatorService(ConnectionProfile connectionProfile)
    {
        this.connectionProfile = connectionProfile;
    }

    public By getGPTLocator(WebIdentifier webIdentifier)
    {
        // TODO
        return null;
    }
}
