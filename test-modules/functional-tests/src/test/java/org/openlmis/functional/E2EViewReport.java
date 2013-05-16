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

 @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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
      facilityMailingListReportPage.verifyPdfReportOutputOnFacilityMailingListScreen();
      testWebDriver.sleep(500);
      homePage.goBack();
      facilityMailingListReportPage.verifyMailingReportOutputOnFacilityMailingListScreen();
      homePage.goBack();
      facilityMailingListReportPage.verifyXlsReportOutputOnFacilityMailingListScreen();
      homePage.goBack();

 }


   @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
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


  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive" )
  public void verifySummaryReport(String[] credentials) throws Exception{

      setupRnRData(credentials);
      LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

      HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

      SummaryReportPage summaryReportPage = homePage.navigateViewSummaryReport();
      summaryReportPage.enterFilterValuesInSummaryReport("Period2");
      summaryReportPage.verifyHTMLReportOutputOnSummaryReportScreen();
      summaryReportPage.verifyPdfReportOutputOnSummaryReportScreen();
      testWebDriver.sleep(500);
      homePage.goBack();

  }

    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyNonReportingFacilityReport(String[] credentials) throws Exception{

        setupRnRData(credentials);
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        NonReportingFacilityReportPage nonReportingFacilityReportPage = homePage.navigateViewNonReportingFacilityReport();
        nonReportingFacilityReportPage.enterFilterValues("HIV","Monthly" , "Period2", "Requistion Group 2", "Lvl3 Hospital");
        nonReportingFacilityReportPage.verifyHTMLReportOutput();
        nonReportingFacilityReportPage.verifyPdfReportOutput();
        testWebDriver.sleep(500);
        homePage.goBack();
    }

    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyAverageConsumptionReport(String[] credentials) throws Exception{

        setupRnRData(credentials);
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        AverageConsumptionReportPage averageConsumptionReportPage = homePage.navigateViewAverageConsumptionReport();
        averageConsumptionReportPage.enterFilterValues("Monthly", "2013","Jan","2013","May","Root","Antibiotics","Lvl3 Hospital","Requistion Group 2","TDF/FTC/EFV","HIV");
        averageConsumptionReportPage.verifyHTMLReportOutput();
        averageConsumptionReportPage.verifyPdfReportOutput();
        testWebDriver.sleep(500);
        homePage.goBack();
    }

    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyAdjustmentSummaryReport(String[] credentials) throws Exception{

        //setupRnRData(credentials);
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        AdjustmentSummaryReportPage adjustmentSummaryReportPage = homePage.navigateViewAdjustmentSummaryReport();
        adjustmentSummaryReportPage.enterFilterValues("Monthly", "2013","Jan","2013","May","Root","Antibiotics","Lvl3 Hospital","Requistion Group 2","TDF/FTC/EFV","HIV", "Damaged");
        adjustmentSummaryReportPage.verifyHTMLReportOutput();
        adjustmentSummaryReportPage.verifyPdfReportOutput();
        testWebDriver.sleep(500);
        homePage.goBack();
    }

    private void setupRnRData(String[] credentials) throws IOException, SQLException {
        List<String> rightsList = new ArrayList<String>();
        rightsList.add("CREATE_REQUISITION");
        rightsList.add("VIEW_REQUISITION");
        setupTestDataToInitiateRnR(true, "HIV", credentials[2], "200", "openLmis", rightsList);
        dbWrapper.assignRight(STORE_IN_CHARGE, APPROVE_REQUISITION);
        dbWrapper.assignRight(STORE_IN_CHARGE, CONVERT_TO_ORDER);
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(credentials[2], credentials[3]);
        homePage.navigateAndInitiateRnr("HIV");
        InitiateRnRPage initiateRnRPage = homePage.clickProceed();
        HomePage homePage1 = initiateRnRPage.clickHome();

        ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
        viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
        dbWrapper.insertValuesInRequisition();
        dbWrapper.updateRequisitionStatus(SUBMITTED);
        viewRequisitionPage.enterViewSearchCriteria();
        viewRequisitionPage.clickSearch();
        viewRequisitionPage.verifyNoRequisitionFound();
        dbWrapper.updateRequisitionStatus(AUTHORIZED);
        viewRequisitionPage.clickSearch();
        viewRequisitionPage.clickRnRList();

        HomePage homePageAuthorized = viewRequisitionPage.verifyFieldsPreApproval("12.50", "1");
        ViewRequisitionPage viewRequisitionPageAuthorized = homePageAuthorized.navigateViewRequisition();
        viewRequisitionPageAuthorized.enterViewSearchCriteria();
        viewRequisitionPageAuthorized.clickSearch();
        viewRequisitionPageAuthorized.verifyStatus(AUTHORIZED);
        viewRequisitionPageAuthorized.clickRnRList();

        HomePage homePageInApproval = viewRequisitionPageAuthorized.verifyFieldsPreApproval("12.50", "1");
        dbWrapper.updateRequisitionStatus(IN_APPROVAL);
        ViewRequisitionPage viewRequisitionPageInApproval = homePageInApproval.navigateViewRequisition();
        viewRequisitionPageInApproval.enterViewSearchCriteria();
        viewRequisitionPageInApproval.clickSearch();
        viewRequisitionPageInApproval.verifyStatus(IN_APPROVAL);

        ApprovePage approvePageTopSNUser = homePageInApproval.navigateToApprove();
        approvePageTopSNUser.verifyAndClickRequisitionPresentForApproval();
        approvePageTopSNUser.editApproveQuantityAndVerifyTotalCostViewRequisition("20");
        approvePageTopSNUser.addComments("Dummy Comments");
        approvePageTopSNUser.approveRequisition();
        approvePageTopSNUser.clickOk();
        approvePageTopSNUser.verifyNoRequisitionPendingMessage();
        ViewRequisitionPage viewRequisitionPageApproved = homePageInApproval.navigateViewRequisition();
        viewRequisitionPageApproved.enterViewSearchCriteria();
        viewRequisitionPageApproved.clickSearch();
        viewRequisitionPageApproved.verifyStatus(APPROVED);
        viewRequisitionPageApproved.clickRnRList();
        viewRequisitionPageApproved.verifyComment("Dummy Comments", "storeincharge", 1);
        viewRequisitionPageApproved.verifyCommentBoxNotPresent();

        HomePage homePageApproved = viewRequisitionPageApproved.verifyFieldsPostApproval("25.00", "1");

        dbWrapper.updateRequisition("F10");
        ConvertOrderPage convertOrderPage = homePageApproved.navigateConvertToOrder();
        convertOrderPage.convertToOrder();
        ViewRequisitionPage viewRequisitionPageOrdered = homePageApproved.navigateViewRequisition();
        viewRequisitionPageOrdered.enterViewSearchCriteria();
        viewRequisitionPageOrdered.clickSearch();
        viewRequisitionPageOrdered.verifyStatus(RELEASED);
        viewRequisitionPageOrdered.clickRnRList();
        viewRequisitionPageOrdered.verifyFieldsPostApproval("25.00", "1");
        viewRequisitionPageOrdered.verifyApprovedQuantityFieldPresent();

        homePage = new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);

    }
  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    //dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
            {new String[]{"Admin123", "Admin123", "storeincharge", "Admin123"}}
    };
  }

}
