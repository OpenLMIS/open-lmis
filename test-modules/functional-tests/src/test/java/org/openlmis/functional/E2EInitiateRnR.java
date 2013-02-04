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
    public void testE2EInitiateRnR(String period,String program,String userSIC, String userMO, String password,String[] credentials) throws Exception {

        LoginPage loginPage=new LoginPage(testWebDriver);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
        String date_time=createFacilityPage.enterAndVerifyFacility();

        DBWrapper dbWrapper = new DBWrapper();

        UserPage userPageSIC=homePage.navigateToUser();
        userPageSIC.enterAndverifyUserDetails("User123","manjyots@thoughtworks.com","Manjyot", "Singh");

        UserPage userPageMO=homePage.navigateToUser();
        userPageMO.enterAndverifyUserDetails("User234","lokeshag@thoughtworks.com","Lokesh", "Agarwal");

        dbWrapper.insertFacility();

        dbWrapper.updateUser("200","F10","manjyots@thoughtworks.com");
        dbWrapper.updateUser("300","F10","lokeshag@thoughtworks.com");

        dbWrapper.insertSupervisoryNodes("F10");
        dbWrapper.insertSupervisoryNodesSecond("F11");
        dbWrapper.insertProducts();
        dbWrapper.insertProgramProducts();
        dbWrapper.insertFacilityApprovedProducts();
        dbWrapper.insertRequisitionGroup();

        String facility_name="FCcode" + date_time;
        dbWrapper.insertRequisitionGroupMembers("F10", facility_name);

        ManageSchedulePage manageSchedulePage=homePage.navigateToSchedule();
        manageSchedulePage.createAndVerifySchedule();
        manageSchedulePage.editAndVerifySchedule();
        PeriodsPage periodsPage=manageSchedulePage.navigatePeriods();
        periodsPage.createAndVerifyPeriods();
        periodsPage.deleteAndVerifyPeriods();

        dbWrapper.insertRequisitionGroupProgramSchedule();

        dbWrapper.allocateFacilityToUser("200");

        TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
        templateConfigPage.configureTemplate();

        RolesPage rolesPage = homePage.navigateRoleAssignments();
        List<String> userRoleListStoreincharge = new ArrayList<String>();
        userRoleListStoreincharge.add("Create Requisition");
        userRoleListStoreincharge.add("Authorize Requisition");
        userRoleListStoreincharge.add("Approve Requisition");

        rolesPage.createRole("Store-in-charge", "Store-in-charge", userRoleListStoreincharge);
        dbWrapper.insertRoleAssignment("200", "Store-in-charge");

        List<String> userRoleListMedicalofficer = new ArrayList<String>();
        userRoleListMedicalofficer.add("Approve Requisition");
        rolesPage.createRole("Medical-officer", "Medical-officer", userRoleListMedicalofficer);
        dbWrapper.insertRoleAssignment("300", "Medical-officer");
        dbWrapper.updateRoleAssignment("300");
        dbWrapper.updateRoleGroupMember("FCcode"+date_time);

        LoginPage loginPageSecond=homePage.logout();
        HomePage homePageUser = loginPageSecond.loginAs(userSIC, password);

        String periodDetails=homePageUser.navigateAndInitiateRnr(program);
        InitiateRnRPage initiateRnRPage =  homePageUser.clickProceed();
        initiateRnRPage.verifyRnRHeader("FCcode", "FCname", date_time, program, periodDetails);

        initiateRnRPage.calculateAndVerifyStockOnHand(10,10,10,1);

        initiateRnRPage.submitRnR();
        initiateRnRPage.verifySubmitRnrErrorMsg();

        initiateRnRPage.enterValuesAndVerifyCalculatedOrderQuantity(10,10,101,101,303,292);
        initiateRnRPage.verifyPacksToShip(30);

        initiateRnRPage.enterAndVerifyRequestedQuantityExplanation(10);
        initiateRnRPage.verifyPacksToShip(1);
        initiateRnRPage.calculateAndVerifyTotalCost();
        initiateRnRPage.saveRnR();

        initiateRnRPage.submitRnR();
        initiateRnRPage.verifySubmitRnrSuccessMsg();

        initiateRnRPage.authorizeRnR();
        initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
        initiateRnRPage.verifyBeginningBalanceDisabled();

        ApprovePage approvePage=homePage.navigateToApprove();
        approvePage.verifyNoRequisitionPendingMessage();
        LoginPage loginPagethird=homePage.logout();

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
        ApprovePage approvePageTopSNUser=homePageTopSNUser.navigateToApprove();
        String periodTopSNUser=approvePageTopSNUser.verifyandclickRequisitionPresentForApproval();
        approvePageTopSNUser.verifyRnRHeader("FCcode", "FCname", date_time, program, periodTopSNUser);
        approvePageTopSNUser.verifyApprovedQuantityApprovedFromLowerHierarchy("290");
        approvePageTopSNUser.editApproveQuantityAndVerifyTotalCost("2900");
        approvePageTopSNUser.approveRequisition();
        approvePageTopSNUser.verifyNoRequisitionPendingMessage();

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
                {"Period1","HIV","User123", "User234", "openLmis123",new String[]{"Admin123", "Admin123"}}
        };

    }
}

