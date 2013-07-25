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
  public String passwordUsers = "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie";
  public String userEmail = "Fatim_Doe@openlmis.com";

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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));
  }

  @Test(groups = {"webservice"})
  public void testChwFeedWithParentFacilityCodeAsVirtualFacility() throws Exception {
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Parent facility can not be virtual facility\"}"));
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Agent already registered\"}"));
  }

  @Test(groups = {"webservice"})
  public void testShouldVerifyAgentIsNotAVirtualFacility() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("F11");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      "http://localhost:9091/rest-api/chw/update.json",
      PUT,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Agent is not a virtual facility\"}"));
  }

  @Test(groups = {"webservice"})
  public void testChwFeedWithInvalidParentFacilityCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("A10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid Facility code\"}"));
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("BAD_REQUEST"));

  }

  @Test(groups = {"webservice"})
  public void testBlankJson() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity = client.SendJSON("{}",
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testMissingMandatoryFieldsWhenFieldIsNotPresent() throws Exception {
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testMissingMandatoryFieldValueWhenFieldIsNotPresent() throws Exception {
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
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
    String modifiedString = getJsonStringFor(chwJson).replaceFirst(", \"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      "http://localhost:9091/rest-api/chw/update.json",
      PUT,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Invalid agent code\"}"));

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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst(",\"active\":\"true\"", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      "http://localhost:9091/rest-api/chw/update.json",
      PUT,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

  }

  @Test(groups = {"webservice"})
  public void testMissingMandatoryFieldsWhenFieldValueIsNotPresent() throws Exception {
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));

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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst("true", "");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      "http://localhost:9091/rest-api/chw/update.json",
      PUT,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("{\"error\":\"Missing mandatory fields\"}"));

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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));


    String modifiedString = getJsonStringFor(chwJson).replaceFirst("true", " ");


    ResponseEntity responseEntity = client.SendJSON(modifiedString,
      "http://localhost:9091/rest-api/chw/update.json",
      PUT,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      "http://localhost:9091/rest-api/chw/update.json",
      PUT,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
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
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("BAD_REQUEST"));

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
      "http://localhost:9091/rest-api/chw/update.json",
      PUT,
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse() + " modifiedString : " + modifiedString, responseEntity.getResponse().contains("Active should be True/False"));

  }

  @Test(groups = {"webservice"})
  public void testInvalidAuthenticationToken() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "commTrack",
      "Testing");
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));


  }

  @Test(groups = {"webservice"})
  public void testInvalidUserName() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    CHW chwJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, CHW.class);
    chwJson.setAgentCode("A2");
    chwJson.setAgentName("AgentVinod");
    chwJson.setParentFacilityCode("F10");
    chwJson.setPhoneNumber("0099887766");
    chwJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(chwJson),
      "http://localhost:9091/rest-api/chw.json",
      POST,
      "Testing",
      dbWrapper.getAuthToken("commTrack"));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));

  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}

