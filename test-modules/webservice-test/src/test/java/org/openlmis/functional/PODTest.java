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
import org.openlmis.pod.domain.OrderPOD;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.String.format;
import static org.openlmis.UiUtils.HttpClient.POST;


public class PODTest extends JsonUtility {

  public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
  public static final String POD_URL = "http://localhost:9091/rest-api/orders/%s/pod.json";

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(false);
    super.setupDataRequisitionApprove();
    dbWrapper.updateRestrictLogin("commTrack", true);
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webserviceSmoke"}, dataProvider = "Data-Provider")
  public void testValidAndDuplicatePOD(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    orderPODFromJson.getPodLineItems().get(0).setQuantityReturned(05);
    orderPODFromJson.setDeliveredBy("openlmis");
    orderPODFromJson.setReceivedBy("Incharge");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
    assertEquals("RECEIVED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));

    Map<String, String> podLineItemFor = dbWrapper.getPodLineItemFor(dbWrapper.getMaxRnrID(), "P10");
    assertEquals("65", podLineItemFor.get("quantityreceived"));
    assertEquals("5", podLineItemFor.get("quantityreturned"));

    responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Delivery already confirmed\"}");
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void testMandatoryFieldsMissing(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReturned(5);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Missing mandatory fields\"}");
    assertEquals("RELEASED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));

    orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    orderPODFromJson.getPodLineItems().get(0).setQuantityReturned(5);

    responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Missing mandatory fields\"}");
    assertEquals("RELEASED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));

    orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    response = responseEntity.getResponse();

    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
    assertEquals("RECEIVED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
    assertEquals(200, responseEntity.getStatus());

    Map<String, String> podLineItemFor = dbWrapper.getPodLineItemFor(dbWrapper.getMaxRnrID(), "P10");
    assertEquals("65", podLineItemFor.get("quantityreceived"));
    assertEquals(null, podLineItemFor.get("quantityreturned"));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void testQuantityReturnedDataTypeValidation(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    orderPODFromJson.getPodLineItems().get(0).setQuantityReturned(-5);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Invalid returned quantity\"}");
    assertEquals("RELEASED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));

    String wrongJson = "{\"podLineItems\": [" +
      "        {" +
      "            \"productCode\": \"P10\"," +
      "            \"quantityReceived\": \"10\"," +
      "            \"quantityReturned\": \"juhyhv\"," +
      "        }" +
      "    ]" +
      "}";

    responseEntity = client.SendJSON(wrongJson,
      "http://localhost:9091/rest-api/orders/" + id + "/pod", POST, "commTrack", "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("RELEASED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
    assertTrue(responseEntity.getResponse().contains("{\"error\":\"Could not read JSON:"));
    assertTrue(responseEntity.getResponse().contains("not a valid Integer value"));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyPODHavingProductNotAvailableInRnR(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P11");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(650);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
    assertEquals("RECEIVED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
    assertEquals(650, dbWrapper.getPODLineItemQuantityReceived(Long.parseLong(id), "P11"));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyUserPermissionOnWarehouse(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "READY_TO_PACK", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"User does not have permission\"}");
    assertEquals("READY_TO_PACK", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyRoleManagePOD(String userName, String program) throws SQLException, IOException {
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "READY_TO_PACK", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"User does not have permission\"}");
    assertEquals("READY_TO_PACK", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
  }

  @Test(groups = {"webservice"})
  public void verifyInvalidOrderId() throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, 19999999), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Invalid Order ID\"}");
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyInvalidProductCode(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "READY_TO_PACK", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P1000000");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"[P1000000] Invalid product code\"}");
    assertEquals("READY_TO_PACK", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyAuthentication(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "READY_TO_PACK", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack100", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(401, responseEntity.getStatus());
    assertTrue(response.contains("Error 401 Authentication Failed"));
    assertEquals("READY_TO_PACK", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void verifyInvalidQuantityReceived(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", "store in-charge", "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "TRANSFER_FAILED", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(-65);

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, id), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(400, responseEntity.getStatus());
    assertEquals(response, "{\"error\":\"Invalid received quantity\"}");
    assertEquals("TRANSFER_FAILED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));

    String wrongJson = "{\"podLineItems\": [" +
      "        {" +
      "            \"productCode\": \"P10\"," +
      "            \"quantityReceived\": \"uhj\"," +
      "            \"quantityReturned\": \"12\"," +

      "        }" +
      "    ]" +
      "}";

    responseEntity = client.SendJSON(wrongJson,
      "http://localhost:9091/rest-api/orders/" + id + "/pod", POST, "commTrack", "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("TRANSFER_FAILED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
    assertTrue(responseEntity.getResponse().contains("{\"error\":\"Could not read JSON:"));
    assertTrue(responseEntity.getResponse().contains("not a valid Integer value"));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void testShowErrorMessageForUnrecognizedFieldInAPI(String userName, String program) throws SQLException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    String wrongJson = "{\"podLineItems\": [" +
      "        {" +
      "            \"productCode\": \"P10\"," +
      "            \"quantityReceived\": \"10\"," +
      "            \"beginningbbbb\": \"10\"," +

      "        }" +
      "    ]" +
      "}";

    ResponseEntity responseEntity = client.SendJSON(wrongJson,
      "http://localhost:9091/rest-api/orders/" + id + "/pod", POST, "commTrack", "Admin123");

    assertEquals(400, responseEntity.getStatus());
    assertEquals("RELEASED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
    assertTrue(responseEntity.getResponse().contains("{\"error\":\"Could not read JSON: Unrecognized field"));
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void testVerifyDeliveryDetailsWhenSubmittedByConfirmDeliveryAPI(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    String orderId = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    orderPODFromJson.getPodLineItems().get(0).setQuantityReturned(95);
    orderPODFromJson.setDeliveredBy("openLMIS");
    orderPODFromJson.setReceivedBy("facility Incharge");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, orderId), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider")
  public void testVerifyDeliveryDetailsWhen(String userName, String program) throws SQLException, IOException {
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");

    HttpClient client = new HttpClient();
    client.createContext();

    createOrder(userName, "RELEASED", program);
    String orderId = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD orderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    orderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    orderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    orderPODFromJson.getPodLineItems().get(0).setQuantityReturned(95);
    orderPODFromJson.setDeliveredBy("openLMIS");
    orderPODFromJson.setReceivedBy("facility Incharge");

    ResponseEntity responseEntity = client.SendJSON(getJsonStringFor(orderPODFromJson),
      format(POD_URL, orderId), "POST", "commTrack", "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
  }

  @DataProvider(name = "Data-Provider")
  public Object[][] parameterIntTest() {
    return new Object[][]{
      {"commTrack1", "HIV"}
    };
  }
}

