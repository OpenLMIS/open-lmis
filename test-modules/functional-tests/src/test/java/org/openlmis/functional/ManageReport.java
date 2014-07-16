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


import org.openlmis.UiUtils.CaptureScreenshotOnFailureListener;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.HomePage;
import org.openlmis.pageobjects.LoginPage;
import org.openlmis.pageobjects.PageObjectFactory;
import org.openlmis.pageobjects.ReportPage;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static com.thoughtworks.selenium.SeleneseTestBase.*;

@Listeners(CaptureScreenshotOnFailureListener.class)

public class ManageReport extends TestCaseHelper {

  String reportName, fileName;
  LoginPage loginPage;
  ReportPage reportPage;
  static String separator = System.getProperty("file.separator");
  private static String downloadedFilePath = new File(System.getProperty("user.dir")).getParent() + separator + "csv";

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", "MANAGE_REPORT");
    dbWrapper.deleteRowFromTable("report_rights", "rightName", reportName);
    dbWrapper.deleteRowFromTable("templates", "name", reportName);
    dbWrapper.deleteRowFromTable("rights", "name", reportName);
    reportName = "Test-Report";
    fileName = "OrderRoutingConsistencyReport.jrxml";
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void invalidScenariosReports(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertEquals("No Reports", reportPage.getNoReportsMessage());

    reportPage.clickAddNewButton();
    assertEquals("Report name *", reportPage.getNameLabel());
    assertEquals("Description", reportPage.getDescriptionLabel());
    assertEquals("Upload file *", reportPage.getUploadFileLabel());
    assertTrue("Save button missing", reportPage.isSaveButtonDisplayed());
    assertTrue("Cancel button missing", reportPage.isCancelButtonDisplayed());

    reportPage.clickSaveButton();
    assertEquals("Please fill this value", reportPage.getErrorReportNameMessage());
    assertEquals("Please fill this value", reportPage.getErrorFileMessage());

    reportPage.enterReportName(reportName);
    reportPage.clickSaveButton();
    assertEquals("Please fill this value", reportPage.getErrorFileMessage());

    reportPage.enterReportName("");
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Please fill this value", reportPage.getErrorReportNameMessage());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadWrongReport(String[] credentials) {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    reportPage.clickAddNewButton();

    testWebDriver.sleep(1000);
    fileName = "invalidActivefacility.jrxml";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    testWebDriver.sleep(1000);
    assertEquals("File uploaded is invalid", reportPage.getSaveErrorMessage());
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyDuplicateReport(String[] credentials) throws SQLException {
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.enterReportDescription("describe");
    reportPage.clickSaveButton();

    reportPage.clickAddNewButton();
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report with same name already exists", reportPage.getSaveErrorMessage());

    reportPage.clickCancelButton();
    assertEquals("REPORTING", dbWrapper.getAttributeFromTable("rights", "rightType", "name", reportName));
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void verifyDefaultReports(String[] credentials) throws SQLException, IOException, InterruptedException {
    dbWrapper.insertConsistencyReportsViewRights("Admin");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportName = "Delivery Zones Missing Manage Distribution Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));
    reportName = "Facilities Missing Authorize Requisition Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(2).equalsIgnoreCase(reportName));
    reportName = "Facilities Missing Create Requisition Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(3).equalsIgnoreCase(reportName));
    reportName = "Facilities Missing Supporting Requisition Group";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(4).equalsIgnoreCase(reportName));
    reportName = "Order Routing Inconsistencies";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(5).equalsIgnoreCase(reportName));
    reportName = "Requisition Groups Missing Supply Line";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(6).equalsIgnoreCase(reportName));
    reportName = "Supervisory Nodes Missing Approve Requisition Role";
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(7).equalsIgnoreCase(reportName));

    reportPage.clickReport(1);
    reportPage.clickCsvLink();
