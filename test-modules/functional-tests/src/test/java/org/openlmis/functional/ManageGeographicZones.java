package org.openlmis.functional;

import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.testng.AssertJUnit.assertEquals;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageGeographicZones extends TestCaseHelper {

  LoginPage loginPage;
  WebElement secondPageLink;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.setupDataForGeoZones();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"})
  public void testE2EManageGeographicZones() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateManageGeographicZonesPage();
    homePage.verifyAdminTabs();
    manageGeographicZonesPage.goToGeoZoneTab();
    manageGeographicZonesPage.searchGeoZoneUsingGeoZoneName("Dis");
    verifyLevelOrderOnPage(new String[] {"Country","Country","Country","State","State","State","State","Province","Province","Province"});
    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    manageGeographicZonesPage.verifyNumberOfItemsPerPage(3);
    navigateToPage(1);
    verifyPageNumberSelected(1);
    verifyPreviousAndFirstPageLinksDisabled();
    manageGeographicZonesPage.verifyNumberOfItemsPerPage(10);
    manageGeographicZonesPage.clickOnFirstElement();
    testWebDriver.waitForPageToLoad();
    manageGeographicZonesPage.editAlreadyExistingGeoZone("Mozambique", "Mozambique", "20000", "99.99999", "99.99999");
    //manageGeographicZonesPage.clickOnSaveButton();
    manageGeographicZonesPage.clickOnCancelButton();
    //manageGeographicZonesPage.searchGeoZoneUsingGeoZoneName("Moz");
  }

  @Test(groups = {"admin"})
  public void testAddingNewGeoZone() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateManageGeographicZonesPage();
    homePage.verifyAdminTabs();
    manageGeographicZonesPage.goToGeoZoneTab();
    manageGeographicZonesPage.addNewGeoZone("Dummy", "Dummy", "900", "99.99999", "99.99999", "Province", "District13");
    manageGeographicZonesPage.clickOnSaveButton();
  }

  @Test(groups = {"admin"})
  public void testSearchByGeographicZoneParent() throws SQLException
  {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateManageGeographicZonesPage();
    homePage.verifyAdminTabs();
    manageGeographicZonesPage.goToGeoZoneTab();
    manageGeographicZonesPage.changeSearchOption();
    manageGeographicZonesPage.searchGeoZoneUsingGeoZoneParentName("Arusha");
    manageGeographicZonesPage.verifySearchResultTable();
    manageGeographicZonesPage.verifySearchResultBody();
    manageGeographicZonesPage.verifySearchResult("Arusha");
    manageGeographicZonesPage.verifySearchResult("9 matches found for 'dis'");
  }

  @Test(groups = {"admin"})
  public  void testWithNoSearchResult() throws SQLException
  {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateManageGeographicZonesPage();
    homePage.verifyAdminTabs();
    manageGeographicZonesPage.goToGeoZoneTab();
    manageGeographicZonesPage.changeSearchOption();
    manageGeographicZonesPage.searchGeoZoneUsingGeoZoneParentName("XYZ");
    manageGeographicZonesPage.verifySearchResultTable();
    manageGeographicZonesPage.verifySearchResultCounter("No matches found for 'XYZ'");
  }

  @Test(groups = {"admin"})
  public  void testCrossButtonOnSearchTable() throws SQLException
  {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateManageGeographicZonesPage();
    homePage.verifyAdminTabs();
    manageGeographicZonesPage.goToGeoZoneTab();
    manageGeographicZonesPage.changeSearchOption();
    manageGeographicZonesPage.searchGeoZoneUsingGeoZoneParentName("XYZ");
    manageGeographicZonesPage.verifySearchResultTable();
    manageGeographicZonesPage.clickOnCrossButton();
  }

  private void verifyLevelOrderOnPage(String[] levelName) {
    ManageGeographicZonesPage manageGeographicZonesPage = PageObjectFactory.getManageGeographicZonesPage(testWebDriver);
    for (int i = 1; i < levelName.length; i++) {
      assertEquals(levelName[i - 1], manageGeographicZonesPage.getLevelName(i));
    }
  }


  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

}