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
import org.openlmis.restapi.domain.Agent;
import org.openlmis.restapi.domain.Report;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openlmis.UiUtils.HttpClient.GET;
import static org.openlmis.UiUtils.HttpClient.POST;


public class SubmitReportTest extends JsonUtility {

  public static final String MINIMUM_JSON_TXT_FILE_NAME = "ReportMinimumJson.txt";
  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
  public static final String PRODUCT_JSON_TXT_FILE_NAME = "ReportWithProductJson.txt";

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(false);
    createVirtualFacilityThroughApi("V10", "F10");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-01-30", "2016-01-30", 1, "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNode("700", "store in-charge", "N1");
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  //@Test(groups = {"webservice"})
  public void testInitiateRnr() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
//TODO set product code
    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        "Admin123");

    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));
  }

  @Test(groups = {"webservice"})
  public void shouldReturn401StatusWhenSubmittingReportWithInvalidAPIUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack1000",
        "Admin123");

    assertEquals(401, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenUserWithoutRights() throws Exception {
    dbWrapper.deleteSupervisoryRoleFromRoleAssignment();
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals(responseEntity.getResponse(), "{\"error\":\"User does not have permission\"}");
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportInvalidFacility() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("Invalid");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals(responseEntity.getResponse(), "{\"error\":\"Invalid Facility code\"}");
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWithoutHeaders() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(100L);
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSONWithoutHeaders(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "",
      "");
    assertEquals(401, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportInvalidProgram() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("InvalidProgram");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Invalid program code\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportMandatoryFieldsMissing() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Missing mandatory fields\"}", responseEntity.getResponse());

    reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setProgramCode("HIV");

    responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Missing mandatory fields\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenVirtualFacilityInactive() throws Exception {
    dbWrapper.updateActiveStatusOfFacility("V10", "false");
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Facility is inoperative\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenVirtualFacilityDisabled() throws Exception {
    dbWrapper.updateFacilityFieldBYCode("enabled", "false", "V10");
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Facility is inoperative\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenParentFacilityInactive() throws Exception {
    dbWrapper.updateActiveStatusOfFacility("V10", "false");
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Facility is inoperative\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenProgramGloballyInactive() throws Exception {
    dbWrapper.updateActiveStatusOfProgram("HIV", false);
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"User does not have permission\"}", responseEntity.getResponse());
    dbWrapper.updateActiveStatusOfProgram("HIV", true);
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenProgramInactiveAtVirtualFacility() throws Exception {
    dbWrapper.updateProgramsSupportedByField("active", "false", "V10");
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"User does not have permission\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenParentFacilityDisabled() throws Exception {
    dbWrapper.updateFacilityFieldBYCode("enabled", "false", "F10");
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Facility is inoperative\"}", responseEntity.getResponse());
    dbWrapper.updateFacilityFieldBYCode("enabled", "true", "F10");
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportWhenTemplateNotConfigured() throws Exception {
    dbWrapper.deleteRnrTemplate();
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Please contact admin to define R&R template for this program\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void shouldSubmitDuplicateReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    String jsonStringFor = getJsonStringFor(reportFromJson);
    client.SendJSON(jsonStringFor,
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    ResponseEntity responseEntity = client.SendJSON(jsonStringFor,
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));
  }


  // @Test(groups = {"webservice"})
  public void testBlankProductSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
//    TODO set product cod ein the json
    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson), "http://localhost:9091/rest-api/requisitions.json", POST,
      "commTrack",
      "Admin123");

    String response = responseEntity.getResponse();
    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));
  }

  @Test(groups = {"webservice"})
  public void testInitiateRnrWhenCurrentPeriodNotDefined() throws Exception {
    dbWrapper.deleteCurrentPeriod();
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson), "http://localhost:9091/rest-api/requisitions.json", POST,
      "commTrack",
      "Admin123");

    String response = responseEntity.getResponse();
    assertEquals(400, responseEntity.getStatus());
    assertEquals(responseEntity.getResponse(), "{\"error\":\"Program configuration missing\"}");
  }

  // @Test(groups = {"webservice"})
  public void testInitiateRnrWhenProgramStartDateIsAfterCurrentDateAndInCurrentPeriod() throws Exception {
    dbWrapper.updateProgramsSupportedByField("startDate", "2015-01-01", "V10");
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson), "http://localhost:9091/rest-api/requisitions.json", POST,
      "commTrack",
      "Admin123");

    String response = responseEntity.getResponse();
    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));
  }

  @Test(groups = {"webservice"})
  public void testInitiateRnrWhenProgramStartDateIsAfterCurrentDateAndCurrentPeriodEndDate() throws Exception {
    dbWrapper.insertProcessingPeriod("future", "future period", "2016-01-30", "2017-01-30", 1, "M");
    dbWrapper.updateProgramsSupportedByField("startDate", "2017-01-01", "V10");
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson), "http://localhost:9091/rest-api/requisitions.json", POST,
      "commTrack",
      "Admin123");

    String response = responseEntity.getResponse();
    assertEquals(400, responseEntity.getStatus());
    assertEquals(responseEntity.getResponse(), "{\"error\":\"Program configuration missing\"}");
  }


  public void testInvalidProductSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("V10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10000");

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Invalid product code\"}", response);
  }

  public void testSubmitReportValidRnR() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");

    String jsonStringFor = getJsonStringFor(reportFromJson);
    ResponseEntity responseEntity = client.SendJSON(jsonStringFor,
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));
  }

  public void testBlankBeginningBalanceSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("V10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"R&R has errors, please correct them to proceed.\"}", response);
  }

  public void createVirtualFacilityThroughApi(String agentCode, String facilityCode) throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    Agent agentJson = JsonUtility.readObjectFromFile("AgentValid.txt", Agent.class);
    agentJson.setAgentCode(agentCode);
    agentJson.setAgentName("Agent");
    agentJson.setParentFacilityCode(facilityCode);
    agentJson.setPhoneNumber("3434234");
    agentJson.setActive("true");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(agentJson),
      "http://localhost:9091/rest-api/agents.json",
      POST,
      "commTrack",
      "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"success\":\"CHW created successfully\"}"));
  }

}

