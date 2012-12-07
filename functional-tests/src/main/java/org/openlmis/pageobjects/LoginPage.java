package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;


public class LoginPage extends Page {

    @FindBy(how = How.ID, using = "username")
    private static WebElement userNameField;

    @FindBy(how = How.ID, using = "password")
    private static WebElement passwordField;


    @FindBy(how = How.XPATH, using = "//a[@id=\"username\"]")
    private static WebElement usernameDisplay;

    @FindBy(how = How.LINK_TEXT, using = "Logout")
    private static WebElement logoutLink;

    private String BASE_URL = "http://localhost:9091/";

    private String ERROR_MESSAGE_LOGIN = "The username or password you entered is incorrect. Please try again.";


    public LoginPage(TestWebDriver driver) {
        super(driver);
        testWebDriver.setBaseURL(BASE_URL);
        testWebDriver.setErrorMessage(ERROR_MESSAGE_LOGIN);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);
    }


    public void login(String username, String password) {
        testWebDriver.waitForElementToAppear(userNameField);
        testWebDriver.waitForElementToAppear(passwordField);
        userNameField.sendKeys(username);
        passwordField.sendKeys(password);
        userNameField.submit();

    }

    public void logout() {
        testWebDriver.waitForElementToAppear(usernameDisplay);
        testWebDriver.mouseOver(usernameDisplay);
        usernameDisplay.click();
        testWebDriver.waitForElementToAppear(logoutLink);
        logoutLink.click();

    }

    public boolean verifyWelcomeMessage(String user) {
        testWebDriver.waitForTextToAppear("Welcome " + user);
        return testWebDriver.getPageSource().contains("Welcome " + user);
    }
}