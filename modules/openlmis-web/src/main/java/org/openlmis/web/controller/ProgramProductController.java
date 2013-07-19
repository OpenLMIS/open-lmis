package org.openlmis.web.controller;

import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ProgramProductController {

    @Autowired
    ProgramProductService service;
    private static final String PROGRAM_PRODUCT_LIST = "programProductList";
    private static final String PROGRAM_PRODUCT_PRICE_LIST = "programProductPriceList";

    @RequestMapping(value = "/programProducts/programId/{programId}", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
        List<ProgramProduct> programProductsByProgram = service.getByProgram(new Program(programId));
        return OpenLmisResponse.response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
    }
 /*
    @RequestMapping(value = "/programProducts/programProductId/{programPorductId}", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getProgramsProductPrices(@PathVariable Long programProductId) {
        List<ProgramProductPrice> programsProductPrice = service.getProgramProductPrice(new ProgramProduct(programProductId));
        return OpenLmisResponse.response(PROGRAM_PRODUCT_PRICE_LIST, programsProductPrice);
    }
   */

}
