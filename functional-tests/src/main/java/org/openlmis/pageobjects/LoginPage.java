package org.openlmis.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;


import java.util.concurrent.TimeUnit;

public class LoginPage {

    @FindBy(how = How.ID, using = "username")
    private WebElement userNameField;

    @FindBy(how = How.ID, using = "password")
    private WebElement passwordField;

    @FindBy(how = How.LINK_TEXT, using = "logout")
    private WebElement logoutLink;



    public LoginPage(WebDriver driver) {
        driver.get("http://192.168.34.2:8080/openlmis-web/");
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 15), this);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
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

    public void logout() {
        logoutLink.click();

    }

}