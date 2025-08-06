package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * DataHandlingKeywords - keywords to handle reading/writing Excel, CSV, JSON files and related data utilities.
 */
public class DataHandlingKeywords {

	 private static final Logger logger = LoggerFactory.getLogger(DataHandlingKeywords.class);

	/**
	 * Validates that input parameter is not null or empty.
	 */
	private void validateInput(String param, String paramName) {
		if (param == null || param.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	// === Excel Handling ===

	/**
	 * Reads data from an Excel sheet and returns it as a List of Maps (each Map is a row with column header keys).
	 *
	 * @param filePath  Absolute path to Excel (.xlsx) file.
	 * @param sheetName Name of the sheet to read.
	 * @return List of Maps representing rows.
	 */
	@SuppressWarnings("deprecation")
	public List<Map<String, String>> readExcelSheet(String filePath, String sheetName) {
		validateInput(filePath, "Excel File Path");
		validateInput(sheetName, "Sheet Name");

		try {
			BaseClass.logActionStart("Reading Excel sheet '" + sheetName + "' from file '" + filePath + "'", "Excel Data");

			try (FileInputStream fis = new FileInputStream(filePath);
					Workbook workbook = new XSSFWorkbook(fis)) {

				Sheet sheet = workbook.getSheet(sheetName);
				if (sheet == null) {
					BaseClass.logActionFailure("Read Excel sheet", "Excel Data", "Sheet '" + sheetName + "' not found in file");
					throw new FrameworkException("Sheet '" + sheetName + "' not found in " + filePath);
				}

				Iterator<Row> rowIterator = sheet.rowIterator();
				List<Map<String, String>> rows = new ArrayList<>();
				List<String> headers = new ArrayList<>();

				if (!rowIterator.hasNext()) {
					BaseClass.logActionSuccess("Read Excel sheet (empty sheet)", "Excel Data");
					return rows; // empty sheet
				}

				// Read header row
				Row headerRow = rowIterator.next();
				for (Cell cell : headerRow) {
					headers.add(cell.getStringCellValue());
				}

				// Read data rows
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					Map<String, String> rowData = new LinkedHashMap<>();
					for (int i = 0; i < headers.size(); i++) {
						Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
						cell.setCellType(CellType.STRING);
						rowData.put(headers.get(i), cell.getStringCellValue());
					}
					rows.add(rowData);
				}

				logger.info("Read {} rows from Excel sheet '{}' in file {}", rows.size(), sheetName, filePath);
				BaseClass.logActionSuccess("Read " + rows.size() + " rows from Excel sheet '" + sheetName + "'", "Excel Data");
				return rows;
			}

		} catch (IOException e) {
			logger.error("Failed to read Excel file: " + filePath, e);
			BaseClass.logActionFailure("Read Excel sheet", "Excel Data", "IO error reading file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to read Excel file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Read Excel sheet", "Excel Data", "Failed to read Excel sheet: " + e.getMessage());
			throw new FrameworkException("Failed to read Excel sheet", e);
		}
	}

	/**
	 * Writes data to an Excel sheet from a List of Maps (each Map is a row).
	 *
	 * @param filePath  Absolute path to Excel (.xlsx) file.
	 * @param sheetName Name of the sheet to write (will be created or overwritten).
	 * @param data      List of Maps representing rows.
	 */
	public void writeExcelSheet(String filePath, String sheetName, List<Map<String, String>> data) {
		validateInput(filePath, "Excel File Path");
		validateInput(sheetName, "Sheet Name");

		if (data == null || data.isEmpty()) {
			BaseClass.logActionFailure("Write Excel sheet", "Excel Data", "Data for Excel writing is null or empty");
			throw new FrameworkException("Data for Excel writing is null or empty");
		}

		try {
			BaseClass.logActionStart("Writing " + data.size() + " rows to Excel sheet '" + sheetName + "' in file '" + filePath + "'", "Excel Data");

			try (Workbook workbook = new XSSFWorkbook()) {

				Sheet sheet = workbook.createSheet(sheetName);

				// Create header row
				Row headerRow = sheet.createRow(0);
				List<String> headers = new ArrayList<>(data.get(0).keySet());
				for (int i = 0; i < headers.size(); i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(headers.get(i));
				}

				// Create data rows
				for (int i = 0; i < data.size(); i++) {
					Row row = sheet.createRow(i + 1);
					Map<String, String> rowData = data.get(i);
					for (int j = 0; j < headers.size(); j++) {
						Cell cell = row.createCell(j);
						cell.setCellValue(rowData.get(headers.get(j)));
					}
				}

				// Write to file
				try (FileOutputStream fos = new FileOutputStream(filePath)) {
					workbook.write(fos);
				}

				logger.info("Wrote {} rows to Excel sheet '{}' in file {}", data.size(), sheetName, filePath);
				BaseClass.logActionSuccess("Wrote " + data.size() + " rows to Excel sheet '" + sheetName + "'", "Excel Data");
			}

		} catch (IOException e) {
			logger.error("Failed to write Excel file: " + filePath, e);
			BaseClass.logActionFailure("Write Excel sheet", "Excel Data", "IO error writing file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to write Excel file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Write Excel sheet", "Excel Data", "Failed to write Excel sheet: " + e.getMessage());
			throw new FrameworkException("Failed to write Excel sheet", e);
		}
	}

	// === CSV Handling ===

	/**
	 * Reads a CSV file and returns the data as a List of Maps (header to value).
	 *
	 * @param filePath Absolute path to CSV file.
	 * @param delimiter The delimiter used in CSV (e.g., ',').
	 * @return List of Maps representing rows.
	 */
	public List<Map<String, String>> readCSV(String filePath, char delimiter) {
		validateInput(filePath, "CSV File Path");

		try {
			BaseClass.logActionStart("Reading CSV file '" + filePath + "' with delimiter '" + delimiter + "'", "CSV Data");

			try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
				String headerLine = br.readLine();
				if (headerLine == null) {
					BaseClass.logActionFailure("Read CSV file", "CSV Data", "Empty CSV file: " + filePath);
					throw new FrameworkException("Empty CSV file: " + filePath);
				}

				String[] headers = headerLine.split(Character.toString(delimiter));
				List<Map<String, String>> data = new ArrayList<>();
				String line;

				while ((line = br.readLine()) != null) {
					String[] values = line.split(Character.toString(delimiter), -1);
					Map<String, String> row = new LinkedHashMap<>();
					for (int i = 0; i < headers.length; i++) {
						row.put(headers[i], i < values.length ? values[i] : "");
					}
					data.add(row);
				}

				logger.info("Read {} rows from CSV file {}", data.size(), filePath);
				BaseClass.logActionSuccess("Read " + data.size() + " rows from CSV file", "CSV Data");
				return data;
			}

		} catch (IOException e) {
			logger.error("Failed to read CSV file: " + filePath, e);
			BaseClass.logActionFailure("Read CSV file", "CSV Data", "IO error reading file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to read CSV file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Read CSV file", "CSV Data", "Failed to read CSV file: " + e.getMessage());
			throw new FrameworkException("Failed to read CSV file", e);
		}
	}

