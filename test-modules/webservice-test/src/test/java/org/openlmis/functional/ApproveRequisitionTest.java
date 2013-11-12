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

public class ApproveRequisitionTest extends JsonUtility {

  public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "ReportJsonApprove.txt";

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(false);
    super.setupDataRequisitionApprover();
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testApproveRequisitionValidRnR() throws Exception {
    HttpClient client = new HttpClient();
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");
    client.createContext();

    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);
    dbWrapper.updateRequisitionStatus("AUTHORIZED","commTrack","HIV");

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserName("commTrack1");
    reportFromJson.setRequisitionId(id);
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
        "PUT",
        "commTrack",
        "Admin123");

    response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertTrue(response.contains("{\"R&R\":"));
    assertEquals("APPROVED", dbWrapper.getRequisitionStatus(id));

    ResponseEntity responseEntity1 = client.SendJSON("", "http://localhost:9091/feeds/requisition-status/recent", "GET", "", "");

    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"requisitionStatus\":\"APPROVED\",\"emergency\":false,\"startDate\":1358274600000,\"endDate\":1359570599000}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"requisitionStatus\":\"APPROVED\",\"emergency\":false,\"startDate\":1358274600000,\"endDate\":1359570599000}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"requisitionStatus\":\"APPROVED\",\"emergency\":false,\"startDate\":1358274600000,\"endDate\":1359570599000}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"requisitionStatus\":\"APPROVED\",\"emergency\":false,\"startDate\":1358274600000,\"endDate\":1359570599000}"));
    assertTrue(responseEntity1.getResponse().contains("{\"requisitionId\":" + id + ",\"requisitionStatus\":\"APPROVED\",\"emergency\":false,\"startDate\":1358274600000,\"endDate\":1359570599000}"));
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionInValidUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");

    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);
    dbWrapper.updateRequisitionStatus("AUTHORIZED","commTrack","HIV");

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserName("ABCD");
    reportFromJson.setRequisitionId(id);
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", "Admin123");
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Please provide a valid username\"}", response);
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionUnauthorizedAccess() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");

    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);
    dbWrapper.updateRequisitionStatus("AUTHORIZED","commTrack","HIV");

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setRequisitionId(id);
    reportFromJson.setUserName("commTrack100");
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack100", "Admin123");
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    assertEquals(401, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionInvalidProduct() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");

    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);
    dbWrapper.updateRequisitionStatus("AUTHORIZED","commTrack","HIV");

    Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserName("commTrack");
    reportFromJson.setRequisitionId(id);
    reportFromJson.getProducts().get(0).setProductCode("P1000");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
        "PUT",
        "commTrack",
        "Admin123");

    response = responseEntity.getResponse();
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Invalid product code\"}", response);
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionInvalidRequisitionId() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");

    String response = submitReport();
    Long id = 999999L;
    Long id2 = getRequisitionIdFromResponse(response);
    dbWrapper.updateRequisitionStatus("AUTHORIZED","commTrack","HIV");

    Report reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setRequisitionId(id2);
    reportFromJson.setUserName("commTrack");
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve", "PUT",
      "commTrack", "Admin123");
    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Invalid RequisitionID\"}", response);
  }

  @Test(groups = {"webservice"}, dependsOnMethods = {"testApproveRequisitionValidRnR"})
  public void testApproveRequisitionBlankQuantityApproved() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");

    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);
    dbWrapper.updateRequisitionStatus("AUTHORIZED","commTrack","HIV");

    Report reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setRequisitionId(id);
    reportFromJson.setUserName("commTrack1");
    reportFromJson.getProducts().get(0).setProductCode("P10");

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
        "PUT",
        "commTrack",
        "Admin123");

    response = responseEntity.getResponse();
    client.SendJSON("", "http://localhost:9091/", "GET", "", "");
    assertEquals(400, responseEntity.getStatus());
    assertEquals("{\"error\":\"Missing mandatory fields\"}", response);
  }

}

