/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.functional;

import org.apache.commons.lang3.StringUtils;
import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.pod.domain.OrderPOD;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;
import static java.lang.String.format;

public class RequisitionStatusFeed extends JsonUtility {

  public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
  public static final String URL = "http://localhost:9091/feeds/requisition-status/";

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(false);
    super.setupDataRequisitionApprove();
    createVirtualFacilityThroughApi("V10", "F10");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-01-30", "2016-01-30", 1, "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId("commTrack", "store in-charge", "N1");
    dbWrapper.insertFulfilmentRoleAssignment("commTrack", "store in-charge", "F10");
    dbWrapper.updateRestrictLogin("commTrack", true);
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webserviceSmoke"})
  public void testRequisitionStatusUsingCommTrackUserForExportOrderFlagFalse() throws IOException, SQLException, ParserConfigurationException, SAXException {
    HttpClient client = new HttpClient();
    client.createContext();
    submitRnRThroughApi("V10", "HIV", "P10", 1, 10, 1, 0, 0, 2);
    Long id = (long) dbWrapper.getMaxRnrID();

    ResponseEntity responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    assertEquals(StringUtils.countMatches(responseEntity.getResponse(), ":"), 41);
    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkRequisitionStatusOnFeed("INITIATED", feedJSONList.get(0), id);
    checkRequisitionStatusOnFeed("SUBMITTED", feedJSONList.get(1), id);
    checkRequisitionStatusOnFeed("AUTHORIZED", feedJSONList.get(2), id);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(false, "F10");

    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack", false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);
    responseEntity = client.SendJSON("", URL + "1", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());

    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkRequisitionStatusOnFeed("APPROVED", feedJSONList.get(3), id);
    checkRequisitionStatusOnFeed("RELEASED", feedJSONList.get(4), id);

    responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkOrderStatusOnFeed("READY_TO_PACK", feedJSONList.get(0), generateOrderNumberForIdAndDefaultConfiguration(id, "HIV"), id);

    dbWrapper.assignRight("store in-charge", "MANAGE_POD");

    OrderPOD OrderPODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, OrderPOD.class);
    OrderPODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
    OrderPODFromJson.getPodLineItems().get(0).setProductCode("P10");

    client.SendJSON(getJsonStringFor(OrderPODFromJson),
      format(POD_URL, generateOrderNumberForIdAndDefaultConfiguration(id, "HIV")),
      "POST",
      "commTrack",
      "Admin123");

    responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkOrderStatusOnFeed("RECEIVED", feedJSONList.get(1), generateOrderNumberForIdAndDefaultConfiguration(id, "HIV"), id);
  }

  @Test(groups = {"webservice"})
  public void testRequisitionStatusUsingCommTrackUserForExportOrderFlagTrue() throws IOException, SQLException, ParserConfigurationException, SAXException, InterruptedException {
    HttpClient client = new HttpClient();
    client.createContext();

    submitRnRThroughApi("V10", "HIV", "P10", 1, 10, 1, 0, 0, 2);
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkRequisitionStatusOnFeed("INITIATED", feedJSONList.get(0), id);
    checkRequisitionStatusOnFeed("SUBMITTED", feedJSONList.get(1), id);
    checkRequisitionStatusOnFeed("AUTHORIZED", feedJSONList.get(2), id);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(true, "F10");
    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack", false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);
    responseEntity = client.SendJSON("", URL + "1", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkRequisitionStatusOnFeed("APPROVED", feedJSONList.get(3), id);
    checkRequisitionStatusOnFeed("RELEASED", feedJSONList.get(4), id);

    responseEntity = waitForOrderStatusUpdatedOrTimeOut(0);
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkOrderStatusOnFeed("IN_ROUTE", feedJSONList.get(0), generateOrderNumberForIdAndDefaultConfiguration(id, "HIV"), id);

    responseEntity = waitForOrderStatusUpdatedOrTimeOut(1);
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkOrderStatusOnFeed("TRANSFER_FAILED", feedJSONList.get(1), generateOrderNumberForIdAndDefaultConfiguration(id, "HIV"), id);
  }

  private String generateOrderNumberForIdAndDefaultConfiguration(Long id, String programCode) {
    return "O" + programCode + String.format("%08d", id) + "R";
  }

  @Test(groups = {"webservice"})
  public void testRequisitionStatusUsingCommTrackUserForExportOrderFlagTrueAndFtpDetailsValid() throws IOException, SQLException, ParserConfigurationException, SAXException, InterruptedException {
    HttpClient client = new HttpClient();
    client.createContext();

    submitRnRThroughApi("V10", "HIV", "P10", 1, 10, 1, 0, 0, 2);
    Long id = (long) dbWrapper.getMaxRnrID();
    ResponseEntity responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkRequisitionStatusOnFeed("INITIATED", feedJSONList.get(0), id);
    checkRequisitionStatusOnFeed("SUBMITTED", feedJSONList.get(1), id);
    checkRequisitionStatusOnFeed("AUTHORIZED", feedJSONList.get(2), id);

    dbWrapper.setExportOrdersFlagInSupplyLinesTable(true, "F10");
    dbWrapper.enterValidDetailsInFacilityFtpDetailsTable("F10");
    approveRequisition(id, 65);
    dbWrapper.updateRestrictLogin("commTrack", false);
    convertToOrder("commTrack", "Admin123");
    dbWrapper.updateRestrictLogin("commTrack", true);
    responseEntity = client.SendJSON("", URL + "1", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkRequisitionStatusOnFeed("APPROVED", feedJSONList.get(3), id);
    checkRequisitionStatusOnFeed("RELEASED", feedJSONList.get(4), id);

    responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkOrderStatusOnFeed("IN_ROUTE", feedJSONList.get(0), generateOrderNumberForIdAndDefaultConfiguration(id, "HIV"), id);

    responseEntity = waitForOrderStatusUpdatedOrTimeOut(1);
    assertEquals(200, responseEntity.getStatus());
    feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    checkOrderStatusOnFeed("RELEASED", feedJSONList.get(1), generateOrderNumberForIdAndDefaultConfiguration(id, "HIV"), id);
  }

  private void checkRequisitionStatusOnFeed(String requisitionStatus, String feedString, Long id) {
    assertTrue("feed json list : " + feedString, feedString.contains("\"requisitionId\":" + id));
    assertTrue("feed json list : " + feedString,
      feedString.contains("\"requisitionStatus\":\"" + requisitionStatus + "\""));
    assertTrue("Response entity : " + feedString, feedString.contains("\"emergency\":false"));
    assertTrue("Response entity : " + feedString, feedString.contains("\"startDate\":1359484200000"));
    assertTrue("Response entity : " + feedString, feedString.contains("\"endDate\":1454178599000"));
    assertTrue("Response entity : " + feedString, feedString.contains("\"stringStartDate\":\"30-01-2013\""));
    assertTrue("Response entity : " + feedString, feedString.contains("\"stringEndDate\":\"30-01-2016\""));
    assertFalse("Response entity : " + feedString, feedString.contains("\"orderStatus\":"));
    assertFalse("Response entity : " + feedString, feedString.contains("\"orderID\""));
  }

  private void checkOrderStatusOnFeed(String orderStatus, String feedString, String orderNumber, Long id) {
    assertTrue("feed json list : " + feedString, feedString.contains("\"requisitionId\":" + id));
    assertTrue("feed json list : " + feedString, feedString.contains("\"requisitionStatus\":\"RELEASED\""));
    assertTrue("Response entity : " + feedString, feedString.contains("\"emergency\":false"));
    assertTrue("Response entity : " + feedString, feedString.contains("\"startDate\":1359484200000"));
    assertTrue("Response entity : " + feedString, feedString.contains("\"endDate\":1454178599000"));
    assertTrue("Response entity : " + feedString, feedString.contains("\"stringStartDate\":\"30-01-2013\""));
    assertTrue("Response entity : " + feedString, feedString.contains("\"stringEndDate\":\"30-01-2016\""));
    assertTrue("Response entity : " + feedString, feedString.contains("\"orderStatus\":\"" + orderStatus + "\""));
    assertTrue("Response entity : " + feedString, feedString.contains("\"orderId\":\"" + orderNumber + "\""));
  }

  private ResponseEntity waitForOrderStatusUpdatedOrTimeOut(int index) throws ParserConfigurationException, SAXException, InterruptedException {
    HttpClient client = new HttpClient();
    client.createContext();
    ResponseEntity responseEntity;

    responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    int time = 0;
    while (time <= 5000) {
      if (feedJSONList.size() == index + 1) {
        break;
      }
      responseEntity = client.SendJSON("", URL + "recent", "GET", "", "");
      feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
      time += 500;
      Thread.sleep(500);
    }
    return responseEntity;
  }
}
