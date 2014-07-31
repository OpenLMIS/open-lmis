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

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.pageobjects.*;
import org.openlmis.restapi.domain.Agent;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.lang.String.format;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.testng.Assert.assertNull;


public class CreateUpdateCHW extends JsonUtility {
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String FULL_JSON_TXT_FILE_NAME = "AgentValid.txt";
  public static final String userEmail = "Fatim_Doe@openlmis.com";
  public static final String CREATE_URL = "http://localhost:9091/rest-api/agents.json";
  public static final String UPDATE_URL = "http://localhost:9091/rest-api/agents/";
  public static final String commTrackUser = "commTrack";
  public static final String PHONE_NUMBER = "0099887766";
  public static final String DEFAULT_AGENT_NAME = "Agent A1";
  public static final String DEFAULT_PARENT_FACILITY_CODE = "F10";
  public static final String ACTIVE_STATUS = "true";
  public static final String DEFAULT_AGENT_CODE = "A2";
  public static final String FALSE_FLAG = "f";
  public static final String TRUE_FLAG = "t";
  public static final String JSON_EXTENSION = ".json";

  LoginPage loginPage;

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(true);
    dbWrapper.updateRestrictLogin("commTrack", true);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();

  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldNotShowVirtualFacilityOnManageUserScreen(String[] credentials) throws SQLException {
    dbWrapper.updateFieldValue("facilities", "virtualFacility", ACTIVE_STATUS, "code", DEFAULT_PARENT_FACILITY_CODE);
    String parentFacilityId = dbWrapper.getAttributeFromTable("facilities", "id", "code", DEFAULT_PARENT_FACILITY_CODE);
    dbWrapper.updateFieldValue("facilities", "parentFacilityId", parentFacilityId, "code", DEFAULT_PARENT_FACILITY_CODE);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UserPage userPage = homePage.navigateToUser();
    userPage.enterUserDetails("storeInCharge", userEmail, "Fatim", "Doe");
    userPage.clickViewHere();
    userPage.enterUserHomeFacility(DEFAULT_PARENT_FACILITY_CODE);
    assertTrue("No match found link should show up", userPage.isNoFacilityResultMessageDisplayed());
    homePage.logout(baseUrlGlobal);

    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity = client.SendJSON("", format("http://localhost:9091/rest-api/facilities/%s", "F10"), "GET", commTrackUser, "Admin123");
    String response = responseEntity.getResponse();
    assertTrue("Response entity : " + response, response.contains("\"code\":\"F10\""));
    assertTrue("Response entity : " + response, response.contains("\"name\":\"Village Dispensary\""));
    assertTrue("Response entity : " + response, response.contains("\"facilityType\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + response, response.contains("\"virtualFacility\":true"));
    assertFalse("Response entity : " + response, response.contains("\"parentFacility\":" + parentFacilityId));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyFacilityUpload(String[] credentials) throws FileNotFoundException, SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadGeographicZone("QA_Geographic_Data_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilities("QA_facilities_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(FALSE_FLAG, dbWrapper.getAttributeFromTable("facilities", "virtualFacility", "code", "facilityf10"));
    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldManageFacility(String[] credentials) throws IOException, SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    String agentCode = "ABC";

    agentJson.setAgentCode(agentCode);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    FacilityPage facilityPage = homePage.navigateManageFacility();
    facilityPage.searchFacility(agentCode);
    facilityPage.clickFirstFacilityList();
    facilityPage.disableFacility(agentCode, DEFAULT_AGENT_NAME);

    facilityPage.verifyDisabledFacility(agentCode, DEFAULT_AGENT_NAME);
    HomePage homePageRestore = facilityPage.enableFacility();
    assertEquals(facilityPage.getEnabledFacilityText(), "Yes");
    FacilityPage facilityPageRestore = homePageRestore.navigateManageFacility();
    facilityPageRestore.searchFacility(agentCode);
    facilityPageRestore.clickFirstFacilityList();
    facilityPage.saveFacility();
    facilityPage.verifyMessageOnFacilityScreen(DEFAULT_AGENT_NAME, "updated");
    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", "virtualFacility", "code", agentCode));
    homePage.logout(baseUrlGlobal);
  }


  @Test(groups = {"webserviceSmoke"})
  public void testChwFeedWithValidParentFacilityCode() throws IOException, SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getRequisitionGroupId(DEFAULT_PARENT_FACILITY_CODE),
      dbWrapper.getRequisitionGroupId(DEFAULT_AGENT_CODE));
    List<Integer> listOfProgramsSupportedByParentFacility = dbWrapper.getAllProgramsOfFacility(
      DEFAULT_PARENT_FACILITY_CODE);
    List<Integer> listOfProgramsSupportedByVirtualFacility = dbWrapper.getAllProgramsOfFacility(DEFAULT_AGENT_CODE);
    Set<Integer> setOfProgramsSupportedByParentFacility = new HashSet<>();
    setOfProgramsSupportedByParentFacility.addAll(listOfProgramsSupportedByParentFacility);
    Set<Integer> setOfProgramsSupportedByVirtualFacility = new HashSet<>();
    setOfProgramsSupportedByVirtualFacility.addAll(listOfProgramsSupportedByVirtualFacility);
    assertTrue(setOfProgramsSupportedByParentFacility.equals(setOfProgramsSupportedByVirtualFacility));
    assertEquals(listOfProgramsSupportedByParentFacility.size(), listOfProgramsSupportedByVirtualFacility.size());
    for (Integer programId : listOfProgramsSupportedByParentFacility) {
      assertEquals(
        dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, DEFAULT_PARENT_FACILITY_CODE, "active"),
        dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, DEFAULT_AGENT_CODE, "active"));
      assertEquals(dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId, DEFAULT_PARENT_FACILITY_CODE),
        dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId, DEFAULT_AGENT_CODE));
    }
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedForEnableScenarios() throws IOException, SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");

    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    agentJson.setActive("false");
    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
      responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", DEFAULT_AGENT_CODE);

    ResponseEntity responseEntityEnabledFalse = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntityEnabledFalse.getResponse(),
      responseEntityEnabledFalse.getResponse().contains(
        "{\"error\":\"CHW cannot be updated as it has been deleted\"}")
    );

  }

  @Test(groups = {"webservice"})
  public void testChwFeedCreateWithInvalidDataLength() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber("0099887766759785759859757757887");
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"error\":\"Incorrect data length\"}"));
  }

  @Test(groups = {"webservice"})
  public void testChwFeedUpdateWithInvalidDataLength() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber("0099887");
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    agentJson.setPhoneNumber("0099887766759785759859757757887");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
      responseEntityUpdated.getResponse().contains("{\"error\":\"Incorrect data length\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateStatusOfAgentCode() throws IOException, SQLException {
    String AGENT_CODE = "ABCD";
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    agentJson.setActive("false");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
      responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

    assertEquals(FALSE_FLAG, dbWrapper.getAttributeFromTable("facilities", "active", "code", AGENT_CODE));
  }

  @Test(groups = {"webservice"})
  public void testVerifyFieldsAfterChangeInParentFacilityCode() throws IOException, SQLException {
    String typeId = "typeId";
    String geographicZoneId = "geographicZoneId";
    String parentFacilityId = "parentFacilityId";
    String agentCode = "ABCDE";
    String firstParentFacility = DEFAULT_PARENT_FACILITY_CODE;
    String updateParentFacility = "F11";
    String id = "id";


    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(agentCode);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(firstParentFacility);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getAttributeFromTable("facilities", typeId, "code", firstParentFacility),
      dbWrapper.getAttributeFromTable("facilities", typeId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", firstParentFacility),
      dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", id, "code", firstParentFacility),
      dbWrapper.getAttributeFromTable("facilities", parentFacilityId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "name", "code", agentCode), DEFAULT_AGENT_NAME);
    assertNotEquals(dbWrapper.getAttributeFromTable("facilities", "id", "code", agentCode),
      dbWrapper.getAttributeFromTable("facilities", "id", "code", firstParentFacility));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "code", "code", agentCode), agentCode);
    assertNull(dbWrapper.getAttributeFromTable("facilities", "description", "code", agentCode));
    assertNull(dbWrapper.getAttributeFromTable("facilities", "gln", "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "mainPhone", "code", agentCode), PHONE_NUMBER);
    assertNull(dbWrapper.getAttributeFromTable("facilities", "fax", "code", agentCode));
    assertNull(dbWrapper.getAttributeFromTable("facilities", "address1", "code", agentCode));
    assertNull(dbWrapper.getAttributeFromTable("facilities", "address2", "code", agentCode));
    assertNull(dbWrapper.getAttributeFromTable("facilities", "catchmentPopulation", "code", agentCode));
    assertNull(dbWrapper.getAttributeFromTable("facilities", "operatedById", "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "active", "code", agentCode), "t");
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "enabled", "code", agentCode), TRUE_FLAG);
    assertEquals(dbWrapper.getAttributeFromTable("facilities", "virtualFacility", "code", agentCode), TRUE_FLAG);
    assertEquals(dbWrapper.getRequisitionGroupId(firstParentFacility), dbWrapper.getRequisitionGroupId(agentCode));

    agentJson.setParentFacilityCode(updateParentFacility);

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + agentCode + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
      responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", typeId, "code", updateParentFacility),
      dbWrapper.getAttributeFromTable("facilities", typeId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", updateParentFacility),
      dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", id, "code", updateParentFacility),
      dbWrapper.getAttributeFromTable("facilities", parentFacilityId, "code", agentCode));
    assertEquals(dbWrapper.getRequisitionGroupId(updateParentFacility), dbWrapper.getRequisitionGroupId(agentCode));

    List<Integer> listOfProgramsSupportedByParentFacility = dbWrapper.getAllProgramsOfFacility(updateParentFacility);
    List<Integer> listOfProgramsSupportedByVirtualFacility = dbWrapper.getAllProgramsOfFacility(agentCode);
    Set<Integer> setOfProgramsSupportedByParentFacility = new HashSet<>();
    setOfProgramsSupportedByParentFacility.addAll(listOfProgramsSupportedByParentFacility);
    Set<Integer> setOfProgramsSupportedByVirtualFacility = new HashSet<>();
    setOfProgramsSupportedByVirtualFacility.addAll(listOfProgramsSupportedByVirtualFacility);
    assertTrue(setOfProgramsSupportedByParentFacility.equals(setOfProgramsSupportedByVirtualFacility));
    assertEquals(listOfProgramsSupportedByParentFacility.size(), listOfProgramsSupportedByVirtualFacility.size());
    for (Integer programId : listOfProgramsSupportedByParentFacility) {
      assertEquals(dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, updateParentFacility, "active"),
        dbWrapper.getProgramFieldForProgramIdAndFacilityCode(programId, agentCode, "active"));
      assertEquals(dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId, updateParentFacility),
        dbWrapper.getProgramStartDateForProgramIdAndFacilityCode(programId, agentCode));
    }
  }

  @Test(groups = {"webservice"})
  public void testVerifyFieldsAfterCHWCreation() throws IOException, SQLException {
    String typeId = "typeId";
    String geographicZoneId = "geographicZoneId";
    String parentFacilityId = "parentFacilityId";
    String agentCode = "commtrk";
    String agentName = DEFAULT_AGENT_NAME;
    String agentNameUpdated = "AgentJyot";
    String firstParentFacility = DEFAULT_PARENT_FACILITY_CODE;
    String firstParentFacilityUpdated = "F11";
    String code = "code";
    String name = "name";
    String id = "id";
    String mainPhone = "mainPhone";
    String phoneNumber = PHONE_NUMBER;
    String phoneNumberUpdated = "12345678";
    String active = "active";
    String virtualFacility = "virtualFacility";
    String sdp = "sdp";
    String enabled = "enabled";


    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(agentCode);
    agentJson.setAgentName(agentName);
    agentJson.setParentFacilityCode(firstParentFacility);
    agentJson.setPhoneNumber(phoneNumber);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getAttributeFromTable("facilities", typeId, "code", firstParentFacility),
      dbWrapper.getAttributeFromTable("facilities", typeId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", firstParentFacility),
      dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", id, "code", firstParentFacility),
      dbWrapper.getAttributeFromTable("facilities", parentFacilityId, "code", agentCode));
    assertEquals(agentCode, dbWrapper.getAttributeFromTable("facilities", code, "code", agentCode));
    assertEquals(agentName, dbWrapper.getAttributeFromTable("facilities", name, "code", agentCode));
    assertEquals(phoneNumber, dbWrapper.getAttributeFromTable("facilities", mainPhone, "code", agentCode));

    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", active, "code", agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", virtualFacility, "code", agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", sdp, "code", agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", enabled, "code", agentCode));

    agentJson.setAgentName(agentNameUpdated);
    agentJson.setParentFacilityCode(firstParentFacilityUpdated);
    agentJson.setPhoneNumber(phoneNumberUpdated);
    agentJson.setActive("false");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + agentCode + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
      responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", typeId, "code", firstParentFacilityUpdated),
      dbWrapper.getAttributeFromTable("facilities", typeId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", firstParentFacilityUpdated),
      dbWrapper.getAttributeFromTable("facilities", geographicZoneId, "code", agentCode));
    assertEquals(dbWrapper.getAttributeFromTable("facilities", id, "code", firstParentFacilityUpdated),
      dbWrapper.getAttributeFromTable("facilities", parentFacilityId, "code", agentCode));
    assertEquals(agentCode, dbWrapper.getAttributeFromTable("facilities", code, "code", agentCode));
    assertEquals(agentNameUpdated, dbWrapper.getAttributeFromTable("facilities", name, "code", agentCode));
    assertEquals(phoneNumberUpdated, dbWrapper.getAttributeFromTable("facilities", mainPhone, "code", agentCode));
    assertEquals(FALSE_FLAG, dbWrapper.getAttributeFromTable("facilities", active, "code", agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", virtualFacility, "code", agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", sdp, "code", agentCode));
    assertEquals(TRUE_FLAG, dbWrapper.getAttributeFromTable("facilities", enabled, "code", agentCode));
  }

  @Test(groups = {"webservice"})
  public void testCreateChwFeedWithParentFacilityCodeAsVirtualFacility() throws SQLException, IOException {
    dbWrapper.updateFieldValue("facilities", "virtualFacility", ACTIVE_STATUS, "code", DEFAULT_PARENT_FACILITY_CODE);
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedWithParentFacilityCodeAsVirtualFacility() throws IOException, SQLException {
    String facilityCode = DEFAULT_PARENT_FACILITY_CODE;

    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(facilityCode);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    dbWrapper.updateFieldValue("facilities", "virtualFacility", ACTIVE_STATUS, "code", DEFAULT_PARENT_FACILITY_CODE);

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
      responseEntityUpdated.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));
  }


  @Test(groups = {"webservice"})
  public void testChwFeedWithAgentCodeAlreadyRegistered() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"error\":\"Agent already registered\"}"));
  }


  @Test(groups = {"webservice"})
  public void testUpdateShouldVerifyAgentIsNotAVirtualFacility() throws IOException {
    String Agent_code = "F11";
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + Agent_code + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"error\":\"Agent is not a virtual facility\"}"));
  }

  @Test(groups = {"webservice"})
  public void testCreateChwFeedWithInvalidParentFacilityCode() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode("A10");
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"error\":\"Invalid Facility code\"}"));
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedWithInvalidParentFacilityCode() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    agentJson.setParentFacilityCode("A10");
    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(),
      responseEntityUpdated.getResponse().contains("{\"error\":\"Invalid Facility code\"}"));
  }

  @Test(groups = {"webservice"})
  public void testMalformedJson() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedJson = getJsonStringFor(agentJson).replace(':', ';');

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedJson,
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");

    assertEquals(responseEntityUpdated.getStatus(), SC_BAD_REQUEST);

  }


  @Test(groups = {"webservice"})
  public void testBlankJson() {
    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity = client.SendJSON("{}", CREATE_URL, POST, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenFieldIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"Agent A1\",", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, CREATE_URL, POST, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenFieldIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"Agent A1\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString,
      responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldValueWhenFieldIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"Agent A1\",", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, CREATE_URL, POST, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldValueWhenFieldIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);


    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    String modifiedString = getJsonStringFor(agentJson).replaceFirst("\"agentName\":\"Agent A1\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");


    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString,
      responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenActiveFieldIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(", \"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, CREATE_URL, POST, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateAgentCodeNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);


    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " +
      getJsonStringFor(agentJson), responseEntity.getResponse().contains("{\"error\":\"Invalid agent code\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");


    String modifiedString = getJsonStringFor(agentJson).replaceFirst(",\"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenFieldValueIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(DEFAULT_AGENT_NAME, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, CREATE_URL, POST, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenFieldValueIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    String modifiedString = getJsonStringFor(agentJson).replaceFirst(DEFAULT_AGENT_NAME, "");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString,
      responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenActiveFieldValueIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, CREATE_URL, POST, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldValueIsNotPresent() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");


    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldValueIsNotCorrect() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");


    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString, UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("Active should be True/False"));

  }

  @Test(groups = {"webservice"})
  public void testUpdatedSuccessfully() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser, "Admin123");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUnrecognizedField() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst("phoneNumber", "phonenumber");

    ResponseEntity responseEntity = client.SendJSON(modifiedString, CREATE_URL, POST, commTrackUser, "Admin123");


    assertEquals(responseEntity.getStatus(), SC_BAD_REQUEST);
  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForCreateCHW() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode("casesensitive");
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    agentJson.setAgentCode("CASESENSITIVE");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " updated json : " +
        getJsonStringFor(agentJson),
      responseEntityUpdated.getResponse().contains("{\"error\":\"Agent already registered\"}")
    );

  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForUpdateCHW() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    String agent_code = "casesensitive";
    agentJson.setAgentCode(agent_code);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    String agent_code_updated = "CASESENSITIVE";
    agentJson.setAgentCode(agent_code_updated);


    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + agent_code_updated + JSON_EXTENSION, PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " updated json : " +
        getJsonStringFor(agentJson),
      responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}")
    );

  }

  @Test(groups = {"webservice"})
  public void testInvalidActiveFieldOption() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(agentJson).replaceFirst(ACTIVE_STATUS, "truefalse");

    ResponseEntity responseEntity = client.SendJSON(modifiedString, UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT, commTrackUser, "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString,
      responseEntity.getResponse().contains("Active should be True/False"));

  }

  @Test(groups = {"webservice"})
  public void testCreateInvalidAuthenticationToken() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, commTrackUser,
      "Testing");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Authentication Failed"));


  }

  @Test(groups = {"webservice"})
  public void testUpdateInvalidAuthenticationToken() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, commTrackUser, "Testing");
    assertEquals(responseEntity.getStatus(), SC_UNAUTHORIZED);
  }

  @Test(groups = {"webservice"})
  public void testCreateInvalidUserName() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson), CREATE_URL, POST, "Testing",
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Authentication Failed"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateInvalidUserName() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION, PUT, "Testing", "Admin123");
    assertEquals(responseEntity.getStatus(), SC_UNAUTHORIZED);
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{{new String[]{"Admin123", "Admin123"}}};
  }
}

