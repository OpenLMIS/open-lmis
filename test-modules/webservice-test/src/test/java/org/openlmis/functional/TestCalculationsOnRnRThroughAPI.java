/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.restapi.domain.Report;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openlmis.UiUtils.HttpClient.POST;

public class TestCalculationsOnRnRThroughAPI extends JsonUtility {

  @BeforeMethod(groups = {"webservice", "webserviceSmoke"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    super.setupTestData(true);
    createVirtualFacilityThroughApi("V10", "F10");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-01-30", "2016-01-30", 1, "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId("commTrack", "store in-charge", "N1");
    dbWrapper.updateRestrictLogin("commTrack", true);
    dbWrapper.updateFieldValue("products", "fullSupply", "true", "code", "P11");
  }

  @AfterMethod(groups = {"webservice", "webserviceSmoke"})
  public void tearDown() throws SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webserviceSmoke"})
  public void testCalculationWhenReportingDaysUndefined() throws IOException, SQLException {
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 10, 5, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "previousStockInHand", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
  }

  @Test(groups = {"webserviceSmoke"})
  public void testCalculationWhenReportingDaysZero() throws Exception {
    submitRnRThroughApi("V10", "HIV", "P10", 10, 5, null, null, null, null);
    Long id = submitRnRThroughApi("V10", "HIV", "P10", null, 3, null, null, null, null);
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(id, "previousStockInHand", "P10"));
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationAndTracingWhenMultipleProducts() throws IOException, SQLException {
    submitRnRThroughApi("V10", "HIV", "P10", 10, 5, null, null, null, null);
    submitRnRThroughApi("V10", "HIV", "P11", 5, 3, null, null, null, null);

    HttpClient client = new HttpClient();
    client.createContext();
    Report reportFromJson = JsonUtility.readObjectFromFile("ReportJsonMultipleProducts.txt", Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(1).setProductCode("P11");
    reportFromJson.getProducts().get(0).setStockInHand(2);
    reportFromJson.getProducts().get(1).setStockInHand(2);

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        "Admin123");

    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));

