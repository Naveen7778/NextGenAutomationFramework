# 🚀 NextGenAutomationFramework

Enterprise-grade test automation framework built with **Java**, **Selenium WebDriver**, **TestNG**, and **ExtentReports**. Features 100% XPath-based locator strategy, parallel execution, intelligent data management, and comprehensive reporting.

## 🏆 Key Features

### ⚡ **Advanced Execution Capabilities**
- **Thread-safe parallel execution** across multiple browsers
- **Smart test data management** with Excel integration
- **Flexible parameter handling** with dynamic data source selection
- **Cross-browser compatibility** (Chrome, Firefox, Edge)

### 🎯 **XPath-Centric Architecture**
- **100% XPath-based locator strategy** - Complete framework built around XPath selectors
- **XPath validation engine** - Pre-execution verification of XPath syntax and structure
- **Dynamic XPath handling** - Robust support for complex and dynamic XPath expressions
- **XPath optimization** - Smart strategies for efficient element location

### ⏳ **Smart Wait Strategy**
- **Fluent Wait implementation** - Robust waiting strategy with 10-second timeout
- **500ms polling interval** - Continuous element detection until found
- **XPath-specific wait conditions** - Tailored for XPath-based element location
- **Intelligent timeout handling** - Reduces test flakiness significantly

### 📊 **Enterprise-grade Reporting**
- **Professional ExtentReports** with embedded screenshots
- **Automated failure documentation** with visual evidence
- **Comprehensive test metrics** and execution summaries
- **Real-time reporting** with timestamp tracking

### 🔄 **Intelligent Lifecycle Management**
- **Automated cleanup** of previous test artifacts
- **Self-maintaining architecture** with file management
- **ZIP packaging** for easy report distribution
- **Fresh environment** for each test execution

## 🛠️ Technology Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Language** | Java 11+ | Core programming language |
| **Test Framework** | TestNG | Test execution and management |
| **Web Automation** | Selenium WebDriver | Browser automation |
| **Locator Strategy** | **100% XPath-based** | **Universal element location approach** |
| **Wait Strategy** | Fluent Wait + XPath validation | Smart XPath element detection with 10s timeout |
| **Build Tool** | Maven | Dependency management |
| **Reporting** | ExtentReports 5.x | Professional test reporting |
| **Data Management** | Apache POI | Excel integration |

## 📈 Framework Architecture

XPath-Centric Framework Components:
├── 🧠 BaseClass → Core functionality & test lifecycle
├── 🛠️ DriverManager → Browser management & configuration
├── 📊 ExtentReportManager → Professional test reporting system
├── 🎯 Keywords → XPath-based reusable action libraries
├── ⏳ WaitKeywords → XPath Fluent wait implementation (10s default)
├── ✅ XPath Validator → Comprehensive XPath syntax verification
├── 📸 Screenshot System → Automated failure documentation
├── 🧹 SuiteListener → Lifecycle management & cleanup
└── 📋 Test Cases → Business logic with XPath locators

### 🔧 **XPath-Based Execution Flow**

🔍 Validate XPath syntax and structure (not empty/null/malformed)

⏳ Apply XPath-optimized Fluent Wait (10s timeout, 500ms polling)

✅ Verify XPath element state (visible/clickable/present)

🎯 Execute keyword action using XPath locator

📸 Capture screenshot with XPath details on failure

📝 Log XPath-specific execution results


## 🚦 Getting Started

### Prerequisites
- **Java 24** or higher
- **Maven 3.9.11+**
- **Chrome/Firefox** browsers

### Quick Setup

Clone the repository
git clone https://github.com/Naveen7778/NextGenAutomationFramework.git

Navigate to project directory
cd NextGenAutomationFramework

Install dependencies
mvn clean install

Verify setup
mvn compile

Run sample tests
mvn test


