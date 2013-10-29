package org.openlmis.functional;

import org.openlmis.UiUtils.HttpClient;
import org.openlmis.UiUtils.ResponseEntity;
import org.openlmis.pod.domain.POD;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: shilpi
 * Date: 10/29/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequisitionStatusFeed extends JsonUtility{

    public static final String FULL_JSON_POD_TXT_FILE_NAME = "ReportJsonPOD.txt";
    public static final String URL = "http://localhost:9091/feeds/requisition-status/";

    @BeforeMethod(groups = {"webservice"})
    public void setUp() throws Exception {
        super.setup();
        super.setupTestData(false);
        super.setupDataRequisitionApprover();
    }

    @AfterMethod(groups = {"webservice"})
    public void tearDown() throws IOException, SQLException {
        dbWrapper.deleteData();
        dbWrapper.closeConnection();
    }

    @Test(groups = {"webservice"})
    public void testRequisitionStatusUsingCommTrackUserForExportOrderFlagFalse() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkRequisitionStatusOnFeed("INITIATED", feedJSONList.get(0), id);
        checkRequisitionStatusOnFeed("SUBMITTED", feedJSONList.get(1), id);
        checkRequisitionStatusOnFeed("AUTHORIZED", feedJSONList.get(2), id);

        dbWrapper.setExportOrdersFlagInSupplyLinesTable(false,"F10");
        approveRequisition(id, 65);
        responseEntity = client.SendJSON("", URL+"1", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkRequisitionStatusOnFeed("APPROVED", feedJSONList.get(3), id);
        checkRequisitionStatusOnFeed("RELEASED", feedJSONList.get(4), id);
        responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkOrderStatusOnFeed("READY_TO_PACK", feedJSONList.get(0), id);

        dbWrapper.assignRight("store in-charge", "MANAGE_POD");
        dbWrapper.setupUserForFulfillmentRole("commTrack","store in-charge","F10");

        POD PODFromJson = JsonUtility.readObjectFromFile(FULL_JSON_POD_TXT_FILE_NAME, POD.class);
        PODFromJson.getPodLineItems().get(0).setQuantityReceived(65);
        PODFromJson.getPodLineItems().get(0).setProductCode("P10");

        responseEntity = client.SendJSON(getJsonStringFor(PODFromJson),
                "http://localhost:9091/rest-api/order/" + id +"/pod.json",
                "POST",
                "commTrack",
                "Admin123");

        responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkOrderStatusOnFeed("RECEIVED", feedJSONList.get(1), id);
    }

    @Test(groups = {"webservice"})
    public void testRequisitionStatusUsingCommTrackUserForExportOrderFlagTrue() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkRequisitionStatusOnFeed("INITIATED", feedJSONList.get(0), id);
        checkRequisitionStatusOnFeed("SUBMITTED", feedJSONList.get(1), id);
        checkRequisitionStatusOnFeed("AUTHORIZED", feedJSONList.get(2), id);

        dbWrapper.setExportOrdersFlagInSupplyLinesTable(true,"F10");
        approveRequisition(id, 65);
        responseEntity = client.SendJSON("", URL+"1", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkRequisitionStatusOnFeed("APPROVED", feedJSONList.get(3), id);
        checkRequisitionStatusOnFeed("RELEASED", feedJSONList.get(4), id);
        responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkOrderStatusOnFeed("IN_ROUTE", feedJSONList.get(0), id);
        testWebDriver.sleep(3000);
        responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkOrderStatusOnFeed("TRANSFER_FAILED", feedJSONList.get(1), id);

    }

    @Test(groups = {"webservice"})
    public void testRequisitionStatusUsingCommTrackUserForExportOrderFlagTrueAndFtpDetailsValid() throws Exception {
        HttpClient client = new HttpClient();
        client.createContext();
        String response = submitReport();
        Long id = getRequisitionIdFromResponse(response);
        ResponseEntity responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        List<String> feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkRequisitionStatusOnFeed("INITIATED", feedJSONList.get(0), id);
        checkRequisitionStatusOnFeed("SUBMITTED", feedJSONList.get(1), id);
        checkRequisitionStatusOnFeed("AUTHORIZED", feedJSONList.get(2), id);

        dbWrapper.setExportOrdersFlagInSupplyLinesTable(true,"F10");
        dbWrapper.enterValidDetailsInFacilityFtpDetailsTable("F10");
        approveRequisition(id, 65);
        responseEntity = client.SendJSON("", URL+"1", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkRequisitionStatusOnFeed("APPROVED", feedJSONList.get(3), id);
        checkRequisitionStatusOnFeed("RELEASED", feedJSONList.get(4), id);
        responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkOrderStatusOnFeed("IN_ROUTE", feedJSONList.get(0), id);
        testWebDriver.sleep(3000);
        responseEntity = client.SendJSON("", URL+"recent", "GET", "", "");
        assertEquals(200, responseEntity.getStatus());
        feedJSONList = XmlUtils.getNodeValues(responseEntity.getResponse(), "content");
        checkOrderStatusOnFeed("RELEASED", feedJSONList.get(1), id);

    }


    private void checkRequisitionStatusOnFeed(String requisitionStatus, String feedSting, Long id) throws IOException, SAXException, ParserConfigurationException {
        assertTrue("feed json list : " + feedSting, feedSting.contains("\"requisitionId\":"+id));
        assertTrue("feed json list : " + feedSting, feedSting.contains("\"requisitionStatus\":\""+requisitionStatus+"\""));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"emergency\":false"));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"startDate\":1358274600000"));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"endDate\":1359570599000"));
        assertFalse("Response entity : " + feedSting, feedSting.contains("\"orderStatus\":"));
        assertFalse("Response entity : " + feedSting, feedSting.contains("\"orderID\""));
    }

    private void checkOrderStatusOnFeed(String orderStatus, String feedSting, Long id){
        assertTrue("feed json list : " + feedSting, feedSting.contains("\"requisitionId\":"+id));
        assertTrue("feed json list : " + feedSting, feedSting.contains("\"requisitionStatus\":\"RELEASED\""));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"emergency\":false"));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"startDate\":1358274600000"));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"endDate\":1359570599000"));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"orderStatus\":\""+orderStatus+"\""));
        assertTrue("Response entity : " + feedSting, feedSting.contains("\"orderId\":"+id));
    }

}
