package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.openlmis.web.response.OpenLmisResponse.response;

@Controller
@NoArgsConstructor
public class FacilityApprovedProductController extends BaseController {

  public static final String NON_FULL_SUPPLY_PRODUCTS = "nonFullSupplyProducts";

  private FacilityApprovedProductService facilityApprovedProductService;

  @Autowired
  public FacilityApprovedProductController(FacilityApprovedProductService facilityApprovedProductService) {
    this.facilityApprovedProductService = facilityApprovedProductService;
  }

  @RequestMapping(value = "/facilityApprovedProducts/facility/{facilityId}/program/{programId}/nonFullSupply", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getAllNonFullSupplyProductsByFacilityAndProgram(@PathVariable("facilityId") Integer facilityId,
                                                                                          @PathVariable("programId") Integer programId) {
    return response(NON_FULL_SUPPLY_PRODUCTS, facilityApprovedProductService.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId));
  }
}
