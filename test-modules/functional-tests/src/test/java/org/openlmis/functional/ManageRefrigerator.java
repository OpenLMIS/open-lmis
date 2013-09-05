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
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRefrigerator extends TestCaseHelper {

  public String userSIC, password;
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";

  @BeforeMethod(groups = "functional2")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @When("^I add new refrigerator$")
  public void clickAddNewButton() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickAddNew();
  }

  @Then("^I should see New Refrigerator screen$")
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

  @And("^I access done$")
  public void clickDoneOnModal() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDoneOnModal();
  }

  @And("^I should see refrigerator \"([^\"]*)\" added successfully")
  public void refrigeratorShouldBeAddedSuccessfully(String refrigeratorDetails) throws IOException, SQLException {
    verifyRefrigeratorAdded(refrigeratorDetails);
  }

  @And("^I verify Refrigerator data is not synchronised")
  public void verifyRefrigeratorsInDB() throws IOException, SQLException {
    dbWrapper.verifyRecordCountInTable("Refrigerators","0");
  }

  @And("^I delete refrigerator")
  public void clickDelete() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDelete();
  }

  @And("^I edit refrigerator")
  public void clickEdit() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickEdit();
  }

  @When("^I confirm delete$")
  public void clickOK() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickOKButton();
  }

  @Then("^I should see refrigerator \"([^\"]*)\" deleted successfully$")
  public void shouldSeeRefrigeratorDeleted(String refrigeratorData) throws IOException, SQLException {
    String[] data = refrigeratorData.split(";");
    for (String aData : data)
      assertFalse("Refrigerator with data :" + aData + " should not exist", testWebDriver.getPageSource().contains(aData));
  }

  @Then("^I should see confirmation for delete$")
  public void shouldSeeConfirmationOfDelete() throws IOException, SQLException {
    verifyConfirmationPopUp();
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

  @And("^I enter Notes \"([^\"]*)\"$")
  public void enterNotes(String notes) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInNotesTextArea(notes);
  }

  @And("^I add refrigerator$")
  public void clickDone() throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDone();
  }

    @Then("^I should see \"([^\"]*)\" refrigerator icon as \"([^\"]*)\"$")
    public void verifyIndividualRefrigeratorColor(String whichIcon, String color) throws IOException, SQLException {
        RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
        refrigeratorPage.verifyIndividualRefrigeratorColor(whichIcon, color);
    }

  @Then("^I should not see Refrigerator details section$")
  public void shouldNotSeeRefrigeratorSection() throws IOException, SQLException {
    verifyShouldNotSeeRefrigeratorSection();
  }

  @And("^I should see Edit button$")
  public void shouldSeeEditButton() throws IOException, SQLException {
    assertTrue("Edit button should show up", new RefrigeratorPage(testWebDriver).editButton.isDisplayed());
  }

  @And("^I verify \"([^\"]*)\" it was working correctly when I left$")
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

  @And("^I verify \"([^\"]*)\" that there is a problem with refrigerator since last visit$")
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
    refrigeratorPage.onRefrigeratorScreen();
  }

  @Then("^I should see refrigerator details as refrigerator temperature \"([^\"]*)\" low alarm events \"([^\"]*)\" high alarm events \"([^\"]*)\" notes \"([^\"]*)\"")
  public void verifyRefrigeratorDetails(String temperature, String low, String high, String notes) throws IOException, SQLException {
    RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
    assertEquals(refrigeratorPage.getRefrigeratorTemperateTextFieldValue(), temperature);
//    assertEquals(refrigeratorPage.getLowAlarmEventsTextFieldValue(),low);
//    assertEquals(refrigeratorPage.getHighAlarmEventsTextFieldValue(),high);
    assertEquals(refrigeratorPage.getNotesTextAreaValue(), notes);
  }

    @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function")
    public void testVerifyDuplicateRefrigerator(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                    String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                    String facilityCodeFirst, String facilityCodeSecond,
                                                    String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

        List<String> rightsList = new ArrayList<String>();
        rightsList.add("MANAGE_DISTRIBUTION");
        setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
        setupDataForDeliveryZone(true, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
                deliveryZoneNameFirst, deliveryZoneNameSecond,
                facilityCodeFirst, facilityCodeSecond,
                programFirst, programSecond, schedule);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
        dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        DistributionPage distributionPage = homePage.navigatePlanDistribution();
        distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
        distributionPage.selectValueFromProgram(programFirst);
        distributionPage.clickInitiateDistribution();
        distributionPage.clickRecordData();
        FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
        facilityListPage.selectFacility("F10");
        RefrigeratorPage refrigeratorPage = new RefrigeratorPage(testWebDriver);
        refrigeratorPage.clickAddNew();
        refrigeratorPage.addNewRefrigerator("a","a","a");
        refrigeratorPage.clickAddNew();
        refrigeratorPage.addNewRefrigerator("a","a","a");
        refrigeratorPage.verifyDuplicateErrorMessage("Duplicate Manufacturer Serial Number");
    }

  public void verifyNewRefrigeratorModalWindowExist() {
    assertTrue("New Refrigerator modal window should show up", new RefrigeratorPage(testWebDriver).newRefrigeratorHeaderOnModal.isDisplayed());
  }


  public void verifyShouldNotSeeRefrigeratorSection() {
    assertFalse("Refrigerator details section should not show up", new RefrigeratorPage(testWebDriver).refrigeratorTemperatureTextField.isDisplayed());
  }

  public void verifyConfirmationPopUp() {
    testWebDriver.sleep(250);
    assertTrue("Refrigerator confirmation for delete should show up", new RefrigeratorPage(testWebDriver).deletePopUpHeader.isDisplayed());
  }

  public void verifyRefrigeratorAdded(String data) {
    String[] refrigeratorDetails = data.split(";");

    for (int i = 0; i < refrigeratorDetails.length; i++) {
      assertEquals(testWebDriver.getElementByXpath("//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(), refrigeratorDetails[i]);
    }

  }


  @AfterMethod(groups = "functional2")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(250);
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

