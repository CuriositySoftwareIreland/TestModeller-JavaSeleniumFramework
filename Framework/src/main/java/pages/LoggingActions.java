package pages;

import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.WebDriver;

public class LoggingActions extends BasePage {
    @TestModellerIgnore
    public LoggingActions(WebDriver driver) {
        super(driver);
    }

    /**
     * @name Pass Step
     */
    public void logPassStep(String msg)
    {
        passStep(msg);
    }

    /**
     * @name Fail Step
     */
    public void logFailStep(String msg)
    {
        failStep(msg);
    }

    /**
     * @name Pass Step with Screenshot
     */
    public void logPassStepWithScreenshot(String msg)
    {
        passStepWithScreenshot(msg);
    }
}
