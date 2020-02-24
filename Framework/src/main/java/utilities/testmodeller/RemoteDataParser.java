package utilities.testmodeller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class RemoteDataParser {
    private static String DataFileLocation = "data/resolved/";

    private static List<String> ColumnHeaders;

    private static HashMap<String, List<Row>> TestCaseData;

    public static void SetCurrentSheet(String sheet) throws IOException {
        // Parse the sheet
        FileInputStream excelFile = new FileInputStream(new File(DataFileLocation + sheet + ".xlsx"));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheet("data");

        // Get headers
        ColumnHeaders = new ArrayList<String>();
        Row headerRow = datatypeSheet.getRow(0);
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            ColumnHeaders.add(headerRow.getCell(i).toString());
        }

        // Parse data
        TestCaseData = new HashMap<String, List<Row>>();
        for (int i = 1; i < datatypeSheet.getPhysicalNumberOfRows(); i++) {
            String currentName = datatypeSheet.getRow(i).getCell(ColumnHeaders.indexOf("TestName")).getStringCellValue();

            if (!TestCaseData.containsKey(currentName)) {
                TestCaseData.put(currentName, new ArrayList<Row>());
            }

            TestCaseData.get(currentName).add(datatypeSheet.getRow(i));
        }
    }

    public static String GetDataValue(String testName, String columnName, int iterationNo)
    {
        Cell curCell = TestCaseData.get(testName).get(iterationNo).getCell(ColumnHeaders.indexOf(columnName));

        String value = getCellValue(curCell);

        return value;
    }

    public static int GetNoIterations(String testName)
    {
        return TestCaseData.get(testName).size();
    }

    public static String getCellValue(Cell curCell)
    {
        CellType cellType = curCell.getCellTypeEnum();

        switch(cellType) {
            case NUMERIC:
                return Double.toString(curCell.getNumericCellValue());
            case STRING:
                return curCell.getStringCellValue();
            case BOOLEAN:
                return Boolean.toString(curCell.getBooleanCellValue());
            case FORMULA:
                return curCell.getStringCellValue();
            default:
                return "";
        }
    }
}