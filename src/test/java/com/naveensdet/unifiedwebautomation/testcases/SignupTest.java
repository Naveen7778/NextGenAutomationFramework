package com.naveensdet.unifiedwebautomation.testcases;

import org.testng.annotations.Test;
import com.naveensdet.unifiedwebautomation.base.BaseClass;
import com.naveensdet.unifiedwebautomation.bpc.AuthBPC;
import com.naveensdet.unifiedwebautomation.keywords.Keywords;

/**
 * SignupTest - Simple, working test suite that matches your framework architecture
 */
public class SignupTest extends BaseClass {

	private AuthBPC authBPC;
	private Keywords keywords;

	/**
	 * Initialize components when needed - simple and reliable
	 */
	private void initializeComponents() {
		if (authBPC == null) {
			System.out.println("[DEBUG] Initializing test components...");
			keywords = new Keywords();
			authBPC = new AuthBPC(keywords);
			System.out.println("[DEBUG] Components initialized successfully");
		}
	}
	 

	@Test(priority = 1, description = "Test successful user registration with valid data", 
			groups = {"positive", "smoke", "registration", "critical","testValidUserRegistration"})
	public void testValidUserRegistration() {
		String testCaseName = "testValidUserRegistration";

		// Initialize components when first test runs
		initializeComponents();
		System.out.println("[DEBUG] Test starting - AuthBPC available: " + (authBPC != null));

		// Step 1: Register new user
		authBPC.registerUser("userName", "userEmail", "userPassword", true, testCaseName);

		// Step 2: Complete account information
		authBPC.completeAccountInformation(
				"userPassword", "dobDay", "dobMonth", "dobYear",
				"firstName", "lastName", "company", "address",
				"country", "state", "city", "zipcode", "mobile",
				"expectedSuccessMessage", true, testCaseName
				);

		// Step 3: Verify registration success
		authBPC.verifyRegistrationSuccess("expectedSuccessMessage", true, testCaseName);

		// Step 4: Verify user is logged in
		authBPC.verifyUserLoggedIn("expectedUsername", true, testCaseName);
	}

	@Test(priority = 2, description = "Test user login with valid credentials", 
			groups = {"positive", "smoke", "authentication", "critical"})
	public void testValidUserLogin() {
		String testCaseName = "testValidUserLogin";
		initializeComponents();

		// Step 1: Login with valid credentials
		authBPC.loginUser("userEmail", "userPassword", true, testCaseName);

		// Step 2: Verify successful login
		authBPC.verifyUserLoggedIn("expectedUsername", true, testCaseName);

		// Step 3: Take screenshot for evidence
		keywords.takeScreenshotWithTimestamp("login_success", testCaseName);
	}

	@Test(priority = 3, description = "Test user logout functionality", 
			groups = {"positive", "authentication", "functional"})
	public void testUserLogout() {
		String testCaseName = "testUserLogout";
		initializeComponents();

		// Step 1: Login first
		authBPC.loginUser("userEmail", "userPassword", true, testCaseName);
		authBPC.verifyUserLoggedIn("expectedUsername", true, testCaseName);

		// Step 2: Perform logout
		authBPC.logoutUser();

		// Step 3: Verify successful logout
		authBPC.verifyUserLoggedOut();

		// Step 4: Take screenshot for evidence
		keywords.takeScreenshotWithTimestamp("logout_success", testCaseName);
	}

	@Test(priority = 4, description = "Test complete user lifecycle - register, login, logout, delete", 
			groups = {"positive", "regression", "end-to-end", "critical"})
	public void testCompleteUserLifecycle() {
		String testCaseName = "testCompleteUserLifecycle";
		initializeComponents();

		// Step 1: Register new user
		authBPC.registerUser("userName", "userEmail", "userPassword", true, testCaseName);

		// Step 2: Complete account information
		authBPC.completeAccountInformation(
				"userPassword", "dobDay", "dobMonth", "dobYear",
				"firstName", "lastName", "company", "address",
				"country", "state", "city", "zipcode", "mobile",
				"expectedSuccessMessage", true, testCaseName
				);

		// Step 3: Verify registration and login
		authBPC.verifyRegistrationSuccess("expectedSuccessMessage", true, testCaseName);
		authBPC.verifyUserLoggedIn("expectedUsername", true, testCaseName);

		// Step 4: Delete account
		authBPC.deleteAccount("expectedDeletionMessage", true, testCaseName);

		// Step 5: Verify account deletion
		authBPC.verifyUserLoggedOut();
	}

