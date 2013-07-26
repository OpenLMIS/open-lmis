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
  public String userEmail = "Fatim_Doe@openlmis.com";
  public static String CREATE_URL = "http://localhost:9091/rest-api/chw.json";
  public static String UPDATE_URL = "http://localhost:9091/rest-api/chw/update.json";
  public static String commTrackUser = "commTrack";

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
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UserPage userPage = homePage.navigateToUser();
    userPage.enterAndVerifyUserDetails("storeincharge", userEmail, "Fatim", "Doe", DEFAULT_BASE_URL, DEFAULT_DB_URL);
    userPage.enterUserHomeFacility("F10");
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
    assertEquals("f", dbWrapper.getVirtualPropertyOfFacility("facilityf10"));
    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldManageFacility(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("ABC");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility("ABC");
    deleteFacilityPage.clickFacilityList("ABC");
    deleteFacilityPage.deleteFacility("ABC", "AgentVinod");

    deleteFacilityPage.verifyDeletedFacility("ABC", "AgentVinod");
    HomePage homePageRestore = deleteFacilityPage.restoreFacility();

    DeleteFacilityPage deleteFacilityPageRestore = homePageRestore.navigateSearchFacility();
    deleteFacilityPageRestore.searchFacility("ABC");
    deleteFacilityPageRestore.clickFacilityList("ABC");
    deleteFacilityPage.saveFacility();
    deleteFacilityPage.verifyMessageOnFacilityScreen("AgentVinod", "updated");
    assertEquals("t", dbWrapper.getVirtualPropertyOfFacility("ABC"));
    homePage.logout(baseUrlGlobal);

  }


  @Test(groups = {"webservice"})
  public void testChwFeedWithValidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));
  }

  @Test(groups = {"webservice"})
  public void testChwFeedCreateWithInvalidDataLength() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766759785759859757757887");
    chwJson.setActive("true");

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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887");
    chwJson.setActive("true");

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    chwJson.setPhoneNumber("0099887766759785759859757757887");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"error\":\"Incorrect data length\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateStatusOfAgentCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("ABCD");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    chwJson.setActive("false");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

    assertEquals("f", dbWrapper.getActivePropertyOfFacility("ABCD"));

  }

  @Test(groups = {"webservice"})
  public void testVerifyFieldsAfterChangeInParentFacilityCode() throws Exception {
    String typeid = "typeid";
    String geographiczoneid = "geographiczoneid";
    String operatedbyid = "operatedbyid";
    String parentfacilityid = "parentfacilityid";
    String vendorCode = "ABCDE";
    String firstParentFacility = "F10";
    String updateParentFacility = "F11";
    String id = "id";


    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(vendorCode);
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode(firstParentFacility);
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getFacilityFieldBYCode(typeid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(typeid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographiczoneid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(geographiczoneid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacility),dbWrapper.getFacilityFieldBYCode(parentfacilityid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(operatedbyid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(operatedbyid, vendorCode));
    chwJson.setParentFacilityCode(updateParentFacility);

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
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
    String vendorName = "AgentVinod";
    String firstParentFacility = "F10";
    String code = "code";
    String name = "name";
    String id = "id";
    String mainphone = "mainphone";
    String phoneNumber = "0099887766";


    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode(vendorCode);
    chwJson.setAgentName(vendorName);
    chwJson.setParentFacilityCode(firstParentFacility);
    chwJson.setPhoneNumber(phoneNumber);
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    assertEquals(dbWrapper.getFacilityFieldBYCode(typeid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(typeid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(geographiczoneid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(geographiczoneid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(id, firstParentFacility),dbWrapper.getFacilityFieldBYCode(parentfacilityid, vendorCode));
    assertEquals(dbWrapper.getFacilityFieldBYCode(operatedbyid, firstParentFacility), dbWrapper.getFacilityFieldBYCode(operatedbyid, vendorCode));
    assertEquals(vendorCode, dbWrapper.getFacilityFieldBYCode(code, vendorCode));
    assertEquals(vendorName, dbWrapper.getFacilityFieldBYCode(name, vendorCode));
    assertEquals(phoneNumber, dbWrapper.getFacilityFieldBYCode(mainphone, vendorCode));
    assertEquals("t", dbWrapper.getFacilityFieldBYCode("active", vendorCode));
    assertEquals("t", dbWrapper.getFacilityFieldBYCode("virtualfacility", vendorCode));
    assertEquals("t", dbWrapper.getFacilityFieldBYCode("sdp", vendorCode));
    assertEquals("t", dbWrapper.getFacilityFieldBYCode("datareportable", vendorCode));
  }

    @Test(groups = {"webservice"})
  public void testCreateChwFeedWithParentFacilityCodeAsVirtualFacility() throws Exception {
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));

  }

  @Test(groups = {"webservice"})
  public void testUpdateChwFeedWithParentFacilityCodeAsVirtualFacility() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");

    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

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
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("F11");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("A10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    chwJson.setParentFacilityCode("A10");
    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
    String modifiedJson = getJsonStringFor(chwJson).replace(':', ';');

    ResponseEntity responseEntity = client.SendJSON(modifiedJson,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("BAD_REQUEST"));

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedJson,
      UPDATE_URL,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
//    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("BAD_REQUEST"));

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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");


    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    String modifiedString = getJsonStringFor(chwJson).replaceFirst("\"agentName\":\"AgentVinod\",", " ");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");


    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst(",\"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("AgentVinod", "");


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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    String modifiedString = getJsonStringFor(chwJson).replaceFirst("AgentVinod", "");

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("true", "");


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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst("true", "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst("true", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("phoneNumber", "phonenumber");

    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("BAD_REQUEST"));

    ResponseEntity responseEntityUpdated = client.SendJSON(modifiedString,
      UPDATE_URL,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
//    assertTrue("Showing response as : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("BAD_REQUEST"));

  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForCreateCHW() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("casesensitive");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

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
//    assertTrue("Showing response as : " + responseEntityUpdated.getResponse()+ " updated json : "+getJsonStringFor(chwJson), responseEntityUpdated.getResponse().contains("{\"error\":\"Agent already registered\"}"));

  }

  @Test(groups = {"webservice"})
  public void testCaseSensitiveCheckForUpdateCHW() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("casesensitive");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      CREATE_URL,
      POST,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

    chwJson.setAgentCode("CASESENSITIVE");


    ResponseEntity responseEntityUpdated = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
//    assertTrue("Showing response as : " + responseEntityUpdated.getResponse() + " updated json : "+getJsonStringFor(chwJson), responseEntityUpdated.getResponse().contains("{\"success\":\"CHW updated successfully\"}"));

  }

  @Test(groups = {"webservice"})
  public void testInvalidActiveFieldOption() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");
    String modifiedString = getJsonStringFor(chwJson).replaceFirst("true", "truefalse");

    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      UPDATE_URL,
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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
      PUT,
      commTrackUser,
      "Testing");
    //Its a feedback. Needs to uncomment the line as soon as feedback is incorporated
//    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));


  }

  @Test(groups = {"webservice"})
  public void testCreateInvalidUserName() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

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
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL,
      PUT,
      "Testing",
      dbWrapper.getAuthToken(commTrackUser));
    //Its a feedback. Needs to uncomment the line as soon as feedback is incorporated
//    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));

  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}

