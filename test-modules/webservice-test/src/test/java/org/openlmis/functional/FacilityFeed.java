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

import org.apache.commons.lang3.StringUtils;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.pageobjects.*;
import org.openlmis.restapi.domain.Agent;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;


public class FacilityFeed extends JsonUtility {
  public static final String FULL_JSON_TXT_FILE_NAME = "AgentValid.txt";
  public static final String CREATE_URL = "http://localhost:9091/rest-api/agents.json";
  public static final String UPDATE_URL = "http://localhost:9091/rest-api/agents/";
  public static final String commTrackUser = "commTrack";
  public static final String PHONE_NUMBER = "0099887766";
  public static final String DEFAULT_AGENT_NAME = "Agent A1";
  public static final String DEFAULT_PARENT_FACILITY_CODE = "F10";
  public static final String ACTIVE_STATUS = "true";
  public static final String DEFAULT_AGENT_CODE = "A2";
  public static final String JSON_EXTENSION = ".json";
  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String FACILITY_FEED_URL = "http://localhost:9091/feeds/facilities/recent";

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
  public void testFacilityFeedUsingUI(String user, String program, String[] credentials) throws SQLException, ParserConfigurationException, SAXException, InterruptedException {
    HttpClient client = new HttpClient();
    client.createContext();

    dbWrapper.insertUser(user, "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F10", "Jane_Doe@openlmis.com");

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);

    FacilityPage facilityPage = homePage.navigateManageFacility();
    homePage.clickCreateFacilityButton();
    String geoZone = "Ngorongoro";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "FCcode";
    String facilityNamePrefix = "FCname";
    String catchmentPopulationValue = "100";

    String date_time = facilityPage.enterValuesInFacilityAndClickSave(facilityCodePrefix, facilityNamePrefix, program, geoZone, facilityType, operatedBy, catchmentPopulationValue);
    facilityPage.verifyMessageOnFacilityScreen(facilityNamePrefix + date_time, "created");

