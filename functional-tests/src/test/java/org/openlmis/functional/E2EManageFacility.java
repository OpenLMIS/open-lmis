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
        dbWrapper.deleteData();
    }

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void testE2EManageFacility(String user,  String[] credentials) throws Exception {

        LoginPage loginPage=new LoginPage(testWebDriver);
        DBWrapper dbWrapper = new DBWrapper();

        dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==","F10", "Jane_Doe@openlmis.com");

        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

        CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
        String date_time=createFacilityPage.enterAndVerifyFacility();

        DeleteFacilityPage deleteFacilityPage=homePage.navigateSearchFacility();
        deleteFacilityPage.searchFacility(date_time);
        deleteFacilityPage.deleteAndVerifyFacility("FCcode"+date_time,"FCname"+date_time );
        HomePage homePage1=deleteFacilityPage.restoreAndVerifyFacility("FCcode"+date_time,"FCname"+date_time );

        DeleteFacilityPage deleteFacilityPage1=homePage1.navigateSearchFacility();
        deleteFacilityPage1.searchFacility(date_time);
        deleteFacilityPage1.editAndVerifyFacility("FCname"+date_time);

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
                {"User123", new String[]{"Admin123", "Admin123"}}
        };
    }
}
