package org.openlmis.web.controller;

import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.rest.RestClient;
import org.openlmis.web.rest.model.ColdTradeData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint to get fridge status.
 */

@Controller
public class FridgeStatusController extends BaseController {

    @RequestMapping(value = "/fridges", method = GET)
    public ResponseEntity<OpenLmisResponse> getFridges(HttpServletRequest request) {
        RestClient restClient = new RestClient("vrapi", "vrapi_vrapi_coldtrace");
        ColdTradeData coldTradeData = restClient.getForObject("http://uar.coldtrace.org/api/v1/fridges/", ColdTradeData.class);
        return response("coldTradeData", coldTradeData);
    }

}