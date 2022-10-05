package pages.bases;

import ie.curiositysoftware.pageobjects.Identifier.ModellerObjectIdentifier;
import ie.curiositysoftware.pageobjects.Identifier.ObjectIdentifier;
import ie.curiositysoftware.pageobjects.dto.PageObjectEntity;
import ie.curiositysoftware.pageobjects.dto.PageObjectHistoryEntity;
import ie.curiositysoftware.pageobjects.dto.PageObjectParameterEntity;
import ie.curiositysoftware.pageobjects.dto.PageObjectParameterStateEnum;
import ie.curiositysoftware.pageobjects.elementscanner.ElementExtractor;
import ie.curiositysoftware.pageobjects.services.PageObjectService;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.BasePage;
import utilities.PropertiesLoader;
import utilities.reports.ExtentReportManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@TestModellerIgnore
public class IntelligentMultiObjectPageBase extends BasePage {
    protected HashMap<ObjectIdentifier, PageObjectParameterEntity> objectIdentifierHash;

    private final double ObjectThreshold = 0.9f;

    public IntelligentMultiObjectPageBase(WebDriver driver)
    {
        super(driver);

        objectIdentifierHash = new HashMap<ObjectIdentifier, PageObjectParameterEntity>();
    }

    // Get element from a list of identifiers
    protected WebElement getWebElement(PageObjectEntity po, List<ObjectIdentifier> elementIdentifiers)
    {
        // Create a hash with maintains our votes
        HashMap<WebElement, Double> noVotes = new HashMap<WebElement, Double>();

        // Track the identifiers for use when tracking broken items later
        HashMap<ObjectIdentifier, List<WebElement>> identiferNodeHash = new HashMap<ObjectIdentifier, List<WebElement>>();

        // Loop over each identifer and calculate the votes assigned
        for(ObjectIdentifier identifer : elementIdentifiers) {
            List<WebElement> foundElements = getWebElements(identifer.getIdentifier());

            identiferNodeHash.put(identifer, foundElements);

            if (foundElements == null) {
                continue;
            }

            for (WebElement foundElem : foundElements) {
                if (!noVotes.containsKey(foundElem)) {
                    noVotes.put(foundElem, 0.0d);
                }

                // Ignore if < threshold
                double conf = noVotes.get(foundElem) + ((1.0d / foundElements.size()) * identifer.getConfidence());

                noVotes.put(foundElem, conf);
            }
        }

        // Return object with the most votes (note: it may return null)
        // We could also add a threshold here. E.g. must be over x certainty
        double max = 0;
        WebElement curElem = null;
        for (WebElement key : noVotes.keySet()) {
            if (noVotes.get(key) > max && noVotes.get(key) > ObjectThreshold) {
                max = noVotes.get(key);

                curElem = key;
            }
        }

        PageObjectHistoryEntity poHis = new PageObjectHistoryEntity();
        poHis.setLatestRun(new Date());
        poHis.setPageObject(po.getId());
        poHis.setTestGuid(ExtentReportManager.currentTestGuid.get());
        poHis.setTestName(ExtentReportManager.currentTestName.get());

        Boolean containsChanges = false;
        if (curElem != null) {
            // If it doesn;t find the resulting object -> we need to update it
            poHis.setPageObjectStatus(PageObjectParameterStateEnum.Active);

            for(ObjectIdentifier iden : identiferNodeHash.keySet()) {
                if (identiferNodeHash.get(iden) == null || !identiferNodeHash.get(iden).contains(curElem)) {
                    updateElement(curElem, objectIdentifierHash.get(iden));

                    poHis.setPageObjectStatus(PageObjectParameterStateEnum.IntelligentPass);

                    containsChanges = true;
                } else {
                    PageObjectParameterEntity parameterEntity = objectIdentifierHash.get(iden);

                    if (!parameterEntity.getParameterState().equals(PageObjectParameterStateEnum.Active)) {
                        parameterEntity.setParameterState(PageObjectParameterStateEnum.Active);

                        containsChanges = true;
                    }
                }
            }

        } else {
            // Mark all parameters as failing
            for (ObjectIdentifier iden : elementIdentifiers) {
                objectIdentifierHash.get(iden).setParameterState(PageObjectParameterStateEnum.Fail);
            }

            // Mark as failure
            poHis.setPageObjectStatus(PageObjectParameterStateEnum.Fail);

            containsChanges = true;
        }

        // Post history
        {
            PageObjectService poService = new PageObjectService(PropertiesLoader.getConnectionProfile());
            poService.AddPageObjectHistory(poHis);
        }

        // Post the page object to update the references and states
        if (containsChanges) {
            PageObjectService poService = new PageObjectService(PropertiesLoader.getConnectionProfile());
            poService.UpdatePageObject(po);
        }

        return curElem;
    }

    protected WebElement getWebElement(ModellerObjectIdentifier elementIdentifier)
    {
        // Get the object and parameters
        List<ObjectIdentifier> objectIdentifiers = new ArrayList<ObjectIdentifier>();
        for (PageObjectParameterEntity parameterEntity :  elementIdentifier.getPageObjectEntity(PropertiesLoader.getConnectionProfile()).getParameters()) {

//            if (parameterEntity.getParamType().equals(VipAutomationSelectorEnum.TagName))
//                continue;

            // Build the parameter
            ObjectIdentifier curIdentifer = null;

            By resolvedIdentifier = ElementExtractor.GetElementIdentifierForParameter(parameterEntity);
            if (resolvedIdentifier == null) {
                continue;
            }

            curIdentifer = new ObjectIdentifier(resolvedIdentifier, parameterEntity.getConfidence());

            objectIdentifiers.add(curIdentifer);

            objectIdentifierHash.put(curIdentifer, parameterEntity);
        }

        WebElement resolvedElem = getWebElement(elementIdentifier.getPageObjectEntity(PropertiesLoader.getConnectionProfile()), objectIdentifiers);

        return resolvedElem;
    }

    // Update the element reference
    protected void updateElement(WebElement elem, PageObjectParameterEntity pageObjectParameter)
    {
        // Update the page object references
        if (!ElementExtractor.updateParameter(pageObjectParameter, elem, m_Driver)) {
            // If its not able to update with a new element - Mark as inactive / failed
            pageObjectParameter.setParameterState(PageObjectParameterStateEnum.Fail);
        } else {
            // Mark as intelligent identifier which has been updated
            pageObjectParameter.setParameterState(PageObjectParameterStateEnum.IntelligentPass);
        }
    }
}