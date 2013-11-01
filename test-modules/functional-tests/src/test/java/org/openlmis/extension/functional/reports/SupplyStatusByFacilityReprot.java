/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.extension.functional.reports;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.extension.pageobjects.SupplyStatusByFacilityPage;
import org.openqa.selenium.By;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class SupplyStatusByFacilityReprot extends ReportTestHelper {

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

    private static final Integer FACILITY_NAME = 1;
    private static final Integer FACILITY_CODE = 2;
    private static final Integer PRODUCT = 3;
    private static final Integer OPENING_BALANCE = 4;
    private static final Integer RECEIPTS = 5;
    private static final Integer ISSUES = 6;
    private static final Integer ADJUSTMENTS = 7;
    private static final Integer CLOSING_BALANCE = 8;
    private static final Integer MONTHS_OF_STOCK = 9;
    private static final Integer AMC = 10;
    private static final Integer MAXIMUM_STOCK = 11;
    private static final Integer REORDER_AMOUNT = 12;


    private ReportHomePage homePage;
    private ReportLoginPage loginPage;
    private SupplyStatusByFacilityPage supplyStatusByFacilityPage;

    @BeforeMethod(groups = {"report"})
    public void setUp() throws Exception {
        super.setup();
    }

    private void navigateToSupplyStatusByFacilityReport(String userName, String passWord) throws IOException {
        login(userName, passWord);
        supplyStatusByFacilityPage = homePage.navigateViewSupplyStatusByFacilityPage();
    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyReportFiltersRendered(String[] credentials) throws Exception {
        navigateToSupplyStatusByFacilityReport(credentials[0], credentials[1]);

        System.out.println();
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityCodeIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityNameIsDisplayed());
        // SeleneseTestNgHelper.assertTrue(summaryReportPage.facilityTypeIsDisplayed());

        navigateToSupplyStatusByFacilityReport(credentials[0], credentials[1]);
        enterFilterValues();

    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPDFOUtput(String[] credentials) throws Exception {
        navigateToSupplyStatusByFacilityReport(credentials[0], credentials[1]);
        supplyStatusByFacilityPage.verifyPdfReportOutput();
    }


    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyXLSOUtput(String[] credentials) throws Exception {
        navigateToSupplyStatusByFacilityReport(credentials[0], credentials[1]);
        supplyStatusByFacilityPage.verifyXlsReportOutput();
    }

    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifySorting(String[] credentials) throws IOException {
        navigateToSupplyStatusByFacilityReport(credentials[0], credentials[1]);

        Map<String, String> templates = new HashMap<String, String>() {{
            put(SORT_BUTTON_ASC_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(SORT_BUTTON_DESC_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");

            put(TABLE_CELL_TEMPLATE, "test");
        }};
        verifySort("ASC", FACILITY_NAME, templates);
        verifySort("ASC", FACILITY_CODE, templates);
        verifySort("ASC", PRODUCT, templates);
        verifySort("ASC", OPENING_BALANCE, templates);
        verifySort("ASC", RECEIPTS, templates);
        verifySort("ASC", ISSUES, templates);
        verifySort("ASC", ADJUSTMENTS, templates);
        verifySort("ASC", CLOSING_BALANCE, templates);
        verifySort("ASC", MONTHS_OF_STOCK, templates);
        verifySort("ASC", AMC, templates);
        verifySort("ASC", MAXIMUM_STOCK, templates);
        verifySort("ASC", REORDER_AMOUNT, templates);

    }


    @Test(groups = {"report"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToSupplyStatusByFacilityReport(credentials[0], credentials[1]);


        Map<String, String> templates = new HashMap<String, String>() {{
            put(PAGINATION_BUTTON_PREV_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_NEXT_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_FIRST_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(PAGINATION_BUTTON_LAST_TEMPLATE, "//div[@id='wrap']/div/div/div/div/div[3]/div/div[2]/div/div[{column}]/div/div");
            put(TABLE_CELL_TEMPLATE, "//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div[{row}]/div[{column}]/div/span");
        }};
        verifyPagination(templates);
    }

    public void enterFilterValues() {
        testWebDriver.findElement(By.cssSelector("b")).click();
        testWebDriver.findElement(By.name("program")).click();
        testWebDriver.findElement(By.name("schedule")).click();
        testWebDriver.findElement(By.name("period")).click();
        testWebDriver.findElement(By.name("zone")).click();
        testWebDriver.findElement(By.id("name")).sendKeys("Facility Name");
        testWebDriver.findElement(By.id("facility-type")).click();
        testWebDriver.findElement(By.name("product")).click();
        testWebDriver.findElement(By.name("requisitionGroup")).click();
    }


    @AfterMethod(groups = {"report"})
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
