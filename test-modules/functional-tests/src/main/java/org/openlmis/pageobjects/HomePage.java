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
import org.openlmis.pageobjects.edi.ConfigureSystemSettingsPage;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import java.util.NoSuchElementException;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static org.openqa.selenium.support.How.ID;
import static org.openqa.selenium.support.How.XPATH;


public class HomePage extends Page {

  @FindBy(how = ID, using = "logout")
  private static WebElement logoutLink = null;

  @FindBy(how = XPATH, using = "//div[@class='user-info ng-scope']/strong")
  private static WebElement loggedInUserLabel = null;

  @FindBy(how = ID, using = "requisitions-menu")
  private static WebElement requisitionMenuItem = null;

  @FindBy(how = ID, using = "distributions-menu")
  private static WebElement distributionsMenuItem = null;

  @FindBy(how = ID, using = "configureProgramProductIsa")
  private static WebElement programProductISAMenuItem = null;

  @FindBy(how = ID, using = "homeMenu")
  private static WebElement homeMenuItem = null;

  @FindBy(how = ID, using = "report-menu")
  private static WebElement reportMenuItem = null;

  @FindBy(how = ID, using = "reportHeader")
  private static WebElement reportsTitle = null;

  @FindBy(how = ID, using = "orders-menu")
  private static WebElement ordersMenuItem = null;

  @FindBy(how = ID, using = "approveRnr")
  private static WebElement approveLink = null;

  @FindBy(how = ID, using = "administration-menu")
  private static WebElement AdministrationMenuItem = null;

  @FindBy(how = ID, using = "manage-option")
  private static WebElement manageLink = null;

  @FindBy(how = ID, using = "convertToOrderRnr")
  private static WebElement convertToOrderMenuItem = null;

  @FindBy(how = ID, using = "manage-distribution")
  private static WebElement manageDistributionMenuItem = null;

  @FindBy(how = ID, using = "distributions-menu")
  private static WebElement offlineDistributions = null;

  @FindBy(how = ID, using = "viewOrder")
  private static WebElement viewOrdersMenuItem = null;

  @FindBy(how = ID, using = "viewRnr")
  private static WebElement viewRequisitionMenuItem = null;

  @FindBy(how = ID, using = "viewRequisitionHeader")
  private static WebElement viewRequisitionHeader = null;

  @FindBy(how = ID, using = "convertToOrderHeader")
  private static WebElement convertToOrderHeader = null;

  @FindBy(how = ID, using = "manageDistributionHeader")
  private static WebElement manageDistributionHeader = null;

  @FindBy(how = ID, using = "viewOrderHeader")
  private static WebElement viewOrdersHeader = null;

  @FindBy(how = ID, using = "facilityAddNew")
  private static WebElement createFacility = null;

  @FindBy(how = ID, using = "addNewFacilityHeader")
  private static WebElement addNewFacilityHeader = null;

  @FindBy(how = ID, using = "configure")
  private static WebElement TemplateConfigTab = null;

  @FindBy(how = ID, using = "configureRequisitionTemplate")
  private static WebElement RnRTemplateConfigTab = null;

  @FindBy(how = ID, using = "configureEdi")
  private static WebElement ediFileTab = null;

  @FindBy(how = ID, using = "configureRegimenTemplate")
  private static WebElement regimenTemplateConfigTab = null;

  @FindBy(how = ID, using = "regimenTemplateHeader")
  private static WebElement regimenTemplateHeader = null;

  @FindBy(how = ID, using = "requisitions-menu")
  private static WebElement requisitionsLink = null;

  @FindBy(how = XPATH, using = "//div[@class='submenu']")
  private static WebElement subMenuItem = null;

  @FindBy(how = ID, using = "createRnr")
  private static WebElement createLink = null;

  @FindBy(how = ID, using = "myFacilityRnr")
  private static WebElement myFacilityRadioButton = null;

  @FindBy(how = ID, using = "upload")
  private static WebElement uploadLink = null;

  @FindBy(how = XPATH, using = "//input[@ng-click='initRnr(row.entity)']")
  private static WebElement proceedButton = null;

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

  @FindBy(how = ID, using = "supervisedFacilityRnr")
  private static WebElement supervisedFacilityRadioButton = null;

  @FindBy(how = ID, using = "programListSupervisedFacility")
  private static WebElement ProgramDropDownSupervisedFacility = null;

