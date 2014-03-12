package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.report.service.lookup.DashboardLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Autowired
    DashboardLookupService lookupService;

    @RequestMapping(value = "/itemFillRate", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getItemFillRate(@RequestParam("geographicZoneId") Long geographicZoneId,
                                                             @RequestParam("periodId") Long periodId,
                                                             @RequestParam("facilityId") Long facilityId,
                                                             @RequestParam("programId") Long programId,
                                                             @RequestParam("productListId")List<Long> productListId){
      return OpenLmisResponse.response(ITEM_FILL_RATE, this.lookupService.getItemFillRate(geographicZoneId, periodId, facilityId, programId, productListId));
    }

    @RequestMapping(value = "/orderFillRate", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getOrderFillRate(@RequestParam("geographicZoneId") Long geographicZoneId,
                                                              @RequestParam("periodId") Long periodId,
                                                              @RequestParam("facilityId") Long facilityId,
                                                              @RequestParam("programId") Long programId){
        return OpenLmisResponse.response(ORDER_FILL_RATE, this.lookupService.getOrderFillRate(geographicZoneId,periodId,facilityId,programId));
    }

    @RequestMapping(value = "/shipmentLeadTime", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getShipmentLeadTime(@RequestParam("geographicZoneId") Long geographicZoneId,
                                                              @RequestParam("periodId") Long periodId,
                                                              @RequestParam("programId") Long programId){
        return OpenLmisResponse.response(SHIPMENT_LEAD_TIME, this.lookupService.getShipmentLeadTime(geographicZoneId,periodId,programId));
    }


    @RequestMapping(value = "/stockEfficiency", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getStockEfficiencyData(@RequestParam("geographicZoneId") Long geographicZoneId,
                                                                    @RequestParam("periodId") Long periodId,
                                                                    @RequestParam("programId") Long programId,
                                                                    @RequestParam("productListId")List<Long> productListId){
        return OpenLmisResponse.response(STOCKING_EFFICIENCY_STATICS, this.lookupService.getStockEfficiencyData(geographicZoneId, periodId, programId, productListId));
    }
    @RequestMapping(value = "/stockEfficiencyDetail", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getStockEfficiencyDetailData(@RequestParam("geographicZoneId") Long geographicZoneId,
                                                                    @RequestParam("periodId") Long periodId,
                                                                    @RequestParam("programId") Long programId,
                                                                    @RequestParam("productListId")List<Long> productListId){
        return OpenLmisResponse.response(STOCKING_EFFICIENCY_DETAIL, this.lookupService.getStockEfficiencyDetailData(geographicZoneId, periodId, programId, productListId));
    }

}
