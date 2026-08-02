package com.saucedemo.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ExcelDataInitializer creates sample testdata.xlsx file for Data-Driven Testing.
 */
public class ExcelDataInitializer {

    public static void main(String[] args) {
        createTestDataExcel("src/test/resources/testdata.xlsx");
    }

    public static void createTestDataExcel(String targetFilePath) {
        File file = new File(targetFilePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("LoginData");

            String[][] excelData = {
                {"Username", "Password", "ExpectedResult", "ExpectedErrorMessage"},
                {"standard_user", "secret_sauce", "success", ""},
                {"locked_out_user", "secret_sauce", "locked", "Epic sadface: Sorry, this user has been locked out."},
                {"problem_user", "secret_sauce", "success", ""},
                {"performance_glitch_user", "secret_sauce", "success", ""},
                {"invalid_user", "invalid_password", "invalid", "Epic sadface: Username and password do not match any user in this service"},
                {"error_user", "secret_sauce", "success", ""},
                {"visual_user", "secret_sauce", "success", ""}
            };

            for (int r = 0; r < excelData.length; r++) {
                Row row = sheet.createRow(r);
                for (int c = 0; c < excelData[r].length; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellValue(excelData[r][c]);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                LoggerUtility.info("Test data Excel file created successfully at: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            LoggerUtility.error("Failed to create test data Excel file.", e);
        }
    }
}
