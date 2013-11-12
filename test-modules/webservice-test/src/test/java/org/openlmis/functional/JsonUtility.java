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
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.ConvertOrderPage;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.restapi.domain.Report;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;


public class JsonUtility extends TestCaseHelper {
  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
  public static final String FULL_JSON_APPROVE_TXT_FILE_NAME = "ReportJsonApprove.txt";
  public static final String STORE_IN_CHARGE = "store in-charge";

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
  public static void submitRequisition(String userName, String program) throws Exception {
      dbWrapper.insertRequisitions(1, program, true);
      dbWrapper.updateRequisitionStatus("SUBMITTED", userName, program);
  }


  public static Long getRequisitionIdFromResponse(String response) {
    return Long.parseLong(response.substring(response.lastIndexOf(":") + 1, response.lastIndexOf("}")));
  }

  public static void createOrder(String userName, String status, String program) throws Exception {
      dbWrapper.insertRequisitions(1, program, true);
      dbWrapper.updateRequisitionStatus("SUBMITTED", userName, program);
      dbWrapper.updateRequisitionStatus("APPROVED", userName, program);
      dbWrapper.insertApprovedQuantity(1);
      dbWrapper.insertFulfilmentRoleAssignment(userName,"store in-charge","F10");
      dbWrapper.insertOrders(status, userName, program);
      dbWrapper.updatePacksToShip("1");
  }

  public static void approveRequisition(Long id, int quantityApproved) throws Exception {

    HttpClient client = new HttpClient();
    client.createContext();

    Report reportFromJson = readObjectFromFile(FULL_JSON_APPROVE_TXT_FILE_NAME, Report.class);
    reportFromJson.setUserName("commTrack1");
    reportFromJson.setRequisitionId(id);
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(0).setQuantityApproved(quantityApproved);

    client.SendJSON(getJsonStringFor(reportFromJson),
      "http://localhost:9091/rest-api/requisitions/" + id + "/approve",
      "PUT",
      "commTrack",
      "Admin123");
  }

  public static void convertToOrder(String userName,String password) throws Exception {
    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);
    HomePage homePage = loginPage.loginAs(userName, password);
    ConvertOrderPage convertOrderPage = homePage.navigateConvertToOrder();
    convertOrderPage.convertToOrder();
  }

}

