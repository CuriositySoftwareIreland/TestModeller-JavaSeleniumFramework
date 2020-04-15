package utilities.testmodeller;

import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.dto.TestPathRunStep;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class TestModellerLogger {
    public static List<TestPathRunStep> steps = new ArrayList<TestPathRunStep>();

    public static void LogMessage(String name, String description, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setTestStatus(status);

        steps.add(step);
    }

    public static void LogMessageWithScreenshot(String name, String description, byte[] image, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setImage(image);
        step.setTestStatus(status);

        steps.add(step);
    }

    public static void FailStep(String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setTestStatus(TestPathRunStatusEnum.Failed);

        steps.add(step);
    }

    public static void PassStep(WebDriver driver, String stepName)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setTestStatus(TestPathRunStatusEnum.Passed);

        steps.add(step);
    }

    public static void PassStepWithScreenshot(WebDriver driver, String stepName)
    {
        PassStepWithScreenshot(driver, stepName, stepName);
    }

    public static void PassStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setImage(GetScreenShot.captureAsByteArray(driver));
        step.setTestStatus(TestPathRunStatusEnum.Passed);

        steps.add(step);
    }

    public static void FailStepWithScreenshot(WebDriver driver, String stepName)
    {
        FailStepWithScreenshot(driver, stepName, stepName);
    }

    public static void FailStepWithScreenshot(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setImage(GetScreenShot.captureAsByteArray(driver));
        step.setTestStatus(TestPathRunStatusEnum.Failed);

        steps.add(step);
    }

    public static void ClearMessages()
    {
        steps.clear();
    }

}
