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


import com.thoughtworks.selenium.SeleneseTestBase;
import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;
import static java.lang.Math.round;
import static java.util.Arrays.asList;
import static org.openlmis.UiUtils.DBWrapper.DEFAULT_MAX_MONTH_OF_STOCK;

@Listeners(CaptureScreenshotOnFailureListener.class)
public class InitiateRnR extends TestCaseHelper {

  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String CREATE_REQUISITION = "CREATE_REQUISITION";
  public static final String SUBMITTED = "SUBMITTED";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String AUTHORIZE_REQUISITION = "AUTHORIZE_REQUISITION";
  public static final String VIEW_REQUISITION = "VIEW_REQUISITION";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  private static final int MILLISECONDS_IN_ONE_DAY = 24 * 60 * 60 * 1000;
  public String program, userSIC, categoryCode, password, regimenCode, regimenName, regimenCode2, regimenName2;

  public LoginPage loginPage;
  public InitiateRnRPage initiateRnRPage;

  @BeforeMethod(groups = "requisition")
  @Before
  public void setUp() throws Exception {
    super.setup();
    dbWrapper.deleteData();
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

  @Then("I should see no period available$")
  public void verifyPeriodNotAvailable() throws IOException {
    HomePage homePage = new HomePage(testWebDriver);
    assertEquals("No current period defined. Please contact the Admin.", homePage.getFirstPeriod());
  }

  @Then("^I should verify \"([^\"]*)\" with status \"([^\"]*)\" in row \"([^\"]*)\"$")
  public void verifyPeriodNotAvailable(String period, String status, String row) throws IOException {
    verifyRnRsInGrid(period, status, row);
  }

  @Given("I have \"([^\"]*)\" user with \"([^\"]*)\" rights and data to initiate requisition$")
  public void setupUserWithRightsAndInitiateRequisitionData(String user,
                                                            String rights) throws Exception {
    String[] rightList = rights.split(",");

    setupTestDataToInitiateRnR(true, program, user, "200", asList(rightList));
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
    dbWrapper.insertValuesInRequisition(false);
  }

  @When("^I access regimen tab$")
  public void clickRegimenTab() throws IOException, SQLException {
    initiateRnRPage.clickRegimenTab();
  }

  @When("^I enter beginning balance \"([^\"]*)\"$")
  public void enterBeginningBalance(String beginningBalance) throws IOException, SQLException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterBeginningBalanceForFirstProduct(valueOf(beginningBalance));
  }

