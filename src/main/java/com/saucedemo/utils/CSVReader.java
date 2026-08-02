package com.saucedemo.utils;

import com.opencsv.exceptions.CsvException;
import com.saucedemo.exceptions.FrameworkException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * CSVReader provides reusable methods for reading CSV test-data files (OpenCSV-backed),
 * used for Data-Driven Testing alongside {@link ExcelUtility} and {@link JSONReader}.
 */
public class CSVReader {

    private CSVReader() {
        // Prevent instantiation
    }

    /**
     * Reads all rows of a CSV file, including the header row, as raw String arrays.
     *
     * @param filePath path to the CSV file
     * @return list of rows, each row a String[] of column values
     */
    public static List<String[]> readAllRows(String filePath) {
        try (FileReader fileReader = new FileReader(filePath);
             com.opencsv.CSVReader reader = new com.opencsv.CSVReader(fileReader)) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            throw new FrameworkException("Failed to read CSV file: " + filePath, e);
        }
    }

    /**
     * Reads a CSV file (with a header row) and returns only the data rows
     * (header excluded), ready for a TestNG {@code @DataProvider}.
     *
     * @param filePath path to the CSV file
     * @return 2D Object array of data rows, suitable for {@code @DataProvider} return type
     */
    public static Object[][] getTestData(String filePath) {
        List<String[]> allRows = readAllRows(filePath);
        if (allRows.isEmpty()) {
            return new Object[0][0];
        }

        List<String[]> dataRows = allRows.subList(1, allRows.size()); // skip header
        Object[][] data = new Object[dataRows.size()][];
        for (int i = 0; i < dataRows.size(); i++) {
            data[i] = dataRows.get(i);
        }
        return data;
    }
}
