package com.naveensdet.unifiedwebautomation.keywords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import com.fasterxml.jackson.databind.JsonNode;
import com.naveensdet.unifiedwebautomation.utils.ScreenshotUtility;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import java.awt.image.BufferedImage;
import java.util.function.Function;


/**
 * Keywords - Complete unified facade providing access to ALL keyword functionalities.
 * This class includes EVERY method from ALL keyword classes for maximum test readability.
 */
public class Keywords {

	 private static final Logger logger = LoggerFactory.getLogger(Keywords.class);

	// All keyword class instances
	private final ClickKeywords clickKeywords;
	private final InputKeywords inputKeywords;
	private final BrowserKeywords browserKeywords;
	private final NavigationKeywords navigationKeywords;
	private final VerificationKeywords verificationKeywords;
	private final DatePickerKeywords datePickerKeywords;
	private final FrameKeywords frameKeywords;
	private final ScreenshotKeywords screenshotKeywords;
	private final DataHandlingKeywords dataHandlingKeywords;
	private final JavaScriptKeywords javaScriptKeywords;
	private final UploadDownloadKeywords uploadDownloadKeywords;
	private final WaitKeywords waitKeywords;
	private final MouseKeyboardKeywords mouseKeyboardKeywords;
	private final SelectKeywords selectKeywords;

	/** Constructor initializes all keyword class instances. */
	public Keywords() {
		logger.info("Initializing complete unified Keywords facade with ALL methods");

		this.clickKeywords = new ClickKeywords();
		this.inputKeywords = new InputKeywords();
		this.browserKeywords = new BrowserKeywords();
		this.navigationKeywords = new NavigationKeywords();
		this.verificationKeywords = new VerificationKeywords();
		this.datePickerKeywords = new DatePickerKeywords();
		this.frameKeywords = new FrameKeywords();
		this.screenshotKeywords = new ScreenshotKeywords();
		this.dataHandlingKeywords = new DataHandlingKeywords();
		this.javaScriptKeywords = new JavaScriptKeywords();
		this.uploadDownloadKeywords = new UploadDownloadKeywords();
		this.waitKeywords = new WaitKeywords();
		this.mouseKeyboardKeywords = new MouseKeyboardKeywords();
		this.selectKeywords = new SelectKeywords();

		logger.info("ALL keyword classes initialized in unified facade - COMPLETE IMPLEMENTATION");
	}

	// ========================================
	// CLICK KEYWORDS - ALL METHODS
	// ========================================

	/** Clicks on element located by XPath with specified timeout. */
	public void clickElement(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.clickElement(xpath, elementName, timeoutSeconds);
	}

	/** Clicks on element located by XPath using default timeout. */
	public void clickElement(String xpath, String elementName) {
		clickKeywords.clickElement(xpath, elementName);
	}

	/** Double clicks on element located by XPath. */
	public void doubleClickElement(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.doubleClickElement(xpath, elementName, timeoutSeconds);
	}

	/** Right clicks on element located by XPath. */
	public void rightClickElement(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.rightClickElement(xpath, elementName, timeoutSeconds);
	}

	/** Clicks element using JavaScript executor. */
	public void clickElementUsingJS(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.clickElementUsingJS(xpath, elementName, timeoutSeconds);
	}

	/** Clicks element with retry mechanism. */
	public void clickElementWithRetry(String xpath, String elementName, int maxRetries, int timeoutSeconds) {
		clickKeywords.clickElementWithRetry(xpath, elementName, maxRetries, timeoutSeconds);
	}

	/** Clicks element if visible within timeout. */
	public void clickElementIfVisible(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.clickElementIfVisible(xpath, elementName, timeoutSeconds);
	}

	/** Force clicks element using Actions class. */
	public void forceClickElement(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.forceClickElement(xpath, elementName, timeoutSeconds);
	}

	/** Clicks element at specific coordinates. */
	public void clickElementAtCoordinates(String xpath, String elementName, int offsetX, int offsetY, int timeoutSeconds) {
		clickKeywords.clickElementAtCoordinates(xpath, elementName, offsetX, offsetY, timeoutSeconds);
	}

	/** Verifies element is clickable before clicking. */
	public void verifyAndClickElement(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.verifyAndClickElement(xpath, elementName, timeoutSeconds);
	}

	/** Soft click - returns boolean instead of throwing exception. */
	public boolean softClickElement(String xpath, String elementName, int timeoutSeconds) {
		return clickKeywords.softClickElement(xpath, elementName, timeoutSeconds);
	}

	/** Clicks element and waits for page load. */
	public void clickElementAndWaitForPageLoad(String xpath, String elementName, int timeoutSeconds) {
		clickKeywords.clickElementAndWaitForPageLoad(xpath, elementName, timeoutSeconds);
	}

	/** Clicks multiple elements in sequence. */
	public void clickMultipleElements(List<String> xpaths, List<String> elementNames, int timeoutSeconds) {
		clickKeywords.clickMultipleElements(xpaths, elementNames, timeoutSeconds);
	}

	/** Clicks element with custom wait condition. */
	public void clickElementWithCustomWait(String xpath, String elementName, String waitCondition, int timeoutSeconds) {
		clickKeywords.clickElementWithCustomWait(xpath, elementName, waitCondition, timeoutSeconds);
	}

	// ========================================
	// INPUT KEYWORDS - ALL METHODS
	// ========================================

	/** Enters text into input field located by XPath. */
	public void enterText(String xpath, String inputValue, String elementName, boolean excelData, String testName) {
		inputKeywords.enterText(xpath, inputValue, elementName, excelData, testName);
	}

	/** Clears text from input field located by XPath. */
	public void clearText(String xpath, String elementName) {
		inputKeywords.clearText(xpath, elementName);
	}

	/** Clears text from input field located by XPath. */
	public void clearText(String xpath, String elementName, int timeoutSeconds) {
		inputKeywords.clearText(xpath, elementName, timeoutSeconds);
	}

	/** Gets text value from input field. */
	public String getInputValue(String xpath, String elementName, int timeoutSeconds) {
		return inputKeywords.getInputValue(xpath, elementName, timeoutSeconds);
	}

	/** Enters text character by character. */
	public void enterTextSlowly(String xpath, String inputValueOrKey, String elementName, int delayMs, boolean excelData, String testName) {
		inputKeywords.enterTextSlowly(xpath, inputValueOrKey, elementName, delayMs, excelData, testName);
	}

	/** Enters text using JavaScript executor for elements that don't respond to standard sendKeys. */
	public void enterTextUsingJS(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		inputKeywords.enterTextUsingJS(xpath, inputValueOrKey, elementName, excelData, testName);
	}


