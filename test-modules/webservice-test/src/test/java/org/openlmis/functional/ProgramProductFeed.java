/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;


public class ProgramProductFeed extends TestCaseHelper {

  public static final String URL = "http://localhost:9091/rest-api/programProducts.json";
  public static final String commTrackUser = "commTrack";
  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupDataExternalVendor(true);
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws Exception {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithValidProgramCodeAndValidFacilityType() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "TB";
    String facilityType = "lvl3_hospital";

//    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"facilityTypeCode\":\"" + facilityType + "\"}",
//      URL,
//      GET,
//      commTrackUser,
//      dbWrapper.getAuthToken(commTrackUser));

    ResponseEntity responseEntity = client.SendJSON("", URL+"?programCode="+programCode+"&facilityTypeCode="+facilityType+"", GET, commTrackUser, dbWrapper.getAuthToken(commTrackUser));

    List<String> productDetails = dbWrapper.getProductDetailsForProgramAndFacilityType(programCode,facilityType);
    for (String product : productDetails) {
      String[] productDetailsArray = product.split(",");
      assertTrue("Response entity : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("\"programCode\":\"" + productDetailsArray[0] + "\",\"programName\":\"" + productDetailsArray[1] + "\",\"productCode\":\"" + productDetailsArray[2] + "\"" +
          ",\"productName\":\"" + productDetailsArray[3] + "\",\"description\":\"" + productDetailsArray[4] + "\",\"unit\":\"" + productDetailsArray[5] + "\",\"category\":\"" + productDetailsArray[6] + "\""));
    }
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithValidProgramCodeAndValidFacilityTypeAndLowerCase() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "tb";
    String facilityType = "WAREHOUSE";

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"facilityTypeCode\":\"" + facilityType + "\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    List<String> productDetails = dbWrapper.getProductDetailsForProgramAndFacilityType(programCode,facilityType);
    for (String product : productDetails) {
      String[] productDetailsArray = product.split(",");
      assertTrue("Response entity : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("\"programCode\":\"" + productDetailsArray[0] + "\",\"programName\":\"" + productDetailsArray[1] + "\",\"productCode\":\"" + productDetailsArray[2] + "\"" +
          ",\"productName\":\"" + productDetailsArray[3] + "\",\"description\":\"" + productDetailsArray[4] + "\",\"unit\":\"" + productDetailsArray[5] + "\",\"category\":\"" + productDetailsArray[6] + "\""));
    }
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithValidProgramCodeAndInvalidFacilityType() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "TB";
    String facilityType = "testing";

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"facilityTypeCode\":\"" + facilityType + "\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    List<String> productDetails = dbWrapper.getProductDetailsForProgramAndFacilityType(programCode,facilityType);
    for (String product : productDetails) {
      String[] productDetailsArray = product.split(",");
      assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid facility type\"}"));
    }
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithValidProgramCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "TB";

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));

    List<String> productDetails = dbWrapper.getProductDetailsForProgram(programCode);
    for (String product : productDetails) {
      String[] productDetailsArray = product.split(",");
      assertTrue("Response entity : " + responseEntity.getResponse(),
        responseEntity.getResponse().contains("\"programCode\":\"" + productDetailsArray[0] + "\",\"programName\":\"" + productDetailsArray[1] + "\",\"productCode\":\"" + productDetailsArray[2] + "\"" +
          ",\"productName\":\"" + productDetailsArray[3] + "\",\"description\":\"" + productDetailsArray[4] + "\",\"unit\":\"" + productDetailsArray[5] + "\",\"category\":\"" + productDetailsArray[6] + "\""));
    }
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWitInvalidProgramCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "testing123";

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid Program code\"}"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWhenProductIsInactiveGlobally() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "TB";
    String productCode = "P10";

    dbWrapper.updateActiveStatusOfProduct(productCode, "false");

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"" + productCode));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWhenProgramProductIsInactive() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "TB";
    String productCode = "P10";

    dbWrapper.updateActiveStatusOfProgramProduct(productCode, programCode, "false");

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"productCode\":\"" + productCode));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithSpaceInProgramCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "testing123";

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + "  " + programCode + "   " + "\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("{\"error\":\"Invalid Program code\"}"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithBlankProgramCode() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"\"}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Bad request"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWithBlankJson() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();

    ResponseEntity responseEntity = client.SendJSON("{}",
      URL,
      GET,
      commTrackUser,
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("Bad request"));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWitInvalidAuthToken() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "testing123";

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"}",
      URL,
      GET,
      commTrackUser,
      "testing");
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"Authentication Failed\""));
  }

  @Test(groups = {"webservice"})
  public void shouldVerifyProgramProductWitInvalidUser() throws Exception {
    HttpClient client = new HttpClient();
    client.createContext();
    String programCode = "testing123";

    ResponseEntity responseEntity = client.SendJSON("{\"programCode\":\"" + programCode + "\"}",
      URL,
      GET,
      "testing",
      dbWrapper.getAuthToken(commTrackUser));
    assertTrue("Showing response as : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"Authentication Failed\""));
  }

}

