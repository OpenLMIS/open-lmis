package org.openlmis.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;


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
    private String baseurl;


    public LoginPage(WebDriver webdriver, String baseUrl) {
        driver = webdriver;
        baseurl = baseUrl;
        driver.get(baseurl);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    /*
    Function for entering username, password and submitting data
     */
    public void login(String username, String password) {

        userNameField.clear();
        passwordField.clear();

        // Type user name and password
        userNameField.sendKeys(username);
        passwordField.sendKeys(password);

        // Submit user name and password
        userNameField.submit();

    }

    /*
    Function for clicking log out link
     */
    public void logout() {
        logoutLink.click();

    }

    /*
    Function for verification of URL
     */
    public void verifyUrl(String identifier) {
        String url = driver.getCurrentUrl().toString();
        if (identifier.equalsIgnoreCase("Admin"))
            assertTrue(url.contains(baseurl + "admin/home"));
        else
            assertTrue(url.contains(baseurl + "home"));

    }
}