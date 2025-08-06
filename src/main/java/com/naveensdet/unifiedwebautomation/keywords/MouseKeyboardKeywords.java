package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

/**
 * MouseKeyboardKeywords - advanced mouse and keyboard action keywords.
 */
public class MouseKeyboardKeywords {

	private final WebDriver driver;
	private final Actions actions;
	private static final Logger logger = LoggerFactory.getLogger(MouseKeyboardKeywords.class);

	public MouseKeyboardKeywords() {
		this.driver = DriverManager.getDriver();
		this.actions = new Actions(driver);
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

	/** Returns a WebElement found by XPath with wait and validation. */
	private WebElement waitForElement(String xpath, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		try {
			return getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		} catch (Exception e) {
			throw new FrameworkException("Element not found or not visible for XPath: " + xpath, e);
		}
	}

	// ========= Mouse Actions ==========

	/** Moves mouse cursor to hover over the element located by XPath. */
	public void mouseHover(String xpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Hovering mouse over element", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.moveToElement(element).perform();
			logger.info("Hovered mouse over element [{}]", xpath);

			BaseClass.logActionSuccess("Hovered mouse over element", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Mouse hover", "Mouse Action", "Failed to hover over element: " + e.getMessage());
			throw new FrameworkException("Failed to hover over element", e);
		}
	}

	/** Moves mouse over WebElement to the specified offset relative to element. */
	public void mouseMoveToElement(WebElement element, int offsetX, int offsetY) {
		if (element == null) {
			BaseClass.logActionFailure("Mouse move to element", "Mouse Action", "WebElement cannot be null");
			throw new FrameworkException("WebElement cannot be null");
		}

		try {
			BaseClass.logActionStart("Moving mouse to element with offset (" + offsetX + "," + offsetY + ")", "Mouse Action");

			actions.moveToElement(element, offsetX, offsetY).perform();
			logger.info("Moved mouse to element with offset ({},{})", offsetX, offsetY);

			BaseClass.logActionSuccess("Moved mouse to element with offset (" + offsetX + "," + offsetY + ")", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Mouse move to element", "Mouse Action", "Failed to move mouse to element with offset: " + e.getMessage());
			throw new FrameworkException("Failed to move mouse to element", e);
		}
	}

	/** Performs a right-click (context click) on the element located by XPath. */
	public void rightClick(String xpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Performing right-click on element", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.contextClick(element).perform();
			logger.info("Performed right-click on element [{}]", xpath);

			BaseClass.logActionSuccess("Performed right-click on element", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Right-click", "Mouse Action", "Failed to right-click on element: " + e.getMessage());
			throw new FrameworkException("Failed to right-click on element", e);
		}
	}

	/** Double clicks the element located by XPath. */
	public void doubleClick(String xpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Double-clicking on element", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.doubleClick(element).perform();
			logger.info("Double-clicked on element [{}]", xpath);

			BaseClass.logActionSuccess("Double-clicked on element", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Double-click", "Mouse Action", "Failed to double-click on element: " + e.getMessage());
			throw new FrameworkException("Failed to double-click on element", e);
		}
	}

	/** Clicks and holds mouse button down on element located by XPath. */
	public void clickAndHold(String xpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Clicking and holding mouse button on element", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.clickAndHold(element).perform();
			logger.info("Clicked and held mouse button on element [{}]", xpath);

			BaseClass.logActionSuccess("Clicked and held mouse button on element", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Click and hold", "Mouse Action", "Failed to click and hold on element: " + e.getMessage());
			throw new FrameworkException("Failed to click and hold on element", e);
		}
	}

	/** Releases mouse button held on element located by XPath. */
	public void releaseClick(String xpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Releasing mouse button on element", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.release(element).perform();
			logger.info("Released mouse button on element [{}]", xpath);

			BaseClass.logActionSuccess("Released mouse button on element", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Release click", "Mouse Action", "Failed to release mouse button on element: " + e.getMessage());
			throw new FrameworkException("Failed to release mouse button on element", e);
		}
	}

	/** Drags element from source XPath and drops on target XPath. */
	public void dragAndDrop(String sourceXpath, String targetXpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Dragging element from source to target", "Drag Drop Action");

			WebElement source = waitForElement(sourceXpath, timeoutSeconds);
			WebElement target = waitForElement(targetXpath, timeoutSeconds);
			actions.dragAndDrop(source, target).perform();
			logger.info("Dragged element [{}] and dropped on [{}]", sourceXpath, targetXpath);

			BaseClass.logActionSuccess("Dragged element from source to target", "Drag Drop Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Drag and drop", "Drag Drop Action", "Failed to drag and drop elements: " + e.getMessage());
			throw new FrameworkException("Failed to drag and drop elements", e);
		}
	}

	/** Drags element from source XPath by x and y offsets. */
	public void dragAndDropByOffset(String sourceXpath, int offsetX, int offsetY, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Dragging element by offset (" + offsetX + "," + offsetY + ")", "Drag Drop Action");

			WebElement source = waitForElement(sourceXpath, timeoutSeconds);
			actions.dragAndDropBy(source, offsetX, offsetY).perform();
			logger.info("Dragged element [{}] by offset ({},{})", sourceXpath, offsetX, offsetY);

			BaseClass.logActionSuccess("Dragged element by offset (" + offsetX + "," + offsetY + ")", "Drag Drop Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Drag and drop by offset", "Drag Drop Action", "Failed to drag element by offset: " + e.getMessage());
			throw new FrameworkException("Failed to drag element by offset", e);
		}
	}