  @When("^I enter quantity received \"([^\"]*)\"$")
  public void enterQuantityReceived(String quantityReceived) throws IOException, SQLException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(valueOf(quantityReceived));
  }

  @When("^I enter quantity dispensed \"([^\"]*)\"$")
  public void enterQuantityDispensed(String quantityDispensed) throws IOException, SQLException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(valueOf(quantityDispensed));
  }

  @Then("^I validate beginning balance \"([^\"]*)\"$")
  public void validateBeginningBalance(String beginningBalance) throws IOException, SQLException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyBeginningBalanceForFirstProduct(parseInt(beginningBalance));
  }

  @Then("^I validate quantity received \"([^\"]*)\"$")
  public void validateQuantityReceived(String quantityReceived) throws IOException, SQLException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyQuantityReceivedForFirstProduct(parseInt(quantityReceived));
  }

  @Then("^I validate quantity dispensed \"([^\"]*)\"$")
  public void validateQuantityDispensed(String quantityDispensed) throws IOException, SQLException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyQuantityDispensedForFirstProduct(parseInt(quantityDispensed));
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
  public void clickSubmit() throws IOException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.clickSubmitButton();
    testWebDriver.sleep(250);
  }

  @When("^I click ok$")
  public void clickOk() throws IOException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    testWebDriver.sleep(1000);
    initiateRnRPage.clickOk();

  }

  @When("^I should see submit successfully$")
  public void shouldSeeSubmitSuccessfully() {
    initiateRnRPage.verifySubmitSuccessMsg();
  }

  @Then("^I got error message \"([^\"]*)\"$")
  public void shouldSeeSubmitSuccessfully(String errorMsg) throws IOException {
    HomePage homePage = new HomePage(testWebDriver);
    assertEquals(homePage.getErrorMessage(), errorMsg);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testSubmitAndAuthorizeRegimen(String program, String userSIC, String categoryCode, String password,
                                            String regimenCode, String regimenName, String regimenCode2,
                                            String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.insertValuesInRequisition(false);
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
  public void testApproveRegimen(String program, String userSIC, String categoryCode, String password,
                                 String regimenCode, String regimenName, String regimenCode2,
                                 String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "testing");
    dbWrapper.updateRequisitionStatus(SUBMITTED, userSIC, "HIV");
    dbWrapper.insertApprovedQuantity(10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED, userSIC, "HIV");

    ApprovePage approvePageLowerSNUser = homePage.navigateToApprove();
    approvePageLowerSNUser.ClickRequisitionPresentForApproval();
    approvePageLowerSNUser.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage, "100", "200", "300", "testing");
    approvePageLowerSNUser.clickSaveButton();
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.clickOk();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testApproveRegimenWithoutSave(String program, String userSIC, String categoryCode, String password,
                                            String regimenCode, String regimenName, String regimenCode2,
                                            String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();

    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "testing");
    dbWrapper.updateRequisitionStatus(SUBMITTED, userSIC, "HIV");
    dbWrapper.insertApprovedQuantity(10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED, userSIC, "HIV");

    ApprovePage approvePageLowerSNUser = homePage.navigateToApprove();
    approvePageLowerSNUser.ClickRequisitionPresentForApproval();
    approvePageLowerSNUser.editFullSupplyApproveQuantity("");
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.editFullSupplyApproveQuantity("100");
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.clickOk();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInvisibleProgramRegimenColumn(String program, String userSIC, String categoryCode,
                                                       String password, String regimenCode, String regimenName,
                                                       String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
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
    SeleneseTestBase.assertEquals(initiateRnRPage.getRegimenTableColumnCount(), 6);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInActiveRegimen(String program, String userSIC, String categoryCode, String password,
                                         String regimenCode, String regimenName, String regimenCode2,
                                         String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
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

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageIfPeriodNotDefinedForRegularType(String program, String userSIC,
                                                                  String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period2");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    verifyErrorMessages("No current period defined. Please contact the Admin.");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageWhileAuthorizingForRegularType(String program, String userSIC,
                                                                String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period2");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    verifyErrorMessages("No current period defined. Please contact the Admin.");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageWhileAuthorizingForEmergencyType(String program, String userSIC,
                                                                  String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyErrorMessages("No current period defined. Please contact the Admin.");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageIfPeriodNotDefinedForEmergencyType(String program, String userSIC,
                                                                    String password
  ) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyErrorMessages("No current period defined. Please contact the Admin.");
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void shouldVerifyRequisitionAlreadySubmittedMessage(String program,
                                                             String userSIC,
                                                             String password
  ) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage1 = homePage.clickProceed();
    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.clickOk();

    initiateRnRPage1.clickAuthorizeButton();
    initiateRnRPage1.clickOk();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");

    verifyErrorMessages("R&R for current period already submitted");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testValidRnRSubmittedAuthorizedViewAndVerifyStateOfFields(String program, String userSIC,
                                                                        String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);

    List<String> rightsList1 = new ArrayList<>();
    rightsList1.add(AUTHORIZE_REQUISITION);
    rightsList1.add(VIEW_REQUISITION);
    createUserAndAssignRoleRights("201", "mo", "Maar_Doe@openlmis.com", "F10", "district pharmacist",
      rightsList1);

    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(100);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(100);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(100);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyRnRsInGrid("current Period", "Not yet started", "1");
    verifyRnRsInGrid("current Period", "INITIATED", "2");
    InitiateRnRPage initiateRnRPage1 = homePage.clickProceed();
    initiateRnRPage1.enterBeginningBalanceForFirstProduct(100);
    initiateRnRPage1.enterQuantityReceivedForFirstProduct(100);
    initiateRnRPage1.enterQuantityDispensedForFirstProduct(100);
    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.clickOk();

    initiateRnRPage1.verifyBeginningBalanceForFirstProduct(100);
    initiateRnRPage1.verifyQuantityReceivedForFirstProduct(100);
    initiateRnRPage1.verifyQuantityDispensedForFirstProduct(100);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyRnRsInGrid("current Period", "Not yet started", "1");
    verifyRnRsInGrid("current Period", "INITIATED", "3");
    verifyRnRsInGrid("current Period", "SUBMITTED", "2");

    homePage.logout(baseUrlGlobal);


    HomePage homePage1 = loginPage.loginAs("mo", password);
    homePage1.navigateAndInitiateEmergencyRnr(program);

    verifyRnRsInGrid("current Period", "Not yet started", "1");
    verifyRnRsInGrid("current Period", "INITIATED", "3");
    verifyRnRsInGrid("current Period", "SUBMITTED", "2");

    clickProceed(1);
    verifyErrorMessages("Requisition not initiated yet");

    clickProceed(3);
    verifyErrorMessages("Requisition not submitted yet");

    clickProceed(2);
    initiateRnRPage1.clickAuthorizeButton();
    initiateRnRPage1.clickOk();
    initiateRnRPage1.verifyAuthorizeRnrSuccessMsg();

    homePage1.navigateAndInitiateEmergencyRnr(program);

    verifyRnRsInGrid("current Period", "Not yet started", "1");
    verifyRnRsInGrid("current Period", "INITIATED", "2");

    homePage1.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    clickProceed(1);
    verifyErrorMessages("Requisition not initiated yet");

    ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.verifyEmergencyStatus();
    viewRequisitionPage.verifyStatus("AUTHORIZED");

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testEmergencyRnRApprovedAndConvertedToOrder(String program,
                                                          String userSIC,
                                                          String password) throws Exception {

    String userName = "lmuInCharge";
    String roleName = "lmuInCharge";

    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);

    List<String> rightsList1 = asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION);
    createUserAndAssignRoleRights("201", "mo", "Maar_Doe@openlmis.com", "F10", "district pharmacist", rightsList1);

    List<String> rightsList2 = asList(APPROVE_REQUISITION, VIEW_REQUISITION);
    createUserAndAssignRoleRights("301", "lmu", "Maafi_De_Doe@openlmis.com", "F10", "lmu", rightsList2);

    List<String> rightsList3 = asList(CONVERT_TO_ORDER, VIEW_ORDER);
    createUserAndAssignRoleRights("401", userName, "Jaan_V_Doe@openlmis.com", "F10", roleName, rightsList3);

    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    String periodStartDate = "2013-10-03";
    String periodEndDate = "2014-01-30";
    dbWrapper.insertProcessingPeriod("current Period", "current Period", periodStartDate, periodEndDate, 1, "M");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    Integer quantityDispensed = 100;
    Integer beginningBalance = 100;
    Integer quantityReceived = 100;

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage1 = homePage.clickProceed();
    initiateRnRPage1.enterBeginningBalanceForFirstProduct(beginningBalance);
    initiateRnRPage1.enterQuantityReceivedForFirstProduct(quantityReceived);
    initiateRnRPage1.enterQuantityDispensedForFirstProduct(quantityDispensed);
    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.clickOk();
    initiateRnRPage1.verifyBeginningBalanceForFirstProduct(beginningBalance);
    initiateRnRPage1.verifyQuantityReceivedForFirstProduct(quantityReceived);
    initiateRnRPage1.verifyQuantityDispensedForFirstProduct(quantityDispensed);
    homePage.logout(baseUrlGlobal);


    HomePage homePage1 = loginPage.loginAs("mo", password);
    homePage1.navigateAndInitiateEmergencyRnr(program);

    clickProceed(2);
    initiateRnRPage1.clickAuthorizeButton();
    initiateRnRPage1.clickOk();

    homePage1.logout(baseUrlGlobal);

    HomePage homePage2 = loginPage.loginAs("lmu", password);
    ApprovePage approvePage = homePage2.navigateToApprove();
    approvePage.verifyEmergencyStatus();
    approvePage.ClickRequisitionPresentForApproval();

    int reportingDays = calculateReportingDays(periodStartDate);
    int stockOutDays = 0;
    int noOfDaysInOnePeriod = 30;
    Integer normalizedConsumption = round(quantityDispensed * ((float) noOfDaysInOnePeriod / (reportingDays - stockOutDays)));
    Integer AMC = normalizedConsumption / 1;
    Integer maxStockQuantity = DEFAULT_MAX_MONTH_OF_STOCK * AMC;
    Integer stockInHand = beginningBalance + quantityReceived - quantityDispensed;
    Integer calculatedOrderQuantity = maxStockQuantity - stockInHand;

    assertEquals(calculatedOrderQuantity.toString(), approvePage.getApprovedQuantity());
    assertEquals(normalizedConsumption.toString(), approvePage.getAdjustedTotalConsumption());
    assertEquals(AMC.toString(), approvePage.getAMC());
    assertEquals(maxStockQuantity.toString(), approvePage.getMaxStockQuantity());
    assertEquals(calculatedOrderQuantity.toString(), approvePage.getCalculatedOrderQuantity());
    approvePage.editFullSupplyApproveQuantity("");
    approvePage.approveRequisition();
    approvePage.verifyApproveErrorDiv();
    approvePage.editFullSupplyApproveQuantity("0");
    approvePage.approveRequisition();
    approvePage.clickOk();
    approvePage.verifyNoRequisitionPendingMessage();

    homePage2.logout(baseUrlGlobal);

    HomePage homePage3 = loginPage.loginAs(userName, password);
    ConvertOrderPage convertOrderPage = homePage3.navigateConvertToOrder();
    convertOrderPage.verifyNoRequisitionPendingMessage();

    ViewOrdersPage viewOrdersPage = homePage3.navigateViewOrders();
    viewOrdersPage.verifyNoRequisitionReleasedAsOrderMessage();

    dbWrapper.insertFulfilmentRoleAssignment(userName, roleName, "F10");
    homePage3.navigateHomePage();
    homePage3.navigateConvertToOrder();
    convertOrderPage.verifyOrderListElements(program, "F10", "Village Dispensary", "03/10/2013", "30/01/2014", "Village Dispensary");
    convertOrderPage.convertToOrder();

    homePage3.navigateHomePage();
    ViewOrdersPage viewOrdersPage1 = homePage3.navigateViewOrders();
    viewOrdersPage1.isFirstRowPresent();

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testValidationsOnStockOnHandRnRField(String program,
                                                   String userSIC,
                                                   String password
  ) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(100);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(0);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(1000);
    verifyStockOnHandErrorMessage();

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testValidationsOnTotalConsumedQuantityRnRField(String program,
                                                             String userSIC,
                                                             String password
  ) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    dbWrapper.updateSourceOfAProgramTemplate("HIV", "Total Consumed Quantity", "C");
    dbWrapper.updateSourceOfAProgramTemplate("HIV", "Stock on Hand", "U");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(100);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(0);
    initiateRnRPage.enterStockOnHandForFirstProduct(1000);
    verifyTotalQuantityConsumedErrorMessage();
    initiateRnRPage.verifyStockOnHandForFirstProduct("1000");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyAllStatusOfRequisitions(String program,
                                                String userSIC,
                                                String password
  ) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(100);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(100);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(100);
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitRnrSuccessMsg();
    initiateRnRPage.verifyAllFieldsDisabled();
    initiateRnRPage.verifySaveButtonDisabled();
    initiateRnRPage.calculateAndVerifyTotalCost();

  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageForSubmitterWithRequisitionAlreadyAuthorized(String program,
                                                                              String userSIC,
                                                                              String password
  ) throws Exception {
    setupTestDataToInitiateRnR(true, program, userSIC, "200", asList(CREATE_REQUISITION, VIEW_REQUISITION));

    createUserAndAssignRoleRights("201", "mo", "Maar_Doe@openlmis.com", "F10", "district pharmacist",
      asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION));

    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage1 = homePage.clickProceed();
    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.clickOk();

    homePage.logout(baseUrlGlobal);


    HomePage homePage1 = loginPage.loginAs("mo", password);
    homePage1.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage2 = homePage1.clickProceed();

    initiateRnRPage2.clickAuthorizeButton();
    initiateRnRPage2.clickOk();

    homePage1.logout(baseUrlGlobal);
    HomePage homePage2 = loginPage.loginAs(userSIC, password);
    homePage2.navigateViewRequisition();
    homePage2.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    testWebDriver.refresh();
    testWebDriver.sleep(1000);

    testWebDriver.selectByVisibleText(testWebDriver.getElementByXpath("//select[@id='programListMyFacility']"),
      program);

    verifyErrorMessages("R&R for current period already submitted");

    homePage2.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage2.clickProceed();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void shouldVerifyNoCurrentPeriodDefinedMessage(String program,
                                                        String userSIC,
                                                        String password) throws Exception {

    List<String> rightsList = new ArrayList<>();
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);


    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");

    String errorMessage = "No current period defined. Please contact the Admin.";
    verifyErrorMessages(errorMessage);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyErrorMessages(errorMessage);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRBeginningBalance(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.DeleteProcessingPeriods();
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "M");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();

    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "storeIncharge", "HIV");

    dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2016-01-30", 1, "M");
    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateEmergencyRnr(program);
    homePage.clickProceed();

    assertEquals(initiateRnRPage.getBeginningBalance(), "1");
    testWebDriver.sleep(2000);

    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    assertEquals(initiateRnRPage.getBeginningBalance(), "1");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRestrictVirtualFacilityFromRnRScreen(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.updateVirtualPropertyOfFacility("F10", "True");
    dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId1("200", "store in-charge", "N1");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnrForSupervisedFacility(program);
    String str = homePage.getFacilityDropDownList();
    assertFalse(str.contains("F10"));

  }


  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testSkipProductRnRField(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    dbWrapper.UpdateProductFullSupplyStatus("P11", true);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterRequestedQuantityForFirstProduct(100);
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.verifyCostOnFooter();

    initiateRnRPage.skipSingleProduct(1);
    initiateRnRPage.verifyAllFieldsDisabled();
    initiateRnRPage.calculateAndVerifyTotalCost();
    assertEquals(initiateRnRPage.getTotalCostFooter(), "0.00");
    assertEquals(initiateRnRPage.getFullySupplyCostFooter(), "0.00");

    initiateRnRPage.skipSingleProduct(1);
    assertTrue(initiateRnRPage.isEnableBeginningBalanceForFirstProduct());
    initiateRnRPage.calculateAndVerifyTotalCost();

    initiateRnRPage.skipAllProduct();
    initiateRnRPage.verifyAllFieldsDisabled();
    assertEquals(initiateRnRPage.getTotalCostFooter(), "0.00");
    assertEquals(initiateRnRPage.getFullySupplyCostFooter(), "0.00");

    initiateRnRPage.unSkipAllProduct();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(10);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(0);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(0);
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.enterBeginningBalanceForSecondProduct(10);
    initiateRnRPage.enterQuantityReceivedForSecondProduct(0);
    initiateRnRPage.enterQuantityDispensedForSecondProduct(0);
    initiateRnRPage.enterRequestedQuantityForSecondProduct(100);
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitRnrSuccessMsg();
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.verifyCostOnFooter();
    assertEquals("125.0", dbWrapper.getRequisitionFullSupplyItemsSubmittedCost(dbWrapper.getMaxRnrID()));
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testSkipProductRnRAuthorizeApproveForRegularRnR(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    dbWrapper.UpdateProductFullSupplyStatus("P11", true);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(10);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(0);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(0);
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.enterBeginningBalanceForSecondProduct(10);
    initiateRnRPage.enterQuantityReceivedForSecondProduct(0);
    initiateRnRPage.enterQuantityDispensedForSecondProduct(0);
    initiateRnRPage.enterRequestedQuantityForSecondProduct(100);
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitRnrSuccessMsg();
    initiateRnRPage.skipAllProduct();
    initiateRnRPage.verifyAllFieldsDisabled();
    initiateRnRPage.calculateAndVerifyTotalCost();
    assertEquals(initiateRnRPage.getTotalCostFooter(), "0.00");
    assertEquals(initiateRnRPage.getFullySupplyCostFooter(), "0.00");
    initiateRnRPage.unSkipAllProduct();
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.verifyCostOnFooter();
    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.ClickRequisitionPresentForApproval();
    assertTrue(approvePage.approveQuantityVisible(1));
    approvePage.editFullSupplyApproveQuantity("5");
    assertFalse(approvePage.approveQuantityVisible(2));
    approvePage.clickApproveButton();
    approvePage.clickOk();
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testSkipProductRnRAuthorizeApproveForEmergencyRnR(String program, String userSIC, String password) throws Exception {
    List<String> rightsList = new ArrayList<>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
    rightsList.add(AUTHORIZE_REQUISITION);
    rightsList.add(APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.deletePeriod("Period1");
    dbWrapper.deletePeriod("Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2014-01-30", 1, "M");
    dbWrapper.UpdateProductFullSupplyStatus("P11", true);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterBeginningBalanceForFirstProduct(10);
    initiateRnRPage.enterQuantityReceivedForFirstProduct(0);
    initiateRnRPage.enterQuantityDispensedForFirstProduct(0);
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.enterBeginningBalanceForSecondProduct(10);
    initiateRnRPage.enterQuantityReceivedForSecondProduct(0);
    initiateRnRPage.enterQuantityDispensedForSecondProduct(0);
    initiateRnRPage.enterRequestedQuantityForSecondProduct(100);
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitRnrSuccessMsg();
    initiateRnRPage.skipAllProduct();
    initiateRnRPage.verifyAllFieldsDisabled();
    initiateRnRPage.calculateAndVerifyTotalCost();
    assertEquals(initiateRnRPage.getTotalCostFooter(), "0.00");
    assertEquals(initiateRnRPage.getFullySupplyCostFooter(), "0.00");
    initiateRnRPage.unSkipAllProduct();
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.verifyCostOnFooter();
    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.ClickRequisitionPresentForApproval();
    assertTrue(approvePage.approveQuantityVisible(1));
    approvePage.editFullSupplyApproveQuantity("5");
    assertFalse(approvePage.approveQuantityVisible(2));
    approvePage.clickApproveButton();
    approvePage.clickOk();
    approvePage.verifyNoRequisitionPendingMessage();
  }

  private void verifyRegimenFieldsPresentOnRegimenTab(String regimenCode, String regimenName,
                                                      InitiateRnRPage initiateRnRPage) {
    assertTrue("Regimen tab should be displayed.", initiateRnRPage.existRegimenTab());
    assertEquals(initiateRnRPage.getRegimenTableRowCount(), 2);

    assertTrue("Regimen Code should be displayed.", initiateRnRPage.existRegimenCode(regimenCode, 2));
    assertTrue("Regimen Name should be displayed.", initiateRnRPage.existRegimenName(regimenName, 2));

    assertTrue("Reporting Field 1 should be displayed.", initiateRnRPage.existRegimenReportingField(3, 2));
    assertTrue("Reporting Field 2 should be displayed.", initiateRnRPage.existRegimenReportingField(4, 2));
    assertTrue("Reporting Field 3 should be displayed.", initiateRnRPage.existRegimenReportingField(5, 2));
    assertTrue("Reporting Field 4 should be displayed.", initiateRnRPage.existRegimenReportingField(6, 2));
  }

  private void verifyErrorMessages(String message) {
    testWebDriver.sleep(500);
    assertTrue("Message : " + message + " should show up. Showing : " + testWebDriver.getElementByXpath(
      "//div[@id='saveSuccessMsgDiv' and @class='alert alert-error']").getText(),
      testWebDriver.getElementByXpath(
        "//div[@id='saveSuccessMsgDiv' and @class='alert alert-error']").getText().equalsIgnoreCase(
        message));
  }

  private void verifyValuesOnRegimenScreen(InitiateRnRPage initiateRnRPage, String patientsOnTreatment,
                                           String patientsToInitiateTreatment, String patientsStoppedTreatment,
                                           String remarks) {
    assertEquals(patientsOnTreatment, initiateRnRPage.getPatientsOnTreatmentValue());
    assertEquals(patientsToInitiateTreatment,
      initiateRnRPage.getPatientsToInitiateTreatmentValue());
    assertEquals(patientsStoppedTreatment, initiateRnRPage.getPatientsStoppedTreatmentValue());
    assertEquals(remarks, initiateRnRPage.getRemarksValue());
  }

  private void verifyValuesOnAuthorizeRegimenScreen(InitiateRnRPage initiateRnRPage, String patientsOnTreatment,
                                                    String patientsToInitiateTreatment,
                                                    String patientsStoppedTreatment, String remarks) {
    assertEquals(patientsOnTreatment, initiateRnRPage.getPatientsOnTreatmentInputValue());
    assertEquals(patientsToInitiateTreatment,
      initiateRnRPage.getPatientsToInitiateTreatmentInputValue());
    assertEquals(patientsStoppedTreatment,
      initiateRnRPage.getPatientsStoppedTreatmentInputValue());
    assertEquals(remarks, initiateRnRPage.getRemarksInputValue());
  }

  private void verifyRnRsInGrid(String period, String rnrStatus, String row) {
    assertEquals(testWebDriver.getElementByXpath(
      "//div[@class='ngCanvas']/div[" + row + "]/div[1]/div[2]/div/span").getText(), period);
    assertEquals(testWebDriver.getElementByXpath(
      "//div[@class='ngCanvas']/div[" + row + "]/div[4]/div[2]/div/span").getText(), rnrStatus);
  }

  private void verifyStockOnHandErrorMessage() {
    testWebDriver.waitForPageToLoad();
    assertTrue("Error message 'verifyStockOnHandErrorMessage' should show up", testWebDriver.getElementByXpath("//span[contains(text(),'Stock On Hand is calculated to be negative, please validate entries')]").isDisplayed());
  }

  private void verifyTotalQuantityConsumedErrorMessage() {
    testWebDriver.waitForPageToLoad();
    assertTrue("Error message 'verifyStockOnHandErrorMessage' should show up", testWebDriver.getElementByXpath("//span[contains(text(),'Total Quantity Consumed is calculated to be negative, please validate entries')]").isDisplayed());
  }

  private void clickProceed(int row) {
    testWebDriver.waitForElementToAppear(testWebDriver.getElementByXpath("(//input[@value='Proceed'])[" + row + "]"));
    testWebDriver.getElementByXpath("(//input[@value='Proceed'])[" + row + "]").click();
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
      {"HIV", "storeIncharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}
    };
  }

  @DataProvider(name = "Data-Provider-Function-RnR")
  public Object[][] parameterIntTestProviderRnR() {
    return new Object[][]{
      {"HIV", "storeIncharge", "Admin123"}
    };
  }

  private int calculateReportingDays(String periodStartString) throws ParseException {
    Date currentDate = new Date();
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date periodStartDate = formatter.parse(periodStartString);
    return (int) ((currentDate.getTime() - periodStartDate.getTime()) / MILLISECONDS_IN_ONE_DAY);
  }

}

