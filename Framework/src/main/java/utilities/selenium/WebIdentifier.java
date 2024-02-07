package utilities.selenium;

import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.pageobjects.dto.PageObjectEntity;
import ie.curiositysoftware.pageobjects.dto.PageObjectParameterEntity;
import ie.curiositysoftware.pageobjects.dto.VipAutomationSelectorEnum;
import ie.curiositysoftware.pageobjects.services.PageObjectService;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class WebIdentifier {
    private By elementBy;

    private Long pageObjectId;

    private List<By> elementBys;

    private PageObjectEntity pageObject;

    public WebIdentifier(By elementBy, Long pageObjectId) {
        this.elementBy = elementBy;
        this.pageObjectId = pageObjectId;
    }

    public WebIdentifier(By elementBy) {
        this.elementBy = elementBy;
    }

    public By getElementBy() {
        return elementBy;
    }

    public Long getPageObjectId() {
        return pageObjectId;
    }

    public void setElementBy(By elementBy) {
        this.elementBy = elementBy;
    }

    public void setPageObjectId(Long pageObjectId) {
        this.pageObjectId = pageObjectId;
    }

    public List<By> getElementBys(ConnectionProfile connectionProfile) {
        if (this.pageObjectId == null) return elementBys;

        if (elementBys == null) {
            elementBys = new ArrayList<By>();

            PageObjectEntity pageObjectEntity = getPageObject(connectionProfile);
            if (pageObjectEntity != null && pageObjectEntity.getParameters() != null) {
                for (PageObjectParameterEntity poParam : pageObjectEntity.getParameters()) {
                    if (poParam.getConfidence() < 1)
                        continue;

                    elementBys.add(ConvertPageObjectIdentifierToLocator(poParam));
                }
            }
        }

        return elementBys;
    }

    public List<PageObjectParameterEntity> getPageObjectParameters(ConnectionProfile connectionProfile) {
        if (this.pageObjectId == null) return new ArrayList<>();

        List<PageObjectParameterEntity> pageObjectParams = new ArrayList<PageObjectParameterEntity>();

        PageObjectEntity pageObjectEntity = getPageObject(connectionProfile);
        if (pageObjectEntity != null && pageObjectEntity.getParameters() != null) {
            for (PageObjectParameterEntity poParam : pageObjectEntity.getParameters()) {
                if (poParam.getConfidence() < 1)
                    continue;

                pageObjectParams.add(poParam);
            }
        }

        return pageObjectParams;
    }

    public static By ConvertPageObjectIdentifierToLocator(PageObjectParameterEntity poParam)
    {
        if (poParam.getParamType() == VipAutomationSelectorEnum.ClassName) {
            return (By.className(poParam.getParamValue()));
        } else if (poParam.getParamType() == VipAutomationSelectorEnum.Id) {
            return (By.id(poParam.getParamValue()));
        } else if (poParam.getParamType() == VipAutomationSelectorEnum.XPath) {
            return (By.xpath(poParam.getParamValue()));
        } else if (poParam.getParamType() == VipAutomationSelectorEnum.TagName) {
            return (By.tagName(poParam.getParamValue()));
        } else if (poParam.getParamType() == VipAutomationSelectorEnum.CssSelector) {
            return (By.cssSelector(poParam.getParamValue()));
        } else if (poParam.getParamType() == VipAutomationSelectorEnum.LinkText) {
            return (By.linkText(poParam.getParamValue()));
        } else if (poParam.getParamType() == VipAutomationSelectorEnum.PartialLinkText) {
            return (By.partialLinkText(poParam.getParamValue()));
        }

        return null;
    }

    public void setPageObject(PageObjectEntity pageObject) {
        this.pageObject = pageObject;
    }

    public PageObjectEntity getPageObject(ConnectionProfile connectionProfile) {
        if (pageObject == null) {
            PageObjectService pageObjectService = new PageObjectService(connectionProfile);
            pageObject = pageObjectService.GetPageObject(this.pageObjectId);
        }

        return pageObject;
    }
}