    Long id = (long) dbWrapper.getMaxRnrID();
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P11"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P11"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P11"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P11"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWhenSameProductInTwoPrograms() throws SQLException, IOException {
    dbWrapper.insertProgramProduct("P10", "ESS_MEDS", "10", "true");
    dbWrapper.insertProgramProduct("P11", "ESS_MEDS", "10", "true");
    dbWrapper.insertFacilityApprovedProduct("P10", "ESS_MEDS", "lvl3_hospital");
    dbWrapper.insertRoleAssignmentForSupervisoryNode("commTrack", "store in-charge", "N1", "ESS_MEDS");
    dbWrapper.configureTemplateForCommTrack("ESS_MEDS");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgramAfterDelete("RG1", "ESS_MEDS", "M");
    submitRnRThroughApi("V10", "ESS_MEDS", "P10", 5, 3, null, null, null, null);
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 10, 5, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingWhenNumberOfMonthsIs1() throws IOException, SQLException {
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 40, 37, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
    Date d = new Date();
    String dateBefore = ft.format(new Date(d.getTime() - 7 * 24 * 3600 * 1000));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges(dateBefore, id);
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 3, null, null, null, null);
    assertEquals("7", dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("146", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("75", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2012-12-15", id);
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 15, null, null, null, null);
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2012-12-5", id);
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 3, null, null, null, null);
    assertEquals("7", dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("146", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("98", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingForAMCOnlyWhenNumberOfMonthsIs1() throws SQLException, IOException {
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2012-9-1", "2012-9-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2012-11-1", "2012-11-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2012-10-1", "2013-10-31", 1, "M");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 40, 37, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2012-9-11", id);
    id = submitRnRThroughApi("V10", "HIV", "P10", 5, 3, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithNoPeriodTrackingWhenNumberOfMonthsIs1() throws SQLException, IOException {
    dbWrapper.insertProcessingPeriod("p", "period", "2012-8-1", "2012-8-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2012-9-1", "2012-9-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2012-11-1", "2012-11-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2012-10-1", "2013-10-31", 1, "M");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 40, 37, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2012-8-11", id);
    id = submitRnRThroughApi("V10", "HIV", "P10", 5, 3, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithNoPeriodTrackingWhenNumberOfMonthsIs2() throws SQLException, IOException {
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 2, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 40, 37, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-2-11", id);
    id = submitRnRThroughApi("V10", "HIV", "P10", 5, 3, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "periodNormalizedConsumption", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingWhenNumberOfMonthsIs2() throws SQLException, IOException, ParseException {
    String createdDate = "2013-4-11";
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 2, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 40, 37, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"),
      dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges(createdDate, id);
    id = submitRnRThroughApi("V10", "HIV", "P10", 5, 3, null, null, null, null);

    assertEquals(calculateDaysDifference(createdDate),
      dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingWhenNumberOfMonthsIs3() throws SQLException, IOException, ParseException {
    dbWrapper.deleteCurrentPeriod();
    String createdDate = "2013-6-11";
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 3, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 40, 37, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges(createdDate, id);
    id = submitRnRThroughApi("V10", "HIV", "P10", 5, 3, null, null, null, null);

    assertEquals(calculateDaysDifference(createdDate), dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithNoPeriodTrackingWhenNumberOfMonthsIs3() throws SQLException, IOException {
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 3, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 3, "M");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 40, 37, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"), dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("3", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
    dbWrapper.updateCreatedDateInRequisitionStatusChanges("2013-4-11", id);
    id = submitRnRThroughApi("V10", "HIV", "P10", 5, 3, null, null, null, null);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingDays", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "normalizedConsumption", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "amc", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testValuesSavedInDatabaseAreSameAsReportedInAPI() throws Exception {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", 10, 5, null, 5, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsForRequisition1WhenNumberOfMonthsIs1() throws SQLException, IOException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsForRequisition2WhenNumberOfMonthsIs1() throws Exception {
    Long id;
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    convertToOrderAndUpdatePOD("commTrack", "HIV", 10);
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 4, null, null, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("16", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "previousStockInHand", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "previousStockInHand", "P11"));
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P11"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsForRequisition1WhenNumberOfMonthsIs2() throws SQLException, IOException {
    Long id;
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");

    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 2, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsForRequisition2WhenNumberOfMonthsIs2() throws SQLException, IOException {
    Long id;
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 2, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");

    submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    convertToOrderAndUpdatePOD("commTrack", "HIV", 10);

    id = submitRnRThroughApi("V10", "HIV", "P10", null, 4, null, null, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("16", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsForRequisition1WhenNumberOfMonthsIs3() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 3, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 3, "M");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsForRequisition2WhenNumberOfMonthsIs3() throws IOException, SQLException {
    Long id;
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 3, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 3, "M");
    submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    convertToOrderAndUpdatePOD("commTrack", "HIV", 10);
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 4, null, null, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("16", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsForWhenNumberOfMonthsIs3AndPODCreatedDateIsChanged() throws IOException, SQLException {
    Long id;
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 3, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 3, "M");
    submitRnRThroughApi("V10", "HIV", "P10", null, 10, null, null, null, null);
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 5, null, null, null, null);
    convertToOrderAndUpdatePOD("commTrack", "HIV", 5);
    dbWrapper.updateFieldValue("pod", "createdDate", "2010-02-11", "orderId", id.toString());
    id = submitRnRThroughApi("V10", "HIV", "P10", null, 4, null, null, null, null);
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("1", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldsWhenMultipleProducts() throws IOException, SQLException {
    HttpClient client = new HttpClient();
    client.createContext();
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");

    Report reportFromJson = JsonUtility.readObjectFromFile("ReportJsonMultipleProducts.txt", Report.class);
    reportFromJson.setAgentCode("V10");
    reportFromJson.setProgramCode("HIV");
    reportFromJson.getProducts().get(0).setProductCode("P10");
    reportFromJson.getProducts().get(1).setProductCode("P11");
    reportFromJson.getProducts().get(0).setStockInHand(5);
    reportFromJson.getProducts().get(1).setStockInHand(2);

    ResponseEntity responseEntity =
      client.SendJSON(
        getJsonStringFor(reportFromJson),
        "http://localhost:9091/rest-api/requisitions.json",
        POST,
        "commTrack",
        "Admin123");

    assertEquals(201, responseEntity.getStatus());
    assertTrue(responseEntity.getResponse().contains("{\"requisitionId\":"));

    Long id = (long) dbWrapper.getMaxRnrID();
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("5", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P11"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P11"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P11"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P11"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P11"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P11"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldWhenStockInHandIsCalculated() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "quantityDispensed");
    Long id = submitRnRThroughApi("V10", "HIV", "P10", null, 20, 2, 10, null, null);
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("8", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("2", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationForAllFieldWhenBeginningBalanceIsHidden() throws IOException, SQLException {
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "true", "stockInHand");
    dbWrapper.updateConfigureTemplate("HIV", "source", "C", "true", "quantityDispensed");
    dbWrapper.updateConfigureTemplate("HIV", "source", "U", "false", "beginningBalance");
    dbWrapper.deleteRnrData();
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period1");
    dbWrapper.deleteRowFromTable("processing_periods", "name", "Period2");
    dbWrapper.insertRequisitions(1, "HIV", false, "2013-01-16", "2013-01-29", "V10", false);

    Long id = submitRnRThroughApi("V10", "HIV", "P10", null, 4, null, 10, null, null);
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "beginningBalance", "P10"));
    assertEquals("4", dbWrapper.getRequisitionLineItemFieldValue(id, "stockInHand", "P10"));
    assertEquals("10", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityReceived", "P10"));
    assertEquals("16", dbWrapper.getRequisitionLineItemFieldValue(id, "quantityDispensed", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "newPatientCount", "P10"));
    assertEquals("0", dbWrapper.getRequisitionLineItemFieldValue(id, "stockOutDays", "P10"));
  }

  public Long calculateDaysDifference(String createdDateInString) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date formattedCreatedDate = formatter.parse(createdDateInString);
    long timeDifference = new Date().getTime() - formattedCreatedDate.getTime();
    return (timeDifference / (24 * 3600 * 1000));
  }
}
