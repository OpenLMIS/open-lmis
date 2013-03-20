package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;


@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewRequisition extends TestCaseHelper {

  @BeforeMethod(groups = {"functional"})
  public void setUp() throws Exception {
    super.setup();
  }


  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testViewRequisition(String program, String userSIC, String password) throws Exception {

    dbWrapper.insertProducts("P10", "P11");
    dbWrapper.insertProgramProducts("P10", "P11", program);
    dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    dbWrapper.insertRole("store in-charge", "false", "");
    dbWrapper.insertRole("district pharmacist", "false", "");
    dbWrapper.insertRoleRights();
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    dbWrapper.insertUser("200", userSIC, passwordUsers, "F10", "Fatima_Doe@openlmis.com");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertRoleAssignment("200", "store in-charge");
    dbWrapper.insertSchedules();
    dbWrapper.insertProcessingPeriods();
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
    dbWrapper.insertRequisitionGroupMembers("F10", "F11");
    dbWrapper.insertRequisitionGroupProgramSchedule();
    dbWrapper.insertSupplyLines("N1", program, "F10");

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userSIC, password);
    String periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    HomePage homePage1 = initiateRnRPage.clickHome();


    ViewRequisitionPage viewRequisitionPage = homePage1.navigateViewRequisition();
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
    dbWrapper.insertValuesInRequisition();
    dbWrapper.updateRequisitionStatus("SUBMITTED");
    viewRequisitionPage.enterSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.verifyNoRequisitionFound();
    dbWrapper.updateRequisitionStatus("AUTHORIZED");
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();

    HomePage homePageAuthorized = viewRequisitionPage.verifyFieldsPreApproval("12.50", "1");
    ViewRequisitionPage viewRequisitionPageAuthorized = homePageAuthorized.navigateViewRequisition();
    viewRequisitionPageAuthorized.enterSearchCriteria();
    viewRequisitionPageAuthorized.clickSearch();
    viewRequisitionPageAuthorized.verifyStatus("AUTHORIZED");
    viewRequisitionPageAuthorized.clickRnRList();

    HomePage homePageInApproval = viewRequisitionPageAuthorized.verifyFieldsPreApproval("12.50", "1");
    dbWrapper.updateRequisitionStatus("IN_APPROVAL");
    ViewRequisitionPage viewRequisitionPageInApproval = homePageInApproval.navigateViewRequisition();
    viewRequisitionPageInApproval.enterSearchCriteria();
    viewRequisitionPageInApproval.clickSearch();
    viewRequisitionPageInApproval.verifyStatus("IN_APPROVAL");


    ApprovePage approvePageTopSNUser = homePageInApproval.navigateToApprove();
    approvePageTopSNUser.verifyandclickRequisitionPresentForApproval();
    approvePageTopSNUser.editApproveQuantityAndVerifyTotalCostViewRequisition("20");
    approvePageTopSNUser.approveRequisition();
    approvePageTopSNUser.verifyNoRequisitionPendingMessage();
    ViewRequisitionPage viewRequisitionPageApproved = homePageInApproval.navigateViewRequisition();
    viewRequisitionPageApproved.enterSearchCriteria();
    viewRequisitionPageApproved.clickSearch();
    viewRequisitionPageApproved.verifyStatus("APPROVED");
    viewRequisitionPageApproved.clickRnRList();
//    HomePage homePageApproved=viewRequisitionPageApproved.verifyFieldsPostApproval("25.00", "1");
    HomePage homePageApproved = viewRequisitionPageApproved.verifyFieldsPostApproval("12.50", "1");

    dbWrapper.updateRequisition("F10");
    OrderPage orderPage = homePageApproved.navigateConvertToOrder();
    orderPage.convertToOrder();
    ViewRequisitionPage viewRequisitionPageOrdered = homePageApproved.navigateViewRequisition();
    viewRequisitionPageOrdered.enterSearchCriteria();
    viewRequisitionPageOrdered.clickSearch();
    viewRequisitionPageOrdered.verifyStatus("ORDERED");
    viewRequisitionPageOrdered.clickRnRList();
    //viewRequisitionPageOrdered.verifyFieldsPostApproval("25.00", "1");

  }

  @AfterMethod(groups = {"functional"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
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

