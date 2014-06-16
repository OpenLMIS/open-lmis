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
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertNotEquals;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ManageRequisitionGroups extends TestCaseHelper {

  LoginPage loginPage;
  RequisitionGroupPage requisitionGroupPage;

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
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
    dbWrapper.insertFacilities("F10", "F100");
    dbWrapper.insertFacilities("F11", "F10A");

    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    requisitionGroupPage = PageObjectFactory.getRequisitionGroupPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateManageFacility();

    assertFalse(homePage.isRequisitionGroupTabDisplayed());
    homePage.logout();

    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateManageFacility();
    assertTrue(homePage.isRequisitionGroupTabDisplayed());
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    assertTrue(requisitionGroupPage.isAddNewButtonDisplayed());
    assertEquals("Requisition group", requisitionGroupPage.getSelectedSearchOption());
    assertTrue(requisitionGroupPage.isSearchIconDisplayed());
    assertTrue(homePage.isRequisitionGroupTabDisplayed());
    assertEquals("Search requisition group", requisitionGroupPage.getSearchRequisitionGroupLabel());
    assertFalse(requisitionGroupPage.isResultDisplayed());
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSearch() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    search("re");
    assertEquals("2 matches found for 're'", requisitionGroupPage.getNResultsMessage());
    assertEquals("Requisition group name", requisitionGroupPage.getRequisitionGroupHeader());
    assertEquals("Code", requisitionGroupPage.getCodeHeader());
    assertEquals("Supervisory node name", requisitionGroupPage.getSupervisoryNodeHeader());
    assertEquals("Facilities count", requisitionGroupPage.getFacilityCountHeader());

    assertEquals("Requisition Group 2", requisitionGroupPage.getRequisitionGroupName(1));
    assertEquals("RG2", requisitionGroupPage.getRequisitionGroupCode(1));
    assertEquals("Super1", requisitionGroupPage.getSupervisoryNodeName(1));
    assertEquals("", requisitionGroupPage.getFacilityCount(1));

    dbWrapper.updateFieldValue("requisition_groups", "name", "rg", "code", "RG2");
    requisitionGroupPage.clickSearchIcon();
    assertEquals("1 match found for 're'", requisitionGroupPage.getOneResultsMessage());
    assertEquals("Requisition Group 1", requisitionGroupPage.getRequisitionGroupName(1));
    assertEquals("RG1", requisitionGroupPage.getRequisitionGroupCode(1));
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.assignRight("Admin", "UPLOADS");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadRequisitionGroup("QA_RequisitionGroups21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    dbWrapper.insertRequisitionGroupMembersTestData();

    homePage.navigateToRequisitionGroupPage();
    search("Requisition Group 1");
    assertEquals("11 matches found for 'Requisition Group 1'", requisitionGroupPage.getNResultsMessage());
    search("Requisition Group ");
    assertEquals("22 matches found for 'Requisition Group'", requisitionGroupPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(22, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1",
      "Super1", "Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});
    String[] counts = new String[]{"2", "2", "1", "1", "1", "2", "", "1", "1", "2"};
    for (int i = 1; i < counts.length; i++) {
      assertEquals(counts[i - 1], requisitionGroupPage.getFacilityCount(i));
    }

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super2", "Super2", "Super2", "Super2", "Super2", "Super2", "Super2",
      "Super2", "Super2"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 9", "Requisition Group 1", "Requisition Group 10",
      "Requisition Group 14", "Requisition Group 17", "Requisition Group 19", "Requisition Group 2", "Requisition Group 20",
      "Requisition Group 20", "Requisition Group 3"});

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super2", "Super2"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 6", "Requisition Group 8"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1",
      "Super1", "Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(2);

    navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    requisitionGroupPage.closeSearchResults();
    assertFalse(requisitionGroupPage.isRequisitionGroupHeaderDisplayed());
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSupervisoryNodeSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.assignRight("Admin", "UPLOADS");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadRequisitionGroup("QA_RequisitionGroups21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    dbWrapper.insertRequisitionGroupMembersTestData();

    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    assertEquals("Requisition group", requisitionGroupPage.getSelectedSearchOption());
    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectSupervisoryNodeAsSearchOption();
    search("Super2");
    assertEquals("11 matches found for 'Super2'", requisitionGroupPage.getNResultsMessage());
    search("Super1");
    assertEquals("11 matches found for 'Super1'", requisitionGroupPage.getNResultsMessage());
    assertEquals("Supervisory node", requisitionGroupPage.getSelectedSearchOption());

    verifyNumberOFPageLinksDisplayed(11, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1", "Super1",
      "Super1", "Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Super1"});
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 9"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyRequisitionGroupNameOrderOnPage(new String[]{"Requisition Group 11", "Requisition Group 12", "Requisition Group 13",
      "Requisition Group 15", "Requisition Group 16", "Requisition Group 18", "Requisition Group 20", "Requisition Group 4",
      "Requisition Group 5", "Requisition Group 7"});

    navigateToLastPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);

    navigateToPreviousPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
  }

  @Test(groups = {"admin"})
  public void testRequisitionGroupSearchWhenNoResults() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    assertEquals("Requisition group", requisitionGroupPage.getSelectedSearchOption());
    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectSupervisoryNodeAsSearchOption();
    assertEquals("Supervisory node", requisitionGroupPage.getSelectedSearchOption());
    search("RE");
    assertTrue(requisitionGroupPage.isNoResultMessageDisplayed());

    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectRequisitionGroupAsSearchOption();
    assertTrue(requisitionGroupPage.isNoResultMessageDisplayed());

    dbWrapper.insertRequisitionGroup("RG1", "Req Group", "N1");
    testWebDriver.refresh();
    search("RE");
    assertTrue(requisitionGroupPage.isOneResultMessageDisplayed());
    assertEquals("Req Group", requisitionGroupPage.getRequisitionGroupName(1));
    assertEquals("RG1", requisitionGroupPage.getRequisitionGroupCode(1));
    assertEquals("Super1", requisitionGroupPage.getSupervisoryNodeName(1));
    assertEquals("", requisitionGroupPage.getFacilityCount(1));

    requisitionGroupPage.clickSearchOptionButton();
    requisitionGroupPage.selectSupervisoryNodeAsSearchOption();
    requisitionGroupPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertTrue(requisitionGroupPage.isNoResultMessageDisplayed());
  }

  @Test(groups = {"admin"})
  public void testValidationsOnAddNewRequisitionGroup() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertRequisitionGroup("RG1", "Requisition Group 1", "N1");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    requisitionGroupPage.clickAddNewButton();
    requisitionGroupPage.clickSaveButton();
    assertEquals("There are some errors in the form. Please resolve them.", requisitionGroupPage.getErrorMessage());

    requisitionGroupPage.enterRequisitionGroupCode("RG1");
    requisitionGroupPage.enterRequisitionGroupName("Requisition Group 1");
    requisitionGroupPage.enterParameterToSearchSupervisoryNode("Node");
    testWebDriver.waitForAjax();
    requisitionGroupPage.selectSupervisoryNodeSearchResult(1);
    requisitionGroupPage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals("Duplicate Requisition Group Code", requisitionGroupPage.getErrorMessage());

    requisitionGroupPage.enterRequisitionGroupCode("RG2");
    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F10");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    requisitionGroupPage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals("No Program(s) mapped for Requisition Group", requisitionGroupPage.getErrorMessage());
  }

  @Test(groups = {"admin"})
  public void testAddNewRequisitionGroupWithoutMembers() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSchedule("M", "monthly", "monthly");
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11A", "F11B", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11C", "F11D", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11E", "F11F", 2, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11G", "F11H", 2, 3);

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    requisitionGroupPage.clickAddNewButton();
    requisitionGroupPage.enterRequisitionGroupCode("RG5");
    requisitionGroupPage.enterRequisitionGroupName("Requisition Group 5");
    requisitionGroupPage.enterParameterToSearchSupervisoryNode("node");
    testWebDriver.waitForAjax();
    assertEquals("Node1", requisitionGroupPage.getSupervisoryNodeSearchResult(1));
    assertEquals("Node2", requisitionGroupPage.getSupervisoryNodeSearchResult(2));
    requisitionGroupPage.selectSupervisoryNodeSearchResult(1);
    requisitionGroupPage.clickSaveButton();
    assertEquals("Requisition Group \"Requisition Group 5\" created successfully.   View Here", requisitionGroupPage.getSuccessMessage());

    dbWrapper.insertRequisitionGroupProgramScheduleForProgramAfterDelete("RG5", "HIV", "M");

    search("Requisition Group 5");
    requisitionGroupPage.clickManageRequisitionGroupSearchResult(1);
    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F11");
    assertEquals("9 matches found for 'F11'", requisitionGroupPage.getNFacilityResultsMessage());
    requisitionGroupPage.clickFilterButton();
    testWebDriver.waitForAjax();
    requisitionGroupPage.selectFacilityType("Warehouse");
    requisitionGroupPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();
    assertEquals("4 matches found for 'F11'", requisitionGroupPage.getNFacilityResultsMessage());
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.checkFacilityToBeAssociated(2);
    requisitionGroupPage.checkFacilityToBeAssociated(3);
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.clickAddMembersButton();

    requisitionGroupPage.checkFacilityToBeAssociated(4);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    assertEquals("Facilities added successfully", requisitionGroupPage.getFacilityAddedMessage());

    requisitionGroupPage.clickSaveButton();
    assertEquals("Requisition Group \"Requisition Group 5\" updated successfully.   View Here", requisitionGroupPage.getSuccessMessage());
    assertEquals("4", requisitionGroupPage.getFacilityCount(1));
  }

  @Test(groups = {"admin"})
  public void testUpdateRequisitionGroup() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11A", "F11B", 1, 3);
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertRequisitionGroup("RG1", "Requisition Group 1", "N1");
    dbWrapper.insertRequisitionGroupMember("RG1", "F10");
    dbWrapper.insertSchedule("M", "monthly", "monthly");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgramAfterDelete("RG1", "HIV", "M");
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F12A", "F12B", 1, 3);

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    search("Requisition Group 1");
    requisitionGroupPage.clickManageRequisitionGroupSearchResult(1);

    requisitionGroupPage.clickClearNodeSearchButton();
    requisitionGroupPage.enterParameterToSearchSupervisoryNode("Node");
    testWebDriver.waitForAjax();
    requisitionGroupPage.selectSupervisoryNodeSearchResult(2);

    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F10");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    assertEquals("Facility \"Village Dispensary\" is already added", requisitionGroupPage.getDuplicateFacilityMessage());

    requisitionGroupPage.searchFacility("F12A");
    requisitionGroupPage.checkFacilityToBeAssociated(1);

    requisitionGroupPage.searchFacility("F12B");
    assertFalse(requisitionGroupPage.isAddSelectedFacilityButtonEnabled());
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    assertTrue(requisitionGroupPage.isAddSelectedFacilityButtonEnabled());
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    assertFalse(requisitionGroupPage.isAddSelectedFacilityButtonEnabled());
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    assertTrue(requisitionGroupPage.isAddSelectedFacilityButtonEnabled());

    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("Village Dispensary");
    requisitionGroupPage.checkFacilityToBeAssociated(2);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    assertFalse(requisitionGroupPage.isSearchFacilityIconDisplayed());
    assertNotEquals("F12A", requisitionGroupPage.getMemberFacilityCode(1));
    assertNotEquals("F12A", requisitionGroupPage.getMemberFacilityCode(2));
    assertNotEquals("F12A", requisitionGroupPage.getMemberFacilityCode(3));

    requisitionGroupPage.clickSaveButton();
    assertEquals("Requisition Group \"Requisition Group 1\" updated successfully.   View Here", requisitionGroupPage.getSuccessMessage());
    assertEquals("3", requisitionGroupPage.getFacilityCount(1));
    assertEquals("Node2", requisitionGroupPage.getSupervisoryNodeName(1));
  }

  @Test(groups = {"admin"})
  public void testCancelUpdateRequisitionGroup() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertRequisitionGroup("RG1", "Requisition Group 1", "N1");
    dbWrapper.insertRequisitionGroupMember("RG1", "F10");
    dbWrapper.insertRequisitionGroupMember("RG1", "F11");
    dbWrapper.updateFieldValue("facilities", "enabled", "f", "code", "F11");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();

    search("Requisition Group 1");
    testWebDriver.waitForAjax();
    assertEquals("1", requisitionGroupPage.getFacilityCount(1));
    requisitionGroupPage.clickManageRequisitionGroupSearchResult(1);

    requisitionGroupPage.enterRequisitionGroupName("ReqGrp");
    requisitionGroupPage.clickMembersAccordionLink();

    assertEquals("F10 - Village Dispensary", requisitionGroupPage.getMemberFacilityCode(1));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(1));
    assertTrue(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(1));

    assertEquals("F11 - Village Dispensary", requisitionGroupPage.getMemberFacilityCode(2));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(2));
    assertFalse(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(2));

    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F10");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickCloseButton();
    assertTrue(requisitionGroupPage.isSearchFacilityIconDisplayed());
    assertFalse(requisitionGroupPage.isAddSelectedFacilityButtonEnabled());

    requisitionGroupPage.clickCancelButton();
    testWebDriver.waitForAjax();
    assertEquals("Requisition Group 1", requisitionGroupPage.getRequisitionGroupName(1));
  }

  @Test(groups = {"admin"})
  public void testValidationsOnAssociatedFacilitySearch() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertFacilities("F111B", "F1C");
    dbWrapper.insertFacilities("f111D", "f1E");
    dbWrapper.insertFacilities("F111F", "F1G");
    dbWrapper.insertFacilities("f111H", "f1I");
    dbWrapper.insertFacilities("F111J", "F1K");
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F1K");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    requisitionGroupPage.clickAddNewButton();

    assertEquals("Add requisition group", requisitionGroupPage.getAddRequisitionGroupHeader());
    assertEquals("Code", requisitionGroupPage.getCodeLabel());
    assertEquals("Name", requisitionGroupPage.getNameLabel());
