package pages;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.openqa.selenium.WebDriver;

public class WaitActions extends BasePage {
    @Ignore
    public WaitActions(WebDriver driver) {
        super(driver);
    }

    /**
     * Wait for the defined number of milliseconds until continuing.
     * @name Sleep
     */
    public void Sleep(int milliseconds)
    {
        sleep(milliseconds);
    }
}
