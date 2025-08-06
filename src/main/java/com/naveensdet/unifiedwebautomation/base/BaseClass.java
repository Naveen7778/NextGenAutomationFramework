package com.naveensdet.unifiedwebautomation.base;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.qameta.allure.Attachment;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.apache.commons.io.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naveensdet.unifiedwebautomation.utils.ConfigManager;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExtentReportManager;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import com.aventstack.extentreports.ExtentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.lang.reflect.Method;



public class BaseClass {


	protected static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
	private static final AtomicInteger activeTests = new AtomicInteger(0);
	private static final Map<Long, Long> threadStartTimes = new ConcurrentHashMap<>();
	// Thread-local variable to store test start time
	private ThreadLocal<Long> testStartTime = new ThreadLocal<>();

	/**
	 * Properties object to hold configuration key-value pairs loaded from external property files.
	 * It is protected to allow access in subclasses, enabling environment-driven and flexible configuration.
	 */
	protected static Properties props;

	public static Properties getProps() {
		if (props == null) {
			loadConfig(); // optionally ensure it's loaded before usage
		}
		return props;
	}


	/**
	 * Logger instance from Log4j2 used for logging informational, debug, and error messages throughout the BaseClass.
	 * It helps track the execution flow and record issues for debugging and audit purposes.
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseClass.class);

	/**
	 * Retrieves an integer value from the configuration properties.
	 * 
	 * This method attempts to read the property with the specified key from the
	 * loaded properties. If the property is missing, or if the value is not a
	 * valid integer, it returns the provided default value.
	 * 
	 * @param key           the key of the property to look up in the properties file.
	 * @param defaultValue  the default integer value to return if the property is missing or invalid.
	 * @return              the integer value of the specified property, or the default value if not found or invalid.
	 */
	private static int getIntProperty(String key, int defaultValue) {
		if (props == null) return defaultValue;
		try {
			return Integer.parseInt(ConfigManager.getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid integer for property '{}', using default {}", key, defaultValue);
			return defaultValue;
		}
	}


	/**
	 * Retrieves a String value from the loaded configuration properties.
	 * 
	 * This method attempts to fetch the value associated with the specified key
	 * from the properties object. If the properties have not yet been loaded,
	 * or if the key is not found, it returns the provided default value.
	 *
	 * @param key           the key of the property to retrieve from the configuration.
	 * @param defaultValue  the default String value to return if the key is not present or properties are not loaded.
	 * @return              the String value of the property corresponding to the given key;
	 *                      or the defaultValue if the property is missing or properties object is null.
	 */
	private static String getStringProperty(String key, String defaultValue) {
		if (props == null) return defaultValue;
		return ConfigManager.getProperty(key, defaultValue);
	}

	/**
	 * Loads the configuration properties file from the classpath.
	 * The environment is read from the 'environment' key inside the properties file.
	 *
	 * @throws FrameworkException if the properties file cannot be found or loaded,
	 *                            or if the 'environment' property is missing.
	 */
	public static void loadConfig() {
		props = new Properties();
		try (InputStream input = BaseClass.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				throw new FrameworkException("Config properties file not found");
			}
			props.load(input);
			String env = ConfigManager.getProperty("environment");
			if (env == null || env.trim().isEmpty()) {
				throw new FrameworkException("Environment property not set");
			}
			logger.info("Config loaded for environment: {}", env.toUpperCase());
		} catch (IOException e) {
			throw new FrameworkException("Config load failed", e);
		}
	}

	/**  
	 * Clears all browser cookies and storage (session and local) to reset session state after a test.  
	 */
	public static void cleanUpSession() {
		try {
			DriverManager.getDriver().manage().deleteAllCookies();
			((JavascriptExecutor) DriverManager.getDriver()).executeScript("window.sessionStorage.clear(); window.localStorage.clear();");
			logger.debug("Cookies and storage cleared after test.");
		} catch (Exception e) {
			logger.warn("Session cleanup failed.", e);
		}
	}