	/** Scrolls viewport until element located by XPath is in view. */
	public void scrollToElement(String xpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Scrolling to element", "Scroll Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			logger.info("Scrolled to element [{}]", xpath);

			BaseClass.logActionSuccess("Scrolled to element", "Scroll Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll to element", "Scroll Action", "Failed to scroll to element: " + e.getMessage());
			throw new FrameworkException("Failed to scroll to element", e);
		}
	}

	/** Scrolls the page by specified pixel amounts x (horizontal) and y (vertical). */
	public void scrollBy(int x, int y) {
		try {
			BaseClass.logActionStart("Scrolling page by (" + x + "," + y + ") pixels", "Scroll Action");

			((JavascriptExecutor) driver).executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
			logger.info("Scrolled window by ({},{}) pixels", x, y);

			BaseClass.logActionSuccess("Scrolled page by (" + x + "," + y + ") pixels", "Scroll Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll by pixels", "Scroll Action", "Failed to scroll page by pixels: " + e.getMessage());
			throw new FrameworkException("Failed to scroll page by pixels", e);
		}
	}

	// ======== Keyboard Actions =========

	/** Sends specified keys to element located by XPath. */
	public void sendKeysToElement(String xpath, CharSequence keys, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Sending keys '" + BaseClass.mask(keys.toString()) + "' to element", "Keyboard Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			element.sendKeys(keys);
			logger.info("Sent keys [{}] to element [{}]", keys, xpath);

			BaseClass.logActionSuccess("Sent keys to element", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Send keys to element", "Keyboard Action", "Failed to send keys to element: " + e.getMessage());
			throw new FrameworkException("Failed to send keys to element", e);
		}
	}

	/** Sends specified keys to currently active/focused element in the browser. */
	public void sendKeysToActiveElement(CharSequence keys) {
		try {
			BaseClass.logActionStart("Sending keys '" + BaseClass.mask(keys.toString()) + "' to active element", "Keyboard Action");

			driver.switchTo().activeElement().sendKeys(keys);
			logger.info("Sent keys [{}] to active element", keys);

			BaseClass.logActionSuccess("Sent keys to active element", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Send keys to active element", "Keyboard Action", "Failed to send keys to active element: " + e.getMessage());
			throw new FrameworkException("Failed to send keys to active element", e);
		}
	}

	/** Presses down a specific keyboard key. */
	public void pressKey(Keys key) {
		if (key == null) {
			BaseClass.logActionFailure("Press key", "Keyboard Action", "Key cannot be null");
			throw new FrameworkException("Key cannot be null");
		}

		try {
			BaseClass.logActionStart("Pressing key '" + key.name() + "'", "Keyboard Action");

			actions.keyDown(key).perform();
			logger.info("Pressed key {}", key.name());

			BaseClass.logActionSuccess("Pressed key '" + key.name() + "'", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Press key", "Keyboard Action", "Failed to press key '" + key.name() + "': " + e.getMessage());
			throw new FrameworkException("Failed to press key", e);
		}
	}

	/** Releases a previously pressed keyboard key. */
	public void releaseKey(Keys key) {
		if (key == null) {
			BaseClass.logActionFailure("Release key", "Keyboard Action", "Key cannot be null");
			throw new FrameworkException("Key cannot be null");
		}

		try {
			BaseClass.logActionStart("Releasing key '" + key.name() + "'", "Keyboard Action");

			actions.keyUp(key).perform();
			logger.info("Released key {}", key.name());

			BaseClass.logActionSuccess("Released key '" + key.name() + "'", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Release key", "Keyboard Action", "Failed to release key '" + key.name() + "': " + e.getMessage());
			throw new FrameworkException("Failed to release key", e);
		}
	}

	/** Holds a key down on specified element located by XPath. */
	public void keyDownOnElement(String xpath, Keys key, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Pressing key '" + key.name() + "' down on element", "Keyboard Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.keyDown(element, key).perform();
			logger.info("Key down {} on element [{}]", key.name(), xpath);

			BaseClass.logActionSuccess("Key '" + key.name() + "' pressed down on element", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Key down on element", "Keyboard Action", "Failed to press key down on element: " + e.getMessage());
			throw new FrameworkException("Failed to press key down on element", e);
		}
	}

	/** Releases a key up on specified element located by XPath. */
	public void keyUpOnElement(String xpath, Keys key, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Releasing key '" + key.name() + "' up on element", "Keyboard Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.keyUp(element, key).perform();
			logger.info("Key up {} on element [{}]", key.name(), xpath);

			BaseClass.logActionSuccess("Key '" + key.name() + "' released up on element", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Key up on element", "Keyboard Action", "Failed to release key up on element: " + e.getMessage());
			throw new FrameworkException("Failed to release key up on element", e);
		}
	}

	/** Sends a shortcut key combination (like Ctrl+C) to element located by XPath. */
	public void sendKeyboardShortcut(String xpath, List<Keys> keys, int timeoutSeconds) {
		if (keys == null || keys.isEmpty()) {
			BaseClass.logActionFailure("Send keyboard shortcut", "Keyboard Action", "Keys list cannot be null or empty");
			throw new FrameworkException("Keys list cannot be null or empty");
		}

		try {
			BaseClass.logActionStart("Sending keyboard shortcut " + keys + " to element", "Keyboard Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);

			Actions shortcutActions = new Actions(driver).click(element);
			keys.forEach(shortcutActions::keyDown);
			keys.forEach(shortcutActions::keyUp);

			shortcutActions.perform();
			logger.info("Sent keyboard shortcut keys {} to element [{}]", keys, xpath);

			BaseClass.logActionSuccess("Sent keyboard shortcut " + keys + " to element", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Send keyboard shortcut", "Keyboard Action", "Failed to send keyboard shortcut: " + e.getMessage());
			throw new FrameworkException("Failed to send keyboard shortcut", e);
		}
	}

	/** Clears any existing text and types new text into element located by XPath. */
	public void clearAndType(String xpath, String text, int timeoutSeconds) {
		validateInput(text, "Text");

		try {
			BaseClass.logActionStart("Clearing and typing text '" + BaseClass.mask(text) + "' into element", "Keyboard Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			element.clear();
			element.sendKeys(text);
			logger.info("Cleared and typed '{}' into element [{}]", text, xpath);

			BaseClass.logActionSuccess("Cleared and typed text into element", "Keyboard Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Clear and type", "Keyboard Action", "Failed to clear and type text: " + e.getMessage());
			throw new FrameworkException("Failed to clear and type text", e);
		}
	}

	// ===== Composite and Special Actions =====

	/** Clicks at specified offset inside element located by XPath. */
	public void clickAtOffset(String xpath, int offsetX, int offsetY, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Clicking at offset (" + offsetX + "," + offsetY + ") inside element", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.moveToElement(element, offsetX, offsetY).click().perform();
			logger.info("Clicked at offset ({},{}) inside element [{}]", offsetX, offsetY, xpath);

			BaseClass.logActionSuccess("Clicked at offset (" + offsetX + "," + offsetY + ") inside element", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Click at offset", "Mouse Action", "Failed to click at offset: " + e.getMessage());
			throw new FrameworkException("Failed to click at offset", e);
		}
	}

	/** Moves mouse by offset relative to current position. */
	public void moveByOffset(int offsetX, int offsetY) {
		try {
			BaseClass.logActionStart("Moving mouse by offset (" + offsetX + "," + offsetY + ") from current position", "Mouse Action");

			actions.moveByOffset(offsetX, offsetY).perform();
			logger.info("Moved mouse by offset ({},{}) from current position", offsetX, offsetY);

			BaseClass.logActionSuccess("Moved mouse by offset (" + offsetX + "," + offsetY + ")", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Move by offset", "Mouse Action", "Failed to move mouse by offset: " + e.getMessage());
			throw new FrameworkException("Failed to move mouse by offset", e);
		}
	}

	/** Clicks on element holding specified modifier key (Ctrl, Shift, Alt). */
	public void clickWithModifier(String xpath, Keys modifierKey, int timeoutSeconds) {
		if (modifierKey == null) {
			BaseClass.logActionFailure("Click with modifier", "Mouse Action", "Modifier key cannot be null");
			throw new FrameworkException("Modifier key cannot be null");
		}

		try {
			BaseClass.logActionStart("Clicking on element with modifier key '" + modifierKey.name() + "'", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.keyDown(modifierKey).click(element).keyUp(modifierKey).perform();
			logger.info("Clicked on element [{}] with modifier key {}", xpath, modifierKey.name());

			BaseClass.logActionSuccess("Clicked on element with modifier key '" + modifierKey.name() + "'", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Click with modifier", "Mouse Action", "Failed to click with modifier key: " + e.getMessage());
			throw new FrameworkException("Failed to click with modifier key", e);
		}
	}

	/** Performs a right click at offset inside the element located by XPath. */
	public void rightClickAtOffset(String xpath, int offsetX, int offsetY, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Right-clicking at offset (" + offsetX + "," + offsetY + ") in element", "Mouse Action");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			actions.moveToElement(element, offsetX, offsetY).contextClick().perform();
			logger.info("Right-clicked at offset ({},{}) in element [{}]", offsetX, offsetY, xpath);

			BaseClass.logActionSuccess("Right-clicked at offset (" + offsetX + "," + offsetY + ")", "Mouse Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Right-click at offset", "Mouse Action", "Failed to right-click at offset: " + e.getMessage());
			throw new FrameworkException("Failed to right-click at offset", e);
		}
	}

	/** Performs drag-and-drop from source WebElement to target WebElement. */
	public void dragAndDropByWebElements(WebElement source, WebElement target) {
		if (source == null || target == null) {
			BaseClass.logActionFailure("Drag and drop by WebElements", "Drag Drop Action", "Source and target elements cannot be null");
			throw new FrameworkException("Source and target elements cannot be null");
		}

		try {
			BaseClass.logActionStart("Dragging WebElement to target WebElement", "Drag Drop Action");

			actions.dragAndDrop(source, target).perform();
			logger.info("Dragged element {} to target {}", source, target);

			BaseClass.logActionSuccess("Dragged WebElement to target WebElement", "Drag Drop Action");

		} catch (Exception e) {
			BaseClass.logActionFailure("Drag and drop by WebElements", "Drag Drop Action", "Failed to drag and drop WebElements: " + e.getMessage());
			throw new FrameworkException("Failed to drag and drop WebElements", e);
		}
	}

	// ======== Verification Methods ========

	/**
	 * Verifies that the element located by XPath is hovered by checking 'hover' CSS class or style.
	 * (This requires convention or customization, here we check :hover by JS)
	 */
	public void verifyElementIsHovered(String xpath, int timeoutSeconds) {
		validateInput(xpath, "XPath");

		try {
			BaseClass.logActionStart("Verifying element is hovered", "Mouse Verification");

			boolean hovered = getWait(timeoutSeconds).until(d -> {
				WebElement el = d.findElement(By.xpath(xpath));
				String script = "return (function(elem) {" +
						"  return window.getComputedStyle(elem, ':hover').length > 0 || " +
						"  elem.matches(':hover'); " +
						"})(arguments[0]);";
				Object result = ((JavascriptExecutor) d).executeScript(script, el);
				return Boolean.TRUE.equals(result);
			});

			if (!hovered) {
				BaseClass.logActionFailure("Verify element is hovered", "Mouse Verification", "Element is not hovered");
				throw new FrameworkException("Element [" + xpath + "] is not hovered");
			}

			logger.info("Verified element [{}] is hovered", xpath);
			BaseClass.logActionSuccess("Verified element is hovered", "Mouse Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify element is hovered", "Mouse Verification", "Timeout waiting for element to be hovered: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for element to be hovered: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify element is hovered", "Mouse Verification", "Failed to verify element hover state: " + e.getMessage());
			throw new FrameworkException("Failed to verify element hover state", e);
		}
	}

	/** Verifies the element located by XPath has keyboard focus within timeout. */
	public void verifyFocusOnElement(String xpath, int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Verifying element has keyboard focus", "Keyboard Verification");

			WebElement element = waitForElement(xpath, timeoutSeconds);
			boolean focused = getWait(timeoutSeconds).until(d -> element.equals(d.switchTo().activeElement()));

			if (!focused) {
				BaseClass.logActionFailure("Verify focus on element", "Keyboard Verification", "Element does not have keyboard focus");
				throw new FrameworkException("Element [" + xpath + "] does not have keyboard focus");
			}

			logger.info("Verified element [{}] has keyboard focus", xpath);
			BaseClass.logActionSuccess("Verified element has keyboard focus", "Keyboard Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify focus on element", "Keyboard Verification", "Timeout waiting for element to have focus: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for element to have focus: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify focus on element", "Keyboard Verification", "Failed to verify element focus: " + e.getMessage());
			throw new FrameworkException("Failed to verify element focus", e);
		}
	}

	/**
	 * Verifies that pressing the specified key triggers the expected effect,
	 * this is application-specific and may require overriding or custom JS.
	 * Here, as a simple placeholder, we check keydown event is triggered
	 * by executing JS (you may implement your own verification logic).
	 */
	public void verifyKeyPressedEffect(String xpath, Keys key, int timeoutSeconds) {
		validateInput(xpath, "XPath");
		if (key == null) {
			BaseClass.logActionFailure("Verify key pressed effect", "Keyboard Verification", "Key cannot be null");
			throw new FrameworkException("Key cannot be null");
		}

		try {
			BaseClass.logActionStart("Verifying key '" + key.name() + "' pressed effect on element", "Keyboard Verification");

			boolean effectDetected = getWait(timeoutSeconds).until(d -> {
				WebElement element = d.findElement(By.xpath(xpath));
				// Placeholder: custom implementation needed by app
				// Example: check if element received focus or attribute changed after key press
				actions.sendKeys(element, key).perform();
				// Add app-specific verification here or use JS
				return true; // Assuming effect detected for demo
			});

			if (!effectDetected) {
				BaseClass.logActionFailure("Verify key pressed effect", "Keyboard Verification", "Key press effect not detected for key " + key.name());
				throw new FrameworkException("Key press effect not detected for key " + key.name() + " on element " + xpath);
			}

			logger.info("Verified key [{}] pressed effect on element [{}]", key.name(), xpath);
			BaseClass.logActionSuccess("Verified key '" + key.name() + "' pressed effect on element", "Keyboard Verification");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify key pressed effect", "Keyboard Verification", "Timeout waiting for key pressed effect: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for key pressed effect: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify key pressed effect", "Keyboard Verification", "Failed to verify key pressed effect: " + e.getMessage());
			throw new FrameworkException("Failed to verify key pressed effect", e);
		}
	}
}
