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
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.SupervisoryNodesPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;

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
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNodeWithoutDelete("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    homePage.navigateToUser();
    homePage.verifyAdminTabs();
    assertFalse(homePage.isSupervisoryNodeTabDisplayed());
    homePage.logout();
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    assertTrue(homePage.isSupervisoryNodeTabDisplayed());
    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    assertEquals("Search supervisory node", supervisoryNodesPage.getSearchSupervisoryNodeLabel());
    assertTrue(supervisoryNodesPage.isAddNewButtonDisplayed());
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNodeWithoutDelete("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    homePage.verifyAdminTabs();
    assertTrue(homePage.isSupervisoryNodeTabDisplayed());
    assertEquals("Search supervisory node", supervisoryNodesPage.getSearchSupervisoryNodeLabel());
    assertTrue(supervisoryNodesPage.isAddNewButtonDisplayed());
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    assertFalse(supervisoryNodesPage.isNoResultMessageDisplayed());
    assertFalse(supervisoryNodesPage.isNResultsMessageDisplayed());
    assertFalse(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertFalse(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());

    supervisoryNodesPage.enterSearchParameter("nod");
    assertEquals("3 matches found for 'nod'", supervisoryNodesPage.getNResultsMessage());
    assertEquals("Supervisory Node Name", supervisoryNodesPage.getSupervisoryNodeHeader());
    assertEquals("Code", supervisoryNodesPage.getCodeHeader());
    assertEquals("Facility", supervisoryNodesPage.getFacilityHeader());
    assertEquals("Parent", supervisoryNodesPage.getParentHeader());

    assertEquals("Node3", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N3", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
    assertEquals("Node2", supervisoryNodesPage.getParent(1));

    //verify sorting and pagination

    supervisoryNodesPage.closeSearchResults();
    assertFalse(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeParentSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNodeWithoutDelete("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    assertEquals("Supervisory node parent", supervisoryNodesPage.getSelectedSearchOption());
    assertFalse(supervisoryNodesPage.isNoResultMessageDisplayed());
    assertFalse(supervisoryNodesPage.isNResultsMessageDisplayed());
    assertFalse(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertFalse(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());

    supervisoryNodesPage.enterSearchParameter("nod");
    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertEquals("Supervisory Node Name", supervisoryNodesPage.getSupervisoryNodeHeader());
    assertEquals("Code", supervisoryNodesPage.getCodeHeader());
    assertEquals("Facility", supervisoryNodesPage.getFacilityHeader());
    assertEquals("Parent", supervisoryNodesPage.getParentHeader());

    assertEquals("Node3", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N3", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
    assertEquals("Node2", supervisoryNodesPage.getParent(1));

    //verify sorting and pagination

    supervisoryNodesPage.closeSearchResults();
    assertFalse(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeParentSearchWhenNoResults() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N1", "Super1", null);
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    assertEquals("Supervisory node parent", supervisoryNodesPage.getSelectedSearchOption());
    supervisoryNodesPage.enterSearchParameter("nod");
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeAsSearchOption();
    //assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N2", "Node2", null);
    testWebDriver.refresh();
    supervisoryNodesPage.enterSearchParameter("nod");
    //assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertEquals("Node2", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N2", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
    assertEquals("", supervisoryNodesPage.getParent(1));

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());
  }


  //@Test(groups = {"admin"})
  public void testAddNewSupervisoryNode() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.clickAddNewButton();
    //add a valid supervisoryNode
    //click save

    supervisoryNodesPage.enterSearchParameter(""); //enter the one added
    //verify it

    supervisoryNodesPage.clickAddNewButton();
    //add same node
    //verify error message
    //add new with parent as previous one
    //click save

    supervisoryNodesPage.enterSearchParameter(""); //new added
    //verify
    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    supervisoryNodesPage.enterSearchParameter(""); //previously added
    //verify

    supervisoryNodesPage.clickAddNewButton();
    //add same node
    //verify error message
    //add new with parent as previous one
    //click cancel

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeAsSearchOption();
    supervisoryNodesPage.enterSearchParameter(""); //new added
    //verify no result
  }

  //@Test(groups = {"admin"})
  public void testUpdateSupervisoryNode() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNodeWithoutDelete("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNodeWithoutDelete("F11", "N2", "Super2", null);
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    SupervisoryNodesPage supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.enterSearchParameter("sup");
    //click the resultant Super1
    //update the code and parent
    //click save

    supervisoryNodesPage.enterSearchParameter("sup");
    assertFalse(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    supervisoryNodesPage.enterSearchParameter(""); //new code
    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());

    supervisoryNodesPage.enterSearchParameter("sup");
    //click the resultant
    //update the code as the last updated code and parent
    //click save
    //verify error msg
    //update code
    //enter invalid parent
    //verify error message
    //enter parent as the new code above
    //verify error msg
    //enter valid parent
    //click save
    supervisoryNodesPage.enterSearchParameter(""); //new code
    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());
    //click the resultant
    //update code
    //click cancel
    supervisoryNodesPage.enterSearchParameter(""); //new code
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

}
