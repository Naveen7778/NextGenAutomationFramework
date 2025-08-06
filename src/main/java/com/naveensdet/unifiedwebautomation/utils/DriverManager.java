package com.naveensdet.unifiedwebautomation.utils;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {

	// Update logger declaration:
	private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);

	/**
	 * ThreadLocal variable to store WebDriver instances for each thread individually.
	 * This allows parallel test execution by providing each test thread its own isolated WebDriver,
	 * preventing conflicts and ensuring thread safety.
	 */
	private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

	/**
	 * Initializes the WebDriver based on environment-specific browser configuration
	 */
	public static void initDriver() {
		try {
			String browser = ConfigManager.getEnvSpecificProperty("browser", "chrome").toLowerCase();
			WebDriver driver;

			switch (browser) {
			case "chrome":
				WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver(getChromeOptions());
				break;
			case "firefox":
				WebDriverManager.firefoxdriver().setup();
				driver = new FirefoxDriver(getFirefoxOptions());
				break;
			case "edge":
				WebDriverManager.edgedriver().setup();
				driver = new EdgeDriver(getEdgeOptions());
				break;
			case "safari":
				WebDriverManager.safaridriver().setup();
				driver = new SafariDriver(getSafariOptions());
				break;
			case "remote":
				driver = createRemoteWebDriver();
				break;
			default:
				logger.error("Browser not supported: {}", browser);
				throw new FrameworkException("Browser not supported: " + browser);
			}

			tlDriver.set(driver);
			setupDriverConfiguration(driver);
			logger.info("Browser {} launched successfully.", browser.toUpperCase());

		} catch (Exception e) {
			logger.error("WebDriver initialization failed", e);
			throw new FrameworkException("WebDriver initialization failed", e);
		}
	}

	/**
	 * Gets the thread-local WebDriver instance - THIS WAS MISSING IN MY PREVIOUS CODE
	 */
	public static WebDriver getDriver() {
		WebDriver driver = tlDriver.get();
		if (driver == null) {
			throw new FrameworkException("WebDriver not initialized. Call initDriver() first.");
		}
		return driver;
	}

	/**
	 * Quits the WebDriver and removes it from ThreadLocal
	 */
	public static void quitDriver() {
		WebDriver driver = tlDriver.get();
		if (driver != null) {
			driver.quit();
			tlDriver.remove();
			logger.info("Browser closed successfully.");
		}
	}

	/**
	 * Configures the WebDriver with timeouts and window settings
	 */
	private static void setupDriverConfiguration(WebDriver driver) {
		if (driver != null) {
			// Maximize browser window
			driver.manage().window().maximize();

			// Set implicit wait
			int implicitWaitSec = ConfigManager.getIntProperty("implicitWait", 10);
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSec));
		}
	}

	/**
	 * Creates Chrome options with configured settings including dynamic download directory
	 */
	private static ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();

		if (Boolean.parseBoolean(ConfigManager.getProperty("headless", "false"))) {
			options.addArguments("--headless=new");
		}

		String mobileDevice = ConfigManager.getProperty("mobileDevice");
		if (mobileDevice != null && !mobileDevice.isEmpty()) {
			Map<String, String> mobileEmu = new HashMap<>();
			mobileEmu.put("deviceName", mobileDevice);
			options.setExperimentalOption("mobileEmulation", mobileEmu);
			logger.info("Chrome mobile emulation enabled for device: {}", mobileDevice);
		}

		// ADD THIS: Configure dynamic download directory
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", ConfigManager.getDownloadDirectoryEnsureExists());
		prefs.put("download.prompt_for_download", false);
		prefs.put("download.directory_upgrade", true);
		prefs.put("safebrowsing.enabled", true);
		options.setExperimentalOption("prefs", prefs);
		logger.info("Chrome download directory configured: {}", ConfigManager.getDownloadDirectory());

		Proxy proxy = getProxyCapability();
		if (proxy != null) {
			options.setCapability("proxy", proxy);
		}

		options.addArguments("--disable-gpu", "--window-size=1920,1080", "--no-sandbox");
		return options;
	}

	/**
	 * Creates Firefox options with configured settings including dynamic download directory
	 */
	private static FirefoxOptions getFirefoxOptions() {
		FirefoxOptions options = new FirefoxOptions();

		if (Boolean.parseBoolean(ConfigManager.getProperty("headless", "false"))) {
			options.addArguments("--headless");
		}

		// ADD THIS: Configure Firefox download directory
		options.addPreference("browser.download.dir", ConfigManager.getDownloadDirectoryEnsureExists());
		options.addPreference("browser.download.folderList", 2); // Use custom download directory
		options.addPreference("browser.download.useDownloadDir", true);
		options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,application/octet-stream,text/csv,application/zip");
		logger.info("Firefox download directory configured: {}", ConfigManager.getDownloadDirectory());

		Proxy proxy = getProxyCapability();
		if (proxy != null) {
			options.setCapability("proxy", proxy);
		}

		options.addArguments("--width=1920", "--height=1080");
		return options;
	}


	/**
	 * Creates Edge options with configured settings including dynamic download directory
	 */
	private static EdgeOptions getEdgeOptions() {
		EdgeOptions options = new EdgeOptions();

		if (Boolean.parseBoolean(ConfigManager.getProperty("headless", "false"))) {
			options.addArguments("--headless=new");
		}

		// ADD THIS: Configure Edge download directory (similar to Chrome)
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", ConfigManager.getDownloadDirectoryEnsureExists());
		prefs.put("download.prompt_for_download", false);
		prefs.put("download.directory_upgrade", true);
		prefs.put("safebrowsing.enabled", true);
		options.setExperimentalOption("prefs", prefs);
		logger.info("Edge download directory configured: {}", ConfigManager.getDownloadDirectory());

		Proxy proxy = getProxyCapability();
		if (proxy != null) {
			options.setCapability("proxy", proxy);
		}

		options.addArguments("--window-size=1920,1080");
		return options;
	}


	/**
	 * Creates Safari options with configured settings
	 */
	private static SafariOptions getSafariOptions() {
		SafariOptions options = new SafariOptions();

		Proxy proxy = getProxyCapability();
		if (proxy != null) {
			options.setCapability("proxy", proxy);
		}

		return options;
	}

	/**
	 * Creates a RemoteWebDriver for grid execution
	 */
	private static WebDriver createRemoteWebDriver() {
		try {
			String gridUrl = ConfigManager.getProperty("gridUrl");
			String browser = ConfigManager.getEnvSpecificProperty("browser", "chrome").toLowerCase();

			Capabilities options;
			switch (browser) {
			case "chrome":
				options = getChromeOptions();
				break;
			case "firefox":
				options = getFirefoxOptions();
				break;
			case "edge":
				options = getEdgeOptions();
				break;
			default:
				options = getChromeOptions();
			}

			return new RemoteWebDriver(new URI(gridUrl).toURL(), options);
		} catch (MalformedURLException | URISyntaxException e) {
			throw new FrameworkException("Grid URL malformed", e);
		}
	}

	/**
	 * Creates proxy configuration if enabled
	 */
	private static Proxy getProxyCapability() {
		if (!Boolean.parseBoolean(ConfigManager.getProperty("proxyEnabled", "false"))) {
			return null;
		}

		String host = ConfigManager.getProperty("proxyHost");
		String port = ConfigManager.getProperty("proxyPort");
		if (host != null && port != null) {
			String address = host + ":" + port;
			Proxy proxy = new Proxy();
			proxy.setHttpProxy(address);
			proxy.setSslProxy(address);
			return proxy;
		}

		return null;
	}

	// Add this method to DriverManager.java
	public static boolean isDriverInitialized() {
		try {
			WebDriver driver = tlDriver.get();
			return driver != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static WebDriver getDriverSafely() {
		try {
			return tlDriver.get();
		} catch (Exception e) {
			logger.warn("WebDriver not available: {}", e.getMessage());
			return null;
		}
	}

}
