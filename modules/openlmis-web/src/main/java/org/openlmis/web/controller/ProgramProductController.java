package org.openlmis.web.controller;

import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ProgramProductController {

  @Autowired
  ProgramProductService service;
  private static final String PROGRAM_PRODUCT_LIST = "programProductList";

  @RequestMapping(value = "/programProducts/programId/{programId}", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
    List<ProgramProduct> programProductsByProgram = service.getByProgram(new Program(programId));
    return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }

  @RequestMapping(value = "/programProducts", method = GET, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getProgramProductsBy(@RequestParam String programCode,
                                                               @RequestParam(required = false) String facilityTypeCode) {
    try {
      List<ProgramProduct> programProducts = service.getProgramProductsBy(programCode, facilityTypeCode);
      return response(PROGRAM_PRODUCT_LIST, programProducts);
    } catch (DataException de) {
      return error(de, BAD_REQUEST);
    }
  }
}
