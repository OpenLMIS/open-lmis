/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.openlmis.restapi.domain.CHW;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class ChwFeed extends TestCaseHelper {
  public WebDriver driver;
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String FULL_JSON_TXT_FILE_NAME = "CHWValid.txt";
  public static final String userEmail = "Fatim_Doe@openlmis.com";
  public static final String CREATE_URL = "http://localhost:9091/rest-api/chw.json";
  public static final String UPDATE_URL = "http://localhost:9091/rest-api/chw/";
  public static final String commTrackUser = "commTrack";
  public static final String PHONE_NUMBER = "0099887766";
  public static final String DEFAULT_AGENT_NAME = "AgentVinod";
  public static final String DEFAULT_PARENT_FACILITY_CODE = "F10";
  public static final String ACTIVE_STATUS = "true";
  public static final String DEFAULT_AGENT_CODE = "A2";
  public static final String FALSE_FLAG = "f";
  public static final String TRUE_FLAG = "t";
  public static final String JSON_EXTENSION = ".json";
  public static final int AUTH_FAILED_STATUS_CODE = 401;
  public static final int BAD_REQUEST_STATUS_CODE = 400;



  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupDataExternalVendor(true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws Exception {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldNotShowSatelliteFacilityOnManageUserScreen(String user, String program, String[] credentials) throws Exception {
    dbWrapper.updateVirtualPropertyOfFacility(DEFAULT_PARENT_FACILITY_CODE, ACTIVE_STATUS);
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UserPage userPage = homePage.navigateToUser();
    userPage.enterAndVerifyUserDetails("storeincharge", userEmail, "Fatim", "Doe", DEFAULT_BASE_URL, DEFAULT_DB_URL);
    userPage.enterUserHomeFacility(DEFAULT_PARENT_FACILITY_CODE);
    userPage.verifyNoMatchedFoundMessage();
    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyFacilityUpload(String user, String program, String[] credentials) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilities("QA_facilities_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    assertEquals(FALSE_FLAG, dbWrapper.getVirtualPropertyOfFacility("facilityf10"));
    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldManageFacility(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    String vendorCode = "ABC";

    chwJson.setAgentCode(vendorCode);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(vendorCode);
    deleteFacilityPage.clickFacilityList(vendorCode);
    deleteFacilityPage.deleteFacility(vendorCode, DEFAULT_AGENT_NAME);

    deleteFacilityPage.verifyDeletedFacility(vendorCode, DEFAULT_AGENT_NAME);
    HomePage homePageRestore = deleteFacilityPage.restoreFacility();

    DeleteFacilityPage deleteFacilityPageRestore = homePageRestore.navigateSearchFacility();
    deleteFacilityPageRestore.searchFacility(vendorCode);
    deleteFacilityPageRestore.clickFacilityList(vendorCode);
    deleteFacilityPage.saveFacility();
    deleteFacilityPage.verifyMessageOnFacilityScreen(DEFAULT_AGENT_NAME, "updated");
    assertEquals(TRUE_FLAG, dbWrapper.getVirtualPropertyOfFacility(vendorCode));
    homePage.logout(baseUrlGlobal);

  }


  @Test(groups = {"webservice"})
  public void testChwFeedWithValidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedForDataReportableScenarios() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    chwJson.setActive("false");
    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

    dbWrapper.updateFacilityFieldBYCode("datareportable", "false", DEFAULT_AGENT_CODE);

    ResponseEntity responseEntityDataReportableFalse = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    assertTrue("Showing response as : " + responseEntityDataReportableFalse.getResponse(), responseEntityDataReportableFalse.getResponse().contains("{\"error\":\"CHW cannot be updated as it has been deleted\"}"));

  }

  @Test(groups = {"webservice"})
  public void testChwFeedCreateWithInvalidDataLength() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber("0099887766759785759859757757887");
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Incorrect data length\"}"));
  }

  @Test(groups = {"webservice"})
  public void testChwFeedUpdateWithInvalidDataLength() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber("0099887");
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    chwJson.setPhoneNumber("0099887766759785759859757757887");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"error\":\"Incorrect data length\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateStatusOfAgentCode() throws Exception {
    String AGENT_CODE = "ABCD";
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    chwJson.setActive("false");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

    assertEquals(FALSE_FLAG, dbWrapper.getActivePropertyOfFacility(AGENT_CODE));
  }

  @Test(groups = {"webservice"})
  public void testVerifyFieldsAfterChangeInParentFacilityCode() throws Exception {
    String typeid = "typeid";
    String geographiczoneid = "geographiczoneid";
    String operatedbyid = "operatedbyid";
    String parentfacilityid = "parentfacilityid";
    String vendorCode = "ABCDE";
    String firstParentFacility = DEFAULT_PARENT_FACILITY_CODE;
    String updateParentFacility = "F11";
    String id = "id";


    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(vendorCode);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(firstParentFacility);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getFacilityFieldBYCode(typeid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(typeid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographiczoneid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(geographiczoneid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacility), dbWrapper.getFacilityFieldBYCode(parentfacilityid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(operatedbyid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(operatedbyid, vendorCode));
    chwJson.setParentFacilityCode(updateParentFacility);

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+vendorCode+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));
    assertEquals(dbWrapper.getFacilityFieldBYCode(typeid, updateParentFacility), dbWrapper.getFacilityFieldBYCode(typeid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographiczoneid, updateParentFacility), dbWrapper.getFacilityFieldBYCode(geographiczoneid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, updateParentFacility), dbWrapper.getFacilityFieldBYCode(parentfacilityid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(operatedbyid, updateParentFacility), dbWrapper.getFacilityFieldBYCode(operatedbyid, vendorCode));
  }

  @Test(groups = {"webservice"})
  public void testVerifyFieldsAfterCHWCreation() throws Exception {
    String typeid = "typeid";
    String geographiczoneid = "geographiczoneid";
    String operatedbyid = "operatedbyid";
    String parentfacilityid = "parentfacilityid";
    String vendorCode = "commtrk";
    String vendorName = DEFAULT_AGENT_NAME;
    String vendorNameUpdated = "AgentJyot";
    String firstParentFacility = DEFAULT_PARENT_FACILITY_CODE;
    String firstParentFacilityUpdated = "F11";
    String code = "code";
    String name = "name";
    String id = "id";
    String mainphone = "mainphone";
    String phoneNumber = PHONE_NUMBER;
    String phoneNumberUpdated = "12345678";
    String active = "active";
    String virtualfacility = "virtualfacility";
    String sdp = "sdp";
    String datareportable = "datareportable";


    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(vendorCode);
    chwJson.setAgentName(vendorName);
    chwJson.setParentFacilityCode(firstParentFacility);
    chwJson.setPhoneNumber(phoneNumber);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getFacilityFieldBYCode(typeid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(typeid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographiczoneid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(geographiczoneid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacility), dbWrapper.getFacilityFieldBYCode(parentfacilityid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(operatedbyid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(operatedbyid, vendorCode));
    assertEquals(vendorCode, dbWrapper.getFacilityFieldBYCode(code, vendorCode));
    assertEquals(vendorName, dbWrapper.getFacilityFieldBYCode(name, vendorCode));
    assertEquals(phoneNumber, dbWrapper.getFacilityFieldBYCode(mainphone, vendorCode));

    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(active, vendorCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(virtualfacility, vendorCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(sdp, vendorCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(datareportable, vendorCode));

    chwJson.setAgentName(vendorNameUpdated);
    chwJson.setParentFacilityCode(firstParentFacilityUpdated);
    chwJson.setPhoneNumber(phoneNumberUpdated);
    chwJson.setActive("false");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+vendorCode+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));
    assertEquals(dbWrapper.getFacilityFieldBYCode(typeid, firstParentFacilityUpdated), dbWrapper.getFacilityFieldBYCode(typeid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographiczoneid, firstParentFacilityUpdated), dbWrapper.getFacilityFieldBYCode(geographiczoneid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacilityUpdated), dbWrapper.getFacilityFieldBYCode(parentfacilityid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(operatedbyid, firstParentFacilityUpdated), dbWrapper.getFacilityFieldBYCode(operatedbyid, vendorCode));
    assertEquals(vendorCode, dbWrapper.getFacilityFieldBYCode(code, vendorCode));
    assertEquals(vendorNameUpdated, dbWrapper.getFacilityFieldBYCode(name, vendorCode));
    assertEquals(phoneNumberUpdated, dbWrapper.getFacilityFieldBYCode(mainphone, vendorCode));
    assertEquals(FALSE_FLAG, dbWrapper.getFacilityFieldBYCode(active, vendorCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(virtualfacility, vendorCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(sdp, vendorCode));
    assertEquals(TRUE_FLAG, dbWrapper.getFacilityFieldBYCode(datareportable, vendorCode));
  }

  @Test(groups = {"webservice"})
  public void testCreateChwFeedWithParentFacilityCodeAsVirtualFacility() throws Exception {
    dbWrapper.updateVirtualPropertyOfFacility(DEFAULT_PARENT_FACILITY_CODE, ACTIVE_STATUS);
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedWithParentFacilityCodeAsVirtualFacility() throws Exception {
    String facilityCode = DEFAULT_PARENT_FACILITY_CODE;

    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(facilityCode);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    dbWrapper.updateVirtualPropertyOfFacility(facilityCode, ACTIVE_STATUS);

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));
  }


  @Test(groups = {"webservice"})
  public void testChwFeedWithAgentCodeAlreadyRegistered() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Agent already registered\"}"));
  }


  @Test(groups = {"webservice"})
  public void testUpdateShouldVerifyAgentIsNotAVirtualFacility() throws Exception {
    String Agent_code = "F11";
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(Agent_code);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+ Agent_code +JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Agent is not a virtual facility\"}"));
  }

  @Test(groups = {"webservice"})
  public void testCreateChwFeedWithInvalidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode("A10");
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid Facility code\"}"));
  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedWithInvalidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    chwJson.setParentFacilityCode("A10");
    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"error\":\"Invalid Facility code\"}"));
  }

  @Test(groups = {"webservice"})
  public void testMalformedJson() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedJson = getJsonStringFor(chwJson).replace(':', ';');

    ResponseEntity responseEntity = client.SendJSON(modifiedJson,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("BAD_REQUEST"));

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedJson,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertEquals(responseEntityUpdated.getStatus(), BAD_REQUEST_STATUS_CODE);

  }


  @Test(groups = {"webservice"})
  public void testBlankJson() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity = client.SendJSON("{}",
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString, responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldValueWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldValueWhenFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);


    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    String modifiedString = getJsonStringFor(chwJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));


    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString, responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenActiveFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(chwJson).replaceFirst(", \"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateAgentCodeNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);


    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + getJsonStringFor(chwJson), responseEntity.getResponse().contains("{\"error\":\"Invalid agent code\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst(",\"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(chwJson).replaceFirst(DEFAULT_AGENT_NAME, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    String modifiedString = getJsonStringFor(chwJson).replaceFirst(DEFAULT_AGENT_NAME, "");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " modifiedString : " + modifiedString, responseEntityUpdated.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCreateMissingMandatoryFieldsWhenActiveFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(chwJson).replaceFirst(ACTIVE_STATUS, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Active should be True/False\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldValueIsNotPresent() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst(ACTIVE_STATUS, "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Active should be True/False\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateMissingMandatoryFieldsWhenActiveFieldValueIsNotCorrect() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst(ACTIVE_STATUS, " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("Active should be True/False"));

  }

  @Test(groups = {"webservice"})
  public void testUpdatedSuccessfully() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUnrecognizedField() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("phoneNumber", "phonenumber");

    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("BAD_REQUEST"));

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertEquals(responseEntityUpdated.getStatus(), BAD_REQUEST_STATUS_CODE);
  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForCreateCHW() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("casesensitive");
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    chwJson.setAgentCode("CASESENSITIVE");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " updated json : " + getJsonStringFor(chwJson), responseEntityUpdated.getResponse().contains("{\"error\":\"Agent already registered\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForUpdateCHW() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    String agent_code = "casesensitive";
    chwJson.setAgentCode(agent_code);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    String agent_code_updated = "CASESENSITIVE";
    chwJson.setAgentCode(agent_code_updated);


    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+ agent_code_updated +JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " updated json : " + getJsonStringFor(chwJson), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testInvalidActiveFieldOption() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);
    String modifiedString = getJsonStringFor(chwJson).replaceFirst(ACTIVE_STATUS, "truefalse");

    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("Active should be True/False"));

  }

  @Test(groups = {"webservice"})
  public void testCreateInvalidAuthenticationToken() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      "Testing");
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));


  }

  @Test(groups = {"webservice"})
  public void testUpdateInvalidAuthenticationToken() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      commTrackUser,
      "Testing");
    assertEquals(responseEntity.getStatus(), AUTH_FAILED_STATUS_CODE);
  }

  @Test(groups = {"webservice"})
  public void testCreateInvalidUserName() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      "Testing",
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateInvalidUserName() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(DEFAULT_AGENT_CODE);
    chwJson.setAgentName(DEFAULT_AGENT_NAME);
    chwJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    chwJson.setPhoneNumber(PHONE_NUMBER);
    chwJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL+DEFAULT_AGENT_CODE+JSON_EXTENSION,
      PUT,
      "Testing",
      dbWrapper.getAuthToken(commTrackUser));
    assertEquals(responseEntity.getStatus(), AUTH_FAILED_STATUS_CODE);
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}

