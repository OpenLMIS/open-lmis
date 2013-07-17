/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ApprovePage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

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

  @BeforeMethod(groups = {"functional", "smoke"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function-Positive")
  public void testVerifyRegimensColumnsAndShouldSaveData(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = new ArrayList<String>();
    rightsList.add(CREATE_REQUISITION);
    rightsList.add(VIEW_REQUISITION);
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

    verifyRegimenFieldsPresentOnRegimenTab(regimenCode, regimenName, initiateRnRPage, initiateRnRPage1);

    initiateRnRPage1.enterValuesOnRegimenScreen(3, 2, 100);
    initiateRnRPage1.enterValuesOnRegimenScreen(4, 2, 200);
    initiateRnRPage1.enterValuesOnRegimenScreen(5, 2, 300);
    initiateRnRPage1.enterValuesOnRegimenScreen(6, 2, 400);

    initiateRnRPage1.clickSaveButton();
    initiateRnRPage1.verifySaveSuccessMsg();

    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.clickOk();
    initiateRnRPage1.verifySubmitSuccessMsg();

  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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

    verifyRegimenFieldsPresentOnRegimenTab(regimenCode, regimenName, initiateRnRPage, initiateRnRPage1);
    initiateRnRPage1.enterValuesOnRegimenScreen(3, 2, 100);
    initiateRnRPage1.enterValuesOnRegimenScreen(4, 2, 200);
    initiateRnRPage1.enterValuesOnRegimenScreen(6, 2, 400);

    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.verifySubmitRnrErrorMsg();
    initiateRnRPage1.enterValuesOnRegimenScreen(5, 2, 300);
    initiateRnRPage1.clickSubmitButton();
    initiateRnRPage1.clickOk();
    initiateRnRPage1.verifySubmitSuccessMsg();

    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage2 = homePage.clickProceed();
    initiateRnRPage2.clickRegimenTab();
    verifyValuesOnAuthorizeRegimenScreen(initiateRnRPage2,"100","200","300","400");
    initiateRnRPage2.clickAuthorizeButton();
    initiateRnRPage2.clickOk();
    initiateRnRPage2.verifyAuthorizeRnrSuccessMsg();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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
    dbWrapper.insertValuesInRegimenLineItems("100","200","300","testing");
    dbWrapper.updateRequisitionStatus(SUBMITTED);
    dbWrapper.insertApprovedQuantity(10);
    dbWrapper.updateRequisitionStatus(AUTHORIZED);

    ApprovePage approvePageLowerSNUser = homePage.navigateToApprove();
    approvePageLowerSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageLowerSNUser.clickRegimenTab();
    verifyValuesOnRegimenScreen(initiateRnRPage,"100","200","300","testing");
    approvePageLowerSNUser.clickSaveButton();
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.clickOk();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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
    dbWrapper.insertValuesInRegimenLineItems("100","200","300","testing");
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

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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

  private void verifyRegimenFieldsPresentOnRegimenTab(String regimenCode, String regimenName, InitiateRnRPage initiateRnRPage, InitiateRnRPage initiateRnRPage1) {
    assertTrue("Regimen tab should be displayed.", initiateRnRPage.existRegimenTab());
    assertEquals(initiateRnRPage1.getRegimenTableRowCount(), 2);

    assertTrue("Regimen Code should be displayed.", initiateRnRPage1.existRegimenCode(regimenCode, 2));
    assertTrue("Regimen Name should be displayed.", initiateRnRPage1.existRegimenName(regimenName, 2));

    assertTrue("Reporting Field 1 should be displayed.", initiateRnRPage1.existRegimenReportingField(3, 2));
    assertTrue("Reporting Field 2 should be displayed.", initiateRnRPage1.existRegimenReportingField(4, 2));
    assertTrue("Reporting Field 3 should be displayed.", initiateRnRPage1.existRegimenReportingField(5, 2));
    assertTrue("Reporting Field 4 should be displayed.", initiateRnRPage1.existRegimenReportingField(6, 2));
  }

  private void verifyValuesOnRegimenScreen(InitiateRnRPage initiateRnRPage, String patientsontreatment, String patientstoinitiatetreatment, String patientsstoppedtreatment, String remarks )
  {
    assertEquals(patientsontreatment,initiateRnRPage.getPatientsOnTreatmentValue());
    assertEquals(patientstoinitiatetreatment,initiateRnRPage.getPatientsToInitiateTreatmentValue());
    assertEquals(patientsstoppedtreatment,initiateRnRPage.getPatientsStoppedTreatmentValue());
    assertEquals(remarks,initiateRnRPage.getRemarksValue());
  }

  private void verifyValuesOnAuthorizeRegimenScreen(InitiateRnRPage initiateRnRPage, String patientsontreatment, String patientstoinitiatetreatment, String patientsstoppedtreatment, String remarks )
  {
    assertEquals(patientsontreatment,initiateRnRPage.getPatientsOnTreatmentInputValue());
    assertEquals(patientstoinitiatetreatment,initiateRnRPage.getPatientsToInitiateTreatmentInputValue());
    assertEquals(patientsstoppedtreatment,initiateRnRPage.getPatientsStoppedTreatmentInputValue());
    assertEquals(remarks,initiateRnRPage.getRemarksInputValue());
  }


  @AfterMethod(groups = {"functional", "smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}
    };


  }
}

