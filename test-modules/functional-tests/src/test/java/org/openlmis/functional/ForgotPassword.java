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


import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ForgotPasswordPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.testng.annotations.*;

import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ForgotPassword extends TestCaseHelper {

  ForgotPasswordPage forgotPasswordPage;
  LoginPage loginPage;

  @BeforeMethod(groups = "admin")
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    forgotPasswordPage = PageObjectFactory.getForgotPasswordPage(testWebDriver);
  }


  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidEmail(String userName, String email) throws Exception {
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
    forgotPasswordPage.enterEmail(email);
    forgotPasswordPage.clickSubmit();
    verifyEmailSendSuccessfullyMessage();

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidUsername(String userName, String email) {
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
    forgotPasswordPage.enterUserName(userName);
    forgotPasswordPage.clickSubmit();
    verifyEmailSendSuccessfullyMessage();

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidEmailInvalidUsername(String userName, String email) throws Exception {
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
    forgotPasswordPage.enterEmail(email);
    forgotPasswordPage.enterUserName(userName + "vague");
    forgotPasswordPage.clickSubmit();
    verifyEmailSendSuccessfullyMessage();

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidUserNameInvalidEmail(String userName, String email) throws Exception {
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
    forgotPasswordPage.enterEmail(email + "vague");
    forgotPasswordPage.enterUserName(userName);
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage("Please provide a valid email");

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyBlankUserNameInvalidEmail(String userName, String email) throws Exception {
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
    forgotPasswordPage.enterEmail(email + "vague");
    forgotPasswordPage.enterUserName("");
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage("Please provide a valid email");

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidUserNameBlankEmail(String userName, String email) {
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
    forgotPasswordPage.enterEmail("");
    forgotPasswordPage.enterUserName(userName + "vague");
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage("Please provide a valid username");

  }

  @Given("^I am on forgot password screen$")
  public void onForgotPageAndVerifyElements() {
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
  }

  @When("^I type email \"([^\"]*)\"$")
  public void enterEmail(String email) {
    forgotPasswordPage = PageObjectFactory.getForgotPasswordPage(testWebDriver);
    forgotPasswordPage.enterEmail(email);
  }

  @When("^I type and username \"([^\"]*)\"$")
  public void enterPassword(String userName) {
    forgotPasswordPage = PageObjectFactory.getForgotPasswordPage(testWebDriver);
    forgotPasswordPage.enterUserName(userName);
  }

  @When("^I click submit button$")
  public void clickSubmit() {
    forgotPasswordPage = PageObjectFactory.getForgotPasswordPage(testWebDriver);
    forgotPasswordPage.clickSubmit();
  }

  @Then("^I should see email send successfully$")
  public void verifyEmailSendMessage() {
    forgotPasswordPage = PageObjectFactory.getForgotPasswordPage(testWebDriver);
    verifyEmailSendSuccessfullyMessage();
  }

  @Test(groups = {"admin"})
  public void testVerifyBlankEmailAndUserName() {
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen();
    forgotPasswordPage.enterEmail("");
    forgotPasswordPage.enterUserName("");
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage("Please enter either your Email or Username");
    forgotPasswordPage.clickCancel();
    assertTrue("Forgot Password Link should show up", forgotPasswordPage.getForgotPasswordLink().isDisplayed());

  }

  private void verifyElementsOnForgotPasswordScreen() {
    testWebDriver.waitForElementToAppear(forgotPasswordPage.getForgotPasswordHeader());
    assertTrue("emailTextField should be displayed", forgotPasswordPage.getEmailTextField().isDisplayed());
    assertTrue("usernameTextField should be displayed", forgotPasswordPage.getUsernameTextField().isDisplayed());
    assertTrue("submitButton should be displayed", forgotPasswordPage.getSubmitButton().isDisplayed());
    assertTrue("cancelButton should be displayed", forgotPasswordPage.getCancelButton().isDisplayed());
    assertTrue("forgotPasswordHeader should be displayed", forgotPasswordPage.getForgotPasswordHeader().isDisplayed());
  }

  private void verifyEmailSendSuccessfullyMessage() {
    testWebDriver.waitForElementToAppear(forgotPasswordPage.getEmailSendSuccessMessage());
    assertTrue("email send message should be displayed", forgotPasswordPage.getEmailSendSuccessMessage().isDisplayed());
    assertTrue("please check message div should be displayed", forgotPasswordPage.getPleaseCheckMailDiv().isDisplayed());
  }

  private void verifyErrorMessage(String errorMessage) {
    testWebDriver.waitForElementToAppear(forgotPasswordPage.getSaveFailedMessage());
    assertTrue("Error message " + errorMessage + " should show up", forgotPasswordPage.getSaveFailedMessage().getText().contains(errorMessage));
  }


  @AfterMethod(groups = "admin")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    dbWrapper.insertAllAdminRightsAsSeedData();
    try {
      if (!testWebDriver.getElementById("username").isDisplayed()) {
        HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
        dbWrapper.deleteData();
        dbWrapper.closeConnection();
      }
    } catch (Exception e) {
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "John_Doe@openlmis.com"}
    };
  }
}

