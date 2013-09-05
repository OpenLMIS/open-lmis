/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;


public class StockImbalanceByFacilityPage extends Page {

    @FindBy(how = NAME, using = "schedule")
    private static WebElement schedule;

    @FindBy(how = NAME, using = "period")
    private static WebElement period;

    @FindBy(how = NAME, using = "zone")
    private static WebElement zone;

    @FindBy(how = ID, using = "name")
    private static WebElement name;

    @FindBy(how = ID, using = "facility-type")
    private static WebElement facilityType;

    @FindBy(how = NAME, using = "product")
    private static WebElement product;

    @FindBy(how = NAME, using = "requisitionGroup")
    private static WebElement requisitionGroup;

    @FindBy(how = NAME, using = "program")
    private static WebElement program;


    @FindBy(how = ID, using = "pdf-button")
    private static WebElement PdfButton;

    @FindBy(how = ID, using = "xls-button")
    private static WebElement XLSButton;

    public StockImbalanceByFacilityPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(10);

    }

    public void enterFilterValues() {


        testWebDriver.sleep(500);
    }

    public void verifyHTMLReportOutput() {

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

        testWebDriver.sleep(500);
    }

}