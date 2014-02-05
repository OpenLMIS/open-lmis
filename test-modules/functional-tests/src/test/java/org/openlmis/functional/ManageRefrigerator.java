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
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.RefrigeratorPage;
import org.openqa.selenium.JavascriptExecutor;
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
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickAddNew();
  }

  @Then("^I should see New Refrigerator screen$")
  public void shouldSeeNewRefrigeratorModalWindow() throws SQLException {
    verifyNewRefrigeratorModalWindowExist();
  }

  @When("^I enter Brand \"([^\"]*)\"$")
  public void enterBrand(String brand) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInBrandModal(brand);
  }

  @And("^I enter Modal \"([^\"]*)\"$")
  public void enterModal(String modal) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInModelModal(modal);
  }

  @And("^I enter Serial Number \"([^\"]*)\"$")
  public void enterSerialNumber(String serial) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInManufacturingSerialNumberModal(serial);
  }

  @And("^I access done$")
  public void clickDoneOnModal() throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
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
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDelete();
  }

  @And("^I edit refrigerator")
  public void clickEdit() throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickShowForRefrigerator1();
  }

  @When("^I confirm delete$")
  public void clickOK() throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
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
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInRefrigeratorTemperature(temperature);
  }

  @And("^I enter low alarm events \"([^\"]*)\"$")
  public void enterLowEvents(String event) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInLowAlarmEvents(event);
  }

  @And("^I enter high alarm events \"([^\"]*)\"$")
  public void enterHighEvents(String event) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInHighAlarmEvents(event);
  }

  @And("^I enter Notes \"([^\"]*)\"$")
  public void enterNotes(String notes) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.enterValueInNotesTextArea(notes);
  }

  @And("^I add refrigerator$")
  public void clickDone() throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    refrigeratorPage.clickDone();
  }

  @Then("^I see \"([^\"]*)\" refrigerator icon as \"([^\"]*)\"$")
  public void verifyIndividualRefrigeratorColor(String whichIcon, String color) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
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
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    Thread.sleep(1000);
    if (flag.equalsIgnoreCase("Yes")) refrigeratorPage.clickFunctioningCorrectlyYesRadio();
    else if (flag.equalsIgnoreCase("No")) refrigeratorPage.clickFunctioningCorrectlyNoRadio();
    else if (flag.equalsIgnoreCase("Dont know")) refrigeratorPage.clickFunctioningCorrectlyDontKnowRadio();
    else refrigeratorPage.clickFunctioningCorrectlyNR();
  }

  @And("^I verify \"([^\"]*)\" that there is a problem with refrigerator since last visit$")
  public void clickProblemSinceLastVisit(String flag) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    if (flag.equalsIgnoreCase("Yes")) refrigeratorPage.clickProblemSinceLastVisitYesRadio();
    else if (flag.equalsIgnoreCase("No")) refrigeratorPage.clickProblemSinceLastVisitNoRadio();
    else if (flag.equalsIgnoreCase("Dont know")) refrigeratorPage.clickProblemSinceLastVisitDontKnowRadio();
    else refrigeratorPage.clickProblemSinceLastVisitNR();
  }

  @Then(
    "^I should see refrigerator details as refrigerator temperature \"([^\"]*)\" low alarm events \"([^\"]*)\" high alarm events \"([^\"]*)\" notes \"([^\"]*)\"")
  public void verifyRefrigeratorDetails(String temperature,
                                        String low,
                                        String high,
                                        String notes) throws SQLException {
    refrigeratorPage = PageFactory.getInstanceOfRefrigeratorPage(testWebDriver);
    assertEquals(refrigeratorPage.getRefrigeratorTemperateTextFieldValue(), temperature);
    assertEquals(refrigeratorPage.getNotesTextAreaValue(), notes);
    assertEquals(refrigeratorPage.getLowAlarmEventsTextFieldValue(), low);
    assertEquals(refrigeratorPage.getHighAlarmEventsTextFieldValue(), high);
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

  @AfterMethod(groups = "distribution")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(250);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
      ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
    }
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{{"storeInCharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second", "F10", "F11", "VACCINES", "TB", "M", "Period", 14}};
  }
}