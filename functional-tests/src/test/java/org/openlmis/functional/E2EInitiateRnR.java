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
        LoginPage loginPage=new LoginPage(testWebDriver);

        dbWrapper.insertUser();
        dbWrapper.insertRoles();
        dbWrapper.insertRoleRights();
        dbWrapper.insertRoleAssignment();

        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createfacilitypage=new CreateFacilityPage(testWebDriver);
        createfacilitypage.navigateCreateFacility();
        String date_time=createfacilitypage.enterAndVerifyFacility();

        dbWrapper.insertUserAndAllocateFacility();

        TemplateConfigPage config=new TemplateConfigPage(testWebDriver);
        config.selectProgramToConfigTemplate(program);
        config.configureTemplate();

        homePage.logout();

        HomePage homePage1 = loginPage.loginAs(user, password);

        InitiateRnRPage initiateRnR=new InitiateRnRPage(testWebDriver);
        initiateRnR.navigateAndInitiateRnr(date_time, program);
        initiateRnR.verifyRnRHeader(date_time,program);
        homePage1.logout();

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
