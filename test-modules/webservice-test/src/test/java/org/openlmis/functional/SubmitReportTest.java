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
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportInvalidFacility() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(100L);
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    String response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", GET, "", "");

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"User does not have rights to save this R&R\"}");
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
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(500L);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      POST,
      "commTrack",
      "Admin123");

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"User does not have rights to save this R&R\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportValidRnR() throws Exception {
    String response = submitReport();
    assertTrue(response.contains("{\"R&R\":"));
  }

  @Test(groups = {"webservice"})
  public void shouldThrowErrorOnSubmittingDuplicateReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

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

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Please finish all R&R of previous period(s)\"}", responseEntity.getResponse());
  }

  @Test(groups = {"webservice"})
  public void testBlankProductSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson), "http://localhost:9091/rest-api/requisitions.json", POST,
      "commTrack",
      "Admin123");

    String response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Invalid product code\"}", response);
  }

  @Test(groups = {"webservice"})
  public void testInvalidProductSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
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

  @Test(groups = {"webservice"})
  public void testBlankBeginningBalanceSubmitReport() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(PRODUCT_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
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

  @Test(groups = {"webservice"})
  public void testMinimumSubmitReportValidRnR() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack");
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
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
    assertEquals(201, responseEntity.getStatus());
    assertTrue(response.contains("{\"R&R\":"));
  }

  @Test(groups = {"webservice"})
  public void testSubmitReportInvalidUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack100");
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json", POST,
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    client.SendJSON("", "http://localhost:9091/", GET, "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Please provide a valid username\"}", response);
  }

  @Test(groups = {"webservice"})
  public void shouldReturn401StatusWhenSubmittingReportWithInvalidAPIUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = JsonUtility.readObjectFromFile(MINIMUM_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack");
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack1000",
        "Admin123");

    assertEquals(401, responseEntity.getStatus());
  }

}

