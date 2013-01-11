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
    public void testE2EInitiateRnR(String program,String user, String password,String[] credentials) throws Exception {

        DBWrapper dbWrapper = new DBWrapper();

        dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==");
        dbWrapper.insertFacility();
        dbWrapper.insertSupervisoryNodes();
        dbWrapper.insertSupervisoryNodesSecond();
        dbWrapper.insertProducts();
        dbWrapper.insertProgramProducts();
        dbWrapper.insertFacilityApprovedProducts();
        dbWrapper.insertRequisitionGroup();
        dbWrapper.insertRequisitionGroupMembers();
        dbWrapper.insertSchedules();
        dbWrapper.insertProcessingPeriods();



        LoginPage loginPage=new LoginPage(testWebDriver);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
        String date_time=createFacilityPage.enterAndVerifyFacility();

        dbWrapper.allocateFacilityToUser();

        TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
        templateConfigPage.configureTemplate();

        RolesPage rolesPage = homePage.navigateRoleAssignments();
        List<String> userRoleList = new ArrayList<>();
        userRoleList.add("Create Requisition");

        rolesPage.createRole("User", "User", userRoleList);

        dbWrapper.insertRoleAssignment("User");

        LoginPage loginPageSecond=homePage.logout();
        HomePage homePageUser = loginPageSecond.loginAs(user, password);

        InitiateRnRPage initiateRnRPage = homePageUser.navigateAndInitiateRnr("FCcode", "FCname", date_time, program);
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
        homePageUser.logout();

    }
    @AfterClass
    public void tearDown() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();

    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"HIV","User123", "User123",new String[]{"Admin123", "Admin123"}}
        };

    }
}
