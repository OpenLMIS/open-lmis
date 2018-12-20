package org.openlmis.restapi.controller.integration;

import org.openlmis.restapi.controller.BaseController;
import org.openlmis.restapi.service.integration.IntegrationToFCService;
import org.openlmis.restapi.service.integration.ProgramIntegrationFromFCService;
import org.openlmis.restapi.service.integration.RegimenIntegrationFromFCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class IntegrationController extends BaseController {

    private IntegrationToFCService integrationToFCService;

    private ProgramIntegrationFromFCService programIntegrationFromFCService;

    private RegimenIntegrationFromFCService regimenIntegrationFromFCService;

    @Autowired
    public IntegrationController(IntegrationToFCService integrationToFCService,
                                 ProgramIntegrationFromFCService programIntegrationFromFCService,
                                 RegimenIntegrationFromFCService regimenIntegrationFromFCService) {
        this.integrationToFCService = integrationToFCService;
        this.programIntegrationFromFCService = programIntegrationFromFCService;
        this.regimenIntegrationFromFCService = regimenIntegrationFromFCService;
    }

    @RequestMapping("/rest-api/page")
    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam String type) {
        return ResponseEntity.ok(integrationToFCService.getPageInfo(fromStartDate, type));
    }


    @RequestMapping("/rest-api/sohs")
    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getSohByDate(fromStartDate, startPage));
    }

    @RequestMapping("/rest-api/movs")
    public ResponseEntity getStockMovements(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getStockMovementsByDate(fromStartDate, startPage));
    }

    @RequestMapping("/rest-api/syn/all")
    @ResponseStatus(HttpStatus.OK)
    public void synAll(@RequestParam(required = false) String fromStartDate) {
        programIntegrationFromFCService.sycDataFromFC(fromStartDate);
    }

    @RequestMapping("/rest-api/syn/program")
    @ResponseStatus(HttpStatus.OK)
    public void synProgram(@RequestParam(required = false) String fromStartDate) {
        programIntegrationFromFCService.sycDataFromFC(fromStartDate);
    }

//    @RequestMapping("/rest-api/syn/regimen")
//    public ResponseEntity synRegimen(@RequestParam(required = false) String fromStartDate) {
//        return ResponseEntity.ok(regimenIntegrationFromFCService.getDataFromFC(fromStartDate));
//    }

    @RequestMapping("/rest-api/requisitions")
    public ResponseEntity getRequisitions(@RequestParam String fromStartDate, @RequestParam int startPage) {
        return ResponseEntity.ok(integrationToFCService.getRequisitionsByDate(fromStartDate, startPage));
    }
}