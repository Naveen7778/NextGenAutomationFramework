package com.naveensdet.unifiedwebautomation.bpc;

import com.naveensdet.unifiedwebautomation.keywords.Keywords;
import com.naveensdet.unifiedwebautomation.pageobjects.ORAuthPage;

/**
 * AuthBPC - Authentication Business Process Component
 * Uses ORAuthPage constants with inline descriptive comments
 */
public class AuthBPC {

	private Keywords keywords;

	public AuthBPC(Keywords keywords) {
		this.keywords = keywords;
	}

	// ================================
	// USER REGISTRATION METHODS (Updated with OR)
	// ================================

	public void registerUser(String name, String email, String password, boolean excelData, String testCaseName) {
		// Clicks on the Signup/Login button to navigate to registration page
		keywords.clickElement(ORAuthPage.OR_SignUp_Button, ORAuthPage.S_SignUp_Button, 10);
		// Waits until the signup header becomes visible on the page
		keywords.waitForElementVisible(ORAuthPage.OR_SignUp_Header, ORAuthPage.S_SignUp_Header, 10);
		// Enters the user's name into the signup name input field
		keywords.enterText(ORAuthPage.OR_SignUp_Name_Field, name, ORAuthPage.S_SignUp_Name_Field, excelData, testCaseName);
		// Enters the user's email into the signup email input field
		keywords.enterText(ORAuthPage.OR_SignUp_Email_Field, email, ORAuthPage.S_SignUp_Email_Field, excelData, testCaseName);
		// Clicks the signup submit button to proceed with registration
		keywords.clickElement(ORAuthPage.OR_SignUp_Submit_Button, ORAuthPage.S_SignUp_Submit_Button, 10);
		// Waits for the account information form to become visible
		keywords.waitForElementVisible(ORAuthPage.OR_Account_Info_Header, ORAuthPage.S_Account_Info_Header, 10);
	}

	// ================================
	// ENHANCED ACCOUNT INFORMATION COMPLETION METHOD
	// ================================

	public void completeAccountInformation(String password, String day, String month, String year, String firstName, String lastName, 
			String company, String address, String country, 
			String state, String city, String zipcode, String mobile, String textContains,
			boolean excelData, String testCaseName) {
		// Clicks on the 'Mr' gender radio button selection
		keywords.clickElement(ORAuthPage.OR_Gender_Mr, ORAuthPage.S_Gender_Mr, 5);
		// Enters the user's password into the password field
		keywords.enterText(ORAuthPage.OR_Password_Field, password, ORAuthPage.S_Password_Field, excelData, testCaseName);
		// Selects day '15' from the date of birth dropdown by value
		keywords.selectByValue(ORAuthPage.OR_Day_Dropdown, day, ORAuthPage.S_Day_Dropdown, false, testCaseName);
		// Selects 'January' from the month dropdown by visible text
		keywords.selectByVisibleText(ORAuthPage.OR_Month_Dropdown, month, ORAuthPage.S_Month_Dropdown, false, testCaseName);
		// Selects year '1990' from the year dropdown by value
		keywords.selectByValue(ORAuthPage.OR_Year_Dropdown, year, ORAuthPage.S_Year_Dropdown, false, "");
		// Enters the user's first name into the first name field
		keywords.enterText(ORAuthPage.OR_First_Name_Field, firstName, ORAuthPage.S_First_Name_Field, excelData, testCaseName);
		// Enters the user's last name into the last name field
		keywords.enterText(ORAuthPage.OR_Last_Name_Field, lastName, ORAuthPage.S_Last_Name_Field, excelData, testCaseName);
		// Enters the user's company name into the company field
		keywords.enterText(ORAuthPage.OR_Company_Field, company, ORAuthPage.S_Company_Field, excelData, testCaseName);
		// Enters the user's address into the address field
		keywords.enterText(ORAuthPage.OR_Address_Field, address, ORAuthPage.S_Address_Field, excelData, testCaseName);
		// Selects the user's country from dropdown by visible text
		keywords.selectByVisibleText(ORAuthPage.OR_Country_Dropdown, country, ORAuthPage.S_Country_Dropdown, excelData, testCaseName);
		// Enters the user's state into the state field
		keywords.enterText(ORAuthPage.OR_State_Field, state, ORAuthPage.S_State_Field, excelData, testCaseName);
		// Enters the user's city into the city field
		keywords.enterText(ORAuthPage.OR_City_Field, city, ORAuthPage.S_City_Field, excelData, testCaseName);
		// Enters the user's zipcode into the zipcode field
		keywords.enterText(ORAuthPage.OR_Zipcode_Field, zipcode, ORAuthPage.S_Zipcode_Field, excelData, testCaseName);
		// Enters the user's mobile number into the mobile field
		keywords.enterText(ORAuthPage.OR_Mobile_Field, mobile, ORAuthPage.S_Mobile_Field, excelData, testCaseName);
		// Clicks the 'Create Account' button to complete registration
		keywords.clickElement(ORAuthPage.OR_Create_Account_Button, ORAuthPage.S_Create_Account_Button, 10);
		// Verifies that the account creation success message is displayed
		keywords.verifyTextContains(ORAuthPage.OR_Account_Created_Success, textContains, ORAuthPage.S_Account_Created_Success, 10, excelData, testCaseName);
		// Clicks the 'Continue' button to proceed after account creation
		keywords.clickElement(ORAuthPage.OR_Continue_Button, ORAuthPage.S_Continue_Button, 10);
	}

