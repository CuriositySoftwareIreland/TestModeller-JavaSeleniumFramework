package utilities.testmodeller;

import ie.curiositysoftware.runresult.dto.TestPathRunStatusEnum;
import ie.curiositysoftware.runresult.dto.TestPathRunStep;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class TestModellerLogger {
    public static final ThreadLocal<List<TestPathRunStep>> steps = new ThreadLocal<List<TestPathRunStep>>();

    public static final ThreadLocal<String> LastNodeGuid = new ThreadLocal<String>();

    public static void LogMessage(String name, String description, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setTestStatus(status);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static void LogMessageWithScreenshot(String name, String description, byte[] image, TestPathRunStatusEnum status)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(name);
        step.setStepDescription(description);
        step.setImage(image);
        step.setTestStatus(status);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static void FailStep(String stepName)
    {
        FailStep(null, stepName);
    }

    public static void FailStep(String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setTestStatus(TestPathRunStatusEnum.Failed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static void PassStep(WebDriver driver, String stepName)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setTestStatus(TestPathRunStatusEnum.Passed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static void PassStep(WebDriver driver, String stepName, String details)
    {
        TestPathRunStep step = new TestPathRunStep();

        step.setStepName(stepName);
        step.setStepDescription(details);
        step.setTestStatus(TestPathRunStatusEnum.Passed);
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static void PassStep(String stepName)
    {
        PassStep(null, stepName);
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
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
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
        step.setNodeGuid(LastNodeGuid.get());

        addStep(step);
    }

    public static void SetLastNodeGuid(String guid)
    {
        LastNodeGuid.set(guid);
    }

    public static void ClearMessages()
    {
        if (steps.get() != null)
            steps.get().clear();
    }

    private static void addStep(TestPathRunStep step)
    {
        if (steps.get() == null)
            steps.set(new ArrayList<>());

        steps.get().add(step);
    }
}
