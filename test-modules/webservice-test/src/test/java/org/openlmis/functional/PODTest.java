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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.String.format;
import static org.openlmis.UiUtils.HttpClient.POST;


public class PODTest extends JsonUtility {

  public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
  public static final String POD_URL = "http://localhost:9091/rest-api/orders/%s/pod.json";

  @BeforeMethod(groups = {"webservice","webserviceSmoke"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(false);
    super.setupDataRequisitionApprover();
    dbWrapper.updateRestrictLogin("commTrack", true);
  }

  @AfterMethod(groups = {"webservice","webserviceSmoke"})
  public void tearDown() throws IOException, SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webserviceSmoke"}, dataProvider = "Data-Provider")
  public void testValidAndDuplicatePOD(String userName, String program) throws Exception {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();

    client.createContext();

    createOrder(userName, "RELEASED", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P10");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
    assertEquals("RECEIVED", dbWrapper.getOrderStatus(id));

    dbWrapper.verifyPODAndPODLineItems(id, "P10", 65);

    responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");
    response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Delivery already confirmed\"}");
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyPODHavingProductNotAvailableinRnR(String userName, String program) throws Exception {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    HttpClient client = new HttpClient();

    client.createContext();

    createOrder(userName, "RELEASED", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P11");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(650);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
    assertEquals("RECEIVED", dbWrapper.getOrderStatus(id));

    dbWrapper.verifyPODAndPODLineItems(id, "P11", 650);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyUserPermissionOnWarehouse(String userName, String program) throws Exception {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    HttpClient client = new HttpClient();

    client.createContext();

    createOrder(userName, "READY_TO_PACK", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P10");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"User does not have permission\"}");
    assertEquals("READY_TO_PACK", dbWrapper.getOrderStatus(id));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyRoleManagePOD(String userName, String program) throws Exception {
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    HttpClient client = new HttpClient();

    client.createContext();
    createOrder(userName, "READY_TO_PACK", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P10");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"User does not have permission\"}");
    assertEquals("READY_TO_PACK", dbWrapper.getOrderStatus(id));
  }

  @Test(groups = {"webservice"})
  public void verifyInvalidOrderId() throws Exception {
    HttpClient client = new HttpClient();

    client.createContext();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P10");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, 19999999),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Invalid Order ID\"}");
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyInvalidProductCode(String userName, String program) throws Exception {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    HttpClient client = new HttpClient();

    client.createContext();

    createOrder(userName, "READY_TO_PACK", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P1000000");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"[P1000000] Invalid product code\"}");
    assertEquals("READY_TO_PACK", dbWrapper.getOrderStatus(id));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyAuthentication(String userName, String program) throws Exception {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    HttpClient client = new HttpClient();

    client.createContext();

    createOrder(userName, "READY_TO_PACK", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P10");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack100",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(401, responseEntity.getStatus());
    assertTrue(response.contains("Error 401 Authentication Failed"));
    assertEquals("READY_TO_PACK", dbWrapper.getOrderStatus(id));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyInvalidQuantity(String userName, String program) throws Exception {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");
    HttpClient client = new HttpClient();

    client.createContext();

    createOrder(userName, "TRANSFER_FAILED", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
    PODFromJson.getPodLineItems().get(0).setProductCode("P10");
    PODFromJson.getPodLineItems().get(0).setQuantityReceived(-65);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(PODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Invalid received quantity\"}");
    assertEquals("TRANSFER_FAILED", dbWrapper.getOrderStatus(id));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void testShowErrorMessageForUnrecognizedFieldInAPI(String userName, String program) throws Exception {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    Long id = (long) dbWrapper.getMaxRnrID();

    String wrongJson = "{\"podLineItems\": [" +
      "        {" +
      "            \"productCode\": \"P10\"," +
      "            \"quantityReceived\": \"10\"," +
      "            \"beginningbbbb\": \"10\"," +

      "        }" +
      "    ]" +
      "}";
    ResponseEntity responseEntity =
      client.SendJSON(wrongJson,
        "http://localhost:9091/rest-api/orders/"+id+"/pod",
        POST,
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();
    assertEquals(400, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"error\":\"Could not read JSON: Unrecognized field"));


  }

  @DataProvider(name = "Data-Provider")
  public Object[][] parameterIntTest() {
    return new Object[][]{
      {"commTrack1", "HIV"}
    };
  }
}

