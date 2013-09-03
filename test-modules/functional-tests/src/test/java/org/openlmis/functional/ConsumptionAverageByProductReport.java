/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.AverageConsumptionReportPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ConsumptionAverageByProductReport extends ReportTestHelper {

    public static final String STORE_IN_CHARGE = "store in-charge";
    public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
    public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String AUTHORIZED = "AUTHORIZED";
    public static final String IN_APPROVAL = "IN_APPROVAL";
    public static final String APPROVED = "APPROVED";
    public static final String RELEASED = "RELEASED";
    public static final String TABLE_CELL_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span";
    public static final String TABLE_SORT_BUTTON_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div[2]/div/div[2]/div/div[2]/div/div/div[2]/div/div[{column}]/div/div";


    public static final Integer FACILITY_TYPE = 1;
    public static final Integer FACILITY_NAME = 2;
    public static final Integer SUPPLYING_FACILITY = 3;
    public static final Integer PRODUCT = 4;
    public static final Integer PRODUCT_DESCRIPTION = 5;
    public static final Integer PRODUCT_CODE = 6;
    public static final Integer AMC = 7;


    private HomePage homePage;
    private LoginPage loginPage;
    private AverageConsumptionReportPage averageConsumptionReportPage;

    @BeforeMethod(groups = {"functional3"})
    public void setUp() throws Exception {
        super.setup();
    }

    private void login(String userName, String passWord) throws IOException {
        loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        homePage = loginPage.loginAs(userName, passWord);
    }

    private void navigateToSummaryReportPage(String userName, String passWord) throws IOException {
        login(userName, passWord);
        averageConsumptionReportPage = homePage.navigateViewAverageConsumptionReport();
    }

    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportMenu(String[] credentials) throws IOException {
        // Assign rights here
        // List<String> rightsList = new ArrayList<String>();
        //rightsList.add("VIEW_REPORT");
        //setUpRoleRightstoUser(String "5", String userSIC, String vendorName, List<String> rightsList, String roleName , String roleType)

        login(credentials[0], credentials[1]);
        SeleneseTestNgHelper.assertTrue(homePage.reportMenuIsDisplayed());
        homePage.logout(DEFAULT_BASE_URL);
    }

//    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportMenuHiddenForUnauthorizedUser(String[] credentials) throws IOException {
        // Assign rights here
        //List<String> rightsList = new ArrayList<String>();
        //rightsList.add("VIEW_REPORT");
        //setUpRoleRightstoUser(String "5", String userSIC, String vendorName, List<String> rightsList, String roleName , String roleType)
        login(credentials[2], credentials[3]);
        SeleneseTestNgHelper.assertFalse(homePage.reportMenuIsDisplayed());
        homePage.logout(DEFAULT_BASE_URL);
    }

    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);

        System.out.println();
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityCodeIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityNameIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityTypeIsDisplayed());

        navigateToSummaryReportPage(credentials[0], credentials[1]);


    }

    ////@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);
        averageConsumptionReportPage.verifyPdfReportOutput();
    }


    ////@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);
        averageConsumptionReportPage.verifyXlsReportOutput();
    }

    ////@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String[] credentials) throws IOException {
        navigateToSummaryReportPage(credentials[0], credentials[1]);


        Map<String, String> templates =     new HashMap<String, String>(){{
            put(SORT_BUTTON_ASC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(SORT_BUTTON_DESC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifySort("ASC", FACILITY_TYPE , templates);
        verifySort("ASC", FACILITY_NAME , templates);
        verifySort("ASC", SUPPLYING_FACILITY , templates);
        verifySort("ASC", PRODUCT , templates);
        verifySort("ASC", PRODUCT_CODE , templates);
        verifySort("ASC", PRODUCT_DESCRIPTION , templates);
        verifySort("ASC", AMC , templates);
    }




    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);
        averageConsumptionReportPage.verifyPagination();
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
