/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestBase;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.Integer.parseInt;
import static java.lang.Math.round;
import static org.junit.Assert.assertTrue;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EInitiateRnR extends TestCaseHelper {
  public String facility_code;
  public String facility_name;
  public String date_time;
  public String geoZone = "Ngorongoro";
  public String parentGeoZone = "Dodoma";
  public String facilityType = "Lvl3 Hospital";
  public String operatedBy = "MoH";
  public String facilityCodePrefix = "FCcode";
  public String facilityNamePrefix = "FCname";
  public String catchmentPopulation = "500000";
  public String periodDetails;
  public String periodTopSNUser;
  public String program = "HIV";

  public String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
  public String userSICUserName = "storeInCharge";

  //@Before
  public void setUp() throws Exception {
    super.setup();
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }

  @And("^I access create facility page$")
  public void navigateManageFacilityPage() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateManageFacility();
    homePage.clickCreateFacilityButton();
  }

  @When("^I create \"([^\"]*)\" program supported facility$")
  public void createFacilityForProgram(String program) throws Exception {
    ManageFacilityPage manageFacilityPage = ManageFacilityPage.getInstance(testWebDriver);

    date_time = manageFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, catchmentPopulation);
    facility_code = facilityCodePrefix + date_time;
    facility_name = facilityNamePrefix + date_time;
  }

  @Then("^I should see message for successfully created facility$")
  public void verify() throws Exception {
    ManageFacilityPage manageFacilityPage = new ManageFacilityPage(testWebDriver);
    manageFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
  }

  @When("^I create \"([^\"]*)\" role having \"([^\"]*)\" based \"([^\"]*)\" rights$")
  public void createRoleWithRights(String roleName, String roleType, String rightsList) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    String[] roleRights = rightsList.split(",");
    List<String> userRoleListStoreInCharge = new ArrayList<>();
    Collections.addAll(userRoleListStoreInCharge, roleRights);
    createRoleAndAssignRights(homePage, userRoleListStoreInCharge, roleName, roleName, roleType);
  }

  @And("^I setup supervisory node data$")
  public void supervisoryNodeDataSetup() throws Exception {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "Node 2", "N1");
  }

  @And("^I setup warehouse data$")
  public void warehouseDataSetup() throws Exception {
    dbWrapper.insertWarehouseIntoSupplyLinesTable("F11", "HIV", "N1", true);
  }

  @And("^I create users:$")
  public void createUser(DataTable userTable) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data) {
      createUserAndAssignRoles(homePage, passwordUsers, map.get("Email").toString(), map.get("FirstName").toString(), map.get("LastName").toString(), map.get("UserName").toString(), map.get("FacilityCode").toString(), map.get("Program").toString(), map.get("Node").toString(), map.get("Role").toString(), map.get("RoleType").toString(), map.get("Warehouse").toString(), map.get("WarehouseRole").toString());
    }
  }

  @And("^I update user$")
  public void updateUser() throws Exception {
    UserPage userPage = new UserPage(testWebDriver);
    userPage.saveUser();
  }

  @And("^I assign warehouse \"([^\"]*)\" and role \"([^\"]*)\" to user$")
  public void assignWarehouse(String warehouse, String warehouseRole) throws Exception {
    UserPage userPage = new UserPage(testWebDriver);
    userPage.assignWarehouse(warehouse, warehouseRole);
  }

  @And("^I setup product & requisition group data$")
  public void productAndRequisitionGroupDataSetup() throws Exception {
    dbWrapper.updateRoleGroupMember(facility_code);
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
    dbWrapper.insertRequisitionGroupMembers("F10", facility_code);
  }

  @And("^I setup period, schedule & requisition group data$")
  public void periodScheduleAndRequisitionGroupDataSetup() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    ManageSchedulePage manageSchedulePage = homePage.navigateToSchedule();
    manageSchedulePage.createSchedule("Q1stM", "M");
    manageSchedulePage.verifyScheduleCode("Q1stM", "M");
    manageSchedulePage.editSchedule("M1", "M");
    manageSchedulePage.verifyScheduleCode("Q1stM", "M");
    PeriodsPage periodsPage = manageSchedulePage.navigatePeriods();
    periodsPage.createAndVerifyPeriods();
    periodsPage.deleteAndVerifyPeriods();
    dbWrapper.insertRequisitionGroupProgramSchedule();
  }

  @And("^I have period \"([^\"]*)\" associated with schedule \"([^\"]*)\"$")
  public void insertPeriodAndAssociateItWithSchedule(String period, String schedule) throws Exception {
    dbWrapper.insertPeriodAndAssociateItWithSchedule(period, schedule);
  }

  @And("^I update \"([^\"]*)\" home facility$")
  public void updateHomeFacility(String user) throws Exception {
    dbWrapper.allocateFacilityToUser(dbWrapper.getAttributeFromTable("users", "id", "userName", user), facility_code);
  }

  @And("^I configure \"([^\"]*)\" template$")
  public void configureTemplate(String program) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.configureTemplate();
  }

  @And("^I initiate and submit requisition$")
  public void initiateRnR() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);

    periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
    initiateRnRPage.submitRnR();
    initiateRnRPage.verifySubmitRnrErrorMsg();
  }

  @And("^I enter beginning balance as \"([^\"]*)\", quantityDispensed as \"([^\"]*)\", quantityReceived as \"([^\"]*)\" and totalAdjustmentAndLoses as \"([^\"]*)\"$")
  public void enterValuesInRnR(String beginningBalance, String quantityDispensed, String quantityReceived, String totalAdjustmentAndLoses) throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.calculateAndVerifyStockOnHand(parseInt(beginningBalance), parseInt(quantityDispensed),
      parseInt(quantityReceived), parseInt(totalAdjustmentAndLoses));
    initiateRnRPage.verifyTotalField();
  }

  @And("^I verify normalized consumption as \"([^\"]*)\" and amc as \"([^\"]*)\"$")
  public void verifyNormalisedConsumptionAndAmc(String normalisedConsumption, String amc) throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(parseInt(normalisedConsumption));
    initiateRnRPage.verifyAmcForFirstProduct(parseInt(amc));
  }

  @And("^I submit RnR$")
  public void submitRnR() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
  }

  @And("^I initiate and submit emergency requisition$")
  public void initiateEmergencyRnR() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateInitiateRnRScreenAndSelectingRequiredFields(program, "Emergency");
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails,
      geoZone, parentGeoZone, operatedBy, facilityType);
    initiateRnRPage.submitRnR();
    initiateRnRPage.verifySubmitRnrErrorMsg();
    initiateRnRPage.calculateAndVerifyStockOnHand(10, 10, 10, 1);
    initiateRnRPage.verifyTotalField();
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
  }

  @And("^I access proceed$")
  public void accessProceed() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.clickProceed();
  }

  @And("^I add comments$")
  public void addComments() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.clickCommentsButton();
    initiateRnRPage.typeCommentsInCommentsTextArea("Test comment.");
    initiateRnRPage.closeCommentPopUp();
    initiateRnRPage.clickCommentsButton();
    initiateRnRPage.verifyValueInCommentsTextArea("");
    initiateRnRPage.closeCommentPopUp();
    initiateRnRPage.addComments("Dummy Comments.");
    initiateRnRPage.verifyComment("Dummy Comments.", userSICUserName, 1);
  }

  @And("^I update & verify ordered quantities$")
  public void enterAndVerifyOrderedQuantities() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(10, "newPatientFirstProduct");
    initiateRnRPage.enterValue(10, "totalStockOutDaysFirstProduct");
    int expectedCalculatedNC = calculatedExpectedNC(10, 10, 10);
    initiateRnRPage.verifyAmcAndCalculatedOrderQuantity(expectedCalculatedNC, 36, 3, 11);
    Integer packSize = parseInt(dbWrapper.getAttributeFromTable("products", "packSize", "code", "P10"));
    initiateRnRPage.verifyPacksToShip(packSize);
  }

  @And("^I update & verify quantities for emergency RnR$")
  public void enterAndVerifyOrderedQuantitiesForEmergencyRnR() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(10, "newPatientFirstProduct");
    initiateRnRPage.enterValue(10, "totalStockOutDaysFirstProduct");
    int expectedCalculatedNC = calculatedExpectedNC(10, 10, 10);
    initiateRnRPage.verifyAmcAndCalculatedOrderQuantity(expectedCalculatedNC, round(((float) (expectedCalculatedNC + 36) / 2)), 3, 11);
    Integer packSize = Integer.parseInt(dbWrapper.getAttributeFromTable("products", "packSize", "code", "P10"));
    initiateRnRPage.verifyPacksToShip(packSize);
  }

  @And("^I update & verify requested quantities$")
  public void enterAndVerifyRequestedQuantities() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValue(10, "requestedQuantityFirstProduct");
    initiateRnRPage.verifyRequestedQuantityExplanation();
    initiateRnRPage.enterExplanationReason();
    Integer packSize = Integer.parseInt(dbWrapper.getAttributeFromTable("products", "packSize", "code", "P10"));
    initiateRnRPage.verifyPacksToShip(packSize);
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.saveRnR();
  }

  @And("^I add non full supply items & verify total cost$")
  public void enterNonFullSupplyAndVerifyTotalCost() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.addNonFullSupplyLineItems("99", "Due to unforeseen event", "antibiotic", "P11", "Antibiotics");
    initiateRnRPage.calculateAndVerifyTotalCostNonFullSupply();
    initiateRnRPage.verifyCostOnFooterForProducts(1);
  }

  @And("^I authorize RnR$")
  public void authorizeRnR() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.clickFullSupplyTab();
  }

  @And("^I verify normalized consumption as \"([^\"]*)\" and amc as \"([^\"]*)\" for product \"([^\"]*)\" in Database$")
  public void verifyNormalisedConsumptionAndAmcInDatabase(String normalizedConsumption, String amc, String productCode) throws Exception {
    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId, "normalizedConsumption", productCode), normalizedConsumption);
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId, "amc", productCode), amc);
  }

  @Then("^I verify cost & authorize message$")
  public void verifyAuthorizeRnR() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyTotalField();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    initiateRnRPage.verifyApproveButtonNotPresent();
  }

  @Then("^I should not see requisition to approve$")
  public void verifyNoRequisitionToApprove() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @When("^I access requisition on approval page$")
  public void navigateRequisitionApprovalPage() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    ApprovePage approvePage = homePage.navigateToApprove();
    periodTopSNUser = approvePage.clickRequisitionPresentForApproval();
  }

  @Then("I access non full supply tab$")
  public void accessNonFullSupplyTab() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.accessNonFullSupplyTab();
  }

  @Then("I should see RnR Header$")
  public void verifyRnRHeader() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
  }

  @Then("I should see full supply approved quantity$")
  public void verifyFullSupplyApprovedQuantity() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyFullSupplyApprovedQuantity();
  }

  @Then("I should see non full supply approved quantity$")
  public void verifyNonFullSupplyApprovedQuantity() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyNonFullSupplyApprovedQuantity();
  }

  @Then("I should see approved quantity from lower hierarchy$")
  public void verifyApprovedQuantityFromLastHierarchy() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
  }

  @When("I update full supply approve quantity as \"([^\"]*)\"$")
  public void updateFullSupplyApproveQuantity(String approvedQuantity) throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.editFullSupplyApproveQuantity(approvedQuantity);
  }

  @Then("I verify full supply cost for approved quantity \"([^\"]*)\"$")
  public void verifyFullSupplyCost(String approvedQuantity) throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyFullSupplyCost(approvedQuantity);
  }

  @When("I update non full supply approve quantity as \"([^\"]*)\"$")
  public void updateNonFullSupplyApproveQuantity(String approvedQuantity) throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.editNonFullSupplyApproveQuantity(approvedQuantity);
  }

  @Then("I verify non full supply cost for approved quantity \"([^\"]*)\"$")
  public void verifyNonFullSupplyCost(String approvedQuantity) throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyNonFullSupplyCost(approvedQuantity);
  }

  @And("I add comments without save$")
  public void addCommentWithoutSave() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.clickCommentsButton();
    approvePage.typeCommentsInCommentsTextArea("Test comment.");
    approvePage.closeCommentPopUp();
    approvePage.clickCommentsButton();
  }

  @Then("I should see blank comment section$")
  public void verifyBlankCommentTextArea() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyValueInCommentsTextArea("");
    approvePage.closeCommentPopUp();
  }

  @When("I add \"([^\"]*)\" comment$")
  public void addSpecificComment(String comment) throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.addComments(comment);
  }

  @Then("I should see \"([^\"]*)\" comments as \"([^\"]*)\"$")
  public void verifyCommentForUser(String user, String comment) throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyComment(comment, user, 2);
  }

  @And("I should see correct total after authorize$")
  public void verifyTotalAfterAuthorization() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.clickFullSupplyTab();
    approvePage.verifyTotalFieldPostAuthorize();
  }

  @When("I approve requisition$")
  public void approveRequisition() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.clickSaveButton();
    approvePage.clickApproveButton();
    approvePage.clickOk();
  }

  @Then("I should see no requisition pending message$")
  public void verifyNoRequisitionPendingMessage() throws Exception {
    ApprovePage approvePage = new ApprovePage(testWebDriver);
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @When("^I access convert to order page$")
  public void navigateConvertToOrderPage() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateConvertToOrder();
  }

  @Then("^I should see pending order list$")
  public void verifyPendingOrderList() throws Exception {
    ConvertOrderPage convertOrderPageOrdersPending = new ConvertOrderPage(testWebDriver);
    String[] periods = periodTopSNUser.split("-");
    String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", program);
    convertOrderPageOrdersPending.verifyOrderListElements(program, facility_code, facility_name, periods[0].trim(), periods[1].trim(), supplyFacilityName);
  }

  @When("^I convert to order$")
  public void convertToOrderAndVerify() throws Exception {
    ConvertOrderPage convertOrderPageOrdersPending = new ConvertOrderPage(testWebDriver);
    verifyConvertToOrder(convertOrderPageOrdersPending);
  }

  @When("^I access view orders page$")
  public void navigateViewOrdersPage() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateViewOrders();
  }

  @Then("^I should see ordered list with download link$")
  public void verifyOrderListWithDownloadLink() throws Exception {
    verifyOrderedList(true);
  }

  @When("^I do not have anything to pack to ship$")
  public void updatePacksToShip() throws Exception {
    dbWrapper.updatePacksToShip("0");
  }

  @Then("^I should see ordered list without download link$")
  public void verifyOrderListWithoutDownloadLink() throws Exception {
    verifyOrderedList(false);
  }

  @Then("^I verify Regular RnR Type$")
  public void verifyRegularRnRText() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    assertEquals(initiateRnRPage.getRegularLabelText(), "Regular");
  }

  @Then("^I verify Emergency RnR Type$")
  public void verifyEmergencyRnRText() throws Exception {
    InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
    assertEquals(initiateRnRPage.getEmergencyLabelText(), "Emergency");
  }

  @When("^I access Manage POD page$")
  public void navigateManagePodPage() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateManagePOD();
  }

  @Then("^I should see list of orders to manage POD for \"([^\"]*)\" Rnr$")
  public void verifyListOfOrdersOnPodScreen(String rnrType) throws Exception {
    testWebDriver.sleep(1000);
    assertEquals("Central Hospital", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Central Hospital')]")).getText());
    assertEquals("HIV", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'HIV')]")).getText());
    assertEquals("Transfer failed", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Transfer failed')]")).getText());
    assertEquals("Period1 (01/12/2013 - 02/02/2014)", testWebDriver.findElement(By.xpath("//div/span[contains(text(),'Period1 (01/12/2013 - 02/02/2014)')]")).getText());
    assertEquals("Update POD", testWebDriver.findElement(By.xpath("//div/a[contains(text(),'Update POD')]")).getText());
    //TODO find proper xpath or give id for facility_code
    if (rnrType.equals("Emergency")) {
      assertTrue(testWebDriver.findElement(By.xpath("//i[@class='icon-ok']")).isDisplayed());
    }
  }

  @When("^I click on update Pod link for Row \"([^\"]*)\"$")
  public void navigateUploadPodPage(Integer rowNumber) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.navigateManagePOD();
    UpdatePodPage updatePodPage = new UpdatePodPage(testWebDriver);
    updatePodPage.selectRequisitionToUpdatePod(rowNumber);
  }

  @Then("^I should see all products to update pod$")
  public void verifyUpdatePodPage() throws Exception {
    UpdatePodPage updatePodPage = new UpdatePodPage(testWebDriver);
    SeleneseTestBase.assertTrue(updatePodPage.getProductCode(1).contains("P10"));
    SeleneseTestBase.assertTrue(updatePodPage.getProductName(1).contains("antibiotic"));
    SeleneseTestBase.assertFalse(updatePodPage.getProductCode(1).contains("P11"));
  }


  private void createUserAndAssignRoles(HomePage homePage, String passwordUsers, String userEmail,
                                        String userFirstName, String userLastName, String userUserName,
                                        String facility, String program, String supervisoryNode, String role,
                                        String roleType, String warehouse, String warehouseRole) throws IOException, SQLException {
    UserPage userPage = homePage.navigateToUser();
    userPage.enterUserDetails(userUserName, userEmail, userFirstName, userLastName);
    userPage.clickViewHere();
    dbWrapper.updateUser(passwordUsers, userEmail);
    userPage.enterMyFacilityAndMySupervisedFacilityData(facility, program,
      supervisoryNode, role, roleType);
    userPage.assignWarehouse(warehouse, warehouseRole);
    userPage.saveUser();
    userPage.verifyUserUpdated(userFirstName, userLastName);
  }

  private void verifyConvertToOrder(ConvertOrderPage convertOrderPageOrdersPending) {
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.verifyMessageOnOrderScreen("Message 'Please select at least one Requisition for Converting to Order.' is not displayed");
    convertOrderPageOrdersPending.clickCheckBoxConvertToOrder();
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.clickOk();
  }

  private void createRoleAndAssignRights(HomePage homePage, List<String> userRoleList, String roleName, String roleDescription, String roleType) throws IOException {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createRole(roleName, roleDescription, userRoleList, roleType);
    rolesPage.verifyCreatedRoleMessage(roleName);
  }

  private void verifyOrderedList(boolean downloadFlag) throws Exception {
    ViewOrdersPage viewOrdersPage = new ViewOrdersPage(testWebDriver);
    String[] periods = periodTopSNUser.split("-");
    String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", program);
    viewOrdersPage.verifyOrderListElements(program, dbWrapper.getMaxRnrID(), facility_code + " - " + facility_name, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "Transfer failed", downloadFlag);
  }

  public int calculatedExpectedNC(Integer numberOfNewPatients, Integer stockOutDays, Integer quantityDispensed) throws IOException, SQLException {
    int id = dbWrapper.getMaxRnrID();

    Integer dayDiff = parseInt(dbWrapper.getRequisitionLineItemFieldValue((long) id, "reportingDays", "P10"));
    float ans;

    if (dayDiff <= stockOutDays) {
      ans = (quantityDispensed + (numberOfNewPatients * (30 / 10)));
    } else {
      ans = ((quantityDispensed * 30.0f / (dayDiff - stockOutDays))) + (numberOfNewPatients * (30 / 10));
    }
    return round(ans);
  }

  //@After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

}

