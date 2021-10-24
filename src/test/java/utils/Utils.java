package utils;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    private static XSSFWorkbook ExcelWBook;
    private static XSSFSheet ExcelWSheet;
    private static XSSFCell Cell;
    private static XSSFRow Row;

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


    /**
     * @description a methods which gets and reads an excel file sheet
     * @param filePath
     * @param sheetName
     * @return data object[][] - which is actually the sheet data
     */
    public static Object[][] getDataFromExcel(String filePath, String sheetName) {
        Object[][] data = null;
        try {
            fis = new FileInputStream(filePath);
            // Access the required test data sheet
            ExcelWBook = new XSSFWorkbook(fis);
            ExcelWSheet = ExcelWBook.getSheet(sheetName);
            int startRow = 1;
            int startCol = 0;
            int totRows = ExcelWSheet.getLastRowNum();
            Row = ExcelWSheet.getRow(0);
            int totCols = Row.getLastCellNum();
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


    /**
     * a method which gets a cell and returns its value
     * @param rowNum - int
     * @param colNUm - int
     * @return - string - cell value
     */
    public static String getCellValue(int rowNum, int colNUm){
        Cell = ExcelWSheet.getRow(rowNum).getCell(colNUm);
        Cell.setCellType(CellType.STRING);
        CellType cellType = Cell.getCellType();
        if(cellType == CellType.BLANK)
            return "";
        else return Cell.getStringCellValue();
    }
}