	/** Appends the given text to the input field (does not clear); logs the action. */
	public void appendText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		inputKeywords.appendText(xpath, inputValueOrKey, elementName, excelData, testName);
	}


	/** Enters text and then presses specified key in input field; logs the action. */
	public void enterTextAndPressKey(String xpath, String inputValueOrKey, Keys key, String elementName, boolean excelData, String testName) {
		inputKeywords.enterTextAndPressKey(xpath, inputValueOrKey, key, elementName, excelData, testName);
	}


	/** Verifies that input field contains the expected value (direct string or Excel sourced). */
	public void verifyInputValue(String xpath, String expectedValueOrKey, String elementName, boolean excelData, String testName) {
		inputKeywords.verifyInputValue(xpath, expectedValueOrKey, elementName, excelData, testName);
	}

	/** Enters text into input field and validates that the text was entered correctly. */
	public void enterTextWithValidation(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		inputKeywords.enterTextWithValidation(xpath, inputValueOrKey, elementName, excelData, testName);
	}

	/** Soft enters text into input field and returns true if successful, false otherwise without throwing exceptions. */
	public boolean softEnterText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		return inputKeywords.softEnterText(xpath, inputValueOrKey, elementName, excelData, testName);
	}

	/** Enters text into input field and waits for specified element to appear or become visible. */
	public void enterTextAndWaitForElement(String xpath, String inputValueOrKey, String waitElementXpath, String elementName, boolean excelData, String testName) {
		inputKeywords.enterTextAndWaitForElement(xpath, inputValueOrKey, waitElementXpath, elementName, excelData, testName);
	}

	/** Enters masked text (for passwords or sensitive data) with enhanced security logging. */
	public void enterMaskedText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		inputKeywords.enterMaskedText(xpath, inputValueOrKey, elementName, excelData, testName);
	}


	/** Enters numeric text only into input field after validating input is numeric. */
	public void enterNumericText(String xpath, String inputValueOrKey, String elementName, boolean excelData, String testName) {
		inputKeywords.enterNumericText(xpath, inputValueOrKey, elementName, excelData, testName);
	}

	// ========================================
	// BROWSER KEYWORDS - ALL METHODS
	// ========================================

	/** Gets current page URL. */
	public String getCurrentUrl() {
		return browserKeywords.getCurrentUrl();
	}

	/** Gets current page title. */
	public String getPageTitle() {
		return browserKeywords.getPageTitle();
	}

	/** Maximizes browser window. */
	public void maximizeWindow() {
		browserKeywords.maximizeWindow();
	}

	/** Minimizes browser window. */
	public void minimizeWindow() {
		browserKeywords.minimizeWindow();
	}

	/** Sets browser window size. */
	public void setWindowSize(int width, int height) {
		browserKeywords.setWindowSize(width, height);
	}

	/** Gets browser window size. */
	public org.openqa.selenium.Dimension getWindowSize() {
		return browserKeywords.getWindowSize();
	}

	/** Sets browser window position to specified x and y coordinates on screen. */
	public void setWindowPosition(int x, int y) {
		browserKeywords.setWindowPosition(x, y);
	}

	/** Gets browser window position. */
	public org.openqa.selenium.Point getWindowPosition() {
		return browserKeywords.getWindowPosition();
	}

	/** Closes the current browser window. */
	public void closeCurrentWindow() {
		browserKeywords.closeCurrentWindow();
	}

	/** Quits the browser completely and terminates the WebDriver session. */
	public void quitBrowser() {
		browserKeywords.quitBrowser();
	}

	/** Opens new tab. */
	public void openNewTab() {
		browserKeywords.openNewTab();
	}

	/** Opens new window. */
	public void openNewWindow() {
		browserKeywords.openNewWindow();
	}

	/** Switches to next tab. */
	public void switchToNextTab() {
		browserKeywords.switchToNextTab();
	}

	/** Switches to previous tab. */
	public void switchToPreviousTab() {
		browserKeywords.switchToPreviousTab();
	}

	/** Switches to tab by index. */
	public void switchToTabByIndex(int index) {
		browserKeywords.switchToTabByIndex(index);
	}

	/** Switches to window by title. */
	public void switchToWindowByTitle(String title, int timeoutSeconds) {
		browserKeywords.switchToWindowByTitle(title, timeoutSeconds);
	}

	/** Switches to browser window that contains the specified URL within timeout. */
	public void switchToWindowByUrl(String url, int timeoutSeconds) {
		browserKeywords.switchToWindowByUrl(url, timeoutSeconds);
	}

	/** Gets window count. */
	public int getWindowCount() {
		return browserKeywords.getWindowCount();
	}

	/** Gets all window handles. */
	public Set<String> getAllWindowHandles() {
		return browserKeywords.getAllWindowHandles();
	}

	/** Gets current window handle. */
	public String getCurrentWindowHandle() {
		return browserKeywords.getCurrentWindowHandle();
	}

	/** Closes current window and switches to last. */
	public void closeCurrentWindowAndSwitchToLast() {
		browserKeywords.closeCurrentWindowAndSwitchToLast();
	}

	/** Closes all windows except main. */
	public void closeAllWindowsExceptMain() {
		browserKeywords.closeAllWindowsExceptMain();
	}

	// Alert Methods
	/** Accepts alert within timeout. */
	public void acceptAlert2(int timeoutSeconds) {
		browserKeywords.acceptAlert(timeoutSeconds);
	}

	/** Dismisses alert within timeout. */
	public void dismissAlert2(int timeoutSeconds) {
		browserKeywords.dismissAlert(timeoutSeconds);
	}

	/** Gets alert text within timeout. */
	public String getAlertText2(int timeoutSeconds) {
		return browserKeywords.getAlertText(timeoutSeconds);
	}

	/** Enters text in alert prompt. */
	public void enterTextInAlert(String text, int timeoutSeconds) {
		browserKeywords.enterTextInAlert(text, timeoutSeconds);
	}

	/** Checks if alert is present. */
	public boolean isAlertPresent(int timeoutSeconds) {
		return browserKeywords.isAlertPresent(timeoutSeconds);
	}

	/** Waits for alert to appear. */
	public void waitForAlert(int timeoutSeconds) {
		browserKeywords.waitForAlert(timeoutSeconds);
	}

	/** Handles alert with specific action. */
	public void handleAlert(String action, int timeoutSeconds) {
		browserKeywords.handleAlert(action, timeoutSeconds);
	}

	// Cookie Methods
	/** Adds cookie to browser. */
	public void addCookie(String name, String value, String domain, String path, java.util.Date expiry, boolean isSecure) {
		browserKeywords.addCookie(name, value, domain, path, expiry, isSecure);
	}

	/** Gets cookie by name. */
	public Cookie getCookieByName(String name) {
		return browserKeywords.getCookieByName(name);
	}

	/** Gets all cookies. */
	public Set<Cookie> getAllCookies() {
		return browserKeywords.getAllCookies();
	}

	/** Deletes cookie by name. */
	public void deleteCookieByName(String name) {
		browserKeywords.deleteCookieByName(name);
	}

	/** Deletes all cookies. */
	public void deleteAllCookies() {
		browserKeywords.deleteAllCookies();
	}

	/** Verifies cookie exists. */
	public void verifyCookieExists(String name) {
		browserKeywords.verifyCookieExists(name);
	}

	/** Verifies cookie value. */
	public void verifyCookieValue(String name, String expectedValue) {
		browserKeywords.verifyCookieValue(name, expectedValue);
	}

	// Local Storage Methods
	/** Sets local storage item. */
	public void setLocalStorageItem(String key, String value) {
		browserKeywords.setLocalStorageItem(key, value);
	}

	/** Gets local storage item. */
	public String getLocalStorageItem(String key) {
		return browserKeywords.getLocalStorageItem(key);
	}

	/** Clears local storage. */
	public void clearLocalStorage() {
		browserKeywords.clearLocalStorage();
	}

	// Session Storage Methods
	/** Sets session storage item. */
	public void setSessionStorageItem(String key, String value) {
		browserKeywords.setSessionStorageItem(key, value);
	}

	/** Gets session storage item. */
	public String getSessionStorageItem(String key) {
		return browserKeywords.getSessionStorageItem(key);
	}

	/** Clears session storage. */
	public void clearSessionStorage() {
		browserKeywords.clearSessionStorage();
	}

	// ========================================
	// NAVIGATION KEYWORDS - ALL METHODS
	// ========================================

	// Usage with Excel data
	public void navigateToApplicationWithValidation(String url, String expectedTitlePart, boolean excelData, String testCaseName) {
		// Fetches URL and expected title from Excel based on testCaseName
		navigationKeywords.navigateToUrlWithValidation(url, expectedTitlePart, 30, excelData, testCaseName);
	}

	/** Navigates to URL and waits for specified element to appear or become visible. */
	public void navigateToUrlAndWaitForElement(String url, String elementXpath, int timeoutSeconds) {
		navigationKeywords.navigateToUrlAndWaitForElement(url, elementXpath, timeoutSeconds);
	}


	/** Refreshes page and waits for element. */
	public void refreshPageAndWaitForElement(String elementXpath, int timeoutSeconds) {
		navigationKeywords.refreshPageAndWaitForElement(elementXpath, timeoutSeconds);
	}

	// Usage with Excel data
	public void navigateBackWithValidation(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		// Fetches expected URL from Excel based on testCaseName
		navigationKeywords.goBackWithValidation(expectedUrl, timeoutSeconds, excelData, testCaseName);
	}

	// Usage with Excel data for forward navigation
	public void navigateForwardWithValidation(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		// Fetches expected URL from Excel based on testCaseName
		navigationKeywords.goForwardWithValidation(expectedUrl, timeoutSeconds, excelData, testCaseName);
	}

	/** Waits for page to load completely. */
	public void waitForPageToLoad(int timeoutSeconds) {
		navigationKeywords.waitForPageToLoad(timeoutSeconds);
	}

	// Usage with Excel data
	public void verifyNavigationSuccess(String expectedUrlPart, boolean excelData, String testCaseName) {
		// Fetches expected URL part from Excel based on testCaseName
		navigationKeywords.verifyUrlContains(expectedUrlPart, excelData, testCaseName);
	}

	// Usage with Excel data
	public void verifyNavigationToPage(String expectedUrl, boolean excelData, String testCaseName) {
		// Fetches expected URL from Excel based on testCaseName
		navigationKeywords.verifyUrlEquals(expectedUrl, excelData, testCaseName);
	}

	/** Gets page load time. */
	public long getPageLoadTime() {
		return navigationKeywords.getPageLoadTime();
	}

	// Usage with Excel data
	public void waitForNavigationChange(String currentUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		// Fetches current URL from Excel based on testCaseName
		navigationKeywords.waitForUrlChange(currentUrl, timeoutSeconds, excelData, testCaseName);
	}

	/** Navigates the browser to the specified URL. */
	public void navigateToUrl(String url, boolean excelData, String testCaseName) {
		navigationKeywords.navigateToUrl(url, excelData, testCaseName);
	}

	/** Refreshes the current browser page. */
	public void refreshPage() {
		navigationKeywords.refreshPage();
	}

	/** Navigates backward in browser history. */
	public void goBack() {
		navigationKeywords.goBack();
	}

	/** Navigates forward in browser history. */
	public void goForward() {
		navigationKeywords.goForward();
	}

	/** Moves the current browser window to the specified position on the screen. */
	public void moveWindowTo(int x, int y) {
		navigationKeywords.moveWindowTo(x, y);
	}

	/** Switches the WebDriver context to the most recently opened window or tab. */
	public void switchToNewlyOpenedWindow() {
		navigationKeywords.switchToNewlyOpenedWindow();
	}

	/** Switches the WebDriver context to the window identified by a zero-based index. */
	public void switchToWindowByIndex(int index) {
		navigationKeywords.switchToWindowByIndex(index);
	}

	/** Switches the WebDriver context to the window with the specified title, waiting up to the specified timeout. */
	public void switchToWindowByTitleV2(String windowTitle, int timeoutSeconds) {
		navigationKeywords.switchToWindowByTitle(windowTitle, timeoutSeconds);
	}

	/** Switches the WebDriver context to the window with the specified window handle. */
	public void switchToWindowByHandle(String windowHandle) {
		navigationKeywords.switchToWindowByHandle(windowHandle);
	}

	/** Switches the WebDriver context to the original parent window. */
	public void switchToParentWindow() {
		navigationKeywords.switchToParentWindow();
	}

	/** Opens the specified URL in a new browser window and switches to it. */
	public void openUrlInNewWindow(String url) {
		navigationKeywords.openUrlInNewWindow(url);
	}

	/** Waits until the number of open windows/tabs equals the expected count. */
	public void waitForNumberOfWindows(int expectedNumber, int timeoutSeconds) {
		navigationKeywords.waitForNumberOfWindows(expectedNumber, timeoutSeconds);
	}

	// ========================================
	// TAB MANAGEMENT OPERATIONS
	// ========================================

	/** Opens a new browser tab and switches focus to it. */
	public void openNewTabV2() {
		navigationKeywords.openNewTab();
	}

	/** Closes the current window or tab and switches to the main window. */
	public void closeCurrentTabAndSwitchToMain() {
		navigationKeywords.closeCurrentTabAndSwitchToMain();
	}

	/** Closes all windows or tabs except the main window. */
	public void closeAllOtherTabsExceptMain() {
		navigationKeywords.closeAllOtherTabsExceptMain();
	}

	// Usage with Excel data
	public void openNewTabWithUrl(String url, boolean excelData, String testCaseName) {
	    // Fetches URL from Excel based on testCaseName
		navigationKeywords.openUrlInNewTab(url, excelData, testCaseName);
	}

	// ========================================
	// FRAME MANAGEMENT OPERATIONS
	// ========================================

	/** Switches the WebDriver context to the iframe specified by XPath. */
	public void switchToFrameByXpath(String frameXpath, String elementName, int timeoutSeconds) {
		navigationKeywords.switchToFrameByXpath(frameXpath, elementName, timeoutSeconds);
	}

	/** Switches the WebDriver context to the iframe specified by name or ID. */
	public void switchToFrameByNameOrId(String nameOrId) {
		navigationKeywords.switchToFrameByNameOrId(nameOrId);
	}

	/** Switches the WebDriver context to the iframe specified by index. */
	public void switchToFrameByIndex(int frameIndex) {
		navigationKeywords.switchToFrameByIndex(frameIndex);
	}

	// ========================================
	// ALERT MANAGEMENT OPERATIONS
	// ========================================

	/** Accepts an alert, waiting up to the specified timeout. */
	public void acceptAlert(int timeoutSeconds) {
		navigationKeywords.acceptAlert(timeoutSeconds);
	}

	/** Dismisses an alert, waiting up to the specified timeout. */
	public void dismissAlert(int timeoutSeconds) {
		navigationKeywords.dismissAlert(timeoutSeconds);
	}

	/** Retrieves the text of an alert, waiting up to the specified timeout. */
	public String getAlertText(int timeoutSeconds) {
		return navigationKeywords.getAlertText(timeoutSeconds);
	}

	/** Sends text to a prompt alert, waiting up to the specified timeout. */
	public void enterAlertText(String text, int timeoutSeconds, boolean excelData, String testCaseName) {
		navigationKeywords.enterAlertText(text, timeoutSeconds, excelData, testCaseName);
	}

	/** Waits until the page's readyState is 'complete' within the timeout. */
	public void waitForPageLoadComplete(int timeoutSeconds) {
		navigationKeywords.waitForPageLoadComplete(timeoutSeconds);
	}

	// ========================================
	// VERIFICATION OPERATIONS
	// ========================================

	/** Verifies that the current URL exactly matches the expected URL. */
	public void verifyCurrentUrl(String expectedUrl, boolean excelData, String testCaseName) {
		navigationKeywords.verifyCurrentUrl(expectedUrl, excelData, testCaseName);
	}

	/** Verifies that the current URL contains the expected substring. */
	public void verifyCurrentUrlContains(String fragment, boolean excelData, String testCaseName) {
		navigationKeywords.verifyCurrentUrlContains(fragment, excelData, testCaseName);
	}

	/** Verifies that the page title exactly matches the expected title. */
	public void verifyPageTitle(String expectedTitle, boolean excelData, String testCaseName) {
		navigationKeywords.verifyPageTitle(expectedTitle, excelData, testCaseName);
	}

	/** Verifies that the page title contains the given fragment. */
	public void verifyPageTitleContains(String fragment, boolean excelData, String testCaseName) {
		navigationKeywords.verifyPageTitleContains(fragment, excelData, testCaseName);
	}


	// ========================================
	// VERIFICATION KEYWORDS - ALL METHODS
	// ========================================

	/** Verifies element is visible within timeout. */
	public void verifyElementVisible(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementVisible(xpath, elementName, timeoutSeconds);
	}

	/** Verifies element is not visible within timeout. */
	public void verifyElementNotVisible(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementNotVisible(xpath, elementName, timeoutSeconds);
	}

	/** Verifies element is not present. */
	public void verifyElementNotPresent(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementNotPresent(xpath, elementName, timeoutSeconds);
	}

	/** Verifies element is selected. */
	public void verifyElementSelected(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementSelected(xpath, elementName, timeoutSeconds);
	}

	/** Verifies text contains expected value. */
	public void verifyTextContains(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyTextContains(xpath, expectedText, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies text equals expected value. */
	public void verifyTextEquals(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyTextEquals(xpath, expectedText, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies text does not contain value. */
	public void verifyTextDoesNotContain(String xpath, String unexpectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyTextDoesNotContain(xpath, unexpectedText, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies text is empty. */
	public void verifyTextIsEmpty(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyTextIsEmpty(xpath, elementName, timeoutSeconds);
	}

	/** Verifies text is not empty. */
	public void verifyTextIsNotEmpty(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyTextIsNotEmpty(xpath, elementName, timeoutSeconds);
	}

	// Example usage with Excel data
	public void verifyApplicationTitle(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		// Verifies page title contains expected text with Excel support
		verificationKeywords.verifyPageTitleContains(fragment, timeoutSeconds, excelData, testCaseName);
	}

	public void verifyElementAttribute(String xpath, String attributeName, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		// Verifies element attribute contains expected value with Excel support
		verificationKeywords.verifyElementAttributeContains(xpath, attributeName, expectedFragment, elementName, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// BOOLEAN VERIFICATION
	// ========================================

	/** Verifies that boolean condition is true. */
	public void verifyTrue(boolean condition, String message) {
		verificationKeywords.verifyTrue(condition, message);
	}

	/** Verifies that boolean condition is false. */
	public void verifyFalse(boolean condition, String message) {
		verificationKeywords.verifyFalse(condition, message);
	}

	// ========================================
	// STRING VERIFICATION
	// ========================================

	/** Verifies that string contains expected substring. */
	public void verifyStringContains(String actual, String expected, String message, boolean excelData, String testCaseName) {
		verificationKeywords.verifyStringContains(actual, expected, message, excelData, testCaseName);
	}

	/** Verifies that string equals expected value. */
	public void verifyStringEquals(String actual, String expected, String message, boolean excelData, String testCaseName) {
		verificationKeywords.verifyStringEquals(actual, expected, message, excelData, testCaseName);
	}

	// ========================================
	// ADDITIONAL VERIFICATION METHODS
	// ========================================

	// ========================================
	// ADDITIONAL VERIFICATION METHODS
	// ========================================

	/** Verifies that element count equals expected value within timeout. */
	public void verifyElementCount(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyElementCount(xpath, expectedCount, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies element CSS property value within timeout. */
	public void verifyElementCSSProperty(String xpath, String cssProperty, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyElementCSSProperty(xpath, cssProperty, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// SOFT VERIFICATION METHODS
	// ========================================

	/** Soft verification - element visible, returns boolean instead of throwing exception. */
	public boolean softVerifyElementVisible(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.softVerifyElementVisible(xpath, elementName, timeoutSeconds);
	}

	/** Soft verification - text contains, returns boolean instead of throwing exception. */
	public boolean softVerifyTextContains(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		return verificationKeywords.softVerifyTextContains(xpath, expectedText, elementName, timeoutSeconds, excelData, testCaseName);
	}


	// ========================================
	// ELEMENT VISIBILITY VERIFICATION
	// ========================================

	/** Soft verify that element is visible; logs warning on failure. */
	public boolean verifyElementVisibleNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementVisibleNoReport(xpath, elementName, timeoutSeconds);
	}

	/** Verifies that element is invisible within timeout. */
	public void verifyElementInvisible(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementInvisible(xpath, elementName, timeoutSeconds);
	}

	/** Soft verify that element is invisible; logs warning on failure. */
	public boolean verifyElementInvisibleNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementInvisibleNoReport(xpath, elementName, timeoutSeconds);
	}

	// ========================================
	// ELEMENT PRESENCE VERIFICATION
	// ========================================

	/** Verifies that element is present in DOM within timeout. */
	public void verifyElementPresent(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementPresent(xpath, elementName, timeoutSeconds);
	}

	/** Soft validation: checks if an element is present in the DOM within the specified timeout. */
	public boolean verifyElementPresentNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementPresentNoReport(xpath, elementName, timeoutSeconds);
	}

	/** Soft validation: checks if an element is not present in the DOM within the specified timeout. */
	public boolean verifyElementNotPresentNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementNotPresentNoReport(xpath, elementName, timeoutSeconds);
	}

	// ========================================
	// TEXT VERIFICATION
	// ========================================

	/** Verifies that the text of the element identified by the XPath exactly equals the expected text within the specified timeout. */
	public void verifyElementTextEquals(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyElementTextEquals(xpath, expectedText, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Soft validation: checks if the text of the element equals the expected text within the specified timeout. */
	public void verifyElementTextEqualsNoReport(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyElementTextEqualsNoReport(xpath, expectedText, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies that the element's text contains the specified substring within the given timeout. */
	public void verifyElementTextContains(String xpath, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyElementTextContains(xpath, expectedFragment, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies element attribute contains expected fragment without detailed reporting. */
	public void verifyElementAttributeContainsNoReport(String xpath, String attributeName, String expectedFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyElementAttributeContainsNoReport(xpath, attributeName, expectedFragment, elementName, timeoutSeconds, excelData, testCaseName);
	}



	// ========================================
	// ATTRIBUTE VERIFICATION
	// ========================================

	/** Verifies that the specified attribute of an element matches the expected value within the given timeout. */
	public void verifyElementAttributeEquals(String xpath, String attributeName, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyElementAttributeEquals(xpath, attributeName, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies element attribute equals expected value without detailed reporting. */
	public void verifyElementAttributeEqualsNoReport(String xpath, String attributeName, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyElementAttributeEqualsNoReport(xpath, attributeName, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// INPUT VALUE VERIFICATION
	// ========================================

	/** Verifies that the input element's 'value' attribute equals the expected value within the given timeout. */
	public void verifyInputValueEquals(String xpath, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyInputValueEquals(xpath, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies input element value equals expected value without detailed reporting. */
	public void verifyInputValueEqualsNoReport(String xpath, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyInputValueEqualsNoReport(xpath, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies that the input element's "value" attribute is empty within the specified timeout. */
	public void verifyInputValueIsEmpty(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyInputValueIsEmpty(xpath, elementName, timeoutSeconds);
	}

	/** Performs a soft verification that the input element's value is empty. */
	public boolean verifyInputValueIsEmptyNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyInputValueIsEmptyNoReport(xpath, elementName, timeoutSeconds);
	}

	// ========================================
	// ELEMENT STATE VERIFICATION
	// ========================================

	/** Verifies that the element located by the XPath is enabled within the specified timeout. */
	public void verifyElementEnabled(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementEnabled(xpath, elementName, timeoutSeconds);
	}

	/** Soft validation: checks if the element located by the XPath is enabled within the specified timeout. */
	public boolean verifyElementEnabledNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementEnabledNoReport(xpath, elementName, timeoutSeconds);
	}

	/** Verifies that the element located by the XPath is disabled within the specified timeout. */
	public void verifyElementDisabled(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementDisabled(xpath, elementName, timeoutSeconds);
	}

	/** Soft validation: checks if the element located by the XPath is disabled within the specified timeout. */
	public boolean verifyElementDisabledNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementDisabledNoReport(xpath, elementName, timeoutSeconds);
	}

	/** Soft validation: checks if the element located by the XPath is selected within the specified timeout. */
	public boolean verifyElementSelectedNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementSelectedNoReport(xpath, elementName, timeoutSeconds);
	}

	/** Verifies that the element located by the XPath is not selected within the specified timeout. */
	public void verifyElementNotSelected(String xpath, String elementName, int timeoutSeconds) {
		verificationKeywords.verifyElementNotSelected(xpath, elementName, timeoutSeconds);
	}

	/** Soft validation: checks if the element located by the XPath is not selected within the specified timeout. */
	public boolean verifyElementNotSelectedNoReport(String xpath, String elementName, int timeoutSeconds) {
		return verificationKeywords.verifyElementNotSelectedNoReport(xpath, elementName, timeoutSeconds);
	}

	// ========================================
	// PAGE TITLE & URL VERIFICATION
	// ========================================

	/** Verifies that the current page title exactly matches the expected title within the specified timeout. */
	public void verifyPageTitleEquals(String expectedTitle, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyPageTitleEquals(expectedTitle, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies page title equals expected value without detailed reporting. */
	public void verifyPageTitleEqualsNoReport(String expectedTitle, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyPageTitleEqualsNoReport(expectedTitle, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies page title contains expected fragment without detailed reporting. */
	public void verifyPageTitleContainsNoReport(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyPageTitleContainsNoReport(fragment, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies that the current browser URL exactly equals the expected URL within the specified timeout. */
	public void verifyUrlEquals(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyUrlEquals(expectedUrl, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies current URL equals expected URL without detailed reporting. */
	public void verifyUrlEqualsNoReport(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyUrlEqualsNoReport(expectedUrl, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies that the current URL contains the specified substring within the given timeout. */
	public void verifyUrlContains(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyUrlContains(fragment, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies current URL contains expected fragment without detailed reporting. */
	public void verifyUrlContainsNoReport(String fragment, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyUrlContainsNoReport(fragment, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// COUNT VERIFICATION
	// ========================================

	/** Verifies element count equals expected value. */
	public void verifyElementCountEquals(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyElementCountEquals(xpath, expectedCount, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies element count equals expected value without detailed reporting. */
	public void verifyElementCountEqualsNoReport(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyElementCountEqualsNoReport(xpath, expectedCount, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies select element options count equals expected value. */
	public void verifySelectOptionsCount(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifySelectOptionsCount(xpath, expectedCount, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies select element options count equals expected value without detailed reporting. */
	public void verifySelectOptionsCountNoReport(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifySelectOptionsCountNoReport(xpath, expectedCount, elementName, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// ALERT VERIFICATION
	// ========================================

	/** Verifies that a browser alert is present and its text matches the expected value within the specified timeout. */
	public void verifyAlertText(String expectedAlertText, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyAlertText(expectedAlertText, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies alert text equals expected value without detailed reporting. */
	public void verifyAlertTextNoReport(String expectedAlertText, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyAlertTextNoReport(expectedAlertText, timeoutSeconds, excelData, testCaseName);
	}


	// ========================================
	// CSS PROPERTY VERIFICATION
	// ========================================

	/** Verifies that the CSS property of the specified element equals the expected value within the given timeout. */
	public void verifyElementCssValue(String xpath, String cssProperty, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
		verificationKeywords.verifyElementCssValue(xpath, cssProperty, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Verifies element CSS property value without detailed reporting. */
	public void verifyElementCssValueNoReport(String xpath, String cssProperty, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    verificationKeywords.verifyElementCssValueNoReport(xpath, cssProperty, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}


	// ========================================
	// DATE PICKER KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// DIRECT INPUT STYLE PICKERS
	// ========================================

	/** Sets the value of a date input field located by XPath. */
	public void setDateInputValue(String xpath, String dateStr, int timeoutSeconds, boolean excelData, String testCaseName) {
		datePickerKeywords.setDateInputValue(xpath, dateStr, timeoutSeconds, excelData, testCaseName);
	}


	/** Gets the current value from a date input field. */
	public String getDateInputValue(String xpath, int timeoutSeconds) {
		return datePickerKeywords.getDateInputValue(xpath, timeoutSeconds);
	}

	// ========================================
	// POPUP / WIDGET STYLE PICKERS
	// ========================================

	/** Opens the date picker popup by clicking the input/icon. */
	public void openDatePicker(String xpath, int timeoutSeconds) {
		datePickerKeywords.openDatePicker(xpath, timeoutSeconds);
	}

	/** Selects a day in the visible date picker using XPath. */
	public void selectDayInDatePicker(String dayXpath, int timeoutSeconds) {
		datePickerKeywords.selectDayInDatePicker(dayXpath, timeoutSeconds);
	}

	/** Selects a month in the date picker widget using XPath. */
	public void selectMonthInDatePicker(String monthXpath, int timeoutSeconds) {
		datePickerKeywords.selectMonthInDatePicker(monthXpath, timeoutSeconds);
	}

	/** Selects a year in the date picker widget using XPath. */
	public void selectYearInDatePicker(String yearXpath, int timeoutSeconds) {
		datePickerKeywords.selectYearInDatePicker(yearXpath, timeoutSeconds);
	}

	/** Navigates to the next month in the date picker by clicking the "next" button XPath. */
	public void navigateToNextMonth(String nextBtnXpath, int timeoutSeconds) {
		datePickerKeywords.navigateToNextMonth(nextBtnXpath, timeoutSeconds);
	}

	/** Navigates to the previous month in the date picker by clicking the "previous" button XPath. */
	public void navigateToPreviousMonth(String prevBtnXpath, int timeoutSeconds) {
		datePickerKeywords.navigateToPreviousMonth(prevBtnXpath, timeoutSeconds);
	}

	/** Uses JavaScript to set date on special date-pickers. */
	public void selectDateByCustomJS(String dateStr, String datePickerXpath, boolean excelData, String testCaseName) {
		datePickerKeywords.selectDateByCustomJS(dateStr, datePickerXpath, excelData, testCaseName);
	}


	// ========================================
	// MULTI-COMPONENT / COMPOSITE SELECTORS
	// ========================================

	/** Sets date by selecting day, month and year dropdowns. */
	public void setDateByDropdowns(String dayXpath, String monthXpath, String yearXpath,
	        String day, String month, String year, int timeoutSeconds, boolean excelData, String testCaseName) {
		datePickerKeywords.setDateByDropdowns(dayXpath, monthXpath, yearXpath, day, month, year, timeoutSeconds, excelData, testCaseName);
	}


	// ========================================
	// VALIDATION, DISABLED DAYS, AND EDGE CASES
	// ========================================

	/** Checks if a specific day element is selectable/enabled in the date picker. */
	public boolean isDateSelectable(String dayXpath, int timeoutSeconds) {
		return datePickerKeywords.isDateSelectable(dayXpath, timeoutSeconds);
	}

	/** Verifies the date picker popup opens when triggered; waits for popup presence. */
	public void verifyDatePickerOpens(String pickerPopupXpath, int timeoutSeconds) {
		datePickerKeywords.verifyDatePickerOpens(pickerPopupXpath, timeoutSeconds);
	}

	/** Verifies that the date input field or picker shows the expected date string. */
	public void verifyDatePickerValue(String xpath, String expectedDate, int timeoutSeconds, boolean excelData, String testCaseName) {
		datePickerKeywords.verifyDatePickerValue(xpath, expectedDate, timeoutSeconds, excelData, testCaseName);
	}


	/** Clears any date selection from the input/picker by clearing the input field. */
	public void clearDate(String xpath, int timeoutSeconds) {
		datePickerKeywords.clearDate(xpath, timeoutSeconds);
	}

	// ========================================
	// DATE RANGE PICKERS
	// ========================================

	/** Sets the start and end dates of a date range picker using input fields. */
	public void setDateRange(String startXpath, String endXpath, String startDate, String endDate, int timeoutSeconds, boolean excelData, String testCaseName) {
		datePickerKeywords.setDateRange(startXpath, endXpath, startDate, endDate, timeoutSeconds, excelData, testCaseName);
	}


	// ========================================
	// JS AND UTILITY ACTIONS
	// ========================================

	/**
	 * Sets the date value directly using JavaScript (for pickers resisting standard inputs).
	 */
	public void setDateByJS(String xpath, String dateStr) {
		datePickerKeywords.setDateByJS(xpath, dateStr);
	}

	/**
	 * Waits until the date picker popup is closed/hidden.
	 */
	public void waitForDatePickerToClose(String pickerPopupXpath, int timeoutSeconds) {
		datePickerKeywords.waitForDatePickerToClose(pickerPopupXpath, timeoutSeconds);
	}

	/**
	 * Selects today's date in a date picker by dynamically fetching the current system date,
	 * formatting it to match the date picker's expected format, and selecting the corresponding day.
	 */
	public void selectTodayInDatePicker(String dayXpathPattern, String dateFormatForDay, int timeoutSeconds) {
		datePickerKeywords.selectTodayInDatePicker(dayXpathPattern, dateFormatForDay, timeoutSeconds);
	}

	/** Inputs a date into a textbox with fixed delimiters using Actions. */
	public void inputDateWithFixedDelimiters(String xpath, String dateStr, int timeoutSeconds, char delimiter, boolean excelData, String testCaseName) {
		datePickerKeywords.inputDateWithFixedDelimiters(xpath, dateStr, timeoutSeconds, delimiter, excelData, testCaseName);
	}

	/**
	 * Fetches today's date, formats it as digits only (no delimiters), and inputs it
	 * digit-by-digit into a date input textbox that has fixed delimiters (e.g., '/').
	 */
	public void inputTodayDateWithFixedDelimiters(String xpath, int timeoutSeconds, char delimiter, String dateFormat) {
		datePickerKeywords.inputTodayDateWithFixedDelimiters(xpath, timeoutSeconds, delimiter, dateFormat);
	}

	/**
	 * Fetches the date for next week (7 days from today), formats it, and inputs it
	 * digit-by-digit into a date input box that has fixed delimiters (e.g., '/') in place.
	 */
	public void inputDateNextWeekWithFixedDelimiters(String xpath, int timeoutSeconds, char delimiter, String dateFormat) {
		datePickerKeywords.inputDateNextWeekWithFixedDelimiters(xpath, timeoutSeconds, delimiter, dateFormat);
	}


	// ========================================
	// FRAME KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// SWITCHING CONTEXT
	// ========================================

	/** Switch to frame by zero-based index within timeout. */
	public void switchToFrameByIndex(int index, int timeoutSeconds) {
		frameKeywords.switchToFrameByIndex(index, timeoutSeconds);
	}

	/** Switch to frame by name or ID within timeout. */
	public void switchToFrameByNameOrId(String nameOrId, int timeoutSeconds) {
		frameKeywords.switchToFrameByNameOrId(nameOrId, timeoutSeconds);
	}

	/** Switch to frame by XPath within timeout. */
	public void switchToFrameByXPath(String xpath, int timeoutSeconds) {
		frameKeywords.switchToFrameByXPath(xpath, timeoutSeconds);
	}

	/** Switch to frame using a previously located WebElement. */
	public void switchToFrameByElement(WebElement frameElement) {
		frameKeywords.switchToFrameByElement(frameElement);
	}

	/** Switch to the first frame that matches the given XPath within timeout. */
	public void switchToFirstFrameThatMatches(String xpath, int timeoutSeconds) {
		frameKeywords.switchToFirstFrameThatMatches(xpath, timeoutSeconds);
	}

	// ========================================
	// RETURNING CONTEXT
	// ========================================

	/** Switch to immediate parent frame of current frame. */
	public void switchToParentFrame() {
		frameKeywords.switchToParentFrame();
	}

	/** Switch to main document (exits all frames). */
	public void switchToDefaultContent() {
		frameKeywords.switchToDefaultContent();
	}

	// ========================================
	// NAVIGATING FRAME CHAINS
	// ========================================

	/**
	 * Robustly switch through a chain of nested frames identified by locators.
	 * Each locator can be name/id or XPath depending on locatorType.
	 */
	public void switchToMainAndThenToNestedFrames(List<String> frameLocators, FrameKeywords.FrameLocatorType locatorType, int timeoutSeconds) {
		frameKeywords.switchToMainAndThenToNestedFrames(frameLocators, locatorType, timeoutSeconds);
	}

	// ========================================
	// FRAME PRESENCE AND VERIFICATION
	// ========================================

	/** Verify that a frame exists by XPath within timeout. */
	public void verifyFramePresentByXPath(String xpath, int timeoutSeconds) {
		frameKeywords.verifyFramePresentByXPath(xpath, timeoutSeconds);
	}

	/** Verify that a frame is NOT present by XPath within timeout. */
	public void verifyFrameNotPresentByXPath(String xpath, int timeoutSeconds) {
		frameKeywords.verifyFrameNotPresentByXPath(xpath, timeoutSeconds);
	}

	/** Soft check — returns true if frame present by XPath, false otherwise. */
	public boolean isFramePresent(String xpath, int timeoutSeconds) {
		return frameKeywords.isFramePresent(xpath, timeoutSeconds);
	}

	/** Returns true if driver is currently inside any frame (not default content). */
	public boolean isInsideFrame() {
		return frameKeywords.isInsideFrame();
	}

	/** Verify that the context is not inside any frame (default content). */
	public void verifyNotInAnyFrame() {
		frameKeywords.verifyNotInAnyFrame();
	}

	// ========================================
	// FRAME ENUMERATION / UTILITIES
	// ========================================

	/** Returns a List of top-level frame WebElements in current context. */
	public List<WebElement> getAllFrameWebElements() {
		return frameKeywords.getAllFrameWebElements();
	}

	/** Returns the count of all immediate frames in current context. */
	public int getFrameCount() {
		return frameKeywords.getFrameCount();
	}

	/** Waits until the number of frames matches expected count. */
	public boolean waitForFrameCount(int expectedCount, int timeoutSeconds, boolean excelData, String testCaseName) {
	    return frameKeywords.waitForFrameCount(expectedCount, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// FRAME ATTRIBUTE/PROPERTY ACTIONS
	// ========================================

	/** Gets the frame element's 'name' or 'id' attribute (if present). */
	public String getFrameNameOrId(WebElement frameElement) {
		return frameKeywords.getFrameNameOrId(frameElement);
	}

	/** Gets the frame element's 'src' attribute. */
	public String getFrameSrc(WebElement frameElement) {
		return frameKeywords.getFrameSrc(frameElement);
	}

	// ========================================
	// ROBUSTNESS / ADVANCED
	// ========================================

	/** Waits until all frames and nested frames in current context are fully loaded. */
	public void waitForAllFramesToBeLoaded(int timeoutSeconds) {
		frameKeywords.waitForAllFramesToBeLoaded(timeoutSeconds);
	}

	/** Refreshes or reloads the current frame's content. */
	public void refreshCurrentFrame() {
		frameKeywords.refreshCurrentFrame();
	}


	// ========================================
	// SCREENSHOT KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// SCREENSHOT CAPTURE OPERATIONS
	// ========================================

	/**
	 * Takes a full page screenshot with timestamp and test case context
	 * @param actionName Descriptive name for the screenshot action
	 * @param testCaseName Name of the test case for better organization
	 */
	public void takeScreenshotWithTimestamp(String actionName, String testCaseName) {
	    String path = ScreenshotUtility.generateScreenshotPath(actionName, testCaseName);
	    screenshotKeywords.takeFullPageScreenshot(path);
	}
	
	/**
	 * Takes a full page screenshot with timestamp (simpler version without test case)
	 * @param actionName Descriptive name for the screenshot action
	 */
	public void takeScreenshotWithTimestamp(String actionName) {
	    String path = ScreenshotUtility.generateScreenshotPath(actionName);
	    screenshotKeywords.takeFullPageScreenshot(path);
	}
	
	/**
	 * Takes a failure screenshot with additional context
	 * @param actionName Descriptive name for the screenshot action
	 * @param testCaseName Name of the test case
	 * @param failureReason Brief description of the failure
	 */
	public void takeFailureScreenshot(String actionName, String testCaseName, String failureReason) {
	    String path = ScreenshotUtility.generateFailureScreenshotPath(actionName, testCaseName, failureReason);
	    screenshotKeywords.takeFullPageScreenshot(path);
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
		screenshotKeywords.takeElementScreenshot(xpath, filePath, timeoutSeconds);
	}

	/**
	 * Takes a screenshot of the visible viewport (browser window).
	 *
	 * @param filePath Absolute file path to save the viewport screenshot.
	 * @throws FrameworkException if capture or saving fails.
	 */
	public void takeViewportScreenshot(String filePath) {
		screenshotKeywords.takeViewportScreenshot(filePath);
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
		screenshotKeywords.takeCroppedScreenshot(x, y, width, height, filePath);
	}

	// ========================================
	// SCREENSHOT PROCESSING OPERATIONS
	// ========================================

	/**
	 * Captures screenshot and returns it as BufferedImage.
	 * Useful for further processing or cropping.
	 *
	 * @return BufferedImage screenshot of the full viewport.
	 */
	public BufferedImage captureAsImage() {
		return screenshotKeywords.captureAsImage();
	}

	// ========================================
	// SCREENSHOT DATA OPERATIONS
	// ========================================

	/**
	 * Takes a screenshot and returns it as Base64 encoded string.
	 * Useful for embedding screenshots in reports or messages.
	 *
	 * @return Base64 screenshot string.
	 * @throws FrameworkException if capture fails.
	 */
	public String getScreenshotAsBase64() {
		return screenshotKeywords.getScreenshotAsBase64();
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
		return screenshotKeywords.getElementScreenshotAsBase64(xpath, timeoutSeconds);
	}

	/**
	 * Takes a screenshot of the visible viewport and returns as byte array.
	 *
	 * @return byte[] containing raw screenshot data.
	 * @throws FrameworkException if capture fails.
	 */
	public byte[] getViewportScreenshotAsBytes() {
		return screenshotKeywords.getViewportScreenshotAsBytes();
	}


	// ========================================
	// DATA HANDLING KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// EXCEL HANDLING
	// ========================================

	/**
	 * Reads data from an Excel sheet and returns it as a List of Maps (each Map is a row with column header keys).
	 *
	 * @param filePath  Absolute path to Excel (.xlsx) file.
	 * @param sheetName Name of the sheet to read.
	 * @return List of Maps representing rows.
	 */
	public List<Map<String, String>> readExcelSheet(String filePath, String sheetName) {
		return dataHandlingKeywords.readExcelSheet(filePath, sheetName);
	}

	/**
	 * Writes data to an Excel sheet from a List of Maps (each Map is a row).
	 *
	 * @param filePath  Absolute path to Excel (.xlsx) file.
	 * @param sheetName Name of the sheet to write (will be created or overwritten).
	 * @param data      List of Maps representing rows.
	 */
	public void writeExcelSheet(String filePath, String sheetName, List<Map<String, String>> data) {
		dataHandlingKeywords.writeExcelSheet(filePath, sheetName, data);
	}

	// ========================================
	// CSV HANDLING
	// ========================================

	/**
	 * Reads a CSV file and returns the data as a List of Maps (header to value).
	 *
	 * @param filePath Absolute path to CSV file.
	 * @param delimiter The delimiter used in CSV (e.g., ',').
	 * @return List of Maps representing rows.
	 */
	public List<Map<String, String>> readCSV(String filePath, char delimiter) {
		return dataHandlingKeywords.readCSV(filePath, delimiter);
	}

	/**
	 * Writes data to a CSV file from a List of Maps.
	 *
	 * @param filePath  Absolute path to CSV file.
	 * @param delimiter Delimiter to use in CSV.
	 * @param data      List of Maps representing rows.
	 */
	public void writeCSV(String filePath, char delimiter, List<Map<String, String>> data) {
		dataHandlingKeywords.writeCSV(filePath, delimiter, data);
	}

	// ========================================
	// JSON HANDLING
	// ========================================

	/**
	 * Reads a JSON file and returns it as a JsonNode tree.
	 *
	 * @param filePath Absolute path to JSON file.
	 * @return JsonNode root of the parsed JSON.
	 */
	public JsonNode readJSON(String filePath) {
		return dataHandlingKeywords.readJSON(filePath);
	}

	/**
	 * Writes a JsonNode to a JSON file.
	 *
	 * @param filePath Absolute path to JSON file.
	 * @param root     JsonNode to write.
	 */
	public void writeJSON(String filePath, JsonNode root) {
		dataHandlingKeywords.writeJSON(filePath, root);
	}

	// ========================================
	// UTILITY METHODS
	// ========================================

	/**
	 * Parses a JSON file and returns a value for the given JSON Pointer expression.
	 *
	 * @param filePath      Absolute path to JSON file.
	 * @param jsonPointer   JSON Pointer expression (e.g., "/data/user/name").
	 * @return Value as string or null if not found.
	 */
	public String readJSONPointerValue(String filePath, String jsonPointer) {
		return dataHandlingKeywords.readJSONPointerValue(filePath, jsonPointer);
	}

	/**
	 * Reads all lines from a plain text file.
	 *
	 * @param filePath Absolute path to text file.
	 * @return List of lines.
	 */
	public List<String> readTextFileLines(String filePath) {
		return dataHandlingKeywords.readTextFileLines(filePath);
	}

	/**
	 * Writes lines to a plain text file.
	 *
	 * @param filePath Absolute path to text file.
	 * @param lines    Lines to write.
	 */
	public void writeTextFileLines(String filePath, List<String> lines) {
		dataHandlingKeywords.writeTextFileLines(filePath, lines);
	}


	// ========================================
	// JAVASCRIPT KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// GENERAL JAVASCRIPT EXECUTION
	// ========================================

	/**
	 * Executes arbitrary synchronous JavaScript with optional arguments.
	 *
	 * @param script JavaScript code to execute.
	 * @param args   Optional script arguments.
	 * @return Result of script execution.
	 */
	public Object executeScript(String script, Object... args) {
		return javaScriptKeywords.executeScript(script, args);
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
		return javaScriptKeywords.executeAsyncScript(script, args);
	}

	// ========================================
	// ELEMENT ATTRIBUTE/VALUE/PROPERTY
	// ========================================

	/**
	 * Sets an attribute value on the specified element.
	 *
	 * @param xpath  XPath locator of the element.
	 * @param attr   Attribute name.
	 * @param value  Value to set.
	 */
	public void setAttribute(String xpath, String attr, String value) {
		javaScriptKeywords.setAttribute(xpath, attr, value);
	}

	/**
	 * Removes an attribute from the specified element.
	 *
	 * @param xpath XPath locator.
	 * @param attr  Attribute name to remove.
	 */
	public void removeAttribute(String xpath, String attr) {
		javaScriptKeywords.removeAttribute(xpath, attr);
	}

	/**
	 * Gets an attribute value from the element.
	 *
	 * @param xpath XPath locator.
	 * @param attr  Attribute name.
	 * @return Attribute value (string or null).
	 */
	public String getAttribute(String xpath, String attr) {
		return javaScriptKeywords.getAttribute(xpath, attr);
	}

	/**
	 * Sets the value property of an element (e.g., input field) directly via JS.
	 *
	 * @param xpath XPath locator.
	 * @param value Value to set.
	 */
	public void setValue(String xpath, String value) {
		javaScriptKeywords.setValue(xpath, value);
	}

	/**
	 * Gets the value property of an element via JS.
	 *
	 * @param xpath XPath locator.
	 * @return Value property as string.
	 */
	public String getValue(String xpath) {
		return javaScriptKeywords.getValue(xpath);
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
		javaScriptKeywords.addAttribute(xpath, attr, value);
	}

	// ========================================
	// SCROLLING OPERATIONS
	// ========================================

	/** Scrolls the viewport so the element is in view (align to top). */
	public void scrollToElement(String xpath) {
		javaScriptKeywords.scrollToElement(xpath);
	}

	/** Scrolls the viewport by specified pixels horizontally and vertically. */
	public void scrollBy(int x, int y) {
		javaScriptKeywords.scrollBy(x, y);
	}

	/** Scrolls to the bottom of the page. */
	public void scrollToBottom() {
		javaScriptKeywords.scrollToBottom();
	}

	/** Scrolls to the top of the page. */
	public void scrollToTop() {
		javaScriptKeywords.scrollToTop();
	}

	// ========================================
	// CSS STYLE / VISUALS
	// ========================================

	/**
	 * Gets computed style value for given CSS property on element.
	 *
	 * @param xpath        XPath of element.
	 * @param cssProperty  CSS property name (e.g., color, display).
	 * @return Computed CSS value.
	 */
	public String getComputedStyle(String xpath, String cssProperty) {
		return javaScriptKeywords.getComputedStyle(xpath, cssProperty);
	}

	/**
	 * Sets inline CSS style property on element.
	 *
	 * @param xpath       XPath of element.
	 * @param cssProperty CSS property name.
	 * @param value       Value to set.
	 */
	public void setStyle(String xpath, String cssProperty, String value) {
		javaScriptKeywords.setStyle(xpath, cssProperty, value);
	}

	/**
	 * Highlights an element temporarily by flashing its border color (for debugging).
	 *
	 * @param xpath XPath locator.
	 * @param flashes Number of flashes.
	 * @param delayMs Delay in milliseconds between flashes.
	 */
	public void flashElement(String xpath, int flashes, long delayMs) {
		javaScriptKeywords.flashElement(xpath, flashes, delayMs);
	}

	// ========================================
	// EVENT TRIGGERING
	// ========================================

	/**
	 * Triggers a DOM event on the element.
	 *
	 * @param xpath   XPath locator.
	 * @param eventName Name of event (click, change, input, focus, blur, etc.).
	 */
	public void triggerEvent(String xpath, String eventName) {
		javaScriptKeywords.triggerEvent(xpath, eventName);
	}

	// ========================================
	// DOM MANIPULATION
	// ========================================

	/**
	 * Removes element from DOM.
	 *
	 * @param xpath XPath locator.
	 */
	public void removeElement(String xpath) {
		javaScriptKeywords.removeElement(xpath);
	}

	// ========================================
	// UTILITY & WAIT FOR JS CONDITIONS
	// ========================================

	/**
	 * Waits until the given JavaScript condition returns true or timeout.
	 *
	 * @param jsCondition JavaScript condition string that returns boolean.
	 * @param timeoutSeconds Max seconds to wait.
	 */
	public void waitForJSCondition(String jsCondition, int timeoutSeconds) {
		javaScriptKeywords.waitForJSCondition(jsCondition, timeoutSeconds);
	}

	// ========================================
	// ALERTS, CONFIRMS, PROMPTS VIA JS
	// ========================================

	/**
	 * Generates a JavaScript alert with the provided message.
	 *
	 * @param message Alert message.
	 */
	public void jsAlert(String message) {
		javaScriptKeywords.jsAlert(message);
	}

	/**
	 * Generates a JS confirmation dialog with message.
	 *
	 * @param message Confirmation message.
	 */
	public void jsConfirm(String message) {
		javaScriptKeywords.jsConfirm(message);
	}

	/**
	 * Generates a JS prompt dialog with message and default text.
	 *
	 * @param message   Prompt message.
	 * @param defaultText Default text shown in prompt input.
	 */
	public void jsPrompt(String message, String defaultText) {
		javaScriptKeywords.jsPrompt(message, defaultText);
	}


	// ========================================
	// UPLOAD/DOWNLOAD KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// FILE UPLOAD OPERATIONS
	// ========================================

	/** Upload a file by sending the full file path to a file input element. */
	public void uploadFileByInput(String xpath, String absoluteFilePath, boolean excelData, String testCaseName) {
		uploadDownloadKeywords.uploadFileByInput(xpath, absoluteFilePath, excelData, testCaseName);
	}


	/** Upload a file using Robot class for native OS dialog. */
	public void uploadFileUsingRobot(String absoluteFilePath, boolean excelData, String testCaseName) {
		uploadDownloadKeywords.uploadFileUsingRobot(absoluteFilePath, excelData, testCaseName);
	}

	/** Upload a file using AutoIt script. */
	public void uploadFileUsingAutoIt(String autoItExecutablePath, String filePath, boolean excelData, String testCaseName) {
		uploadDownloadKeywords.uploadFileUsingAutoIt(autoItExecutablePath, filePath, excelData, testCaseName);
	}


	// ========================================
	// FILE DOWNLOAD OPERATIONS
	// ========================================

	/** Waits for a file to appear in the downloads directory. */
	public Path waitForFileDownload(String downloadsDir, String expectedFileName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    return uploadDownloadKeywords.waitForFileDownload(downloadsDir, expectedFileName, timeoutSeconds, excelData, testCaseName);
	}

	/**
	 * Initiates clicking on the given element to start a file download.
	 *
	 * @param xpath XPath locator of the download button/link.
	 * @param timeoutSeconds Timeout to wait before click.
	 */
	public void startFileDownload(String xpath, int timeoutSeconds) {
		uploadDownloadKeywords.startFileDownload(xpath, timeoutSeconds);
	}

	// ========================================
	// FILE MANAGEMENT OPERATIONS
	// ========================================

	/**
	 * Deletes all files from a directory. Useful to clean download folder before test.
	 *
	 * @param directoryPath Directory to clean files from.
	 */
	public void cleanDirectory(String directoryPath) {
		uploadDownloadKeywords.cleanDirectory(directoryPath);
	}
	
	/** Cleans all files from the download directory. */
	public void cleanDownloadDirectory() {
	    uploadDownloadKeywords.cleanDownloadDirectory();
	}

	/** Cleans all files from the upload directory. */
	public void cleanUploadDirectory() {
	    uploadDownloadKeywords.cleanUploadDirectory();
	}

	/** Cleans all files from the test data directory. */
	public void cleanTestDataDirectory() {
	    uploadDownloadKeywords.cleanTestDataDirectory();
	}

	// ========================================
	// FILE VERIFICATION OPERATIONS
	// ========================================

	/**
	 * Validates if file exists at expected path.
	 *
	 * @param expectedFilePath Absolute file path.
	 * @return true if file exists, else false.
	 */
	public boolean isFileDownloaded(String expectedFilePath) {
		return uploadDownloadKeywords.isFileDownloaded(expectedFilePath);
	}


	// ========================================
	// WAIT KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// WAIT CONFIGURATION
	// ========================================

	/** Sets the implicit wait timeout globally for the WebDriver */
	public void setImplicitWait(int timeoutSeconds) {
		waitKeywords.setImplicitWait(timeoutSeconds);
	}

	// ========================================
	// ELEMENT WAIT OPERATIONS
	// ========================================

	/** Waits until element is visible on the page. */
	public void waitForElementVisible(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementVisible(xpath, elementName, timeoutSeconds);
	}

	/** Waits until element is invisible or not present. */
	public void waitForElementInvisible(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementInvisible(xpath, elementName, timeoutSeconds);
	}

	/** Waits until element is clickable (visible and enabled). */
	public void waitForElementClickable(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementClickable(xpath, elementName, timeoutSeconds);
	}

	/** Waits until element is present in the DOM regardless of visibility. */
	public void waitForElementPresent(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementPresent(xpath, elementName, timeoutSeconds);
	}

	/** Waits until element is selected. */
	public void waitForElementSelected(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementSelected(xpath, elementName, timeoutSeconds);
	}

	/** Waits until checkbox or radio button is NOT selected. */
	public void waitForElementNotSelected(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementNotSelected(xpath, elementName, timeoutSeconds);
	}

	/** Waits for element to become enabled within timeout. */
	public void waitForElementToBeEnabled(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementToBeEnabled(xpath, elementName, timeoutSeconds);
	}

	/** Waits for element to become selected within timeout. */
	public void waitForElementToBeSelected(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForElementToBeSelected(xpath, elementName, timeoutSeconds);
	}

	// ========================================
	// TEXT AND INPUT WAIT OPERATIONS
	// ========================================

	/** Waits until element's text contains specified substring. */
	public void waitForTextInElement(String xpath, String text, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForTextInElement(xpath, text, elementName, timeoutSeconds, excelData, testCaseName);
	}

	/** Waits until the element's value attribute contains the specified string. */
	public void waitForValueInInput(String xpath, String valueFragment, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForValueInInput(xpath, valueFragment, elementName, timeoutSeconds, excelData, testCaseName);
	}


	/** Waits for specific text to be present in element within timeout. */
	public void waitForTextToBePresentInElement(String xpath, String expectedText, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForTextToBePresentInElement(xpath, expectedText, elementName, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// ATTRIBUTE WAIT OPERATIONS
	// ========================================

	/** Waits for element attribute to contain specific value within timeout. */
	public void waitForAttributeToContain(String xpath, String attributeName, String expectedValue, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForAttributeToContain(xpath, attributeName, expectedValue, elementName, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// PAGE STATE WAIT OPERATIONS
	// ========================================

	/** Waits until page title equals expected. */
	public void waitForTitleIs(String expectedTitle, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForTitleIs(expectedTitle, timeoutSeconds, excelData, testCaseName);
	}

	/** Waits until page title contains specified fragment. */
	public void waitForTitleContains(String titleFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForTitleContains(titleFragment, timeoutSeconds, excelData, testCaseName);
	}

	/** Waits for page title to contain specific text within timeout. */
	public void waitForTitleToContain(String expectedFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForTitleToContain(expectedFragment, timeoutSeconds, excelData, testCaseName);
	}


	/** Waits until the current URL equals expected. */
	public void waitForUrlToBe(String expectedUrl, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForUrlToBe(expectedUrl, timeoutSeconds, excelData, testCaseName);
	}

	/** Waits until the current URL contains specified fragment. */
	public void waitForUrlContains(String urlFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForUrlContains(urlFragment, timeoutSeconds, excelData, testCaseName);
	}


	/** Waits for current URL to contain specific text within timeout. */
	public void waitForUrlToContain(String expectedFragment, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForUrlToContain(expectedFragment, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// ALERT AND FRAME WAIT OPERATIONS
	// ========================================

	/** Waits until an alert is present on the page. */
	public void waitForAlertPresent(int timeoutSeconds) {
		waitKeywords.waitForAlertPresent(timeoutSeconds);
	}

	/** Waits for frame by XPath or name/id, then switches focus to it. */
	public void waitForFrameAndSwitch(String frameXpathOrName, int timeoutSeconds) {
		waitKeywords.waitForFrameAndSwitch(frameXpathOrName, timeoutSeconds);
	}

	// ========================================
	// MULTIPLE ELEMENTS WAIT OPERATIONS
	// ========================================

	/** Waits until all elements matching XPath are visible. */
	public void waitForAllElementsVisible(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForAllElementsVisible(xpath, elementName, timeoutSeconds);
	}

	/** Waits until all elements matching XPath are present in DOM. */
	public void waitForAllElementsPresent(String xpath, String elementName, int timeoutSeconds) {
		waitKeywords.waitForAllElementsPresent(xpath, elementName, timeoutSeconds);
	}

	/** Waits for specific number of elements matching XPath within timeout. */
	public void waitForElementCount(String xpath, int expectedCount, String elementName, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForElementCount(xpath, expectedCount, elementName, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// WINDOW WAIT OPERATIONS
	// ========================================

	/** Waits until the number of browser windows/tabs equals the expected count. */
	public void waitForNumberOfWindows(int numberOfWindows, int timeoutSeconds, boolean excelData, String testCaseName) {
	    waitKeywords.waitForNumberOfWindows(numberOfWindows, timeoutSeconds, excelData, testCaseName);
	}

	// ========================================
	// CUSTOM WAIT OPERATIONS
	// ========================================

	/** Fluent wait with custom polling interval. */
	public void fluentWait(String xpath, String elementName, int timeoutSeconds, int pollingMillis, Function<WebDriver, Boolean> condition) {
		waitKeywords.fluentWait(xpath, elementName, timeoutSeconds, pollingMillis, condition);
	}

	/** Waits until a custom JavaScript condition returns true. */
	public void waitForJavaScriptCondition(String jsCondition, int timeoutSeconds) {
		waitKeywords.waitForJavaScriptCondition(jsCondition, timeoutSeconds);
	}

	/** Waits for custom condition using JavaScript expression within timeout. */
	public void waitForCustomCondition(String jsCondition, int timeoutSeconds) {
		waitKeywords.waitForCustomCondition(jsCondition, timeoutSeconds);
	}

	// ========================================
	// HARD WAIT OPERATIONS
	// ========================================

	/** Waits for specified number of seconds (hard wait). */
	public void waitForSeconds(int seconds) {
		waitKeywords.waitForSeconds(seconds);
	}

	/** Waits for specified number of milliseconds (hard wait). */
	public void waitForMilliseconds(int milliseconds) {
		waitKeywords.waitForMilliseconds(milliseconds);
	}



	// ========================================
	// MOUSE/KEYBOARD KEYWORDS - ALL METHODS
	// ========================================

	// ========================================
	// MOUSE ACTIONS
	// ========================================

	/** Moves mouse cursor to hover over the element located by XPath. */
	public void mouseHover(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.mouseHover(xpath, timeoutSeconds);
	}

	/** Moves mouse over WebElement to the specified offset relative to element. */
	public void mouseMoveToElement(WebElement element, int offsetX, int offsetY) {
		mouseKeyboardKeywords.mouseMoveToElement(element, offsetX, offsetY);
	}

	/** Performs a right-click (context click) on the element located by XPath. */
	public void rightClick(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.rightClick(xpath, timeoutSeconds);
	}

	/** Double clicks the element located by XPath. */
	public void doubleClick(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.doubleClick(xpath, timeoutSeconds);
	}

	/** Clicks and holds mouse button down on element located by XPath. */
	public void clickAndHold(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.clickAndHold(xpath, timeoutSeconds);
	}

	/** Releases mouse button held on element located by XPath. */
	public void releaseClick(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.releaseClick(xpath, timeoutSeconds);
	}

	/** Drags element from source XPath and drops on target XPath. */
	public void dragAndDrop(String sourceXpath, String targetXpath, int timeoutSeconds) {
		mouseKeyboardKeywords.dragAndDrop(sourceXpath, targetXpath, timeoutSeconds);
	}

	/** Drags element from source XPath by x and y offsets. */
	public void dragAndDropByOffset(String sourceXpath, int offsetX, int offsetY, int timeoutSeconds) {
		mouseKeyboardKeywords.dragAndDropByOffset(sourceXpath, offsetX, offsetY, timeoutSeconds);
	}

	/** Scrolls viewport until element located by XPath is in view. */
	public void scrollToElement(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.scrollToElement(xpath, timeoutSeconds);
	}

	/** Scrolls the page by specified pixel amounts x (horizontal) and y (vertical). */
	public void scrollByV2(int x, int y) {
		mouseKeyboardKeywords.scrollBy(x, y);
	}

	// ========================================
	// KEYBOARD ACTIONS
	// ========================================

	/** Sends specified keys to element located by XPath. */
	public void sendKeysToElement(String xpath, CharSequence keys, int timeoutSeconds) {
		mouseKeyboardKeywords.sendKeysToElement(xpath, keys, timeoutSeconds);
	}

	/** Sends specified keys to currently active/focused element in the browser. */
	public void sendKeysToActiveElement(CharSequence keys) {
		mouseKeyboardKeywords.sendKeysToActiveElement(keys);
	}

	/** Presses down a specific keyboard key. */
	public void pressKey(Keys key) {
		mouseKeyboardKeywords.pressKey(key);
	}

	/** Releases a previously pressed keyboard key. */
	public void releaseKey(Keys key) {
		mouseKeyboardKeywords.releaseKey(key);
	}

	/** Holds a key down on specified element located by XPath. */
	public void keyDownOnElement(String xpath, Keys key, int timeoutSeconds) {
		mouseKeyboardKeywords.keyDownOnElement(xpath, key, timeoutSeconds);
	}

	/** Releases a key up on specified element located by XPath. */
	public void keyUpOnElement(String xpath, Keys key, int timeoutSeconds) {
		mouseKeyboardKeywords.keyUpOnElement(xpath, key, timeoutSeconds);
	}

	/** Sends a shortcut key combination (like Ctrl+C) to element located by XPath. */
	public void sendKeyboardShortcut(String xpath, List<Keys> keys, int timeoutSeconds) {
		mouseKeyboardKeywords.sendKeyboardShortcut(xpath, keys, timeoutSeconds);
	}

	/** Clears any existing text and types new text into element located by XPath. */
	public void clearAndType(String xpath, String text, int timeoutSeconds) {
		mouseKeyboardKeywords.clearAndType(xpath, text, timeoutSeconds);
	}

	// ========================================
	// COMPOSITE AND SPECIAL ACTIONS
	// ========================================

	/** Clicks at specified offset inside element located by XPath. */
	public void clickAtOffset(String xpath, int offsetX, int offsetY, int timeoutSeconds) {
		mouseKeyboardKeywords.clickAtOffset(xpath, offsetX, offsetY, timeoutSeconds);
	}

	/** Moves mouse by offset relative to current position. */
	public void moveByOffset(int offsetX, int offsetY) {
		mouseKeyboardKeywords.moveByOffset(offsetX, offsetY);
	}

	/** Clicks on element holding specified modifier key (Ctrl, Shift, Alt). */
	public void clickWithModifier(String xpath, Keys modifierKey, int timeoutSeconds) {
		mouseKeyboardKeywords.clickWithModifier(xpath, modifierKey, timeoutSeconds);
	}

	/** Performs a right click at offset inside the element located by XPath. */
	public void rightClickAtOffset(String xpath, int offsetX, int offsetY, int timeoutSeconds) {
		mouseKeyboardKeywords.rightClickAtOffset(xpath, offsetX, offsetY, timeoutSeconds);
	}

	/** Performs drag-and-drop from source WebElement to target WebElement. */
	public void dragAndDropByWebElements(WebElement source, WebElement target) {
		mouseKeyboardKeywords.dragAndDropByWebElements(source, target);
	}

	// ========================================
	// VERIFICATION METHODS
	// ========================================

	/**
	 * Verifies that the element located by XPath is hovered by checking 'hover' CSS class or style.
	 * (This requires convention or customization, here we check :hover by JS)
	 */
	public void verifyElementIsHovered(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.verifyElementIsHovered(xpath, timeoutSeconds);
	}

	/** Verifies the element located by XPath has keyboard focus within timeout. */
	public void verifyFocusOnElement(String xpath, int timeoutSeconds) {
		mouseKeyboardKeywords.verifyFocusOnElement(xpath, timeoutSeconds);
	}

	/**
	 * Verifies that pressing the specified key triggers the expected effect,
	 * this is application-specific and may require overriding or custom JS.
	 * Here, as a simple placeholder, we check keydown event is triggered
	 * by executing JS (you may implement your own verification logic).
	 */
	public void verifyKeyPressedEffect(String xpath, Keys key, int timeoutSeconds) {
		mouseKeyboardKeywords.verifyKeyPressedEffect(xpath, key, timeoutSeconds);
	}

	// ========================================
	// SELECT OPTION METHODS
	// ========================================

	/** Selects option by visible text in dropdown element. */
	public void selectByVisibleText(String xpath, String visibleTextOrKey, String elementName, boolean excelData, String testName) {
		selectKeywords.selectByVisibleText(xpath, visibleTextOrKey, elementName, excelData, testName);
	}

	/** Selects option by value attribute in dropdown element. */
	public void selectByValue(String xpath, String valueOrKey, String elementName, boolean excelData, String testName) {
		selectKeywords.selectByValue(xpath, valueOrKey, elementName, excelData, testName);
	}

	/** Selects option by index position in dropdown element. */
	public void selectByIndex(String xpath, int index, String elementName) {
		selectKeywords.selectByIndex(xpath, index, elementName);
	}

	/** Selects first option containing partial visible text. */
	public void selectOptionContainsText(String xpath, String partialTextOrKey, String elementName, boolean excelData, String testName) {
		selectKeywords.selectOptionContainsText(xpath, partialTextOrKey, elementName, excelData, testName);
	}

	/** Selects the last option in the dropdown list. */
	public void selectLastOption(String xpath, String elementName) {
		selectKeywords.selectLastOption(xpath, elementName);
	}

	/** Selects all options in a multi-select dropdown. */
	public void selectAllOptions(String xpath, String elementName) {
		selectKeywords.selectAllOptions(xpath, elementName);
	}

	// ========================================
	// DESELECT METHODS (FOR MULTI-SELECT)
	// ========================================

	/** Deselects option by visible text in multi-select dropdown. */
	public void deselectByVisibleText(String xpath, String visibleTextOrKey, String elementName, boolean excelData, String testName) {
		selectKeywords.deselectByVisibleText(xpath, visibleTextOrKey, elementName, excelData, testName);
	}

	/** Deselects option by value attribute in multi-select dropdown. */
	public void deselectByValue(String xpath, String valueOrKey, String elementName, boolean excelData, String testName) {
		selectKeywords.deselectByValue(xpath, valueOrKey, elementName, excelData, testName);
	}

	/** Deselects option by index position in multi-select dropdown. */
	public void deselectByIndex(String xpath, int index, String elementName) {
		selectKeywords.deselectByIndex(xpath, index, elementName);
	}

	/** Deselects all options in multi-select dropdown. */
	public void deselectAll(String xpath, String elementName) {
		selectKeywords.deselectAll(xpath, elementName);
	}

	/** Clear all selections (synonym for deselectAll) to be explicit. */
	public void clearAllSelections(String xpath, String elementName) {
		selectKeywords.clearAllSelections(xpath, elementName);
	}

	// ========================================
	// GET SELECTED OPTIONS
	// ========================================

	/** Gets the text of the first selected option in dropdown. */
	public String getSelectedOptionText(String xpath, String elementName) {
		return selectKeywords.getSelectedOptionText(xpath, elementName);
	}

	/** Gets text of all selected options in multi-select dropdown. */
	public List<String> getAllSelectedOptionsText(String xpath, String elementName) {
		return selectKeywords.getAllSelectedOptionsText(xpath, elementName);
	}

	// ========================================
	// UTILITY METHODS
	// ========================================

	/** Checks if the select element supports multiple selections. */
	public boolean isMultiple(String xpath, String elementName) {
		return selectKeywords.isMultiple(xpath, elementName);
	}

	/** Returns the number of options in the select dropdown. */
	public int getOptionsCount(String xpath, String elementName) {
		return selectKeywords.getOptionsCount(xpath, elementName);
	}

	/** Waits for the dropdown options to be populated (count > 0). */
	public void waitForOptionsToLoad(String xpath, String elementName, int timeoutSeconds) {
		selectKeywords.waitForOptionsToLoad(xpath, elementName, timeoutSeconds);
	}

	// ========================================
	// VERIFICATION METHODS
	// ========================================

	/** Verify the selected option matches expected value (supports Excel key or direct value). */
	public void verifySelectedOption(String xpath, String expectedValueOrKey, String elementName, boolean excelData, String testName) {
		selectKeywords.verifySelectedOption(xpath, expectedValueOrKey, elementName, excelData, testName);
	}

	/** Verify that an option with the exact visible text exists. */
	public void verifyOptionExists(String xpath, String optionTextOrKey, String elementName, boolean excelData, String testName) {
		selectKeywords.verifyOptionExists(xpath, optionTextOrKey, elementName, excelData, testName);
	}

	/** Verifies that no options are selected in the multi-select dropdown. */
	public void verifyNoSelection(String xpath, String elementName) {
		selectKeywords.verifyNoSelection(xpath, elementName);
	}
}
