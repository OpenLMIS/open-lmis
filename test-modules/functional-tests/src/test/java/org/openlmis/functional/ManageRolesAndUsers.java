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


  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageFacility(String user, String program, String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";
    String date_time = createFacilityPage.enterValuesInFacility(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy);
    String facility_code = facilityCodePrefix + date_time;
    String facility_name = facilityNamePrefix + date_time;
    createFacilityPage.verifyMessageOnFacilityScreen(facility_name, "created");

    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add("Create Requisition");
    userRoleList.add("Authorize Requisition");
    userRoleList.add("Approve Requisition");
    createRoleAndAssignRights(homePage, userRoleList, "Lab-in-charge", "Lab-in-charge", true);

    List<String> userRoleListLmu = new ArrayList<String>();
    userRoleListLmu.add("Convert To Order Requisition");
    userRoleListLmu.add("View Orders Requisition");
    createRoleAndAssignRights(homePage, userRoleListLmu, "lmu", "lmu", false);

    RolesPage rolesPage=new RolesPage(testWebDriver);
    rolesPage.clickARole("Lab-in-charge");
    rolesPage.verifyAdminRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleList);
    rolesPage.clickCancelButton();

    rolesPage.clickARole("lmu");
    rolesPage.verifyProgramRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleListLmu);
    rolesPage.clickCancelButton();

    dbWrapper.insertSupervisoryNode(facility_code, "N1", "Node 1", "null");

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    String userName = "labInCharge";
    createUserAndAssignRoles(homePage, passwordUsers, "Jake_Doe@openlmis.com", "Jake", "Doe", "lmu", facility_code, program, "Node 1", "lmu", true);
    UserPage userPage=new UserPage(testWebDriver);
    userPage.clickViewHere();
    userPage.removeRole(1, true);
    userPage.verifyRoleNotPresent("lmu");
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.verifyRoleNotPresent("lmu");
    userPage.clickCancelButton();

    createUserAndAssignRoles(homePage, passwordUsers, "Jasmine_Doe@openlmis.com", "Jasmine", "Doe", userName, facility_code, program, "Node 1", "Lab-in-charge", false);
    userPage.clickViewHere();
    userPage.removeRole(1, false);
    userPage.verifyRolePresent("Lab-in-charge");
    userPage.removeRole(1, false);
    userPage.verifyRoleNotPresent("Lab-in-charge");
    userPage.clickAllRemoveButton();
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.verifyRoleNotPresent("Lab-in-charge");
    userPage.verifyRemoveNotPresent();

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

  @AfterMethod(groups = {"functional"})
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
