/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ForgotPasswordPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ForgotPassword extends TestCaseHelper {

  ForgotPasswordPage forgotPasswordPage;

  @BeforeMethod(groups = "functional2")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidEmail(String userName, String email) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
    forgotPasswordPage.enterEmail(email);
    forgotPasswordPage.clickSubmit();
    verifyEmailSendSuccessfullyMessage(forgotPasswordPage);

  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidUsername(String userName, String email) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
    forgotPasswordPage.enterUserName(userName);
    forgotPasswordPage.clickSubmit();
    verifyEmailSendSuccessfullyMessage(forgotPasswordPage);

  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidEmailInvalidUsername(String userName, String email) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
    forgotPasswordPage.enterEmail(email);
    forgotPasswordPage.enterUserName(userName + "vague");
    forgotPasswordPage.clickSubmit();
    verifyEmailSendSuccessfullyMessage(forgotPasswordPage);

  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidUserNameInvalidEmail(String userName, String email) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
    forgotPasswordPage.enterEmail(email + "vague");
    forgotPasswordPage.enterUserName(userName);
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage(forgotPasswordPage, "Please provide a valid email");

  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyBlankUserNameInvalidEmail(String userName, String email) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
    forgotPasswordPage.enterEmail(email + "vague");
    forgotPasswordPage.enterUserName("");
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage(forgotPasswordPage, "Please provide a valid email");

  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyValidUserNameBlankEmail(String userName, String email) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
    forgotPasswordPage.enterEmail("");
    forgotPasswordPage.enterUserName(userName + "vague");
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage(forgotPasswordPage, "Please provide a valid username");

  }

  @Given("^I am on forgot password screen$")
  public void onForgotPageAndVerifyElements() throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
  }

  @When("^I type email \"([^\"]*)\"$")
  public void enterEmail(String email) throws Exception {
    forgotPasswordPage.enterEmail(email);
  }

  @When("^I type and username \"([^\"]*)\"$")
  public void enterPassword(String userName) throws Exception {
    forgotPasswordPage.enterUserName(userName);
  }

  @When("^I click submit button$")
  public void clickSubmit() throws Exception {
    forgotPasswordPage.clickSubmit();
  }

  @Then("^I should see email send successfully$")
  public void verifyEmailSendMessage() throws Exception {
    verifyEmailSendSuccessfullyMessage(forgotPasswordPage);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
  public void testVerifyBlankEmailAndUserName(String userName, String email) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    ForgotPasswordPage forgotPasswordPage = loginPage.clickForgotPasswordLink();
    verifyElementsOnForgotPasswordScreen(forgotPasswordPage);
    forgotPasswordPage.enterEmail("");
    forgotPasswordPage.enterUserName("");
    forgotPasswordPage.clickSubmit();
    verifyErrorMessage(forgotPasswordPage, "Please enter either your Email or Username");
    forgotPasswordPage.clickCancel();
    assertTrue("Forgot Password Link should show up", forgotPasswordPage.getForgotPasswordLink().isDisplayed());

  }

  private void verifyElementsOnForgotPasswordScreen(ForgotPasswordPage forgotPasswordPage) {
    testWebDriver.waitForElementToAppear(forgotPasswordPage.getForgotPasswordHeader());
    assertTrue("emailTextField should be displayed", forgotPasswordPage.getEmailTextField().isDisplayed());
    assertTrue("usernameTextField should be displayed", forgotPasswordPage.getUsernameTextField().isDisplayed());
    assertTrue("submitButton should be displayed", forgotPasswordPage.getSubmitButton().isDisplayed());
    assertTrue("cancelButton should be displayed", forgotPasswordPage.getCancelButton().isDisplayed());
    assertTrue("forgotPasswordHeader should be displayed", forgotPasswordPage.getForgotPasswordHeader().isDisplayed());
  }

  private void verifyEmailSendSuccessfullyMessage(ForgotPasswordPage forgotPasswordPage) {
    testWebDriver.waitForElementToAppear(forgotPasswordPage.getEmailSendSuccessMessage());
    assertTrue("email send message should be displayed", forgotPasswordPage.getEmailSendSuccessMessage().isDisplayed());
    assertTrue("please check message div should be displayed", forgotPasswordPage.getPleaseCheckMailDiv().isDisplayed());
  }

  private void verifyErrorMessage(ForgotPasswordPage forgotPasswordPage, String errorMessage) {
    testWebDriver.waitForElementToAppear(forgotPasswordPage.getSaveFailedMessage());
    assertTrue("Error message " + errorMessage + " should show up", forgotPasswordPage.getSaveFailedMessage().getText().contains(errorMessage));
  }


  @AfterMethod(groups = "functional2")
  @After
  public void tearDown() throws Exception {
    try{
    if(!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
    }catch(NoSuchElementException e){}
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"Admin123", "John_Doe@openlmis.com"}
    };

  }
}

