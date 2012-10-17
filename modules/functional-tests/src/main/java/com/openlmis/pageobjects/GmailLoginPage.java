package com.openlmis.pageobjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
public class GmailLoginPage {
    @FindBy(how = How.ID, using = "Email")
    private WebElement userNameField;

    @FindBy (how = How.ID, using = "Passwd")
    private WebElement passwordField;


    public GmailLoginPage(WebDriver driver) {
        driver.get("http://mail.google.com");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
    }

    public void login(String username, String password) {

        userNameField.clear();
        passwordField.clear();

        // Type user name and password
        userNameField.sendKeys(username);
        passwordField.sendKeys(password);

        // Submit user name and password
        userNameField.submit();
    }
}