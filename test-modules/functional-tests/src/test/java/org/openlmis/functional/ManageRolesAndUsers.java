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
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String date_time = createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy);
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
    rolesPage.clickCancelButton();
    rolesPage.clickARole(LMU);
    rolesPage.verifyProgramRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleListLmu);

    rolesPage.clickCancelButton();
    dbWrapper.insertSupervisoryNode(facility_code, "N1", "Node 1", "null");
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    createUserAndAssignRoles(homePage, passwordUsers, "Jake_Doe@openlmis.com", "Jake", "Doe", LMU, facility_code, program, "Node 1", LMU, true);
    UserPage userPage = new UserPage(testWebDriver);
    userPage.clickViewHere();
    userPage.removeRole(1, true);
    userPage.verifyRoleNotPresent(LMU);
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.verifyRoleNotPresent(LMU);
    System.out.println("brute force" + "14");
    createUserAndAssignRoles(homePage, passwordUsers, "Jasmine_Doe@openlmis.com", "Jasmine", "Doe", LAB_IN_CHARGE, facility_code, program, "Node 1", LAB_IN_CHARGE, false);
    System.out.println("brute force" + "15");
    userPage.clickViewHere();
    System.out.println("brute force" + "16");
    userPage.removeRole(1, false);
    System.out.println("brute force" + "17");
    userPage.verifyRolePresent(LAB_IN_CHARGE);
    System.out.println("brute force" + "18");
    userPage.removeRole(1, false);
    System.out.println("brute force" + "19");
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    System.out.println("brute force" + "20");
    userPage.clickAllRemoveButton();
    userPage.clickSaveButton();
    System.out.println("brute force" + "21");
    userPage.clickViewHere();
    System.out.println("brute force" + "22");
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    System.out.println("brute force" + "23");
    userPage.verifyRemoveNotPresent();
    System.out.println("brute force" + "24");
    homePage.navigateUploads();
  }

  private String createUserAndAssignRoles(HomePage homePage, String passwordUsers, String userEmail, String userFirstName, String userLastName, String userUserName, String facility, String program, String supervisoryNode, String role, boolean adminRole) throws IOException, SQLException {
    System.out.println("brute force" + "14.1");
    UserPage userPage = homePage.navigateToUser();
    System.out.println("brute force" + "14.2");
    String userID = userPage.enterAndverifyUserDetails(userUserName, userEmail, userFirstName, userLastName, baseUrlGlobal, dburlGlobal);
    System.out.println("brute force" + "14.3");
    dbWrapper.updateUser(passwordUsers, userEmail);
    System.out.println("brute force" + "14.4");
    userPage.enterMyFacilityAndMySupervisedFacilityData(userFirstName, userLastName, facility, program, supervisoryNode, role, adminRole);
    System.out.println("brute force" + "14.5");
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
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}
