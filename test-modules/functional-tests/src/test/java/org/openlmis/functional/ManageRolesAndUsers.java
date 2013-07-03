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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRolesAndUsers extends TestCaseHelper {

  public static final String LAB_IN_CHARGE = "Lab-in-charge";
  public static final String AUTHORIZE_REQUISITION = "Authorize Requisition";
  public static final String CREATE_REQUISITION = "Create Requisition";
  public static final String APPROVE_REQUISITION = "Approve Requisition";
  public static final String CONVERT_TO_ORDER_REQUISITION = "Convert To Order Requisition";
  public static final String VIEW_ORDER_REQUISITION = "View Orders Requisition";
  public static final String MANAGE_DISTRIBUTION = "Manage Distribution";
  public static final String LMU = "lmu";
  public static final String ADMIN = "admin";
  public static final String geoZone = "Ngorongoro";
  public static final String facilityType = "Lvl3 Hospital";
  public static final String operatedBy = "MoH";
  public static final String facilityCodePrefix = "FCcode";
  public static final String facilityNamePrefix = "FCname";

  @BeforeMethod(groups = {"functional2"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Positive")
  public void testVerifyRightsUponOK(String user, String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.getCreateNewRoleButton().click();
    testWebDriver.waitForElementToAppear(rolesPage.getAllocationRoleType());
    assertEquals(rolesPage.getWebElementMap().get(MANAGE_DISTRIBUTION).isEnabled(), false);
    assertEquals(rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION).isEnabled(), true);
    assertEquals(rolesPage.getWebElementMap().get(APPROVE_REQUISITION).isEnabled(), false);
    testWebDriver.handleScrollByPixels(0,3000);
    testWebDriver.waitForElementToAppear(rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION));
    rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION).click();
    rolesPage.getAllocationRoleType().click();
    rolesPage.clickContinueButton();
    assertEquals(rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION).isSelected(), false);
    assertEquals(rolesPage.getWebElementMap().get(APPROVE_REQUISITION).isEnabled(), false);
    assertEquals(rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION).isEnabled(), false);
    assertEquals(rolesPage.getWebElementMap().get(MANAGE_DISTRIBUTION).isEnabled(), true);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Positive")
  public void testVerifyRightsUponCancel(String user, String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.getCreateNewRoleButton().click();
    testWebDriver.waitForElementToAppear(rolesPage.getAllocationRoleType());
    rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION).click();
    rolesPage.getAllocationRoleType().click();
    rolesPage.clickCancelButtonOnModal();
    assertEquals(rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION).isSelected(), true);
    assertEquals(rolesPage.getWebElementMap().get(APPROVE_REQUISITION).isEnabled(), false);
    assertEquals(rolesPage.getWebElementMap().get(CONVERT_TO_ORDER_REQUISITION).isEnabled(), true);
    assertEquals(rolesPage.getWebElementMap().get(MANAGE_DISTRIBUTION).isEnabled(), false);
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Positive")
  public void testVerifyDuplicateRoleName(String user, String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add(CONVERT_TO_ORDER_REQUISITION);
    rolesPage.createRole(ADMIN, ADMIN,userRoleList, false);
    assertEquals(rolesPage.getSaveErrorMsgDiv().getText().trim(),"Duplicate Role found");
  }

  @Test(groups = {"functional2"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageRolesAndFacility(String user, String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, "500000");
    String facility_code = facilityCodePrefix + date_time;
    String facility_name = facilityNamePrefix + date_time;
    createFacilityPage.verifyMessageOnFacilityScreen(facility_name, "created");

    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add(CREATE_REQUISITION);
    userRoleList.add(AUTHORIZE_REQUISITION);
    userRoleList.add(APPROVE_REQUISITION);
    createRoleAndAssignRights(homePage, userRoleList, LAB_IN_CHARGE, LAB_IN_CHARGE, true);

    List<String> userRoleListLmu = new ArrayList<String>();
    userRoleListLmu.add(CONVERT_TO_ORDER_REQUISITION);
    userRoleListLmu.add(VIEW_ORDER_REQUISITION);
    createRoleAndAssignRights(homePage, userRoleListLmu, LMU, LMU, false);

    RolesPage rolesPage = new RolesPage(testWebDriver);
    rolesPage.clickARole(LAB_IN_CHARGE);
    rolesPage.verifyAdminRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleList);
    homePage.navigateRoleAssignments();
    rolesPage.clickARole(LMU);
    rolesPage.verifyProgramRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleListLmu);
    dbWrapper.insertSupervisoryNode(facility_code, "N1", "Node 1", "null");

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPage = new UserPage(testWebDriver);
    createUserAndAssignRoles(homePage, passwordUsers, "Jasmine_Doe@openlmis.com", "Jasmine", "Doe", LAB_IN_CHARGE, facility_code, program, "Node 1", LAB_IN_CHARGE, "REQUISITION");
    userPage.clickViewHere();
    userPage.removeRole(1, false);
    userPage.verifyRolePresent(LAB_IN_CHARGE);
    userPage.removeRole(1, false);
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    userPage.clickAllRemoveButton();
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    userPage.verifyRemoveNotPresent();
    verifyPUSHProgramNotAvailableForHomeFacilityRolesAndSupervisoryRoles(userPage);

  }

  private String createUserAndAssignRoles(HomePage homePage, String passwordUsers, String userEmail, String userFirstName, String userLastName, String userUserName, String facility, String program, String supervisoryNode, String role, String roleType) throws IOException, SQLException {
    UserPage userPage = homePage.navigateToUser();
    String userID = userPage.enterAndVerifyUserDetails(userUserName, userEmail, userFirstName, userLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, userEmail);
    userPage.enterMyFacilityAndMySupervisedFacilityData(userFirstName, userLastName, facility, program, supervisoryNode, role, roleType);
    return userID;
  }

  private void createRoleAndAssignRights(HomePage homePage, List<String> userRoleList, String roleName, String roleDescription, boolean programDependent) throws IOException {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createRoleWithSuccessMessageExpected(roleName, roleDescription, userRoleList, programDependent);
  }

  private void verifyPUSHProgramNotAvailableForHomeFacilityRolesAndSupervisoryRoles(UserPage userPage) throws IOException, SQLException {
    assertFalse(userPage.getAllProgramsHomeFacility().contains("VACCINES"));
    assertFalse(userPage.getAllProgramsToSupervise().contains("VACCINES"));
  }

  @AfterMethod(groups = {"functional2"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}
