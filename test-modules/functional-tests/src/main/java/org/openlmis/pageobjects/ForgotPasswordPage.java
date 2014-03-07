/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static org.openqa.selenium.support.How.ID;

public class ForgotPasswordPage extends Page {

  @FindBy(how = ID, using = "email")
  private static WebElement emailTextField = null;

  @FindBy(how = ID, using = "username")
  private static WebElement usernameTextField = null;

  @FindBy(how = ID, using = "submitPasswordButton")
  private static WebElement submitButton = null;

  @FindBy(how = ID, using = "cancelPasswordButton")
  private static WebElement cancelButton = null;

  @FindBy(how = ID, using = "forgotPasswordHeader")
  private static WebElement forgotPasswordHeader = null;

  @FindBy(how = ID, using = "forgotPasswordLink")
  private static WebElement forgotPasswordLink = null;

  @FindBy(how = ID, using = "saveFailMessage")
  private static WebElement saveFailMessage = null;

  @FindBy(how = ID, using = "emailSentHeader")
  private static WebElement emailSendSuccessMessage = null;

  @FindBy(how = ID, using = "emailCheckMessage")
  private static WebElement pleaseCheckMailDiv = null;

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

  public ForgotPasswordPage(TestWebDriver driver) {
    super(driver);

    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 1), this);
    testWebDriver.setImplicitWait(1);
  }

  public void enterEmail(String email) {
    testWebDriver.waitForElementToAppear(emailTextField);
    emailTextField.clear();
    emailTextField.sendKeys(email);
  }

  public WebElement getForgotPasswordLink() {
    return forgotPasswordLink;
  }

  public WebElement getEmailSendSuccessMessage() {
    return emailSendSuccessMessage;
  }

  public WebElement getPleaseCheckMailDiv() {
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

  public String getPageIdentifierOnForgotPasswordPageAttribute() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(submitButton);
    return testWebDriver.getAttribute(submitButton, "value");
  }
}
