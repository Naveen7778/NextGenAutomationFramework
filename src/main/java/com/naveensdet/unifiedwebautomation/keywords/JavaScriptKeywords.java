package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.ExpectedCondition;
import java.time.Duration;

/**
 * JavaScriptKeywords - keyword methods to execute and interact with JavaScript in the browser.
 */
public class JavaScriptKeywords {

	private final WebDriver driver;
	private final JavascriptExecutor jsExecutor;
	private static final Logger logger = LoggerFactory.getLogger(JavaScriptKeywords.class);


	public JavaScriptKeywords() {
		this.driver = DriverManager.getDriver();
		if (!(driver instanceof JavascriptExecutor)) {
			throw new FrameworkException("Driver does not support JavaScript execution");
		}
		this.jsExecutor = (JavascriptExecutor) driver;
	}

	/** Gets int config property for waits/polling. */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) return defaultValue;
		try {
			return Integer.parseInt(BaseClass.getProps().getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid config for '{}', using default {}", key, defaultValue);
			return defaultValue;
		}
	}

	/** Validates non-null and non-empty strings. */
	private void validateInput(String param, String paramName) {
		if (param == null || param.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	/** Returns a FluentWait for JS conditions with polling from config. */
	private FluentWait<WebDriver> getWait(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(pollingMillis))
				.ignoring(JavascriptException.class)
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	// === General JavaScript Execution ===

	/**
	 * Executes arbitrary synchronous JavaScript with optional arguments.
	 *
	 * @param script JavaScript code to execute.
	 * @param args   Optional script arguments.
	 * @return Result of script execution.
	 */
	public Object executeScript(String script, Object... args) {
		validateInput(script, "JavaScript script");

		try {
			BaseClass.logActionStart("Executing JavaScript: '" + BaseClass.mask(script) + "'", "JavaScript Execution");

			Object result = jsExecutor.executeScript(script, args);
			logger.info("Executed JS script: {}", script);

			BaseClass.logActionSuccess("Executed JavaScript successfully", "JavaScript Execution");
			return result;

		} catch (JavascriptException e) {
			BaseClass.logActionFailure("Execute JavaScript", "JavaScript Execution", "JavaScript execution failed: " + e.getMessage());
			throw new FrameworkException("JavaScript execution failed: " + e.getMessage(), e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Execute JavaScript", "JavaScript Execution", "Failed to execute JavaScript: " + e.getMessage());
			throw new FrameworkException("Failed to execute JavaScript", e);
		}
	}

	/**
	 * Executes asynchronous JavaScript with optional arguments.
	 * Supports callback-based scripts like setTimeout or AJAX wait.
	 *
	 * @param script JS async script ending with callback invocation.
	 * @param args   Arguments to the script.
	 * @return Result of async script execution.
	 */
	public Object executeAsyncScript(String script, Object... args) {
		validateInput(script, "Async JavaScript script");

		try {
			BaseClass.logActionStart("Executing async JavaScript: '" + BaseClass.mask(script) + "'", "JavaScript Execution");

			Object result = jsExecutor.executeAsyncScript(script, args);
			logger.info("Executed async JS script: {}", script);

			BaseClass.logActionSuccess("Executed async JavaScript successfully", "JavaScript Execution");
			return result;

		} catch (JavascriptException e) {
			BaseClass.logActionFailure("Execute async JavaScript", "JavaScript Execution", "Async JavaScript execution failed: " + e.getMessage());
			throw new FrameworkException("Async JavaScript execution failed: " + e.getMessage(), e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Execute async JavaScript", "JavaScript Execution", "Failed to execute async JavaScript: " + e.getMessage());
			throw new FrameworkException("Failed to execute async JavaScript", e);
		}
	}

	// === Element Attribute/Value/Property ===

	/**
	 * Sets an attribute value on the specified element.
	 *
	 * @param xpath  XPath locator of the element.
	 * @param attr   Attribute name.
	 * @param value  Value to set.
	 */
	public void setAttribute(String xpath, String attr, String value) {
		validateInput(attr, "Attribute");

		try {
			BaseClass.logActionStart("Setting attribute '" + attr + "' to '" + BaseClass.mask(value) + "' on element", "JavaScript Attribute");

			WebElement el = getElement(xpath);
			jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", el, attr, value);
			logger.info("Set attribute '{}'='{}' on element [{}]", attr, value, xpath);

			BaseClass.logActionSuccess("Set attribute '" + attr + "'", "JavaScript Attribute");

		} catch (Exception e) {
			BaseClass.logActionFailure("Set attribute", "JavaScript Attribute", "Failed to set attribute '" + attr + "': " + e.getMessage());
			throw new FrameworkException("Failed to set attribute", e);
		}
	}

	/**
	 * Removes an attribute from the specified element.
	 *
	 * @param xpath XPath locator.
	 * @param attr  Attribute name to remove.
	 */
	public void removeAttribute(String xpath, String attr) {
		validateInput(attr, "Attribute");

		try {
			BaseClass.logActionStart("Removing attribute '" + attr + "' from element", "JavaScript Attribute");

			WebElement el = getElement(xpath);
			jsExecutor.executeScript("arguments[0].removeAttribute(arguments[1]);", el, attr);
			logger.info("Removed attribute '{}' from element [{}]", attr, xpath);

			BaseClass.logActionSuccess("Removed attribute '" + attr + "'", "JavaScript Attribute");

		} catch (Exception e) {
			BaseClass.logActionFailure("Remove attribute", "JavaScript Attribute", "Failed to remove attribute '" + attr + "': " + e.getMessage());
			throw new FrameworkException("Failed to remove attribute", e);
		}
	}

	/**
	 * Gets an attribute value from the element.
	 *
	 * @param xpath XPath locator.
	 * @param attr  Attribute name.
	 * @return Attribute value (string or null).
	 */
	public String getAttribute(String xpath, String attr) {
		validateInput(attr, "Attribute");

		try {
			BaseClass.logActionStart("Getting attribute '" + attr + "' from element", "JavaScript Attribute");

			WebElement el = getElement(xpath);
			Object val = jsExecutor.executeScript("return arguments[0].getAttribute(arguments[1]);", el, attr);
			String value = val != null ? val.toString() : null;
			logger.info("Got attribute '{}'='{}' from element [{}]", attr, val, xpath);

			BaseClass.logActionSuccess("Retrieved attribute '" + attr + "' with value: '" + BaseClass.mask(value) + "'", "JavaScript Attribute");
			return value;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get attribute", "JavaScript Attribute", "Failed to get attribute '" + attr + "': " + e.getMessage());
			throw new FrameworkException("Failed to get attribute", e);
		}
	}

	/**
	 * Sets the value property of an element (e.g., input field) directly via JS.
	 *
	 * @param xpath XPath locator.
	 * @param value Value to set.
	 */
	public void setValue(String xpath, String value) {
		try {
			BaseClass.logActionStart("Setting value '" + BaseClass.mask(value) + "' on element", "JavaScript Value");

			WebElement el = getElement(xpath);
			jsExecutor.executeScript("arguments[0].value = arguments[1];", el, value);
			logger.info("Set value '{}' on element [{}]", value, xpath);

			BaseClass.logActionSuccess("Set value using JavaScript", "JavaScript Value");

		} catch (Exception e) {
			BaseClass.logActionFailure("Set value", "JavaScript Value", "Failed to set value: " + e.getMessage());
			throw new FrameworkException("Failed to set value", e);
		}
	}

	/**
	 * Gets the value property of an element via JS.
	 *
	 * @param xpath XPath locator.
	 * @return Value property as string.
	 */
	public String getValue(String xpath) {
		try {
			BaseClass.logActionStart("Getting value from element", "JavaScript Value");

			WebElement el = getElement(xpath);
			Object val = jsExecutor.executeScript("return arguments[0].value;", el);
			String value = val != null ? val.toString() : null;
			logger.info("Got value '{}' from element [{}]", val, xpath);

			BaseClass.logActionSuccess("Retrieved value: '" + BaseClass.mask(value) + "'", "JavaScript Value");
			return value;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get value", "JavaScript Value", "Failed to get value: " + e.getMessage());
			throw new FrameworkException("Failed to get value", e);
		}
	}

	// === Scrolling ===

	/** Scrolls the viewport so the element is in view (align to top). */
	public void scrollToElement(String xpath) {
		try {
			BaseClass.logActionStart("Scrolling element into view", "JavaScript Scroll");

			WebElement el = getElement(xpath);
			jsExecutor.executeScript("arguments[0].scrollIntoView(true);", el);
			logger.info("Scrolled element [{}] into view", xpath);

			BaseClass.logActionSuccess("Scrolled element into view", "JavaScript Scroll");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll to element", "JavaScript Scroll", "Failed to scroll element into view: " + e.getMessage());
			throw new FrameworkException("Failed to scroll to element", e);
		}
	}

	/** Scrolls the viewport by specified pixels horizontally and vertically. */
	public void scrollBy(int x, int y) {
		try {
			BaseClass.logActionStart("Scrolling window by (" + x + "," + y + ") pixels", "JavaScript Scroll");

			jsExecutor.executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
			logger.info("Scrolled window by ({},{})", x, y);

			BaseClass.logActionSuccess("Scrolled window by (" + x + "," + y + ") pixels", "JavaScript Scroll");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll by pixels", "JavaScript Scroll", "Failed to scroll by pixels: " + e.getMessage());
			throw new FrameworkException("Failed to scroll by pixels", e);
		}
	}

	/** Scrolls to the bottom of the page. */
	public void scrollToBottom() {
		try {
			BaseClass.logActionStart("Scrolling to bottom of page", "JavaScript Scroll");

			jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
			logger.info("Scrolled to bottom of the page");

			BaseClass.logActionSuccess("Scrolled to bottom of page", "JavaScript Scroll");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll to bottom", "JavaScript Scroll", "Failed to scroll to bottom: " + e.getMessage());
			throw new FrameworkException("Failed to scroll to bottom", e);
		}
	}

	/** Scrolls to the top of the page. */
	public void scrollToTop() {
		try {
			BaseClass.logActionStart("Scrolling to top of page", "JavaScript Scroll");

			jsExecutor.executeScript("window.scrollTo(0, 0);");
			logger.info("Scrolled to top of the page");

			BaseClass.logActionSuccess("Scrolled to top of page", "JavaScript Scroll");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll to top", "JavaScript Scroll", "Failed to scroll to top: " + e.getMessage());
			throw new FrameworkException("Failed to scroll to top", e);
		}
	}

	// === CSS Style / Visuals ===

	/**
	 * Gets computed style value for given CSS property on element.
	 *
	 * @param xpath        XPath of element.
	 * @param cssProperty  CSS property name (e.g., color, display).
	 * @return Computed CSS value.
	 */
	public String getComputedStyle(String xpath, String cssProperty) {
		try {
			BaseClass.logActionStart("Getting computed style '" + cssProperty + "' from element", "JavaScript CSS");

			WebElement el = getElement(xpath);
			Object val = jsExecutor.executeScript(
					"return window.getComputedStyle(arguments[0]).getPropertyValue(arguments[1]);",
					el, cssProperty);
			String value = val != null ? val.toString() : null;
			logger.info("Got CSS style '{}'='{}' from element [{}]", cssProperty, val, xpath);

			BaseClass.logActionSuccess("Retrieved CSS style '" + cssProperty + "': '" + value + "'", "JavaScript CSS");
			return value;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get computed style", "JavaScript CSS", "Failed to get CSS style '" + cssProperty + "': " + e.getMessage());
			throw new FrameworkException("Failed to get computed style", e);
		}
	}

	/**
	 * Sets inline CSS style property on element.
	 *
	 * @param xpath       XPath of element.
	 * @param cssProperty CSS property name.
	 * @param value       Value to set.
	 */
	public void setStyle(String xpath, String cssProperty, String value) {
		try {
			BaseClass.logActionStart("Setting CSS style '" + cssProperty + "' to '" + value + "' on element", "JavaScript CSS");

			WebElement el = getElement(xpath);
			jsExecutor.executeScript(
					"arguments[0].style[arguments[1]] = arguments[2];",
					el, cssProperty, value);
			logger.info("Set inline style '{}'='{}' on element [{}]", cssProperty, value, xpath);

			BaseClass.logActionSuccess("Set CSS style '" + cssProperty + "'", "JavaScript CSS");

		} catch (Exception e) {
			BaseClass.logActionFailure("Set CSS style", "JavaScript CSS", "Failed to set CSS style '" + cssProperty + "': " + e.getMessage());
			throw new FrameworkException("Failed to set CSS style", e);
		}
	}

	/**
	 * Highlights an element temporarily by flashing its border color (for debugging).
	 *
	 * @param xpath XPath locator.
	 * @param flashes Number of flashes.
	 * @param delayMs Delay in milliseconds between flashes.
	 */
	public void flashElement(String xpath, int flashes, long delayMs) {
		try {
			BaseClass.logActionStart("Flashing element " + flashes + " times with " + delayMs + "ms delay", "JavaScript Visual");

			WebElement el = getElement(xpath);
			String originalStyle = (String) jsExecutor.executeScript("return arguments[0].getAttribute('style');", el);

			for (int i = 0; i < flashes; i++) {
				jsExecutor.executeScript("arguments[0].setAttribute('style', arguments[1]);", el, "border: 3px solid red;");
				Thread.sleep(delayMs);
				jsExecutor.executeScript("arguments[0].setAttribute('style', arguments[1]);", el, originalStyle);
				Thread.sleep(delayMs);
			}

			logger.info("Flashed element [{}] {} times", xpath, flashes);
			BaseClass.logActionSuccess("Flashed element " + flashes + " times", "JavaScript Visual");

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warn("Flash interrupted");
			BaseClass.logActionFailure("Flash element", "JavaScript Visual", "Flash operation interrupted");
			throw new FrameworkException("Flash operation interrupted", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Flash element", "JavaScript Visual", "Failed to flash element: " + e.getMessage());
			throw new FrameworkException("Failed to flash element", e);
		}
	}

	// === Event Triggering ===

	/**
	 * Triggers a DOM event on the element.
	 *
	 * @param xpath   XPath locator.
	 * @param eventName Name of event (click, change, input, focus, blur, etc.).
	 */
	public void triggerEvent(String xpath, String eventName) {
		validateInput(eventName, "Event Name");

		try {
			BaseClass.logActionStart("Triggering event '" + eventName + "' on element", "JavaScript Event");

			WebElement el = getElement(xpath);
			String script = "var event = new Event(arguments[1], { bubbles: true, cancelable: true }); arguments[0].dispatchEvent(event);";
			jsExecutor.executeScript(script, el, eventName);
			logger.info("Triggered event '{}' on element [{}]", eventName, xpath);

			BaseClass.logActionSuccess("Triggered event '" + eventName + "'", "JavaScript Event");

		} catch (Exception e) {
			BaseClass.logActionFailure("Trigger event", "JavaScript Event", "Failed to trigger event '" + eventName + "': " + e.getMessage());
			throw new FrameworkException("Failed to trigger event", e);
		}
	}

	// === DOM Manipulation ===

	/**
	 * Removes element from DOM.
	 *
	 * @param xpath XPath locator.
	 */
	public void removeElement(String xpath) {
		try {
			BaseClass.logActionStart("Removing element from DOM", "JavaScript DOM");

			WebElement el = getElement(xpath);
			jsExecutor.executeScript("arguments[0].remove();", el);
			logger.info("Removed element [{}] from DOM", xpath);

			BaseClass.logActionSuccess("Removed element from DOM", "JavaScript DOM");

		} catch (Exception e) {
			BaseClass.logActionFailure("Remove element", "JavaScript DOM", "Failed to remove element from DOM: " + e.getMessage());
			throw new FrameworkException("Failed to remove element", e);
		}
	}

	/**
	 * Adds an attribute with a value to element.
	 * (Alternative to setAttribute method.)
	 *
	 * @param xpath XPath locator.
	 * @param attr  Attribute name.
	 * @param value Value to set.
	 */
	public void addAttribute(String xpath, String attr, String value) {
		try {
			BaseClass.logActionStart("Adding attribute '" + attr + "' with value '" + BaseClass.mask(value) + "' to element", "JavaScript Attribute");
			setAttribute(xpath, attr, value);
			BaseClass.logActionSuccess("Added attribute '" + attr + "'", "JavaScript Attribute");
		} catch (Exception e) {
			BaseClass.logActionFailure("Add attribute", "JavaScript Attribute", e.getMessage());
			throw e;
		}
	}

	// === Utility & Wait for JS Conditions ===

	/**
	 * Waits until the given JavaScript condition returns true or timeout.
	 *
	 * @param jsCondition JavaScript condition string that returns boolean.
	 * @param timeoutSeconds Max seconds to wait.
	 */
	public void waitForJSCondition(String jsCondition, int timeoutSeconds) {
		validateInput(jsCondition, "JavaScript Condition");

		try {
			BaseClass.logActionStart("Waiting for JavaScript condition: '" + BaseClass.mask(jsCondition) + "'", "JavaScript Wait");

			getWait(timeoutSeconds).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					Object result = jsExecutor.executeScript("return " + jsCondition + ";");
					if (result instanceof Boolean) {
						return (Boolean) result;
					}
					return false;
				}
			});

			logger.info("JavaScript condition '{}' became true", jsCondition);
			BaseClass.logActionSuccess("JavaScript condition became true", "JavaScript Wait");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Wait for JS condition", "JavaScript Wait", "Timeout waiting for JS condition: " + jsCondition);
			throw new FrameworkException("Timeout waiting for JS condition: " + jsCondition, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for JS condition", "JavaScript Wait", "Failed to wait for JS condition: " + e.getMessage());
			throw new FrameworkException("Failed to wait for JS condition", e);
		}
	}

	// === Alerts, Confirms, Prompts via JS ===

	/**
	 * Generates a JavaScript alert with the provided message.
	 *
	 * @param message Alert message.
	 */
	public void jsAlert(String message) {
		validateInput(message, "Alert message");

		try {
			BaseClass.logActionStart("Triggering JavaScript alert with message: '" + BaseClass.mask(message) + "'", "JavaScript Alert");

			jsExecutor.executeScript("alert(arguments[0]);", message);
			logger.info("Triggered JS alert with message '{}'", message);

			BaseClass.logActionSuccess("Triggered JavaScript alert", "JavaScript Alert");

		} catch (Exception e) {
			BaseClass.logActionFailure("Trigger JS alert", "JavaScript Alert", "Failed to trigger JavaScript alert: " + e.getMessage());
			throw new FrameworkException("Failed to trigger JavaScript alert", e);
		}
	}

	/**
	 * Generates a JS confirmation dialog with message.
	 *
	 * @param message Confirmation message.
	 */
	public void jsConfirm(String message) {
		validateInput(message, "Confirm message");

		try {
			BaseClass.logActionStart("Triggering JavaScript confirm with message: '" + BaseClass.mask(message) + "'", "JavaScript Confirm");

			jsExecutor.executeScript("confirm(arguments[0]);", message);
			logger.info("Triggered JS confirm with message '{}'", message);

			BaseClass.logActionSuccess("Triggered JavaScript confirm", "JavaScript Confirm");

		} catch (Exception e) {
			BaseClass.logActionFailure("Trigger JS confirm", "JavaScript Confirm", "Failed to trigger JavaScript confirm: " + e.getMessage());
			throw new FrameworkException("Failed to trigger JavaScript confirm", e);
		}
	}

	/**
	 * Generates a JS prompt dialog with message and default text.
	 *
	 * @param message   Prompt message.
	 * @param defaultText Default text shown in prompt input.
	 */
	public void jsPrompt(String message, String defaultText) {
		validateInput(message, "Prompt message");

		try {
			BaseClass.logActionStart("Triggering JavaScript prompt with message: '" + BaseClass.mask(message) + "' and default: '" + BaseClass.mask(defaultText) + "'", "JavaScript Prompt");

			jsExecutor.executeScript("prompt(arguments[0], arguments[1]);", message, defaultText);
			logger.info("Triggered JS prompt with message '{}' and default text '{}'", message, defaultText);

			BaseClass.logActionSuccess("Triggered JavaScript prompt", "JavaScript Prompt");

		} catch (Exception e) {
			BaseClass.logActionFailure("Trigger JS prompt", "JavaScript Prompt", "Failed to trigger JavaScript prompt: " + e.getMessage());
			throw new FrameworkException("Failed to trigger JavaScript prompt", e);
		}
	}

	// === Private Helpers ===

	private WebElement getElement(String xpath) {
		validateInput(xpath, "XPath");
		try {
			return driver.findElement(By.xpath(xpath));
		} catch (NoSuchElementException e) {
			throw new FrameworkException("Element not found for XPath: " + xpath, e);
		}
	}
}
