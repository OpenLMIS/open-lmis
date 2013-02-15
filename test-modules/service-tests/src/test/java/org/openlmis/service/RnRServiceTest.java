package org.openlmis.service;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.servicelayerutils.ServiceUtils;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@TransactionConfiguration(defaultRollback = true)
@Transactional

public class RnRServiceTest extends TestCaseHelper {

  @BeforeClass
  public void setUp() throws Exception {
    DBWrapper dbWrapper = new DBWrapper();
    dbWrapper.deleteData();
  }

  @Test
  public void testInitiateRnR() {
    try {

      DBWrapper dbWrapper = new DBWrapper();
      dbWrapper.insertFacilities("F10", "F11");
      dbWrapper.insertUser("200", "User123", "Agismyf1Whs0fxr1FFfK8cs3qisVJ1qMs3yuMLDTeEcZEGzstjiswaaUsQNQTIKk1U5JRzrDbPLCzCO1isvB5YGaEQieie", "F10", "manjyots@thoughtworks.com");
      dbWrapper.insertSupervisoryNode("F10", "N1", "null");
      dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "N1");
      dbWrapper.insertProducts("P10", "P11");
      dbWrapper.insertProgramProducts("P10", "P11", "HIV");
      dbWrapper.insertFacilityApprovedProducts("P10", "P11", "HIV", "Lvl3 Hospital");


      dbWrapper.insertRequisitionGroups("RG1", "RG2", "N1", "N2");
      dbWrapper.insertRequisitionGroupMembers("F11", "F10");

      dbWrapper.configureTemplate();
      dbWrapper.insertRoles();
      dbWrapper.insertRoleRights();


      dbWrapper.insertRoleAssignment("200", "store in-charge");
      dbWrapper.insertSchedules();
      dbWrapper.insertProcessingPeriods();
      dbWrapper.insertRequisitionGroupProgramSchedule();


      String facilityId = dbWrapper.getFacilityID("F10");
      String periodId = dbWrapper.getPeriodID("Period2");

      String BASE_URL = "http://localhost:9091";

      String INITIATE_RNR_JSON = "{}";

      ServiceUtils serviceUtils = new ServiceUtils();

      serviceUtils.postNONJSON("j_username=User123&j_password=User123", BASE_URL + "/j_spring_security_check");

      serviceUtils.postJSON(INITIATE_RNR_JSON, BASE_URL + "/requisitions.json?facilityId=" + facilityId + "&periodId=" + periodId + "&programId=1");

      String requisitionId = dbWrapper.getRequisitionId();


      String SUBMIT_RNR_ENDPOINT = "/requisitions/" + requisitionId + "/submit.json";

      String SAVE_RNR_ENDPOINT = "/requisitions/" + requisitionId + "/save.json";

      String AUTHORIZE_RNR_ENDPOINT = "/requisitions/" + requisitionId + "/authorize.json";

      String SUBMIT_RNR_JSON = "{\n" +
          "    \"id\": " + requisitionId + ",\n" +
          "    \"lineItems\": [\n" +
          "        {\n" +
          "            \"id\": 1,\n" +
          "            \"rnrId\": " + requisitionId + ",\n" +
          "            \"product\": \"antibiotic Capsule 300/200/600 mg\",\n" +
          "            \"productCode\": \"P10\",\n" +
          "            \"roundToZero\": false,\n" +
          "            \"packRoundingThreshold\": 1,\n" +
          "            \"packSize\": 10,\n" +
          "            \"dosesPerMonth\": 30,\n" +
          "            \"dosesPerDispensingUnit\": 10,\n" +
          "            \"dispensingUnit\": \"Strip\",\n" +
          "            \"maxMonthsOfStock\": 3,\n" +
          "            \"fullSupply\": true,\n" +
          "            \"beginningBalance\": 1,\n" +
          "            \"totalLossesAndAdjustments\": 1,\n" +
          "            \"previousStockInHandAvailable\": false,\n" +
          "            \"price\": \"12.50\",\n" +
          "            \"previousNormalizedConsumptions\": [],\n" +
          "            \"lossesAndAdjustments\": [\n" +
          "                {\n" +
          "                    \"type\": {\n" +
          "                        \"name\": \"TRANSFER_IN\",\n" +
          "                        \"description\": \"Transfer In\",\n" +
          "                        \"additive\": true,\n" +
          "                        \"displayOrder\": 2\n" +
          "                    },\n" +
          "                    \"quantity\": \"1\"\n" +
          "                }\n" +
          "            ],\n" +
          "            \"quantityReceived\": 1,\n" +
          "            \"quantityDispensed\": 1,\n" +
          "            \"stockInHand\": 2,\n" +
          "            \"stockOutDays\": 1,\n" +
          "            \"newPatientCount\": 1,\n" +
          "            \"normalizedConsumption\": 10,\n" +
          "            \"maxStockQuantity\": 30,\n" +
          "            \"calculatedOrderQuantity\": 28,\n" +
          "            \"packsToShip\": 1,\n" +
          "            \"cost\": \"12.50\",\n" +
          "            \"amc\": 10,\n" +
          "            \"quantityRequested\": \"1\",\n" +
          "            \"reasonForRequestedQuantity\": \"1\",\n" +
          "            \"remarks\": \"1\"\n" +
          "        }\n" +
          "    ],\n" +
          "    \"nonFullSupplyLineItems\": [\n" +
          "        {\n" +
          "            \"quantityRequested\": \"11\",\n" +
          "            \"reasonForRequestedQuantity\": \"test\",\n" +
          "            \"productCode\": \"P11\",\n" +
          "            \"product\": \"antibiotic Capsule 300/200/600 mg\",\n" +
          "            \"dosesPerDispensingUnit\": 10,\n" +
          "            \"packSize\": 10,\n" +
          "            \"roundToZero\": false,\n" +
          "            \"packRoundingThreshold\": 1,\n" +
          "            \"dispensingUnit\": \"Strip\",\n" +
          "            \"fullSupply\": false,\n" +
          "            \"maxMonthsOfStock\": 3,\n" +
          "            \"dosesPerMonth\": 30,\n" +
          "            \"price\": \"0.00\",\n" +
          "            \"quantityReceived\": 0,\n" +
          "            \"quantityDispensed\": 0,\n" +
          "            \"beginningBalance\": 0,\n" +
          "            \"stockInHand\": 0,\n" +
          "            \"totalLossesAndAdjustments\": 0,\n" +
          "            \"calculatedOrderQuantity\": 0,\n" +
          "            \"newPatientCount\": 0,\n" +
          "            \"stockOutDays\": 0,\n" +
          "            \"normalizedConsumption\": 0,\n" +
          "            \"amc\": 0,\n" +
          "            \"maxStockQuantity\": 0,\n" +
          "            \"rnrId\": " + requisitionId + ",\n" +
          "            \"previousNormalizedConsumptions\": [],\n" +
          "            \"lossesAndAdjustments\": [],\n" +
          "            \"packsToShip\": 2,\n" +
          "            \"cost\": \"0.00\"\n" +
          "        }\n" +
          "    ]\n" +
          "}";

      String rnrStatusSave = serviceUtils.putJSON(SUBMIT_RNR_JSON, BASE_URL + SAVE_RNR_ENDPOINT);
      SeleneseTestNgHelper.assertTrue("R&R is not saved successfully", rnrStatusSave.contains("R&R saved successfully!"));

      String rnrStatusSubmit = serviceUtils.putJSON(SUBMIT_RNR_JSON, BASE_URL + SUBMIT_RNR_ENDPOINT);
      SeleneseTestNgHelper.assertTrue("R&R is not submitted successfully", rnrStatusSubmit.contains("R&R submitted successfully!"));

      String rnrStatusAuthorize = serviceUtils.putJSON(SUBMIT_RNR_JSON, BASE_URL + AUTHORIZE_RNR_ENDPOINT);
      SeleneseTestNgHelper.assertTrue("R&R is not authorized successfully", rnrStatusAuthorize.contains("There is no supervisor assigned to review and approve this R&R, Please contact the Administrator"));



      serviceUtils.getJSON(BASE_URL + "//j_spring_security_logout");


      serviceUtils.getJSON(BASE_URL + "//j_spring_security_logout");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }


}






