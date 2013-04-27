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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EViewReport extends TestCaseHelper {

  public static final String STORE_IN_CHARGE = "store in-charge";
  public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
  public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
  public static final String SUBMITTED = "SUBMITTED";
  public static final String AUTHORIZED = "AUTHORIZED";
  public static final String IN_APPROVAL = "IN_APPROVAL";
  public static final String APPROVED = "APPROVED";
  public static final String RELEASED = "RELEASED";

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive", priority = 1)
  public void verifyFacilityMailingListReport(String[] credentials) throws Exception {

      String geoZone = "Ngorongoro";
      String facilityType = "Lvl3 Hospital";
      String facilityCodePrefix = "FCcode";
      String facilityNamePrefix = "FCname";

      Date dObj = new Date();
      SimpleDateFormat formatter_date_time = new SimpleDateFormat(
              "yyyyMMdd-hhmmss");
      String date_time = formatter_date_time.format(dObj);

      dbWrapper.insertFacility(facilityNamePrefix + date_time, facilityCodePrefix + date_time, facilityType, geoZone);

      LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

      HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

      FacilityMailingListReportPage facilityMailingListReportPage = homePage.navigateViewFacilityMailingListReport();
      facilityMailingListReportPage.enterFilterValuesInFacilityMailingListReport(facilityNamePrefix + date_time, facilityCodePrefix + date_time, facilityType);
      facilityMailingListReportPage.verifyHTMLReportOutputOnFacilityMailingListScreen();

    //  facilityMailingListReportPage.verifyPdfReportOutputOnFacilityMailingListScreen();
  }


   @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive", priority = 2)
   public void verifyFacilityListingReport(String[] credentials) throws Exception{

       String geoZone = "Ngorongoro";
       String facilityType = "Lvl3 Hospital";
       String facilityCodePrefix = "FCcode";
       String facilityNamePrefix = "FCname";
       String status = "true";

       Date dObj = new Date();
       SimpleDateFormat formatter_date_time = new SimpleDateFormat(
               "yyyyMMdd-hhmmss");
       String date_time = formatter_date_time.format(dObj);

       dbWrapper.insertFacility(facilityNamePrefix + date_time, facilityCodePrefix + date_time, facilityType, geoZone);

       LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

       HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

       FacilityListingReportPage facilityListingReportPage = homePage.navigateViewFacilityListingReport();
       facilityListingReportPage.enterFilterValuesInFacilityListingReport(geoZone, facilityType, status);
       facilityListingReportPage.verifyHTMLReportOutputOnFacilityListingScreen();
   }


  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive", priority = 0)
  public void verifySummaryReport(String[] credentials) throws Exception{
      List<String> rightsList = new ArrayList<String>();
      rightsList.add("CREATE_REQUISITION");
      rightsList.add("VIEW_REQUISITION");
      setupTestDataToInitiateRnR(true, "HIV", credentials[2], "200", "openLmis", rightsList);
      dbWrapper.assignRight(STORE_IN_CHARGE, APPROVE_REQUISITION);
      dbWrapper.assignRight(STORE_IN_CHARGE, CONVERT_TO_ORDER);
      dbWrapper.insertValuesInRequisition();
      dbWrapper.updateRequisitionStatus(SUBMITTED);
      dbWrapper.updateRequisitionStatus(AUTHORIZED);
      dbWrapper.updateRequisitionStatus(IN_APPROVAL);

      LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

      HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
      SummaryReportPage summaryReportPage = homePage.navigateViewSummaryReport();
      summaryReportPage.enterFilterValuesInSummaryReport("period");
      summaryReportPage.verifyHTMLReportOutputOnSummaryReportScreen();
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
            {new String[]{"Admin123", "Admin123","storeincharge", "Admin123"}}
    };
  }

}
