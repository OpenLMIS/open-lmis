/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.StockedOutReportPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class StockedOutReport extends ReportTestHelper {

    public static final String STORE_IN_CHARGE = "store in-charge";
    public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
    public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String AUTHORIZED = "AUTHORIZED";
    public static final String IN_APPROVAL = "IN_APPROVAL";
    public static final String APPROVED = "APPROVED";
    public static final String RELEASED = "RELEASED";
    public static final String TABLE_CELL_XPATH_PREFIX = "//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/";


    public static final String TABLE_CELL_XPATH_TEMPLATE =  "//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span";
    public static final String TABLE_SORT_BUTTON_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div[2]/div/div[3]/div/div[2]/div/div[{column}]/div/div";


    public static final Integer SUPPLYING_FACILITY = 1;
    public static final Integer CODE = 2;
    public static final Integer FACILITY_NAME = 3;
    public static final Integer FACILITY_TYPE = 4;
    public static final Integer LOCATION = 5;
    public static final Integer PRODUCT_STOCKED_OUT = 6;

    private HomePage homePage;
    private LoginPage loginPage;
    private StockedOutReportPage stockedOutReportPage;

    @BeforeMethod(groups = {"functional3"})
    public void setUp() throws Exception {
        super.setup();
    }


    private void navigateToStockedOutReport(String userName, String passWord) throws IOException {
        login(userName, passWord);
        stockedOutReportPage = homePage.navigateViewStockedOutReport();
    }

    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String[] credentials) throws Exception {
        navigateToStockedOutReport(credentials[0], credentials[1]);

/*        SeleneseTestNgHelper.assertTrue(stockedOutReportPage.facilityCodeIsDisplayed());
        SeleneseTestNgHelper.assertTrue(stockedOutReportPage.facilityNameIsDisplayed());
        SeleneseTestNgHelper.assertTrue(stockedOutReportPage.facilityTypeIsDisplayed());*/
    }

    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput(String[] credentials) throws Exception {
        navigateToStockedOutReport(credentials[0], credentials[1]);
        verifyPdfReportOutput("pdf-button");
    }


    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput(String[] credentials) throws Exception {
        navigateToStockedOutReport(credentials[0], credentials[1]);
        verifyXlsReportOutput("xls-button");
    }

    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String[] credentials) throws IOException {
        navigateToStockedOutReport(credentials[0], credentials[1]);
        Map<String, String> templates =     new HashMap<String, String>(){{
            put(SORT_BUTTON_ASC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(SORT_BUTTON_DESC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};

        verifySort("ASC",  SUPPLYING_FACILITY ,templates);
        verifySort("ASC",  CODE  ,templates);
        verifySort("ASC",  FACILITY_NAME  ,templates);
        verifySort("ASC",  FACILITY_TYPE  ,templates);
        verifySort("ASC",  LOCATION  ,templates);
        verifySort("ASC",  PRODUCT_STOCKED_OUT  ,templates);
    }


    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToStockedOutReport(credentials[0], credentials[1]);

        Map<String, String> templates =     new HashMap<String, String>(){{
            put(PAGINATION_BUTTON_PREV_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_NEXT_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_FIRST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_LAST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifyPagination(templates);
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
