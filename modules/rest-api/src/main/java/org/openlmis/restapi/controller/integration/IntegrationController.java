package org.openlmis.restapi.controller.integration;

import org.openlmis.restapi.controller.BaseController;
import org.openlmis.restapi.service.integration.FacilityIntegrationFromFCService;
import org.openlmis.restapi.service.integration.IntegrationToFCService;
import org.openlmis.restapi.service.integration.ProductIntegrationFromFCService;
import org.openlmis.restapi.service.integration.ProgramIntegrationFromFCService;
import org.openlmis.restapi.service.integration.RegimenIntegrationFromFCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class IntegrationController extends BaseController {

    private IntegrationToFCService integrationToFCService;

    private ProgramIntegrationFromFCService programIntegrationFromFCService;

    private RegimenIntegrationFromFCService regimenIntegrationFromFCService;

    private FacilityIntegrationFromFCService facilityIntegrationFromFCService;

    private ProductIntegrationFromFCService productIntegrationFromFCService;

    private static ExecutorService singleThreadExecutor = null;

    @Autowired
    public IntegrationController(IntegrationToFCService integrationToFCService,
                                 ProgramIntegrationFromFCService programIntegrationFromFCService,
                                 RegimenIntegrationFromFCService regimenIntegrationFromFCService,
                                 ProductIntegrationFromFCService productIntegrationFromFCService,
                                 FacilityIntegrationFromFCService facilityIntegrationFromFCService) {
        this.integrationToFCService = integrationToFCService;
        this.programIntegrationFromFCService = programIntegrationFromFCService;
        this.regimenIntegrationFromFCService = regimenIntegrationFromFCService;
        this.facilityIntegrationFromFCService = facilityIntegrationFromFCService;
        this.productIntegrationFromFCService = productIntegrationFromFCService;
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

//    @RequestMapping(value = "/rest-api/sync/page", method = RequestMethod.GET)
//    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam String type) {
//        return ResponseEntity.ok(integrationToFCService.getPageInfo(fromStartDate, type));
//    }
//
//    @RequestMapping(value ="/rest-api/sync/sohs", method = RequestMethod.GET)
//    public ResponseEntity getPageInfo(@RequestParam String fromStartDate, @RequestParam int startPage) {
//        return ResponseEntity.ok(integrationToFCService.getSohByDate(fromStartDate, startPage));
//    }
//
//    @RequestMapping(value = "rest-api/sync/movs", method = RequestMethod.GET)
//    public ResponseEntity getStockMovements(@RequestParam String fromStartDate, @RequestParam int startPage) {
//        return ResponseEntity.ok(integrationToFCService.getStockMovementsByDate(fromStartDate, startPage));
//    }
//
//    @RequestMapping(value = "/rest-api/sync/requisitions", method = RequestMethod.GET)
//    public ResponseEntity getRequisitions(@RequestParam String fromStartDate, @RequestParam int startPage) {
//        return ResponseEntity.ok(integrationToFCService.getRequisitionsByDate(fromStartDate, startPage));
//    }
//
//    @RequestMapping(value = "/rest-api/sync/all", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public void synAll(final @RequestParam(required = false) String fromStartDate) {
//        singleThreadExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                programIntegrationFromFCService.sycDataFromFC(fromStartDate);
//                regimenIntegrationFromFCService.sycDataFromFC(fromStartDate);
//                facilityIntegrationFromFCService.sycDataFromFC(fromStartDate);
//                productIntegrationFromFCService.sycDataFromFC(fromStartDate);
//            }
//        });
//
//    }
//
//    @RequestMapping(value = "/rest-api/sync/program", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public void synProgram(@RequestParam(required = false) String fromStartDate) {
//        programIntegrationFromFCService.sycDataFromFC(fromStartDate);
//    }
//
//    @RequestMapping(value = "/rest-api/sync/regimen", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public void synRegimen(@RequestParam(required = false) String fromStartDate) {
//        regimenIntegrationFromFCService.sycDataFromFC(fromStartDate);
//    }
//
//    @RequestMapping(value = "/rest-api/sync/product", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public void synProduct(@RequestParam(required = false) String fromStartDate) {
//        productIntegrationFromFCService.sycDataFromFC(fromStartDate);
//    }
//
//    @RequestMapping(value = "/rest-api/sync/facility", method = RequestMethod.GET)
//    @ResponseStatus(HttpStatus.OK)
//    public void synFacility(@RequestParam(required = false) String fromStartDate) {
//        facilityIntegrationFromFCService.sycDataFromFC(fromStartDate);
//    }
}