	// ================================
	// ENHANCED USER LOGIN METHOD
	// ================================

	public void loginUser(String email, String password, boolean excelData, String testCaseName) {
		// Clicks on the Signup/Login button to access login page
		keywords.clickElement(ORAuthPage.OR_SignUp_Button, ORAuthPage.S_SignUp_Button, 10);
		// Waits until the login header becomes visible on the page
		keywords.waitForElementVisible(ORAuthPage.OR_Login_Header, ORAuthPage.S_Login_Header, 10);
		// Enters the user's email into the login email field
		keywords.enterText(ORAuthPage.OR_Login_Email_Field, email, ORAuthPage.S_Login_Email_Field, excelData, testCaseName);
		// Enters the user's password into the login password field
		keywords.enterText(ORAuthPage.OR_Login_Password_Field, password, ORAuthPage.S_Login_Password_Field, excelData, testCaseName);
		// Clicks the login submit button to authenticate user
		keywords.clickElement(ORAuthPage.OR_Login_Submit_Button, ORAuthPage.S_Login_Submit_Button, 10);
		// Waits for the logged-in indicator to confirm successful login
		keywords.waitForElementVisible(ORAuthPage.OR_Logged_In_Indicator, ORAuthPage.S_Logged_In_Indicator, 10);
	}

	// ================================
	// ENHANCED USER LOGOUT METHOD
	// ================================

	public void logoutUser() {
		// Clicks the logout button to end user session
		keywords.clickElement(ORAuthPage.OR_Logout_Button, ORAuthPage.S_Logout_Button, 10);
		// Waits for login page to appear confirming successful logout
		keywords.waitForElementVisible(ORAuthPage.OR_Login_Header, ORAuthPage.S_Login_Header, 10);
	}

	// ================================
	// ENHANCED VERIFICATION METHODS
	// ================================

	public void verifyUserLoggedIn(String expectedUsername, boolean excelData, String testCaseName) {
		// Verifies that the logged-in indicator element is visible
		keywords.verifyElementVisible(ORAuthPage.OR_Logged_In_Indicator, ORAuthPage.S_Logged_In_Indicator, 10);
		// Verifies that the logged-in indicator contains the expected username
		keywords.verifyTextContains(ORAuthPage.OR_Logged_In_Indicator, expectedUsername, ORAuthPage.S_Logged_In_Indicator, 10, excelData, testCaseName);
	}

	public void verifyRegistrationSuccess(String expectedUsername, boolean excelData, String testCaseName) {
		// Verifies that the account creation success message is displayed
		keywords.verifyTextContains(ORAuthPage.OR_Account_Created_Success, expectedUsername, ORAuthPage.S_Account_Created_Success, 10, excelData, testCaseName);
		// Captures a full page screenshot for registration success evidence using utility
		keywords.takeScreenshotWithTimestamp("registration_success", testCaseName);
	}

	public void verifyUserLoggedOut() {
		// Verifies that the login page header is visible after logout
		keywords.verifyElementVisible(ORAuthPage.OR_Login_Header, ORAuthPage.S_Login_Header, 10);
		// Verifies that the logged-in indicator is not visible anymore
		keywords.verifyElementNotVisible(ORAuthPage.OR_Logged_In_Indicator, ORAuthPage.S_Logged_In_Indicator, 5);
	}

	public void verifyLoginError() {
		// Verifies that the login error message is visible on the page
		keywords.verifyElementVisible(ORAuthPage.OR_Login_Error_Message, ORAuthPage.S_Login_Error_Message, 10);
	}

	public void verifyEmailAlreadyExists() {
		// Verifies that the email already exists error message is displayed
		keywords.verifyElementVisible(ORAuthPage.OR_Email_Exists_Error, ORAuthPage.S_Email_Exists_Error, 10);
	}

	public void deleteAccount(String expectedUsername, boolean excelData, String testCaseName) {
		// Clicks the delete account button to remove user account
		keywords.clickElement(ORAuthPage.OR_Delete_Account_Button, ORAuthPage.S_Delete_Account_Button, 10);
		// Verifies that the account deletion success message is displayed
		keywords.verifyTextContains(ORAuthPage.OR_Account_Deleted_Success, expectedUsername, ORAuthPage.S_Account_Deleted_Success, 10, excelData, testCaseName);
		// Captures a full page screenshot for account deletion evidence using utility
		keywords.takeScreenshotWithTimestamp("account_deleted", testCaseName);
		// Clicks the continue button to proceed after account deletion
		keywords.clickElement(ORAuthPage.OR_Continue_Button, ORAuthPage.S_Continue_Button, 10);
	}

}
