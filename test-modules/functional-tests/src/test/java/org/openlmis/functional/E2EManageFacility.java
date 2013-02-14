package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.DeleteFacilityPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EManageFacility extends TestCaseHelper {

  DBWrapper dbWrapper;

  @BeforeClass
  public void setUp() throws Exception {
    dbWrapper = new DBWrapper();
    dbWrapper.deleteData();
  }

  @Test(dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageFacility(String user, String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String date_time = createFacilityPage.enterAndVerifyFacility();

    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.deleteAndVerifyFacility("FCcode" + date_time, "FCname" + date_time);
    HomePage homePage1 = deleteFacilityPage.restoreAndVerifyFacility("FCcode" + date_time, "FCname" + date_time);

    DeleteFacilityPage deleteFacilityPage1 = homePage1.navigateSearchFacility();
    deleteFacilityPage1.searchFacility(date_time);
    HomePage homePage2 = deleteFacilityPage1.editAndVerifyFacility("FCname" + date_time);

    DeleteFacilityPage deleteFacilityPage2 = homePage2.navigateSearchFacility();
    deleteFacilityPage2.searchFacility(date_time);
    deleteFacilityPage2.verifyProgramSupported();


  }

  @AfterClass
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
        {"User123", new String[]{"Admin123", "Admin123"}}
    };
  }
}
