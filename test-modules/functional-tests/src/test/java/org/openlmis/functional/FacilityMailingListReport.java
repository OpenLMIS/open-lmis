/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.FacilityMailingListReportPage;
import org.openlmis.pageobjects.HomePage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class FacilityMailingListReport extends ReportTestHelper {

    public static final String TABLE_CELL_XPATH_PREFIX = "//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/";


    public static final String TABLE_CELL_XPATH_TEMPLATE =  "//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span";
    public static final String TABLE_SORT_BUTTON_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div[2]/div/div[3]/div/div[2]/div/div[{column}]/div/div";


    public static final Integer COLUMN_NAME_FACILITY_CODE = 1;
    public static final Integer COLUMN_NAME_FACILITY_NAME = 2;
    public static final Integer COLUMN_NAME_FACILITY_TYPE = 3;
    public static final Integer COLUMN_NAME_REGION = 4;
    public static final Integer COLUMN_NAME_ADDRESS = 5;
    public static final Integer COLUMN_NAME_CONTACT = 6;
    public static final Integer COLUMN_NAME_OPERATOR = 7;
    public static final Integer COLUMN_NAME_PHONE = 8;
    public static final Integer COLUMN_NAME_ACTIVE = 9;

    private FacilityMailingListReportPage facilityMailingListReportPage;

    @BeforeMethod(groups = {"functional3"})
    public void setUp() throws Exception {
        super.setup();
    }


    private void navigateToFacilityMailingListReportingPage(String userName, String passWord) throws IOException {
        login(userName, passWord);
        facilityMailingListReportPage = homePage.navigateViewFacilityMailingListReport();
    }


    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String[] credentials) throws Exception {

        String geoZone = "Ngorongoro";
        String facilityType = "Lvl3 Hospital";
        String facilityCodePrefix = "FCcode";
        String facilityNamePrefix = "FCname";

        Date dObj = new Date();
        SimpleDateFormat formatter_date_time = new SimpleDateFormat(
                "yyyyMMdd-hhmmss");
        String date_time = formatter_date_time.format(dObj);

        navigateToFacilityMailingListReportingPage(credentials[0], credentials[1]);

        SeleneseTestNgHelper.assertTrue(facilityMailingListReportPage.facilityCodeIsDisplayed());
        SeleneseTestNgHelper.assertTrue(facilityMailingListReportPage.facilityNameIsDisplayed());
        SeleneseTestNgHelper.assertTrue(facilityMailingListReportPage.facilityTypeIsDisplayed());

        facilityMailingListReportPage.enterFilterValuesInFacilityMailingListReport(facilityNamePrefix + date_time, facilityCodePrefix + date_time, facilityType);
    }

    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput(String[] credentials) throws Exception {
        navigateToFacilityMailingListReportingPage(credentials[0], credentials[1]);
        facilityMailingListReportPage.verifyPdfReportOutput();
    }


    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput(String[] credentials) throws Exception {
        navigateToFacilityMailingListReportingPage(credentials[0], credentials[1]);
        facilityMailingListReportPage.verifyXlsReportOutput();
    }

    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String[] credentials) throws IOException {
        navigateToFacilityMailingListReportingPage(credentials[0], credentials[1]);


        Map<String, String> templates =     new HashMap<String, String>(){{
            put(SORT_BUTTON_ASC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(SORT_BUTTON_DESC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifySort("ASC", COLUMN_NAME_FACILITY_CODE , templates);
        verifySort("ASC", COLUMN_NAME_FACILITY_NAME , templates);
/*        verifySort("ASC", COLUMN_NAME_FACILITY_TYPE , templates);
        verifySort("ASC", COLUMN_NAME_REGION , templates);
        verifySort("ASC", COLUMN_NAME_ADDRESS , templates);
        verifySort("ASC", COLUMN_NAME_CONTACT , templates);
        verifySort("ASC", COLUMN_NAME_OPERATOR , templates);
        verifySort("ASC", COLUMN_NAME_PHONE , templates);
        verifySort("ASC", COLUMN_NAME_ACTIVE , templates);*/
    }


    @Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToFacilityMailingListReportingPage(credentials[0], credentials[1]);


        Map<String, String> templates =     new HashMap<String, String>(){{
            put(PAGINATION_BUTTON_PREV_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[3]/div/div[2]/div[2]/button[2]");
            put(PAGINATION_BUTTON_NEXT_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[3]/div/div[2]/div[2]/button[3]");
            put(PAGINATION_BUTTON_FIRST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_LAST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifyPagination(templates);

        //driver.findElement(By.xpath("//input[@type='number']")).sendKeys("5"); enter number
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
