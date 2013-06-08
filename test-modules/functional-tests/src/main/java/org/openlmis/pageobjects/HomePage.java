/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.pageobjects;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.TestWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;


public class HomePage extends Page {

  @FindBy(how = How.XPATH, using = "//strong[@class='ng-binding']")
  private static WebElement usernameDisplay;

  @FindBy(how = How.LINK_TEXT, using = "Logout")
  private static WebElement logoutLink;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Requisitions')]")
  private static WebElement requisitionMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Reports')]")
  private static WebElement reportMenuItem;

  @FindBy(how = How.XPATH, using = "//h2/span[contains(text(),'Reports')]")
  private static WebElement reportsTitle;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Orders')]")
  private static WebElement ordersMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Approve')]")
  private static WebElement approveLink;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Administration')]")
  private static WebElement AdministrationMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Manage')]")
  private static WebElement manageFacilityMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Facilities')]")
  private static WebElement facilityMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Roles')]")
  private static WebElement manageRoleAssignmentLink;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Convert to Order')]")
  private static WebElement convertToOrderMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'View Orders')]")
  private static WebElement viewOrdersMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'View')]")
  private static WebElement viewRequisitonMenuItem;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'View Requisitions')]")
  private static WebElement viewRequisitonHeader;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Convert Requisitions to Order')]")
  private static WebElement convertToOrderHeader;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'View Orders')]")
  private static WebElement viewOrdersHeader;

  @FindBy(how = How.ID, using = "add-new-facility")
  private static WebElement createFacility;

  @FindBy(how = How.XPATH, using = "//div[@class='ng-scope']/div[@ng-hide='facility.id']/h2")
  private static WebElement facilityHeader;

  @FindBy(how = How.LINK_TEXT, using = "Configure")
  private static WebElement TemplateConfigTab;

  @FindBy(how = How.LINK_TEXT, using = "R & R Template")
  private static WebElement RnRTemplateConfigTab;

  @FindBy(how = How.LINK_TEXT, using = "R & R")
  private static WebElement ConfigureTemplateSelectProgramPage;

  @FindBy(how = How.ID, using = "selectProgram")
  private static WebElement ProgramDropDown;

  @FindBy(how = How.LINK_TEXT, using = "Requisitions")
  private static WebElement requisitionsLink;

  @FindBy(how = How.XPATH, using = "//div[@class='submenu']")
  private static WebElement SubMenuItem;

  @FindBy(how = How.LINK_TEXT, using = "Create / Authorize")
  private static WebElement createLink;

  @FindBy(how = How.XPATH, using = "//input[@id='myFacilityRnr']")
  private static WebElement myFacilityRadioButton;

  @FindBy(how = How.LINK_TEXT, using = "My Facility")
  private static WebElement myFacilityLink;

  @FindBy(how = How.XPATH, using = "//a[contains(@href,'/public/pages/logistics/rnr/create.html')]")
  private static WebElement createRnRLink;

  @FindBy(how = How.ID, using = "facilityList")
  private static WebElement facilityDropDown;

  @FindBy(how = How.XPATH, using = "//select[@id='programListMyFacility']")
  private static WebElement programDropDown;

  @FindBy(how = How.XPATH, using = "//option[@value='0']")
  private static WebElement periodDropDown;


  @FindBy(how = How.XPATH, using = "//select[1]")
  private static WebElement programDropDownSelect;

  @FindBy(how = How.XPATH, using = "//select[3]")
  private static WebElement periodDropDownSelect;

  @FindBy(how = How.XPATH, using = "//input[@value='Next']")
  private static WebElement nextButton;

  @FindBy(how = How.LINK_TEXT, using = "Manage")
  private static WebElement manageLink;

  @FindBy(how = How.LINK_TEXT, using = "Schedules")
  private static WebElement schedulesLink;

  @FindBy(how = How.LINK_TEXT, using = "Search")
  private static WebElement searchLink;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Upload')]")
  private static WebElement uploadLink;

  @FindBy(how = How.XPATH, using = "//input[@ng-click='initRnr()']")
  private static WebElement proceedButton;

  @FindBy(how = How.ID, using = "facility-tab")
  private static WebElement facilitiesTab;

  @FindBy(how = How.ID, using = "role-tab")
  private static WebElement rolesTab;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Roles')]")
  private static WebElement rolesLink;

  @FindBy(how = How.ID, using = "schedule-tab")
  private static WebElement schedulesTab;


  @FindBy(how = How.ID, using = "user-tab")
  private static WebElement usersTab;


  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col1 colt1']/span")
  private static WebElement startDate;

  @FindBy(how = How.XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement endDate;

  @FindBy(how = How.XPATH, using = "//div[@id='saveSuccessMsgDiv']")
  private static WebElement errorMsg;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Product Reports')]")
  private static WebElement ProductReportsMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Reports')]")
  private static WebElement ReportsMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Facility List (V1)')]")
  private static WebElement FacilityListingReportMenu;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Facility List (V2)')]")
  private static WebElement FacilityMailingListReportMenu;

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

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Facility List')]")
  private static WebElement facilityListingReportPageHeader;


  public HomePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(testWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
    //SeleneseTestNgHelper.assertTrue(usernameDisplay.isDisplayed());
  }

  public LoginPage logout(String baseurl) throws IOException {

    testWebDriver.waitForElementToAppear(logoutLink);
    logoutLink.click();
    return new LoginPage(testWebDriver, baseurl);
  }

  public boolean verifyWelcomeMessage(String user) {
    testWebDriver.waitForTextToAppear("Welcome " + user);
    return testWebDriver.getPageSource().contains("Welcome " + user);
  }

  public CreateFacilityPage navigateCreateFacility() throws IOException {
    SeleneseTestNgHelper.assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageFacilityMenuItem);
    testWebDriver.keyPress(manageFacilityMenuItem);
    //manageFacilityMenuItem.click();
    verifyTabs();
    clickCreateFacilityButton();
    verifyHeader("Add new facility");
    return new CreateFacilityPage(testWebDriver);
  }

  private void clickCreateFacilityButton() {
    testWebDriver.waitForElementToAppear(createFacility);
    testWebDriver.sleep(1000);
    testWebDriver.keyPress(createFacility);
  }

  private void verifyHeader(String headingToVerify) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(facilityHeader);
    SeleneseTestNgHelper.assertEquals(facilityHeader.getText().trim(), headingToVerify);
  }


  private void verifyTabs() {
    testWebDriver.waitForElementToAppear(facilitiesTab);
    SeleneseTestNgHelper.assertTrue(facilitiesTab.isDisplayed());
    SeleneseTestNgHelper.assertTrue(rolesTab.isDisplayed());
    SeleneseTestNgHelper.assertTrue(schedulesTab.isDisplayed());
    SeleneseTestNgHelper.assertTrue(usersTab.isDisplayed());
  }


  public TemplateConfigPage selectProgramToConfigTemplate(String programme) {
    SeleneseTestNgHelper.assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    //TemplateConfigTab.click();
    testWebDriver.waitForElementToAppear(RnRTemplateConfigTab);
    testWebDriver.keyPress(RnRTemplateConfigTab);
    //RnRTemplateConfigTab.click();
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById(programme));
    //testWebDriver.selectByVisibleText(ProgramDropDown, programme);
    testWebDriver.getElementById(programme).click();

    return new TemplateConfigPage(testWebDriver);
  }

  public String navigateAndInitiateRnr(String program) throws IOException {
    String periodDetails = null;
    testWebDriver.waitForElementToAppear(requisitionsLink);
    testWebDriver.keyPress(requisitionsLink);
    testWebDriver.waitForElementToAppear(createLink);
    testWebDriver.sleep(2000);
    testWebDriver.keyPress(createLink);
    testWebDriver.waitForElementToAppear(myFacilityRadioButton);
    myFacilityRadioButton.click();
    testWebDriver.waitForElementToAppear(programDropDown);
    testWebDriver.selectByVisibleText(programDropDown, program);
    testWebDriver.waitForElementToAppear(startDate);
    periodDetails = startDate.getText().trim() + " - " + endDate.getText().trim();

    return periodDetails;

  }

  public void verifySubMenuItems(String[] expectedSubMenuItem) throws IOException {
    testWebDriver.waitForElementToAppear(requisitionsLink);
    testWebDriver.keyPress(requisitionsLink);
    String[] subMenuItem = SubMenuItem.getText().split("\n");
    SeleneseTestNgHelper.assertEquals(subMenuItem, expectedSubMenuItem);
  }


  public InitiateRnRPage clickProceed() throws IOException {
    testWebDriver.waitForElementToAppear(proceedButton);
    proceedButton.click();

    return new InitiateRnRPage(testWebDriver);
  }


  public ViewRequisitionPage navigateViewRequisition() throws IOException {
    SeleneseTestNgHelper.assertTrue(requisitionMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitonMenuItem);
    testWebDriver.keyPress(viewRequisitonMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitonHeader);
    return new ViewRequisitionPage(testWebDriver);
  }

  public ReportPage navigateReportScreen() throws IOException {
    SeleneseTestNgHelper.assertTrue(reportMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(reportMenuItem);
    testWebDriver.keyPress(reportMenuItem);
    testWebDriver.waitForElementToAppear(reportsTitle);
    return new ReportPage(testWebDriver);
  }

  public DeleteFacilityPage navigateSearchFacility() throws IOException {
    SeleneseTestNgHelper.assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(facilitiesTab);
    facilitiesTab.click();
    return new DeleteFacilityPage(testWebDriver);
  }


  public RolesPage navigateRoleAssignments() throws IOException {
    SeleneseTestNgHelper.assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(rolesTab);
    testWebDriver.keyPress(rolesTab);
    return new RolesPage(testWebDriver);
  }

  public UploadPage navigateUploads() throws IOException {
    SeleneseTestNgHelper.assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(uploadLink);
    uploadLink.click();
    return new UploadPage(testWebDriver);
  }

  public ManageSchedulePage navigateToSchedule() throws IOException {
    SeleneseTestNgHelper.assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(schedulesTab);
    schedulesTab.click();
    return new ManageSchedulePage(testWebDriver);

  }

  public UserPage navigateToUser() throws IOException {
    SeleneseTestNgHelper.assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(usersTab);
    usersTab.click();
    return new UserPage(testWebDriver);

  }

  public ApprovePage navigateToApprove() throws IOException {
    SeleneseTestNgHelper.assertTrue(requisitionMenuItem.isDisplayed());
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(approveLink);
    testWebDriver.keyPress(approveLink);
    return new ApprovePage(testWebDriver);

  }

  public ConvertOrderPage navigateConvertToOrder() throws IOException {
    SeleneseTestNgHelper.assertTrue(requisitionMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(convertToOrderMenuItem);
    testWebDriver.keyPress(convertToOrderMenuItem);
    testWebDriver.waitForElementToAppear(convertToOrderHeader);
    return new ConvertOrderPage(testWebDriver);
  }

  public ViewOrdersPage navigateViewOrders() throws IOException {
    SeleneseTestNgHelper.assertTrue(ordersMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ordersMenuItem);
    testWebDriver.keyPress(ordersMenuItem);
    testWebDriver.waitForElementToAppear(viewOrdersMenuItem);
    testWebDriver.keyPress(viewOrdersMenuItem);
    testWebDriver.waitForElementToAppear(viewOrdersHeader);
    return new ViewOrdersPage(testWebDriver);
  }

  public void verifyErrorMessage() {
    testWebDriver.waitForElementToAppear(errorMsg);
    SeleneseTestNgHelper.assertEquals(errorMsg.getText().trim(), "Requisition not initiated yet");
  }

  public FacilityMailingListReportPage navigateViewFacilityMailingListReport() throws IOException {
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(FacilityMailingListReportMenu);
    testWebDriver.keyPress(FacilityMailingListReportMenu);
    testWebDriver.waitForElementToAppear(facilityListingReportPageHeader);
    return new FacilityMailingListReportPage(testWebDriver);
  }

  public FacilityListingReportPage navigateViewFacilityListingReport() throws IOException {
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(FacilityListingReportMenu);
    testWebDriver.keyPress(FacilityListingReportMenu);
    return new FacilityListingReportPage(testWebDriver);
  }

  public SummaryReportPage navigateViewSummaryReport() throws IOException{
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(SummaryReportMenu);
    testWebDriver.keyPress(SummaryReportMenu);
    return new SummaryReportPage(testWebDriver);
  }

  public NonReportingFacilityReportPage navigateViewNonReportingFacilityReport() throws IOException{
    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(NonReportingFacilityReportMenu);
    testWebDriver.keyPress(NonReportingFacilityReportMenu);
    return new NonReportingFacilityReportPage(testWebDriver);
  }

  public AverageConsumptionReportPage navigateViewAverageConsumptionReport() throws IOException{

    SeleneseTestNgHelper.assertTrue(ReportsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(ReportsMenuItem);
    testWebDriver.keyPress(ReportsMenuItem);
    testWebDriver.waitForElementToAppear(AverageConsumptionReportMenu);
    testWebDriver.keyPress(AverageConsumptionReportMenu);
    return new AverageConsumptionReportPage(testWebDriver);
  }

  public AdjustmentSummaryReportPage navigateViewAdjustmentSummaryReport() throws IOException{

        SeleneseTestNgHelper.assertTrue(ProductReportsMenuItem.isDisplayed());
        testWebDriver.waitForElementToAppear(ProductReportsMenuItem);
        testWebDriver.keyPress(ProductReportsMenuItem);
        testWebDriver.waitForElementToAppear(AdjustmentSummaryReportMenu);
        testWebDriver.keyPress(AdjustmentSummaryReportMenu);
        return new AdjustmentSummaryReportPage(testWebDriver);
    }
  public StockedOutReportPage navigateViewStockedOutReport() throws IOException{

        SeleneseTestNgHelper.assertTrue(ProductReportsMenuItem.isDisplayed());
        testWebDriver.waitForElementToAppear(ProductReportsMenuItem);
        testWebDriver.keyPress(ProductReportsMenuItem);
        testWebDriver.waitForElementToAppear(StockedOutReportMenu);
        testWebDriver.keyPress(StockedOutReportMenu);
        return new StockedOutReportPage(testWebDriver);
    }
  public void goBack(){
      TestWebDriver.getDriver().navigate().back();
  }


}