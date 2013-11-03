/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.extension.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.support.How.*;


public class OrderReportPage extends Page {

    @FindBy(how = NAME, using = "orderType")
    private static WebElement orderType;

    @FindBy(how = NAME, using = "periodType")
    private static WebElement periodType;

    @FindBy(how = NAME, using = "zone")
    private static WebElement zone;

    @FindBy(how = NAME, using = "program")
    private static WebElement program;

    @FindBy(how = NAME, using = "startYear")
    private static WebElement startYear;

    @FindBy(how = NAME, using = "endYear")
    private static WebElement endYear;

    @FindBy(how = NAME, using = "endQuarter")
    private static WebElement endQuarter;

    @FindBy(how = NAME, using = "startQuarter")
    private static WebElement startQuarter;

    @FindBy(how = NAME, using = "product")
    private static WebElement product;

    @FindBy(how = ID, using = "facility-type")
    private static WebElement facilityType;

    @FindBy(how = ID, using = "facility-name")
    private static WebElement facilityName;

    @FindBy(how = ID, using = "pdf-button")
    private static WebElement PdfButton;

    @FindBy(how = ID, using = "xls-button")
    private static WebElement XLSButton;

    /*driver.findElement(By.cssSelector("select[name=\"startYear\"] > option[value=\"1\"]")).click();
    driver.findElement(By.cssSelector("select[name=\"startQuarter\"] > option[value=\"2\"]")).click();
    driver.findElement(By.cssSelector("select[name=\"endYear\"] > option[value=\"2\"]")).click();
    driver.findElement(By.cssSelector("select[name=\"endQuarter\"] > option[value=\"2\"]")).click();*/


    @FindBy(how = XPATH, using = "//a[contains(text(),'Emergency')]")
    private static WebElement emergency;

    public OrderReportPage(TestWebDriver driver) throws IOException {
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
}