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
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.RolesPage;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRole extends TestCaseHelper {

  public static final String CREATE_REQUISITION = "Create Requisition";
  public static final String APPROVE_REQUISITION = "Approve Requisition";
  public static final String MANAGE_DISTRIBUTION = "Manage Distribution";
  public static final String ADMIN = "admin";

  LoginPage loginPage;
  RolesPage rolesPage;

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_ROLE");
    dbWrapper.assignRight("Admin", "MANAGE_FACILITY");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    rolesPage = PageObjectFactory.getRolesPage(testWebDriver);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyRightsUponOK(String[] credentials) throws SQLException {
    String UPLOADS = "Uploads";
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.getCreateNewRoleButton().click();
    testWebDriver.waitForElementToAppear(rolesPage.getAllocationRoleType());
    assertFalse(rolesPage.getWebElementMap().get(MANAGE_DISTRIBUTION).isEnabled());
    assertTrue(rolesPage.getWebElementMap().get(UPLOADS).isEnabled());
    assertFalse(rolesPage.getWebElementMap().get(APPROVE_REQUISITION).isEnabled());

    testWebDriver.handleScrollByPixels(0, 3000);
    testWebDriver.waitForElementToAppear(rolesPage.getWebElementMap().get(UPLOADS));

    rolesPage.getWebElementMap().get(UPLOADS).click();
    rolesPage.getAllocationRoleType().click();
    rolesPage.clickContinueButton();
    assertFalse(rolesPage.getWebElementMap().get(UPLOADS).isSelected());
    assertFalse(rolesPage.getWebElementMap().get(APPROVE_REQUISITION).isEnabled());
    assertFalse(rolesPage.getWebElementMap().get(UPLOADS).isEnabled());
    assertTrue(rolesPage.getWebElementMap().get(MANAGE_DISTRIBUTION).isEnabled());

    rolesPage.selectRight("Manage Distribution");
    rolesPage.enterRoleName("DistributionRole");
    rolesPage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals(dbWrapper.getListOfRightsForRole("DistributionRole"), asList("MANAGE_DISTRIBUTION"));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyRightsUponCancel(String[] credentials) throws SQLException {
    String UPLOADS = "Uploads";
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.getCreateNewRoleButton().click();
    testWebDriver.waitForElementToAppear(rolesPage.getAllocationRoleType());
    rolesPage.getWebElementMap().get(UPLOADS).click();
    rolesPage.getAllocationRoleType().click();
    rolesPage.clickCancelButtonOnModal();
    assertTrue(rolesPage.getWebElementMap().get(UPLOADS).isSelected());
    assertFalse(rolesPage.getWebElementMap().get(APPROVE_REQUISITION).isEnabled());
    assertTrue(rolesPage.getWebElementMap().get(UPLOADS).isEnabled());
    assertFalse(rolesPage.getWebElementMap().get(MANAGE_DISTRIBUTION).isEnabled());
    rolesPage.enterRoleName("new role");
    rolesPage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals(dbWrapper.getListOfRightsForRole("new role"), asList("UPLOADS"));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyDuplicateRoleName(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    List<String> userRoleList = new ArrayList<>();
    userRoleList.add("Uploads");
    rolesPage.createRole(ADMIN, ADMIN, userRoleList, false);
    assertEquals("Duplicate Role found", rolesPage.getSaveErrorMsgDiv().getText().trim());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyFacilityBasedRole(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.createFacilityBasedRole("Facility Based Role Name", "Facility Based Role Description");
    verifyCreatedRoleMessage("Facility Based Role Name");
    assertEquals(dbWrapper.getListOfRightsForRole("Facility Based Role Name"), asList("FACILITY_FILL_SHIPMENT", "VIEW_ORDER"));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyEditRole(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.createFacilityBasedRole("Facility Role", "Facility Based Role Description");
    verifyCreatedRoleMessage("Facility Role");
    rolesPage.clickARole("Facility Role");
    rolesPage.clickProgramRole();
    rolesPage.clickContinueButton();
    rolesPage.selectRight("Approve Requisition");
    rolesPage.clickSaveButton();
    assertEquals(rolesPage.getSuccessMessage(), "\"Facility Role\" updated successfully");
    assertEquals(dbWrapper.getListOfRightsForRole("Facility Role"), asList("APPROVE_REQUISITION", "VIEW_REQUISITION"));
  }

  public void verifyCreatedRoleMessage(String roleName) {
    assertEquals(rolesPage.getSuccessMessage(), "\"" + roleName + "\" created successfully");
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.insertAllAdminRightsAsSeedData();
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Role-Function")
  public Object[][] parameterIntRoleTestProvider() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
