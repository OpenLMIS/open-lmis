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

    @Autowired
    DashboardLookupService lookupService;

    @RequestMapping(value = "/itemFillRate", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getItemFillRate(){
      return OpenLmisResponse.response("itemFillRate", this.lookupService.getItemFillRate());
    }

    @RequestMapping(value = "/orderFillRate", method = GET, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse>  getOrderFillRate(@RequestParam("geographiczoneId") Long geographiczoneId,
                                                              @RequestParam("periodId") Long periodId,
                                                              @RequestParam("facilityId") Long facilityId,
                                                              HttpServletRequest request){
        return OpenLmisResponse.response("orderFillRate", this.lookupService.getOrderFillRate(geographiczoneId,facilityId,periodId,null));
    }
}
