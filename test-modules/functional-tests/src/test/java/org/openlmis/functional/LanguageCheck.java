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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LanguageCheck extends TestCaseHelper {

  @BeforeMethod(groups = "admin")
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsEnglishOnLoginPage() throws Exception {
    LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginpage.setLangAsEnglish();
    verifyColorOfTextAsGray(loginpage.getEnglishColor());
    verifyPageIdentifierLabelOnLoginPage("Sign In");
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsPortugueseOnLoginPage() throws Exception {
    LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginpage.setLangAsPortugues();
    verifyColorOfTextAsGray(loginpage.getPortuguesColor());
    verifyPageIdentifierLabelOnLoginPage("Entrar");
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsPortugueseBeforeForgotPasswordPage() throws Exception {
    LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginpage.setLangAsPortugues();
    loginpage.clickForgotPasswordLink();
    verifyColorOfTextAsGray(loginpage.getPortuguesColor());
    verifyPageIdentifierLabelOnForgotPasswordPage("Submeter");
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsEnglishBeforeForgotPasswordPage() throws Exception {
    LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginpage.setLangAsEnglish();
    loginpage.clickForgotPasswordLink();
    verifyColorOfTextAsGray(loginpage.getEnglishColor());
    verifyPageIdentifierLabelOnForgotPasswordPage("Submit");
  }

  @Test(groups = {"admin"})
  public void changeLanguageAsPortugueseOnForgotPasswordPage() throws Exception {
    LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginpage.clickForgotPasswordLink();
    loginpage.setLangAsPortugues();
    verifyColorOfTextAsGray(loginpage.getPortuguesColor());
    verifyPageIdentifierLabelOnForgotPasswordPage("Submeter");
  }

  private void verifyColorOfTextAsGray(String color) {
    assertEquals(color, "rgba(136, 135, 135, 1)");
  }

  private void verifyPageIdentifierLabelOnLoginPage(String expectedLabel) throws IOException {
    LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
    assertEquals(loginpage.getPageIdentifierOnLoginPageText(), expectedLabel);
  }

  private void verifyPageIdentifierLabelOnForgotPasswordPage(String expectedLabel) throws IOException {
    ForgotPasswordPage forgotPassword = new ForgotPasswordPage(testWebDriver);
    assertEquals(forgotPassword.getPageIdentifierOnForgotPasswordPageAttribute(), expectedLabel);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void signInAsAdmin123AndEnglish(String user) throws Exception {
    LoginPage loginpage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginpage.setLangAsEnglish();
    verifyColorOfTextAsGray(loginpage.getEnglishColor());
    HomePage homePage = loginpage.loginAs(user, user);
    verifyColorOfTextAsGray(loginpage.getEnglishColor());
    assertTrue(homePage.getLogoutLink().isDisplayed());
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123"}
    };
  }

  @AfterMethod(groups = "admin")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }
}
