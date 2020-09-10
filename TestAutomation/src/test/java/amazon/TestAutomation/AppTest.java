package amazon.TestAutomation;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.testng.SkipException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import amazon.TestAutomation.ExcelUtils;
import junit.framework.Assert;

//test class for amazon.com website
public class AppTest {

	WebDriver driver = null;
	public static long implicitlyWait = 20; // in seconds
	public static String driverPath = "C:\\Users\\asus\\Downloads\\New folder\\chromedriver.exe";
	Scanner sc = new Scanner(System.in);

	// Suite Initialization
	@BeforeSuite
	public void suiteSetup() {
		System.out.println("suiteSetup");
		System.setProperty("webdriver.chrome.driver", driverPath);
		driver = new ChromeDriver();

	}

	// Suite cleanup
	@AfterSuite
	public void suiteTeardown() {
		System.out.println("suiteTeardown");
		driver.close();
		driver.quit();
	}

	// browser set-up
	@BeforeTest
	public void beforeTest() throws Exception {
		System.out.println("Open Browser");
		driver.get("https://www.amazon.com/");
		// waitForPageLoaded();
		driver.manage().window().maximize();
		takeSnapShot("urlLoading");

		driver.findElement(By.id("nav-link-accountList")).click();

	}

	// Method for new customer registration
	// test requires name, email, password, confirm password in String format
	@Test(dataProvider = "RegistrationData")
	public void register(String name, String email, String password, String confirmPassword) throws Exception {

		driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, 50);

		driver.findElement(By.id("createAccountSubmit")).click(); // Click on create your amazon account

		// Insert data into registration form
		driver.findElement(By.id("ap_customer_name")).sendKeys(name); // Enter customer name

		driver.findElement(By.id("ap_email")).sendKeys(email); // Enter customer email

		driver.findElement(By.id("ap_password")).sendKeys(password); // Enter password

		driver.findElement(By.id("ap_password_check")).sendKeys(confirmPassword); // confirm password

		driver.findElement(By.id("continue")).click(); // submit button

		String pageTitle = driver.getTitle();
		AssertJUnit.assertEquals("Authentication required", pageTitle);
		takeSnapShot("Authncation page");

		System.out.println("Enter Email otp");
		String otp = sc.nextLine();

		driver.findElement(By.name("code")).sendKeys(otp); // enter otp
		driver.findElement(By.id("a-autoid-0")).click(); // submit button
		
		/*
		 * //select mobile number and enter otp Select country = new Select
		 * (driver.findElement(By.name("cvf_phone_cc")));
		 * country.selectByVisibleText("India +91");
		 * 
		 * String mobile=sc.nextLine();
		 * driver.findElement(By.name("cvf_phone_num")).sendKeys(mobile); // select
		 * phone text box and enter mobile number
		 * 
		 * driver.findElement(By.name("cvf_action")).click(); // click on submit button
		 */
		
		System.out.println("enter mobile authentication");
		String mobileAuthnticate = sc.nextLine();
		System.out.println(mobileAuthnticate);

		String pageTitle1 = driver.getTitle();
		AssertJUnit.assertEquals("Amazon Registration", pageTitle1);
		takeSnapShot("Registration");

		WebElement signIn = driver
				.findElement(By.xpath("//*[@class='a-link-emphasis' and contains(text(),'Sign-In')]")); // click on sign
																										// in button
		wait.until(ExpectedConditions.elementToBeClickable(signIn));
		signIn.click();

		driver.findElement(By.id("ap_email")).sendKeys(email); // enter email
		driver.findElement(By.id("continue")).click(); // click on submit button

		driver.findElement(By.id("ap_password")).sendKeys(password); // enter password
		driver.findElement(By.id("signInSubmit")).click(); // click on submit button

	}

	@DataProvider(name = "RegistrationData")
	public Object[][] datasupplierRegistration() throws Exception {
		final String xlsxFile = "C:\\Users\\asus\\eclipse-workspace\\TestAutomation\\Resources\\test_data.xlsx";
		String[][] arrayObject = (String[][]) ExcelUtils.getExcelData(xlsxFile, "Register");

		System.out.println("done");
		return arrayObject;
	}

	// Method for searching products(books)
	// Requires search name of book
	@Test(dataProvider = "SearchData", dependsOnMethods = "register")
	public void search(String searchName) {

		driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
		WebDriverWait wait = new WebDriverWait(driver, 50);

		// select books category from dropdown
		wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("nav-link-accountList"))));
		Select category = new Select(driver.findElement(By.id("searchDropdownBox")));
		category.selectByVisibleText("Books");

		driver.findElement(By.id("twotabsearchtextbox")).sendKeys(searchName); // enter product name in search text box
		driver.findElement(By.xpath("//*[@value='Go']")).click(); // click on submit button

		// wait.until(ExpectedConditions
		// .elementToBeClickable(driver.findElement(By.id("p_n_feature_browse-bin/2656022011"))));
		driver.findElement(By.xpath("//*[@class='a-list-item']//span[contains(text(),'Paperback')]")).click(); // select
																												// paperback
																												// filter

		/*
		 * select 4th book from list List<WebElement> books
		 * =driver.findElements(By.xpath("//*[@data-index]")); for(WebElement
		 * book:books) { System.out.println(book.toString()); } books.get(3);
		 */

		driver.findElement(By.xpath("//*[@data-index='3']//img")).click(); // select 4th item from list

		driver.findElement(By.xpath(
				"//*[@class='a-button a-spacing-mini a-button-toggle format']//span[contains(text(),'Paperback')]")) // select
																														// paperback
				.click();
