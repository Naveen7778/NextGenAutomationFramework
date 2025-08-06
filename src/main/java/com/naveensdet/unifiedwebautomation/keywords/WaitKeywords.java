package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExcelUtilities;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.util.List;
import java.util.function.Function;
import java.time.Duration;

public class WaitKeywords {

	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(WaitKeywords.class);

	public WaitKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/**
	 * Retrieves an integer configuration property dynamically from BaseClass.props.
	 * Returns defaultValue if property not found or invalid.
	 */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(BaseClass.getProps().getProperty(key, String.valueOf(defaultValue)));
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

	private void validateInput(String xpath) {
		if (xpath == null || xpath.trim().isEmpty()) {
			throw new FrameworkException("XPath cannot be null or empty");
		}
	}

	private By parseLocator(String locator) {
		if (locator == null || locator.trim().isEmpty()) {
			throw new FrameworkException("Locator cannot be null or empty");
		}
		return By.xpath(locator.trim());
	}

	/** Creates and returns a FluentWait<WebDriver> with dynamic timeout and polling interval from config. */
	private FluentWait<WebDriver> getWait(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(pollingMillis))
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	/** Creates and returns a FluentWait<WebDriver> with dynamic timeout and polling interval from config. */
	private FluentWait<WebDriver> getFluentWait(int timeoutSeconds) {
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

	// ----- Wait Methods -----

	/** Sets the implicit wait timeout globally for the WebDriver */
	public void setImplicitWait(int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Setting implicit wait to " + timeoutSeconds + " seconds", "Wait Configuration");

			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutSeconds));
			logger.info("Set implicit wait to {} seconds", timeoutSeconds);

