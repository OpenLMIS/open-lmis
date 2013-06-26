/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

public class ForgotPasswordPage extends Page {


  @FindBy(how = How.ID, using = "email")
  private static WebElement emailTextField;

  @FindBy(how = How.ID, using = "username")
  private static WebElement usernameTextField;

  @FindBy(how = How.XPATH, using = "//input[@value='Submit']")
  private static WebElement submitButton;

  @FindBy(how = How.XPATH, using = "//input[@value='Cancel']")
  private static WebElement cancelButton;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Forgot Password')]")
  private static WebElement forgotPasswordHeader;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Forgot password?')]")
  private static WebElement forgotPasswordLink;

  @FindBy(how = How.ID, using = "saveFailMessage")
  private static WebElement saveFailMessage;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Email Sent!')]")
  private static WebElement emailSendSuccessMessage;

  @FindBy(how = How.XPATH, using = "//div[contains(text(),'Please check your email and click on reset password link.')]")
  private static WebElement pleaseCheckMailDiv;

  public WebElement getEmailTextField() {
    return emailTextField;
  }

  public WebElement getUsernameTextField() {
    return usernameTextField;
  }

  public WebElement getSaveFailedMessage() {
    return saveFailMessage;
  }

  public WebElement getSubmitButton() {
    return submitButton;
  }

  public WebElement getCancelButton() {
    return cancelButton;
  }

  public WebElement getForgotPasswordHeader() {
    return forgotPasswordHeader;
  }

  public ForgotPasswordPage(TestWebDriver driver) throws IOException {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }


  public void enterEmail(String email) {
    testWebDriver.waitForElementToAppear(emailTextField);
    emailTextField.clear();
    emailTextField.sendKeys(email);
  }

  public  WebElement getForgotPasswordLink() {
    return forgotPasswordLink;
  }

  public  WebElement getSaveFailMessage() {
    return saveFailMessage;
  }

  public  WebElement getEmailSendSuccessMessage() {
    return emailSendSuccessMessage;
  }

  public  WebElement getPleaseCheckMailDiv() {
    return pleaseCheckMailDiv;
  }

  public void enterUserName(String userName) {

    testWebDriver.waitForElementToAppear(usernameTextField);
    usernameTextField.clear();
    usernameTextField.sendKeys(userName);
  }

  public void clickSubmit() {
    testWebDriver.waitForElementToAppear(submitButton);
    submitButton.click();

  }

  public void clickCancel() {
    testWebDriver.waitForElementToAppear(cancelButton);
    cancelButton.click();

  }



}
