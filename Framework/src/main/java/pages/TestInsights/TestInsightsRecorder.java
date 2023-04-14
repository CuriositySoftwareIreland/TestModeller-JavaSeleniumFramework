package pages.TestInsights;

import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.journey.dto.Journey;
import ie.curiositysoftware.journey.dto.JourneyPool;
import ie.curiositysoftware.journey.services.JourneyPoolService;
import ie.curiositysoftware.journey.services.JourneyService;
import ie.curiositysoftware.modellerstorage.dto.ModellerFile;
import ie.curiositysoftware.modellerstorage.dto.ModellerFolder;
import ie.curiositysoftware.modellerstorage.services.ModellerFileService;
import ie.curiositysoftware.modellerstorage.services.ModellerFolderService;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import org.apache.http.entity.FileEntity;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;
import utilities.PropertiesLoader;
import utilities.reports.ExtentReportManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TestInsightsRecorder extends BasePage {
    public static JourneyPool journey = null;

    @TestModellerIgnore
    public TestInsightsRecorder(WebDriver driver) {
        super(driver);

        createNewJourneyPool();
    }


    public void StartRecorder()
    {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) m_Driver;

        jsExecutor.executeScript("var event = new CustomEvent('Event');" +
                                        "event.initEvent('start_record');" +
                                        "document.dispatchEvent(event);");
    }

    public void StopRecorder()
    {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) m_Driver;

        jsExecutor.executeScript("var event = new CustomEvent('Event');" +
                                        "event.initEvent('stop_record');" +
                                        "document.dispatchEvent(event);");
    }

    public void UploadRecording()
    {
        ConnectionProfile cp = PropertiesLoader.getConnectionProfile();

        JavascriptExecutor jsExecutor = (JavascriptExecutor) m_Driver;

        jsExecutor.executeScript("var event = new CustomEvent('Event', {detail: {" +
                                            "name: '" + ExtentReportManager.getTestName() + "', " +
                                            "poolId: " + journey.getId() + ", " +
                                            "url: '" + cp.getAPIUrl().substring(0, cp.getAPIUrl().length() -1) + "', " +
                                            "apiKey: '" + cp.getAPIKey() + "', " +
                                            "source:' QuickStart Web Framework', " +
                                            "sourceLocation:'" + "Automation" + "', " +
                                            "releaseID: " + PropertiesLoader.getReleaseId() + "}});" +
                "        event.initEvent('upload_session_sync');" +
                "        document.dispatchEvent(event);");

        // Wait for alert and print the text;
        try {
            WebDriverWait wait = new WebDriverWait(m_Driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.alertIsPresent());

            Alert alert = m_Driver.switchTo().alert();
            System.out.println("Alert text: " + alert.getText());

            alert.accept();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void createNewJourneyPool()
    {
        if (journey != null)
            return;

        Long releaseId = PropertiesLoader.getReleaseId();
        ConnectionProfile cp = PropertiesLoader.getConnectionProfile();

        // Create folder
        ModellerFolder mf = new ModellerFolder();
        mf.setFolderName("Automation Insights");
        mf.setRelease(releaseId);
        ModellerFolderService modellerFolderService = new ModellerFolderService(cp);
        mf = modellerFolderService.mergeFolder(mf);


        // Details
        String resultName = "Test run - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // Create pool
        JourneyPoolService journeyPoolService = new JourneyPoolService(cp);
        if (journey == null) {
            journey = journeyPoolService.createJourneyPool(Optional.of(mf.getId()), releaseId, resultName);
        }
    }
}
