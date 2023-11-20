package utilities.selenium;

import ie.curiositysoftware.gptprompt.services.GPTPromptService;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.*;

public class GPTWebLocatorService
{
    private ConnectionProfile connectionProfile;

    public GPTWebLocatorService(ConnectionProfile connectionProfile)
    {
        this.connectionProfile = connectionProfile;
    }

    public By getGPTLocator(WebDriver driver, WebIdentifier webIdentifier)
    {
        // 1) Pull it
        GPTPromptService gptPromptService = new GPTPromptService(connectionProfile);
        String val = gptPromptService.RetrieveElementIdentifier(getTrimmedDOM(driver), webIdentifier.getPageObject(connectionProfile).getName() + " " +  webIdentifier.getPageObject(connectionProfile).getDocDescription());

        if (val == null)
            return null;

        return By.xpath(val);
    }

    private String getTrimmedDOM(WebDriver webDriver)
    {
        String html = webDriver.getPageSource();
        Document doc = cleanDocument(org.jsoup.Jsoup.parse(html));
        html = doc.html();

        return html.replace("\r\n", "").replace("\n", "");
    }

    private Document cleanDocument(Document doc) {
        // Remove script and style tags
        doc.select("script, style").remove();
        doc.select("link").remove();

        // Remove all HTML comments
        doc.select("*").forEach(element -> {
            element.childNodes().forEach(node -> {
                if (node.nodeName().equals("#comment")) {
                    node.remove();
                }
            });
        });

        // Remove or trim large text blocks
        doc.select("p, div").forEach(element -> {
            String text = element.ownText();
            if (text != null && text.length() > 100) {
                element.text("");
            }
        });

        // Remove non-essential attributes
        Set<String> essentialAttributes = new HashSet<>(Arrays.asList("id", "class", "name", "href"));
        doc.select("*").forEach(element -> {
            element.attributes().asList().forEach(attribute -> {
                if (!essentialAttributes.contains(attribute.getKey())) {
                    element.removeAttr(attribute.getKey());
                }
            });
        });

        // Remove inline styles
        doc.select("[style]").removeAttr("style");

        // Replace images with placeholders
        doc.select("img").attr("src", "placeholder.jpg");

        // Collapse whitespace
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));

        // Remove embedded fonts and icons
        doc.select("link[href*='.woff'], link[href*='.ttf'], link[href*='.svg']").remove();

        // Exclude metadata
        doc.select("meta").remove();

        return doc;
    }
}
