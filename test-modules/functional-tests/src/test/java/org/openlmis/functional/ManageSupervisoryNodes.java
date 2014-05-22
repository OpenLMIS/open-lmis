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

import com.thoughtworks.selenium.SeleneseTestBase;
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
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class ManageSupervisoryNodes extends TestCaseHelper {

  LoginPage loginPage;

  public static final String USER = "user";
  public static final String ADMIN = "admin";
  public static final String PASSWORD = "password";

  public Map<String, String> testData = new HashMap<String, String>() {{
    put(USER, "fieldCoordinator");
    put(PASSWORD, "Admin123");
    put(ADMIN, "Admin123");
  }};

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.insertFacilities("F10", "F11");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    homePage.navigateToUser();
    homePage.verifyAdminTabs();
    assertFalse(homePage.isSupervisoryNodeTabDisplayed());
    homePage.logout();
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToUser();
    assertTrue(homePage.isSupervisoryNodeTabDisplayed());
    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    assertEquals("Search supervisory node", supervisoryNodesPage.getSearchSupervisoryNodeLabel());
    assertTrue(supervisoryNodesPage.isAddNewButtonDisplayed());
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    assertTrue(supervisoryNodesPage.isSearchIconDisplayed());
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    homePage.verifyAdminTabs();
    assertTrue(homePage.isSupervisoryNodeTabDisplayed());
    assertEquals("Search supervisory node", supervisoryNodesPage.getSearchSupervisoryNodeLabel());
    assertTrue(supervisoryNodesPage.isAddNewButtonDisplayed());
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    //assertFalse(supervisoryNodesPage.isNoResultMessageDisplayed());
    //assertFalse(supervisoryNodesPage.isNResultsMessageDisplayed());
    //assertFalse(supervisoryNodesPage.isOneResultMessageDisplayed());
    //assertFalse(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());

    searchNode("nod");
    assertEquals("3 matches found for 'nod'", supervisoryNodesPage.getNResultsMessage());
    assertEquals("Supervisory Node Name", supervisoryNodesPage.getSupervisoryNodeHeader());
    assertEquals("Code", supervisoryNodesPage.getCodeHeader());
    assertEquals("Facility", supervisoryNodesPage.getFacilityHeader());
    assertEquals("Parent", supervisoryNodesPage.getParentHeader());

    assertEquals("Node3", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N3", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
    assertEquals("Node2", supervisoryNodesPage.getParent(1));

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadSupervisoryNodes("QA_supervisoryNodes21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    homePage.navigateToSupervisoryNodes();
    assertEquals("Search supervisory node", supervisoryNodesPage.getSearchSupervisoryNodeLabel());
    searchNode("Approval Point");
    assertEquals("19 matches found for 'Approval Point'", supervisoryNodesPage.getNResultsMessage());
    searchNode("Ap");
    assertEquals("21 matches found for 'Ap'", supervisoryNodesPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(21, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "approval Point 4", "approval Point 4",
      "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6", "approval Point 7"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8", "Approval Point 9"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 8", "Approval Point 8", "Approval Point 9", "Node1", "Node1", "Node1",
      "Node1", "Node1", "Node1", "Node3"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 11", "Approval Point 13", "Approval Point 13", "Approval Point 10",
      "Approval Point 13", "Approval Point 3", "approval Point 7", "Aproval Point 3", "Aproval Point 4", "Approval Point 13"});

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifyParentNameOrderOnPage(new String[]{"Node3"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "approval Point 4", "approval Point 4",
      "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6", "approval Point 7"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8", "Approval Point 9"});

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

    supervisoryNodesPage.closeSearchResults();
    assertFalse(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeParentSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadSupervisoryNodes("QA_supervisoryNodes21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    assertEquals("Supervisory node parent", supervisoryNodesPage.getSelectedSearchOption());

    searchNode("Approval Point");
    assertEquals("14 matches found for 'Approval Point'", supervisoryNodesPage.getNResultsMessage());
    searchNode("Ap");
    assertEquals("14 matches found for 'Ap'", supervisoryNodesPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(16, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "Approval Point 3", "approval Point 4",
      "approval Point 4", "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Node3", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(4);
    verifyParentNameOrderOnPage(new String[]{"approval Point 7", "Approval Point 8", "Approval Point 8", "Approval Point 9"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 9", "Approval Point 11", "Approval Point 13", "Approval Point 13"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "Approval Point 3", "approval Point 4",
      "approval Point 4", "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Node3", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8"});

    navigateToLastPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(4);

    navigateToPreviousPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeParentSearchWhenNoResults() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    assertEquals("Supervisory node parent", supervisoryNodesPage.getSelectedSearchOption());
    searchNode("nod");
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeAsSearchOption();
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    dbWrapper.insertSupervisoryNode("F10", "N2", "Node2", null);
    testWebDriver.refresh();
    searchNode("nod");
    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertEquals("Node2", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N2", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
    assertEquals("", supervisoryNodesPage.getParent(1));

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    supervisoryNodesPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());
  }


  @Test(groups = {"admin"})
  public void testAddNewSupervisoryNode() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.clickAddNewButton();
    addNewSupervisoryNode("N4", "Node 4", "This is Node 4", "Nod", "F10");
    supervisoryNodesPage.clickSaveButton();
    searchNode("Node 4"); //enter the one added
    assertEquals("Node 4", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N4", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
    supervisoryNodesPage.clickOnSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    searchNode("Node 4"); //previously added
    supervisoryNodesPage.clickAddNewButton();
    addNewSupervisoryNode("N5", "Node 5", "This is Node 5", "Nod", "F10");
    supervisoryNodesPage.clickSaveButton();
    searchNode("Node 5"); //enter the one added
    assertEquals("Node 5", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N5", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
  }

  @Test(groups = {"admin"})
  public void testSaveWithoutEnteringValuesInSupervisoryNode() throws SQLException{
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    supervisoryNodesPage.clickAddNewButton();
    supervisoryNodesPage.clickSaveButton();
    supervisoryNodesPage.verifyErrorMessage();
    supervisoryNodesPage.clickCancelButton();

    supervisoryNodesPage.isAddNewButtonDisplayed();
    supervisoryNodesPage.isSearchIconDisplayed();
  }

  @Test(groups = {"admin"})
  public void testWhetherDisabledFacilityIsVisible() throws SQLException{
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    ManageFacilityPage facilityPage = homePage.navigateManageFacility();

    facilityPage.searchFacility("F10");
    facilityPage.clickFacilityList("F10");
    facilityPage.disableFacility("F10","Village Dispensary");

    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    supervisoryNodesPage.clickAddNewButton();

    searchAssociatedFacility("F10");

    supervisoryNodesPage.verifyAbsenceOfDisabledFacility();

    supervisoryNodesPage.clickCrossButton();

    SeleneseTestBase.assertEquals("",supervisoryNodesPage.getSearchFacilityText());

  }

//  @Test(groups = {"admin"})
//  public void testUpdateSupervisoryNode() throws SQLException {
//    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
//    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
//    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
//    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
//    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
//    searchNode("sup");
//    assertTrue(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());
//
//    searchNode("sup");
//    assertFalse(supervisoryNodesPage.isOneResultMessageDisplayed());
//
//    supervisoryNodesPage.clickOnFirstFacilitySearchResultLink();
//    editSelectedSupervisoryNode("sup");
//
//    supervisoryNodesPage.clickSaveButton();
//
//    searchNode("super1"); //new code
//    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());
//
//    assertEquals("Super2", supervisoryNodesPage.getParent(1));
//    assertEquals("Central Hospital",supervisoryNodesPage.getFacility(1));
//
//    supervisoryNodesPage.clickCrossButton();
//
//    SeleneseTestBase.assertEquals("",supervisoryNodesPage.getSearchSupervisoryNodeText());
//
//  }



  public void searchNode(String searchParameter) {
    SupervisoryNodesPage supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
    supervisoryNodesPage.enterSearchParameter(searchParameter);
    supervisoryNodesPage.clickSearchIcon();
    testWebDriver.waitForAjax();
  }

  public void addNewSupervisoryNode(String code, String name, String description, String parentNode,String facilityName)
  {
    SupervisoryNodesPage supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);

    supervisoryNodesPage.enterSupervisoryNodeCodeValue(code);
    supervisoryNodesPage.enterSupervisoryNodeNameValues(name);
    supervisoryNodesPage.enterSupervisoryNodeDescriptionValue(description);
    supervisoryNodesPage.enterSearchParentNodeParameter(parentNode);
    supervisoryNodesPage.verifySearchParentNodeResult();

    supervisoryNodesPage.clickOnFirstFacilitySearchResultLink();
    assertTrue(supervisoryNodesPage.isClearSearchButtonIsVisible());

    supervisoryNodesPage.clickOnClearSearchResultButton();

    supervisoryNodesPage.enterSearchParentNodeParameter(parentNode);

    supervisoryNodesPage.clickOnFirstParentSearchResult();
    
    searchAssociatedFacility(facilityName);
    supervisoryNodesPage.selectFirstFacilityToBeAssociated();
  }

  public void editSelectedSupervisoryNode(String parentNode){
    SupervisoryNodesPage supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
    supervisoryNodesPage.enterSearchParentNodeParameter(parentNode);
    supervisoryNodesPage.verifySearchParentNodeResult();
    supervisoryNodesPage.clickOnFirstParentSearchResult();
    searchAssociatedFacility("Village Dispensary");
    supervisoryNodesPage.selectFirstFacilityToBeAssociated();
  }



  public void searchAssociatedFacility(String facilityName){
    SupervisoryNodesPage supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
    supervisoryNodesPage.clickAssociatedFacilityField();
    supervisoryNodesPage.searchFacilityToBeAssociated(facilityName);
    supervisoryNodesPage.clickSearchIcon();
  }

  private void verifySupervisoryNodeNameOrderOnPage(String[] nodeNames) {
    SupervisoryNodesPage supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
    for (int i = 1; i < nodeNames.length; i++) {
      assertEquals(nodeNames[i - 1], supervisoryNodesPage.getSupervisoryNodeName(i));
    }
  }

  private void verifyParentNameOrderOnPage(String[] parentNames) {
    SupervisoryNodesPage supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
    for (int i = 1; i < parentNames.length; i++) {
      assertEquals(parentNames[i - 1], supervisoryNodesPage.getParent(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, testWebDriver.getElementsSizeByXpath("//table[@id='supervisoryNodesTable']/tbody/tr"));
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

}
