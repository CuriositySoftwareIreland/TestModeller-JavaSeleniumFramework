package utilities;

import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.jobengine.services.job.CodeGenerationService;

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
        defaults.setProperty("testModeller.analyser.jobTimeout", "120000");
        defaults.setProperty("testModeller.analyser.codeTemplateId", CodeGenerationService.DEFAULT_JAVA_TEMPLATE_ID.toString());
        defaults.setProperty("testModeller.analyser.includeOldTests", "true");

        prop = new Properties(defaults);

        // Load properties from project.properties
        try (InputStream inputStream = PropertiesLoader.class.getResourceAsStream("/project.properties"))
        {
            prop.load(inputStream);
        }

        // Override with System properties (command-line properties)
        for (String key : prop.stringPropertyNames()) {
            String systemPropertyValue = System.getProperty(key);
            if (systemPropertyValue != null) {
                prop.setProperty(key, systemPropertyValue);
            }
        }
    }

    public static Boolean isDebug()
    {
        return Boolean.parseBoolean(getProperties().getProperty("testModeller.debug"));
    }

    public static Boolean isSmartLocators()
    {
        return Boolean.parseBoolean(getProperties().getProperty("testModeller.smartLocators"));
    }

    public static Boolean isGPTLocators()
    {
        return Boolean.parseBoolean(getProperties().getProperty("testModeller.gptLocators"));
    }

    public static Boolean isHeadless()
    {
        return Boolean.parseBoolean(getProperties().getProperty("testModeller.headless"));
    }

    public static ConnectionProfile getConnectionProfile()
    {
        return new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey"));
    }

    public static Long getReleaseId()
    {
        return Long.parseLong(PropertiesLoader.getProperties().getProperty("testModeller.releaseId"));
    }
}
