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


import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewRequisition extends TestCaseHelper {

  public static final String STORE_IN_CHARGE = "store in-charge";
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String SUBMITTED = "SUBMITTED";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String IN_APPROVAL = "IN_APPROVAL";
  public static final String APPROVED = "APPROVED";
  public static final String RELEASED = "RELEASED";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  public static final String patientsOnTreatment = "100";
  public static final String patientsToInitiateTreatment = "200";
  public static final String patientsStoppedTreatment = "300";
  public static final String remarks = "testing";
  public String program, userSIC, password;

  LoginPage loginPage;
  InitiateRnRPage initiateRnRPage;

  @BeforeMethod(groups = "requisition")
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
  }

  @When(
    "^I populate Regimen data as patientsOnTreatment \"([^\"]*)\" patientsToInitiateTreatment \"([^\"]*)\" patientsStoppedTreatment \"([^\"]*)\" remarks \"([^\"]*)\"$")
  public void enterValuesFromDB(String patientsOnTreatment,
                                String patientsToInitiateTreatment,
                                String patientsStoppedTreatment,
                                String remarks) throws SQLException {
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment,
      remarks);
  }

  @When("^I access home page")
  public void accessHomePage() throws SQLException {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.clickHome();
  }

  @When("^I access view RnR screen$")
  public void accessViewRnRScreen() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateViewRequisition();
  }

  @Then("^I should see elements on view requisition page$")
  public void shouldSeeElementsOnViewRequisitionPage() {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
  }

  @When("^I update requisition status to \"([^\"]*)\"$")
  public void updateRequisitionStatus(String status) throws SQLException {
    dbWrapper.updateRequisitionStatus(status, "storeInCharge", "HIV");
  }

  @When("^I type view search criteria$")
  public void typeViewResultCriteria() throws SQLException {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.enterViewSearchCriteria();
  }

  @When("^I click search$")
  public void clickSearch() throws SQLException {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.clickSearch();
  }

  @When("^I access regimen tab for view requisition$")
  public void clickRegimenTab() throws SQLException {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.clickRegimenTab();
  }

  @Then("^I should see no requisition found message$")
  public void shouldSeeNoRequisitionMessage() throws SQLException {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyNoRequisitionFound();
  }

  @When("^I update approved quantity \"([^\"]*)\"$")
  public void updateApprovedQuantity(String quantity) throws SQLException {
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", Integer.parseInt(quantity));
  }

  @Then("^I should see requisition status as \"([^\"]*)\"$")
  public void verifyRequisitionStatus(String status) throws SQLException {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyStatus(status);
  }

  @When("^I click RnR List$")
  public void clickRnRList() throws SQLException {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.clickRnRList();
  }

  @Then("^I verify total field$")
  public void verifyTotalField() throws SQLException {
    ViewRequisitionPage viewRequisitionPage = PageObjectFactory.getViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyTotalFieldPostAuthorize();
  }

  @Then(
    "^I verify values on regimen page as patientsOnTreatment \"([^\"]*)\" patientsToInitiateTreatment \"([^\"]*)\" patientsStoppedTreatment \"([^\"]*)\" remarks \"([^\"]*)\"$")
  public void verifyValuesONRegimenPage(String patientsOnTreatment,
                                        String patientsToInitiateTreatment,
                                        String patientsStoppedTreatment,
                                        String remarks) {
    verifyValuesOnRegimenScreen(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Including-Regimen")
  public void testViewRequisitionRegimenAndEmergencyStatus(String program,
                                                           String userSIC,
                                                           String categoryCode,
                                                           String password,
                                                           String regimenCode,
                                                           String regimenName,
                                                           String regimenCode2,
                                                           String regimenName2) throws SQLException {
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION");

    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.assignRight(STORE_IN_CHARGE, APPROVE_REQUISITION);
    dbWrapper.assignRight(STORE_IN_CHARGE, CONVERT_TO_ORDER);
    dbWrapper.assignRight(STORE_IN_CHARGE, VIEW_ORDER);
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, STORE_IN_CHARGE, "F10");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    HomePage homePage1 = initiateRnRPage.clickHome();

    ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
    dbWrapper.insertValuesInRequisition(true);
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment,
      remarks);
    dbWrapper.updateRequisitionStatus(SUBMITTED, userSIC, "HIV");
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.verifyNoRequisitionFound();
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED, userSIC, "HIV");
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();
    viewRequisitionPage.verifyTotalFieldPostAuthorize();
    HomePage homePageAuthorized = viewRequisitionPage.verifyFieldsPreApproval("12.50", "1");
    viewRequisitionPage.clickRegimenTab();
    verifyValuesOnRegimenScreen(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    ViewRequisitionPage viewRequisitionPageAuthorized = homePageAuthorized.navigateViewRequisition();
    viewRequisitionPageAuthorized.enterViewSearchCriteria();
    viewRequisitionPageAuthorized.clickSearch();
    viewRequisitionPageAuthorized.verifyStatus(AUTHORIZED);
    viewRequisitionPageAuthorized.verifyEmergencyStatus();
    viewRequisitionPageAuthorized.clickRnRList();

    HomePage homePageInApproval = viewRequisitionPageAuthorized.verifyFieldsPreApproval("12.50", "1");
    viewRequisitionPageAuthorized.clickRegimenTab();
    verifyValuesOnRegimenScreen(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    dbWrapper.updateRequisitionStatus(IN_APPROVAL, userSIC, "HIV");
    ViewRequisitionPage viewRequisitionPageInApproval = homePageInApproval.navigateViewRequisition();
    viewRequisitionPageInApproval.enterViewSearchCriteria();
    viewRequisitionPageInApproval.clickSearch();
    viewRequisitionPageInApproval.verifyStatus(IN_APPROVAL);
    viewRequisitionPageInApproval.verifyEmergencyStatus();

    ApprovePage approvePageTopSNUser = homePageInApproval.navigateToApprove();
    approvePageTopSNUser.verifyEmergencyStatus();
    approvePageTopSNUser.clickRequisitionPresentForApproval();
    approvePageTopSNUser.editFullSupplyApproveQuantity("20");
    approvePageTopSNUser.VerifyTotalCostViewRequisition("20");
    approvePageTopSNUser.addComments("Dummy Comments");
    approvePageTopSNUser.verifyTotalFieldPostAuthorize();
    approvePageTopSNUser.clickRegimenTab();
    verifyValuesOnRegimenScreen(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    approvePageTopSNUser.approveRequisition();
    approvePageTopSNUser.clickOk();
    approvePageTopSNUser.verifyNoRequisitionPendingMessage();
    ViewRequisitionPage viewRequisitionPageApproved = homePageInApproval.navigateViewRequisition();
    viewRequisitionPageApproved.enterViewSearchCriteria();
    viewRequisitionPageApproved.clickSearch();
    viewRequisitionPageApproved.verifyStatus(APPROVED);
    viewRequisitionPageApproved.verifyEmergencyStatus();
    viewRequisitionPageApproved.clickRnRList();
    viewRequisitionPageApproved.verifyTotalFieldPostAuthorize();
    viewRequisitionPageApproved.verifyComment("Dummy Comments", userSIC, 1);
    viewRequisitionPageApproved.verifyCommentBoxNotPresent();

    HomePage homePageApproved = viewRequisitionPageApproved.verifyFieldsPostApproval("25.00", "1");
    viewRequisitionPageAuthorized.clickRegimenTab();
    verifyValuesOnRegimenScreen(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);

    ConvertOrderPage convertOrderPage = homePageApproved.navigateConvertToOrder();
    convertOrderPage.verifyEmergencyStatus();
    convertOrderPage.verifyNoRequisitionSelectedMessage();
    convertOrderPage.convertToOrder();
    ViewRequisitionPage viewRequisitionPageOrdered = homePageApproved.navigateViewRequisition();
    viewRequisitionPageOrdered.enterViewSearchCriteria();
    viewRequisitionPageOrdered.clickSearch();
    viewRequisitionPageOrdered.verifyStatus(RELEASED);
    viewRequisitionPageOrdered.verifyEmergencyStatus();
    viewRequisitionPageOrdered.clickRnRList();
    viewRequisitionPageOrdered.verifyTotalFieldPostAuthorize();
    viewRequisitionPageOrdered.verifyFieldsPostApproval("25.00", "1");
    viewRequisitionPageOrdered.verifyApprovedQuantityFieldPresent();
    viewRequisitionPageOrdered.clickRegimenTab();
    verifyValuesOnRegimenScreen(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);

    ViewOrdersPage viewOrdersPage = homePageApproved.navigateViewOrders();
    viewOrdersPage.verifyEmergencyStatus();
  }

  private void verifyValuesOnRegimenScreen(String patientsOnTreatment,
                                           String patientsToInitiateTreatment,
                                           String patientsStoppedTreatment,
                                           String remarks) {
    initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    assertEquals(patientsOnTreatment, initiateRnRPage.getPatientsOnTreatmentValue());
    assertEquals(patientsToInitiateTreatment, initiateRnRPage.getPatientsToInitiateTreatmentValue());
    assertEquals(patientsStoppedTreatment, initiateRnRPage.getPatientsStoppedTreatmentValue());
    assertEquals(remarks, initiateRnRPage.getRemarksValue());
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testViewVirtualFacilityFromRnRViewScreen(String program,
                                                       String userSIC,
                                                       String password) throws SQLException {
    List<String> rightsList = new ArrayList<>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10");
    dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId(userSIC, "store in-charge", "N1");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateViewRequisition();

    String str1 = homePage.getFacilityDropDownListForViewRequisition();
    assertTrue(str1.contains("F10"));
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-RnR")
  public void testViewRnRSkipProductField(String program, String userSIC, String password) throws SQLException {
    List<String> rightsList = new ArrayList<>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("AUTHORIZE_REQUISITION");
    rightsList.add("APPROVE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertCurrentPeriod("current Period", "current Period", 1, "M");
    dbWrapper.updateFieldValue("products", "fullSupply", "true", "code", "P11");

    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    homePage.clickProceed();
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(1, "beginningBalanceFirstProduct");
    initiateRnRPage.enterValueIfNotNull(1, "quantityDispensedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(2, "quantityReceivedFirstProduct");
    initiateRnRPage.enterValueIfNotNull(10, "beginningBalanceSecondProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityReceivedSecondProduct");
    initiateRnRPage.enterValueIfNotNull(0, "quantityDispensedSecondProduct");
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.verifyCostOnFooterForProducts(2);
    initiateRnRPage.skipAllProduct();
    initiateRnRPage.verifyAllFieldsDisabled();
    initiateRnRPage.calculateAndVerifyTotalCost();
    assertEquals(initiateRnRPage.getTotalCostFooter(), "0.00");
    assertEquals(initiateRnRPage.getFullySupplyCostFooter(), "0.00");

    initiateRnRPage.unSkipAllProduct();
    assertTrue(initiateRnRPage.isEnableBeginningBalanceForFirstProduct());
    initiateRnRPage.skipSingleProduct(1);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifySubmitRnrSuccessMsg();
    initiateRnRPage.clickAuthorizeButton();
    initiateRnRPage.clickOk();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    ViewRequisitionPage viewRequisitionPage = homePage.navigateViewRequisition();
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();
    viewRequisitionPage.verifySkippedProductsOnRnRScreen(1);
  }

  @Test(groups = {"requisition"}, dataProvider = "Data-Provider-Function-Including-Regimen")
  public void testRetainSearchResultOnViewRequisitionPage(String program, String userSIC, String categoryCode, String password,
                                                          String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws SQLException {
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION");

    setupTestDataToInitiateRnR(true, program, userSIC, rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.assignRight(STORE_IN_CHARGE, APPROVE_REQUISITION);
    dbWrapper.assignRight(STORE_IN_CHARGE, CONVERT_TO_ORDER);
    dbWrapper.assignRight(STORE_IN_CHARGE, VIEW_ORDER);
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, STORE_IN_CHARGE, "F10");

    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    HomePage homePage1 = initiateRnRPage.clickHome();

    dbWrapper.insertValuesInRequisition(true);
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment,
      remarks);
    dbWrapper.updateRequisitionStatus(SUBMITTED, userSIC, "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED, userSIC, "HIV");

    ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    testWebDriver.waitForAjax();
    String url = testWebDriver.getCurrentUrl();
    viewRequisitionPage.clickRnRList();
    testWebDriver.waitForAjax();

    testWebDriver.sleep(200);
    testWebDriver.getUrl(url);
    testWebDriver.waitForAjax();
    testWebDriver.setImplicitWait(1000);
    assertTrue(viewRequisitionPage.isViewRnRListPresent());
    testWebDriver.sleep(200);
    testWebDriver.waitForAjax();
    assertTrue(viewRequisitionPage.isRnRListReq1Present());
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

  @DataProvider(name = "Data-Provider-Function-RnR")
  public Object[][] parameterIntTestProviderRnR() {
    return new Object[][]{{"HIV", "storeInCharge", "Admin123"}};
  }

  @DataProvider(name = "Data-Provider-Function-Including-Regimen")
  public Object[][] parameterIntTest() {
    return new Object[][]{{"HIV", "storeInCharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}};
  }
}

