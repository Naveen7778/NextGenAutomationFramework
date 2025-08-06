package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExcelUtilities;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InputKeywords {

	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(InputKeywords.class);
	Properties props = BaseClass.getProps();

	public InputKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/**
	 * Get integer configuration property dynamically from BaseClass props.
	 * Returns defaultValue if property not found or invalid.
	 */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (props == null) return defaultValue;
		try {
			return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid integer config for key '{}', using default {}", key, defaultValue);
			return defaultValue;
		}
	}

	private void validateInput(String xpath, String elementName) {
		if (xpath == null || xpath.trim().isEmpty()) {
			throw new FrameworkException("XPath cannot be null or empty");
		}
		if (elementName == null || elementName.trim().isEmpty()) {
			throw new FrameworkException("Element name cannot be null or empty");
		}
	}

	private By parseLocator(String locator) {
		if (locator == null || locator.trim().isEmpty()) {
			throw new FrameworkException("Locator cannot be null or empty");
		}
		return By.xpath(locator.trim());
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

	/**
	 * Finds element using fluent wait with timeouts read from config.properties dynamically.
	 */
	private WebElement findElementWithWait(String xpath) {
		validateInput(xpath, "Input");
		int waitTimeout = getIntConfigProperty("fluentWaitTimeout", 10);
		int pollingInterval = getIntConfigProperty("fluentWaitPolling", 500);
		try {
			return BaseClass.fluentWait(parseLocator(xpath), waitTimeout, pollingInterval);
		} catch (Exception e) {
			throw new FrameworkException("Element not found or not visible after waiting for "
					+ waitTimeout + " seconds. XPath: " + xpath, e);
		}
	}


	/** Types the given text into the input field after clearing it; logs the action. */
	public void enterText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering text '" + BaseClass.mask(valueToUse) + "' in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse);
			logger.info("Entered text '{}' in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Entered text", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter text '{}' in [{}]", valueToUse, elementName, e);
			BaseClass.logActionFailure("Enter text", elementName, "Failed to enter text '" + BaseClass.mask(valueToUse) + "': " + e.getMessage());
			throw new FrameworkException("Failed to enter text [" + valueToUse + "] in [" + elementName + "]", e);
		}
	}

	/** Appends the given text to the input field (does not clear); logs the action. */
	public void appendText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Appending text '" + BaseClass.mask(valueToUse) + "' to " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.sendKeys(valueToUse);
			logger.info("Appended text '{}' to [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Appended text", elementName);

		} catch (Exception e) {
			logger.error("Failed to append text '{}' to [{}]", valueToUse, elementName, e);
			BaseClass.logActionFailure("Append text", elementName, "Failed to append text '" + BaseClass.mask(valueToUse) + "': " + e.getMessage());
			throw new FrameworkException("Failed to append text [" + valueToUse + "] to [" + elementName + "]", e);
		}
	}

	/** Clears the text field; logs the action. */
	public void clearText(String xpath, String elementName) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Clearing text in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			logger.info("Cleared text in [{}]", elementName);

			BaseClass.logActionSuccess("Cleared text", elementName);

		} catch (Exception e) {
			logger.error("Failed to clear text in [{}]", elementName, e);
			BaseClass.logActionFailure("Clear text", elementName, "Failed to clear text: " + e.getMessage());
			throw new FrameworkException("Failed to clear text in [" + elementName + "]", e);
		}
	}

	/** Reads and returns the current value in the input field. */
	public String readInputValue(String xpath, String elementName) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Reading input value from " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			String value = element.getAttribute("value");
			logger.info("Read input value '{}' from [{}]", value, elementName);

			BaseClass.logActionSuccess("Read input value: '" + BaseClass.mask(value) + "'", elementName);
			return value;

		} catch (Exception e) {
			logger.error("Failed to read value from [{}]", elementName, e);
			BaseClass.logActionFailure("Read input value", elementName, "Failed to read value: " + e.getMessage());
			throw new FrameworkException("Failed to read value from [" + elementName + "]", e);
		}
	}

	/** Types value and then sends TAB key; logs the action. */
	public void enterTextAndTab(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering text '" + BaseClass.mask(valueToUse) + "' and TAB in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse, Keys.TAB);
			logger.info("Entered text '{}' and TAB in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Entered text and TAB", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter text and TAB in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter text and TAB", elementName, "Failed to enter text and TAB: " + e.getMessage());
			throw new FrameworkException("Failed to enter text and TAB in [" + elementName + "]", e);
		}
	}

	/** Types character by character with delay; logs the action. */
	public void typeCharByChar(String xpath, String inputValueOrKey, String elementName, int delayMs, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Typing character by character '" + BaseClass.mask(valueToUse) + "' in " + elementName + " with " + delayMs + "ms delay", elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			for (char c : valueToUse.toCharArray()) {
				element.sendKeys(String.valueOf(c));
				Thread.sleep(delayMs);
			}
			logger.info("Typed by character '{}' in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Typed character by character", elementName);

		} catch (Exception e) {
			logger.error("Failed char-by-char type in [{}]", elementName, e);
			BaseClass.logActionFailure("Type character by character", elementName, "Failed char-by-char typing: " + e.getMessage());
			throw new FrameworkException("Failed typing by character in [" + elementName + "]", e);
		}
	}

	/** Sends specific key(s) to an input field. */
	public void sendKeys(String xpath, Keys key, String elementName) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Sending key '" + key + "' to " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.sendKeys(key);
			logger.info("Sent key [{}] to [{}]", key, elementName);

			BaseClass.logActionSuccess("Sent key '" + key + "'", elementName);

		} catch (Exception e) {
			logger.error("Failed to send key [{}] to [{}]", key, elementName, e);
			BaseClass.logActionFailure("Send keys", elementName, "Failed to send key '" + key + "': " + e.getMessage());
			throw new FrameworkException("Failed to send key to [" + elementName + "]", e);
		}
	}

	/** Sets the value using JavaScript (helpful for tricky input controls). */
	public void setInputValueWithJS(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Setting value '" + BaseClass.mask(valueToUse) + "' using JavaScript in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));",
					element, valueToUse);
			logger.info("Set value with JS '{}' in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Set value using JavaScript", elementName);

		} catch (Exception e) {
			logger.error("Failed to set value with JS in [{}]", elementName, e);
			BaseClass.logActionFailure("Set value with JavaScript", elementName, "Failed to set value with JS: " + e.getMessage());
			throw new FrameworkException("Failed to set value with JS in [" + elementName + "]", e);
		}
	}

	/** Triggers a DOM event (e.g., 'change' or 'input') on the input element. */
	public void triggerInputEvent(String xpath, String event, String elementName) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Triggering event '" + event + "' on " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].dispatchEvent(new Event(arguments[1], { bubbles:true }))", element, event);
			logger.info("Triggered event '{}' on [{}]", event, elementName);

			BaseClass.logActionSuccess("Triggered event '" + event + "'", elementName);

		} catch (Exception e) {
			logger.error("Failed to trigger event '{}' on [{}]", event, elementName, e);
			BaseClass.logActionFailure("Trigger input event", elementName, "Failed to trigger event '" + event + "': " + e.getMessage());
			throw new FrameworkException("Failed to trigger input event on [" + elementName + "]", e);
		}
	}

	/** Enters text only if the input is visible. */
	public void enterTextIfVisible(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Checking visibility and entering text '" + BaseClass.mask(valueToUse) + "' in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			if (element.isDisplayed()) {
				element.clear();
				element.sendKeys(valueToUse);
				logger.info("Entered text '{}' in visible [{}]", valueToUse, elementName);
				BaseClass.logActionSuccess("Entered text in visible element", elementName);
			} else {
				logger.warn("Element [{}] is not visible; skipping entry.", elementName);
				BaseClass.logActionSuccess("Element not visible, skipped text entry", elementName);
			}

		} catch (Exception e) {
			logger.error("Failed entering text if visible in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter text if visible", elementName, "Failed to enter text if visible: " + e.getMessage());
			throw new FrameworkException("Failed entering text if visible in [" + elementName + "]", e);
		}
	}

	/** Enters text only if the input is enabled. */
	public void enterTextIfEnabled(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Checking if enabled and entering text '" + BaseClass.mask(valueToUse) + "' in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			if (element.isEnabled()) {
				element.clear();
				element.sendKeys(valueToUse);
				logger.info("Entered text '{}' in enabled [{}]", valueToUse, elementName);
				BaseClass.logActionSuccess("Entered text in enabled element", elementName);
			} else {
				logger.warn("Element [{}] is not enabled; skipping entry.", elementName);
				BaseClass.logActionSuccess("Element not enabled, skipped text entry", elementName);
			}

		} catch (Exception e) {
			logger.error("Failed entering text if enabled in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter text if enabled", elementName, "Failed to enter text if enabled: " + e.getMessage());
			throw new FrameworkException("Failed entering text if enabled in [" + elementName + "]", e);
		}
	}

	/** Asserts that input value equals expected (direct string or Excel sourced). */
	public void assertInputValueEquals(String xpath, String expectedValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String expectedValue = getInputValue(testName, expectedValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Asserting input value equals '" + BaseClass.mask(expectedValue) + "' in " + elementName, elementName);

			String actual = readInputValue(xpath, elementName);
			if (!expectedValue.equals(actual)) {
				BaseClass.logActionFailure("Assert input value equals", elementName, "Expected '" + expectedValue + "' but found '" + actual + "'");
				throw new FrameworkException("Expected input value '" + expectedValue + "' but found '" + actual + "'. [" + elementName + "]");
			}

			logger.info("Assertion passed: [{}] input value equals '{}'", elementName, expectedValue);
			BaseClass.logActionSuccess("Input value assertion passed", elementName);

		} catch (Exception e) {
			logger.error("Assertion failed in [{}]", elementName, e);
			BaseClass.logActionFailure("Assert input value equals", elementName, e.getMessage());
			throw new FrameworkException("Assertion for input value failed for [" + elementName + "]", e);
		}
	}

	/** Asserts the input field is empty. */
	public void assertInputIsEmpty(String xpath, String elementName) {
		try {
			BaseClass.logActionStart("Asserting input field is empty in " + elementName, elementName);
			assertInputValueEquals(xpath, "", elementName, false, null);
			BaseClass.logActionSuccess("Input is empty assertion passed", elementName);
		} catch (Exception e) {
			BaseClass.logActionFailure("Assert input is empty", elementName, e.getMessage());
			throw e;
		}
	}

	/** Asserts the input field is enabled. */
	public void assertInputIsEnabled(String xpath, String elementName) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Asserting input field is enabled in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			if (!element.isEnabled()) {
				BaseClass.logActionFailure("Assert input is enabled", elementName, "Input field is not enabled");
				throw new FrameworkException("Expected input [" + elementName + "] to be enabled.");
			}

			logger.info("Assertion passed: [{}] is enabled", elementName);
			BaseClass.logActionSuccess("Input is enabled assertion passed", elementName);

		} catch (Exception e) {
			logger.error("Assertion for enabled input failed in [{}]", elementName, e);
			BaseClass.logActionFailure("Assert input is enabled", elementName, e.getMessage());
			throw new FrameworkException("Input enabled assertion failed for [" + elementName + "]", e);
		}
	}

	/** Asserts the input field is disabled. */
	public void assertInputIsDisabled(String xpath, String elementName) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Asserting input field is disabled in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			if (element.isEnabled()) {
				BaseClass.logActionFailure("Assert input is disabled", elementName, "Input field is enabled (expected disabled)");
				throw new FrameworkException("Expected input [" + elementName + "] to be disabled.");
			}

			logger.info("Assertion passed: [{}] is disabled", elementName);
			BaseClass.logActionSuccess("Input is disabled assertion passed", elementName);

		} catch (Exception e) {
			logger.error("Assertion for disabled input failed in [{}]", elementName, e);
			BaseClass.logActionFailure("Assert input is disabled", elementName, e.getMessage());
			throw new FrameworkException("Input disabled assertion failed for [" + elementName + "]", e);
		}
	}

	/** Asserts the input contains substring. */
	public void assertInputContains(String xpath, String expectedFragmentOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String expectedFragment = getInputValue(testName, expectedFragmentOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Asserting input contains '" + BaseClass.mask(expectedFragment) + "' in " + elementName, elementName);

			String actual = readInputValue(xpath, elementName);
			if (!actual.contains(expectedFragment)) {
				BaseClass.logActionFailure("Assert input contains", elementName, "Expected to contain '" + expectedFragment + "' but found '" + actual + "'");
				throw new FrameworkException("Expected input value to contain '" + expectedFragment + "', but found '" + actual + "'. [" + elementName + "]");
			}

			logger.info("Assertion passed: [{}] input value contains '{}'", elementName, expectedFragment);
			BaseClass.logActionSuccess("Input contains assertion passed", elementName);

		} catch (Exception e) {
			logger.error("Assertion (contains) failed in [{}]", elementName, e);
			BaseClass.logActionFailure("Assert input contains", elementName, e.getMessage());
			throw new FrameworkException("Assertion (contains) failed for [" + elementName + "]", e);
		}
	}

	/**
	 * Clears text from input field located by XPath.
	 */
	public void clearText(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Clearing text from element '" + elementName + "'", elementName);

			// Use your existing findElementWithWait method
			WebElement element = findElementWithWait(xpath);

			// Clear the text using Selenium's clear() method
			element.clear();

			// Verify the field is actually cleared
			String remainingText = element.getAttribute("value");
			if (remainingText != null && !remainingText.isEmpty()) {
				logger.warn("Standard clear() didn't fully clear element [{}], trying alternative methods", elementName);

				// Method 1: Select all and delete
				element.sendKeys(Keys.CONTROL + "a");
				element.sendKeys(Keys.DELETE);

				// Check again
				remainingText = element.getAttribute("value");
				if (remainingText != null && !remainingText.isEmpty()) {
					// Method 2: JavaScript clear as last resort
					JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
					jsExecutor.executeScript("arguments[0].value = '';", element);
					logger.info("Used JavaScript to clear element [{}]", elementName);
				}
			}

			logger.info("Successfully cleared text from element [{}]", elementName);
			BaseClass.logActionSuccess("Cleared text from element", elementName);

		} catch (Exception e) {
			logger.error("Failed to clear text from element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Clear text from element", elementName, "Text clearing failed: " + e.getMessage());
			throw new FrameworkException("Failed to clear text from element [" + elementName + "]", e);
		}
	}

	/**
	 * Gets text value from input field located by XPath.
	 */
	public String getInputValue(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Getting input value from element '" + elementName + "'", elementName);

			// Use your existing findElementWithWait method (but need to modify it to accept timeout)
			WebElement element = findElementWithWait(xpath);

			// Get the value attribute from the input field
			String inputValue = element.getAttribute("value");

			// Handle null case
			if (inputValue == null) {
				inputValue = "";
				logger.info("Input value was null for element [{}], returning empty string", elementName);
			}

			logger.info("Retrieved input value from element [{}]: '{}'", elementName, inputValue);
			BaseClass.logActionSuccess("Retrieved input value", elementName + " (value: '" + inputValue + "')");

			return inputValue;

		} catch (Exception e) {
			logger.error("Failed to get input value from element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Get input value", elementName, "Failed to retrieve value: " + e.getMessage());
			throw new FrameworkException("Failed to get input value from element [" + elementName + "]", e);
		}
	}

	/** Enters text character by character with specified delay between each character. */
	public void enterTextSlowly(String xpath, String inputValueOrKey, String elementName, int delayMs, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering text slowly '" + BaseClass.mask(valueToUse) + "' in " + elementName + " with " + delayMs + "ms delay", elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			for (char c : valueToUse.toCharArray()) {
				element.sendKeys(String.valueOf(c));
				Thread.sleep(delayMs);
			}
			logger.info("Entered text slowly '{}' in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Entered text slowly", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter text slowly in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter text slowly", elementName, "Failed slow text entry: " + e.getMessage());
			throw new FrameworkException("Failed entering text slowly in [" + elementName + "]", e);
		}
	}

	/** Enters text using JavaScript executor for elements that don't respond to standard sendKeys. */
	public void enterTextUsingJS(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering text using JavaScript '" + BaseClass.mask(valueToUse) + "' in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);

			// Clear field first using JavaScript
			((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);

			// Set the value using JavaScript and trigger input events
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].value = arguments[1]; " +
							"arguments[0].dispatchEvent(new Event('input', { bubbles: true })); " +
							"arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
							element, valueToUse);

			logger.info("Entered text using JavaScript '{}' in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Entered text using JavaScript", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter text using JavaScript in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter text using JavaScript", elementName, "Failed JS text entry: " + e.getMessage());
			throw new FrameworkException("Failed entering text using JavaScript in [" + elementName + "]", e);
		}
	}

	/** Enters text and then presses specified key in input field; logs the action. */
	public void enterTextAndPressKey(String xpath, String inputValueOrKey, Keys key, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering text '" + BaseClass.mask(valueToUse) + "' and pressing key '" + key + "' in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse, key);
			logger.info("Entered text '{}' and pressed key '{}' in [{}]", valueToUse, key, elementName);

			BaseClass.logActionSuccess("Entered text and pressed key '" + key + "'", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter text and press key '{}' in [{}]", key, elementName, e);
			BaseClass.logActionFailure("Enter text and press key", elementName, "Failed to enter text and press key '" + key + "': " + e.getMessage());
			throw new FrameworkException("Failed to enter text and press key '" + key + "' in [" + elementName + "]", e);
		}
	}

	/** Verifies that input field contains the expected value (direct string or Excel sourced). */
	public void verifyInputValue(String xpath, String expectedValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String expectedValue = getInputValue(testName, expectedValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Verifying input value equals '" + BaseClass.mask(expectedValue) + "' in " + elementName, elementName);

			String actual = readInputValue(xpath, elementName);
			if (!expectedValue.equals(actual)) {
				BaseClass.logActionFailure("Verify input value", elementName, "Expected '" + expectedValue + "' but found '" + actual + "'");
				throw new FrameworkException("Expected input value '" + expectedValue + "' but found '" + actual + "'. [" + elementName + "]");
			}

			logger.info("Verification passed: [{}] input value equals '{}'", elementName, expectedValue);
			BaseClass.logActionSuccess("Input value verification passed", elementName);

		} catch (Exception e) {
			logger.error("Verification failed in [{}]", elementName, e);
			BaseClass.logActionFailure("Verify input value", elementName, e.getMessage());
			throw new FrameworkException("Verification for input value failed for [" + elementName + "]", e);
		}
	}

	/** Enters text into input field and validates that the text was entered correctly. */
	public void enterTextWithValidation(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering text with validation '" + BaseClass.mask(valueToUse) + "' in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse);

			// Validate that the text was entered correctly
			String actualValue = element.getAttribute("value");
			if (actualValue == null) {
				actualValue = "";
			}

			if (!valueToUse.equals(actualValue)) {
				BaseClass.logActionFailure("Enter text with validation", elementName, 
						"Text entry validation failed - Expected '" + valueToUse + "' but found '" + actualValue + "'");
				throw new FrameworkException("Text entry validation failed for [" + elementName + "] - Expected '" + valueToUse + "' but found '" + actualValue + "'");
			}

			logger.info("Entered and validated text '{}' in [{}]", valueToUse, elementName);
			BaseClass.logActionSuccess("Entered text with validation", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter text with validation in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter text with validation", elementName, "Failed text entry with validation: " + e.getMessage());
			throw new FrameworkException("Failed entering text with validation in [" + elementName + "]", e);
		}
	}

	/** Soft enters text into input field and returns true if successful, false otherwise without throwing exceptions. */
	public boolean softEnterText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Soft entering text '" + BaseClass.mask(valueToUse) + "' in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse);
			logger.info("Soft entered text '{}' in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Soft entered text", elementName);
			return true;

		} catch (Exception e) {
			logger.warn("Soft enter text failed for element [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionSuccess("Soft enter text failed - " + e.getMessage(), elementName);
			return false;
		}
	}

	/** Enters text into input field and waits for specified element to appear or become visible. */
	@SuppressWarnings("unused")
	public void enterTextAndWaitForElement(String xpath, String inputValueOrKey, String waitElementXpath, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		validateInput(waitElementXpath, "Wait Element XPath");
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering text '" + BaseClass.mask(valueToUse) + "' in " + elementName + " and waiting for element", elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse);
			logger.info("Entered text '{}' in [{}]", valueToUse, elementName);

			// Wait for the specified element to appear
			WebElement waitElement = findElementWithWait(waitElementXpath);
			logger.info("Wait element appeared after entering text in [{}]", elementName);

			BaseClass.logActionSuccess("Entered text and wait element appeared", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter text and wait for element in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter text and wait for element", elementName, "Failed text entry and wait: " + e.getMessage());
			throw new FrameworkException("Failed entering text and waiting for element in [" + elementName + "]", e);
		}
	}

	/** Enters masked text (for passwords or sensitive data) with enhanced security logging. */
	public void enterMaskedText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering masked text in " + elementName, elementName);

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse);
			logger.info("Entered masked text in [{}]", elementName);

			BaseClass.logActionSuccess("Entered masked text", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter masked text in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter masked text", elementName, "Failed masked text entry: " + e.getMessage());
			throw new FrameworkException("Failed entering masked text in [" + elementName + "]", e);
		}
	}

	/** Enters numeric text only into input field after validating input is numeric. */
	public void enterNumericText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		validateInput(xpath, elementName);
		String valueToUse = getInputValue(testName, inputValueOrKey, elementName, excelData);

		try {
			BaseClass.logActionStart("Entering numeric text '" + BaseClass.mask(valueToUse) + "' in " + elementName, elementName);

			// Validate that the input is numeric
			if (valueToUse != null && !valueToUse.trim().isEmpty()) {
				try {
					// Check if it's a valid number (allows decimals, negatives)
					Double.parseDouble(valueToUse.trim());
				} catch (NumberFormatException e) {
					BaseClass.logActionFailure("Enter numeric text", elementName, "Input value '" + valueToUse + "' is not numeric");
					throw new FrameworkException("Input value '" + valueToUse + "' is not numeric for [" + elementName + "]");
				}
			}

			WebElement element = findElementWithWait(xpath);
			element.clear();
			element.sendKeys(valueToUse);
			logger.info("Entered numeric text '{}' in [{}]", valueToUse, elementName);

			BaseClass.logActionSuccess("Entered numeric text", elementName);

		} catch (Exception e) {
			logger.error("Failed to enter numeric text in [{}]", elementName, e);
			BaseClass.logActionFailure("Enter numeric text", elementName, "Failed numeric text entry: " + e.getMessage());
			throw new FrameworkException("Failed entering numeric text in [" + elementName + "]", e);
		}
	}

	
	
	
	
	
	
	
	
	





}
