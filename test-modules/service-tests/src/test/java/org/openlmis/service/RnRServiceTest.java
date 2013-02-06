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
            dbWrapper.insertProducts();
            dbWrapper.insertProgramProducts();
            dbWrapper.insertFacilityApprovedProducts();
            dbWrapper.insertFacilities();
            dbWrapper.configureTemplate();
            dbWrapper.insertRoles();
            dbWrapper.insertRoleRights();
            dbWrapper.insertUser("200", "User123", "Ag/myf1Whs0fxr1FFfK8cs3q/VJ1qMs3yuMLDTeEcZEGzstj/waaUsQNQTIKk1U5JRzrDbPLCzCO1/vB5YGaEQ==", "F12", "manjyots@thoughtworks.com");
            dbWrapper.insertSupervisoryNode("F10");
            dbWrapper.insertSupervisoryNodeSecond("F11");
            dbWrapper.insertRoleAssignment("200", "store in-charge");
            dbWrapper.insertSchedules();
            dbWrapper.insertProcessingPeriods();
            dbWrapper.insertRequisitionGroups();
            dbWrapper.insertRequisitionGroupMembers("F10","F11");
            dbWrapper.insertRequisitionGroupProgramSchedule();

            String BASE_URL="http://localhost:9091";
            String INITIATE_RNR_ENDPOINT="/requisitions/1/submit.json";

            String INITIATE_RNR_JSON="{}";

            String SUBMIT_RNR_JSON="{\"id\":1," +
                    "\"facility\":{\"id\":1,\"code\":\"F10\",\"name\":\"Village Dispensary\"," +
                    "\"geographicZone\":{\"id\":1,\"name\":\"Arusha\"," +
                    "\"level\":{\"id\":null,\"name\":\"state\"}," +
                    "\"parent\":{\"id\":null,\"name\":\"Arusha\"," +
                    "\"level\":{\"id\":null,\"name\":\"state\"}," +
                    "\"parent\":null}}," +
                    "\"facilityType\":{\"id\":1,\"code\":\"warehouse\",\"name\":\"Warehouse\",\"description\":\"Central Supply Depot\"," +
                    "\"nominalMaxMonth\":3,\"nominalEop\":0.5,\"displayOrder\":11,\"active\":true}," +
                    "\"operatedBy\":{\"id\":2,\"code\":\"NGO\",\"text\":\"NGO\",\"displayOrder\":2}," +
                    "\"supportedPrograms\":[]}," +
                    "\"program\":{\"id\":1,\"code\":null,\"name\":\"HIV\",\"description\":null,\"active\":null}," +
                    "\"period\":{\"id\":2,\"scheduleId\":null,\"name\":null,\"description\":null,\"modifiedBy\":null," +
                    "\"startDate\":1351708200000,\"endDate\":1354300200000,\"numberOfMonths\":3,\"modifiedDate\":null}," +
                    "\"status\":\"INITIATED\",\"fullSupplyItemsSubmittedCost\":375,\"nonFullSupplyItemsSubmittedCost\":0," +
                    "\"lineItems\":[{\"id\":1,\"rnrId\":1,\"product\":\"antibiotic Capsule 300/200/600 mg\"," +
                    "\"productCode\":\"P10\",\"roundToZero\":false,\"packRoundingThreshold\":1,\"packSize\":10,\"dosesPerMonth\":30," +
                    "\"dosesPerDispensingUnit\":10,\"dispensingUnit\":\"Strip\",\"maxMonthsOfStock\":3,\"fullSupply\":true,\"totalLossesAndAdjustments\":0," +
                    "\"previousStockInHandAvailable\":false,\"price\":\"12.50\",\"cost\":375,\"lossesAndAdjustments\":[]," +
                    "\"beginningBalance\":\"10\",\"stockInHand\":10,\"normalizedConsumption\":101,\"amc\":101,\"maxStockQuantity\":303," +
                    "\"calculatedOrderQuantity\":293,\"packsToShip\":30,\"quantityReceived\":\"10\",\"quantityDispensed\":\"10\"," +
                    "\"newPatientCount\":\"10\",\"stockOutDays\":\"10\"},{\"id\":2,\"rnrId\":1,\"product\":\"antibiotic Capsule 300/200/600 mg\"," +
                    "\"productCode\":\"P12\",\"roundToZero\":true,\"packRoundingThreshold\":1,\"packSize\":10,\"dosesPerMonth\":30," +
                    "\"dosesPerDispensingUnit\":10,\"dispensingUnit\":\"Strip\",\"maxMonthsOfStock\":3,\"fullSupply\":true,\"totalLossesAndAdjustments\":0," +
                    "\"previousStockInHandAvailable\":false,\"price\":\"0.00\",\"cost\":0,\"lossesAndAdjustments\":[]," +
                    "\"beginningBalance\":\"10\",\"stockInHand\":10,\"normalizedConsumption\":101,\"amc\":101,\"maxStockQuantity\":303," +
                    "\"calculatedOrderQuantity\":293,\"packsToShip\":30,\"quantityReceived\":\"10\",\"quantityDispensed\":\"10\",\"newPatientCount\":\"10\"," +
                    "\"stockOutDays\":\"10\"}],\"nonFullSupplyLineItems\":[]}";


//            DBWrapper dbWrapper = new DBWrapper();
//            dbWrapper.deleteFacilities();

            ServiceUtils serviceUtils = new ServiceUtils();

            serviceUtils.postNONJSON("j_username=User123&j_password=User123", BASE_URL+"/j_spring_security_check");

            serviceUtils.postJSON(INITIATE_RNR_JSON, BASE_URL + "/requisitions.json?facilityId=2&periodId=2&programId=1");

            serviceUtils.putJSON(SUBMIT_RNR_JSON, BASE_URL + INITIATE_RNR_ENDPOINT);

            serviceUtils.getJSON(BASE_URL + "//j_spring_security_logout");


            serviceUtils.getJSON(BASE_URL+"//j_spring_security_logout");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
//                DBWrapper dbWrapper = new DBWrapper();
//                dbWrapper.deleteData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}






