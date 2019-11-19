package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static Properties prop;

    public static Properties getProperties()
    {
        if (prop == null) {
            try {
                PropertiesLoader.readProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return prop;
    }


    private static void readProperties() throws IOException
    {
        Properties defaults = new Properties();
        defaults.setProperty("testModeller.uploadResults", "true");
        defaults.setProperty("testModeller.analyseFailures", "false");
        defaults.setProperty("testModeller.jobTimeout", "30000");

        prop = new Properties(defaults);

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("project.properties"))
        {
            prop.load(inputStream);
        }
    }
}