	/**
	 * Waits until all jQuery AJAX requests have completed or the timeout is reached.
	 */
	public static void waitForAjaxComplete(int timeoutSecs) {
		new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeoutSecs)).until(
				d -> ((JavascriptExecutor) d).executeScript("return (window.jQuery ? jQuery.active : 0) === 0").equals(true)
				);
	}

	/**
	 * Waits for the visibility of the element located by the given locator using configured timeout and polling intervals.
	 */
	public static WebElement fluentWait(By locator) {
		int timeout = getIntProperty("fluentWaitTimeout", 20);
		int polling = getIntProperty("fluentWaitPolling", 500);
		return fluentWait(locator, timeout, polling);
	}

	/**
	 * Performs explicit fluent wait for visibility of element located by the given locator.
	 *
	 * @param locator the Selenium By locator for the element to wait for.
	 * @param timeout maximum time to wait in seconds.
	 * @param polling interval in milliseconds between polling the condition.
	 * @return the visible WebElement.
	 * @throws org.openqa.selenium.TimeoutException if the element is not visible in the timeout.
	 */
	public static WebElement fluentWait(By locator, int timeout, int polling) {
		Wait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
				.withTimeout(Duration.ofSeconds(timeout))
				.pollingEvery(Duration.ofMillis(polling))
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Creates a RemoteWebDriver instance connected to the Selenium Grid URL specified in the configuration.
	 */
	public static WebDriver createRemoteWebDriver(Capabilities options ) {
		try {
			String remoteUrl = ConfigManager.getProperty("gridUrl");
			return new RemoteWebDriver(new URI(remoteUrl).toURL(), options);
		} catch (MalformedURLException | URISyntaxException e) {
			throw new FrameworkException("Grid URL malformed", e);
		}
	}

	/** Returns a configured Proxy object if proxy is enabled in properties; otherwise returns null. */
	@SuppressWarnings("unused")
	private static Proxy getProxyCapability() {
		if (!Boolean.parseBoolean(ConfigManager.getProperty("proxyEnabled", "false"))) {
			return null;
		}
		String host = ConfigManager.getProperty("proxyHost");
		String port = ConfigManager.getProperty("proxyPort");
		if (host != null && port != null) {
			String address = host + ":" + port;
			Proxy proxy = new Proxy();
			proxy.setHttpProxy(address);
			proxy.setSslProxy(address);
			return proxy;
		}
		return null;
	}

	/** Ensures the 'screenshots' directory exists by creating it if missing. */
	private static void ensureScreenshotsFolderExists() {
		File folder = new File("screenshots");
		if (!folder.exists()){
			boolean created = folder.mkdirs();
			if (created) {
				logger.info("Created screenshots directory.");
			}
		}
	}

	private static void ensurePageSourceFolderExists() {
		File folder = new File(getStringProperty("pageSourceFolder", "screenshots"));
		if (!folder.exists()){
			boolean created = folder.mkdirs();
			if (created) {
				logger.info("Created page source directory.");
			}
		}
	}

	/** Saves the current page source to an HTML file in the configured page source directory. */
	public static void savePageSource(String fileName) {
		try {
			ensurePageSourceFolderExists();
			// Ensure folder exists before saving
			ensureScreenshotsFolderExists();
			String html = DriverManager.getDriver().getPageSource();
			String pageSourceDir = getStringProperty("pageSourceFolder", "screenshots");
			FileUtils.writeStringToFile(new File(pageSourceDir + "/" + fileName + ".html"), html, StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Failed to save page source", e);
		}
	}

	/**
	 * Takes a screenshot of the current browser window, saves it under the configured screenshots folder.
	 *
	 * @param fileName the name of the screenshot file (without extension).
	 * @return the path to the saved screenshot file.
	 */
	public static String takeScreenshot(String fileName) {
		// Ensure folder exists before saving
		ensureScreenshotsFolderExists();
		File src = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
		String screenshotDir = getStringProperty("screenshotFolder", "screenshots");
		String path = screenshotDir + "/" + fileName + ".png";
		try {
			FileUtils.copyFile(src, new File(path));
			logger.info("Screenshot captured: {}", path);
		} catch (IOException e) {
			logger.error("Failed to save screenshot: {}", path, e);
		}
		return path;
	}

	/** Attaches a screenshot as a PNG byte array to Allure reports with the given name. */
	@Attachment(value = "{0}", type = "image/png")
	public static byte[] attachScreenshotToAllure(String name) {
		return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
	}

	/**
	 * Logs a test step (info level) - NO screenshot
	 */
	public void logStep(String stepDescription) {
		try {
			String message = "📝 " + stepDescription;
			logInfo(message);
		} catch (Exception e) {
			logger.warn("Error in step logging: {}", e.getMessage());
		}
	}

	/** Loads test data from a CSV file into a map for the specified test case name. */
	public static Map<String, String> loadTestData(String testCaseName) {
		Map<String, String> testData = new HashMap<>();
		String dataFile = "src/test/resources/testdata/data.csv"; // Adjust path as needed

		try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
			String headerLine = br.readLine(); // Read CSV header
			if (headerLine == null) return testData;

			String[] headers = headerLine.split(",");
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length > 0 && values[0].equalsIgnoreCase(testCaseName)) {
					for (int i = 1; i < headers.length && i < values.length; i++) {
						testData.put(headers[i].trim(), values[i].trim());
					}
					break; // Stop after finding the matching test case row
				}
			}
		} catch (IOException e) {
			throw new FrameworkException("Test data file read failed", e);
		}
		return testData;
	}

	/**
	 * Logs a successful step - NO screenshot
	 */
	public void logStepPass(String stepDescription) {
		try {
			String message = "✅ " + stepDescription;
			logPass(message);
		} catch (Exception e) {
			logger.warn("Error in step pass logging: {}", e.getMessage());
		}
	}

	/**
	 * Logs a failed step with AUTOMATIC screenshot
	 */
	public void logStepFail(String stepDescription, String errorDetails) {
		try {
			String message = "❌ " + stepDescription;
			if (errorDetails != null && !errorDetails.isEmpty()) {
				message += " - " + errorDetails;
			}

			// ✅ ONLY capture screenshot on failure
			String screenshotPath = captureFailureScreenshot(stepDescription);
			if (screenshotPath != null) {
				ExtentReportManager.logFailureWithScreenshot(message, screenshotPath);
			} else {
				logFail(message);
			}
		} catch (Exception e) {
			logger.error("Error in step failure logging: {}", e.getMessage());
			logFail("❌ " + stepDescription); // Fallback without screenshot
		}
	}

	/**
	 * Enhanced screenshot capture with detailed debugging
	 */
	private String captureFailureScreenshot(String stepName) {
		try {
			if (DriverManager.getDriver() != null) {
				String sanitizedStepName = stepName.replaceAll("[^a-zA-Z0-9_-]", "_");
				String timestamp = new SimpleDateFormat("HH-mm-ss-SSS").format(new Date());
				String screenshotName = "FAILURE_" + sanitizedStepName + "_" + timestamp + ".png";

				// ✅ FIXED: Save screenshots in reports/screenshots folder
				String reportsDir = System.getProperty("user.dir") + File.separator + "reports";
				String screenshotsDir = reportsDir + File.separator + "screenshots";

				// Ensure screenshots directory exists
				File screenshotFolder = new File(screenshotsDir);
				if (!screenshotFolder.exists()) {
					screenshotFolder.mkdirs();
					logger.info("📁 Created screenshots directory: {}", screenshotsDir);
				}

				// Take and save screenshot
				File screenshot = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
				String fullPath = screenshotsDir + File.separator + screenshotName;
				FileUtils.copyFile(screenshot, new File(fullPath));

				logger.info("✅ Screenshot saved to: {}", fullPath);

				// ✅ CRITICAL: Return RELATIVE path for ExtentReports
				return "./screenshots/" + screenshotName;
			}
		} catch (Exception e) {
			logger.warn("Could not capture failure screenshot: {}", e.getMessage());
		}
		return null;
	}


	/**
	 * Manual screenshot capture (use sparingly for critical checkpoints)
	 */
	public String captureCheckpointScreenshot(String checkpointName) {
		try {
			if (DriverManager.getDriver() != null) {
				String sanitizedName = checkpointName.replaceAll("[^a-zA-Z0-9_-]", "_");
				String timestamp = new SimpleDateFormat("HH-mm-ss-SSS").format(new Date());
				String screenshotName = "CHECKPOINT_" + sanitizedName + "_" + timestamp;

				String screenshotPath = captureScreenshot(DriverManager.getDriver(), screenshotName);
				if (screenshotPath != null) {
					ExtentReportManager.attachScreenshot(screenshotPath);
					logInfo("📸 Checkpoint screenshot captured: " + checkpointName);
				}
				return screenshotPath;
			}
		} catch (Exception e) {
			logger.warn("Could not capture checkpoint screenshot: {}", e.getMessage());
		}
		return null;
	}

	/**
	 * Logs a warning step - NO screenshot
	 */
	public void logStepWarning(String stepDescription) {
		try {
			String message = "⚠️ " + stepDescription;
			logWarning(message);
		} catch (Exception e) {
			logger.warn("Error in step warning logging: {}", e.getMessage());
		}
	}


	/**
	 * Loads test data for a given test case from an Excel file specified in configuration.
	 *
	 * @param testCaseName the name of the test case whose data is to be loaded
	 * @return a map containing key-value pairs representing column headers and corresponding cell values
	 * @throws FrameworkException if reading the Excel file fails
	 */
	public static Map<String, String> loadTestDataFromExcel(String testCaseName) {
		Map<String, String> testData = new HashMap<>();

		// Read the Excel data file path from configuration with a default fallback
		String dataFile = ConfigManager.getProperty("excelDataFile", "src/test/resources/testdata/data.xlsx");

		try (FileInputStream fis = new FileInputStream(dataFile);
				Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0); // Assuming data in first sheet
			Row headerRow = sheet.getRow(0);
			if (headerRow == null) return testData;

			int numCols = headerRow.getLastCellNum();
			int testCaseColumnIndex = 0; // Usually first column has test case names

			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue; // Skip header row

				Cell testCaseCell = row.getCell(testCaseColumnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (testCaseCell != null && testCaseName.equalsIgnoreCase(testCaseCell.getStringCellValue())) {
					for (int c = 1; c < numCols; c++) {
						Cell headerCell = headerRow.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
						Cell valueCell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

						String key = headerCell != null ? headerCell.getStringCellValue().trim() : "Column" + c;
						String value = "";

						if (valueCell != null) {
							switch (valueCell.getCellType()) {
							case STRING:
								value = valueCell.getStringCellValue().trim();
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(valueCell)) {
									value = valueCell.getDateCellValue().toString();
								} else {
									value = String.valueOf(valueCell.getNumericCellValue());
								}
								break;
							case BOOLEAN:
								value = String.valueOf(valueCell.getBooleanCellValue());
								break;
							case FORMULA:
								value = valueCell.getCellFormula();
								break;
							default:
								value = "";
							}
						}
						testData.put(key, value);
					}
					break; // We found the row, no need to continue
				}
			}

		} catch (IOException e) {
			throw new FrameworkException("Test data Excel file read failed", e);
		}
		return testData;
	}

	/**
	 * Loads test data for a given test case from a JSON file specified in the configuration.
	 *
	 * @param testCaseName the name of the test case whose data is to be loaded
	 * @return a map containing key-value pairs representing JSON fields and corresponding values
	 * @throws FrameworkException if reading the JSON file fails
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, String> loadTestDataFromJson(String testCaseName) {
		Map<String, String> testData = new HashMap<>();
		// Read JSON data file path from configuration with a default fallback
		String dataFile = ConfigManager.getProperty("jsonDataFile", "src/test/resources/testdata/data.json");

		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode root = mapper.readTree(new File(dataFile));
			JsonNode testCaseNode = root.get(testCaseName);

			if (testCaseNode != null && testCaseNode.isObject()) {
				// Iterate over fields (key-value pairs) in the test case JSON object
				testCaseNode.fields().forEachRemaining(entry -> {
					testData.put(entry.getKey(), entry.getValue().asText(""));
				});
			}
		} catch (IOException e) {
			throw new FrameworkException("Test data JSON file read failed", e);
		}
		return testData;
	}

	/** Masks an input string by revealing only the first two characters and replacing the rest with asterisks. */
	public static String mask(String input) {
		if (input == null) return null;
		int unmasked = Math.min(2, input.length());
		return input.substring(0, unmasked) + "****";
	}

	/** Clears all browser cookies and local/session storage to reset browser state. */
	public static void clearCookiesAndStorage() {
		DriverManager.getDriver().manage().deleteAllCookies();
		((JavascriptExecutor) DriverManager.getDriver()).executeScript("window.localStorage.clear();window.sessionStorage.clear();");
	}

	@AfterClass(alwaysRun = true)
	public void tearDownClass() {
		// Quit driver and cleanup after all tests in the class are done
		DriverManager.quitDriver();
	}

	@BeforeClass(alwaysRun = true)
	public void setupClass() {
		try {
			logger.info("🚀 Starting test class setup...");

			// ✅ VALIDATION 1: Ensure config is loaded
			validateConfigurationLoaded();

			// ✅ VALIDATION 2: Initialize driver with validation
			initializeDriverWithValidation();

			// ✅ VALIDATION 3: Navigate to base URL with validation
			navigateToBaseUrlWithValidation();

			logger.info("✅ Test class setup completed successfully");

		} catch (FrameworkException fe) {
			logger.error("❌ Framework error during test class setup: {}", fe.getMessage(), fe);
			throw fe;
		} catch (Exception e) {
			logger.error("❌ Unexpected error during test class setup: {}", e.getMessage(), e);
			throw new FrameworkException("Test class setup failed", e);
		}
	}

	/**
	 * VALIDATION 1: Ensure configuration is properly loaded
	 */
	private void validateConfigurationLoaded() {
		try {
			if (props == null) {
				logger.info("Loading configuration...");
				loadConfig();
			}

			if (props == null || props.isEmpty()) {
				throw new FrameworkException("Configuration properties are null or empty after loading");
			}

			// Validate essential properties exist
			String environment = ConfigManager.getProperty("environment");
			if (environment == null || environment.trim().isEmpty()) {
				throw new FrameworkException("Environment property is missing in configuration");
			}

			logger.info("✅ Configuration validated successfully for environment: {}", environment);

		} catch (Exception e) {
			logger.error("Configuration validation failed: {}", e.getMessage(), e);
			throw new FrameworkException("Configuration validation failed", e);
		}
	}

	/**
	 * VALIDATION 2: Initialize driver with comprehensive validation
	 */
	private void initializeDriverWithValidation() {
		try {
			logger.info("Initializing WebDriver...");

			// Initialize driver
			DriverManager.initDriver();

			// ✅ VALIDATE: Driver is actually created
			WebDriver driver = DriverManager.getDriver();
			if (driver == null) {
				throw new FrameworkException("Driver is null after initialization");
			}

			// ✅ VALIDATE: Driver session is active
			String sessionId = ((RemoteWebDriver) driver).getSessionId().toString();
			if (sessionId == null || sessionId.isEmpty()) {
				throw new FrameworkException("Driver session ID is invalid");
			}

			// ✅ VALIDATE: Browser window is responsive
			driver.getTitle(); // This will throw exception if browser is not responsive

			logger.info("✅ WebDriver initialized and validated successfully");
			logger.info("📋 Driver Session ID: {}", sessionId);

		} catch (FrameworkException fe) {
			throw fe;
		} catch (Exception e) {
			logger.error("Driver initialization validation failed: {}", e.getMessage(), e);
			throw new FrameworkException("Driver initialization failed", e);
		}
	}

	/**
	 * VALIDATION 3: Navigate to base URL with validation
	 */
	private void navigateToBaseUrlWithValidation() {
		try {
			String baseUrl = ConfigManager.getEnvSpecificProperty("baseUrl", "https://default.example.com");

			// ✅ VALIDATE: URL format is correct
			if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
				throw new FrameworkException("Invalid base URL format: " + baseUrl);
			}

			WebDriver driver = DriverManager.getDriver();

			logger.info("Navigating to base URL: {}", baseUrl);
			driver.get(baseUrl);

			// ✅ VALIDATE: Navigation was successful
			String currentUrl = driver.getCurrentUrl();
			if (currentUrl == null || currentUrl.isEmpty()) {
				throw new FrameworkException("Navigation failed - current URL is empty");
			}

			// ✅ VALIDATE: Page is loaded (basic check)
			String pageTitle = driver.getTitle();
			if (pageTitle == null) {
				logger.warn("⚠️ Page title is null - page might not be fully loaded");
			}

			logger.info("✅ Successfully navigated to: {}", currentUrl);
			logger.info("📋 Page Title: {}", pageTitle != null ? pageTitle : "No title");

		} catch (FrameworkException fe) {
			throw fe;
		} catch (Exception e) {
			logger.error("Base URL navigation validation failed: {}", e.getMessage(), e);
			throw new FrameworkException("Base URL navigation failed", e);
		}
	}


	/**
	 * Captures screenshot of current browser state for test documentation
	 * 
	 * @param driver WebDriver instance for screenshot capture
	 * @param testName Name of the test for screenshot naming
	 * @return String path to the captured screenshot file
	 * @throws FrameworkException if screenshot capture fails
	 */
	public String captureScreenshot(WebDriver driver, String testName) {
		try {
			// Input validation
			if (driver == null) {
				logger.warn("WebDriver instance is null, cannot capture screenshot");
				throw new FrameworkException("WebDriver cannot be null for screenshot capture");
			}

			if (testName == null || testName.trim().isEmpty()) {
				testName = "UnknownTest";
				logger.warn("Test name is null or empty, using default name: " + testName);
			}

			// Create and validate screenshots directory
			String screenshotPath = createScreenshotDirectory();

			// Generate unique screenshot filename
			String screenshotFilePath = generateScreenshotFilePath(screenshotPath, testName);

			// Capture screenshot
			byte[] screenshotData = captureScreenshotData(driver);

			// Save screenshot to file
			saveScreenshotToFile(screenshotData, screenshotFilePath);

			logger.info("Screenshot captured successfully: " + screenshotFilePath);
			return screenshotFilePath;

		} catch (FrameworkException e) {
			logger.error("Framework exception during screenshot capture: " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error during screenshot capture: " + e.getMessage(), e);
			throw new FrameworkException("Screenshot capture failed for test: " + testName, e);
		}
	}

	/**
	 * Creates screenshots directory if it doesn't exist
	 * 
	 * @return String path to screenshots directory
	 * @throws FrameworkException if directory creation fails
	 */
	private String createScreenshotDirectory() {
		try {
			String screenshotsDir = System.getProperty("user.dir") + File.separator + "screenshots";
			File directory = new File(screenshotsDir);

			if (!directory.exists()) {
				boolean created = directory.mkdirs();
				if (!created) {
					throw new FrameworkException("Failed to create screenshots directory: " + screenshotsDir);
				}
				logger.info("Created screenshots directory: " + screenshotsDir);
			}

			return screenshotsDir;

		} catch (Exception e) {
			logger.error("Error creating screenshots directory: " + e.getMessage(), e);
			throw new FrameworkException("Screenshots directory creation failed", e);
		}
	}

	/**
	 * Generates unique screenshot file path with timestamp
	 * 
	 * @param screenshotsDir Base screenshots directory
	 * @param testName Name of the test
	 * @return String complete file path for screenshot
	 */
	private String generateScreenshotFilePath(String screenshotsDir, String testName) {
		try {
			String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
			String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
			String screenshotName = sanitizedTestName + "_" + timestamp + ".png";

			return screenshotsDir + File.separator + screenshotName;

		} catch (Exception e) {
			logger.error("Error generating screenshot file path: " + e.getMessage(), e);
			throw new FrameworkException("Screenshot file path generation failed", e);
		}
	}

	/**
	 * Captures screenshot data from WebDriver
	 * 
	 * @param driver WebDriver instance
	 * @return byte array containing screenshot data
	 * @throws FrameworkException if screenshot data capture fails
	 */
	private byte[] captureScreenshotData(WebDriver driver) {
		try {
			TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
			return takesScreenshot.getScreenshotAs(OutputType.BYTES);

		} catch (Exception e) {
			logger.error("Error capturing screenshot data: " + e.getMessage(), e);
			throw new FrameworkException("Screenshot data capture failed", e);
		}
	}

	/**
	 * Saves screenshot data to specified file path
	 * 
	 * @param screenshotData Screenshot data as byte array
	 * @param filePath Target file path for saving screenshot
	 * @throws FrameworkException if file save operation fails
	 */
	private void saveScreenshotToFile(byte[] screenshotData, String filePath) {
		try {
			if (screenshotData == null || screenshotData.length == 0) {
				throw new FrameworkException("Screenshot data is null or empty");
			}

			FileUtils.writeByteArrayToFile(new File(filePath), screenshotData);

		} catch (Exception e) {
			logger.error("Error saving screenshot to file: " + e.getMessage(), e);
			throw new FrameworkException("Screenshot file save failed", e);
		}
	}

	/**
	 * Logs action start to both logger and Extent Report
	 * 
	 * @param action Description of the action being performed
	 * @param elementName Name of the element being acted upon
	 */
	public static void logActionStart(String action, String elementName) {
		try {
			String message = "🔄 " + action + " on " + elementName;
			logger.info(message);
			ExtentReportManager.logInfo(message);
		} catch (Exception e) {
			logger.info("🔄 {} on {}", action, elementName);
		}
	}

	/**
	 * Logs successful action completion
	 * 
	 * @param action Description of the completed action
	 * @param elementName Name of the element that was acted upon
	 */
	public static void logActionSuccess(String action, String elementName) {
		try {
			String message = "✅ Successfully " + action.toLowerCase() + " on " + elementName;
			logger.info(message);
			ExtentReportManager.logPass(message);
		} catch (Exception e) {
			logger.info("✅ Successfully {} on {}", action.toLowerCase(), elementName);
		}
	}

	/**
	 * Logs action failure with screenshot capture
	 * 
	 * @param action Description of the failed action
	 * @param elementName Name of the element where action failed
	 * @param error Error message or exception details
	 */
	public static void logActionFailure(String action, String elementName, String error) {
		try {
			String message = "❌ Failed to " + action.toLowerCase() + " on " + elementName + ": " + error;
			logger.error(message);
			ExtentReportManager.logFail(message);
		} catch (Exception e) {
			logger.error("❌ Failed to {} on {}: {}", action.toLowerCase(), elementName, error);
		}
	}

	// Add to your BaseClass.java
	public static void logToExtentReport(String status, String message) {
		try {
			switch (status.toUpperCase()) {
			case "PASS":
			case "SUCCESS":
				ExtentReportManager.logPass(message);
				break;
			case "FAIL":
			case "FAILURE":
				ExtentReportManager.logFail(message);
				break;
			case "INFO":
				ExtentReportManager.logInfo(message);
				break;
			case "WARNING":
				ExtentReportManager.logWarning(message);
				break;
			default:
				ExtentReportManager.logInfo(message);
			}
		} catch (Exception e) {
			// Silently handle if ExtentReports not initialized
			logger.debug("Could not log to ExtentReports: {}", e.getMessage());
		}
	}

	/**
	 * Logs action failure with details and throwable to both logger and ExtentReports.
	 *
	 * @param action The action that failed
	 * @param elementName The element or component name where failure occurred
	 * @param throwable The exception/throwable that caused the failure
	 */
	public static void logActionFailure(String action, String elementName, Throwable throwable) {
		try {
			String message = "Failed to " + action.toLowerCase() + " on " + elementName;

			// Log to console/file with throwable details
			if (throwable != null) {
				logger.error("{} - Error: {}", message, throwable.getMessage(), throwable);
			} else {
				logger.error(message);
			}

			// Log to ExtentReports with detailed failure message
			String extentMessage = throwable != null ? 
					message + " - Error: " + throwable.getMessage() : 
						message;

			ExtentReportManager.logFail(extentMessage);

		} catch (Exception e) {
			logger.warn("Failed to log action failure: " + e.getMessage());
		}
	}

	/** 
	 * Take screenshot as byte array for Cucumber attachments.
	 * @return screenshot as PNG bytes
	 */
	public static byte[] takeScreenshotForCucumber() {
		if (DriverManager.getDriver() == null) {
			throw new FrameworkException("WebDriver not initialized for screenshot.");
		}
		return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
	}

	/**
	 * Starts timing for current test
	 */
	private void startTestTimer() {
		testStartTime.set(System.currentTimeMillis());
	}

	/**
	 * Ends test timing and logs execution time
	 */
	private void endTestTimer(String testName) {
		try {
			Long startTime = testStartTime.get();
			if (startTime != null) {
				long duration = System.currentTimeMillis() - startTime;
				String timeMessage = String.format("⏱️ Test Execution Time: %d ms (%.2f seconds)", 
						duration, duration / 1000.0);
				logInfo(timeMessage);

				// Log performance warning if test takes too long
				long maxTestTime = getIntProperty("maxTestTimeMs", 30000); // 30 seconds default
				if (duration > maxTestTime) {
					logWarning(String.format("⚠️ Test exceeded expected duration: %d ms > %d ms", 
							duration, maxTestTime));
				}
			}
		} catch (Exception e) {
			logger.debug("Error calculating test duration: {}", e.getMessage());
		} finally {
			testStartTime.remove();
		}
	}

	/**
	 * Logs browser and environment information
	 */
	private void logEnvironmentInfo() {
		try {
			WebDriver driver = DriverManager.getDriver();
			if (driver != null) {
				Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
				String browserInfo = String.format("🌐 Browser: %s %s", 
						caps.getBrowserName(), caps.getBrowserVersion());
				String environment = ConfigManager.getProperty("environment", "Unknown");
				String baseUrl = ConfigManager.getEnvSpecificProperty("baseUrl", "Not configured");

				logInfo(browserInfo);
				logInfo("🏷️ Environment: " + environment);
				logInfo("🔗 Base URL: " + baseUrl);
			}
		} catch (Exception e) {
			logger.debug("Could not log environment info: {}", e.getMessage());
		}
	}

	// ================================
	// EXTENT TEST LIFECYCLE MANAGEMENT - ADD THIS SECTION
	// ================================

	@SuppressWarnings({ "unused", "deprecation" })
	@BeforeMethod(alwaysRun = true)
	public void setUpTest(Method method) {
		try {
			long threadId = Thread.currentThread().getId();
			int currentActiveTests = activeTests.incrementAndGet();
			threadStartTimes.put(threadId, System.currentTimeMillis());

			logger.info("📝 Setting up test method: {} on Thread-{} (Active tests: {})", 
					method.getName(), threadId, currentActiveTests);

			String className = this.getClass().getSimpleName();
			String methodName = method.getName();
			String testName = className + "." + methodName;

			String testDescription = getTestDescription(method);

			ExtentTest test = ExtentReportManager.createTest(testName, testDescription);

			// Start performance tracking
			startTestTimer();

			// Log parallel execution info
			logInfo("🚀 Test execution started: " + testName);
			logInfo(String.format("🧵 Thread Info: Thread-%d (Active: %d)", threadId, currentActiveTests));

			// Log environment information
			logEnvironmentInfo();

			logger.info("✅ Test setup completed for: {} on Thread-{}", testName, threadId);

		} catch (Exception e) {
			activeTests.decrementAndGet(); // Rollback counter on failure
			logger.error("❌ Error during test setup on Thread-{}: {}", 
					Thread.currentThread().getId(), e.getMessage(), e);
			throw new RuntimeException("Test setup failed", e);
		}
	}

	@SuppressWarnings("deprecation")
	@AfterMethod(alwaysRun = true)
	public void tearDownTest(ITestResult result) {
		long threadId = Thread.currentThread().getId();

		try {
			String testName = result.getMethod().getMethodName();
			int remainingActiveTests = activeTests.decrementAndGet();

			logger.info("🧹 Starting cleanup for test: {} on Thread-{} (Remaining: {})", 
					testName, threadId, remainingActiveTests);

			// End performance tracking
			endTestTimer(testName);

			// Log thread execution time
			Long startTime = threadStartTimes.remove(threadId);
			if (startTime != null) {
				long threadExecutionTime = System.currentTimeMillis() - startTime;
				logInfo(String.format("🧵 Thread-%d execution time: %d ms", threadId, threadExecutionTime));
			}

			logTestResult(result);
			performAdditionalCleanup();

			logger.info("✅ Cleanup completed for test: {} on Thread-{}", testName, threadId);

		} catch (Exception e) {
			logger.error("❌ Error during test cleanup on Thread-{}: {}", threadId, e.getMessage(), e);
		} finally {
			try {
				ExtentReportManager.removeTest();
				logger.debug("ThreadLocal ExtentTest removed for Thread-{}", threadId);
			} catch (Exception cleanupException) {
				logger.warn("Warning during ThreadLocal cleanup on Thread-{}: {}", 
						threadId, cleanupException.getMessage());
			}
		}
	}

	/**
	 * Get parallel execution statistics
	 */
	protected void logParallelExecutionStats() {
		try {
			int active = activeTests.get();
			int maxThreads = Runtime.getRuntime().availableProcessors();

			logInfo(String.format("📊 Parallel Execution Stats - Active: %d, Max CPU Threads: %d", 
					active, maxThreads));

			if (active > maxThreads) {
				logWarning(String.format("⚠️ High thread usage: %d active > %d CPU threads", 
						active, maxThreads));
			}
		} catch (Exception e) {
			logger.debug("Could not log parallel execution stats: {}", e.getMessage());
		}
	}

	private String getTestDescription(Method method) {
		try {
			Test testAnnotation = method.getAnnotation(Test.class);
			if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
				return testAnnotation.description();
			}
		} catch (Exception e) {
			logger.debug("Could not extract test description: {}", e.getMessage());
		}
		return "Test method: " + method.getName();
	}

	private void logTestResult(ITestResult result) {
		try {
			String testName = result.getMethod().getMethodName();

			switch (result.getStatus()) {
			case ITestResult.SUCCESS:
				logPass("✅ Test completed successfully: " + testName);
				logger.info("✅ Test PASSED: {}", testName);
				break;

			case ITestResult.FAILURE:
				String failureMessage = result.getThrowable() != null ? 
						result.getThrowable().getMessage() : "Test failed with unknown error";
				logger.error("❌ Test FAILED: {} - {}", testName, failureMessage);
				break;

			case ITestResult.SKIP:
				String skipReason = result.getThrowable() != null ? 
						result.getThrowable().getMessage() : "Test was skipped";
				logSkip("⏭️ Test skipped: " + skipReason);
				logger.warn("⏭️ Test SKIPPED: {} - {}", testName, skipReason);
				break;

			default:
				logWarning("⚠️ Test completed with unknown status");
				logger.warn("⚠️ Test {} completed with unknown status: {}", testName, result.getStatus());
			}
		} catch (Exception e) {
			logger.error("Error logging test result: {}", e.getMessage());
		}
	}

	// ================================
	// ENHANCED LOGGING METHODS - ADD THIS SECTION
	// ================================

	public void logInfo(String message) {
		try {
			ExtentReportManager.logInfo(message);
			logger.info(message);
		} catch (Exception e) {
			logger.info(message);
		}
	}

	public void logPass(String message) {
		try {
			ExtentReportManager.logPass(message);
			logger.info("PASS: {}", message);
		} catch (Exception e) {
			logger.info("PASS: {}", message);
		}
	}

	public void logFail(String message) {
		try {
			ExtentReportManager.logFail(message);
			logger.error("FAIL: {}", message);
		} catch (Exception e) {
			logger.error("FAIL: {}", message);
		}
	}

	public void logWarning(String message) {
		try {
			ExtentReportManager.logWarning(message);
			logger.warn("WARNING: {}", message);
		} catch (Exception e) {
			logger.warn("WARNING: {}", message);
		}
	}

	public void logSkip(String message) {
		try {
			ExtentReportManager.logSkip(message);
			logger.info("SKIP: {}", message);
		} catch (Exception e) {
			logger.info("SKIP: {}", message);
		}
	}

	public void logTestStep(String stepDescription) {
		logInfo("📝 " + stepDescription);
	}

	public void logTestStepPass(String stepDescription) {
		logPass("✅ " + stepDescription);
	}

	public void logTestStepFail(String stepDescription) {
		logFail("❌ " + stepDescription);
	}

	public void logTestStepWarning(String stepDescription) {
		logWarning("⚠️ " + stepDescription);
	}

	protected void performAdditionalCleanup() {
		// Override in test classes if additional cleanup is needed
	}

	protected String getCurrentTestName() {
		try {
			return Thread.currentThread().getStackTrace()[2].getMethodName();
		} catch (Exception e) {
			return "UnknownTest";
		}
	}



}