	@Test(priority = 5, description = "Test registration with existing email address", 
			groups = {"negative", "validation", "registration", "functional"})
	public void testRegistrationWithExistingEmail() {
		String testCaseName = "testRegistrationWithExistingEmail";
		initializeComponents();

		// Step 1: Try to register with existing email
		authBPC.registerUser("existingUserName", "existingUserEmail", "userPassword", true, testCaseName);

		// Step 2: Verify error message for existing email
		authBPC.verifyEmailAlreadyExists();

		// Step 3: Take screenshot for evidence
		keywords.takeScreenshotWithTimestamp("existing_email_error", testCaseName);
	}

	@Test(priority = 6, description = "Test login with invalid credentials", 
			groups = {"negative", "security", "authentication", "functional"})
	public void testLoginWithInvalidCredentials() {
		String testCaseName = "testLoginWithInvalidCredentials";
		initializeComponents();

		// Step 1: Attempt login with invalid credentials
		authBPC.loginUser("invalidEmail", "invalidPassword", true, testCaseName);

		// Step 2: Verify login error message
		authBPC.verifyLoginError();

		// Step 3: Take screenshot for evidence
		keywords.takeScreenshotWithTimestamp("invalid_login_error", testCaseName);
	}

	@Test(priority = 7, description = "Test login with empty credentials", 
			groups = {"negative", "validation", "authentication", "boundary"})
	public void testLoginWithEmptyCredentials() {
		String testCaseName = "testLoginWithEmptyCredentials";
		initializeComponents();

		// Step 1: Attempt login with empty credentials
		authBPC.loginUser("", "", false, testCaseName);

		// Step 2: Verify login error message
		authBPC.verifyLoginError();

		// Step 3: Take screenshot for evidence
		keywords.takeScreenshotWithTimestamp("empty_credentials_error", testCaseName);
	}

	@Test(priority = 8, description = "Test registration with minimum required fields", 
			groups = {"boundary", "validation", "registration", "data-driven"})
	public void testRegistrationWithMinimumFields() {
		String testCaseName = "testRegistrationWithMinimumFields";
		initializeComponents();

		// Step 1: Register with minimum required fields
		authBPC.registerUser("minUserName", "minUserEmail", "minPassword", true, testCaseName);

		// Step 2: Complete with minimum account information
		authBPC.completeAccountInformation(
				"minPassword", "1", "January", "1990",
				"MinFirst", "MinLast", "", "Basic Address",
				"United States", "CA", "TestCity", "12345", "1234567890",
				"expectedSuccessMessage", true, testCaseName
				);

		// Step 3: Verify successful registration
		authBPC.verifyRegistrationSuccess("expectedSuccessMessage", true, testCaseName);
	}

	@Test(priority = 9, description = "Test registration with maximum field lengths", 
			groups = {"boundary", "validation", "registration", "data-driven"})
	public void testRegistrationWithMaximumFields() {
		String testCaseName = "testRegistrationWithMaximumFields";
		initializeComponents();

		// Step 1: Register with maximum length data
		authBPC.registerUser("maxUserName", "maxUserEmail", "maxPassword", true, testCaseName);

		// Step 2: Complete with maximum length account information
		authBPC.completeAccountInformation(
				"maxPassword", "31", "December", "1950",
				"maxFirstName", "maxLastName", "maxCompanyName", "maxAddressLine",
				"maxCountry", "maxState", "maxCity", "maxZipcode", "maxMobile",
				"expectedSuccessMessage", true, testCaseName
				);

		// Step 3: Verify successful registration
		authBPC.verifyRegistrationSuccess("expectedSuccessMessage", true, testCaseName);
	}

	@Test(priority = 10, description = "Test user registration in different browser configurations", 
			groups = {"cross-browser", "compatibility", "registration", "regression"})
	public void testRegistrationCrossBrowser() {
		String testCaseName = "testRegistrationCrossBrowser";
		initializeComponents();

		// Step 1: Register user for cross-browser testing
		authBPC.registerUser("crossBrowserUser", "crossBrowserEmail", "crossBrowserPassword", true, testCaseName);

		// Step 2: Complete account information
		authBPC.completeAccountInformation(
				"crossBrowserPassword", "15", "June", "1985",
				"CrossFirst", "CrossLast", "CrossCompany", "Cross Address",
				"Canada", "Ontario", "Toronto", "M5V3A8", "9876543210",
				"expectedSuccessMessage", true, testCaseName
				);

		// Step 3: Verify functionality across browser configurations
		authBPC.verifyRegistrationSuccess("expectedSuccessMessage", true, testCaseName);
		authBPC.verifyUserLoggedIn("expectedUsername", true, testCaseName);
	}
}
