package com.naveensdet.unifiedwebautomation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retry listener for handling flaky tests
 * Automatically retries failed tests up to configured limit
 */
public class RetryListener implements IRetryAnalyzer {
    
	private static final Logger logger = LoggerFactory.getLogger(RetryListener.class);
    private int currentAttempt = 0;
    private final int maxRetryCount;
    
    public RetryListener() {
        // Get retry count from system property or use default
        this.maxRetryCount = Integer.parseInt(System.getProperty("retry.count", "2"));
    }
    
    @Override
    public boolean retry(ITestResult result) {
        if (currentAttempt < maxRetryCount) {
            currentAttempt++;
            String testName = result.getMethod().getMethodName();
            
            logger.warn("🔄 RETRYING TEST: {} (Attempt {}/{})", 
                       testName, currentAttempt, maxRetryCount);
            
            return true;
        }
        return false;
    }
}
