package com.naveensdet.unifiedwebautomation.utils;

public class TestContext {
    private static ThreadLocal<String> currentTestCase = new ThreadLocal<>();

    public static void setCurrentTestCaseName(String testCaseName) {
        currentTestCase.set(testCaseName);
    }

    public static String getCurrentTestCaseName() {
        String testName = currentTestCase.get();
        if (testName == null) {
            throw new RuntimeException("Current test case name is not set in TestContext.");
        }
        return testName;
    }

    public static void clear() {
        currentTestCase.remove();
    }
}