//    getReportData();
//    deleteFile(downloadedFilePath);
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadValidNoParameterReport(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "noParameterReport.jrxml";
    reportName = "noParameterReport";
    reportPage.enterReportName(reportName);
    reportPage.enterReportDescription("no Parameter Reports#");
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    assertEquals("REPORTING", dbWrapper.getAttributeFromTable("rights", "rightType", "name", "noParameterReport"));
    assertEquals("0", dbWrapper.getRowsCountFromDB("template_parameters"));
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertEquals("Reports", reportPage.getReportHeader());
    assertEquals("Report name", reportPage.getReportNameHeader());
    assertEquals("Description", reportPage.getDescriptionHeader());
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));
    assertTrue(reportPage.getReportDescription(1).equalsIgnoreCase("no Parameter Reports#"));

    reportPage.clickReport(1);
    assertEquals(reportName, reportPage.getReportName());
    verifyLinksOnReportView();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadValidSingleBooleanParameterReportWithAllProperties(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F11");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "validSingleBooleanParameterReport.jrxml";
    reportName = "valid Single Boolean Parameter Report";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    assertEquals("1", dbWrapper.getRowsCountFromDB("template_parameters"));
    String parameterName = "isEnabled";
    verifyParameterDetails(parameterName, "isEnable", "true", "is facility enabled", "java.lang.Boolean");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));
    assertEquals("", reportPage.getReportDescription(1));

    reportPage.clickReport(1);
    assertEquals("isEnable", reportPage.getParameterDisplayName(parameterName));
    assertEquals("is facility enabled", reportPage.getParameterDescription(parameterName));
    assertTrue(reportPage.isParameterTrueOptionSelected(parameterName));
    assertFalse(reportPage.isParameterFalseOptionSelected(parameterName));
    verifyLinksOnReportTableView();
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadValidSingleIntParameterReportWithAllProperties(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    dbWrapper.updateFieldValue("facilities", "typeId", dbWrapper.getAttributeFromTable("facility_types", "id", "code", "warehouse"),"code","F11");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "validSingleIntParameterReport.jrxml";
    reportName = "valid Single Int Parameter Report";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    assertEquals("REPORTING", dbWrapper.getAttributeFromTable("rights", "rightType", "name", reportName));
    verifyParameterDetails("typeId", "facilityTypeId", "2", "id for facility type", "java.lang.Integer");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));

    reportPage.clickReport(1);
    String parameterName = "typeId";
    assertEquals("facilityTypeId", reportPage.getParameterDisplayName(parameterName));
    assertEquals("id for facility type", reportPage.getParameterDescription(parameterName));
    assertEquals("2", reportPage.getParameterInt(parameterName));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadValidSingleStringParameterReportWithAllProperties(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "validSingleStringParameterReport.jrxml";
    reportName = "valid Single String Parameter Report";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    assertEquals("REPORTING", dbWrapper.getAttributeFromTable("rights", "rightType", "name", reportName));
    verifyParameterDetails("code", "facilityCode", "F10", "facility code", "java.lang.String");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));

    reportPage.clickReport(1);
    String parameterName = "code";
    assertEquals("facilityCode", reportPage.getParameterDisplayName(parameterName));
    assertEquals("facility code", reportPage.getParameterDescription(parameterName));
    assertEquals("F10", reportPage.getParameterString(parameterName));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadValidSingleDateParameterReportWithAllProperties(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    dbWrapper.updateFieldValue("facilities", "goLiveDate","2014-07-01","code","F11");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "validSingleDateParameterReport.jrxml";
    reportName = "valid Single Date Parameter Report";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    assertEquals("REPORTING", dbWrapper.getAttributeFromTable("rights", "rightType", "name", reportName));
    verifyParameterDetails("goLiveDate", "facilityGoLiveDate", "01/07/14", "go live date of facility", "java.util.Date");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));

    reportPage.clickReport(1);
    String parameterName = "goLiveDate";
    assertEquals("facilityGoLiveDate", reportPage.getParameterDisplayName(parameterName));
    assertEquals("go live date of facility", reportPage.getParameterDescription(parameterName));
    assertEquals("01/07/2014", reportPage.getParameterDate(parameterName));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadReportWithoutDisplayNameOfParameter(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    int numberOfExistingReportRights = Integer.parseInt(dbWrapper.getRowsCountFromDB("report_rights"));
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "noDisplayNameOfParameterInReport.jrxml";
    reportName = "no Display Name Of Parameter In Report";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Display name is missing for parameter \"code\"", reportPage.getSaveErrorMessage());
    assertEquals(numberOfExistingReportRights, Integer.parseInt(dbWrapper.getRowsCountFromDB("report_rights")));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadReportWithoutOptionalPropertiesOfParameter(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "onlyMandatoryPropertiesOfParameter.jrxml";
    reportName = "only Mandatory Properties Of Parameter";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    assertEquals("REPORTING", dbWrapper.getAttributeFromTable("rights", "rightType", "name", reportName));
    verifyParameterDetails("code", "facilityCode", null, null, "java.lang.String");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));

    reportPage.clickReport(1);
    String parameterName = "code";
    assertEquals("facilityCode", reportPage.getParameterDisplayName(parameterName));
    assertEquals("", reportPage.getParameterDescription(parameterName));
    assertEquals("", reportPage.getParameterString(parameterName));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadReportWithExtraPropertiesOfParameter(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    int numberOfExistingReportRights = Integer.parseInt(dbWrapper.getRowsCountFromDB("report_rights"));
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "extraPropertiesOfParameter.jrxml";
    reportName = "extra Properties Of Parameter";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Unidentified property found for parameter \"code\"", reportPage.getSaveErrorMessage());
    assertEquals(numberOfExistingReportRights, Integer.parseInt(dbWrapper.getRowsCountFromDB("report_rights")));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadReportWithExtraParameters(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "extraParametersInReport.jrxml";
    reportName = "extra Parameters In Report";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    verifyParameterDetails("code", "facilityCode", "F10", null, "java.lang.String");
    verifyParameterDetails("name", "facilityName", "Facility", null, "java.lang.String");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));

    reportPage.clickReport(1);
    String parameterName = "za";
    assertEquals("za", reportPage.getParameterDisplayName(parameterName));
    assertEquals("", reportPage.getParameterDescription(parameterName));
    assertEquals("", reportPage.getParameterString(parameterName));

    parameterName = "code";
    assertEquals("facilityCode", reportPage.getParameterDisplayName(parameterName));
    assertEquals("", reportPage.getParameterDescription(parameterName));
    assertEquals("F10", reportPage.getParameterString(parameterName));

    parameterName = "name";
    assertEquals("facilityName", reportPage.getParameterDisplayName(parameterName));
    assertEquals("", reportPage.getParameterDescription(parameterName));
    assertEquals("Facility", reportPage.getParameterString(parameterName));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadReportWithMissingParameters(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    int numberOfExistingReportRights = Integer.parseInt(dbWrapper.getRowsCountFromDB("report_rights"));
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "missingParametersInReport.jrxml";
    reportName = "missing Parameters In Report";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("File uploaded is invalid", reportPage.getSaveErrorMessage());
    assertEquals(numberOfExistingReportRights, Integer.parseInt(dbWrapper.getRowsCountFromDB("report_rights")));
  }

  @Test(groups = {"admin"}, dataProvider = "Data-Provider-Function-Positive")
  public void uploadReportWithDefaultValueAndTypeOfParameterMismatch(String[] credentials) throws SQLException {
    dbWrapper.insertFacilities("F10", "F11");
    dbWrapper.insertFacilities("F12", "F13");
    HomePage homePage = loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();

    reportPage.clickAddNewButton();
    fileName = "defaultValueAndTypeOfParameterMismatch.jrxml";
    reportName = "default Value And Type Of Parameter Mismatch";
    reportPage.enterReportName(reportName);
    reportPage.uploadFile(fileName);
    reportPage.clickSaveButton();
    assertEquals("Report created successfully", reportPage.getSaveSuccessMessage());
    verifyParameterDetails("id", "fCode","abc",null,"java.lang.Integer");
    homePage.logout();

    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.assignRight("Admin", reportName);
    loginPage.loginAs(credentials[0], credentials[1]);
    reportPage = homePage.navigateReportScreen();
    assertTrue("Report Name '" + reportName + "' should display in list", reportPage.getReportName(1).equalsIgnoreCase(reportName));

    reportPage.clickReport(1);
    String parameterName = "id";
    assertEquals("fCode", reportPage.getParameterDisplayName(parameterName));
    assertEquals("", reportPage.getParameterDescription(parameterName));
    assertEquals("abc", reportPage.getParameterInt(parameterName));
  }

  public void verifyLinksOnReportView() {
    assertTrue("PDF link missing", reportPage.isPDFLinkDisplayed());
    assertTrue("XLS link missing", reportPage.isXLSLinkDisplayed());
    assertTrue("CSV link missing", reportPage.isCSVLinkDisplayed());
    assertTrue("HTML link missing", reportPage.isHTMLLinkDisplayed());
  }

  public void verifyLinksOnReportTableView() {
    assertTrue("PDF link missing", reportPage.isPDFTableLinkDisplayed());
    assertTrue("XLS link missing", reportPage.isXLSTableLinkDisplayed());
    assertTrue("CSV link missing", reportPage.isCSVTableLinkDisplayed());
    assertTrue("HTML link missing", reportPage.isHTMLTableLinkDisplayed());
  }

  public void verifyParameterDetails(String parameterName, String displayName, String defaultValue, String description, String parameterType) throws SQLException {
    assertEquals(displayName, dbWrapper.getAttributeFromTable("template_parameters", "displayName", "name", parameterName));
    assertEquals(defaultValue, dbWrapper.getAttributeFromTable("template_parameters", "defaultValue", "name", parameterName));
    assertEquals(description, dbWrapper.getAttributeFromTable("template_parameters", "description", "name", parameterName));
    assertEquals(parameterType, dbWrapper.getAttributeFromTable("template_parameters", "dataType", "name", parameterName));
  }

  public String[] getReportData() throws InterruptedException, IOException, SQLException {
    Thread.sleep(2500);
    return (readCSVFile(downloadedFilePath));
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.removeAllExistingRights("Admin");
    dbWrapper.deleteRowFromTable("report_rights", "rightName", reportName);
    dbWrapper.deleteRowFromTable("rights", "name", reportName);
    if(Integer.parseInt(dbWrapper.getRowsCountFromDB("template_parameters"))>0) {
      dbWrapper.deleteRowFromTable("template_parameters", "templateId", dbWrapper.getAttributeFromTable("templates", "id", "name", reportName));
    }
    dbWrapper.deleteRowFromTable("templates", "name", reportName);
    dbWrapper.insertAllAdminRightsAsSeedData();
    dbWrapper.closeConnection();
  }

  @DataProvider(name = "Data-Provider-Function-Positive")
  public Object[][] parameterIntTestProviderPositive() {
    return new Object[][]{
      {new String[]{"Admin123", "Admin123"}}
    };
  }
}
