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
import org.openlmis.pod.domain.POD;
import org.openlmis.restapi.domain.Report;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openlmis.functional.JsonUtility.getJsonStringFor;
import static org.openlmis.functional.JsonUtility.readObjectFromFile;


public class GetRequisitionDetailsAPI extends TestCaseHelper {

  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
  public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "ReportJsonApprove.txt";
  public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
  public static final String URL = "http://localhost:9091/rest-api/requisitions/";
  public WebDriver driver;

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    driver.get("http://localhost:9091");
    super.setup();
    super.setupTestData(false);
    super.setupDataRequisitionApprover();
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    driver.close();
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testGetRequisitionDetails() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String response = submitReport();
    Long id = getRequisitionIdFromResponse(response);
    ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
    checkRequisitionStatus("AUTHORIZED",responseEntity );
  }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithInvalidRequisitionID() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL + 100, "GET", "commTrack", "Admin123");
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"error\":\"Requisition Not Found\""));
        assertEquals(400, responseEntity.getStatus());

        responseEntity = client.SendJSON("", URL + "@", "GET", "commTrack", "Admin123");
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"error\":\"Oops, something has gone wrong. Please try again later\""));
        assertEquals(500, responseEntity.getStatus());
    }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithInvalidPassword() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);

        ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin");
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));
        assertEquals(401, responseEntity.getStatus());
    }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithInvalidUser() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);

        ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "comm", "Admin123");
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));
        assertEquals(401, responseEntity.getStatus());
    }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithBlankRequisitionID() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();

        ResponseEntity responseEntity = client.SendJSON("", URL , "GET", "commTrack", "Admin123");
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Request method 'GET' not supported"));
        assertEquals(405, responseEntity.getStatus());
    }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithMalformedRequest() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);

        ResponseEntity responseEntity = client.SendJSON("", "http://localhost:9091/rest-api/requisition/"+id , "GET", "commTrack", "Admin123");
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("NOT_FOUND"));
        assertEquals(404, responseEntity.getStatus());
    }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithUnrecognizedField() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);

        ResponseEntity responseEntity = client.SendJSON("", URL+id+"/programCode" , "GET", "commTrack", "Admin123");
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("NOT_FOUND"));
        assertEquals(404, responseEntity.getStatus());
    }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithSpaceBeforeRequisitionID() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
        checkRequisitionStatus("AUTHORIZED",responseEntity );
    }

    @Test(groups = {"webservice"})
    public void testGetRequisitionDetailsWithNullRemarks() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        dbWrapper = new DBWrapper();

        Report reportFromJson = readObjectFromFile(FULL_JSON_TXT_FILE_NAME, Report.class);
        reportFromJson.setFacilityId(dbWrapper.getFacilityID("F10"));
        reportFromJson.setPeriodId(dbWrapper.getPeriodID("Period2"));
        reportFromJson.setProgramId(dbWrapper.getProgramID("HIV"));
        reportFromJson.getProducts().get(0).setRemarks(null);

        ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(reportFromJson),
                "http://localhost:9091/rest-api/requisitions.json",
                "POST",
                "commTrack",
                "Admin123");

        client.SendJSON("", "http://localhost:9091/", "GET", "", "");
        String response=responseEntity.getResponse();

        Long id = getRequisitionIdFromResponse(response);

        responseEntity = client.SendJSON("", URL+id , "GET", "commTrack", "Admin123");
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
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
        checkRequisitionStatus("AUTHORIZED", responseEntity);

         dbWrapper.setExportOrdersFlagInSupplyLinesTable(false,"F10");
        approveRequisition(id, 65);
        responseEntity = client.SendJSON("", URL+id, "GET", "commTrack", "Admin123");
        checkOrderStatus("RELEASED",65,"READY_TO_PACK",responseEntity );
    }


    public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetTrue() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
        checkRequisitionStatus("AUTHORIZED", responseEntity);

        dbWrapper.setExportOrdersFlagInSupplyLinesTable(true,"F10");
        approveRequisition(id, 65);
        responseEntity = client.SendJSON("", URL+id, "GET", "commTrack", "Admin123");
        checkOrderStatus("RELEASED", 65, "IN_ROUTE", responseEntity);
        testWebDriver.sleep(3000);
        responseEntity = client.SendJSON("", URL+id, "GET", "commTrack", "Admin123");
        checkOrderStatus("RELEASED", 65,"TRANSFER_FAILED", responseEntity);
    }


    public void testRequisitionDetailsAfterApprovalForExportOrdersFlagSetTrueAndFtpDetailsValid() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
        checkRequisitionStatus("AUTHORIZED",responseEntity );

        dbWrapper.setExportOrdersFlagInSupplyLinesTable(true,"F10");
        dbWrapper.enterValidDetailsInFacilityFtpDetailsTable("F10");
        approveRequisition(id, 65);
        responseEntity = client.SendJSON("", URL+id, "GET", "commTrack", "Admin123");
        checkOrderStatus("RELEASED",65,"IN_ROUTE",responseEntity );
        testWebDriver.sleep(3000);
        responseEntity = client.SendJSON("", URL+id, "GET", "commTrack", "Admin123");
        checkOrderStatus("RELEASED", 65,"RELEASED",responseEntity );
    }

    @Test(groups = {"webservice"})
    public void testRequisitionDetailsAfterPOD() throws Exception {

        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL + id, "GET", "commTrack", "Admin123");
        checkRequisitionStatus("AUTHORIZED",responseEntity );

        approveRequisition(id, 65);
        responseEntity = client.SendJSON("", URL+id, "GET", "commTrack", "Admin123");
        checkOrderStatus("RELEASED",65,"READY_TO_PACK",responseEntity );

        dbWrapper.assignRight("store in-charge", "MANAGE_POD");
        dbWrapper.setupUserForFulfillmentRole("commTrack","store in-charge","F10");

        POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
        PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
        PODFromJson.getPodLineItems().get(0).setProductCode("P10");

        responseEntity = client.SendJSON(getJsonStringFor(PODFromJson),
                        "http://localhost:9091/rest-api/order/" + id +"/pod.json",
                        "POST",
                        "commTrack",
                        "Admin123");

        responseEntity = client.SendJSON("", URL+id, "GET", "commTrack", "Admin123");
        checkOrderStatus("RELEASED", 65,"RECEIVED",responseEntity);
    }

    public String submitReport() throws Exception {
    dbWrapper = new DBWrapper();

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
      "Admin123");

    client.SendJSON("", "http://localhost:9091/", "GET", "", "");

    return responseEntity.getResponse();
  }


  private Long getRequisitionIdFromResponse(String response) {
    return Long.parseLong(response.substring(response.lastIndexOf(":") + 1, response.lastIndexOf("}")));
  }

    public void checkRequisitionStatus(String requisitionStatus, ResponseEntity responseEntity){
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"agentCode\":\"F10\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"emergency\":false"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodStartDate\":1358274600000"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodEndDate\":1359570599000"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"requisitionStatus\":\""+requisitionStatus+"\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"products\":[{"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"P10\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"beginningBalance\":3"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityReceived\":0"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityDispensed\":1"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"totalLossesAndAdjustments\":-2"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stockInHand\":0"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"newPatientCount\":2"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stockOutDays\":2"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityRequested\":3"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"reasonForRequestedQuantity\":\"reason\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"calculatedOrderQuantity\":57"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityApproved\":57"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"remarks\":\"1\""));
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderStatus\":"));
        assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"supplyingFacilityCode\""));
        assertEquals(200, responseEntity.getStatus());
    }

    public void approveRequisition(Long id, int quantityApproved) throws Exception {

        HttpClient client = new HttpClient();
        client.createContext();

        Report reportFromJson = JsonUtility.readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
        reportFromJson.setUserId("commTrack1");
        reportFromJson.setRequisitionId(id);
        reportFromJson.getProducts().get(0).setProductCode("P10");
        reportFromJson.getProducts().get(0).setQuantityApproved(quantityApproved);

        client.SendJSON(getJsonStringFor(reportFromJson),
                "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
                "PUT",
                "commTrack",
                "Admin123");
    }

    public void checkOrderStatus(String requisitionStatus, int quantityApproved, String orderStatus, ResponseEntity responseEntity)
    {
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"HIV\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"agentCode\":\"F10\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodStartDate\":1358274600000"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"periodEndDate\":1359570599000"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"requisitionStatus\":\""+requisitionStatus+"\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"P10\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"beginningBalance\":3"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"stockInHand\":0"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityRequested\":3"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"calculatedOrderQuantity\":57"));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"quantityApproved\":"+quantityApproved));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"remarks\":\"1\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"orderStatus\":\""+ orderStatus+"\""));
        assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"supplyingFacilityCode\":\"F10\""));
        assertEquals(200, responseEntity.getStatus());
    }

}



