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


import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.RefrigeratorPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRefrigerator extends TestCaseHelper {

  public String userSIC, password;
  RefrigeratorPage refrigeratorPage;

  @BeforeMethod(groups = "distribution")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @When("^I add new refrigerator$")
  public void clickAddNewButton() throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickAddNew();
  }

  @Then("^I should see New Refrigerator screen$")
  public void shouldSeeNewRefrigeratorModalWindow() throws SQLException {
    verifyNewRefrigeratorModalWindowExist();
  }

  @When("^I enter Brand \"([^\"]*)\"$")
  public void enterBrand(String brand) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInBrandModal(brand);
  }

  @And("^I enter Modal \"([^\"]*)\"$")
  public void enterModal(String modal) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInModelModal(modal);
  }

  @And("^I enter Serial Number \"([^\"]*)\"$")
  public void enterSerialNumber(String serial) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInManufacturingSerialNumberModal(serial);
  }

  @And("^I access done$")
  public void clickDoneOnModal() throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDoneOnModal();
  }

  @And("^I should see refrigerator \"([^\"]*)\" added successfully")
  public void refrigeratorShouldBeAddedSuccessfully(String refrigeratorDetails) throws SQLException {
    verifyRefrigeratorAdded(refrigeratorDetails);
  }

  @And("^I verify Refrigerator data is not synchronised")
  public void verifyRefrigeratorsInDB() throws SQLException {
    assertEquals(dbWrapper.getRowsCountFromDB("Refrigerators"), 0);
  }

  @And("^I delete refrigerator")
  public void clickDelete() throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDelete();
  }

  @And("^I edit refrigerator")
  public void clickEdit() throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator(1);
  }

  @When("^I confirm delete$")
  public void clickOK() throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickOKButton();
  }

  @Then("^I should see refrigerator \"([^\"]*)\" deleted successfully$")
  public void shouldSeeRefrigeratorDeleted(String refrigeratorData) throws SQLException {
    String[] data = refrigeratorData.split(";");
    for (String aData : data)
      assertFalse("Refrigerator with data :" + aData + " should not exist",
        testWebDriver.getPageSource().contains(aData));
  }

  @Then("^I should see confirmation for delete$")
  public void shouldSeeConfirmationOfDelete() throws SQLException {
    verifyConfirmationPopUp();
  }

  @And("^I enter refrigerator temperature \"([^\"]*)\"$")
  public void enterRefrigeratorTemperature(String temperature) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInRefrigeratorTemperature(temperature, 1);
  }

  @And("^I enter low alarm events \"([^\"]*)\"$")
  public void enterLowEvents(String event) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInLowAlarmEvents(event, 1);
  }

  @And("^I enter high alarm events \"([^\"]*)\"$")
  public void enterHighEvents(String event) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInHighAlarmEvents(event, 1);
  }

  @And("^I enter Notes \"([^\"]*)\"$")
  public void enterNotes(String notes) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInNotesTextArea(notes, 1);
  }

  @And("^I add refrigerator$")
  public void clickDone() throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDone();
  }

  @Then("^I see \"([^\"]*)\" refrigerator icon as \"([^\"]*)\"$")
  public void verifyIndividualRefrigeratorColor(String whichIcon, String color) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    refrigeratorPage.verifyRefrigeratorColor(whichIcon, color);
  }

  @Then("^I should not see Refrigerator details section$")
  public void shouldNotSeeRefrigeratorSection() throws SQLException {
    verifyShouldNotSeeRefrigeratorSection();
  }

  @And("^I should see Edit button$")
  public void shouldSeeEditButton() throws SQLException {
    assertTrue("Edit button should show up", RefrigeratorPage.showButtonForRefrigerator1.isDisplayed());
  }

  @And("^I verify \"([^\"]*)\" it was working correctly when I left$")
  public void clickFunctioningCorrectly(String flag) throws InterruptedException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    Thread.sleep(1000);
    if (flag.equalsIgnoreCase("Yes")) refrigeratorPage.clickFunctioningCorrectlyYesRadio(1);
    else if (flag.equalsIgnoreCase("No")) refrigeratorPage.clickFunctioningCorrectlyNoRadio(1);
    else if (flag.equalsIgnoreCase("Dont know")) refrigeratorPage.clickFunctioningCorrectlyDoNotKnowRadio(1);
    else refrigeratorPage.clickFunctioningCorrectlyNR(1);
  }

  @And("^I verify \"([^\"]*)\" that there is a problem with refrigerator since last visit$")
  public void clickProblemSinceLastVisit(String flag) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    if (flag.equalsIgnoreCase("Yes")) refrigeratorPage.clickProblemSinceLastVisitYesRadio(1);
    else if (flag.equalsIgnoreCase("No")) refrigeratorPage.clickProblemSinceLastVisitNoRadio(1);
    else if (flag.equalsIgnoreCase("Dont know")) refrigeratorPage.clickProblemSinceLastVisitDoNotKnowRadio(1);
    else refrigeratorPage.clickProblemSinceLastVisitNR(1);
    refrigeratorPage.removeFocusFromElement();
  }

  @Then("^I should see refrigerator details as refrigerator temperature \"([^\"]*)\" low alarm events \"([^\"]*)\" high alarm events \"([^\"]*)\" notes \"([^\"]*)\"")
  public void verifyRefrigeratorDetails(String temperature, String low, String high, String notes) throws SQLException {
    refrigeratorPage = PageObjectFactory.getRefrigeratorPage(testWebDriver);
    assertEquals(refrigeratorPage.getRefrigeratorTemperateTextFieldValue(1), temperature);
    assertEquals(refrigeratorPage.getNotesTextAreaValue(1), notes);
    assertEquals(refrigeratorPage.getLowAlarmEventsTextFieldValue(1), low);
    assertEquals(refrigeratorPage.getHighAlarmEventsTextFieldValue(1), high);
  }

  public void verifyNewRefrigeratorModalWindowExist() {
    assertTrue("New Refrigerator modal window should show up",
      RefrigeratorPage.newRefrigeratorHeaderOnModal.isDisplayed());
  }

  public void verifyShouldNotSeeRefrigeratorSection() {
    assertFalse("Refrigerator details section should not show up",
      RefrigeratorPage.refrigeratorTemperatureTextField.isDisplayed());
  }

  public void verifyConfirmationPopUp() {
    testWebDriver.sleep(250);
    assertTrue("Refrigerator confirmation for delete should show up", RefrigeratorPage.deletePopUpHeader.isDisplayed());
  }

  @And("^I verify the refrigerator \"([^\"]*)\" present$")
  public void verifyRefrigeratorAdded(String data) {
    String[] refrigeratorDetails = data.split(";");

    for (int i = 0; i < refrigeratorDetails.length; i++) {
      assertEquals(testWebDriver.getElementByXpath(
        "//div[@class='list-row ng-scope']/ng-include/form/div[1]/div[" + (i + 2) + "]").getText(),
        refrigeratorDetails[i]);
    }
  }
}