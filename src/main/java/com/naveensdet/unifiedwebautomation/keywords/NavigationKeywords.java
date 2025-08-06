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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NavigationKeywords {

	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(NavigationKeywords.class);

	public NavigationKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/** Retrieves an integer configuration property from BaseClass.props, with a default fallback. */
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

	/** Validates that a string parameter is neither null nor empty. */
	private void validateInput(String param, String paramName) {
		if (param == null || param.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
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

	/** Navigates the browser to the specified URL. */
	public void navigateToUrl(String url, boolean excelData, String testCaseName) {
		validateInput(url, "URL");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, url, "URL", excelData);

		try {
			BaseClass.logActionStart("Navigating to URL '" + valueToUse + "'", "Navigation");

			driver.get(valueToUse.trim());
			logger.info("Navigated to URL: {}", valueToUse);

			BaseClass.logActionSuccess("Navigated to URL '" + valueToUse + "'", "Navigation");

		} catch (Exception e) {
			logger.error("Failed to navigate to URL: {}", valueToUse, e);
			BaseClass.logActionFailure("Navigate to URL", "Navigation", "Failed to navigate to '" + valueToUse + "': " + e.getMessage());
			throw new FrameworkException("Failed to navigate to URL: " + valueToUse, e);
		}
	}


	/** Refreshes the current browser page. */
	public void refreshPage() {
		try {
			BaseClass.logActionStart("Refreshing current page", "Navigation");

			driver.navigate().refresh();
			logger.info("Refreshed the current page.");

			BaseClass.logActionSuccess("Refreshed current page", "Navigation");

		} catch (Exception e) {
			logger.error("Failed to refresh the page.", e);
			BaseClass.logActionFailure("Refresh page", "Navigation", "Failed to refresh page: " + e.getMessage());
			throw new FrameworkException("Failed to refresh the page.", e);
		}
	}

	/** Navigates backward in browser history. */
	public void goBack() {
		try {
			BaseClass.logActionStart("Navigating back in browser history", "Navigation");

			driver.navigate().back();
			logger.info("Navigated back.");

			BaseClass.logActionSuccess("Navigated back in browser history", "Navigation");

		} catch (Exception e) {
			logger.error("Failed to navigate back.", e);
			BaseClass.logActionFailure("Navigate back", "Navigation", "Failed to navigate back: " + e.getMessage());
			throw new FrameworkException("Failed to navigate back.", e);
		}
	}

	/** Navigates forward in browser history. */
	public void goForward() {
		try {
			BaseClass.logActionStart("Navigating forward in browser history", "Navigation");

			driver.navigate().forward();
			logger.info("Navigated forward.");

			BaseClass.logActionSuccess("Navigated forward in browser history", "Navigation");

		} catch (Exception e) {
			logger.error("Failed to navigate forward.", e);
			BaseClass.logActionFailure("Navigate forward", "Navigation", "Failed to navigate forward: " + e.getMessage());
			throw new FrameworkException("Failed to navigate forward.", e);
		}
	}


	/** Moves the current browser window to the specified position on the screen. */
	public void moveWindowTo(int x, int y) {
		try {
			BaseClass.logActionStart("Moving window to position (" + x + "," + y + ")", "Window Management");

			driver.manage().window().setPosition(new Point(x, y));
			logger.info("Moved window to position ({}, {})", x, y);

			BaseClass.logActionSuccess("Moved window to position (" + x + "," + y + ")", "Window Management");

		} catch (Exception e) {
			logger.error("Failed to move window.", e);
			BaseClass.logActionFailure("Move window", "Window Management", "Failed to move window to (" + x + "," + y + "): " + e.getMessage());
			throw new FrameworkException("Failed to move window.", e);
		}
	}

	/** Opens a new browser tab and switches focus to it. */
	public void openNewTab() {
		try {
			BaseClass.logActionStart("Opening new browser tab", "Tab Management");

			((JavascriptExecutor) driver).executeScript("window.open()");
			switchToNewlyOpenedWindow();
			logger.info("Opened and switched to new browser tab.");

			BaseClass.logActionSuccess("Opened new browser tab and switched to it", "Tab Management");

		} catch (Exception e) {
			logger.error("Failed to open new tab.", e);
			BaseClass.logActionFailure("Open new tab", "Tab Management", "Failed to open new tab: " + e.getMessage());
			throw new FrameworkException("Failed to open new tab.", e);
		}
	}


	/** Switches the WebDriver context to the most recently opened window or tab. */
	public void switchToNewlyOpenedWindow() {
		try {
			BaseClass.logActionStart("Switching to newly opened window/tab", "Window Management");

			Set<String> handles = driver.getWindowHandles();
			String lastHandle = new ArrayList<>(handles).get(handles.size() - 1);
			driver.switchTo().window(lastHandle);
			logger.info("Switched to newly opened window/tab.");

			BaseClass.logActionSuccess("Switched to newly opened window/tab", "Window Management");

		} catch (Exception e) {
			logger.error("Failed to switch to new window/tab.", e);
			BaseClass.logActionFailure("Switch to newly opened window", "Window Management", "Failed to switch to newly opened window/tab: " + e.getMessage());
			throw new FrameworkException("Failed to switch to new window/tab.", e);
		}
	}

	/** Switches the WebDriver context to the window identified by a zero-based index. */
	public void switchToWindowByIndex(int index) {
		try {
			BaseClass.logActionStart("Switching to window/tab at index " + index, "Window Management");

			List<String> handles = new ArrayList<>(driver.getWindowHandles());
			if (index < 0 || index >= handles.size()) {
				BaseClass.logActionFailure("Switch to window by index", "Window Management", "Window/tab index " + index + " out of bounds (total windows: " + handles.size() + ")");
				throw new FrameworkException("Window/tab index out of bounds.");
			}
			driver.switchTo().window(handles.get(index));
			logger.info("Switched to window/tab at index {}", index);

			BaseClass.logActionSuccess("Switched to window/tab at index " + index, "Window Management");

		} catch (Exception e) {
			logger.error("Failed to switch to window/tab by index.", e);
			BaseClass.logActionFailure("Switch to window by index", "Window Management", "Failed to switch to window by index " + index + ": " + e.getMessage());
			throw new FrameworkException("Failed to switch to window/tab by index.", e);
		}
	}

	/** Switches the WebDriver context to the window with the specified title, waiting up to the specified timeout. */
	public void switchToWindowByTitle(String windowTitle, int timeoutSeconds) {
		validateInput(windowTitle, "Window title");
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Switching to window with title '" + windowTitle + "'", "Window Management");

			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> {
				for (String handle : drv.getWindowHandles()) {
					drv.switchTo().window(handle);
					if (drv.getTitle().equals(windowTitle)) {
						return true;
					}
				}
				return false;
			});
			logger.info("Switched to window/tab with title '{}'", windowTitle);

			BaseClass.logActionSuccess("Switched to window with title '" + windowTitle + "'", "Window Management");

		} catch (TimeoutException e) {
			logger.error("Timeout in switching to window with title '{}'", windowTitle, e);
			BaseClass.logActionFailure("Switch to window by title", "Window Management", "Timeout waiting for window with title '" + windowTitle + "'");
			throw new FrameworkException("Timeout in switching to window with title: " + windowTitle, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to window by title", "Window Management", "Failed to switch to window by title: " + e.getMessage());
			throw new FrameworkException("Failed to switch to window by title", e);
		}
	}

	/** Switches the WebDriver context to the window with the specified window handle. */
	public void switchToWindowByHandle(String windowHandle) {
		validateInput(windowHandle, "Window handle");

		try {
			BaseClass.logActionStart("Switching to window with handle '" + windowHandle + "'", "Window Management");

			driver.switchTo().window(windowHandle);
			logger.info("Switched to window/tab with handle '{}'", windowHandle);

			BaseClass.logActionSuccess("Switched to window with handle '" + windowHandle + "'", "Window Management");

		} catch (Exception e) {
			logger.error("Failed to switch to window/tab with handle '{}'", windowHandle, e);
			BaseClass.logActionFailure("Switch to window by handle", "Window Management", "Failed to switch to window with handle '" + windowHandle + "': " + e.getMessage());
			throw new FrameworkException("Failed to switch to window/tab with handle: " + windowHandle, e);
		}
	}

	/** Switches the WebDriver context to the original parent window. */
	public void switchToParentWindow() {
		try {
			BaseClass.logActionStart("Switching to parent (main) window", "Window Management");

			String mainHandle = new ArrayList<>(driver.getWindowHandles()).get(0);
			driver.switchTo().window(mainHandle);
			logger.info("Switched to parent (main) window.");

			BaseClass.logActionSuccess("Switched to parent (main) window", "Window Management");

		} catch (Exception e) {
			logger.error("Failed to switch to parent window.", e);
			BaseClass.logActionFailure("Switch to parent window", "Window Management", "Failed to switch to parent window: " + e.getMessage());
			throw new FrameworkException("Failed to switch to parent window.", e);
		}
	}

	/** Closes the current window or tab and switches to the main window. */
	public void closeCurrentTabAndSwitchToMain() {
		try {
			BaseClass.logActionStart("Closing current tab and switching to main window", "Tab Management");

			driver.close();
			switchToParentWindow();
			logger.info("Closed current tab and switched to main window.");

			BaseClass.logActionSuccess("Closed current tab and switched to main window", "Tab Management");

		} catch (Exception e) {
			logger.error("Failed to close current tab and switch to main.", e);
			BaseClass.logActionFailure("Close current tab and switch to main", "Tab Management", "Failed to close current tab and switch to main: " + e.getMessage());
			throw new FrameworkException("Failed to close current tab and switch to main.", e);
		}
	}

	/** Closes all windows or tabs except the main window. */
	public void closeAllOtherTabsExceptMain() {
		try {
			BaseClass.logActionStart("Closing all tabs except main window", "Tab Management");

			List<String> handles = new ArrayList<>(driver.getWindowHandles());
			String mainHandle = handles.get(0);
			int closedCount = 0;

			for (String handle : handles) {
				if (!handle.equals(mainHandle)) {
					driver.switchTo().window(handle).close();
					closedCount++;
				}
			}
			driver.switchTo().window(mainHandle);
			logger.info("Closed all tabs except main.");

			BaseClass.logActionSuccess("Closed " + closedCount + " tabs, kept main window", "Tab Management");

		} catch (Exception e) {
			logger.error("Failed to close all tabs except main.", e);
			BaseClass.logActionFailure("Close all tabs except main", "Tab Management", "Failed to close all tabs except main: " + e.getMessage());
			throw new FrameworkException("Failed to close all tabs except main.", e);
		}
	}

	/** Switches the WebDriver context to the iframe specified by XPath. */
	public void switchToFrameByXpath(String frameXpath, String elementName, int timeoutSeconds) {
		validateInput(frameXpath, elementName);
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Switching to frame '" + elementName + "' by XPath", "Frame Management");

			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath(frameXpath.trim())));
			logger.info("Switched to frame [{}] with xpath '{}'", elementName, frameXpath);

			BaseClass.logActionSuccess("Switched to frame '" + elementName + "'", "Frame Management");

		} catch (TimeoutException e) {
			logger.error("Timeout switching to frame [{}] with xpath '{}'", elementName, frameXpath, e);
			BaseClass.logActionFailure("Switch to frame by XPath", "Frame Management", "Timeout switching to frame '" + elementName + "' with XPath '" + frameXpath + "'");
			throw new FrameworkException("Timeout switching to frame [" + elementName + "] with xpath [" + frameXpath + "]", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to frame by XPath", "Frame Management", "Failed to switch to frame: " + e.getMessage());
			throw new FrameworkException("Failed to switch to frame", e);
		}
	}

	/** Switches the WebDriver context to the iframe specified by name or ID. */
	public void switchToFrameByNameOrId(String nameOrId) {
		validateInput(nameOrId, "Frame name or ID");

		try {
			BaseClass.logActionStart("Switching to frame with name/ID '" + nameOrId + "'", "Frame Management");

			driver.switchTo().frame(nameOrId.trim());
			logger.info("Switched to frame with name/ID '{}'", nameOrId);

			BaseClass.logActionSuccess("Switched to frame with name/ID '" + nameOrId + "'", "Frame Management");

		} catch (Exception e) {
			logger.error("Failed to switch to frame with name/ID '{}'", nameOrId, e);
			BaseClass.logActionFailure("Switch to frame by name/ID", "Frame Management", "Failed to switch to frame with name/ID '" + nameOrId + "': " + e.getMessage());
			throw new FrameworkException("Failed to switch to frame with name/ID: " + nameOrId, e);
		}
	}

	/** Switches the WebDriver context to the iframe specified by index. */
	public void switchToFrameByIndex(int frameIndex) {
		try {
			BaseClass.logActionStart("Switching to frame at index " + frameIndex, "Frame Management");

			driver.switchTo().frame(frameIndex);
			logger.info("Switched to frame at index {}", frameIndex);

			BaseClass.logActionSuccess("Switched to frame at index " + frameIndex, "Frame Management");

		} catch (Exception e) {
			logger.error("Failed to switch to frame at index {}", frameIndex, e);
			BaseClass.logActionFailure("Switch to frame by index", "Frame Management", "Failed to switch to frame at index " + frameIndex + ": " + e.getMessage());
			throw new FrameworkException("Failed to switch to frame at index " + frameIndex, e);
		}
	}

	/** Accepts an alert, waiting up to the specified timeout. */
	public void acceptAlert(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Accepting alert", "Alert Management");

			Alert alert = new FluentWait<>(driver)
					.withTimeout(Duration.ofSeconds(timeoutSeconds))
					.pollingEvery(Duration.ofMillis(pollingMillis))
					.ignoring(NoAlertPresentException.class)
					.until(ExpectedConditions.alertIsPresent());
			alert.accept();
			logger.info("Accepted alert.");

			BaseClass.logActionSuccess("Accepted alert", "Alert Management");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for alert to accept.", e);
			BaseClass.logActionFailure("Accept alert", "Alert Management", "Timeout waiting for alert to accept");
			throw new FrameworkException("Timeout waiting for alert to accept.", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Accept alert", "Alert Management", "Failed to accept alert: " + e.getMessage());
			throw new FrameworkException("Failed to accept alert", e);
		}
	}

	/** Dismisses an alert, waiting up to the specified timeout. */
	public void dismissAlert(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Dismissing alert", "Alert Management");

			Alert alert = new FluentWait<>(driver)
					.withTimeout(Duration.ofSeconds(timeoutSeconds))
					.pollingEvery(Duration.ofMillis(pollingMillis))
					.ignoring(NoAlertPresentException.class)
					.until(ExpectedConditions.alertIsPresent());
			alert.dismiss();
			logger.info("Dismissed alert.");

			BaseClass.logActionSuccess("Dismissed alert", "Alert Management");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for alert to dismiss.", e);
			BaseClass.logActionFailure("Dismiss alert", "Alert Management", "Timeout waiting for alert to dismiss");
			throw new FrameworkException("Timeout waiting for alert to dismiss.", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Dismiss alert", "Alert Management", "Failed to dismiss alert: " + e.getMessage());
			throw new FrameworkException("Failed to dismiss alert", e);
		}
	}

	/** Retrieves the text of an alert, waiting up to the specified timeout. */
	public String getAlertText(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Getting alert text", "Alert Management");

			Alert alert = new FluentWait<>(driver)
					.withTimeout(Duration.ofSeconds(timeoutSeconds))
					.pollingEvery(Duration.ofMillis(pollingMillis))
					.ignoring(NoAlertPresentException.class)
					.until(ExpectedConditions.alertIsPresent());
			String text = alert.getText();
			logger.info("Retrieved alert text: '{}'", text);

			BaseClass.logActionSuccess("Retrieved alert text: '" + BaseClass.mask(text) + "'", "Alert Management");
			return text;

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for alert to get text.", e);
			BaseClass.logActionFailure("Get alert text", "Alert Management", "Timeout waiting for alert to get text");
			throw new FrameworkException("Timeout waiting for alert to get text.", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Get alert text", "Alert Management", "Failed to get alert text: " + e.getMessage());
			throw new FrameworkException("Failed to get alert text", e);
		}
	}

	/** Sends text to a prompt alert, waiting up to the specified timeout. */
	public void enterAlertText(String text, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(text, "Alert text");
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, text, "Alert text", excelData);

		try {
			BaseClass.logActionStart("Entering text '" + BaseClass.mask(valueToUse) + "' into alert", "Alert Management");

			Alert alert = new FluentWait<>(driver)
					.withTimeout(Duration.ofSeconds(timeoutSeconds))
					.pollingEvery(Duration.ofMillis(pollingMillis))
					.ignoring(NoAlertPresentException.class)
					.until(ExpectedConditions.alertIsPresent());
			alert.sendKeys(valueToUse);
			logger.info("Entered text into alert.");

			BaseClass.logActionSuccess("Entered text into alert", "Alert Management");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting to enter text into alert.", e);
			BaseClass.logActionFailure("Enter alert text", "Alert Management", "Timeout waiting to enter text into alert");
			throw new FrameworkException("Timeout waiting to enter text into alert.", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Enter alert text", "Alert Management", "Failed to enter text into alert: " + e.getMessage());
			throw new FrameworkException("Failed to enter text into alert", e);
		}
	}


	/** Verifies that the current URL exactly matches the expected URL. */
	public void verifyCurrentUrl(String expectedUrl, boolean excelData, String testCaseName) {
		validateInput(expectedUrl, "Expected URL");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrl, "Expected URL", excelData);

		try {
			BaseClass.logActionStart("Verifying current URL equals '" + BaseClass.mask(valueToUse) + "'", "URL Verification");

			String actual = driver.getCurrentUrl();
			if (!actual.equals(valueToUse)) {
				BaseClass.logActionFailure("Verify current URL", "URL Verification", "Expected URL: " + valueToUse + " but was: " + actual);
				throw new FrameworkException("Expected URL: " + valueToUse + " but was: " + actual);
			}
			logger.info("Verified current URL equals '{}'", valueToUse);

			BaseClass.logActionSuccess("Current URL verification passed", "URL Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify current URL", "URL Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("URL verification failed.", e);
			BaseClass.logActionFailure("Verify current URL", "URL Verification", "URL verification failed: " + e.getMessage());
			throw new FrameworkException("URL verification failed.", e);
		}
	}

	/** Verifies that the current URL contains the expected substring. */
	public void verifyCurrentUrlContains(String fragment, boolean excelData, String testCaseName) {
		validateInput(fragment, "URL fragment");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, fragment, "URL fragment", excelData);

		try {
			BaseClass.logActionStart("Verifying current URL contains '" + BaseClass.mask(valueToUse) + "'", "URL Verification");

			String actual = driver.getCurrentUrl();
			if (!actual.contains(valueToUse)) {
				BaseClass.logActionFailure("Verify URL contains", "URL Verification", "URL '" + actual + "' does not contain expected fragment: " + valueToUse);
				throw new FrameworkException("URL '" + actual + "' does not contain expected fragment: " + valueToUse);
			}
			logger.info("Verified current URL contains '{}'", valueToUse);

			BaseClass.logActionSuccess("URL contains verification passed", "URL Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify URL contains", "URL Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("URL fragment verification failed.", e);
			BaseClass.logActionFailure("Verify URL contains", "URL Verification", "URL fragment verification failed: " + e.getMessage());
			throw new FrameworkException("URL fragment verification failed.", e);
		}
	}

	/** Verifies that the page title exactly matches the expected title. */
	public void verifyPageTitle(String expectedTitle, boolean excelData, String testCaseName) {
		validateInput(expectedTitle, "Expected title");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedTitle, "Expected title", excelData);

		try {
			BaseClass.logActionStart("Verifying page title equals '" + BaseClass.mask(valueToUse) + "'", "Title Verification");

			String actual = driver.getTitle();
			if (!actual.equals(valueToUse)) {
				BaseClass.logActionFailure("Verify page title", "Title Verification", "Expected title: '" + valueToUse + "' but was: '" + actual + "'");
				throw new FrameworkException("Expected title: '" + valueToUse + "' but was: '" + actual + "'");
			}
			logger.info("Verified page title equals '{}'", valueToUse);

			BaseClass.logActionSuccess("Page title verification passed", "Title Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify page title", "Title Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Title verification failed.", e);
			BaseClass.logActionFailure("Verify page title", "Title Verification", "Title verification failed: " + e.getMessage());
			throw new FrameworkException("Title verification failed.", e);
		}
	}

	/** Verifies that the page title contains the given fragment. */
	public void verifyPageTitleContains(String fragment, boolean excelData, String testCaseName) {
		validateInput(fragment, "Title fragment");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, fragment, "Title fragment", excelData);

		try {
			BaseClass.logActionStart("Verifying page title contains '" + BaseClass.mask(valueToUse) + "'", "Title Verification");

			String actual = driver.getTitle();
			if (!actual.contains(valueToUse)) {
				BaseClass.logActionFailure("Verify title contains", "Title Verification", "Page title '" + actual + "' does not contain expected fragment: " + valueToUse);
				throw new FrameworkException("Page title '" + actual + "' does not contain expected fragment: " + valueToUse);
			}
			logger.info("Verified page title contains '{}'", valueToUse);

			BaseClass.logActionSuccess("Title contains verification passed", "Title Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify title contains", "Title Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Title fragment verification failed.", e);
			BaseClass.logActionFailure("Verify title contains", "Title Verification", "Title fragment verification failed: " + e.getMessage());
			throw new FrameworkException("Title fragment verification failed.", e);
		}
	}


	/** Waits until the page's readyState is 'complete' within the timeout. */
	public void waitForPageLoadComplete(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Waiting for page load to complete", "Page Loading");

			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> ((JavascriptExecutor) drv)
					.executeScript("return document.readyState").equals("complete"));
			logger.info("Page load complete (readyState=='complete').");

			BaseClass.logActionSuccess("Page load completed", "Page Loading");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for page to load completely.", e);
			BaseClass.logActionFailure("Wait for page load", "Page Loading", "Timeout waiting for page to load completely");
			throw new FrameworkException("Timeout waiting for page to load completely.", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for page load", "Page Loading", "Failed to wait for page load: " + e.getMessage());
			throw new FrameworkException("Failed to wait for page load", e);
		}
	}

	/** Opens the specified URL in a new browser tab and switches to it. */
	public void openUrlInNewTab(String url, boolean excelData, String testCaseName) {
		validateInput(url, "URL");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, url, "URL", excelData);

		try {
			BaseClass.logActionStart("Opening URL '" + valueToUse + "' in new tab", "Tab Management");

			((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", valueToUse);
			switchToNewlyOpenedWindow();
			logger.info("Opened URL '{}' in new tab and switched.", valueToUse);

			BaseClass.logActionSuccess("Opened URL '" + valueToUse + "' in new tab and switched", "Tab Management");

		} catch (Exception e) {
			logger.error("Failed to open URL in new tab.", e);
			BaseClass.logActionFailure("Open URL in new tab", "Tab Management", "Failed to open URL '" + valueToUse + "' in new tab: " + e.getMessage());
			throw new FrameworkException("Failed to open URL in new tab.", e);
		}
	}


	/** Opens the specified URL in a new browser window and switches to it. */
	public void openUrlInNewWindow(String url) {
		validateInput(url, "URL");

		try {
			BaseClass.logActionStart("Opening URL '" + url + "' in new window", "Window Management");

			((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank','noopener,noreferrer');", url);
			switchToNewlyOpenedWindow();
			logger.info("Opened URL '{}' in new window and switched.", url);

			BaseClass.logActionSuccess("Opened URL '" + url + "' in new window and switched", "Window Management");

		} catch (Exception e) {
			logger.error("Failed to open URL in new window.", e);
			BaseClass.logActionFailure("Open URL in new window", "Window Management", "Failed to open URL '" + url + "' in new window: " + e.getMessage());
			throw new FrameworkException("Failed to open URL in new window.", e);
		}
	}

	/** Waits until the number of open windows/tabs equals the expected count. */
	public void waitForNumberOfWindows(int expectedNumber, int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Waiting for number of windows/tabs to be " + expectedNumber, "Window Management");

			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> drv.getWindowHandles().size() == expectedNumber);
			logger.info("Number of windows/tabs is now {}.", expectedNumber);

			BaseClass.logActionSuccess("Number of windows/tabs reached " + expectedNumber, "Window Management");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for number of windows/tabs to be {}", expectedNumber, e);
			BaseClass.logActionFailure("Wait for number of windows", "Window Management", "Timeout waiting for number of windows/tabs to be " + expectedNumber);
			throw new FrameworkException("Timeout waiting for number of windows/tabs to be " + expectedNumber, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for number of windows", "Window Management", "Failed to wait for number of windows: " + e.getMessage());
			throw new FrameworkException("Failed to wait for number of windows", e);
		}
	}

	/** Navigates to URL and validates that the navigation was successful by checking expected URL or title. */
	public void navigateToUrlWithValidation(String url, String expectedTitlePart, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(url, "URL");
		validateInput(expectedTitlePart, "Expected Title Part");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		// Get the actual values to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, url, "URL", excelData);
		String expectedTitleToUse = getInputValue(testCaseName, expectedTitlePart, "Expected Title Part", excelData);

		try {
			BaseClass.logActionStart("Navigating to URL '" + BaseClass.mask(valueToUse) + "' with validation for title containing '" + BaseClass.mask(expectedTitleToUse) + "'", "Navigation");

			driver.get(valueToUse.trim());
			logger.info("Navigated to URL: {}", valueToUse);

			// Wait for page to load and validate title contains expected part
			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> {
				String currentTitle = drv.getTitle();
				return currentTitle != null && currentTitle.contains(expectedTitleToUse);
			});

			String actualTitle = driver.getTitle();
			logger.info("Navigation validated - page title '{}' contains expected part '{}'", actualTitle, expectedTitleToUse);

			BaseClass.logActionSuccess("Navigated to URL with validation successful", "Navigation");

		} catch (TimeoutException e) {
			String actualTitle = driver.getTitle();
			logger.error("Navigation validation failed - timeout waiting for title to contain '{}'. Current title: '{}'", expectedTitleToUse, actualTitle);
			BaseClass.logActionFailure("Navigate to URL with validation", "Navigation", 
					"Timeout waiting for title to contain '" + expectedTitleToUse + "'. Current title: '" + actualTitle + "'");
			throw new FrameworkException("Navigation validation failed - timeout waiting for title to contain '" + expectedTitleToUse + "'. Current title: '" + actualTitle + "'", e);
		} catch (Exception e) {
			logger.error("Failed to navigate to URL with validation: {}", valueToUse, e);
			BaseClass.logActionFailure("Navigate to URL with validation", "Navigation", "Failed to navigate with validation to '" + valueToUse + "': " + e.getMessage());
			throw new FrameworkException("Failed to navigate to URL with validation: " + valueToUse, e);
		}
	}

	/** Navigates to URL and waits for specified element to appear or become visible. */
	public void navigateToUrlAndWaitForElement(String url, String elementXpath, int timeoutSeconds) {
		validateInput(url, "URL");
		validateInput(elementXpath, "Element XPath");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Navigating to URL '" + url + "' and waiting for element", "Navigation");

			driver.get(url.trim());
			logger.info("Navigated to URL: {}", url);

			// Wait for the specified element to be visible
			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath.trim())));

			logger.info("Element with XPath '{}' appeared after navigation to '{}'", elementXpath, url);
			BaseClass.logActionSuccess("Navigated to URL and element appeared", "Navigation");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element '{}' after navigating to URL '{}' within {} seconds", elementXpath, url, timeoutSeconds);
			BaseClass.logActionFailure("Navigate to URL and wait for element", "Navigation", 
					"Timeout waiting for element '" + elementXpath + "' after navigating to '" + url + "'");
			throw new FrameworkException("Timeout waiting for element '" + elementXpath + "' after navigating to URL '" + url + "' within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to navigate to URL and wait for element: {}", url, e);
			BaseClass.logActionFailure("Navigate to URL and wait for element", "Navigation", "Failed to navigate and wait for element: " + e.getMessage());
			throw new FrameworkException("Failed to navigate to URL '" + url + "' and wait for element '" + elementXpath + "'", e);
		}
	}

	/** Refreshes the current page and waits for specified element to appear or become visible. */
	public void refreshPageAndWaitForElement(String elementXpath, int timeoutSeconds) {
		validateInput(elementXpath, "Element XPath");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Refreshing page and waiting for element", "Navigation");

			driver.navigate().refresh();
			logger.info("Refreshed the current page");

			// Wait for the specified element to be visible
			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath.trim())));

			logger.info("Element with XPath '{}' appeared after page refresh", elementXpath);
			BaseClass.logActionSuccess("Refreshed page and element appeared", "Navigation");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for element '{}' after page refresh within {} seconds", elementXpath, timeoutSeconds);
			BaseClass.logActionFailure("Refresh page and wait for element", "Navigation", 
					"Timeout waiting for element '" + elementXpath + "' after page refresh");
			throw new FrameworkException("Timeout waiting for element '" + elementXpath + "' after page refresh within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to refresh page and wait for element: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Refresh page and wait for element", "Navigation", "Failed to refresh page and wait for element: " + e.getMessage());
			throw new FrameworkException("Failed to refresh page and wait for element '" + elementXpath + "'", e);
		}
	}

	/** Navigates backward in browser history and validates the expected URL. */
	public void goBackWithValidation(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedUrl, "Expected URL");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrl, "Expected URL", excelData);

		try {
			BaseClass.logActionStart("Navigating back with URL validation for '" + BaseClass.mask(valueToUse) + "'", "Navigation");

			driver.navigate().back();
			logger.info("Navigated back in browser history");

			// Wait for URL to match expected URL
			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> {
				String currentUrl = drv.getCurrentUrl();
				return currentUrl != null && currentUrl.equals(valueToUse);
			});

			String actualUrl = driver.getCurrentUrl();
			logger.info("Navigation back validated - current URL '{}' matches expected URL", actualUrl);
			BaseClass.logActionSuccess("Navigated back with validation successful", "Navigation");

		} catch (TimeoutException e) {
			String actualUrl = driver.getCurrentUrl();
			logger.error("Navigation back validation failed - timeout waiting for URL '{}'. Current URL: '{}'", valueToUse, actualUrl);
			BaseClass.logActionFailure("Navigate back with validation", "Navigation", 
					"Timeout waiting for URL '" + valueToUse + "'. Current URL: '" + actualUrl + "'");
			throw new FrameworkException("Navigation back validation failed - timeout waiting for URL '" + valueToUse + "'. Current URL: '" + actualUrl + "'", e);
		} catch (Exception e) {
			logger.error("Failed to navigate back with validation: {}", valueToUse, e);
			BaseClass.logActionFailure("Navigate back with validation", "Navigation", "Failed to navigate back with validation: " + e.getMessage());
			throw new FrameworkException("Failed to navigate back with validation for URL: " + valueToUse, e);
		}
	}

	/** Navigates forward in browser history and validates the expected URL. */
	public void goForwardWithValidation(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(expectedUrl, "Expected URL");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrl, "Expected URL", excelData);

		try {
			BaseClass.logActionStart("Navigating forward with URL validation for '" + BaseClass.mask(valueToUse) + "'", "Navigation");

			driver.navigate().forward();
			logger.info("Navigated forward in browser history");

			// Wait for URL to match expected URL
			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> {
				String currentUrl = drv.getCurrentUrl();
				return currentUrl != null && currentUrl.equals(valueToUse);
			});

			String actualUrl = driver.getCurrentUrl();
			logger.info("Navigation forward validated - current URL '{}' matches expected URL", actualUrl);
			BaseClass.logActionSuccess("Navigated forward with validation successful", "Navigation");

		} catch (TimeoutException e) {
			String actualUrl = driver.getCurrentUrl();
			logger.error("Navigation forward validation failed - timeout waiting for URL '{}'. Current URL: '{}'", valueToUse, actualUrl);
			BaseClass.logActionFailure("Navigate forward with validation", "Navigation", 
					"Timeout waiting for URL '" + valueToUse + "'. Current URL: '" + actualUrl + "'");
			throw new FrameworkException("Navigation forward validation failed - timeout waiting for URL '" + valueToUse + "'. Current URL: '" + actualUrl + "'", e);
		} catch (Exception e) {
			logger.error("Failed to navigate forward with validation: {}", valueToUse, e);
			BaseClass.logActionFailure("Navigate forward with validation", "Navigation", "Failed to navigate forward with validation: " + e.getMessage());
			throw new FrameworkException("Failed to navigate forward with validation for URL: " + valueToUse, e);
		}
	}


	/** Waits for page to load completely by checking document ready state and other loading indicators. */
	public void waitForPageToLoad(int timeoutSeconds) {
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		try {
			BaseClass.logActionStart("Waiting for page to load completely within " + timeoutSeconds + " seconds", "Page Loading");

			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> {
				// Check document ready state
				String readyState = (String) ((JavascriptExecutor) drv)
						.executeScript("return document.readyState");

				// Check jQuery if present
				Boolean jQueryReady = (Boolean) ((JavascriptExecutor) drv)
						.executeScript("return typeof jQuery !== 'undefined' ? jQuery.active == 0 : true");

				return "complete".equals(readyState) && jQueryReady;
			});

			logger.info("Page loaded completely within {} seconds", timeoutSeconds);
			BaseClass.logActionSuccess("Page loaded completely", "Page Loading");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for page to load completely within {} seconds", timeoutSeconds);
			BaseClass.logActionFailure("Wait for page to load", "Page Loading", "Timeout waiting for page to load completely");
			throw new FrameworkException("Timeout waiting for page to load completely within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to wait for page to load: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Wait for page to load", "Page Loading", "Failed to wait for page to load: " + e.getMessage());
			throw new FrameworkException("Failed to wait for page to load", e);
		}
	}

	/** Verifies that the current URL contains the expected substring. */
	public void verifyUrlContains(String expectedUrlPart, boolean excelData, String testCaseName) {
		validateInput(expectedUrlPart, "Expected URL Part");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrlPart, "Expected URL Part", excelData);

		try {
			BaseClass.logActionStart("Verifying current URL contains '" + BaseClass.mask(valueToUse) + "'", "URL Verification");

			String actualUrl = driver.getCurrentUrl();
			if (actualUrl == null || !actualUrl.contains(valueToUse)) {
				BaseClass.logActionFailure("Verify URL contains", "URL Verification", "URL '" + actualUrl + "' does not contain expected part: '" + valueToUse + "'");
				throw new FrameworkException("URL '" + actualUrl + "' does not contain expected part: '" + valueToUse + "'");
			}

			logger.info("Verified current URL '{}' contains expected part '{}'", actualUrl, valueToUse);
			BaseClass.logActionSuccess("URL contains verification passed", "URL Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify URL contains", "URL Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("URL contains verification failed: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Verify URL contains", "URL Verification", "URL contains verification failed: " + e.getMessage());
			throw new FrameworkException("URL contains verification failed", e);
		}
	}

	/** Verifies that the current URL exactly equals the expected URL. */
	public void verifyUrlEquals(String expectedUrl, boolean excelData, String testCaseName) {
		validateInput(expectedUrl, "Expected URL");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedUrl, "Expected URL", excelData);

		try {
			BaseClass.logActionStart("Verifying current URL equals '" + BaseClass.mask(valueToUse) + "'", "URL Verification");

			String actualUrl = driver.getCurrentUrl();
			if (!valueToUse.equals(actualUrl)) {
				BaseClass.logActionFailure("Verify URL equals", "URL Verification", "Expected URL: '" + valueToUse + "' but actual URL: '" + actualUrl + "'");
				throw new FrameworkException("Expected URL: '" + valueToUse + "' but actual URL: '" + actualUrl + "'");
			}

			logger.info("Verified current URL equals expected URL: '{}'", valueToUse);
			BaseClass.logActionSuccess("URL equals verification passed", "URL Verification");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify URL equals", "URL Verification", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("URL equals verification failed: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Verify URL equals", "URL Verification", "URL equals verification failed: " + e.getMessage());
			throw new FrameworkException("URL equals verification failed", e);
		}
	}

	/** Gets the page load time using Navigation Timing API and returns time in milliseconds. */
	public long getPageLoadTime() {
		try {
			BaseClass.logActionStart("Getting page load time", "Page Loading");

			// Use Navigation Timing API to get load time
			Long loadTime = (Long) ((JavascriptExecutor) driver).executeScript(
					"return window.performance.timing.loadEventEnd - window.performance.timing.navigationStart;"
					);

			if (loadTime == null || loadTime == 0) {
				logger.warn("Page load time not available or page still loading");
				loadTime = -1L;
			}

			logger.info("Page load time: {} milliseconds", loadTime);
			BaseClass.logActionSuccess("Retrieved page load time: " + loadTime + "ms", "Page Loading");

			return loadTime;

		} catch (Exception e) {
			logger.error("Failed to get page load time: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Get page load time", "Page Loading", "Failed to get page load time: " + e.getMessage());
			throw new FrameworkException("Failed to get page load time", e);
		}
	}

	/** Waits for the current URL to change from the specified current URL within timeout. */
	public void waitForUrlChange(String currentUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(currentUrl, "Current URL");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, currentUrl, "Current URL", excelData);

		try {
			BaseClass.logActionStart("Waiting for URL to change from '" + BaseClass.mask(valueToUse) + "' within " + timeoutSeconds + " seconds", "Navigation");

			new FluentWait<>(driver)
			.withTimeout(Duration.ofSeconds(timeoutSeconds))
			.pollingEvery(Duration.ofMillis(pollingMillis))
			.until(drv -> {
				String actualUrl = drv.getCurrentUrl();
				return actualUrl != null && !actualUrl.equals(valueToUse);
			});

			String newUrl = driver.getCurrentUrl();
			logger.info("URL changed from '{}' to '{}'", valueToUse, newUrl);
			BaseClass.logActionSuccess("URL changed successfully", "Navigation");

		} catch (TimeoutException e) {
			String actualUrl = driver.getCurrentUrl();
			logger.error("Timeout waiting for URL to change from '{}' within {} seconds. Current URL: '{}'", valueToUse, timeoutSeconds, actualUrl);
			BaseClass.logActionFailure("Wait for URL change", "Navigation", 
					"Timeout waiting for URL to change from '" + valueToUse + "'. Current URL: '" + actualUrl + "'");
			throw new FrameworkException("Timeout waiting for URL to change from '" + valueToUse + "' within " + timeoutSeconds + " seconds. Current URL: '" + actualUrl + "'", e);
		} catch (Exception e) {
			logger.error("Failed to wait for URL change: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Wait for URL change", "Navigation", "Failed to wait for URL change: " + e.getMessage());
			throw new FrameworkException("Failed to wait for URL change from: " + valueToUse, e);
		}
	}



}
