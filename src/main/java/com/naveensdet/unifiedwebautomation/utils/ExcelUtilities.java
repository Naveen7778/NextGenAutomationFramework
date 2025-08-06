package com.naveensdet.unifiedwebautomation.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.naveensdet.unifiedwebautomation.keywords.InputKeywords;

@SuppressWarnings("unused")
public class ExcelUtilities {

	private static Properties properties = new Properties();
	// Update logger declaration:
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ExcelUtilities.class);

	static {
		try (InputStream input = ExcelUtilities.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				throw new RuntimeException("config.properties file not found in classpath");
			}
			properties.load(input);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load config.properties", e);
		}
	}

	// Method to get any property by key
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	// Get Excel data file path from config.properties or fallback default
	public static String getExcelDataFilePath() {
		String path = getProperty("excelDataFile");
		// Provide a default path if not set in properties
		return (path != null && !path.trim().isEmpty()) 
				? path.trim() 
						: "src/test/resources/testdata/data.xlsx";
	}

	/**
	 * Retrieves the value for a given testCaseName and key assuming single row with alternating key-value pairs.
	 */
	public static String getData(String testCaseName, String key) {
		String dataFilePath = getExcelDataFilePath();

		try (FileInputStream fis = new FileInputStream(dataFilePath);
				Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0);  // or specify the sheet by name if needed

			int testCaseColIndex = 0; // First column with test case names

			for (Row row : sheet) {
				if (row.getRowNum() == 0) {
					// skip header row if it exists -- remove this if your sheet has no header
					continue;
				}

				Cell testCaseCell = row.getCell(testCaseColIndex);
				if (testCaseCell == null) continue;

				if (testCaseName.equalsIgnoreCase(getCellValueAsString(testCaseCell))) {
					// Iterate pairs of columns starting from 1: col=1,3,5,... keys and col=2,4,6,... values
					for (int col = 1; col < row.getLastCellNum(); col += 2) {
						Cell keyCell = row.getCell(col);
						Cell valueCell = row.getCell(col + 1);

						if (keyCell != null && key.equalsIgnoreCase(getCellValueAsString(keyCell))) {
							return getCellValueAsString(valueCell);
						}
					}
					return null; // key not found for this test case
				}
			}
			return null; // test case not found

		} catch (Exception e) {
			throw new RuntimeException("Error reading Excel data file: " + dataFilePath, e);
		}
	}

	/**
	 * Converts a given Excel cell's content to its String representation safely.
	 */
	private static String getCellValueAsString(Cell cell) {
		if (cell == null) return "";
		switch (cell.getCellType()) {
		case STRING: return cell.getStringCellValue().trim();
		case NUMERIC: 
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				return String.valueOf(cell.getNumericCellValue());
			}
		case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
		case FORMULA: 
			return cell.getCellFormula(); // or consider evaluating formula
		default: return "";
		}
	}

}

