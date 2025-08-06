package com.naveensdet.unifiedwebautomation.utils;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtility - Utility class for managing screenshot paths and operations
 * Provides methods for generating unique screenshot file paths
 */
public final class ScreenshotUtility {

	// Private constructor to prevent instantiation
	private ScreenshotUtility() {
		throw new AssertionError("Utility class should not be instantiated");
	}

	/**
	 * Generates a unique screenshot file path using action name and timestamp.
	 * @param actionName Descriptive name for the screenshot action
	 * @return Full file path where screenshot should be saved
	 */
	public static String generateScreenshotPath(String actionName) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String screenshotDir = ConfigManager.getScreenshotDirectory();
		return Paths.get(screenshotDir, actionName + "_" + timestamp + ".png").toString();
	}

	/**
	 * Generates screenshot path with test case name for better organization.
	 * @param actionName Descriptive name for the screenshot action
	 * @param testCaseName Name of the test case
	 * @return Full file path where screenshot should be saved
	 */
	public static String generateScreenshotPath(String actionName, String testCaseName) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String screenshotDir = ConfigManager.getScreenshotDirectory();
		return Paths.get(screenshotDir, testCaseName + "_" + actionName + "_" + timestamp + ".png").toString();
	}

	/**
	 * Generates screenshot path for failure scenarios with additional context.
	 * @param actionName Descriptive name for the screenshot action
	 * @param testCaseName Name of the test case
	 * @param failureReason Brief description of failure
	 * @return Full file path where screenshot should be saved
	 */
	public static String generateFailureScreenshotPath(String actionName, String testCaseName, String failureReason) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String screenshotDir = ConfigManager.getScreenshotDirectory();
		String sanitizedReason = failureReason.replaceAll("[^a-zA-Z0-9]", "_");
		return Paths.get(screenshotDir, "FAILURE_" + testCaseName + "_" + actionName + "_" + sanitizedReason + "_" + timestamp + ".png").toString();
	}
}
