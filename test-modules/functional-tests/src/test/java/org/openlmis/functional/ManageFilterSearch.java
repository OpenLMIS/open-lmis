package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class ManageFilterSearch extends TestCaseHelper {

  LoginPage loginPage;
  RequisitionGroupSearchPage requisitionGroupPage;
  SupervisoryNodesPage supervisoryNodesPage;
  FilterSearchPage filterSearchPage;

  public static final String ADMIN = "admin";
  public static final String PASSWORD = "password";

  public Map<String, String> testData = new HashMap<String, String>() {{
    put(PASSWORD, "Admin123");
    put(ADMIN, "Admin123");
  }};

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F10", "F100", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F100A", "F10A", 2, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F10B", "F10C", 3, 5);

    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    requisitionGroupPage = PageObjectFactory.getRequisitionGroupPage(testWebDriver);
    filterSearchPage = PageObjectFactory.getFilterSearchPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testFilterAfterFacilitySearchOnRequisitionGroupPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    requisitionGroupPage.clickAddNewButton();

    requisitionGroupPage.clickAssociatedFacilityLink();
    requisitionGroupPage.searchFacilityToBeAssociated("F10");
    requisitionGroupPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("6 matches found for 'F10'", requisitionGroupPage.getNResultsMessage());
    filterSearchPage.clickFilterButton();

    filterSearchPage.selectFacilityType("Lvl3 Hospital");
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("2 matches found for 'F10'", requisitionGroupPage.getNResultsMessage());

    filterSearchPage.clickFilterButton();
    assertEquals("Lvl3 Hospital", filterSearchPage.getSelectedFacilityTypeOnFilterPopUp());
    filterSearchPage.searchGeographicZone("Root");
    filterSearchPage.selectGeographicZoneResult(1);
    assertEquals("Root", filterSearchPage.getSelectedGeoZoneOnFilterPopUp());
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("No matches found for 'F10'", requisitionGroupPage.getNoFacilitySearchResultMessage());
    assertEquals("Lvl3 Hospital", filterSearchPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Root", filterSearchPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(filterSearchPage.isSetFilterButtonPresent());
  }

  @Test(groups = {"admin"})
  public void testFilterBeforeFacilitySearchOnRequisitionGroupPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    requisitionGroupPage.clickAddNewButton();

    requisitionGroupPage.clickAssociatedFacilityLink();
    filterSearchPage.clickFilterButton();
    testWebDriver.waitForAjax();

    filterSearchPage.selectFacilityType("Warehouse");
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeOnFilterPopUp());
    filterSearchPage.searchGeographicZone("Arusha");
    filterSearchPage.selectGeographicZoneResult(1);
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneOnFilterPopUp());
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    requisitionGroupPage.searchFacilityToBeAssociated("F10");
    requisitionGroupPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("2 matches found for 'F10'", requisitionGroupPage.getNResultsMessage());

    filterSearchPage.clickFilterButton();
    filterSearchPage.searchGeographicZone("Root");
    filterSearchPage.selectGeographicZoneResult(1);
    filterSearchPage.clickCancelFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("2 matches found for 'F10'", requisitionGroupPage.getNResultsMessage());
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(filterSearchPage.isSetFilterButtonPresent());

  }

  @Test(groups = {"admin"})
  public void testFilterAfterTooManyFacilitySearchResultsOnRequisitionGroupPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11", "F11A", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11B", "F11C", 2, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11D", "F11E", 3, 5);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11F", "F11G", 3, 5);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11H", "F11I", 3, 5);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F110", "F111", 3, 5);
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    requisitionGroupPage.clickAddNewButton();

    requisitionGroupPage.clickAssociatedFacilityLink();
    requisitionGroupPage.searchFacilityToBeAssociated("F11");
    requisitionGroupPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("Too many results found. Please refine your search.", requisitionGroupPage.getTooManyFacilitySearchResultMessage());
    filterSearchPage.clickFilterButton();
    testWebDriver.waitForAjax();

    filterSearchPage.selectFacilityType("Warehouse");
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeOnFilterPopUp());
    filterSearchPage.searchGeographicZone("Arusha");
    filterSearchPage.selectGeographicZoneResult(1);
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneOnFilterPopUp());
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();
    assertEquals("2 matches found for 'F11'", requisitionGroupPage.getNResultsMessage());

    filterSearchPage.clickFilterButton();
    testWebDriver.waitForAjax();
    filterSearchPage.searchGeographicZone("Root");
    filterSearchPage.selectGeographicZoneResult(1);
    filterSearchPage.clickCancelFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("2 matches found for 'F11'", requisitionGroupPage.getNResultsMessage());
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(filterSearchPage.isSetFilterButtonPresent());

  }

  @Test(groups = {"admin"})
  public void testFilterAfterFacilitySearchOnSupervisoryNodesPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.clickAddNewButton();
    testWebDriver.waitForAjax();

    supervisoryNodesPage.clickAssociatedFacilityMemberField();
    supervisoryNodesPage.searchFacilityToBeAssociated("F10");
    supervisoryNodesPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("6 matches found for 'F10'", supervisoryNodesPage.getNResultsMessage());
    filterSearchPage.clickFilterButton();

    filterSearchPage.selectFacilityType("Lvl3 Hospital");
    assertEquals("Lvl3 Hospital", filterSearchPage.getSelectedFacilityTypeOnFilterPopUp());
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("2 matches found for 'F10'", supervisoryNodesPage.getNResultsMessage());

    filterSearchPage.clickFilterButton();
    assertEquals("Lvl3 Hospital", filterSearchPage.getSelectedFacilityTypeOnFilterPopUp());
    filterSearchPage.searchGeographicZone("Root");
    filterSearchPage.selectGeographicZoneResult(1);
    assertEquals("Root", filterSearchPage.getSelectedGeoZoneOnFilterPopUp());
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("No matches found for 'F10'", supervisoryNodesPage.getNoFacilitySearchResultMessage());
    assertEquals("Lvl3 Hospital", filterSearchPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Root", filterSearchPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(filterSearchPage.isSetFilterButtonPresent());
  }

  @Test(groups = {"admin"})
  public void testFilterBeforeFacilitySearchOnSupervisoryNodesPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.clickAddNewButton();

    supervisoryNodesPage.clickAssociatedFacilityMemberField();
    filterSearchPage.clickFilterButton();
    testWebDriver.waitForAjax();

    filterSearchPage.selectFacilityType("Warehouse");
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeOnFilterPopUp());
    filterSearchPage.searchGeographicZone("Arusha");
    filterSearchPage.selectGeographicZoneResult(1);
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneOnFilterPopUp());
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    supervisoryNodesPage.searchFacilityToBeAssociated("F10");
    supervisoryNodesPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("2 matches found for 'F10'", supervisoryNodesPage.getNResultsMessage());


    filterSearchPage.clickFilterButton();
    filterSearchPage.searchGeographicZone("Root");
    filterSearchPage.selectGeographicZoneResult(1);
    filterSearchPage.clickCancelFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("2 matches found for 'F10'", supervisoryNodesPage.getNResultsMessage());
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(filterSearchPage.isSetFilterButtonPresent());

  }

  @Test(groups = {"admin"})
  public void testFilterAfterTooManyFacilitySearchResultsOnSupervisoryNodesPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11", "F11A", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11B", "F11C", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11D", "F11E", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11F", "F11G", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F11H", "F11I", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F110", "F111", 3, 5);
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.clickAddNewButton();

    supervisoryNodesPage.clickAssociatedFacilityMemberField();
    supervisoryNodesPage.searchFacilityToBeAssociated("F11");
    supervisoryNodesPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertEquals("Too many results found. Please refine your search.", supervisoryNodesPage.getTooManyResultsMessage());

    filterSearchPage.clickFilterButton();
    testWebDriver.waitForAjax();
    filterSearchPage.selectFacilityType("Warehouse");
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeOnFilterPopUp());
    filterSearchPage.searchGeographicZone("Arusha");
    filterSearchPage.selectGeographicZoneResult(1);
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneOnFilterPopUp());
    filterSearchPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Arusha", filterSearchPage.getSelectedGeoZoneLabelOnAddFilterPage());


    assertEquals("10 matches found for 'F11'", supervisoryNodesPage.getNResultsMessage());


    filterSearchPage.clickFilterButton();
    filterSearchPage.searchGeographicZone("Root");
    filterSearchPage.selectGeographicZoneResult(1);
    filterSearchPage.clickCancelFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("10 matches found for 'F11'", supervisoryNodesPage.getNResultsMessage());
    assertEquals("Warehouse", filterSearchPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertTrue(filterSearchPage.isSetFilterButtonPresent());

  }
}
