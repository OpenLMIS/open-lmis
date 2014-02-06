/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.support.How.*;


public class FacilityMailingListReportPage extends Page {

    @FindBy(how = ID, using = "name")
    private static WebElement facilityName;

    @FindBy(how = ID, using = "code")
    private static WebElement facilityCode;

    @FindBy(how = ID, using = "facility-type")
    private static WebElement facilityType;

    @FindBy(how = ID, using = "pdf-button")
    private static WebElement PdfButton;

    @FindBy(how = ID, using = "mailing-button")
    private static WebElement MailingReportButton;

    @FindBy(how = ID, using = "xls-button")
    private static WebElement XLSButton;

    @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
    private static WebElement facilityListGrid;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']/span")
    private static WebElement columnFacilityCode;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col1 colt1']/span")
    private static WebElement columnFacilityName;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
    private static WebElement columnFacilityType;

    @FindBy(how = CSS, using = "//div.ngHeaderCell:nth-child(1) > div:nth-child(1) > div:nth-child(2)")
    private static WebElement sortByNameButton;


    private String facilityNameFilter;
    private String facilityCodeFilter;
    private String facilityTypeFilter;

    public FacilityMailingListReportPage(TestWebDriver driver)  {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(10);

    }

    private void verifyHeader(String headingToVerify) {
        testWebDriver.sleep(1000);
    }

    public void enterFilterValuesInFacilityMailingListReport(String name, String code, String facilityTypeValue) {
        facilityNameFilter = name;
        facilityCodeFilter = code;
        facilityTypeFilter = facilityTypeValue;

        testWebDriver.waitForElementToAppear(facilityName);
        facilityName.clear();
        facilityName.sendKeys(name);
        facilityCode.sendKeys(code);
        testWebDriver.selectByVisibleText(facilityType, "Dispensary");
        testWebDriver.sleep(500);

    }

    public void verifyHTMLReportOutputOnFacilityMailingListScreen() {

        //verify facility list grid has the filtered record
        testWebDriver.waitForElementToAppear(facilityListGrid);

        testWebDriver.waitForElementToAppear(columnFacilityCode);
        SeleneseTestNgHelper.assertEquals(columnFacilityCode.getText().trim(), facilityCodeFilter);

        testWebDriver.waitForElementToAppear(columnFacilityName);
        SeleneseTestNgHelper.assertEquals(columnFacilityName.getText().trim(), facilityNameFilter);

        testWebDriver.waitForElementToAppear(columnFacilityType);
        SeleneseTestNgHelper.assertEquals(columnFacilityType.getText().trim(), facilityTypeFilter);
    }

    public void verifyPdfReportOutputOnFacilityMailingListScreen() throws Exception {
        testWebDriver.waitForElementToAppear(XLSButton);
        PdfButton.click();
        testWebDriver.sleep(500);

        SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
        downloadHandler.setURI(testWebDriver.getCurrentUrl());
        File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".pdf");
        SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(), 200);
        SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);

        testWebDriver.sleep(500);
    }

    public void verifyMailingReportOutputOnFacilityMailingListScreen() throws Exception {
        testWebDriver.waitForElementToAppear(XLSButton);
        MailingReportButton.click();
        testWebDriver.sleep(500);

        SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
        downloadHandler.setURI(testWebDriver.getCurrentUrl());
        File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName() + "_mailing_label", ".pdf");
        SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(), 200);
        SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);

        testWebDriver.sleep(500);
    }

    public void verifyXlsReportOutputOnFacilityMailingListScreen() throws Exception {
        testWebDriver.waitForElementToAppear(XLSButton);
        XLSButton.click();
        testWebDriver.sleep(500);

        SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
        downloadHandler.setURI(testWebDriver.getCurrentUrl());
        File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".xls");
        SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(), 200);
        SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);

        testWebDriver.sleep(500);
    }

    public boolean facilityNameIsDisplayed() {
        return facilityName.isDisplayed();
    }

    public boolean facilityCodeIsDisplayed() {
        return facilityCode.isDisplayed();
    }

    public boolean facilityTypeIsDisplayed() {
        return facilityType.isDisplayed();
    }


    public void verifyPdfReportOutput() throws Exception {
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

    public void verifyXlsReportOutput() throws Exception {
        testWebDriver.waitForElementToAppear(PdfButton);
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

    public void verifyPagination() throws IOException {

        WebElement btnPrev = testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div[2]/div/div[3]/div[3]/div/div[2]/div[2]/button[2]"));
        WebElement btnNext = testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div[2]/div/div[3]/div[3]/div/div[2]/div[2]/button[3]"));

        for (int i = 0; i < 10; i++)
            btnNext.click();
        for (int i = 0; i < 10; i++)
            btnPrev.click();

    }
}