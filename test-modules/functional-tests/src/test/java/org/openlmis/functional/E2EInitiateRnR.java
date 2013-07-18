/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EInitiateRnR extends TestCaseHelper {
    public String facility_code ;
    public String facility_name;
    public String date_time;
    public String geoZone = "Ngorongoro";
    public String parentGeoZone = "Dodoma";
    public String facilityType = "Lvl3 Hospital";
    public String operatedBy = "MoH";
    public String facilityCodePrefix = "FCcode";
    public String facilityNamePrefix = "FCname";
    public String catchmentPopulation = "500000";
    public String userIDSIC;
    public String periodDetails;
    public String periodTopSNUser;
    public String program="HIV";

    public String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    public String userSICUserName = "storeincharge";


    @BeforeMethod(groups = {"smoke"})
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @DataProvider(name = "envData")
  public Object[][] getEnvData() {
    return new Object[][]{};
  }

    @Given("^I am logged in as Admin$")
    public void adminLogin() throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginPage.loginAs("Admin123", "Admin123");
    }

    @And("^I access create facility page$")
    public void nevigateCreateFacilityPage() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        homePage.navigateCreateFacility();
    }

    @When("^I create facility supporting \"([^\"]*)\"$")
    public void createFacility(String program) throws Exception {
        CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);

        date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
                geoZone, facilityType, operatedBy, catchmentPopulation);
        facility_code = facilityCodePrefix + date_time;
        facility_name = facilityNamePrefix + date_time;
    }

    @Then("^I should see message for successfully created facility$")
    public void verify() throws Exception {
        CreateFacilityPage createFacilityPage = new CreateFacilityPage(testWebDriver);
        createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
    }
    @When("^I create \"([^\"]*)\" role having \"([^\"]*)\" based \"([^\"]*)\" rights$")
    public void createRoleWithRights(String roleName, String roleType, String rightsList) throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        String [] roleRights = rightsList.split(",");
        List<String> userRoleListStoreInCharge = new ArrayList<String>();
        for (int i=0; i<roleRights.length;i++)
            userRoleListStoreInCharge.add(roleRights[i]);
        if (roleType.equals("Requisition"))
            createRoleAndAssignRights(homePage, userRoleListStoreInCharge, roleName, roleName, true);
        else if (roleType.equals("Admin"))
            createRoleAndAssignRights(homePage, userRoleListStoreInCharge, roleName, roleName, false);


    }

    @And("^I setup supervisory node data$")
    public void supervisoryNodeDataSetup() throws Exception {
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
        dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "Node 2", "N1");
    }

    @And("^I create users:$")
    public void createUser(DataTable userTable) throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        List<Map<String, String>> data=userTable.asMaps();
        for(Map map:data)
            createUserAndAssignRoles(homePage, passwordUsers, map.get("Email").toString(), map.get("Firstname").toString(), map.get("Lastname").toString(), map.get("UserName").toString(), map.get("FacilityCode").toString(), map.get("Program").toString(), map.get("Node").toString(), map.get("Role").toString(), map.get("RoleType").toString());
    }

    @And("^I setup product & requisition group data$")
    public void productAndRequisitionGroupDataSetup() throws Exception {
        dbWrapper.updateRoleGroupMember(facility_code);
        setupProductTestData("P10", "P11", program, "Lvl3 Hospital");
        dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
        dbWrapper.insertRequisitionGroupMembers("F10", facility_code);
    }

    @And("^I setup period, schedule & requisition group data$")
    public void periodScheduleAndRequisitionGroupDataSetup() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        ManageSchedulePage manageSchedulePage = homePage.navigateToSchedule();
        manageSchedulePage.createAndVerifySchedule();
        manageSchedulePage.editAndVerifySchedule();
        PeriodsPage periodsPage = manageSchedulePage.navigatePeriods();
        periodsPage.createAndVerifyPeriods();
        periodsPage.deleteAndVerifyPeriods();

        dbWrapper.insertRequisitionGroupProgramSchedule();
    }

    @And("^I update \"([^\"]*)\" home facility$")
    public void updateHomeFacility(String user) throws Exception {
        dbWrapper.allocateFacilityToUser(dbWrapper.getUserID(user), facility_code);
    }

    @And("^I configure \"([^\"]*)\" template$")
    public void configureTemplate(String program) throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
        templateConfigPage.configureTemplate();

        dbWrapper.insertSupplyLines("N1", program, facilityCodePrefix + date_time);
    }

    @And("^I logout$")
    public void logout() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        homePage.logout(baseUrlGlobal);
    }

    @And("^I am logged in as \"([^\"]*)\"$")
    public void login(String username) throws Exception {
        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        loginPage.loginAs(username, "Admin123");
    }

    @And("^I initiate RnR$")
    public void initiateRnR() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);

        periodDetails = homePage.navigateAndInitiateRnr(program);
        InitiateRnRPage initiateRnRPage = homePage.clickProceed();
        initiateRnRPage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
        initiateRnRPage.submitRnR();
        initiateRnRPage.verifySubmitRnrErrorMsg();
        initiateRnRPage.calculateAndVerifyStockOnHand(10, 10, 10, 1);
        initiateRnRPage.verifyTotalField();

        initiateRnRPage.submitRnR();
        initiateRnRPage.clickOk();
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
        initiateRnRPage.enterValuesAndVerifyCalculatedOrderQuantity(10, 10, 101, 51, 153, 142);
        initiateRnRPage.verifyPacksToShip(15);
    }

    @And("^I update & verify requested quantities$")
    public void enterAndVerifyRequestedQuantities() throws Exception {
        InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
        initiateRnRPage.enterAndVerifyRequestedQuantityExplanation(10);
        initiateRnRPage.verifyPacksToShip(1);
        initiateRnRPage.calculateAndVerifyTotalCost();
        initiateRnRPage.saveRnR();
    }

    @And("^I add non full supply items & verify total cost$")
    public void enterNonFullSupplyAndVerifyTotalCost() throws Exception {
        InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
        initiateRnRPage.addNonFullSupplyLineItems("99", "Due to unforeseen event", "antibiotic", "P11", "Antibiotics", baseUrlGlobal, dburlGlobal);
        initiateRnRPage.calculateAndVerifyTotalCostNonFullSupply();
        initiateRnRPage.verifyCostOnFooter();
    }
    @And("^I authorize RnR$")
    public void authorizeRnR() throws Exception {
        InitiateRnRPage initiateRnRPage = new InitiateRnRPage(testWebDriver);
        initiateRnRPage.authorizeRnR();
        initiateRnRPage.clickOk();
        initiateRnRPage.clickFullSupplyTab();
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
    public void nevigateRequisitionApprovalPage() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        ApprovePage approvePage = homePage.navigateToApprove();
        periodTopSNUser = approvePage.verifyAndClickRequisitionPresentForApproval();
    }
    @Then("I should see RnR Header$")
    public void verifyRnRHeader() throws Exception {
        ApprovePage approvePage = new ApprovePage(testWebDriver);
        approvePage.verifyRnRHeader(facilityCodePrefix, facilityNamePrefix, date_time, program, periodDetails, geoZone, parentGeoZone, operatedBy, facilityType);
    }

    @Then("I should see approved quantity$")
    public void verifyApprovedQuantity() throws Exception {
        ApprovePage approvePage = new ApprovePage(testWebDriver);
        approvePage.verifyApprovedQuantity();
    }

    @Then("I should see approved quantity from lower hierarchy$")
    public void verifyApprovedQuantityFromLastHierarchy() throws Exception {
        ApprovePage approvePage = new ApprovePage(testWebDriver);
        approvePage.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
    }

    @When("I update approve quantity and verify total cost as \"([^\"]*)\"$")
    public void updateApproveQuantityAndVerifyTotalCost(String cost) throws Exception {
        ApprovePage approvePage = new ApprovePage(testWebDriver);
        approvePage.editApproveQuantityAndVerifyTotalCost(cost);
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
    public void nevigateConvertToOrderPage() throws Exception {
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
    public void nevigateViewOrdersPage() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        homePage.navigateViewOrders();
    }

    @Then("^I should see ordered list$")
    public void verifyOrderedList() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        ViewOrdersPage viewOrdersPage = new ViewOrdersPage(testWebDriver);
        String requisitionId = dbWrapper.getLatestRequisitionId();
        String[] periods = periodTopSNUser.split("-");
        String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", program);
        viewOrdersPage.verifyOrderListElements(program, requisitionId, facility_code + " - " + facility_name, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "RELEASED", true);
        dbWrapper.updatePacksToShip("0");
        homePage.navigateConvertToOrder();
        homePage.navigateViewOrders();
        viewOrdersPage.verifyOrderListElements(program, requisitionId, facility_code + " - " + facility_name, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "RELEASED", false);
    }

    @When ("^I do not have anything to pack to ship$")
    public void updatePacksToShip() throws Exception {
        dbWrapper.updatePacksToShip("0");
    }

    @Then ("^I should not see download link$")
    public void verifyOrderListAndDownloadLink() throws Exception {
        HomePage homePage = new HomePage(testWebDriver);
        ViewOrdersPage viewOrdersPage = new ViewOrdersPage(testWebDriver);
        String requisitionId = dbWrapper.getLatestRequisitionId();
        String[] periods = periodTopSNUser.split("-");
        String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", program);
        homePage.navigateConvertToOrder();
        homePage.navigateViewOrders();
        viewOrdersPage.verifyOrderListElements(program, requisitionId, facility_code + " - " + facility_name, "Period1" + " (" + periods[0].trim() + " - " + periods[1].trim() + ")", supplyFacilityName, "RELEASED", false);
    }

  private String createUserAndAssignRoles(HomePage homePage, String passwordUsers, String userEmail, String userFirstName, String userLastName, String userUserName, String facility, String program, String supervisoryNode, String role, String roleType) throws IOException, SQLException {
    UserPage userPage = homePage.navigateToUser();
    String userID = userPage.enterAndVerifyUserDetails(userUserName, userEmail, userFirstName, userLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, userEmail);
    userPage.enterMyFacilityAndMySupervisedFacilityData(userFirstName, userLastName, facility, program, supervisoryNode, role, roleType);
    return userID;
  }

  private void verifyConvertToOrder(ConvertOrderPage convertOrderPageOrdersPending) {
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.verifyMessageOnOrderScreen("Message 'Please select at least one Requisition for Converting to Order.' is not displayed");
    convertOrderPageOrdersPending.clickCheckBoxConvertToOrder();
    convertOrderPageOrdersPending.clickConvertToOrderButton();
    convertOrderPageOrdersPending.clickOk();
  }

  private void createRoleAndAssignRights(HomePage homePage, List<String> userRoleList, String roleName, String roleDescription, boolean programDependent) throws IOException {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createRoleWithSuccessMessageExpected(roleName, roleDescription, userRoleList, programDependent);
  }

  @AfterMethod(groups = {"smoke"})
  @After
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

}

