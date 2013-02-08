package org.openlmis.service;


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
    public void setUp() throws Exception
    {
        DBWrapper dbWrapper = new DBWrapper();
        dbWrapper.deleteData();
    }

    @Test
    public void testInitiateRnR() {
        try {

            DBWrapper dbWrapper = new DBWrapper();
            dbWrapper.insertFacilities("F10", "F11");
            dbWrapper.insertUser("200", "User123", "TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie", "F10", "manjyots@thoughtworks.com");
            dbWrapper.insertSupervisoryNode("F10", "N1", "null");
            dbWrapper.insertSupervisoryNodeSecond("F11", "N2", "N1");
            dbWrapper.insertProducts("P10", "P11");
            dbWrapper.insertProgramProducts("P10", "P11", "HIV");
            dbWrapper.insertFacilityApprovedProducts("P10", "P11", "HIV", "Lvl3 Hospital");


            dbWrapper.insertRequisitionGroups("RG1","RG2","N1","N2");
            dbWrapper.insertRequisitionGroupMembers("F11","F10");

            dbWrapper.configureTemplate();
            dbWrapper.insertRoles();
            dbWrapper.insertRoleRights();


            dbWrapper.insertRoleAssignment("200", "store in-charge");
            dbWrapper.insertSchedules();
            dbWrapper.insertProcessingPeriods();
            dbWrapper.insertRequisitionGroupProgramSchedule();


            String facilityId=dbWrapper.getFacilityID("F10");
            String periodId=dbWrapper.getPeriodID("Period2");

            String BASE_URL="http://localhost:9091";

            String INITIATE_RNR_JSON="{}";

            ServiceUtils serviceUtils = new ServiceUtils();

            serviceUtils.postNONJSON("j_username=User123&j_password=Admin123", BASE_URL+"/j_spring_security_check");

           serviceUtils.postJSON(INITIATE_RNR_JSON, BASE_URL + "/requisitions.json?facilityId="+facilityId+"&periodId="+periodId+"&programId=1");

            String requisitionId=dbWrapper.getRequisitionId();

            String INITIATE_RNR_ENDPOINT="/requisitions/"+requisitionId+"/submit.json";

            String SUBMIT_RNR_JSON="{\n" +
                    "    \"id\": "+requisitionId+",\n" +
                    "    \"lineItems\": [\n" +
                    "        {\n" +
                    "            \"id\": 1,\n" +
                    "            \"rnrId\": 2,\n" +
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
                    "            \"totalLossesAndAdjustments\": 0,\n" +
                    "            \"previousStockInHandAvailable\": false,\n" +
                    "            \"price\": \"12.50\",\n" +
                    "            \"previousNormalizedConsumptions\": [],\n" +
                    "            \"lossesAndAdjustments\": [],\n" +
                    "            \"beginningBalance\": 1,\n" +
                    "            \"quantityReceived\": 1,\n" +
                    "            \"quantityDispensed\": 1,\n" +
                    "            \"stockInHand\": 1,\n" +
                    "            \"stockOutDays\": 1,\n" +
                    "            \"newPatientCount\": 1,\n" +
                    "            \"normalizedConsumption\": 10,\n" +
                    "            \"maxStockQuantity\": 30,\n" +
                    "            \"calculatedOrderQuantity\": 29,\n" +
                    "            \"packsToShip\": 1,\n" +
                    "            \"cost\": \"12.50\",\n" +
                    "            \"amc\": 10,\n" +
                    "            \"quantityRequested\": \"1\",\n" +
                    "            \"reasonForRequestedQuantity\": \"1\",\n" +
                    "            \"remarks\": \"1\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": 2,\n" +
                    "            \"rnrId\": 2,\n" +
                    "            \"product\": \"antibiotic Capsule 300/200/600 mg\",\n" +
                    "            \"productCode\": \"P11\",\n" +
                    "            \"roundToZero\": false,\n" +
                    "            \"packRoundingThreshold\": 1,\n" +
                    "            \"packSize\": 10,\n" +
                    "            \"dosesPerMonth\": 30,\n" +
                    "            \"dosesPerDispensingUnit\": 10,\n" +
                    "            \"dispensingUnit\": \"Strip\",\n" +
                    "            \"maxMonthsOfStock\": 3,\n" +
                    "            \"fullSupply\": true,\n" +
                    "            \"totalLossesAndAdjustments\": 0,\n" +
                    "            \"previousStockInHandAvailable\": false,\n" +
                    "            \"price\": \"0.00\",\n" +
                    "            \"previousNormalizedConsumptions\": [],\n" +
                    "            \"lossesAndAdjustments\": [],\n" +
                    "            \"beginningBalance\": 1,\n" +
                    "            \"quantityReceived\": 1,\n" +
                    "            \"quantityDispensed\": 1,\n" +
                    "            \"stockInHand\": 1,\n" +
                    "            \"stockOutDays\": 1,\n" +
                    "            \"newPatientCount\": 1,\n" +
                    "            \"normalizedConsumption\": 10,\n" +
                    "            \"maxStockQuantity\": 30,\n" +
                    "            \"calculatedOrderQuantity\": 29,\n" +
                    "            \"packsToShip\": 1,\n" +
                    "            \"cost\": \"0.00\",\n" +
                    "            \"amc\": 10,\n" +
                    "            \"quantityRequested\": \"1\",\n" +
                    "            \"reasonForRequestedQuantity\": \"1\",\n" +
                    "            \"remarks\": \"1\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"nonFullSupplyLineItems\": []\n" +
                    "}";

            serviceUtils.putJSON(SUBMIT_RNR_JSON, BASE_URL + INITIATE_RNR_ENDPOINT);


            serviceUtils.getJSON(BASE_URL + "//j_spring_security_logout");


            serviceUtils.getJSON(BASE_URL+"//j_spring_security_logout");
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






