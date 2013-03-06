package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.Unzip;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EInitiateRnR extends TestCaseHelper {

  DBWrapper dbWrapper;

  @BeforeMethod(groups = {"smoke"})
  @Parameters({"browser"})
  public void setUp(String browser) throws Exception {
    super.setupSuite(browser);
    dbWrapper = new DBWrapper();
    dbWrapper.deleteData();
  }

  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EInitiateRnR(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String geoZone = "Arusha";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String date_time = createFacilityPage.enterAndVerifyFacility(geoZone, facilityType, operatedBy);
    String facility_code = "FCcode" + date_time;
    dbWrapper.insertFacilities("F10", "F11");

    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleListStoreincharge = new ArrayList<String>();
    userRoleListStoreincharge.add("Create Requisition");
    userRoleListStoreincharge.add("Authorize Requisition");
    userRoleListStoreincharge.add("Approve Requisition");
    userRoleListStoreincharge.add("Convert To Order Requisition");
    rolesPage.createRole("Store-in-charge", "Store-in-charge", userRoleListStoreincharge);

    List<String> userRoleListMedicalofficer = new ArrayList<String>();
    userRoleListMedicalofficer.add("Approve Requisition");
    rolesPage.createRole("Medical-officer", "Medical-officer", userRoleListMedicalofficer);

    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "Node 2", "N1");

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPageSIC = homePage.navigateToUser();
    String userSICEmail = "Fatima_Doe@openlmis.com";
    String userSICFirstName = "Fatima";
    String userSICLastName = "Doe";
    String userSICUserName = "storeincharge";
    String userIDSIC = userPageSIC.enterAndverifyUserDetails(userSICUserName, userSICEmail, userSICFirstName, userSICLastName);
    dbWrapper.updateUser(passwordUsers, userSICEmail);
    userPageSIC.enterMyFacilityAndMySupervisedFacilityData(userSICFirstName, userSICLastName, "F10", "HIV", "Node 1", "Store-in-charge");

    UserPage userPageMO = homePage.navigateToUser();
    String userMOEmail = "Jane_Doe@openlmis.com";
    String userMOFirstName = "Jane";
    String userMOLastName = "Doe";
    String userMOUserName = "medicalofficer";
    String userIDMO = userPageMO.enterAndverifyUserDetails(userMOUserName, userMOEmail, userMOFirstName, userMOLastName);
    dbWrapper.updateUser(passwordUsers, userMOEmail);
    userPageMO.enterMyFacilityAndMySupervisedFacilityData(userMOFirstName, userMOLastName, "F11", "HIV", "Node 2", "Medical-Officer");

    dbWrapper.updateRoleGroupMember(facility_code);
    dbWrapper.insertProducts("P10", "P11");
    dbWrapper.insertProgramProducts("P10", "P11", program);
    dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");

    dbWrapper.insertRequisitionGroupMembers("F10", facility_code);

    ManageSchedulePage manageSchedulePage = homePage.navigateToSchedule();
    manageSchedulePage.createAndVerifySchedule();
    manageSchedulePage.editAndVerifySchedule();
    PeriodsPage periodsPage = manageSchedulePage.navigatePeriods();
    periodsPage.createAndVerifyPeriods();
    periodsPage.deleteAndVerifyPeriods();

    dbWrapper.insertRequisitionGroupProgramSchedule();

    dbWrapper.allocateFacilityToUser(userIDSIC, facility_code);

    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
    templateConfigPage.configureTemplate();


    dbWrapper.insertSupplyLines("N1", "HIV", "FCcode" + date_time);

    LoginPage loginPageSecond = homePage.logout();
    HomePage homePageUser = loginPageSecond.loginAs(userSIC, password);

    String periodDetails = homePageUser.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePageUser.clickProceed();
    initiateRnRPage.verifyRnRHeader("FCcode", "FCname", date_time, program, periodDetails, geoZone, operatedBy, facilityType);
    testWebDriver.sleep(2000);
    initiateRnRPage.submitRnR();
    testWebDriver.sleep(1000);
    initiateRnRPage.verifySubmitRnrErrorMsg();
    initiateRnRPage.calculateAndVerifyStockOnHand(10, 10, 10, 1);

    initiateRnRPage.submitRnR();

    initiateRnRPage.enterValuesAndVerifyCalculatedOrderQuantity(10, 10, 101, 51, 153, 142);
    initiateRnRPage.verifyPacksToShip(15);

    initiateRnRPage.enterAndVerifyRequestedQuantityExplanation(10);
    initiateRnRPage.verifyPacksToShip(1);
    initiateRnRPage.calculateAndVerifyTotalCost();
    //initiateRnRPage.saveRnR();

    initiateRnRPage.addNonFullSupplyLineItems("99", "Due to unforeseen event", "antibiotic", "P11");
    initiateRnRPage.calculateAndVerifyTotalCostNonFullSupply();
    initiateRnRPage.verifyCostOnFooter();

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    initiateRnRPage.verifyBeginningBalanceDisabled();

    ApprovePage approvePage = homePageUser.navigateToApprove();
    approvePage.verifyNoRequisitionPendingMessage();
    LoginPage loginPagethird = homePageUser.logout();

    HomePage homePageLowerSNUser = loginPagethird.loginAs(userMO, password);
    ApprovePage approvePageLowerSNUser = homePageLowerSNUser.navigateToApprove();
    String periodLowerSNUser = approvePageLowerSNUser.verifyandclickRequisitionPresentForApproval();
    approvePageLowerSNUser.verifyRnRHeader("FCcode", "FCname", date_time, program, periodLowerSNUser);
    approvePageLowerSNUser.verifyApprovedQuantity();
    approvePageLowerSNUser.editApproveQuantityAndVerifyTotalCost("290");
    approvePageLowerSNUser.approveRequisition();
    approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
    LoginPage loginPageTopSNUser = homePageLowerSNUser.logout();

    HomePage homePageTopSNUser = loginPageTopSNUser.loginAs(userSIC, password);

    ApprovePage approvePageTopSNUser = homePageTopSNUser.navigateToApprove();
    String periodTopSNUser = approvePageTopSNUser.verifyandclickRequisitionPresentForApproval();
    approvePageTopSNUser.verifyRnRHeader("FCcode", "FCname", date_time, program, periodTopSNUser);
    approvePageTopSNUser.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
    approvePageTopSNUser.editApproveQuantityAndVerifyTotalCost("2900");
    approvePageTopSNUser.approveRequisition();
    approvePageTopSNUser.verifyNoRequisitionPendingMessage();

    OrderPage orderPageOrdersPending = homePageTopSNUser.navigateConvertToOrder();
    String[] periods = periodTopSNUser.split("-");
    String supplyFacilityName = dbWrapper.getSupplyFacilityName("N1", "HIV");
    orderPageOrdersPending.verifyOrderListElements(program, "FCcode" + date_time, "FCname" + date_time, periods[0].trim(), periods[1].trim(), supplyFacilityName);
    orderPageOrdersPending.convertToOrder();


  }

  @AfterMethod(groups = {"smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "medicalofficer", "Admin123", new String[]{"Admin123", "Admin123"}}
    };

  }
}

