package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.ConfigManager;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExcelUtilities;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

/**
 * UploadDownloadKeywords - keywords to handle file upload and download scenarios.
 */
public class UploadDownloadKeywords {

	private final WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(UploadDownloadKeywords.class);

	public UploadDownloadKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/** Get int config property or use default for polling interval. */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) return defaultValue;
		try {
			return Integer.parseInt(BaseClass.getProps().getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid config for '{}', default {} used", key, defaultValue);
			return defaultValue;
		}
	}

	/** Validate input parameter string, else throw FrameworkException. */
	private void validateInput(String param, String paramName) {
		if (param == null || param.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	/** Returns configured FluentWait with polling and timeout. */
	private FluentWait<WebDriver> getWait(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(pollingMillis))
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	/**
	 * Helper to return input value from Excel if excelData is true; else return inputValueOrKey.
	 */
	private String getInputValue(String testName, String inputValueOrKey, String elementName, boolean excelData) {
		if (!excelData) {
			return inputValueOrKey;
		}
		String excelValue = ExcelUtilities.getData(testName, inputValueOrKey);
		if (excelValue == null || excelValue.trim().isEmpty()) {
			logger.warn("No Excel data found for test case [{}], key [{}]. Using empty string.", testName, inputValueOrKey);
			return "";
		} else {
			logger.info("Using Excel data for element [{}]: {}", elementName, excelValue);
			return excelValue;
		}
	}

	// ==== FILE UPLOAD ====

	/**
	 * Upload a file by sending the full file path to a file input element located by XPath.
	 * Supports fetching the file path from Excel based on the testCaseName if excelData is true.
	 *
	 * @param xpath XPath of the file input element (type='file').
	 * @param absoluteFilePath Absolute path to the file on local machine (or parameter name if using Excel).
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 */
	public void uploadFileByInput(String xpath, String absoluteFilePath, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(absoluteFilePath, "File Path");

		// Get the actual file path to use (from Excel or direct input)
		String filePathToUse = getInputValue(testCaseName, absoluteFilePath, "File Path", excelData);

		File file = new File(filePathToUse);
		if (!file.exists()) {
			BaseClass.logActionFailure("Upload file by input", "File Upload", "File does not exist: " + filePathToUse);
			throw new FrameworkException("File does not exist: " + filePathToUse);
		}

		try {
			BaseClass.logActionStart("Uploading file '" + BaseClass.mask(filePathToUse) + "' via input element", "File Upload");

			WebElement fileInput = getWait(10).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			fileInput.sendKeys(file.getAbsolutePath());
			logger.info("Uploaded file '{}' via input [{}]", filePathToUse, xpath);

			BaseClass.logActionSuccess("File uploaded successfully via input element", "File Upload");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Upload file by input", "File Upload", "File input element not found: " + xpath + " - " + e.getMessage());
			throw new FrameworkException("File input element not found: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Upload file by input", "File Upload", "Failed to upload file: " + e.getMessage());
			throw new FrameworkException("Failed to upload file via input", e);
		}
	}


	/**
	 * Upload a file using Robot class for native OS dialog in Windows.
	 * WARNING: Works only on Windows OS focused desktop.
	 * Supports fetching the file path from Excel based on the testCaseName if excelData is true.
	 *
	 * @param absoluteFilePath Absolute path to the file (or parameter name if using Excel).
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if error occurs.
	 */
	public void uploadFileUsingRobot(String absoluteFilePath, boolean excelData, String testCaseName) {
		validateInput(absoluteFilePath, "File Path");

		// Get the actual file path to use (from Excel or direct input)
		String filePathToUse = getInputValue(testCaseName, absoluteFilePath, "File Path", excelData);

		File file = new File(filePathToUse);
		if (!file.exists()) {
			BaseClass.logActionFailure("Upload file using Robot", "File Upload", "File does not exist: " + filePathToUse);
			throw new FrameworkException("File does not exist: " + filePathToUse);
		}

		try {
			BaseClass.logActionStart("Uploading file '" + BaseClass.mask(filePathToUse) + "' using Robot automation", "File Upload");

			java.awt.Robot robot = new java.awt.Robot();
			java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(file.getAbsolutePath());
			java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

			// CTRL + V and ENTER press release simulation
			robot.delay(500);

			robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
			robot.keyPress(java.awt.event.KeyEvent.VK_V);
			robot.keyRelease(java.awt.event.KeyEvent.VK_V);
			robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);

			robot.delay(500);

			robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
			robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);

			logger.info("Uploaded file using Robot: {}", filePathToUse);
			BaseClass.logActionSuccess("File uploaded successfully using Robot automation", "File Upload");

		} catch (Exception e) {
			BaseClass.logActionFailure("Upload file using Robot", "File Upload", "Failed to upload file using Robot: " + e.getMessage());
			throw new FrameworkException("Failed to upload file using Robot: " + e.getMessage(), e);
		}
	}

	/**
	 * Upload a file using AutoIt script.
	 * Requires AutoIt executable pre-configured.
	 * Supports fetching script path and file path from Excel based on the testCaseName if excelData is true.
	 *
	 * @param autoItExecutablePath Path to the AutoIt executable script (or parameter name if using Excel).
	 * @param filePath             Absolute path to file to upload (or parameter name if using Excel).
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 */
	public void uploadFileUsingAutoIt(String autoItExecutablePath, String filePath, boolean excelData, String testCaseName) {
		validateInput(autoItExecutablePath, "AutoIt Executable Path");
		validateInput(filePath, "File Path");

		// Get the actual values to use (from Excel or direct input)
		String executablePathToUse = getInputValue(testCaseName, autoItExecutablePath, "AutoIt Executable Path", excelData);
		String filePathToUse = getInputValue(testCaseName, filePath, "File Path", excelData);

		File file = new File(filePathToUse);
		if (!file.exists()) {
			BaseClass.logActionFailure("Upload file using AutoIt", "File Upload", "File does not exist: " + filePathToUse);
			throw new FrameworkException("File does not exist: " + filePathToUse);
		}

		try {
			BaseClass.logActionStart("Uploading file '" + BaseClass.mask(filePathToUse) + "' using AutoIt script '" + BaseClass.mask(executablePathToUse) + "'", "File Upload");

			Process p = new ProcessBuilder(executablePathToUse, filePathToUse).start();
			logger.info("Started AutoIt script '{}' to upload file '{}'", executablePathToUse, filePathToUse);
			int exitCode = p.waitFor();

			if (exitCode == 0) {
				BaseClass.logActionSuccess("File uploaded successfully using AutoIt script", "File Upload");
			} else {
				BaseClass.logActionFailure("Upload file using AutoIt", "File Upload", "AutoIt script exited with code: " + exitCode);
				throw new FrameworkException("AutoIt script failed with exit code: " + exitCode);
			}

		} catch (IOException | InterruptedException e) {
			BaseClass.logActionFailure("Upload file using AutoIt", "File Upload", "Failed to execute AutoIt upload script: " + e.getMessage());
			throw new FrameworkException("Failed to execute AutoIt upload script: " + e.getMessage(), e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Upload file using AutoIt", "File Upload", "Unexpected error with AutoIt upload: " + e.getMessage());
			throw new FrameworkException("Failed to upload file using AutoIt", e);
		}
	}

	// ==== FILE DOWNLOAD ====

	/**
	 * Waits for a file with expected name (or partial name) to appear in the given directory.
	 * Supports fetching downloadsDir and expectedFileName from Excel based on testCaseName if excelData is true.
	 *
	 * @param downloadsDir     Directory where files are downloaded (or parameter name if using Excel).
	 * @param expectedFileName Expected file name (or partial) (or parameter name if using Excel).
	 * @param timeoutSeconds   Max seconds to wait for the file.
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @return Path of the found file.
	 */
	public Path waitForFileDownload(String downloadsDir, String expectedFileName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(downloadsDir, "Downloads Directory");
		validateInput(expectedFileName, "Expected File Name");

		// Get the actual values to use (from Excel or direct input)
		String dirToUse = getInputValue(testCaseName, downloadsDir, "Downloads Directory", excelData);
		String expectedFileToUse = getInputValue(testCaseName, expectedFileName, "Expected File Name", excelData);

		Path dirPath = Paths.get(dirToUse);
		if (!Files.isDirectory(dirPath)) {
			BaseClass.logActionFailure("Wait for file download", "File Download", "Downloads directory does not exist: " + dirToUse);
			throw new FrameworkException("Downloads directory does not exist: " + dirToUse);
		}

		try {
			BaseClass.logActionStart("Waiting for file '" + BaseClass.mask(expectedFileToUse) + "' to download in '" + dirToUse + "' (timeout: " + timeoutSeconds + "s)", "File Download");

			Predicate<Path> fileNameMatch = path -> path.getFileName().toString().contains(expectedFileToUse);

			long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);

			while (System.currentTimeMillis() < endTime) {
				try {
					Path foundFile = Files.list(dirPath)
							.filter(Files::isRegularFile)
							.filter(fileNameMatch)
							.findFirst()
							.orElse(null);

					if (foundFile != null && !isFileDownloading(foundFile)) {
						logger.info("Found downloaded file: {}", foundFile);
						BaseClass.logActionSuccess("File downloaded successfully: '" + foundFile.getFileName().toString() + "'", "File Download");
						return foundFile;
					}
					Thread.sleep(1000);
				} catch (IOException | InterruptedException e) {
					BaseClass.logActionFailure("Wait for file download", "File Download", "Error while waiting for file download: " + e.getMessage());
					throw new FrameworkException("Error while waiting for file download", e);
				}
			}

			BaseClass.logActionFailure("Wait for file download", "File Download", "Timeout waiting for file download: " + expectedFileToUse);
			throw new FrameworkException("Timeout waiting for file download: " + expectedFileToUse);

		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for file download", "File Download", "Failed to wait for file download: " + e.getMessage());
			throw new FrameworkException("Failed to wait for file download", e);
		}
	}

	/** Helper method: check if file is still being written/downloaded (basic heuristic). */
	private boolean isFileDownloading(Path file) {
		String fileName = file.getFileName().toString().toLowerCase();
		// Common in-progress download file extensions or temporary names
		return fileName.endsWith(".crdownload")
				|| fileName.endsWith(".part")
				|| fileName.endsWith(".tmp");
	}

	/**
	 * Deletes all files from a directory. Useful to clean download folder before test.
	 *
	 * @param directoryPath Directory to clean files from.
	 */
	public void cleanDirectory(String directoryPath) {
		validateInput(directoryPath, "Directory Path");

		Path dirPath = Paths.get(directoryPath);
		if (!Files.isDirectory(dirPath)) {
			BaseClass.logActionFailure("Clean directory", "File Management", "Directory does not exist: " + directoryPath);
			throw new FrameworkException("Directory does not exist: " + directoryPath);
		}

		try {
			BaseClass.logActionStart("Cleaning directory '" + directoryPath + "'", "File Management");

			int[] fileCount = {0}; // Array to allow modification in lambda
			Files.list(dirPath)
			.filter(Files::isRegularFile)
			.forEach(path -> {
				try {
					Files.delete(path);
					logger.info("Deleted file {}", path);
					fileCount[0]++;
				} catch (IOException e) {
					logger.warn("Failed to delete file {}", path);
				}
			});

			BaseClass.logActionSuccess("Cleaned directory - deleted " + fileCount[0] + " files", "File Management");

		} catch (IOException e) {
			BaseClass.logActionFailure("Clean directory", "File Management", "Failed to clean directory: " + e.getMessage());
			throw new FrameworkException("Failed to clean directory: " + directoryPath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Clean directory", "File Management", "Unexpected error cleaning directory: " + e.getMessage());
			throw new FrameworkException("Failed to clean directory", e);
		}
	}

	public void cleanDownloadDirectory() {
	    String downloadDir = ConfigManager.getDownloadDirectoryEnsureExists();
	    cleanDirectory(downloadDir);
	}

	public void cleanUploadDirectory() {
	    String uploadDir = ConfigManager.getUploadDirectoryEnsureExists();
	    cleanDirectory(uploadDir);
	}

	public void cleanTestDataDirectory() {
	    String testDataDir = ConfigManager.getTestDataDirectory();
	    cleanDirectory(testDataDir);
	}

	/**
	 * Validates if file exists at expected path.
	 *
	 * @param expectedFilePath Absolute file path.
	 * @return true if file exists, else false.
	 */
	public boolean isFileDownloaded(String expectedFilePath) {
		validateInput(expectedFilePath, "Expected File Path");

		try {
			BaseClass.logActionStart("Checking if file exists at '" + BaseClass.mask(expectedFilePath) + "'", "File Verification");

			File file = new File(expectedFilePath);
			boolean exists = file.exists() && file.isFile();
			logger.info("File {} exists: {}", expectedFilePath, exists);

			if (exists) {
				BaseClass.logActionSuccess("File exists at specified path", "File Verification");
			} else {
				BaseClass.logActionSuccess("File does not exist at specified path", "File Verification");
			}

			return exists;

		} catch (Exception e) {
			BaseClass.logActionFailure("Check file exists", "File Verification", "Error checking file existence: " + e.getMessage());
			throw new FrameworkException("Failed to check file existence", e);
		}
	}

	/**
	 * Initiates clicking on the given element to start a file download.
	 *
	 * @param xpath XPath locator of the download button/link.
	 * @param timeoutSeconds Timeout to wait before click.
	 */
	public void startFileDownload(String xpath, int timeoutSeconds) {
		validateInput(xpath, "XPath");

		try {
			BaseClass.logActionStart("Clicking element to start file download", "File Download");

			WebElement downloadElement = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			downloadElement.click();
			logger.info("Clicked element [{}] to start file download", xpath);

			BaseClass.logActionSuccess("Download initiated successfully", "File Download");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Start file download", "File Download", "Download element not clickable: " + xpath + " - " + e.getMessage());
			throw new FrameworkException("Download element not clickable: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Start file download", "File Download", "Failed to start file download: " + e.getMessage());
			throw new FrameworkException("Failed to start file download", e);
		}
	}
}
