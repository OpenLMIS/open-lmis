package org.openlmis.pageobjects;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openlmis.UiUtils.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;


public class LoginPage extends Page {

    @FindBy(how = How.ID, using = "username")
    private static WebElement userNameField;

    @FindBy(how = How.ID, using = "password")
    private static WebElement passwordField;

    @FindBy(how = How.LINK_TEXT, using = "logout")
    private static WebElement logoutLink;



    public LoginPage(TestWebDriver driver) {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    }


    public void login(String username, String password) {
        testWebDriver.waitForElementToAppear(userNameField);
        testWebDriver.waitForElementToAppear(passwordField);

        userNameField.sendKeys(username);
        passwordField.sendKeys(password);

        userNameField.submit();

    }

    public void logout() {
        testWebDriver.waitForElementToAppear(logoutLink);
        logoutLink.click();

    }

    public boolean verifyWelcomeMessage(String user) {
        testWebDriver.waitForTextToAppear("Welcome " + user);
        return testWebDriver.getPageSource().contains("Welcome " + user);
    }



}