  @FindBy(how = ID, using = "facilityList")
  private static WebElement facilityDropDown = null;

  @FindBy(how = ID, using = "programListMyFacility")
  private static WebElement programDropDown = null;

  @FindBy(how = ID, using = "managePod")
  private static WebElement viewManagePODMenuItem = null;

  @FindBy(how = ID, using = "managePodHeader")
  private static WebElement viewManagePODHeader = null;

  @FindBy(how = ID, using = "requisitionGroupTab")
  private static WebElement requisitionGroupTab = null;

  @FindBy(how = ID, using = "supervisoryNodeTab")
  private static WebElement supervisoryNodesTab = null;

  @FindBy(how = ID, using = "geoZoneTab")
  private static WebElement geoZoneTab = null;

  @FindBy(how = ID, using = "supplyLineTab")
  private static WebElement supplyLineTab = null;

  @FindBy(how = ID, using = "facilityApprovedProductTab")
  private static WebElement facilityApprovedProductTab;

  @FindBy(how = ID, using = "productTab")
  private static WebElement productTab;

  @FindBy(how = ID, using = "facilityMenu")
  private static WebElement facilityMenu;

  @FindBy(how = ID, using = "rolesMenu")
  private static WebElement rolesMenu;

  @FindBy(how = ID, using = "schedulesMenu")
  private static WebElement schedulesMenu;

  @FindBy(how = ID, using = "usersMenu")
  private static WebElement usersMenu;

  @FindBy(how = ID, using = "geographicZonesMenu")
  private static WebElement geographicZonesMenu;

  @FindBy(how = ID, using = "supervisoryNodesMenu")
  private static WebElement supervisoryNodesMenu;

  @FindBy(how = ID, using = "requisitionGroupsMenu")
  private static WebElement requisitionGroupsMenu;

  @FindBy(how = ID, using = "supplyLinesMenu")
  private static WebElement supplyLinesMenu;

  @FindBy(how = ID, using = "facilityApprovedProductsMenu")
  private static WebElement facilityApprovedProductsMenu;

  @FindBy(how = ID, using = "productsMenu")
  private static WebElement productsMenu;

  public HomePage(TestWebDriver driver) {
    super(driver);
    driver.getUrl(  LoginPage.baseUrl );
    PageFactory.initElements(new AjaxElementLocatorFactory(TestWebDriver.getDriver(), 10), this);
    testWebDriver.setImplicitWait(10);
  }

  public WebElement getLogoutLink() {
    return logoutLink;
  }

  public LoginPage logout(String baseUrl) {
      testWebDriver.setImplicitWait(10);
    testWebDriver.waitForElementToAppear(logoutLink);
    logoutLink.click();
    return PageObjectFactory.getLoginPage(testWebDriver, baseUrl);
  }

