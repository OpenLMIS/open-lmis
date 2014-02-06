package org.openlmis.extension.functional.reports;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.extension.pageobjects.*;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;
import java.sql.SQLException;

public class ExtensionHomePage extends HomePage{

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Logistics Reports')]")
  private static WebElement LogisticsReportsMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Reports')]")
  private static WebElement ReportsMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Facility List (V1)')]")
  private static WebElement FacilityListingReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Facility List (V2)')]")
  private static WebElement FacilityMailingListReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Admin Reports')]")
  private static WebElement AdminReportsMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Summary Report')]")
  private static WebElement SummaryReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Non Reporting Facilities')]")
  private static WebElement NonReportingFacilityReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Average Consumption Report')]")
  private static WebElement AverageConsumptionReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Adjustment Summary')]")
  private static WebElement AdjustmentSummaryReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Stocked Out')]")
  private static WebElement StockedOutReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Order Report')]")
  private static WebElement OrderReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Supply Status by Facility')]")
  private static WebElement SupplyStatusByFacilityMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Stock Imbalance by Facility')]")
  private static WebElement StockImbalanceByFacility;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Facility List')]")
  private static WebElement facilityListingReportPageHeader;


  public ExtensionHomePage(TestWebDriver driver)  {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }



  public FacilityMailingListReportPage navigateViewFacilityMailingListReport() {
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(AdminReportsMenuItem);
    testWebDriver.keyPress(AdminReportsMenuItem);
    testWebDriver.waitForElementToAppear(FacilityMailingListReportMenu);
    testWebDriver.keyPress(FacilityMailingListReportMenu);
    testWebDriver.waitForElementToAppear(facilityListingReportPageHeader);
    return new FacilityMailingListReportPage(testWebDriver);
  }

  public FacilityListingReportPage navigateViewFacilityListingReport() {
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(FacilityListingReportMenu);
    testWebDriver.keyPress(FacilityListingReportMenu);
    return new FacilityListingReportPage(testWebDriver);
  }

  public SummaryReportPage navigateViewSummaryReport() {
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(SummaryReportMenu);
    testWebDriver.keyPress(SummaryReportMenu);
    return new SummaryReportPage(testWebDriver);
  }

  public NonReportingFacilityReportPage navigateViewNonReportingFacilityReport(){
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(NonReportingFacilityReportMenu);
    testWebDriver.keyPress(NonReportingFacilityReportMenu);
    return new NonReportingFacilityReportPage(testWebDriver);
  }

  public AverageConsumptionReportPage navigateViewAverageConsumptionReport() {

    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(AverageConsumptionReportMenu);
    testWebDriver.keyPress(AverageConsumptionReportMenu);
    return new AverageConsumptionReportPage(testWebDriver);
  }

  public AdjustmentSummaryReportPage navigateViewAdjustmentSummaryReport(){

    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(AdjustmentSummaryReportMenu);
    testWebDriver.keyPress(AdjustmentSummaryReportMenu);
    return new AdjustmentSummaryReportPage(testWebDriver);
  }

  public StockedOutReportPage navigateViewStockedOutReport() {

    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(StockedOutReportMenu);
    testWebDriver.keyPress(StockedOutReportMenu);
    return new StockedOutReportPage(testWebDriver);
  }

  public OrderReportPage navigateViewOrderReport() {

    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(OrderReportMenu);
    testWebDriver.keyPress(OrderReportMenu);
    return new OrderReportPage(testWebDriver);
  }


  public SupplyStatusByFacilityPage navigateViewSupplyStatusByFacilityPage(){

    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(SupplyStatusByFacilityMenu);
    testWebDriver.keyPress(SupplyStatusByFacilityMenu);
    return new SupplyStatusByFacilityPage(testWebDriver);
  }





  public StockImbalanceByFacilityPage navigateViewStockImbalanceByFacilityPage() {

    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(LogisticsReportsMenuItem);
    testWebDriver.keyPress(LogisticsReportsMenuItem);
    testWebDriver.waitForElementToAppear(SupplyStatusByFacilityMenu);
    testWebDriver.keyPress(SupplyStatusByFacilityMenu);
    return new StockImbalanceByFacilityPage(testWebDriver);
  }


}
