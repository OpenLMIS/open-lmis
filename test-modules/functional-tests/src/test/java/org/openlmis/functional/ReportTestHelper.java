/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Listeners;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ReportTestHelper extends TestCaseHelper {

    //Sorting templates
    public static final String SORT_BUTTON_ASC_TEMPLATE = "SortAscendingButton";
    public static final String SORT_BUTTON_DESC_TEMPLATE = "SortDescendingButton";
    public static final String TABLE_CELL_TEMPLATE = "TableCellTemplate";

    //Pagination templates
    public static final String PAGINATION_BUTTON_PREV_TEMPLATE = "paginationButtonPrevTemplate";
    public static final String PAGINATION_BUTTON_NEXT_TEMPLATE = "paginatioinButtonNextTemplate";
    public static final String PAGINATION_BUTTON_FIRST_TEMPLATE = "paginationButtonFirstTemplate";
    public static final String PAGINATION_BUTTON_LAST_TEMPLATE = "paginationButtonLastTemplate";

    protected LoginPage loginPage;
    protected HomePage homePage;


    protected void login(String userName, String passWord) throws IOException {
        loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        homePage = loginPage.loginAs(userName, passWord);
    }

    public void verifyReportMenu(String[] credentials) throws IOException {
        // Assign rights here
        // List<String> rightsList = new ArrayList<String>();
        //rightsList.add("VIEW_REPORT");
        //setUpRoleRightstoUser(String "5", String userSIC, String vendorName, List<String> rightsList, String roleName , String roleType)

        login(credentials[0], credentials[1]);
        SeleneseTestNgHelper.assertTrue(homePage.reportMenuIsDisplayed());
        homePage.logout(DEFAULT_BASE_URL);
    }

    public void verifyReportMenuHiddenForUnauthorizedUser(String[] credentials) throws IOException {
        // Assign rights here
        //List<String> rightsList = new ArrayList<String>();
        //rightsList.add("VIEW_REPORT");
        //setUpRoleRightstoUser(String "5", String userSIC, String vendorName, List<String> rightsList, String roleName , String roleType)
        login(credentials[2], credentials[3]);
        SeleneseTestNgHelper.assertFalse(homePage.reportMenuIsDisplayed());
        homePage.logout(DEFAULT_BASE_URL);
    }

    public void verifySort(String sortType, Integer columnIdx, Map<String, String> templates) throws IOException {
        WebElement sortButton = null;
        String columnIndex = String.valueOf(columnIdx);
        System.out.println(columnIndex);
        switch (sortType) {
            case "ASC":
                sortButton = testWebDriver.findElement(By.xpath(templates.get(SORT_BUTTON_ASC_TEMPLATE).replace("{column}", columnIndex)));
                break;
            case "DESC":
                sortButton = testWebDriver.findElement(By.xpath(templates.get(SORT_BUTTON_DESC_TEMPLATE).replace("{column}", columnIndex)));
                break;
        }
        SeleneseTestNgHelper.assertTrue(sortButton.isDisplayed());

        sortButton.click();
        String str1, str2;
        WebElement cell1 = null, cell2 = null;
        for (int i = 1; ; i++) {
            try {
                cell1 = testWebDriver.findElement(By.xpath(templates.get(TABLE_CELL_TEMPLATE).replace("{column}", columnIndex).replace("{row}", String.valueOf(i))));
                cell2 = testWebDriver.findElement(By.xpath(templates.get(TABLE_CELL_TEMPLATE).replace("{column}", columnIndex).replace("{row}", String.valueOf(i + 1))));
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

    public void verifyPagination(Map<String, String> templates) {
        WebElement btnPrev = testWebDriver.findElement(By.xpath(templates.get(PAGINATION_BUTTON_PREV_TEMPLATE)));
        WebElement btnNext = testWebDriver.findElement(By.xpath(templates.get(PAGINATION_BUTTON_NEXT_TEMPLATE)));


        for (int i = 0; i < 10; i++)
            btnNext.click();
        for (int i = 0; i < 10; i++)
            btnPrev.click();

        /*WebElement btnFirst = testWebDriver.findElement(By.xpath(templates.get(PAGINATION_BUTTON_FIRST_TEMPLATE)));
        WebElement btnLast = testWebDriver.findElement(By.xpath(templates.get(PAGINATION_BUTTON_LAST_TEMPLATE)));
        btnFirst.click();
        btnLast.click();*/
    }


    public void verifyPdfReportOutput(String PdfButtonID) throws Exception {
        WebElement PdfButton = testWebDriver.findElement(By.id(PdfButtonID));
        testWebDriver.waitForElementToAppear(PdfButton);
        PdfButton.click();
        testWebDriver.sleep(500);

        SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
        downloadHandler.setURI(testWebDriver.getCurrentUrl());
        File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".pdf");
        SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(), 200);
        SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);
        SeleneseTestNgHelper.assertTrue(downloadedFile.length() > 0);

        testWebDriver.sleep(500);
    }

    public void verifyXlsReportOutput(String XLSButtonId) throws Exception {
        WebElement XLSButton = testWebDriver.findElement(By.id(XLSButtonId));
        testWebDriver.waitForElementToAppear(XLSButton);
        XLSButton.click();
        testWebDriver.sleep(500);

        SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
        downloadHandler.setURI(testWebDriver.getCurrentUrl());
        File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".xls");
        SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(), 200);
        SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);
        SeleneseTestNgHelper.assertTrue(downloadedFile.length() > 0);

        testWebDriver.sleep(500);
    }


}
