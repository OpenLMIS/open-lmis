/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.DistributionPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class InitiateDistribution extends TestCaseHelper {

  public static final String periodDisplayedByDefault = "Period14";
  public String userSIC, password, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
    deliveryZoneNameFirst, deliveryZoneNameSecond,
    facilityCodeFirst, facilityCodeSecond,
    programFirst, programSecond, schedule;

  @BeforeMethod(groups = {"functional2"})
  @Before
  public void setUp() throws Exception {
    super.setup();
  }


  @Given("^I have the following data for distribution:$")
  public void theFollowingDataExist(DataTable tableData) throws Exception {
    List<Map<String, String>> data = tableData.asMaps();
    for (Map map : data) {
      userSIC = map.get("userSIC").toString();
      password = map.get("password").toString();
      deliveryZoneCodeFirst = map.get("deliveryZoneCodeFirst").toString();
      deliveryZoneCodeSecond = map.get("deliveryZoneCodeSecond").toString();
      deliveryZoneNameFirst = map.get("deliveryZoneNameFirst").toString();
      deliveryZoneNameSecond = map.get("deliveryZoneNameSecond").toString();
      facilityCodeFirst = map.get("facilityCodeFirst").toString();
      facilityCodeSecond = map.get("facilityCodeSecond").toString();
      programFirst = map.get("programFirst").toString();
      programSecond = map.get("programSecond").toString();
      schedule = map.get("schedule").toString();
    }

  }

  @Given("^I access plan my distribution page$")
  public void accessDistributionPage() throws IOException, SQLException {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRForDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
    setupDataForDeliveryZone(deliveryZoneCodeFirst, deliveryZoneCodeSecond,
      deliveryZoneNameFirst, deliveryZoneNameSecond,
      facilityCodeFirst, facilityCodeSecond,
      programFirst, programSecond, schedule);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigatePlanDistribution();
  }

  @When("^I select delivery zone \"([^\"]*)\"$")
  public void selectDeliveryZone(String deliveryZone) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
  }

  @And("^I select program \"([^\"]*)\"$")
  public void selectProgram(String program) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromProgram(program);
  }

  @And("^I select period \"([^\"]*)\"$")
  public void selectPeriod(String period) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromPeriod(period);
  }

  @And("^I initiate distribution$")
  public void initiateDistribution() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickInitiateDistribution();
  }

  @Then("^I should see data download successfully$")
  public void seeDownloadSuccessfully() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.verifyDownloadSuccessFullMessage(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }

  @And("^I should see delivery zone \"([^\"]*)\" program \"([^\"]*)\" period \"([^\"]*)\" in table$")
  public void verifyTableValue(String deliveryZoneNameFirst, String programFirst, String periodDisplayedByDefault) throws IOException {
    verifyElementsInTable(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }

  private void verifyElementsInTable(String deliveryZoneNameFirst, String programFirst, String periodDisplayedByDefault) {
    assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]/" +
      "div[1]/div[1]/div").getText(), deliveryZoneNameFirst);

    assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[2]").getText(), programFirst);

    assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[3]").getText(), periodDisplayedByDefault);

    assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[4]").getText(), "INITIATED");

    assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[5]/a").getText(), "Record Data");

    assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[6]/a").getText(), "Sync");
  }

  @AfterMethod(groups = {"functional2", "smoke"})
  @After
  public void tearDown() throws Exception {
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }
}

