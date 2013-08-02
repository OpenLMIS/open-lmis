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

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class FacilityFeed extends TestCaseHelper {
  public WebDriver driver;
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
  public static final String JSON_EXTENSION = ".json";
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";

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
  public void testFacilityFeedUsingUI(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    dbWrapper.insertUser("200", user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com", "openLmis");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    CreateFacilityPage createFacilityPage = homePage.navigateCreateFacility();
    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";
    String catchmentPopulationValue = "100";

    String date_time = createFacilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy, catchmentPopulationValue);
    createFacilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + facilityCodePrefix + date_time + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + facilityNamePrefix + date_time + "\""));
//    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"type\":\"" + facilityType + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"description\":\"Testing description\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"9711231305\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"fax\":\"9711231305\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address1\":\"Address1\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address2\":\"Address2\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"" + geoZone + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"catchmentPopulation\":" + catchmentPopulationValue + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"latitude\":-555.5555"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"longitude\":444.4444"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"altitude\":4545.4545"));
//    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"" + operatedBy + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageGrossCapacity\":3434.3434"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageNetCapacity\":3535.3535"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"suppliesOthers\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\":1377369000000"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goDownDate\":1377455400000"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"satelliteFacility\":false"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":false"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"comments\":\"Comments\""));

    DeleteFacilityPage deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    deleteFacilityPage.deleteFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    deleteFacilityPage.verifyDeletedFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    deleteFacilityPage.restoreFacility();
    responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent", "GET", "", "");

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");

    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"dataReportable\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"online\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"gln\":\"Testing Gln\""));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"dataReportable\":false"));
//    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"type\":\"" + facilityType + "\""));
//    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"operatedBy\":\"" + operatedBy + "\""));

    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"dataReportable\":true"));
//    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"type\":\"" + facilityType + "\""));
//    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"operatedBy\":\"" + operatedBy + "\""));

    deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.addProgram("VACCINES", true);
    createFacilityPage.saveFacility();
    Thread.sleep(5000);
    assertEquals(feedJSONList.size(), 3);

    deleteFacilityPage = homePage.navigateSearchFacility();
    deleteFacilityPage.searchFacility(date_time);
    deleteFacilityPage.clickFacilityList(date_time);
    createFacilityPage.removeFirstProgram();
    createFacilityPage.saveFacility();

    Thread.sleep(5000);
    assertEquals(feedJSONList.size(), 3);

    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyFacilityFeedForFacilityUpload(String user, String program, String[] credentials) throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    String geoZone = "virtualgeozone";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "facilityf10";
    String parentFacilityCode = "F10";
    String facilityNamePrefix = "facilityf10 Village Dispensary";
    String facilityCodeUpdatedPrefix = "facilityf11";
    String facilityNameUpdatedPrefix = "facilityf11 Village Dispensary";
    String catchmentPopulationValue = "100";
    String catchmentPopulationUpdatedValue = "9999999";

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadAndVerifyGeographicZone("QA_Geographic_Data_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilities("QA_facilities_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + facilityCodePrefix + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + facilityNamePrefix + "\""));
//      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"type\":\"" + facilityType + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"description\":\"IT department\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"9711231305\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"fax\":\"9711231305\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address1\":\"Address1\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"address2\":\"Address2\","));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"" + geoZone + "\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"catchmentPopulation\":" + catchmentPopulationValue + ""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"latitude\":-555.5555"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"longitude\":444.4444"));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"altitude\":4545.4545"));
//      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"" + operatedBy + "\"" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageGrossCapacity\":3434.3434" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageNetCapacity\":3535.3535" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"suppliesOthers\":true" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":true" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":true" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":true" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":true" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\":1352572200000" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goDownDate\":-2592106200000" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"satelliteFacility\":true" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":false" ));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"comments\":\"fc\""));

    uploadPage.uploadFacilities("QA_facilities_Subsequent_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntityUpdated = client.SendJSON("", "http://localhost:9091/feeds/facility/recent", "GET", "", "");

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"code\":\"" + facilityCodeUpdatedPrefix + "\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"name\":\"" + facilityNameUpdatedPrefix + "\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"catchmentPopulation\":" + catchmentPopulationUpdatedValue));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"sdp\":true"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"online\":true"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"parentFacility\":\"" + parentFacilityCode + "\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"gln\":\"G7645\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"dataReportable\":true"));


    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"})
  public void testFacilityFeedUsingCommTrack() throws Exception {

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

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"type\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"NGO\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"parentFacility\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

//    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"isOnline\":"));
//    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":"));
//    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":"));
//    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":"));
//    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"satelliteFacility\":"));

    chwJson.setActive("false");
    client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntityUpdated = client.SendJSON("", "http://localhost:9091/feeds/facility/recent", "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"active\":false"));
//    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"isOnline\":"));
//    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"hasElectricity\":"));
//    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"hasElectronicSCC\":"));
//    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"hasElectronicDAR\":"));
//    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"satelliteFacility\":"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"dataReportable\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"online\":false"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"dataReportable\":true"));


  }

//  @Test(groups = {"webservice"})
  public void testFacilityFeedUsingCommTrackUsingOpenLmisVendor() throws Exception {

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

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent?vendor=openlmis", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"type\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"NGO\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"parentFacility\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

    chwJson.setActive("false");
    client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntityUpdated = client.SendJSON("", "http://localhost:9091/feeds/facility/recent?vendor=openlmis", "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"active\":false"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"dataReportable\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"online\":false"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"dataReportable\":true"));


  }

//  @Test(groups = {"webservice"})
  public void testFacilityFeedUsingCommTrackUsingInvalidVendor() throws Exception {

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

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent?vendor=testing", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"type\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"NGO\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"parentFacility\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

    chwJson.setActive("false");
    client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntityUpdated = client.SendJSON("", "http://localhost:9091/feeds/facility/recent?vendor=testing", "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"active\":false"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"dataReportable\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"online\":false"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"dataReportable\":true"));


  }

  //  @Test(groups = {"webservice"})
  public void testFacilityFeedForCommTrackVendorSpecificInfo() throws Exception {

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

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/feeds/facility/recent?vendor=commtrack", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityCode\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityName\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityTypeID\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityMainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZoneID\":\"Ngorongoro\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityOperatedBy\":\"NGO\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityIsActive\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityGoLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityIsVirtual\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"SatelliteParentID\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

    chwJson.setActive("false");
    client.SendJSON(getJsonStringFor(chwJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntityUpdated = client.SendJSON("", "http://localhost:9091/feeds/facility/recent?vendor=commTrack", "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"facilityIsActive\":false"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"facilityIsActive\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"dataReportable\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"facilityIsSDP\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"facilityIsOnline\":false"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"facilityIsActive\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"dataReportable\":true"));


  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }
}

