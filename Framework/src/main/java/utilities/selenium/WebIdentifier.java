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

                    if (poParam.getParamType() == VipAutomationSelectorEnum.ClassName) {
                        elementBys.add(By.className(poParam.getParamValue()));
                    } else if (poParam.getParamType() == VipAutomationSelectorEnum.Id) {
                        elementBys.add(By.id(poParam.getParamValue()));
                    } else if (poParam.getParamType() == VipAutomationSelectorEnum.XPath) {
                        elementBys.add(By.xpath(poParam.getParamValue()));
                    } else if (poParam.getParamType() == VipAutomationSelectorEnum.TagName) {
                        elementBys.add(By.tagName(poParam.getParamValue()));
                    } else if (poParam.getParamType() == VipAutomationSelectorEnum.CssSelector) {
                        elementBys.add(By.cssSelector(poParam.getParamValue()));
                    } else if (poParam.getParamType() == VipAutomationSelectorEnum.LinkText) {
                        elementBys.add(By.linkText(poParam.getParamValue()));
                    } else if (poParam.getParamType() == VipAutomationSelectorEnum.PartialLinkText) {
                        elementBys.add(By.partialLinkText(poParam.getParamValue()));
                    }
                }
            }
        }

        return elementBys;
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
