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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.lang.Integer.parseInt;
import static java.lang.Math.round;

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

  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }

  @And("^I access create facility page$")
  public void navigateManageFacilityPage() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateManageFacility();
    homePage.clickCreateFacilityButton();
  }

  @When("^I create \"([^\"]*)\" program supported facility$")
  public void createFacilityForProgram(String program) {
    FacilityPage facilityPage = PageObjectFactory.getFacilityPage(testWebDriver);

    date_time = facilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, catchmentPopulation);
    facility_code = facilityCodePrefix + date_time;
    facility_name = facilityNamePrefix + date_time;
  }

  @Then("^I should see message for successfully created facility$")
  public void verify() {
    FacilityPage facilityPage = PageObjectFactory.getFacilityPage(testWebDriver);
    facilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
  }

  @When("^I create \"([^\"]*)\" role having \"([^\"]*)\" based \"([^\"]*)\" rights$")
  public void createRoleWithRights(String roleName, String roleType, String rightsList) {
    String[] roleRights = rightsList.split(",");
    List<String> userRoleListStoreInCharge = new ArrayList<>();
    Collections.addAll(userRoleListStoreInCharge, roleRights);
    createRoleAndAssignRights(userRoleListStoreInCharge, roleName, roleName, roleType);
  }

  @And("^I setup supervisory node data$")
  public void supervisoryNodeDataSetup() throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.deleteSupervisoryNodes();
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node 2", "N1");
  }

  @And("^I setup warehouse data$")
  public void warehouseDataSetup() throws SQLException {
    dbWrapper.insertWarehouseIntoSupplyLinesTable("F11", "HIV", "N1", true);
  }

  @And("^I create users:$")
  public void createUser(DataTable userTable) throws SQLException {
    List<Map<String, String>> data = userTable.asMaps(String.class, String.class);
    for (Map map : data) {
      createUserAndAssignRoles(passwordUsers, map.get("Email").toString(), map.get("FirstName").toString(), map.get("LastName").toString(), map.get("UserName").toString(), map.get("FacilityCode").toString(), map.get("Program").toString(), map.get("Node").toString(), map.get("Role").toString(), map.get("RoleType").toString(), map.get("Warehouse").toString(), map.get("WarehouseRole").toString());
    }
  }

  @And("^I update user$")
  public void updateUser() {
    UserPage userPage = PageObjectFactory.getUserPage(testWebDriver);
    userPage.clickSaveButton();
  }

  @And("^I assign warehouse \"([^\"]*)\" and role \"([^\"]*)\" to user$")
  public void assignWarehouse(String warehouse, String warehouseRole) {
    UserPage userPage = PageObjectFactory.getUserPage(testWebDriver);
    userPage.assignWarehouse(warehouse, warehouseRole);
  }

  @And("^I setup product & requisition group data$")
  public void productAndRequisitionGroupDataSetup() throws SQLException {
    dbWrapper.updateRoleGroupMember(facility_code);
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
    dbWrapper.insertRequisitionGroupMembers("F10", facility_code);
  }

  @And("^I setup period, schedule & requisition group data$")
  public void periodScheduleAndRequisitionGroupDataSetup() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
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
  public void insertPeriodAndAssociateItWithSchedule(String period, String schedule) throws SQLException {
    dbWrapper.insertProcessingPeriod(period, period, "2013-09-29", "2020-09-30", 66, schedule);
  }

  @And("^I update \"([^\"]*)\" home facility$")
  public void updateHomeFacility(String user) throws SQLException {
    dbWrapper.allocateFacilityToUser(user, facility_code);
  }

  @And("^I configure \"([^\"]*)\" template$")
  public void configureTemplate(String program) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.configureTemplate();
  }

  @And("^I initiate and submit requisition$")
  public void initiateRnR() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    initiateRnRPage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
    initiateRnRPage.submitRnR();
    initiateRnRPage.verifySubmitRnrErrorMsg();
  }

  @And("^I enter beginning balance as \"([^\"]*)\", quantityDispensed as \"([^\"]*)\", quantityReceived as \"([^\"]*)\" and totalAdjustmentAndLoses as \"([^\"]*)\"$")
  public void enterValuesInRnR(String beginningBalance, String quantityDispensed, String quantityReceived, String totalAdjustmentAndLoses) {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.calculateAndVerifyStockOnHand(parseInt(beginningBalance), parseInt(quantityDispensed),
      parseInt(quantityReceived), parseInt(totalAdjustmentAndLoses));
    initiateRnRPage.verifyTotalField();
  }

  @And("^I verify normalized consumption as \"([^\"]*)\" and amc as \"([^\"]*)\"$")
  public void verifyNormalisedConsumptionAndAmc(String normalisedConsumption, String amc) {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyNormalizedConsumptionForFirstProduct(parseInt(normalisedConsumption));
    initiateRnRPage.verifyAmcForFirstProduct(parseInt(amc));
  }

  @And("^I submit RnR$")
  public void submitRnR() {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.submitRnR();
    initiateRnRPage.clickOk();
  }

  @And("^I initiate and submit emergency requisition$")
  public void initiateEmergencyRnR() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
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
  public void accessProceed() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.clickProceed();
  }

  @And("^I add comments$")
  public void addComments() {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
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
  public void enterAndVerifyOrderedQuantities() throws SQLException {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(10, "newPatientFirstProduct");
    initiateRnRPage.enterValueIfNotNull(10, "totalStockOutDaysFirstProduct");
    int expectedCalculatedNC = calculatedExpectedNC(10, 10, 10);
    initiateRnRPage.verifyAmcAndCalculatedOrderQuantity(expectedCalculatedNC, 36, 3, 11);
    Integer packSize = parseInt(dbWrapper.getAttributeFromTable("products", "packSize", "code", "P10"));
    initiateRnRPage.verifyPacksToShip(packSize);
  }

  @And("^I update & verify quantities for emergency RnR$")
  public void enterAndVerifyOrderedQuantitiesForEmergencyRnR() throws SQLException {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(10, "newPatientFirstProduct");
    initiateRnRPage.enterValueIfNotNull(10, "totalStockOutDaysFirstProduct");
    int expectedCalculatedNC = calculatedExpectedNC(10, 10, 10);
    initiateRnRPage.verifyAmcAndCalculatedOrderQuantity(expectedCalculatedNC, round(((float) (expectedCalculatedNC + 36) / 2)), 3, 11);
    Integer packSize = Integer.parseInt(dbWrapper.getAttributeFromTable("products", "packSize", "code", "P10"));
    initiateRnRPage.verifyPacksToShip(packSize);
  }

  @And("^I update & verify requested quantities$")
  public void enterAndVerifyRequestedQuantities() throws SQLException {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.enterValueIfNotNull(10, "requestedQuantityFirstProduct");
    initiateRnRPage.verifyRequestedQuantityExplanation();
    initiateRnRPage.enterExplanationReason();
    Integer packSize = Integer.parseInt(dbWrapper.getAttributeFromTable("products", "packSize", "code", "P10"));
    initiateRnRPage.verifyPacksToShip(packSize);
    initiateRnRPage.calculateAndVerifyTotalCost();
    initiateRnRPage.saveRnR();
  }

  @And("^I add non full supply items & verify total cost$")
  public void enterNonFullSupplyAndVerifyTotalCost() throws SQLException {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.addNonFullSupplyLineItems("99", "Due to unforeseen event", "antibiotic", "P11", "Antibiotics");
    initiateRnRPage.calculateAndVerifyTotalCostNonFullSupply();
    initiateRnRPage.verifyCostOnFooterForProducts(1);
  }

  @And("^I authorize RnR$")
  public void authorizeRnR() {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.authorizeRnR();
    initiateRnRPage.clickOk();
    initiateRnRPage.clickFullSupplyTab();
  }

  @And("^I verify normalized consumption as \"([^\"]*)\" and amc as \"([^\"]*)\" for product \"([^\"]*)\" in Database$")
  public void verifyNormalisedConsumptionAndAmcInDatabase(String normalizedConsumption, String amc, String productCode) throws SQLException {
    Long rnrId = (long) dbWrapper.getMaxRnrID();
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId, "normalizedConsumption", productCode), normalizedConsumption);
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(rnrId, "amc", productCode), amc);
  }

  @Then("^I verify cost & authorize message$")
  public void verifyAuthorizeRnR() {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    initiateRnRPage.verifyTotalField();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    initiateRnRPage.verifyApproveButtonNotPresent();
  }

  @Then("^I should not see requisition to approve$")
  public void verifyNoRequisitionToApprove() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    ApprovePage approvePage = homePage.navigateToApprove();
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @When("^I access requisition on approval page$")
  public void navigateRequisitionApprovalPage() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    ApprovePage approvePage = homePage.navigateToApprove();
    periodTopSNUser = approvePage.clickRequisitionPresentForApproval();
  }

  @Then("I access non full supply tab$")
  public void accessNonFullSupplyTab() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.accessNonFullSupplyTab();
  }

  @Then("I should see RnR Header$")
  public void verifyRnRHeader() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
  }

  @Then("I should see full supply approved quantity$")
  public void verifyFullSupplyApprovedQuantity() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyFullSupplyApprovedQuantity();
  }

  @Then("I should see non full supply approved quantity$")
  public void verifyNonFullSupplyApprovedQuantity() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyNonFullSupplyApprovedQuantity();
  }

  @Then("I should see approved quantity from lower hierarchy$")
  public void verifyApprovedQuantityFromLastHierarchy() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
  }

  @When("I update full supply approve quantity as \"([^\"]*)\"$")
  public void updateFullSupplyApproveQuantity(String approvedQuantity) {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.editFullSupplyApproveQuantity(approvedQuantity);
  }

  @Then("I verify full supply cost for approved quantity \"([^\"]*)\"$")
  public void verifyFullSupplyCost(String approvedQuantity) {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyFullSupplyCost(approvedQuantity);
  }

  @When("I update non full supply approve quantity as \"([^\"]*)\"$")
  public void updateNonFullSupplyApproveQuantity(String approvedQuantity) {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.editNonFullSupplyApproveQuantity(approvedQuantity);
  }

  @Then("I verify non full supply cost for approved quantity \"([^\"]*)\"$")
  public void verifyNonFullSupplyCost(String approvedQuantity) {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyNonFullSupplyCost(approvedQuantity);
  }

  @And("I add comments without save$")
  public void addCommentWithoutSave() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.clickCommentsButton();
    approvePage.typeCommentsInCommentsTextArea("Test comment.");
    approvePage.closeCommentPopUp();
    approvePage.clickCommentsButton();
  }

  @Then("I should see blank comment section$")
  public void verifyBlankCommentTextArea() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyValueInCommentsTextArea("");
    approvePage.closeCommentPopUp();
  }

  @When("I add \"([^\"]*)\" comment$")
  public void addSpecificComment(String comment) {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.addComments(comment);
  }

  @Then("I should see \"([^\"]*)\" comments as \"([^\"]*)\"$")
  public void verifyCommentForUser(String user, String comment) {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyComment(comment, user, 2);
  }

  @And("I should see correct total after authorize$")
  public void verifyTotalAfterAuthorization() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.clickFullSupplyTab();
    approvePage.verifyTotalFieldPostAuthorize();
  }

  @When("I approve requisition$")
  public void approveRequisition() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.clickSaveButton();
    approvePage.clickApproveButton();
    approvePage.clickOk();
  }

  @Then("I should see no requisition pending message$")
  public void verifyNoRequisitionPendingMessage() {
    ApprovePage approvePage = PageObjectFactory.getApprovePage(testWebDriver);
    approvePage.verifyNoRequisitionPendingMessage();
  }

  @When("^I access convert to order page$")
  public void navigateConvertToOrderPage() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateConvertToOrder();
  }

  @Then("^I should see pending order list$")
  public void verifyPendingOrderList() throws SQLException {
    ConvertOrderPage convertOrderPage = PageObjectFactory.getConvertOrderPage(testWebDriver);
    String[] periods = periodTopSNUser.split("-");
    String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", program);
    convertOrderPage.verifyOrderListElements(program, facility_code, facility_name, periods[0].trim(), periods[1].trim(), supplyFacilityName);
  }

  @When("^I convert to order$")
  public void convertToOrderAndVerify() {
    verifyConvertToOrder();
  }

  @When("^I access view orders page$")
  public void navigateViewOrdersPage() {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.navigateViewOrders();
  }

  @And("^I verify order status as \"([^\"]*)\" in row \"([^\"]*)\"$")
  public void verifyOrderStatus(String orderStatus, String rowNumber) {
    ViewOrdersPage viewOrdersPage = PageObjectFactory.getViewOrdersPage(testWebDriver);
    testWebDriver.sleep(500);
    assertEquals(orderStatus, viewOrdersPage.getOrderStatus(Integer.parseInt(rowNumber)));
  }

  @Then("^I should see ordered list with download link$")
  public void verifyOrderListWithDownloadLink() throws SQLException {
    verifyOrderedList(true);
  }

  @And("^I change order status to \"([^\"]*)\"$")
  public void updateOrderStatus(String orderStatus) throws SQLException {
    dbWrapper.updateOrderStatus(orderStatus);
  }

  @When("^I do not have anything to pack to ship$")
  public void updatePacksToShip() throws SQLException {
    dbWrapper.updatePacksToShip("0");
  }

  @Then("^I should see ordered list without download link$")
  public void verifyOrderListWithoutDownloadLink() throws SQLException {
    verifyOrderedList(false);
  }

  @Then("^I verify Regular RnR Type$")
  public void verifyRegularRnRText() {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    assertEquals(initiateRnRPage.getRegularLabelText(), "Regular");
  }

  @Then("^I verify Emergency RnR Type$")
  public void verifyEmergencyRnRText() {
    InitiateRnRPage initiateRnRPage = PageObjectFactory.getInitiateRnRPage(testWebDriver);
    assertEquals(initiateRnRPage.getEmergencyLabelText(), "Emergency");
  }

  private void createUserAndAssignRoles(String passwordUsers, String userEmail, String userFirstName, String userLastName,
                                        String userUserName, String facility, String program, String supervisoryNode, String role,
                                        String roleType, String warehouse, String warehouseRole) throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    userPage.enterUserDetails(userUserName, userEmail, userFirstName, userLastName);
    userPage.clickViewHere();
    dbWrapper.updateUser(passwordUsers, userEmail);
    userPage.enterMyFacilityAndMySupervisedFacilityData(facility, program, supervisoryNode, role, roleType);
    userPage.assignWarehouse(warehouse, warehouseRole);
    userPage.clickSaveButton();
    userPage.verifyUserUpdated(userFirstName, userLastName);
  }

  private void verifyConvertToOrder() {
    ConvertOrderPage convertOrderPageOrdersPending = PageObjectFactory.getConvertOrderPage(testWebDriver);
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.verifyMessageOnOrderScreen("Message 'Please select at least one Requisition for Converting to Order.' is not displayed");
    convertOrderPageOrdersPending.clickCheckBoxConvertToOrder();
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.clickOk();
  }

  private void createRoleAndAssignRights(List<String> userRoleList, String roleName, String roleDescription, String roleType) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.createRole(roleName, roleDescription, userRoleList, roleType);
    assertEquals(rolesPage.getSuccessMessage(), "\"" + roleName + "\" created successfully");
  }

  private void verifyOrderedList(boolean downloadFlag) throws SQLException {
    ViewOrdersPage viewOrdersPage = PageObjectFactory.getViewOrdersPage(testWebDriver);
    String[] periods = periodTopSNUser.split("-");
    String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", program);
    NumberFormat numberFormat = NumberFormat.getIntegerInstance();
    numberFormat.setMinimumIntegerDigits(8);
    numberFormat.setGroupingUsed(false);
    int id = dbWrapper.getMaxRnrID();
    String orderNumber = "O" + program.substring(0, Math.min(program.length(), 35)) + numberFormat.format(id);
    viewOrdersPage.verifyOrderListElements(program, orderNumber, facility_code + " - " + facility_name, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "Transfer failed", downloadFlag);
  }

  public int calculatedExpectedNC(Integer numberOfNewPatients, Integer stockOutDays, Integer quantityDispensed) throws SQLException {
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
}

