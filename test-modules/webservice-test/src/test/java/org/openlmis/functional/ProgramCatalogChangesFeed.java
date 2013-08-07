/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.UploadPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


public class ProgramCatalogChangesFeed extends TestCaseHelper {

  public static final String URL = "http://localhost:9091/feeds/programCatalogChanges/recent";
  public static final String GET = "GET";

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


  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActive(String[] credentials) throws Exception {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProductCategory("QA_Productcategoryupload_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProductGroupsScenarios("QA_product_group_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Not_Mapped_With_Program_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntity = client.SendJSON("", URL, GET, "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertEquals(feedJSONList.size(), 1);

    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActiveUsingCommTrackVendor(String[] credentials) throws Exception {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProductCategory("QA_Productcategoryupload_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProductGroupsScenarios("QA_product_group_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Not_Mapped_With_Program_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntity = client.SendJSON("", URL+"?vendor=commtrack", GET, "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertEquals(feedJSONList.size(), 1);

    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActiveUsingInvalidVendor(String[] credentials) throws Exception {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProductCategory("QA_Productcategoryupload_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProductGroupsScenarios("QA_product_group_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Not_Mapped_With_Program_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntity = client.SendJSON("", URL+"?vendor=testing", GET, "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertEquals(feedJSONList.size(), 1);

    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActiveUsingOpenLmisVendor(String[] credentials) throws Exception {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProductCategory("QA_Productcategoryupload_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProductGroupsScenarios("QA_product_group_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_inactive_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product_active_with_product_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_Not_Mapped_With_Program_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntity = client.SendJSON("", URL+"?vendor=openlmis", GET, "", "");

    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertEquals(feedJSONList.size(), 1);

    homePage.logout(baseUrlGlobal);
  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedUsingAndConditions(String[] credentials) throws Exception {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

    LoginPage loginPage = new LoginPage(testWebDriver, baseUrlGlobal);

    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadProductCategory("QA_Productcategoryupload_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProductGroupsScenarios("QA_product_group_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_For_And_Condition_Setup_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    dbWrapper.insertProgramProduct("prod1active", "HIV", "30", "TRUE");
    dbWrapper.insertProgramProduct("prod2active", "HIV", "30", "TRUE");
    dbWrapper.insertProgramProduct("prod3active", "HIV", "30", "TRUE");
    dbWrapper.insertProgramProduct("prod4active", "MALARIA", "30", "FALSE");
    dbWrapper.insertProgramProduct("prod5inactive", "TB", "30", "TRUE");
    dbWrapper.insertProgramProduct("prod6inactive", "MALARIA", "30", "FALSE");

    uploadPage.uploadProducts("QA_products_prod1inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product2_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_prod3inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntity = client.SendJSON("", URL, GET, "", "");
    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONList.get(2) + ", response : "+responseEntity.getResponse(), feedJSONList.get(2).contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));

    uploadPage.uploadProgramProductMapping("QA_program_product3_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product4_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_prod5active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();


    responseEntity = client.SendJSON("", URL, GET, "", "");
    List<String> feedJSONListUpdated = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONListUpdated.get(0)+ ", response : "+responseEntity.getResponse(), feedJSONListUpdated.get(0).contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertTrue("feed json list : " + feedJSONListUpdated.get(1)+ ", response : "+responseEntity.getResponse(), feedJSONListUpdated.get(1).contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertTrue("feed json list : " + feedJSONListUpdated.get(3)+ ", response : "+responseEntity.getResponse(), feedJSONListUpdated.get(3).contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertTrue("feed json list : " + feedJSONListUpdated.get(4)+ ", response : "+responseEntity.getResponse(), feedJSONListUpdated.get(4).contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));
    assertEquals(feedJSONList.size(), 5);

    uploadPage.uploadProducts("QA_products_prod6active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_program_product6_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntityUpdated = client.SendJSON("", URL, GET, "", "");

    List<String> feedJSONListFinal = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONListFinal.get(0)+ ", response : "+responseEntityUpdated.getResponse(), feedJSONListFinal.get(0).contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertEquals(feedJSONListUpdated.size(), 1);

    homePage.logout(baseUrlGlobal);
  }


  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}

