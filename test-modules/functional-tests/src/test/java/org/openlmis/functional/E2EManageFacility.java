/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.DeleteFacilityPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;
import org.testng.annotations.Listeners;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EManageFacility extends TestCaseHelper {


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
    createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");

    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList();
    deleteFacilityPage.deleteFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    deleteFacilityPage.verifyDeletedFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    HomePage homePageRestore = deleteFacilityPage.restoreFacility();

    DeleteFacilityPage deleteFacilityPageRestore = homePageRestore.navigateSearchFacility();
    deleteFacilityPageRestore.searchFacility(date_time);
    deleteFacilityPageRestore.clickFacilityList();
    HomePage homePageEdit = deleteFacilityPageRestore.editAndVerifyFacility("ESSENTIAL MEDICINES", facilityNamePrefix + date_time);

    DeleteFacilityPage deleteFacilityPageEdit = homePageEdit.navigateSearchFacility();
    deleteFacilityPageEdit.searchFacility(date_time);

    ArrayList<String> programsSupported = new ArrayList<String>();
    programsSupported.add("HIV");
    programsSupported.add("ESSENTIAL MEDICINES");
    deleteFacilityPageEdit.verifyProgramSupported(programsSupported);


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
