package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.InitiateRnRPage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRights extends TestCaseHelper {

  DBWrapper dbWrapper;

  @BeforeMethod(groups = {"functional"})
  @Parameters({"browser"})
  public void setUp(String browser) throws Exception {
    super.setupSuite(browser);
    dbWrapper = new DBWrapper();
    dbWrapper.deleteData();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testOnlyCreateRight(String program, String userSIC,  String password) throws Exception {

    dbWrapper.insertProducts("P10", "P11");
    dbWrapper.insertProgramProducts("P10", "P11", program);
    dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    dbWrapper.insertRole("store in-charge","false","");
    dbWrapper.insertRole("district pharmacist","false","");
    dbWrapper.assignRight("store in-charge", "CREATE_REQUISITION");
    dbWrapper.assignRight("store in-charge", "VIEW_REQUISITION");
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser("200",userSIC,passwordUsers,"F10","Fatima_Doe@openlmis.com");
    dbWrapper.insertSupervisoryNode("F10","N1","Node 1","null");
    dbWrapper.insertRoleAssignment("200","store in-charge");
    dbWrapper.insertSchedules();
    dbWrapper.insertProcessingPeriods();
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
    dbWrapper.insertRequisitionGroupMembers("F10", "F11");
    dbWrapper.insertRequisitionGroupProgramSchedule();
    dbWrapper.insertSupplyLines("N1",program,"F10");

    LoginPage loginPage = new LoginPage(testWebDriver);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    String[] expectedMenuItem = {"Create / Authorize", "View"};
    homePage.verifySubMenuItems(expectedMenuItem);
    String periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();

      initiateRnRPage.enterBeginningBalance("10");
      initiateRnRPage.enterQuantityDispensed("10");
      initiateRnRPage.enterQuantityReceived("10");
      initiateRnRPage.submitRnR();
      initiateRnRPage.verifyAuthorizeButtonNotPresent();

  }

    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void testUserTryToAuthorizeUnSubmittedRnR(String program, String userSIC,  String password) throws Exception {

        dbWrapper.insertProducts("P10", "P11");
        dbWrapper.insertProgramProducts("P10", "P11", program);
        dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.configureTemplate(program);
        dbWrapper.insertRole("store in-charge","false","");
        dbWrapper.insertRole("district pharmacist","false","");
        dbWrapper.assignRight("store in-charge", "AUTHORIZE_REQUISITION");
        dbWrapper.assignRight("store in-charge", "VIEW_REQUISITION");
        String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
        dbWrapper.insertUser("200",userSIC,passwordUsers,"F10","Fatima_Doe@openlmis.com");
        dbWrapper.insertSupervisoryNode("F10","N1","Node 1","null");
        dbWrapper.insertRoleAssignment("200","store in-charge");
        dbWrapper.insertSchedules();
        dbWrapper.insertProcessingPeriods();
        dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
        dbWrapper.insertRequisitionGroupMembers("F10", "F11");
        dbWrapper.insertRequisitionGroupProgramSchedule();
        dbWrapper.insertSupplyLines("N1",program,"F10");

        LoginPage loginPage = new LoginPage(testWebDriver);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        String[] expectedMenuItem = {"Create / Authorize", "View"};
        homePage.verifySubMenuItems(expectedMenuItem);
        homePage.navigateAndInitiateRnr(program);
        homePage.clickProceed();
        homePage.verifyErrorMessage();

        dbWrapper.insertValuesInRequisition();
        dbWrapper.updateRequisitionStatus("INITIATED");

        homePage.navigateAndInitiateRnr(program);
        homePage.clickProceed();
        homePage.verifyErrorMessage();

    }



  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "Admin123"}
    };

  }
}

