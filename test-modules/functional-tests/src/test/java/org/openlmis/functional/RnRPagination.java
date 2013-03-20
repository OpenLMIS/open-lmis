package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
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

public class RnRPagination extends TestCaseHelper {
    @BeforeMethod(groups = {"functional"})
    public void setUp() throws Exception {
        super.setup();
    }

  @Test(groups = {"smoke"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRPagination(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
      dbWrapper.setupProducts_Pagination(program,"Lvl3 Hospital",21);
      dbWrapper.insertFacilities("F10", "F11");
      dbWrapper.configureTemplate(program);
      dbWrapper.insertRole("store in-charge","false","");
      dbWrapper.insertRole("district pharmacist","false","");
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

      LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
      HomePage homePage = loginPage.loginAs(userSIC, password);
      String periodDetails = homePage.navigateAndInitiateRnr(program);
      InitiateRnRPage initiateRnRPage = homePage.clickProceed();


    verifyNumberOfPageLinks(21,20);
    verifyPreviousAndFirstLinksDisabled();


    initiateRnRPage.PopulateMandatoryFullSupplyDetails(21,20);

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    verifyNextAndLastLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '«')]").click();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '>')]").click();
    verifyNextAndLastLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '<')]").click();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '»')]").click();
    verifyNextAndLastLinksDisabled();

    initiateRnRPage.addMultipleNonFullSupplyLineItems(21,20);
    verifyNumberOfPageLinks(21,20);
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
    verifyNextAndLastLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '«')]").click();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '>')]").click();
    verifyNextAndLastLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '<')]").click();
    verifyPreviousAndFirstLinksDisabled();

    testWebDriver.getElementByXpath("//a[contains(text(), '»')]").click();
    verifyNextAndLastLinksDisabled();

    initiateRnRPage.submitRnR();

    initiateRnRPage.authorizeRnR();
    initiateRnRPage.verifyAuthorizeRnrSuccessMsg();
    initiateRnRPage.verifyBeginningBalanceDisabled();

  }

    public void verifyNumberOfPageLinks(int numberOfProducts, int numberOfLineItemsPerPage) throws Exception {
      int numberOfPages= numberOfProducts/numberOfLineItemsPerPage;
        if (numberOfProducts%numberOfLineItemsPerPage != 0)
        {
            numberOfPages=numberOfPages+1;
        }
      for (int i=1; i<=numberOfPages;i++ )
      {
          SeleneseTestNgHelper.assertTrue(testWebDriver.getElementByXpath("//a[contains(text(), '" + i + "') and @class='ng-binding']").isDisplayed());
      }
    }

    public void verifyNextAndLastLinksEnabled() throws Exception {
        SeleneseTestNgHelper.assertTrue(testWebDriver.getElementByXpath("//a[contains(text(), '>')]").isEnabled());
        SeleneseTestNgHelper.assertTrue(testWebDriver.getElementByXpath("//a[contains(text(), '»')]").isEnabled());
    }

    public void verifyPreviousAndFirstLinksEnabled() throws Exception {
            SeleneseTestNgHelper.assertTrue(testWebDriver.getElementByXpath("//a[contains(text(), '<')]").isEnabled());
            SeleneseTestNgHelper.assertTrue(testWebDriver.getElementByXpath("//a[contains(text(), '«')]").isEnabled());
    }

    public void verifyNextAndLastLinksDisabled() throws Exception {
        //try{
            testWebDriver.getElementByXpath("//a[contains(text(), '>')]").click();
        //    }
        //catch(Exception e){
        //    e.printStackTrace();
        //}

        //try{
            testWebDriver.getElementByXpath("//a[contains(text(), '»')]").click();
        //}
        //catch(Exception e){
        //    e.printStackTrace();
        //}

    }

    public void verifyPreviousAndFirstLinksDisabled() throws Exception {
        //try{
            testWebDriver.getElementByXpath("//a[contains(text(), '<')]").click();
        //}
        //catch(Exception e){
        //    e.printStackTrace();
        //}

        //try{
            testWebDriver.getElementByXpath("//a[contains(text(), '«')]");
        //}
        //catch(Exception e){
        //    e.printStackTrace();
        //}
    }

    @AfterMethod(groups = {"smoke"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"HIV", "storeincharge", "medicalofficer", "Admin123", new String[]{"Admin123", "Admin123"}}
    };

  }
}

