package org.openlmis.functional;


import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@TransactionConfiguration(defaultRollback=true)
@Transactional

public class E2EInitiateRnR extends TestCaseHelper {

    @BeforeClass
    public void setUp() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteUser();
    }

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testE2EInitiateRnR(String program,String user, String password,String[] credentials) throws Exception {

        DBWrapper dbWrapper = new DBWrapper();

        dbWrapper.insertUser();
        dbWrapper.insertRoles();
        dbWrapper.insertRoleRights();
        dbWrapper.insertRoleAssignment();

        LoginPage loginPage=new LoginPage(testWebDriver);
        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
        String date_time=createFacilityPage.enterAndVerifyFacility();

        dbWrapper.allocateFacilityToUser();

        TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate(program);
        templateConfigPage.configureTemplate();

        LoginPage loginPageSecond=homePage.logout();
        HomePage homePageUser = loginPageSecond.loginAs(user, password);

        InitiateRnRPage initiateRnRPage = homePageUser.navigateAndInitiateRnr(date_time, program);
        initiateRnRPage.verifyRnRHeader(date_time,program);
        homePageUser.logout();

    }
    @AfterClass
    public void tearDown() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteUser();
    }

    @DataProvider(name = "Data-Provider-Function-Positive")
    public Object[][] parameterIntTestProviderPositive() {
        return new Object[][]{
                {"HIV","User123", "User123",new String[]{"Admin123", "Admin123"}}
        };
    }
}
