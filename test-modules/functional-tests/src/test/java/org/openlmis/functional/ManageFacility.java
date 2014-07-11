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
import org.openlmis.pageobjects.FacilityPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.thoughtworks.selenium.SeleneseTestBase.*;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageFacility extends TestCaseHelper {

  LoginPage loginPage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageFacility(String user, String program, String[] credentials) throws SQLException {
    dbWrapper.insertUser(user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    FacilityPage facilityPage = homePage.navigateManageFacility();
    homePage.clickCreateFacilityButton();
    homePage.verifyHeader("Add new facility");

    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";
    String catchmentPopulationValue = "600000";
    String latitudeValue = "955.5555";
    String longitudeValue = "644.4444";
    String altitudeValue = "6545.4545";

    String date_time = facilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, "500000");
    facilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");
    assertEquals("f", dbWrapper.getAttributeFromTable("facilities", "virtualFacility", "code", facilityCodePrefix + date_time));

    homePage.navigateManageFacility();
    facilityPage.searchFacility(date_time);
    facilityPage.clickFacilityList(date_time);
    facilityPage.disableFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    facilityPage.verifyDisabledFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    HomePage homePageRestore = facilityPage.enableFacility();
    assertEquals(facilityPage.getEnabledFacilityText(), "Yes");
    FacilityPage facilityPageRestore = homePageRestore.navigateManageFacility();
    facilityPageRestore.searchFacility(date_time);
    facilityPageRestore.clickFacilityList(date_time);
    assertEquals("Edit facility", facilityPageRestore.getEditFacilityHeader());
    HomePage homePageEdit = facilityPageRestore.editFacility("ESSENTIAL MEDICINES", catchmentPopulationValue, latitudeValue, longitudeValue, altitudeValue);

    facilityPageRestore.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "updated");
    homePage.navigateManageFacility();
    facilityPage.searchFacility(date_time);
    facilityPage.clickFacilityList(date_time);
    facilityPageRestore.verifyEditedFacility(catchmentPopulationValue, latitudeValue, longitudeValue, altitudeValue);

    FacilityPage facilityPageEdit = homePageEdit.navigateManageFacility();
    facilityPageEdit.searchFacility(date_time);
    facilityPageEdit.clickFacilityList(date_time);
    ArrayList<String> programsSupported = new ArrayList<>();
    programsSupported.add("HIV");
    programsSupported.add("ESSENTIAL MEDICINES");
    facilityPageEdit.verifyProgramSupported(programsSupported);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testFacilityTypeAndGeoZonePropagationFromParentFacility(String user, String program, String[] credentials) throws SQLException {
    String geoZone = "District 1";
    String facilityType = "Lvl2 Hospital";

    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");

    dbWrapper.insertVirtualFacility("V10", "F10");
    dbWrapper.insertGeographicZone("District 1", "District 1", "Dodoma");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    FacilityPage facilityPage = homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFacilityList("F10");
    facilityPage.editFacilityType(facilityType);
    facilityPage.editGeographicZone(geoZone);
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFacilityList("V10");

    assertEquals(facilityType, facilityPage.getFacilityType());
    assertEquals(geoZone, facilityPage.getGeographicZone());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testProgramSupportedPropagationFromParentFacility(String user, String program, String[] credentials) throws SQLException {
    setupProductTestData("P10", "P11", program, "lvl3_hospital");
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node 1", "null");
    dbWrapper.insertSchedule("Q1stM", "QuarterMonthly", "QuarterMonth");
    dbWrapper.insertSchedule("M", "Monthly", "Month");
    setupRequisitionGroupData("RG1", "RG2", "N1", "N2", "F10", "F11");
    dbWrapper.insertVirtualFacility("V10", "F10");
    dbWrapper.insertGeographicZone("District 1", "District 1", "Dodoma");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    FacilityPage facilityPage = homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFacilityList("F10");
    facilityPage.removeFirstProgram();
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFacilityList("V10");

    assertEquals("ESSENTIAL MEDICINES", facilityPage.getProgramSupported(1));
    assertEquals("VACCINES", facilityPage.getProgramSupported(2));

    homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFacilityList("F10");
    facilityPage.removeFirstProgram();
    facilityPage.activeInactiveFirstProgram();
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFacilityList("V10");

    assertEquals("VACCINES", facilityPage.getProgramSupported(1));
    assertFalse("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));

    facilityPage.activeInactiveFirstProgram();
    facilityPage.saveFacility();
    facilityPage.clickFacilityList("V10");
    assertTrue("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));

    homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFacilityList("F10");
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFacilityList("V10");

    assertTrue("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));

    homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFacilityList("F10");
    facilityPage.addProgram("HIV", false);
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFacilityList("V10");

    assertEquals("HIV", facilityPage.getProgramSupported(1));
    assertTrue("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));
    assertEquals("VACCINES", facilityPage.getProgramSupported(2));
    assertFalse("Program supported flag incorrect", facilityPage.getProgramSupportedActive(2));
    assertEquals(dbWrapper.getRequisitionGroupId("F10"), dbWrapper.getRequisitionGroupId("V10"));
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
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
