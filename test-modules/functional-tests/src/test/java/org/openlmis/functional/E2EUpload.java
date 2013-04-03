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
    uploadPage.uploadAndVerifyInvalidUserScenarios();
    uploadPage.uploadAndVerifyUsers(2);
    String userName = "User123";
    String userId = "200";
    dbWrapper.alterUserID(userName, userId);
    dbWrapper.insertRoleAssignment(userId, "User");

    uploadPage.uploadProductCategory();
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadAndVerifyProductsInvalidScenarios();
    uploadPage.uploadAndVerifyProducts(2);

    uploadPage.uploadAndVerifyProgramProductMappingInvalidScenarios();
    uploadPage.uploadAndVerifyProgramProductMapping(2);

    uploadPage.uploadProgramProductPrice();
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadGeographicZoneInvalid();

    uploadPage.uploadAndVerifyGeographicZone(2);

    uploadPage.uploadFacilitiesNotAssignedToLowestGeoCode();
    uploadPage.uploadAndVerifyFacilitiesInvalidScenarios();
    uploadPage.uploadAndVerifyFacilities(2);

    uploadPage.uploadAndVerifyFacilityTypeToProductMappingInvalidScenarios();
    uploadPage.uploadAndVerifyFacilityTypeToProductMapping(2);
    dbWrapper.allocateFacilityToUser(userId, "F10");

    uploadPage.uploadProgramSupportedByFacilitiesInvalidScenarios();
    uploadPage.uploadProgramSupportedByFacilities(2);

    uploadPage.uploadSupervisoryNodes();
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadRequisitionGroup();
    uploadPage.verifySuccessMessageOnUploadScreen();

    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
      dbWrapper.insertProcessingPeriod("Period1", "first period", "2012-12-01", "2013-01-15", 1, "Q1stM");
      dbWrapper.insertProcessingPeriod("Period2", "second period", "2013-01-16", "2013-01-30", 1, "M");

    uploadPage.uploadRequisitionGroupProgramSchedule();
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadRequisitionGroupMembers();
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadSupplyLines();
    uploadPage.verifySuccessMessageOnUploadScreen();
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
