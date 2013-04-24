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
  public static final String LMU = "lmu";
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
  public void testE2EManageRolesAndFacility(String user, String program, String[] credentials) throws Exception {
    System.out.println("brute force"+"1");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    System.out.println("brute force"+"2");
    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    System.out.println("brute force"+"3");
    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String date_time = createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy);
    String facility_code = facilityCodePrefix + date_time;
    String facility_name = facilityNamePrefix + date_time;
    createFacilityPage.verifyMessageOnFacilityScreen(facility_name, "created");
    System.out.println("brute force"+"4");
    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add(CREATE_REQUISITION);
    userRoleList.add(AUTHORIZE_REQUISITION);
    userRoleList.add(APPROVE_REQUISITION);
    createRoleAndAssignRights(homePage, userRoleList, LAB_IN_CHARGE, LAB_IN_CHARGE, true);
    System.out.println("brute force"+"5");
    List<String> userRoleListLmu = new ArrayList<String>();
    userRoleListLmu.add(CONVERT_TO_ORDER_REQUISITION);
    userRoleListLmu.add(VIEW_ORDER_REQUISITION);
    createRoleAndAssignRights(homePage, userRoleListLmu, "lmu", "lmu", false);
    System.out.println("brute force"+"6");
    RolesPage rolesPage = new RolesPage(testWebDriver);
    rolesPage.clickARole(LAB_IN_CHARGE);
    rolesPage.verifyAdminRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleList);
    rolesPage.clickCancelButton();
    System.out.println("brute force"+"7");
    rolesPage.clickARole(LMU);
    rolesPage.verifyProgramRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleListLmu);
    rolesPage.clickCancelButton();
    System.out.println("brute force"+"8");
    dbWrapper.insertSupervisoryNode(facility_code, "N1", "Node 1", "null");
    System.out.println("brute force"+"9");
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    createUserAndAssignRoles(homePage, passwordUsers, "Jake_Doe@openlmis.com", "Jake", "Doe", LMU, facility_code, program, "Node 1", LMU, true);
    UserPage userPage = new UserPage(testWebDriver);
    userPage.clickViewHere();
    userPage.removeRole(1, true);
    System.out.println("brute force"+"10");
    userPage.verifyRoleNotPresent(LMU);
    System.out.println("brute force"+"11");
    userPage.clickSaveButton();
    userPage.clickViewHere();
    System.out.println("brute force"+"12");
    userPage.verifyRoleNotPresent(LMU);
    System.out.println("brute force"+"13");
    userPage.clickCancelButton();
    System.out.println("brute force"+"14");

    createUserAndAssignRoles(homePage, passwordUsers, "Jasmine_Doe@openlmis.com", "Jasmine", "Doe", LAB_IN_CHARGE, facility_code, program, "Node 1", LAB_IN_CHARGE, false);
    userPage.clickViewHere();
    userPage.removeRole(1, false);
    userPage.verifyRolePresent(LAB_IN_CHARGE);
    userPage.removeRole(1, false);
    System.out.println("brute force"+"15");
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    System.out.println("brute force"+"16");
    userPage.clickAllRemoveButton();
    userPage.clickSaveButton();
    System.out.println("brute force"+"Before clickViewHere");
    userPage.clickViewHere();
    System.out.println("brute force"+"After clickViewHere and before verifyRoleNotPresent");
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    System.out.println("brute force"+"After verifyRoleNotPresent and before verifyRemoveNotPresent");
    userPage.verifyRemoveNotPresent();
    System.out.println("brute force"+"After verifyRemoveNotPresent and before navigateUploads");
    homePage.navigateUploads();
    System.out.println("brute force"+"After navigateUploads");

  }

  private String createUserAndAssignRoles(HomePage homePage, String passwordUsers, String userEmail, String userFirstName, String userLastName, String userUserName, String facility, String program, String supervisoryNode, String role, boolean adminRole) throws IOException, SQLException {
    UserPage userPage = homePage.navigateToUser();
    String userID = userPage.enterAndverifyUserDetails(userUserName, userEmail, userFirstName, userLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, userEmail);
    userPage.enterMyFacilityAndMySupervisedFacilityData(userFirstName, userLastName, facility, program, supervisoryNode, role, adminRole);
    return userID;
  }

  private void createRoleAndAssignRights(HomePage homePage, List<String> userRoleList, String roleName, String roleDescription, boolean programDependent) throws IOException {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createRole(roleName, roleDescription, userRoleList, programDependent);
  }

  @AfterMethod(groups = {"functional2"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
    System.out.println("brute force"+"Inside tearDown");
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}
