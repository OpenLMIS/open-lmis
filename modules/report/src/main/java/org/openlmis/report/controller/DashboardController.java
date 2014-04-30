package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.report.service.lookup.DashboardLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * User: Issa
 * Date: 2/18/14
 * Time: 5:18 PM
 */
@Controller
@NoArgsConstructor
@RequestMapping(value = "/dashboard")
public class DashboardController extends BaseController {

    public static final String ITEM_FILL_RATE = "itemFillRate";
    public static final String ORDER_FILL_RATE = "orderFillRate";
    public static final String SHIPMENT_LEAD_TIME = "leadTime";
    public static final String STOCKING_EFFICIENCY_STATICS = "stocking";
    public static final String STOCKING_EFFICIENCY_DETAIL = "stocking";
    public static final String STOCKED_OUT_FACILITIES = "stockOut";
    public static final String ALERTS = "alerts";
    public static final String NOTIFICATIONS = "notifications";
    public static final String NOTIFICATIONS_DETAIL = "detail";


    @Autowired
    DashboardLookupService lookupService;

    @RequestMapping(value = "/itemFillRate", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getItemFillRate(@RequestParam("periodId") Long periodId,
                                                             @RequestParam("facilityId") Long facilityId,
                                                             @RequestParam("programId") Long programId,
                                                             @RequestParam("productListId")List<Long> productListId){
      return OpenLmisResponse.response(ITEM_FILL_RATE, this.lookupService.getItemFillRate(periodId, facilityId, programId, productListId));
    }

    @RequestMapping(value = "/orderFillRate", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getOrderFillRate(@RequestParam("periodId") Long periodId,
                                                              @RequestParam("facilityId") Long facilityId,
                                                              @RequestParam("programId") Long programId){
        return OpenLmisResponse.response(ORDER_FILL_RATE, this.lookupService.getOrderFillRate(periodId, facilityId, programId));
    }

    @RequestMapping(value = "/shipmentLeadTime", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getShipmentLeadTime(@RequestParam("periodId") Long periodId,
                                                              @RequestParam("programId") Long programId,
                                                              @RequestParam("rgroupId") List<Long> rgroupId){
        return OpenLmisResponse.response(SHIPMENT_LEAD_TIME, this.lookupService.getShipmentLeadTime(periodId,programId,rgroupId));
    }


    @RequestMapping(value = "/stockEfficiency", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getStockEfficiencyData(@RequestParam("periodId") Long periodId,
                                                                    @RequestParam("programId") Long programId,
                                                                    @RequestParam("rgroupId") List<Long> rgroupId,
                                                                    @RequestParam("productListId")List<Long> productListId){
        return OpenLmisResponse.response(STOCKING_EFFICIENCY_STATICS, this.lookupService.getStockEfficiencyData(periodId, programId,rgroupId, productListId));
    }
    @RequestMapping(value = "/stockEfficiencyDetail", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getStockEfficiencyDetailData(@RequestParam("periodId") Long periodId,
                                                                    @RequestParam("programId") Long programId,
                                                                    @RequestParam("rgroupId") List<Long> rgroupId,
                                                                    @RequestParam("productListId")List<Long> productListId){
        return OpenLmisResponse.response(STOCKING_EFFICIENCY_DETAIL, this.lookupService.getStockEfficiencyDetailData(periodId, programId,rgroupId, productListId));
    }

    @RequestMapping(value = "/stockedOutFacilities", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getStockedOutFacilities(     @RequestParam("periodId") Long periodId,
                                                                          @RequestParam("programId") Long programId,
                                                                          @RequestParam("productId") Long productId,
                                                                          @RequestParam("rgroupId") List<Long> requisitionGroupId){
        return OpenLmisResponse.response(STOCKED_OUT_FACILITIES, this.lookupService.getStockOutFacilities(periodId, programId, productId,requisitionGroupId));
    }

    @RequestMapping(value = "/requisitionGroup/{rgroupId}/program/{programId}/period/{periodId}/product/{productId}/stockedOutFacilities", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getStockedOutFacilitiesByRequisitionGroupFilter(@PathVariable("periodId") Long periodId,
                                                                          @PathVariable("programId") Long programId,
                                                                          @PathVariable("productId") Long productId,
                                                                          @PathVariable("rgroupId") Long requisitionGroupId){
        return OpenLmisResponse.response(STOCKED_OUT_FACILITIES, this.lookupService.getStockOutFacilitiesByRequisitionGroup(periodId, programId, productId,requisitionGroupId));
    }
    @RequestMapping(value = "/alerts", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getAlerts(@RequestParam("supervisoryNodeId") Long supervisoryNodeId, @RequestParam("programId") Long programId,
                                                       HttpServletRequest request){
        return OpenLmisResponse.response(ALERTS, this.lookupService.getAlerts(loggedInUserId(request), supervisoryNodeId, programId ));
    }

    @RequestMapping(value = "/notification/alerts", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getNotificationTypeAlerts(HttpServletRequest request){
        return OpenLmisResponse.response(NOTIFICATIONS, this.lookupService.getNotificationAlerts());
    }

    @RequestMapping(value = "/notifications/{alertId}/{detailTable}", method = GET, headers = ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> getNotificationsByCategory(@PathVariable("alertId") Long id, @PathVariable("detailTable") String detailTable){
        return OpenLmisResponse.response(NOTIFICATIONS_DETAIL, this.lookupService.getNotificationsByCategory(detailTable, id));
    }

}
