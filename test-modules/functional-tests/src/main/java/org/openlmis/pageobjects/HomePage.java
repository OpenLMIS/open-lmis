/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pageobjects;


import org.openlmis.UiUtils.TestWebDriver;
import org.openlmis.pageobjects.edi.ConfigureEDIPage;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.io.IOException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.*;


public class HomePage extends Page {

  @FindBy(how = LINK_TEXT, using = "Logout")
  private static WebElement logoutLink = null;

  @FindBy(how = XPATH, using = "//div[@class='user-info ng-scope']/strong")
  private static WebElement loggedInUserLabel = null;

  @FindBy(how = ID, using = "requisitions-menu")
  private static WebElement requisitionMenuItem = null;

  @FindBy(how = ID, using = "distributions-menu")
  private static WebElement distributionsMenuItem = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Program Product ISA')]")
  private static WebElement programProductISAMenuItem = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Home')]")
  private static WebElement homeMenuItem = null;

  @FindBy(how = ID, using = "reports-menu")
  private static WebElement reportMenuItem = null;

  @FindBy(how = XPATH, using = "//h2/span[contains(text(),'Reports')]")
  private static WebElement reportsTitle = null;

  @FindBy(how = ID, using = "orders-menu")
  private static WebElement ordersMenuItem = null;

  @FindBy(how = ID, using = "approveRnr")
  private static WebElement approveLink = null;

  @FindBy(how = ID, using = "administration-menu")
  private static WebElement AdministrationMenuItem = null;

  @FindBy(how = ID, using = "manage-option")
  private static WebElement manageFacilityMenuItem = null;

  @FindBy(how = ID, using = "convertToOrderRnr")
  private static WebElement convertToOrderMenuItem = null;

