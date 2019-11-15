package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static Properties prop = new Properties();

    private static boolean propertiesLoaded = false;

    public static Properties getProperties()
    {
        if (!propertiesLoaded) {
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
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("project.properties"))
        {
            prop.load(inputStream);

            propertiesLoaded = true;
        }
    }
}
