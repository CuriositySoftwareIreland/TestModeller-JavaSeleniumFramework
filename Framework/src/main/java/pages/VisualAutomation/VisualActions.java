package pages.VisualAutomation;

import ie.curiositysoftware.jobengine.dto.file.FileDataStorage;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import ie.curiositysoftware.utils.ServiceBase;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.*;
import org.testng.Assert;
import pages.BasePage;
import pages.WebGeneralActionsFunctions;
import utilities.PropertiesLoader;
import utilities.reports.ExtentReportManager;
import utilities.testmodeller.TestModellerLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class VisualActions extends BasePage {
    private Screen screen;
    private static List<App> appList = new ArrayList<App>();
    private static final int MAX_RETRIES = 3;  // Maximum number of retry attempts
    private static final int RETRY_DELAY = 2000;  // Delay between retries in milliseconds
    private static final int WAIT_TIME = 5;  // Maximum wait time for an image to appear in seconds

    @TestModellerIgnore
    public VisualActions(WebDriver driver) {
        super(driver);

        this.screen = new Screen();
    }

    /**
     * Generic retry mechanism
     * @param action The action to be performed.
     * @param description Description of the action for logging.
     */
    private boolean performWithRetry(Runnable action, String description) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                action.run();
                passStepWithScreenshot(description);
                return true;
            } catch (Exception e) {
                attempt++;
                System.out.println("Attempt " + attempt + ": Failed to " + description);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        System.out.println("Failed to sleep between retries.");
                    }
                }
            }
        }
        failStep(description, "Failed after " + MAX_RETRIES + " attempts.");
        return false;
    }

    /**
     * Generic download an image from a URL and saves it to a temporary file.
     * @param imageId URL of the image to be downloaded.
     * @return Path to the temporary image file.
     */
    private String downloadImageToTempFile(String imageId) throws IOException {
        File tempImageFile = File.createTempFile("sikulix_", ".png");

        // Construct image url
        String imageUrl = ServiceBase.createURLs(PropertiesLoader.getConnectionProfile().getAPIUrl(),
                                                "/api/apikey/",
                                                PropertiesLoader.getConnectionProfile().getAPIKey(),
                                                "/file-storage/download-file/" + imageId);

        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempImageFile.getAbsolutePath();
    }

    /**
     * Opens the specified application.
     * @name Open Application
     * @param appName Name or path of the application to be opened.
     * @param waitForOpen Time to wait for the application to open in milliseconds.
     * @return true if the application is opened successfully, false otherwise.
     */
    public boolean openApplication(String appName, Integer waitForOpen) {
        try {
            App app = new App(appName);
            appList.add(app);
            
            // Check if the app is running; if not, open it
            if (!app.isRunning()) {
                app.open();
            }

            // Give it a few seconds to launch
            Thread.sleep(waitForOpen);

            // Check if the app is now running
            if (app.isRunning()) {
                passStepWithScreenshot("Open Application");
                return true;
            } else {
                BasePage.StopOnFail = false;
                failStep("Open Application", "Failed to open " + appName + " will attempt to continue.");
                BasePage.StopOnFail = true;
                return true;
            }
        } catch (Exception e) {
            failStep("Open Application", "Error encountered: " + e.getMessage());
            return false;
        }
    }

    /**
     * Closes all open application.
     * @name Close Application
     */
    public static void closeApplication() {
        for (App app : appList) {
            try {
                app.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Clicks on a specified image on the screen.
     * @name Click Image
     */
    private void clickImage(String imagePath) {
        try {
            performWithRetry(() -> {
                if (screen.exists(imagePath, WAIT_TIME) != null) {
                    try {
                        screen.click(imagePath);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Unable to locate object: " + imagePath);
                }
            }, "Click Image");
        } catch (Exception e) {
            failStep("Click image failed", e.getMessage());
        }
    }

    /**
     * Double-clicks on a specified image on the screen.
     * @name Double Click Image
     * @param imagePath Path to the image to be double-clicked.
     */
    private void doubleClickImage(String imagePath) {
        try {
            performWithRetry(() -> {
                if (screen.exists(imagePath, WAIT_TIME) != null) {
                    try {
                        screen.doubleClick(imagePath);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Unable to locate object: " + imagePath);
                }
            }, "Double Click Image");
        } catch (Exception e) {
            failStep("Double click image failed", e.getMessage());
        }
    }


    /**
     * Double-clicks on a specified image on the screen.
     * @name Double Click Image
     * @param imageId Id to the image to be double-clicked.
     */
    public void doubleClickImageFromId(String imageId) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            doubleClickImage(imagePath);
        } catch (IOException e) {
            failStep("Double click image failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    /**
     * Clicks on a specified image on the screen using an image URL.
     * @name Click Image
     * @param imageId Id of the image to be clicked.
     */
    public void clickImageFromId(String imageId) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            clickImage(imagePath);
        } catch (IOException e) {
            failStep("Click image failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    /**
     * Right-clicks on a specified image on the screen.
     * @name Right Click Image
     * @param imageId Id of the image to be right-clicked.
     */
    public void rightClickImageFromId(String imageId) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            rightClickImage(imagePath);
        } catch (IOException e) {
            failStep("Right click image failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    private void rightClickImage(String imagePath) {
        try {
            performWithRetry(() -> {
                if (screen.exists(imagePath, WAIT_TIME) != null) {
                    try {
                        screen.rightClick(imagePath);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Unable to locate object: " + imagePath);
                }
            }, "Right Click Image");
        } catch (Exception e) {
            failStep("Right click image failed", e.getMessage());
        }
    }

    /**
     * Hovers over a specified image on the screen.
     * @name Hover Over Image
     * @param imageId Id of the image to be hovered over.
     */
    public void hoverOverImageFromId(String imageId) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            hoverOverImage(imagePath);
        } catch (IOException e) {
            failStep("Hover over image failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    private void hoverOverImage(String imagePath) {
        try {
            performWithRetry(() -> {
                if (screen.exists(imagePath, WAIT_TIME) != null) {
                    try {
                        screen.hover(imagePath);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Unable to locate object: " + imagePath);
                }
            }, "Hover Over Image");
        } catch (Exception e) {
            failStep("Hover over image failed", e.getMessage());
        }
    }

    /**
     * Drags an image and drops it onto another image on the screen.
     * @name Drag and Drop Image
     * @param sourceImageId Id of the source image to be dragged.
     * @param targetImageId Id of the target image where the source will be dropped.
     */
    public void dragAndDropFromId(String sourceImageId, String targetImageId) {
        String sourceImagePath = null;
        String targetImagePath = null;
        try {
            sourceImagePath = downloadImageToTempFile(sourceImageId);
            targetImagePath = downloadImageToTempFile(targetImageId);
            dragAndDrop(sourceImagePath, targetImagePath);
        } catch (IOException e) {
            failStep("Drag and Drop failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image files
            if (sourceImagePath != null) {
                new File(sourceImagePath).delete();
            }
            if (targetImagePath != null) {
                new File(targetImagePath).delete();
            }
        }
    }

    private void dragAndDrop(String sourceImagePath, String targetImagePath) {
        try {
            performWithRetry(() -> {
                if (screen.exists(sourceImagePath, WAIT_TIME) != null && screen.exists(targetImagePath, WAIT_TIME) != null) {
                    try {
                        screen.dragDrop(sourceImagePath, targetImagePath);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Unable to locate source or target object.");
                }
            }, "Drag and Drop");
        } catch (Exception e) {
            failStep("Drag and drop failed", e.getMessage());
        }
    }

    /**
     * Enters text next to a specified image on the screen.
     * @name Enter Text By Image
     * @param imageId Id of the image next to which the text will be entered.
     * @param text The text to be entered.
     */
    public void enterTextByImageFromId(String imageId, String text) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            enterTextByImage(imagePath, text);
        } catch (IOException e) {
            failStep("Enter text by image failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    private void enterTextByImage(String imagePath, String text) {
        try {
            performWithRetry(() -> {
                if (screen.exists(imagePath, WAIT_TIME) != null) {
                    try {
                        screen.click(imagePath);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                    screen.type(text);
                } else {
                    throw new RuntimeException("Unable to locate object: " + imagePath);
                }
            }, "Enter Text By Image");
        } catch (Exception e) {
            failStep("Enter text by image failed", e.getMessage());
        }
    }

    /**
     * Clicks on a specified image on the screen with an offset.
     * @name Click Image With Offset
     * @param imageId Id of the image to be clicked.
     * @param offsetX X offset from the center of the image.
     * @param offsetY Y offset from the center of the image.
     */
    public void clickImageWithOffsetFromId(String imageId, int offsetX, int offsetY) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            clickImageWithOffset(imagePath, offsetX, offsetY);
        } catch (IOException e) {
            failStep("Click image with offset failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    private void clickImageWithOffset(String imagePath, int offsetX, int offsetY) {
        try {
            performWithRetry(() -> {
                Pattern imagePattern = new Pattern(imagePath).targetOffset(offsetX, offsetY);
                if (screen.exists(imagePattern, WAIT_TIME) != null) {
                    try {
                        screen.click(imagePattern);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Unable to locate object: " + imagePath);
                }
            }, "Click Image With Offset");
        } catch (Exception e) {
            failStep("Click image with offset failed", e.getMessage());
        }
    }

    /**
     * Enters text on a specified image on the screen with an offset.
     * @name Enter text with offset
     * @param imageId Id of the image to enter text.
     * @param text text of the image to be entered.
     * @param offsetX X offset from the center of the image.
     * @param offsetY Y offset from the center of the image.
     */
    public void enterTextWithOffset(String text, String imageId, int offsetX, int offsetY) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            enterTextWithOffsetImage(text, imagePath, offsetX, offsetY);
        } catch (IOException e) {
            failStep("Click image with offset failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    private void enterTextWithOffsetImage(String text, String imagePath, int offsetX, int offsetY) {
        try {
            performWithRetry(() -> {
                Pattern imagePattern = new Pattern(imagePath).targetOffset(offsetX, offsetY);
                if (screen.exists(imagePattern, WAIT_TIME) != null) {
                    try {
                        screen.type(imagePattern, text);
                    } catch (FindFailed e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException("Unable to locate object: " + imagePath);
                }
            }, "Click Image With Offset");
        } catch (Exception e) {
            failStep("Click image with offset failed", e.getMessage());
        }
    }

    /**
     * Waits for a specified image to appear on the screen.
     * @name Wait For Image
     * @param imageId Id of the image to wait for.
     * @param timeoutSeconds Maximum time in seconds to wait for the image to appear.
     */
    public void waitForImage(String imageId, int timeoutSeconds) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            if (!screen.wait(imagePath, timeoutSeconds).isValid()) {
                failStep("Wait For Image", "Image did not appear within the specified timeout: " + imagePath);
            }
        } catch (Exception e) {
            failStep("Wait for image failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    /**
     * Waits until a specified image vanishes from the screen.
     * @name Wait Until Image Vanishes
     * @param imageId Id of the image to wait for it to vanish.
     * @param timeoutSeconds Maximum time in seconds to wait for the image to vanish.
     */
    public void waitUntilImageVanishes(String imageId, int timeoutSeconds) {
        String imagePath = null;
        try {
            imagePath = downloadImageToTempFile(imageId);
            if (!screen.waitVanish(imagePath, timeoutSeconds)) {
                failStep("Wait Until Image Vanishes", "Image did not vanish within the specified timeout: " + imagePath);
            }
        } catch (IOException e) {
            failStep("Wait until image vanishes failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
    }

    /**
     * Checks if a specified image exists on the screen.
     * @name Exists
     * @param imageId Id of the image to check for.
     * @return true if the image exists, false otherwise.
     */
    public boolean exists(String imageId) {
        String imagePath = null;
        boolean imageExists = false;
        try {
            imagePath = downloadImageToTempFile(imageId);
            imageExists = screen.exists(imagePath) != null;
        } catch (IOException e) {
            failStep("Exists check failed", "Error downloading or writing image from URL: " + e.getMessage());
        } finally {
            // Clean up by deleting the temporary image file
            if (imagePath != null) {
                new File(imagePath).delete();
            }
        }
        return imageExists;
    }

    /**
     * Clicks on the specified text on the screen using OCR.
     * @name Click By Text
     * @param text The text to be clicked.
     */
    public void clickByText(String text) {
        try {
            performWithRetry(() -> {
                Region textRegion = null;
                try {
                    textRegion = screen.findText(text);
                } catch (FindFailed e) {
                    throw new RuntimeException(e);
                }
                if (textRegion != null) {
                    textRegion.click();
                } else {
                    throw new RuntimeException("Unable to locate text: " + text);
                }
            }, "Click By Text");
        } catch (Exception e) {
            failStep("Click by text failed", e.getMessage());
        }
    }

    /**
     * Double-clicks on the specified text on the screen using OCR.
     * @name Double Click By Text
     * @param text The text to be double-clicked.
     */
    public void doubleClickByText(String text) {
        try {
            performWithRetry(() -> {
                Region textRegion = null;
                try {
                    textRegion = screen.findText(text);
                } catch (FindFailed e) {
                    throw new RuntimeException(e);
                }
                if (textRegion != null) {
                    textRegion.doubleClick();
                } else {
                    throw new RuntimeException("Unable to locate text: " + text);
                }
            }, "Double Click By Text");
        } catch (Exception e) {
            failStep("Double click by text failed", e.getMessage());
        }
    }

    /**
     * Right-clicks on the specified text on the screen using OCR.
     * @name Right Click By Text
     * @param text The text to be right-clicked.
     */
    public void rightClickByText(String text) {
        try {
            performWithRetry(() -> {
                Region textRegion = null;
                try {
                    textRegion = screen.findText(text);
                } catch (FindFailed e) {
                    throw new RuntimeException(e);
                }
                if (textRegion != null) {
                    textRegion.rightClick();
                } else {
                    throw new RuntimeException("Unable to locate text: " + text);
                }
            }, "Right Click By Text");
        } catch (Exception e) {
            failStep("Right click by text failed", e.getMessage());
        }
    }

    /**
     * Types the specified text onto the active window or field.
     * @name Type Text
     * @param text The text to be typed.
     */
    public void typeText(String text) {
        try {
            screen.type(text);
        } catch (Exception e) {
            failStep("Type text failed", e.getMessage());
        }
    }

    /**
     * Pastes the specified text onto the active window or field.
     * @name Paste Text
     * @param text The text to be pasted.
     */
    public void pasteText(String text) {
        try {
            screen.paste(text);
        } catch (Exception e) {
            failStep("Paste text failed", e.getMessage());
        }
    }

    /**
     * Presses the specified key.
     * @name Press Key
     * @param keyConstant The key to be pressed (from SikuliX's Key class).
     */
    public void pressKey(String keyConstant) {
        try {
            screen.keyDown(keyConstant);
        } catch (Exception e) {
            failStep("Press key failed", e.getMessage());
        }
    }

    /**
     * Releases the specified key.
     * @name Release Key
     * @param keyConstant The key to be released (from SikuliX's Key class).
     */
    public void releaseKey(String keyConstant) {
        try {
            screen.keyUp(keyConstant);
        } catch (Exception e) {
            failStep("Release key failed", e.getMessage());
        }
    }

    /**
     * Presses the specified key modifiers.
     * @name Press Modifiers
     * @param keyMod The key modifier(s) to be pressed (from SikuliX's KeyModifier class).
     */
    public void pressModifiers(int keyMod) {
        try {
            screen.keyDown(keyMod);
        } catch (Exception e) {
            failStep("Press modifiers failed", e.getMessage());
        }
    }

    /**
     * Releases the specified key modifiers.
     * @name Release Modifiers
     * @param keyMod The key modifier(s) to be released (from SikuliX's KeyModifier class).
     */
    public void releaseModifiers(int keyMod) {
        try {
            screen.keyUp(keyMod);
        } catch (Exception e) {
            failStep("Release modifiers failed", e.getMessage());
        }
    }

    /**
     * Scrolls up by the specified wheel amount.
     * @name Scroll Up
     * @param wheelAmount The amount to scroll up.
     */
    public void scrollUp(int wheelAmount) {
        try {
            screen.wheel(Integer.parseInt(Key.UP), wheelAmount);
        } catch (Exception e) {
            failStep("Scroll Up failed", e.getMessage());
        }
    }

    /**
     * Scrolls down by the specified wheel amount.
     * @name Scroll Down
     * @param wheelAmount The amount to scroll down.
     */
    public void scrollDown(int wheelAmount) {
        try {
            screen.wheel(Integer.parseInt(Key.DOWN), wheelAmount);
        } catch (Exception e) {
            failStep("Scroll Down failed", e.getMessage());
        }
    }

    /**
     * Checks if the specified text exists on the screen using OCR.
     * @name Exists Text
     * @param text The text to be searched for.
     * @return true if the text exists, false otherwise.
     */
    public boolean existsText(String text) {
        try {
            return screen.findText(text) != null;
        } catch (Exception e) {
            failStep("Exists Text failed", e.getMessage());
            return false;
        }
    }

    /**
     * Waits for the specified text to appear on the screen for a certain duration using OCR.
     * @name Wait For Text
     * @param text The text to be waited for.
     * @param timeoutSeconds The maximum time to wait in seconds.
     * @return true if the text appeared within the timeout, false otherwise.
     */
    public boolean waitForText(String text, int timeoutSeconds) {
        try {
            return screen.waitText(text, timeoutSeconds) != null;
        } catch (FindFailed e) {
            failStep("Wait For Text failed", e.getMessage());
            return false;
        }
    }


    /**
     * Waits until the specified text vanishes from the screen or until the timeout using OCR.
     * @name Wait Until Text Vanishes
     * @param text The text to be waited for to vanish.
     * @param timeoutSeconds The maximum time to wait in seconds.
     * @return true if the text vanished within the timeout, false otherwise.
     */
    public boolean waitUntilTextVanishes(String text, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (timeoutSeconds * 1000);
        while (System.currentTimeMillis() < endTime) {
            if (!existsText(text)) {
                return true;
            }
            try {
                Thread.sleep(1000);  // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        failStep("Wait Until Text Vanishes failed", "Text did not vanish within the specified timeout.");
        return false;
    }

    /**
     * Captures the screen.
     * @name Capture Screen
     * @param text The text for the screenshot
     */
    public void captureScreen(String text) {
        passStepWithScreenshot(text);
    }

    @Override
    protected void passStepWithScreenshot(String msg)
    {
        ExtentReportManager.passStepWithScreenshot(screen, msg);
        TestModellerLogger.PassStepWithScreenshot(screen, msg);

        System.out.println("Test (" + ExtentReportManager.getTestName() + ") - Pass Step: " + msg);
    }

    @Override
    protected void failStep(String msg, String details)
    {
        ExtentReportManager.failStepWithScreenshot(screen, msg, details);
        TestModellerLogger.FailStepWithScreenshot(screen, msg, details);

        System.out.println("Test (" + ExtentReportManager.getTestName() + ") - Fail Step: " + msg + " - " + details);

        if (BasePage.StopOnFail) {
            Assert.fail(msg + " - " + details);
        }
    }

}
