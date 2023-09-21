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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@TestModellerIgnore
public class WebGeneralActionsFunctions extends BasePage {
    public WebGeneralActionsFunctions(WebDriver driver) {
        super(driver);
    }

    /**
     * Verifies checkbox locator is selected/checked.
     * @name Checkbox Should Be Selected
     */
    public void CheckboxShouldBeSelected(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void CheckboxShouldNotBeSelected(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ClearElementText(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Clear Element Text", "Unable to locate object: " + objectLocator);
        }

        elem.clear();

        passStepWithScreenshot("Clear Element Text");
    }

    /**
     * Drags the element identified with locator by xoffset/yoffset.
     * The element will be moved by xPos and yPos, each of which is a negative or positive number specifying the offset.
     * @name Drag And Drop By Offset
     */
    public void DragAndDropByOffset(By objectLocator, Integer xPos, Integer yPos)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementAttributeValueShouldBe(By objectLocator, String elementAttribute, String attributeValue)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementShouldBeDisabled(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementShouldBeEnabled(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementShouldBeFocused(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementShouldBeVisible(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementShouldContain(By objectLocator, String message)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementShouldNotBeVisible(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementShouldNotContain(By objectLocator, String message)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementTextShouldBe(By objectLocator, String text)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void ElementTextShouldNotBe(By objectLocator, String text)
    {
        WebElement elem = getWebElement(objectLocator);
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
     * Verifies that frame identified by locator contains text.
     * @name Frame Should Contain
     */
    public void FrameShouldContain(By objectLocator, String text)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public String GetElementAttribute(By objectLocator, String attribute)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Get Element Attribute", "Unable to locate object: " + objectLocator);
        }

        return elem.getAttribute(attribute);
    }

    /**
     * Returns the text value of the element identified by locator.
     * @name Get Text
     */
    public String GetText(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Get Text", "Unable to locate object: " + objectLocator);
        }

        return elem.getText();
    }

    /**
     * Returns the value attribute of the element identified by locator.
     * @name Get Value
     */
    public String GetValue(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Get Value", "Unable to locate object: " + objectLocator);
        }

        return elem.getText();
    }

    /**
     * Simulates pressing the left mouse button on the element locator.
     * @name Mouse Down
     */
    public void MouseDown(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void MouseOut(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void MouseOver(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void MouseUp(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void OpenContextMenu(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Open context menu", "Unable to locate object: " + objectLocator);
        }

        Actions action = new Actions(m_Driver);
        action.contextClick(elem);
        action.perform();

        passStepWithScreenshot("Open context menu");
    }

    /**
     * Verifies that element locator is found on the current page.
     * @name Page Should Contain Element
     */
    public void PageShouldContainElement(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Page does not contain element", "Unable to locate object: " + objectLocator);
        }

        passStepWithScreenshot("Page contains element");
    }

    /**
     * Verifies that element locator is not found on the current page.
     * @name Page Should Not Contain Element
     */
    public void PageShouldNotContainElement(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            passStepWithScreenshot("Page does not contain element '" + objectLocator + "'");
        }

        failStep("Page contains element '" + objectLocator + "'");
    }

    /**
     * Scrolls the element identified by locator into view.
     * @name Scroll Element Into View
     */
    public void ScrollElementIntoView(By objectLocator) throws InterruptedException {
        WebElement elem = getWebElement(objectLocator);
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
    public void SelectAllFromList(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void SelectCheckbox(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void SelectFromListByIndex(By objectLocator, Integer index)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void SelectFromListByLabel(By objectLocator, String label)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void SelectFromListByValue(By objectLocator, String value)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Select From List By Value", "Select From List By Value. Unable to locate object: " + objectLocator);
        }

        Select dropdown = new Select(elem);
        dropdown.selectByValue(value);

        passStepWithScreenshot("Select From List By Value");
    }

    /**
     * Submits a form identified by locator.
     * @name Submit Form
     */
    public void SubmitForm(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Submit Form", "Unable to locate object: " + objectLocator);
        }

        elem.submit();;

        passStepWithScreenshot("Submit Form");
    }

    /**
     * Unselects all options from multi-selection list locator.
     * @name Unselect All From List
     */
    public void UnselectAllFromList(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void UnselectCheckbox(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void UnselectFromListByIndex(By objectLocator, Integer index)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void UnselectFromListByLabel(By objectLocator, String label)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void UnselectFromListByValue(By objectLocator, String value)
    {
        WebElement elem = getWebElement(objectLocator);
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
    public void WaitUntilElementIsEnabled(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Wait Until Element Is Enabled", "Unable to locate object: " + objectLocator);
        }

        try {
            WebDriverWait wait = new WebDriverWait(m_Driver, Duration.ofSeconds(15));

            wait.until(ExpectedConditions.elementToBeClickable(elem));
        } catch (Exception e) {

        }

        passStepWithScreenshot("Wait until element is enabled");
    }

    /**
     * Waits until the element locator is not visible.
     * @name Wait Until Element Is Not Visible
     */
    public void WaitUntilElementIsNotVisible(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);
        if (elem == null) {
            failStep("Wait Until Element Is Not Visible", "Unable to locate object: " + objectLocator);
        }

        try {
            WebDriverWait wait = new WebDriverWait(m_Driver, Duration.ofSeconds(10));

            wait.until(ExpectedConditions.invisibilityOf(elem));
        } catch (Exception e) {

        }

        passStepWithScreenshot("Wait until element is not visible");
    }

    /**
     * Waits until the element locator is visible.
     * @name Wait Until Element Is Visible
     */
    public void WaitUntilElementIsVisible(By objectLocator)
    {
        waitForVisible(objectLocator, 10);

        passStepWithScreenshot("Wait until element '" + objectLocator + "' is visible");
    }


    /**
     * Waits until the element locator appears on the current page.
     * @name Wait Until Page Contains Element
     */
    public void WaitUntilPageContainsElement(By objectLocator)
    {
        waitForLoaded(objectLocator, 10);

        passStepWithScreenshot("Wait until page contains element '" + objectLocator + "'");
    }

    /**
     * Click the element identified by locator.
     * @name Click
     */
    public void Click(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);

        if (elem == null) {
            failStep("Click", "Click failed. Unable to locate object: " + objectLocator);
        }

        elem.click();

        passStepWithScreenshot("Click");
    }

    /**
     * Types the given text into the element identified by locator.
     * @name Enter Text
     */
    public void EnterText(By objectLocator, String text)
    {
        WebElement elem = getWebElement(objectLocator);

        if (elem == null) {
            failStep("EnterText", "Send keys failed. Unable to locate object: " + objectLocator);
        }

        elem.sendKeys(text);

        passStepWithScreenshot("EnterText " + text);
    }

    /**
     * Double clicks the element identified by locator.
     * @name Double Click
     */
    public void DoubleClick(By objectLocator)
    {
        WebElement elem = getWebElement(objectLocator);

        if (elem == null) {
            failStep("Double Click", "Double Click failed. Unable to locate object: " + objectLocator);
        }

        Actions act = new Actions(m_Driver);
        act.doubleClick(elem).perform();

        passStepWithScreenshot("Double Click");
    }
}
