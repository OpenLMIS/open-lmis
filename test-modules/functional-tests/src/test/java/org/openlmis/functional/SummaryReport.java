/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.SummaryReportPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class SummaryReport extends ReportTestHelper {

    public static final String STORE_IN_CHARGE = "store in-charge";
    public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
    public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String AUTHORIZED = "AUTHORIZED";
    public static final String IN_APPROVAL = "IN_APPROVAL";
    public static final String APPROVED = "APPROVED";
    public static final String RELEASED = "RELEASED";

    public static final Integer COLUMN_NAME_CODE = 1;
    public static final Integer COLUMN_NAME_PRODUCT = 2;
    public static final Integer COLUMN_NAME_OPENING_BALANCE = 3;
    public static final Integer COLUMN_NAME_RECEIPTS = 4;
    public static final Integer COLUMN_NAME_ISSUES = 5;
    public static final Integer COLUMN_NAME_ADJUSTMENTS = 6;
    public static final Integer COLUMN_NAME_CLOSING_BALANCE = 7;
    public static final Integer COLUMN_NAME_MONTHS_OF_STOCK = 8;
    public static final Integer COLUMN_NAME_AMC = 9;
    public static final Integer COLUMN_NAME_MAXIMUM_STOCK = 10;
    public static final Integer COLUMN_NAME_REORDER_AMOUNT = 11;


    private SummaryReportPage summaryReportPage;

    @BeforeMethod(groups = {"functional3"})
    public void setUp() throws Exception {
        super.setup();
    }

    private void navigateToSummaryReportPage(String userName, String passWord) throws IOException {
        login(userName, passWord);
        summaryReportPage = homePage.navigateViewSummaryReport();
    }

    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportMenu(String[] credentials) throws IOException {
        verifyReportMenu(credentials);
    }


    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);

        System.out.println();
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityCodeIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityNameIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityTypeIsDisplayed());

        navigateToSummaryReportPage(credentials[0], credentials[1]);
        enterFilterValues();

    }

    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);
        summaryReportPage.verifyPdfReportOutput();
    }


    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);
        summaryReportPage.verifyXlsReportOutput();
    }

    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String[] credentials) throws IOException {
        navigateToSummaryReportPage(credentials[0], credentials[1]);

        Map<String, String> templates =     new HashMap<String, String>(){{
            put(SORT_BUTTON_ASC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(SORT_BUTTON_DESC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"test");
        }};

        verifySort("ASC", COLUMN_NAME_PRODUCT,templates);
        verifySort("ASC", COLUMN_NAME_CODE ,templates);
        verifySort("ASC", COLUMN_NAME_PRODUCT ,templates);
        verifySort("ASC", COLUMN_NAME_OPENING_BALANCE,templates);
        verifySort("ASC", COLUMN_NAME_RECEIPTS,templates);
        verifySort("ASC", COLUMN_NAME_ISSUES,templates);
        verifySort("ASC", COLUMN_NAME_ADJUSTMENTS,templates);
        verifySort("ASC", COLUMN_NAME_CLOSING_BALANCE,templates);
        verifySort("ASC", COLUMN_NAME_MONTHS_OF_STOCK,templates);
        verifySort("ASC", COLUMN_NAME_AMC,templates);
        verifySort("ASC", COLUMN_NAME_MAXIMUM_STOCK,templates);
        verifySort("ASC", COLUMN_NAME_REORDER_AMOUNT,templates);
    }


    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);


        Map<String, String> templates =     new HashMap<String, String>(){{
            put(PAGINATION_BUTTON_PREV_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div[3]/div/div[2]/div[2]/button[2]");
            put(PAGINATION_BUTTON_NEXT_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div[3]/div/div[2]/div[2]/button[3]");
            put(PAGINATION_BUTTON_FIRST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_LAST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifyPagination(templates);
    }

    public void enterFilterValues(){
        summaryReportPage.selectZoneByVisibleText("Arusha");
        summaryReportPage.enterName("Uhuru");
        summaryReportPage.selectFacilityTypeByVisibleText("Dispensary");
        summaryReportPage.selectProductByVisibleText("3TC/AZT/NVP (30mg/60mg/50mg) Tabs");
        summaryReportPage.selectRequisitionGroupByVisibleText("Korogwe Requestion group");
        summaryReportPage.selectProgramByVisibleText("ARV");
        //summaryReportPage.selectPeriodByVisibleText("");
        summaryReportPage.selectScheduleByVisibleText("Group A");
        summaryReportPage.selectPeriodByVisibleText("Oct-Dec");

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
                {new String[]{"msolomon", "Admin123", "storeincharge", "Admin123"}}
        };
    }

}
