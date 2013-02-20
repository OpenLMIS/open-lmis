package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EUpload extends TestCaseHelper {

  DBWrapper dbWrapper;

  @BeforeClass
  public void setUp() throws Exception {
    super.setupSuite();
    dbWrapper = new DBWrapper();
    dbWrapper.deleteData();
    dbWrapper.deleteFacilities();
  }

  @Test(dataProvider = "Data-Provider-Function-Positive")
  public void uploadCSVFiles(String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    TemplateConfigPage templateConfigPage = homePage.selectProgramToConfigTemplate("HIV");
    templateConfigPage.configureTemplate();

    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add("Create Requisition");

    rolesPage.createRole("User", "User", userRoleList);

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadUsers();
    dbWrapper.alterUserID("200");

    dbWrapper.insertRoleAssignment("200", "User");

    uploadPage.uploadProductCategory();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadProducts();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadProgramProductMapping();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadProgramProductPrice();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadFacilities();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadFacilityTypeToProductMapping();
    dbWrapper.allocateFacilityToUser("200", "F10");

    uploadPage.uploadProgramSupportedByFacilities();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadSupervisoryNodes();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadRequisitionGroup();
    testWebDriver.setImplicitWait(2500);

    dbWrapper.insertSchedules();
    dbWrapper.insertProcessingPeriods();

    uploadPage.uploadRequisitionGroupProgramSchedule();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadRequisitionGroupMembers();
    testWebDriver.setImplicitWait(2500);

    uploadPage.uploadSupplyLines();
    testWebDriver.setImplicitWait(2500);


//        LoginPage loginPageSecond=homePage.logout();
//        HomePage homePageUser = loginPageSecond.loginAs("User123", "User123");
//
//        InitiateRnRPage initiateRnRPage = homePageUser.navigateAndInitiateRnr("F11","F11 Village Dispensary", "", "HIV", "Period2");
//        initiateRnRPage.verifyRnRHeader("F11","F11 Village Dispensary", "", "HIV");

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
        {new String[]{"Admin123", "Admin123"}}
    };
  }
}
