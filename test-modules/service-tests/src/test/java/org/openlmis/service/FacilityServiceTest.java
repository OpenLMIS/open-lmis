package org.openlmis.service;


import org.openlmis.UiUtils.DBWrapper;
import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.servicelayerutils.ServiceUtils;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;


@TransactionConfiguration(defaultRollback = true)
@Transactional

public class FacilityServiceTest extends TestCaseHelper {

  DBWrapper dbWrapper;

  @BeforeClass
  public void setUp() throws Exception {
    dbWrapper = new DBWrapper();
  }

  @Test
  public void testFacility() {
    try {

      String BASE_URL = "http://localhost:9091";
      String CREATE_FACILITY_ENDPOINT = "/facilities.json";
      String DELETE_FACILITY_ENDPOINT = "/facility/update/delete.json";
      String RESTORE_FACILITY_ENDPOINT = "/facility/update/restore.json";


      String CREATE_FACILITY_JSON = "{\"supportedPrograms\":[{\"program\":{\"id\":1},\"active\":true,\"editedStartDate\":\"2013-02-07T18:30:00.000Z\"," +
          "\"startDate\":\"2013-02-07T18:30:00.000Z\"}],\"dataReportable\":\"true\",\"code\":\"facilitycode\",\"gln\":\"fac\"," +
          "\"name\":\"facilityname\",\"facilityType\":{\"code\":\"lvl3_hospital\"},\"sdp\":\"true\",\"active\":\"true\"," +
          "\"goLiveDate\":\"2013-02-07T18:30:00.000Z\",\"geographicZone\":{\"id\":1}}";


      dbWrapper.deleteData();

      ServiceUtils serviceUtils = new ServiceUtils();

      String newFacilityCreatedPreLogin = serviceUtils.postJSON(CREATE_FACILITY_JSON, BASE_URL + CREATE_FACILITY_ENDPOINT);

      String facilityIDPreLogin = serviceUtils.getFacilityFieldJSON(newFacilityCreatedPreLogin, "id");
      /*
     Checking for unauthorized creation of facility : Pre login
      */
      assertEquals(facilityIDPreLogin, dbWrapper.getFacilityIDDB());

      /*
     Hitting Login endpoint with required credentials
      */
      serviceUtils.postNONJSON("j_username=Admin123&j_password=Admin123", BASE_URL + "/j_spring_security_check");


      String newFacilityCreatedPostLogin = serviceUtils.postJSON(CREATE_FACILITY_JSON, BASE_URL + CREATE_FACILITY_ENDPOINT);
      serviceUtils.getJSON("http://localhost:9091/facilities.json");


      String facilityID = serviceUtils.getFacilityFieldJSON(newFacilityCreatedPostLogin, "id");

      String DELETE_FACILITY_JSON = "{\"id\":" + facilityID + "}";


      String RESTORE_FACILITY_JSON = "{\"id\":" + facilityID + ",\"active\":true}";

      assertEquals(dbWrapper.getFacilityFieldBYID("active", facilityID), "t");
      assertEquals(dbWrapper.getFacilityFieldBYID("datareportable", facilityID), "t");
      /*
     Checking for authorized creation of facility : post login
      */
      assertEquals(facilityID, dbWrapper.getFacilityIDDB());

      serviceUtils.putJSON(DELETE_FACILITY_JSON, BASE_URL + DELETE_FACILITY_ENDPOINT);


      assertEquals(dbWrapper.getFacilityFieldBYID("active", facilityID), "f");
      assertEquals(dbWrapper.getFacilityFieldBYID("datareportable", facilityID), "f");


      serviceUtils.putJSON(RESTORE_FACILITY_JSON, BASE_URL + RESTORE_FACILITY_ENDPOINT);

      assertEquals(dbWrapper.getFacilityFieldBYID("active", facilityID), "t");
      assertEquals(dbWrapper.getFacilityFieldBYID("datareportable", facilityID), "t");

      serviceUtils.getJSON(BASE_URL + "//j_spring_security_logout");


      serviceUtils.getJSON(BASE_URL + "//j_spring_security_logout");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        dbWrapper.deleteFacilities();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  @AfterClass
  public void tearDown() throws Exception {
    dbWrapper.closeConnection();
  }

}







