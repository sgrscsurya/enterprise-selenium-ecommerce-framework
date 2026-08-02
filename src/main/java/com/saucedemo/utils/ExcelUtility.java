package com.saucedemo.utils;

import com.saucedemo.exceptions.FrameworkException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Function;

/**
 * ExcelUtility provides reusable methods for reading and writing Excel spreadsheets (.xlsx)
 * using Apache POI for Data-Driven Testing (DDT).
 */
public class ExcelUtility {

    private final String filePath;

    public ExcelUtility(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Opens the workbook, resolves the named sheet, and applies {@code action} to it.
     * Centralizes the open/close/error-handling boilerplate previously duplicated across
     * {@link #getRowCount}, {@link #getCellCount}, and {@link #getCellData}.
     *
     * @param sheetName    sheet to resolve within the instance's Excel file
     * @param defaultValue value returned if the sheet is missing or a read error occurs
     * @param action       transformation applied to the resolved sheet
     */
    private <T> T withSheet(String sheetName, T defaultValue, Function<Sheet, T> action) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = WorkbookFactory.create(fis)) {
            Sheet sh = wb.getSheet(sheetName);
            return (sh != null) ? action.apply(sh) : defaultValue;
        } catch (IOException e) {
            LoggerUtility.error("Error accessing sheet: " + sheetName + " in file: " + filePath, e);
            return defaultValue;
        }
    }

    /**
     * Reads test data from specified sheet and returns a 2D Object array suitable for TestNG @DataProvider.
     *
     * @param filePath Absolute or relative path to Excel file
     * @param sheetName Name of the sheet to read
     * @return 2D Object array containing row data
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sh = wb.getSheet(sheetName);
            if (sh == null) {
                throw new FrameworkException("Sheet '" + sheetName + "' not found in excel file: " + filePath);
            }

            int rowCount = sh.getLastRowNum(); // 0-indexed, excludes row count if headers are row 0
            int colCount = sh.getRow(0).getLastCellNum();

            Object[][] data = new Object[rowCount][colCount];
            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= rowCount; i++) {
                Row row = sh.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    if (row == null) {
                        data[i - 1][j] = "";
                    } else {
                        Cell cell = row.getCell(j);
                        data[i - 1][j] = formatter.formatCellValue(cell);
                    }
                }
            }
            return data;
        } catch (IOException e) {
            LoggerUtility.error("Failed to read Excel test data from file: " + filePath + ", sheet: " + sheetName, e);
            throw new FrameworkException("Excel data read error for file: " + filePath, e);
        }
    }

    /**
     * Returns total row count in a given sheet.
     */
    public int getRowCount(String sheetName) {
        return withSheet(sheetName, 0, Sheet::getLastRowNum);
    }

    /**
     * Returns column count for a specific row in a sheet.
     */
    public int getCellCount(String sheetName, int rowNum) {
        return withSheet(sheetName, 0, sh -> {
            Row row = sh.getRow(rowNum);
            return (row != null) ? row.getLastCellNum() : 0;
        });
    }

    /**
     * Reads a specific cell data value as string.
     */
    public String getCellData(String sheetName, int rowNum, int colNum) {
        return withSheet(sheetName, "", sh -> {
            Row row = sh.getRow(rowNum);
            if (row == null) {
                return "";
            }
            Cell cell = row.getCell(colNum);
            return new DataFormatter().formatCellValue(cell);
        });
    }
}
