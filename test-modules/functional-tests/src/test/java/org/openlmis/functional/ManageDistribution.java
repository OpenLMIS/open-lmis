/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageDistribution extends TestCaseHelper {

  public static final String NONE_ASSIGNED = "--None Assigned--";
  public static final String SELECT_DELIVERY_ZONE = "--Select Delivery Zone--";
  public static final String periodDisplayedByDefault = "Period14";
  public static final String periodNotToBeDisplayedInDropDown = "Period1";
  public String userSIC, password, deliveryZoneCodeFirst, deliveryZoneCodeSecond,
    deliveryZoneNameFirst, deliveryZoneNameSecond,
    facilityCodeFirst, facilityCodeSecond,
    programFirst, programSecond, schedule;

  @BeforeMethod(groups = "distribution")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }


  @Given("^I have the following data for distribution:$")
  public void theFollowingDataExist(DataTable tableData) throws Exception {
    List<Map<String, String>> data = tableData.asMaps();
    for (Map map : data) {
      userSIC = map.get("userSIC").toString();
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

    List<String> rightsList = new ArrayList<>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true,
      programFirst, userSIC, "200", "openLmis", rightsList, programSecond, "District1", "Ngorongoro", "Ngorongoro");
  }

  @And("^I update product \"([^\"]*)\" to have product group \"([^\"]*)\"$")
  public void setupProductAndProductGroup(String product, String productGroup) throws Exception {
    updateProductWithGroup(product, productGroup);
  }

  @Then("^I should see product group \"([^\"]*)\"")
  public void verifyProductGroup(String productGroup) {
    new EPIUse(testWebDriver).verifyProductGroup(productGroup, 1);
  }

  @When("^I Enter EPI values without end of month:$")
  public void enterEPIValues(DataTable tableData) {
    EPIUse epiUse = new EPIUse(testWebDriver);
    Map<String, String> epiData = tableData.asMaps().get(0);

    epiUse.enterValueInDistributed(epiData.get("distributed"), 1);
    epiUse.enterValueInExpirationDate(epiData.get("expirationDate"), 1);
    epiUse.enterValueInLoss(epiData.get("loss"), 1);
    epiUse.enterValueInReceived(epiData.get("received"), 1);
    epiUse.enterValueInStockAtFirstOfMonth(epiData.get("firstOfMonth"), 1);
  }

  @When("^I verify saved EPI values:$")
  public void verifySavedEPIValues(DataTable tableData) {
    new RefrigeratorPage(testWebDriver).navigateToRefrigeratorTab();
    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigateToEPISUse();
    Map<String, String> epiData = tableData.asMaps().get(0);

    epiUse.verifyData(epiData);
  }

  @And("^I verify total is \"([^\"]*)\"$")
  public void verifyTotalField(String total) {
    new EPIUse(testWebDriver).verifyTotal(total, 1);
  }

  @Then("^I should see program \"([^\"]*)\"$")
  public void verifyProgram(String programs) throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    List<String> firstProgramValuesToBeVerified = new ArrayList<>();

    String[] program = programs.split(",");
    for (int i = 0; i < program.length; i++)
      firstProgramValuesToBeVerified.add(program[i]);

    List<WebElement> valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromProgram();
    verifyAllSelectFieldValues(firstProgramValuesToBeVerified, valuesPresentInDropDown);
  }

  @Then("^I verify fields$")
  public void verifyFieldsOnScreen() throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    verifyElementsPresent(distributionPage);
  }

  @Then("^I should see period \"([^\"]*)\"$")
  public void verifyPeriod(String period) throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    WebElement actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    testWebDriver.sleep(100);
    verifySelectedOptionFromSelectField(period, actualSelectFieldElement);
  }

  @Then("^I should see deliveryZone \"([^\"]*)\"$")
  public void verifyDeliveryZone(String deliveryZone) throws IOException, SQLException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    WebElement actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromDeliveryZone();
    verifySelectedOptionFromSelectField(deliveryZone, actualSelectFieldElement);
  }

  @Given("^I login as user \"([^\"]*)\" having password \"([^\"]*)\"$")
  public void login(String user, String password) throws IOException, SQLException {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    loginPage.loginAs(user, password);
  }

  @And("^I access plan my distribution page$")
  public void accessDistributionPage() throws IOException, SQLException {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();
  }

  @When("^I assign delivery zone \"([^\"]*)\" to user \"([^\"]*)\" having role \"([^\"]*)\"$")
  public void assignDeliveryZone(String deliveryZone, String user, String role) throws IOException, SQLException {
    dbWrapper.insertRoleAssignmentForDistribution(user, role, deliveryZone);
  }

  @When("^I select delivery zone \"([^\"]*)\"$")
  public void selectDeliveryZone(String deliveryZone) throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.selectValueFromDeliveryZone(deliveryZone);
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

  @And("^I verify Distributions data is not synchronised$")
  public void verifyDistributionsInDB() throws IOException, SQLException {
    dbWrapper.verifyRecordCountInTable("Distributions", "1");
  }

  @And("^I initiate distribution$")
  public void initiateDistribution() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickInitiateDistribution();
  }

  @And("^I record data$")
  public void clickRecordData() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickRecordData();
  }

  @And("^I switch off network$")
  public void switchOffNetwork() throws IOException {
    testWebDriver.sleep(2000);
    Runtime.getRuntime().exec("sudo ifconfig en1 down");
    testWebDriver.sleep(2000);
  }

  @And("^I switch on network$")
  public void switchOnNetwork() throws IOException {
    testWebDriver.sleep(2000);
    Runtime.getRuntime().exec("sudo ifconfig en1 up");
    testWebDriver.sleep(2000);
  }

  @Then("^I should see No facility selected$")
  public void shouldSeeNoFacilitySelected() throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyNoFacilitySelected();
  }

  @Then("^Navigate to EPI tab$")
  public void navigateToEpiTab() throws IOException {
    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.navigateToEPISUse();
  }

  @Then("^Verify indicator should be \"([^\"]*)\"$")
  public void shouldVerifyIndicatorColor(String color) throws IOException, SQLException {
    EPIUse epiUse = new EPIUse(testWebDriver);
    epiUse.verifyOverallEPIUseIcon(color);
  }

  @When("^I enter EPI end of month as \"([^\"]*)\"")
  public void enterEPIEndOfMonth(String endOfMonth) {
    new EPIUse(testWebDriver).enterValueInStockAtEndOfMonth(endOfMonth, 1);
  }

  @And("^I should see \"([^\"]*)\" facilities that support the program \"([^\"]*)\" and delivery zone \"([^\"]*)\"$")
  public void shouldSeeNoFacilitySelected(String active, String program, String deliveryZone) throws IOException, SQLException {
    boolean activeFlag = false;
    if (active.equalsIgnoreCase("active"))
      activeFlag = true;
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    List<String> valuesToBeVerified = dbWrapper.getFacilityCodeNameForDeliveryZoneAndProgram(deliveryZone, program, activeFlag);
    List<WebElement> facilityList = facilityListPage.getAllFacilitiesFromDropDown();
    verifyAllSelectFieldValues(valuesToBeVerified, facilityList);
  }

  @When("^I choose facility \"([^\"]*)\"$")
  public void selectFacility(String facilityCode) throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.selectFacility(facilityCode);
  }

  @And("^I should see Delivery Zone \"([^\"]*)\", Program \"([^\"]*)\" and Period \"([^\"]*)\" in the header$")
  public void shouldVerifyHeaderElements(String deliveryZone, String program, String period) throws IOException, SQLException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyHeaderElements(deliveryZone, program, period);
  }


  @And("^I click view load amount$")
  public void clickViewLoadAmount() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    distributionPage.clickViewLoadAmount();
  }

  @Then("^I should see data download successfully$")
  public void seeDownloadSuccessfully() throws IOException {
    DistributionPage distributionPage = new DistributionPage(testWebDriver);
    testWebDriver.sleep(1500);
    distributionPage.verifyDownloadSuccessFullMessage(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }

  @Then("^I should see \"([^\"]*)\" in the header$")
  public void verifyFacilityNameInHeader(String facilityName) throws IOException {
    FacilityListPage facilityListPage = new FacilityListPage(testWebDriver);
    facilityListPage.verifyFacilityNameInHeader(facilityName);
  }

  @And("^I should see delivery zone \"([^\"]*)\" program \"([^\"]*)\" period \"([^\"]*)\" in table$")
  public void verifyTableValue(String deliveryZoneNameFirst, String programFirst, String periodDisplayedByDefault) throws IOException {
    verifyElementsInTable(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }

  private void verifyElementsInTable(String deliveryZoneNameFirst, String programFirst, String periodDisplayedByDefault) {
    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]/" +
      "div[1]/div[1]/div").getText(), deliveryZoneNameFirst);

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[2]").getText(), programFirst);

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[3]").getText(), periodDisplayedByDefault);

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[4]").getText(), "INITIATED");

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[5]/a").getText(), "Record Data");

    SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//div[@id='cachedDistributions']/div[2]" +
      "/div[1]/div[6]/a").getText(), "Sync");
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyAlreadyCachedDistribution(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
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
    distributionPage.verifyDownloadSuccessFullMessage(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyDataAlreadyCachedMessage(deliveryZoneNameFirst, programFirst, periodDisplayedByDefault);
  }


  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testManageDistribution(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
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


    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    verifyElementsPresent(distributionPage);

    String defaultDistributionZoneValuesToBeVerified = NONE_ASSIGNED;
    WebElement actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromDeliveryZone();
    verifySelectedOptionFromSelectField(defaultDistributionZoneValuesToBeVerified, actualSelectFieldElement);

    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeFirst);
    dbWrapper.insertRoleAssignmentForDistribution(userSIC, "store in-charge", deliveryZoneCodeSecond);

    homePage.navigateHomePage();
    homePage.navigatePlanDistribution();

    List<String> distributionZoneValuesToBeVerified = new ArrayList<String>();
    distributionZoneValuesToBeVerified.add(deliveryZoneNameFirst);
    distributionZoneValuesToBeVerified.add(deliveryZoneNameSecond);
    List<WebElement> valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromDeliveryZone();
    verifyAllSelectFieldValues(distributionZoneValuesToBeVerified, valuesPresentInDropDown);

    String defaultProgramValuesToBeVerified = NONE_ASSIGNED;
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromProgram();
    verifySelectedOptionFromSelectField(defaultProgramValuesToBeVerified, actualSelectFieldElement);

    String defaultPeriodValuesToBeVerified = NONE_ASSIGNED;
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);


    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    List<String> firstProgramValuesToBeVerified = new ArrayList<String>();
    firstProgramValuesToBeVerified.add(programFirst);
    firstProgramValuesToBeVerified.add(programSecond);
    valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromProgram();
    verifyAllSelectFieldValues(firstProgramValuesToBeVerified, valuesPresentInDropDown);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);


    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameSecond);
    List<String> secondProgramValuesToBeVerified = new ArrayList<String>();
    secondProgramValuesToBeVerified.add(programSecond);
    valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromProgram();
    verifyAllSelectFieldValues(secondProgramValuesToBeVerified, valuesPresentInDropDown);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);


    distributionPage.selectValueFromProgram(programSecond);
    List<String> periodValuesToBeVerified = new ArrayList<String>();
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();

    verifySelectedOptionFromSelectField(periodDisplayedByDefault, actualSelectFieldElement);
    for (int counter = 2; counter <= totalNumberOfPeriods; counter++) {
      String periodWithCounter = period + counter;
      periodValuesToBeVerified.add(periodWithCounter);
    }
    valuesPresentInDropDown = distributionPage.getAllSelectOptionsFromPeriod();
    verifyAllSelectFieldValues(periodValuesToBeVerified, valuesPresentInDropDown);
    verifySelectFieldValueNotPresent(periodNotToBeDisplayedInDropDown, valuesPresentInDropDown);

    distributionPage.selectValueFromPeriod(periodDisplayedByDefault);

    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromDeliveryZone();
    verifySelectedOptionFromSelectField(deliveryZoneNameSecond, actualSelectFieldElement);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromProgram();
    verifySelectedOptionFromSelectField(programSecond, actualSelectFieldElement);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(periodDisplayedByDefault, actualSelectFieldElement);

    distributionPage.selectValueFromDeliveryZone(SELECT_DELIVERY_ZONE);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromProgram();
    verifySelectedOptionFromSelectField(defaultProgramValuesToBeVerified, actualSelectFieldElement);
    actualSelectFieldElement = distributionPage.getFirstSelectedOptionFromPeriod();
    verifySelectedOptionFromSelectField(defaultPeriodValuesToBeVerified, actualSelectFieldElement);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyNoFacilityToBeShownIfNotMappedWithDeliveryZone(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
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
    dbWrapper.deleteDeliveryZoneToFacilityMapping(deliveryZoneNameFirst);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyFacilityNotSupportedMessage(programFirst, deliveryZoneNameFirst);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyNoFacilityToBeShownIfNotMappedWithPrograms(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
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
    dbWrapper.deleteProgramToFacilityMapping(programFirst);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyFacilityNotSupportedMessage(programFirst, deliveryZoneNameFirst);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyNoFacilityToBeShownIfInactive(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
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
    dbWrapper.updateActiveStatusOfFacility(facilityCodeFirst, "false");
    dbWrapper.updateActiveStatusOfFacility(facilityCodeSecond, "false");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    DistributionPage distributionPage = homePage.navigatePlanDistribution();
    distributionPage.selectValueFromDeliveryZone(deliveryZoneNameFirst);
    distributionPage.selectValueFromProgram(programFirst);
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    distributionPage.verifyFacilityNotSupportedMessage(programFirst, deliveryZoneNameFirst);
  }

  @Test(groups = {"distribution"}, dataProvider = "Data-Provider-Function")
  public void testVerifyGeoZonesOrderOnFacilityListPage(String userSIC, String password, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                        String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                        String facilityCodeFirst, String facilityCodeSecond,
                                                        String programFirst, String programSecond, String schedule, String period, Integer totalNumberOfPeriods) throws Exception {

    String geoZoneFirst = "District1";
    String geoZoneSecond = "Ngorongoro";
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("MANAGE_DISTRIBUTION");
    setupTestDataToInitiateRnRAndDistribution("F10", "F11", true, programFirst, userSIC, "200", "openLmis", rightsList, programSecond, geoZoneFirst, geoZoneSecond, geoZoneSecond);
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
    distributionPage.selectValueFromPeriod(period + totalNumberOfPeriods);
    distributionPage.clickInitiateDistribution();
    FacilityListPage facilityListPage = distributionPage.clickRecordData();
    facilityListPage.clickFacilityListDropDown();
    facilityListPage.verifyGeographicZoneOrder(geoZoneFirst, geoZoneSecond);


  }


  private void verifyElementsPresent(DistributionPage distributionPage) {
    assertTrue("selectDeliveryZoneSelectBox should be present", distributionPage.IsDisplayedSelectDeliveryZoneSelectBox());
    assertTrue("selectProgramSelectBox should be present", distributionPage.IsDisplayedSelectProgramSelectBox());
    assertTrue("selectPeriodSelectBox should be present", distributionPage.IsDisplayedSelectPeriodSelectBox());
    assertTrue("proceedButton should be present", distributionPage.IsDisplayedViewLoadAmountButton());
  }


  private void verifyAllSelectFieldValues(List<String> valuesToBeVerified, List<WebElement> valuesPresentInDropDown) {
    String collectionOfValuesPresentINDropDown = "";
    int valuesToBeVerifiedCounter = valuesToBeVerified.size();
    int valuesInSelectFieldCounter = valuesPresentInDropDown.size();

    if (valuesToBeVerifiedCounter == valuesInSelectFieldCounter - 1) {
      for (WebElement webElement : valuesPresentInDropDown) {
        collectionOfValuesPresentINDropDown = collectionOfValuesPresentINDropDown + webElement.getText().trim();
      }
      for (String values : valuesToBeVerified) {
        assertTrue(collectionOfValuesPresentINDropDown.contains(values));
      }
    } else {
      fail("Values in select field are not same in number as values to be verified");
    }

  }


  private void verifySelectFieldValueNotPresent(String valueToBeVerified, List<WebElement> valuesPresentInDropDown) {
    boolean flag = false;
    for (WebElement webElement : valuesPresentInDropDown) {
      if (valueToBeVerified.equalsIgnoreCase(webElement.getText().trim())) {
        flag = true;
        break;
      }
    }
    assertTrue(valueToBeVerified + " should not exist in period drop down", flag == false);
  }


  private void verifySelectedOptionFromSelectField(String valuesToBeVerified, WebElement actualSelectFieldElement) {
    testWebDriver.sleep(200);
    testWebDriver.waitForElementToAppear(actualSelectFieldElement);
    assertEquals(valuesToBeVerified, actualSelectFieldElement.getText());
  }

  @AfterMethod(groups = "distribution")
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

