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

import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.restapi.domain.Report;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertTrue;
import static java.lang.System.getProperty;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class ApproveRequisitionTest extends TestCaseHelper {

  public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "ReportJsonApprove.txt";
  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";

  public WebDriver driver;

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    driver.get("http://localhost:9091");
    super.setup();
    super.setupDataExternalVendor(false);
    super.setupDataApproverExternalVendor();
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    driver.close();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testApproveRequisitionValidRnR() throws Exception {
    HttpClient client = new HttpClient();

    client.createContext();

    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);

    assertEquals(dbWrapper.getRequisitionStatus(id), "AUTHORIZED");

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack1");
    reportFromJson.setRequisitionId(id);
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
        "PUT",
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    response = responseEntity.getResponse();

    assertEquals(responseEntity.getStatus(),200);
    assertTrue(response.contains("{\"R&R\":"));
    assertEquals(dbWrapper.getRequisitionStatus(id), "RELEASED");

    ResponseEntity responseEntity1 = client.SendJSON("", "http://localhost:9091/feeds/requisition/recent", "GET", "", "");

    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"facilityId\":" + dbWrapper.getFacilityID("F10")+ ",\"programId\":" + dbWrapper.getProgramID("HIV") + ",\"periodId\":" + dbWrapper.getPeriodID("Period2") + ",\"requisitionStatus\":\"INITIATED\",\"externalSystem\":\"commTrack\"}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"facilityId\":" + dbWrapper.getFacilityID("F10")+ ",\"programId\":" + dbWrapper.getProgramID("HIV") + ",\"periodId\":" + dbWrapper.getPeriodID("Period2") + ",\"requisitionStatus\":\"SUBMITTED\",\"externalSystem\":\"commTrack\"}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"facilityId\":" + dbWrapper.getFacilityID("F10")+ ",\"programId\":" + dbWrapper.getProgramID("HIV") + ",\"periodId\":" + dbWrapper.getPeriodID("Period2") + ",\"requisitionStatus\":\"AUTHORIZED\",\"externalSystem\":\"commTrack\"}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"facilityId\":" + dbWrapper.getFacilityID("F10")+ ",\"programId\":" + dbWrapper.getProgramID("HIV") + ",\"periodId\":" + dbWrapper.getPeriodID("Period2") + ",\"requisitionStatus\":\"APPROVED\",\"externalSystem\":\"commTrack\"}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"facilityId\":" + dbWrapper.getFacilityID("F10")+ ",\"programId\":" + dbWrapper.getProgramID("HIV") + ",\"periodId\":" + dbWrapper.getPeriodID("Period2") + ",\"requisitionStatus\":\"RELEASED\",\"externalSystem\":\"commTrack\"}"));
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionInValidUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("ABCD");
    reportFromJson.setRequisitionId(id);
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"Please provide a valid username\"}");
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionInvalidProduct() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserId("commTrack");
    reportFromJson.setRequisitionId(id);
    reportFromJson.getProducts().get(0).setProductCode("P1000");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
        "PUT",
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    response = responseEntity.getResponse();
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"Invalid product code\"}");
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionInvalidRequisitionId() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = submitReport();
    Long id = 999999L;
    Long id2 = getRequisitionIdFromResponse(response);

    Report reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setRequisitionId(id2);
    reportFromJson.setUserId("commTrack");
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", dbWrapper.getAuthToken("commTrack"));
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"Requisition Not Found\"}");
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionBlankQuantityApproved() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);

    Report reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setRequisitionId(id);
    reportFromJson.setUserId("commTrack1");
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
        "PUT",
        "commTrack",
        dbWrapper.getAuthToken("commTrack"));

    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");
    assertEquals(responseEntity.getStatus(), 400);
    assertEquals(response, "{\"error\":\"R&R has errors, please correct them to proceed.\"}");
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionInValidVendor() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setRequisitionId(id);
    reportFromJson.setUserId("commTrack100");
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack1", dbWrapper.getAuthToken("commTrack"));
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    assertEquals(responseEntity.getStatus(), 401);
  }

  public String submitReport() throws Exception {
    baseUrlGlobal = getProperty("baseurl", DEFAULT_BASE_URL);
    dburlGlobal = getProperty("dburl", DEFAULT_DB_URL);
    dbWrapper = new DBWrapper(baseUrlGlobal, dburlGlobal);

    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
    reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
    reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
    reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions.json",
      "POST",
      "commTrack",
      dbWrapper.getAuthToken("commTrack"));

    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    return responseEntity.getResponse();
  }


  private Long getRequisitionIdFromResponse(String response) {
    return Long.parseLong(response.substring(response.lastIndexOf(":") + 1, response.lastIndexOf("}")));
  }

}

