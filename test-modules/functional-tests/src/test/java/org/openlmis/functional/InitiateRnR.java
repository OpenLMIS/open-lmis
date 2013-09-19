/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ApprovePage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class InitiateRnR extends TestCaseHelper {

  public static final String STORE_IN_CHARGE = "store in-charge";
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String SUBMITTED = "SUBMITTED";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String IN_APPROVAL = "IN_APPROVAL";
  public static final String AUTHORIZE_REQUISITION = "AUTHORIZE_REQUISITION";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public String program, userSIC, categoryCode, password, regimenCode, regimenName, regimenCode2, regimenName2;

  public LoginPage loginPage;
  public InitiateRnRPage initiateRnRPage;

  @BeforeMethod(groups = "requisition")
  @Before
  public void setUp() throws Exception {
    super.setup();

  }

  @Given("^I have the following data for regimen:$")
  public void theFollowingDataExistForRegimen(DataTable data) throws Exception {
    List<String> dataString = data.flatten();
    program = dataString.get(0);
    userSIC = dataString.get(1);
    categoryCode = dataString.get(2);
    regimenCode = dataString.get(3);
    regimenName = dataString.get(4);
    regimenCode2 = dataString.get(5);
    regimenName2 = dataString.get(6);
  }

  @Given("^I have regimen template configured$")
  public void configureRegimenTemplate() throws IOException, SQLException {
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
  }

  @Given("^I access initiate requisition page$")
  public void onInitiateRnRScreen() throws IOException, SQLException {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateAndInitiateRnr(program);
  }

  @Given("^I access initiate emergency requisition page$")
  public void onInitiateEmergencyRnRScreen() throws IOException, SQLException {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateAndInitiateEmergencyRnr(program);
  }

  @Then ("I should see no period available$")
  public void verifyPeriodNotAvailable()throws IOException{
      HomePage homePage = new HomePage(testWebDriver);
      assertEquals("No period(s) available",homePage.getFirstPeriod());
  }

  @Given("I have \"([^\"]*)\" user with \"([^\"]*)\" rights and data to initiate requisition$")
  public void setupUserWithRightsAndInitiateRequisitionData(String user, String rights) throws IOException, SQLException {
    String[] rightList = rights.split(",");
    List<String> rightsList = new ArrayList<String>();
    for (int i = 0; i < rightList.length; i++)
      rightsList.add(rightList[i]);
    setupTestDataToInitiateRnR(true, program, user, "200", "openLmis", rightsList);
  }

  @When("^I click proceed$")
  public void clickOnProceed() throws IOException {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    testWebDriver.sleep(2000);
  }

  @When("^I populate RnR data$")
  public void enterValuesFromDB() throws IOException, SQLException {
    dbWrapper.insertValuesInRequisition();
  }

  @When("^I access regimen tab$")
  public void clickRegimenTab() throws IOException, SQLException {
    initiateRnRPage.clickRegimenTab();
  }

  @Then("^I should see regimen fields$")
  public void shouldSeeRegimenFields() {
    verifyRegimenFieldsPresentOnRegimenTab(regimenCode, regimenName, initiateRnRPage);
  }

  @When("^I type patients on treatment \"([^\"]*)\"$")
  public void typePatientsOnTreatment(String value) {
    initiateRnRPage.enterValuesOnRegimenScreen(3, 2, value);
  }

  @When("^I type patients initiated treatment \"([^\"]*)\"$")
  public void typePatientsInitiatedTreatment(String value) {
    initiateRnRPage.enterValuesOnRegimenScreen(4, 2, value);
  }

  @When("^I type patients stopped treatment \"([^\"]*)\"$")
  public void typePatientsStoppedTreatment(String value) {
    initiateRnRPage.enterValuesOnRegimenScreen(5, 2, value);
  }

  @When("^I type remarks \"([^\"]*)\"$")
  public void typeRemarks(String value) {
    initiateRnRPage.enterValuesOnRegimenScreen(6, 2, value);
  }

  @When("^I click save$")
  public void clickSave() {
    initiateRnRPage.clickSaveButton();
  }

  @When("^I should see saved successfully$")
  public void shouldSeeSavedSuccessfully() {
    initiateRnRPage.verifySaveSuccessMsg();
  }

  @When("^I click submit$")
  public void clickSubmit() {
    initiateRnRPage.clickSubmitButton();
    testWebDriver.sleep(250);
  }

  @When("^I click ok$")
  public void clickOk() {
    testWebDriver.sleep(1000);
    initiateRnRPage.clickOk();

  }

  @When("^I should see submit successfully$")
  public void shouldSeeSubmitSuccessfully() {
    initiateRnRPage.verifySubmitSuccessMsg();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testSubmitAndAuthorizeRegimen(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.insertValuesInRequisition();
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage1 = homePage.clickProceed();
    initiateRnRPage1.clickRegimenTab();

    verifyRegimenFieldsPresentOnRegimenTab(regimenCode, regimenName, initiateRnRPage);
    initiateRnRPage1.enterValuesOnRegimenScreen(3, 2, "100");
    initiateRnRPage1.enterValuesOnRegimenScreen(4, 2, "200");
    initiateRnRPage1.enterValuesOnRegimenScreen(6, 2, "400");

    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.verifySubmitRnrErrorMsg();
    initiateRnRPage1.enterValuesOnRegimenScreen(5, 2, "300");
    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.clickOk();
    initiateRnRPage1.verifySubmitSuccessMsg();

    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage2 = homePage.clickProceed();
    initiateRnRPage2.clickRegimenTab();
    verifyValuesOnAuthorizeRegimenScreen(initiateRnRPage2, "100", "200", "300", "400");
    initiateRnRPage2.clickAuthorizeButton();
    initiateRnRPage2.clickOk();
    initiateRnRPage2.verifyAuthorizeRnrSuccessMsg();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testApproveRegimen(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.insertValuesInRequisition();
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "testing");
    dbWrapper.updateRequisitionStatus(SUBMITTED);
    dbWrapper.insertApprovedQuantity(10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED);

    ApprovePage approvePageLowerSNUser = homePage.navigateToApprove();
    approvePageLowerSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageLowerSNUser.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage, "100", "200", "300", "testing");
    approvePageLowerSNUser.clickSaveButton();
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.clickOk();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testApproveRegimenWithoutSave(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.insertValuesInRequisition();
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "testing");
    dbWrapper.updateRequisitionStatus(SUBMITTED);
    dbWrapper.insertApprovedQuantity(10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED);

    ApprovePage approvePageLowerSNUser = homePage.navigateToApprove();
    approvePageLowerSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageLowerSNUser.editApproveQuantity("");
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.editApproveQuantity("100");
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.clickOk();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInvisibleProgramRegimenColumn(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.updateProgramRegimenColumns(program, "remarks", false);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    initiateRnRPage.clickRegimenTab();
    assertEquals(initiateRnRPage.getRegimenTableColumnCount(), 6);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInActiveRegimen(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, false);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    assertFalse("Regimen tab should not be displayed.", initiateRnRPage.existRegimenTab());
  }

  private void verifyRegimenFieldsPresentOnRegimenTab(String regimenCode, String regimenName, InitiateRnRPage initiateRnRPage) {
    assertTrue("Regimen tab should be displayed.", initiateRnRPage.existRegimenTab());
    assertEquals(initiateRnRPage.getRegimenTableRowCount(), 2);

    assertTrue("Regimen Code should be displayed.", initiateRnRPage.existRegimenCode(regimenCode, 2));
    assertTrue("Regimen Name should be displayed.", initiateRnRPage.existRegimenName(regimenName, 2));

    assertTrue("Reporting Field 1 should be displayed.", initiateRnRPage.existRegimenReportingField(3, 2));
    assertTrue("Reporting Field 2 should be displayed.", initiateRnRPage.existRegimenReportingField(4, 2));
    assertTrue("Reporting Field 3 should be displayed.", initiateRnRPage.existRegimenReportingField(5, 2));
    assertTrue("Reporting Field 4 should be displayed.", initiateRnRPage.existRegimenReportingField(6, 2));
  }

  private void verifyValuesOnRegimenScreen(InitiateRnRPage initiateRnRPage, String patientsontreatment, String patientstoinitiatetreatment, String patientsstoppedtreatment, String remarks) {
    assertEquals(patientsontreatment, initiateRnRPage.getPatientsOnTreatmentValue());
    assertEquals(patientstoinitiatetreatment, initiateRnRPage.getPatientsToInitiateTreatmentValue());
    assertEquals(patientsstoppedtreatment, initiateRnRPage.getPatientsStoppedTreatmentValue());
    assertEquals(remarks, initiateRnRPage.getRemarksValue());
  }

  private void verifyValuesOnAuthorizeRegimenScreen(InitiateRnRPage initiateRnRPage, String patientsontreatment, String patientstoinitiatetreatment, String patientsstoppedtreatment, String remarks) {
    assertEquals(patientsontreatment, initiateRnRPage.getPatientsOnTreatmentInputValue());
    assertEquals(patientstoinitiatetreatment, initiateRnRPage.getPatientsToInitiateTreatmentInputValue());
    assertEquals(patientsstoppedtreatment, initiateRnRPage.getPatientsStoppedTreatmentInputValue());
    assertEquals(remarks, initiateRnRPage.getRemarksInputValue());
  }


  @AfterMethod(groups = "requisition")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }

  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}
    };


  }
}

