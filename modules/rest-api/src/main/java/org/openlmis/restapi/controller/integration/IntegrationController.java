package org.openlmis.restapi.controller.integration;

import org.openlmis.restapi.controller.BaseController;
import org.openlmis.restapi.service.integration.IntegrationToFCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IntegrationController extends BaseController {

    private IntegrationToFCService integrationToFCService;

    @Autowired
    public IntegrationController(IntegrationToFCService integrationToFCService) {
        this.integrationToFCService = integrationToFCService;
    }

    @RequestMapping("/rest-api/page")
    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam String type) {
        return ResponseEntity.ok(integrationToFCService.getPageInfo(fromStartDate, type));
    }


    @RequestMapping("/rest-api/sohs")
    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getSohByDate(fromStartDate, startPage));
    }

    @RequestMapping("rest-api/movs")
    public ResponseEntity getStockMovements(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getStockMovementsByDate(fromStartDate, startPage));
    }
}