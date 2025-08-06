package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExcelUtilities;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.List;

/**
 * FrameKeywords - keyword utilities for handling frames and iframes in web pages.
 */
public class FrameKeywords {

	private final WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(FrameKeywords.class);

	public FrameKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/** Retrieves an int config property or returns default if absent or invalid. */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(BaseClass.getProps().getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid config for '{}'; default {} used", key, defaultValue);
			return defaultValue;
		}
	}

	/** Validates that a parameter is not null or empty, else throws FrameworkException. */
	private void validateInput(String val, String paramName) {
		if (val == null || val.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	/** Returns FluentWait configured with given timeout and polling interval from config.properties. */
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

	// --- Switching Context ---

	/** Switch to frame by zero-based index within timeout. */
	public void switchToFrameByIndex(int index, int timeoutSeconds) {
		if (index < 0) {
			BaseClass.logActionFailure("Switch to frame by index", "Frame Context", "Frame index cannot be negative: " + index);
			throw new FrameworkException("Frame index cannot be negative");
		}

		try {
			BaseClass.logActionStart("Switching to frame by index " + index, "Frame Context");

			boolean switched = getWait(timeoutSeconds).until(d -> {
				d.switchTo().defaultContent();
				d.switchTo().frame(index);
				return true;
			});

			if (!switched) {
				BaseClass.logActionFailure("Switch to frame by index", "Frame Context", "Could not switch to frame with index: " + index);
				throw new FrameworkException("Could not switch to frame with index: " + index);
			}

			logger.info("Switched to frame by index {}", index);
			BaseClass.logActionSuccess("Switched to frame by index " + index, "Frame Context");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Switch to frame by index", "Frame Context", "Timeout waiting to switch to frame index " + index + ": " + e.getMessage());
			throw new FrameworkException("Timeout waiting to switch to frame index " + index, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to frame by index", "Frame Context", "Failed to switch to frame by index: " + e.getMessage());
			throw new FrameworkException("Failed to switch to frame by index", e);
		}
	}

	/** Switch to frame by name or ID within timeout. */
	public void switchToFrameByNameOrId(String nameOrId, int timeoutSeconds) {
		validateInput(nameOrId, "Frame Name/ID");

		try {
			BaseClass.logActionStart("Switching to frame by name/ID '" + nameOrId + "'", "Frame Context");

			boolean switched = getWait(timeoutSeconds).until(d -> {
				d.switchTo().defaultContent();
				d.switchTo().frame(nameOrId);
				return true;
			});

			if (!switched) {
				BaseClass.logActionFailure("Switch to frame by name/ID", "Frame Context", "Could not switch to frame with name or ID: " + nameOrId);
				throw new FrameworkException("Could not switch to frame with name or ID: " + nameOrId);
			}

			logger.info("Switched to frame by name/ID '{}'", nameOrId);
			BaseClass.logActionSuccess("Switched to frame by name/ID '" + nameOrId + "'", "Frame Context");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Switch to frame by name/ID", "Frame Context", "Timeout waiting to switch to frame name/ID '" + nameOrId + "': " + e.getMessage());
			throw new FrameworkException("Timeout waiting to switch to frame name/ID '" + nameOrId + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to frame by name/ID", "Frame Context", "Failed to switch to frame by name/ID: " + e.getMessage());
			throw new FrameworkException("Failed to switch to frame by name/ID", e);
		}
	}

	/** Switch to frame by XPath within timeout. */
	public void switchToFrameByXPath(String xpath, int timeoutSeconds) {
		validateInput(xpath, "Frame XPath");

		try {
			BaseClass.logActionStart("Switching to frame by XPath '" + xpath + "'", "Frame Context");

			boolean switched = getWait(timeoutSeconds).until(d -> {
				d.switchTo().defaultContent();
				WebElement frameElem = d.findElement(By.xpath(xpath));
				d.switchTo().frame(frameElem);
				return true;
			});

			if (!switched) {
				BaseClass.logActionFailure("Switch to frame by XPath", "Frame Context", "Could not switch to frame by XPath: " + xpath);
				throw new FrameworkException("Could not switch to frame by XPath: " + xpath);
			}

			logger.info("Switched to frame by XPath '{}'", xpath);
			BaseClass.logActionSuccess("Switched to frame by XPath '" + xpath + "'", "Frame Context");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Switch to frame by XPath", "Frame Context", "Timeout waiting to switch to frame by XPath '" + xpath + "': " + e.getMessage());
			throw new FrameworkException("Timeout waiting to switch to frame by XPath '" + xpath + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to frame by XPath", "Frame Context", "Failed to switch to frame by XPath: " + e.getMessage());
			throw new FrameworkException("Failed to switch to frame by XPath", e);
		}
	}

	/** Switch to frame using a previously located WebElement. */
	public void switchToFrameByElement(WebElement frameElement) {
		if (frameElement == null) {
			BaseClass.logActionFailure("Switch to frame by element", "Frame Context", "Frame element cannot be null");
			throw new FrameworkException("Frame element cannot be null");
		}

		try {
			BaseClass.logActionStart("Switching to frame by WebElement", "Frame Context");

			driver.switchTo().defaultContent();
			driver.switchTo().frame(frameElement);
			logger.info("Switched to frame by WebElement");

			BaseClass.logActionSuccess("Switched to frame by WebElement", "Frame Context");

		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to frame by element", "Frame Context", "Failed to switch to frame by element: " + e.getMessage());
			throw new FrameworkException("Failed to switch to frame by element", e);
		}
	}

	// --- Returning Context ---

	/** Switch to immediate parent frame of current frame. */
	public void switchToParentFrame() {
		try {
			BaseClass.logActionStart("Switching to parent frame", "Frame Context");

			driver.switchTo().parentFrame();
			logger.info("Switched to parent frame");

			BaseClass.logActionSuccess("Switched to parent frame", "Frame Context");

		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to parent frame", "Frame Context", "Failed to switch to parent frame: " + e.getMessage());
			throw new FrameworkException("Failed to switch to parent frame", e);
		}
	}

	/** Switch to main document (exits all frames). */
	public void switchToDefaultContent() {
		try {
			BaseClass.logActionStart("Switching to default content", "Frame Context");

			driver.switchTo().defaultContent();
			logger.info("Switched to default content");

			BaseClass.logActionSuccess("Switched to default content", "Frame Context");

		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to default content", "Frame Context", "Failed to switch to default content: " + e.getMessage());
			throw new FrameworkException("Failed to switch to default content", e);
		}
	}

	// --- Navigating Frame Chains ---

	/**
	 * Robustly switch through a chain of nested frames identified by locators.
	 * Each locator can be name/id or XPath depending on locatorType.
	 */
	public void switchToMainAndThenToNestedFrames(List<String> frameLocators, FrameLocatorType locatorType, int timeoutSeconds) {
		if (frameLocators == null || frameLocators.isEmpty()) {
			BaseClass.logActionFailure("Switch to nested frames", "Frame Context", "Frame locator list cannot be null or empty");
			throw new FrameworkException("Frame locator list cannot be null or empty");
		}

		try {
			BaseClass.logActionStart("Switching to nested frames chain (depth: " + frameLocators.size() + ")", "Frame Context");

			driver.switchTo().defaultContent();

			for (int i = 0; i < frameLocators.size(); i++) {
				String locator = frameLocators.get(i);
				boolean switched;

				try {
					switched = getWait(timeoutSeconds).until(driver -> {
						switch (locatorType) {
						case NAME_OR_ID:
							driver.switchTo().frame(locator);
							return true;
						case XPATH:
							WebElement frameElem = driver.findElement(By.xpath(locator));
							driver.switchTo().frame(frameElem);
							return true;
						default:
							throw new FrameworkException("Unsupported FrameLocatorType: " + locatorType);
						}
					});
				} catch (TimeoutException e) {
					BaseClass.logActionFailure("Switch to nested frames", "Frame Context", "Timeout switching to nested frame '" + locator + "' at level " + i + ": " + e.getMessage());
					throw new FrameworkException("Timeout switching to nested frame '" + locator + "' at level " + i, e);
				}

				if (!switched) {
					BaseClass.logActionFailure("Switch to nested frames", "Frame Context", "Could not switch to nested frame '" + locator + "' at level " + i);
					throw new FrameworkException("Could not switch to nested frame '" + locator + "' at level " + i);
				}

				logger.info("Switched to nested frame '{}' at level {}", locator, i);
			}

			BaseClass.logActionSuccess("Successfully switched through " + frameLocators.size() + " nested frames", "Frame Context");

		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to nested frames", "Frame Context", "Failed to switch to nested frames: " + e.getMessage());
			throw new FrameworkException("Failed to switch to nested frames", e);
		}
	}

	// --- Frame Presence and Verification ---

	/** Verify that a frame exists by XPath within timeout. */
	public void verifyFramePresentByXPath(String xpath, int timeoutSeconds) {
		validateInput(xpath, "Frame XPath");

		try {
			BaseClass.logActionStart("Verifying frame present by XPath '" + xpath + "'", "Frame Verification");

			boolean present = getWait(timeoutSeconds).until(d -> !d.findElements(By.xpath(xpath)).isEmpty());

			if (!present) {
				BaseClass.logActionFailure("Verify frame present", "Frame Verification", "Frame not present for XPath: " + xpath);
				throw new FrameworkException("Frame not present for XPath: " + xpath);
			}

			logger.info("Verified frame present by XPath '{}'", xpath);
			BaseClass.logActionSuccess("Verified frame present by XPath '" + xpath + "'", "Frame Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify frame present", "Frame Verification", "Timeout waiting for frame present by XPath: " + xpath + ": " + e.getMessage());
			throw new FrameworkException("Timeout waiting for frame present by XPath: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify frame present", "Frame Verification", "Failed to verify frame present: " + e.getMessage());
			throw new FrameworkException("Failed to verify frame present", e);
		}
	}

	/** Verify that a frame is NOT present by XPath within timeout. */
	public void verifyFrameNotPresentByXPath(String xpath, int timeoutSeconds) {
		validateInput(xpath, "Frame XPath");

		try {
			BaseClass.logActionStart("Verifying frame NOT present by XPath '" + xpath + "'", "Frame Verification");

			boolean absent = getWait(timeoutSeconds).until(d -> d.findElements(By.xpath(xpath)).isEmpty());

			if (!absent) {
				BaseClass.logActionFailure("Verify frame not present", "Frame Verification", "Frame still present for XPath: " + xpath);
				throw new FrameworkException("Frame still present for XPath: " + xpath);
			}

			logger.info("Verified frame NOT present by XPath '{}'", xpath);
			BaseClass.logActionSuccess("Verified frame NOT present by XPath '" + xpath + "'", "Frame Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify frame not present", "Frame Verification", "Timeout waiting for frame to NOT be present by XPath: " + xpath + ": " + e.getMessage());
			throw new FrameworkException("Timeout waiting for frame to NOT be present by XPath: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify frame not present", "Frame Verification", "Failed to verify frame not present: " + e.getMessage());
			throw new FrameworkException("Failed to verify frame not present", e);
		}
	}

	/** Soft check — returns true if frame present by XPath, false otherwise. */
	public boolean isFramePresent(String xpath, int timeoutSeconds) {
		validateInput(xpath, "Frame XPath");

		try {
			BaseClass.logActionStart("Checking if frame is present by XPath '" + xpath + "'", "Frame Verification");

			boolean present = getWait(timeoutSeconds).until(d -> !d.findElements(By.xpath(xpath)).isEmpty());

			BaseClass.logActionSuccess("Frame present status: " + present, "Frame Verification");
			return present;

		} catch (Exception e) {
			logger.info("Soft verify: frame NOT present by XPath '{}'", xpath);
			BaseClass.logActionSuccess("Frame not present (soft check)", "Frame Verification");
			return false;
		}
	}

	/** Returns true if driver is currently inside any frame (not default content). */
	public boolean isInsideFrame() {
		try {
			BaseClass.logActionStart("Checking if currently inside a frame", "Frame Context");

			// Attempt to switch to parent frame to see if possible
			driver.switchTo().parentFrame();
			logger.info("Currently inside a frame");

			BaseClass.logActionSuccess("Currently inside a frame", "Frame Context");
			return true;

		} catch (NoSuchFrameException e) {
			logger.info("Currently NOT inside any frame");
			BaseClass.logActionSuccess("Currently NOT inside any frame", "Frame Context");
			return false;
		} catch (Exception e) {
			BaseClass.logActionFailure("Check if inside frame", "Frame Context", "Error checking frame context: " + e.getMessage());
			return false;
		}
	}

	/** Verify that the context is not inside any frame (default content). */
	public void verifyNotInAnyFrame() {
		try {
			BaseClass.logActionStart("Verifying not inside any frame", "Frame Verification");

			driver.switchTo().defaultContent();
			logger.info("Verified currently not inside any frame");

			BaseClass.logActionSuccess("Verified not inside any frame", "Frame Verification");

		} catch (Exception e) {
			BaseClass.logActionFailure("Verify not in any frame", "Frame Verification", "Failed to verify frame context: " + e.getMessage());
			throw new FrameworkException("Failed to verify frame context", e);
		}
	}

	// --- Frame Enumeration / Utilities ---

	/** Returns a List of top-level frame WebElements in current context. */
	public List<WebElement> getAllFrameWebElements() {
		try {
			BaseClass.logActionStart("Getting all frame WebElements", "Frame Utility");

			List<WebElement> frames = driver.findElements(By.tagName("iframe"));
			frames.addAll(driver.findElements(By.tagName("frame")));
			logger.info("Fetched {} top-level frame elements", frames.size());

			BaseClass.logActionSuccess("Retrieved " + frames.size() + " frame elements", "Frame Utility");
			return frames;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get all frame elements", "Frame Utility", "Failed to get frame elements: " + e.getMessage());
			throw new FrameworkException("Failed to get frame elements", e);
		}
	}

	/** Returns the count of all immediate frames in current context. */
	public int getFrameCount() {
		try {
			BaseClass.logActionStart("Getting frame count", "Frame Utility");

			int count = driver.findElements(By.tagName("iframe")).size() + driver.findElements(By.tagName("frame")).size();
			logger.info("Frame count in current context: {}", count);

			BaseClass.logActionSuccess("Frame count: " + count, "Frame Utility");
			return count;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get frame count", "Frame Utility", "Failed to get frame count: " + e.getMessage());
			throw new FrameworkException("Failed to get frame count", e);
		}
	}

	/** Waits until the number of frames matches expectedCount within timeout. Returns true if matched, false otherwise. */
	public boolean waitForFrameCount(int expectedCount, int timeoutSeconds, boolean excelData, String testCaseName) {
		// Declare final variable after conditional processing
		final int actualCountToUse;
		if (excelData) {
			String countValue = getInputValue(testCaseName, String.valueOf(expectedCount), "Frame Count", excelData);

			if (countValue == null || countValue.trim().isEmpty()) {
				throw new FrameworkException("Frame count value from Excel is null or empty for test case: " + testCaseName);
			}

			try {
				actualCountToUse = Integer.parseInt(countValue.trim());
			} catch (NumberFormatException e) {
				throw new FrameworkException("Invalid frame count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
			}
		} else {
			actualCountToUse = expectedCount;
		}

		if (actualCountToUse < 0) {
			BaseClass.logActionFailure("Wait for frame count", "Frame Utility", "Expected frame count must be non-negative: " + actualCountToUse);
			throw new FrameworkException("Expected frame count must be non-negative");
		}

		try {
			BaseClass.logActionStart("Waiting for frame count to reach " + actualCountToUse, "Frame Utility");

			boolean matched = getWait(timeoutSeconds).until(driver -> {
				int count = driver.findElements(By.tagName("iframe")).size() + driver.findElements(By.tagName("frame")).size();
				return count == actualCountToUse;
			});

			logger.info("Frame count {} achieved", actualCountToUse);
			BaseClass.logActionSuccess("Frame count reached " + actualCountToUse, "Frame Utility");
			return matched;

		} catch (TimeoutException e) {
			int currentCount = driver.findElements(By.tagName("iframe")).size() + driver.findElements(By.tagName("frame")).size();
			logger.warn("Frame count did not reach {} within timeout. Current count: {}", actualCountToUse, currentCount);
			BaseClass.logActionFailure("Wait for frame count", "Frame Utility", 
					"Frame count did not reach " + actualCountToUse + " within " + timeoutSeconds + " seconds. Current count: " + currentCount);
			return false;
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for frame count", "Frame Utility", "Error waiting for frame count: " + e.getMessage());
			return false;
		}
	}


	// --- Frame Attribute/Property Actions ---

	/** Gets the frame element's 'name' or 'id' attribute (if present). */
	public String getFrameNameOrId(WebElement frameElement) {
		if (frameElement == null) {
			BaseClass.logActionFailure("Get frame name or ID", "Frame Attribute", "Frame element cannot be null");
			throw new FrameworkException("Frame element cannot be null");
		}

		try {
			BaseClass.logActionStart("Getting frame name or ID attribute", "Frame Attribute");

			String name = frameElement.getAttribute("name");
			if (name != null && !name.trim().isEmpty()) {
				logger.info("Frame name attribute: '{}'", name);
				BaseClass.logActionSuccess("Retrieved frame name: '" + name + "'", "Frame Attribute");
				return name;
			}

			String id = frameElement.getAttribute("id");
			logger.info("Frame id attribute: '{}'", id);
			BaseClass.logActionSuccess("Retrieved frame ID: '" + id + "'", "Frame Attribute");
			return id;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get frame name or ID", "Frame Attribute", "Failed to get frame name/ID: " + e.getMessage());
			throw new FrameworkException("Failed to get frame name or ID", e);
		}
	}

	/** Gets the frame element's 'src' attribute. */
	public String getFrameSrc(WebElement frameElement) {
		if (frameElement == null) {
			BaseClass.logActionFailure("Get frame src", "Frame Attribute", "Frame element cannot be null");
			throw new FrameworkException("Frame element cannot be null");
		}

		try {
			BaseClass.logActionStart("Getting frame src attribute", "Frame Attribute");

			String src = frameElement.getAttribute("src");
			logger.info("Frame src attribute: '{}'", src);

			BaseClass.logActionSuccess("Retrieved frame src: '" + BaseClass.mask(src) + "'", "Frame Attribute");
			return src;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get frame src", "Frame Attribute", "Failed to get frame src: " + e.getMessage());
			throw new FrameworkException("Failed to get frame src", e);
		}
	}

	// --- Robustness / Advanced ---

	/** Waits until all frames and nested frames in current context are fully loaded. */
	public void waitForAllFramesToBeLoaded(int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Waiting for all frames to be fully loaded", "Frame Loading");

			getWait(timeoutSeconds).until(d -> {
				List<WebElement> frames = d.findElements(By.tagName("iframe"));
				frames.addAll(d.findElements(By.tagName("frame")));

				for (WebElement frame : frames) {
					try {
						d.switchTo().frame(frame);
						String readyState = (String) ((JavascriptExecutor) d)
								.executeScript("return document.readyState");
						d.switchTo().defaultContent();
						if (!"complete".equals(readyState)) {
							return false;
						}
					} catch (NoSuchFrameException | StaleElementReferenceException ex) {
						// Frame might become stale, retry next wait poll
						return false;
					}
				}
				return true;
			});

			logger.info("All frames fully loaded");
			BaseClass.logActionSuccess("All frames fully loaded", "Frame Loading");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Wait for all frames to load", "Frame Loading", "Timeout waiting for all frames to be fully loaded: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for all frames to be fully loaded", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for all frames to load", "Frame Loading", "Failed to wait for frames to load: " + e.getMessage());
			throw new FrameworkException("Failed to wait for frames to load", e);
		}
	}

	/** Refreshes or reloads the current frame's content. */
	public void refreshCurrentFrame() {
		try {
			BaseClass.logActionStart("Refreshing current frame", "Frame Action");

			driver.navigate().refresh();
			logger.info("Refreshed current frame");

			BaseClass.logActionSuccess("Refreshed current frame", "Frame Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Refresh current frame", "Frame Action", "Failed to refresh current frame: " + e.getMessage());
			throw new FrameworkException("Failed to refresh current frame", e);
		}
	}

	/** Switch to the first frame that matches the given XPath within timeout. */
	public void switchToFirstFrameThatMatches(String xpath, int timeoutSeconds) {
		validateInput(xpath, "Frame XPath");

		try {
			BaseClass.logActionStart("Switching to first frame that matches XPath '" + xpath + "'", "Frame Context");

			boolean switched = getWait(timeoutSeconds).until(d -> {
				List<WebElement> frames = d.findElements(By.xpath(xpath));
				if (frames.isEmpty()) return false;
				d.switchTo().frame(frames.get(0));
				return true;
			});

			if (!switched) {
				BaseClass.logActionFailure("Switch to first matching frame", "Frame Context", "No matching frame found for XPath: " + xpath);
				throw new FrameworkException("No matching frame found for XPath: " + xpath);
			}

			logger.info("Switched to first matching frame by XPath '{}'", xpath);
			BaseClass.logActionSuccess("Switched to first matching frame by XPath '" + xpath + "'", "Frame Context");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Switch to first matching frame", "Frame Context", "Timeout waiting to switch to first matching frame by XPath: " + xpath + ": " + e.getMessage());
			throw new FrameworkException("Timeout waiting to switch to first matching frame by XPath: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to first matching frame", "Frame Context", "Failed to switch to first matching frame: " + e.getMessage());
			throw new FrameworkException("Failed to switch to first matching frame", e);
		}
	}

	/**
	 * Enum to choose frame locator type for nested frame navigation.
	 */
	public enum FrameLocatorType {
		NAME_OR_ID,
		XPATH
	}
}
