package org.openlmis.service;


import com.thoughtworks.selenium.SeleneseTestNgHelper;
import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.servicelayerutils.ServiceUtils;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;


@TransactionConfiguration(defaultRollback = true)
@Transactional

public class FacilityServiceTest extends TestCaseHelper {

    @Test
    public void testFacility() {
        try {

            String BASE_URL="http://localhost:9091";
            DBWrapper dbWrapper = new DBWrapper();
            dbWrapper.deleteFacilities();

            ServiceUtils serviceUtils = new ServiceUtils();

            String newFacilityCreatedPreLogin = serviceUtils.postJSON("{\"dataReportable\":\"true\",\"code\":\"facilitycode\",\"gln\":\"fac\"," +
                    "\"name\":\"facilityname\",\"facilityType\":{\"code\":\"lvl3_hospital\"},\"sdp\":\"true\",\"active\":\"true\"," +
                    "\"goLiveDate\":\"2013-01-10T18:30:00.000Z\",\"supportedPrograms\":[{\"id\":1,\"code\":\"HIV\",\"name\":\"HIV\"," +
                    "\"description\":\"HIV\",\"active\":true}],\"geographicZone\":{\"id\":1}}", BASE_URL+"/admin/facility.json");

            String facilityIDPreLogin = serviceUtils.getFacilityFieldJSON(newFacilityCreatedPreLogin,"id");
            /*
            Checking for unauthorized creation of facility : Pre login
             */
            SeleneseTestNgHelper.assertEquals(facilityIDPreLogin, dbWrapper.getFacilityIDDB());

            /*
            Hitting Login endpoint with required credentials
             */
            serviceUtils.postNONJSON("j_username=Admin123&j_password=Admin123", BASE_URL+"/j_spring_security_check");


            String newFacilityCreatedPostLogin = serviceUtils.postJSON("{\"dataReportable\":\"true\",\"code\":\"facilitycode\",\"gln\":\"fac\"," +
                    "\"name\":\"facilityname\",\"facilityType\":{\"code\":\"lvl3_hospital\"},\"sdp\":\"true\",\"active\":\"true\"," +
                    "\"goLiveDate\":\"2013-01-10T18:30:00.000Z\",\"supportedPrograms\":[{\"id\":1,\"code\":\"HIV\",\"name\":\"HIV\"," +
                    "\"description\":\"HIV\",\"active\":true}],\"geographicZone\":{\"id\":1}}", BASE_URL+"/admin/facility.json");
            serviceUtils.getJSON("http://localhost:9091/admin/facilities.json");


            String facilityID = serviceUtils.getFacilityFieldJSON(newFacilityCreatedPostLogin,"id");
            SeleneseTestNgHelper.assertEquals(dbWrapper.getFacilityFieldBYID("active", facilityID),"t");
            SeleneseTestNgHelper.assertEquals(dbWrapper.getFacilityFieldBYID("datareportable", facilityID),"t");
            /*
            Checking for authorized creation of facility : post login
             */
            SeleneseTestNgHelper.assertEquals(facilityID, dbWrapper.getFacilityIDDB());

            serviceUtils.postJSON("{\"id\":" + facilityID + ",\"code\":\"facilitycode\",\"name\":\"facilityname\",\"gln\":\"fac\"," +
                    "\"geographicZone\":{\"id\":1,\"label\":null,\"value\":null},\"facilityType\":{\"id\":2,\"code\":\"lvl3_hospital\"," +
                    "\"name\":\"Lvl3 Hospital\",\"description\":\"State Hospital\",\"nominalMaxMonth\":3,\"nominalEop\":0.5,\"displayOrder\":1," +
                    "\"active\":true},\"sdp\":\"true\",\"active\":\"true\",\"goLiveDate\":1357842600000,\"dataReportable\":\"true\"," +
                    "\"supportedPrograms\":[{\"id\":1,\"code\":\"HIV\",\"name\":\"HIV\",\"description\":\"HIV\",\"active\":true}],\"modifiedBy\":" +
                    "\"Admin123\",\"modifiedDate\":1359279527557,\"suppliesOthers\":\"\",\"hasElectricity\":\"\",\"online\":\"\"," +
                    "\"hasElectronicScc\":\"\",\"hasElectronicDar\":\"\"}", BASE_URL+"/admin/facility/update/delete.json");


            SeleneseTestNgHelper.assertEquals(dbWrapper.getFacilityFieldBYID("active", facilityID),"f");
            SeleneseTestNgHelper.assertEquals(dbWrapper.getFacilityFieldBYID("datareportable", facilityID),"f");


            serviceUtils.postJSON("{\"id\":" + facilityID + ",\"code\":\"facilitycode\",\"name\":\"facilityname\",\"gln\":\"fac\"," +
                    "\"geographicZone\":{\"id\":1,\"label\":null,\"value\":null},\"facilityType\":{\"id\":2,\"code\":\"lvl3_hospital\"," +
                    "\"name\":\"Lvl3 Hospital\",\"description\":\"State Hospital\",\"nominalMaxMonth\":3,\"nominalEop\":0.5,\"displayOrder\":1," +
                    "\"active\":true},\"sdp\":\"true\",\"active\":\"true\",\"goLiveDate\":1357842600000,\"dataReportable\":\"false\"," +
                    "\"supportedPrograms\":[{\"id\":1,\"code\":\"HIV\",\"name\":\"HIV\",\"description\":\"HIV\",\"active\":true}]," +
                    "\"modifiedBy\":\"Admin123\",\"modifiedDate\":1359279527557,\"suppliesOthers\":\"\",\"hasElectricity\":\"\"," +
                    "\"online\":\"\",\"hasElectronicScc\":\"\",\"hasElectronicDar\":\"\"}",
                    BASE_URL+"/admin/facility/update/restore.json");

            SeleneseTestNgHelper.assertEquals(dbWrapper.getFacilityFieldBYID("active", facilityID),"t");
            SeleneseTestNgHelper.assertEquals(dbWrapper.getFacilityFieldBYID("datareportable", facilityID),"t");

            serviceUtils.getJSON(BASE_URL + "//j_spring_security_logout");


            serviceUtils.getJSON(BASE_URL+"//j_spring_security_logout");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                DBWrapper dbWrapper = new DBWrapper();
                dbWrapper.deleteFacilities();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}






