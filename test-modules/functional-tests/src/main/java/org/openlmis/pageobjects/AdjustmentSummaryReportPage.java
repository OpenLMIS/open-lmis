package org.openlmis.pageobjects;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.SeleniumFileDownloadUtil;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.NAME;

/**
 * User: Wolde
 * Date: 5/15/13
 * Time: 10:19 AM
 */
public class AdjustmentSummaryReportPage extends Page {

    @FindBy(how = NAME, using = "periodType")
    private static WebElement periodType;

    @FindBy(how = NAME, using = "startYear")
    private static WebElement startYear;

    @FindBy(how = NAME, using = "startMonth")
    private static WebElement startMonth;

    @FindBy(how = NAME, using = "endYear")
    private static WebElement endYear;

    @FindBy(how = NAME, using = "endMonth")
    private static WebElement endMonth;

    @FindBy(how = NAME, using = "zone")
    private static WebElement zone;

    @FindBy(how = NAME, using = "productCategory")
    private static WebElement productCategory;

    @FindBy(how = NAME, using = "facilityType")
    private static WebElement facilityType;

    @FindBy(how = NAME, using = "requisitionGroup")
    private static WebElement  requisitionGroup;

    @FindBy(how = NAME, using = "product")
    private static WebElement product;

    @FindBy(how = NAME, using = "startQuarter")
    private static WebElement startQuarter;

    @FindBy(how = NAME, using = "endQuarter")
    private static WebElement endQuarter;

    @FindBy(how = NAME, using = "program")
    private static WebElement program;

    @FindBy(how = NAME, using = "adjustmentType")
    private static WebElement adjustmentType;


    @FindBy(how = How.XPATH, using = "//div[@ng-grid='gridOptions']")
    private static WebElement adjustmentSummaryReportListGrid;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col0 colt0']/span")
    private static WebElement firstRowFacilityTypeColumn;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col1 colt1']/span")
    private static WebElement  firstRowFacilityColumn;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
    private static WebElement  firstRowSupplyingFacilityColumn;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col3 colt3']/span")
    private static WebElement firstRowProductDescriptionColumn;;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col4 colt4']/span")
    private static WebElement firstRowAdjustmentTypeColumn;

    @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col5 colt5']/span")
    private static WebElement firstRowAdjustmentColumn;


    @FindBy(how = ID, using = "pdf-button")
    private static WebElement PdfButton;

    @FindBy(how = ID, using = "xls-button")
    private static WebElement XLSButton;

    public AdjustmentSummaryReportPage(TestWebDriver driver) throws IOException {
        super(driver);
        PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
        testWebDriver.setImplicitWait(10);

    }

    public void enterFilterValues(String periodTypeValue, String startYearValue, String startMonthValue,
                                  String endYearValue, String endMonthValue, String zoneValue, String productCategoryValue,
                                  String facilityTypeValue, String requesitionGroupValue, String productValue, String programValue, String adjustmentTypeValue){

        testWebDriver.waitForElementToAppear(periodType);
        testWebDriver.selectByVisibleText(periodType, periodTypeValue);
        testWebDriver.selectByVisibleText(startYear, startYearValue);
        testWebDriver.selectByVisibleText(startMonth, startMonthValue);
        testWebDriver.selectByVisibleText(endYear, endYearValue);
        testWebDriver.selectByVisibleText(endMonth, endMonthValue);
        testWebDriver.selectByVisibleText(zone, zoneValue);
        testWebDriver.selectByVisibleText(productCategory, productCategoryValue);
        testWebDriver.selectByVisibleText(facilityType, facilityTypeValue);
        testWebDriver.selectByVisibleText(requisitionGroup, requesitionGroupValue);
        testWebDriver.selectByVisibleText(product, productValue);
        testWebDriver.selectByVisibleText(program, programValue);
        testWebDriver.selectByVisibleText(adjustmentType, adjustmentTypeValue);

        testWebDriver.sleep(500);
    }

    public void verifyHTMLReportOutput(){

        testWebDriver.waitForElementToAppear(adjustmentSummaryReportListGrid);
        testWebDriver.waitForElementToAppear(firstRowAdjustmentTypeColumn);

        testWebDriver.sleep(500);

    }

    public void verifyPdfReportOutput() throws Exception {
        testWebDriver.waitForElementToAppear(PdfButton);
        PdfButton.click();
        testWebDriver.sleep(500);

        SeleniumFileDownloadUtil downloadHandler = new SeleniumFileDownloadUtil(TestWebDriver.getDriver());
        downloadHandler.setURI(testWebDriver.getCurrentUrl());
        File downloadedFile = downloadHandler.downloadFile(this.getClass().getSimpleName(), ".pdf");
        SeleneseTestNgHelper.assertEquals(downloadHandler.getLinkHTTPStatus(),200);
        SeleneseTestNgHelper.assertEquals(downloadedFile.exists(), true);

        testWebDriver.sleep(500);
    }
}
