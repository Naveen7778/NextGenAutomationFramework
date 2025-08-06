package com.naveensdet.unifiedwebautomation.listeners;

import com.naveensdet.unifiedwebautomation.utils.ExtentReportManager;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Main test execution listener for comprehensive test monitoring
 * Handles test lifecycle events and coordinates with other listeners
 */
public class TestExecutionListener implements ITestListener {
    
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestExecutionListener.class);
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.info("🚀 TEST STARTED: {} in class {}", testName, className);
        
        // Create ExtentReport test
        ExtentReportManager.createTest(testName, getTestDescription(result));
        ExtentReportManager.logInfo("Test execution started: " + testName);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("✅ TEST PASSED: {} (Duration: {}ms)", testName, duration);
        ExtentReportManager.logPass("Test completed successfully");
        ExtentReportManager.logInfo("Execution time: " + duration + " ms");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String errorMessage = result.getThrowable().getMessage();
        
        logger.error("❌ TEST FAILED: {} - Error: {}", testName, errorMessage);
        ExtentReportManager.logFail("Test failed: " + errorMessage);
        
        // Delegate screenshot capture to specialized listener
        // ScreenshotListener will handle this automatically
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String skipReason = result.getThrowable() != null ? 
            result.getThrowable().getMessage() : "Dependency failure";
            
        logger.warn("⏭️ TEST SKIPPED: {} - Reason: {}", testName, skipReason);
        ExtentReportManager.createTest(testName, "Skipped test");
        ExtentReportManager.logSkip("Test skipped: " + skipReason);
    }
    
    private String getTestDescription(ITestResult result) {
        String description = result.getMethod().getDescription();
        return description != null && !description.isEmpty() ? 
            description : "No description available";
    }
}
