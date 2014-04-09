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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;


public class ProgramProductAPI extends JsonUtility {

  public static final String URL = "http://localhost:9091/rest-api/program-products.json";
  public static final String commTrackUser = "commTrack";
  public static final String GET = "GET";

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(true);
    dbWrapper.updateRestrictLogin("commTrack", true);
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webserviceSmoke"})
  public void shouldVerifyProgramProductWithValidProgramCodeAndValidFacilityType() {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";
    String facilityType = "lvl3_hospital";


    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode + "&facilityTypeCode=" + facilityType + "", GET, commTrackUser, "Admin123");

    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"" + programCode.toUpperCase() + "\",\"programName\":\"HIV\",\"productCode\":\"P10\"," +
        "\"productName\":\"antibiotic\",\"description\":\"TDF/FTC/EFV\",\"unit\":10,\"category\":\"Antibiotics\"}"));

    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"" + programCode.toUpperCase() + "\",\"programName\":\"HIV\",\"productCode\":\"P11\"," +
        "\"productName\":\"antibiotic\",\"description\":\"TDF/FTC/EFV\",\"unit\":10,\"category\":\"Antibiotics\"}"));

  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithValidProgramCodeAndValidFacilityTypeAndLowerCase() {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "hiv";
    String facilityType = "LVL3_HOSPITAL";

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode + "&facilityTypeCode=" + facilityType + "", GET, commTrackUser, "Admin123");

    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"" + programCode.toUpperCase() + "\",\"programName\":\"HIV\",\"productCode\":\"P10\"," +
        "\"productName\":\"antibiotic\",\"description\":\"TDF/FTC/EFV\",\"unit\":10,\"category\":\"Antibiotics\"}"));

    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"" + programCode.toUpperCase() + "\",\"programName\":\"HIV\",\"productCode\":\"P11\"," +
        "\"productName\":\"antibiotic\",\"description\":\"TDF/FTC/EFV\",\"unit\":10,\"category\":\"Antibiotics\"}"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithValidProgramCodeAndInvalidFacilityType() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";
    String facilityType = "testing";

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode + "&facilityTypeCode=" + facilityType + "", GET, commTrackUser, "Admin123");


    List<String> productDetails = dbWrapper.getProductDetailsForProgramAndFacilityType(programCode, facilityType);
    assertTrue("0 records should show up", productDetails.size() == 0);
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid facility type\"}"));

  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithValidProgramCode() {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, commTrackUser, "Admin123");
    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"" + programCode.toUpperCase() + "\",\"programName\":\"HIV\",\"productCode\":\"P10\"," +
        "\"productName\":\"antibiotic\",\"description\":\"TDF/FTC/EFV\",\"unit\":10,\"category\":\"Antibiotics\"}"));

    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"" + programCode.toUpperCase() + "\",\"programName\":\"HIV\",\"productCode\":\"P11\"," +
        "\"productName\":\"antibiotic\",\"description\":\"TDF/FTC/EFV\",\"unit\":10,\"category\":\"Antibiotics\"}"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithoutDescription() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";
    dbWrapper.deleteDescriptionFromProducts();
    dbWrapper.deleteDescriptionFromProducts();

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, commTrackUser, "Admin123");

    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"HIV\",\"programName\":\"HIV\",\"productCode\":\"P11\"," +
        "\"productName\":\"antibiotic\",\"unit\":10,\"category\":\"Antibiotics\"}"));

    assertTrue("Actual Response entity : " + responseEntity.getResponse(),
      responseEntity.getResponse().contains("{\"programCode\":\"HIV\",\"programName\":\"HIV\",\"productCode\":\"P10\"," +
        "\"productName\":\"antibiotic\",\"unit\":10,\"category\":\"Antibiotics\"}"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithInvalidProgramCode() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "testing123";

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, commTrackUser, "Admin123");
    List<String> productDetails = dbWrapper.getProductDetailsForProgram(programCode);
    assertTrue("0 records should show up", productDetails.size() == 0);
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid program code\"}"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWhenProductIsInactiveGlobally() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";
    String productCode = "P10";

    dbWrapper.updateFieldValue("products", "active", "false", "code", productCode);

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, commTrackUser, "Admin123");

    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"" + productCode));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWhenProgramProductIsInactive() throws SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";
    String productCode = "P10";

    dbWrapper.updateActiveStatusOfProgramProduct(productCode, programCode, "false");

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, commTrackUser, "Admin123");

    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"" + productCode));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithProgramCodeNotAllowableLength() {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIVtestingtestingtestingHIVtestingtestingtestingHIVtestingtestingtestingHIVtestingtestingtesting";

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid program code\"}"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithBlankProgramCodeValue() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=", GET, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Invalid program code"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithProgramCodeAttributeNotPresent() {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("", URL, GET, commTrackUser, "Admin123");

    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Required String parameter 'programCode' is not present\"}"));
  }


  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWitInvalidAuthToken() {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, commTrackUser, "testing");

    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithNoHeaders() {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";

    ResponseEntity responseEntity = client.SendJSONWithoutHeaders("", URL + "?programCode=" + programCode, GET, "", "");

    assertTrue("Showing response as : " + responseEntity.getStatus(), responseEntity.getStatus() == 401);
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWitInvalidUser() {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "HIV";

    ResponseEntity responseEntity = client.SendJSON("", URL + "?programCode=" + programCode, GET, "testing", "Admin123");
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Authentication Failed"));
  }

}