//		driver.findElement(By.xpath("//*[@class='a-size-large mediaTab_title'][contains(text(),'Paperback')]")).click();

		driver.findElement(By.id("add-to-cart-button")).click(); // clcik on add to cart button

		driver.findElement(By.id("hlb-ptc-btn")).click(); // click on proceed to check out button

	}

	@DataProvider(name = "SearchData")
	public Object[][] datasupplierSearch() throws Exception {
		final String xlsxFile = "C:\\Users\\asus\\eclipse-workspace\\TestAutomation\\Resources\\test_data.xlsx";
		String[][] arrayObject = (String[][]) ExcelUtils.getExcelData(xlsxFile, "Search");

		System.out.println("done");
		return arrayObject;
	}

	// Method for registering address
	// Requires customer name, mobile, pincode, location, area, landmark,city,
	// address type as String
	@Test(dataProvider = "AddressData", dependsOnMethods = "search")
	public void address(String name, String mobile, String pinCode, String location, String area, String landmark,
			String city, String type) throws InterruptedException {

		driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
		Thread.sleep(12000);

		WebDriverWait wait = new WebDriverWait(driver, 200);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("address-ui-widgets-countryCode")));

		Thread.sleep(2000);

		// select country from dropdown
		WebElement select = driver.findElement(By.name("address-ui-widgets-countryCode"));
		((JavascriptExecutor) driver).executeScript(
				"var select = arguments[0]; for(var i = 0; i < select.options.length; i++){ if(select.options[i].text == arguments[1]){ select.options[i].selected = true; } }",
				select, "India");

		WebElement countryDropdown = driver.findElement(By.xpath(
				"//div[@class='a-section a-spacing-micro adddress-ui-widgets-form-field-container address-ui-widgets-desktop-form-field']/span[@class='a-dropdown-container']"));
		countryDropdown.click();
		Thread.sleep(2000);
		wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//*[@data-value='IN' and @aria-selected='true']"))));
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(By.xpath("//*[@data-value='IN']"))).perform();
		action.click(driver.findElement(By.xpath("//*[@data-value='IS']"))).perform();
		countryDropdown.click();
		action.click(driver.findElement(By.xpath("//*[@data-value='IN']"))).perform();

		// fill address form
		wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("address-ui-widgets-landmark"))));
		driver.findElement(By.id("address-ui-widgets-enterAddressFullName")).sendKeys(name); // enter customer name in
																								// text box

		driver.findElement(By.id("address-ui-widgets-enterAddressPhoneNumber")).sendKeys(mobile); // enter mobile number
																									// in text box

		driver.findElement(By.id("address-ui-widgets-enterAddressPostalCode")).sendKeys(pinCode); // enter pin code in
																									// text box

		driver.findElement(By.id("address-ui-widgets-enterAddressLine1")).sendKeys(location.trim()); // enter location
																										// in text box

		driver.findElement(By.id("address-ui-widgets-enterAddressLine2")).sendKeys(area.trim()); // enter area in text
																									// box

		driver.findElement(By.id("address-ui-widgets-landmark")).sendKeys(landmark.trim()); // enter landmark in text
																							// box

		driver.findElement(By.id("address-ui-widgets-enterAddressCity")).sendKeys(city); // enter city name in text box

		// select state from dropdown
		Select state = new Select(
				driver.findElement(By.id("address-ui-widgets-enterAddressStateOrRegion-dropdown-nativeId")));
		state.selectByVisibleText("MAHARASHTRA");

		// select address type in dropdown
		WebElement selectType = driver
				.findElement(By.name("address-ui-widgets-addr-details-address-type-and-business-hours"));
		((JavascriptExecutor) driver).executeScript(
				"var selectType = arguments[0]; for(var i = 0; i < selectType.options.length; i++){ if(selectType.options[i].text == arguments[1]){ selectType.options[i].selected = true; } }",
				selectType, " Home (7 am – 9 pm delivery) ");

		WebElement typeDropdown = driver
				.findElement(By.id("address-ui-widgets-addr-details-address-type-and-business-hours"));
		typeDropdown.click();

		if (type.equalsIgnoreCase("Home")) {
			action.moveToElement(driver.findElement(By.xpath("//*[@data-value='RES']"))).perform();
			action.click(driver.findElement(By.xpath("//*[@data-value='RES']"))).perform(); // select residential type
																							// in dropdown

			driver.findElement(By.id("address-ui-widgets-use-as-my-default")).click(); // click to default check box
																						// based on condition
		}

		else {
			action.moveToElement(driver.findElement(By.xpath("//*[@data-value='COM']"))).perform();
			action.click(driver.findElement(By.xpath("//*[@data-value='COM']"))).perform(); // select commercial type in
																							// dropdown

		}

		driver.findElement(By.xpath("//*[@type='submit']")).click(); // click on submit button

		wait.until(
				ExpectedConditions.elementToBeClickable(By.name("address-ui-widgets-saveOriginalOrSuggestedAddress")));
		driver.findElement(By.name("address-ui-widgets-saveOriginalOrSuggestedAddress")).click(); // click on save
																									// button

		driver.navigate().back();

		driver.findElement(By.id("hlb-ptc-btn")).click(); // click on proceed to check out button

	}

	@DataProvider(name = "AddressData")
	public Object[][] datasupplierAddress() throws Exception {
		final String xlsxFile = "C:\\Users\\asus\\eclipse-workspace\\TestAutomation\\Resources\\test_data.xlsx";
		String[][] arrayObject = (String[][]) ExcelUtils.getExcelData(xlsxFile, "Address");

		System.out.println("done");
		return arrayObject;
	}

	// Method for logging out of application
	@Test(dependsOnMethods = "address")
	public void logOut() {
		driver.navigate().back();
		WebDriverWait wait = new WebDriverWait(driver, 200);

		driver.findElement(By.id("nav-hamburger-menu")).click(); // click on menu
		wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//*[@class='hmenu-item' and contains(text(),'Sign Out')]")));
		driver.findElement(By.xpath("//*[@class='hmenu-item' and contains(text(),'Sign Out')]")).click(); // click on
																											// sign out
																											// button

	}

	// Method for login to application
	// Requires email & password in String format
	@Test(dataProvider = "LoginData", dependsOnMethods = "logOut")
	public void login(String email, String password) throws Exception {
		driver.findElement(By.id("ap_email")).sendKeys(email); // enter email
		driver.findElement(By.id("continue")).click(); // click on submit button

		driver.findElement(By.id("ap_password")).sendKeys(password); // enter password
		driver.findElement(By.id("signInSubmit")).click(); // click on submit button

		// to verify security email by Amazon
		System.out.println("Enter 1 to approve security");

		int i = sc.nextInt();
		System.out.println(i);

		String title = driver.getTitle();
		AssertJUnit.assertTrue(title.contains("Amazon.com: Online"));
		takeSnapShot("login");
		// driver.findElement(By.id("ap-account-fixup-phone-skip-link")).click();
	}

	@DataProvider(name = "LoginData")
	public Object[][] datasupplierLogin() throws Exception {
		final String xlsxFile = "C:\\Users\\asus\\eclipse-workspace\\TestAutomation\\Resources\\test_data.xlsx";
		String[][] arrayObject = (String[][]) ExcelUtils.getExcelData(xlsxFile, "Login");

		System.out.println("done");
		return arrayObject;
	}

	// Method for verifying address
	// Requires customer name, mobile, pincode, location, area, landmark,city,
	// address type as String
	@Test(dataProvider = "AddressData", dependsOnMethods = "login")
	public void verifyAddress(String name, String mobile, String pinCode, String location, String area, String landmark,
			String city, String type) throws Exception {

		driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);

		WebDriverWait wait = new WebDriverWait(driver, 50);
		wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("nav-link-accountList"))));

		// click on address symbol on home page
		WebElement addressHomePage = driver
				.findElement(By.xpath("//*[@class='nav-a nav-a-2 a-popover-trigger a-declarative']"));
		wait.until(ExpectedConditions.elementToBeClickable(addressHomePage));
		addressHomePage.click();
		takeSnapShot("Address List");

		// select address from list
		List<WebElement> address = driver.findElements(By.xpath("//ul[@id='GLUXAddressList']/li"));
		List<String> lines = new ArrayList<String>();
		for (WebElement e : address) {
			String verify = e.getText();
			lines.add(verify);
			System.out.println(verify + "/n");
		}

		driver.findElement(By.name("glowDoneButton")).click(); // click on done button

		AssertJUnit.assertTrue(lines.get(0).contains(location));

	}

	// Method for taking snapshots by name
	public void takeSnapShot(String fileName) throws Exception {

		TakesScreenshot scrShot = ((TakesScreenshot) driver); // Convert web driver object to TakeScreenshot

		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE); // Call getScreenshotAs method to create image file

		FileHandler.copy(SrcFile,
				new File("C:\\Users\\asus\\eclipse-workspace\\TestAutomation\\test-output" + fileName + ".png"));

	}

}
