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
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.*;


@TransactionConfiguration(defaultRollback = true)
@Transactional

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

  @BeforeMethod(groups = {"admin"})
  @Before
  public void setUp() throws Exception {
    super.setup();
  }

  @And("^I create a user:$")
  public void createUser(DataTable userTable) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    List<Map<String, String>> data = userTable.asMaps();
    for (Map map : data)
      userPage.enterAndVerifyUserDetails(map.get("UserName").toString(), map.get("Email").toString(), map.get("FirstName").toString(), map.get("LastName").toString(), baseUrlGlobal, dburlGlobal);
  }

  @When("^I disable user \"([^\"]*)\"$")
  public void disableUser(String user) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    userPage.searchUser(user);
    userPage.clickUserList(user);
    userPage.clickDisableButton();
  }

  @Then("^I should see disable user \"([^\"]*)\" message$")
  public void verifyDisableUser(String user) throws Exception {
    UserPage userPage = new UserPage(testWebDriver);
    userPage.verifyMessage("User \''" + user + "\'' has been disabled");
  }

  @Then("^I should see user not verified$")
  public void notVerifiedUser() throws Exception {
    UserPage userPage = new UserPage(testWebDriver);
    assertEquals("No", userPage.getVerifiedLabel());
  }

  @Then("^I should see user \"([^\"]*)\" verified$")
  public void VerifiedUser(String user) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    userPage.searchUser(user);
    userPage.clickUserList(user);
    assertEquals(userPage.getVerifiedLabel(), "Yes");
  }

  @When("^I enable user \"([^\"]*)\"$")
  public void enableUser(String user) throws Exception {
    HomePage homePage = new HomePage(testWebDriver);
    UserPage userPage = homePage.navigateToUser();
    userPage.searchUser(user);
    userPage.clickUserList(user);
    userPage.clickEnableButton();
  }

  @Then("^I should see enable user \"([^\"]*)\" message$")
  public void verifyEnabledUser(String user) throws Exception {
    UserPage userPage = new UserPage(testWebDriver);
    userPage.verifyMessage("User \''" + user + "\'' has been enabled");
  }

  @When("^I verify user email \"([^\"]*)\"$")
  public void verifyUserEmail(String email) throws Exception {
    dbWrapper.updateUser("abc123", email);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyRightsUponOK(String user, String program, String[] credentials) throws Exception {
    String UPLOADS = "Uploads";
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
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

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testVerifyRightsUponCancel(String user, String program, String[] credentials) throws Exception {
    String UPLOADS = "Uploads";
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
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
  public void testVerifyDuplicateRoleName(String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add("Uploads");
    rolesPage.createRole(ADMIN, ADMIN, userRoleList, false);
    assertEquals("Duplicate Role found", rolesPage.getSaveErrorMsgDiv().getText().trim());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Role-Function")
  public void testVerifyFacilityBasedRole(String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createFacilityBasedRoleWithSuccessMessageExpected("Facility Based Role Name", "Facility Based Role Description");
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testE2EManageRolesAndFacility(String user, String program, String[] credentials, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                            String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                            String facilityCodeFirst, String facilityCodeSecond,
                                            String programFirst, String programSecond, String schedule, String rolename) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program,
      geoZone, facilityType, operatedBy, "500000");
    String facility_code = facilityCodePrefix + date_time;
    String facility_name = facilityNamePrefix + date_time;
    createFacilityPage.verifyMessageOnFacilityScreen(facility_name, "created");

    List<String> userRoleList = new ArrayList<String>();
    userRoleList.add(CREATE_REQUISITION);
    userRoleList.add(AUTHORIZE_REQUISITION);
    userRoleList.add(APPROVE_REQUISITION);
    createRoleAndAssignRights(homePage, userRoleList, LAB_IN_CHARGE, LAB_IN_CHARGE, true);


    RolesPage rolesPage = new RolesPage(testWebDriver);
    rolesPage.clickARole(LAB_IN_CHARGE);
    rolesPage.verifyAdminRoleRadioNonEditable();
    rolesPage.verifyRoleSelected(userRoleList);
    homePage.navigateRoleAssignments();

    dbWrapper.insertSupervisoryNode(facility_code, "N1", "Node 1", "null");

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPage = new UserPage(testWebDriver);
    createUserAndAssignRoles(homePage, passwordUsers, "Jasmine_Doe@openlmis.com", "Jasmine", "Doe", LAB_IN_CHARGE, facility_code, program, "Node 1", LAB_IN_CHARGE, "REQUISITION");

    SetupDeliveryZoneRolesAndRights(deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule, rolename);
    userPage.clickViewHere();
    userPage.enterDeliveryZoneData(deliveryZoneNameFirst, programFirst, "");
    userPage.clickSaveButton();
    userPage.clickViewHere();
    testWebDriver.getElementByXpath("//a[contains(text(),'Delivery zones')]").click();
    testWebDriver.sleep(1000);
    assertEquals(deliveryZoneNameFirst, userPage.getAddedDeliveryZoneLabel());

    testWebDriver.getElementByXpath("//a[contains(text(),'Home Facility Roles')]").click();
    testWebDriver.sleep(500);
    userPage.removeRole(1, false);
    testWebDriver.getElementByXpath("//a[contains(text(),'Supervisory Roles')]").click();
    testWebDriver.sleep(500);
    userPage.verifyRolePresent(LAB_IN_CHARGE);
    userPage.removeRole(1, false);
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    userPage.clickRemoveButtonWithOk(2);

    testWebDriver.getElementByXpath("//a[contains(text(),'Home Facility Roles')]").click();
    testWebDriver.sleep(500);
    userPage.clickRemoveButtonWithOk(1);
    userPage.clickSaveButton();
    userPage.clickViewHere();
    testWebDriver.getElementByXpath("//a[contains(text(),'Home Facility Roles')]").click();
    testWebDriver.sleep(500);
    userPage.verifyRoleNotPresent(LAB_IN_CHARGE);
    userPage.verifyRemoveNotPresent();
    verifyPUSHProgramNotAvailableForHomeFacilityRolesAndSupervisoryRoles(userPage);

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void testCreateUserAndVerifyOnManageDistributionScreen(String user, String program, String[] credentials, String deliveryZoneCodeFirst, String deliveryZoneCodeSecond,
                                                                String deliveryZoneNameFirst, String deliveryZoneNameSecond,
                                                                String facilityCodeFirst, String facilityCodeSecond,
                                                                String programFirst, String programSecond, String schedule, String rolename) throws Exception {
    SetupDeliveryZoneRolesAndRights(deliveryZoneCodeFirst, deliveryZoneCodeSecond, deliveryZoneNameFirst, deliveryZoneNameSecond, facilityCodeFirst, facilityCodeSecond, programFirst, programSecond, schedule, rolename);

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPage = homePage.navigateToUser();
    String email = "Jasmine_Doe@openlmis.com";
    userPage.enterAndVerifyUserDetails(LAB_IN_CHARGE, email, "Jasmine", "Doe", baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, email);

    userPage.enterDeliveryZoneDataWithoutHomeAndSupervisoryRolesAssigned(deliveryZoneNameFirst, programFirst, FIELD_COORDINATOR);
    userPage.clickSaveButton();
    userPage.clickViewHere();

    assertEquals(deliveryZoneNameFirst, dbWrapper.getDeliveryZoneNameAssignedToUser(LAB_IN_CHARGE));
    assertEquals(FIELD_COORDINATOR, dbWrapper.getRoleNameAssignedToUser(LAB_IN_CHARGE));

  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function")
  public void testCreateSearchResetPasswordUser(String user, String program, String[] credentials) throws Exception {

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
    UserPage userPage = homePage.navigateToUser();

    String email = "Jasmine_Doe@openlmis.com";
    userPage.enterAndVerifyUserDetails(LAB_IN_CHARGE, email, "Jasmine", "Doe", baseUrlGlobal, dburlGlobal);
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
    //userPage.verifyResetPassword();

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

  private String createUserAndAssignRoles(HomePage homePage, String passwordUsers, String userEmail, String userFirstName, String userLastName, String userUserName, String facility, String program, String supervisoryNode, String role, String roleType) throws IOException, SQLException {
    UserPage userPage = homePage.navigateToUser();
    String userID = userPage.enterAndVerifyUserDetails(userUserName, userEmail, userFirstName, userLastName, baseUrlGlobal, dburlGlobal);
    dbWrapper.updateUser(passwordUsers, userEmail);

    userPage.enterMyFacilityAndMySupervisedFacilityData(userFirstName, userLastName, facility, program, supervisoryNode, role, roleType);
    return userID;
  }


  private void createRoleAndAssignRights(HomePage homePage, List<String> userRoleList, String roleName, String roleDescription, boolean programDependent) throws IOException {
    RolesPage rolesPage = homePage.navigateRoleAssignments();
    rolesPage.createRoleWithSuccessMessageExpected(roleName, roleDescription, userRoleList, programDependent);
  }

  private void verifyPUSHProgramNotAvailableForHomeFacilityRolesAndSupervisoryRoles(UserPage userPage) throws IOException, SQLException {
    assertFalse(userPage.getAllProgramsHomeFacility().contains("VACCINES"));
    assertFalse(userPage.getAllProgramsToSupervise().contains("VACCINES"));
  }

  @AfterMethod(groups = "functional2")
  @After
  public void tearDown() throws Exception {
    testWebDriver.sleep(500);
    if (!testWebDriver.getElementById("username").isDisplayed()) {
      HomePage homePage = new HomePage(testWebDriver);
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
