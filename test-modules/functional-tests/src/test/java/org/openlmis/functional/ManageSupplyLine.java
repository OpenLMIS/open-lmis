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

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;

public class ManageSupplyLine extends TestCaseHelper {

  LoginPage loginPage;
  SupplyLinePage supplyLinePage;

  public static final String ADMIN = "admin";
  public static final String PASSWORD = "password";

  public Map<String, String> testData = new HashMap<String, String>() {{
    put(PASSWORD, "Admin123");
    put(ADMIN, "Admin123");
  }};

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F10", "F11", 1, 1);
    dbWrapper.updateFieldValue("facilities", "name", "central Hospital", "code", "F11");
    dbWrapper.updateFieldValue("programs", "name", "malaria", "code", "MALARIA");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "node3", null);
    dbWrapper.insertSupervisoryNode("F10", "N4", "Node4", null);
    dbWrapper.insertSupervisoryNode("F10", "N5", "Node5", null);
    dbWrapper.insertSupervisoryNode("F10", "N6", "Node6", null);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11A", "F11B", 1, 1);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11C", "F11D", 1, 1);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    supplyLinePage = PageObjectFactory.getSupplyLinePage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    testWebDriver.waitForAjax();
    homePage.navigateManageFacility();
    assertFalse(homePage.isSupplyLineTabDisplayed());
    homePage.logout();

    dbWrapper.assignRight("Admin", "MANAGE_SUPPLY_LINE");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToSupplyLine();
    assertTrue(homePage.isSupplyLineTabDisplayed());

    assertEquals("Search supply line", supplyLinePage.getSearchSupplyLineLabel());
    assertTrue(supplyLinePage.isAddNewButtonDisplayed());
    assertEquals("Supplying facility", supplyLinePage.getSelectedSearchOption());
    assertTrue(supplyLinePage.isSearchIconDisplayed());
  }

  @Test(groups = {"admin"})
  public void testSupplyLineSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPPLY_LINE");
    dbWrapper.assignRight("Admin", "UPLOADS");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadSupplyLines("QA_supplyLines21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    homePage.navigateToSupplyLine();
    searchSupplyLine("V");
    assertEquals("11 matches found for 'V'", supplyLinePage.getNResultsMessage());
    searchSupplyLine("A");
    assertEquals("21 matches found for 'A'", supplyLinePage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(21, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupplyingFacilityOrderOnPage(asList("central Hospital", "central Hospital", "central Hospital",
      "central Hospital", "central Hospital", "central Hospital", "central Hospital", "central Hospital", "central Hospital",
      "central Hospital"));
    verifySupervisoryNodesOrderOnPage(asList("Node1", "node2", "node2", "node2", "node3", "Node4", "Node4",
      "Node5", "Node6", "Node6"));
    verifyProgramsOrderOnPage(asList("ESSENTIAL MEDICINES", "HIV", "malaria", "TB", "ESSENTIAL MEDICINES", "HIV",
      "malaria", "ESSENTIAL MEDICINES", "HIV", "malaria"));

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupplyingFacilityOrderOnPage(asList("Village Dispensary", "Village Dispensary", "Village Dispensary",
      "Village Dispensary", "Village Dispensary", "Village Dispensary", "Village Dispensary", "Village Dispensary",
      "Village Dispensary", "Village Dispensary"));
    verifySupervisoryNodesOrderOnPage(asList("Node1", "Node1", "Node1", "node2", "node3", "node3", "node3",
      "Node4", "Node5", "Node5"));
    verifyProgramsOrderOnPage(asList("HIV", "malaria", "TB", "ESSENTIAL MEDICINES", "HIV",
      "malaria", "TB", "ESSENTIAL MEDICINES", "HIV", "malaria"));

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifySupplyingFacilityOrderOnPage(asList("Village Dispensary"));
    verifySupervisoryNodesOrderOnPage(asList("Node6"));
    verifyProgramsOrderOnPage(asList("ESSENTIAL MEDICINES"));

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupplyingFacilityOrderOnPage(asList("central Hospital", "central Hospital", "central Hospital",
      "central Hospital", "central Hospital", "central Hospital", "central Hospital", "central Hospital", "central Hospital",
      "central Hospital"));
    verifySupervisoryNodesOrderOnPage(asList("Node1", "node2", "node2", "node2", "node3", "Node4", "Node4",
      "Node5", "Node6", "Node6"));
    verifyProgramsOrderOnPage(asList("ESSENTIAL MEDICINES", "HIV", "malaria", "TB", "ESSENTIAL MEDICINES", "HIV",
      "malaria", "ESSENTIAL MEDICINES", "HIV", "malaria"));

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

    supplyLinePage.closeSearchResults();
      testWebDriver.sleep(1000);
    assertFalse(supplyLinePage.isProgramHeaderPresent());
  }

  @Test(groups = {"admin"})
  public void testProgramSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPPLY_LINE");
    dbWrapper.assignRight("Admin", "UPLOADS");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadSupplyLines("QA_supplyLines21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    supplyLinePage = homePage.navigateToSupplyLine();
    supplyLinePage.clickSearchOptionButton();
    supplyLinePage.selectProgramAsSearchOption();
    assertEquals("Program", supplyLinePage.getSelectedSearchOption());

    searchSupplyLine("hiv");
    assertEquals("6 matches found for 'hiv'", supplyLinePage.getNResultsMessage());

    assertEquals("Program", supplyLinePage.getProgramHeader());
    assertEquals("Supplying facility", supplyLinePage.getSupplyingFacilityHeader());
    assertEquals("Supervisory node name", supplyLinePage.getSupervisoryNodeHeader());
    assertEquals("Description", supplyLinePage.getDescriptionHeader());

    verifyNumberOFPageLinksDisplayed(6, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(6);

    verifySupplyingFacilityOrderOnPage(asList("central Hospital", "central Hospital", "central Hospital",
      "Village Dispensary", "Village Dispensary", "Village Dispensary"));
    verifySupervisoryNodesOrderOnPage(asList("node2", "Node4", "Node6", "Node1", "node3", "Node5"));
    verifyProgramsOrderOnPage(asList("HIV", "HIV", "HIV", "HIV", "HIV", "HIV"));
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPPLY_LINE");
    dbWrapper.assignRight("Admin", "UPLOADS");
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F10");
    dbWrapper.updateFieldValue("facilities", "active", "false", "code", "F11");
    dbWrapper.updateFieldValue("programs", "active", "false", "code", "HIV");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadSupplyLines("QA_supplyLines21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    supplyLinePage = homePage.navigateToSupplyLine();
    supplyLinePage.clickSearchOptionButton();
    supplyLinePage.selectSupervisoryNodeAsSearchOption();
    assertEquals("Supervisory node", supplyLinePage.getSelectedSearchOption());

    searchSupplyLine("node6");
    assertEquals("3 matches found for 'node6'", supplyLinePage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(3, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(3);

    verifySupplyingFacilityOrderOnPage(asList("central Hospital", "central Hospital", "Village Dispensary"));
    verifySupervisoryNodesOrderOnPage(asList("Node6", "Node6", "Node6"));
    verifyProgramsOrderOnPage(asList("HIV", "malaria", "ESSENTIAL MEDICINES"));

    supplyLinePage.clickSearchOptionButton();
    supplyLinePage.selectSupplyLineAsSearchOption();
    supplyLinePage.clickSearchIcon();
    assertTrue(supplyLinePage.isNoResultMessageDisplayed());
    dbWrapper.updateFieldValue("programs", "active", "true", "code", "HIV");
  }

  @Test(groups = {"admin"})
  public void testAddAndSearchSupplyLine() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPPLY_LINE");
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F10");
    dbWrapper.updateFieldValue("facilities", "active", "false", "code", "F11");
    dbWrapper.updateFieldValue("programs", "active", "false", "code", "HIV");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
      supplyLinePage = homePage.navigateToSupplyLine();
    supplyLinePage.clickAddNewButton();

    assertEquals("Add supply line", supplyLinePage.getAddSupplyLineHeader());
    assertEquals("Program *", supplyLinePage.getProgramLabel());
    assertEquals("Facility exports orders? *", supplyLinePage.getFacilityExportOrdersLabel());
    assertEquals("Supplying facility *", supplyLinePage.getSupplyingFacilityLabel());
    assertEquals("Supervisory node *", supplyLinePage.getSupervisoryNodeLabel());
    assertEquals("Description", supplyLinePage.getDescriptionLabel());

    supplyLinePage.enterValuesInSupplyLineFields("Malaria", "node", false);
    supplyLinePage.clickSupplyingFacilityField();
    supplyLinePage.searchFacility("F11");
    testWebDriver.waitForAjax();
    supplyLinePage.clickFilterButton();
    testWebDriver.waitForAjax();
    supplyLinePage.selectFacilityType("Warehouse");
    supplyLinePage.searchGeographicZone("Root");
    supplyLinePage.selectGeographicZoneResult(1);
    supplyLinePage.clickApplyFilterButton();
    testWebDriver.sleep(500);
    supplyLinePage.selectFacility(1);

    supplyLinePage.clickSaveButton();
    assertEquals("Supply line created successfully", supplyLinePage.getSuccessMessage());

    searchSupplyLine("central");
    assertEquals("malaria", supplyLinePage.getProgram(1));
    assertEquals("central Hospital", supplyLinePage.getSupplyingFacility(1));
    assertEquals("Node1", supplyLinePage.getSupervisoryNode(1));
    assertEquals("", supplyLinePage.getDescription(1));
  }

  @Test(groups = {"admin"})
  public void testEditAndSearchSupplyLine() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPPLY_LINE");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supplyLinePage = homePage.navigateToSupplyLine();
    searchSupplyLine("village");
    assertTrue(supplyLinePage.isNoResultMessageDisplayed());
    supplyLinePage.clickAddNewButton();

    supplyLinePage.enterValuesInSupplyLineFields("HIV", "node", false);
    supplyLinePage.clickSupplyingFacilityField();
    supplyLinePage.searchFacility("F11");
    testWebDriver.waitForAjax();
    supplyLinePage.selectFacility(1);
    supplyLinePage.clickSaveButton();

    assertEquals("Supply line created successfully", supplyLinePage.getSuccessMessage());
    supplyLinePage.clickViewHereLink();

    supplyLinePage.selectProgram("HIV");
    supplyLinePage.selectExportOrderFlag(true);
    supplyLinePage.enterDescription("Description");

    supplyLinePage.clickSupplyingFacilityField();
    supplyLinePage.searchFacility("F11A");
    testWebDriver.waitForAjax();
    supplyLinePage.selectFacility(1);
    supplyLinePage.clickSaveButton();

    assertEquals("Supply line updated successfully", supplyLinePage.getSuccessMessage());
    supplyLinePage.clickViewHereLink();

    assertEquals("Edit supply line", supplyLinePage.getEditSupplyLineHeader());
    supplyLinePage.clickClearNodeSearchResult();
    supplyLinePage.searchSupervisoryNode("node");
    supplyLinePage.selectSupervisoryNodeResult(1);

    supplyLinePage.selectProgram("Malaria");
    supplyLinePage.selectExportOrderFlag(false);
    supplyLinePage.clickCancelButton();

    searchSupplyLine("Village");
    assertTrue(supplyLinePage.isOneResultMessageDisplayed());

    assertEquals("HIV", supplyLinePage.getProgram(1));
    assertEquals("Village Dispensary", supplyLinePage.getSupplyingFacility(1));
    assertEquals("Node1", supplyLinePage.getSupervisoryNode(1));
    assertEquals("Description", supplyLinePage.getDescription(1));
  }

  @Test(groups = {"admin"})
  public void testValidationsOnSupplyLine() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPPLY_LINE");
    dbWrapper.insertSupplyLines("N1", "HIV", "F11", true);
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supplyLinePage = homePage.navigateToSupplyLine();
    supplyLinePage.clickAddNewButton();

    supplyLinePage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals("There are some errors in the form. Please resolve them.", supplyLinePage.getSaveErrorMessage());

    supplyLinePage.selectProgram("HIV");
    supplyLinePage.enterDescription("new");
    supplyLinePage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals("There are some errors in the form. Please resolve them.", supplyLinePage.getSaveErrorMessage());

    supplyLinePage.searchSupervisoryNode("node");
    supplyLinePage.selectSupervisoryNodeResult(1);
    supplyLinePage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals("There are some errors in the form. Please resolve them.", supplyLinePage.getSaveErrorMessage());

    supplyLinePage.selectExportOrderFlag(true);
    supplyLinePage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals("There are some errors in the form. Please resolve them.", supplyLinePage.getSaveErrorMessage());

    supplyLinePage.clickSupplyingFacilityField();
    supplyLinePage.searchFacility("F11");
    supplyLinePage.selectFacility(1);
    supplyLinePage.clickSaveButton();
    testWebDriver.sleep(1000);
    assertEquals("Supervisory node and Program already has a supplying facility assigned to it.", supplyLinePage.getSaveErrorMessage());

    supplyLinePage.clickClearNodeSearchResult();
    supplyLinePage.clickSaveButton();
    assertEquals("There are some errors in the form. Please resolve them.", supplyLinePage.getSaveErrorMessage());
    supplyLinePage.clickCancelButton();

    assertTrue(supplyLinePage.isAddNewButtonDisplayed());
    assertTrue(supplyLinePage.isSearchIconDisplayed());
    supplyLinePage.clickSearchOptionButton();
    supplyLinePage.selectProgramAsSearchOption();
    supplyLinePage.enterSearchParameter("Hiv");
    supplyLinePage.clickSearchIcon();
    assertTrue(supplyLinePage.isOneResultMessageDisplayed());
    assertNotEquals("new", supplyLinePage.getDescription(1));
  }

  public void searchSupplyLine(String searchParameter) {
    supplyLinePage.enterSearchParameter(searchParameter);
    supplyLinePage.clickSearchIcon();
    testWebDriver.waitForAjax();
  }

  private void verifyProgramsOrderOnPage(List<String> programsName) {
    for (int i = 1; i < programsName.size(); i++) {
      assertEquals(programsName.get(i - 1), supplyLinePage.getProgram(i));
    }
  }

  private void verifySupplyingFacilityOrderOnPage(List<String> facilityName) {
    for (int i = 1; i < facilityName.size(); i++) {
      assertEquals(facilityName.get(i - 1), supplyLinePage.getSupplyingFacility(i));
    }
  }

  private void verifySupervisoryNodesOrderOnPage(List<String> nodeName) {
    for (int i = 1; i < nodeName.size(); i++) {
      assertEquals(nodeName.get(i - 1), supplyLinePage.getSupervisoryNode(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, supplyLinePage.getSizeOfResultsTable());
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      testWebDriver.sleep(1000);
      homePage.logout(baseUrlGlobal);
    dbWrapper.updateFieldValue("programs", "name", "MALARIA", "code", "MALARIA");
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }
}
