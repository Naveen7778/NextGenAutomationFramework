package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExcelUtilities;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * DatePickerKeywords - keywords to handle diverse date/time picker scenarios in web automation.
 */
public class DatePickerKeywords {

	private final WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);

	public DatePickerKeywords() {
		this.driver = DriverManager.getDriver();
	}

	/** Reads integer config property or returns default if missing or invalid. */
	private int getIntConfigProperty(String key, int defaultValue) {
		if (BaseClass.getProps() == null) return defaultValue;
		try {
			return Integer.parseInt(BaseClass.getProps().getProperty(key, String.valueOf(defaultValue)));
		} catch (NumberFormatException e) {
			logger.warn("Invalid config for '{}'; using default {}", key, defaultValue);
			return defaultValue;
		}
	}

	/** Validates a string input, throws FrameworkException if null or empty. */
	private void validateInput(String val, String paramName) {
		if (val == null || val.trim().isEmpty()) {
			throw new FrameworkException(paramName + " cannot be null or empty");
		}
	}

	/** Returns a configured FluentWait for waiting with polling, ignoring common exceptions. */
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

	// === Direct Input Style Pickers ===

	/** Sets the value of a date input field located by XPath. */
	public void setDateInputValue(String xpath, String dateStr, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(dateStr, "Date String");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, dateStr, "Date String", excelData);

		try {
			BaseClass.logActionStart("Setting date input value to '" + BaseClass.mask(valueToUse) + "'", "Date Input");

			WebElement input = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			input.clear();
			input.sendKeys(valueToUse);
			logger.info("Set date input [{}] value to '{}'", xpath, valueToUse);

			BaseClass.logActionSuccess("Set date input value to '" + BaseClass.mask(valueToUse) + "'", "Date Input");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Set date input value", "Date Input", "Timeout waiting for date input to be clickable: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for date input to be clickable: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Set date input value", "Date Input", "Failed to set date input value: " + e.getMessage());
			throw new FrameworkException("Failed to set date input value", e);
		}
	}

	/** Gets the current value from a date input field. */
	public String getDateInputValue(String xpath, int timeoutSeconds) {
		validateInput(xpath, "XPath");

		try {
			BaseClass.logActionStart("Getting date input value", "Date Input");

			WebElement input = getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			String val = input.getAttribute("value");
			logger.info("Got date input [{}] value '{}'", xpath, val);

			BaseClass.logActionSuccess("Retrieved date input value: '" + BaseClass.mask(val) + "'", "Date Input");
			return val;

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Get date input value", "Date Input", "Timeout waiting for date input visibility: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for date input visibility: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Get date input value", "Date Input", "Failed to get date input value: " + e.getMessage());
			throw new FrameworkException("Failed to get date input value", e);
		}
	}

	// === Popup / Widget Style Pickers ===

	/** Opens the date picker popup by clicking the input/icon. */
	public void openDatePicker(String xpath, int timeoutSeconds) {
		validateInput(xpath, "XPath");

		try {
			BaseClass.logActionStart("Opening date picker", "Date Picker");

			WebElement pickerControl = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			pickerControl.click();
			logger.info("Opened date picker by clicking [{}]", xpath);

			BaseClass.logActionSuccess("Opened date picker", "Date Picker");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Open date picker", "Date Picker", "Timeout waiting to open date picker: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to open date picker: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Open date picker", "Date Picker", "Failed to open date picker: " + e.getMessage());
			throw new FrameworkException("Failed to open date picker", e);
		}
	}

	/** Selects a day in the visible date picker using XPath. */
	public void selectDayInDatePicker(String dayXpath, int timeoutSeconds) {
		validateInput(dayXpath, "Day XPath");

		try {
			BaseClass.logActionStart("Selecting day in date picker", "Date Picker");

			WebElement dayElem = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(dayXpath)));
			dayElem.click();
			logger.info("Selected day in date picker [{}]", dayXpath);

			BaseClass.logActionSuccess("Selected day in date picker", "Date Picker");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Select day in date picker", "Date Picker", "Timeout waiting to select day: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to select day in date picker: " + dayXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Select day in date picker", "Date Picker", "Failed to select day: " + e.getMessage());
			throw new FrameworkException("Failed to select day in date picker", e);
		}
	}

	/** Selects a month in the date picker widget using XPath. */
	public void selectMonthInDatePicker(String monthXpath, int timeoutSeconds) {
		validateInput(monthXpath, "Month XPath");

		try {
			BaseClass.logActionStart("Selecting month in date picker", "Date Picker");

			WebElement monthElem = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(monthXpath)));
			monthElem.click();
			logger.info("Selected month in date picker [{}]", monthXpath);

			BaseClass.logActionSuccess("Selected month in date picker", "Date Picker");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Select month in date picker", "Date Picker", "Timeout waiting to select month: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to select month in date picker: " + monthXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Select month in date picker", "Date Picker", "Failed to select month: " + e.getMessage());
			throw new FrameworkException("Failed to select month in date picker", e);
		}
	}

	/** Selects a year in the date picker widget using XPath. */
	public void selectYearInDatePicker(String yearXpath, int timeoutSeconds) {
		validateInput(yearXpath, "Year XPath");

		try {
			BaseClass.logActionStart("Selecting year in date picker", "Date Picker");

			WebElement yearElem = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(yearXpath)));
			yearElem.click();
			logger.info("Selected year in date picker [{}]", yearXpath);

			BaseClass.logActionSuccess("Selected year in date picker", "Date Picker");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Select year in date picker", "Date Picker", "Timeout waiting to select year: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to select year in date picker: " + yearXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Select year in date picker", "Date Picker", "Failed to select year: " + e.getMessage());
			throw new FrameworkException("Failed to select year in date picker", e);
		}
	}

	/** Navigates to the next month in the date picker by clicking the "next" button XPath. */
	public void navigateToNextMonth(String nextBtnXpath, int timeoutSeconds) {
		validateInput(nextBtnXpath, "Next Month Button XPath");

		try {
			BaseClass.logActionStart("Navigating to next month in date picker", "Date Picker Navigation");

			WebElement nextBtn = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(nextBtnXpath)));
			nextBtn.click();
			logger.info("Navigated to next month in date picker [{}]", nextBtnXpath);

			BaseClass.logActionSuccess("Navigated to next month", "Date Picker Navigation");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Navigate to next month", "Date Picker Navigation", "Timeout waiting to click next month button: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to click next month button: " + nextBtnXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Navigate to next month", "Date Picker Navigation", "Failed to navigate to next month: " + e.getMessage());
			throw new FrameworkException("Failed to navigate to next month", e);
		}
	}

	/** Navigates to the previous month in the date picker by clicking the "previous" button XPath. */
	public void navigateToPreviousMonth(String prevBtnXpath, int timeoutSeconds) {
		validateInput(prevBtnXpath, "Previous Month Button XPath");

		try {
			BaseClass.logActionStart("Navigating to previous month in date picker", "Date Picker Navigation");

			WebElement prevBtn = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(prevBtnXpath)));
			prevBtn.click();
			logger.info("Navigated to previous month in date picker [{}]", prevBtnXpath);

			BaseClass.logActionSuccess("Navigated to previous month", "Date Picker Navigation");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Navigate to previous month", "Date Picker Navigation", "Timeout waiting to click previous month button: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to click previous month button: " + prevBtnXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Navigate to previous month", "Date Picker Navigation", "Failed to navigate to previous month: " + e.getMessage());
			throw new FrameworkException("Failed to navigate to previous month", e);
		}
	}

	/** Uses JavaScript to directly set the date on special date-pickers where normal sendKeys/clicks don't work. */
	public void selectDateByCustomJS(String dateStr, String datePickerXpath, boolean excelData, String testCaseName) {
		validateInput(dateStr, "Date String");
		validateInput(datePickerXpath, "Date Picker XPath");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, dateStr, "Date String", excelData);

		try {
			BaseClass.logActionStart("Setting date '" + BaseClass.mask(valueToUse) + "' using JavaScript", "Date Picker JS");

			WebElement datePicker = getWait(5).until(ExpectedConditions.presenceOfElementLocated(By.xpath(datePickerXpath)));
			String script = "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));";
			((JavascriptExecutor) driver).executeScript(script, datePicker, valueToUse);
			logger.info("Set date picker [{}] value to '{}' via JS", datePickerXpath, valueToUse);

			BaseClass.logActionSuccess("Set date using JavaScript", "Date Picker JS");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Set date by custom JS", "Date Picker JS", "Timeout waiting for date picker presence: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for date picker presence: " + datePickerXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Set date by custom JS", "Date Picker JS", "Failed to set date using JavaScript: " + e.getMessage());
			throw new FrameworkException("Failed to set date using JavaScript", e);
		}
	}

	// === Multi-Component / Composite Selectors ===

	/**
	 * Sets date by selecting day, month and year dropdowns.
	 * Supports fetching date values from Excel based on the testCaseName if excelData is true.
	 */
	public void setDateByDropdowns(String dayXpath, String monthXpath, String yearXpath,
			String day, String month, String year, int timeoutSeconds, boolean excelData, String testCaseName) {

		validateInput(dayXpath, "Day Dropdown XPath");
		validateInput(monthXpath, "Month Dropdown XPath");
		validateInput(yearXpath, "Year Dropdown XPath");
		validateInput(day, "Day");
		validateInput(month, "Month");
		validateInput(year, "Year");

		// Get the actual values to use (from Excel or direct input)
		String dayToUse = getInputValue(testCaseName, day, "Day", excelData);
		String monthToUse = getInputValue(testCaseName, month, "Month", excelData);
		String yearToUse = getInputValue(testCaseName, year, "Year", excelData);

		try {
			BaseClass.logActionStart("Setting date by dropdowns: Day='" + dayToUse + "', Month='" + monthToUse + "', Year='" + yearToUse + "'", "Date Dropdown");

			Select daySelect = new Select(getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(dayXpath))));
			daySelect.selectByVisibleText(dayToUse);

			Select monthSelect = new Select(getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(monthXpath))));
			monthSelect.selectByVisibleText(monthToUse);

			Select yearSelect = new Select(getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(yearXpath))));
			yearSelect.selectByVisibleText(yearToUse);

			logger.info("Set date by dropdowns Day:[{}] Month:[{}] Year:[{}]", dayToUse, monthToUse, yearToUse);
			BaseClass.logActionSuccess("Set date by dropdowns: " + dayToUse + "/" + monthToUse + "/" + yearToUse, "Date Dropdown");

		} catch (TimeoutException | NoSuchElementException e) {
			BaseClass.logActionFailure("Set date by dropdowns", "Date Dropdown", "Failed to set date by dropdowns: " + e.getMessage());
			throw new FrameworkException("Failed to set date by dropdowns: " + e.getMessage(), e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Set date by dropdowns", "Date Dropdown", "Unexpected error setting date by dropdowns: " + e.getMessage());
			throw new FrameworkException("Failed to set date by dropdowns", e);
		}
	}


	// === Validation, Disabled Days, and Edge Cases ===

	/** Checks if a specific day element is selectable/enabled in the date picker. */
	public boolean isDateSelectable(String dayXpath, int timeoutSeconds) {
		validateInput(dayXpath, "Day XPath");

		try {
			BaseClass.logActionStart("Checking if date is selectable", "Date Picker Validation");

			WebElement dayElem = getWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(By.xpath(dayXpath)));
			boolean enabled = dayElem.isEnabled() && dayElem.isDisplayed();
			logger.info("Day [{}] selectable: {}", dayXpath, enabled);

			BaseClass.logActionSuccess("Date selectable status: " + enabled, "Date Picker Validation");
			return enabled;

		} catch (TimeoutException e) {
			logger.warn("Day [{}] not selectable or not present within timeout", dayXpath);
			BaseClass.logActionSuccess("Date not selectable (not present within timeout)", "Date Picker Validation");
			return false;
		} catch (Exception e) {
			BaseClass.logActionFailure("Check if date is selectable", "Date Picker Validation", "Error checking date selectability: " + e.getMessage());
			return false;
		}
	}

	/** Verifies the date picker popup opens when triggered; waits for popup presence. */
	public void verifyDatePickerOpens(String pickerPopupXpath, int timeoutSeconds) {
		validateInput(pickerPopupXpath, "Date Picker Popup XPath");

		try {
			BaseClass.logActionStart("Verifying date picker popup opens", "Date Picker Validation");

			getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pickerPopupXpath)));
			logger.info("Verified date picker popup [{}] is open", pickerPopupXpath);

			BaseClass.logActionSuccess("Date picker popup opened successfully", "Date Picker Validation");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify date picker opens", "Date Picker Validation", "Date picker popup did not open within timeout: " + e.getMessage());
			throw new FrameworkException("Date picker popup did not open: " + pickerPopupXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify date picker opens", "Date Picker Validation", "Failed to verify date picker opens: " + e.getMessage());
			throw new FrameworkException("Failed to verify date picker opens", e);
		}
	}

	/** Verifies that the date input field or picker shows the expected date string. */
	public void verifyDatePickerValue(String xpath, String expectedDate, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(expectedDate, "Expected Date");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, expectedDate, "Expected Date", excelData);

		try {
			BaseClass.logActionStart("Verifying date picker value equals '" + BaseClass.mask(valueToUse) + "'", "Date Picker Validation");

			WebElement input = getWait(timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			String actualDate = input.getAttribute("value");

			if (!valueToUse.equals(actualDate)) {
				BaseClass.logActionFailure("Verify date picker value", "Date Picker Validation", "Expected '" + valueToUse + "' but found '" + actualDate + "'");
				throw new FrameworkException("Expected date '" + valueToUse + "' but found '" + actualDate + "'");
			}

			logger.info("Verified date picker [{}] value equals '{}'", xpath, valueToUse);
			BaseClass.logActionSuccess("Date picker value verified successfully", "Date Picker Validation");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Verify date picker value", "Date Picker Validation", "Timeout waiting for date picker value: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for date picker value: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Verify date picker value", "Date Picker Validation", "Failed to verify date picker value: " + e.getMessage());
			throw new FrameworkException("Failed to verify date picker value", e);
		}
	}

	/** Clears any date selection from the input/picker by clearing the input field. */
	public void clearDate(String xpath, int timeoutSeconds) {
		validateInput(xpath, "XPath");

		try {
			BaseClass.logActionStart("Clearing date input", "Date Input");

			WebElement input = getWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			input.clear();
			logger.info("Cleared date input [{}]", xpath);

			BaseClass.logActionSuccess("Cleared date input", "Date Input");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Clear date input", "Date Input", "Timeout waiting to clear date input: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to clear date input: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Clear date input", "Date Input", "Failed to clear date input: " + e.getMessage());
			throw new FrameworkException("Failed to clear date input", e);
		}
	}

	// === Date Range Pickers ===

	/**
	 * Sets the start and end dates of a date range picker using input fields.
	 * Supports fetching start and end dates from Excel based on the testCaseName if excelData is true.
	 */
	public void setDateRange(String startXpath, String endXpath, String startDate, String endDate, int timeoutSeconds, boolean excelData, String testCaseName) {
		validateInput(startXpath, "Start Date XPath");
		validateInput(endXpath, "End Date XPath");
		validateInput(startDate, "Start Date");
		validateInput(endDate, "End Date");

		// Get the actual values to use (from Excel or direct input)
		String startDateToUse = getInputValue(testCaseName, startDate, "Start Date", excelData);
		String endDateToUse = getInputValue(testCaseName, endDate, "End Date", excelData);

		try {
			BaseClass.logActionStart("Setting date range: Start='" + BaseClass.mask(startDateToUse) + "', End='" + BaseClass.mask(endDateToUse) + "'", "Date Range");

			setDateInputValue(startXpath, startDateToUse, timeoutSeconds, false, "");
			setDateInputValue(endXpath, endDateToUse, timeoutSeconds, false, "");
			logger.info("Set date range Start:[{}] End:[{}]", startDateToUse, endDateToUse);

			BaseClass.logActionSuccess("Set date range successfully", "Date Range");

		} catch (Exception e) {
			BaseClass.logActionFailure("Set date range", "Date Range", "Failed to set date range: " + e.getMessage());
			throw new FrameworkException("Failed to set date range", e);
		}
	}

	// === JS and Utility Actions ===

	/**
	 * Sets the date value directly using JavaScript (for pickers resisting standard inputs).
	 */
	public void setDateByJS(String xpath, String dateStr) {
		validateInput(xpath, "XPath");
		validateInput(dateStr, "Date String");

		try {
			BaseClass.logActionStart("Setting date '" + BaseClass.mask(dateStr) + "' using JavaScript", "Date Input JS");

			WebElement el = getWait(5).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
			((JavascriptExecutor) driver).executeScript("arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('change'));", el, dateStr);
			logger.info("Set date via JS [{}] to '{}'", xpath, dateStr);

			BaseClass.logActionSuccess("Set date using JavaScript", "Date Input JS");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Set date by JS", "Date Input JS", "Timeout waiting for element: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for element to set date by JS: " + xpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Set date by JS", "Date Input JS", "Failed to set date using JavaScript: " + e.getMessage());
			throw new FrameworkException("Failed to set date using JavaScript", e);
		}
	}

	/**
	 * Waits until the date picker popup is closed/hidden.
	 */
	public void waitForDatePickerToClose(String pickerPopupXpath, int timeoutSeconds) {
		validateInput(pickerPopupXpath, "Picker Popup XPath");

		try {
			BaseClass.logActionStart("Waiting for date picker popup to close", "Date Picker");

			getWait(timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(pickerPopupXpath)));
			logger.info("Date picker popup [{}] closed", pickerPopupXpath);

			BaseClass.logActionSuccess("Date picker popup closed", "Date Picker");

		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Wait for date picker to close", "Date Picker", "Timeout waiting for date picker to close: " + e.getMessage());
			throw new FrameworkException("Timeout waiting for date picker to close: " + pickerPopupXpath, e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Wait for date picker to close", "Date Picker", "Failed waiting for date picker to close: " + e.getMessage());
			throw new FrameworkException("Failed waiting for date picker to close", e);
		}
	}

	/**
	 * Selects today's date in a date picker by dynamically fetching the current system date,
	 * formatting it to match the date picker's expected format, and selecting the corresponding day.
	 */
	public void selectTodayInDatePicker(String dayXpathPattern, String dateFormatForDay, int timeoutSeconds) {
		if (dayXpathPattern == null || !dayXpathPattern.contains("%s")) {
			BaseClass.logActionFailure("Select today in date picker", "Date Picker", "dayXpathPattern must contain '%s' placeholder");
			throw new FrameworkException("dayXpathPattern must be non-null and contain a '%s' placeholder for day");
		}

		try {
			BaseClass.logActionStart("Selecting today's date in date picker", "Date Picker");

			// Get todays date
			LocalDate today = LocalDate.now();

			// Determine the day string to search in calendar
			String dayToSelect;
			if (dateFormatForDay == null) {
				// Use day of month as integer string, e.g. '5', '15', '31'
				dayToSelect = String.valueOf(today.getDayOfMonth());
			} else {
				// Format day as per format, e.g. with leading zero '05'
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatForDay);
				dayToSelect = today.format(formatter);
			}

			// Prepare the XPath for current day element
			String dayXpath = String.format(dayXpathPattern, dayToSelect);
			WebElement dayElement = getWait(timeoutSeconds).until(
					ExpectedConditions.elementToBeClickable(By.xpath(dayXpath))
					);

			dayElement.click();
			logger.info("Selected today's date ({}) in date picker via XPath [{}]", dayToSelect, dayXpath);

			BaseClass.logActionSuccess("Selected today's date (" + dayToSelect + ") in date picker", "Date Picker");

		} catch (DateTimeParseException e) {
			BaseClass.logActionFailure("Select today in date picker", "Date Picker", "Failed to parse/format today's date: " + e.getMessage());
			throw new FrameworkException("Failed to parse/format today's date", e);
		} catch (TimeoutException e) {
			BaseClass.logActionFailure("Select today in date picker", "Date Picker", "Timeout waiting to select today's date: " + e.getMessage());
			throw new FrameworkException("Timeout waiting to select today's date in date picker", e);
		} catch (Exception e) {
			BaseClass.logActionFailure("Select today in date picker", "Date Picker", "Failed to select today's date: " + e.getMessage());
			throw new FrameworkException("Failed to select today's date", e);
		}
	}

	/**
	 * Inputs a date into a textbox that contains fixed delimiters (e.g., '/') by typing each digit
	 * separately using Actions, preserving the delimiters.
	 * Supports fetching the date string from Excel based on the testCaseName if excelData is true.
	 */
	public void inputDateWithFixedDelimiters(String xpath, String dateStr, int timeoutSeconds, char delimiter, boolean excelData, String testCaseName) {
		validateInput(xpath, "XPath");
		validateInput(dateStr, "Date String");

		// Get the actual value to use (from Excel or direct input)
		String valueToUse = getInputValue(testCaseName, dateStr, "Date String", excelData);

		try {
			BaseClass.logActionStart("Inputting date '" + BaseClass.mask(valueToUse) + "' with fixed delimiters '" + delimiter + "'", "Date Input");

			WebElement inputElement = getWait(timeoutSeconds)
					.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

			inputElement.clear();  // optional clear if you want to start fresh

			Actions actions = new Actions(driver);
			inputElement.click();

			for (int i = 0, dateIndex = 0; i < inputElement.getAttribute("value").length(); i++) {
				char currentChar = inputElement.getAttribute("value").charAt(i);
				if (currentChar == delimiter) {
					// Move the cursor ahead (right arrow) to skip delimiter
					actions.sendKeys(Keys.ARROW_RIGHT).perform();
				} else if (dateIndex < valueToUse.length()) {
					// Send one digit from date string
					char ch = valueToUse.charAt(dateIndex);
					actions.sendKeys(String.valueOf(ch)).perform();
					dateIndex++;
				} else {
					break; // No more date digits to input
				}
				// Small pause per character can be added here optionally if needed
			}

			logger.info("Entered date [{}] with fixed delimiters [{}] into input [{}]", valueToUse, delimiter, xpath);
			BaseClass.logActionSuccess("Entered date with fixed delimiters", "Date Input");

		} catch (Exception e) {
			BaseClass.logActionFailure("Input date with fixed delimiters", "Date Input", "Failed to input date with fixed delimiters: " + e.getMessage());
			throw new FrameworkException("Failed to input date with fixed delimiters into element: " + xpath, e);
		}
	}

	/**
	 * Fetches today's date, formats it as digits only (no delimiters), and inputs it
	 * digit-by-digit into a date input textbox that has fixed delimiters (e.g., '/').
	 */
	public void inputTodayDateWithFixedDelimiters(String xpath, int timeoutSeconds, char delimiter, String dateFormat) {
		validateInput(xpath, "XPath");
		validateInput(dateFormat, "Date Format");

		try {
			BaseClass.logActionStart("Inputting today's date with fixed delimiters '" + delimiter + "' using format '" + dateFormat + "'", "Date Input");

			WebElement inputElement = getWait(timeoutSeconds)
					.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

			// Fetch today's date and format as per specified pattern (digits only)
			LocalDate today = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
			String formattedDate = today.format(formatter);  // e.g. "07282025" for "MMddyyyy"

			inputElement.clear(); // Optional: clear existing text

			Actions actions = new Actions(driver);
			inputElement.click();

			// Type digits of the formatted date one by one,
			// moving cursor forward if delimiter is encountered in the input's pre-filled value
			String currentValue = inputElement.getAttribute("value");
			int dateIndex = 0;

			for (int i = 0; i < currentValue.length(); i++) {
				char currentChar = currentValue.charAt(i);
				if (currentChar == delimiter) {
					// Move cursor right to skip the delimiter
					actions.sendKeys(Keys.ARROW_RIGHT).perform();
				} else if (dateIndex < formattedDate.length()) {
					char digit = formattedDate.charAt(dateIndex++);
					actions.sendKeys(String.valueOf(digit)).perform();
				} else {
					break; // Finished typing all digits
				}

				// Optional: add a short wait here if needed between keys to mimic typing speed
				// Thread.sleep(50);
			}

			logger.info("Input today's date [{}] with fixed delimiter [{}] into element [{}]", formattedDate, delimiter, xpath);
			BaseClass.logActionSuccess("Input today's date with fixed delimiters", "Date Input");

		} catch (Exception e) {
			BaseClass.logActionFailure("Input today's date with fixed delimiters", "Date Input", "Failed to input today's date: " + e.getMessage());
			throw new FrameworkException("Failed to input today's date into element: " + xpath, e);
		}
	}

	/**
	 * Fetches the date for next week (7 days from today), formats it, and inputs it
	 * digit-by-digit into a date input box that has fixed delimiters (e.g., '/') in place.
	 */
	public void inputDateNextWeekWithFixedDelimiters(String xpath, int timeoutSeconds, char delimiter, String dateFormat) {
		validateInput(xpath, "XPath");
		validateInput(dateFormat, "Date Format");

		try {
			BaseClass.logActionStart("Inputting next week's date with fixed delimiters '" + delimiter + "' using format '" + dateFormat + "'", "Date Input");

			WebElement inputElement = getWait(timeoutSeconds)
					.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

			LocalDate nextWeekDate = LocalDate.now().plusDays(7);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
			String formattedDate = nextWeekDate.format(formatter);

			inputElement.clear();  // Optional: clear existing values
			Actions actions = new Actions(driver);
			inputElement.click();

			String currentValue = inputElement.getAttribute("value");
			int dateIndex = 0;

			for (int i = 0; i < currentValue.length(); i++) {
				char currentChar = currentValue.charAt(i);
				if (currentChar == delimiter) {
					// Move cursor right to skip the delimiter
					actions.sendKeys(Keys.ARROW_RIGHT).perform();
				} else if (dateIndex < formattedDate.length()) {
					char digit = formattedDate.charAt(dateIndex++);
					actions.sendKeys(String.valueOf(digit)).perform();
				} else {
					break; // Finished typing all digits
				}
				// Optionally add delay here if needed for stability: Thread.sleep(50);
			}

			logger.info("Entered next week date [{}] with delimiter [{}] into element [{}]", 
					formattedDate, delimiter, xpath);
			BaseClass.logActionSuccess("Input next week's date with fixed delimiters", "Date Input");

		} catch (Exception e) {
			BaseClass.logActionFailure("Input next week's date with fixed delimiters", "Date Input", "Failed to input next week's date: " + e.getMessage());
			throw new FrameworkException("Failed to input next week date into element: " + xpath, e);
		}
	}
}
