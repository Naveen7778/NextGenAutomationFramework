package com.naveensdet.unifiedwebautomation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * ExtentReportManager handles the initialization, configuration, and management
 * of Extent Reports for test execution reporting.
 * 
 * @author Naveen Rangari
 * @version 1.0
 * @since 2025-07-29
 */
public class ExtentReportManager {

	private static final Logger Log = LoggerFactory.getLogger(ExtentReportManager.class);
	private static ExtentReports extent;
	private static ExtentSparkReporter sparkReporter;
	private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
	private static String reportPath;
	private static final Object REPORT_LOCK = new Object();
	private static final Map<String, String> threadTestMap = new ConcurrentHashMap<>();

	/**
	 * Initializes the Extent Report with proper configuration and system information
	 * 
	 * @throws FrameworkException if report initialization fails
	 */
	@BeforeSuite
	public static void initExtentReport() {
		try {
			Log.info("Initializing Extent Report...");

			// Validate and create reports directory
			String reportsDir = createReportsDirectory();

			// Generate unique report path
			reportPath = generateReportPath(reportsDir);

			// Initialize and configure Spark Reporter
			initializeSparkReporter();

			// Initialize Extent Reports
			initializeExtentReports();

			// Set system information
			setSystemInformation();

			Log.info("Extent Report initialized successfully at: " + reportPath);

		} catch (Exception e) {
			Log.error("Failed to initialize Extent Report: " + e.getMessage(), e);
			throw new FrameworkException("Extent Report initialization failed", e);
		}
	}

	/**
	 * Updated screenshot path generation in ExtentReportManager
	 */
	private static String createReportsDirectory() {
		try {
			String reportsDir = System.getProperty("user.dir") + File.separator + "reports";
			File directory = new File(reportsDir);

			if (!directory.exists()) {
				boolean created = directory.mkdirs();
				if (!created) {
					throw new FrameworkException("Failed to create reports directory: " + reportsDir);
				}
				Log.info("Created reports directory: " + reportsDir);
			}

			// ✅ ALSO CREATE SCREENSHOTS SUBFOLDER
			String screenshotsDir = reportsDir + File.separator + "screenshots";
			File screenshotsDirectory = new File(screenshotsDir);
			if (!screenshotsDirectory.exists()) {
				screenshotsDirectory.mkdirs();
				Log.info("Created screenshots directory: " + screenshotsDir);
			}

			return reportsDir;

		} catch (Exception e) {
			Log.error("Error creating reports directory: " + e.getMessage(), e);
			throw new FrameworkException("Reports directory creation failed", e);
		}
	}

	/**
	 * Generates unique report path with timestamp
	 */
	private static String generateReportPath(String reportsDir) {
		try {
			if (reportsDir == null || reportsDir.trim().isEmpty()) {
				throw new FrameworkException("Reports directory path cannot be null or empty");
			}

			String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
			return reportsDir + File.separator + "ExtentReport_" + timestamp + ".html";

		} catch (Exception e) {
			Log.error("Error generating report path: " + e.getMessage(), e);
			throw new FrameworkException("Report path generation failed", e);
		}
	}

	/**
	 * Initializes and configures Spark Reporter
	 */
	private static void initializeSparkReporter() {
		try {
			if (reportPath == null || reportPath.trim().isEmpty()) {
				throw new FrameworkException("Report path cannot be null or empty");
			}

			sparkReporter = new ExtentSparkReporter(reportPath);

			// Configure reporter settings
			sparkReporter.config().setDocumentTitle("Automation Test Report");
			sparkReporter.config().setReportName("Test Execution Report");
			sparkReporter.config().setTheme(Theme.STANDARD);
			sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

			Log.info("Spark Reporter configured successfully");

		} catch (Exception e) {
			Log.error("Failed to initialize Spark Reporter: " + e.getMessage(), e);
			throw new FrameworkException("Spark Reporter initialization failed", e);
		}
	}

	/**
	 * Initializes Extent Reports and attaches reporter
	 */
	private static void initializeExtentReports() {
		try {
			if (sparkReporter == null) {
				throw new FrameworkException("Spark Reporter must be initialized before Extent Reports");
			}

			extent = new ExtentReports();
			extent.attachReporter(sparkReporter);

			Log.info("Extent Reports initialized successfully");

		} catch (Exception e) {
			Log.error("Failed to initialize Extent Reports: " + e.getMessage(), e);
			throw new FrameworkException("Extent Reports initialization failed", e);
		}
	}