  public FacilityPage navigateManageFacility() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(facilityMenu);
    facilityMenu.click();
    return PageObjectFactory.getFacilityPage(testWebDriver);
  }

  public GeographicZonePage navigateManageGeographicZonesPage() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    AdministrationMenuItem.click();
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(geographicZonesMenu);
    geographicZonesMenu.click();
    return PageObjectFactory.getGeographicZonePage(testWebDriver);
  }

  public void clickCreateFacilityButton() {
    testWebDriver.waitForElementToAppear(createFacility);
    testWebDriver.sleep(1000);
    createFacility.click();
  }

  public void verifyHeader(String headingToVerify) {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(addNewFacilityHeader);
    assertEquals(addNewFacilityHeader.getText().trim(), headingToVerify);
  }

  public boolean isSupervisoryNodeTabDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(supervisoryNodesTab);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return supervisoryNodesTab.isDisplayed();
  }

  public boolean isSupplyLineTabDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(supplyLineTab);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return supplyLineTab.isDisplayed();
  }

  public boolean isRequisitionGroupTabDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(requisitionGroupTab);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return requisitionGroupTab.isDisplayed();
  }

  public boolean isFacilityApprovedProductTabDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(facilityApprovedProductTab);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return facilityApprovedProductTab.isDisplayed();
  }

  public boolean isProductTabDisplayed() {
    try {
      testWebDriver.waitForElementToAppear(productTab);
    } catch (TimeoutException e) {
      return false;
    } catch (NoSuchElementException e) {
      return false;
    }
    return productTab.isDisplayed();
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
    return PageObjectFactory.getTemplateConfigPage(testWebDriver);
  }

  public ConfigureSystemSettingsPage navigateSystemSettingsScreen() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    testWebDriver.waitForElementToAppear(ediFileTab);
    testWebDriver.keyPress(ediFileTab);
    return PageObjectFactory.getConfigureSystemSettingsPage(testWebDriver);
  }

  public RegimenTemplateConfigPage navigateToRegimenConfigTemplate() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    testWebDriver.waitForElementToAppear(regimenTemplateConfigTab);
    testWebDriver.keyPress(regimenTemplateConfigTab);
    testWebDriver.waitForElementToAppear(regimenTemplateHeader);
    return PageObjectFactory.getRegimenTemplateConfigPage(testWebDriver);
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

  public void navigateInitiateRnRScreenAndSelectingRequiredFields(String program, String type) {
    navigateRnr();
    myFacilityRadioButton.click();
    testWebDriver.sleep(500);
    testWebDriver.waitForElementToAppear(programDropDown);
    testWebDriver.selectByVisibleText(programDropDown, program);
    testWebDriver.selectByVisibleText(rnrTypeSelectBox, type);
    testWebDriver.sleep(1000);
  }

  public void clickRequisitionSubMenuItem() {
    testWebDriver.waitForElementToAppear(requisitionsLink);
    testWebDriver.keyPress(requisitionsLink);
  }

  public void verifySubMenuItems(String[] expectedSubMenuItem) {
    String[] subMenuItem = HomePage.subMenuItem.getText().split("\n");
    assertEquals(subMenuItem, expectedSubMenuItem);
  }

  public InitiateRnRPage clickProceed() {
    testWebDriver.setImplicitWait(100);
    testWebDriver.waitForElementToAppear(proceedButton);
    proceedButton.click();
    testWebDriver.sleep(1000);
    return PageObjectFactory.getInitiateRnRPage(testWebDriver);
  }

  public ViewRequisitionPage navigateViewRequisition() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitionMenuItem);
    testWebDriver.keyPress(viewRequisitionMenuItem);
    testWebDriver.waitForElementToAppear(viewRequisitionHeader);
    return PageObjectFactory.getViewRequisitionPage(testWebDriver);
  }

  public ReportPage navigateReportScreen() {
    testWebDriver.waitForElementToAppear(reportMenuItem);
    testWebDriver.keyPress(reportMenuItem);
    testWebDriver.waitForElementToAppear(reportsTitle);
    return PageObjectFactory.getReportPage(testWebDriver);
  }

  public RolesPage navigateToRolePage() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(rolesMenu);
    testWebDriver.keyPress(rolesMenu);
    return PageObjectFactory.getRolesPage(testWebDriver);
  }

  public UploadPage navigateUploads() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(uploadLink);
    uploadLink.click();
    return PageObjectFactory.getUploadPage(testWebDriver);
  }

  public ManageSchedulePage navigateToSchedule() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(schedulesMenu);
    schedulesMenu.click();
    return PageObjectFactory.getManageSchedulePage(testWebDriver);
  }

  public UserPage navigateToUser() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(usersMenu);
    usersMenu.click();
    return PageObjectFactory.getUserPage(testWebDriver);
  }

  public ApprovePage navigateToApprove() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(approveLink);
    testWebDriver.keyPress(approveLink);
    testWebDriver.sleep(500);
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    testWebDriver.waitForAjax();
    approvePage.waitForPageToAppear();
    return approvePage;
  }

  public ConvertOrderPage navigateConvertToOrder() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(requisitionMenuItem);
    testWebDriver.keyPress(requisitionMenuItem);
    testWebDriver.waitForElementToAppear(convertToOrderMenuItem);
    testWebDriver.keyPress(convertToOrderMenuItem);
    testWebDriver.sleep(5000);
    testWebDriver.waitForElementToAppear(convertToOrderHeader);
    return PageObjectFactory.getConvertOrderPage(testWebDriver);
  }

  public DistributionPage navigateToDistributionWhenOnline() {
    testWebDriver.waitForElementToAppear(distributionsMenuItem);
    testWebDriver.keyPress(distributionsMenuItem);
    testWebDriver.waitForElementToAppear(manageDistributionMenuItem);
    testWebDriver.keyPress(manageDistributionMenuItem);
    testWebDriver.waitForElementToAppear(manageDistributionHeader);
    return PageObjectFactory.getDistributionPage(testWebDriver);
  }

  public DistributionPage navigateOfflineDistribution() {
    testWebDriver.waitForElementToAppear(offlineDistributions);
    testWebDriver.keyPress(offlineDistributions);
    testWebDriver.waitForElementToAppear(manageDistributionMenuItem);
    testWebDriver.keyPress(manageDistributionMenuItem);
    return PageObjectFactory.getDistributionPage(testWebDriver);
  }

  public ProgramProductISAPage navigateProgramProductISA() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(TemplateConfigTab);
    testWebDriver.keyPress(TemplateConfigTab);
    testWebDriver.waitForElementToAppear(programProductISAMenuItem);
    testWebDriver.keyPress(programProductISAMenuItem);
    testWebDriver.waitForElementToAppear(selectProgramSelectBox);
    return PageObjectFactory.getProgramProductIsaPage(testWebDriver);
  }

  public HomePage navigateHomePage() {
    testWebDriver.waitForElementToAppear(homeMenuItem);
    testWebDriver.keyPress(homeMenuItem);
    testWebDriver.sleep(500);
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public HomePage navigateOfflineHomePage() {
    WebElement homeOfflineMenu = testWebDriver.getElementByXpath("//ng-include[2]/div/ul[1]/li[1]/a");
    testWebDriver.waitForElementToAppear(homeOfflineMenu);
    testWebDriver.keyPress(homeOfflineMenu);
    testWebDriver.sleep(500);
    return PageObjectFactory.getHomePage(testWebDriver);
  }

  public ViewOrdersPage navigateViewOrders() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(ordersMenuItem);
    testWebDriver.keyPress(ordersMenuItem);
    testWebDriver.waitForElementToAppear(viewOrdersMenuItem);
    testWebDriver.keyPress(viewOrdersMenuItem);
    testWebDriver.waitForElementToAppear(viewOrdersHeader);
    return PageObjectFactory.getViewOrdersPage(testWebDriver);
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
    testWebDriver.selectByVisibleText(rnrTypeSelectBox, "Emergency");
    testWebDriver.selectByVisibleText(programDropDown, program);
    testWebDriver.waitForAjax();
  }

  public String getFirstPeriod() {
    return firstPeriodLabel.getText().trim();
  }

  public void navigateRnr() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToBeEnabled(requisitionsLink);
    testWebDriver.keyPress(requisitionsLink);
    testWebDriver.waitForElementToBeEnabled(createLink);
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

  public void navigateAndInitiateRnrForSupervisedFacility(String program) {
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

  public ManagePodPage navigateManagePOD() {
    testWebDriver.sleep(1000);
    testWebDriver.waitForElementToAppear(ordersMenuItem);
    testWebDriver.keyPress(ordersMenuItem);
    testWebDriver.waitForElementToAppear(viewManagePODMenuItem);
    testWebDriver.keyPress(viewManagePODMenuItem);
    testWebDriver.waitForElementToAppear(viewManagePODHeader);
    return PageObjectFactory.getManagePodPage(testWebDriver);
  }

  public SupervisoryNodesPage navigateToSupervisoryNodes() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(supervisoryNodesMenu);
    supervisoryNodesMenu.click();
    return PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
  }

  public RequisitionGroupPage navigateToRequisitionGroupPage() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(requisitionGroupsMenu);
    requisitionGroupsMenu.click();
    return PageObjectFactory.getRequisitionGroupPage(testWebDriver);
  }

  public SupplyLinePage navigateToSupplyLine() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(supplyLinesMenu);
    supplyLinesMenu.click();
    return PageObjectFactory.getSupplyLinePage(testWebDriver);
  }

  public FacilityApprovedProductPage navigateToFacilityApprovedProductPage() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(facilityApprovedProductsMenu);
    facilityApprovedProductsMenu.click();
    return PageObjectFactory.getFacilityApprovedProductPage(testWebDriver);
  }

  public ProductPage navigateToProductPage() {
    testWebDriver.waitForElementToAppear(AdministrationMenuItem);
    testWebDriver.keyPress(AdministrationMenuItem);
    testWebDriver.waitForElementToAppear(manageLink);
    testWebDriver.keyPress(manageLink);
    testWebDriver.waitForElementToAppear(productsMenu);
    productsMenu.click();
    return PageObjectFactory.getProductPage(testWebDriver);
  }
}

