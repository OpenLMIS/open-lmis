package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback=true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EInitiateRnR extends TestCaseHelper {

    @BeforeClass
    public void setUp() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();
    }

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testE2EInitiateRnR(String program,String userSIC, String userMO, String password,String[] credentials) throws Exception {

        LoginPage loginPage=new LoginPage(testWebDriver);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
        String date_time=createFacilityPage.enterAndVerifyFacility();
        String facility_code="FCcode" + date_time;

        DBWrapper dbWrapper = new DBWrapper();

        UserPage userPageSIC=homePage.navigateToUser();
        userPageSIC.enterAndverifyUserDetails("User123", "manjyots@thoughtworks.com", "Manjyot", "Singh");

        UserPage userPageMO=homePage.navigateToUser();
        userPageMO.enterAndverifyUserDetails("User234", "lokeshag@thoughtworks.com", "Lokesh", "Agarwal");

        dbWrapper.insertFacilities("F10", "F11");

        String passwordUsers="TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
        dbWrapper.updateUser("200",passwordUsers,"F10","manjyots@thoughtworks.com");
        dbWrapper.updateUser("300",passwordUsers,"F11","lokeshag@thoughtworks.com");

        dbWrapper.insertSupervisoryNode("F10", "N1", "null");
        dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "N1");
        dbWrapper.insertProducts("P10", "P11");
        dbWrapper.insertProgramProducts("P10", "P11", program);
        dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
        dbWrapper.insertRequisitionGroups("RG1","RG2","N1","N2");

        dbWrapper.insertRequisitionGroupMembers("F10", facility_code);

        ManageSchedulePage manageSchedulePage=homePage.navigateToSchedule();
        manageSchedulePage.createAndVerifySchedule();
        manageSchedulePage.editAndVerifySchedule();
        PeriodsPage periodsPage=manageSchedulePage.navigatePeriods();
        periodsPage.createAndVerifyPeriods();
        periodsPage.deleteAndVerifyPeriods();

        dbWrapper.insertRequisitionGroupProgramSchedule();

        dbWrapper.allocateFacilityToUser("200", facility_code);

        TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
        templateConfigPage.configureTemplate();

        RolesPage rolesPage = homePage.navigateRoleAssignments();
        List<String> userRoleListStoreincharge = new ArrayList<String>();
        userRoleListStoreincharge.add("Create Requisition");
        userRoleListStoreincharge.add("Authorize Requisition");
        userRoleListStoreincharge.add("Approve Requisition");
        userRoleListStoreincharge.add("Convert To Order Requisition");

        rolesPage.createRole("Store-in-charge", "Store-in-charge", userRoleListStoreincharge);
        dbWrapper.insertRoleAssignment("200", "Store-in-charge");

        List<String> userRoleListMedicalofficer = new ArrayList<String>();
        userRoleListMedicalofficer.add("Approve Requisition");
        rolesPage.createRole("Medical-officer", "Medical-officer", userRoleListMedicalofficer);
        dbWrapper.insertRoleAssignment("300", "Medical-officer");
        dbWrapper.updateRoleAssignment("300");
        dbWrapper.updateRoleGroupMember(facility_code);

        dbWrapper.insertSupplyLines("N1","HIV","FCcode"+date_time);

        LoginPage loginPageSecond=homePage.logout();
        HomePage homePageUser = loginPageSecond.loginAs(userSIC, password);

        String periodDetails=homePageUser.navigateAndInitiateRnr(program);
        InitiateRnRPage initiateRnRPage =  homePageUser.clickProceed();
        initiateRnRPage.verifyRnRHeader("FCcode", "FCname", date_time, program, periodDetails);

        initiateRnRPage.calculateAndVerifyStockOnHand(10,10,10,1);

        initiateRnRPage.submitRnR();
        initiateRnRPage.verifySubmitRnrErrorMsg();

        initiateRnRPage.enterValuesAndVerifyCalculatedOrderQuantity(10,10,101,51,153,142);
        initiateRnRPage.verifyPacksToShip(15);

        initiateRnRPage.enterAndVerifyRequestedQuantityExplanation(10);
        initiateRnRPage.verifyPacksToShip(1);
        initiateRnRPage.calculateAndVerifyTotalCost();
        initiateRnRPage.saveRnR();

        initiateRnRPage.addNonFullSupplyLineItems("99","Due to unforeseen event","antibiotic","P11");
        initiateRnRPage.calculateAndVerifyTotalCostNonFullSupply();
        initiateRnRPage.verifyCostOnFooter();
        initiateRnRPage.submitRnR();
        initiateRnRPage.verifySubmitRnrSuccessMsg();

        initiateRnRPage.authorizeRnR();
        initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
        initiateRnRPage.verifyBeginningBalanceDisabled();

        ApprovePage approvePage=homePageUser.navigateToApprove();
        approvePage.verifyNoRequisitionPendingMessage();
        LoginPage loginPagethird=homePageUser.logout();

        HomePage homePageLowerSNUser=loginPagethird.loginAs(userMO,password);
        ApprovePage approvePageLowerSNUser=homePageLowerSNUser.navigateToApprove();
        String periodLowerSNUser=approvePageLowerSNUser.verifyandclickRequisitionPresentForApproval();
        approvePageLowerSNUser.verifyRnRHeader("FCcode", "FCname", date_time, program, periodLowerSNUser);
        approvePageLowerSNUser.verifyApprovedQuantity();
        approvePageLowerSNUser.editApproveQuantityAndVerifyTotalCost("290");
        approvePageLowerSNUser.approveRequisition();
        approvePageLowerSNUser.verifyNoRequisitionPendingMessage();
        LoginPage loginPageTopSNUser=homePageLowerSNUser.logout();

        HomePage homePageTopSNUser=loginPageTopSNUser.loginAs(userSIC, password);

        OrderPage orderPageNoOrdersPending=homePageTopSNUser.navigateConvertToOrder();
        orderPageNoOrdersPending.verifyNoPendingOrdersMessage();

        ApprovePage approvePageTopSNUser=homePageTopSNUser.navigateToApprove();
        String periodTopSNUser=approvePageTopSNUser.verifyandclickRequisitionPresentForApproval();
        approvePageTopSNUser.verifyRnRHeader("FCcode", "FCname", date_time, program, periodTopSNUser);
        approvePageTopSNUser.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
        approvePageTopSNUser.editApproveQuantityAndVerifyTotalCost("2900");
        approvePageTopSNUser.approveRequisition();
        approvePageTopSNUser.verifyNoRequisitionPendingMessage();

        OrderPage orderPageOrdersPending=homePageTopSNUser.navigateConvertToOrder();
        String[] periods=periodTopSNUser.split("-");
        String supplyFacilityName=dbWrapper.getSupplyFacilityName("N1", "HIV");
        orderPageOrdersPending.verifyOrderListElements(program, "FCcode"+date_time, "FCname"+date_time, periods[0].trim(), periods[1].trim(), supplyFacilityName );



    }
    @AfterClass
    public void tearDown() throws Exception
    {
        HomePage homePage = new HomePage(testWebDriver);
        homePage.logout();
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();
    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"HIV","User123", "User234", "Admin123",new String[]{"Admin123", "Admin123"}}
        };

    }
}

