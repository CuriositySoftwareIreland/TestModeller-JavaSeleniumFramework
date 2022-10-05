package pages;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import groovy.lang.GString;
import ie.curiositysoftware.datacatalogue.DataListRowDto;
import ie.curiositysoftware.datacatalogue.services.DataCatalogueTestCriteriaExecutionService;
import ie.curiositysoftware.jobengine.services.ConnectionProfile;
import ie.curiositysoftware.tdm.services.DataListService;
import ie.curiositysoftware.testdata.TestDataResolver;
import ie.curiositysoftware.testmodeller.TestModellerIgnore;
import ie.curiositysoftware.utils.PageImplToHashMap;
import ie.curiositysoftware.utils.UnirestHelper;
import org.apache.poi.ss.formula.functions.T;
import org.openqa.selenium.WebDriver;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import utilities.ConnectionManager;
import utilities.PropertiesLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class TestDataActions extends BasePage
{
    @TestModellerIgnore
    public TestDataActions(WebDriver driver) {
        super(driver);
    }

    /**
     * @name Resolve Test Data Expression
     */
    public String resolveDataExpression(String expression)
    {
        ConnectionProfile connectionProfile = new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey"));

        TestDataResolver dataResolver = new TestDataResolver(connectionProfile);

        return dataResolver.ResolveTestDataValue(expression);
    }
    /**
     * @name Get Data Using List Test Criteria for Row and Column
     */
    public String getDataUsingListTestCriteriaForRowAndColumn
            (Long catalogueID, long testCriteriaID, Pageable pageable, Map<String, String> parameters, int row, String column)
    {
        return getDataUsingListTestCriteria(catalogueID, testCriteriaID, pageable, parameters).get(row).get(column);
    }

    /**
     * @name Get Data Using List Test Criteria
     */
    public List<HashMap<String, String>> getDataUsingListTestCriteria
            (Long catalogueID, long testCriteriaID, Pageable pageable, Map<String, String> parameters)
    {
        UnirestHelper.initUnirestMapper();

        ConnectionProfile connectionProfile = new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey"));
        DataCatalogueTestCriteriaExecutionService testCriteriaExecutionService = new DataCatalogueTestCriteriaExecutionService(connectionProfile);

        PageImpl<DataListRowDto> listRowDtoPage = testCriteriaExecutionService.GetDataListRows(catalogueID, testCriteriaID, pageable, parameters);

        PageImplToHashMap converter = new PageImplToHashMap();

        return converter.convert(listRowDtoPage);
    }

    /**
     * @name Get List Data
     */
    public List<HashMap<String, String>> getListData
            (Long listID, String query, String select)
    {
        UnirestHelper.initUnirestMapper();

        ConnectionProfile connectionProfile = new ConnectionProfile(PropertiesLoader.getProperties().getProperty("testModeller.apiHost"), PropertiesLoader.getProperties().getProperty("testModeller.apiKey"));
        DataListService dataListService = new DataListService(connectionProfile);

        PageImpl<DataListRowDto> listRowDtoPage = dataListService.GetDataListRows(listID, query, select);

        PageImplToHashMap converter = new PageImplToHashMap();

        return converter.convert(listRowDtoPage);
    }

    /**
     * @name Get List Data for Row and Column
     */
    public String getListDataForRowAndColumn
            (Long listID, String query, String select, int row, String column)
    {
        return getListData(listID, query, select).get(row).get(column);
    }

    /**
     * @name Get CSV Data for Row and Column
     */
    public String getCsvDataForRowAndColumn
            (String csvFilePath, int row, String column) throws CsvValidationException, IOException {
        return getCsvData(csvFilePath).get(row).get(column);
    }

    /**
     * @name Get CSV Data
     */
    public List<HashMap<String, String>> getCsvData(String csvFilePath) throws IOException, CsvValidationException
    {
            ArrayList<HashMap<String, String>> dataItems = new ArrayList<>();
            // Create an object of file reader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(csvFilePath);

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .build();

            String[] headers = csvReader.readNext();

        //Getting the iterator object for this reader
            Iterator it = csvReader.iterator();
            while (it.hasNext()) {
                String[] data = (String[]) it.next();
                HashMap<String, String> items = new HashMap<>();
                for (int i = 0; i < Arrays.stream(data).count(); i++) {

                    items.put(headers[i], data[i]);
                }

                dataItems.add(items);
            }

            return dataItems;
    }

    /**
     * @name Get SQL Data for Row and Column
     */
    public String getSqlDataForRowAndColumn
            (String driverName, String urlString, String username, String password, String query, int row, String column) throws ClassNotFoundException, SQLException
    {
        return getSqlData(driverName, urlString, username, password, query).get(row).get(column);
    }

    /**
     * @name Get SQL Data
     */
    public List<HashMap<String, String>> getSqlData(String driverName, String urlString, String username, String password, String query) throws ClassNotFoundException, SQLException
    {
        ArrayList<HashMap<String, String>> dataItems = new ArrayList<>();

        ConnectionManager manager = new ConnectionManager();
        Connection connection = manager.getConnection(driverName, urlString, username, password);

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();

        while (rs.next()) {
            HashMap<String, String> row = new HashMap();
            for (int i = 1; i <= columns; i++)
            {
                String data = "";
                if (rs.getObject(i) != null)
                {
                    data = rs.getObject(i).toString();
                }
                row.put(md.getColumnName(i), data);
            }

            dataItems.add(row);
        }

        connection.close();

        return dataItems;
    }
}
