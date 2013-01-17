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
    public void testE2EInitiateRnR(String period,String program,String user, String password,String[] credentials) throws Exception {

        DBWrapper dbWrapper = new DBWrapper();

        dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==","F10", "Jane_Doe@openlmis.com");
        dbWrapper.insertFacility();
        dbWrapper.insertSupervisoryNodes("F10");
        dbWrapper.insertSupervisoryNodesSecond("F11");
        dbWrapper.insertProducts();
        dbWrapper.insertProgramProducts();
        dbWrapper.insertFacilityApprovedProducts();
        dbWrapper.insertRequisitionGroup();

        LoginPage loginPage=new LoginPage(testWebDriver);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
        String date_time=createFacilityPage.enterAndVerifyFacility();

        String facility_name="FCcode" + date_time;
        dbWrapper.insertRequisitionGroupMembers("F10", facility_name);

        ManageSchedulePage manageSchedulePage=homePage.navigateToSchedule();
        manageSchedulePage.createAndVerifySchedule();
        PeriodsPage periodsPage=manageSchedulePage.navigatePeriods();
        periodsPage.createAndVerifyPeriods();
        periodsPage.deleteAndVerifyPeriods();

        dbWrapper.insertRequisitionGroupProgramSchedule();

        dbWrapper.allocateFacilityToUser("200");

        TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
        templateConfigPage.configureTemplate();



        RolesPage rolesPage = homePage.navigateRoleAssignments();
        List<String> userRoleList = new ArrayList<>();
        userRoleList.add("Create Requisition");

        rolesPage.createRole("User", "User", userRoleList);

        dbWrapper.insertRoleAssignment("200", "User");

        LoginPage loginPageSecond=homePage.logout();
        HomePage homePageUser = loginPageSecond.loginAs(user, password);

        InitiateRnRPage initiateRnRPage = homePageUser.navigateAndInitiateRnr("FCcode", "FCname", date_time, program, period);
        initiateRnRPage.verifyRnRHeader("FCcode", "FCname", date_time, program);

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
        initiateRnRPage.verifyBeginningBalanceDisabled();

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
                {"Period2","HIV","User123", "User123",new String[]{"Admin123", "Admin123"}}
        };

    }
}
