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
import org.openlmis.pageobjects.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageFacility extends TestCaseHelper {

  LoginPage loginPage;
  FacilityPage facilityPage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    facilityPage = PageObjectFactory.getFacilityPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testUserSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "UPLOADS");
    dbWrapper.insertGeographicZone("Ngorongoro1", "Ngorongoro1", "Root");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadFacilities("QA_Facilities21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    FacilityPage facilityPage = homePage.navigateManageFacility();
    assertEquals("Search facility", facilityPage.getSearchFacilityLabel());
    facilityPage.searchFacility("fac");
    assertEquals("No matches found for 'fac'", facilityPage.getNoResultMessage());

    facilityPage.searchFacility("F14 Village Dispensary");
    assertEquals("14 matches found for 'F14 Village Dispensary'", facilityPage.getNResultsMessage());
    facilityPage.searchFacility("Village Dispensary");
    assertEquals("21 matches found for 'Village Dispensary'", facilityPage.getNResultsMessage());

    assertEquals("Name", facilityPage.getNameHeader());
    assertEquals("Code", facilityPage.getCodeHeader());
    assertEquals("Geographic Zone", facilityPage.getGeographicZoneHeader());
    assertEquals("Type", facilityPage.getTypeHeader());
    assertEquals("Active", facilityPage.getActiveHeader());
    assertEquals("Enabled", facilityPage.getEnabledHeader());

    assertEquals("Lvl3 Hospital", facilityPage.getFacilityType(1));
    assertTrue(facilityPage.getIsActive(1));
    assertTrue(facilityPage.getIsEnabled(1));

    verifyNumberOFPageLinksDisplayed(21, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyNameOrderOnPage(new String[]{"F10 Village Dispensary", "F11 Village Dispensary", "F12 Village Dispensary",
      "F13 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary",
      "F14 Village Dispensary", "F14 Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F10", "F11", "F12", "F13", "F14", "F17", "F19", "F20", "F21", "F22"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro1", "Ngorongoro", "Ngorongoro1", "Ngorongoro", "Ngorongoro1",
      "Ngorongoro", "Ngorongoro1", "Ngorongoro", "Ngorongoro1", "Ngorongoro"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyNameOrderOnPage(new String[]{"F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary",
      "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary",
      "F14a Village Dispensary", "F14d Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F23", "F24", "F25", "F26", "F27", "F28", "F29", "F30", "F16", "F18"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro1", "Ngorongoro", "Ngorongoro1", "Ngorongoro1", "Ngorongoro",
      "Ngorongoro1", "Ngorongoro", "Ngorongoro1", "Ngorongoro1", "Ngorongoro"});

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifyNameOrderOnPage(new String[]{"F14s Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F15"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyNameOrderOnPage(new String[]{"F10 Village Dispensary", "F11 Village Dispensary", "F12 Village Dispensary",
      "F13 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary",
      "F14 Village Dispensary", "F14 Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F10", "F11", "F12", "F13", "F14", "F17", "F19", "F20", "F21", "F22"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro1", "Ngorongoro", "Ngorongoro1", "Ngorongoro", "Ngorongoro1",
      "Ngorongoro", "Ngorongoro1", "Ngorongoro", "Ngorongoro1", "Ngorongoro"});

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);

    navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    facilityPage.closeSearchResults();
    assertFalse(facilityPage.isNameHeaderPresent());
  }

  @Test(groups = {"admin"})
  public void testFacilitySearchSortAndPaginationByGeographicZone() throws SQLException {
    dbWrapper.assignRight("Admin", "UPLOADS");
    dbWrapper.insertGeographicZone("Ngorongoro1", "Ngorongoro1", "Root");

    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadFacilities("QA_Facilities21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    facilityPage = homePage.navigateManageFacility();
    assertEquals("Facility", facilityPage.getSelectedSearchOption());
    facilityPage.clickSearchOptionButton();
    facilityPage.selectGeographicZoneAsSearchOption();
    assertEquals("Geographic zone", facilityPage.getSelectedSearchOption());

    facilityPage.searchFacility("Ngorongoro1");
    assertEquals("11 matches found for 'Ngorongoro1'", facilityPage.getNResultsMessage());
    facilityPage.searchFacility("Ngorongoro");
    assertEquals("21 matches found for 'Ngorongoro'", facilityPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(21, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyNameOrderOnPage(new String[]{"F11 Village Dispensary", "F13 Village Dispensary", "F14 Village Dispensary",
      "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary",
      "F14d Village Dispensary", "F14s Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F11", "F13", "F17", "F20", "F22", "F24", "F27", "F29", "F18", "F15"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro",
      "Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyNameOrderOnPage(new String[]{"F10 Village Dispensary", "F12 Village Dispensary", "F14 Village Dispensary",
      "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary",
      "F14 Village Dispensary", "F14 Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F10", "F12", "F14", "F19", "F21", "F23", "F25", "F26", "F28", "F30"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro1", "Ngorongoro1", "Ngorongoro1", "Ngorongoro1", "Ngorongoro1",
      "Ngorongoro1", "Ngorongoro1", "Ngorongoro1", "Ngorongoro1", "Ngorongoro1"});

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifyNameOrderOnPage(new String[]{"F14a Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F16"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro1"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyNameOrderOnPage(new String[]{"F11 Village Dispensary", "F13 Village Dispensary", "F14 Village Dispensary",
      "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary", "F14 Village Dispensary",
      "F14d Village Dispensary", "F14s Village Dispensary"});
    verifyCodeOrderOnPage(new String[]{"F11", "F13", "F17", "F20", "F22", "F24", "F27", "F29", "F18", "F15"});
    verifyGeographicZoneOrderOnPage(new String[]{"Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro",
      "Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro", "Ngorongoro"});

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);

    navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
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
    facilityPage.clickFirstFacilityList();
    facilityPage.disableFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    facilityPage.verifyDisabledFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    HomePage homePageRestore = facilityPage.enableFacility();
    assertEquals(facilityPage.getEnabledFacilityText(), "Yes");
    FacilityPage facilityPageRestore = homePageRestore.navigateManageFacility();
    facilityPageRestore.searchFacility(date_time);
    facilityPageRestore.clickFirstFacilityList();
    assertEquals("Edit facility", facilityPageRestore.getEditFacilityHeader());
    HomePage homePageEdit = facilityPageRestore.editFacility("ESSENTIAL MEDICINES", catchmentPopulationValue, latitudeValue, longitudeValue, altitudeValue);

    facilityPageRestore.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "updated");
    homePage.navigateManageFacility();
    facilityPage.searchFacility(date_time);
    facilityPage.clickFirstFacilityList();
    facilityPageRestore.verifyEditedFacility(catchmentPopulationValue, latitudeValue, longitudeValue, altitudeValue);

    FacilityPage facilityPageEdit = homePageEdit.navigateManageFacility();
    facilityPageEdit.searchFacility(date_time);
    facilityPageEdit.clickFirstFacilityList();
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
    facilityPage.clickFirstFacilityList();
    facilityPage.editFacilityType(facilityType);
    facilityPage.editGeographicZone(geoZone);
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFirstFacilityList();

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
    facilityPage.clickFirstFacilityList();
    facilityPage.removeFirstProgram();
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFirstFacilityList();

    assertEquals("ESSENTIAL MEDICINES", facilityPage.getProgramSupported(1));
    assertEquals("VACCINES", facilityPage.getProgramSupported(2));

    homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFirstFacilityList();
    facilityPage.removeFirstProgram();
    facilityPage.activeInactiveFirstProgram();
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFirstFacilityList();

    assertEquals("VACCINES", facilityPage.getProgramSupported(1));
    assertFalse("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));

    facilityPage.activeInactiveFirstProgram();
    facilityPage.saveFacility();
    facilityPage.clickFirstFacilityList();
    assertTrue("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));

    homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFirstFacilityList();
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFirstFacilityList();

    assertTrue("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));

    homePage.navigateManageFacility();
    facilityPage.searchFacility("F10");
    facilityPage.clickFirstFacilityList();
    facilityPage.addProgram("HIV", false);
    facilityPage.saveFacility();

    facilityPage.searchFacility("V10");
    facilityPage.clickFirstFacilityList();

    assertEquals("HIV", facilityPage.getProgramSupported(1));
    assertTrue("Program supported flag incorrect", facilityPage.getProgramSupportedActive(1));
    assertEquals("VACCINES", facilityPage.getProgramSupported(2));
    assertFalse("Program supported flag incorrect", facilityPage.getProgramSupportedActive(2));
    assertEquals(dbWrapper.getRequisitionGroupId("F10"), dbWrapper.getRequisitionGroupId("V10"));
  }

  private void verifyNameOrderOnPage(String[] nodeNames) {
    for (int i = 1; i < nodeNames.length; i++) {
      assertEquals(nodeNames[i - 1], facilityPage.getName(i));
    }
  }

  private void verifyCodeOrderOnPage(String[] nodeNames) {
    for (int i = 1; i < nodeNames.length; i++) {
      assertEquals(nodeNames[i - 1], facilityPage.getCode(i));
    }
  }

  private void verifyGeographicZoneOrderOnPage(String[] parentNames) {
    for (int i = 1; i < parentNames.length; i++) {
      assertEquals(parentNames[i - 1], facilityPage.getGeographicZone(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, testWebDriver.getElementsSizeByXpath("//table[@id='facilitySearchResultTable']/tbody/tr"));
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
