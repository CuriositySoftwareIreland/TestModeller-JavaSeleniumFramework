package pages.VisualAssertions;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.WebDriver;
import pages.BasePage;

public class Applitools extends BasePage {
    protected EyesRunner runner;

    protected Eyes eyes;

    @TestModellerIgnore
    public Applitools(WebDriver driver) {
        super(driver);
    }

    /**
     * Configure Applitools API Key and Application
     * @name Set API Key
     */
    public void SetAPIKey(String appName, String testName, String apiKey)
    {
        runner = new ClassicRunner();
        eyes = new Eyes(runner);

        BatchInfo batch = new BatchInfo();
        eyes.setBatch(batch);
        eyes.setApiKey(apiKey);
        eyes.setStitchMode(StitchMode.CSS);
        eyes.setHideScrollbars(true);
        eyes.setHideCaret(true);

        eyes.open(m_Driver, appName, testName);
    }

    /**
     * Check the current window
     * @name Check Window
     */
    public void CheckWindow(String tag)
    {
        eyes.checkWindow(tag);
    }

    /**
     * Close Eyes
     * @name Close Eyes
     */
    public void CloseEyes()
    {
        CloseEyes(false);
    }

    /**
     * Close Eyes
     * @name Close Eyes
     */
    public void CloseEyes(Boolean throwExceptionOnFail)
    {
        eyes.close(throwExceptionOnFail);
    }
}
