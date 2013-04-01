/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
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

public class RnRPagination extends TestCaseHelper {
    @BeforeMethod(groups = {"functional"})
    public void setUp() throws Exception {
        super.setup();
    }

 @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
  public void testRnRPaginationAndSpecificDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
      dbWrapper.setupMultipleProducts(program,"Lvl3 Hospital",21,false);
      dbWrapper.insertFacilities("F10", "F11");
      dbWrapper.configureTemplate(program);
      setupTestUserRoleRightsData(userSIC);
      dbWrapper.insertSupervisoryNode("F10","N1","Node 1","null");
      dbWrapper.insertRoleAssignment("200","store in-charge");
      dbWrapper.insertSchedules();
      dbWrapper.insertProcessingPeriods();
      setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
      dbWrapper.insertSupplyLines("N1",program,"F10");

      LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
      HomePage homePage = loginPage.loginAs(userSIC, password);
      homePage.navigateAndInitiateRnr(program);
      InitiateRnRPage initiateRnRPage = homePage.clickProceed();

      testWebDriver.sleep(2000);
      verifyNumberOfPageLinks(21,20);
      verifyNextAndLastLinksEnabled();
      verifyPreviousAndFirstLinksDisabled();
      verifyDisplayOrderFullSupply(20);

      //initiateRnRPage.PopulateMandatoryFullSupplyDetails(21,20);

      testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
      SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody[1]/tr[1]/td").getText(),"Antibiotics");
      verifyNextAndLastLinksDisabled();
      verifyPreviousAndFirstLinksEnabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '«')]").click();
      verifyNextAndLastLinksEnabled();
      verifyPreviousAndFirstLinksDisabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '>')]").click();
      verifyNextAndLastLinksDisabled();
      verifyPreviousAndFirstLinksEnabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '<')]").click();
      verifyNextAndLastLinksEnabled();
      verifyPreviousAndFirstLinksDisabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '»')]").click();
      verifyNextAndLastLinksDisabled();
      verifyPreviousAndFirstLinksEnabled();

      initiateRnRPage.addMultipleNonFullSupplyLineItems(21,false);
      verifyDisplayOrderNonFullSupply(20);
      verifyNumberOfPageLinks(21,20);
      verifyPreviousAndFirstLinksDisabled();
      verifyNextAndLastLinksEnabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '2') and @class='ng-binding']").click();
      SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody[1]/tr[1]/td").getText(),"Antibiotics");
      verifyNextAndLastLinksDisabled();
      verifyPreviousAndFirstLinksEnabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '«')]").click();
      verifyPreviousAndFirstLinksDisabled();
      verifyNextAndLastLinksEnabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '>')]").click();
      verifyNextAndLastLinksDisabled();
      verifyPreviousAndFirstLinksEnabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '<')]").click();
      verifyPreviousAndFirstLinksDisabled();
      verifyNextAndLastLinksEnabled();

      testWebDriver.getElementByXpath("//a[contains(text(), '»')]").click();
      verifyNextAndLastLinksDisabled();

      //initiateRnRPage.submitRnR();

  }

    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void testProductDefaultDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
        dbWrapper.setupMultipleProducts(program,"Lvl3 Hospital",11,true);
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.configureTemplate(program);
        setupTestUserRoleRightsData(userSIC);
        dbWrapper.insertSupervisoryNode("F10","N1","Node 1","null");
        dbWrapper.insertRoleAssignment("200","store in-charge");
        dbWrapper.insertSchedules();
        dbWrapper.insertProcessingPeriods();
        setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
        dbWrapper.insertSupplyLines("N1",program,"F10");

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        String periodDetails = homePage.navigateAndInitiateRnr(program);
        InitiateRnRPage initiateRnRPage = homePage.clickProceed();

        testWebDriver.sleep(2000);
        verifyDefaultDisplayOrderFullSupply();


        initiateRnRPage.addMultipleNonFullSupplyLineItems(11,false);
        verifyDefaultDisplayOrderNonFullSupply();
    }

    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void testCategoryDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
        dbWrapper.setupMultipleCategoryProducts(program,"Lvl3 Hospital",11,false) ;
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.configureTemplate(program);
        setupTestUserRoleRightsData(userSIC);
        dbWrapper.insertSupervisoryNode("F10","N1","Node 1","null");
        dbWrapper.insertRoleAssignment("200","store in-charge");
        dbWrapper.insertSchedules();
        dbWrapper.insertProcessingPeriods();
        setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
        dbWrapper.insertSupplyLines("N1",program,"F10");

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        String periodDetails = homePage.navigateAndInitiateRnr(program);
        InitiateRnRPage initiateRnRPage = homePage.clickProceed();

        testWebDriver.sleep(2000);
        verifyCategoryDisplayOrderFullSupply(11);


        initiateRnRPage.addMultipleNonFullSupplyLineItems(11,true);
        verifyCategoryDisplayOrderNonFullSupply(11);
    }

    @Test(groups = {"functional"}, dataProvider = "Data-Provider-Function-Positive")
    public void testCategoryDefaultDisplayOrder(String program, String userSIC, String userMO, String password, String[] credentials) throws Exception {
        dbWrapper.setupMultipleCategoryProducts(program,"Lvl3 Hospital",11,true) ;
        dbWrapper.insertFacilities("F10", "F11");
        dbWrapper.configureTemplate(program);
        setupTestUserRoleRightsData(userSIC);
        dbWrapper.insertSupervisoryNode("F10","N1","Node 1","null");
        dbWrapper.insertRoleAssignment("200","store in-charge");
        dbWrapper.insertSchedules();
        dbWrapper.insertProcessingPeriods();
        setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
        dbWrapper.insertSupplyLines("N1",program,"F10");

        LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
        HomePage homePage = loginPage.loginAs(userSIC, password);
        String periodDetails = homePage.navigateAndInitiateRnr(program);
        InitiateRnRPage initiateRnRPage = homePage.clickProceed();

        testWebDriver.sleep(2000);
        verifyCategoryDefaultDisplayOrderFullSupply();


        initiateRnRPage.addMultipleNonFullSupplyLineItems(11,true);
        verifyCategoryDefaultDisplayOrderNonFullSupply();
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
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '>')]").getCssValue("color"),"rgba(119, 119, 119, 1)") ;
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '»')]").getCssValue("color"),"rgba(119, 119, 119, 1)") ;
    }

    public void verifyPreviousAndFirstLinksEnabled() throws Exception {
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '<')]").getCssValue("color"),"rgba(119, 119, 119, 1)") ;
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '«')]").getCssValue("color"),"rgba(119, 119, 119, 1)") ;
    }

    public void verifyNextAndLastLinksDisabled() throws Exception {
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '>')]").getCssValue("color"),"rgba(204, 204, 204, 1)") ;
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '»')]").getCssValue("color"),"rgba(204, 204, 204, 1)") ;
    }

    public void verifyPreviousAndFirstLinksDisabled() throws Exception {
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '«')]").getCssValue("color"),"rgba(204, 204, 204, 1)") ;
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//a[contains(text(), '<')]").getCssValue("color"),"rgba(204, 204, 204, 1)") ;
    }

    public void verifyDisplayOrderFullSupply(int numberOfLineItemsPerPage) throws Exception {
        for (int i=0; i<numberOfLineItemsPerPage;i++)
        {
            SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody["+ (i+1) +"]/tr[2]/td[1]/ng-switch/span/ng-switch/span").getText(),"F"+i);
        }
    }

    public void verifyDisplayOrderNonFullSupply(int numberOfLineItemsPerPage) throws Exception {
        for (int i=0; i<numberOfLineItemsPerPage;i++)
        {
            SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody["+ (i+1) +"]/tr[2]/td[1]/ng-switch/span").getText(),"NF"+i);
        }
    }

    public void verifyDefaultDisplayOrderFullSupply() throws Exception {
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody[1]/tr[2]/td[1]/ng-switch/span/ng-switch/span").getText(),"F0");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody[2]/tr[2]/td[1]/ng-switch/span/ng-switch/span").getText(),"F1");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody[3]/tr[2]/td[1]/ng-switch/span/ng-switch/span").getText(),"F10");
    }

    public void verifyDefaultDisplayOrderNonFullSupply() throws Exception {
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody[1]/tr[2]/td[1]/ng-switch/span").getText(),"NF0");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody[2]/tr[2]/td[1]/ng-switch/span").getText(),"NF1");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody[3]/tr[2]/td[1]/ng-switch/span").getText(),"NF10");

    }

    public void verifyCategoryDisplayOrderFullSupply(int numberOfLineItems) throws Exception {
        for (int i=0; i<numberOfLineItems;i++)
        {
            SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody["+ (i+1) +"]/tr[1]/td").getText(),"Antibiotics"+i);
        }
    }

    public void verifyCategoryDisplayOrderNonFullSupply(int numberOfLineItems) throws Exception {
        for (int i=0; i<numberOfLineItems;i++)
        {
            SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody["+ (i+1) +"]/tr[1]/td").getText(),"Antibiotics"+i);
        }
    }

    public void verifyCategoryDefaultDisplayOrderFullSupply() throws Exception {
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody[1]/tr[1]/td").getText(),"Antibiotics0");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody[2]/tr[1]/td").getText(),"Antibiotics1");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='fullSupplyTable']/tbody[3]/tr[1]/td").getText(),"Antibiotics10");
    }

    public void verifyCategoryDefaultDisplayOrderNonFullSupply() throws Exception {
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody[1]/tr[1]/td").getText(),"Antibiotics0");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody[2]/tr[1]/td").getText(),"Antibiotics1");
        SeleneseTestNgHelper.assertEquals(testWebDriver.getElementByXpath("//table[@id='nonFullSupplyTable']/tbody[3]/tr[1]/td").getText(),"Antibiotics10");

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
      {"HIV", "storeincharge", "medicalofficer", "Admin123", new String[]{"Admin123", "Admin123"}}
    };

  }
}

