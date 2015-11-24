/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ForgotPasswordPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LanguageCheck extends TestCaseHelper {

  LoginPage loginPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsEnglishOnLoginPage() {
    loginPage.setLangAsEnglish();
    testWebDriver.sleep(1000);
    verifyColorOfTextAsGray(loginPage.getEnglishColor());
    verifyPageIdentifierLabelOnLoginPage("Sign In");
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsPortugueseOnLoginPage() {
    loginPage.setLangAsPortuguese();
    testWebDriver.sleep(1000);
    verifyColorOfTextAsGray(loginPage.getPortugueseColor());
    verifyPageIdentifierLabelOnLoginPage("Entrar");
    loginPage.setLangAsEnglish();
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsPortugueseBeforeForgotPasswordPage() {
    loginPage.setLangAsPortuguese();
    testWebDriver.sleep(1000);
    loginPage.clickForgotPasswordLink();
    testWebDriver.sleep(1000);
    verifyColorOfTextAsGray(loginPage.getPortugueseColor());
    verifyPageIdentifierLabelOnForgotPasswordPage("Submeter");
    loginPage.setLangAsEnglish();
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsEnglishBeforeForgotPasswordPage() {
    loginPage.setLangAsEnglish();
    testWebDriver.sleep(1000);
    loginPage.clickForgotPasswordLink();
    testWebDriver.sleep(1000);
    verifyColorOfTextAsGray(loginPage.getEnglishColor());
    verifyPageIdentifierLabelOnForgotPasswordPage("Submit");
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsPortugueseOnForgotPasswordPage() {
    loginPage.clickForgotPasswordLink();
    testWebDriver.sleep(1000);
    loginPage.setLangAsPortuguese();
    testWebDriver.sleep(1000);
    verifyColorOfTextAsGray(loginPage.getPortugueseColor());
    verifyPageIdentifierLabelOnForgotPasswordPage("Submeter");
    loginPage.setLangAsEnglish();
  }

  private void verifyColorOfTextAsGray(String color) {
    assertEquals(color, "rgba(136, 135, 135, 1)");
  }

  private void verifyPageIdentifierLabelOnLoginPage(String expectedLabel) {
    assertEquals(loginPage.getPageIdentifierOnLoginPageText(), expectedLabel);
  }

  private void verifyPageIdentifierLabelOnForgotPasswordPage(String expectedLabel) {
    ForgotPasswordPage forgotPasswordPage = PageObjectFactory.getForgotPasswordPage(testWebDriver);
    assertEquals(forgotPasswordPage.getPageIdentifierOnForgotPasswordPageAttribute(), expectedLabel);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void signInAsAdmin123AndEnglish(String user) {
    loginPage.setLangAsEnglish();
    testWebDriver.sleep(1000);
    verifyColorOfTextAsGray(loginPage.getEnglishColor());
    HomePage homePage = loginPage.loginAs(user, user);
    testWebDriver.sleep(1000);
    verifyColorOfTextAsGray(loginPage.getEnglishColor());
    assertTrue(homePage.getLogoutLink().isDisplayed());
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123"}
    };
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    dbWrapper.insertAllAdminRightsAsSeedData();
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }
}