//    assertEquals("Supervisory node", requisitionGroupPage.getSupervisoryNodeLabel());
    assertEquals("Description", requisitionGroupPage.getDescriptionLabel());
    assertEquals("Program and Schedules", requisitionGroupPage.getProgramsAndScheduleLabel());
    assertEquals("Members", requisitionGroupPage.getMembersLabel());

    requisitionGroupPage.clickMembersAccordionLink();
    assertTrue(requisitionGroupPage.isAddMembersButtonDisplayed());
    assertFalse(requisitionGroupPage.isSearchFacilityIconDisplayed());

    requisitionGroupPage.clickCollapseAll();
    assertFalse(requisitionGroupPage.isAddMembersButtonDisplayed());
    assertFalse(requisitionGroupPage.isSearchFacilityIconDisplayed());

    requisitionGroupPage.clickExpandAll();
    assertTrue(requisitionGroupPage.isAddMembersButtonDisplayed());
    assertFalse(requisitionGroupPage.isSearchFacilityIconDisplayed());

    requisitionGroupPage.clickMembersAccordionLink();
    assertFalse(requisitionGroupPage.isAddMembersButtonDisplayed());

    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    assertFalse(requisitionGroupPage.isAddSelectedFacilityButtonEnabled());
    requisitionGroupPage.searchFacility("F1");
    assertTrue(requisitionGroupPage.isFacilitySearchListDisplayed());
    assertEquals("Too many results found. Please refine your search.", requisitionGroupPage.getTooManyFacilitySearchResultMessage());

    requisitionGroupPage.searchFacility("F990");

    assertEquals("No matches found for 'F990'", requisitionGroupPage.getNoFacilityResultMessage());

    requisitionGroupPage.searchFacility("F111");
    assertEquals("F111B - Village Dispensary", requisitionGroupPage.getFacilityResult(1));
    assertEquals("f111D - Village Dispensary", requisitionGroupPage.getFacilityResult(2));
    assertEquals("F111F - Village Dispensary", requisitionGroupPage.getFacilityResult(3));
    assertEquals("f111H - Village Dispensary", requisitionGroupPage.getFacilityResult(4));
    assertEquals("F111J - Village Dispensary", requisitionGroupPage.getFacilityResult(5));

    requisitionGroupPage.searchFacility("F1K");
    assertEquals("No matches found for 'F1K'", requisitionGroupPage.getNoFacilityResultMessage());

    requisitionGroupPage.searchFacility("F111B");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();

    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F111B");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    assertEquals("Facility \"Village Dispensary\" is already added", requisitionGroupPage.getDuplicateFacilityMessage());
    assertTrue(requisitionGroupPage.isSearchFacilityIconDisplayed());
  }

  @Test(groups = {"admin"})
  public void testFacilityMappingToRequisitionGroupsWithSameScheduleAndProgram() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertRequisitionGroupWithoutDelete("RG1", "Requisition Group 1", "N1");
    dbWrapper.insertRequisitionGroupWithoutDelete("RG2", "Requisition Group 2", "N1");
    dbWrapper.insertRequisitionGroupWithoutDelete("RG3", "Requisition Group 3", "N1");
    dbWrapper.insertSchedule("M", "monthly", "monthly");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgramWithoutDelete("RG1", "HIV", "M");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgramWithoutDelete("RG2", "MALARIA", "M");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgramWithoutDelete("RG3", "HIV", "M");
    dbWrapper.insertRequisitionGroupMember("RG1", "F10");
    dbWrapper.insertRequisitionGroupMember("RG2", "F11");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    search("Re");
    requisitionGroupPage.clickManageRequisitionGroupSearchResult(3);

    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F10");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    requisitionGroupPage.clickSaveButton();
    assertEquals("Facility F10 is already assigned to Requisition Group RG1 running same program HIV", requisitionGroupPage.getErrorMessage());
    requisitionGroupPage.removeRequisitionMember(1);

    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F11");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();
    requisitionGroupPage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals("Requisition Group \"Requisition Group 3\" updated successfully.   View Here", requisitionGroupPage.getSuccessMessage());
  }

  @Test(groups = {"admin"})
  public void testRemoveRequisitionGroupMemberAndSorting() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertRequisitionGroup("RG1", "Requisition Group 1", "N1");
    dbWrapper.insertSchedule("M", "monthly", "monthly");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgramWithoutDelete("RG1", "HIV", "M");
    dbWrapper.insertRequisitionGroupMember("RG1", "F10");
    dbWrapper.insertRequisitionGroupMember("RG1", "F11");
    dbWrapper.insertRequisitionGroupMember("RG1", "F100");
    dbWrapper.insertRequisitionGroupMember("RG1", "F10A");
    dbWrapper.updateFieldValue("facilities", "enabled", "f", "code", "F11");
    dbWrapper.insertFacilities("F1B", "F1C");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    search("Requisition Group 1");
    testWebDriver.waitForAjax();
    assertEquals("3", requisitionGroupPage.getFacilityCount(1));
    requisitionGroupPage.clickManageRequisitionGroupSearchResult(1);

    requisitionGroupPage.clickMembersAccordionLink();

    assertEquals("Associated facility", requisitionGroupPage.getFacilityHeader());
    assertEquals("Geographic Zone", requisitionGroupPage.getGeoZoneHeader());
    assertEquals("Facility type", requisitionGroupPage.getFacilityTypeHeader());
    assertEquals("Enabled", requisitionGroupPage.getFacilityEnabledHeader());

    assertEquals("F10 - Village Dispensary", requisitionGroupPage.getMemberFacilityCode(1));
    assertEquals("Ngorongoro", requisitionGroupPage.getMemberGeoZone(1));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(1));
    assertTrue(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(1));

    assertEquals("F100 - Central Hospital", requisitionGroupPage.getMemberFacilityCode(2));
    assertEquals("Ngorongoro", requisitionGroupPage.getMemberGeoZone(2));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(2));
    assertTrue(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(2));

    assertEquals("F10A - Central Hospital", requisitionGroupPage.getMemberFacilityCode(3));
    assertEquals("Ngorongoro", requisitionGroupPage.getMemberGeoZone(3));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(3));
    assertTrue(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(3));

    assertEquals("F11 - Village Dispensary", requisitionGroupPage.getMemberFacilityCode(4));
    assertEquals("Ngorongoro", requisitionGroupPage.getMemberGeoZone(4));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(4));
    assertFalse(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(4));

    requisitionGroupPage.removeRequisitionMember(2);
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.removeRequisitionMember(2);
    requisitionGroupPage.removeRequisitionMember(2);

    requisitionGroupPage.searchFacility("F11");
    assertTrue(requisitionGroupPage.isNoFacilityResultMessageDisplayed());

    requisitionGroupPage.searchFacility("F10A");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();

    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchFacility("F1B");
    requisitionGroupPage.checkFacilityToBeAssociated(1);
    requisitionGroupPage.clickOnAddSelectedFacilityButton();

    assertEquals("F10 - Village Dispensary", requisitionGroupPage.getMemberFacilityCode(1));
    assertEquals("Ngorongoro", requisitionGroupPage.getMemberGeoZone(1));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(1));
    assertTrue(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(1));

    assertEquals("F10A - Central Hospital", requisitionGroupPage.getMemberFacilityCode(2));
    assertEquals("Ngorongoro", requisitionGroupPage.getMemberGeoZone(2));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(2));
    assertTrue(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(2));

    assertEquals("F1B - Village Dispensary", requisitionGroupPage.getMemberFacilityCode(3));
    assertEquals("Ngorongoro", requisitionGroupPage.getMemberGeoZone(3));
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getMemberFacilityType(3));
    assertTrue(requisitionGroupPage.isMemberFacilityEnableFlagDisplayed(3));

    requisitionGroupPage.clickSaveButton();
    testWebDriver.waitForAjax();
    assertEquals("Requisition Group 1", requisitionGroupPage.getRequisitionGroupName(1));
    assertEquals("3", requisitionGroupPage.getFacilityCount(1));
  }

  public void search(String searchParameter) {
    requisitionGroupPage.enterSearchParameter(searchParameter);
    requisitionGroupPage.clickSearchIcon();
    testWebDriver.waitForAjax();
  }

  private void verifyRequisitionGroupNameOrderOnPage(String[] requisitionGroupNames) {
    for (int i = 1; i < requisitionGroupNames.length; i++) {
      assertEquals(requisitionGroupNames[i - 1], requisitionGroupPage.getRequisitionGroupName(i));
    }
  }

  private void verifySupervisoryNodeNameOrderOnPage(String[] supervisoryNodeNames) {
    for (int i = 1; i < supervisoryNodeNames.length; i++) {
      assertEquals(supervisoryNodeNames[i - 1], requisitionGroupPage.getSupervisoryNodeName(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, requisitionGroupPage.getRequisitionGroupSearchResultsTableSize());
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
}
