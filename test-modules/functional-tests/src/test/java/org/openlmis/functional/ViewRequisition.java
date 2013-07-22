/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

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
  public static final String patientsOnTreatment = "100";
  public static final String patientsToInitiateTreatment = "200";
  public static final String patientsStoppedTreatment = "300";
  public static final String remarks = "testing";
  public String program, userSIC, password;


  @BeforeMethod(groups = "functional")
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @When("^I populate Regimen data as patientsOnTreatment \"([^\"]*)\" patientsToInitiateTreatment \"([^\"]*)\" patientsStoppedTreatment \"([^\"]*)\" remarks \"([^\"]*)\"$")
  public void enterValuesFromDB(String patientsOnTreatment, String patientsToInitiateTreatment, String patientsStoppedTreatment, String remarks) throws IOException, SQLException {
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
  }

  @When("^I access home page")
  public void accessHomePage() throws IOException, SQLException {
    InitiateRnRPage initiateRnRPage=new InitiateRnRPage(testWebDriver);
    initiateRnRPage.clickHome();
  }

  @When("^I access view RnR screen$")
  public void accessViewRnRScreen() throws IOException {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateViewRequisition();
  }

  @Then("^I should see elements on view requisition page$")
  public void shouldSeeElementsOnViewRequisitionPage() throws IOException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
  }

  @When("^I update requisition status to \"([^\"]*)\"$")
  public void updateRequisitionStatus(String status) throws IOException, SQLException {
    dbWrapper.updateRequisitionStatus(status);
  }

  @When("^I type view search criteria$")
  public void typeViewResultCriteria() throws IOException, SQLException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.enterViewSearchCriteria();
  }

  @When("^I click search$")
  public void clickSearch() throws IOException, SQLException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.clickSearch();
  }

  @When("^I access regimen tab for view requisition$")
  public void clickRegimenTab() throws IOException, SQLException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.clickRegimenTab();
  }

  @Then("^I should see no requisition found message$")
  public void shouldSeeNoRequisitionMessage() throws IOException, SQLException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyNoRequisitionFound();
  }

  @When("^I update approved quantity \"([^\"]*)\"$")
  public void updateApprovedQuantity(String quantity) throws IOException, SQLException {
    dbWrapper.insertApprovedQuantity(Integer.parseInt(quantity));
  }

  @Then("^I should see requisition status as \"([^\"]*)\"$")
  public void verifyRequisitionStatus(String status) throws IOException, SQLException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyStatus(status);
  }

  @When("^I click RnR List$")
  public void clickRnRList() throws IOException, SQLException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.clickRnRList();
  }

  @Then("^I verify total field$")
  public void verifyTotalField() throws IOException, SQLException {
    ViewRequisitionPage viewRequisitionPage = new ViewRequisitionPage(testWebDriver);
    viewRequisitionPage.verifyTotalFieldPostAuthorize();
  }

  @Then("^I verify values on regimen page as patientsOnTreatment \"([^\"]*)\" patientsToInitiateTreatment \"([^\"]*)\" patientsStoppedTreatment \"([^\"]*)\" remarks \"([^\"]*)\"$")
  public void verifyValuesONRegimenPage(String patientsOnTreatment, String patientsToInitiateTreatment, String patientsStoppedTreatment, String remarks) throws IOException {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    verifyValuesOnRegimenScreen(initiateRnRPage, patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Including-Regimen")
  public void testViewRequisitionAndRegimen(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add("CREATE_REQUISITION");
    rightsList.add("VIEW_REQUISITION");

    setupTestDataToInitiateRnR(true, program, userSIC, "200", "openLmis", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.assignRight(STORE_IN_CHARGE, APPROVE_REQUISITION);
    dbWrapper.assignRight(STORE_IN_CHARGE, CONVERT_TO_ORDER);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    HomePage homePage1 = initiateRnRPage.clickHome();

    ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
    dbWrapper.insertValuesInRequisition();
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    dbWrapper.updateRequisitionStatus(SUBMITTED);
    viewRequisitionPage.enterViewSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.verifyNoRequisitionFound();
    dbWrapper.insertApprovedQuantity(10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED);
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();
    viewRequisitionPage.verifyTotalFieldPostAuthorize();
    HomePage homePageAuthorized = viewRequisitionPage.verifyFieldsPreApproval("12.50", "1");
    viewRequisitionPage.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage, patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    ViewRequisitionPage viewRequisitionPageAuthorized = homePageAuthorized.navigateViewRequisition();
    viewRequisitionPageAuthorized.enterViewSearchCriteria();
    viewRequisitionPageAuthorized.clickSearch();
    viewRequisitionPageAuthorized.verifyStatus(AUTHORIZED);
    viewRequisitionPageAuthorized.clickRnRList();

    HomePage homePageInApproval = viewRequisitionPageAuthorized.verifyFieldsPreApproval("12.50", "1");
    viewRequisitionPageAuthorized.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage, patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    dbWrapper.updateRequisitionStatus(IN_APPROVAL);
    ViewRequisitionPage viewRequisitionPageInApproval = homePageInApproval.navigateViewRequisition();
    viewRequisitionPageInApproval.enterViewSearchCriteria();
    viewRequisitionPageInApproval.clickSearch();
    viewRequisitionPageInApproval.verifyStatus(IN_APPROVAL);

    ApprovePage approvePageTopSNUser = homePageInApproval.navigateToApprove();
    approvePageTopSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageTopSNUser.editApproveQuantityAndVerifyTotalCostViewRequisition("20");
    approvePageTopSNUser.addComments("Dummy Comments");
    approvePageTopSNUser.verifyTotalFieldPostAuthorize();
    approvePageTopSNUser.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage, patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    approvePageTopSNUser.approveRequisition();
    approvePageTopSNUser.clickOk();
    approvePageTopSNUser.verifyNoRequisitionPendingMessage();
    ViewRequisitionPage viewRequisitionPageApproved = homePageInApproval.navigateViewRequisition();
    viewRequisitionPageApproved.enterViewSearchCriteria();
    viewRequisitionPageApproved.clickSearch();
    viewRequisitionPageApproved.verifyStatus(APPROVED);
    viewRequisitionPageApproved.clickRnRList();
    viewRequisitionPageApproved.verifyTotalFieldPostAuthorize();
    viewRequisitionPageApproved.verifyComment("Dummy Comments", userSIC, 1);
    viewRequisitionPageApproved.verifyCommentBoxNotPresent();

    HomePage homePageApproved = viewRequisitionPageApproved.verifyFieldsPostApproval("25.00", "1");
    viewRequisitionPageAuthorized.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage, patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    dbWrapper.updateSupplyingFacilityForRequisition("F10");
    ConvertOrderPage convertOrderPage = homePageApproved.navigateConvertToOrder();
    convertOrderPage.convertToOrder();
    ViewRequisitionPage viewRequisitionPageOrdered = homePageApproved.navigateViewRequisition();
    viewRequisitionPageOrdered.enterViewSearchCriteria();
    viewRequisitionPageOrdered.clickSearch();
    viewRequisitionPageOrdered.verifyStatus(RELEASED);
    viewRequisitionPageOrdered.clickRnRList();
    viewRequisitionPageOrdered.verifyTotalFieldPostAuthorize();
    viewRequisitionPageOrdered.verifyFieldsPostApproval("25.00", "1");
    viewRequisitionPageOrdered.verifyApprovedQuantityFieldPresent();
    viewRequisitionPageOrdered.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage, patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
  }

  private void verifyValuesOnRegimenScreen(InitiateRnRPage initiateRnRPage, String patientsontreatment, String patientstoinitiatetreatment, String patientsstoppedtreatment, String remarks) {
    assertEquals(patientsontreatment, initiateRnRPage.getPatientsOnTreatmentValue());
    assertEquals(patientstoinitiatetreatment, initiateRnRPage.getPatientsToInitiateTreatmentValue());
    assertEquals(patientsstoppedtreatment, initiateRnRPage.getPatientsStoppedTreatmentValue());
    assertEquals(remarks, initiateRnRPage.getRemarksValue());
  }

  @AfterMethod(groups = "functional")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(1000);
    if(!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
    }
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Including-Regimen")
  public Object[][] parameterIntTest() {
    return new Object[][]{
      {"HIV", "storeincharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}
    };


  }
}

