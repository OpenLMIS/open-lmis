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
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.By;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

  public static final String MANAGE_POD = "MANAGE_POD";
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
  HomePage homePage;

  @BeforeMethod(groups = "requisition")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.deleteData();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    homePage = PageObjectFactory.getHomePage(testWebDriver);
  }

  @Given("^I have the following data for regimen:$")
  public void theFollowingDataExistForRegimen(DataTable data) {
    List<String> dataString = data.asList(String.class);
    program = dataString.get(0);
    userSIC = dataString.get(1);
    categoryCode = dataString.get(2);
    regimenCode = dataString.get(3);
    regimenName = dataString.get(4);
    regimenCode2 = dataString.get(5);
    regimenName2 = dataString.get(6);
  }

  @Given("^I have regimen template configured$")
  public void configureRegimenTemplate() throws SQLException {
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
  }

  @Given("^I access initiate requisition page$")
  public void onInitiateRnRScreen() {
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateAndInitiateRnr(program);
  }

  @Given("^I access initiate emergency requisition page$")
  public void onInitiateEmergencyRnRScreen() {
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateAndInitiateEmergencyRnr(program);
  }

  @Then("I should see no period available$")
  public void verifyPeriodNotAvailable() {
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    assertEquals("No current period defined. Please contact the Admin.", homePage.getFirstPeriod());
  }

  @Then("^I should verify \"([^\"]*)\" with status \"([^\"]*)\" in row \"([^\"]*)\"$")
  public void verifyPeriodNotAvailable(String period, String status, String row) {
    verifyRnRsInGrid(period, status, row);
  }

  @Given("I have \"([^\"]*)\" user with \"([^\"]*)\" rights and data to initiate requisition$")
  public void setupUserWithRightsAndInitiateRequisitionData(String user, String rights) throws SQLException {
    String[] rightList = rights.split(",");
    setupTestDataToInitiateRnR(true, program, user, asList(rightList));
  }

  @When("^I click proceed$")
  public void clickOnProceed() {
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    testWebDriver.sleep(2000);
  }

  @When("^I populate RnR data$")
  public void enterValuesFromDB() throws SQLException {
    dbWrapper.insertValuesInRequisition(false);
  }

  @When("^I access regimen tab$")
  public void clickRegimenTab() throws SQLException {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.clickRegimenTab();
  }

  @When("^I enter beginning balance \"([^\"]*)\"$")
  public void enterBeginningBalance(String beginningBalance) throws SQLException {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(valueOf(beginningBalance), "beginningBalanceFirstProduct");
  }

  @When("^I enter quantity received \"([^\"]*)\"$")
  public void enterQuantityReceived(String quantityReceived) throws SQLException {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(valueOf(quantityReceived), "quantityReceivedFirstProduct");
  }

  @When("^I enter quantity dispensed \"([^\"]*)\"$")
  public void enterQuantityDispensed(String quantityDispensed) throws SQLException {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(valueOf(quantityDispensed), "quantityDispensedFirstProduct");
  }

  @Then("^I validate beginning balance \"([^\"]*)\"$")
  public void validateBeginningBalance(String beginningBalance) throws SQLException {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyBeginningBalanceForFirstProduct(parseInt(beginningBalance));
  }

  @Then("^I validate quantity received \"([^\"]*)\"$")
  public void validateQuantityReceived(String quantityReceived) throws SQLException {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyQuantityReceivedForFirstProduct(parseInt(quantityReceived));
  }

  @Then("^I validate quantity dispensed \"([^\"]*)\"$")
  public void validateQuantityDispensed(String quantityDispensed) throws SQLException {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyQuantityDispensedForFirstProduct(parseInt(quantityDispensed));
  }

  @Then("^I should see regimen fields$")
  public void shouldSeeRegimenFields() {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    verifyRegimenFieldsPresentOnRegimenTab(regimenCode, regimenName);
  }

  @When("^I type patients on treatment \"([^\"]*)\"$")
  public void typePatientsOnTreatment(String value) {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValuesOnRegimenScreen(3, 1, value);
  }

  @When("^I type patients initiated treatment \"([^\"]*)\"$")
  public void typePatientsInitiatedTreatment(String value) {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValuesOnRegimenScreen(4, 1, value);
  }

  @When("^I type patients stopped treatment \"([^\"]*)\"$")
  public void typePatientsStoppedTreatment(String value) {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValuesOnRegimenScreen(5, 1, value);
  }

  @When("^I type remarks \"([^\"]*)\"$")
  public void typeRemarks(String value) {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValuesOnRegimenScreen(6, 1, value);
  }

  @When("^I click save$")
  public void clickSave() {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.clickSaveButton();
  }

  @When("^I should see saved successfully$")
  public void shouldSeeSavedSuccessfully() {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.verifySaveSuccessMsg();
  }

  @When("^I click submit$")
  public void clickSubmit() {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.clickSubmitButton();
    testWebDriver.sleep(250);
  }

  @When("^I click ok$")
  public void clickOk() {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    testWebDriver.sleep(1000);
    initiateRnRPage.clickOk();
  }

  @When("^I should see submit successfully$")
  public void shouldSeeSubmitSuccessfully() {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.verifySubmitSuccessMsg();
  }

  @Then("^I got error message \"([^\"]*)\"$")
  public void shouldSeeSubmitSuccessfully(String errorMsg) {
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    assertEquals(homePage.getErrorMessage(), errorMsg);
  }

  @When("^I receive shipment for the order$")
  public void insertShipmentData() throws SQLException {
    dbWrapper.updateFieldValue("orders", "status", "RELEASED", null, null);
    testDataForShipment(100, true, "P10", 1111);
    dbWrapper.updateFieldValue("orders", "status", "PACKED", null, null);
  }

  @Then("^I should see all products listed in shipment file to update pod$")
  public void verifyDataForPodForPackedOrders() throws SQLException {
    UpdatePodPage updatePodPage = PageObjectFactory.getUpdatePodPage(testWebDriver);
    assertEquals("P10", updatePodPage.getProductCode(1));
    assertEquals("antibiotic Capsule 300/200/600 mg", updatePodPage.getProductName(1));
    assertEquals(100, updatePodPage.getPacksToShip(1));
    assertEquals("Strip", updatePodPage.getUnitOfIssue(1));
    assertEquals(1111, updatePodPage.getQuantityShipped(1));
  }

  @Then("^I should see list of orders to manage POD for Rnr$")
  public void verifyListOfOrdersOnPodScreen() {
    testWebDriver.sleep(1000);
    assertEquals("Village Dispensary", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Village Dispensary')]")).getText());
    assertEquals("HIV", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'HIV')]")).getText());
    assertEquals("Ready to pack", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Ready to pack')]")).getText());
    assertEquals("PeriodName1 (01/12/2012 - 01/12/2015)", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'PeriodName1 (01/12/2012 - 01/12/2015)')]")).getText());
    assertEquals("Update POD", testWebDriver.findElement(By.xpath("//div/a[contains(text(),'Update POD')]")).getText());
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testOnlyCreateRight(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);

    String[] expectedMenuItem = {"Create / Authorize", "View"};
    HomePage homePage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal).loginAs(userSIC, password);

    homePage.clickRequisitionSubMenuItem();
    homePage.verifySubMenuItems(expectedMenuItem);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();

    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(10, "quantityDispensedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(10, "quantityReceivedFirstProduct");
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    assertFalse(initiateRnRPage.isAuthorizeButtonPresent());

    initiateRnRPage.verifyBeginningBalanceForFirstProduct(10);
    initiateRnRPage.verifyQuantityReceivedForFirstProduct(10);
    initiateRnRPage.verifyQuantityDispensedForFirstProduct(10);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testSubmitAndAuthorizeRegimen(String program, String userSIC, String categoryCode, String password,
                                            String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION, AUTHORIZE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();

    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.clickRegimenTab();

    verifyRegimenFieldsPresentOnRegimenTab(regimenCode, regimenName);
    initiateRnRPage.enterValuesOnRegimenScreen(3, 1, "100");
    initiateRnRPage.enterValuesOnRegimenScreen(4, 1, "200");
    initiateRnRPage.enterValuesOnRegimenScreen(6, 1, "400");

    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.verifySubmitRnrErrorMsg();
    initiateRnRPage.enterValuesOnRegimenScreen(5, 1, "300");
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitSuccessMsg();

    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.clickRegimenTab();
    verifyValuesOnAuthorizeRegimenScreen("100", "200", "300", "400");
    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testApproveRegimen(String program, String userSIC, String categoryCode, String password,
                                 String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "testing");
    dbWrapper.updateRequisitionStatus(SUBMITTED, userSIC, "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED, userSIC, "HIV");

    ApprovePage approvePage = homePage.navigateToApprove();
    testWebDriver.waitForAjax();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.clickRegimenTab();
    verifyValuesOnRegimenScreen("100", "200", "300", "testing");
    approvePage.clickSaveButton();
    approvePage.clickApproveButton();
    approvePage.clickOk();
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testApproveRegimenWithoutSave(String program, String userSIC, String categoryCode, String password,
                                            String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();

    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems("100", "200", "300", "testing");
    dbWrapper.updateRequisitionStatus(SUBMITTED, userSIC, "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED, userSIC, "HIV");

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.editFullSupplyApproveQuantity("");
    approvePage.clickApproveButton();
    approvePage.editFullSupplyApproveQuantity("100");
    approvePage.clickApproveButton();
    approvePage.clickOk();
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInvisibleProgramRegimenColumn(String program, String userSIC, String categoryCode,
                                                       String password, String regimenCode, String regimenName,
                                                       String regimenCode2, String regimenName2) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.updateProgramRegimenColumns(program, "remarks", false);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    initiateRnRPage.clickRegimenTab();
    SeleneseTestBase.assertEquals(initiateRnRPage.getRegimenTableColumnCount(), 6);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRWithInActiveRegimen(String program, String userSIC, String categoryCode, String password,
                                         String regimenCode, String regimenName, String regimenCode2,
                                         String regimenName2) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, false);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();

    testWebDriver.sleep(2000);
    assertFalse("Regimen tab should not be displayed.", initiateRnRPage.existRegimenTab());
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageIfPeriodNotDefinedForRegularType(String program, String userSIC,
                                                                  String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    verifyErrorMessages("No current period defined. Please contact the Admin.");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageWhileAuthorizingForRegularType(String program, String userSIC,
                                                                String password) throws SQLException {
    List<String> rightsList = asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    verifyErrorMessages("No current period defined. Please contact the Admin.");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageWhileAuthorizingForEmergencyType(String program, String userSIC,
                                                                  String password) throws SQLException {
    List<String> rightsList = asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyErrorMessages("No current period defined. Please contact the Admin.");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageIfPeriodNotDefinedForEmergencyType(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");

    verifyErrorMessages("No current period defined. Please contact the Admin.");
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void shouldVerifyRequisitionAlreadySubmittedMessage(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, AUTHORIZE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();

    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");

    verifyErrorMessages("R&R for current period already submitted");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testValidRnRSubmittedAuthorizedViewAndVerifyStateOfFields(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);

    List<String> rightsList1 = asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION);
    createUserAndAssignRoleRights("mo", "Maar_Doe@openlmis.com", "F10", "district pharmacist", rightsList1);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    initiateRnRPage.enterValueIfNotNull(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(100, "quantityDispensedFirstProduct");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyRnRsInGrid("current Period", "Not yet started", "1");
    verifyRnRsInGrid("current Period", "INITIATED", "2");
    InitiateRnRPage initiateRnRPage1 = homePage.clickProceed();
    initiateRnRPage1.enterValueIfNotNull(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(100, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(100, "quantityDispensedFirstProduct");
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
    homePage = loginPage.loginAs("mo", password);
    homePage.navigateAndInitiateEmergencyRnr(program);

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

    homePage.navigateAndInitiateEmergencyRnr(program);

    verifyRnRsInGrid("current Period", "Not yet started", "1");
    verifyRnRsInGrid("current Period", "INITIATED", "2");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    clickProceed(1);
    verifyErrorMessages("Requisition not initiated yet");

    ViewRequisitionPage viewRequisitionPage = homePage.navigateViewRequisition();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.verifyEmergencyStatus();
    viewRequisitionPage.verifyStatus("AUTHORIZED");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testEmergencyRnRApprovedAndConvertedToOrder(String program, String userSIC, String password) throws SQLException, ParseException {
    String userName = "lmuInCharge";
    String roleName = "lmuInCharge";

    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);

    List<String> rightsList1 = asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION);
    createUserAndAssignRoleRights("mo", "Maar_Doe@openlmis.com", "F10", "district pharmacist", rightsList1);

    List<String> rightsList2 = asList(APPROVE_REQUISITION, VIEW_REQUISITION);
    createUserAndAssignRoleRights("lmu", "Maafi_De_Doe@openlmis.com", "F10", "lmu", rightsList2);

    List<String> rightsList3 = asList(CONVERT_TO_ORDER, VIEW_ORDER);
    createUserAndAssignRoleRights(userName, "Jaan_V_Doe@openlmis.com", "F10", roleName, rightsList3);

    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    String periodStartDate = "2013-10-03";
    String periodEndDate = "2016-01-30";
    dbWrapper.insertProcessingPeriod("current Period", "current Period", periodStartDate, periodEndDate, 1, "M");

    homePage = loginPage.loginAs(userSIC, password);

    Integer quantityDispensed = 100;
    Integer beginningBalance = 1;
    Integer quantityReceived = 100;

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    initiateRnRPage.enterValueIfNotNull(beginningBalance, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(quantityReceived, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(quantityDispensed, "quantityDispensedFirstProduct");
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyBeginningBalanceForFirstProduct(beginningBalance);
    initiateRnRPage.verifyQuantityReceivedForFirstProduct(quantityReceived);
    initiateRnRPage.verifyQuantityDispensedForFirstProduct(quantityDispensed);
    homePage.logout(baseUrlGlobal);

    homePage = loginPage.loginAs("mo", password);
    homePage.navigateAndInitiateEmergencyRnr(program);

    clickProceed(2);
    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();
    homePage.logout(baseUrlGlobal);

    homePage = loginPage.loginAs("lmu", password);
    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.verifyEmergencyStatus();
    approvePage.clickRequisitionPresentForApproval();

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

    homePage.logout(baseUrlGlobal);
    homePage = loginPage.loginAs(userName, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    assertTrue(convertOrderPage.isNoRequisitionPendingMessageDisplayed());

    ViewOrdersPage viewOrdersPage = homePage.navigateViewOrders();
    viewOrdersPage.verifyNoRequisitionReleasedAsOrderMessage();

    dbWrapper.insertFulfilmentRoleAssignment(userName, roleName, "F10");
    homePage.navigateHomePage();
    homePage.navigateConvertToOrder();
    convertOrderPage.verifyOrderListElements(program, "F10", "Village Dispensary", "03/10/2013", "30/01/2016", "Village Dispensary");
    convertOrderPage.convertToOrder();

    homePage.navigateHomePage();
    ViewOrdersPage viewOrdersPage1 = homePage.navigateViewOrders();
    viewOrdersPage1.isFirstRowPresent();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testValidationsOnStockOnHandRnRField(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    initiateRnRPage.enterValueIfNotNull(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(1000, "quantityDispensedFirstProduct");
    verifyStockOnHandErrorMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testValidationsOnTotalConsumedQuantityRnRField(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");
    dbWrapper.updateSourceOfAProgramTemplate("HIV", "Total Consumed Quantity", "C");
    dbWrapper.updateSourceOfAProgramTemplate("HIV", "Stock on Hand", "U");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    initiateRnRPage.enterValueIfNotNull(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(1000, "stockInHandFirstProduct");
    verifyTotalQuantityConsumedErrorMessage();
    initiateRnRPage.verifyStockOnHandForFirstProduct("1000");
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testVerifyAllStatusOfRequisitions(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.enterValueIfNotNull(100, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(100, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(100, "quantityDispensedFirstProduct");
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitRnrSuccessMsg();
    initiateRnRPage.verifyAllFieldsDisabled();
    initiateRnRPage.verifySaveButtonDisabled();
    initiateRnRPage.calculateAndVerifyTotalCost();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRErrorMessageForSubmitterWithRequisitionAlreadyAuthorized(String program, String userSIC, String password) throws SQLException {
    setupTestDataToInitiateRnR(true, program, userSIC, asList(CREATE_REQUISITION, VIEW_REQUISITION));

    createUserAndAssignRoleRights("mo", "Maar_Doe@openlmis.com", "F10", "district pharmacist",
      asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION));
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();
    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.clickSubmitButton();
    initiateRnRPage.clickOk();
    homePage.logout(baseUrlGlobal);

    homePage = loginPage.loginAs("mo", password);
    homePage.navigateAndInitiateRnr(program);
    initiateRnRPage = homePage.clickProceed();

    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();

    homePage.logout(baseUrlGlobal);
    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateViewRequisition();
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    testWebDriver.refresh();
    testWebDriver.sleep(1000);

    testWebDriver.selectByVisibleText(testWebDriver.getElementByXpath("//select[@id='programListMyFacility']"), program);
    verifyErrorMessages("R&R for current period already submitted");

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void shouldVerifyNoCurrentPeriodDefinedMessage(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(AUTHORIZE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");

    String errorMessage = "No current period defined. Please contact the Admin.";
    verifyErrorMessages(errorMessage);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    verifyErrorMessages(errorMessage);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testRnRBeginningBalance(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteTable("processing_periods");
    dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "M");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();

    dbWrapper.insertValuesInRequisition(false);
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "storeInCharge", "HIV");

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
  public void testRestrictVirtualFacilityFromRnRScreen(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10");
    dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId(userSIC, "store in-charge", "N1");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnrForSupervisedFacility(program);
    String str = homePage.getFacilityDropDownList();
    assertFalse(str.contains("F10"));
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testSkipProductRnRField(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2016-01-30", 1, "M");
    dbWrapper.updateFieldValue("products", "fullSupply", "true", "code", "P11");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    initiateRnRPage.enterValueIfNotNull(100, "requestedQuantityFirstProduct");
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.verifyCostOnFooterForProducts(1);

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
    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityDispensedFirstProduct");
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceSecondProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityDispensedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(100, "requestedQuantitySecondProduct");
    initiateRnRPage.skipSingleProduct(2);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitRnrSuccessMsg();
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.verifyCostOnFooterForProducts(1);
    assertEquals("125.0", Float.parseFloat(dbWrapper.getAttributeFromTable("requisitions", "fullSupplyItemsSubmittedCost", "id", String.valueOf(dbWrapper.getMaxRnrID()))));
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testSkipProductRnRAuthorizeApproveForRegularRnR(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2016-01-30", 1, "M");
    dbWrapper.updateFieldValue("products", "fullSupply", "true", "code", "P11");

    homePage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal).loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Regular");
    homePage.clickProceed();

    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityDispensedFirstProduct");
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceSecondProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(100, "requestedQuantitySecondProduct");
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
    initiateRnRPage.verifyCostOnFooterForProducts(1);
    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    assertTrue(approvePage.approveQuantityVisible(1));
    approvePage.editFullSupplyApproveQuantity("5");
    assertFalse(approvePage.approveQuantityVisible(2));
    approvePage.clickApproveButton();
    approvePage.clickOk();
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testSkipProductRnRAuthorizeApproveUpdatePodForEmergencyRnR(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = asList(CREATE_REQUISITION, VIEW_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION, CONVERT_TO_ORDER, MANAGE_POD);
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertProcessingPeriod("current Period", "current Period", "2013-10-03", "2016-01-30", 1, "M");
    dbWrapper.updateFieldValue("products", "fullSupply", "true", "code", "P11");

    homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();

    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityDispensedFirstProduct");
    initiateRnRPage.enterExplanationReason();
    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceSecondProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityDispensedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(100, "requestedQuantitySecondProduct");
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
    initiateRnRPage.verifyCostOnFooterForProducts(1);
    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    assertTrue(approvePage.approveQuantityVisible(1));
    approvePage.editFullSupplyApproveQuantity("5");
    assertFalse(approvePage.approveQuantityVisible(2));
    approvePage.clickApproveButton();
    approvePage.clickOk();
    approvePage.verifyNoRequisitionPendingMessage();

    dbWrapper.insertFulfilmentRoleAssignment("storeInCharge", "store in-charge", "F10");
    dbWrapper.insertOrders("RELEASED", userSIC, program);
    dbWrapper.updateRequisitionStatus("RELEASED", userSIC, program);

    ManagePodPage managePodPage = homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = managePodPage.selectRequisitionToUpdatePod(1);

    assertTrue(updatePodPage.getPodTableData().contains("P10"));
    assertTrue(updatePodPage.getPodTableData().contains("antibiotic Capsule 300/200/600 mg"));
    assertTrue(updatePodPage.getPodTableData().contains("Strip"));

    assertFalse(updatePodPage.getPodTableData().contains("P11"));
  }

  private void verifyRegimenFieldsPresentOnRegimenTab(String regimenCode, String regimenName) {
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
        message)
    );
  }

  private void verifyValuesOnRegimenScreen(String patientsOnTreatment, String patientsToInitiateTreatment,
                                           String patientsStoppedTreatment, String remarks) {
    assertEquals(patientsOnTreatment, initiateRnRPage.getPatientsOnTreatmentValue());
    assertEquals(patientsToInitiateTreatment, initiateRnRPage.getPatientsToInitiateTreatmentValue());
    assertEquals(patientsStoppedTreatment, initiateRnRPage.getPatientsStoppedTreatmentValue());
    assertEquals(remarks, initiateRnRPage.getRemarksValue());
  }

  private void verifyValuesOnAuthorizeRegimenScreen(String patientsOnTreatment, String patientsToInitiateTreatment,
                                                    String patientsStoppedTreatment, String remarks) {
    assertEquals(patientsOnTreatment, initiateRnRPage.getPatientsOnTreatmentInputValue());
    assertEquals(patientsToInitiateTreatment, initiateRnRPage.getPatientsToInitiateTreatmentInputValue());
    assertEquals(patientsStoppedTreatment, initiateRnRPage.getPatientsStoppedTreatmentInputValue());
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
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeInCharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}
    };
  }

  @DataProvider(name = "Data-Provider-Function-RnR")
  public Object[][] parameterIntTestProviderRnR() {
    return new Object[][]{
      {"HIV", "storeInCharge", "Admin123"}
    };
  }

  private int calculateReportingDays(String periodStartString) throws ParseException {
    Date currentDate = new Date();
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date periodStartDate = formatter.parse(periodStartString);
    return (int) ((currentDate.getTime() - periodStartDate.getTime()) / MILLISECONDS_IN_ONE_DAY);
  }
}

