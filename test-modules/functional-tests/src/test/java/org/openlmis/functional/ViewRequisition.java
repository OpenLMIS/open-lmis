package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.UiUtils.Unzip;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ViewRequisition extends TestCaseHelper {

  DBWrapper dbWrapper;

  @BeforeMethod(groups = {"functional"})
  @Parameters({"browser"})
  public void setUp(String browser) throws Exception {
    super.setupSuite(browser);
    dbWrapper = new DBWrapper();
    dbWrapper.deleteData();
  }

  @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testViewRequisition(String program, String userSIC,  String password) throws Exception {

    dbWrapper.insertProducts("P10", "P11");
    dbWrapper.insertProgramProducts("P10", "P11", program);
    dbWrapper.insertFacilityApprovedProducts("P10", "P11", program, "Lvl3 Hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.configureTemplate(program);
    dbWrapper.insertRole("store in-charge","");
    dbWrapper.insertRole("district pharmacist","");
    dbWrapper.insertRoleRights();
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
    String periodDetails = homePage.navigateAndInitiateRnr(program);
    InitiateRnRPage initiateRnRPage = homePage.clickProceed();
    HomePage homePage1=initiateRnRPage.clickHome();


    ViewRequisitionPage viewRequisitionPage=homePage1.navigateViewRequisition();
    viewRequisitionPage.verifyElementsOnViewRequisitionScreen();
    dbWrapper.insertValuesInRequisition();
    dbWrapper.updateRequisitionStatus("SUBMITTED");
    viewRequisitionPage.enterSearchCriteria();
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.verifyNoRequisitionFound();
    dbWrapper.updateRequisitionStatus("AUTHORIZED");
    viewRequisitionPage.clickSearch();
    viewRequisitionPage.clickRnRList();

    HomePage homePage2=viewRequisitionPage.verifyFieldsPreApproval("12.50", "1");
    ViewRequisitionPage viewRequisitionPage2=homePage2.navigateViewRequisition();
    viewRequisitionPage2.enterSearchCriteria();
    viewRequisitionPage2.clickSearch();
    viewRequisitionPage2.verifyStatus("AUTHORIZED");
    viewRequisitionPage2.clickRnRList();

    HomePage homePage3=viewRequisitionPage2.verifyFieldsPreApproval("12.50", "1");
    dbWrapper.updateRequisitionStatus("IN_APPROVAL");
    ViewRequisitionPage viewRequisitionPage3=homePage3.navigateViewRequisition();
    viewRequisitionPage3.enterSearchCriteria();
    viewRequisitionPage3.clickSearch();
    viewRequisitionPage3.verifyStatus("IN_APPROVAL");


    ApprovePage approvePageTopSNUser = homePage3.navigateToApprove();
    approvePageTopSNUser.verifyandclickRequisitionPresentForApproval();
    approvePageTopSNUser.editApproveQuantityAndVerifyTotalCostViewRequisition("20");
    approvePageTopSNUser.approveRequisition();
    approvePageTopSNUser.verifyNoRequisitionPendingMessage();
    ViewRequisitionPage viewRequisitionPage4=homePage3.navigateViewRequisition();
    viewRequisitionPage4.enterSearchCriteria();
    viewRequisitionPage4.clickSearch();
    viewRequisitionPage4.verifyStatus("APPROVED");
    viewRequisitionPage4.clickRnRList();
//    HomePage homePage4=viewRequisitionPage4.verifyFieldsPostApproval("25.00", "1");
    HomePage homePage4=viewRequisitionPage4.verifyFieldsPostApproval("12.50", "1");

    dbWrapper.updateRequisition("F10");
    OrderPage orderPage=homePage4.navigateConvertToOrder();
    orderPage.convertToOrder();
    ViewRequisitionPage viewRequisitionPage5=homePage4.navigateViewRequisition();
    viewRequisitionPage5.enterSearchCriteria();
    viewRequisitionPage5.clickSearch();
    viewRequisitionPage5.verifyStatus("ORDERED");
    viewRequisitionPage5.clickRnRList();
    //viewRequisitionPage5.verifyFieldsPostApproval("25.00", "1");

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