    ResponseEntity responseEntity = client.SendJSON("", FACILITY_FEED_URL, "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + facilityCodePrefix + date_time + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + facilityNamePrefix + date_time + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityType\":\"" + facilityType + "\""));
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
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"" + operatedBy + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageGrossCapacity\":3434.3434"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageNetCapacity\":3535.3535"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"suppliesOthers\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":false"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"comment\":\"Comments\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stringGoLiveDate\":\"25-" + new SimpleDateFormat("MM-yyyy").format(new Date()) + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stringGoDownDate\":\"26-" + new SimpleDateFormat("MM-yyyy").format(new Date()) + "\""));

    homePage.navigateManageFacility();
    facilityPage.searchFacility(date_time);
    facilityPage.clickFirstFacilityList();
    facilityPage.disableFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    facilityPage.verifyDisabledFacility(facilityCodePrefix + date_time, facilityNamePrefix + date_time);
    facilityPage.enableFacility();
    assertEquals(facilityPage.getEnabledFacilityText(), "Yes");
    responseEntity = client.SendJSON("", FACILITY_FEED_URL, "GET", "", "");

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");

    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"enabled\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"online\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"gln\":\"Testing Gln\""));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"enabled\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"facilityType\":\"" + facilityType + "\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"operatedBy\":\"" + operatedBy + "\""));

    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"enabled\":true"));
    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"facilityType\":\"" + facilityType + "\""));
    assertTrue("feed json list : " + feedJSONList.get(2), feedJSONList.get(2).contains("\"operatedBy\":\"" + operatedBy + "\""));

    facilityPage = homePage.navigateManageFacility();
    facilityPage.searchFacility(date_time);
    facilityPage.clickFirstFacilityList();
    facilityPage.addProgram("VACCINES", true);
    facilityPage.saveFacility();
    Thread.sleep(5000);
    assertEquals(3, feedJSONList.size());

    facilityPage = homePage.navigateManageFacility();
    facilityPage.searchFacility(date_time);
    facilityPage.clickFirstFacilityList();
    facilityPage.removeFirstProgram();
    facilityPage.saveFacility();

    Thread.sleep(5000);
    assertEquals(3, feedJSONList.size());

    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Credentials")
  public void shouldVerifyFacilityFeedForFacilityUpload(String[] credentials) throws FileNotFoundException, ParserConfigurationException, SAXException {
    HttpClient client = new HttpClient();
    client.createContext();

    String geoZone = "virtualgeozone";
    String facilityType = "Lvl3 Hospital";
    String operatedBy = "MoH";
    String facilityCodePrefix = "facilityf10";
    String facilityNamePrefix = "facilityf10 Village Dispensary";
    String facilityCodeUpdatedPrefix = "facilityf11";
    String facilityNameUpdatedPrefix = "facilityf11 Village Dispensary";
    String catchmentPopulationValue = "100";
    String catchmentPopulationUpdatedValue = "9999999";

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadGeographicZone("QA_Geographic_Data_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();
    uploadPage.uploadFacilities("QA_facilities_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntity = client.SendJSON("", FACILITY_FEED_URL, "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + facilityCodePrefix + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + facilityNamePrefix + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityType\":\"" + facilityType + "\""));
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
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"operatedBy\":\"" + operatedBy + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageGrossCapacity\":3434.3434"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"coldStorageNetCapacity\":3535.3535"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"suppliesOthers\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goDownDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stringGoLiveDate\":\"11-11-2012\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stringGoDownDate\":\"11-11-1887"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"satellite\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":false"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"comment\":\"fc\""));

    uploadPage.uploadFacilities("QA_facilities_Subsequent_WebService.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntityUpdated = client.SendJSON("", FACILITY_FEED_URL, "GET", "", "");

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"code\":\"" + facilityCodeUpdatedPrefix + "\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"name\":\"" + facilityNameUpdatedPrefix + "\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"catchmentPopulation\":" + catchmentPopulationUpdatedValue));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"sdp\":true"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"online\":true"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"gln\":\"G7645\""));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"enabled\":true"));

    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webserviceSmoke"})
  public void testFacilityFeedUsingCommTrack() throws IOException, ParserConfigurationException, SAXException {

    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
      CREATE_URL,
      POST,
      commTrackUser,
      "Admin123");

    String stringGoLiveDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    ResponseEntity responseEntity = client.SendJSON("", FACILITY_FEED_URL, "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityType\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stringGoLiveDate\":\"" + stringGoLiveDate + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"parentFacility\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityIsOnline\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectricity\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicSCC\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"hasElectronicDAR\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"satelliteFacility\":"));

    assertEquals(StringUtils.countMatches(responseEntity.getResponse(), ":"), 29);
    agentJson.setActive("false");
    client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      "Admin123");

    ResponseEntity responseEntityUpdated = client.SendJSON("", FACILITY_FEED_URL, "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"active\":false"));
    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"facilityIsOnline\":"));
    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"hasElectricity\":"));
    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"hasElectronicSCC\":"));
    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"hasElectronicDAR\":"));
    assertFalse("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"satelliteFacility\":"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"enabled\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"enabled\":true"));
  }

  @Test(groups = {"webservice"})
  public void testFacilityFeedUsingCommTrackWithoutHeaders() throws IOException {

    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    ResponseEntity responseEntity = client.SendJSONWithoutHeaders(getJsonStringFor(agentJson),
      CREATE_URL,
      POST,
      "",
      "");

    assertTrue("Showing response as : " + responseEntity.getStatus(), responseEntity.getStatus() == 401);
  }

  @Test(groups = {"webserviceSmoke"})
  public void testFacilityFeedUsingCommTrackUsingOpenLmisVendor() throws IOException, ParserConfigurationException, SAXException {

    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
      CREATE_URL,
      POST,
      commTrackUser,
      "Admin123");

    ResponseEntity responseEntity = client.SendJSON("", FACILITY_FEED_URL + "?vendor=openlmis", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityType\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"parentFacility\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

    agentJson.setActive("false");
    client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      "Admin123");

    ResponseEntity responseEntityUpdated = client.SendJSON("", FACILITY_FEED_URL + "?vendor=openlmis", "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"active\":false"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"enabled\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"enabled\":true"));
  }

  @Test(groups = {"webservice"})
  public void testFacilityFeedUsingCommTrackUsingInvalidVendor() throws IOException, ParserConfigurationException, SAXException {

    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
      CREATE_URL,
      POST,
      commTrackUser,
      "Admin123");

    ResponseEntity responseEntity = client.SendJSON("", FACILITY_FEED_URL + "?vendor=testing", "GET", "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"code\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"name\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"facilityType\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"mainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"active\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"goLiveDate\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"virtualFacility\":true"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"parentFacility\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

    agentJson.setActive("false");
    client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      "Admin123");

    ResponseEntity responseEntityUpdated = client.SendJSON("", FACILITY_FEED_URL + "?vendor=testing", "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"active\":false"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"active\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"enabled\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"sdp\":true"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"active\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"enabled\":true"));
  }

  @Test(groups = {"webservice"})
  public void testFacilityFeedForCommTrackVendorSpecificInfo() throws IOException, ParserConfigurationException, SAXException {

    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Agent.class);
    agentJson.setAgentCode(DEFAULT_AGENT_CODE);
    agentJson.setAgentName(DEFAULT_AGENT_NAME);
    agentJson.setParentFacilityCode(DEFAULT_PARENT_FACILITY_CODE);
    agentJson.setPhoneNumber(PHONE_NUMBER);
    agentJson.setActive(ACTIVE_STATUS);

    client.SendJSON(getJsonStringFor(agentJson),
      CREATE_URL,
      POST,
      commTrackUser,
      "Admin123");

    ResponseEntity responseEntity = client.SendJSON("", FACILITY_FEED_URL + "?vendor=commtrack", "GET", "", "");
    String response = responseEntity.getResponse();
    assertTrue("Response entity : " + response, response.contains("\"facilityCode\":\"" + DEFAULT_AGENT_CODE + "\""));
    assertTrue("Response entity : " + response, response.contains("\"facilityName\":\"" + DEFAULT_AGENT_NAME + "\""));
    assertTrue("Response entity : " + response, response.contains("\"facilityType\":\"Lvl3 Hospital\""));
    assertTrue("Response entity : " + response, response.contains("\"facilityMainPhone\":\"" + PHONE_NUMBER + "\""));
    assertTrue("Response entity : " + response, response.contains("\"geographicZone\":\"Ngorongoro\""));
    assertTrue("Response entity : " + response, response.contains("\"facilityIsActive\":" + ACTIVE_STATUS + ""));
    assertTrue("Response entity : " + response, response.contains("\"facilityGoLiveDate\""));
    assertTrue("Response entity : " + response, response.contains("\"facilityIsVirtual\":true"));
    assertTrue("Response entity : " + response, response.contains("\"parentFacilityCode\":\"" + DEFAULT_PARENT_FACILITY_CODE + "\""));

    agentJson.setActive("false");
    client.SendJSON(getJsonStringFor(agentJson),
      UPDATE_URL + DEFAULT_AGENT_CODE + JSON_EXTENSION,
      PUT,
      commTrackUser,
      "Admin123");

    ResponseEntity responseEntityUpdated = client.SendJSON("", FACILITY_FEED_URL + "?vendor=commtrack", "GET", "", "");
    assertTrue("Response entity : " + responseEntityUpdated.getResponse(), responseEntityUpdated.getResponse().contains("\"facilityIsActive\":false"));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"facilityIsActive\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"enabled\":true"));
    assertTrue("feed json list : " + feedJSONList.get(0), feedJSONList.get(0).contains("\"facilityIsSDP\":true"));

    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"facilityIsActive\":false"));
    assertTrue("feed json list : " + feedJSONList.get(1), feedJSONList.get(1).contains("\"enabled\":true"));
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {"User123", "HIV", new String[]{"Admin123", "Admin123"}}
    };
  }

  @DataProvider(name = "Data-Provider-Function-Credentials")
  public Object[][] parameterIntTestProviderCredentials() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}

