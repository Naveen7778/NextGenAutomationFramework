package com.naveensdet.unifiedwebautomation.listeners;

import com.naveensdet.unifiedwebautomation.utils.ExtentReportManager;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.annotations.BeforeSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.*;
import java.util.zip.*;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * SuiteListener - handles ONLY suite-level operations
 * NO driver initialization - only reports, directories, and environment setup
 */
public class SuiteListener implements ISuiteListener {

	private static final Logger logger = LoggerFactory.getLogger(SuiteListener.class);

	/**
	 * WORKING: Add this method to your SuiteListener.java class
	 */
	@BeforeSuite(alwaysRun = true)
	public void cleanupPreviousReports() {
		try {
			System.out.println("🧹 SuiteListener: Starting cleanup of previous reports...");
			logger.info("🧹 SuiteListener: Starting cleanup of previous reports...");

			String reportsDir = System.getProperty("user.dir") + File.separator + "reports";
			File reportsFolder = new File(reportsDir);

			System.out.println("🔍 Target directory: " + reportsDir);
			logger.info("🔍 Target directory: {}", reportsDir);

			if (reportsFolder.exists() && reportsFolder.isDirectory()) {
				File[] files = reportsFolder.listFiles();

				if (files != null && files.length > 0) {
					System.out.println("📊 Found " + files.length + " items to delete");
					logger.info("📊 Found {} items to delete", files.length);

					for (File file : files) {
						try {
							if (file.isDirectory()) {
								deleteDirectory(file);
								System.out.println("✅ Deleted directory: " + file.getName());
								logger.info("✅ Deleted directory: {}", file.getName());
							} else {
								if (file.delete()) {
									System.out.println("✅ Deleted file: " + file.getName());
									logger.info("✅ Deleted file: {}", file.getName());
								} else {
									System.out.println("❌ Could not delete file: " + file.getName());
									logger.warn("❌ Could not delete file: {}", file.getName());
								}
							}
						} catch (Exception e) {
							System.out.println("❌ Error deleting " + file.getName() + ": " + e.getMessage());
							logger.error("❌ Error deleting {}: {}", file.getName(), e.getMessage());
						}
					}

					System.out.println("✅ Cleanup completed");
					logger.info("✅ Cleanup completed");
				} else {
					System.out.println("📂 Reports directory is empty");
					logger.info("📂 Reports directory is empty");
				}
			} else {
				System.out.println("📂 Reports directory doesn't exist - will be created");
				logger.info("📂 Reports directory doesn't exist - will be created");
			}

		} catch (Exception e) {
			System.out.println("❌ SuiteListener cleanup error: " + e.getMessage());
			logger.error("❌ SuiteListener cleanup error: {}", e.getMessage(), e);
		}
	}

	/**
	 * Helper method to delete directories recursively
	 */
	private void deleteDirectory(File directory) {
		try {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
			directory.delete();
		} catch (Exception e) {
			logger.warn("Error deleting directory {}: {}", directory.getName(), e.getMessage());
		}
	}


	@Override
	public void onStart(ISuite suite) {
		// Add cleanup at the very beginning of onStart
		cleanupPreviousReports();
		String suiteName = suite.getName();
		logger.info("🎪 SUITE STARTING: {}", suiteName);

		try {
			// ✅ ONLY Suite-level operations
			initializeExtentReports();
			createNecessaryDirectories();
			logSuiteInformation(suite);

			logger.info("✅ Suite environment initialized successfully for: {}", suiteName);
		} catch (Exception e) {
			logger.error("❌ Suite initialization failed: {}", e.getMessage(), e);
			throw new RuntimeException("Suite initialization failed", e);
		}
	}

