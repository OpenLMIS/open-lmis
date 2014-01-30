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


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ApprovePage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.testng.annotations.*;

import java.util.List;

import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class CreateTestRequisition extends TestCaseHelper {

  public static final String STORE_IN_CHARGE = "store in-charge";
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String SUBMITTED = "SUBMITTED";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String IN_APPROVAL = "IN_APPROVAL";
  public static final String VIEW_ORDER = "VIEW_ORDER";
  public static final String patientsOnTreatment = "100";
  public static final String patientsToInitiateTreatment = "200";
  public static final String patientsStoppedTreatment = "300";
  public static final String remarks = "testing";
  public String program, userSIC, password;


  @BeforeMethod(groups = "createTestRequisition")
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"createTestRequisition"}, dataProvider = "Data-Provider-Function-Including-Regimen")
  public void testCreateRequisitionWithEmergencyStatus(String program, String userSIC, String categoryCode, String password, String regimenCode, String regimenName, String regimenCode2, String regimenName2) throws Exception {
    List<String> rightsList = asList("CREATE_REQUISITION", "VIEW_REQUISITION");
    setupTestDataToInitiateRnR(true, program, userSIC, "200", rightsList);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode, regimenName, true);
    dbWrapper.insertRegimenTemplateConfiguredForProgram(program, categoryCode, regimenCode2, regimenName2, false);
    dbWrapper.insertRegimenTemplateColumnsForProgram(program);
    dbWrapper.assignRight(STORE_IN_CHARGE, APPROVE_REQUISITION);
    dbWrapper.assignRight(STORE_IN_CHARGE, CONVERT_TO_ORDER);
    dbWrapper.assignRight(STORE_IN_CHARGE, VIEW_ORDER);
    dbWrapper.insertFulfilmentRoleAssignment(userSIC, STORE_IN_CHARGE, "F10");

    LoginPage loginPage = PageFactory.getInstanceOfLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);

    homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.clickHome();

    dbWrapper.insertValuesInRequisition(true);
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment,
      remarks);
    dbWrapper.updateRequisitionStatusByRnrId(SUBMITTED, userSIC, dbWrapper.getMaxRnrID());
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatusByRnrId(AUTHORIZED, userSIC, dbWrapper.getMaxRnrID());
    dbWrapper.updateRequisitionStatusByRnrId(IN_APPROVAL, userSIC, dbWrapper.getMaxRnrID());

    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.clickRequisitionPresentForApproval();
    approvePage.verifyNoRequisitionMessage();
    approvePage.editFullSupplyApproveQuantity("20");
    approvePage.clickRegimenTab();
    approvePage.approveRequisition();
    approvePage.clickOk();

    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.convertToOrder();

    initiateRnRPage.clickHome();
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    initiateRnRPage.clickHome();

    dbWrapper.insertValuesInRequisition(true);
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    dbWrapper.updateRequisitionStatusByRnrId(SUBMITTED, userSIC, dbWrapper.getMaxRnrID());
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatusByRnrId(AUTHORIZED, userSIC, dbWrapper.getMaxRnrID());
    dbWrapper.updateRequisitionStatusByRnrId(IN_APPROVAL, userSIC, dbWrapper.getMaxRnrID());

    initiateRnRPage.clickHome();
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    initiateRnRPage.clickHome();
    dbWrapper.insertValuesInRequisition(true);
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    dbWrapper.updateRequisitionStatusByRnrId(SUBMITTED, userSIC, dbWrapper.getMaxRnrID());
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.updateRequisitionStatusByRnrId(AUTHORIZED, userSIC, dbWrapper.getMaxRnrID());

    initiateRnRPage.clickHome();
    homePage.navigateAndInitiateRnr(program);
    homePage.clickProceed();
    dbWrapper.insertValuesInRequisition(false);
    dbWrapper.insertValuesInRegimenLineItems(patientsOnTreatment, patientsToInitiateTreatment, patientsStoppedTreatment, remarks);
    dbWrapper.updateRequisitionStatusByRnrId(SUBMITTED, userSIC, dbWrapper.getMaxRnrID());
  }

  @AfterMethod(groups = "createTestRequisition")
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageFactory.getInstanceOfHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Function-Including-Regimen")
  public Object[][] parameterIntTest() {
    return new Object[][]{
      {"HIV", "storeInCharge", "ADULTS", "Admin123", "RegimenCode1", "RegimenName1", "RegimenCode2", "RegimenName2"}
    };
  }
}