	/**
	 * Sets system information in the extent report
	 */
	private static void setSystemInformation() {
		try {
			if (extent == null) {
				throw new FrameworkException("Extent Reports must be initialized before setting system info");
			}

			// Set basic system information
			extent.setSystemInfo("Operating System", System.getProperty("os.name"));
			extent.setSystemInfo("OS Version", System.getProperty("os.version"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));
			extent.setSystemInfo("User Name", System.getProperty("user.name"));

			// Set configuration details with validation
			setConfigInformation();

			Log.info("System information set successfully");

		} catch (Exception e) {
			Log.error("Failed to set system information: " + e.getMessage(), e);
			throw new FrameworkException("System information setting failed", e);
		}
	}

	/**
	 * Sets configuration information from ConfigManager
	 */
	private static void setConfigInformation() {
		try {
			String browser = ConfigManager.getProperty("browser");
			String environment = ConfigManager.getProperty("environment");
			String url = ConfigManager.getProperty("url");

			extent.setSystemInfo("Browser", (browser != null && !browser.trim().isEmpty()) ? browser : "Not Configured");
			extent.setSystemInfo("Environment", (environment != null && !environment.trim().isEmpty()) ? environment : "Not Configured");
			extent.setSystemInfo("Application URL", (url != null && !url.trim().isEmpty()) ? url : "Not Configured");

		} catch (Exception e) {
			Log.warn("Could not load configuration details: " + e.getMessage());
			extent.setSystemInfo("Configuration", "Config file not accessible");
		}
	}

	/**
	 * Creates a new test in the Extent Report with validation
	 */
	public static ExtentTest createTest(String testName) {
		try {
			if (testName == null || testName.trim().isEmpty()) {
				throw new FrameworkException("Test name cannot be null or empty");
			}

			if (extent == null) {
				throw new FrameworkException("Extent Reports not initialized. Call initExtentReport() first");
			}

			ExtentTest test = extent.createTest(testName);
			extentTest.set(test);

			Log.info("Test created in Extent Report: " + testName);
			return test;

		} catch (Exception e) {
			Log.error("Failed to create test in Extent Report: " + e.getMessage(), e);
			throw new FrameworkException("Test creation failed for: " + testName, e);
		}
	}


	/**
	 * Gets the current test instance with validation
	 */
	public static ExtentTest getTest() {
		try {
			ExtentTest test = extentTest.get();
			if (test == null) {
				throw new FrameworkException("No active test found. Create test first using createTest()");
			}
			return test;

		} catch (Exception e) {
			Log.error("Failed to get current test: " + e.getMessage(), e);
			throw new FrameworkException("Failed to retrieve current test", e);
		}
	}


	/**
	 * Attaches screenshot to the current test with validation
	 */
	public static void attachScreenshot(String screenshotPath) {
		try {
			if (screenshotPath == null || screenshotPath.trim().isEmpty()) {
				Log.warn("Screenshot path is null or empty. Skipping attachment.");
				return;
			}

			File screenshotFile = new File(screenshotPath);
			if (!screenshotFile.exists()) {
				Log.warn("Screenshot file does not exist at path: " + screenshotPath);
				return;
			}

			ExtentTest test = getTest();
			test.addScreenCaptureFromPath(screenshotPath);

			Log.info("Screenshot attached successfully: " + screenshotPath);

		} catch (Exception e) {
			Log.error("Failed to attach screenshot: " + e.getMessage(), e);
			throw new FrameworkException("Screenshot attachment failed", e);
		}
	}

	/**
	 * Logs info message to current test with validation
	 */
	public static void logInfo(String message) {
		try {
			if (message == null) {
				message = "Info message was null";
			}

			ExtentTest test = getTest();
			test.info(message);
			Log.info("Extent Report Info: " + message);

		} catch (Exception e) {
			Log.error("Failed to log info message: " + e.getMessage(), e);
			throw new FrameworkException("Info logging failed", e);
		}
	}

