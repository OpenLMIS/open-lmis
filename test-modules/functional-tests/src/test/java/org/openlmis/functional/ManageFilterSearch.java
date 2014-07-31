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

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class ManageFilterSearch extends TestCaseHelper {

  LoginPage loginPage;
  RequisitionGroupPage requisitionGroupPage;
  SupervisoryNodesPage supervisoryNodesPage;

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
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F10", "F100", 1, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F100A", "F10A", 2, 3);
    dbWrapper.insertFacilitiesWithFacilityTypeIDAndGeoZoneId("F10B", "F10C", 3, 5);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    requisitionGroupPage = PageObjectFactory.getRequisitionGroupPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testFilterAfterFacilitySearchOnRequisitionGroupPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_REQUISITION_GROUP");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F10B");
    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10C");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    requisitionGroupPage.clickAddNewButton();

    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchMultipleFacilities("F10");
    testWebDriver.waitForAjax();
    assertEquals("5 matches found for 'F10'", requisitionGroupPage.getNFacilityResultsMessage());
    requisitionGroupPage.clickFilterButton();

    requisitionGroupPage.selectFacilityType("Lvl3 Hospital");
    requisitionGroupPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("2 matches found for 'F10'", requisitionGroupPage.getNFacilityResultsMessage());

    requisitionGroupPage.clickFilterButton();
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getSelectedFacilityTypeOnFilterPopUp());
    requisitionGroupPage.searchGeographicZone("Root");
    requisitionGroupPage.selectGeographicZoneResult(1);
    assertEquals("Root", requisitionGroupPage.getSelectedGeoZoneOnFilterPopUp());
    requisitionGroupPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("No matches found for 'F10'", requisitionGroupPage.getNoFacilityResultMessage());
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Root", requisitionGroupPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(requisitionGroupPage.isSetFilterButtonPresent());

    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickMembersAccordionLink();
    assertEquals("No matches found for 'F10'", requisitionGroupPage.getNoFacilityResultMessage());
    assertEquals("Lvl3 Hospital", requisitionGroupPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Root", requisitionGroupPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(requisitionGroupPage.isSetFilterButtonPresent());
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
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F11A");
    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F11");
    dbWrapper.setupDataForGeoZones();

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    requisitionGroupPage = homePage.navigateToRequisitionGroupPage();
    requisitionGroupPage.clickAddNewButton();

    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    requisitionGroupPage.searchMultipleFacilities("F11");
    testWebDriver.waitForAjax();
    assertEquals("Too many results found. Please refine your search.", requisitionGroupPage.getTooManyFacilitySearchResultMessage());
    requisitionGroupPage.clickFilterButton();
    testWebDriver.waitForAjax();

    requisitionGroupPage.selectFacilityType("Warehouse");
    assertEquals("Warehouse", requisitionGroupPage.getSelectedFacilityTypeOnFilterPopUp());
    requisitionGroupPage.searchGeographicZone("ABD");
    assertEquals("No matches found for 'ABD'", requisitionGroupPage.getNoGeoZoneResultMessage());
    requisitionGroupPage.searchGeographicZone("%");
    assertEquals("Too many results found. Please refine your search.", requisitionGroupPage.getTooManyGeoZoneSearchResultMessage());
    requisitionGroupPage.searchGeographicZone("A");
    assertEquals("3 matches found for 'A'", requisitionGroupPage.getNGeoZoneResultsMessage());
    requisitionGroupPage.searchGeographicZone("Arusha");
    assertEquals("1 match found for 'Arusha'", requisitionGroupPage.getOneGeoZoneResultMessage());
    requisitionGroupPage.selectGeographicZoneResult(1);
    assertEquals("Arusha", requisitionGroupPage.getSelectedGeoZoneOnFilterPopUp());
    requisitionGroupPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();
    assertEquals("1 match found for 'F11'", requisitionGroupPage.getOneFacilityResultMessage());

    requisitionGroupPage.clickFilterButton();
    testWebDriver.waitForAjax();
    requisitionGroupPage.searchGeographicZone("Root");
    requisitionGroupPage.selectGeographicZoneResult(1);
    requisitionGroupPage.clickCancelFilterButton();
    testWebDriver.waitForAjax();

    assertEquals("1 match found for 'F11'", requisitionGroupPage.getOneFacilityResultMessage());
    assertEquals("Warehouse", requisitionGroupPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Arusha", requisitionGroupPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(requisitionGroupPage.isSetFilterButtonPresent());

    testWebDriver.refresh();
    requisitionGroupPage.clickMembersAccordionLink();
    requisitionGroupPage.clickAddMembersButton();
    assertFalse(requisitionGroupPage.isNoFacilityResultMessageDisplayed());
    assertEquals("", requisitionGroupPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("", requisitionGroupPage.getSelectedGeoZoneLabelOnAddFilterPage());
    assertTrue(requisitionGroupPage.isSetFilterButtonPresent());
  }

  @Test(groups = {"admin"})
  public void testClearFilterAfterFacilitySearchOnSupervisoryNodesPage() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F100A", "N2", "Super2", null);
    dbWrapper.insertRequisitionGroups("RG1", "RG2", "N2", "N1");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.clickAddNewButton();
    testWebDriver.waitForAjax();

    supervisoryNodesPage.clickAssociatedFacilityMemberField();
    supervisoryNodesPage.searchFacility("F10");
    testWebDriver.waitForAjax();
    assertEquals("6 matches found for 'F10'", supervisoryNodesPage.getNFacilityResultsMessage());
    supervisoryNodesPage.clickFilterButton();
    supervisoryNodesPage.clickApplyFilterButton();
    assertEquals("6 matches found for 'F10'", supervisoryNodesPage.getNFacilityResultsMessage());

    supervisoryNodesPage.clickFilterButton();
    supervisoryNodesPage.selectFacilityType("Lvl3 Hospital");
    assertEquals("Lvl3 Hospital", supervisoryNodesPage.getSelectedFacilityTypeOnFilterPopUp());
    supervisoryNodesPage.searchGeographicZone("Root");
    supervisoryNodesPage.selectGeographicZoneResult(1);
    assertEquals("Root", supervisoryNodesPage.getSelectedGeoZoneOnFilterPopUp());
    supervisoryNodesPage.clickApplyFilterButton();
    testWebDriver.waitForAjax();
    assertEquals("No matches found for 'F10'", supervisoryNodesPage.getNoFacilityResultMessage());

    supervisoryNodesPage.clickFilterButton();
    supervisoryNodesPage.clickCancelFilterButton();
    testWebDriver.waitForAjax();
    assertEquals("No matches found for 'F10'", supervisoryNodesPage.getNoFacilityResultMessage());
    assertEquals("Lvl3 Hospital", supervisoryNodesPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Root", supervisoryNodesPage.getSelectedGeoZoneLabelOnAddFilterPage());

    supervisoryNodesPage.clickFilterButton();
    assertEquals("Lvl3 Hospital", supervisoryNodesPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("Root", supervisoryNodesPage.getSelectedGeoZoneLabelOnAddFilterPage());
    supervisoryNodesPage.clickRemoveFacilityTypeFilter();
    assertFalse(supervisoryNodesPage.isSelectedFacilityTypeOnFilterPopUpDisplayed());
    assertFalse(supervisoryNodesPage.isRemoveFacilityTypeFilterDisplayed());
    supervisoryNodesPage.clickRemoveGeoZoneFilter();
    assertFalse(supervisoryNodesPage.isSelectedGeoZoneOnFilterPopUpDisplayed());
    assertFalse(supervisoryNodesPage.isRemoveGeoZoneFilterDisplayed());

    supervisoryNodesPage.clickApplyFilterButton();

    assertTrue(supervisoryNodesPage.isSetFilterButtonPresent());
    assertEquals("", supervisoryNodesPage.getSelectedFacilityTypeLabelOnAddFilterPage());
    assertEquals("", supervisoryNodesPage.getSelectedGeoZoneLabelOnAddFilterPage());
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
