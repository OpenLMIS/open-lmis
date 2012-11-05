package org.openlmis.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class LoginPage {

    @FindBy(how = How.ID, using = "username")
    private WebElement userNameField;

    @FindBy(how = How.ID, using = "password")
    private WebElement passwordField;

    @FindBy(how = How.LINK_TEXT, using = "logout")
    private WebElement logoutLink;

    private WebDriver driver;
    private String BASE_URL = "http://192.168.34.2:8080/openlmis-web/";
    private String ERROR_MESSAGE = "The username or password you entered is incorrect . Please try again.";
    private int DEFAULT_WAIT_TIME = 30;


    public LoginPage() {
        driver = new FirefoxDriver();
        get(BASE_URL);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
        setImplicitWait(30);
    }

    /*
    Function for entering username, password and submitting data
     */
    public void login(String username, String password) {

        waitForElementToAppear(userNameField);
        waitForElementToAppear(passwordField);

        new OpenLMISWebElement(userNameField).sendKeys(username);
        new OpenLMISWebElement(passwordField).sendKeys(password);

        new OpenLMISWebElement(userNameField).submit();
        //userNameField.clear();
        //passwordField.clear();

        // Type user name and password
        // userNameField.sendKeys(username);
        //passwordField.sendKeys(password);

        // Submit user name and password
        //userNameField.submit();

    }

    /*
    Function for clicking log out link
     */
    public void logout() {
        waitForElementToAppear(logoutLink);
        new OpenLMISWebElement(logoutLink).click();
        //logoutLink.click();

    }

    /*
    Function for verification of URL
     */
    public void verifyUrl(String identifier) {
        String url = getCurrentUrl();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(BASE_URL + "admin/home"));
        else
            assertTrue(url.contains(BASE_URL + "home"));

    }

    /*
      Function for verification of error message
    */
    public boolean verifyErrorMessage() {
        waitForTextToAppear(ERROR_MESSAGE);
        return getPageSource().contains(ERROR_MESSAGE);
    }

    /*
      Function for verification of welcome message
    */
    public boolean verifyWelcomeMessage(String user) {
        waitForTextToAppear("Welcome " + user);
        return getPageSource().contains("Welcome " + user);
    }


    /*
   Function to get base url
    */
    public void get(String url) {
        driver.get(BASE_URL);
    }

    /*
    Function to get current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl().toString();
    }

    /*
    Function for getting page source
     */
    public String getPageSource() {
        return driver.getPageSource();
    }

    /*
       Function for implicit wait
     */
    private void setImplicitWait(int defaultTimeToWait) {
        driver.manage().timeouts().implicitlyWait(defaultTimeToWait, TimeUnit.SECONDS);
    }

    /*
      Function to quit driver
    */
    public void quitDriver() {
        driver.quit();
    }

    /*
    Function to wait for an element
     */
    public void waitForElementToAppear(final WebElement element) {
        (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (element.isDisplayed());
            }
        });
    }

    /*
   Function to wait for text to appear on web page
    */
    public void waitForTextToAppear(final String textToWaitFor) {
        (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (getPageSource().contains(textToWaitFor));
            }
        });
    }


}