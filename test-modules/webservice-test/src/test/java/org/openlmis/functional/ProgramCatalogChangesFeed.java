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
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.UploadPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.*;


public class ProgramCatalogChangesFeed extends JsonUtility {

  public static final String URL = "http://localhost:9091/feeds/program-catalog-changes/recent";
  public static final String GET = "GET";
  LoginPage loginPage;

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(true);
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }


  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActive(String[] credentials) throws FileNotFoundException, ParserConfigurationException, SAXException {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

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
    assertEquals(1, feedJSONList.size());

  }

  @Test(groups = {"webserviceSmoke"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActiveUsingCommTrackVendor(String[] credentials) throws FileNotFoundException, ParserConfigurationException, SAXException {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

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

    ResponseEntity responseEntity = client.SendJSON("", URL + "?vendor=commtrack", GET, "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertEquals(1, feedJSONList.size());

  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActiveUsingInvalidVendor(String[] credentials) throws FileNotFoundException, ParserConfigurationException, SAXException {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

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

    ResponseEntity responseEntity = client.SendJSON("", URL + "?vendor=testing", GET, "", "");
    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertEquals(1, feedJSONList.size());

  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedWhenProductIsActiveOrInActiveUsingOpenLmisVendor(String[] credentials) throws FileNotFoundException, ParserConfigurationException, SAXException {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

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

    ResponseEntity responseEntity = client.SendJSON("", URL + "?vendor=openlmis", GET, "", "");

    assertTrue("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertFalse("Response entity : " + responseEntity.getResponse(), responseEntity.getResponse().contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));

    List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertEquals(feedJSONList.size(), 1);

  }

  @Test(groups = {"webservice"}, dataProvider = "Data-Provider-Function-Positive")
  public void shouldVerifyProgramFeedUsingAndConditions(String[] credentials) throws FileNotFoundException, SQLException, ParserConfigurationException, SAXException {
    String Program = "HIV";
    String ProgramSecond = "MALARIA";
    String ProgramThird = "TB";

    HttpClient client = new HttpClient();
    client.createContext();

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
    assertTrue("feed json list : " + feedJSONList.get(2) + ", response : " + responseEntity.getResponse(), feedJSONList.get(2).contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));

    uploadPage.uploadProgramProductMapping("QA_program_product3_inactive_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product4_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProducts("QA_products_prod5active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();


    responseEntity = client.SendJSON("", URL, GET, "", "");
    List<String> feedJSONListUpdated = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONListUpdated.get(0) + ", response : " + responseEntity.getResponse(), feedJSONListUpdated.get(0).contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertTrue("feed json list : " + feedJSONListUpdated.get(1) + ", response : " + responseEntity.getResponse(), feedJSONListUpdated.get(1).contains("\"programCode\":\"" + Program + "\",\"programName\":\"" + Program + "\""));
    assertTrue("feed json list : " + feedJSONListUpdated.get(3) + ", response : " + responseEntity.getResponse(), feedJSONListUpdated.get(3).contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertTrue("feed json list : " + feedJSONListUpdated.get(4) + ", response : " + responseEntity.getResponse(), feedJSONListUpdated.get(4).contains("\"programCode\":\"" + ProgramThird + "\",\"programName\":\"" + ProgramThird + "\""));
    assertEquals(5, feedJSONListUpdated.size());

    uploadPage.uploadProducts("QA_products_prod6active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    uploadPage.uploadProgramProductMapping("QA_program_product6_active_Webservice.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    ResponseEntity responseEntityUpdated = client.SendJSON("", URL, GET, "", "");

    List<String> feedJSONListFinal = XmlUtils.getNodeValues(responseEntityUpdated.getResponse(), "content");
    assertTrue("feed json list : " + feedJSONListFinal.get(0) + ", response : " + responseEntityUpdated.getResponse(), feedJSONListFinal.get(0).contains("\"programCode\":\"" + ProgramSecond + "\",\"programName\":\"" + ProgramSecond + "\""));
    assertEquals(1, feedJSONListFinal.size());
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}

