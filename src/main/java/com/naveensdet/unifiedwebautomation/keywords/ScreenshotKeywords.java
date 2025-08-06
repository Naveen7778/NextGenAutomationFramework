package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * ScreenshotKeywords - advanced screenshot capture and manipulation methods.
 */
public class ScreenshotKeywords {

	private final WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(ScreenshotKeywords.class);

	public ScreenshotKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/** Reads integer config property or returns default if missing or invalid. */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) return defaultValue;
		try {
			return Integer.parseInt(BaseClass.getProps().getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid config for '{}'; using default {}", key, defaultValue);
			return defaultValue;
		}
	}

	/**
	 * Constructor that accepts WebDriver instance
	 * @param driver WebDriver instance for taking screenshots
	 */
	public ScreenshotKeywords(WebDriver driver) {
		if (driver == null) {
			throw new IllegalArgumentException("WebDriver cannot be null");
		}
		this.driver = driver;
		logger.debug("ScreenshotKeywords initialized with WebDriver: {}", driver.getClass().getSimpleName());
	}

	/** Validates string input; throws FrameworkException if null or empty. */
	private void validateInput(String val, String paramName) {
		if(val == null || val.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	/** Returns configured FluentWait for given timeout with polling. */
	private FluentWait<WebDriver> getWait(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(pollingMillis))
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	/**
	 * Takes a full-page screenshot if supported by driver; falls back to viewport screenshot.
	 *
	 * @param filePath Absolute file path where screenshot will be saved (including extension).
	 * @throws FrameworkException If unable to take or save screenshot.
	 */
	public void takeFullPageScreenshot(String filePath) {
		validateInput(filePath, "File Path");

		try {
			BaseClass.logActionStart("Taking full page screenshot to '" + filePath + "'", "Screenshot Capture");

			// For drivers supporting full page screenshot (e.g. Firefox)
			if (driver instanceof TakesScreenshot) {
				File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				saveFile(screenshot, filePath);
				logger.info("Full page screenshot saved to {}", filePath);

				BaseClass.logActionSuccess("Full page screenshot captured and saved", "Screenshot Capture");
			} else {
				BaseClass.logActionFailure("Take full page screenshot", "Screenshot Capture", "Driver does not support screenshots");
				throw new UnsupportedOperationException("Driver does not support screenshots");
			}

		} catch (IOException | WebDriverException e) {
			BaseClass.logActionFailure("Take full page screenshot", "Screenshot Capture", "Failed to capture or save screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to take full page screenshot: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Take full page screenshot", "Screenshot Capture", "Unexpected error: " + e.getMessage());
			throw new FrameworkException("Failed to take full page screenshot", e);
		}
	}

	/**
	 * Takes a screenshot of a specific element identified by XPath.
	 *
	 * @param xpath XPath locator of the element.
	 * @param filePath Absolute file path to save the element screenshot.
	 * @param timeoutSeconds Time to wait for element visible/clickable.
	 * @throws FrameworkException if element not found or screenshot failure.
	 */
	public void takeElementScreenshot(String xpath, String filePath, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(filePath, "File Path");

		try {
			BaseClass.logActionStart("Taking element screenshot of '" + xpath + "' to '" + filePath + "'", "Screenshot Capture");

			WebElement element = getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			File screenshot = element.getScreenshotAs(OutputType.FILE);
			saveFile(screenshot, filePath);
			logger.info("Element screenshot saved to {}", filePath);

			BaseClass.logActionSuccess("Element screenshot captured and saved", "Screenshot Capture");

		} catch (IOException | WebDriverException e) {
			BaseClass.logActionFailure("Take element screenshot", "Screenshot Capture", "Failed to capture element screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to capture element screenshot: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Take element screenshot", "Screenshot Capture", "Failed to take element screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to take element screenshot", e);
		}
	}

	/**
	 * Takes a screenshot of the visible viewport (browser window).
	 *
	 * @param filePath Absolute file path to save the viewport screenshot.
	 * @throws FrameworkException if capture or saving fails.
	 */
	public void takeViewportScreenshot(String filePath) {
		validateInput(filePath, "File Path");

		try {
			BaseClass.logActionStart("Taking viewport screenshot to '" + filePath + "'", "Screenshot Capture");

			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			saveFile(screenshot, filePath);
			logger.info("Viewport screenshot saved to {}", filePath);

			BaseClass.logActionSuccess("Viewport screenshot captured and saved", "Screenshot Capture");

		} catch (IOException | WebDriverException e) {
			BaseClass.logActionFailure("Take viewport screenshot", "Screenshot Capture", "Failed to capture or save viewport screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to take viewport screenshot: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Take viewport screenshot", "Screenshot Capture", "Failed to take viewport screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to take viewport screenshot", e);
		}
	}

	/**
	 * Captures screenshot and returns it as BufferedImage.
	 * Useful for further processing or cropping.
	 *
	 * @return BufferedImage screenshot of the full viewport.
	 */
	public BufferedImage captureAsImage() {
		try {
			BaseClass.logActionStart("Capturing screenshot as BufferedImage", "Screenshot Processing");

			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage image = ImageIO.read(screenshot);

			BaseClass.logActionSuccess("Screenshot captured as BufferedImage", "Screenshot Processing");
			return image;

		} catch (IOException | WebDriverException e) {
			BaseClass.logActionFailure("Capture as BufferedImage", "Screenshot Processing", "Failed to capture screenshot as BufferedImage: " + e.getMessage());
			throw new FrameworkException("Failed to capture screenshot as BufferedImage", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Capture as BufferedImage", "Screenshot Processing", "Failed to capture as image: " + e.getMessage());
			throw new FrameworkException("Failed to capture as BufferedImage", e);
		}
	}

	/**
	 * Captures a cropped screenshot of a specific region within the viewport.
	 *
	 * @param x X coordinate of the region's top-left corner.
	 * @param y Y coordinate of the region's top-left corner.
	 * @param width Width of the region to capture.
	 * @param height Height of the region to capture.
	 * @param filePath Absolute file path to save the cropped screenshot.
	 * @throws FrameworkException if error occurs during capture or save.
	 */
	public void takeCroppedScreenshot(int x, int y, int width, int height, String filePath) {
		validateInput(filePath, "File Path");
		if (width <= 0 || height <= 0) {
			BaseClass.logActionFailure("Take cropped screenshot", "Screenshot Processing", "Width and height must be positive: width=" + width + ", height=" + height);
			throw new FrameworkException("Width and height must be positive for cropping screenshot");
		}

		try {
			BaseClass.logActionStart("Taking cropped screenshot (" + x + "," + y + "," + width + "x" + height + ") to '" + filePath + "'", "Screenshot Processing");

			BufferedImage fullImg = captureAsImage();
			Rectangle rect = new Rectangle(x, y, width, height);
			BufferedImage cropped = fullImg.getSubimage(rect.x, rect.y, rect.width, rect.height);

			File outputFile = new File(filePath);
			ImageIO.write(cropped, getFileExtension(filePath), outputFile);
			logger.info("Cropped screenshot saved to {}", filePath);

			BaseClass.logActionSuccess("Cropped screenshot captured and saved", "Screenshot Processing");

		} catch (IOException e) {
			BaseClass.logActionFailure("Take cropped screenshot", "Screenshot Processing", "Failed to save cropped screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to save cropped screenshot: " + filePath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Take cropped screenshot", "Screenshot Processing", "Failed to take cropped screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to take cropped screenshot", e);
		}
	}

	/**
	 * Saves a screenshot file to the specified destination path.
	 *
	 * @param sourceFile Source image File.
	 * @param destPath Destination path (absolute).
	 * @throws IOException if saving fails.
	 */
	private void saveFile(File sourceFile, String destPath) throws IOException {
		Path dest = Paths.get(destPath);
		File destFile = dest.toFile();
		File parent = destFile.getParentFile();
		if (parent != null && !parent.exists()) {
			boolean created = parent.mkdirs();
			if (!created) 
				logger.warn("Could not create directory structure for {}", destPath);
		}
		org.openqa.selenium.io.FileHandler.copy(sourceFile, destFile);
	}

	/**
	 * Extracts the file extension (without dot) from a filename.
	 *
	 * @param filename The filename or absolute path string.
	 * @return File extension in lowercase, or "png" if none found.
	 */
	private String getFileExtension(String filename) {
		int dotIndex = filename.lastIndexOf('.');
		if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
			return filename.substring(dotIndex + 1).toLowerCase();
		}
		return "png"; // default to png if extension missing
	}

	/**
	 * Takes a screenshot and returns it as Base64 encoded string.
	 * Useful for embedding screenshots in reports or messages.
	 *
	 * @return Base64 screenshot string.
	 * @throws FrameworkException if capture fails.
	 */
	public String getScreenshotAsBase64() {
		try {
			BaseClass.logActionStart("Capturing screenshot as Base64 string", "Screenshot Data");

			String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

			BaseClass.logActionSuccess("Screenshot captured as Base64 string", "Screenshot Data");
			return base64Screenshot;

		} catch (WebDriverException e) {
			BaseClass.logActionFailure("Get screenshot as Base64", "Screenshot Data", "Failed to capture screenshot as Base64: " + e.getMessage());
			throw new FrameworkException("Failed to capture screenshot as Base64", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Get screenshot as Base64", "Screenshot Data", "Failed to get Base64 screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to get Base64 screenshot", e);
		}
	}

	/**
	 * Takes a screenshot of a specified element and returns Base64 encoding.
	 *
	 * @param xpath XPath locator of the element.
	 * @param timeoutSeconds Max wait to ensure element is visible.
	 * @return Base64 string of the element screenshot.
	 * @throws FrameworkException if capture fails.
	 */
	public String getElementScreenshotAsBase64(String xpath, int timeoutSeconds) {
		validateInput(xpath, "XPath");

		try {
			BaseClass.logActionStart("Capturing element screenshot as Base64 for '" + xpath + "'", "Screenshot Data");

			WebElement element = getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			String base64Screenshot = element.getScreenshotAs(OutputType.BASE64);

			BaseClass.logActionSuccess("Element screenshot captured as Base64", "Screenshot Data");
			return base64Screenshot;

		} catch (WebDriverException e) {
			BaseClass.logActionFailure("Get element screenshot as Base64", "Screenshot Data", "Failed to capture element screenshot as Base64: " + e.getMessage());
			throw new FrameworkException("Failed to capture element screenshot as Base64 for element: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Get element screenshot as Base64", "Screenshot Data", "Failed to get element Base64 screenshot: " + e.getMessage());
			throw new FrameworkException("Failed to get element Base64 screenshot", e);
		}
	}

	/**
	 * Takes a screenshot of the visible viewport and returns as byte array.
	 *
	 * @return byte[] containing raw screenshot data.
	 * @throws FrameworkException if capture fails.
	 */
	public byte[] getViewportScreenshotAsBytes() {
		try {
			BaseClass.logActionStart("Capturing viewport screenshot as byte array", "Screenshot Data");

			byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

			BaseClass.logActionSuccess("Viewport screenshot captured as byte array", "Screenshot Data");
			return screenshotBytes;

		} catch (WebDriverException e) {
			BaseClass.logActionFailure("Get viewport screenshot as bytes", "Screenshot Data", "Failed to capture viewport screenshot as bytes: " + e.getMessage());
			throw new FrameworkException("Failed to capture viewport screenshot as bytes", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Get viewport screenshot as bytes", "Screenshot Data", "Failed to get screenshot bytes: " + e.getMessage());
			throw new FrameworkException("Failed to get screenshot bytes", e);
		}
	}
}