	/**
	 * Logs pass message to current test with validation
	 */
	public static void logPass(String message) {
		try {
			if (message == null) {
				message = "Pass message was null";
			}

			ExtentTest test = getTest();
			test.pass(message);
			Log.info("Extent Report Pass: " + message);

		} catch (Exception e) {
			Log.error("Failed to log pass message: " + e.getMessage(), e);
			throw new FrameworkException("Pass logging failed", e);
		}
	}

	/**
	 * Logs fail message to current test with validation
	 */
	public static void logFail(String message) {
		try {
			if (message == null) {
				message = "Fail message was null";
			}

			ExtentTest test = getTest();
			test.fail(message);
			Log.error("Extent Report Fail: " + message);

		} catch (Exception e) {
			Log.error("Failed to log fail message: " + e.getMessage(), e);
			throw new FrameworkException("Fail logging failed", e);
		}
	}

	/**
	 * Logs warning message to current test with validation
	 */
	public static void logWarning(String message) {
		try {
			if (message == null) {
				message = "Warning message was null";
			}

			ExtentTest test = getTest();
			test.warning(message);
			Log.warn("Extent Report Warning: " + message);

		} catch (Exception e) {
			Log.error("Failed to log warning message: " + e.getMessage(), e);
			throw new FrameworkException("Warning logging failed", e);
		}
	}

	/**
	 * Logs skip message to current test with validation
	 */
	public static void logSkip(String message) {
		try {
			if (message == null) {
				message = "Skip message was null";
			}

			ExtentTest test = getTest();
			test.skip(message);
			Log.info("Extent Report Skip: " + message);

		} catch (Exception e) {
			Log.error("Failed to log skip message: " + e.getMessage(), e);
			throw new FrameworkException("Skip logging failed", e);
		}
	}

	/**
	 * Gets the Extent Reports instance with validation
	 */
	public static ExtentReports getExtentReports() {
		try {
			if (extent == null) {
				throw new FrameworkException("Extent Reports not initialized. Call initExtentReport() first");
			}
			return extent;

		} catch (Exception e) {
			Log.error("Failed to get ExtentReports instance: " + e.getMessage(), e);
			throw new FrameworkException("ExtentReports instance retrieval failed", e);
		}
	}

	// ================================
	// CORRECTED SCREENSHOT ATTACHMENT METHODS
	// ================================

	/**
	 * Attaches screenshot to failure message with proper ThreadLocal usage
	 * 
	 * @param screenshotPath Path to the screenshot file
	 * @param failureMessage The failure message to log
	 */
	public static void attachScreenshotToFailure(String screenshotPath, String failureMessage) throws IOException {
		try {
			// Get the actual ExtentTest instance from ThreadLocal
			ExtentTest test = extentTest.get();

			if (test != null && screenshotPath != null) {
				// Convert to relative path for better portability
				String relativePath = screenshotPath.replace(System.getProperty("user.dir"), ".");

				// Use MediaEntityBuilder to attach screenshot inline with failure message
				test.fail(failureMessage, 
						MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());

				Log.info("Screenshot attached successfully to failure report");
			} else {
				Log.warn("ExtentTest or screenshot path is null");
				if (test != null) {
					test.fail(failureMessage);
				}
			}
		} catch (Exception e) {
			Log.error("Unexpected error while attaching screenshot: " + e.getMessage());
			ExtentTest test = extentTest.get();
			if (test != null) {
				test.fail(failureMessage);
			}
		}
	}

	/**
	 * Logs failure with screenshot using proper ThreadLocal usage
	 * 
	 * @param message The failure message to log
	 * @param screenshotPath Path to the screenshot file
	 */
	public static void logFailureWithScreenshot(String message, String screenshotPath) throws IOException {
		ExtentTest test = extentTest.get();

		if (test != null && screenshotPath != null) {
			// ✅ CRITICAL: Convert to relative path if absolute
			String relativePath = screenshotPath;

			// If absolute path, convert to relative
			if (screenshotPath.contains("reports" + File.separator + "screenshots")) {
				// Extract just the relative part: ./screenshots/filename.png
				String fileName = new File(screenshotPath).getName();
				relativePath = "./screenshots/" + fileName;
			}

			Log.info("Using relative screenshot path: {}", relativePath);

			test.fail("❌ " + message, 
					MediaEntityBuilder.createScreenCaptureFromPath(relativePath).build());

			Log.info("Screenshot attached successfully to failure: {}", relativePath);
		} else {
			Log.warn("ExtentTest or screenshot path is null");
			if (test != null) {
				test.fail("❌ " + message);
			}
		}
	}

