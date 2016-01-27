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


public class LoginPage extends Page {

  @FindBy(how = ID, using = "signInLabel")
  private static WebElement pageIdentifierOnLoginPage = null;

  @FindBy(how = ID, using = "username")
  private static WebElement userNameField = null;

  @FindBy(how = ID, using = "password")
  private static WebElement passwordField = null;

  @FindBy(how = ID, using = "forgotPasswordLink")
  private static WebElement forgotPasswordLink = null;

  @FindBy(how = ID, using = "locale_en")
  private static WebElement langEnglish = null;

  @FindBy(how = ID, using = "locale_pt")
  private static WebElement langPortuguese = null;

  @FindBy(how = ID, using = "loginError")
  private static WebElement loginErrorLabel = null;

  public static String baseUrl = "http://localhost:9091";

  public LoginPage(TestWebDriver driver, String baseUrl) {
    super(driver);
    LoginPage.baseUrl = baseUrl;
    testWebDriver.setBaseURL(baseUrl + "/public/pages/login.html");
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(userNameField);
  }

  public LoginPage(TestWebDriver testWebDriver) {
    super(testWebDriver);
    testWebDriver.getUrl(baseUrl + "/public/pages/login.html");
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(userNameField);
  }

  public HomePage loginAs(String username, String password) {
    testWebDriver.getUrl(baseUrl + "/public/pages/login.html");
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(userNameField);
    testWebDriver.waitForElementToAppear(passwordField);

    sendKeys(userNameField, username);
    sendKeys(passwordField, password);

    userNameField.submit();
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public ForgotPasswordPage clickForgotPasswordLink() {
    testWebDriver.getUrl(baseUrl + "/public/pages/login.html");
    testWebDriver.waitForElementToAppear(forgotPasswordLink);
    forgotPasswordLink.click();
    return PageObjectFactory.getForgotPasswordPage(testWebDriver);
  }

  public String getEnglishColor() {
    testWebDriver.sleep(1500);
    return langEnglish.getCssValue("color");
  }

  public String getPortugueseColor() {
    testWebDriver.sleep(1500);
    testWebDriver.waitForElementToAppear(langPortuguese);
    return langPortuguese.getCssValue("color");
  }

  public void setLangAsEnglish() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(langEnglish);
    langEnglish.click();
  }

  public void setLangAsPortuguese() {
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

  public void clearUserName() {
    testWebDriver.waitForElementToAppear(userNameField);
    userNameField.clear();
  }
}