			BaseClass.logActionSuccess("Set implicit wait to " + timeoutSeconds + " seconds", "Wait Configuration");

		} catch (Exception e) {
			logger.error("Failed to set implicit wait to {} seconds", timeoutSeconds, e);
			BaseClass.logActionFailure("Set implicit wait", "Wait Configuration", "Failed to set implicit wait to " + timeoutSeconds + " seconds: " + e.getMessage());
			throw new FrameworkException("Failed to set implicit wait", e);
		}
	}

	/** Waits until element is visible on the page. */
	public void waitForElementVisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to be visible (timeout: " + timeoutSeconds + "s)", "Element Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(parseLocator(xpath)));
			logger.info("Element [{}] became visible within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Element '" + elementName + "' became visible", "Element Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] to be visible", elementName, e);
			BaseClass.logActionFailure("Wait for element visible", "Element Wait", "Timeout waiting for element '" + elementName + "' to be visible after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] to be visible", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for element visible", "Element Wait", "Failed to wait for element visibility: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element visibility", e);
		}
	}

	/** Waits until element is invisible or not present. */
	public void waitForElementInvisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to be invisible (timeout: " + timeoutSeconds + "s)", "Element Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(parseLocator(xpath)));
			logger.info("Element [{}] became invisible within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Element '" + elementName + "' became invisible", "Element Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] to be invisible", elementName, e);
			BaseClass.logActionFailure("Wait for element invisible", "Element Wait", "Timeout waiting for element '" + elementName + "' to be invisible after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] to be invisible", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for element invisible", "Element Wait", "Failed to wait for element invisibility: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element invisibility", e);
		}
	}

	/** Waits until element is clickable (visible and enabled). */
	public void waitForElementClickable(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to be clickable (timeout: " + timeoutSeconds + "s)", "Element Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(parseLocator(xpath)));
			logger.info("Element [{}] became clickable within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Element '" + elementName + "' became clickable", "Element Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] to be clickable", elementName, e);
			BaseClass.logActionFailure("Wait for element clickable", "Element Wait", "Timeout waiting for element '" + elementName + "' to be clickable after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] to be clickable", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for element clickable", "Element Wait", "Failed to wait for element clickable: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element clickable", e);
		}
	}

	/** Waits until element is present in the DOM regardless of visibility. */
	public void waitForElementPresent(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to be present in DOM (timeout: " + timeoutSeconds + "s)", "Element Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(parseLocator(xpath)));
			logger.info("Element [{}] is present in DOM within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Element '" + elementName + "' is present in DOM", "Element Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] presence in DOM", elementName, e);
			BaseClass.logActionFailure("Wait for element present", "Element Wait", "Timeout waiting for element '" + elementName + "' presence in DOM after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] presence in DOM", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for element present", "Element Wait", "Failed to wait for element presence: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element presence", e);
		}
	}

	/** Waits until element's text contains specified substring. */
	public void waitForTextInElement(String xpath, String text, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		if (text == null || text.isEmpty()) {
			BaseClass.logActionFailure("Wait for text in element", "Text Wait", "Text parameter cannot be null or empty");
			throw new FrameworkException("Text parameter cannot be null or empty");
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, text, "Expected Text", excelData);

		try {
			BaseClass.logActionStart("Waiting for text '" + BaseClass.mask(valueToUse) + "' in element '" + elementName + "' (timeout: " + timeoutSeconds + "s)", "Text Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(parseLocator(xpath), valueToUse));
			logger.info("Element [{}] contains text '{}' within {} seconds", elementName, valueToUse, timeoutSeconds);

			BaseClass.logActionSuccess("Element '" + elementName + "' contains expected text", "Text Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for text '{}' in element [{}]", valueToUse, elementName, e);
			BaseClass.logActionFailure("Wait for text in element", "Text Wait", "Timeout waiting for text '" + valueToUse + "' in element '" + elementName + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for text '" + valueToUse + "' in element [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for text in element", "Text Wait", "Failed to wait for text in element: " + e.getMessage());
			throw new FrameworkException("Failed to wait for text in element", e);
		}
	}

	/** Waits until the element's value attribute contains the specified string. */
	public void waitForValueInInput(String xpath, String valueFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		if (valueFragment == null || valueFragment.isEmpty()) {
			BaseClass.logActionFailure("Wait for value in input", "Input Wait", "Value fragment cannot be null or empty");
			throw new FrameworkException("Value fragment cannot be null or empty");
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, valueFragment, "Expected Value", excelData);

		try {
			BaseClass.logActionStart("Waiting for input value containing '" + BaseClass.mask(valueToUse) + "' in element '" + elementName + "' (timeout: " + timeoutSeconds + "s)", "Input Wait");

			int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
			FluentWait<WebDriver> wait = getFluentWait(timeoutSeconds).pollingEvery(Duration.ofMillis(pollingMillis));
			wait.until(driver -> {
				try {
					WebElement element = driver.findElement(parseLocator(xpath));
					String val = element.getAttribute("value");
					return val != null && val.contains(valueToUse);
				} catch (StaleElementReferenceException | NoSuchElementException ex) {
					return false;
				}
			});
			logger.info("Element [{}] input value contains '{}' within {} seconds", elementName, valueToUse, timeoutSeconds);

			BaseClass.logActionSuccess("Input value contains expected fragment", "Input Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for input value containing '{}' in element [{}]", valueToUse, elementName, e);
			BaseClass.logActionFailure("Wait for value in input", "Input Wait", "Timeout waiting for input value containing '" + valueToUse + "' in element '" + elementName + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for input value containing '" + valueToUse + "' in [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for value in input", "Input Wait", "Failed to wait for input value: " + e.getMessage());
			throw new FrameworkException("Failed to wait for input value", e);
		}
	}


	/** Waits until page title equals expected. */
	public void waitForTitleIs(String expectedTitle, int timeoutSeconds, boolean excelData, String testCaseName) {
		if (expectedTitle == null || expectedTitle.isEmpty()) {
			BaseClass.logActionFailure("Wait for title", "Page Wait", "Expected title cannot be null or empty");
			throw new FrameworkException("Expected title cannot be null or empty");
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedTitle, "Expected Title", excelData);

		try {
			BaseClass.logActionStart("Waiting for page title to be '" + BaseClass.mask(valueToUse) + "' (timeout: " + timeoutSeconds + "s)", "Page Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.titleIs(valueToUse));
			logger.info("Page title became '{}' within {} seconds", valueToUse, timeoutSeconds);

			BaseClass.logActionSuccess("Page title became expected value", "Page Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for page title to be '{}'", valueToUse, e);
			BaseClass.logActionFailure("Wait for title", "Page Wait", "Timeout waiting for page title to be '" + valueToUse + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for page title to be '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for title", "Page Wait", "Failed to wait for page title: " + e.getMessage());
			throw new FrameworkException("Failed to wait for page title", e);
		}
	}


	/** Waits until page title contains specified fragment. */
	public void waitForTitleContains(String titleFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		if (titleFragment == null || titleFragment.isEmpty()) {
			BaseClass.logActionFailure("Wait for title contains", "Page Wait", "Title fragment cannot be null or empty");
			throw new FrameworkException("Title fragment cannot be null or empty");
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, titleFragment, "Title Fragment", excelData);

		try {
			BaseClass.logActionStart("Waiting for page title to contain '" + BaseClass.mask(valueToUse) + "' (timeout: " + timeoutSeconds + "s)", "Page Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.titleContains(valueToUse));
			logger.info("Page title contains '{}' within {} seconds", valueToUse, timeoutSeconds);

			BaseClass.logActionSuccess("Page title contains expected fragment", "Page Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for page title to contain '{}'", valueToUse, e);
			BaseClass.logActionFailure("Wait for title contains", "Page Wait", "Timeout waiting for page title to contain '" + valueToUse + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for page title to contain '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for title contains", "Page Wait", "Failed to wait for title contains: " + e.getMessage());
			throw new FrameworkException("Failed to wait for title contains", e);
		}
	}


	/** Waits until the current URL equals expected. */
	public void waitForUrlToBe(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		if (expectedUrl == null || expectedUrl.isEmpty()) {
			BaseClass.logActionFailure("Wait for URL", "Page Wait", "Expected URL cannot be null or empty");
			throw new FrameworkException("Expected URL cannot be null or empty");
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrl, "Expected URL", excelData);

		try {
			BaseClass.logActionStart("Waiting for URL to be '" + BaseClass.mask(valueToUse) + "' (timeout: " + timeoutSeconds + "s)", "Page Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.urlToBe(valueToUse));
			logger.info("URL became '{}' within {} seconds", valueToUse, timeoutSeconds);

			BaseClass.logActionSuccess("URL became expected value", "Page Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for URL to be '{}'", valueToUse, e);
			BaseClass.logActionFailure("Wait for URL", "Page Wait", "Timeout waiting for URL to be '" + valueToUse + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for URL to be '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for URL", "Page Wait", "Failed to wait for URL: " + e.getMessage());
			throw new FrameworkException("Failed to wait for URL", e);
		}
	}


	/** Waits until the current URL contains specified fragment. */
	public void waitForUrlContains(String urlFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		if (urlFragment == null || urlFragment.isEmpty()) {
			BaseClass.logActionFailure("Wait for URL contains", "Page Wait", "URL fragment cannot be null or empty");
			throw new FrameworkException("URL fragment cannot be null or empty");
		}

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, urlFragment, "URL Fragment", excelData);

		try {
			BaseClass.logActionStart("Waiting for URL to contain '" + BaseClass.mask(valueToUse) + "' (timeout: " + timeoutSeconds + "s)", "Page Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.urlContains(valueToUse));
			logger.info("URL contains '{}' within {} seconds", valueToUse, timeoutSeconds);

			BaseClass.logActionSuccess("URL contains expected fragment", "Page Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for URL to contain '{}'", valueToUse, e);
			BaseClass.logActionFailure("Wait for URL contains", "Page Wait", "Timeout waiting for URL to contain '" + valueToUse + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for URL to contain '" + valueToUse + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for URL contains", "Page Wait", "Failed to wait for URL contains: " + e.getMessage());
			throw new FrameworkException("Failed to wait for URL contains", e);
		}
	}


	/** Waits until an alert is present on the page. */
	public void waitForAlertPresent(int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Waiting for alert to be present (timeout: " + timeoutSeconds + "s)", "Alert Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
			logger.info("Alert appeared within {} seconds", timeoutSeconds);

			BaseClass.logActionSuccess("Alert appeared", "Alert Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for alert to be present", e);
			BaseClass.logActionFailure("Wait for alert present", "Alert Wait", "Timeout waiting for alert to be present after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for alert to be present", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for alert present", "Alert Wait", "Failed to wait for alert: " + e.getMessage());
			throw new FrameworkException("Failed to wait for alert", e);
		}
	}

	/** Waits for frame by XPath or name/id, then switches focus to it. */
	public void waitForFrameAndSwitch(String frameXpathOrName, int timeoutSeconds) {
		validateInput(frameXpathOrName);

		try {
			BaseClass.logActionStart("Waiting for frame '" + frameXpathOrName + "' and switching to it (timeout: " + timeoutSeconds + "s)", "Frame Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameXpathOrName));
			logger.info("Switched to frame [{}] within {} seconds", frameXpathOrName, timeoutSeconds);

			BaseClass.logActionSuccess("Switched to frame '" + frameXpathOrName + "'", "Frame Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for frame [{}] to be available", frameXpathOrName, e);
			BaseClass.logActionFailure("Wait for frame and switch", "Frame Wait", "Timeout waiting for frame '" + frameXpathOrName + "' to be available after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for frame [" + frameXpathOrName + "] to be available", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for frame and switch", "Frame Wait", "Failed to wait for frame: " + e.getMessage());
			throw new FrameworkException("Failed to wait for frame", e);
		}
	}

	/** Waits until checkbox is selected. */
	public void waitForElementSelected(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to be selected (timeout: " + timeoutSeconds + "s)", "Element State Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.elementToBeSelected(parseLocator(xpath)));
			logger.info("Element [{}] is selected within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Element '" + elementName + "' is selected", "Element State Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] to be selected", elementName, e);
			BaseClass.logActionFailure("Wait for element selected", "Element State Wait", "Timeout waiting for element '" + elementName + "' to be selected after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] to be selected", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for element selected", "Element State Wait", "Failed to wait for element selected: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element selected", e);
		}
	}

	/** Waits until checkbox or radio button is NOT selected. */
	public void waitForElementNotSelected(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to NOT be selected (timeout: " + timeoutSeconds + "s)", "Element State Wait");

			int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
			FluentWait<WebDriver> fluentWait = getFluentWait(timeoutSeconds).pollingEvery(Duration.ofMillis(pollingMillis));
			fluentWait.until(driver -> {
				try {
					WebElement element = driver.findElement(parseLocator(xpath));
					return !element.isSelected();
				} catch (NoSuchElementException | StaleElementReferenceException ex) {
					return false;
				}
			});
			logger.info("Element [{}] is not selected within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Element '" + elementName + "' is not selected", "Element State Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] to NOT be selected", elementName, e);
			BaseClass.logActionFailure("Wait for element not selected", "Element State Wait", "Timeout waiting for element '" + elementName + "' to NOT be selected after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] to NOT be selected", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for element not selected", "Element State Wait", "Failed to wait for element not selected: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element not selected", e);
		}
	}

	/** Waits until all elements matching XPath are visible. */
	public void waitForAllElementsVisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for all elements '" + elementName + "' to be visible (timeout: " + timeoutSeconds + "s)", "Multiple Elements Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(parseLocator(xpath)));
			logger.info("All elements [{}] are visible within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("All elements '" + elementName + "' are visible", "Multiple Elements Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for all elements [{}] to be visible", elementName, e);
			BaseClass.logActionFailure("Wait for all elements visible", "Multiple Elements Wait", "Timeout waiting for all elements '" + elementName + "' to be visible after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for all elements [" + elementName + "] to be visible", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for all elements visible", "Multiple Elements Wait", "Failed to wait for all elements visible: " + e.getMessage());
			throw new FrameworkException("Failed to wait for all elements visible", e);
		}
	}

	/** Waits until all elements matching XPath are present in DOM. */
	public void waitForAllElementsPresent(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for all elements '" + elementName + "' to be present in DOM (timeout: " + timeoutSeconds + "s)", "Multiple Elements Wait");

			getWait(timeoutSeconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(parseLocator(xpath)));
			logger.info("All elements [{}] are present in DOM within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("All elements '" + elementName + "' are present in DOM", "Multiple Elements Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for all elements [{}] presence in DOM", elementName, e);
			BaseClass.logActionFailure("Wait for all elements present", "Multiple Elements Wait", "Timeout waiting for all elements '" + elementName + "' presence in DOM after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for all elements [" + elementName + "] presence in DOM", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for all elements present", "Multiple Elements Wait", "Failed to wait for all elements present: " + e.getMessage());
			throw new FrameworkException("Failed to wait for all elements present", e);
		}
	}

	/**
	 * Fluent wait with custom polling interval.
	 * Waits until element located by XPath satisfies any custom function.
	 */
	public void fluentWait(String xpath, String elementName, int timeoutSeconds, int pollingMillis, Function<WebDriver, Boolean> condition) {
		validateInput(xpath, elementName);

		try {
			BaseClass.logActionStart("Waiting for custom condition on element '" + elementName + "' (timeout: " + timeoutSeconds + "s, polling: " + pollingMillis + "ms)", "Custom Wait");

			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.ignoring(NoSuchElementException.class)
			.ignoring(StaleElementReferenceException.class)
			.until(condition);
			logger.info("Fluent wait condition met for [{}] within {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Custom condition met for element '" + elementName + "'", "Custom Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for fluent wait condition on [{}]", elementName, e);
			BaseClass.logActionFailure("Fluent wait", "Custom Wait", "Timeout in fluent wait condition for element '" + elementName + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout in fluent wait condition for [" + elementName + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Fluent wait", "Custom Wait", "Failed in fluent wait: " + e.getMessage());
			throw new FrameworkException("Failed in fluent wait", e);
		}
	}

	/**
	 * Waits until a custom JavaScript condition returns true.
	 * 
	 * @param jsCondition - JavaScript snippet (must return boolean)
	 * @param timeoutSeconds - max wait time
	 */
	public void waitForJavaScriptCondition(String jsCondition, int timeoutSeconds) {
		if (jsCondition == null || jsCondition.trim().isEmpty()) {
			BaseClass.logActionFailure("Wait for JavaScript condition", "JavaScript Wait", "JavaScript condition cannot be null or empty");
			throw new FrameworkException("JavaScript condition cannot be null or empty");
		}

		try {
			BaseClass.logActionStart("Waiting for JavaScript condition: '" + BaseClass.mask(jsCondition) + "' (timeout: " + timeoutSeconds + "s)", "JavaScript Wait");

			int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> {
				Object result = ((JavascriptExecutor) drv).executeScript("return " + jsCondition);
				return Boolean.TRUE.equals(result);
			});
			logger.info("JavaScript condition '{}' met within {} seconds", jsCondition, timeoutSeconds);

			BaseClass.logActionSuccess("JavaScript condition became true", "JavaScript Wait");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for JavaScript condition '{}'", jsCondition, e);
			BaseClass.logActionFailure("Wait for JavaScript condition", "JavaScript Wait", "Timeout waiting for JS condition '" + jsCondition + "' after " + timeoutSeconds + " seconds");
			throw new FrameworkException("Timeout waiting for JS condition: " + jsCondition, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for JavaScript condition", "JavaScript Wait", "Failed to wait for JavaScript condition: " + e.getMessage());
			throw new FrameworkException("Failed to wait for JavaScript condition", e);
		}
	}

	/**
	 * Waits until the number of browser windows/tabs equals the expected count.
	 */
	public void waitForNumberOfWindows(int numberOfWindows, int timeoutSeconds, boolean excelData, String testCaseName) {
	    // Get the actual count to use (from Excel or direct input)
	    final int countToUse;
	    if (excelData) {
	        String countValue = getInputValue(testCaseName, String.valueOf(numberOfWindows), "Expected Window Count", excelData);
	        
	        if (countValue == null || countValue.trim().isEmpty()) {
	            throw new FrameworkException("Window count value from Excel is null or empty for test case: " + testCaseName);
	        }
	        
	        try {
	            countToUse = Integer.parseInt(countValue.trim());
	        } catch (NumberFormatException e) {
	            throw new FrameworkException("Invalid window count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
	        }
	    } else {
	        countToUse = numberOfWindows;
	    }

	    try {
	        BaseClass.logActionStart("Waiting for number of windows/tabs to be " + countToUse + " (timeout: " + timeoutSeconds + "s)", "Window Wait");

	        int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
	        new FluentWait<>(driver)
	                .withTimeout(Duration.ofSeconds(timeoutSeconds))
	                .pollingEvery(Duration.ofMillis(pollingMillis))
	                .until(drv -> drv.getWindowHandles().size() == countToUse);
	        
	        logger.info("Number of windows/tabs reached {} within {} seconds", countToUse, timeoutSeconds);
	        BaseClass.logActionSuccess("Number of windows/tabs reached " + countToUse, "Window Wait");

	    } catch (TimeoutException e) {
	        int currentCount = driver.getWindowHandles().size();
	        logger.error("Timeout waiting for {} windows/tabs. Current count: {}", countToUse, currentCount, e);
	        BaseClass.logActionFailure("Wait for number of windows", "Window Wait", "Timeout waiting for " + countToUse + " windows/tabs after " + timeoutSeconds + " seconds. Current count: " + currentCount);
	        throw new FrameworkException("Timeout waiting for number of windows: " + countToUse + ". Current count: " + currentCount, e);
	    } catch (Exception e) {
	        BaseClass.logActionFailure("Wait for number of windows", "Window Wait", "Failed to wait for number of windows: " + e.getMessage());
	        throw new FrameworkException("Failed to wait for number of windows", e);
	    }
	}


	/** Waits for element to become enabled within timeout. */
	public void waitForElementToBeEnabled(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to be enabled", "Element State Verification");

			getWait(timeoutSeconds).until(driver -> {
				WebElement element = driver.findElement(By.xpath(xpath));
				return element.isEnabled();
			});

			logger.info("Element [{}] became enabled", elementName);
			BaseClass.logActionSuccess("Element '" + elementName + "' became enabled", "Element State Verification");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] to be enabled within {} seconds", elementName, timeoutSeconds);
			BaseClass.logActionFailure("Wait for element to be enabled", "Element State Verification", 
					"Timeout waiting for element '" + elementName + "' to be enabled");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] to be enabled within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to wait for element [{}] to be enabled: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for element to be enabled", "Element State Verification", "Failed to wait for element to be enabled: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element [" + elementName + "] to be enabled", e);
		}
	}

	/** Waits for element to become selected within timeout. */
	public void waitForElementToBeSelected(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Waiting for element '" + elementName + "' to be selected", "Element State Verification");

			getWait(timeoutSeconds).until(driver -> {
				WebElement element = driver.findElement(By.xpath(xpath));
				return element.isSelected();
			});

			logger.info("Element [{}] became selected", elementName);
			BaseClass.logActionSuccess("Element '" + elementName + "' became selected", "Element State Verification");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element [{}] to be selected within {} seconds", elementName, timeoutSeconds);
			BaseClass.logActionFailure("Wait for element to be selected", "Element State Verification", 
					"Timeout waiting for element '" + elementName + "' to be selected");
			throw new FrameworkException("Timeout waiting for element [" + elementName + "] to be selected within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to wait for element [{}] to be selected: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for element to be selected", "Element State Verification", "Failed to wait for element to be selected: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element [" + elementName + "] to be selected", e);
		}
	}

	/** Waits for specific text to be present in element within timeout. */
	public void waitForTextToBePresentInElement(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedText, "Expected Text");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedText, "Expected Text", excelData);

		try {
			BaseClass.logActionStart("Waiting for text '" + BaseClass.mask(valueToUse) + "' to be present in element '" + elementName + "'", "Text Verification");

			getWait(timeoutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(xpath), valueToUse));

			logger.info("Text '{}' appeared in element [{}]", valueToUse, elementName);
			BaseClass.logActionSuccess("Text appeared in element '" + elementName + "'", "Text Verification");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for text '{}' in element [{}] within {} seconds", valueToUse, elementName, timeoutSeconds);
			BaseClass.logActionFailure("Wait for text in element", "Text Verification", "Timeout waiting for text '" + valueToUse + "' in '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for text '" + valueToUse + "' in element [" + elementName + "] within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to wait for text in element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for text in element", "Text Verification", "Failed to wait for text: " + e.getMessage());
			throw new FrameworkException("Failed to wait for text in element [" + elementName + "]", e);
		}
	}

	/** Waits for element attribute to contain specific value within timeout. */
	public void waitForAttributeToContain(String xpath, String attributeName, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(attributeName, "Attribute Name");
		validateInput(expectedValue, "Expected Value");
		validateInput(elementName, "Element Name");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedValue, "Expected Value", excelData);

		try {
			BaseClass.logActionStart("Waiting for attribute '" + attributeName + "' to contain '" + BaseClass.mask(valueToUse) + "' in element '" + elementName + "'", "Attribute Verification");

			getWait(timeoutSeconds).until(ExpectedConditions.attributeContains(By.xpath(xpath), attributeName, valueToUse));

			logger.info("Attribute '{}' contains '{}' in element [{}]", attributeName, valueToUse, elementName);
			BaseClass.logActionSuccess("Attribute '" + attributeName + "' contains expected value in '" + elementName + "'", "Attribute Verification");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for attribute '{}' to contain '{}' in element [{}] within {} seconds", attributeName, valueToUse, elementName, timeoutSeconds);
			BaseClass.logActionFailure("Wait for attribute to contain", "Attribute Verification", "Timeout waiting for attribute '" + attributeName + "' to contain '" + valueToUse + "' in '" + elementName + "'");
			throw new FrameworkException("Timeout waiting for attribute '" + attributeName + "' to contain '" + valueToUse + "' in element [" + elementName + "] within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to wait for attribute in element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for attribute to contain", "Attribute Verification", "Failed to wait for attribute: " + e.getMessage());
			throw new FrameworkException("Failed to wait for attribute in element [" + elementName + "]", e);
		}
	}


	/** Waits for page title to contain specific text within timeout. */
	public void waitForTitleToContain(String expectedFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedFragment, "Expected Fragment");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedFragment, "Expected Fragment", excelData);

		try {
			BaseClass.logActionStart("Waiting for page title to contain '" + BaseClass.mask(valueToUse) + "'", "Page Verification");

			getWait(timeoutSeconds).until(ExpectedConditions.titleContains(valueToUse));

			String actualTitle = driver.getTitle();
			logger.info("Page title '{}' contains expected fragment '{}'", actualTitle, valueToUse);
			BaseClass.logActionSuccess("Page title contains expected fragment", "Page Verification");

		} catch (TimeoutException e) {
			String actualTitle = driver.getTitle();
			logger.error("Timeout waiting for page title to contain '{}' within {} seconds. Current title: '{}'", valueToUse, timeoutSeconds, actualTitle);
			BaseClass.logActionFailure("Wait for title to contain", "Page Verification", "Timeout waiting for title to contain '" + valueToUse + "'. Current title: '" + actualTitle + "'");
			throw new FrameworkException("Timeout waiting for page title to contain '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current title: '" + actualTitle + "'", e);
		} catch (Exception e) {
			logger.error("Failed to wait for page title to contain '{}': {}", valueToUse, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for title to contain", "Page Verification", "Failed to wait for title: " + e.getMessage());
			throw new FrameworkException("Failed to wait for page title to contain '" + valueToUse + "'", e);
		}
	}


	/** Waits for current URL to contain specific text within timeout. */
	public void waitForUrlToContain(String expectedFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedFragment, "Expected Fragment");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedFragment, "Expected Fragment", excelData);

		try {
			BaseClass.logActionStart("Waiting for URL to contain '" + BaseClass.mask(valueToUse) + "'", "URL Verification");

			getWait(timeoutSeconds).until(ExpectedConditions.urlContains(valueToUse));

			String actualUrl = driver.getCurrentUrl();
			logger.info("URL '{}' contains expected fragment '{}'", actualUrl, valueToUse);
			BaseClass.logActionSuccess("URL contains expected fragment", "URL Verification");

		} catch (TimeoutException e) {
			String actualUrl = driver.getCurrentUrl();
			logger.error("Timeout waiting for URL to contain '{}' within {} seconds. Current URL: '{}'", valueToUse, timeoutSeconds, actualUrl);
			BaseClass.logActionFailure("Wait for URL to contain", "URL Verification", "Timeout waiting for URL to contain '" + valueToUse + "'. Current URL: '" + actualUrl + "'");
			throw new FrameworkException("Timeout waiting for URL to contain '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current URL: '" + actualUrl + "'", e);
		} catch (Exception e) {
			logger.error("Failed to wait for URL to contain '{}': {}", valueToUse, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for URL to contain", "URL Verification", "Failed to wait for URL: " + e.getMessage());
			throw new FrameworkException("Failed to wait for URL to contain '" + valueToUse + "'", e);
		}
	}


	/** Waits for specified number of seconds (hard wait). */
	public void waitForSeconds(int seconds) {
		if (seconds <= 0) {
			throw new FrameworkException("Wait seconds must be positive, provided: " + seconds);
		}

		try {
			BaseClass.logActionStart("Waiting for " + seconds + " seconds", "Wait Operation");

			Thread.sleep(seconds * 1000L);

			logger.info("Waited for {} seconds", seconds);
			BaseClass.logActionSuccess("Waited for " + seconds + " seconds", "Wait Operation");

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error("Wait interrupted after {} seconds", seconds, e);
			BaseClass.logActionFailure("Wait for seconds", "Wait Operation", "Wait interrupted: " + e.getMessage());
			throw new FrameworkException("Wait for " + seconds + " seconds was interrupted", e);
		} catch (Exception e) {
			logger.error("Failed to wait for {} seconds: {}", seconds, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for seconds", "Wait Operation", "Failed to wait: " + e.getMessage());
			throw new FrameworkException("Failed to wait for " + seconds + " seconds", e);
		}
	}

	/** Waits for specified number of milliseconds (hard wait). */
	public void waitForMilliseconds(int milliseconds) {
		if (milliseconds <= 0) {
			throw new FrameworkException("Wait milliseconds must be positive, provided: " + milliseconds);
		}

		try {
			BaseClass.logActionStart("Waiting for " + milliseconds + " milliseconds", "Wait Operation");

			Thread.sleep(milliseconds);

			logger.info("Waited for {} milliseconds", milliseconds);
			BaseClass.logActionSuccess("Waited for " + milliseconds + " milliseconds", "Wait Operation");

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error("Wait interrupted after {} milliseconds", milliseconds, e);
			BaseClass.logActionFailure("Wait for milliseconds", "Wait Operation", "Wait interrupted: " + e.getMessage());
			throw new FrameworkException("Wait for " + milliseconds + " milliseconds was interrupted", e);
		} catch (Exception e) {
			logger.error("Failed to wait for {} milliseconds: {}", milliseconds, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for milliseconds", "Wait Operation", "Failed to wait: " + e.getMessage());
			throw new FrameworkException("Failed to wait for " + milliseconds + " milliseconds", e);
		}
	}

	/** Waits for custom condition using JavaScript expression within timeout. */
	public void waitForCustomCondition(String jsCondition, int timeoutSeconds) {
		validateInput(jsCondition, "JavaScript Condition");

		try {
			BaseClass.logActionStart("Waiting for custom condition: " + jsCondition, "Custom Verification");

			getWait(timeoutSeconds).until(driver -> {
				try {
					Object result = ((JavascriptExecutor) driver).executeScript("return " + jsCondition);
					return result != null && result.equals(true);
				} catch (Exception e) {
					logger.debug("Custom condition evaluation failed: {}", e.getMessage());
					return false;
				}
			});

			logger.info("Custom condition met: {}", jsCondition);
			BaseClass.logActionSuccess("Custom condition met", "Custom Verification");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for custom condition '{}' within {} seconds", jsCondition, timeoutSeconds);
			BaseClass.logActionFailure("Wait for custom condition", "Custom Verification", "Timeout waiting for condition '" + jsCondition + "'");
			throw new FrameworkException("Timeout waiting for custom condition '" + jsCondition + "' within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to wait for custom condition '{}': {}", jsCondition, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for custom condition", "Custom Verification", "Failed to wait for condition: " + e.getMessage());
			throw new FrameworkException("Failed to wait for custom condition '" + jsCondition + "'", e);
		}
	}

	/** Waits for specific number of elements matching XPath within timeout. */
	public void waitForElementCount(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		// Get the actual count to use (from Excel or direct input)
		final int countToUse;
		if (excelData) {
			String countValue = getInputValue(testCaseName, String.valueOf(expectedCount), elementName + " Count", excelData);

			if (countValue == null || countValue.trim().isEmpty()) {
				throw new FrameworkException("Count value from Excel is null or empty for test case: " + testCaseName);
			}

			try {
				countToUse = Integer.parseInt(countValue.trim());
			} catch (NumberFormatException e) {
				throw new FrameworkException("Invalid count value from Excel: '" + countValue + "' for test case: " + testCaseName + ". Expected a valid integer.", e);
			}
		} else {
			countToUse = expectedCount;
		}

		try {
			BaseClass.logActionStart("Waiting for element count of '" + elementName + "' to be " + countToUse, "Count Verification");

			getWait(timeoutSeconds).until(driver -> {
				List<WebElement> elements = driver.findElements(By.xpath(xpath));
				return elements.size() == countToUse;
			});

			logger.info("Element count of [{}] reached expected count {}", elementName, countToUse);
			BaseClass.logActionSuccess("Element count reached " + countToUse, "Count Verification");

		} catch (TimeoutException e) {
			List<WebElement> elements = driver.findElements(By.xpath(xpath));
			int actualCount = elements.size();
			logger.error("Timeout waiting for element count of [{}] to be {}. Current count: {}", elementName, countToUse, actualCount);
			BaseClass.logActionFailure("Wait for element count", "Count Verification", "Timeout waiting for count " + countToUse + ". Current count: " + actualCount);
			throw new FrameworkException("Timeout waiting for element count of [" + elementName + "] to be " + countToUse + ". Current count: " + actualCount, e);
		} catch (Exception e) {
			logger.error("Failed to wait for element count of [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Wait for element count", "Count Verification", "Failed to wait for count: " + e.getMessage());
			throw new FrameworkException("Failed to wait for element count of [" + elementName + "]", e);
		}
	}



}
