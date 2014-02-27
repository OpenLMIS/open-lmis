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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.EPIUsePage;
import org.openlmis.pageobjects.HomePage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class RecordEPIUse extends TestCaseHelper {

  public String userSIC, password;
  EPIUsePage epiUsePage;

  @BeforeMethod(groups = {"distribution"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @Then("^I should see product group \"([^\"]*)\"")
  public void verifyProductGroup(String productGroup) {
    epiUsePage = PageFactory.getEpiUsePage(testWebDriver);
    epiUsePage.verifyProductGroup(productGroup, 1);
  }

  @When("^I Enter EPI values without end of month:$")
  public void enterEPIValues(DataTable tableData) {
    Map<String, String> epiData = tableData.asMaps().get(0);
    epiUsePage = PageFactory.getEpiUsePage(testWebDriver);
    epiUsePage.enterValueInDistributed(epiData.get("distributed"), 1);
    epiUsePage.enterValueInExpirationDate(epiData.get("expirationDate"), 1);
    epiUsePage.enterValueInLoss(epiData.get("loss"), 1);
    epiUsePage.enterValueInReceived(epiData.get("received"), 1);
    epiUsePage.enterValueInStockAtFirstOfMonth(epiData.get("firstOfMonth"), 1);
  }

  @When("^I verify saved EPI values:$")
  public void verifySavedEPIValues(DataTable tableData) {
    epiUsePage = PageFactory.getEpiUsePage(testWebDriver);
    epiUsePage.navigateToRefrigerators();
    epiUsePage.navigateToEpiUse();
    List<Map<String, String>> epiData = tableData.asMaps();
    epiUsePage.verifyData(epiData);
  }

  @And("^I verify total is \"([^\"]*)\"$")
  public void verifyTotalField(String total) {
    epiUsePage = PageFactory.getEpiUsePage(testWebDriver);
    epiUsePage.verifyTotal(total, 1);
  }

  @Then("^Verify indicator should be \"([^\"]*)\"$")
  public void shouldVerifyIndicatorColor(String color) throws SQLException {
    epiUsePage = PageFactory.getEpiUsePage(testWebDriver);
    epiUsePage.verifyIndicator(color);
  }

  @When("^I enter EPI end of month as \"([^\"]*)\"")
  public void enterEPIEndOfMonth(String endOfMonth) throws InterruptedException {
    epiUsePage = PageFactory.getEpiUsePage(testWebDriver);
    epiUsePage.enterValueInStockAtEndOfMonth(endOfMonth, 1);
  }

  @AfterMethod(groups = {"distribution"})
  public void tearDown() throws SQLException {
    testWebDriver.sleep(250);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
    ((JavascriptExecutor) TestWebDriver.getDriver()).executeScript("indexedDB.deleteDatabase('open_lmis');");
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"storeInCharge", "Admin123", "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "Period", 14}
    };
  }
}