	@Override
	public void onFinish(ISuite suite) {
		String suiteName = suite.getName();
		logger.info("🎪 SUITE FINISHING: {}", suiteName);

		try {
			// ✅ EXISTING: Suite-level cleanup
			flushReports();
			generateSuiteSummary(suite);
			cleanupTemporaryFiles();

			// ✅ NEW: Create ZIP file
			System.out.println("📦 Starting ZIP creation...");
			logger.info("📦 Starting ZIP creation...");
			createReportZip();

			logger.info("✅ Suite cleanup completed successfully for: {}", suiteName);
		} catch (Exception e) {
			logger.error("❌ Suite cleanup failed: {}", e.getMessage(), e);
			e.printStackTrace(); // Add this to see any ZIP creation errors
		}
	}

	/**
	 * Updated ZIP creation method - saves ZIP in reports folder
	 */
	private void createReportZip() {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		try {
			System.out.println("📦 Creating report ZIP in reports folder...");
			logger.info("📦 Creating report ZIP in reports folder...");

			// Brief pause to ensure all files are released
			Thread.sleep(500);

			String reportsDir = System.getProperty("user.dir") + File.separator + "reports";
			File reportsFolder = new File(reportsDir);

			System.out.println("🔍 Checking reports directory: " + reportsDir);
			logger.info("🔍 Checking reports directory: {}", reportsDir);

			if (!reportsFolder.exists() || !reportsFolder.isDirectory()) {
				System.out.println("⚠️ Reports directory not found - no ZIP created");
				logger.warn("⚠️ Reports directory not found - no ZIP created");
				return;
			}

			File[] files = reportsFolder.listFiles();
			if (files == null || files.length == 0) {
				System.out.println("⚠️ Reports directory is empty - no ZIP created");
				logger.warn("⚠️ Reports directory is empty - no ZIP created");
				return;
			}

			System.out.println("📊 Found " + files.length + " items to ZIP");
			logger.info("📊 Found {} items to ZIP", files.length);

			// Create ZIP filename
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String zipFileName = "ExtentReport_" + timestamp + ".zip";

			// ✅ UPDATED: Save ZIP directly in reports folder
			String zipFilePath = reportsDir + File.separator + zipFileName;

			System.out.println("📦 Creating ZIP: " + zipFileName);
			System.out.println("📂 ZIP destination: " + zipFilePath);
			logger.info("📦 Creating ZIP: {}", zipFileName);
			logger.info("📂 ZIP destination: {}", zipFilePath);

			// Create ZIP file
			fos = new FileOutputStream(zipFilePath);
			zos = new ZipOutputStream(fos);

			// Add files to ZIP (excluding the ZIP file itself)
			addDirectoryToZip(reportsFolder, "reports", zos, zipFileName);

			System.out.println("📦 ZIP file created successfully!");
			logger.info("📦 ZIP file created successfully!");

			// Verify creation
			File zipFile = new File(zipFilePath);
			if (zipFile.exists() && zipFile.length() > 0) {
				System.out.println("✅ ZIP file: " + zipFileName);
				System.out.println("📊 Size: " + zipFile.length() + " bytes");
				System.out.println("📍 Location: " + zipFilePath);

				logger.info("✅ ZIP file: {}", zipFileName);
				logger.info("📊 Size: {} bytes", zipFile.length());
				logger.info("📍 Location: {}", zipFilePath);
			} else {
				System.out.println("❌ ZIP creation verification failed");
				logger.error("❌ ZIP creation verification failed");
			}

		} catch (Exception e) {
			System.out.println("❌ ZIP error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
			logger.error("❌ ZIP error: {}", e.getMessage(), e);
			e.printStackTrace();
		} finally {
			// Ensure streams are closed
			try {
				if (zos != null) zos.close();
				if (fos != null) fos.close();
			} catch (IOException e) {
				logger.warn("Error closing ZIP streams: {}", e.getMessage());
			}
		}
	}

	/**
	 * Updated helper method to exclude ZIP files from being added to ZIP
	 */
	private void addDirectoryToZip(File folder, String parentPath, ZipOutputStream zos, String currentZipFileName) throws IOException {
		File[] files = folder.listFiles();

		if (files != null) {
			for (File file : files) {
				// ✅ IMPORTANT: Skip the ZIP file we're currently creating
				if (file.getName().equals(currentZipFileName)) {
					continue;
				}

				if (file.isDirectory()) {
					String dirPath = parentPath + "/" + file.getName() + "/";
					ZipEntry dirEntry = new ZipEntry(dirPath);
					zos.putNextEntry(dirEntry);
					zos.closeEntry();

					addDirectoryToZip(file, parentPath + "/" + file.getName(), zos, currentZipFileName);
					System.out.println("📁 Added directory: " + file.getName());
					logger.debug("📁 Added directory: {}", file.getName());
				} else {
					String filePath = parentPath + "/" + file.getName();
					ZipEntry fileEntry = new ZipEntry(filePath);
					zos.putNextEntry(fileEntry);

					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
						byte[] buffer = new byte[1024];
						int length;
						while ((length = fis.read(buffer)) > 0) {
							zos.write(buffer, 0, length);
						}
					} finally {
						if (fis != null) fis.close();
					}

					zos.closeEntry();
					System.out.println("📄 Added file: " + file.getName());
					logger.debug("📄 Added file: {}", file.getName());
				}
			}
		}
	}


