package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManageSchedulePage;
import org.openlmis.pageobjects.PeriodsPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

@TransactionConfiguration(defaultRollback=true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EManageSchedule extends TestCaseHelper {

    @BeforeClass
    public void setUp() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();
        dbWrapper.deleteFacilities();
    }

    @Test(dataProvider = "Data-Provider-Function-Positive")
    public void addSchedule(String[] credentials) throws Exception {

        LoginPage loginPage=new LoginPage(testWebDriver);

        HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
        ManageSchedulePage manageSchedulePage=homePage.navigateToSchedule();
        manageSchedulePage.createAndVerifySchedule();
        manageSchedulePage.editAndVerifySchedule();
        PeriodsPage periodsPage=manageSchedulePage.navigatePeriods();
        periodsPage.createAndVerifyPeriods();
        periodsPage.deleteAndVerifyPeriods();

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
                {new String[]{"Admin123", "Admin123"}}
        };
    }
}