	/**
	 * Thread-safe test creation with thread name tracking
	 */
	public static ExtentTest createTest(String testName, String testDescription) {
		synchronized (REPORT_LOCK) {
			try {
				if (testName == null || testName.trim().isEmpty()) {
					throw new FrameworkException("Test name cannot be null or empty");
				}

				if (extent == null) {
					throw new FrameworkException("Extent Reports not initialized. Call initExtentReport() first");
				}

				// ✅ Use thread name instead of ID
				String threadName = Thread.currentThread().getName();
				String threadInfo = String.format("[%s]", threadName);
				String enhancedTestName = testName + " " + threadInfo;

				String description = (testDescription != null && !testDescription.trim().isEmpty()) 
						? testDescription : "No description provided";

				ExtentTest test = extent.createTest(enhancedTestName, description);
				extentTest.set(test);

				// Track thread-test mapping using thread name
				threadTestMap.put(threadName, testName);

				Log.info("Test created in Extent Report: {} on Thread: {}", testName, threadName);
				return test;

			} catch (Exception e) {
				Log.error("Failed to create test with description: {}", e.getMessage(), e);
				throw new FrameworkException("Test creation failed for: " + testName, e);
			}
		}
	}

	/**
	 * Thread-safe test removal with thread name tracking
	 */
	public static void removeTest() {
		try {
			String threadName = Thread.currentThread().getName();
			String testName = threadTestMap.remove(threadName);

			extentTest.remove();

			Log.debug("ExtentTest removed for {} on Thread: {}", 
					testName != null ? testName : "Unknown", threadName);

		} catch (Exception e) {
			Log.warn("Error removing test from ThreadLocal: {}", e.getMessage(), e);
		}
	}

	/**
	 * Get current thread's test information
	 */
	public static String getCurrentThreadTestInfo() {
		try {
			String threadName = Thread.currentThread().getName();
			String testName = threadTestMap.get(threadName);
			return String.format("%s: %s", threadName, testName != null ? testName : "No Test");
		} catch (Exception e) {
			return "Thread info unavailable";
		}
	}

	/**
	 * Monitor active threads and tests
	 */
	public static void logActiveThreads() {
		try {
			if (!threadTestMap.isEmpty()) {
				Log.info("🧵 Active Test Threads:");
				threadTestMap.forEach((threadName, testName) -> 
				Log.info("   {}: {}", threadName, testName)
						);
			}
		} catch (Exception e) {
			Log.debug("Error logging active threads: {}", e.getMessage());
		}
	}

	/**
	 * Flush reports with thread safety
	 */
	@AfterSuite
	public static void flushExtentReport() {
		synchronized (REPORT_LOCK) {
			try {
				// Log final thread summary
				logActiveThreads();

				if (extent != null) {
					extent.flush();
					Log.info("Extent Report flushed successfully. Report saved at: {}", reportPath);

					// Clear thread tracking
					threadTestMap.clear();
					Log.info("Thread tracking cleared - {} active threads cleaned up", 
							threadTestMap.size());
				} else {
					Log.warn("Extent Reports instance is null. Nothing to flush.");
				}

			} catch (Exception e) {
				Log.error("Failed to flush Extent Report: {}", e.getMessage(), e);
				throw new FrameworkException("Extent Report flush failed", e);
			}
		}
	}

	/**
	 * Helper method to set ExtentTest in ThreadLocal (for use in listeners)
	 * 
	 * @param test ExtentTest instance to set
	 */
	public static void setExtentTest(ExtentTest test) {
		extentTest.set(test);
	}

	/**
	 * Helper method to get current ExtentTest from ThreadLocal
	 * 
	 * @return Current ExtentTest instance or null if not set
	 */
	public static ExtentTest getCurrentTest() {
		return extentTest.get();
	}
}
