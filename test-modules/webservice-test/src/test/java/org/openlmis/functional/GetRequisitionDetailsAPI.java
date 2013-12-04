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
import org.openlmis.pod.domain.POD;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.lang.String.format;

public class GetRequisitionDetailsAPI extends JsonUtility {

  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
  public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
  public static final String URL = "http://localhost:9091/rest-api/requisitions/";


  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
      super.setup();
      super.setupTestData(false);
      super.setupDataRequisitionApprover();
      createVirtualFacilityThroughApi("V10", "F10");
      dbWrapper.insertProcessingPeriod("current", "current period", "2013-01-30", "2016-01-30", 1, "M");
      dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId1("700", "store in-charge", "N1");
      dbWrapper.updateRestrictLogin("commTrack", true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetails() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long)dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithMultipleProducts() throws Exception {
    dbWrapper.updateProductFullSupplyFlag(true, "P11");

    long id= submitRnRThroughApi("V10","HIV","P10",1,10,1,0,0,2);
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"P11\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"skipped\":true"));
    dbWrapper.updateProductFullSupplyFlag(false, "P11");
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithInvalidRequisitionID() throws Exception {
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
  public void testGetRequisitionDetailsWithInvalidPassword() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    Long id = (long)dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Authentication Failed"));
    assertEquals(401, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithInvalidUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    Long id = (long)dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "comm", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Authentication Failed"));
    assertEquals(401, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithBlankRequisitionID() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", URL, "GET", "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("Request method 'GET' not supported"));
    assertEquals(405, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithMalformedRequest() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    Long id = (long)dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/rest-api/requisition/" + id, "GET",
      "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("NOT_FOUND"));
    assertEquals(404, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithUnrecognizedField() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    Long id = (long)dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id + "/programCode", "GET", "commTrack", "Admin123");
    assertFalse("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("NOT_FOUND"));
    assertEquals(404, responseEntity.getStatus());
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithSpaceBeforeRequisitionID() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long)dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetailsWithNullRemarks() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long)dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("remarks:"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderStatus\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"supplyingFacilityCode\""));
    assertEquals(200, responseEntity.getStatus());
  }


  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetFalse() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long)dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(false, "F10");
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    dbWrapper.updateRestrictLogin("commTrack",false);
    approveRequisition(id, 65);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack",true);
    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkOrderStatus("RELEASED", 65, "READY_TO_PACK", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetTrue() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long)dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(true, "F10");
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack",false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack",true);
    responseEntity = waitUntilOrderStatusUpdatedOrTimeOut(id, "\"orderStatus\":\"RELEASED\"");
    checkOrderStatus("RELEASED", 65, "TRANSFER_FAILED", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetTrueAndFtpDetailsValid() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long)dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(true, "F10");
    dbWrapper.enterValidDetailsInFacilityFtpDetailsTable("F10");
    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack",false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack",true);

    responseEntity = waitUntilOrderStatusUpdatedOrTimeOut(id, "\"orderStatus\":\"RELEASED\"");
    checkOrderStatus("RELEASED", 65, "RELEASED", responseEntity);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionDetailsAfterPOD() throws Exception {

    HttpClient client = new HttpClient();
    client.createContext();
    submitRequisition("commTrack1","HIV");
    dbWrapper.updateRequisitionStatus("AUTHORIZED", "commTrack", "HIV");
    Long id = (long)dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED", responseEntity);

    dbWrapper.updateVirtualPropertyOfFacility("F10", "true");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack",false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack",true);
    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkOrderStatus("RELEASED", 65, "READY_TO_PACK", responseEntity);

    dbWrapper.assignRight("store in-charge", "MANAGE_POD");

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    PODFromJson.getPodLineItems().get(0).setProductCode("P10");

    client.SendJSON(getJsonStringFor(PODFromJson),
      format(POD_URL, id),
      "POST",
      "commTrack",
      "Admin123");

    responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkOrderStatus("RELEASED", 65, "RECEIVED", responseEntity);
  }

  private void checkRequisitionStatus(String requisitionStatus, ResponseEntity responseEntity) {
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"agentCode\":\"F10\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"emergency\":false"));
//    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodStartDate\":1354300200000"));
//    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodEndDate\":1448994599000"));
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
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityRequested\":3"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"reasonForRequestedQuantity\":\"reason\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"calculatedOrderQuantity\":57"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityApproved\":57"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"skipped\":false"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"remarks\":\"1\""));
    assertFalse("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"orderStatus\":"));
    assertFalse("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"supplyingFacilityCode\""));
    assertEquals(200, responseEntity.getStatus());
  }

  private void checkOrderStatus(String requisitionStatus, int quantityApproved, String orderStatus, ResponseEntity responseEntity) throws Exception {
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"agentCode\":\"F10\""));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodStartDate\":1354300200000"));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"periodEndDate\":1448994599000"));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"requisitionStatus\":\"" + requisitionStatus + "\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"productCode\":\"P10\""));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"beginningBalance\":0"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stockInHand\":10"));
    assertFalse("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"quantityRequested\":3"));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"calculatedOrderQuantity\":57"));
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityApproved\":" + quantityApproved));
    assertTrue("Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("\"remarks\":\"1\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"supplyingFacilityCode\":\"F10\""));
      assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderStatus\":\"" + orderStatus + "\""));
    assertEquals(200, responseEntity.getStatus());
  }

  private ResponseEntity waitUntilOrderStatusUpdatedOrTimeOut(long id, String expected) throws Exception {
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



