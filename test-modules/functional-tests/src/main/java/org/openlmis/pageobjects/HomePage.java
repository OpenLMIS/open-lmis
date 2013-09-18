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

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openqa.selenium.support.How.ID;


public class HomePage extends Page {

  @FindBy(how = How.XPATH, using = "//strong[@class='ng-binding']")
  private static WebElement usernameDisplay;

  @FindBy(how = How.LINK_TEXT, using = "Logout")

  private static WebElement logoutLink;

  @FindBy(how = How.XPATH, using = "//div[@class='user-info ng-scope']/strong")
  private static WebElement loggedInUserLabel;

  @FindBy(how = How.ID, using = "requisitions-menu")
  private static WebElement requisitionMenuItem;

  @FindBy(how = ID, using = "distributions-menu")
  private static WebElement distributionsMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Program Product ISA')]")
  private static WebElement programProductISAMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Home')]")
  private static WebElement homeMenuItem;

  @FindBy(how = How.ID, using = "reports-menu")
  private static WebElement reportMenuItem;

  @FindBy(how = How.XPATH, using = "//h2/span[contains(text(),'Reports')]")
  private static WebElement reportsTitle;

  @FindBy(how = How.ID, using = "orders-menu")
  private static WebElement ordersMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Approve')]")
  private static WebElement approveLink;

  @FindBy(how = How.ID, using = "administration-menu")
  private static WebElement AdministrationMenuItem;

