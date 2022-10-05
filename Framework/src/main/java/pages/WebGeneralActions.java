package pages;

import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class WebGeneralActions extends BasePage {
    @TestModellerIgnore
    public WebGeneralActions(WebDriver driver) {
        super(driver);
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Checkbox Should Be Selected", "Unable to locate object: " + objectLocator);
        }

        if (elem.isSelected()) {
            passStepWithScreenshot("Checkbox with locator '" + objectLocator + "' is Selected");
        } else {
            failStep("Checkbox with locator '" + objectLocator + "' is Selected");
        }
    }

    /**
     * Verifies checkbox locator is not selected/checked.
     * @name Checkbox Should Not Be Selected
     */
    public void CheckboxShouldNotBeSelected(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("", "Unable to locate object: " + objectLocator);
        }

        if (!elem.isSelected()) {
            passStepWithScreenshot("Checkbox with locator '" + objectLocator + "' is not Selected");
        } else {
            failStep("Checkbox with locator '" + objectLocator + "' is Selected");
        }
    }

    /**
     * Clears the value of the text-input-element identified by locator.
     * @name Clear Element Text
     */
    public void ClearElementText(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Clear Element Text", "Unable to locate object: " + objectLocator);
        }

        elem.clear();

        passStepWithScreenshot("Clear Element Text");
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Drag And Drop By Offset", "Unable to locate object: " + objectLocator);
        }

        Actions action = new Actions(m_Driver);
        action.dragAndDropBy(elem, xPos, yPos).build().perform();


        passStepWithScreenshot("Drag And Drop By Offset");
    }

    /**
     * Verifies element identified by locator contains expected attribute value.
     * @name Element Attribute Value Should Be
     */
    public void ElementAttributeValueShouldBe(String objectLocator, String elementAttribute, String attributeValue)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Attribute Value Should Be", "Unable to locate object: " + objectLocator);
        }

        if (elem.getAttribute(elementAttribute) == null)
            failStep("Element '" + objectLocator + "' does not contain attribute '" + elementAttribute + "'");

        if (elem.getAttribute(elementAttribute).equals(attributeValue)) {
            passStepWithScreenshot("Element '" + objectLocator + "' Attribute Value '" + elementAttribute + "' Should Be '" + attributeValue + "'");
        } else {
            failStep("Element '" + objectLocator + "' Attribute Value '" + elementAttribute + "' Should Be '" + attributeValue + "'. Found '" + elem.getAttribute(elementAttribute) + "'");
        }
    }

    /**
     * Verifies that element identified by locator is disabled.
     * @name Element Should Be Disabled
     */
    public void ElementShouldBeDisabled(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Should Be Disabled", "Unable to locate object: " + objectLocator);
        }

        if (elem.isEnabled()) {
            failStep("Element '" + objectLocator + "' is enabled.");
        } else {
            passStepWithScreenshot("Element '" + objectLocator + "' is disabled.");
        }
    }

    /**
     * Verifies that element identified by locator is enabled.
     * @name Element Should Be Enabled
     */
    public void ElementShouldBeEnabled(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Should Be Enabled", "Unable to locate object: " + objectLocator);
        }

        if (elem.isEnabled()) {
            passStepWithScreenshot("Element '" + objectLocator + "' is enabled.");
        } else {
            failStep("Element '" + objectLocator + "' is disabled.");
        }

    }

    /**
     * Verifies that element identified by locator is focused.
     * @name Element Should Be Focused
     */
    public void ElementShouldBeFocused(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Should Be Focused", "Unable to locate object: " + objectLocator);
        }

        if (elem.isSelected()) {
            passStepWithScreenshot("Element '" + objectLocator + "' is focused.");
        } else {
            failStep("Element '" + objectLocator + "' is not focused.");
        }
    }

    /**
     * Verifies that the element identified by locator is visible.
     * @name Element Should Be Visible
     */
    public void ElementShouldBeVisible(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Should Be Visible", "Unable to locate object: " + objectLocator);
        }

        if (elem.isDisplayed()) {
            passStepWithScreenshot("Element '" + objectLocator + "' is visible.");
        } else {
            failStep("Element '" + objectLocator + "' is not visible.");
        }
    }

    /**
     * Verifies that element locator contains text expected.
     * @name Element Should Contain
     */
    public void ElementShouldContain(String objectLocator, String message)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Should Contain", "Unable to locate object: " + objectLocator);
        }

        if (elem.getText().contains(message)) {
            passStepWithScreenshot("Element '" + objectLocator + "' contains text '" + message + "'.");
        } else {
            failStep("Element '" + objectLocator + "' does not contain text '" + message + "'.");
        }
    }

    /**
     * Verifies that the element identified by locator is NOT visible.
     * @name Element Should Not Be Visible
     */
    public void ElementShouldNotBeVisible(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Should Not Be Visible", "Unable to locate object: " + objectLocator);
        }

        if (elem.isDisplayed()) {
            failStep("Element '" + objectLocator + "' is visible.");
        } else {
            passStepWithScreenshot("Element '" + objectLocator + "' is not visible.");
        }
    }

    /**
     * Verifies that element locator does not contain text expected.
     * @name Element Should Not Contain
     */
    public void ElementShouldNotContain(String objectLocator, String message)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Should Not Contain", "Unable to locate object: " + objectLocator);
        }

        if (elem.getText().contains(message)) {
            failStep("Element '" + objectLocator + "' contains text '" + message + "'.");
        } else {
            passStepWithScreenshot("Element '" + objectLocator + "' does not contains text '" + message + "'.");
        }
    }

    /**
     * Verifies that element locator contains exact the text expected.
     * @name Element Text Should Be
     */
    public void ElementTextShouldBe(String objectLocator, String text)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Text Should Be", "Unable to locate object: " + objectLocator);
        }

        if (elem.getText().equals(text)) {
            failStep("Element '" + objectLocator + "' text is '" + text + "'.");
        } else {
            passStepWithScreenshot("Element '" + objectLocator + "' text is not '" + text + "'.");
        }
    }

    /**
     * Verifies that element locator does not contain exact the text not_expected.
     * @name Element Text Should Not Be
     */
    public void ElementTextShouldNotBe(String objectLocator, String text)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Element Text Should Not Be", "Unable to locate object: " + objectLocator);
        }

        if (elem.getText().equals(text)) {
            failStep("Element '" + objectLocator + "' text is '" + text + "'.");
        } else {
            passStepWithScreenshot("Element '" + objectLocator + "' text is not '" + text + "'.");
        }
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Frame Should Contain", "Unable to locate object: " + objectLocator);
        }

        if (elem.getText().contains(text)) {
            passStepWithScreenshot("Frame contains value '" + text + "'");
        } else {
            failStep("Frame does not contain '" + text + "'");
        }
    }

    /**
     * Returns the value of attribute from the element locator.
     * @name Get Element Attribute
     */
    public String GetElementAttribute(String objectLocator, String attribute)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Get Element Attribute", "Unable to locate object: " + objectLocator);
        }

        return elem.getAttribute(attribute);
    }

    /**
     * Returns the text value of the element identified by locator.
     * @name Get Text
     */
    public String GetText(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Get Text", "Unable to locate object: " + objectLocator);
        }

        return elem.getText();
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Get Value", "Unable to locate object: " + objectLocator);
        }

        return elem.getText();
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Mouse Down", "Unable to locate object: " + objectLocator);
        }

        Actions action = new Actions(m_Driver);
        action.clickAndHold(elem);
        action.perform();

        passStepWithScreenshot("Mouse Down");
    }

    /**
     * Simulates moving the mouse away from the element locator.
     * @name Mouse Out
     */
    public void MouseOut(String objectLocator)
    {
// TODO
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Mouse Out", "Unable to locate object: " + objectLocator);
        }

        Actions action = new Actions(m_Driver);
        action.moveToElement(elem);
        action.moveByOffset((int) (elem.getSize().getWidth() / 2.0) + 1, (int) (elem.getSize().getHeight() / 2.0) + 1);
        action.perform();

        passStepWithScreenshot("Mouse Out");

    }

    /**
     * Simulates hovering the mouse over the element locator.
     * @name Mouse Over
     */
    public void MouseOver(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Mouse Over", "Unable to locate object: " + objectLocator);
        }

        Actions action = new Actions(m_Driver);
        action.moveToElement(elem);
        action.perform();

        passStepWithScreenshot("Mouse Over");

    }

    /**
     * Simulates releasing the left mouse button on the element locator.
     * @name Mouse Up
     */
    public void MouseUp(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Mouse Up", "Unable to locate object: " + objectLocator);
        }

        Actions action = new Actions(m_Driver);
        action.release(elem);
        action.perform();

        passStepWithScreenshot("Mouse Up");
    }

    /**
     * Opens the context menu on the element identified by locator.
     * @name Open Context Menu
     */
    public void OpenContextMenu(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Open context menu", "Unable to locate object: " + objectLocator);
        }

        Actions action = new Actions(m_Driver);
        action.contextClick(elem);
        action.perform();

        passStepWithScreenshot("Open context menu");
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Page does not contain element", "Unable to locate object: " + objectLocator);
        }

        passStepWithScreenshot("Page contains element");
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            passStepWithScreenshot("Page does not contain element '" + objectLocator + "'");
        }

        failStep("Page contains element '" + objectLocator + "'");
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Scroll Element Into View", "Unable to locate object: " + objectLocator);
        }

        ((JavascriptExecutor) m_Driver).executeScript("arguments[0].scrollIntoView(true);", elem);
        Thread.sleep(500);

        passStepWithScreenshot("Scroll Element Into View");
    }

    /**
     * Selects all options from multi-selection list locator.
     * @name Select All From List
     */
    public void SelectAllFromList(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Select All From List", "Unable to locate object: " + objectLocator);
        }

        Select selectObject = new Select(elem);
        int index = 0;
        for (WebElement selectOptions : selectObject.getOptions()) {
            selectObject.selectByIndex(index);
            index++;
        }

        passStepWithScreenshot("Select All From List");
    }

    /**
     * Selects the checkbox identified by locator.
     * Does nothing if checkbox is already selected.
     * @name Select Checkbox
     */
    public void SelectCheckbox(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Select Checkbox", "Unable to locate object: " + objectLocator);
        }

        if (!elem.isSelected())
            elem.click();

        passStepWithScreenshot("Select Checkbox");
    }

    /**
     * Selects options from selection list locator by indexes.
     * @name Select From List By Index
     */
    public void SelectFromListByIndex(String objectLocator, Integer index)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Select From List By Index", "Select From List By Index. Unable to locate object: " + objectLocator);
        }

        Select dropdown = new Select(elem);
        dropdown.selectByIndex(index);

        passStepWithScreenshot("Select From List By Index");
    }

    /**
     * Selects options from selection list locator by labels.
     * @name Select From List By Label
     */
    public void SelectFromListByLabel(String objectLocator, String label)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Select From List By Label", "Select From List By Label. Unable to locate object: " + objectLocator);
        }

        Select dropdown = new Select(elem);
        dropdown.selectByVisibleText(label);

        passStepWithScreenshot("Select From List By Label");
    }

    /**
     * Selects options from selection list locator by values.
     * @name Select From List By Value
     */
    public void SelectFromListByValue(String objectLocator, String value)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Select From List By Value", "Select From List By Value. Unable to locate object: " + objectLocator);
        }

        Select dropdown = new Select(elem);
        dropdown.selectByValue(value);

        passStepWithScreenshot("Select From List By Value");
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Submit Form", "Unable to locate object: " + objectLocator);
        }

        elem.submit();;

        passStepWithScreenshot("Submit Form");
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Unselect All From List", "Unable to locate object: " + objectLocator);
        }

        Select selectObject = new Select(elem);
        selectObject.deselectAll();

        passStepWithScreenshot("Unselect All From List");
    }

    /**
     * Removes the selection of checkbox identified by locator.
     * @name Unselect Checkbox
     */
    public void UnselectCheckbox(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Unselect Checkbox", "Unable to locate object: " + objectLocator);
        }

        if (elem.isSelected())
           elem.click();

        passStepWithScreenshot("Unselect Checkbox");
    }

    /**
     * Unselects options from selection list locator by indexes.
     * @name Unselect From List By Index
     */
    public void UnselectFromListByIndex(String objectLocator, Integer index)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Unselect From List By Index", "Unable to locate object: " + objectLocator);
        }

        Select selectObject = new Select(elem);
        selectObject.deselectByIndex(index);

        passStepWithScreenshot("Unselect from list");
    }

    /**
     * Unselects options from selection list locator by labels.
     * @name Unselect From List By Label
     */
    public void UnselectFromListByLabel(String objectLocator, String label)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Unselect From List By Label", "Unable to locate object: " + objectLocator);
        }

        Select selectObject = new Select(elem);
        selectObject.deselectByVisibleText(label);

        passStepWithScreenshot("Unselect from list");
    }

    /**
     * Unselects options from selection list locator by values.
     * @name Unselect From List By Value
     */
    public void UnselectFromListByValue(String objectLocator, String value)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Unselect From List By Value", "Unable to locate object: " + objectLocator);
        }

        Select selectObject = new Select(elem);
        selectObject.deselectByValue(value);

        passStepWithScreenshot("Unselect from list");
    }

    /**
     * Waits until the element locator is enabled.
     * @name Wait Until Element Is Enabled
     */
    public void WaitUntilElementIsEnabled(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Wait Until Element Is Enabled", "Unable to locate object: " + objectLocator);
        }

        try {
            WebDriverWait wait = new WebDriverWait(m_Driver, 10);

            wait.until(ExpectedConditions.elementToBeClickable(elem));
        } catch (Exception e) {

        }

        passStepWithScreenshot("Wait until element is enabled");
    }

    /**
     * Waits until the element locator is not visible.
     * @name Wait Until Element Is Not Visible
     */
    public void WaitUntilElementIsNotVisible(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));
        if (elem == null) {
            failStep("Wait Until Element Is Not Visible", "Unable to locate object: " + objectLocator);
        }

        try {
            WebDriverWait wait = new WebDriverWait(m_Driver, 10);

            wait.until(ExpectedConditions.invisibilityOf(elem));
        } catch (Exception e) {

        }

        passStepWithScreenshot("Wait until element is not visible");
    }

    /**
     * Waits until the element locator is visible.
     * @name Wait Until Element Is Visible
     */
    public void WaitUntilElementIsVisible(String objectLocator)
    {
        waitForVisible(getLocatorFromString(objectLocator), 10);

        passStepWithScreenshot("Wait until element '" + objectLocator + "' is visible");
    }


    /**
     * Waits until the element locator appears on the current page.
     * @name Wait Until Page Contains Element
     */
    public void WaitUntilPageContainsElement(String objectLocator)
    {
        waitForLoaded(getLocatorFromString(objectLocator), 10);

        passStepWithScreenshot("Wait until page contains element '" + objectLocator + "'");
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
     * Click the element identified by locator.
     * @name Click
     */
    public void Click(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));

        if (elem == null) {
            failStep("Click", "Click failed. Unable to locate object: " + objectLocator);
        }

        elem.click();

        passStepWithScreenshot("Click");
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
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));

        if (elem == null) {
            failStep("EnterText", "EnterText failed. Unable to locate object: " + objectLocator);
        }

        elem.sendKeys(text);

        passStepWithScreenshot("EnterText " + text);
    }

    /**
     * Double clicks the element identified by locator.
     * @name Double Click
     */
    public void DoubleClick(String objectLocator)
    {
        WebElement elem = getWebElement(getLocatorFromString(objectLocator));

        if (elem == null) {
            failStep("Double Click", "Double Click failed. Unable to locate object: " + objectLocator);
        }

        Actions act = new Actions(m_Driver);
        act.doubleClick(elem).perform();

        passStepWithScreenshot("Double Click");
    }

    private By getLocatorFromString(String objectLocator)
    {
        if (objectLocator.startsWith("id:")) {
            return By.id(objectLocator.replace("id:", ""));
        } else if (objectLocator.startsWith("name:")) {
            return By.name(objectLocator.replace("name:", ""));
        } else if (objectLocator.startsWith("class:")) {
            return By.className(objectLocator.replace("class:", ""));
        }  else if (objectLocator.startsWith("tagname:")) {
            return By.tagName(objectLocator.replace("tagname:", ""));
        } else if (objectLocator.startsWith("xpath:")) {
            return By.xpath(objectLocator.replace("xpath:", ""));
        } else if (objectLocator.startsWith("css:")) {
            return By.cssSelector(objectLocator.replace("css:", ""));
        } else if (objectLocator.startsWith("linktext:")) {
            return By.linkText(objectLocator.replace("linktext:", ""));
        } else if (objectLocator.startsWith("text:")) {
            return By.xpath("//*[text()=\"" + objectLocator.replace("text:", "") + "\"]");
        } else {
            return By.xpath(objectLocator);
        }
    }

}
