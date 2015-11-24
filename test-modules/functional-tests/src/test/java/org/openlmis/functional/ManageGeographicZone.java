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
import org.openlmis.pageobjects.GeographicZonePage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.*;

public class ManageGeographicZone extends TestCaseHelper {

  LoginPage loginPage;
  GeographicZonePage geographicZonePage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.setupDataForGeoZones();
    dbWrapper.removeAllExistingRights("Admin");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    geographicZonePage = PageObjectFactory.getGeographicZonePage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testSearchGeographicZones() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    geographicZonePage = homePage.navigateManageGeographicZonesPage();

    assertTrue(geographicZonePage.isGeoZoneTabVisible());
    assertEquals("Geographic zones", geographicZonePage.getGeoZoneTabLabel());
    assertEquals("Search geographic zone", geographicZonePage.getGeoZoneSearchPageHeader());
    assertEquals("Geographic zone", geographicZonePage.getSelectedSearchOption());

    geographicZonePage.searchGeoZone("Dis");
    testWebDriver.waitForAjax();
    assertEquals("13 matches found for 'Dis'", geographicZonePage.getNResultsMessage());
    verifyNumberOFPageLinksDisplayed(13, 10);
    verifyPageNumberSelected(1);
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNextAndLastPageLinksEnabled();
    verifyLevelOrder(asList("Country", "State", "State", "State", "State", "State", "Province", "Province", "Province", "District"));
    verifyParentNameOrder(asList("", "district1", "District2", "District2", "", "", "district1", "District2", "District9", "District2"));
    verifyGeoZoneNameOrder(asList("District2", "District10", "district6", "District9", "district1", "District13", "District7", "District3", "District11", "district8"));

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    geographicZonePage.verifyNumberOfItemsPerPage(3);
    verifyLevelOrder(asList("District", "District", "District"));
    verifyParentNameOrder(asList("District3", "District3", "District7"));
    verifyGeoZoneNameOrder(asList("district4", "district5", "District12"));
    navigateToPage(1);
    verifyPageNumberSelected(1);
    geographicZonePage.verifyNumberOfItemsPerPage(10);
    navigateToLastPage();
    verifyPageNumberSelected(2);
    navigateToFirstPage();
    verifyPageNumberSelected(1);
  }

  @Test(groups = {"admin"})
  public void testSearchByGeographicZoneParent() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    geographicZonePage = homePage.navigateManageGeographicZonesPage();

    geographicZonePage.clickSearchOptionButton();
    geographicZonePage.selectGeoZoneParentSearchOption();
    geographicZonePage.searchGeoZone("Dis");
    testWebDriver.waitForAjax();

    assertEquals("Name", geographicZonePage.getNameHeader());
    assertEquals("Code", geographicZonePage.getCodeHeader());
    assertEquals("Level", geographicZonePage.getLevelHeader());
    assertEquals("Parent", geographicZonePage.getParentHeader());

    assertEquals("10 matches found for 'Dis'", geographicZonePage.getNResultsMessage());
    verifyNumberOFPageLinksDisplayed(10, 10);
    verifyPageNumberSelected(1);
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNextAndLastPageLinksDisabled();
    verifyLevelOrder(asList("State", "State", "State", "Province", "Province", "Province", "District", "District", "District", "District"));
    verifyParentNameOrder(asList("district1", "District2", "District2", "district1", "District2", "District9", "District2", "District3", "District3", "District7"));
    verifyGeoZoneNameOrder(asList("District10", "district6", "District9", "District7", "District3", "District11", "district8", "district4", "district5", "District12"));
  }

  @Test(groups = {"admin"})
  public void testSearchAfterAddingNewGeoZoneAtCountryLevel() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    geographicZonePage = homePage.navigateManageGeographicZonesPage();

    geographicZonePage.searchGeoZone("Maputo");
    assertEquals("No matches found for 'Maputo'", geographicZonePage.getNoResultMessage());

    geographicZonePage.clickAddNewButton();
    assertEquals("Add new geographic zone", geographicZonePage.getAddNewGeoZoneHeader());

    assertEquals("Name *", geographicZonePage.getNameLabel());
    assertEquals("Code *", geographicZonePage.getCodeLabel());
    assertEquals("Latitude", geographicZonePage.getLatitudeLabel());
    assertEquals("Longitude", geographicZonePage.getLongitudeLabel());
    assertEquals("Parent", geographicZonePage.getParentLabel());
    assertEquals("Catchment population", geographicZonePage.getPopulationLabel());
    assertEquals("Level *", geographicZonePage.getLevelLabel());

    geographicZonePage.enterGeoZoneName("Maputo");
    geographicZonePage.enterGeoZoneCode("map");
    geographicZonePage.enterCatchmentPopulation("9000");
    geographicZonePage.enterLatitude("99.99999");
    geographicZonePage.enterLongitude("00.0080");
    geographicZonePage.selectGeoZoneLevel("Country");
    geographicZonePage.clickSelectParentField();
    assertTrue(geographicZonePage.isParentDropDownEmpty());
    geographicZonePage.clickOnSaveButton();

    assertTrue(geographicZonePage.isSuccessMessageDisplayed());
    assertEquals("Geographic Zone \"Maputo\" created successfully.   View Here", geographicZonePage.getSuccessMessage());

    geographicZonePage.clickOnViewHereLink();

    assertEquals("Maputo", geographicZonePage.getGeoZoneNameOnEditPage());
    assertEquals("9000", geographicZonePage.getPopulationOnEditPage());
    assertEquals("99.99999", geographicZonePage.getLatitudeOnEditPage());
    assertEquals("0.008", geographicZonePage.getLongitudeOnEditPage());

    geographicZonePage.clickOnCancelButton();

    geographicZonePage.searchGeoZone("Maputo");
    assertEquals("1 match found for 'Maputo'", geographicZonePage.getOneResultMessage());

    geographicZonePage.isSearchResultTableDisplayed();
    geographicZonePage.verifyNumberOfItemsPerPage(1);
    verifyPageNumberSelected(1);
    assertEquals("Maputo", geographicZonePage.getGeoZoneName(1));
    assertEquals("map", geographicZonePage.getGeoZoneCode(1));
    assertEquals("Country", geographicZonePage.getLevelName(1));
    assertEquals("", geographicZonePage.getParentName(1));

    geographicZonePage.clickOnCrossButton();
    assertFalse(geographicZonePage.isSearchResultTableDisplayed());
  }

  @Test(groups = {"admin"})
  public void testAddingNewGeoZoneAtLowestLevel() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    geographicZonePage = homePage.navigateManageGeographicZonesPage();
    geographicZonePage.clickAddNewButton();

    geographicZonePage.enterGeoZoneName("Maputo");
    geographicZonePage.enterGeoZoneCode("map");
    List<String> expectedListOfLevels = asList("--Select geographic level--", "Country", "State", "Province", "District");
    List<String> actualListOfLevels = geographicZonePage.getListOfLevels();
    assertTrue(actualListOfLevels.equals(expectedListOfLevels));

    geographicZonePage.selectGeoZoneLevel("District");
    geographicZonePage.clickOnSaveButton();
    assertEquals("Invalid Geographic Zone Parent Code", geographicZonePage.getSaveErrorMessage());

    List<String> expectedListOfParentGroups = asList("Country\nDistrict2\nRoot",
      "State\nArusha\ndistrict1\nDistrict10\nDistrict13\ndistrict6\nDistrict9",
      "Province\nDistrict11\nDistrict3\nDistrict7\nDodoma");
    List<String> actualListOfParentGroups = geographicZonePage.getListOfParentGroupsWithOptions();
    assertTrue(actualListOfParentGroups.equals(expectedListOfParentGroups));

    geographicZonePage.selectGeoZoneParent("Arusha");
    geographicZonePage.clickOnSaveButton();
    assertEquals("Geographic Zone \"Maputo\" created successfully.   View Here", geographicZonePage.getSuccessMessage());

    geographicZonePage.searchGeoZone("Maputo");
    assertEquals("Maputo", geographicZonePage.getGeoZoneName(1));
    assertEquals("map", geographicZonePage.getGeoZoneCode(1));
    assertEquals("District", geographicZonePage.getLevelName(1));
    assertEquals("Arusha", geographicZonePage.getParentName(1));
  }

  @Test(groups = {"admin"})
  public void testEditGeoZone() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    geographicZonePage = homePage.navigateManageGeographicZonesPage();

    geographicZonePage.searchGeoZone("Dist");
    geographicZonePage.clickOnSearchResultLink(3);
    testWebDriver.waitForAjax();

    assertEquals("Edit geographic zone", geographicZonePage.getEditGeoZoneHeader());
    geographicZonePage.editAlreadyExistingGeoZone("Mozambique", "Mozambique", "20000", "99.99999", "19.99999", "Root");
    assertFalse(geographicZonePage.isLevelCodeDropDownEnabled());
    geographicZonePage.clickOnSaveButton();
    testWebDriver.waitForAjax();
    assertEquals("Geographic Zone \"Mozambique\" updated successfully.   View Here", geographicZonePage.getSuccessMessage());

    geographicZonePage.searchGeoZone("Moz");
    testWebDriver.waitForAjax();
    geographicZonePage.clickOnSearchResultLink(1);
    testWebDriver.waitForAjax();
    assertEquals("20000", geographicZonePage.getPopulationOnEditPage());
    assertEquals("99.99999", geographicZonePage.getLatitudeOnEditPage());
    assertEquals("19.99999", geographicZonePage.getLongitudeOnEditPage());
    geographicZonePage.enterGeoZoneCode("Nomadia");
    geographicZonePage.clickOnCancelButton();

    assertEquals("Mozambique", geographicZonePage.getGeoZoneName(1));
    assertEquals("Mozambique", geographicZonePage.getGeoZoneCode(1));
    assertEquals("State", geographicZonePage.getLevelName(1));
    assertEquals("Root", geographicZonePage.getParentName(1));
  }

  @Test(groups = {"admin"})
  public void testValidationsOnGeoZone() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    geographicZonePage = homePage.navigateManageGeographicZonesPage();
    geographicZonePage.clickAddNewButton();

    geographicZonePage.clickOnSaveButton();
    assertEquals("There are some errors in the form. Please resolve them.", geographicZonePage.getSaveErrorMessage());

    geographicZonePage.enterGeoZoneName("Maputo");
    geographicZonePage.enterGeoZoneCode("Arusha");
    geographicZonePage.selectGeoZoneLevel("Country");
    geographicZonePage.clickOnSaveButton();
    testWebDriver.sleep(500);
    assertEquals("Duplicate Geographic Zone Code", geographicZonePage.getSaveErrorMessage());
  }

  private void verifyLevelOrder(List<String> levelName) {
    for (int i = 1; i < levelName.size(); i++) {
      assertEquals(levelName.get(i - 1), geographicZonePage.getLevelName(i));
    }
  }

  private void verifyParentNameOrder(List<String> parentName) {
    for (int i = 1; i < parentName.size(); i++) {
      assertEquals(parentName.get(i - 1), geographicZonePage.getParentName(i));
    }
  }

  private void verifyGeoZoneNameOrder(List<String> geoZoneName) {
    for (int i = 1; i < geoZoneName.size(); i++) {
      assertEquals(geoZoneName.get(i - 1), geographicZonePage.getGeoZoneName(i));
    }
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