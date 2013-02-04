package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class UserPage extends Page {

    @FindBy(how = How.ID, using = "userName")
    private static WebElement userNameField;

    @FindBy(how = How.ID, using = "email")
    private static WebElement emailField;

    @FindBy(how = How.ID, using = "firstName")
    private static WebElement firstNameField;

    @FindBy(how = How.ID, using = "lastName")
    private static WebElement lastNameField;

    @FindBy(how = How.XPATH, using = "//input[@value='Save']")
    private static WebElement saveButton;

    @FindBy(how = How.XPATH, using = "//a[contains(text(),'Add new')]")
    private static WebElement addNewButton;

    @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
    private static WebElement successMessage;


    public UserPage(TestWebDriver driver) throws  IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(25);

    }

    public void enterAndverifyUserDetails(String userName, String email, String firstName, String lastName) throws IOException {
        testWebDriver.waitForElementToAppear(addNewButton);
        addNewButton.click();
        testWebDriver.waitForElementToAppear(userNameField);
        userNameField.clear();
        userNameField.sendKeys(userName);
        emailField.clear();
        emailField.sendKeys(email);
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
        saveButton.click();
        testWebDriver.sleep(1000);
        SeleneseTestNgHelper.assertTrue("User "+firstName+" "+lastName+" has been successfully created, password link sent on registered Email address message is not getting displayed",successMessage.isDisplayed());

    }





}