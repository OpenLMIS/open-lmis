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
import org.openlmis.pod.domain.OrderPOD;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.lang.String.format;

public class GetRequisitionDetailsAPI extends JsonUtility {

  public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
  public static final String URL = "http://localhost:9091/rest-api/requisitions/";

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(false);
    super.setupDataRequisitionApprove();
    createVirtualFacilityThroughApi("V10", "F10");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-01-30", "2016-01-30", 1, "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId("commTrack", "store in-charge", "N1");
    dbWrapper.setupOrderNumberConfiguration("O", true, true, true, true);
    dbWrapper.updateRestrictLogin("commTrack", true);
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webserviceSmoke"})
  public void testGetRequisitionDetails() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
    checkOrderDetailsNotPresent(responseEntity);
    assertEquals(StringUtils.countMatches(responseEntity.getResponse(), ":"), 20);
  }

  @Test(groups = {"webserviceSmoke"})
  public void testGetRequisitionDetailsWithMultipleProducts() throws SQLException, IOException {
    dbWrapper.updateFieldValue("products", "fullSupply", "true", "code", "P11");

    long id = submitRnRThroughApi("V10", "HIV", "P10", 1, 10, 1, 0, 0, 2);
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"P11\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"skipped\":true"));
    dbWrapper.updateFieldValue("products", "fullSupply", "false", "code", "P11");
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithInvalidRequisitionID() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", URL + 100, "GET", "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"error\":\"Requisition Not Found\""));
    assertEquals(400, responseEntity.getStatus());

    responseEntity = client.SendJSON("", URL + "@", "GET", "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"error\":\"Oops, something has gone wrong. Please try again later\""));
    assertEquals(500, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithInvalidPassword() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Authentication Failed"));
    assertEquals(401, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithInvalidUser() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "comm", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Authentication Failed"));
    assertEquals(401, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithBlankRequisitionID() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", URL, "GET", "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Request method 'GET' not supported"));
    assertEquals(405, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithMalformedRequest() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/rest-api/requisition/" + id, "GET",
      "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("NOT_FOUND"));
    assertEquals(404, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithUnrecognizedField() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id + "/programCode", "GET", "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("NOT_FOUND"));
    assertEquals(404, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithSpaceBeforeRequisitionID() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
    checkOrderDetailsNotPresent(responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithNullRemarks() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("remarks:"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderStatus\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"supplyingFacilityCode\""));
    assertEquals(200, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetFalse() throws SQLException, IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
    checkOrderDetailsNotPresent(responseEntity);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(false, "F10");
    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    dbWrapper.updateRestrictLogin("commTrack", false);
    approveRequisition(id, 65);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);
    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("RELEASED", responseEntity);
    checkOrderStatus(65, "READY_TO_PACK", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetTrue() throws SQLException, IOException, InterruptedException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
    checkOrderDetailsNotPresent(responseEntity);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(true, "F10");
    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack", false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);
    responseEntity = waitUntilOrderStatusUpdatedOrTimeOut(id, "\"orderStatus\":\"RELEASED\"");
    checkRequisitionStatus("RELEASED", responseEntity);
    checkOrderStatus(65, "TRANSFER_FAILED", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetTrueAndFtpDetailsValid() throws SQLException, IOException, InterruptedException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
    checkOrderDetailsNotPresent(responseEntity);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(true, "F10");
    dbWrapper.enterValidDetailsInFacilityFtpDetailsTable("F10");
    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack", false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);

    responseEntity = waitUntilOrderStatusUpdatedOrTimeOut(id, "\"orderStatus\":\"RELEASED\"");
    checkRequisitionStatus("RELEASED", responseEntity);
    checkOrderStatus(65, "RELEASED", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterOrderIsCreated() throws SQLException, IOException {

    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
    checkOrderDetailsNotPresent(responseEntity);

    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack", false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);
    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("RELEASED", responseEntity);
    checkOrderStatus(65, "READY_TO_PACK", responseEntity);

    dbWrapper.assignRight("store in-charge", "MANAGE_POD");

    OrderPOD OrderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    OrderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    OrderPODFromJson.getPodLineItems().get(0).setProductCode("P10");

    String orderId = generateOrderNumberForIdAndDefaultConfiguration(id, "HIV");

    dbWrapper.setupOrderNumberConfiguration("P", true, true, true, true);
    checkOrderNumber(orderId, responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterPOD() throws SQLException, IOException {

    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1", "HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
    checkOrderDetailsNotPresent(responseEntity);

    dbWrapper.updateFieldValue("facilities", "virtualFacility", "true", "code", "F10");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack", false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);
    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("RELEASED", responseEntity);
    checkOrderStatus(65, "READY_TO_PACK", responseEntity);

    dbWrapper.assignRight("store in-charge", "MANAGE_POD");

    OrderPOD OrderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    OrderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    OrderPODFromJson.getPodLineItems().get(0).setProductCode("P10");

    String orderId = generateOrderNumberForIdAndDefaultConfiguration(id, "HIV");

    client.SendJSON(getJsonStringFor(OrderPODFromJson),
      format(POD_URL, orderId),
      "POST",
      "commTrack",
      "Admin123");

    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("RELEASED", responseEntity);
    checkOrderStatus(65, "RECEIVED", responseEntity);
    dbWrapper.setupOrderNumberConfiguration("P", true, true, true, true);
    checkOrderNumber(orderId, responseEntity);
  }

  private String generateOrderNumberForIdAndDefaultConfiguration(Long id, String programCode) {
    return "O" + programCode + String.format("%08d", id) + "R";
  }

  private void checkRequisitionStatus(String requisitionStatus, ResponseEntity responseEntity) {
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"agentCode\":\"F10\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"emergency\":false"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stringPeriodStartDate\":\"01/12/2012\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stringPeriodEndDate\":\"01/12/2015\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodStartDate\":"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodEndDate\":"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"requisitionStatus\":\"" + requisitionStatus + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"products\":[{"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"P10\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"beginningBalance\":0"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityReceived\":11"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityDispensed\":1"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"totalLossesAndAdjustments\":0"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stockInHand\":10"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"newPatientCount\":0"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stockOutDays\":0"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"skipped\":false"));
    assertEquals(200, responseEntity.getStatus());
  }

  private void checkOrderDetailsNotPresent(ResponseEntity responseEntity) {
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityRequested\":3"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"reasonForRequestedQuantity\":\"reason\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"calculatedOrderQuantity\":57"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityApproved\":57"));
    assertFalse("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"quantityRequested\":3"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"reasonForRequestedQuantity\":\"reason\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"calculatedOrderQuantity\":57"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityApproved\":57"));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"skipped\":false"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"remarks\":\"1\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderStatus\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"supplyingFacilityCode\""));

  }

  private void checkOrderStatus(int quantityApproved, String orderStatus, ResponseEntity responseEntity) {
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityApproved\":" + quantityApproved));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"supplyingFacilityCode\":\"F10\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderStatus\":\"" + orderStatus + "\""));
  }

  private void checkOrderNumber(String orderId, ResponseEntity responseEntity) {
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderId\":\"" + orderId + "\""));
  }

  private ResponseEntity waitUntilOrderStatusUpdatedOrTimeOut(long id, String expected) throws InterruptedException {
    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity;

    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    int time = 0;
    while (time <= 30000) {
      if (responseEntity.getResponse().contains(expected))
        break;
      responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
      time += 500;
      Thread.sleep(500);
    }
    return responseEntity;
  }

}



