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
    private String baseurl="http://192.168.34.2:8080/openlmis-web/";
    private String errorMessage = "The username or password you entered is incorrect . Please try again.";
    private int DEFAULT_WAIT_TIME = 30;


    public LoginPage() {
        driver = new FirefoxDriver();
        get(baseurl);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
        setImplicitWait(30);
    }

    /*
    Function for entering username, password and submitting data
     */
    public void login(String username, String password) {

        //userNameField.clear();
        //passwordField.clear();

        // Type user name and password
       // userNameField.sendKeys(username);
        new OpenLMISWebElement(userNameField).sendKeys(username);
        new OpenLMISWebElement(passwordField).sendKeys(password);
        //passwordField.sendKeys(password);

        // Submit user name and password
        //userNameField.submit();
        new OpenLMISWebElement(userNameField).submit();

    }

    /*
    Function for clicking log out link
     */
    public void logout() {
        //logoutLink.click();
        waitForLinkToAppear(logoutLink);
        new OpenLMISWebElement(logoutLink).click();

    }

    /*
    Function for verification of URL
     */
    public void verifyUrl(String identifier) {
        String url = getCurrentUrl();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(baseurl + "admin/home"));
        else
            assertTrue(url.contains(baseurl + "home"));

    }

    /*
      Function for verification of error message
    */
    public boolean verifyErrorMessage() {
        waitForTextToAppear(errorMessage);
        return getPageSource().contains(errorMessage);
    }

    /*
      Function for verification of welcome message
    */
    public boolean verifyWelcomeMessage(String user) {
        waitForTextToAppear("! Welcome " + user);
        return getPageSource().contains("! Welcome " + user);
    }


    /*
   Function to get base url
    */
    public void get(String url) {
        driver.get(baseurl);
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
      Function for implicit wait
    */
    public void quitDriver() {
        driver.quit();
    }

    /*
    Function to wait for a link
     */
    public void waitForLinkToAppear(final WebElement link) {
        (new WebDriverWait(driver, DEFAULT_WAIT_TIME)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return (link.isDisplayed());
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