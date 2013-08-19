package org.openlmis.web.controller;

import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.report.service.ProgramProductPriceListDataProvider;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ProgramProductController  extends BaseController  {

    @Autowired
    ProgramProductService service;

  @Autowired
  private ProgramProductPriceListDataProvider programPriceService;

    private static final String PROGRAM_PRODUCT_LIST = "programProductList";
    private static final String PROGRAM_PRODUCT_PRICE_LIST = "programProductPriceList";

    @RequestMapping(value = "/programProducts/programId/{programId}", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
        List<ProgramProduct> programProductsByProgram = service.getOptionsByProgram(new Program(programId));
        return OpenLmisResponse.response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
    }

  // All product cost
  @RequestMapping(value = "/allproductcost", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getAllPrices(Long id) {
    return OpenLmisResponse.response("AllProgramCosts", programPriceService.getAllPrices());
  }

  // product cost history for this product
  @RequestMapping(value = "/priceHistory/{productId}", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getProductPriceHistory(@PathVariable("productId") Long productId) {
    return OpenLmisResponse.response("priceHistory", programPriceService.getByProductId( productId ) );
  }

}
