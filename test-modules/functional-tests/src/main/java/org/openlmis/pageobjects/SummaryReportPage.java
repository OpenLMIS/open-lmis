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

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;


public class SummaryReportPage extends Page {

    @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
    private static WebElement summaryReportListGrid;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
    private static WebElement columnZone;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col7 colt7']/span")
    private static WebElement columnActive;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
    private static WebElement columnFacilityType;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
    private static WebElement columnBeginingBalance;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col5 colt5']/span")
    private static WebElement columnReceived;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col6 colt6']/span")
    private static WebElement columnDispensed;

    @FindBy(how = NAME, using = "zone")
    private static WebElement zone;

    @FindBy(how = ID, using = "name")
    private static WebElement name;

    @FindBy(how = ID, using = "facility-type")
    private static WebElement facilityType;

    @FindBy(how = NAME, using = "program")
    private static WebElement program;

    @FindBy(how = NAME, using = "product")
    private static WebElement product;

    @FindBy(how = NAME, using = "schedule")
    private static WebElement schedule;

    @FindBy(how = NAME, using = "requisitionGroup")
    private static WebElement requisitionGroup;

    @FindBy(how = NAME, using = "period")
    private static WebElement period;

    @FindBy(how = ID, using = "pdf-button")
    private static WebElement PdfButton;

    @FindBy(how = ID, using = "xls-button")
    private static WebElement XLSButton;

    public SummaryReportPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(10);

    }

    public void enterFilterValuesInSummaryReport(String periodValue) {

        testWebDriver.waitForElementToAppear(period);
        testWebDriver.selectByVisibleText(period, periodValue);
        testWebDriver.sleep(500);
    }

    public void verifyHTMLReportOutputOnSummaryReportScreen() {

        testWebDriver.waitForElementToAppear(summaryReportListGrid);
        testWebDriver.sleep(500);
        SeleneseTestNgHelper.assertEquals(columnBeginingBalance.getText().trim(), 1);

        SeleneseTestNgHelper.assertEquals(columnReceived.getText().trim(), 1);

        SeleneseTestNgHelper.assertEquals(columnDispensed.getText().trim(), 1);
    }

    public void verifyPdfReportOutputOnSummaryReportScreen() throws Exception {
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

        WebElement btnNext = testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div/div/div[3]/div[3]/div/div[2]/div[2]/button[3]"));
        WebElement btnPrev = testWebDriver.findElement(By.xpath("//div[@id='wrap']/div/div/div/div/div[3]/div[3]/div/div[2]/div[2]/button[2]"));


        for (int i = 0; i < 10; i++)
            btnNext.click();
        for (int i = 0; i < 10; i++)
            btnPrev.click();

    }

    public void selectZoneByVisibleText(String visibleText) {
        testWebDriver.selectByVisibleText(zone, visibleText);
    }

    public void selectProgramByVisibleText(String visibleText){
        testWebDriver.selectByVisibleText(program , visibleText);
    }

    public void selectProductByVisibleText(String visibleText){
        testWebDriver.selectByVisibleText(product , visibleText);
    }

    public void selectScheduleByVisibleText(String visibleText){
        testWebDriver.selectByVisibleText(schedule , visibleText);
    }

    public void selectFacilityTypeByVisibleText(String visibleText){
        testWebDriver.selectByVisibleText(facilityType, visibleText);
    }

    public void selectPeriodByVisibleText(String visibleText){
        testWebDriver.selectByVisibleText(period , visibleText);
    }

    public void selectRequisitionGroupByVisibleText(String visibleText){
        testWebDriver.selectByVisibleText(requisitionGroup , visibleText);
    }

    public void enterName(String nameText){
        name.sendKeys(nameText);
    }

}