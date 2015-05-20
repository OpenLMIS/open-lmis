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
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    rolesPage = PageObjectFactory.getRolesPage(testWebDriver);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyRightsUponOK(String[] credentials) throws SQLException {
    String UPLOADS = "Uploads";
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.clickCreateNewRoleButton();

    assertEquals(rolesPage.getAddNewRoleHeader(), "Add new role");
    assertEquals(rolesPage.getRoleNameLabel(), "Role name *");
    assertEquals(rolesPage.getRoleDescriptionLabel(), "Role description");
    assertEquals(rolesPage.getAssignRightsLabel(), "Assigned rights *");
    assertEquals(rolesPage.getRoleMixWarning(), "Note: Individual roles cannot be a mix of these types");

    assertEquals(rolesPage.getAdminRoleLabel(), "Admin and general operations rights");
    assertEquals(rolesPage.getReportingRoleLabel(), "Reporting rights");
    assertEquals(rolesPage.getAllocationRoleLabel(), "Allocation program based rights");
    assertEquals(rolesPage.getRequisitionRoleLabel(), "Request program based rights");
    assertEquals(rolesPage.getFulfilmentRoleLabel(), "Fulfillment based rights");

    assertFalse(rolesPage.isRightEnabled(MANAGE_DISTRIBUTION));
    assertTrue(rolesPage.isRightEnabled(UPLOADS));
    assertFalse(rolesPage.isRightEnabled(APPROVE_REQUISITION));

    testWebDriver.handleScrollByPixels(0, 3000);
    rolesPage.selectRight(UPLOADS);
    rolesPage.selectAllocationRoleType();
    rolesPage.clickContinueButton();

    assertFalse(rolesPage.isRightSelected(UPLOADS));
    assertFalse(rolesPage.isRightEnabled(APPROVE_REQUISITION));
    assertFalse(rolesPage.isRightEnabled(UPLOADS));
    assertTrue(rolesPage.isRightEnabled(MANAGE_DISTRIBUTION));

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
    assertTrue(rolesPage.isCreateNewRoleButtonDisplayed());

    rolesPage.clickCreateNewRoleButton();
    rolesPage.selectRight(UPLOADS);
    rolesPage.selectAllocationRoleType();
    rolesPage.clickCancelButtonOnModal();

    assertTrue(rolesPage.isRightSelected(UPLOADS));
    assertFalse(rolesPage.isRightEnabled(APPROVE_REQUISITION));
    assertTrue(rolesPage.isRightEnabled(UPLOADS));
    assertFalse(rolesPage.isRightEnabled(MANAGE_DISTRIBUTION));

    rolesPage.enterRoleName("new role");
    rolesPage.clickSaveButton();
    testWebDriver.sleep(500);
    assertEquals(dbWrapper.getListOfRightsForRole("new role"), asList("UPLOADS"));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyDuplicateRoleName(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();

    assertEquals(rolesPage.getRolesHeader(), "Roles");
    assertEquals(rolesPage.getNameHeader(), "Name");
    assertEquals(rolesPage.getDescriptionHeader(), "Description");
    assertEquals(rolesPage.getRightsHeader(), "Rights");

    assertEquals(rolesPage.getName(1), "Admin");
    assertEquals(rolesPage.getDescription(1), "Admin");
    assertEquals(rolesPage.getRights(1, 1), "Admin - Manage Roles");

    List<String> userRoleList = new ArrayList<>();
    userRoleList.add("Uploads");
    rolesPage.createRole(ADMIN, ADMIN, userRoleList, "Admin");
    assertEquals(rolesPage.getSaveErrorMsg(), "Duplicate Role found");
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyFulfilmentRole(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.createRole("Facility Role", "Facility Based Role Description", asList("Fill shipment"), "Fulfillment");
    verifyCreatedRoleMessage("Facility Role");
    assertEquals(dbWrapper.getListOfRightsForRole("Facility Role"), asList("FACILITY_FILL_SHIPMENT", "VIEW_ORDER"));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyReportingRole(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.createRole("Reporting role", null, asList("Manage Report"), "Reporting");
    verifyCreatedRoleMessage("Reporting role");
    assertEquals(dbWrapper.getListOfRightsForRole("Reporting role"), asList("MANAGE_REPORT"));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyEditRole(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateToRolePage();
    rolesPage.createRole("Facility Role", "Facility Based Role Description", asList("Fill shipment"), "Fulfillment");
    verifyCreatedRoleMessage("Facility Role");
    rolesPage.clickRole("Facility Role");
    assertEquals(rolesPage.getEditRoleHeader(), "Edit role");
    rolesPage.clickRequisitionTypeRole();
    rolesPage.clickContinueButton();
    rolesPage.selectRight("Approve Requisition");
    rolesPage.clickSaveButton();
    assertEquals(rolesPage.getSuccessMessage(), "\"Facility Role\" updated successfully");
    assertEquals(dbWrapper.getListOfRightsForRole("Facility Role"), asList("APPROVE_REQUISITION", "VIEW_REQUISITION"));

    assertEquals(rolesPage.getName(2), "Facility Role");
    assertEquals(rolesPage.getDescription(2), "Facility Based Role Description");
    assertEquals(rolesPage.getRights(2, 1), "Requisition - View");
    assertEquals(rolesPage.getRights(2, 2), "Requisition - Approve");
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
