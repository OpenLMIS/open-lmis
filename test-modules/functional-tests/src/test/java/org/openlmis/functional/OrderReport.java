/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.OrderReportPage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
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

public class OrderReport extends ReportTestHelper {

    public static final String STORE_IN_CHARGE = "store in-charge";
    public static final String APPROVE_REQUISITION = "APPROVE_REQUISITION";
    public static final String CONVERT_TO_ORDER = "CONVERT_TO_ORDER";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String AUTHORIZED = "AUTHORIZED";
    public static final String IN_APPROVAL = "IN_APPROVAL";
    public static final String APPROVED = "APPROVED";
    public static final String RELEASED = "RELEASED";
    public static final String TABLE_CELL_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span";
    public static final String TABLE_SORT_BUTTON_XPATH_TEMPLATE = "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div";

    public static final Integer PRODUCT_CODE = 1;
    public static final Integer DESCRIPTION = 2;
    public static final Integer FACILITY = 3;
    public static final Integer UNIT_SIZE = 4;
    public static final Integer UNIT_QUANTITY = 5;
    public static final Integer PACK_QUANITTY = 6;
    public static final Integer DISCREPANCY_OR_DAMAGES = 7;

    private HomePage homePage;
    private LoginPage loginPage;
    private OrderReportPage orderReportPage;

    @BeforeMethod(groups = {"functional3"})
    public void setUp() throws Exception {
        super.setup();
    }

    private void navigateToOrderReport(String userName, String passWord) throws IOException {
        login(userName, passWord);
        orderReportPage = homePage.navigateViewOrderReport();
    }

    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String[] credentials) throws Exception {
        navigateToOrderReport(credentials[0], credentials[1]);

        System.out.println();
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityCodeIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityNameIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityTypeIsDisplayed());

        navigateToOrderReport(credentials[0], credentials[1]);
        enterFilterValues();

    }

    ////@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput(String[] credentials) throws Exception {
        navigateToOrderReport(credentials[0], credentials[1]);
        verifyPdfReportOutput("pdf-button");
    }


    ////@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput(String[] credentials) throws Exception {
        navigateToOrderReport(credentials[0], credentials[1]);
        verifyXlsReportOutput("xls-button");
    }

    ////@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String[] credentials) throws IOException {
        navigateToOrderReport(credentials[0], credentials[1]);

        Map<String, String> templates =     new HashMap<String, String>(){{
            put(SORT_BUTTON_ASC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(SORT_BUTTON_DESC_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"test");
        }};

        verifySort("ASC",  PRODUCT_CODE , templates);
        verifySort("ASC",  DESCRIPTION  , templates);
        verifySort("ASC",  FACILITY  , templates);
        verifySort("ASC",  UNIT_SIZE  , templates);
        verifySort("ASC",  UNIT_QUANTITY  , templates);
        verifySort("ASC",  PACK_QUANITTY  , templates);
        verifySort("ASC",  DISCREPANCY_OR_DAMAGES  , templates);
    }


    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToOrderReport(credentials[0], credentials[1]);


        Map<String, String> templates =     new HashMap<String, String>(){{
            put(PAGINATION_BUTTON_PREV_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_NEXT_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_FIRST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_LAST_TEMPLATE,"//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE,"//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifyPagination(templates);
    }

    public void enterFilterValues(){

        testWebDriver.findElement(By.name("orderType")).click();
        testWebDriver.findElement(By.name("periodType")).click();
        testWebDriver.findElement(By.name("program")).click();
        testWebDriver.findElement(By.id("facility-type")).click();
        testWebDriver.findElement(By.id("facility-name")).click();
        testWebDriver.findElement(By.name("periodType")).click();
        new Select(testWebDriver.findElement(By.name("startYear"))).selectByVisibleText("2011");
        testWebDriver.findElement(By.cssSelector("select[name=\"startYear\"] > option[value=\"1\"]")).click();
        new Select(testWebDriver.findElement(By.name("startHalf"))).selectByVisibleText("First Half");
        testWebDriver.findElement(By.cssSelector("select[name=\"startHalf\"] > option[value=\"1\"]")).click();
        new Select(testWebDriver.findElement(By.name("endYear"))).selectByVisibleText("2011");
        testWebDriver.findElement(By.cssSelector("select[name=\"endYear\"] > option[value=\"1\"]")).click();
        new Select(testWebDriver.findElement(By.name("endHalf"))).selectByVisibleText("First Half");
        testWebDriver.findElement(By.cssSelector("select[name=\"endHalf\"] > option[value=\"1\"]")).click();
        testWebDriver.findElement(By.name("product")).click();
        testWebDriver.findElement(By.name("zone")).click();
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
