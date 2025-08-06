package com.naveensdet.unifiedwebautomation.pageobjects;

/**
 * ORAuthPage - Object Repository for Authentication Page
 * Contains all locators and element descriptions for authentication functionality
 */
public class ORAuthPage {
    
    // ================================
    // SIGNUP/LOGIN PAGE ELEMENTS
    // ================================
    
    // Signup/Login Button
    public static final String OR_SignUp_Button = "//a[contains(text(),'Signup / Login')]";
    public static final String S_SignUp_Button = "SignUp Button";
    
    // Signup Section Elements
    public static final String OR_SignUp_Header = "//h2[text()='New User Signup!']";
    public static final String S_SignUp_Header = "Signup Header";
    
    public static final String OR_SignUp_Name_Field = "//input[@name='name']";
    public static final String S_SignUp_Name_Field = "Name Field";
    
    public static final String OR_SignUp_Email_Field = "//input[@data-qa='signup-email']";
    public static final String S_SignUp_Email_Field = "Email Field";
    
    public static final String OR_SignUp_Submit_Button = "//button[@data-qa='signup-button']";
    public static final String S_SignUp_Submit_Button = "Signup Submit Button";
    
    // Login Section Elements
    public static final String OR_Login_Header = "//h2[text()='Login to your account']";
    public static final String S_Login_Header = "Login Header";
    
    public static final String OR_Login_Email_Field = "//input[@data-qa='login-email']";
    public static final String S_Login_Email_Field = "Login Email Field";
    
    public static final String OR_Login_Password_Field = "//input[@data-qa='login-password']";
    public static final String S_Login_Password_Field = "Login Password Field";
    
    public static final String OR_Login_Submit_Button = "//button[@data-qa='login-button']";
    public static final String S_Login_Submit_Button = "Login Submit Button";
    
    // Account Information Page Elements
    public static final String OR_Account_Info_Header = "//b[contains(text(),'Enter Account Information')]";
    public static final String S_Account_Info_Header = "Account Info Header";
    
    public static final String OR_Gender_Mr = "//input[@id='id_gender1']";
    public static final String S_Gender_Mr = "Title Mr";
    
    public static final String OR_Password_Field = "//input[@id='password']";
    public static final String S_Password_Field = "Password Field";
    
    public static final String OR_Day_Dropdown = "//select[@id='days']";
    public static final String S_Day_Dropdown = "Day Dropdown";
    
    public static final String OR_Month_Dropdown = "//select[@id='months']";
    public static final String S_Month_Dropdown = "Month Dropdown";
    
    public static final String OR_Year_Dropdown = "//select[@id='years']";
    public static final String S_Year_Dropdown = "Year Dropdown";
    
    public static final String OR_First_Name_Field = "//input[@id='first_name']";
    public static final String S_First_Name_Field = "First Name Field";
    
    public static final String OR_Last_Name_Field = "//input[@id='last_name']";
    public static final String S_Last_Name_Field = "Last Name Field";
    
    public static final String OR_Company_Field = "//input[@id='company']";
    public static final String S_Company_Field = "Company Field";
    
    public static final String OR_Address_Field = "//input[@id='address1']";
    public static final String S_Address_Field = "Address Field";
    
    public static final String OR_Country_Dropdown = "//select[@id='country']";
    public static final String S_Country_Dropdown = "Country Dropdown";
    
    public static final String OR_State_Field = "//input[@id='state']";
    public static final String S_State_Field = "State Field";
    
    public static final String OR_City_Field = "//input[@id='city']";
    public static final String S_City_Field = "City Field";
    
    public static final String OR_Zipcode_Field = "//input[@id='zipcode']";
    public static final String S_Zipcode_Field = "Zipcode Field";
    
    public static final String OR_Mobile_Field = "//input[@id='mobile_number']";
    public static final String S_Mobile_Field = "Mobile Number Field";
    
    public static final String OR_Create_Account_Button = "//button[@data-qa='create-account']";
    public static final String S_Create_Account_Button = "Create Account Button";
    
    // Success/Error Messages
    public static final String OR_Account_Created_Success = "//b[contains(text(),'Account Created!')]";
    public static final String S_Account_Created_Success = "Account Created Success Message";
    
    public static final String OR_Continue_Button = "//a[@data-qa='continue-button']";
    public static final String S_Continue_Button = "Continue Button";
    
    public static final String OR_Logged_In_Indicator = "//a[contains(text(),'Logged in as')]";
    public static final String S_Logged_In_Indicator = "Logged In Indicator";
    
    public static final String OR_Logout_Button = "//a[contains(text(),'Logout')]";
    public static final String S_Logout_Button = "Logout Button";
    
    public static final String OR_Delete_Account_Button = "//a[contains(text(),'Delete Account')]";
    public static final String S_Delete_Account_Button = "Delete Account Button";
    
    public static final String OR_Account_Deleted_Success = "//b[contains(text(),'Account Deleted!')]";
    public static final String S_Account_Deleted_Success = "Account Deleted Success Message";
    
    // Error Messages
    public static final String OR_Login_Error_Message = "//p[contains(text(),'Your email or password is incorrect!')]";
    public static final String S_Login_Error_Message = "Login Error Message";
    
    public static final String OR_Email_Exists_Error = "//p[contains(text(),'Email Address already exist!')]";
    public static final String S_Email_Exists_Error = "Email Already Exists Error";
}
