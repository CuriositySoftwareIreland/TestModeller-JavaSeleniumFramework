package utilities.testmodeller;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.sikuli.script.Screen;
import org.sikuli.script.ScreenImage;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class GetScreenShot {

    public static String captureAsImage(WebDriver driver,String screenShotName) throws IOException
    {
		if (driver == null)
			return "";
		
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        String dest = "report/"  +"/ErrorScreenshots/"+screenShotName+".png";
        File destination = new File(dest);
        FileUtils.copyFile(source, destination);

        return "ErrorScreenshots/"+screenShotName+".png";
    }

    public static String captureAsBase64(WebDriver driver)
    {
		if (driver == null)
			return "";

        return "data:image/png;base64," + ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    public static byte[] captureAsByteArray(WebDriver driver)
    {
		if (driver == null)
			return null;

        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public static String captureAsBase64(Screen screen) {
        try {
            // Capture the screenshot with SikuliX
            ScreenImage screenshot = screen.capture();

            // Convert the screenshot to base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot.getImage(), "png", baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            System.err.println("Error converting screenshot to base64: " + e.getMessage());
            return "";
        }
    }

    public static byte[] captureAsByteArray(Screen screen) {
        try {
            // Capture the screenshot with SikuliX
            ScreenImage screenshot = screen.capture();

            // Convert the screenshot to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot.getImage(), "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            System.err.println("Error converting screenshot to byte array: " + e.getMessage());
            return null;
        }
    }
}