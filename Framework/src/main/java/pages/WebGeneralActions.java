package pages;

import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.CapabilityLoader;
import utilities.PropertiesLoader;
import utilities.selenium.WebIdentifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WebGeneralActions extends BasePage {
    private WebGeneralActionsFunctions webGeneralActionsFunctions;

    @TestModellerIgnore
    public WebGeneralActions(WebDriver driver) {
        super(driver);

        webGeneralActionsFunctions = new WebGeneralActionsFunctions(driver);

        SetBrowserType( "chrome");
    }

    /**
     * Adds a cookie to your current session.
     * @name Add Cookie
     */
    public void AddCookie(String name, String value)
    {
        Cookie cookie = new Cookie(name, value);
        m_Driver.manage().addCookie(cookie);

        passStep("Cookie added to driver with name '" + name + "' and value '" + value + "'");
    }

    /**
     * Verifies that an alert is present.
     * @name Alert Should Be Present
     */
    public void AlertShouldBePresent()
    {
        try  {
            m_Driver.switchTo().alert();

            passStepWithScreenshot("Alert is present");
        }  catch (NoAlertPresentException Ex)  {
            failStep("Alert is not present");
        }
    }

    /**
     * Verifies that no alert is present.
     * @name Alert Should Not Be Present
     */
    public void AlertShouldNotBePresent()
    {
        try  {
            m_Driver.switchTo().alert();

            failStep("Alert is present");
        }  catch (NoAlertPresentException Ex)  {
            passStepWithScreenshot("Alert is not present");
        }

    }

    /**
     * Verifies that an alert is present and contains the specified text.
     * @name Alert Assert Text
     */
    public void AssertAlertText(String text)
    {
        try  {
            String alertText = m_Driver.switchTo().alert().getText();

            if (alertText.equals(text)) {
                passStepWithScreenshot("Alert text is equal to '" + alertText + "'");
            } else {
                failStep("Expected alert text '" + text + "' found ' " + alertText + "'");

            }
        }  catch (NoAlertPresentException Ex)  {
            failStep("Alert is not present");
        }
    }

    /**
     * Verifies checkbox locator is selected/checked.
     * @name Checkbox Should Be Selected
     */
    public void CheckboxShouldBeSelected(String objectLocator)
    {
        webGeneralActionsFunctions.CheckboxShouldBeSelected(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies checkbox locator is not selected/checked.
     * @name Checkbox Should Not Be Selected
     */
    public void CheckboxShouldNotBeSelected(String objectLocator)
    {
        webGeneralActionsFunctions.CheckboxShouldNotBeSelected(getLocatorFromString(objectLocator));
    }

    /**
     * Clears the value of the text-input-element identified by locator.
     * @name Clear Element Text
     */
    public void ClearElementText(String objectLocator)
    {
        webGeneralActionsFunctions.ClearElementText(getLocatorFromString(objectLocator));
    }

    /**
     * Sets the browser type
     * @name Set Browser Name (chrome, firefox, Safari)
     */
    public void SetBrowserType(String platformName)
    {
        AddCapability("browserName", platformName);
    }

    /**
     * Opens a new instance of chrome
     * @name Open Chrome
     */
    public void OpenChrome()
    {
        QuitCurrentBrowser();

        setDriver(CapabilityLoader.createChromeDriver());
        webGeneralActionsFunctions.setDriver(m_Driver);

        passStep("Chrome browser opened");
    }

    /**
     * Upload a file from modeller using its ID
     * @name Upload Modeller File
     * @param identifier - The identifier of the element to upload the file to
     * @param fileId - The ID of the file to upload
     */
    public void UploadModellerFile(String identifier, String fileId)
    {
        // Get file
        ConnectionProfile connectionProfile = new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey"));

        String fileUrl = connectionProfile.getAPIUrl() + "api/apikey/" + connectionProfile.getAPIKey() + "/file-storage/download-file/" + fileId;
        String targetPath = null;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String fileName = disposition.replaceFirst("(?i)^.*filename=\"([^\"]+)\".*$", "$1");

            String currentWorkingDir = System.getProperty("user.dir");
            Path tempDir = Files.createTempDirectory(Paths.get(currentWorkingDir), "tempDir");
            Path outputPath = Paths.get(tempDir.toString(), fileName);

            try (InputStream in = httpConn.getInputStream()) {
                Files.copy(in, outputPath);
            }

            targetPath = outputPath.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Locate the file input element
        WebElement fileInput = getWebElement(getLocatorFromString(identifier));

        // Upload the file
        fileInput.sendKeys(targetPath);
    }

    /**
     * Disable Stop On Fail
     * @name Disable Stop On Fail
     */
    public void DisableStopOnFail()
    {
        BasePage.StopOnFail = false;
    }

    /**
     * Enable Stop On Fail
     * @name Enable Stop On Fail
     */
    public void EnableStopOnFail()
    {
        BasePage.StopOnFail = true;
    }

    /**
     * Opens a new instance of firefox
     * @name Open Firefox
     */
    public void OpenFirefox()
    {
        QuitCurrentBrowser();

        setDriver(CapabilityLoader.createFirefoxDriver());
        webGeneralActionsFunctions.setDriver(m_Driver);

        passStep("Firefox browser opened");
    }

    /**
     * Opens a new instance of edge
     * @name Open Edge
     */
    public void OpenEdge()
    {
        QuitCurrentBrowser();

        setDriver(CapabilityLoader.createEdgeDriver());
        webGeneralActionsFunctions.setDriver(m_Driver);

        passStep("Edge browser opened");
    }

    /**
     * Closes the current browser
     * @name Close Browser
     */
    public void QuitCurrentBrowser()
    {
        quitCurrentBrowser();
    }

    /**
     * Verifies that the current frame contains text.
     * @name Assert Current Frame Contains
     */
    public void CurrentFrameShouldContain(String text)
    {
        if (m_Driver.getPageSource().contains(text)) {
            passStepWithScreenshot("Current Frame Contains '" + text + "'");
        } else {
            failStep("Current frame does not contain '" + text + "'");
        }
    }

    /**
     * Verifies that the current frame does not contain text.
     * @name Assert Current Frame Does Not Contain
     */
    public void CurrentFrameShouldNotContain(String text)
    {
        if (!m_Driver.getPageSource().contains(text)) {
            passStepWithScreenshot("Current frame does not contain '" + text + "'");
        } else {
            failStep("Current frame contains '" + text + "'");
        }
    }

    /**
     * Deletes all cookies.
     * @name Delete All Cookies
     */
    public void DeleteAllCookies()
    {
        m_Driver.manage().deleteAllCookies();

        passStep("All cookies deleted");
    }

    /**
     * Deletes the cookie matching name.
     * @name Delete Cookie
     */
    public void DeleteCookie(String name)
    {
        m_Driver.manage().deleteCookieNamed(name);

        passStep("Cookie '" + name + "' deleted");
    }

    /**
     * Drags the element identified by locator into the target element.
     * The locator argument is the locator of the dragged element and the target is the locator of the target.
     * @name Drag And Drop
     */
    public void DragAndDrop(String fromLocator, String targetLocator)
    {
        WebElement fromElem = getWebElement(getLocatorFromString(fromLocator));
        if (fromElem == null) {
            failStep("Drag And Drop", ". Unable to locate from object: " + fromLocator);
        }

        WebElement targetElem = getWebElement(getLocatorFromString(targetLocator));
        if (targetElem == null) {
            failStep("Drag And Drop", ". Unable to locate to object: " + targetLocator);
        }

        Actions action = new Actions(m_Driver);
        action.dragAndDrop(fromElem, targetElem).build().perform();

        passStepWithScreenshot("Drag And Drop");
    }

    /**
     * Drags the element identified with locator by xoffset/yoffset.
     * The element will be moved by xPos and yPos, each of which is a negative or positive number specifying the offset.
     * @name Drag And Drop By Offset
     */
    public void DragAndDropByOffset(String objectLocator, Integer xPos, Integer yPos)
    {
        webGeneralActionsFunctions.DragAndDropByOffset(getLocatorFromString(objectLocator), xPos, yPos);
    }

    /**
     * Verifies element identified by locator contains expected attribute value.
     * @name Element Attribute Value Should Be
     */
    public void ElementAttributeValueShouldBe(String objectLocator, String elementAttribute, String attributeValue)
    {
        webGeneralActionsFunctions.ElementAttributeValueShouldBe(getLocatorFromString(objectLocator), elementAttribute, attributeValue);
    }

    /**
     * Verifies that element identified by locator is disabled.
     * @name Element Should Be Disabled
     */
    public void ElementShouldBeDisabled(String objectLocator)
    {
        webGeneralActionsFunctions.ElementShouldBeDisabled(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies that element identified by locator is enabled.
     * @name Element Should Be Enabled
     */
    public void ElementShouldBeEnabled(String objectLocator)
    {
        webGeneralActionsFunctions.ElementShouldBeEnabled(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies that element identified by locator is focused.
     * @name Element Should Be Focused
     */
    public void ElementShouldBeFocused(String objectLocator)
    {
        webGeneralActionsFunctions.ElementShouldBeFocused(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies that the element identified by locator is visible.
     * @name Element Should Be Visible
     */
    public void ElementShouldBeVisible(String objectLocator)
    {
        webGeneralActionsFunctions.ElementShouldBeVisible(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies that element locator contains text expected.
     * @name Element Should Contain
     */
    public void ElementShouldContain(String objectLocator, String message)
    {
        webGeneralActionsFunctions.ElementShouldContain(getLocatorFromString(objectLocator), message);
    }

    /**
     * Verifies that the element identified by locator is NOT visible.
     * @name Element Should Not Be Visible
     */
    public void ElementShouldNotBeVisible(String objectLocator)
    {
        webGeneralActionsFunctions.ElementShouldNotBeVisible(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies that element locator does not contain text expected.
     * @name Element Should Not Contain
     */
    public void ElementShouldNotContain(String objectLocator, String message)
    {
        webGeneralActionsFunctions.ElementShouldNotContain(getLocatorFromString(objectLocator), message);
    }

    /**
     * Verifies that element locator contains exact the text expected.
     * @name Element Text Should Be
     */
    public void ElementTextShouldBe(String objectLocator, String text)
    {
        webGeneralActionsFunctions.ElementTextShouldBe(getLocatorFromString(objectLocator), text);
    }

    /**
     * Verifies that element locator does not contain exact the text not_expected.
     * @name Element Text Should Not Be
     */
    public void ElementTextShouldNotBe(String objectLocator, String text)
    {
        webGeneralActionsFunctions.ElementTextShouldBe(getLocatorFromString(objectLocator), text);
    }

    /**
     * Switch the context to the specified IFrame.
     * @name Switch to IFrame
     */
    public void SwitchToIframe(String identifier)
    {
        m_Driver.switchTo().frame(getWebElement(getLocatorFromString(identifier)));
    }

    /**
     * Set the timeout to way for elements while locating.
     * @name Set Locator Timeout
     */
    public void SetDefaultTimeout(int timeout)
    {
        BasePage.LocatorTimeout = timeout;
    }

    /**
     * Selects either the first frame on the page, or the main document when a page contains iframes.
     * @name Switch to Default Context
     */
    public void SwitchToDefaultContext()
    {
        m_Driver.switchTo().defaultContent();
    }

    /**
     * Executes the given JavaScript code with possible arguments.
     * @name Execute Javascript
     */
    public void ExecuteJavascript(String javaScript)
    {
        ((JavascriptExecutor)m_Driver).executeScript(javaScript);
    }

    /**
     * Verifies that frame identified by locator contains text.
     * @name Frame Should Contain
     */
    public void FrameShouldContain(String objectLocator, String text)
    {
        webGeneralActionsFunctions.FrameShouldContain(getLocatorFromString(objectLocator), text);
    }

    /**
     * Return the desired capability value by desired capability name
     * @name Get Capability
     */
    public String GetCapability(String capability)
    {
        return CapabilityLoader.getCapability(capability);
    }

    /**
     * Add a desired capability value by desired capability name
     * @name Add Capability
     */
    public void AddCapability(String capabilityName, String value)
    {
        CapabilityLoader.addCapability(capabilityName, value);
    }

    /**
     * Returns the value of attribute from the element locator.
     * @name Get Element Attribute
     */
    public String GetElementAttribute(String objectLocator, String attribute)
    {
        return webGeneralActionsFunctions.GetElementAttribute(getLocatorFromString(objectLocator), attribute);
    }

    /**
     * Returns the text value of the element identified by locator.
     * @name Get Text
     */
    public String GetText(String objectLocator)
    {
        return webGeneralActionsFunctions.GetText(getLocatorFromString(objectLocator));
    }

    /**
     * Returns the title of the current page.
     * @name Get Title
     */
    public String GetTitle()
    {
        return m_Driver.getTitle();
    }

    /**
     * Returns the value attribute of the element identified by locator.
     * @name Get Value
     */
    public String GetValue(String objectLocator)
    {
        return webGeneralActionsFunctions.GetValue(getLocatorFromString(objectLocator));
    }

    /**
     * Simulates the user clicking the back button on their browser.
     * @name Go Back
     */
    public void GoBack()
    {
        m_Driver.navigate().back();
    }

    /**
     * Accepts the alert.
     * @name Accept Alert
     */
    public void AcceptAlert()
    {
        try  {
            m_Driver.switchTo().alert().accept();

            passStepWithScreenshot("Accepted alert");
        }  catch (NoAlertPresentException Ex)  {
            failStep("Alert is not present");
        }
    }

    /**
     * Dismisses the alert.
     * @name Dismiss Alert
     */
    public void DismissAlert()
    {
        try  {
            m_Driver.switchTo().alert().dismiss();

            passStepWithScreenshot("Dismissed alert");
        }  catch (NoAlertPresentException Ex)  {
            failStep("Alert is not present");
        }
    }

    /**
     * Types the given text into an input field in an alert.
     * @name Input Text Into Alert
     */
    public void InputTextIntoAlert(String text)
    {
        try  {
            m_Driver.switchTo().alert().sendKeys(text);

            passStepWithScreenshot("Input text '" + text + "' into alert");
        }  catch (NoAlertPresentException Ex)  {
            failStep("Alert is not present");
        }
    }

    /**
     * Simulates pressing the left mouse button on the element locator.
     * @name Mouse Down
     */
    public void MouseDown(String objectLocator)
    {
        webGeneralActionsFunctions.MouseDown(getLocatorFromString(objectLocator));
    }

    /**
     * Simulates moving the mouse away from the element locator.
     * @name Mouse Out
     */
    public void MouseOut(String objectLocator)
    {
        webGeneralActionsFunctions.MouseOut(getLocatorFromString(objectLocator));

    }

    /**
     * Simulates hovering the mouse over the element locator.
     * @name Mouse Over
     */
    public void MouseOver(String objectLocator)
    {
        webGeneralActionsFunctions.MouseOver(getLocatorFromString(objectLocator));

    }

    /**
     * Logs all console messages in the browser
     * @name Log Console Messages
     */
    public void LogConsoleMessages()
    {
        LogEntries logs = m_Driver.manage().logs().get(LogType.BROWSER);

        for (LogEntry entry : logs) {
            System.out.println(entry.getLevel() + " " + entry.getMessage());
        }
    }

    /**
     * Simulates releasing the left mouse button on the element locator.
     * @name Mouse Up
     */
    public void MouseUp(String objectLocator)
    {
        webGeneralActionsFunctions.MouseUp(getLocatorFromString(objectLocator));
    }

    /**
     * Opens the context menu on the element identified by locator.
     * @name Open Context Menu
     */
    public void OpenContextMenu(String objectLocator)
    {
        webGeneralActionsFunctions.OpenContextMenu(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies that current page contains text.
     * @name Page Should Contain Text
     */
    public void PageShouldContainText(String text)
    {
        if (m_Driver.getPageSource().contains(text)) {
            passStepWithScreenshot("Page contains text '" + text + "'");
        } else {
            failStep("Page does not contains text '" + text + "'");
        }
    }

    /**
     * Verifies that element locator is found on the current page.
     * @name Page Should Contain Element
     */
    public void PageShouldContainElement(String objectLocator)
    {
        webGeneralActionsFunctions.PageShouldContainElement(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies that current page does not contain text.
     * @name Page Should Not Contain Text
     */
    public void PageShouldNotContainText(String text)
    {
        if (m_Driver.getPageSource().contains(text)) {
            failStep("Page does contains text '" + text + "'");
        } else {
            passStepWithScreenshot("Page does not contain text '" + text + "'");
        }
    }

    /**
     * Verifies that element locator is not found on the current page.
     * @name Page Should Not Contain Element
     */
    public void PageShouldNotContainElement(String objectLocator)
    {
        webGeneralActionsFunctions.PageShouldNotContainElement(getLocatorFromString(objectLocator));
    }

    /**
     * Verifies radio button group group_name is set to value.
     * @name Radio Button Should Be Set To
     */
    public void RadioButtonShouldBeSetTo(String groupName, String value)
    {
        By radioGroup = By.xpath("//input[@type='radio' and @name='" + groupName + "']");

        List<WebElement> webElements = getWebElements(radioGroup);
        if (webElements.isEmpty()) {
            failStep("Not radio buttons found in group '" + value + "'");
        }

        Boolean found = false;
        for (WebElement elem : webElements) {
            if (elem.isSelected()) {
                if (elem.getText().equals(value)) {
                    found = true;
                }
            }
        }

        if (found) {
            passStepWithScreenshot("Radio button is set to '" + value + "'");
        } else {
            failStep("Radio button '" + value + "' is not set in the group '" + groupName + "'");
        }
    }

    /**
     * Verifies radio button group group_name has no selection.
     * @name Radio Button Should Not Be Selected
     */
    public void RadioButtonShouldNotBeSelected(String groupName, String value)
    {
        By radioGroup = By.xpath("//input[@type='radio' and @name='" + groupName + "']");

        List<WebElement> webElements = getWebElements(radioGroup);
        if (webElements.isEmpty()) {
            failStep("Not radio buttons found in group '" + value + "'");
        }

        Boolean found = false;
        for (WebElement elem : webElements) {
            if (elem.isSelected()) {
                if (elem.getText().equals(value)) {
                    found = true;
                }
            }
        }

        if (found) {
            failStep("Radio button '" + value + "' is set in the group '" + groupName + "'");
        } else {
            passStepWithScreenshot("Radio button is not set to '" + value + "'");
        }
    }

    /**
     * Simulates user reloading page.
     * @name Reload Page
     */
    public void ReloadPage()
    {
        m_Driver.navigate().refresh();
    }

    /**
     * Scrolls the element identified by locator into view.
     * @name Scroll Element Into View
     */
    public void ScrollElementIntoView(String objectLocator) throws InterruptedException {
        webGeneralActionsFunctions.ScrollElementIntoView(getLocatorFromString(objectLocator));
    }

    /**
     * Selects all options from multi-selection list locator.
     * @name Select All From List
     */
    public void SelectAllFromList(String objectLocator)
    {
        webGeneralActionsFunctions.SelectAllFromList(getLocatorFromString(objectLocator));
    }

    /**
     * Selects the checkbox identified by locator.
     * Does nothing if checkbox is already selected.
     * @name Select Checkbox
     */
    public void SelectCheckbox(String objectLocator)
    {
        webGeneralActionsFunctions.SelectCheckbox(getLocatorFromString(objectLocator));
    }

    /**
     * Selects options from selection list locator by indexes.
     * @name Select From List By Index
     */
    public void SelectFromListByIndex(String objectLocator, Integer index)
    {
        webGeneralActionsFunctions.SelectFromListByIndex(getLocatorFromString(objectLocator), index);
    }

    /**
     * Selects options from selection list locator by labels.
     * @name Select From List By Label
     */
    public void SelectFromListByLabel(String objectLocator, String label)
    {
        webGeneralActionsFunctions.SelectFromListByLabel(getLocatorFromString(objectLocator), label);
    }

    /**
     * Selects options from selection list locator by values.
     * @name Select From List By Value
     */
    public void SelectFromListByValue(String objectLocator, String value)
    {
        webGeneralActionsFunctions.SelectFromListByValue(getLocatorFromString(objectLocator), value);
    }

    /**
     * Sets the radio button group group_name to value.
     * @name Select Radio Button
     */
    public void SelectRadioButton(String groupName, String value)
    {
        By radioGroup = By.xpath("//input[@type='radio' and @name='" + groupName + "']");

        List<WebElement> webElements = getWebElements(radioGroup);
        if (webElements.isEmpty()) {
            failStep("Not radio buttons found in group '" + value + "'");
        }

        Boolean found = false;
        for (WebElement elem : webElements) {
            if (elem.getText().equals(value)) {
                elem.click();

                found = true;
            }
        }

        if (found) {
            passStepWithScreenshot("Radio button '" + value + "' selected in group '" + groupName + "'");
        } else {
            failStep("Radio button with value '" + value + "' not found in group '" + groupName + "'");
        }
    }

    /**
     * Submits a form identified by locator.
     * @name Submit Form
     */
    public void SubmitForm(String objectLocator)
    {
        webGeneralActionsFunctions.SubmitForm(getLocatorFromString(objectLocator));
    }


    /**
     * Verifies that the current page title equals title.
     * @name Title Should Be
     */
    public void TitleShouldBe(String value)
    {
        if (m_Driver.getTitle().equals(value)) {
            passStepWithScreenshot("Title is equal to '" + value + "'");
        } else {
            failStep("Title is '" + m_Driver.getTitle() + "' expected '" + value + "'");
        }
    }

    /**
     * Unselects all options from multi-selection list locator.
     * @name Unselect All From List
     */
    public void UnselectAllFromList(String objectLocator)
    {
        webGeneralActionsFunctions.UnselectAllFromList(getLocatorFromString(objectLocator));
    }

    /**
     * Removes the selection of checkbox identified by locator.
     * @name Unselect Checkbox
     */
    public void UnselectCheckbox(String objectLocator)
    {
        webGeneralActionsFunctions.UnselectCheckbox(getLocatorFromString(objectLocator));
    }

    /**
     * Unselects options from selection list locator by indexes.
     * @name Unselect From List By Index
     */
    public void UnselectFromListByIndex(String objectLocator, Integer index)
    {
        webGeneralActionsFunctions.UnselectFromListByIndex(getLocatorFromString(objectLocator), index);
    }

    /**
     * Unselects options from selection list locator by labels.
     * @name Unselect From List By Label
     */
    public void UnselectFromListByLabel(String objectLocator, String label)
    {
        webGeneralActionsFunctions.UnselectFromListByLabel(getLocatorFromString(objectLocator), label);
    }

    /**
     * Unselects options from selection list locator by values.
     * @name Unselect From List By Value
     */
    public void UnselectFromListByValue(String objectLocator, String value)
    {
        webGeneralActionsFunctions.UnselectFromListByValue(getLocatorFromString(objectLocator), value);
    }

    /**
     * Waits until the element locator is enabled.
     * @name Wait Until Element Is Enabled
     */
    public void WaitUntilElementIsEnabled(String objectLocator)
    {
        webGeneralActionsFunctions.WaitUntilElementIsEnabled(getLocatorFromString(objectLocator));
    }

    /**
     * Waits until the element locator is not visible.
     * @name Wait Until Element Is Not Visible
     */
    public void WaitUntilElementIsNotVisible(String objectLocator)
    {
        webGeneralActionsFunctions.WaitUntilElementIsNotVisible(getLocatorFromString(objectLocator));
    }

    /**
     * Waits until the element locator is visible.
     * @name Wait Until Element Is Visible
     */
    public void WaitUntilElementIsVisible(String objectLocator)
    {
        webGeneralActionsFunctions.WaitUntilElementIsVisible(getLocatorFromString(objectLocator));
    }


    /**
     * Waits until the element locator appears on the current page.
     * @name Wait Until Page Contains Element
     */
    public void WaitUntilPageContainsElement(String objectLocator)
    {
        webGeneralActionsFunctions.WaitUntilPageContainsElement(getLocatorFromString(objectLocator));
    }

    /**
     * Navigates the current browser window to the provided url.
     * @name Open URL
     */
    public void OpenURL(String url)
    {
        m_Driver.get(url);

        passStepWithScreenshot("Go to URL - " + url);
    }

	/**
     * Switch tab by index. Index 0 is always the main window.
     * @name Switch tab
     */
    public void SwitchTab(int tabIndex)
    {
        ArrayList<String> tabs = new ArrayList<String>(m_Driver.getWindowHandles());
        m_Driver.switchTo().window(tabs.get(tabIndex));

        passStepWithScreenshot("Switch tab with index " + tabIndex);
    }
	
	/**
     * Opens a new browser window / tab
     * @name New Window
     */
    public void NewWindow()
    {
        ((JavascriptExecutor) m_Driver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<String>(m_Driver.getWindowHandles());
        m_Driver.switchTo().window(tabs.get(1));

        passStepWithScreenshot("New Window");
    }

    /**
     * Maximise browser window
     * @name Maximise Window
     */
    public void MaximiseWindow()
    {
        m_Driver.manage().window().maximize();

        passStepWithScreenshot("Maximise Window");
    }

    /**
     * Set browser window size
     * @name Set Window Size
     */
    public void SetWindowSize(int width, int height)
    {
        m_Driver.manage().window().setSize(new Dimension(width, height));

        passStepWithScreenshot("Set Window Size");
    }
	
    /**
     * Click the element identified by locator.
     * @name Click
     */
    public void Click(String objectLocator)
    {
        webGeneralActionsFunctions.Click(getLocatorFromString(objectLocator));
    }

    /**
     * Click the element identified by element text.
     * @name Click By Text
     */
    public void ClickByText(String text)
    {
        String xPath = "//*[contains(text(), \"" + text + "\")]";
        WebElement elem = getWebElement(By.xpath(xPath));

        if (elem == null) {
            failStep("Click", "Click failed. Unable to locate object by text: " + text + " with xpath " + xPath);
        }

        elem.click();

        passStepWithScreenshot("Click");
    }

    /**
     * Types the given text into the element identified by locator.
     * @name Enter Text
     */
    public void EnterText(String objectLocator, String text)
    {
        webGeneralActionsFunctions.EnterText(getLocatorFromString(objectLocator), text);
    }

    /**
     * Double clicks the element identified by locator.
     * @name Double Click
     */
    public void DoubleClick(String objectLocator)
    {
        webGeneralActionsFunctions.DoubleClick(getLocatorFromString(objectLocator));
    }

    private WebIdentifier getLocatorFromString(String objectLocator)
    {
        if (objectLocator.startsWith("id:")) {
            return new WebIdentifier(By.id(objectLocator.replace("id:", "")));
        } else if (objectLocator.startsWith("name:")) {
            return new WebIdentifier(By.name(objectLocator.replace("name:", "")));
        } else if (objectLocator.startsWith("class:")) {
            return new WebIdentifier(By.className(objectLocator.replace("class:", "")));
        }  else if (objectLocator.startsWith("tagname:")) {
            return new WebIdentifier(By.tagName(objectLocator.replace("tagname:", "")));
        } else if (objectLocator.startsWith("xpath:")) {
            return new WebIdentifier(By.xpath(objectLocator.replace("xpath:", "")));
        } else if (objectLocator.startsWith("css:")) {
            return new WebIdentifier(By.cssSelector(objectLocator.replace("css:", "")));
        } else if (objectLocator.startsWith("linktext:")) {
            return new WebIdentifier(By.linkText(objectLocator.replace("linktext:", "")));
        } else if (objectLocator.startsWith("text:")) {
            return new WebIdentifier(By.xpath("//*[text()=\"" + objectLocator.replace("text:", "") + "\"]"));
        } else {
            return new WebIdentifier(By.xpath(objectLocator));
        }
    }

}
