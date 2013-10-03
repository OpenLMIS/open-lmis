/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.CreateFacilityPage;
import org.openlmis.pageobjects.DeleteFacilityPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.util.ArrayList;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;

@TransactionConfiguration(defaultRollback = true)
@Transactional

@Listeners(CaptureScreenshotOnFailureListener.class)

public class E2EManageFacility extends TestCaseHelper {


  @BeforeMethod(groups = {"admin"})
  public void setUp() throws Exception {
    super.setup();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageFacility(String user, String program, String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";

    String date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, "500000");
    createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
    assertEquals("f", dbWrapper.getVirtualPropertyOfFacility(facilityCodePrefix+date_time));

    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    deleteFacilityPage.disableFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    deleteFacilityPage.verifyDisabledFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    HomePage homePageRestore = deleteFacilityPage.enableFacility();

    DeleteFacilityPage deleteFacilityPageRestore = homePageRestore.navigateSearchFacility();
    deleteFacilityPageRestore.searchFacility(date_time);
    deleteFacilityPageRestore.clickFacilityList(date_time);
    HomePage homePageEdit = deleteFacilityPageRestore.editAndVerifyFacility("ESSENTIAL MEDICINES", facilityNamePrefix + date_time);

    DeleteFacilityPage deleteFacilityPageEdit = homePageEdit.navigateSearchFacility();
    deleteFacilityPageEdit.searchFacility(date_time);

    ArrayList<String> programsSupported = new ArrayList<String>();
    programsSupported.add("HIV");
    programsSupported.add("ESSENTIAL MEDICINES");
    deleteFacilityPageEdit.verifyProgramSupported(programsSupported, date_time);


  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}
