package utilities.testmodeller;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import ie.curiositysoftware.jobengine.dto.job.AutomationExecutionParameter;
import ie.curiositysoftware.jobengine.dto.job.Job;
import ie.curiositysoftware.jobengine.dto.job.JobType;
import ie.curiositysoftware.jobengine.dto.job.ServerProcessScopeEnum;
import ie.curiositysoftware.jobengine.dto.job.settings.VIPAutomationExecutionJobSettings;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.jobengine.utils.JobExecutor;
import ie.curiositysoftware.utils.UnirestHelper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import utilities.PropertiesLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TDADataParser {
    private static HashMap<String, HashMap<String, String>> TestCaseData;

    public static void LoadAndSetCurrentSheet(long testSuiteId, long dataPoolId, String testName) throws IOException, CsvValidationException {
        // Run the job
        UnirestHelper.initUnirestMapper();
        ConnectionProfile cp = new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey"));


        Job job = new Job();
        job.setJobType(JobType.VIPAutoExecutionJob);
        job.setVipAutomationJobSettings(new VIPAutomationExecutionJobSettings());
        job.getVipAutomationJobSettings().setMachineKey(PropertiesLoader.getProperties().getProperty("testModeller.serverName"));
        job.getVipAutomationJobSettings().setAutomationType("Create Data Sheet in Modeller");

        job.getVipAutomationJobSettings().setTestSuiteId(testSuiteId);
        job.getVipAutomationJobSettings().setScope(ServerProcessScopeEnum.ModelTestSuite);
        job.getVipAutomationJobSettings().setSharedJobServer(false);
        job.getVipAutomationJobSettings().setAutomationParameters(new ArrayList<AutomationExecutionParameter>());

        // Name
        AutomationExecutionParameter nameParam = new AutomationExecutionParameter();
        nameParam.setVar("par3");
        nameParam.setValue("job{{{parModellerJobId}}}-" + testName + ".xlsx");
        nameParam.setParamIndex(1);
        job.getVipAutomationJobSettings().getAutomationParameters().add(nameParam);

        // Folder location
        AutomationExecutionParameter folderLocationParam = new AutomationExecutionParameter();
        folderLocationParam.setVar("par1");
        folderLocationParam.setValue("{\"type\":\"Folder\",\"release_id\":" + PropertiesLoader.getProperties().getProperty("testModeller.releaseId") + ",\"folder_id\":" + PropertiesLoader.getProperties().getProperty("testModeller.folderId") + "}");
        folderLocationParam.setParamIndex(2);
        job.getVipAutomationJobSettings().getAutomationParameters().add(folderLocationParam);

        // Pool ID
        AutomationExecutionParameter poolIdParam = new AutomationExecutionParameter();
        poolIdParam.setVar("parPoolID");
        poolIdParam.setValue(Long.toString(dataPoolId));
        poolIdParam.setParamIndex(3);
        job.getVipAutomationJobSettings().getAutomationParameters().add(poolIdParam);

        // 4 - Submit job
        JobExecutor jobExecutor = new JobExecutor(cp);

        String dataDir = System.getProperty("user.dir") +"/data/" + testName + ".zip";
        if (!jobExecutor.executeJob(job, dataDir, 10000000000l)) {
            System.out.println(jobExecutor.getErrorMessage());

            return;
        }

        // Unzip sheet
        String destination = System.getProperty("user.dir") +"/data/" + testName;
        FileUtils.deleteDirectory(new File(destination));
        try {
            ZipFile zipFile = new ZipFile(dataDir);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        // Find all
        TestCaseData = new HashMap<String, HashMap<String, String>>();

        List<String> result;
        try (Stream<Path> walk = Files.walk(Paths.get(destination))) {
            result = walk
                    .filter(p -> !Files.isDirectory(p))   // not a directory
                    .map(p -> p.toString().toLowerCase()) // convert path to string
                    .filter(f -> f.endsWith("_control.csv"))       // check end with
                    .collect(Collectors.toList());        // collect all matched to a List
        }

        // For each CSV
        for (String csvLoc : result) {
            List<List<String>> records = new ArrayList<List<String>>();
            try (CSVReader csvReader = new CSVReader(new FileReader(csvLoc));) {
                String[] values = null;
                while ((values = csvReader.readNext()) != null) {
                    records.add(Arrays.asList(values));
                }
            }

            if (records.isEmpty())
                continue;

            ArrayList<String> columnHeaders = new ArrayList<String>();

            for (String header : records.get(0)) {
                if (!columnHeaders.contains(header))
                    columnHeaders.add(header);
            }

            for (int j = 1; j < records.size(); j++) {
                String currentName = records.get(j).get(columnHeaders.indexOf("ModelPathGUID"));

                if (!TestCaseData.containsKey(currentName)) {
                    TestCaseData.put(currentName, new HashMap<String, String>());
                }

                for (String colHeader : columnHeaders) {
                    TestCaseData.get(currentName).put(colHeader, records.get(j).get(columnHeaders.indexOf(colHeader)));
                }
            }
        }
    }

    public static String GetDataValue(String pathGuid, String colName) {
        if (TestCaseData == null || !TestCaseData.containsKey(pathGuid)) {
            return "";
        }

        if (!TestCaseData.get(pathGuid).containsKey(colName))
            return "";

        return TestCaseData.get(pathGuid).get(colName);
    }
}
