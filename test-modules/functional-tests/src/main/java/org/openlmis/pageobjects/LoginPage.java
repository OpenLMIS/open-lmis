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
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class LoginPage extends Page {

  @FindBy(how = How.XPATH, using = "//div[@class='login-page']/ng-include/div/div[2]/h2")
  private static WebElement pageIdentifierOnLoginPage = null;

  @FindBy(how = How.ID, using = "username")
  private static WebElement userNameField = null;

  @FindBy(how = How.ID, using = "password")
  private static WebElement passwordField = null;

  @FindBy(how = How.XPATH, using = "//a[@openlmis-message='link.forgot.password']")
  private static WebElement forgotPasswordLink = null;

  @FindBy(how = How.ID, using = "locale_en")
  private static WebElement langEnglish = null;

  @FindBy(how = How.ID, using = "locale_pt")
  private static WebElement langPortuguese = null;

  @FindBy(how = How.XPATH, using = "//div[3][@class='alert alert-error ng-binding']")
  private static WebElement loginErrorLabel = null;

  public LoginPage(TestWebDriver driver, String baseUrl) {
    super(driver);

    testWebDriver.setBaseURL(baseUrl);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(userNameField);
  }

  public LoginPage(TestWebDriver testWebDriver) {
    super(testWebDriver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(userNameField);
  }


  public HomePage loginAs(String username, String password) throws IOException {
    testWebDriver.waitForElementToAppear(userNameField);
    testWebDriver.waitForElementToAppear(passwordField);
    userNameField.clear();
    userNameField.sendKeys(username);
    passwordField.sendKeys(password);
    testWebDriver.sleep(500);
    userNameField.submit();
    return new HomePage(testWebDriver);
  }

  public ForgotPasswordPage clickForgotPasswordLink() throws IOException {
    testWebDriver.waitForElementToAppear(forgotPasswordLink);
    forgotPasswordLink.click();
    return new ForgotPasswordPage(testWebDriver);
  }

  public String getEnglishColor() {
    testWebDriver.sleep(1500);
    return langEnglish.getCssValue("color");
  }

  public String getPortuguesColor()

  {
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(langPortuguese);
    return langPortuguese.getCssValue("color");
  }

  public void setLangAsEnglish() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(langEnglish);
    langEnglish.click();
  }

  public void setLangAsPortugues() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(langPortuguese);
    langPortuguese.click();
  }

  public String getPageIdentifierOnLoginPageText() {
    testWebDriver.waitForElementToAppear(pageIdentifierOnLoginPage);
    return pageIdentifierOnLoginPage.getText();
  }

  public String getLoginErrorMessage() {
    return loginErrorLabel.getText();
  }

}