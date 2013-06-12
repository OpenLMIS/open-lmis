/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EInitiateRnR extends TestCaseHelper {

  @BeforeMethod(groups = {"smoke"})
  public void setUp() throws Exception {
    super.setup();
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }

  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EInitiateRnR(String program, String userSIC, String userMO, String userlmu, String password, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String geoZone = "Ngorongoro";
    String parentGeoZone = "Dodoma";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";

    String date_time = createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy);
    createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
    String facility_code = facilityCodePrefix + date_time;
    String facility_name = facilityNamePrefix + date_time;
    dbWrapper.insertFacilities("F10", "F11");


    List<String> userRoleListStoreInCharge = new ArrayList<String>();
    userRoleListStoreInCharge.add("Create Requisition");
    userRoleListStoreInCharge.add("Authorize Requisition");
    userRoleListStoreInCharge.add("Approve Requisition");
    createRoleAndAssignRights(homePage, userRoleListStoreInCharge, "Store-in-charge", "Store-in-charge", true);

    List<String> userRoleListLmu = new ArrayList<String>();
    userRoleListLmu.add("Convert To Order Requisition");
    userRoleListLmu.add("View Orders Requisition");
    createRoleAndAssignRights(homePage, userRoleListLmu, "lmu", "lmu", false);

    List<String> userRoleListMedicalOfficer = new ArrayList<String>();
    userRoleListMedicalOfficer.add("Approve Requisition");
    createRoleAndAssignRights(homePage, userRoleListMedicalOfficer, "Medical-officer", "Medical-officer", true);


    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "Node 2", "N1");

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    String userSICUserName = "storeincharge";
    String userIDSIC = createUserAndAssignRoles(homePage, passwordUsers, "Fatima_Doe@openlmis.com", "Fatima", "Doe", userSICUserName, "F10", program, "Node 1", "Store-in-charge", false);
    createUserAndAssignRoles(homePage, passwordUsers, "Jake_Doe@openlmis.com", "Jake", "Doe", "lmu", "F10", program, "Node 1", "lmu", true);
    createUserAndAssignRoles(homePage, passwordUsers, "Jane_Doe@openlmis.com", "Jane", "Doe", "medicalofficer", "F11", program, "Node 2", "Medical-Officer", false);

    dbWrapper.updateRoleGroupMember(facility_code);
    setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
    dbWrapper.insertRequisitionGroupMembers("F10", facility_code);

    ManageSchedulePage manageSchedulePage = homePage.navigateToSchedule();
    manageSchedulePage.createAndVerifySchedule();
    manageSchedulePage.editAndVerifySchedule();
    PeriodsPage periodsPage = manageSchedulePage.navigatePeriods();
    periodsPage.createAndVerifyPeriods();
    periodsPage.deleteAndVerifyPeriods();

    dbWrapper.insertRequisitionGroupProgramSchedule();
    dbWrapper.allocateFacilityToUser(userIDSIC, facility_code);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.configureTemplate();

    dbWrapper.insertSupplyLines("N1", program, facilityCodePrefix + date_time);

    LoginPage loginPageSecond = homePage.logout(baseUrlGlobal);
    HomePage homePageUser = loginPageSecond.loginAs(userSIC, password);

    String periodDetails = homePageUser.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePageUser.clickProceed();
    initiateRnRPage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
    initiateRnRPage.submitRnR();
    initiateRnRPage.verifySubmitRnrErrorMsg();
    initiateRnRPage.calculateAndVerifyStockOnHand(10, 10, 10, 1);
    initiateRnRPage.verifyTotalField();

    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();

    initiateRnRPage.clickCommentsButton();
    initiateRnRPage.typeCommentsInCommentsTextArea("Test comment.");
    initiateRnRPage.closeCommentPopUp();
    initiateRnRPage.clickCommentsButton();
    initiateRnRPage.verifyValueInCommentsTextArea("");
    initiateRnRPage.closeCommentPopUp();
    initiateRnRPage.addComments("Dummy Comments.");
    initiateRnRPage.verifyComment("Dummy Comments.", userSICUserName, 1);

    initiateRnRPage.enterValuesAndVerifyCalculatedOrderQuantity(10, 10, 101, 51, 153, 142);
    initiateRnRPage.verifyPacksToShip(15);

    initiateRnRPage.enterAndVerifyRequestedQuantityExplanation(10);
    initiateRnRPage.verifyPacksToShip(1);
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.saveRnR();

    initiateRnRPage.addNonFullSupplyLineItems("99", "Due to unforeseen event", "antibiotic", "P11", "Antibiotics", baseUrlGlobal, dburlGlobal);
    initiateRnRPage.calculateAndVerifyTotalCostNonFullSupply();
    initiateRnRPage.verifyCostOnFooter();

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
//    initiateRnRPage.clickFullSupplyTab();
//    initiateRnRPage.verifyTotalField();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    initiateRnRPage.verifyApproveButtonNotPresent();

    ApprovePage approvePage = homePageUser.navigateToApprove();
    approvePage.verifyNoRequisitionPendingMessage();
    LoginPage loginPagethird = homePageUser.logout(baseUrlGlobal);

    HomePage homePageLowerSNUser = loginPagethird.loginAs(userMO, password);
    ApprovePage approvePageLowerSNUser = homePageLowerSNUser.navigateToApprove();
    approvePageLowerSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageLowerSNUser.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
    approvePageLowerSNUser.verifyApprovedQuantity();
    approvePageLowerSNUser.editApproveQuantityAndVerifyTotalCost("290");
    approvePageLowerSNUser.clickCommentsButton();
    approvePageLowerSNUser.typeCommentsInCommentsTextArea("Test comment.");
    approvePageLowerSNUser.closeCommentPopUp();
    approvePageLowerSNUser.clickCommentsButton();
    approvePageLowerSNUser.verifyValueInCommentsTextArea("");
    approvePageLowerSNUser.closeCommentPopUp();
    approvePageLowerSNUser.addComments("This is urgent");
    approvePageLowerSNUser.verifyComment("This is urgent", userMO, 2);
    approvePageLowerSNUser.clickFullSupplyTab();
    approvePageLowerSNUser.verifyTotalFieldPostAuthorize();
    approvePageLowerSNUser.clickSaveButton();
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.clickOk();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
    LoginPage loginPageTopSNUser = homePageLowerSNUser.logout(baseUrlGlobal);

    HomePage homePageTopSNUser = loginPageTopSNUser.loginAs(userSIC, password);

    ApprovePage approvePageTopSNUser = homePageTopSNUser.navigateToApprove();
    String periodTopSNUser = approvePageTopSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageTopSNUser.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
    approvePageTopSNUser.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
    approvePageTopSNUser.editApproveQuantityAndVerifyTotalCost("100");
    approvePageLowerSNUser.clickFullSupplyTab();
    approvePageTopSNUser.verifyTotalFieldPostAuthorize();
    approvePageTopSNUser.approveRequisition();
    approvePageTopSNUser.clickOk();
    approvePageTopSNUser.verifyNoRequisitionPendingMessage();

    LoginPage loginPagelmu = homePageTopSNUser.logout(baseUrlGlobal);
    HomePage homePagelmu = loginPagelmu.loginAs(userlmu, password);

    ConvertOrderPage convertOrderPageOrdersPending = homePagelmu.navigateConvertToOrder();
    String[] periods = periodTopSNUser.split("-");
    String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", program);
    convertOrderPageOrdersPending.verifyOrderListElements(program, facility_code, facility_name, periods[0].trim(), periods[1].trim(), supplyFacilityName);
    verifyConvertToOrder(convertOrderPageOrdersPending);

    ViewOrdersPage viewOrdersPage = homePagelmu.navigateViewOrders();
    String requisitionId = dbWrapper.getLatestRequisitionId();
    viewOrdersPage.verifyOrderListElements(program, requisitionId, facility_code + " - " + facility_name, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "RELEASED", true);
    dbWrapper.updatePacksToShip("0");
    homePagelmu.navigateConvertToOrder();
    homePagelmu.navigateViewOrders();
    viewOrdersPage.verifyOrderListElements(program, requisitionId, facility_code + " - " + facility_name, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "RELEASED", false);

  }

  private String createUserAndAssignRoles(HomePage homePage, String passwordUsers, String userEmail, String userFirstName, String userLastName, String userUserName, String facility, String program, String supervisoryNode, String role, boolean adminRole) throws IOException, SQLException {
    UserPage userPage = homePage.navigateToUser();
    String userID = userPage.enterAndVerifyUserDetails(userUserName, userEmail, userFirstName, userLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, userEmail);
    userPage.enterMyFacilityAndMySupervisedFacilityData(userFirstName, userLastName, facility, program, supervisoryNode, role, adminRole);
    return userID;
  }

  private void verifyConvertToOrder(ConvertOrderPage convertOrderPageOrdersPending) {
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.verifyMessageOnOrderScreen("Message 'Please select at least one Requisition for Converting to Order.' is not displayed");
    convertOrderPageOrdersPending.clickCheckBoxConvertToOrder();
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.clickOk();
  }

  private void createRoleAndAssignRights(HomePage homePage, List<String> userRoleList, String roleName, String roleDescription, boolean programDependent) throws IOException {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createRole(roleName, roleDescription, userRoleList, programDependent);
  }

  @AfterMethod(groups = {"smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "medicalofficer", "lmu", "Admin123", new String[]{"Admin123", "Admin123"}}
    };

  }
}