  @FindBy(how = How.ID, using = "manage-option")
  private static WebElement manageFacilityMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Facilities')]")
  private static WebElement facilityMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Roles')]")
  private static WebElement manageRoleAssignmentLink;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Convert to Order')]")
  private static WebElement convertToOrderMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Manage')]")
  private static WebElement manageMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'Distributions')]")
  private static WebElement offlineDistributions;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'View Orders')]")
  private static WebElement viewOrdersMenuItem;

  @FindBy(how = How.XPATH, using = "//a[contains(text(),'View')]")
  private static WebElement viewRequisitonMenuItem;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'View Requisitions')]")
  private static WebElement viewRequisitonHeader;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Convert Requisitions to Order')]")
  private static WebElement convertToOrderHeader;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Manage a Distribution')]")
  private static WebElement manageDistributionHeader;

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

  @FindBy(how = How.LINK_TEXT, using = "EDI File")
  private static WebElement ediFileTab;

  @FindBy(how = How.LINK_TEXT, using = "Regimen Template")
  private static WebElement RegimenTemplateConfigTab;

  @FindBy(how = How.XPATH, using = "//h2[contains(text(),'Regimen Template')]")
  private static WebElement RegimenTemplateHeader;

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

  @FindBy(how = ID, using = "program")
  private static WebElement selectProgramSelectBox;


  public HomePage(TestWebDriver driver) throws IOException {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public  WebElement getLogoutLink() {
    return logoutLink;
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
    assertTrue(AdministrationMenuItem.isDisplayed());
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
    assertTrue(facilitiesTab.isDisplayed());
    assertTrue(rolesTab.isDisplayed());
    assertTrue(schedulesTab.isDisplayed());
    assertTrue(usersTab.isDisplayed());
  }


  public TemplateConfigPage selectProgramToConfigTemplate(String programme) {
    assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    testWebDriver.waitForElementToAppear(RnRTemplateConfigTab);
    testWebDriver.keyPress(RnRTemplateConfigTab);
    testWebDriver.waitForElementToAppear(testWebDriver.getElementById(programme));
    testWebDriver.getElementById(programme).click();

    return new TemplateConfigPage(testWebDriver);
  }

  public ConfigureEDIPage navigateEdiScreen() throws IOException {
    assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    testWebDriver.waitForElementToAppear(ediFileTab);
    testWebDriver.keyPress(ediFileTab);

    return new ConfigureEDIPage(testWebDriver);
  }

  public RegimenTemplateConfigPage navigateToRegimenConfigTemplate() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    testWebDriver.waitForElementToAppear(RegimenTemplateConfigTab);
    testWebDriver.keyPress(RegimenTemplateConfigTab);
    testWebDriver.waitForElementToAppear(RegimenTemplateHeader);

    return new RegimenTemplateConfigPage(testWebDriver);
  }

  public String navigateAndInitiateRnr(String program) throws IOException {
    String periodDetails = null;
    testWebDriver.waitForElementToAppear(requisitionsLink);
    testWebDriver.keyPress(requisitionsLink);
    testWebDriver.waitForElementToAppear(createLink);
    testWebDriver.sleep(2000);
    testWebDriver.keyPress(createLink);
    testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(myFacilityRadioButton);
    myFacilityRadioButton.click();
    testWebDriver.sleep(2000);
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
//    testWebDriver.handleScrollByPixels(0,2000);
    testWebDriver.waitForElementToAppear(proceedButton);
    proceedButton.click();
    testWebDriver.sleep(1000);
    return new InitiateRnRPage(testWebDriver);
  }


  public ViewRequisitionPage navigateViewRequisition() throws IOException {
      testWebDriver.sleep(1000);
    assertTrue(requisitionMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitonMenuItem);
    testWebDriver.keyPress(viewRequisitonMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitonHeader);
    return new ViewRequisitionPage(testWebDriver);
  }

  public ReportPage navigateReportScreen() throws IOException {
    assertTrue(reportMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(reportMenuItem);
    testWebDriver.keyPress(reportMenuItem);
    testWebDriver.waitForElementToAppear(reportsTitle);
    return new ReportPage(testWebDriver);
  }

  public DeleteFacilityPage navigateSearchFacility() throws IOException {
    assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(facilitiesTab);
    facilitiesTab.click();
    return new DeleteFacilityPage(testWebDriver);
  }


  public RolesPage navigateRoleAssignments() throws IOException {
    assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(rolesTab);
    testWebDriver.keyPress(rolesTab);
    return new RolesPage(testWebDriver);
  }

  public UploadPage navigateUploads() throws IOException {
    assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(uploadLink);
    uploadLink.click();
    return new UploadPage(testWebDriver);
  }

  public ManageSchedulePage navigateToSchedule() throws IOException {
    assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(schedulesTab);
    schedulesTab.click();
    return new ManageSchedulePage(testWebDriver);

  }

  public UserPage navigateToUser() throws IOException {
    assertTrue(AdministrationMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(usersTab);
    usersTab.click();
    return new UserPage(testWebDriver);

  }

  public ApprovePage navigateToApprove() throws IOException {
      testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    assertTrue(requisitionMenuItem.isDisplayed());
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(approveLink);
    testWebDriver.keyPress(approveLink);
    return new ApprovePage(testWebDriver);

  }

  public ConvertOrderPage navigateConvertToOrder() throws IOException {
    testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    assertTrue(requisitionMenuItem.isDisplayed());
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(convertToOrderMenuItem);
    testWebDriver.keyPress(convertToOrderMenuItem);
    testWebDriver.sleep(3500);
    testWebDriver.waitForElementToAppear(convertToOrderHeader);
    return new ConvertOrderPage(testWebDriver);
  }

  public DistributionPage navigatePlanDistribution() throws IOException {
    assertTrue(distributionsMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(distributionsMenuItem);
    testWebDriver.keyPress(distributionsMenuItem);
    testWebDriver.waitForElementToAppear(manageMenuItem);
    testWebDriver.keyPress(manageMenuItem);
    testWebDriver.waitForElementToAppear(manageDistributionHeader);
    return new DistributionPage(testWebDriver);
  }

    public DistributionPage navigateOfflineDistribution() throws IOException {
        assertTrue(offlineDistributions.isDisplayed());
        testWebDriver.waitForElementToAppear(offlineDistributions);
        testWebDriver.keyPress(offlineDistributions);
        testWebDriver.waitForElementToAppear(manageMenuItem);
        testWebDriver.keyPress(manageMenuItem);
        return new DistributionPage(testWebDriver);
    }

  public ProgramProductISAPage navigateProgramProductISA() throws IOException {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    testWebDriver.waitForElementToAppear(programProductISAMenuItem);
    testWebDriver.keyPress(programProductISAMenuItem);
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    return new ProgramProductISAPage(testWebDriver);
  }

  public HomePage navigateHomePage() throws IOException {
    assertTrue(homeMenuItem.isDisplayed());
    testWebDriver.waitForElementToAppear(homeMenuItem);
    testWebDriver.keyPress(homeMenuItem);
    testWebDriver.sleep(500);
    return new HomePage(testWebDriver);
  }

  public ViewOrdersPage navigateViewOrders() throws IOException {
    assertTrue(ordersMenuItem.isDisplayed());
    testWebDriver.sleep(3000);
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

    public void verifyLoggedInUser(String Username) {
        testWebDriver.waitForElementToAppear(loggedInUserLabel);
        SeleneseTestNgHelper.assertEquals(loggedInUserLabel.getText(), Username);
    }
}