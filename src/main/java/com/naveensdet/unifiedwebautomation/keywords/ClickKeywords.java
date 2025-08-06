package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ClickKeywords {

	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(ClickKeywords.class);

	// Polling interval constant retained for wait
	private static final int DEFAULT_POLLING_INTERVAL_MS = 500;

	public ClickKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/**
	 * Get integer configuration property dynamically from BaseClass props.
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

	/** Returns configured FluentWait for given timeout with polling interval. */
	private FluentWait<WebDriver> getWait(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(pollingMillis))
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	// --- Validation methods ---

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

	private void validateInput(String xpath1, String xpath2, String elementName) {
		if (xpath1 == null || xpath1.trim().isEmpty()) {
			throw new FrameworkException("First XPath cannot be null or empty");
		}
		if (xpath2 == null || xpath2.trim().isEmpty()) {
			throw new FrameworkException("Second XPath cannot be null or empty");
		}
		if (elementName == null || elementName.trim().isEmpty()) {
			throw new FrameworkException("Element name cannot be null or empty");
		}
	}

	/** Converts locator string to By */
	private By parseLocator(String locator) {
		if (locator == null || locator.trim().isEmpty()) {
			throw new FrameworkException("Locator cannot be null or empty");
		}
		return By.xpath(locator.trim());
	}

	/** 
	 * Wait for element to be visible and present up to timeoutSeconds with polling interval.
	 */
	private WebElement findElementWithWait(String xpath, int timeoutSeconds) {
		validateInput(xpath);
		try {
			int polling = getIntConfigProperty("fluentWaitPolling", DEFAULT_POLLING_INTERVAL_MS);
			return BaseClass.fluentWait(parseLocator(xpath), timeoutSeconds, polling);
		} catch (Exception e) {
			throw new FrameworkException("Element not found or not visible after waiting for " 
					+ timeoutSeconds + " seconds. XPath: " + xpath, e);
		}
	}

	/** Wait for elements list to be present and returns the list. */
	private List<WebElement> findElementsWithWait(String xpath, int timeoutSeconds) {
		validateInput(xpath);
		int polling = getIntConfigProperty("fluentWaitPolling", DEFAULT_POLLING_INTERVAL_MS);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
		wait.pollingEvery(Duration.ofMillis(polling));
		wait.until(drv -> !drv.findElements(parseLocator(xpath)).isEmpty());
		return driver.findElements(parseLocator(xpath));
	}

	// --- Core click methods with explicit timeout ---

	/**
	 * Clicks the element located by xpath, waits up to timeoutSeconds.
	 */
	public void clickElement(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, timeoutSeconds);
			element.click();
			logger.info("Clicked element [{}] after waiting up to {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Clicked element", elementName);

		} catch (Exception e) {
			logger.error("Failed to click element [{}] after waiting", elementName, e);
			BaseClass.logActionFailure("Click element", elementName, "Failed to click after waiting " + timeoutSeconds + " seconds: " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] after waiting " + timeoutSeconds + " seconds", e);
		}
	}

	/**
	 * Clicks the element located by xpath using default timeout.
	 */
	public void clickElement(String xpath, String elementName) {
		int timeout = getIntConfigProperty("fluentWaitTimeout", 10);
		clickElement(xpath, elementName, timeout);
	}

	/**
	 * Double clicks element after waiting up to timeoutSeconds.
	 */
	public void doubleClickElement(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Double clicking element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, timeoutSeconds);
			new Actions(driver).doubleClick(element).perform();
			logger.info("Double clicked element [{}]", elementName);

			BaseClass.logActionSuccess("Double clicked element", elementName);

		} catch (Exception e) {
			logger.error("Failed to double click element [{}]", elementName, e);
			BaseClass.logActionFailure("Double click element", elementName, "Failed to double click after waiting " + timeoutSeconds + " seconds: " + e.getMessage());
			throw new FrameworkException("Failed to double click element [" + elementName + "] after waiting " + timeoutSeconds + " seconds", e);
		}
	}

	/**
	 * Double clicks element with default timeout.
	 */
	public void doubleClickElement(String xpath, String elementName) {
		int timeout = getIntConfigProperty("fluentWaitTimeout", 10);
		doubleClickElement(xpath, elementName, timeout);
	}

	/**
	 * Click element using JavaScript.
	 */
	public void clickElementWithJS(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "' using JavaScript", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			logger.info("Clicked element [{}] using JavaScript", elementName);

			BaseClass.logActionSuccess("Clicked element using JavaScript", elementName);

		} catch (Exception e) {
			logger.error("Failed to JS-click element [{}]", elementName, e);
			BaseClass.logActionFailure("Click element with JavaScript", elementName, "Failed to JS-click: " + e.getMessage());
			throw new FrameworkException("Failed to JS-click element [" + elementName + "]", e);
		}
	}

	/**
	 * Right-click (context click) element with specified timeout.
	 */
	public void rightClickElement(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Right clicking element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, timeoutSeconds);
			new Actions(driver).contextClick(element).perform();
			logger.info("Right clicked element [{}]", elementName);

			BaseClass.logActionSuccess("Right clicked element", elementName);

		} catch (Exception e) {
			logger.error("Failed to right click element [{}]", elementName, e);
			BaseClass.logActionFailure("Right click element", elementName, "Failed to right click: " + e.getMessage());
			throw new FrameworkException("Failed to right click element [" + elementName + "]", e);
		}
	}


	/**
	 * Click element using Actions click.
	 */
	public void actionClickElement(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "' using Actions", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			new Actions(driver).click(element).perform();
			logger.info("Clicked element [{}] using Actions", elementName);

			BaseClass.logActionSuccess("Clicked element using Actions", elementName);

		} catch (Exception e) {
			logger.error("Failed to action-click element [{}]", elementName, e);
			BaseClass.logActionFailure("Click element using Actions", elementName, "Failed to action-click: " + e.getMessage());
			throw new FrameworkException("Failed to action-click element [" + elementName + "]", e);
		}
	}

	/**
	 * Click and hold element.
	 */
	public void clickAndHoldElement(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking and holding element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			new Actions(driver).clickAndHold(element).perform();
			logger.info("Clicked and held element [{}]", elementName);

			BaseClass.logActionSuccess("Clicked and held element", elementName);

		} catch (Exception e) {
			logger.error("Failed to click and hold element [{}]", elementName, e);
			BaseClass.logActionFailure("Click and hold element", elementName, "Failed to click and hold: " + e.getMessage());
			throw new FrameworkException("Failed to click and hold element [" + elementName + "]", e);
		}
	}

	/**
	 * Release click on element.
	 */
	public void releaseElement(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Releasing element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			new Actions(driver).release(element).perform();
			logger.info("Released element [{}]", elementName);

			BaseClass.logActionSuccess("Released element", elementName);

		} catch (Exception e) {
			logger.error("Failed to release element [{}]", elementName, e);
			BaseClass.logActionFailure("Release element", elementName, "Failed to release: " + e.getMessage());
			throw new FrameworkException("Failed to release element [" + elementName + "]", e);
		}
	}

	/**
	 * Move to element and click.
	 */
	public void moveToAndClickElement(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Moving to and clicking element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			new Actions(driver).moveToElement(element).click().perform();
			logger.info("Moved to and clicked element [{}]", elementName);

			BaseClass.logActionSuccess("Moved to and clicked element", elementName);

		} catch (Exception e) {
			logger.error("Failed to move and click element [{}]", elementName, e);
			BaseClass.logActionFailure("Move to and click element", elementName, "Failed to move and click: " + e.getMessage());
			throw new FrameworkException("Failed to move to and click element [" + elementName + "]", e);
		}
	}

	/**
	 * Click element at offset.
	 */
	public void clickAtCoordinates(String xpath, String elementName, int xOffset, int yOffset) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "' at coordinates (" + xOffset + "," + yOffset + ")", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			new Actions(driver).moveToElement(element, xOffset, yOffset).click().perform();
			logger.info("Clicked element [{}] at offset ({}, {})", elementName, xOffset, yOffset);

			BaseClass.logActionSuccess("Clicked element at coordinates (" + xOffset + "," + yOffset + ")", elementName);

		} catch (Exception e) {
			logger.error("Failed to click element [{}] at coordinates offset ({}, {})", elementName, xOffset, yOffset, e);
			BaseClass.logActionFailure("Click element at coordinates", elementName, "Failed to click at offset (" + xOffset + "," + yOffset + "): " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] at coordinates offset (" + xOffset + ", " + yOffset + ")", e);
		}
	}

	/**
	 * Click if element is visible.
	 */
	public void clickIfVisible(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Checking visibility and clicking element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			if (element.isDisplayed()) {
				element.click();
				logger.info("Clicked visible element [{}]", elementName);
				BaseClass.logActionSuccess("Clicked visible element", elementName);
			} else {
				logger.warn("Element [{}] is not visible; skipping click", elementName);
				BaseClass.logActionSuccess("Element not visible, skipped clicking", elementName);
			}

		} catch (Exception e) {
			logger.error("Failed to click visible element [{}]", elementName, e);
			BaseClass.logActionFailure("Click if visible", elementName, "Failed to check visibility or click: " + e.getMessage());
			throw new FrameworkException("Failed to click visible element [" + elementName + "]", e);
		}
	}

	/**
	 * Click if visible or fail.
	 */
	public void clickIfVisibleOrFail(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Checking visibility and clicking element '" + elementName + "' (fail if not visible)", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			if (element.isDisplayed()) {
				element.click();
				logger.info("Clicked visible element [{}]", elementName);
				BaseClass.logActionSuccess("Clicked visible element", elementName);
			} else {
				String errorMsg = "Element [" + elementName + "] is not visible; failing the step.";
				logger.error(errorMsg);
				BaseClass.logActionFailure("Click if visible or fail", elementName, "Element is not visible");
				throw new FrameworkException(errorMsg);
			}

		} catch (Exception e) {
			logger.error("Failed to click visible element [{}]", elementName, e);
			BaseClass.logActionFailure("Click if visible or fail", elementName, e.getMessage());
			throw new FrameworkException("Failed to click visible element [" + elementName + "]", e);
		}
	}

	/**
	 * Click if enabled.
	 */
	public void clickIfEnabled(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Checking if enabled and clicking element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			if (element.isEnabled()) {
				element.click();
				logger.info("Clicked enabled element [{}]", elementName);
				BaseClass.logActionSuccess("Clicked enabled element", elementName);
			} else {
				logger.warn("Element [{}] is not enabled; skipping click", elementName);
				BaseClass.logActionSuccess("Element not enabled, skipped clicking", elementName);
			}

		} catch (Exception e) {
			logger.error("Failed to click enabled element [{}]", elementName, e);
			BaseClass.logActionFailure("Click if enabled", elementName, "Failed to check enabled state or click: " + e.getMessage());
			throw new FrameworkException("Failed to click enabled element [" + elementName + "]", e);
		}
	}

	/**
	 * Click if enabled or fail.
	 */
	public void clickIfEnabledOrFail(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Checking if enabled and clicking element '" + elementName + "' (fail if not enabled)", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			if (element.isEnabled()) {
				element.click();
				logger.info("Clicked enabled element [{}]", elementName);
				BaseClass.logActionSuccess("Clicked enabled element", elementName);
			} else {
				String errorMsg = "Element [" + elementName + "] is not enabled; failing the step.";
				logger.error(errorMsg);
				BaseClass.logActionFailure("Click if enabled or fail", elementName, "Element is not enabled");
				throw new FrameworkException(errorMsg);
			}

		} catch (Exception e) {
			logger.error("Failed to click enabled element [{}]", elementName, e);
			BaseClass.logActionFailure("Click if enabled or fail", elementName, e.getMessage());
			throw new FrameworkException("Failed to click enabled element [" + elementName + "]", e);
		}
	}

	/**
	 * Click multiple elements matching XPath.
	 */
	public void clickMultipleElements(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking multiple elements matching '" + elementName + "'", elementName);

			List<WebElement> elements = findElementsWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			if (elements.isEmpty()) {
				logger.warn("No elements found to click for [{}]", elementName);
				BaseClass.logActionSuccess("No elements found to click", elementName);
				return;
			}

			for (int i = 0; i < elements.size(); i++) {
				elements.get(i).click();
				logger.info("Clicked element [{}] instance #{}", elementName, i + 1);
			}

			BaseClass.logActionSuccess("Clicked " + elements.size() + " elements", elementName);

		} catch (Exception e) {
			logger.error("Failed to click multiple elements [{}]", elementName, e);
			BaseClass.logActionFailure("Click multiple elements", elementName, "Failed to click multiple elements: " + e.getMessage());
			throw new FrameworkException("Failed to click multiple elements [" + elementName + "]", e);
		}
	}

	/**
	 * Click multiple elements or fail if none found.
	 */
	public void clickMultipleElementsOrFail(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking multiple elements matching '" + elementName + "' (fail if none found)", elementName);

			List<WebElement> elements = findElementsWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			if (elements.isEmpty()) {
				String errorMsg = "No elements found to click for [" + elementName + "]";
				logger.error(errorMsg);
				BaseClass.logActionFailure("Click multiple elements or fail", elementName, "No elements found");
				throw new FrameworkException(errorMsg);
			}

			for (int i = 0; i < elements.size(); i++) {
				elements.get(i).click();
				logger.info("Clicked element [{}] instance #{}", elementName, i + 1);
			}

			BaseClass.logActionSuccess("Clicked " + elements.size() + " elements", elementName);

		} catch (Exception e) {
			logger.error("Failed to click multiple elements [{}]", elementName, e);
			BaseClass.logActionFailure("Click multiple elements or fail", elementName, e.getMessage());
			throw new FrameworkException("Failed to click multiple elements [" + elementName + "]", e);
		}
	}

	/** 
	 * Waits for element visible and clickable then clicks, logs action with timeout.
	 */
	public void clickWithWait(String xpath, String elementName, int waitSeconds) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "' after waiting " + waitSeconds + " seconds", elementName);

			BaseClass.fluentWait(parseLocator(xpath), waitSeconds, DEFAULT_POLLING_INTERVAL_MS).click();
			logger.info("Clicked element [{}] after waiting {} seconds", elementName, waitSeconds);

			BaseClass.logActionSuccess("Clicked element after waiting " + waitSeconds + " seconds", elementName);

		} catch (Exception e) {
			logger.error("Failed to click element [{}] after waiting {} seconds", elementName, waitSeconds, e);
			BaseClass.logActionFailure("Click with wait", elementName, "Failed to click after waiting " + waitSeconds + " seconds: " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] after waiting " + waitSeconds + " seconds", e);
		}
	}

	/**
	 * Scrolls element into view and clicks.
	 */
	public void clickAfterScrollIntoView(String xpath, String elementName) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Scrolling into view and clicking element '" + elementName + "'", elementName);

			WebElement element = findElementWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			element.click();
			logger.info("Scrolled into view and clicked element [{}]", elementName);

			BaseClass.logActionSuccess("Scrolled into view and clicked element", elementName);

		} catch (Exception e) {
			logger.error("Failed to scroll and click element [{}]", elementName, e);
			BaseClass.logActionFailure("Scroll into view and click", elementName, "Failed to scroll and click: " + e.getMessage());
			throw new FrameworkException("Failed to scroll and click element [" + elementName + "]", e);
		}
	}

	/**
	 * Click when element is visible within timeout.
	 */
	public void clickWhenVisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Waiting for visibility and clicking element '" + elementName + "'", elementName);

			WebElement element = BaseClass.fluentWait(parseLocator(xpath), timeoutSeconds, DEFAULT_POLLING_INTERVAL_MS);
			element.click();
			logger.info("Clicked element [{}] after waiting for visibility for {} seconds", elementName, timeoutSeconds);

			BaseClass.logActionSuccess("Clicked element after waiting for visibility", elementName);

		} catch (Exception e) {
			logger.error("Failed to click element [{}] after waiting for visibility", elementName, e);
			BaseClass.logActionFailure("Click when visible", elementName, "Failed to click after waiting for visibility: " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] after waiting for visibility", e);
		}
	}

	/**
	 * Click element by replacing placeholder text in the XPath.
	 */
	public void clickByReplacingText(String xpath, String elementName, String dynamicText) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "' with dynamic text '" + dynamicText + "'", elementName);

			String replacedXpath = xpath.replace("%replace%", dynamicText);
			WebElement element = findElementWithWait(replacedXpath, getIntConfigProperty("fluentWaitTimeout", 10));
			element.click();
			logger.info("Clicked element [{}] with replaced text [{}]", elementName, dynamicText);

			BaseClass.logActionSuccess("Clicked element with dynamic text '" + dynamicText + "'", elementName);

		} catch (Exception e) {
			logger.error("Failed to click element [{}] with replaced text [{}]", elementName, dynamicText, e);
			BaseClass.logActionFailure("Click by replacing text", elementName, "Failed to click with dynamic text '" + dynamicText + "': " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] with replaced text [" + dynamicText + "]", e);
		}
	}

	/**
	 * Finds child element inside parent element and clicks it.
	 */
	private WebElement findChildElementWithWait(WebElement parent, String childXpath, int timeoutSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
			By childLocator = parseLocator(childXpath);
			List<WebElement> childs = wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(parent, childLocator));
			if (childs.isEmpty()) {
				throw new FrameworkException("No child elements found with XPath [" + childXpath + "]");
			}
			return childs.get(0);
		} catch (Exception e) {
			throw new FrameworkException("Child element [" + childXpath + "] not visible inside parent after waiting " + timeoutSeconds + " seconds", e);
		}
	}

	public void clickOnChildElement(String parentXpath, String childXpath, String elementName) {
		validateInput(parentXpath, childXpath, elementName);
		try {
			BaseClass.logActionStart("Clicking child element '" + elementName + "' inside parent", elementName);

			WebElement parent = findElementWithWait(parentXpath, getIntConfigProperty("fluentWaitTimeout", 10));
			WebElement child = findChildElementWithWait(parent, childXpath, getIntConfigProperty("fluentWaitTimeout", 10));
			child.click();
			logger.info("Clicked child element [{}]", elementName);

			BaseClass.logActionSuccess("Clicked child element inside parent", elementName);

		} catch (Exception e) {
			logger.error("Failed to click child element [{}]", elementName, e);
			BaseClass.logActionFailure("Click child element", elementName, "Failed to click child element: " + e.getMessage());
			throw new FrameworkException("Failed to click child element [" + elementName + "]", e);
		}
	}

	/**
	 * Clicks the element at the specified index from the list of elements located by the XPath.
	 */
	public void clickByIndex(String xpath, String elementName, int index) {
		validateInput(xpath, elementName);
		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "' at index " + index, elementName);

			List<WebElement> elements = findElementsWithWait(xpath, getIntConfigProperty("fluentWaitTimeout", 10));
			if (index < 0 || index >= elements.size()) {
				BaseClass.logActionFailure("Click by index", elementName, "Index " + index + " is out of bounds for elements list size " + elements.size());
				throw new IndexOutOfBoundsException("Index " + index + " is out of range");
			}

			elements.get(index).click();
			logger.info("Clicked element [{}] at index {}", elementName, index);

			BaseClass.logActionSuccess("Clicked element at index " + index, elementName);

		} catch (IndexOutOfBoundsException e) {
			String errorMsg = "Index " + index + " is out of bounds for elements list size.";
			logger.error(errorMsg + " Element: [{}]", elementName, e);
			BaseClass.logActionFailure("Click by index", elementName, errorMsg);
			throw new FrameworkException(errorMsg + " Element: " + elementName, e);
		} catch (Exception e) {
			logger.error("Failed to click element [{}] at index {}", elementName, index, e);
			BaseClass.logActionFailure("Click by index", elementName, "Failed to click at index " + index + ": " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] at index " + index, e);
		}
	}

	/**
	 * Clicks element using JavaScript executor when standard click fails.
	 */
	public void clickElementUsingJS(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		try {
			BaseClass.logActionStart("Clicking element using JavaScript '" + elementName + "'", elementName);

			WebElement element = getWait(timeoutSeconds).until(
					ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
					);

			// Execute JavaScript click
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			jsExecutor.executeScript("arguments[0].click();", element);

			logger.info("Clicked element using JavaScript [{}]", elementName);
			BaseClass.logActionSuccess("Clicked element using JavaScript", elementName);

		} catch (TimeoutException e) {
			logger.error("Element not found for JS click [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Click element using JavaScript", elementName, "Element not found: " + e.getMessage());
			throw new FrameworkException("Element not found for JavaScript click [" + elementName + "]", e);
		} catch (Exception e) {
			logger.error("Failed to click element using JavaScript [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Click element using JavaScript", elementName, "JavaScript click failed: " + e.getMessage());
			throw new FrameworkException("Failed to click element using JavaScript [" + elementName + "]", e);
		}
	}

	/**
	 * Clicks element with retry mechanism for handling flaky element interactions.
	 */
	public void clickElementWithRetry(String xpath, String elementName, int maxRetries, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		if (maxRetries < 1) {
			throw new FrameworkException("Maximum retries must be at least 1");
		}

		Exception lastException = null;

		try {
			BaseClass.logActionStart("Clicking element with retry '" + elementName + "' (max retries: " + maxRetries + ")", elementName);

			for (int attempt = 1; attempt <= maxRetries; attempt++) {
				try {
					WebElement element = getWait(timeoutSeconds).until(
							ExpectedConditions.elementToBeClickable(By.xpath(xpath))
							);

					element.click();

					logger.info("Successfully clicked element [{}] on attempt {}/{}", elementName, attempt, maxRetries);
					BaseClass.logActionSuccess("Clicked element with retry", elementName + " (attempt " + attempt + "/" + maxRetries + ")");
					return; // Success - exit the method

				} catch (Exception e) {
					lastException = e;
					logger.warn("Attempt {}/{} failed to click element [{}]: {}", attempt, maxRetries, elementName, e.getMessage());

					if (attempt < maxRetries) {
						// Wait a bit before retrying (progressive delay)
						try {
							Thread.sleep(500 * attempt); // 500ms, 1000ms, 1500ms, etc.
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
							throw new FrameworkException("Thread interrupted during retry delay", ie);
						}
						logger.info("Retrying click on element [{}] - attempt {}/{}", elementName, attempt + 1, maxRetries);
					}
				}
			}

			// All attempts failed
			logger.error("All {} attempts failed to click element [{}]", maxRetries, elementName);
			BaseClass.logActionFailure("Click element with retry", elementName, 
					"All " + maxRetries + " attempts failed. Last error: " + 
							(lastException != null ? lastException.getMessage() : "Unknown error"));

			throw new FrameworkException("Failed to click element [" + elementName + "] after " + maxRetries + " attempts", lastException);

		} catch (FrameworkException e) {
			// Re-throw framework exceptions as-is
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error during retry click on element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Click element with retry", elementName, "Unexpected error: " + e.getMessage());
			throw new FrameworkException("Unexpected error during retry click on element [" + elementName + "]", e);
		}
	}

	/**
	 * Clicks element only if it is visible within the specified timeout, otherwise skips without error.
	 */
	public void clickElementIfVisible(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Checking visibility and clicking element '" + elementName + "'", elementName);

			try {
				// Check if element is visible within timeout
				WebElement element = getWait(timeoutSeconds).until(
						ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))
						);

				// Element is visible, now check if it's clickable
				WebElement clickableElement = getWait(2).until(
						ExpectedConditions.elementToBeClickable(element)
						);

				clickableElement.click();

				logger.info("Element [{}] was visible and clicked successfully", elementName);
				BaseClass.logActionSuccess("Clicked visible element", elementName);

			} catch (TimeoutException e) {
				// Element is not visible within timeout - this is not an error for this method
				logger.info("Element [{}] was not visible within {} seconds, skipping click", elementName, timeoutSeconds);
				BaseClass.logActionSuccess("Element not visible - skipped click", elementName + " (not visible within " + timeoutSeconds + "s)");
			}

		} catch (Exception e) {
			logger.error("Unexpected error while checking visibility and clicking element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Click element if visible", elementName, "Unexpected error: " + e.getMessage());
			throw new FrameworkException("Unexpected error while checking visibility and clicking element [" + elementName + "]", e);
		}
	}

	/**
	 * Force clicks element using Actions class to bypass element interception and overlay issues.
	 */
	public void forceClickElement(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Force clicking element '" + elementName + "'", elementName);

			WebElement element = getWait(timeoutSeconds).until(
					ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
					);

			// Scroll to element first to ensure it's in viewport
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);

			// Wait a moment for scroll to complete
			try {
				Thread.sleep(200);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}

			// Force click using Actions class
			Actions actions = new Actions(driver);
			actions.moveToElement(element).click().perform();

			logger.info("Force clicked element [{}] using Actions class", elementName);
			BaseClass.logActionSuccess("Force clicked element", elementName);

		} catch (TimeoutException e) {
			logger.error("Element not found for force click [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Force click element", elementName, "Element not found: " + e.getMessage());
			throw new FrameworkException("Element not found for force click [" + elementName + "]", e);
		} catch (Exception e) {
			logger.error("Failed to force click element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Force click element", elementName, "Force click failed: " + e.getMessage());
			throw new FrameworkException("Failed to force click element [" + elementName + "]", e);
		}
	}

	/**
	 * Clicks element at specific coordinates with offset from element's top-left corner.
	 */
	public void clickElementAtCoordinates(String xpath, String elementName, int offsetX, int offsetY, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Clicking element '" + elementName + "' at coordinates (" + offsetX + ", " + offsetY + ")", elementName);

			WebElement element = getWait(timeoutSeconds).until(
					ExpectedConditions.elementToBeClickable(By.xpath(xpath))
					);

			// Scroll element into view first
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);

			// Brief pause to ensure scroll completion
			try {
				Thread.sleep(200);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}

			// Click at specific coordinates using Actions
			Actions actions = new Actions(driver);
			actions.moveToElement(element, offsetX, offsetY).click().perform();

			logger.info("Clicked element [{}] at coordinates ({}, {})", elementName, offsetX, offsetY);
			BaseClass.logActionSuccess("Clicked element at coordinates", elementName + " at (" + offsetX + ", " + offsetY + ")");

		} catch (TimeoutException e) {
			logger.error("Element not found for coordinate click [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Click element at coordinates", elementName, "Element not found: " + e.getMessage());
			throw new FrameworkException("Element not found for coordinate click [" + elementName + "]", e);
		} catch (Exception e) {
			logger.error("Failed to click element [{}] at coordinates ({}, {}): {}", elementName, offsetX, offsetY, e.getMessage(), e);
			BaseClass.logActionFailure("Click element at coordinates", elementName, "Coordinate click failed: " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] at coordinates (" + offsetX + ", " + offsetY + ")", e);
		}
	}

	/**
	 * Verifies element is clickable before clicking to ensure reliable interaction.
	 */
	public void verifyAndClickElement(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Verifying and clicking element '" + elementName + "'", elementName);

			// First verify element is present and clickable
			WebElement element = getWait(timeoutSeconds).until(
					ExpectedConditions.elementToBeClickable(By.xpath(xpath))
					);

			// Additional verification - check if element is displayed and enabled
			if (!element.isDisplayed()) {
				throw new FrameworkException("Element [" + elementName + "] is present but not displayed");
			}

			if (!element.isEnabled()) {
				throw new FrameworkException("Element [" + elementName + "] is displayed but not enabled");
			}

			// Element passed all verifications, now click it
			element.click();

			logger.info("Verified and clicked element [{}] successfully", elementName);
			BaseClass.logActionSuccess("Verified and clicked element", elementName);

		} catch (TimeoutException e) {
			logger.error("Element verification failed - not clickable within timeout [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Verify and click element", elementName, "Element not clickable within timeout: " + e.getMessage());
			throw new FrameworkException("Element [" + elementName + "] is not clickable within " + timeoutSeconds + " seconds", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions (from our custom validations)
			logger.error("Element verification failed [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Verify and click element", elementName, e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Failed to verify and click element [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Verify and click element", elementName, "Click failed after verification: " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] after verification", e);
		}
	}

	/**
	 * Soft clicks element and returns true if successful, false otherwise without throwing exceptions.
	 */
	public boolean softClickElement(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Soft clicking element '" + elementName + "'", elementName);

			WebElement element = getWait(timeoutSeconds).until(
					ExpectedConditions.elementToBeClickable(By.xpath(xpath))
					);

			element.click();

			logger.info("Soft clicked element [{}] successfully", elementName);
			BaseClass.logActionSuccess("Soft clicked element", elementName);
			return true;

		} catch (TimeoutException e) {
			logger.warn("Soft click failed - element not clickable within timeout [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionSuccess("Soft click failed - element not clickable", elementName + " (timeout: " + timeoutSeconds + "s)");
			return false;
		} catch (Exception e) {
			logger.warn("Soft click failed for element [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionSuccess("Soft click failed - " + e.getMessage(), elementName);
			return false;
		}
	}

	/**
	 * Clicks element and waits for page to load completely before continuing.
	 */
	@SuppressWarnings("unused")
	public void clickElementAndWaitForPageLoad(String xpath, String elementName, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");

		try {
			BaseClass.logActionStart("Clicking element and waiting for page load '" + elementName + "'", elementName);

			WebElement element = getWait(timeoutSeconds).until(
					ExpectedConditions.elementToBeClickable(By.xpath(xpath))
					);

			// Record current URL to detect navigation
			String currentUrl = driver.getCurrentUrl();

			// Click the element
			element.click();

			logger.info("Clicked element [{}], now waiting for page load", elementName);

			// Wait for page load completion
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

			// Wait for document ready state to be complete
			wait.until(webDriver -> 
			((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")
					);

			// Additional wait for jQuery if present
			wait.until(webDriver -> {
				Object result = ((JavascriptExecutor) webDriver).executeScript(
						"return typeof jQuery !== 'undefined' ? jQuery.active == 0 : true"
						);
				return result.equals(true);
			});

			// Wait for Angular if present
			wait.until(webDriver -> {
				Object result = ((JavascriptExecutor) webDriver).executeScript(
						"return typeof angular !== 'undefined' ? angular.element(document).injector().get('$http').pendingRequests.length === 0 : true"
						);
				return result.equals(true);
			});

			logger.info("Page load completed after clicking element [{}]", elementName);
			BaseClass.logActionSuccess("Clicked element and page loaded", elementName);

		} catch (TimeoutException e) {
			logger.error("Timeout while clicking element or waiting for page load [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Click element and wait for page load", elementName, "Timeout during click or page load: " + e.getMessage());
			throw new FrameworkException("Timeout while clicking element [" + elementName + "] or waiting for page load", e);
		} catch (Exception e) {
			logger.error("Failed to click element and wait for page load [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Click element and wait for page load", elementName, "Click or page load failed: " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] and wait for page load", e);
		}
	}

	/**
	 * Clicks multiple elements in sequence using provided XPaths and element names.
	 */
	public void clickMultipleElements(List<String> xpaths, List<String> elementNames, int timeoutSeconds) {
		// Validate inputs
		if (xpaths == null || xpaths.isEmpty()) {
			throw new FrameworkException("XPaths list cannot be null or empty");
		}
		if (elementNames == null || elementNames.isEmpty()) {
			throw new FrameworkException("Element names list cannot be null or empty");
		}
		if (xpaths.size() != elementNames.size()) {
			throw new FrameworkException("XPaths list size (" + xpaths.size() + ") must match element names list size (" + elementNames.size() + ")");
		}

		try {
			BaseClass.logActionStart("Clicking multiple elements (count: " + xpaths.size() + ")", "Multiple Elements");

			int successfulClicks = 0;
			List<String> failedElements = new ArrayList<>();

			for (int i = 0; i < xpaths.size(); i++) {
				String xpath = xpaths.get(i);
				String elementName = elementNames.get(i);

				try {
					// Validate individual xpath and element name
					validateInput(xpath, "XPath at index " + i);
					validateInput(elementName, "Element Name at index " + i);

					logger.info("Clicking element {}/{}: [{}]", i + 1, xpaths.size(), elementName);

					WebElement element = getWait(timeoutSeconds).until(
							ExpectedConditions.elementToBeClickable(By.xpath(xpath))
							);

					element.click();
					successfulClicks++;

					logger.info("Successfully clicked element {}/{}: [{}]", i + 1, xpaths.size(), elementName);

					// Brief pause between clicks for stability
					try {
						Thread.sleep(200);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}

				} catch (Exception e) {
					String errorMsg = "Failed to click element " + (i + 1) + "/" + xpaths.size() + ": [" + elementName + "] - " + e.getMessage();
					logger.error(errorMsg);
					failedElements.add(elementName + " (" + e.getMessage() + ")");
				}
			}

			// Summary logging and result evaluation
			if (failedElements.isEmpty()) {
				logger.info("Successfully clicked all {} elements", successfulClicks);
				BaseClass.logActionSuccess("Clicked all multiple elements", "All " + successfulClicks + " elements clicked successfully");
			} else {
				String failureDetails = "Successful: " + successfulClicks + "/" + xpaths.size() + 
						", Failed: " + failedElements.size() + " [" + String.join(", ", failedElements) + "]";
				logger.error("Multiple elements click completed with failures: {}", failureDetails);
				BaseClass.logActionFailure("Click multiple elements", "Multiple Elements", failureDetails);
				throw new FrameworkException("Failed to click " + failedElements.size() + " out of " + xpaths.size() + " elements: " + String.join(", ", failedElements));
			}

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error while clicking multiple elements: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Click multiple elements", "Multiple Elements", "Unexpected error: " + e.getMessage());
			throw new FrameworkException("Unexpected error while clicking multiple elements", e);
		}
	}

	/**
	 * Clicks element after waiting for a custom condition to be met using JavaScript evaluation.
	 */
	@SuppressWarnings("unused")
	public void clickElementWithCustomWait(String xpath, String elementName, String waitCondition, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		validateInput(elementName, "Element Name");
		validateInput(waitCondition, "Wait Condition");

		try {
			BaseClass.logActionStart("Clicking element with custom wait condition '" + elementName + "'", elementName);

			// First ensure element is present
			WebElement element = getWait(timeoutSeconds).until(
					ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
					);

			// Wait for custom condition using JavaScript
			WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
			customWait.until(webDriver -> {
				try {
					Object result = ((JavascriptExecutor) webDriver).executeScript("return " + waitCondition);
					return result != null && result.equals(true);
				} catch (Exception e) {
					logger.debug("Custom wait condition evaluation failed: {}", e.getMessage());
					return false;
				}
			});

			logger.info("Custom wait condition met for element [{}]: {}", elementName, waitCondition);

			// Now ensure element is clickable before clicking
			WebElement clickableElement = getWait(5).until(
					ExpectedConditions.elementToBeClickable(By.xpath(xpath))
					);

			clickableElement.click();

			logger.info("Clicked element [{}] after custom wait condition", elementName);
			BaseClass.logActionSuccess("Clicked element with custom wait", elementName + " (condition: " + waitCondition + ")");

		} catch (TimeoutException e) {
			logger.error("Timeout while waiting for custom condition or clicking element [{}]: {}", elementName, e.getMessage());
			BaseClass.logActionFailure("Click element with custom wait", elementName, 
					"Timeout waiting for condition '" + waitCondition + "': " + e.getMessage());
			throw new FrameworkException("Timeout while waiting for custom condition [" + waitCondition + "] or clicking element [" + elementName + "]", e);
		} catch (Exception e) {
			logger.error("Failed to click element with custom wait [{}]: {}", elementName, e.getMessage(), e);
			BaseClass.logActionFailure("Click element with custom wait", elementName, "Custom wait click failed: " + e.getMessage());
			throw new FrameworkException("Failed to click element [" + elementName + "] with custom wait condition [" + waitCondition + "]", e);
		}
	}
	
	




}