	/**
	 * Initialize ExtentReports - SUITE LEVEL OPERATION
	 */
	private void initializeExtentReports() {
		try {
			ExtentReportManager.initExtentReport();
			logger.info("📊 ExtentReports initialized successfully");
		} catch (Exception e) {
			logger.error("Failed to initialize ExtentReports: {}", e.getMessage(), e);
			throw new RuntimeException("ExtentReports initialization failed", e);
		}
	}

	/**
	 * Create all necessary directories - SUITE LEVEL OPERATION
	 */
	private void createNecessaryDirectories() {
		String[] directories = {
				"screenshots",
				"screenshots/failures", 
				"screenshots/success",
				"reports",
				"logs",
				"test-output"
		};

		for (String dirPath : directories) {
			createDirectory(dirPath);
		}
		logger.info("📁 All necessary directories created/validated");
	}

	/**
	 * Create individual directory with validation
	 */
	private void createDirectory(String path) {
		File directory = new File(path);
		if (!directory.exists()) {
			boolean created = directory.mkdirs();
			if (created) {
				logger.info("✅ Created directory: {}", path);
			} else {
				logger.warn("⚠️ Failed to create directory: {}", path);
			}
		} else {
			logger.debug("Directory already exists: {}", path);
		}
	}

	/**
	 * Log suite information - SUITE LEVEL OPERATION
	 */
	private void logSuiteInformation(ISuite suite) {
		try {
			logger.info("📋 Suite Name: {}", suite.getName());
			logger.info("📋 Test Count: {}", suite.getAllMethods().size());
			logger.info("📋 Suite XML File: {}", suite.getXmlSuite().getFileName());
		} catch (Exception e) {
			logger.warn("Could not log suite information: {}", e.getMessage());
		}
	}

	/**
	 * Flush all reports - SUITE LEVEL OPERATION
	 */
	private void flushReports() {
		try {
			ExtentReportManager.flushExtentReport();
			logger.info("📊 All reports flushed successfully");
		} catch (Exception e) {
			logger.error("Failed to flush reports: {}", e.getMessage(), e);
		}
	}

	/**
	 * Generate suite execution summary - SUITE LEVEL OPERATION
	 */
	private void generateSuiteSummary(ISuite suite) {
		try {
			int totalTests = suite.getAllMethods().size();
			logger.info("📊 SUITE SUMMARY for '{}':", suite.getName());
			logger.info("📊 Total Test Methods: {}", totalTests);
			logger.info("📊 Suite execution completed successfully");
		} catch (Exception e) {
			logger.warn("Could not generate suite summary: {}", e.getMessage());
		}
	}

	/**
	 * Cleanup temporary files - SUITE LEVEL OPERATION
	 */
	private void cleanupTemporaryFiles() {
		try {
			// Add cleanup logic if needed
			logger.info("🧹 Temporary files cleanup completed");
		} catch (Exception e) {
			logger.warn("Cleanup warning: {}", e.getMessage());
		}
	}
}
