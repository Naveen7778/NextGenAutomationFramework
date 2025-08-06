package com.naveensdet.unifiedwebautomation.keywords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * BrowserKeywords - contains browser-level action keywords for alert, windows, tabs,
 * navigation, cookies, browser storage, screenshots, resizing, and other utilities.
 */
public class BrowserKeywords {

	private final WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(BrowserKeywords.class);

	public BrowserKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/** Retrieves an int config property or returns default value if not set or parse error. */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) return defaultValue;
		try {
			return Integer.parseInt(BaseClass.getProps().getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid config for '{}', using default {}", key, defaultValue);
			return defaultValue;
		}
	}

	/** Validates input parameter is not null or empty, else throws FrameworkException. */
	private void validateInput(String val, String paramName) {
		if (val == null || val.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	/** Returns a FluentWait configured with timeout and polling interval from config.properties. */
	private FluentWait<WebDriver> getWait(int timeoutSeconds) {
		int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
		return new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(pollingMillis))
				.ignoring(NoSuchWindowException.class)
				.ignoring(NoAlertPresentException.class)
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
	}

	// === Alert Handling ===

	/** Accepts an alert within the timeout. */
	public void acceptAlert(int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Accepting alert", "Browser Alert");

			getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
			driver.switchTo().alert().accept();
			logger.info("Accepted alert");

			BaseClass.logActionSuccess("Accepted alert", "Browser Alert");

		} catch (TimeoutException e) {
			logger.error("Alert not present to accept within {} seconds", timeoutSeconds);
			BaseClass.logActionFailure("Accept alert", "Browser Alert", "Alert not present within " + timeoutSeconds + " seconds");
			throw new FrameworkException("Alert not present to accept within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Accept alert", "Browser Alert", e.getMessage());
			throw new FrameworkException("Failed to accept alert", e);
		}
	}

	/** Dismisses an alert within the timeout. */
	public void dismissAlert(int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Dismissing alert", "Browser Alert");

			getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
			driver.switchTo().alert().dismiss();
			logger.info("Dismissed alert");

			BaseClass.logActionSuccess("Dismissed alert", "Browser Alert");

		} catch (TimeoutException e) {
			logger.error("Alert not present to dismiss within {} seconds", timeoutSeconds);
			BaseClass.logActionFailure("Dismiss alert", "Browser Alert", "Alert not present within " + timeoutSeconds + " seconds");
			throw new FrameworkException("Alert not present to dismiss within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Dismiss alert", "Browser Alert", e.getMessage());
			throw new FrameworkException("Failed to dismiss alert", e);
		}
	}

	/** Gets alert text within the timeout. */
	public String getAlertText(int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Getting alert text", "Browser Alert");

			getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
			String alertText = driver.switchTo().alert().getText();
			logger.info("Got alert text: {}", alertText);

			BaseClass.logActionSuccess("Retrieved alert text: '" + alertText + "'", "Browser Alert");
			return alertText;

		} catch (TimeoutException e) {
			logger.error("Alert not present to get text within {} seconds", timeoutSeconds);
			BaseClass.logActionFailure("Get alert text", "Browser Alert", "Alert not present within " + timeoutSeconds + " seconds");
			throw new FrameworkException("Alert not present to get text within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Get alert text", "Browser Alert", e.getMessage());
			throw new FrameworkException("Failed to get alert text", e);
		}
	}

	/** Sends keys to alert prompt within the timeout. */
	public void sendKeysToAlert(String keys, int timeoutSeconds) {
		validateInput(keys, "Keys");
		try {
			BaseClass.logActionStart("Sending keys '" + BaseClass.mask(keys) + "' to alert", "Browser Alert");

			getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
			driver.switchTo().alert().sendKeys(keys);
			logger.info("Sent keys to alert");

			BaseClass.logActionSuccess("Sent keys to alert", "Browser Alert");

		} catch (TimeoutException e) {
			logger.error("Alert not present to send keys within {} seconds", timeoutSeconds);
			BaseClass.logActionFailure("Send keys to alert", "Browser Alert", "Alert not present within " + timeoutSeconds + " seconds");
			throw new FrameworkException("Alert not present to send keys within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Send keys to alert", "Browser Alert", e.getMessage());
			throw new FrameworkException("Failed to send keys to alert", e);
		}
	}

	// === Window and Tab Management ===

	/** Switches to window/tab by title within timeout. */
	public void switchToWindowByTitle(String title, int timeoutSeconds) {
		validateInput(title, "Window Title");
		try {
			BaseClass.logActionStart("Switching to window with title '" + title + "'", "Browser Window");

			boolean switched = getWait(timeoutSeconds).until(d -> {
				Set<String> handles = d.getWindowHandles();
				for (String handle : handles) {
					d.switchTo().window(handle);
					if (title.equals(d.getTitle())) {
						return true;
					}
				}
				return false;
			});

			if (!switched) {
				BaseClass.logActionFailure("Switch to window by title", "Browser Window", "Window with title '" + title + "' not found");
				throw new FrameworkException("Window with title '" + title + "' not found");
			}

			logger.info("Switched to window with title '{}'", title);
			BaseClass.logActionSuccess("Switched to window with title '" + title + "'", "Browser Window");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Switch to window by title", "Browser Window", "Timeout waiting for window with title '" + title + "'");
			throw new FrameworkException("Timeout waiting to switch to window with title '" + title + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to window by title", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to switch to window by title", e);
		}
	}

	/** Switches to window/tab by index (0-based) within timeout. */
	public void switchToWindowByIndex(int index, int timeoutSeconds) {
		if (index < 0) {
			BaseClass.logActionFailure("Switch to window by index", "Browser Window", "Window index cannot be negative: " + index);
			throw new FrameworkException("Window index cannot be negative");
		}

		try {
			BaseClass.logActionStart("Switching to window at index " + index, "Browser Window");

			boolean switched = getWait(timeoutSeconds).until(d -> {
				List<String> handles = d.getWindowHandles().stream().toList();
				if (index >= handles.size()) return false;
				d.switchTo().window(handles.get(index));
				return true;
			});

			if (!switched) {
				BaseClass.logActionFailure("Switch to window by index", "Browser Window", "Window with index '" + index + "' not found");
				throw new FrameworkException("Window with index '" + index + "' not found");
			}

			logger.info("Switched to window with index '{}'", index);
			BaseClass.logActionSuccess("Switched to window at index " + index, "Browser Window");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Switch to window by index", "Browser Window", "Timeout waiting for window at index " + index);
			throw new FrameworkException("Timeout waiting to switch to window with index '" + index + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to window by index", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to switch to window by index", e);
		}
	}

	/** Switches to window/tab by handle within timeout. */
	public void switchToWindowByHandle(String windowHandle, int timeoutSeconds) {
		validateInput(windowHandle, "Window Handle");
		try {
			BaseClass.logActionStart("Switching to window with handle '" + windowHandle + "'", "Browser Window");

			boolean switched = getWait(timeoutSeconds).until(d -> {
				Set<String> handles = d.getWindowHandles();
				if (!handles.contains(windowHandle)) return false;
				d.switchTo().window(windowHandle);
				return true;
			});

			if (!switched) {
				BaseClass.logActionFailure("Switch to window by handle", "Browser Window", "Window with handle '" + windowHandle + "' not found");
				throw new FrameworkException("Window with handle '" + windowHandle + "' not found");
			}

			logger.info("Switched to window with handle '{}'", windowHandle);
			BaseClass.logActionSuccess("Switched to window with handle '" + windowHandle + "'", "Browser Window");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Switch to window by handle", "Browser Window", "Timeout waiting for window with handle '" + windowHandle + "'");
			throw new FrameworkException("Timeout waiting to switch to window with handle '" + windowHandle + "'", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to window by handle", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to switch to window by handle", e);
		}
	}

	/** Closes current window and switches to the last remaining window. */
	public void closeCurrentWindowAndSwitchToLast() {
		try {
			String currentHandle = driver.getWindowHandle();
			BaseClass.logActionStart("Closing current window '" + currentHandle + "'", "Browser Window");

			driver.close();
			Set<String> handles = driver.getWindowHandles();

			if (!handles.isEmpty()) {
				String lastHandle = handles.iterator().next();
				driver.switchTo().window(lastHandle);
				logger.info("Closed window '{}' and switched to window '{}'", currentHandle, lastHandle);
				BaseClass.logActionSuccess("Closed window and switched to remaining window", "Browser Window");
			} else {
				logger.warn("No windows remaining after closing current window '{}'", currentHandle);
				BaseClass.logActionSuccess("Closed window (no remaining windows)", "Browser Window");
			}

		} catch (Exception e) {
			BaseClass.logActionFailure("Close current window", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to close current window", e);
		}
	}

	/** Closes a window by index (0-based). */
	public void closeWindowByIndex(int index) {
		if (index < 0) {
			BaseClass.logActionFailure("Close window by index", "Browser Window", "Window index cannot be negative: " + index);
			throw new FrameworkException("Window index cannot be negative");
		}

		try {
			BaseClass.logActionStart("Closing window at index " + index, "Browser Window");

			Set<String> handles = driver.getWindowHandles();
			if (index >= handles.size()) {
				BaseClass.logActionFailure("Close window by index", "Browser Window", "Window index out of bounds: " + index);
				throw new FrameworkException("Window index out of bounds: " + index);
			}

			List<String> handlesList = handles.stream().toList();
			String handleToClose = handlesList.get(index);
			driver.switchTo().window(handleToClose);
			driver.close();
			logger.info("Closed window at index {}", index);

			// Optionally switch to a remaining window:
			Set<String> remaining = driver.getWindowHandles();
			if (!remaining.isEmpty()) {
				driver.switchTo().window(remaining.iterator().next());
			}

			BaseClass.logActionSuccess("Closed window at index " + index, "Browser Window");

		} catch (Exception e) {
			BaseClass.logActionFailure("Close window by index", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to close window by index", e);
		}
	}

	/** Gets the count of all open windows/tabs. */
	public int getWindowCount() {
		try {
			BaseClass.logActionStart("Getting window count", "Browser Window");

			int count = driver.getWindowHandles().size();
			logger.info("Number of open windows/tabs: {}", count);

			BaseClass.logActionSuccess("Retrieved window count: " + count, "Browser Window");
			return count;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get window count", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to get window count", e);
		}
	}

	/** Gets the current window handle. */
	public String getCurrentWindowHandle() {
		try {
			BaseClass.logActionStart("Getting current window handle", "Browser Window");

			String handle = driver.getWindowHandle();
			logger.info("Current window handle: {}", handle);

			BaseClass.logActionSuccess("Retrieved current window handle: " + handle, "Browser Window");
			return handle;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get current window handle", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to get current window handle", e);
		}
	}

	/** Gets all window handles as a Set<String>. */
	public Set<String> getAllWindowHandles() {
		try {
			BaseClass.logActionStart("Getting all window handles", "Browser Window");

			Set<String> handles = driver.getWindowHandles();
			logger.info("Window handles: {}", handles);

			BaseClass.logActionSuccess("Retrieved all window handles (count: " + handles.size() + ")", "Browser Window");
			return handles;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get all window handles", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to get all window handles", e);
		}
	}

	// === Tab Operations ===

	/** Opens a new tab by executing JavaScript. */
	public void openNewTab() {
		try {
			BaseClass.logActionStart("Opening new tab", "Browser Tab");

			((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
			logger.info("Opened new tab");

			// Switch to last tab automatically
			List<String> handles = driver.getWindowHandles().stream().toList();
			driver.switchTo().window(handles.get(handles.size() - 1));

			BaseClass.logActionSuccess("Opened new tab and switched to it", "Browser Tab");

		} catch (Exception e) {
			BaseClass.logActionFailure("Open new tab", "Browser Tab", e.getMessage());
			throw new FrameworkException("Failed to open new tab", e);
		}
	}

	/** Switches to next tab cyclically. */
	public void switchToNextTab() {
		try {
			BaseClass.logActionStart("Switching to next tab", "Browser Tab");

			List<String> handles = driver.getWindowHandles().stream().toList();
			String currentHandle = driver.getWindowHandle();
			int idx = handles.indexOf(currentHandle);
			int nextIdx = (idx + 1) % handles.size();
			driver.switchTo().window(handles.get(nextIdx));
			logger.info("Switched to next tab with handle '{}'", handles.get(nextIdx));

			BaseClass.logActionSuccess("Switched to next tab", "Browser Tab");

		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to next tab", "Browser Tab", e.getMessage());
			throw new FrameworkException("Failed to switch to next tab", e);
		}
	}

	/** Switches to previous tab cyclically. */
	public void switchToPreviousTab() {
		try {
			BaseClass.logActionStart("Switching to previous tab", "Browser Tab");

			List<String> handles = driver.getWindowHandles().stream().toList();
			String currentHandle = driver.getWindowHandle();
			int idx = handles.indexOf(currentHandle);
			int prevIdx = (idx - 1 + handles.size()) % handles.size();
			driver.switchTo().window(handles.get(prevIdx));
			logger.info("Switched to previous tab with handle '{}'", handles.get(prevIdx));

			BaseClass.logActionSuccess("Switched to previous tab", "Browser Tab");

		} catch (Exception e) {
			BaseClass.logActionFailure("Switch to previous tab", "Browser Tab", e.getMessage());
			throw new FrameworkException("Failed to switch to previous tab", e);
		}
	}

	/** Gets the current URL of the browser. */
	public String getCurrentUrl() {
		try {
			BaseClass.logActionStart("Getting current URL", "Browser Navigation");

			String url = driver.getCurrentUrl();
			logger.info("Current URL: {}", url);

			BaseClass.logActionSuccess("Retrieved current URL: " + url, "Browser Navigation");
			return url;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get current URL", "Browser Navigation", e.getMessage());
			throw new FrameworkException("Failed to get current URL", e);
		}
	}

	/** Gets the current page title. */
	public String getPageTitle() {
		try {
			BaseClass.logActionStart("Getting page title", "Browser Navigation");

			String title = driver.getTitle();
			logger.info("Page title: {}", title);

			BaseClass.logActionSuccess("Retrieved page title: " + title, "Browser Navigation");
			return title;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get page title", "Browser Navigation", e.getMessage());
			throw new FrameworkException("Failed to get page title", e);
		}
	}

	// === Cookie Management ===

	/** Adds a cookie to the browser. */
	public void addCookie(String name, String value, String domain, String path, Date expiry, boolean isSecure) {
		validateInput(name, "Cookie Name");
		validateInput(value, "Cookie Value");
		try {
			BaseClass.logActionStart("Adding cookie '" + name + "' with value '" + BaseClass.mask(value) + "'", "Browser Cookie");

			Cookie cookie = new Cookie.Builder(name, value)
					.domain(domain != null && !domain.isEmpty() ? domain : null)
					.path(path != null && !path.isEmpty() ? path : "/")
					.expiresOn(expiry)
					.isSecure(isSecure)
					.build();
			driver.manage().addCookie(cookie);
			logger.info("Added cookie: {}", cookie);

			BaseClass.logActionSuccess("Added cookie '" + name + "'", "Browser Cookie");

		} catch (Exception e) {
			BaseClass.logActionFailure("Add cookie", "Browser Cookie", "Failed to add cookie '" + name + "': " + e.getMessage());
			throw new FrameworkException("Failed to add cookie: " + name, e);
		}
	}

	/** Deletes a cookie by its name. */
	public void deleteCookieByName(String name) {
		validateInput(name, "Cookie Name");
		try {
			BaseClass.logActionStart("Deleting cookie with name '" + name + "'", "Browser Cookie");

			driver.manage().deleteCookieNamed(name);
			logger.info("Deleted cookie with name '{}'", name);

			BaseClass.logActionSuccess("Deleted cookie '" + name + "'", "Browser Cookie");

		} catch (Exception e) {
			BaseClass.logActionFailure("Delete cookie by name", "Browser Cookie", "Failed to delete cookie '" + name + "': " + e.getMessage());
			throw new FrameworkException("Failed to delete cookie: " + name, e);
		}
	}

	/** Deletes all cookies. */
	public void deleteAllCookies() {
		try {
			BaseClass.logActionStart("Deleting all cookies", "Browser Cookie");

			driver.manage().deleteAllCookies();
			logger.info("Deleted all cookies");

			BaseClass.logActionSuccess("Deleted all cookies", "Browser Cookie");

		} catch (Exception e) {
			BaseClass.logActionFailure("Delete all cookies", "Browser Cookie", e.getMessage());
			throw new FrameworkException("Failed to delete all cookies", e);
		}
	}

	/** Gets a cookie by its name. */
	public Cookie getCookieByName(String name) {
		validateInput(name, "Cookie Name");
		try {
			BaseClass.logActionStart("Getting cookie with name '" + name + "'", "Browser Cookie");

			Cookie cookie = driver.manage().getCookieNamed(name);
			logger.info("Got cookie: {}", cookie);

			BaseClass.logActionSuccess("Retrieved cookie '" + name + "'", "Browser Cookie");
			return cookie;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get cookie by name", "Browser Cookie", "Failed to get cookie '" + name + "': " + e.getMessage());
			throw new FrameworkException("Failed to get cookie: " + name, e);
		}
	}

	/** Gets all cookies. */
	public Set<Cookie> getAllCookies() {
		try {
			BaseClass.logActionStart("Getting all cookies", "Browser Cookie");

			Set<Cookie> cookies = driver.manage().getCookies();
			logger.info("Got all cookies, count: {}", cookies.size());

			BaseClass.logActionSuccess("Retrieved all cookies (count: " + cookies.size() + ")", "Browser Cookie");
			return cookies;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get all cookies", "Browser Cookie", e.getMessage());
			throw new FrameworkException("Failed to get all cookies", e);
		}
	}

	// === Screenshot and Page Source ===

	/** Takes a screenshot and saves to the specified file path. */
	public void takeScreenshot(String filePath) {
		validateInput(filePath, "File Path");
		try {
			BaseClass.logActionStart("Taking screenshot and saving to '" + filePath + "'", "Browser Screenshot");

			TakesScreenshot ts = (TakesScreenshot) driver;
			File srcFile = ts.getScreenshotAs(OutputType.FILE);
			Path dest = Paths.get(filePath);
			Files.createDirectories(dest.getParent());
			Files.copy(srcFile.toPath(), dest);
			logger.info("Screenshot saved to {}", filePath);

			BaseClass.logActionSuccess("Screenshot saved to '" + filePath + "'", "Browser Screenshot");

		} catch (IOException | WebDriverException e) {
			logger.error("Failed to save screenshot to {}", filePath, e);
			BaseClass.logActionFailure("Take screenshot", "Browser Screenshot", "Failed to save screenshot to '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to save screenshot to " + filePath, e);
		}
	}

	/** Saves page source to the specified file path. */
	public void savePageSource(String filePath) {
		validateInput(filePath, "File Path");
		try {
			BaseClass.logActionStart("Saving page source to '" + filePath + "'", "Browser Page Source");

			String pageSource = driver.getPageSource();
			Path path = Paths.get(filePath);
			Files.createDirectories(path.getParent());
			Files.writeString(path, pageSource);
			logger.info("Page source saved to {}", filePath);

			BaseClass.logActionSuccess("Page source saved to '" + filePath + "'", "Browser Page Source");

		} catch (IOException e) {
			logger.error("Failed to save page source to {}", filePath, e);
			BaseClass.logActionFailure("Save page source", "Browser Page Source", "Failed to save page source to '" + filePath + "': " + e.getMessage());
			throw new FrameworkException("Failed to save page source to " + filePath, e);
		}
	}

	// === Browser Storage ===

	/** Clears browser local storage. */
	public void clearLocalStorage() {
		try {
			BaseClass.logActionStart("Clearing local storage", "Browser Storage");

			executeScript("window.localStorage.clear();");
			logger.info("Cleared local storage");

			BaseClass.logActionSuccess("Cleared local storage", "Browser Storage");

		} catch (Exception e) {
			BaseClass.logActionFailure("Clear local storage", "Browser Storage", e.getMessage());
			throw new FrameworkException("Failed to clear local storage", e);
		}
	}

	/** Clears browser session storage. */
	public void clearSessionStorage() {
		try {
			BaseClass.logActionStart("Clearing session storage", "Browser Storage");

			executeScript("window.sessionStorage.clear();");
			logger.info("Cleared session storage");

			BaseClass.logActionSuccess("Cleared session storage", "Browser Storage");

		} catch (Exception e) {
			BaseClass.logActionFailure("Clear session storage", "Browser Storage", e.getMessage());
			throw new FrameworkException("Failed to clear session storage", e);
		}
	}

	/** Gets a local storage item by key. */
	public String getLocalStorageItem(String key) {
		validateInput(key, "Local Storage Key");
		try {
			BaseClass.logActionStart("Getting local storage item '" + key + "'", "Browser Storage");

			String value = (String) executeScript("return window.localStorage.getItem(arguments[0]);", key);
			logger.info("Got local storage item '{}' with value '{}'", key, value);

			BaseClass.logActionSuccess("Retrieved local storage item '" + key + "' with value '" + BaseClass.mask(value) + "'", "Browser Storage");
			return value;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get local storage item", "Browser Storage", "Failed to get local storage item '" + key + "': " + e.getMessage());
			throw new FrameworkException("Failed to get local storage item: " + key, e);
		}
	}

	/** Gets a session storage item by key. */
	public String getSessionStorageItem(String key) {
		validateInput(key, "Session Storage Key");
		try {
			BaseClass.logActionStart("Getting session storage item '" + key + "'", "Browser Storage");

			String value = (String) executeScript("return window.sessionStorage.getItem(arguments[0]);", key);
			logger.info("Got session storage item '{}' with value '{}'", key, value);

			BaseClass.logActionSuccess("Retrieved session storage item '" + key + "' with value '" + BaseClass.mask(value) + "'", "Browser Storage");
			return value;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get session storage item", "Browser Storage", "Failed to get session storage item '" + key + "': " + e.getMessage());
			throw new FrameworkException("Failed to get session storage item: " + key, e);
		}
	}

	/** Sets a local storage item (key-value pair). */
	public void setLocalStorageItem(String key, String value) {
		validateInput(key, "Local Storage Key");
		validateInput(value, "Local Storage Value");
		try {
			BaseClass.logActionStart("Setting local storage item '" + key + "' to '" + BaseClass.mask(value) + "'", "Browser Storage");

			executeScript("window.localStorage.setItem(arguments[0], arguments[1]);", key, value);
			logger.info("Set local storage item '{}' to '{}'", key, value);

			BaseClass.logActionSuccess("Set local storage item '" + key + "'", "Browser Storage");

		} catch (Exception e) {
			BaseClass.logActionFailure("Set local storage item", "Browser Storage", "Failed to set local storage item '" + key + "': " + e.getMessage());
			throw new FrameworkException("Failed to set local storage item: " + key, e);
		}
	}

	/** Sets a session storage item (key-value pair). */
	public void setSessionStorageItem(String key, String value) {
		validateInput(key, "Session Storage Key");
		validateInput(value, "Session Storage Value");
		try {
			BaseClass.logActionStart("Setting session storage item '" + key + "' to '" + BaseClass.mask(value) + "'", "Browser Storage");

			executeScript("window.sessionStorage.setItem(arguments[0], arguments[1]);", key, value);
			logger.info("Set session storage item '{}' to '{}'", key, value);

			BaseClass.logActionSuccess("Set session storage item '" + key + "'", "Browser Storage");

		} catch (Exception e) {
			BaseClass.logActionFailure("Set session storage item", "Browser Storage", "Failed to set session storage item '" + key + "': " + e.getMessage());
			throw new FrameworkException("Failed to set session storage item: " + key, e);
		}
	}

	private Object executeScript(String script, Object... args) {
		return ((JavascriptExecutor) driver).executeScript(script, args);
	}

	// === Browser window manipulation ===

	/** Maximizes the browser window. */
	public void maximizeWindow() {
		try {
			BaseClass.logActionStart("Maximizing browser window", "Browser Window");

			driver.manage().window().maximize();
			logger.info("Maximized browser window");

			BaseClass.logActionSuccess("Maximized browser window", "Browser Window");

		} catch (Exception e) {
			BaseClass.logActionFailure("Maximize window", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to maximize browser window", e);
		}
	}

	/** Minimizes the browser window. */
	public void minimizeWindow() {
		try {
			BaseClass.logActionStart("Minimizing browser window", "Browser Window");

			driver.manage().window().minimize();
			logger.info("Minimized browser window");

			BaseClass.logActionSuccess("Minimized browser window", "Browser Window");

		} catch (Exception e) {
			BaseClass.logActionFailure("Minimize window", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to minimize browser window", e);
		}
	}

	/** Sets browser window to fullscreen. */
	public void fullscreenWindow() {
		try {
			BaseClass.logActionStart("Setting browser window to fullscreen", "Browser Window");

			driver.manage().window().fullscreen();
			logger.info("Set browser window to fullscreen");

			BaseClass.logActionSuccess("Set browser window to fullscreen", "Browser Window");

		} catch (Exception e) {
			BaseClass.logActionFailure("Fullscreen window", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to set browser window to fullscreen", e);
		}
	}

	/** Resizes browser window to given width and height. */
	public void resizeWindow(int width, int height) {
		if (width <= 0 || height <= 0) {
			BaseClass.logActionFailure("Resize window", "Browser Window", "Width and height must be positive integers");
			throw new FrameworkException("Width and height must be positive integers");
		}

		try {
			BaseClass.logActionStart("Resizing browser window to " + width + "x" + height, "Browser Window");

			driver.manage().window().setSize(new Dimension(width, height));
			logger.info("Resized browser window to {}x{}", width, height);

			BaseClass.logActionSuccess("Resized browser window to " + width + "x" + height, "Browser Window");

		} catch (Exception e) {
			BaseClass.logActionFailure("Resize window", "Browser Window", "Failed to resize to " + width + "x" + height + ": " + e.getMessage());
			throw new FrameworkException("Failed to resize browser window", e);
		}
	}

	/** Moves browser window to (x,y) coordinates. */
	public void moveWindow(int x, int y) {
		if (x < 0 || y < 0) {
			BaseClass.logActionFailure("Move window", "Browser Window", "Window coordinates must be non-negative");
			throw new FrameworkException("Window coordinates must be non-negative");
		}

		try {
			BaseClass.logActionStart("Moving browser window to position (" + x + "," + y + ")", "Browser Window");

			driver.manage().window().setPosition(new Point(x, y));
			logger.info("Moved browser window to ({},{})", x, y);

			BaseClass.logActionSuccess("Moved browser window to (" + x + "," + y + ")", "Browser Window");

		} catch (Exception e) {
			BaseClass.logActionFailure("Move window", "Browser Window", "Failed to move to (" + x + "," + y + "): " + e.getMessage());
			throw new FrameworkException("Failed to move browser window", e);
		}
	}

	/** Scrolls browser window to given (x,y) coordinates using JavaScript. */
	public void scrollTo(int x, int y) {
		try {
			BaseClass.logActionStart("Scrolling to position (" + x + "," + y + ")", "Browser Scroll");

			executeScript("window.scrollTo(arguments[0], arguments[1]);", x, y);
			logger.info("Scrolled to position ({},{})", x, y);

			BaseClass.logActionSuccess("Scrolled to position (" + x + "," + y + ")", "Browser Scroll");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll to position", "Browser Scroll", "Failed to scroll to (" + x + "," + y + "): " + e.getMessage());
			throw new FrameworkException("Failed to scroll to position", e);
		}
	}

	/** Scrolls to top of the page. */
	public void scrollToTop() {
		try {
			BaseClass.logActionStart("Scrolling to top of page", "Browser Scroll");

			executeScript("window.scrollTo(0, 0);");
			logger.info("Scrolled to top of the page");

			BaseClass.logActionSuccess("Scrolled to top of page", "Browser Scroll");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll to top", "Browser Scroll", e.getMessage());
			throw new FrameworkException("Failed to scroll to top", e);
		}
	}

	/** Scrolls to bottom of the page. */
	public void scrollToBottom() {
		try {
			BaseClass.logActionStart("Scrolling to bottom of page", "Browser Scroll");

			executeScript("window.scrollTo(0, document.body.scrollHeight);");
			logger.info("Scrolled to bottom of the page");

			BaseClass.logActionSuccess("Scrolled to bottom of page", "Browser Scroll");

		} catch (Exception e) {
			BaseClass.logActionFailure("Scroll to bottom", "Browser Scroll", e.getMessage());
			throw new FrameworkException("Failed to scroll to bottom", e);
		}
	}

	// === Browser Information ===

	/** Gets the browser name from user agent string. */
	public String getBrowserName() {
		try {
			BaseClass.logActionStart("Getting browser name", "Browser Information");

			String userAgent = (String) executeScript("return navigator.userAgent;");
			// crude extraction
			String name = "Unknown";
			if (userAgent != null) {
				if (userAgent.contains("Chrome")) name = "Chrome";
				else if (userAgent.contains("Firefox")) name = "Firefox";
				else if (userAgent.contains("Safari")) name = "Safari";
				else if (userAgent.contains("Edge")) name = "Edge";
				else if (userAgent.contains("MSIE") || userAgent.contains("Trident"))
					name = "Internet Explorer";
			}
			logger.info("Browser name detected: {}", name);

			BaseClass.logActionSuccess("Detected browser name: " + name, "Browser Information");
			return name;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get browser name", "Browser Information", e.getMessage());
			throw new FrameworkException("Failed to get browser name", e);
		}
	}

	/** Gets browser version from user agent string. */
	public String getBrowserVersion() {
		try {
			BaseClass.logActionStart("Getting browser version", "Browser Information");

			String userAgent = (String) executeScript("return navigator.userAgent;");
			if (userAgent == null) return "Unknown";

			String version = "Unknown";
			// crude extraction per browser
			try {
				String[] tokens = userAgent.split(" ");
				for (String token : tokens) {
					if (token.contains("Chrome/") || token.contains("Firefox/") || token.contains("Version/")) {
						version = token.split("/")[1];
						break;
					}
				}
			} catch (Exception e) {
				logger.warn("Could not parse browser version from userAgent '{}'", userAgent, e);
			}
			logger.info("Browser version detected: {}", version);

			BaseClass.logActionSuccess("Detected browser version: " + version, "Browser Information");
			return version;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get browser version", "Browser Information", e.getMessage());
			throw new FrameworkException("Failed to get browser version", e);
		}
	}

	/** Gets current browser window position. */
	public Point getWindowPosition() {
		try {
			BaseClass.logActionStart("Getting window position", "Browser Window");

			Point pos = driver.manage().window().getPosition();
			logger.info("Window position: {}", pos);

			BaseClass.logActionSuccess("Retrieved window position: (" + pos.getX() + "," + pos.getY() + ")", "Browser Window");
			return pos;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get window position", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to get window position", e);
		}
	}

	/** Gets current browser window size. */
	public Dimension getWindowSize() {
		try {
			BaseClass.logActionStart("Getting window size", "Browser Window");

			Dimension size = driver.manage().window().getSize();
			logger.info("Window size: {}", size);

			BaseClass.logActionSuccess("Retrieved window size: " + size.getWidth() + "x" + size.getHeight(), "Browser Window");
			return size;

		} catch (Exception e) {
			BaseClass.logActionFailure("Get window size", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to get window size", e);
		}
	}

	// === Utility verification methods (non-throwing checks) ===

	/** Checks if alert is present within the timeout. */
	public boolean isAlertPresent(int timeoutSeconds) {
		try {
			BaseClass.logActionStart("Checking if alert is present", "Browser Alert");

			getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
			logger.info("Alert is present");

			BaseClass.logActionSuccess("Alert is present", "Browser Alert");
			return true;

		} catch (TimeoutException e) {
			logger.info("Alert not present within timeout");
			BaseClass.logActionSuccess("Alert is not present within timeout", "Browser Alert");
			return false;
		} catch (Exception e) {
			BaseClass.logActionFailure("Check if alert is present", "Browser Alert", e.getMessage());
			return false;
		}
	}

	/** Checks if a window with the specified title exists within the timeout. */
	public boolean isWindowWithTitlePresent(String title, int timeoutSeconds) {
		validateInput(title, "Window Title");
		try {
			BaseClass.logActionStart("Checking if window with title '" + title + "' is present", "Browser Window");

			boolean present = getWait(timeoutSeconds).until(driver -> {
				Set<String> handles = driver.getWindowHandles();
				for (String handle : handles) {
					driver.switchTo().window(handle);
					if (title.equals(driver.getTitle())) {
						logger.info("Found window with title '{}'", title);
						return true;
					}
				}
				return false;
			});

			BaseClass.logActionSuccess("Window with title '" + title + "' " + (present ? "found" : "not found"), "Browser Window");
			return present;

		} catch (Exception e) {
			logger.info("Window with title '{}' not found within timeout", title);
			BaseClass.logActionSuccess("Window with title '" + title + "' not found within timeout", "Browser Window");
			return false;
		}
	}

	/** Waits for the window count to match expected value. */
	public boolean waitForWindowCount(int expectedCount, int timeoutSeconds) {
		if (expectedCount < 0) {
			BaseClass.logActionFailure("Wait for window count", "Browser Window", "Expected window count must be non-negative");
			throw new FrameworkException("Expected window count must be non-negative");
		}

		try {
			BaseClass.logActionStart("Waiting for window count to reach " + expectedCount, "Browser Window");

			boolean matched = getWait(timeoutSeconds).until(driver -> driver.getWindowHandles().size() == expectedCount);
			logger.info("Window count {} achieved", expectedCount);

			BaseClass.logActionSuccess("Window count reached " + expectedCount, "Browser Window");
			return matched;

		} catch (TimeoutException e) {
			logger.warn("Window count did not reach {} within timeout", expectedCount);
			BaseClass.logActionSuccess("Window count did not reach " + expectedCount + " within timeout", "Browser Window");
			return false;
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for window count", "Browser Window", e.getMessage());
			return false;
		}
	}

	/** Sets browser window size to specified width and height in pixels. */
	public void setWindowSize(int width, int height) {
		if (width <= 0) {
			throw new FrameworkException("Width must be positive, provided: " + width);
		}
		if (height <= 0) {
			throw new FrameworkException("Height must be positive, provided: " + height);
		}

		try {
			BaseClass.logActionStart("Setting window size to " + width + "x" + height, "Browser Window");

			Dimension dimension = new Dimension(width, height);
			driver.manage().window().setSize(dimension);

			logger.info("Set window size to {}x{}", width, height);
			BaseClass.logActionSuccess("Set window size", "Browser Window");

		} catch (Exception e) {
			logger.error("Failed to set window size to {}x{}: {}", width, height, e.getMessage(), e);
			BaseClass.logActionFailure("Set window size", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to set window size to " + width + "x" + height, e);
		}
	}

	/** Sets browser window position to specified x and y coordinates on screen. */
	public void setWindowPosition(int x, int y) {
		try {
			BaseClass.logActionStart("Setting window position to (" + x + ", " + y + ")", "Browser Window");

			Point position = new Point(x, y);
			driver.manage().window().setPosition(position);

			logger.info("Set window position to ({}, {})", x, y);
			BaseClass.logActionSuccess("Set window position", "Browser Window");

		} catch (Exception e) {
			logger.error("Failed to set window position to ({}, {}): {}", x, y, e.getMessage(), e);
			BaseClass.logActionFailure("Set window position", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to set window position to (" + x + ", " + y + ")", e);
		}
	}

	/** Closes the current browser window. */
	public void closeCurrentWindow() {
		try {
			BaseClass.logActionStart("Closing current window", "Browser Window");

			driver.close();

			logger.info("Closed current browser window");
			BaseClass.logActionSuccess("Closed current window", "Browser Window");

		} catch (Exception e) {
			logger.error("Failed to close current window: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Close current window", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to close current window", e);
		}
	}

	/** Quits the browser completely and terminates the WebDriver session. */
	public void quitBrowser() {
		try {
			BaseClass.logActionStart("Quitting browser and terminating WebDriver session", "Browser Window");

			driver.quit();

			logger.info("Browser quit successfully and WebDriver session terminated");
			BaseClass.logActionSuccess("Quit browser", "Browser Window");

		} catch (Exception e) {
			logger.error("Failed to quit browser: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Quit browser", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to quit browser", e);
		}
	}

	/** Opens a new browser window using JavaScript executor. */
	public void openNewWindow() {
		try {
			BaseClass.logActionStart("Opening new browser window", "Browser Window");

			// Use JavaScript to open new window
			executeScript("window.open('', '_blank', 'width=' + window.innerWidth + ',height=' + window.innerHeight);");

			logger.info("Opened new browser window");
			BaseClass.logActionSuccess("Opened new window", "Browser Window");

		} catch (Exception e) {
			logger.error("Failed to open new window: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Open new window", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to open new window", e);
		}
	}

	/** Switches to browser tab at specified index (0-based indexing). */
	public void switchToTabByIndex(int index) {
		if (index < 0) {
			throw new FrameworkException("Tab index cannot be negative, provided: " + index);
		}

		try {
			BaseClass.logActionStart("Switching to tab at index " + index, "Browser Window");

			Set<String> windowHandles = driver.getWindowHandles();
			List<String> windowList = new ArrayList<>(windowHandles);

			if (index >= windowList.size()) {
				throw new FrameworkException("Tab index " + index + " is out of bounds. Total tabs: " + windowList.size());
			}

			String targetWindow = windowList.get(index);
			driver.switchTo().window(targetWindow);

			logger.info("Switched to tab at index {}", index);
			BaseClass.logActionSuccess("Switched to tab at index " + index, "Browser Window");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Switch to tab by index", "Browser Window", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Failed to switch to tab at index {}: {}", index, e.getMessage(), e);
			BaseClass.logActionFailure("Switch to tab by index", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to switch to tab at index " + index, e);
		}
	}

	/** Switches to browser window that contains the specified URL within timeout. */
	public void switchToWindowByUrl(String url, int timeoutSeconds) {
		validateInput(url, "URL");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		try {
			BaseClass.logActionStart("Switching to window with URL containing: " + url, "Browser Window");

			// Wait for window with matching URL to be available
			getWait(timeoutSeconds).until(driver -> {
				Set<String> windowHandles = driver.getWindowHandles();
				for (String handle : windowHandles) {
					driver.switchTo().window(handle);
					String currentUrl = driver.getCurrentUrl();
					if (currentUrl != null && currentUrl.contains(url)) {
						return true;
					}
				}
				return false;
			});

			logger.info("Switched to window with URL containing: {}", url);
			BaseClass.logActionSuccess("Switched to window by URL", "Browser Window");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for window with URL containing '{}' within {} seconds", url, timeoutSeconds);
			BaseClass.logActionFailure("Switch to window by URL", "Browser Window", "Timeout waiting for window with URL: " + url);
			throw new FrameworkException("Timeout waiting for window with URL containing '" + url + "' within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to switch to window with URL {}: {}", url, e.getMessage(), e);
			BaseClass.logActionFailure("Switch to window by URL", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to switch to window with URL: " + url, e);
		}
	}

	/** Closes all browser windows except the main (first opened) window and switches back to it. */
	public void closeAllWindowsExceptMain() {
		try {
			BaseClass.logActionStart("Closing all windows except main window", "Browser Window");

			Set<String> windowHandles = driver.getWindowHandles();
			List<String> windowList = new ArrayList<>(windowHandles);

			if (windowList.size() <= 1) {
				logger.info("Only one window present, no additional windows to close");
				BaseClass.logActionSuccess("No additional windows to close", "Browser Window");
				return;
			}

			String mainWindow = windowList.get(0); // First window is considered main
			int closedCount = 0;

			// Close all windows except the main window
			for (int i = 1; i < windowList.size(); i++) {
				String windowHandle = windowList.get(i);
				driver.switchTo().window(windowHandle);
				driver.close();
				closedCount++;
			}

			// Switch back to main window
			driver.switchTo().window(mainWindow);

			logger.info("Closed {} additional windows and switched back to main window", closedCount);
			BaseClass.logActionSuccess("Closed " + closedCount + " windows, returned to main", "Browser Window");

		} catch (Exception e) {
			logger.error("Failed to close additional windows: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Close all windows except main", "Browser Window", e.getMessage());
			throw new FrameworkException("Failed to close all windows except main", e);
		}
	}

	/** Enters text in alert prompt within specified timeout. */
	public void enterTextInAlert(String text, int timeoutSeconds) {
		validateInput(text, "Alert Text");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		try {
			BaseClass.logActionStart("Entering text '" + BaseClass.mask(text) + "' in alert prompt", "Browser Alert");

			// Wait for alert to be present and switch to it
			Alert alert = getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());
			alert.sendKeys(text);

			logger.info("Entered text in alert prompt: '{}'", text);
			BaseClass.logActionSuccess("Entered text in alert", "Browser Alert");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for alert to enter text within {} seconds", timeoutSeconds);
			BaseClass.logActionFailure("Enter text in alert", "Browser Alert", "Timeout waiting for alert: " + timeoutSeconds + "s");
			throw new FrameworkException("Timeout waiting for alert to enter text within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to enter text in alert: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Enter text in alert", "Browser Alert", e.getMessage());
			throw new FrameworkException("Failed to enter text in alert", e);
		}
	}

	/** Waits for alert to appear within specified timeout. */
	public void waitForAlert(int timeoutSeconds) {
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		try {
			BaseClass.logActionStart("Waiting for alert to appear within " + timeoutSeconds + " seconds", "Browser Alert");

			// Wait for alert to be present
			getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());

			logger.info("Alert appeared within {} seconds", timeoutSeconds);
			BaseClass.logActionSuccess("Alert appeared", "Browser Alert");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for alert to appear within {} seconds", timeoutSeconds);
			BaseClass.logActionFailure("Wait for alert", "Browser Alert", "Timeout waiting for alert: " + timeoutSeconds + "s");
			throw new FrameworkException("Timeout waiting for alert to appear within " + timeoutSeconds + " seconds", e);
		} catch (Exception e) {
			logger.error("Failed to wait for alert: {}", e.getMessage(), e);
			BaseClass.logActionFailure("Wait for alert", "Browser Alert", e.getMessage());
			throw new FrameworkException("Failed to wait for alert", e);
		}
	}

	/** Handles alert with specified action (accept, dismiss, or get text) within timeout. */
	public void handleAlert(String action, int timeoutSeconds) {
		validateInput(action, "Alert Action");
		if (timeoutSeconds <= 0) {
			throw new FrameworkException("Timeout must be positive, provided: " + timeoutSeconds);
		}

		try {
			BaseClass.logActionStart("Handling alert with action '" + action + "'", "Browser Alert");

			// Wait for alert to be present
			Alert alert = getWait(timeoutSeconds).until(ExpectedConditions.alertIsPresent());

			String actionLower = action.toLowerCase().trim();
			switch (actionLower) {
			case "accept":
			case "ok":
				alert.accept();
				logger.info("Accepted alert");
				break;

			case "dismiss":
			case "cancel":
				alert.dismiss();
				logger.info("Dismissed alert");
				break;

			case "gettext":
			case "get":
				String alertText = alert.getText();
				logger.info("Retrieved alert text: '{}'", alertText);
				BaseClass.logActionSuccess("Retrieved alert text: '" + alertText + "'", "Browser Alert");
				return;

			default:
				throw new FrameworkException("Invalid alert action: '" + action + "'. Valid actions: accept, dismiss, gettext");
			}

			BaseClass.logActionSuccess("Handled alert with action '" + action + "'", "Browser Alert");

		} catch (TimeoutException e) {
			logger.error("Timeout waiting for alert to handle with action '{}' within {} seconds", action, timeoutSeconds);
			BaseClass.logActionFailure("Handle alert", "Browser Alert", "Timeout waiting for alert: " + timeoutSeconds + "s");
			throw new FrameworkException("Timeout waiting for alert to handle with action '" + action + "' within " + timeoutSeconds + " seconds", e);
		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Handle alert", "Browser Alert", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Failed to handle alert with action '{}': {}", action, e.getMessage(), e);
			BaseClass.logActionFailure("Handle alert", "Browser Alert", e.getMessage());
			throw new FrameworkException("Failed to handle alert with action '" + action + "'", e);
		}
	}

	/** Verifies that a cookie with the specified name exists in the current browser session. */
	public void verifyCookieExists(String name) {
		validateInput(name, "Cookie Name");

		try {
			BaseClass.logActionStart("Verifying cookie exists: " + name, "Browser Cookie");

			Cookie cookie = driver.manage().getCookieNamed(name);

			if (cookie == null) {
				throw new FrameworkException("Cookie '" + name + "' does not exist");
			}

			logger.info("Cookie '{}' exists with value: '{}'", name, cookie.getValue());
			BaseClass.logActionSuccess("Cookie exists: " + name, "Browser Cookie");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify cookie exists", "Browser Cookie", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Failed to verify cookie exists '{}': {}", name, e.getMessage(), e);
			BaseClass.logActionFailure("Verify cookie exists", "Browser Cookie", e.getMessage());
			throw new FrameworkException("Failed to verify cookie exists: " + name, e);
		}
	}


	/** Verifies that a cookie with the specified name has the expected value. */
	public void verifyCookieValue(String name, String expectedValue) {
		validateInput(name, "Cookie Name");
		validateInput(expectedValue, "Expected Value");

		try {
			BaseClass.logActionStart("Verifying cookie '" + name + "' has expected value", "Browser Cookie");

			Cookie cookie = driver.manage().getCookieNamed(name);

			if (cookie == null) {
				throw new FrameworkException("Cookie '" + name + "' does not exist");
			}

			String actualValue = cookie.getValue();
			if (actualValue == null) {
				actualValue = "";
			}

			if (!expectedValue.equals(actualValue)) {
				throw new FrameworkException("Cookie '" + name + "' value mismatch. Expected: '" + expectedValue + "', but found: '" + actualValue + "'");
			}

			logger.info("Cookie '{}' has expected value: '{}'", name, expectedValue);
			BaseClass.logActionSuccess("Cookie value verified: " + name, "Browser Cookie");

		} catch (FrameworkException e) {
			// Re-throw framework exceptions
			BaseClass.logActionFailure("Verify cookie value", "Browser Cookie", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Failed to verify cookie value '{}': {}", name, e.getMessage(), e);
			BaseClass.logActionFailure("Verify cookie value", "Browser Cookie", e.getMessage());
			throw new FrameworkException("Failed to verify cookie value: " + name, e);
		}
	}



}
