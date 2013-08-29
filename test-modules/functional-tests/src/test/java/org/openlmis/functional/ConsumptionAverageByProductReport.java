/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    private enum Column {
        FACILITY_TYPE,
        FACILITY_NAME,
        SUPPLYING_FACILITY,
        PRODUCT,
        PRODUCT_DESCRIPTION,
        PRODUCT_CODE,
        AMC;
    }

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
        verifySort("ASC", Column.FACILITY_TYPE);
        verifySort("ASC", Column.FACILITY_NAME);
        verifySort("ASC", Column.SUPPLYING_FACILITY);
        verifySort("ASC", Column.PRODUCT);
        verifySort("ASC", Column.PRODUCT_CODE);
        verifySort("ASC", Column.PRODUCT_DESCRIPTION);
        verifySort("ASC", Column.AMC);
    }




    //@Test(groups = {"functional3"}, dataProvider = "Data-Provider-Function-Positive")
    public void verifyPagination(String[] credentials) throws Exception {
        navigateToSummaryReportPage(credentials[0], credentials[1]);
        averageConsumptionReportPage.verifyPagination();
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

        // dbWrapper.updateRequisition("F10");
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

    public void verifySort(String sortType, Column column) throws IOException {
        WebElement sortButton = null;
        String columnIndex = String.valueOf(column.ordinal() + 1);
        System.out.println(columnIndex);
        switch (sortType) {
            case "ASC":
                sortButton = testWebDriver.findElement(By.xpath(TABLE_SORT_BUTTON_XPATH_TEMPLATE.replace("{column}", columnIndex)));
                break;
            case "DESC":
                sortButton = testWebDriver.findElement(By.xpath(TABLE_SORT_BUTTON_XPATH_TEMPLATE.replace("{column}", columnIndex)));
                break;
        }
        SeleneseTestNgHelper.assertTrue(sortButton.isDisplayed());

        sortButton.click();
        String str1, str2;
        WebElement cell1 = null, cell2 = null;
        for (int i = 1; ; i++) {
            try {
                cell1 = testWebDriver.findElement(By.xpath(TABLE_CELL_XPATH_TEMPLATE.replace("{column}", columnIndex).replace("{row}", String.valueOf(i))));
                cell2 = testWebDriver.findElement(By.xpath(TABLE_CELL_XPATH_TEMPLATE.replace("{column}", columnIndex).replace("{row}", String.valueOf(i + 1))));
            } catch (NoSuchElementException ex) {
                break;         // implement other termination condition?
            }

            if (cell1 != null && cell1.isDisplayed()) {
                str1 = cell1.getText();
            } else {
                break;
            }
            if (cell2 != null && cell2.isDisplayed()) {
                str2 = cell2.getText();
                //SeleneseTestNgHelper.assertTrue(str1.trim().compareToIgnoreCase(str2.trim()) < 1);
            } else {
                break;
            }
            // str1 =  testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div"+strIdx+"/div/div/span")).getText();
            // str1 =  testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div[2]/div/div[3]/div[2]/div/div"+strIdx+"/div/div/span")).getText();
            System.out.println(str1);
            System.out.println(str2);

            switch (sortType) {
                case "ASC":
                    // SeleneseTestNgHelper.assertTrue(str1.trim().compareToIgnoreCase(str2.trim()) > 1);
                    break;
                case "DESC":
                    //  SeleneseTestNgHelper.assertTrue(str1.trim().compareToIgnoreCase(str2.trim()) < 1);
                    break;
            }
        }
        System.out.print("~||~");

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
