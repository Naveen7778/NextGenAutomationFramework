package com.naveensdet.unifiedwebautomation.keywords;

import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.utils.DriverManager;
import com.naveensdet.unifiedwebautomation.utils.ExcelUtilities;
import com.naveensdet.unifiedwebautomation.utils.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SelectKeywords {

    private WebDriver driver;
    private static final Logger logger = LoggerFactory.getLogger(SelectKeywords.class);

    public SelectKeywords() {
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

    @SuppressWarnings("unused")
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

    /**
     * Helper method to get actual input value either from Excel or direct value.
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
     * Waits until the element located by the xpath is visible and returns the WebElement.
     */
    private WebElement findElementWithWait(String xpath) {
        validateInput(xpath, "Select element");
        int timeoutSeconds = getIntConfigProperty("fluentWaitTimeout", 10);
        int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.pollingEvery(Duration.ofMillis(pollingMillis));
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(parseLocator(xpath)));
        } catch (Exception e) {
            throw new FrameworkException("Element not visible after waiting for " + timeoutSeconds + " seconds. XPath: " + xpath, e);
        }
    }

    // ====== Select Option Methods ======

    public void selectByVisibleText(String xpath, String visibleTextOrKey, String elementName, boolean excelData, String testName) {
        validateInput(xpath, elementName);
        String visibleText = getInputValue(testName, visibleTextOrKey, elementName, excelData);
        
        try {
            BaseClass.logActionStart("Selecting option by visible text '" + BaseClass.mask(visibleText) + "' in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            select.selectByVisibleText(visibleText);
            logger.info("Selected option with visible text '{}' in [{}]", visibleText, elementName);
            
            BaseClass.logActionSuccess("Selected option by visible text", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to select option with visible text '{}' in [{}]", visibleText, elementName, e);
            BaseClass.logActionFailure("Select by visible text", elementName, "Failed to select option '" + visibleText + "': " + e.getMessage());
            throw new FrameworkException("Failed to select option with visible text [" + visibleText + "] in [" + elementName + "]", e);
        }
    }

    public void selectByValue(String xpath, String valueOrKey, String elementName, boolean excelData, String testName) {
        validateInput(xpath, elementName);
        String value = getInputValue(testName, valueOrKey, elementName, excelData);
        
        try {
            BaseClass.logActionStart("Selecting option by value '" + BaseClass.mask(value) + "' in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            select.selectByValue(value);
            logger.info("Selected option with value '{}' in [{}]", value, elementName);
            
            BaseClass.logActionSuccess("Selected option by value", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to select option with value '{}' in [{}]", value, elementName, e);
            BaseClass.logActionFailure("Select by value", elementName, "Failed to select option with value '" + value + "': " + e.getMessage());
            throw new FrameworkException("Failed to select option with value [" + value + "] in [" + elementName + "]", e);
        }
    }

    public void selectByIndex(String xpath, int index, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Selecting option at index " + index + " in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            select.selectByIndex(index);
            logger.info("Selected option at index {} in [{}]", index, elementName);
            
            BaseClass.logActionSuccess("Selected option at index " + index, elementName);
            
        } catch (Exception e) {
            logger.error("Failed to select option at index {} in [{}]", index, elementName, e);
            BaseClass.logActionFailure("Select by index", elementName, "Failed to select option at index " + index + ": " + e.getMessage());
            throw new FrameworkException("Failed to select option at index " + index + " in [" + elementName + "]", e);
        }
    }

    // ====== Deselect Methods (For Multi-select) ======

    public void deselectByVisibleText(String xpath, String visibleTextOrKey, String elementName, boolean excelData, String testName) {
        validateInput(xpath, elementName);
        String visibleText = getInputValue(testName, visibleTextOrKey, elementName, excelData);
        
        try {
            BaseClass.logActionStart("Deselecting option by visible text '" + BaseClass.mask(visibleText) + "' in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            select.deselectByVisibleText(visibleText);
            logger.info("Deselected option with visible text '{}' in [{}]", visibleText, elementName);
            
            BaseClass.logActionSuccess("Deselected option by visible text", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to deselect option with visible text '{}' in [{}]", visibleText, elementName, e);
            BaseClass.logActionFailure("Deselect by visible text", elementName, "Failed to deselect option '" + visibleText + "': " + e.getMessage());
            throw new FrameworkException("Failed to deselect option with visible text [" + visibleText + "] in [" + elementName + "]", e);
        }
    }

    public void deselectByValue(String xpath, String valueOrKey, String elementName, boolean excelData, String testName) {
        validateInput(xpath, elementName);
        String value = getInputValue(testName, valueOrKey, elementName, excelData);
        
        try {
            BaseClass.logActionStart("Deselecting option by value '" + BaseClass.mask(value) + "' in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            select.deselectByValue(value);
            logger.info("Deselected option with value '{}' in [{}]", value, elementName);
            
            BaseClass.logActionSuccess("Deselected option by value", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to deselect option with value '{}' in [{}]", value, elementName, e);
            BaseClass.logActionFailure("Deselect by value", elementName, "Failed to deselect option with value '" + value + "': " + e.getMessage());
            throw new FrameworkException("Failed to deselect option with value [" + value + "] in [" + elementName + "]", e);
        }
    }

    public void deselectByIndex(String xpath, int index, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Deselecting option at index " + index + " in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            select.deselectByIndex(index);
            logger.info("Deselected option at index {} in [{}]", index, elementName);
            
            BaseClass.logActionSuccess("Deselected option at index " + index, elementName);
            
        } catch (Exception e) {
            logger.error("Failed to deselect option at index {} in [{}]", index, elementName, e);
            BaseClass.logActionFailure("Deselect by index", elementName, "Failed to deselect option at index " + index + ": " + e.getMessage());
            throw new FrameworkException("Failed to deselect option at index " + index + " in [" + elementName + "]", e);
        }
    }

    public void deselectAll(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Deselecting all options in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            select.deselectAll();
            logger.info("Deselected all options in [{}]", elementName);
            
            BaseClass.logActionSuccess("Deselected all options", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to deselect all options in [{}]", elementName, e);
            BaseClass.logActionFailure("Deselect all options", elementName, "Failed to deselect all options: " + e.getMessage());
            throw new FrameworkException("Failed to deselect all options in [" + elementName + "]", e);
        }
    }

    // ====== Get Selected Options ======

    public String getSelectedOptionText(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Getting selected option text from " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            String selectedText = select.getFirstSelectedOption().getText();
            logger.info("First selected option text in [{}] is '{}'", elementName, selectedText);
            
            BaseClass.logActionSuccess("Retrieved selected option text: '" + BaseClass.mask(selectedText) + "'", elementName);
            return selectedText;
            
        } catch (Exception e) {
            logger.error("Failed to get selected option text from [{}]", elementName, e);
            BaseClass.logActionFailure("Get selected option text", elementName, "Failed to get selected option text: " + e.getMessage());
            throw new FrameworkException("Failed to get selected option text from [" + elementName + "]", e);
        }
    }

    public List<String> getAllSelectedOptionsText(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Getting all selected options text from " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            List<WebElement> selectedOptions = select.getAllSelectedOptions();
            List<String> texts = new ArrayList<>();
            for (WebElement option : selectedOptions) {
                texts.add(option.getText());
            }
            logger.info("All selected option texts in [{}]: {}", elementName, texts);
            
            BaseClass.logActionSuccess("Retrieved " + texts.size() + " selected options", elementName);
            return texts;
            
        } catch (Exception e) {
            logger.error("Failed to get all selected options text from [{}]", elementName, e);
            BaseClass.logActionFailure("Get all selected options text", elementName, "Failed to get all selected options: " + e.getMessage());
            throw new FrameworkException("Failed to get all selected options text from [" + elementName + "]", e);
        }
    }

    // ====== Utility Methods ======

    /**
     * Checks if the <select> element supports multiple selections.
     */
    public boolean isMultiple(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Checking if " + elementName + " supports multiple selections", "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            boolean multiple = select.isMultiple();
            logger.info("Select element [{}] isMultiple = {}", elementName, multiple);
            
            BaseClass.logActionSuccess("Multiple selection support: " + multiple, elementName);
            return multiple;
            
        } catch (Exception e) {
            logger.error("Failed to determine if select element [{}] is multiple", elementName, e);
            BaseClass.logActionFailure("Check if multiple", elementName, "Failed to check multiple selection support: " + e.getMessage());
            throw new FrameworkException("Failed to determine if select element [" + elementName + "] is multiple", e);
        }
    }

    /**
     * Selects first option containing partial visible text
     */
    public void selectOptionContainsText(String xpath, String partialTextOrKey, String elementName, boolean excelData, String testName) {
        validateInput(xpath, elementName);
        String partialText = getInputValue(testName, partialTextOrKey, elementName, excelData);
        
        try {
            BaseClass.logActionStart("Selecting option containing text '" + BaseClass.mask(partialText) + "' in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            boolean found = false;
            for (WebElement option : select.getOptions()) {
                if (option.getText().contains(partialText)) {
                    select.selectByVisibleText(option.getText());
                    logger.info("Selected option containing '{}' in [{}]", partialText, elementName);
                    found = true;
                    break;
                }
            }
            if (!found) {
                BaseClass.logActionFailure("Select option contains text", elementName, "No option containing '" + partialText + "' found");
                throw new FrameworkException("No option containing '" + partialText + "' found in [" + elementName + "]");
            }
            
            BaseClass.logActionSuccess("Selected option containing text '" + BaseClass.mask(partialText) + "'", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to select option containing '{}' in [{}]", partialText, elementName, e);
            BaseClass.logActionFailure("Select option contains text", elementName, "Failed to select option containing '" + partialText + "': " + e.getMessage());
            throw new FrameworkException("Failed to select option containing '" + partialText + "' in [" + elementName + "]", e);
        }
    }

    /**
     * Verify the selected option matches expected value (supports Excel key or direct value)
     */
    public void verifySelectedOption(String xpath, String expectedValueOrKey, String elementName, boolean excelData, String testName) {
        validateInput(xpath, elementName);
        String expectedValue = getInputValue(testName, expectedValueOrKey, elementName, excelData);
        
        try {
            BaseClass.logActionStart("Verifying selected option equals '" + BaseClass.mask(expectedValue) + "' in " + elementName, "Select Verification");
            
            String actual = getSelectedOptionText(xpath, elementName);
            if (!expectedValue.equals(actual)) {
                BaseClass.logActionFailure("Verify selected option", elementName, "Expected '" + expectedValue + "' but found '" + actual + "'");
                throw new FrameworkException("Selected option in [" + elementName + "] expected to be '" + expectedValue + "' but was '" + actual + "'");
            }
            logger.info("Verified selected option in [{}] equals '{}'", elementName, expectedValue);
            
            BaseClass.logActionSuccess("Selected option verification passed", elementName);
            
        } catch (Exception e) {
            logger.error("Verification failed for selected option in [{}]", elementName, e);
            BaseClass.logActionFailure("Verify selected option", elementName, e.getMessage());
            throw e;
        }
    }

    /**
     * Verify that an option with the exact visible text exists.
     */
    public void verifyOptionExists(String xpath, String optionTextOrKey, String elementName, boolean excelData, String testName) {
        validateInput(xpath, elementName);
        String optionText = getInputValue(testName, optionTextOrKey, elementName, excelData);
        
        try {
            BaseClass.logActionStart("Verifying option '" + BaseClass.mask(optionText) + "' exists in " + elementName, "Select Verification");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            boolean found = select.getOptions().stream().anyMatch(o -> optionText.equals(o.getText()));
            if (!found) {
                BaseClass.logActionFailure("Verify option exists", elementName, "Option '" + optionText + "' not found");
                throw new FrameworkException("Option '" + optionText + "' not found in [" + elementName + "]");
            }
            logger.info("Verified option '{}' exists in [{}]", optionText, elementName);
            
            BaseClass.logActionSuccess("Option existence verification passed", elementName);
            
        } catch (Exception e) {
            logger.error("Verification failed for option '{}' in [{}]", optionText, elementName, e);
            BaseClass.logActionFailure("Verify option exists", elementName, "Failed to verify option existence: " + e.getMessage());
            throw new FrameworkException("Verification failed for option '" + optionText + "' in [" + elementName + "]", e);
        }
    }

    /**
     * Selects the last option in the dropdown list.
     */
    public void selectLastOption(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Selecting last option in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            List<WebElement> options = select.getOptions();
            if (options.isEmpty()) {
                BaseClass.logActionFailure("Select last option", elementName, "No options available to select");
                throw new FrameworkException("No options available to select in [" + elementName + "]");
            }
            select.selectByIndex(options.size() - 1);
            logger.info("Selected last option '{}' in [{}]", options.get(options.size() - 1).getText(), elementName);
            
            BaseClass.logActionSuccess("Selected last option", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to select last option in [{}]", elementName, e);
            BaseClass.logActionFailure("Select last option", elementName, "Failed to select last option: " + e.getMessage());
            throw new FrameworkException("Failed to select last option in [" + elementName + "]", e);
        }
    }

    /**
     * Returns the number of options in the select dropdown.
     */
    public int getOptionsCount(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Getting options count from " + elementName, "Select Information");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            int count = select.getOptions().size();
            logger.info("Options count for [{}] is {}", elementName, count);
            
            BaseClass.logActionSuccess("Options count: " + count, elementName);
            return count;
            
        } catch (Exception e) {
            logger.error("Failed to get options count from [{}]", elementName, e);
            BaseClass.logActionFailure("Get options count", elementName, "Failed to get options count: " + e.getMessage());
            throw new FrameworkException("Failed to get options count from [" + elementName + "]", e);
        }
    }

    /**
     * Selects all options in a multi-select dropdown.
     */
    public void selectAllOptions(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Selecting all options in " + elementName, "Select Dropdown");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            if (!select.isMultiple()) {
                BaseClass.logActionFailure("Select all options", elementName, "Element is not a multi-select dropdown");
                throw new FrameworkException("Cannot select all: [" + elementName + "] is not a multi-select");
            }
            int optionCount = 0;
            for (WebElement option : select.getOptions()) {
                select.selectByVisibleText(option.getText());
                optionCount++;
            }
            logger.info("Selected all options in [{}]", elementName);
            
            BaseClass.logActionSuccess("Selected all " + optionCount + " options", elementName);
            
        } catch (Exception e) {
            logger.error("Failed to select all options in [{}]", elementName, e);
            BaseClass.logActionFailure("Select all options", elementName, "Failed to select all options: " + e.getMessage());
            throw new FrameworkException("Failed to select all options in [" + elementName + "]", e);
        }
    }

    /**
     * Verifies that no options are selected in the multi-select dropdown.
     */
    public void verifyNoSelection(String xpath, String elementName) {
        validateInput(xpath, elementName);
        
        try {
            BaseClass.logActionStart("Verifying no options are selected in " + elementName, "Select Verification");
            
            WebElement element = findElementWithWait(xpath);
            Select select = new Select(element);
            if (!select.getAllSelectedOptions().isEmpty()) {
                BaseClass.logActionFailure("Verify no selection", elementName, "Some options are selected when none expected");
                throw new FrameworkException("Expected no selection in [" + elementName + "], but some options are selected.");
            }
            logger.info("Verified that no options are selected in [{}]", elementName);
            
            BaseClass.logActionSuccess("No selection verification passed", elementName);
            
        } catch (Exception e) {
            logger.error("Verification failed for no selection in [{}]", elementName, e);
            BaseClass.logActionFailure("Verify no selection", elementName, "Failed to verify no selection: " + e.getMessage());
            throw new FrameworkException("Verification failed for no selection in [" + elementName + "]", e);
        }
    }

    /**
     * Clear all selections (synonym for deselectAll) to be explicit.
     */
    public void clearAllSelections(String xpath, String elementName) {
        try {
            BaseClass.logActionStart("Clearing all selections in " + elementName, "Select Dropdown");
            deselectAll(xpath, elementName);
            BaseClass.logActionSuccess("Cleared all selections", elementName);
        } catch (Exception e) {
            BaseClass.logActionFailure("Clear all selections", elementName, e.getMessage());
            throw e;
        }
    }

    /**
     * Waits for the dropdown options to be populated (count > 0).
     */
    public void waitForOptionsToLoad(String xpath, String elementName, int timeoutSeconds) {
        validateInput(xpath, elementName);
        int pollingMillis = getIntConfigProperty("fluentWaitPolling", 500);
        
        try {
            BaseClass.logActionStart("Waiting for options to load in " + elementName + " (timeout: " + timeoutSeconds + "s)", "Select Wait");
            
            FluentWait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeoutSeconds))
                    .pollingEvery(Duration.ofMillis(pollingMillis))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class);

            boolean loaded = wait.until(drv -> {
                WebElement element = drv.findElement(parseLocator(xpath));
                Select select = new Select(element);
                return select.getOptions().size() > 0;
            });
            
            if (loaded) {
                logger.info("Options loaded for [{}] within {} seconds", elementName, timeoutSeconds);
                BaseClass.logActionSuccess("Options loaded successfully", elementName);
            } else {
                BaseClass.logActionFailure("Wait for options to load", elementName, "Options did not load within timeout");
                throw new FrameworkException("Options did not load for [" + elementName + "] within timeout");
            }
            
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for options to load in [{}]", elementName, e);
            BaseClass.logActionFailure("Wait for options to load", elementName, "Timeout waiting for options to load: " + e.getMessage());
            throw new FrameworkException("Timeout waiting for options to load in [" + elementName + "]", e);
        } catch (Exception e) {
            BaseClass.logActionFailure("Wait for options to load", elementName, "Failed to wait for options: " + e.getMessage());
            throw new FrameworkException("Failed to wait for options to load", e);
        }
    }
}
