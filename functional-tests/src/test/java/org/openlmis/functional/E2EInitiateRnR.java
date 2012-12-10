package org.openlmis.functional;


import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.TemplateConfigPage;
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
    public void testE2EInitiateRnR(String[] credentials) throws Exception {

        DBWrapper dbWrapper = new DBWrapper();
        LoginPage loginpage=new LoginPage(testWebDriver);
        CreateFacilityPage createfacilitypage=new CreateFacilityPage(testWebDriver);
        loginpage.login(credentials[0], credentials[1]);
        createfacilitypage.navigateCreateFacility();
        createfacilitypage.enterAndVerifyFacility();

        dbWrapper.insertUserAndAllocateFacility();

        TemplateConfigPage config=new TemplateConfigPage(testWebDriver);
        config.selectProgramToConfigTemplate("HIV");
        config.configureTemplate();

        dbWrapper.insertRoles();
        dbWrapper.insertRoleRights();
        dbWrapper.insertRoleAssignment();

        loginpage.logout();

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
                {new String[]{"Admin123", "Admin123"}}
        };
    }
}
