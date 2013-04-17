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
import org.testng.annotations.*;

import java.io.IOException;
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
    String parentgeoZone = "Dodoma";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";

    String date_time = createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy);
    createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
    String facility_code = "FCcode" + date_time;
    dbWrapper.insertFacilities("F10", "F11");

    createRole(homePage);

    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "Node 2", "N1");

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPageSIC = homePage.navigateToUser();
    String userSICEmail = "Fatima_Doe@openlmis.com";
    String userSICFirstName = "Fatima";
    String userSICLastName = "Doe";
    String userSICUserName = "storeincharge";
    String userIDSIC = userPageSIC.enterAndverifyUserDetails(userSICUserName, userSICEmail, userSICFirstName, userSICLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, userSICEmail);
    userPageSIC.enterMyFacilityAndMySupervisedFacilityData(userSICFirstName, userSICLastName, "F10", "HIV", "Node 1", "Store-in-charge", false);

    String passwordUserslmu = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPagelmu = homePage.navigateToUser();
    String userlmuEmail = "Lmu_Doe@openlmis.com";
    String userlmuFirstName = "Lmu";
    String userlmuLastName = "Doe";
    String userlmuUserName = "lmu";
    userPagelmu.enterAndverifyUserDetails(userlmuUserName, userlmuEmail, userlmuFirstName, userlmuLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUserslmu, userlmuEmail);
    userPagelmu.enterMyFacilityAndMySupervisedFacilityData(userlmuFirstName, userlmuLastName, "F10", "HIV", "Node 1", "lmu", true);

    UserPage userPageMO = homePage.navigateToUser();
    String userMOEmail = "Jane_Doe@openlmis.com";
    String userMOFirstName = "Jane";
    String userMOLastName = "Doe";
    String userMOUserName = "medicalofficer";
    userPageMO.enterAndverifyUserDetails(userMOUserName, userMOEmail, userMOFirstName, userMOLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, userMOEmail);
    userPageMO.enterMyFacilityAndMySupervisedFacilityData(userMOFirstName, userMOLastName, "F11", "HIV", "Node 2", "Medical-Officer", false);

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

    dbWrapper.insertSupplyLines("N1", "HIV", "FCcode" + date_time);

    LoginPage loginPageSecond = homePage.logout(baseUrlGlobal);
    HomePage homePageUser = loginPageSecond.loginAs(userSIC, password);

    String periodDetails = homePageUser.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePageUser.clickProceed();
    initiateRnRPage.verifyRnRHeader("FCcode", "FCname", date_time, program, periodDetails, geoZone, parentgeoZone, operatedBy, facilityType);
    initiateRnRPage.submitRnR();
    initiateRnRPage.verifySubmitRnrErrorMsg();
    initiateRnRPage.calculateAndVerifyStockOnHand(10, 10, 10, 1);

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
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    initiateRnRPage.verifyApproveButtonNotPresent();

    ApprovePage approvePage = homePageUser.navigateToApprove();
    approvePage.verifyNoRequisitionPendingMessage();
    LoginPage loginPagethird = homePageUser.logout(baseUrlGlobal);

    HomePage homePageLowerSNUser = loginPagethird.loginAs(userMO, password);
    ApprovePage approvePageLowerSNUser = homePageLowerSNUser.navigateToApprove();
    approvePageLowerSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageLowerSNUser.verifyRnRHeader("FCcode", "FCname", date_time, program, periodDetails, geoZone, parentgeoZone, operatedBy, facilityType);
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
    approvePageLowerSNUser.clickSaveButton();
    approvePageLowerSNUser.clickApproveButton();
    approvePageLowerSNUser.clickOk();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
    LoginPage loginPageTopSNUser = homePageLowerSNUser.logout(baseUrlGlobal);

    HomePage homePageTopSNUser = loginPageTopSNUser.loginAs(userSIC, password);

    ApprovePage approvePageTopSNUser = homePageTopSNUser.navigateToApprove();
    String periodTopSNUser = approvePageTopSNUser.verifyAndClickRequisitionPresentForApproval();
    approvePageTopSNUser.verifyRnRHeader("FCcode", "FCname", date_time, program, periodDetails, geoZone, parentgeoZone, operatedBy, facilityType);
    approvePageTopSNUser.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
    approvePageTopSNUser.editApproveQuantityAndVerifyTotalCost("100");
    approvePageTopSNUser.approveRequisition();
    approvePageTopSNUser.clickOk();
    approvePageTopSNUser.verifyNoRequisitionPendingMessage();

    LoginPage loginPagelmu = homePageTopSNUser.logout(baseUrlGlobal);
    HomePage homePagelmu = loginPagelmu.loginAs(userlmu, password);

    ConvertOrderPage convertOrderPageOrdersPending = homePagelmu.navigateConvertToOrder();
    String[] periods = periodTopSNUser.split("-");
    String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", "HIV");
    convertOrderPageOrdersPending.verifyOrderListElements(program, "FCcode" + date_time, "FCname" + date_time, periods[0].trim(), periods[1].trim(), supplyFacilityName);
    verifyConvertToOrder(convertOrderPageOrdersPending);

    ViewOrdersPage viewOrdersPage = homePagelmu.navigateViewOrders();
    String requisitionId = dbWrapper.getLatestRequisitionId();
    viewOrdersPage.verifyOrderListElements(program, "ORD" + requisitionId, "FCcode" + date_time + " - " + "FCname" + date_time, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "RELEASED");
  }

  private void verifyConvertToOrder(ConvertOrderPage convertOrderPageOrdersPending) {
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.verifyMessageOnOrderScreen("Message 'Please select at least one Requisition for Converting to Order.' is not displayed");
    convertOrderPageOrdersPending.clickCheckBoxConvertToOrder();
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.clickOk();
  }

  private void createRole(HomePage homePage) throws IOException {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleListStoreincharge = new ArrayList<String>();
    userRoleListStoreincharge.add("Create Requisition");
    userRoleListStoreincharge.add("Authorize Requisition");
    userRoleListStoreincharge.add("Approve Requisition");
    rolesPage.createRole("Store-in-charge", "Store-in-charge", userRoleListStoreincharge, true);

    RolesPage rolesPagelmu = homePage.navigateRoleAssignments();
    List<String> userRoleListlmu = new ArrayList<String>();

    userRoleListlmu.add("Convert To Order Requisition");
    userRoleListlmu.add("View Orders Requisition");
    rolesPagelmu.createRole("lmu", "lmu", userRoleListlmu, false);

    List<String> userRoleListMedicalofficer = new ArrayList<String>();
    userRoleListMedicalofficer.add("Approve Requisition");
    rolesPage.createRole("Medical-officer", "Medical-officer", userRoleListMedicalofficer, true);
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

