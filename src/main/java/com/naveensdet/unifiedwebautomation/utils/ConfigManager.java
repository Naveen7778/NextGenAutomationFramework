package com.naveensdet.unifiedwebautomation.utils;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.File;

public class ConfigManager {
	private static Properties props = new Properties();

	static {
		loadConfig();
	}

	private static void loadConfig() {
		try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null) {
				throw new RuntimeException("Config properties file not found");
			}
			props.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load config: config.properties", e);
		}
	}

	// Existing methods
	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

	// NEW: Get absolute path for test resources directory
	public static String getTestResourcesDirectory() {
		return Paths.get(System.getProperty("user.dir"), "src", "test", "resources").toString();
	}

	// NEW: Get absolute path from relative path in test resources
	public static String getTestResourcePath(String configKey, String defaultRelativePath) {
		String relativePath = getProperty(configKey, defaultRelativePath);
		return Paths.get(getTestResourcesDirectory(), relativePath).toString();
	}

	// NEW: Convenience methods for your specific directories
	public static String getTestDataDirectory() {
		return getTestResourcePath("testdata.directory", "testdata");
	}

	public static String getDownloadDirectory() {
		return getTestResourcePath("download.directory", "downloads");
	}

	public static String getUploadDirectory() {
		return getTestResourcePath("upload.directory", "uploads");
	}

	public static String getTempDirectory() {
		return getTestResourcePath("temp.directory", "temp");
	}

	// NEW: Ensure directory exists (create if doesn't exist)
	public static String ensureDirectoryExists(String directoryPath) {
		File dir = new File(directoryPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return directoryPath;
	}

	// NEW: Get directory and ensure it exists
	public static String getDownloadDirectoryEnsureExists() {
		return ensureDirectoryExists(getDownloadDirectory());
	}

	public static String getUploadDirectoryEnsureExists() {
		return ensureDirectoryExists(getUploadDirectory());
	}

	// Existing methods remain unchanged
	public static String getEnvSpecificProperty(String key, String defaultValue) {
		String env = System.getProperty("environment");
		if (env == null || env.trim().isEmpty()) {
			env = props.getProperty("environment");
		}
		if (env == null || env.trim().isEmpty()) {
			throw new RuntimeException("Environment property not defined");
		}
		return props.getProperty(env.trim().toLowerCase() + "." + key, defaultValue);
	}

	public static int getIntProperty(String key, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Gets the screenshot directory path from configuration.
	 */
	public static String getScreenshotDirectory() {
		try {
			// ✅ FIXED: Return path aligned with reports folder
			String reportsDir = System.getProperty("user.dir") + File.separator + "reports";
			String screenshotsDir = reportsDir + File.separator + "screenshots";

			// Ensure directory exists
			File directory = new File(screenshotsDir);
			if (!directory.exists()) {
				boolean created = directory.mkdirs();
				if (created) {
					System.out.println("📁 Created screenshots directory: " + screenshotsDir);
				}
			}

			return screenshotsDir;

		} catch (Exception e) {
			System.err.println("❌ Error getting screenshot directory: " + e.getMessage());
			// Fallback to reports/screenshots
			return System.getProperty("user.dir") + File.separator + "reports" + File.separator + "screenshots";
		}
	}

	/**
	 * Gets screenshot directory and ensures it exists.
	 */
	public static String getScreenshotDirectoryEnsureExists() {
		return ensureDirectoryExists(getScreenshotDirectory());
	}

}
