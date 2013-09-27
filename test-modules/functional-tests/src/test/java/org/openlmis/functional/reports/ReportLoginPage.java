/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.functional.reports;

import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.io.IOException;

public class ReportLoginPage extends LoginPage {


  @FindBy(how = How.ID, using = "username")
  private static WebElement userNameField;

  @FindBy(how = How.ID, using = "password")
  private static WebElement passwordField;


  public ReportLoginPage(TestWebDriver driver, String baseUrl) throws IOException {
    super(driver, baseUrl);
  }

  public ReportHomePage loginAs(String username, String password) throws IOException {
    testWebDriver.waitForElementToAppear(userNameField);
    testWebDriver.waitForElementToAppear(passwordField);
    userNameField.sendKeys(username);
    passwordField.sendKeys(password);
    userNameField.submit();
    return new ReportHomePage(testWebDriver);
  }

}
