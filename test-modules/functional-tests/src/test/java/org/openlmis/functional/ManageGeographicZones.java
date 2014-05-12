package org.openlmis.functional;

import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.ManageGeographicZonesPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageGeographicZones extends TestCaseHelper {

  LoginPage loginPage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.deleteGeographicZones();
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"})
  public void testE2EManageGeographicZones() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    dbWrapper.setupDataForGeoZones();
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateManageGeographicZonesPage();
    homePage.verifyAdminTabs();
    manageGeographicZonesPage.goToGeoZoneTab();
    manageGeographicZonesPage.searchGeoZoneUsingGeoZoneName("Dis");
    manageGeographicZonesPage.clickOnElement();
    manageGeographicZonesPage.editFirstElement();
    //manageGeographicZonesPage.clickOnSaveButton();
    manageGeographicZonesPage.clickOnCancelButton();
    //manageGeographicZonesPage.searchGeoZoneUsingGeoZoneName("Moz");
  }

  @Test(groups = {"admin"})
  public void testAddingNewGeoZone() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_GEOGRAPHIC_ZONE");
    dbWrapper.setupDataForGeoZones();
    HomePage homePage = loginPage.loginAs("Admin123", "Admin123");
    ManageGeographicZonesPage manageGeographicZonesPage = homePage.navigateManageGeographicZonesPage();
    homePage.verifyAdminTabs();
    manageGeographicZonesPage.goToGeoZoneTab();
    manageGeographicZonesPage.addNewGeoZone("Dummy", "Dummy", "900", "99.99999", "99.99999", "Province", "District13");
    manageGeographicZonesPage.clickOnSaveButton();
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.deleteGeographicZones();
    dbWrapper.closeConnection();
  }
}