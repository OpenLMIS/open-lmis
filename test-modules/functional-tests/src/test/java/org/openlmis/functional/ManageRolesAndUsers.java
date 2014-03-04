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


import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static java.util.Arrays.asList;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageRolesAndUsers extends TestCaseHelper {

  public static final String LAB_IN_CHARGE = "Lab-in-charge";
  public static final String AUTHORIZE_REQUISITION = "Authorize Requisition";
  public static final String CREATE_REQUISITION = "Create Requisition";
  public static final String APPROVE_REQUISITION = "Approve Requisition";
  public static final String MANAGE_DISTRIBUTION = "Manage Distribution";
  public static final String FIELD_COORDINATOR = "Field Co-Ordinator";
  public static final String ADMIN = "admin";
  public static final String geoZone = "Ngorongoro";
  public static final String facilityType = "Lvl3 Hospital";
  public static final String operatedBy = "MoH";
  public static final String facilityCodePrefix = "FCcode";
  public static final String facilityNamePrefix = "FCname";
  public static final String warehouseRole = "SHIPMENT";

  UserPage userPage;
  LoginPage loginPage;
  HomePage homePage;
  RolesPage rolesPage;

  @BeforeMethod(groups = {"admin123"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    userPage = PageObjectFactory.getUserPage(testWebDriver);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    homePage = PageObjectFactory.getHomePage(testWebDriver);
    rolesPage = PageObjectFactory.getRolesPage(testWebDriver);
  }

  @And("^I create a user:$")
  public void createUser(DataTable userTable) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
      userPage.enterUserDetails(map.get("UserName").toString(), map.get("Email").toString(),
        map.get("FirstName").toString(), map.get("LastName").toString());
    userPage.clickViewHere();
  }

  @When("^I disable user \"([^\"]*)\"$")
  public void disableUser(String user) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    userPage.searchUser(user);
    userPage.clickEditUserButton();
    userPage.clickDisableButton();
  }

  @Then("^I should see disable user \"([^\"]*)\" message$")
  public void verifyDisableUser(String user) {
    UserPage userPage = PageObjectFactory.getUserPage(testWebDriver);
    userPage.verifyMessage("User \"" + user + "\" has been disabled");
  }

  @Then("^I should see user not verified$")
  public void notVerifiedUser() {
    UserPage userPage = PageObjectFactory.getUserPage(testWebDriver);
    assertEquals("No", userPage.getVerifiedLabel());
  }

  @Then("^I should see user \"([^\"]*)\" verified$")
  public void VerifiedUser(String user) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    userPage.searchUser(user);
    userPage.clickUserList();
    assertEquals(userPage.getVerifiedLabel(), "Yes");
  }

  @When("^I enable user \"([^\"]*)\"$")
  public void enableUser(String user) {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    userPage.searchUser(user);
    userPage.clickUserList();
    userPage.clickEnableButton();
  }

  @Then("^I should see enable user \"([^\"]*)\" message$")
  public void verifyEnabledUser(String user) {
    UserPage userPage = PageObjectFactory.getUserPage(testWebDriver);
    userPage.verifyMessage("User \"" + user + "\" has been enabled");
  }

  @When("^I verify user email \"([^\"]*)\"$")
  public void verifyUserEmail(String email) throws SQLException {
    dbWrapper.updateUser("abc123", email);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyRightsUponOK(String[] credentials) {
    String UPLOADS = "Uploads";
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
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
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyRightsUponCancel(String[] credentials) {
    String UPLOADS = "Uploads";
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.getCreateNewRoleButton().click();
    testWebDriver.waitForElementToAppear(rolesPage.getAllocationRoleType());
    rolesPage.getWebElementMap().get(UPLOADS).click();
    rolesPage.getAllocationRoleType().click();
    rolesPage.clickCancelButtonOnModal();
    assertTrue(rolesPage.getWebElementMap().get(UPLOADS).isSelected());
    assertFalse(rolesPage.getWebElementMap().get(APPROVE_REQUISITION).isEnabled());
    assertTrue(rolesPage.getWebElementMap().get(UPLOADS).isEnabled());
    assertFalse(rolesPage.getWebElementMap().get(MANAGE_DISTRIBUTION).isEnabled());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyDuplicateRoleName(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleList = new ArrayList<>();
    userRoleList.add("Uploads");
    rolesPage.createRole(ADMIN, ADMIN, userRoleList, false);
    assertEquals("Duplicate Role found", rolesPage.getSaveErrorMsgDiv().getText().trim());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyFacilityBasedRole(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createFacilityBasedRole("Facility Based Role Name", "Facility Based Role Description");
    rolesPage.verifyCreatedRoleMessage("Facility Based Role Name");
  }

  public void testVerifyTabsForUserWithoutRights(String userName, String password) {
    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userName, password);
    assertTrue(homePage.isHomeMenuTabDisplayed());
    assertFalse(homePage.isRequisitionsMenuTabDisplayed());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageRolesAndFacility(String user, String program, String[] credentials, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                            String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                            String facilityCodeFirst, String facilityCodeSecond,
                                            String programFirst, String programSecond, String schedule, String roleName) throws SQLException {
    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    ManageFacilityPage manageFacilityPage = homePage.navigateManageFacility();
    homePage.clickCreateFacilityButton();
    String date_time = manageFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, "500000");
    String facility_code = facilityCodePrefix + date_time;
    String facility_name = facilityNamePrefix + date_time;
    manageFacilityPage.verifyMessageOnFacilityScreen(facility_name, "created");

    List<String> userRoleList = asList(CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION);
    createRoleAndAssignRights(userRoleList, LAB_IN_CHARGE, LAB_IN_CHARGE, "Requisition");

    RolesPage rolesPage = PageObjectFactory.getRolesPage(testWebDriver);
    rolesPage.clickARole(LAB_IN_CHARGE);
    rolesPage.verifyAdminRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleList);
    homePage.navigateRoleAssignments();

    dbWrapper.insertSupervisoryNode(facility_code, "N1", "Node 1", "null");

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    setupWarehouseRolesAndRights(facilityCodeFirst, facilityCodeSecond, programFirst, schedule, "SHIPMENT");
    String warehouseName = dbWrapper.getAttributeFromTable("facilities", "name", "code", facilityCodeFirst);
    createUserAndAssignRoles(passwordUsers, "Jasmine_Doe@openlmis.com", "Jasmine", "Doe", LAB_IN_CHARGE, facility_code, program, "Node 1", LAB_IN_CHARGE, "REQUISITION");
    userPage.assignWarehouse(warehouseName, warehouseRole);
    userPage.clickSaveButton();
    userPage.verifyUserUpdated("Jasmine", "Doe");
    setupDeliveryZoneRolesAndRightsAfterWarehouse(deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst,
      deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule,
      roleName);
    userPage.clickViewHere();
    userPage.enterDeliveryZoneData(deliveryZoneNameFirst, programFirst, roleName);
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.clickDeliveryZonesAccordion();
    testWebDriver.sleep(1000);
    assertEquals(deliveryZoneNameFirst, userPage.getAddedDeliveryZoneLabel());

    userPage.clickHomeFacilityRolesAccordion();
    testWebDriver.sleep(500);
    userPage.removeRole(1, false);
    userPage.clickSupervisoryRolesAccordion();
    testWebDriver.sleep(500);
    userPage.verifyRolePresent(LAB_IN_CHARGE);
    userPage.removeRole(1, false);
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    userPage.clickRemoveButtonWithOk(2);

    userPage.clickWarehouseRolesAccordion();
    userPage.verifyRolePresent(warehouseRole);
    userPage.removeRole(1, false);
    userPage.verifyRoleNotPresent(warehouseRole);
    userPage.clickRemoveButtonWithOk(2);

    userPage.clickHomeFacilityRolesAccordion();
    testWebDriver.sleep(500);
    userPage.clickRemoveButtonWithOk(1);
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.clickHomeFacilityRolesAccordion();
    testWebDriver.sleep(500);
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    userPage.verifyRemoveNotPresent();
    verifyPushProgramNotAvailableForHomeFacilityRolesAndSupervisoryRoles();
    userPage.clickWarehouseRolesAccordion();
    testWebDriver.sleep(500);
    userPage.verifyRoleNotPresent(warehouseRole);
    userPage.verifyRemoveNotPresent();

    verifyWarehouseAvailableForWarehouseRoles(facilityCodeFirst, warehouseName);

    userPage.clickDeliveryZonesAccordion();
    testWebDriver.sleep(500);
    userPage.clickRemoveButtonWithOk(1);
    userPage.clickSaveButton();
    testVerifyTabsForUserWithoutRights(LAB_IN_CHARGE, "Admin123");
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testCreateUserAndVerifyOnManageDistributionScreen(String user, String program, String[] credentials, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                                String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                                String facilityCodeFirst, String facilityCodeSecond,
                                                                String programFirst, String programSecond, String schedule, String roleName) throws SQLException {
    setupDeliveryZoneRolesAndRights(deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule, roleName);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPage = homePage.navigateToUser();
    String email = "Jasmine_Doe@openlmis.com";
    userPage.enterUserDetails(LAB_IN_CHARGE, email, "Jasmine", "Doe");
    userPage.clickViewHere();
    dbWrapper.updateUser(passwordUsers, email);

    userPage.enterDeliveryZoneDataWithoutHomeAndSupervisoryRolesAssigned(deliveryZoneNameFirst, programFirst, FIELD_COORDINATOR);
    userPage.clickSaveButton();
    userPage.clickViewHere();

    assertEquals(deliveryZoneNameFirst, dbWrapper.getDeliveryZoneNameAssignedToUser(LAB_IN_CHARGE));
    assertEquals(FIELD_COORDINATOR, dbWrapper.getRoleNameAssignedToUser(LAB_IN_CHARGE));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testRestrictLogin(String[] credentials) throws SQLException {
    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UserPage userPage = homePage.navigateToUser();
    String email = "Jasmine_Doe@openlmis.com";
    userPage.enterUserDetails(LAB_IN_CHARGE, email, "Jasmine", "Doe");
    dbWrapper.updateUser(passwordUsers, email);
    userPage.clickViewHere();
    userPage.clickRestrictLoginYes();
    userPage.clickSaveButton();
    homePage.logout(baseUrlGlobal);
    loginPage.loginAs(LAB_IN_CHARGE, credentials[1]);
    assertEquals(loginPage.getLoginErrorMessage(),
      "The username or password you entered is incorrect. Please try again.");
    loginPage.clearUserName();
    loginPage.loginAs(credentials[0], credentials[1]);
    homePage.navigateToUser();
    userPage.searchUser(LAB_IN_CHARGE);
    userPage.clickUserList();
    userPage.clickRestrictLoginNo();
    userPage.clickSaveButton();
    homePage.logout(baseUrlGlobal);
    loginPage.loginAs(LAB_IN_CHARGE, credentials[1]);
    homePage.verifyLoggedInUser(LAB_IN_CHARGE);
  }

  @Test(groups = {"admin123"}, dataProvider = "Data-Provider-Role-Function")
  public void testCreateSearchResetPasswordUser(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPage = homePage.navigateToUser();

    String email = "Jasmine_Doe@openlmis.com";
    userPage.enterUserDetails(LAB_IN_CHARGE, email, "Jasmine", "Doe");
    userPage.clickViewHere();
    dbWrapper.updateUser(passwordUsers, email);

    homePage.navigateToUser();
    userPage.searchUser(email);
    userPage.verifyUserOnList(email);

    homePage.navigateToUser();
    userPage.searchUser(LAB_IN_CHARGE);
    userPage.verifyUserOnList(LAB_IN_CHARGE);

    homePage.navigateToUser();
    userPage.searchUser("Jasmine Doe");
    userPage.verifyUserOnList("Jasmine Doe");

    userPage.focusOnFirstUserLink();

    userPage.clickEditUser();
    userPage.clickDisableButton();
    homePage.navigateToUser();
    userPage.searchUser(LAB_IN_CHARGE);
    userPage.focusOnFirstUserLink();
    userPage.verifyDisabledResetPassword();
    userPage.clickEditUser();
    userPage.clickEnableButton();

    homePage.navigateToUser();
    userPage.searchUser(LAB_IN_CHARGE);
    userPage.focusOnFirstUserLink();
    userPage.resetPassword("abcd1234", "abcd1234");

    homePage.logout(baseUrlGlobal);
    loginPage.loginAs(LAB_IN_CHARGE, "abcd1234");
    homePage.verifyLoggedInUser(LAB_IN_CHARGE);
  }

  private void createUserAndAssignRoles(String passwordUsers, String userEmail, String userFirstName, String userLastName,
                                        String userUserName, String facility, String program, String supervisoryNode, String role,
                                        String roleType) throws SQLException {
    UserPage userPage = homePage.navigateToUser();
    userPage.enterUserDetails(userUserName, userEmail, userFirstName, userLastName);
    userPage.verifyUserCreated(userFirstName, userLastName);
    userPage.clickViewHere();
    dbWrapper.updateUser(passwordUsers, userEmail);

    userPage.ExpandAll();
    userPage.verifyExpandAll();
    userPage.collapseAll();
    userPage.verifyCollapseAll();

    userPage.enterMyFacilityAndMySupervisedFacilityData(facility, program, supervisoryNode, role, roleType);
  }


  private void createRoleAndAssignRights(List<String> userRoleList, String roleName, String roleDescription, String programDependent) {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createRole(roleName, roleDescription, userRoleList, programDependent);
    rolesPage.verifyCreatedRoleMessage(roleName);
  }

  private void verifyPushProgramNotAvailableForHomeFacilityRolesAndSupervisoryRoles() throws SQLException {
    assertFalse(userPage.getAllProgramsHomeFacility().contains("VACCINES"));
    userPage.clickSupervisoryRolesAccordion();
    assertFalse(userPage.getAllProgramsToSupervise().contains("VACCINES"));
  }

  private void verifyWarehouseAvailableForWarehouseRoles(String FacilityCode, String warehouseName) throws SQLException {
    assertTrue(userPage.getAllWarehouseToSelect().contains(warehouseName));
    assertFalse(userPage.getAllWarehouseToSelect().contains(facilityNamePrefix));
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "name", warehouseName);
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.clickWarehouseRolesAccordion();
    assertFalse(userPage.getAllWarehouseToSelect().contains(warehouseName));
    dbWrapper.updateFieldValue("facilities", "enabled", "true", "name", warehouseName);
    dbWrapper.updateFieldValue("facilities", "active", "true", "code", FacilityCode);
    userPage.clickSaveButton();
    userPage.clickViewHere();
    userPage.clickWarehouseRolesAccordion();
    assertTrue(userPage.getAllWarehouseToSelect().contains(warehouseName));
  }

  @AfterMethod(groups = "admin123")
  public void tearDown() throws SQLException {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
      homePage.logout(baseUrlGlobal);
      dbWrapper.deleteData();
      dbWrapper.closeConnection();
    }
  }

  @DataProvider(name = "Data-Provider-Function")
  public Object[][] parameterIntTestProvider() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }

  @DataProvider(name = "Data-Provider-Role-Function")
  public Object[][] parameterIntRoleTestProvider() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}, "DZ1", "DZ2", "Delivery Zone First", "Delivery Zone Second",
        "F10", "F11", "VACCINES", "TB", "M", "Field Co-Ordinator"}
    };
  }
}
