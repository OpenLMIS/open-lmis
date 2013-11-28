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
import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.openlmis.UiUtils.HttpClient.POST;

import java.io.IOException;
import java.sql.SQLException;

public class CalculationInRnR extends JsonUtility{

  public static final String MINIMUM_JSON_TXT_FILE_NAME = "ReportMinimumJson.txt";
  public static final String FULL_JSON_TXT_FILE_NAME = "ReportFullJson.txt";
  public static final String PRODUCT_JSON_TXT_FILE_NAME = "ReportWithProductJson.txt";

  @BeforeMethod(groups = {"webservice"})
  public void setUp() throws Exception {
    super.setup();
    super.setupTestData(true);
    createVirtualFacilityThroughApi("V10", "F10");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-01-30", "2016-01-30", 1, "M");
    dbWrapper.insertRoleAssignmentForSupervisoryNodeForProgramId1("700", "store in-charge", "N1");
    dbWrapper.updateRestrictLogin("commTrack", true);
    dbWrapper.updateProductFullSupplyFlag(true, "P11");
  }

  @AfterMethod(groups = {"webservice"})
  public void tearDown() throws IOException, SQLException {
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }

  @Test(groups = {"webservice"})
  public void testCalculationWhenReportingDaysUndefined() throws Exception {
    Long id = submitRnRThroughApiForV10("HIV","P10",10,5);
    assertEquals(null, dbWrapper.getRequisitionLineItemFieldValue(id, "reportingdays", "P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWhenReportingDaysZero() throws Exception{
    submitRnRThroughApiForV10("HIV","P10",10,5);
    Long id = submitRnRThroughApiForV10("HIV","P10",null,3);
    assertEquals("0",dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("4",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationAndTracingWhenMultipleProducts() throws Exception{
    submitRnRThroughApiForV10("HIV", "P10", 10, 5);
    submitRnRThroughApiForV10("HIV","P11",5,3);

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

    Long id = Long.valueOf(dbWrapper.getMaxRnrID());
    assertEquals("0",dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("4",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    assertEquals("0",dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P11"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P11"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P11"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P11"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWhenSameProductInTwoPrograms() throws Exception{
    dbWrapper.insertProgramProduct("P10","ESS_MEDS","10","true");
    dbWrapper.insertProgramProduct("P11","ESS_MEDS","10","true");
    dbWrapper.insertFacilityApprovedProducts("P10", "ESS_MEDS", "lvl3_hospital");
    dbWrapper.insertRoleAssignmentForSupervisoryNode("700","store in-charge","N1","ESS_MEDS");
    dbWrapper.configureTemplateForCommTrack("ESS_MEDS");
    dbWrapper.insertRequisitionGroupProgramScheduleForProgram("RG1","ESS_MEDS","M");
    submitRnRThroughApiForV10("ESS_MEDS","P10",5,3);
    Long id = submitRnRThroughApiForV10("HIV","P10",10,5);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingWhenNumberOfMonthsIs1() throws Exception{
    Long id = submitRnRThroughApiForV10("HIV","P10",40,37);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("NOW()-interval '7 Days'",id);
    id = submitRnRThroughApiForV10("HIV","P10",null,3);
    assertEquals("7",dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("146",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("75",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2012-12-15'",id);
    id = submitRnRThroughApiForV10("HIV","P10",null,15);
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2012-12-5'",id);
    id = submitRnRThroughApiForV10("HIV","P10",null,3);
    assertEquals("7",dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("146",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("98",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingForAMCOnlyWhenNumberOfMonthsIs1() throws Exception{
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2012-9-1", "2012-9-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2012-11-1", "2012-11-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2012-10-1", "2013-10-31", 1, "M");
    Long id = submitRnRThroughApiForV10("HIV","P10",40,37);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2012-9-11'",id);
    id = submitRnRThroughApiForV10("HIV","P10",5,3);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithNoPeriodTrackingWhenNumberOfMonthsIs1() throws Exception{
    dbWrapper.insertProcessingPeriod("p", "period", "2012-8-1", "2012-8-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2012-9-1", "2012-9-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2012-11-1", "2012-11-30", 1, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2012-10-1", "2013-10-31", 1, "M");
    Long id = submitRnRThroughApiForV10("HIV","P10",40,37);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2012-8-11'",id);
    id = submitRnRThroughApiForV10("HIV","P10",5,3);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithNoPeriodTrackingWhenNumberOfMonthsIs2() throws Exception{
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 2, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");
    Long id = submitRnRThroughApiForV10("HIV","P10",40,37);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2013-2-11'",id);
    id = submitRnRThroughApiForV10("HIV","P10",5,3);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingWhenNumberOfMonthsIs2() throws Exception{
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 2, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 2, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");
    Long id = submitRnRThroughApiForV10("HIV","P10",40,37);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2013-4-11'",id);
    id = submitRnRThroughApiForV10("HIV","P10",5,3);
    assertEquals("231",dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("0",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithPeriodTrackingWhenNumberOfMonthsIs3() throws Exception{
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 3, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 2, "M");
    Long id = submitRnRThroughApiForV10("HIV","P10",40,37);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2013-6-11'",id);
    id = submitRnRThroughApiForV10("HIV","P10",5,3);
    assertEquals("170",dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("0",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

  @Test(groups = {"webservice"})
  public void testCalculationWithNoPeriodTrackingWhenNumberOfMonthsIs3() throws Exception{
    dbWrapper.deleteCurrentPeriod();
    dbWrapper.insertProcessingPeriod("p1", "1 period", "2013-2-1", "2013-3-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p2", "2 period", "2013-4-1", "2013-5-31", 3, "M");
    dbWrapper.insertProcessingPeriod("p3", "3 period", "2013-6-1", "2013-7-31", 3, "M");
    dbWrapper.insertProcessingPeriod("current", "current period", "2013-08-1", "2016-01-30", 3, "M");
    Long id = submitRnRThroughApiForV10("HIV","P10",40,37);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals(dbWrapper.getRequisitionLineItemFieldValue(id,"quantitydispensed","P10"),dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("3",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
    dbWrapper.updateRequisitionStatusChangesCreatedDate("'2013-4-11'",id);
    id = submitRnRThroughApiForV10("HIV","P10",5,3);
    assertEquals(null,dbWrapper.getRequisitionLineItemFieldValue(id,"reportingdays","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"normalizedconsumption","P10"));
    assertEquals("2",dbWrapper.getRequisitionLineItemFieldValue(id,"amc","P10"));
  }

}
