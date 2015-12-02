package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.web.rest.model.ColdTraceData;
import org.openlmis.web.rest.service.FridgeService;
import org.springframework.beans.factory.annotation.Autowired;
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
@NoArgsConstructor
public class FridgeStatusController extends BaseController {

    @Autowired
    private FridgeService fridgeService;

    @RequestMapping(value = "/fridges", method = GET)
    public ResponseEntity<OpenLmisResponse> getFridges(HttpServletRequest request) {
        ColdTraceData coldTraceData = fridgeService.getFridges();

        return response("coldTraceData", coldTraceData);
    }
}
