package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
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

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EManageFacility extends TestCaseHelper {

    @BeforeClass
    public void setUp() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteUser();
    }

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testE2EInitiateRnR(String[] credentials) throws Exception {

        LoginPage loginPage=new LoginPage(testWebDriver);
        DBWrapper dbWrapper = new DBWrapper();

        dbWrapper.insertUser("User123", "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==");
        dbWrapper.insertRoles();
        dbWrapper.insertRoleRights();
        dbWrapper.insertRoleAssignment();
        dbWrapper.insertProducts();
        dbWrapper.insertProgramProducts();
        dbWrapper.insertFacilityApprovedProducts();



        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
        String date_time=createFacilityPage.enterAndVerifyFacility();

        DeleteFacilityPage deleteFacilityPage=homePage.navigateSearchFacility();
        deleteFacilityPage.searchFacility(date_time);
        deleteFacilityPage.deleteAndVerifyFacility("FCcode"+date_time,"FCname"+date_time );
        HomePage homePage1=deleteFacilityPage.restoreAndVerifyFacility("FCcode"+date_time,"FCname"+date_time );

        DeleteFacilityPage deleteFacilityPage1=homePage1.navigateSearchFacility();
        deleteFacilityPage1.searchFacility(date_time);
        deleteFacilityPage1.editAndVerifyFacility();

        homePage.logout();


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
