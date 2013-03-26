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

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EUpload extends TestCaseHelper {

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadCSVFiles(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add("Create Requisition");

    rolesPage.createRole("User", "User", userRoleList, true);

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadUsers();
    String userName = "User123";
    String userId = "200";
    dbWrapper.alterUserID(userName, userId);
    dbWrapper.insertRoleAssignment(userId, "User");

    uploadPage.uploadProductCategory();

    uploadPage.uploadProducts();

    uploadPage.uploadProgramProductMapping();

    uploadPage.uploadProgramProductPrice();

    uploadPage.uploadGeographicZoneInvalid();

    uploadPage.uploadGeographicZone(2);

    uploadPage.uploadFacilitiesNotLowestGeoCode();

    uploadPage.uploadFacilities();

    uploadPage.uploadFacilityTypeToProductMapping();
    dbWrapper.allocateFacilityToUser(userName, "F10");

    uploadPage.uploadProgramSupportedByFacilities();

    uploadPage.uploadSupervisoryNodes();

    uploadPage.uploadRequisitionGroup();

    dbWrapper.insertSchedules();
    dbWrapper.insertProcessingPeriods();

    uploadPage.uploadRequisitionGroupProgramSchedule();

    uploadPage.uploadRequisitionGroupMembers();

    uploadPage.uploadSupplyLines();
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
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