  @FindBy(how = ID, using = "manage-distribution")
  private static WebElement manageDistributionMenuItem = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Distributions')]")
  private static WebElement offlineDistributions = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'View Orders')]")
  private static WebElement viewOrdersMenuItem = null;

  @FindBy(how = ID, using = "viewRnr")
  private static WebElement viewRequisitionMenuItem = null;

  @FindBy(how = XPATH, using = "//h2[contains(text(),'View Requisitions')]")
  private static WebElement viewRequisitionHeader = null;

  @FindBy(how = XPATH, using = "//h2[contains(text(),'Convert Requisitions to Order')]")
  private static WebElement convertToOrderHeader = null;

  @FindBy(how = XPATH, using = "//h2[contains(text(),'Manage a Distribution')]")
  private static WebElement manageDistributionHeader = null;

  @FindBy(how = XPATH, using = "//h2[contains(text(),'View Orders')]")
  private static WebElement viewOrdersHeader = null;

  @FindBy(how = ID, using = "add-new-facility")
  private static WebElement createFacility = null;

  @FindBy(how = XPATH, using = "//div[@class='ng-scope']/div[@ng-hide='facility.id']/h2")
  private static WebElement facilityHeader = null;

  @FindBy(how = LINK_TEXT, using = "Configure")
  private static WebElement TemplateConfigTab = null;

  @FindBy(how = LINK_TEXT, using = "R & R Template")
  private static WebElement RnRTemplateConfigTab = null;

  @FindBy(how = LINK_TEXT, using = "EDI File")
  private static WebElement ediFileTab = null;

  @FindBy(how = LINK_TEXT, using = "Regimen Template")
  private static WebElement RegimenTemplateConfigTab = null;

  @FindBy(how = XPATH, using = "//h2[contains(text(),'Regimen Template')]")
  private static WebElement RegimenTemplateHeader = null;

  @FindBy(how = ID, using = "requisitions-menu")
  private static WebElement requisitionsLink = null;

  @FindBy(how = XPATH, using = "//div[@class='submenu']")
  private static WebElement SubMenuItem = null;

  @FindBy(how = ID, using = "createRnr")
  private static WebElement createLink = null;

  @FindBy(how = XPATH, using = "//input[@id='myFacilityRnr']")
  private static WebElement myFacilityRadioButton = null;

  @FindBy(how = LINK_TEXT, using = "Manage")
  private static WebElement manageLink = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Upload')]")
  private static WebElement uploadLink = null;

  @FindBy(how = XPATH, using = "//input[@ng-click='initRnr(row.entity)']")
  private static WebElement proceedButton = null;

  @FindBy(how = ID, using = "facility-tab")
  private static WebElement facilitiesTab = null;

  @FindBy(how = ID, using = "role-tab")
  private static WebElement rolesTab = null;

  @FindBy(how = ID, using = "schedule-tab")
  private static WebElement schedulesTab = null;

  @FindBy(how = ID, using = "user-tab")
  private static WebElement usersTab = null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col1 colt1']/span")
  private static WebElement startDate = null;

  @FindBy(how = XPATH, using = "//div[@class='ngCellText ng-scope col2 colt2']/span")
  private static WebElement endDate = null;

  @FindBy(how = ID, using = "saveSuccessMsgDiv")
  private static WebElement errorMsg = null;

  @FindBy(how = ID, using = "program")
  private static WebElement selectProgramSelectBox = null;

  @FindBy(how = ID, using = "rnrType")
  private static WebElement rnrTypeSelectBox = null;

  @FindBy(how = XPATH, using = "//div/div/div[1]/div[2]/div/span")
  private static WebElement firstPeriodLabel = null;

  @FindBy(how = XPATH, using = "//input[@id='supervisedFacilityRnr']")
  private static WebElement supervisedFacilityRadioButton = null;

  @FindBy(how = XPATH, using = "//select[@id='programListSupervisedFacility']")
  private static WebElement ProgramDropDownSupervisedFacility = null;

  @FindBy(how = ID, using = "facilityList")
  private static WebElement facilityDropDown = null;

  @FindBy(how = XPATH, using = "//select[@id='programListMyFacility']")
  private static WebElement programDropDown = null;

  @FindBy(how = XPATH, using = "//a[contains(text(),'Manage POD')]")
  private static WebElement viewManagePODMenuItem = null;

  @FindBy(how = XPATH, using = "//h2[contains(text(),'Manage Proof of Delivery')]")
  private static WebElement viewManagePODHeader = null;

  public HomePage(TestWebDriver driver) {
    super(driver);
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public WebElement getLogoutLink() {
    return logoutLink;
  }

  public LoginPage logout(String baseUrl) throws IOException {
    testWebDriver.waitForElementToAppear(logoutLink);
    logoutLink.click();
    return new LoginPage(testWebDriver, baseUrl);
  }

  public ManageFacilityPage navigateManageFacility() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    AdministrationMenuItem.click();
    testWebDriver.waitForElementToAppear(manageFacilityMenuItem);
    manageFacilityMenuItem.click();
    return ManageFacilityPage.getInstance(testWebDriver);
  }

  public void clickCreateFacilityButton() {
    testWebDriver.waitForElementToAppear(createFacility);
    testWebDriver.sleep(1000);
    createFacility.click();
  }

  public void verifyHeader(String headingToVerify) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(facilityHeader);
    assertEquals(facilityHeader.getText().trim(), headingToVerify);
  }


  public void verifyAdminTabs() {
    testWebDriver.waitForElementToAppear(facilitiesTab);
    assertTrue(facilitiesTab.isDisplayed());
    assertTrue(rolesTab.isDisplayed());
    assertTrue(schedulesTab.isDisplayed());
    assertTrue(usersTab.isDisplayed());
  }


  public TemplateConfigPage selectProgramToConfigTemplate(String programme) {
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

  public String navigateAndInitiateRnr(String program) {
    navigateRnr();
    myFacilityRadioButton.click();
    testWebDriver.sleep(2000);
    testWebDriver.waitForElementToAppear(programDropDown);
    testWebDriver.selectByVisibleText(programDropDown, program);
    testWebDriver.waitForElementToAppear(startDate);
    return (startDate.getText().trim() + " - " + endDate.getText().trim());
  }

  public void navigateInitiateRnRScreenAndSelectingRequiredFields(String program, String type) throws IOException {
    navigateRnr();
    myFacilityRadioButton.click();
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(programDropDown);
    testWebDriver.selectByVisibleText(programDropDown, program);
    testWebDriver.selectByVisibleText(rnrTypeSelectBox, type);
    testWebDriver.sleep(1000);
  }

  public void clickRequisitionSubMenuItem() throws IOException {
    testWebDriver.waitForElementToAppear(requisitionsLink);
    testWebDriver.keyPress(requisitionsLink);
  }

  public void verifySubMenuItems(String[] expectedSubMenuItem) throws IOException {
    String[] subMenuItem = SubMenuItem.getText().split("\n");
    assertEquals(subMenuItem, expectedSubMenuItem);
  }

  public InitiateRnRPage clickProceed() throws IOException {
    testWebDriver.setImplicitWait(100);
    testWebDriver.waitForElementToAppear(proceedButton);
    proceedButton.click();
    testWebDriver.sleep(1000);
    return new InitiateRnRPage(testWebDriver);
  }


  public ViewRequisitionPage navigateViewRequisition() throws IOException {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitionMenuItem);
    testWebDriver.keyPress(viewRequisitionMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitionHeader);
    return new ViewRequisitionPage(testWebDriver);
  }

  public ReportPage navigateReportScreen() throws IOException {
    testWebDriver.waitForElementToAppear(reportMenuItem);
    testWebDriver.keyPress(reportMenuItem);
    testWebDriver.waitForElementToAppear(reportsTitle);
    return new ReportPage(testWebDriver);
  }

  public ManageFacilityPage navigateSearchFacility() throws IOException {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(facilitiesTab);
    facilitiesTab.click();
    return ManageFacilityPage.getInstance(testWebDriver);
  }


  public RolesPage navigateRoleAssignments() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(rolesTab);
    testWebDriver.keyPress(rolesTab);
    return new RolesPage(testWebDriver);
  }

  public UploadPage navigateUploads() throws IOException {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(uploadLink);
    uploadLink.click();
    return new UploadPage(testWebDriver);
  }

  public ManageSchedulePage navigateToSchedule() throws IOException {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(schedulesTab);
    schedulesTab.click();
    return new ManageSchedulePage(testWebDriver);

  }

  public UserPage navigateToUser() throws IOException {
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
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(approveLink);
    testWebDriver.keyPress(approveLink);
    return new ApprovePage(testWebDriver);

  }

  public ConvertOrderPage navigateConvertToOrder() throws IOException {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(convertToOrderMenuItem);
    testWebDriver.keyPress(convertToOrderMenuItem);
    testWebDriver.sleep(5000);
    testWebDriver.waitForElementToAppear(convertToOrderHeader);
    return new ConvertOrderPage(testWebDriver);
  }

  public DistributionPage navigateToDistributionWhenOnline() throws IOException {
    testWebDriver.waitForElementToAppear(distributionsMenuItem);
    testWebDriver.keyPress(distributionsMenuItem);
    testWebDriver.waitForElementToAppear(manageDistributionMenuItem);
    testWebDriver.keyPress(manageDistributionMenuItem);
    testWebDriver.waitForElementToAppear(manageDistributionHeader);
    return new DistributionPage(testWebDriver);
  }

  public DistributionPage navigateOfflineDistribution() throws IOException {
    testWebDriver.waitForElementToAppear(offlineDistributions);
    testWebDriver.keyPress(offlineDistributions);
    testWebDriver.waitForElementToAppear(manageDistributionMenuItem);
    testWebDriver.keyPress(manageDistributionMenuItem);
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
    testWebDriver.waitForElementToAppear(homeMenuItem);
    testWebDriver.keyPress(homeMenuItem);
    testWebDriver.sleep(500);
    return new HomePage(testWebDriver);
  }

  public ViewOrdersPage navigateViewOrders() throws IOException {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(ordersMenuItem);
    testWebDriver.keyPress(ordersMenuItem);
    testWebDriver.waitForElementToAppear(viewOrdersMenuItem);
    testWebDriver.keyPress(viewOrdersMenuItem);
    testWebDriver.waitForElementToAppear(viewOrdersHeader);
    return new ViewOrdersPage(testWebDriver);
  }

  public String getErrorMessage() {
    testWebDriver.waitForElementToAppear(errorMsg);
    return errorMsg.getText().trim();
  }

  public void verifyLoggedInUser(String Username) {
    testWebDriver.waitForElementToAppear(loggedInUserLabel);
    assertEquals(loggedInUserLabel.getText(), Username);
  }

  public void navigateAndInitiateEmergencyRnr(String program) {
    navigateRnr();
    myFacilityRadioButton.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(programDropDown);
    testWebDriver.selectByVisibleText(programDropDown, program);
    testWebDriver.selectByVisibleText(rnrTypeSelectBox, "Emergency");
  }

  public String getFirstPeriod() {
    return firstPeriodLabel.getText().trim();
  }

  public void navigateRnr() {
    testWebDriver.waitForElementToBeEnabled(requisitionsLink);
    testWebDriver.keyPress(requisitionsLink);
    testWebDriver.waitForElementToBeEnabled(createLink);
    testWebDriver.sleep(1000);
    testWebDriver.keyPress(createLink);
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(myFacilityRadioButton);
  }

  public boolean isHomeMenuTabDisplayed() {
    return homeMenuItem.isDisplayed();
  }

  public boolean isRequisitionsMenuTabDisplayed() {
    return requisitionMenuItem.isDisplayed();
  }

  public void navigateAndInitiateRnrForSupervisedFacility(String program) throws IOException {
    navigateRnr();
    supervisedFacilityRadioButton.click();
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(ProgramDropDownSupervisedFacility);
    testWebDriver.selectByVisibleText(ProgramDropDownSupervisedFacility, program);
    testWebDriver.sleep(1000);
  }

  public void selectFacilityForSupervisoryNodeRnR(String facilityName) {
    testWebDriver.waitForElementToAppear(facilityDropDown);
    testWebDriver.selectByVisibleText(facilityDropDown, facilityName);
    testWebDriver.sleep(100);
  }

  public String getFacilityDropDownList() {
    return facilityDropDown.getText();
  }

  public String getFacilityDropDownListForViewRequisition() {
    return testWebDriver.findElement(By.name("selectFacility")).getText();
  }

  public ManagePodPage navigateManagePOD() throws IOException {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(ordersMenuItem);
    testWebDriver.keyPress(ordersMenuItem);
    testWebDriver.waitForElementToAppear(viewManagePODMenuItem);
    testWebDriver.keyPress(viewManagePODMenuItem);
    testWebDriver.waitForElementToAppear(viewManagePODHeader);
    return new ManagePodPage(testWebDriver);
  }
}

