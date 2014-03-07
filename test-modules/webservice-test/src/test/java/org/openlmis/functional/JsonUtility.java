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

import org.codehaus.jackson.map.ObjectMapper;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.edi.ConvertOrderPage;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.restapi.domain.Agent;
import org.openlmis.restapi.domain.Report;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static java.lang.String.format;
import static org.openlmis.UiUtils.HttpClient.POST;

public class JsonUtility extends TestCaseHelper {

  public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "ReportJsonApprove.txt";
  public static final String STORE_IN_CHARGE = "store in-charge";
  public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
  public static final String POD_URL = "http://localhost:9091/rest-api/orders/%s/pod.json";

  public static <T> T readObjectFromFile(String fullJsonTxtFileName, Class<T> clazz) throws IOException {
    String classPathFile = JsonUtility.class.getClassLoader().getResource(fullJsonTxtFileName).getFile();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(new File(classPathFile), clazz);
  }

  public static String getJsonStringFor(Object object) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    StringWriter writer = new StringWriter();
    objectMapper.writeValue(writer, object);
    return writer.toString();
  }

  public static void submitRequisition(String userName, String program) throws SQLException {
    dbWrapper.insertRequisitions(1, program, true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userName, program);
  }

  public static void createOrder(String userName, String status, String program) throws SQLException {
    dbWrapper.insertRequisitions(1, program, true, "2012-12-01", "2015-12-01", "F10", false);
    dbWrapper.updateRequisitionStatus("SUBMITTED", userName, program);
    dbWrapper.updateRequisitionStatus("APPROVED", userName, program);
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 1);
    dbWrapper.insertFulfilmentRoleAssignment(userName, "store in-charge", "F10");
    dbWrapper.insertOrders(status, userName, program);
    dbWrapper.updatePacksToShip("1");
  }

  public static void approveRequisition(Long id, int quantityApproved) throws IOException {
    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setApproverName("Dummy");
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(quantityApproved);
    reportFromJson.getProducts().get(0).setRemarks("1");

    client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
      "PUT",
      "commTrack",
      "Admin123");
  }

  public static void convertToOrder(String userName, String password) {
    LoginPage loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userName, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.convertToOrder();
  }

  public Long submitRnRThroughApi(String agentCode, String program, String product, Integer beginningBalance,
                                  Integer stockInHand,
                                  Integer quantityConsumed, Integer quantityReceived, Integer newPatientCount,
                                  Integer stockOutDays) throws IOException, SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile("ReportMinimumJson.txt", Report.class);
    reportFromJson.setAgentCode(agentCode);
    reportFromJson.setProgramCode(program);
    reportFromJson.getProducts().get(0).setProductCode(product);
    reportFromJson.getProducts().get(0).setBeginningBalance(beginningBalance);
    reportFromJson.getProducts().get(0).setQuantityDispensed(quantityConsumed);
    reportFromJson.getProducts().get(0).setQuantityReceived(quantityReceived);
    reportFromJson.getProducts().get(0).setStockInHand(stockInHand);
    reportFromJson.getProducts().get(0).setNewPatientCount(newPatientCount);
    reportFromJson.getProducts().get(0).setStockOutDays(stockOutDays);

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        "Admin123");

    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));
    return (long) dbWrapper.getMaxRnrID();
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

  public void convertToOrderAndUpdatePOD(String userName, String program, Integer quantityReceived) throws SQLException, IOException {
    HttpClient client = new HttpClient();
    client.createContext();
    dbWrapper.assignRight("store in-charge", "MANAGE_POD");
    dbWrapper.setupUserForFulfillmentRole("commTrack", STORE_IN_CHARGE, "F10");
    dbWrapper.updateRequisitionStatus("APPROVED", "commTrack", "HIV");
    dbWrapper.updateFieldValue("requisition_line_items", "quantityApproved", 10);
    dbWrapper.insertOrders("RELEASED", userName, program);
    dbWrapper.updatePacksToShip("1");
    String id = String.valueOf(dbWrapper.getMaxRnrID());

    OrderPOD OrderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    OrderPODFromJson.getPodLineItems().get(0).setProductCode("P10");
    OrderPODFromJson.getPodLineItems().get(0).setQuantityReceived(quantityReceived);

    ResponseEntity responseEntity =
      client.SendJSON(getJsonStringFor(OrderPODFromJson),
        format(POD_URL, id),
        "POST",
        "commTrack",
        "Admin123");

    String response = responseEntity.getResponse();

    assertEquals(200, responseEntity.getStatus());
    assertEquals(response, "{\"success\":\"POD updated successfully\"}");
    assertEquals("RECEIVED", dbWrapper.getAttributeFromTable("orders", "status", "id", id));
  }
}

