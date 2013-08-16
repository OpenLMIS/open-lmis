/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.RefrigeratorPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRefrigerator extends TestCaseHelper {

  public String userSIC, password;

  @BeforeMethod(groups = "functional2")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @When("^I click Add New Button$")
  public void clickAddNewButton() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickAddNew();
  }

  @Then("^I should see New Refrigerator Modal window$")
  public void shouldSeeNewRefrigeratorModalWindow() throws IOException, SQLException {
    verifyNewRefrigeratorModalWindowExist();
  }

  @When("^I enter Brand \"([^\"]*)\"$")
  public void enterBrand(String brand) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInBrandModal(brand);
  }

  @And("^I enter Modal \"([^\"]*)\"$")
  public void enterModal(String modal) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInModelModal(modal);
  }

  @And("^I enter Serial Number \"([^\"]*)\"$")
  public void enterSerialNumber(String serial) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInManufacturingSerialNumberModal(serial);
  }

  @And("^I click Done on modal$")
  public void clickDoneOnModal() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDoneOnModal();
  }

  @And("^I click Delete")
  public void clickDelete() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDelete();
  }

  @And("^I click Edit")
  public void clickEdit() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickEdit();
  }

  @When("^I confirm delete$")
  public void clickOK() throws IOException, SQLException {
    verifyConfirmationPopUp();
  }

  @Then("^I should see confirmation for delete$")
  public void shouldSeeConfirmationOfDelete() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickOKButton();
  }

  @And("^I enter refrigerator temperature \"([^\"]*)\"$")
  public void enterRefrigeratorTemperature(String temperature) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInRefrigeratorTemperature(temperature);
  }

  @And("^I enter low alarm events \"([^\"]*)\"$")
  public void enterLowEvents(String event) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInLowAlarmEvents(event);
  }

  @And("^I enter high alarm events \"([^\"]*)\"$")
  public void enterHighEvents(String event) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInHighAlarmEvents(event);
  }

  @And("^I enter Notes$")
  public void enterNotes(String notes) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInNotesTextArea(notes);
  }

  @And("^I click Done$")
  public void clickDone() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDone();
  }

  @Then("^I should not see Refrigerator details section$")
  public void shouldNotSeeRefrigeratorSection() throws IOException, SQLException {
    verifyShouldNotSeeRefrigeratorSection();
  }

  @And("^I should see Edit button$")
  public void shouldSeeEditButton() throws IOException, SQLException {
    assertTrue("Edit button should show up", new RefrigeratorPage(testWebDriver).editButton.isDisplayed());
  }

  @And("^I click \"([^\"]*)\" it was working correctly when I left$")
  public void clickFunctioningCorrectly(String flag) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    if (flag.equalsIgnoreCase("Yes"))
      refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    else if (flag.equalsIgnoreCase("No"))
      refrigeratorPage.clickFunctioningCorrectlyNoRadio();
    else if (flag.equalsIgnoreCase("Dont know"))
      refrigeratorPage.clickFunctioningCorrectlyDontKnowRadio();
    else
      refrigeratorPage.clickFunctioningCorrectlyNR();
  }

  @And("^I click \"([^\"]*)\" that there is a problem with refrigerator since last visit$")
  public void clickProblemSinceLastVisit(String flag) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    if (flag.equalsIgnoreCase("Yes"))
      refrigeratorPage.clickProblemSinceLastVisitYesRadio();
    else if (flag.equalsIgnoreCase("No"))
      refrigeratorPage.clickProblemSinceLastVisitNoRadio();
    else if (flag.equalsIgnoreCase("Dont know"))
      refrigeratorPage.clickProblemSinceLastVisitDontKnowRadio();
    else
      refrigeratorPage.clickProblemSinceLastVisitNR();
  }

  @Then("^I should see Refrigerators screen")
  public void onRefrigeratorScreen() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickRefrigeratorTab();
  }

  public void verifyNewRefrigeratorModalWindowExist() {
    assertTrue("New Refrigerator modal window should show up", new RefrigeratorPage(testWebDriver).newRefrigeratorHeaderOnModal.isDisplayed());
  }

  public void verifyShouldNotSeeRefrigeratorSection() {
    assertFalse("Refrigerator details section should not show up", new RefrigeratorPage(testWebDriver).refrigeratorTemperatureTextField.isDisplayed());
  }

  public void verifyConfirmationPopUp() {
    assertFalse("Refrigerator confirmation for delete should show up", new RefrigeratorPage(testWebDriver).OKButton.isDisplayed());
  }


  @AfterMethod(groups = "functional2")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeincharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
    };

  }
}

