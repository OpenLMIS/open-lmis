package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class LoginPage extends Page {

    @FindBy(how = How.ID, using = "username")
    private static WebElement userNameField;

    @FindBy(how = How.ID, using = "password")
    private static WebElement passwordField;

    private String BASE_URL;

    private String ERROR_MESSAGE_LOGIN = "The username or password you entered is incorrect. Please try again.";

    private String baseUrl;

    public LoginPage(TestWebDriver driver) throws  IOException{
        super(driver);
        Properties props = new Properties();
        props.load(new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/config.properties"));
        baseUrl = props.getProperty("baseUrl");
        BASE_URL=baseUrl;
        testWebDriver.setBaseURL(BASE_URL);
        testWebDriver.setErrorMessage(ERROR_MESSAGE_LOGIN);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }


    public HomePage loginAs(String username, String password) throws IOException {
        testWebDriver.waitForElementToAppear(userNameField);
        testWebDriver.waitForElementToAppear(passwordField);
        userNameField.sendKeys(username);
        passwordField.sendKeys(password);
        userNameField.submit();
        return new HomePage(testWebDriver);
    }

}