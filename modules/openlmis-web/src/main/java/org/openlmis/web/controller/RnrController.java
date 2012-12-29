package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@NoArgsConstructor
public class RnrController extends BaseController {

  public static final String RNR = "rnr";
  private RnrService rnrService;


  @Autowired
  public RnrController(RnrService rnrService) {
    this.rnrService = rnrService;
  }

  @RequestMapping(value = "/facility/{facilityId}/program/{programId}/rnr", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> initiateRnr(@PathVariable("facilityId") Integer facilityId,
                                                      @PathVariable("programId") Integer programId,
                                                      HttpServletRequest request) {
    try {
      return OpenLmisResponse.response(RNR, rnrService.initRnr(facilityId, programId, loggedInUser(request)));
    } catch (DataException e) {
      return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/facility/{facilityId}/program/{programId}/rnr", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable("facilityId") Integer facilityId,
                                              @PathVariable("programId") Integer programId) {
    try {
      return OpenLmisResponse.response(RNR, rnrService.get(facilityId, programId));
    } catch (DataException e) {
      return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/facility/{facilityId}/program/{programId}/rnr", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public void saveRnr(@RequestBody Rnr rnr,
                      @PathVariable("facilityId") int facilityId,
                      @PathVariable("programId") int programId,
                      HttpServletRequest request) {
    rnr.setFacilityId(facilityId);
    rnr.setProgramId(programId);
    rnr.setModifiedBy(loggedInUser(request));
    rnrService.save(rnr);
  }

  @RequestMapping(value = "/rnr/lossAndAdjustment/{lossAndAdjustmentId}", method = RequestMethod.DELETE, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public void removeLossAndAdjustment(@PathVariable("lossAndAdjustmentId") Integer lossAndAdjustmentId) {
    rnrService.removeLossAndAdjustment(lossAndAdjustmentId);
  }
}
