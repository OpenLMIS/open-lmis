/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ConfigureOrderPage;
import org.openlmis.pageobjects.HomePage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConfigureOrderTemplate extends TestCaseHelper {


  @BeforeMethod(groups = "functional2")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }


  @And("^I access configure order screen$")
  public void accessOrderScreen() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateConfigureOrderScreen();
  }

  @Then("^I should see order file prefix \"([^\"]*)\"$")
  public void verifyOrderPrefix(String prefix) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    assertEquals(configureOrderPage.getOrderPrefix(), prefix);
  }

  @And("^I should see include column header as \"([^\"]*)\"$")
  public void verifyIncludeColumnHeader(String indicator) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    assertEquals(String.valueOf(configureOrderPage.getIncludeOrderHeader()), indicator);
  }

  @And("^I should see all column headers disabled$")
  public void verifyAllColumnsDisabled() throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.verifyColumnHeadersDisabled();
  }

  @And("^I should see include checkbox \"([^\"]*)\" for all column headers$")
  public void verifyAllColumnsDisabled(String flag) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.verifyIncludeCheckboxForAllColumnHeaders(flag);
  }

  @When("^I click save on order file format screen$")
  public void clickSave() throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.clickSaveButton();
  }

  @Then("^I should see \"([^\"]*)\"$")
  public void verifySaveSuccessfullyMessage(String message) throws Exception {
    ConfigureOrderPage configureOrderPage = new ConfigureOrderPage(testWebDriver);
    configureOrderPage.verifySuccessMessage(message);
  }


  @AfterMethod(groups = "functional2")
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
      {"Admin123", "Admin123", "VACCINES"}
    };

  }

}

