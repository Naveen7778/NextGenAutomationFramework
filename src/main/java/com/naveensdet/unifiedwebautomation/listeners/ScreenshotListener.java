package com.naveensdet.unifiedwebautomation.listeners;

import com.naveensdet.unifiedwebautomation.keywords.ScreenshotKeywords;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExtentReportManager;
import com.naveensdet.unifiedwebautomation.utils.ScreenshotUtility;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenshotListener implements ITestListener {

	private static final Logger logger = LoggerFactory.getLogger(ScreenshotListener.class);
	private ScreenshotKeywords screenshotKeywords;

	public ScreenshotListener() {
		logger.info("ScreenshotListener instantiated successfully");
	}

	private ScreenshotKeywords getScreenshotKeywords() {
		if (screenshotKeywords == null && DriverManager.isDriverInitialized()) {
			try {
				screenshotKeywords = new ScreenshotKeywords(DriverManager.getDriverSafely());
				logger.debug("ScreenshotKeywords initialized successfully");
			} catch (Exception e) {
				logger.warn("Failed to initialize ScreenshotKeywords: {}", e.getMessage());
			}
		}
		return screenshotKeywords;
	}

	@Override
	public void onTestStart(ITestResult result) {
		logger.info("Test started: {}", result.getMethod().getMethodName());
		getScreenshotKeywords(); // Initialize when test starts
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failureMessage = result.getThrowable() != null ? 
				result.getThrowable().getMessage() : "Test execution failed";

		try {
			// ✅ ONLY capture screenshot on failure
			String screenshotPath = ScreenshotUtility.generateFailureScreenshotPath(
					"failure", testName, "test_failed"
					);

			ScreenshotKeywords keywords = getScreenshotKeywords();
			if (keywords != null) {
				keywords.takeFullPageScreenshot(screenshotPath);

				// ✅ Attach screenshot to failure message
				ExtentReportManager.logFailureWithScreenshot(
						"Test Failed: " + failureMessage, screenshotPath
						);

				logger.info("📸 Failure screenshot captured and attached: {}", screenshotPath);
			} else {
				// Fallback - log failure without screenshot
				ExtentReportManager.logFail("Test Failed: " + failureMessage);
				logger.warn("Could not capture screenshot - ScreenshotKeywords not available");
			}

		} catch (Exception e) {
			logger.error("Error in failure handling", e);
			// Ensure failure is still logged even if screenshot fails
			ExtentReportManager.logFail("Test Failed: " + failureMessage);
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// ✅ NO screenshot on success - just log success
		String testName = result.getMethod().getMethodName();
		logger.info("✅ Test PASSED: {}", testName);

		// Optional: Enable success screenshots via configuration
		String captureOnSuccess = System.getProperty("screenshot.onSuccess", "false");
		if ("true".equalsIgnoreCase(captureOnSuccess)) {
			try {
				// ✅ Use existing method with different prefix
				String screenshotPath = ScreenshotUtility.generateFailureScreenshotPath(
						"success", testName, "test_passed"
						);

				ScreenshotKeywords keywords = getScreenshotKeywords();
				if (keywords != null) {
					keywords.takeFullPageScreenshot(screenshotPath);
					ExtentReportManager.attachScreenshot(screenshotPath);
					logger.info("📸 Success screenshot captured (optional): {}", screenshotPath);
				}
			} catch (Exception e) {
				logger.debug("Could not capture optional success screenshot: {}", e.getMessage());
			}
		}
	}


	@Override
	public void onTestSkipped(ITestResult result) {
		// ✅ NO screenshot on skip - just log skip reason
		String testName = result.getMethod().getMethodName();
		String skipReason = result.getThrowable() != null ? 
				result.getThrowable().getMessage() : "Test was skipped";

		logger.warn("⏭️ Test SKIPPED: {} - {}", testName, skipReason);
	}
}
