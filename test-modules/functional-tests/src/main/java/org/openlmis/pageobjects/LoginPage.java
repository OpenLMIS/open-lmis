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


public class LoginPage extends Page {

  @FindBy(how = How.ID, using = "username")
  private static WebElement userNameField;

  @FindBy(how = How.ID, using = "password")
  private static WebElement passwordField;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Forgot Password')]")
  private static WebElement forgotPasswordHeader;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Forgot password?')]")
  private static WebElement forgotPasswordLink;

  private String BASE_URL;

  private String baseUrl;

  public LoginPage(TestWebDriver driver, String baseUrl) throws IOException {
    super(driver);

    BASE_URL = baseUrl;
    testWebDriver.setBaseURL(BASE_URL);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(userNameField);
    SeleneseTestNgHelper.assertTrue(userNameField.isDisplayed());
  }


  public HomePage loginAs(String username, String password) throws IOException {
    testWebDriver.waitForElementToAppear(userNameField);
    testWebDriver.waitForElementToAppear(passwordField);
    userNameField.sendKeys(username);
    passwordField.sendKeys(password);
    userNameField.submit();
    return new HomePage(testWebDriver);
  }

  public ForgotPasswordPage clickForgotPasswordLink() throws IOException {
    testWebDriver.waitForElementToAppear(forgotPasswordLink);
    forgotPasswordLink.click();
    testWebDriver.waitForElementToAppear(forgotPasswordHeader);
    return new ForgotPasswordPage(testWebDriver);
  }

}