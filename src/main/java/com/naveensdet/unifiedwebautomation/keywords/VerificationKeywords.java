package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExcelUtilities;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class VerificationKeywords {

	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(VerificationKeywords.class);

	public VerificationKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/** Retrieves integer config property from BaseClass.props or returns default. */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) return defaultValue;
		try {
			return Integer.parseInt(BaseClass.getProps()
					.getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid config for '{}', using default {}", key, defaultValue);
			return defaultValue;
		}
	}

	/** Validates that a parameter is neither null nor empty. */
	private void validateInput(String param, String paramName) {
		if (param == null || param.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	/** Returns a FluentWait with configured timeout and polling interval. */
	private FluentWait<WebDriver> getWait(int timeoutSeconds) {
		int polling = getIntConfigProperty("fluentWaitPolling", 500);
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(polling))
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

	// ==== Element Visibility Verification ====

	/** Verifies that element is visible within timeout. */
	public void verifyElementVisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is visible", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			logger.info("Verified [{}] is visible", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is visible", "Element Verification");

		} catch (TimeoutException e) {
			logger.error("[{}] not visible after {}s", elementName, timeoutSeconds);
			BaseClass.logActionFailure("Verify element visible", "Element Verification", "Element '" + elementName + "' not visible after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Element [" + elementName + "] not visible after " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element visible", "Element Verification", "Failed to verify element visibility: " + e.getMessage());
			throw new FrameworkException("Failed to verify element visibility", e);
		}
	}

	/** Soft verify that element is visible; logs warning on failure. */
	public boolean verifyElementVisibleNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is visible", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			logger.info("Soft verify [{}] is visible", elementName);

			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' is visible", "Element Verification");
			return true;

		} catch (Exception e) {
			logger.warn("Soft verify failed: [{}] not visible after {}s", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' not visible (expected behavior)", "Element Verification");
			return false;
		}
	}

	/** Verifies that element is invisible within timeout. */
	public void verifyElementInvisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is invisible", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
			logger.info("Verified [{}] is not visible", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is invisible", "Element Verification");

		} catch (TimeoutException e) {
			logger.error("[{}] still visible after {}s", elementName, timeoutSeconds);
			BaseClass.logActionFailure("Verify element invisible", "Element Verification", "Element '" + elementName + "' still visible after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Element [" + elementName + "] still visible after " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element invisible", "Element Verification", "Failed to verify element invisibility: " + e.getMessage());
			throw new FrameworkException("Failed to verify element invisibility", e);
		}
	}

	/** Soft verify that element is invisible; logs warning on failure. */
	public boolean verifyElementInvisibleNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is invisible", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
			logger.info("Soft verify [{}] is not visible", elementName);

			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' is invisible", "Element Verification");
			return true;

		} catch (Exception e) {
			logger.warn("Soft verify failed: [{}] still visible after {}s", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' still visible (expected behavior)", "Element Verification");
			return false;
		}
	}

	// ==== Element Presence Verification ====

	/** Verifies that element is present in DOM within timeout. */
	public void verifyElementPresent(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is present in DOM", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			logger.info("Verified [{}] present in DOM", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is present in DOM", "Element Verification");

		} catch (TimeoutException e) {
			logger.error("[{}] not present after {}s", elementName, timeoutSeconds);
			BaseClass.logActionFailure("Verify element present", "Element Verification", "Element '" + elementName + "' not present after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Element [" + elementName + "] not present after " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element present", "Element Verification", "Failed to verify element presence: " + e.getMessage());
			throw new FrameworkException("Failed to verify element presence", e);
		}
	}

	/**
	 * Soft validation: checks if an element is present in the DOM within the specified timeout.
	 * Logs a warning on failure but does not throw an exception.
	 */
	public boolean verifyElementPresentNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is present in DOM", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			logger.info("Soft verify succeeded: [{}] is present in DOM", elementName);

			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' is present in DOM", "Element Verification");
			return true;

		} catch (Exception e) {
			logger.warn("Soft verify failed: [{}] not present in DOM after {} seconds", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' not present in DOM (expected behavior)", "Element Verification");
			return false;
		}
	}

	/**
	 * Verifies that an element is NOT present in the DOM within the specified timeout.
	 */
	public void verifyElementNotPresent(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is not present in DOM", "Element Verification");

			getWait(timeoutSeconds)
			.until(driver -> driver.findElements(By.xpath(xpath)).isEmpty());
			logger.info("Verified [{}] is not present in DOM", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is not present in DOM", "Element Verification");

		} catch (TimeoutException e) {
			logger.error("Element [{}] still present in DOM after {} seconds", elementName, timeoutSeconds);
			BaseClass.logActionFailure("Verify element not present", "Element Verification", "Element '" + elementName + "' still present in DOM after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Element [" + elementName + "] still present in DOM after " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element not present", "Element Verification", "Failed to verify element not present: " + e.getMessage());
			throw new FrameworkException("Failed to verify element not present", e);
		}
	}

	/**
	 * Soft validation: checks if an element is not present in the DOM within the specified timeout.
	 */
	public boolean verifyElementNotPresentNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is not present in DOM", "Element Verification");

			getWait(timeoutSeconds)
			.until(driver -> driver.findElements(By.xpath(xpath)).isEmpty());
			logger.info("Soft verify succeeded: [{}] is not present in DOM", elementName);

			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' is not present in DOM", "Element Verification");
			return true;

		} catch (Exception e) {
			logger.warn("Soft verify failed: [{}] still present in DOM after {} seconds", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' still present in DOM (expected behavior)", "Element Verification");
			return false;
		}
	}

	/**
	 * Verifies that the input element's "value" attribute is empty within the specified timeout.
	 */
	public void verifyInputValueIsEmpty(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying input element '" + elementName + "' value is empty", "Input Verification");

			boolean isEmpty = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String val = el.getAttribute("value");
				return val == null || val.trim().isEmpty();
			});

			if (!isEmpty) {
				BaseClass.logActionFailure("Verify input value is empty", "Input Verification", "Input element '" + elementName + "' value is not empty");
				throw new FrameworkException(
						"Input element [" + elementName + "] value is not empty."
						);
			}
			logger.info("Verified input element [{}] value is empty", elementName);

			BaseClass.logActionSuccess("Input element '" + elementName + "' value is empty", "Input Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify input value is empty", "Input Verification", "Timeout waiting for input value to be empty on '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for input value to be empty on [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify input value is empty", "Input Verification", "Failed to verify input value is empty: " + e.getMessage());
			throw new FrameworkException("Failed to verify input value is empty", e);
		}
	}

	/**
	 * Performs a soft verification that the input element's value is empty.
	 */
	public boolean verifyInputValueIsEmptyNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying input element '" + elementName + "' value is empty", "Input Verification");

			boolean isEmpty = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String val = el.getAttribute("value");
				return val == null || val.trim().isEmpty();
			});

			logger.info("Soft verify input element [{}] value is empty", elementName);
			BaseClass.logActionSuccess("Soft verify: Input element '" + elementName + "' empty value verification result: " + isEmpty, "Input Verification");
			return isEmpty;

		} catch (Exception e) {
			logger.info("Soft verify failed: input element [{}] value not empty within timeout", elementName);
			BaseClass.logActionSuccess("Soft verify: Input element '" + elementName + "' empty value verification failed (expected behavior)", "Input Verification");
			return false;
		}
	}

	// ---------------- Enabled and Disabled Verification ----------------

	/**
	 * Verifies that the element located by the XPath is enabled within the specified timeout.
	 */
	public void verifyElementEnabled(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is enabled", "Element State Verification");

			boolean enabled = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return el.isEnabled();
			});

			if (!enabled) {
				BaseClass.logActionFailure("Verify element enabled", "Element State Verification", "Element '" + elementName + "' is not enabled");
				throw new FrameworkException(
						"Element [" + elementName + "] is not enabled."
						);
			}
			logger.info("Verified element [{}] is enabled", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is enabled", "Element State Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element enabled", "Element State Verification", "Timeout waiting for element '" + elementName + "' to be enabled");
			throw new FrameworkException("Timeout waiting for element to be enabled [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element enabled", "Element State Verification", "Failed to verify element enabled: " + e.getMessage());
			throw new FrameworkException("Failed to verify element enabled", e);
		}
	}

	/**
	 * Soft validation: checks if the element located by the XPath is enabled within the specified timeout.
	 */
	public boolean verifyElementEnabledNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is enabled", "Element State Verification");

			boolean enabled = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return el.isEnabled();
			});

			logger.info("Soft verify element [{}] is enabled", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' enabled verification result: " + enabled, "Element State Verification");
			return enabled;

		} catch (Exception e) {
			logger.info("Soft verify failed: element [{}] not enabled within timeout", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' enabled verification failed (expected behavior)", "Element State Verification");
			return false;
		}
	}

	/**
	 * Verifies that the element located by the XPath is disabled within the specified timeout.
	 */
	public void verifyElementDisabled(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is disabled", "Element State Verification");

			boolean disabled = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return !el.isEnabled();
			});

			if (!disabled) {
				BaseClass.logActionFailure("Verify element disabled", "Element State Verification", "Element '" + elementName + "' is not disabled");
				throw new FrameworkException(
						"Element [" + elementName + "] is not disabled."
						);
			}
			logger.info("Verified element [{}] is disabled", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is disabled", "Element State Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element disabled", "Element State Verification", "Timeout waiting for element '" + elementName + "' to be disabled");
			throw new FrameworkException("Timeout waiting for element to be disabled [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element disabled", "Element State Verification", "Failed to verify element disabled: " + e.getMessage());
			throw new FrameworkException("Failed to verify element disabled", e);
		}
	}

	/**
	 * Soft validation: checks if the element located by the XPath is disabled within the specified timeout.
	 */
	public boolean verifyElementDisabledNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is disabled", "Element State Verification");

			boolean disabled = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return !el.isEnabled();
			});

			logger.info("Soft verify element [{}] is disabled", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' disabled verification result: " + disabled, "Element State Verification");
			return disabled;

		} catch (Exception e) {
			logger.info("Soft verify failed: element [{}] not disabled within timeout", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' disabled verification failed (expected behavior)", "Element State Verification");
			return false;
		}
	}

	// ---------------- Selected Status Verification ----------------

	/**
	 * Verifies that the element located by the given XPath is selected within the specified timeout.
	 */
	public void verifyElementSelected(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is selected", "Element State Verification");

			boolean selected = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return el.isSelected();
			});

			if (!selected) {
				BaseClass.logActionFailure("Verify element selected", "Element State Verification", "Element '" + elementName + "' is not selected");
				throw new FrameworkException(
						"Element [" + elementName + "] is not selected."
						);
			}
			logger.info("Verified element [{}] is selected", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is selected", "Element State Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element selected", "Element State Verification", "Timeout waiting for element '" + elementName + "' to be selected");
			throw new FrameworkException("Timeout waiting for element to be selected [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element selected", "Element State Verification", "Failed to verify element selected: " + e.getMessage());
			throw new FrameworkException("Failed to verify element selected", e);
		}
	}

	/**
	 * Soft validation: checks if the element located by the XPath is selected within the specified timeout.
	 */
	public boolean verifyElementSelectedNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is selected", "Element State Verification");

			boolean selected = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return el.isSelected();
			});

			logger.info("Soft verify element [{}] is selected", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' selected verification result: " + selected, "Element State Verification");
			return selected;

		} catch (Exception e) {
			logger.info("Soft verify failed: element [{}] not selected within timeout", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' selected verification failed (expected behavior)", "Element State Verification");
			return false;
		}
	}

	/**
	 * Verifies that the element located by the XPath is not selected within the specified timeout.
	 */
	public void verifyElementNotSelected(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is not selected", "Element State Verification");

			boolean notSelected = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return !el.isSelected();
			});

			if (!notSelected) {
				BaseClass.logActionFailure("Verify element not selected", "Element State Verification", "Element '" + elementName + "' is selected");
				throw new FrameworkException(
						"Element [" + elementName + "] is selected."
						);
			}
			logger.info("Verified element [{}] is not selected", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is not selected", "Element State Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element not selected", "Element State Verification", "Timeout waiting for element '" + elementName + "' to be not selected");
			throw new FrameworkException("Timeout waiting for element to be not selected [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element not selected", "Element State Verification", "Failed to verify element not selected: " + e.getMessage());
			throw new FrameworkException("Failed to verify element not selected", e);
		}
	}

	/**
	 * Soft validation: checks if the element located by the XPath is not selected within the specified timeout.
	 */
	public boolean verifyElementNotSelectedNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is not selected", "Element State Verification");

			boolean notSelected = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				return !el.isSelected();
			});

			logger.info("Soft verify element [{}] is not selected", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' not selected verification result: " + notSelected, "Element State Verification");
			return notSelected;

		} catch (Exception e) {
			logger.info("Soft verify failed: element [{}] selected within timeout", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' not selected verification failed (expected behavior)", "Element State Verification");
			return false;
		}
	}

	/** Soft verification - element visible, returns boolean instead of throwing exception. */
	public boolean softVerifyElementVisible(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is visible", "Element Verification");

			getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));

			logger.info("Soft verification passed: [{}] is visible", elementName);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' is visible", "Element Verification");
			return true;

		} catch (Exception e) {
			logger.warn("Soft verification failed: [{}] not visible after {}s", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' not visible (expected behavior)", "Element Verification");
			return false;
		}
	}

	/** Verifies that element is not visible within timeout. */
	public void verifyElementNotVisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' is not visible", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
			logger.info("Verified [{}] is not visible", elementName);

			BaseClass.logActionSuccess("Element '" + elementName + "' is not visible", "Element Verification");

		} catch (TimeoutException e) {
			logger.error("[{}] still visible after {}s", elementName, timeoutSeconds);
			BaseClass.logActionFailure("Verify element not visible", "Element Verification", "Element '" + elementName + "' still visible after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Element [" + elementName + "] still visible after " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element not visible", "Element Verification", "Failed to verify element not visible: " + e.getMessage());
			throw new FrameworkException("Failed to verify element not visible", e);
		}
	}

	/** Soft verify that element is not visible; logs warning on failure. */
	public boolean verifyElementNotVisibleNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' is not visible", "Element Verification");

			getWait(timeoutSeconds)
			.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
			logger.info("Soft verify [{}] is not visible", elementName);

			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' is not visible", "Element Verification");
			return true;

		} catch (Exception e) {
			logger.warn("Soft verify failed: [{}] still visible after {}s", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' still visible (expected behavior)", "Element Verification");
			return false;
		}
	}

	/** Verifies that element text is empty within timeout. */
	public void verifyTextIsEmpty(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' text is empty", "Text Verification");

			boolean isEmpty = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text == null || text.trim().isEmpty();
			});

			if (!isEmpty) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualText = element.getText();
				BaseClass.logActionFailure("Verify text is empty", "Text Verification", "Text of '" + elementName + "' should be empty but was '" + actualText + "'");
				throw new FrameworkException("Text of [" + elementName + "] should be empty but was '" + actualText + "'");
			}

			logger.info("Verified element [{}] text is empty", elementName);
			BaseClass.logActionSuccess("Element '" + elementName + "' text is empty", "Text Verification");

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualText = element.getText();
			logger.error("Timeout waiting for text to be empty on [{}]. Current text: '{}'", elementName, actualText);
			BaseClass.logActionFailure("Verify text is empty", "Text Verification", "Timeout waiting for text to be empty on '" + elementName + "'. Current text: '" + actualText + "'");
			throw new FrameworkException("Timeout waiting for text to be empty on element [" + elementName + "]. Current text: '" + actualText + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify text is empty", "Text Verification", "Failed to verify text is empty: " + e.getMessage());
			throw new FrameworkException("Failed to verify text is empty for [" + elementName + "]", e);
		}
	}

	/** Soft verification that element text is empty. */
	public boolean verifyTextIsEmptyNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' text is empty", "Text Verification");

			boolean isEmpty = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text == null || text.trim().isEmpty();
			});

			logger.info("Soft verify element [{}] text is empty - Result: {}", elementName, isEmpty);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' text is empty verification result: " + isEmpty, "Text Verification");
			return isEmpty;

		} catch (Exception e) {
			logger.warn("Soft verify failed: element [{}] text is not empty within {}s", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' text is empty failed (expected behavior)", "Text Verification");
			return false;
		}
	}

	/** Verifies that element text is not empty within timeout. */
	public void verifyTextIsNotEmpty(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' text is not empty", "Text Verification");

			boolean isNotEmpty = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text != null && !text.trim().isEmpty();
			});

			if (!isNotEmpty) {
				BaseClass.logActionFailure("Verify text is not empty", "Text Verification", "Text of '" + elementName + "' should not be empty but was empty or null");
				throw new FrameworkException("Text of [" + elementName + "] should not be empty but was empty or null");
			}

			WebElement element = driver.findElement(By.xpath(xpath));
			String actualText = element.getText();
			logger.info("Verified element [{}] text is not empty: '{}'", elementName, actualText);
			BaseClass.logActionSuccess("Element '" + elementName + "' text is not empty", "Text Verification");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for text to be not empty on [{}]", elementName);
			BaseClass.logActionFailure("Verify text is not empty", "Text Verification", "Timeout waiting for text to be not empty on '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for text to be not empty on element [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify text is not empty", "Text Verification", "Failed to verify text is not empty: " + e.getMessage());
			throw new FrameworkException("Failed to verify text is not empty for [" + elementName + "]", e);
		}
	}

	/** Soft verification that element text is not empty. */
	public boolean verifyTextIsNotEmptyNoReport(String xpath, String elementName, int timeoutSeconds) {
		try {
			validateInput(xpath, "XPath");
			validateInput(elementName, "Element Name");

			BaseClass.logActionStart("Soft verifying element '" + elementName + "' text is not empty", "Text Verification");

			boolean isNotEmpty = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text != null && !text.trim().isEmpty();
			});

			logger.info("Soft verify element [{}] text is not empty - Result: {}", elementName, isNotEmpty);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' text is not empty verification result: " + isNotEmpty, "Text Verification");
			return isNotEmpty;

		} catch (Exception e) {
			logger.warn("Soft verify failed: element [{}] text is empty within {}s", elementName, timeoutSeconds);
			BaseClass.logActionSuccess("Soft verify: Element '" + elementName + "' text is not empty failed (expected behavior)", "Text Verification");
			return false;
		}
	}

	/**
	 * Verifies that the text of the element identified by the XPath exactly equals the expected text within the specified timeout.
	 */
	public void verifyElementTextEquals(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element name");
		validateInput(expectedText, "Expected text");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedText, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' text equals '" + BaseClass.mask(valueToUse) + "'", "Text Verification");

			boolean textMatches = getWait(timeoutSeconds).until(driver -> {
				WebElement element = driver.findElement(By.xpath(xpath));
				return valueToUse.equals(element.getText());
			});

			if (!textMatches) {
				BaseClass.logActionFailure("Verify element text equals", "Text Verification", "Text of '" + elementName + "' did not match expected '" + valueToUse + "'");
				throw new FrameworkException("Text of [" + elementName + "] did not match expected. Expected: '" 
						+ valueToUse + "'");
			}
			logger.info("Verified [{}] text equals '{}'", elementName, valueToUse);

			BaseClass.logActionSuccess("Element '" + elementName + "' text equals expected value", "Text Verification");

		} catch (TimeoutException e) {
			logger.error("Element [{}] text did not become equals '{}' within {} seconds", elementName, valueToUse, timeoutSeconds, e);
			BaseClass.logActionFailure("Verify element text equals", "Text Verification", "Element '" + elementName + "' text did not become equals '" + valueToUse + "' within " + timeoutSeconds + " seconds");
			throw new FrameworkException("Element [" + elementName + "] text did not become equals '" + valueToUse 
					+ "' within " + timeoutSeconds + " seconds", e);
		} catch (NoSuchElementException e) {
			logger.error("Element [{}] not found for text verification", elementName, e);
			BaseClass.logActionFailure("Verify element text equals", "Text Verification", "Element '" + elementName + "' not found for text verification");
			throw new FrameworkException("Element [" + elementName + "] not found for text verification", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element text equals", "Text Verification", "Failed to verify element text: " + e.getMessage());
			throw new FrameworkException("Failed to verify element text", e);
		}
	}

	/**
	 * Verifies that the element's text contains the specified substring within the given timeout.
	 */
	public void verifyElementTextContains(String xpath, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedFragment, "Expected Text Fragment");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedFragment, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' text contains '" + BaseClass.mask(valueToUse) + "'", "Text Verification");

			boolean contains = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text != null && text.contains(valueToUse);
			});

			if (!contains) {
				BaseClass.logActionFailure("Verify element text contains", "Text Verification", "Text of '" + elementName + "' does not contain '" + valueToUse + "'");
				throw new FrameworkException("Text of [" + elementName + "] does not contain '" + valueToUse + "'");
			}
			logger.info("Verified element [{}] text contains '{}'", elementName, valueToUse);

			BaseClass.logActionSuccess("Element '" + elementName + "' text contains expected fragment", "Text Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element text contains", "Text Verification", "Timeout waiting for text containing '" + valueToUse + "' on element '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for text containing '" + valueToUse + "' on element [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element text contains", "Text Verification", "Failed to verify text contains: " + e.getMessage());
			throw new FrameworkException("Failed to verify text contains", e);
		}
	}

	/**
	 * Verifies that element text contains expected substring within timeout.
	 */
	public void verifyTextContains(String xpath, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedFragment, "Expected Text Fragment");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedFragment, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' text contains '" + BaseClass.mask(valueToUse) + "'", "Text Verification");

			boolean contains = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text != null && text.contains(valueToUse);
			});

			if (!contains) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualText = element.getText();
				BaseClass.logActionFailure("Verify text contains", "Text Verification", "Text of '" + elementName + "' expected to contain '" + valueToUse + "' but was '" + actualText + "'");
				throw new FrameworkException("Text of [" + elementName + "] expected to contain '" + valueToUse + "' but was '" + actualText + "'");
			}

			logger.info("Verified element [{}] text contains '{}'", elementName, valueToUse);
			BaseClass.logActionSuccess("Element '" + elementName + "' text contains expected fragment", "Text Verification");

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualText = element.getText();
			logger.error("Timeout waiting for text containing '{}' on [{}]. Current text: '{}'", valueToUse, elementName, actualText);
			BaseClass.logActionFailure("Verify text contains", "Text Verification", "Timeout waiting for text containing '" + valueToUse + "' on '" + elementName + "'. Current text: '" + actualText + "'");
			throw new FrameworkException("Timeout waiting for text containing '" + valueToUse + "' on element [" + elementName + "]. Current text: '" + actualText + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify text contains", "Text Verification", "Failed to verify text contains: " + e.getMessage());
			throw new FrameworkException("Failed to verify text contains for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that element text exactly equals expected value within timeout.
	 */
	public void verifyTextEquals(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedText, "Expected Text");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedText, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' text equals '" + BaseClass.mask(valueToUse) + "'", "Text Verification");

			boolean textMatches = getWait(timeoutSeconds).until(driver -> {
				WebElement element = driver.findElement(By.xpath(xpath));
				return valueToUse.equals(element.getText());
			});

			if (!textMatches) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualText = element.getText();
				BaseClass.logActionFailure("Verify text equals", "Text Verification", "Text of '" + elementName + "' expected '" + valueToUse + "' but was '" + actualText + "'");
				throw new FrameworkException("Text of [" + elementName + "] expected '" + valueToUse + "' but was '" + actualText + "'");
			}

			logger.info("Verified [{}] text equals '{}'", elementName, valueToUse);
			BaseClass.logActionSuccess("Element '" + elementName + "' text equals expected value", "Text Verification");

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualText = element.getText();
			logger.error("Element [{}] text did not become equals '{}' within {} seconds. Current text: '{}'", elementName, valueToUse, timeoutSeconds, actualText);
			BaseClass.logActionFailure("Verify text equals", "Text Verification", "Element '" + elementName + "' text did not become equals '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current text: '" + actualText + "'");
			throw new FrameworkException("Element [" + elementName + "] text did not become equals '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current text: '" + actualText + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify text equals", "Text Verification", "Failed to verify text equals: " + e.getMessage());
			throw new FrameworkException("Failed to verify text equals for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that element text does not contain specified substring within timeout.
	 */
	public void verifyTextDoesNotContain(String xpath, String unwantedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(unwantedFragment, "Unwanted Text Fragment");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, unwantedFragment, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' text does not contain '" + BaseClass.mask(valueToUse) + "'", "Text Verification");

			boolean doesNotContain = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text == null || !text.contains(valueToUse);
			});

			if (!doesNotContain) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualText = element.getText();
				BaseClass.logActionFailure("Verify text does not contain", "Text Verification", "Text of '" + elementName + "' should not contain '" + valueToUse + "' but was '" + actualText + "'");
				throw new FrameworkException("Text of [" + elementName + "] should not contain '" + valueToUse + "' but was '" + actualText + "'");
			}

			logger.info("Verified element [{}] text does not contain '{}'", elementName, valueToUse);
			BaseClass.logActionSuccess("Element '" + elementName + "' text does not contain unwanted fragment", "Text Verification");

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualText = element.getText();
			logger.error("Timeout waiting for text not to contain '{}' on [{}]. Current text: '{}'", valueToUse, elementName, actualText);
			BaseClass.logActionFailure("Verify text does not contain", "Text Verification", "Timeout waiting for text not to contain '" + valueToUse + "' on '" + elementName + "'. Current text: '" + actualText + "'");
			throw new FrameworkException("Timeout waiting for text not to contain '" + valueToUse + "' on element [" + elementName + "]. Current text: '" + actualText + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify text does not contain", "Text Verification", "Failed to verify text does not contain: " + e.getMessage());
			throw new FrameworkException("Failed to verify text does not contain for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the specified attribute of an element matches the expected value within the given timeout.
	 */
	public void verifyElementAttributeEquals(String xpath, String attributeName, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(attributeName, "Attribute Name");
		validateInput(expectedValue, "Expected Attribute Value");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' attribute '" + attributeName + "' equals '" + BaseClass.mask(valueToUse) + "'", "Attribute Verification");

			boolean matched = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String attr = el.getAttribute(attributeName);
				return valueToUse.equals(attr);
			});

			if (!matched) {
				BaseClass.logActionFailure("Verify element attribute equals", "Attribute Verification", "Attribute '" + attributeName + "' of '" + elementName + "' expected '" + valueToUse + "' but did not match");
				throw new FrameworkException(
						"Attribute '" + attributeName + "' of [" + elementName + "] expected '" + valueToUse + "' but did not match."
						);
			}
			logger.info("Verified element [{}] attribute '{}' equals '{}'", elementName, attributeName, valueToUse);

			BaseClass.logActionSuccess("Element '" + elementName + "' attribute '" + attributeName + "' equals expected value", "Attribute Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element attribute equals", "Attribute Verification", "Timeout waiting for attribute '" + attributeName + "' to equal '" + valueToUse + "' on '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for attribute '" + attributeName + "' to equal '" + valueToUse + "' on [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element attribute equals", "Attribute Verification", "Failed to verify attribute equals: " + e.getMessage());
			throw new FrameworkException("Failed to verify attribute equals", e);
		}
	}

	/**
	 * Verifies that the specified attribute of an element contains the expected substring within the timeout.
	 */
	public void verifyElementAttributeContains(String xpath, String attributeName, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(attributeName, "Attribute Name");
		validateInput(expectedFragment, "Expected Attribute Fragment");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedFragment, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' attribute '" + attributeName + "' contains '" + BaseClass.mask(valueToUse) + "'", "Attribute Verification");

			boolean contains = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String attr = el.getAttribute(attributeName);
				return attr != null && attr.contains(valueToUse);
			});

			if (!contains) {
				BaseClass.logActionFailure("Verify element attribute contains", "Attribute Verification", "Attribute '" + attributeName + "' of '" + elementName + "' does not contain '" + valueToUse + "'");
				throw new FrameworkException(
						"Attribute '" + attributeName + "' of [" + elementName + "] does not contain '" + valueToUse + "'."
						);
			}
			logger.info("Verified element [{}] attribute '{}' contains '{}'", elementName, attributeName, valueToUse);

			BaseClass.logActionSuccess("Element '" + elementName + "' attribute '" + attributeName + "' contains expected fragment", "Attribute Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element attribute contains", "Attribute Verification", "Timeout waiting for attribute '" + attributeName + "' to contain '" + valueToUse + "' on '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for attribute '" + attributeName + "' to contain '" + valueToUse + "' on [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element attribute contains", "Attribute Verification", "Failed to verify attribute contains: " + e.getMessage());
			throw new FrameworkException("Failed to verify attribute contains", e);
		}
	}

	/**
	 * Verifies that the input element's 'value' attribute equals the expected value within the given timeout.
	 */
	public void verifyInputValueEquals(String xpath, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedValue, "Expected Value");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying input element '" + elementName + "' value equals '" + BaseClass.mask(valueToUse) + "'", "Input Verification");

			boolean matched = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String val = el.getAttribute("value");
				return valueToUse.equals(val);
			});

			if (!matched) {
				BaseClass.logActionFailure("Verify input value equals", "Input Verification", "Input element '" + elementName + "' value expected '" + valueToUse + "' but did not match");
				throw new FrameworkException(
						"Input element [" + elementName + "] value expected '" + valueToUse + "' but did not match."
						);
			}
			logger.info("Verified input element [{}] value equals '{}'", elementName, valueToUse);

			BaseClass.logActionSuccess("Input element '" + elementName + "' value equals expected value", "Input Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify input value equals", "Input Verification", "Timeout waiting for input value to equal '" + valueToUse + "' on '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for input value to equal '" + valueToUse + "' on [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify input value equals", "Input Verification", "Failed to verify input value equals: " + e.getMessage());
			throw new FrameworkException("Failed to verify input value equals", e);
		}
	}

	/**
	 * Verifies that the current page title exactly matches the expected title within the specified timeout.
	 */
	public void verifyPageTitleEquals(String expectedTitle, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedTitle, "Expected Title");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedTitle, "Page Title", excelData);

		try {
			BaseClass.logActionStart("Verifying page title equals '" + BaseClass.mask(valueToUse) + "'", "Page Verification");

			boolean matched = getWait(timeoutSeconds).until(d -> {
				String title = d.getTitle();
				return valueToUse.equals(title);
			});

			if (!matched) {
				BaseClass.logActionFailure("Verify page title equals", "Page Verification", "Page title did not become '" + valueToUse + "'");
				throw new FrameworkException(
						"Page title did not become '" + valueToUse + "'."
						);
			}
			logger.info("Verified page title equals '{}'", valueToUse);

			BaseClass.logActionSuccess("Page title equals expected value", "Page Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify page title equals", "Page Verification", "Timeout waiting for page title to equal '" + valueToUse + "'");
			throw new FrameworkException("Timeout waiting for page title to equal '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify page title equals", "Page Verification", "Failed to verify page title equals: " + e.getMessage());
			throw new FrameworkException("Failed to verify page title equals", e);
		}
	}

	/**
	 * Verifies that the current page title contains the specified substring within the given timeout.
	 */
	public void verifyPageTitleContains(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(fragment, "Title Fragment");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, fragment, "Page Title Fragment", excelData);

		try {
			BaseClass.logActionStart("Verifying page title contains '" + BaseClass.mask(valueToUse) + "'", "Page Verification");

			boolean contains = getWait(timeoutSeconds).until(d -> {
				String title = d.getTitle();
				return title != null && title.contains(valueToUse);
			});

			if (!contains) {
				BaseClass.logActionFailure("Verify page title contains", "Page Verification", "Page title did not contain '" + valueToUse + "'");
				throw new FrameworkException(
						"Page title did not contain '" + valueToUse + "'."
						);
			}
			logger.info("Verified page title contains '{}'", valueToUse);

			BaseClass.logActionSuccess("Page title contains expected fragment", "Page Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify page title contains", "Page Verification", "Timeout waiting for page title to contain '" + valueToUse + "'");
			throw new FrameworkException("Timeout waiting for page title to contain '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify page title contains", "Page Verification", "Failed to verify page title contains: " + e.getMessage());
			throw new FrameworkException("Failed to verify page title contains", e);
		}
	}

	/**
	 * Verifies that the current browser URL exactly equals the expected URL within the specified timeout.
	 */
	public void verifyUrlEquals(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedUrl, "Expected URL");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrl, "Expected URL", excelData);

		try {
			BaseClass.logActionStart("Verifying URL equals '" + valueToUse + "'", "URL Verification");

			boolean matched = getWait(timeoutSeconds).until(d -> {
				String url = d.getCurrentUrl();
				return valueToUse.equals(url);
			});

			if (!matched) {
				BaseClass.logActionFailure("Verify URL equals", "URL Verification", "URL did not become '" + valueToUse + "'");
				throw new FrameworkException(
						"Url did not become '" + valueToUse + "'."
						);
			}
			logger.info("Verified page URL equals '{}'", valueToUse);

			BaseClass.logActionSuccess("URL equals expected value", "URL Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify URL equals", "URL Verification", "Timeout waiting for URL to equal '" + valueToUse + "'");
			throw new FrameworkException("Timeout waiting for URL to equal '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify URL equals", "URL Verification", "Failed to verify URL equals: " + e.getMessage());
			throw new FrameworkException("Failed to verify URL equals", e);
		}
	}

	/**
	 * Verifies that the current URL contains the specified substring within the given timeout.
	 */
	public void verifyUrlContains(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(fragment, "URL Fragment");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, fragment, "URL Fragment", excelData);

		try {
			BaseClass.logActionStart("Verifying URL contains '" + BaseClass.mask(valueToUse) + "'", "URL Verification");

			boolean contains = getWait(timeoutSeconds).until(d -> {
				String url = d.getCurrentUrl();
				return url != null && url.contains(valueToUse);
			});

			if (!contains) {
				BaseClass.logActionFailure("Verify URL contains", "URL Verification", "URL did not contain '" + valueToUse + "'");
				throw new FrameworkException(
						"URL did not contain '" + valueToUse + "'."
						);
			}
			logger.info("Verified URL contains '{}'", valueToUse);

			BaseClass.logActionSuccess("URL contains expected fragment", "URL Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify URL contains", "URL Verification", "Timeout waiting for URL to contain '" + valueToUse + "'");
			throw new FrameworkException("Timeout waiting for URL to contain '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify URL contains", "URL Verification", "Failed to verify URL contains: " + e.getMessage());
			throw new FrameworkException("Failed to verify URL contains", e);
		}
	}

	/**
	 * Verifies that a browser alert is present and its text matches the expected value within the specified timeout.
	 */
	public void verifyAlertText(String expectedAlertText, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedAlertText, "Expected Alert Text");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedAlertText, "Alert Text", excelData);

		int polling = getIntConfigProperty("fluentWaitPolling", 500);
		try {
			BaseClass.logActionStart("Verifying alert text equals '" + BaseClass.mask(valueToUse) + "'", "Alert Verification");

			String alertText = new FluentWait<>(driver)
					.withTimeout(Duration.ofSeconds(timeoutSeconds))
					.pollingEvery(Duration.ofMillis(polling))
					.ignoring(NoAlertPresentException.class)
					.until(d -> {
						Alert alert = d.switchTo().alert();
						return alert.getText();
					});

			if (!valueToUse.equals(alertText)) {
				BaseClass.logActionFailure("Verify alert text", "Alert Verification", "Alert text expected '" + valueToUse + "' but was '" + alertText + "'");
				throw new FrameworkException("Alert text expected '" + valueToUse + "' but was '" + alertText + "'");
			}
			logger.info("Verified alert text equals '{}'", valueToUse);

			BaseClass.logActionSuccess("Alert text equals expected value", "Alert Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify alert text", "Alert Verification", "Timeout waiting for alert text to be '" + valueToUse + "'");
			throw new FrameworkException("Timeout waiting for alert text to be '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify alert text", "Alert Verification", "Failed to verify alert text: " + e.getMessage());
			throw new FrameworkException("Failed to verify alert text", e);
		}
	}

	/**
	 * Verifies that the CSS property of the specified element equals the expected value within the given timeout.
	 */
	public void verifyElementCssValue(String xpath, String cssProperty, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(cssProperty, "CSS Property");
		validateInput(expectedValue, "Expected Value");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' CSS property '" + cssProperty + "' equals '" + valueToUse + "'", "CSS Verification");

			boolean matched = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String cssVal = el.getCssValue(cssProperty);
				return valueToUse.equals(cssVal);
			});

			if (!matched) {
				BaseClass.logActionFailure("Verify element CSS value", "CSS Verification", "CSS property '" + cssProperty + "' of '" + elementName + "' expected '" + valueToUse + "' but did not match");
				throw new FrameworkException(
						"CSS property '" + cssProperty + "' of [" + elementName + "] expected '" + valueToUse + "' but did not match."
						);
			}
			logger.info("Verified element [{}] CSS property '{}' equals '{}'", elementName, cssProperty, valueToUse);

			BaseClass.logActionSuccess("Element '" + elementName + "' CSS property '" + cssProperty + "' equals expected value", "CSS Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element CSS value", "CSS Verification", "Timeout waiting for CSS property '" + cssProperty + "' to equal '" + valueToUse + "' on '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for CSS property '" + cssProperty + "' to equal '" + valueToUse + "' on [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element CSS value", "CSS Verification", "Failed to verify CSS value: " + e.getMessage());
			throw new FrameworkException("Failed to verify CSS value", e);
		}
	}

	/**
	 * Verifies element CSS property value within timeout.
	 */
	public void verifyElementCSSProperty(String xpath, String cssProperty, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(cssProperty, "CSS Property");
		validateInput(expectedValue, "Expected Value");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying element '" + elementName + "' CSS property '" + cssProperty + "' equals '" + valueToUse + "'", "CSS Verification");

			boolean matched = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String cssVal = el.getCssValue(cssProperty);
				return valueToUse.equals(cssVal);
			});

			if (!matched) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualValue = element.getCssValue(cssProperty);
				BaseClass.logActionFailure("Verify element CSS property", "CSS Verification", 
						"CSS property '" + cssProperty + "' of '" + elementName + "' expected '" + valueToUse + "' but found '" + actualValue + "'");
				throw new FrameworkException("CSS property '" + cssProperty + "' of [" + elementName + "] expected '" + valueToUse + "' but found '" + actualValue + "'");
			}

			logger.info("Verified element [{}] CSS property '{}' equals '{}'", elementName, cssProperty, valueToUse);
			BaseClass.logActionSuccess("Element CSS property verification passed", "CSS Verification");

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualValue = element.getCssValue(cssProperty);
			logger.error("Timeout waiting for CSS property '{}' of [{}] to equal '{}'. Current value: '{}'", cssProperty, elementName, valueToUse, actualValue);
			BaseClass.logActionFailure("Verify element CSS property", "CSS Verification", 
					"Timeout waiting for CSS property '" + cssProperty + "' to equal '" + valueToUse + "' on '" + elementName + "'. Current value: '" + actualValue + "'");
			throw new FrameworkException("Timeout waiting for CSS property '" + cssProperty + "' of [" + elementName + "] to equal '" + valueToUse + "'. Current value: '" + actualValue + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element CSS property", "CSS Verification", "Failed to verify CSS property: " + e.getMessage());
			throw new FrameworkException("Failed to verify CSS property '" + cssProperty + "' for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that string contains expected substring.
	 */
	public void verifyStringContains(String actual, String expected, String message, boolean excelData, String testCaseName) {
		validateInput(actual, "Actual String");
		validateInput(expected, "Expected Substring");
		validateInput(message, "Message");

		// Get the actual values to use (from Excel or direct input)
		String actualValueToUse = getInputValue(testCaseName, actual, "Actual String", excelData);
		String expectedValueToUse = getInputValue(testCaseName, expected, "Expected String", excelData);

		try {
			BaseClass.logActionStart("Verifying string contains '" + BaseClass.mask(expectedValueToUse) + "': " + message, "String Verification");

			if (!actualValueToUse.contains(expectedValueToUse)) {
				BaseClass.logActionFailure("Verify string contains", "String Verification", 
						"String '" + actualValueToUse + "' does not contain expected substring '" + expectedValueToUse + "': " + message);
				throw new FrameworkException("String '" + actualValueToUse + "' does not contain expected substring '" + expectedValueToUse + "': " + message);
			}

			logger.info("Verified string contains expected substring: {}", message);
			BaseClass.logActionSuccess("String contains verification passed", "String Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify string contains", "String Verification", "Failed to verify string contains: " + e.getMessage());
			throw new FrameworkException("Failed to verify string contains: " + message, e);
		}
	}

	/**
	 * Verifies that string equals expected value.
	 */
	public void verifyStringEquals(String actual, String expected, String message, boolean excelData, String testCaseName) {
		validateInput(actual, "Actual String");
		validateInput(expected, "Expected String");
		validateInput(message, "Message");

		// Get the actual values to use (from Excel or direct input)
		String actualValueToUse = getInputValue(testCaseName, actual, "Actual String", excelData);
		String expectedValueToUse = getInputValue(testCaseName, expected, "Expected String", excelData);

		try {
			BaseClass.logActionStart("Verifying string equals '" + BaseClass.mask(expectedValueToUse) + "': " + message, "String Verification");

			if (!expectedValueToUse.equals(actualValueToUse)) {
				BaseClass.logActionFailure("Verify string equals", "String Verification", 
						"Expected '" + expectedValueToUse + "' but actual was '" + actualValueToUse + "': " + message);
				throw new FrameworkException("Expected '" + expectedValueToUse + "' but actual was '" + actualValueToUse + "': " + message);
			}

			logger.info("Verified string equals expected value: {}", message);
			BaseClass.logActionSuccess("String equals verification passed", "String Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify string equals", "String Verification", "Failed to verify string equals: " + e.getMessage());
			throw new FrameworkException("Failed to verify string equals: " + message, e);
		}
	}

	/**
	 * Verifies that the boolean condition is true.
	 * @param condition The boolean condition to verify
	 * @param message Descriptive message for the verification
	 * @throws FrameworkException if the condition is false
	 */
	public void verifyTrue(boolean condition, String message) {
		validateInput(message, "Message");

		try {
			BaseClass.logActionStart("Verifying condition is true: " + message, "Boolean Verification");

			if (!condition) {
				BaseClass.logActionFailure("Verify true condition", "Boolean Verification", "Condition was false: " + message);
				throw new FrameworkException("Condition verification failed - expected true but was false: " + message);
			}

			logger.info("Verified condition is true: {}", message);
			BaseClass.logActionSuccess("Boolean condition verification passed", "Boolean Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify true condition", "Boolean Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Boolean verification failed: {}", message, e);
			BaseClass.logActionFailure("Verify true condition", "Boolean Verification", "Boolean verification failed: " + e.getMessage());
			throw new FrameworkException("Boolean verification failed for: " + message, e);
		}
	}


	/**
	 * Verifies that the boolean condition is false.
	 * @param condition The boolean condition to verify
	 * @param message Descriptive message for the verification
	 * @throws FrameworkException if the condition is true
	 */
	public void verifyFalse(boolean condition, String message) {
		validateInput(message, "Message");

		try {
			BaseClass.logActionStart("Verifying condition is false: " + message, "Boolean Verification");

			if (condition) {
				BaseClass.logActionFailure("Verify false condition", "Boolean Verification", "Condition was true: " + message);
				throw new FrameworkException("Condition verification failed - expected false but was true: " + message);
			}

			logger.info("Verified condition is false: {}", message);
			BaseClass.logActionSuccess("Boolean condition verification passed", "Boolean Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify false condition", "Boolean Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Boolean verification failed: {}", message, e);
			BaseClass.logActionFailure("Verify false condition", "Boolean Verification", "Boolean verification failed: " + e.getMessage());
			throw new FrameworkException("Boolean verification failed for: " + message, e);
		}
	}

	/**
	 * Verifies that the element count equals expected value within timeout.
	 */
	public void verifyElementCount(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Declare final variable after conditional processing
		final int actualCountToUse;
		if (excelData) {
			String countValue = getInputValue(testCaseName, String.valueOf(expectedCount), elementName + " Count", excelData);

			if (countValue == null || countValue.trim().isEmpty()) {
				throw new FrameworkException("Count value from Excel is null or empty for test case: " + testCaseName);
			}

			try {
				actualCountToUse = Integer.parseInt(countValue.trim());
			} catch (NumberFormatException e) {
				throw new FrameworkException("Invalid count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
			}
		} else {
			actualCountToUse = expectedCount;
		}

		try {
			BaseClass.logActionStart("Verifying element count for '" + elementName + "' equals " + actualCountToUse + " within " + timeoutSeconds + " seconds", "Element Count Verification");

			boolean countMatches = getWait(timeoutSeconds).until(driver -> {
				List<WebElement> elements = driver.findElements(By.xpath(xpath));
				return elements.size() == actualCountToUse; // Now using final variable
			});

			if (!countMatches) {
				List<WebElement> elements = driver.findElements(By.xpath(xpath));
				int actualCount = elements.size();
				BaseClass.logActionFailure("Verify element count", "Element Count Verification", 
						"Element count for '" + elementName + "' expected " + actualCountToUse + " but found " + actualCount);
				throw new FrameworkException("Element count for [" + elementName + "] expected " + actualCountToUse + " but found " + actualCount);
			}

			logger.info("Verified element count for [{}] equals {}", elementName, actualCountToUse);
			BaseClass.logActionSuccess("Element count verification passed", "Element Count Verification");

		} catch (TimeoutException e) {
			List<WebElement> elements = driver.findElements(By.xpath(xpath));
			int actualCount = elements.size();
			logger.error("Timeout waiting for element count {} for [{}] within {} seconds. Current count: {}", actualCountToUse, elementName, timeoutSeconds, actualCount);
			BaseClass.logActionFailure("Verify element count", "Element Count Verification", 
					"Timeout waiting for element count " + actualCountToUse + " for '" + elementName + "' within " + timeoutSeconds + " seconds. Current count: " + actualCount);
			throw new FrameworkException("Timeout waiting for element count " + actualCountToUse + " for [" + elementName + "] within " + timeoutSeconds + " seconds. Current count: " + actualCount, e);
		} catch (FrameworkException e) {
			BaseClass.logActionFailure("Verify element count", "Element Count Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element count", "Element Count Verification", "Failed to verify element count: " + e.getMessage());
			throw new FrameworkException("Failed to verify element count for [" + elementName + "]", e);
		}
	}

	/**
	 * Soft verification - text contains, returns boolean instead of throwing exception.
	 * @param xpath XPath to locate the element
	 * @param expectedText Expected text fragment (or parameter name if using Excel)
	 * @param elementName Descriptive name for the element
	 * @param timeoutSeconds Maximum time to wait for element
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @return true if text contains expected fragment, false otherwise
	 */
	public boolean softVerifyTextContains(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedText, "Expected Text Fragment");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedText, elementName, excelData);

		try {
			BaseClass.logActionStart("Soft verifying element '" + elementName + "' text contains '" + BaseClass.mask(valueToUse) + "'", "Soft Text Verification");

			boolean contains = getWait(timeoutSeconds).until(d -> {
				try {
					WebElement el = d.findElement(By.xpath(xpath));
					String text = el.getText();
					return text != null && text.contains(valueToUse);
				} catch (Exception e) {
					return false; // Element not found or other issues
				}
			});

			if (contains) {
				logger.info("Soft verification passed - element [{}] text contains '{}'", elementName, valueToUse);
				BaseClass.logActionSuccess("Soft text contains verification passed", "Soft Text Verification");
				return true;
			} else {
				// Get actual text for logging purposes
				String actualText = "";
				try {
					WebElement element = driver.findElement(By.xpath(xpath));
					actualText = element.getText();
				} catch (Exception e) {
					actualText = "Element not found or not accessible";
				}

				logger.warn("Soft verification failed - element [{}] text '{}' does not contain expected '{}'", elementName, actualText, valueToUse);
				BaseClass.logActionFailure("Soft text contains verification", "Soft Text Verification", 
						"Element '" + elementName + "' text '" + actualText + "' does not contain expected '" + valueToUse + "'");
				return false;
			}

		} catch (TimeoutException e) {
			String actualText = "";
			try {
				WebElement element = driver.findElement(By.xpath(xpath));
				actualText = element.getText();
			} catch (Exception ex) {
				actualText = "Element not found";
			}

			logger.warn("Soft verification timeout - element [{}] text '{}' did not contain '{}' within {} seconds", 
					elementName, actualText, valueToUse, timeoutSeconds);
			BaseClass.logActionFailure("Soft text contains verification", "Soft Text Verification", 
					"Timeout waiting for text containing '" + valueToUse + "' on '" + elementName + "' within " + timeoutSeconds + " seconds");
			return false;

		} catch (Exception e) {
			logger.warn("Soft verification exception for element [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Soft text contains verification", "Soft Text Verification", 
					"Exception during soft verification: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Verifies that the text of the element identified by the XPath exactly equals the expected text within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate the element
	 * @param expectedText Expected text value (or parameter name if using Excel)
	 * @param elementName Descriptive name for the element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the text doesn't match expected value
	 */
	public void verifyElementTextEqualsNoReport(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedText, "Expected Text");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedText, elementName, excelData);

		try {
			boolean textMatches = getWait(timeoutSeconds).until(driver -> {
				WebElement element = driver.findElement(By.xpath(xpath));
				return valueToUse.equals(element.getText());
			});

			if (!textMatches) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualText = element.getText();
				throw new FrameworkException("Text of [" + elementName + "] expected '" + valueToUse + "' but was '" + actualText + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualText = element.getText();
			throw new FrameworkException("Element [" + elementName + "] text did not become equals '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current text: '" + actualText + "'", e);
		} catch (NoSuchElementException e) {
			throw new FrameworkException("Element [" + elementName + "] not found for text verification", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify element text equals for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the element's text contains the specified substring within the given timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate the element
	 * @param expectedFragment Expected text fragment (or parameter name if using Excel)
	 * @param elementName Descriptive name for the element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the text doesn't contain expected fragment
	 */
	public void verifyElementTextContainsNoReport(String xpath, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedFragment, "Expected Text Fragment");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedFragment, elementName, excelData);

		try {
			boolean contains = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String text = el.getText();
				return text != null && text.contains(valueToUse);
			});

			if (!contains) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualText = element.getText();
				throw new FrameworkException("Text of [" + elementName + "] expected to contain '" + valueToUse + "' but was '" + actualText + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualText = element.getText();
			throw new FrameworkException("Timeout waiting for text containing '" + valueToUse + "' on element [" + elementName + "]. Current text: '" + actualText + "'", e);
		} catch (NoSuchElementException e) {
			throw new FrameworkException("Element [" + elementName + "] not found for text contains verification", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify text contains for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the specified attribute of an element matches the expected value within the given timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate the element
	 * @param attributeName The name of the attribute to verify
	 * @param expectedValue Expected attribute value (or parameter name if using Excel)
	 * @param elementName Descriptive name for the element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the attribute doesn't match expected value
	 */
	public void verifyElementAttributeEqualsNoReport(String xpath, String attributeName, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(attributeName, "Attribute Name");
		validateInput(expectedValue, "Expected Value");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, elementName, excelData);

		try {
			boolean matched = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String attr = el.getAttribute(attributeName);
				return valueToUse.equals(attr);
			});

			if (!matched) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualValue = element.getAttribute(attributeName);
				throw new FrameworkException("Attribute '" + attributeName + "' of [" + elementName + "] expected '" + valueToUse + "' but was '" + actualValue + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualValue = element.getAttribute(attributeName);
			throw new FrameworkException("Timeout waiting for attribute '" + attributeName + "' to equal '" + valueToUse + "' on [" + elementName + "]. Current value: '" + actualValue + "'", e);
		} catch (NoSuchElementException e) {
			throw new FrameworkException("Element [" + elementName + "] not found for attribute verification", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify attribute '" + attributeName + "' for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the specified attribute of an element contains the expected substring within the given timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate the element
	 * @param attributeName The name of the attribute to verify
	 * @param expectedFragment Expected attribute fragment (or parameter name if using Excel)
	 * @param elementName Descriptive name for the element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the attribute doesn't contain expected fragment
	 */
	public void verifyElementAttributeContainsNoReport(String xpath, String attributeName, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(attributeName, "Attribute Name");
		validateInput(expectedFragment, "Expected Fragment");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedFragment, elementName, excelData);

		try {
			boolean contains = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String attr = el.getAttribute(attributeName);
				return attr != null && attr.contains(valueToUse);
			});

			if (!contains) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualValue = element.getAttribute(attributeName);
				throw new FrameworkException("Attribute '" + attributeName + "' of [" + elementName + "] expected to contain '" + valueToUse + "' but was '" + actualValue + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualValue = element.getAttribute(attributeName);
			throw new FrameworkException("Timeout waiting for attribute '" + attributeName + "' to contain '" + valueToUse + "' on [" + elementName + "]. Current value: '" + actualValue + "'", e);
		} catch (NoSuchElementException e) {
			throw new FrameworkException("Element [" + elementName + "] not found for attribute contains verification", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify attribute '" + attributeName + "' contains for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the input element's 'value' attribute equals the expected value within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate the input element
	 * @param expectedValue Expected value (or parameter name if using Excel)
	 * @param elementName Descriptive name for the element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the input value doesn't match expected value
	 */
	public void verifyInputValueEqualsNoReport(String xpath, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedValue, "Expected Value");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, elementName, excelData);

		try {
			boolean matched = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String val = el.getAttribute("value");
				return valueToUse.equals(val);
			});

			if (!matched) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualValue = element.getAttribute("value");
				throw new FrameworkException("Input element [" + elementName + "] value expected '" + valueToUse + "' but was '" + actualValue + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualValue = element.getAttribute("value");
			throw new FrameworkException("Input element [" + elementName + "] value did not become '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current value: '" + actualValue + "'", e);
		} catch (NoSuchElementException e) {
			throw new FrameworkException("Input element [" + elementName + "] not found for value verification", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify input value for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the current page title exactly matches the expected title within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param expectedTitle Expected title value (or parameter name if using Excel)
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the title doesn't match expected value
	 */
	public void verifyPageTitleEqualsNoReport(String expectedTitle, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedTitle, "Expected Title");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedTitle, "Expected Title", excelData);

		try {
			boolean matched = getWait(timeoutSeconds).until(d -> {
				String title = d.getTitle();
				return valueToUse.equals(title);
			});

			if (!matched) {
				String actualTitle = driver.getTitle();
				throw new FrameworkException("Expected title: '" + valueToUse + "' but actual title: '" + actualTitle + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			String actualTitle = driver.getTitle();
			throw new FrameworkException("Timeout waiting for page title to equal '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current title: '" + actualTitle + "'", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify page title equals", e);
		}
	}

	/**
	 * Verifies that the current page title contains the given fragment within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param fragment Expected title fragment (or parameter name if using Excel)
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the title doesn't contain expected fragment
	 */
	public void verifyPageTitleContainsNoReport(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(fragment, "Title fragment");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, fragment, "Title fragment", excelData);

		try {
			boolean contains = getWait(timeoutSeconds).until(d -> {
				String title = d.getTitle();
				return title != null && title.contains(valueToUse);
			});

			if (!contains) {
				String actualTitle = driver.getTitle();
				throw new FrameworkException("Page title '" + actualTitle + "' does not contain expected fragment: '" + valueToUse + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			String actualTitle = driver.getTitle();
			throw new FrameworkException("Timeout waiting for page title to contain '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current title: '" + actualTitle + "'", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify page title contains fragment", e);
		}
	}

	/**
	 * Verifies that the current URL exactly matches the expected URL within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param expectedUrl Expected URL (or parameter name if using Excel)
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the URL doesn't match expected value
	 */
	public void verifyUrlEqualsNoReport(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedUrl, "Expected URL");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrl, "Expected URL", excelData);

		try {
			boolean matched = getWait(timeoutSeconds).until(d -> {
				String url = d.getCurrentUrl();
				return valueToUse.equals(url);
			});

			if (!matched) {
				String actualUrl = driver.getCurrentUrl();
				throw new FrameworkException("Expected URL: '" + valueToUse + "' but actual URL: '" + actualUrl + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			String actualUrl = driver.getCurrentUrl();
			throw new FrameworkException("Timeout waiting for URL to equal '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current URL: '" + actualUrl + "'", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify URL equals", e);
		}
	}

	/**
	 * Verifies that the current URL contains the expected substring within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param fragment Expected URL fragment (or parameter name if using Excel)
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the URL doesn't contain expected fragment
	 */
	public void verifyUrlContainsNoReport(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(fragment, "URL fragment");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, fragment, "URL fragment", excelData);

		try {
			boolean contains = getWait(timeoutSeconds).until(d -> {
				String url = d.getCurrentUrl();
				return url != null && url.contains(valueToUse);
			});

			if (!contains) {
				String actualUrl = driver.getCurrentUrl();
				throw new FrameworkException("URL '" + actualUrl + "' does not contain expected fragment: '" + valueToUse + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			String actualUrl = driver.getCurrentUrl();
			throw new FrameworkException("Timeout waiting for URL to contain '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current URL: '" + actualUrl + "'", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify URL contains fragment", e);
		}
	}

	/**
	 * Verifies that the count of elements located by the given XPath equals the expected count within the specified timeout.
	 * @param xpath XPath to locate elements
	 * @param expectedCount Expected number of elements (or parameter name if using Excel)
	 * @param elementName Descriptive name for the elements
	 * @param timeoutSeconds Maximum time to wait for elements
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if element count doesn't match expected value
	 */
	public void verifyElementCountEquals(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Declare final variable after conditional processing
		final int actualCountToUse;
		if (excelData) {
			String countValue = getInputValue(testCaseName, String.valueOf(expectedCount), elementName + " Count", excelData);

			if (countValue == null || countValue.trim().isEmpty()) {
				throw new FrameworkException("Count value from Excel is null or empty for test case: " + testCaseName);
			}

			try {
				actualCountToUse = Integer.parseInt(countValue.trim());
			} catch (NumberFormatException e) {
				throw new FrameworkException("Invalid count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
			}
		} else {
			actualCountToUse = expectedCount;
		}

		try {
			BaseClass.logActionStart("Verifying element count equals " + actualCountToUse + " for '" + elementName + "' within " + timeoutSeconds + " seconds", "Element Count Verification");

			boolean countMatches = getWait(timeoutSeconds).until(driver -> {
				List<WebElement> elements = driver.findElements(By.xpath(xpath));
				return elements.size() == actualCountToUse;
			});

			if (!countMatches) {
				List<WebElement> elements = driver.findElements(By.xpath(xpath));
				int actualCount = elements.size();
				BaseClass.logActionFailure("Verify element count equals", "Element Count Verification", 
						"Element count for '" + elementName + "' expected " + actualCountToUse + " but found " + actualCount);
				throw new FrameworkException("Element count for [" + elementName + "] expected " + actualCountToUse + " but found " + actualCount);
			}

			logger.info("Verified element count equals {} for [{}]", actualCountToUse, elementName);
			BaseClass.logActionSuccess("Element count equals verification passed", "Element Count Verification");

		} catch (TimeoutException e) {
			List<WebElement> elements = driver.findElements(By.xpath(xpath));
			int actualCount = elements.size();
			logger.error("Timeout waiting for element count {} for [{}] within {} seconds. Current count: {}", actualCountToUse, elementName, timeoutSeconds, actualCount);
			BaseClass.logActionFailure("Verify element count equals", "Element Count Verification", 
					"Timeout waiting for element count " + actualCountToUse + " for '" + elementName + "' within " + timeoutSeconds + " seconds. Current count: " + actualCount);
			throw new FrameworkException("Timeout waiting for element count " + actualCountToUse + " for [" + elementName + "] within " + timeoutSeconds + " seconds. Current count: " + actualCount, e);
		} catch (FrameworkException e) {
			BaseClass.logActionFailure("Verify element count equals", "Element Count Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element count equals", "Element Count Verification", "Failed to verify element count: " + e.getMessage());
			throw new FrameworkException("Failed to verify element count for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the count of elements located by the given XPath equals the expected count within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate elements
	 * @param expectedCount Expected number of elements (or parameter name if using Excel)
	 * @param elementName Descriptive name for the elements
	 * @param timeoutSeconds Maximum time to wait for elements
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if element count doesn't match expected value
	 */
	public void verifyElementCountEqualsNoReport(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Declare final variable after conditional processing
		final int actualCountToUse;
		if (excelData) {
			String countValue = getInputValue(testCaseName, String.valueOf(expectedCount), elementName + " Count", excelData);

			if (countValue == null || countValue.trim().isEmpty()) {
				throw new FrameworkException("Count value from Excel is null or empty for test case: " + testCaseName);
			}

			try {
				actualCountToUse = Integer.parseInt(countValue.trim());
			} catch (NumberFormatException e) {
				throw new FrameworkException("Invalid count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
			}
		} else {
			actualCountToUse = expectedCount;
		}

		try {
			boolean countMatches = getWait(timeoutSeconds).until(driver -> {
				List<WebElement> elements = driver.findElements(By.xpath(xpath));
				return elements.size() == actualCountToUse;
			});

			if (!countMatches) {
				List<WebElement> elements = driver.findElements(By.xpath(xpath));
				int actualCount = elements.size();
				throw new FrameworkException("Element count for [" + elementName + "] expected " + actualCountToUse + " but found " + actualCount);
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			List<WebElement> elements = driver.findElements(By.xpath(xpath));
			int actualCount = elements.size();
			throw new FrameworkException("Timeout waiting for element count " + actualCountToUse + " for [" + elementName + "] within " + timeoutSeconds + " seconds. Current count: " + actualCount, e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify element count for [" + elementName + "]", e);
		}
	}


	/**
	 * Verifies that the number of options in a select element equals the expected count within the specified timeout.
	 * @param xpath XPath to locate the select element
	 * @param expectedCount Expected number of options (or parameter name if using Excel)
	 * @param elementName Descriptive name for the select element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the options count doesn't match expected value
	 */
	public void verifySelectOptionsCount(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Declare final variable after conditional processing
		final int actualCountToUse;
		if (excelData) {
			String countValue = getInputValue(testCaseName, String.valueOf(expectedCount), elementName + " Options Count", excelData);

			if (countValue == null || countValue.trim().isEmpty()) {
				throw new FrameworkException("Options count value from Excel is null or empty for test case: " + testCaseName);
			}

			try {
				actualCountToUse = Integer.parseInt(countValue.trim());
			} catch (NumberFormatException e) {
				throw new FrameworkException("Invalid options count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
			}
		} else {
			actualCountToUse = expectedCount;
		}

		try {
			BaseClass.logActionStart("Verifying select element '" + elementName + "' options count equals " + actualCountToUse + " within " + timeoutSeconds + " seconds", "Select Options Verification");

			boolean countMatches = getWait(timeoutSeconds).until(driver -> {
				WebElement selectElement = driver.findElement(By.xpath(xpath));
				Select select = new Select(selectElement);
				List<WebElement> options = select.getOptions();
				return options.size() == actualCountToUse;
			});

			if (!countMatches) {
				WebElement selectElement = driver.findElement(By.xpath(xpath));
				Select select = new Select(selectElement);
				int actualCount = select.getOptions().size();
				BaseClass.logActionFailure("Verify select options count", "Select Options Verification", 
						"Select options count for '" + elementName + "' expected " + actualCountToUse + " but found " + actualCount);
				throw new FrameworkException("Select options count for [" + elementName + "] expected " + actualCountToUse + " but found " + actualCount);
			}

			logger.info("Verified select options count equals {} for [{}]", actualCountToUse, elementName);
			BaseClass.logActionSuccess("Select options count verification passed", "Select Options Verification");

		} catch (TimeoutException e) {
			WebElement selectElement = driver.findElement(By.xpath(xpath));
			Select select = new Select(selectElement);
			int actualCount = select.getOptions().size();
			logger.error("Timeout waiting for select options count {} for [{}] within {} seconds. Current count: {}", actualCountToUse, elementName, timeoutSeconds, actualCount);
			BaseClass.logActionFailure("Verify select options count", "Select Options Verification", 
					"Timeout waiting for select options count " + actualCountToUse + " for '" + elementName + "' within " + timeoutSeconds + " seconds. Current count: " + actualCount);
			throw new FrameworkException("Timeout waiting for select options count " + actualCountToUse + " for [" + elementName + "] within " + timeoutSeconds + " seconds. Current count: " + actualCount, e);
		} catch (FrameworkException e) {
			BaseClass.logActionFailure("Verify select options count", "Select Options Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify select options count", "Select Options Verification", "Failed to verify select options count: " + e.getMessage());
			throw new FrameworkException("Failed to verify select options count for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that the number of options in a select element equals the expected count within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate the select element
	 * @param expectedCount Expected number of options (or parameter name if using Excel)
	 * @param elementName Descriptive name for the select element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the options count doesn't match expected value
	 */
	public void verifySelectOptionsCountNoReport(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Declare final variable after conditional processing
		final int actualCountToUse;
		if (excelData) {
			String countValue = getInputValue(testCaseName, String.valueOf(expectedCount), elementName + " Options Count", excelData);

			if (countValue == null || countValue.trim().isEmpty()) {
				throw new FrameworkException("Options count value from Excel is null or empty for test case: " + testCaseName);
			}

			try {
				actualCountToUse = Integer.parseInt(countValue.trim());
			} catch (NumberFormatException e) {
				throw new FrameworkException("Invalid options count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
			}
		} else {
			actualCountToUse = expectedCount;
		}

		try {
			boolean countMatches = getWait(timeoutSeconds).until(driver -> {
				WebElement selectElement = driver.findElement(By.xpath(xpath));
				Select select = new Select(selectElement);
				List<WebElement> options = select.getOptions();
				return options.size() == actualCountToUse;
			});

			if (!countMatches) {
				WebElement selectElement = driver.findElement(By.xpath(xpath));
				Select select = new Select(selectElement);
				int actualCount = select.getOptions().size();
				throw new FrameworkException("Select options count for [" + elementName + "] expected " + actualCountToUse + " but found " + actualCount);
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			WebElement selectElement = driver.findElement(By.xpath(xpath));
			Select select = new Select(selectElement);
			int actualCount = select.getOptions().size();
			throw new FrameworkException("Timeout waiting for select options count " + actualCountToUse + " for [" + elementName + "] within " + timeoutSeconds + " seconds. Current count: " + actualCount, e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify select options count for [" + elementName + "]", e);
		}
	}

	/**
	 * Verifies that a browser alert is present and its text matches the expected value within the specified timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param expectedAlertText Expected alert text (or parameter name if using Excel)
	 * @param timeoutSeconds Maximum time to wait for alert
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if alert text doesn't match expected value
	 */
	public void verifyAlertTextNoReport(String expectedAlertText, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedAlertText, "Expected Alert Text");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedAlertText, "Alert Text", excelData);

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			String alertText = new FluentWait<>(driver)
					.withTimeout(Duration.ofSeconds(timeoutSeconds))
					.pollingEvery(Duration.ofMillis(pollingMillis))
					.ignoring(NoAlertPresentException.class)
					.until(d -> {
						Alert alert = d.switchTo().alert();
						return alert.getText();
					});

			if (!valueToUse.equals(alertText)) {
				throw new FrameworkException("Alert text expected '" + valueToUse + "' but was '" + alertText + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			throw new FrameworkException("Timeout waiting for alert text to be '" + valueToUse + "' within " + timeoutSeconds + " seconds", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify alert text", e);
		}
	}

	/**
	 * Verifies that the CSS property of the specified element equals the expected value within the given timeout.
	 * This method performs verification without generating detailed reports/logging (NoReport version).
	 * @param xpath XPath to locate the element
	 * @param cssProperty The name of the CSS property to verify
	 * @param expectedValue Expected CSS value (or parameter name if using Excel)
	 * @param elementName Descriptive name for the element
	 * @param timeoutSeconds Maximum time to wait for the condition
	 * @param excelData Whether to fetch data from Excel
	 * @param testCaseName Test case name for Excel data lookup
	 * @throws FrameworkException if the CSS value doesn't match expected value
	 */
	public void verifyElementCssValueNoReport(String xpath, String cssProperty, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(cssProperty, "CSS Property");
		validateInput(expectedValue, "Expected Value");
		validateInput(elementName, "Element Name");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, elementName, excelData);

		try {
			boolean matched = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String cssVal = el.getCssValue(cssProperty);
				return valueToUse.equals(cssVal);
			});

			if (!matched) {
				WebElement element = driver.findElement(By.xpath(xpath));
				String actualValue = element.getCssValue(cssProperty);
				throw new FrameworkException("CSS property '" + cssProperty + "' of [" + elementName + "] expected '" + valueToUse + "' but was '" + actualValue + "'");
			}

			// No reporting/logging - silent success

		} catch (TimeoutException e) {
			WebElement element = driver.findElement(By.xpath(xpath));
			String actualValue = element.getCssValue(cssProperty);
			throw new FrameworkException("Timeout waiting for CSS property '" + cssProperty + "' to equal '" + valueToUse + "' on [" + elementName + "]. Current value: '" + actualValue + "'", e);
		} catch (NoSuchElementException e) {
			throw new FrameworkException("Element [" + elementName + "] not found for CSS property verification", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			throw new FrameworkException("Failed to verify CSS property '" + cssProperty + "' for [" + elementName + "]", e);
		}
	}


}