	/**
	 * Writes data to a CSV file from a List of Maps.
	 *
	 * @param filePath  Absolute path to CSV file.
	 * @param delimiter Delimiter to use in CSV.
	 * @param data      List of Maps representing rows.
	 */
	public void writeCSV(String filePath, char delimiter, List<Map<String, String>> data) {
		validateInput(filePath, "CSV File Path");

		if (data == null || data.isEmpty()) {
			BaseClass.logActionFailure("Write CSV file", "CSV Data", "Data for CSV writing is null or empty");
			throw new FrameworkException("Data for CSV writing is null or empty");
		}

		try {
			BaseClass.logActionStart("Writing " + data.size() + " rows to CSV file '" + filePath + "' with delimiter '" + delimiter + "'", "CSV Data");

			try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath))) {
				List<String> headers = new ArrayList<>(data.get(0).keySet());
				bw.write(headers.stream().collect(Collectors.joining(Character.toString(delimiter))));
				bw.newLine();

				for (Map<String, String> row : data) {
					String line = headers.stream()
							.map(h -> Optional.ofNullable(row.get(h)).orElse(""))
							.collect(Collectors.joining(Character.toString(delimiter)));
					bw.write(line);
					bw.newLine();
				}

				logger.info("Wrote {} rows to CSV file {}", data.size(), filePath);
				BaseClass.logActionSuccess("Wrote " + data.size() + " rows to CSV file", "CSV Data");
			}

		} catch (IOException e) {
			logger.error("Failed to write CSV file: " + filePath, e);
			BaseClass.logActionFailure("Write CSV file", "CSV Data", "IO error writing file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to write CSV file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Write CSV file", "CSV Data", "Failed to write CSV file: " + e.getMessage());
			throw new FrameworkException("Failed to write CSV file", e);
		}
	}

	// === JSON Handling ===

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Reads a JSON file and returns it as a JsonNode tree.
	 *
	 * @param filePath Absolute path to JSON file.
	 * @return JsonNode root of the parsed JSON.
	 */
	public JsonNode readJSON(String filePath) {
		validateInput(filePath, "JSON File Path");

		try {
			BaseClass.logActionStart("Reading JSON file '" + filePath + "'", "JSON Data");

			JsonNode root = objectMapper.readTree(new File(filePath));
			logger.info("Read JSON file {}", filePath);

			BaseClass.logActionSuccess("Read JSON file successfully", "JSON Data");
			return root;

		} catch (IOException e) {
			logger.error("Failed to read JSON file: " + filePath, e);
			BaseClass.logActionFailure("Read JSON file", "JSON Data", "IO error reading file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to read JSON file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Read JSON file", "JSON Data", "Failed to read JSON file: " + e.getMessage());
			throw new FrameworkException("Failed to read JSON file", e);
		}
	}

	/**
	 * Writes a JsonNode to a JSON file.
	 *
	 * @param filePath Absolute path to JSON file.
	 * @param root     JsonNode to write.
	 */
	public void writeJSON(String filePath, JsonNode root) {
		validateInput(filePath, "JSON File Path");
		if (root == null) {
			BaseClass.logActionFailure("Write JSON file", "JSON Data", "JsonNode to write cannot be null");
			throw new FrameworkException("JsonNode to write cannot be null");
		}

		try {
			BaseClass.logActionStart("Writing JSON data to file '" + filePath + "'", "JSON Data");

			objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), root);
			logger.info("Wrote JSON file {}", filePath);

			BaseClass.logActionSuccess("Wrote JSON file successfully", "JSON Data");

		} catch (IOException e) {
			logger.error("Failed to write JSON file: " + filePath, e);
			BaseClass.logActionFailure("Write JSON file", "JSON Data", "IO error writing file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to write JSON file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Write JSON file", "JSON Data", "Failed to write JSON file: " + e.getMessage());
			throw new FrameworkException("Failed to write JSON file", e);
		}
	}

	// === Utility Methods ===

	/**
	 * Parses a JSON file and returns a value for the given JSON Pointer expression.
	 *
	 * @param filePath      Absolute path to JSON file.
	 * @param jsonPointer   JSON Pointer expression (e.g., "/data/user/name").
	 * @return Value as string or null if not found.
	 */
	public String readJSONPointerValue(String filePath, String jsonPointer) {
		validateInput(filePath, "JSON File Path");
		validateInput(jsonPointer, "JSON Pointer");

		try {
			BaseClass.logActionStart("Reading JSON Pointer value '" + jsonPointer + "' from file '" + filePath + "'", "JSON Data");

			JsonNode root = readJSON(filePath);
			JsonNode node = root.at(jsonPointer);

			if (node.isMissingNode() || node.isNull()) {
				logger.info("JSON Pointer '{}' not found in file {}", jsonPointer, filePath);
				BaseClass.logActionSuccess("JSON Pointer '" + jsonPointer + "' not found (returning null)", "JSON Data");
				return null;
			}

			String val = node.asText();
			logger.info("Got JSON Pointer value '{}' from {}", val, jsonPointer);
			BaseClass.logActionSuccess("Retrieved JSON Pointer value: '" + BaseClass.mask(val) + "'", "JSON Data");
			return val;

		} catch (Exception e) {
			BaseClass.logActionFailure("Read JSON Pointer value", "JSON Data", "Failed to read JSON Pointer '" + jsonPointer + "': " + e.getMessage());
			throw new FrameworkException("Failed to read JSON Pointer value", e);
		}
	}

	/**
	 * Reads all lines from a plain text file.
	 *
	 * @param filePath Absolute path to text file.
	 * @return List of lines.
	 */
	public List<String> readTextFileLines(String filePath) {
		validateInput(filePath, "Text File Path");

		try {
			BaseClass.logActionStart("Reading text file lines from '" + filePath + "'", "Text File Data");

			List<String> lines = Files.readAllLines(Paths.get(filePath));
			logger.info("Read {} lines from text file {}", lines.size(), filePath);

			BaseClass.logActionSuccess("Read " + lines.size() + " lines from text file", "Text File Data");
			return lines;

		} catch (IOException e) {
			logger.error("Failed to read text file: " + filePath, e);
			BaseClass.logActionFailure("Read text file lines", "Text File Data", "IO error reading file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to read text file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Read text file lines", "Text File Data", "Failed to read text file: " + e.getMessage());
			throw new FrameworkException("Failed to read text file", e);
		}
	}

	/**
	 * Writes lines to a plain text file.
	 *
	 * @param filePath Absolute path to text file.
	 * @param lines    Lines to write.
	 */
	public void writeTextFileLines(String filePath, List<String> lines) {
		validateInput(filePath, "Text File Path");
		if (lines == null) {
			BaseClass.logActionFailure("Write text file lines", "Text File Data", "Lines to write cannot be null");
			throw new FrameworkException("Lines to write cannot be null");
		}

		try {
			BaseClass.logActionStart("Writing " + lines.size() + " lines to text file '" + filePath + "'", "Text File Data");

			Files.write(Paths.get(filePath), lines);
			logger.info("Wrote {} lines to text file {}", lines.size(), filePath);

			BaseClass.logActionSuccess("Wrote " + lines.size() + " lines to text file", "Text File Data");

		} catch (IOException e) {
			logger.error("Failed to write text file: " + filePath, e);
			BaseClass.logActionFailure("Write text file lines", "Text File Data", "IO error writing file '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to write text file: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Write text file lines", "Text File Data", "Failed to write text file: " + e.getMessage());
			throw new FrameworkException("Failed to write text file", e);
		}
	}
}
