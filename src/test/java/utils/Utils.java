package utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Utils class as an assistance class which includes methods to read/write data from files
 * */
public class Utils {
    // objects
    private static FileInputStream fis;
    private static HSSFWorkbook ExcelWBook;
    private static HSSFSheet ExcelWSheet;
    private static HSSFCell Cell;

    // variables

    // methods
    /**
     * this method reads a property from a configuration.properties file and return its value
     * @param - a String key property name
     * @return - property value
     * */
    public static String readProperty(String key){
        String value = "";
        try {
            fis = new FileInputStream(".\\src\\test\\resources\\data\\configuration.properties");
            Properties prop = new Properties();
            prop.load(fis); // load a properties file
            value = prop.getProperty(key); // get the property value
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Object[][] getDataFromExcel(String filePath, String sheetName) {
        Object[][] data = null;
        try {
            fis = new FileInputStream(filePath);
            // Access the required test data sheet
            ExcelWBook = new HSSFWorkbook(fis);
            ExcelWSheet = ExcelWBook.getSheet(sheetName);
            int startRow = 1;
            int startCol = 0;
            int totRows = ExcelWSheet.getLastRowNum();
            org.apache.poi.ss.usermodel.Row r = ExcelWSheet.getRow(0);
            int totCols = r.getLastCellNum();
            data = new Object[totRows][totCols];
            int di = 0;
            for (int i = startRow; i <= totRows; di++,i++) {
                int dj = 0;
                for (int j = startCol; j < totCols; dj++,j++) {
                    data[di][dj] = getCellValue(i,j);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not read the Excel sheet");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read the Excel sheet");
            e.printStackTrace();
        }
        return data;
    }

    public static String getCellValue(int rowNum, int colNUm){
        Cell = ExcelWSheet.getRow(rowNum).getCell(colNUm);
        Cell.setCellType(CellType.STRING);
        CellType cellType = Cell.getCellType();
        if(cellType == CellType.BLANK)
            return "";
        else return Cell.getStringCellValue();
    }
}
