package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.openlmis.web.model.RnrReferenceData;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@NoArgsConstructor
public class RnrController extends BaseController {

  public static final String RNR = "rnr";
  private RnrService rnrService;


  @Autowired
  public RnrController(RnrService rnrService) {
    this.rnrService = rnrService;
  }

  @RequestMapping(value = "/requisitions", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> initiateRnr(@RequestParam("facilityId") Integer facilityId,
                                                      @RequestParam("programId") Integer programId,
                                                      HttpServletRequest request) {
    try {
      return OpenLmisResponse.response(RNR, rnrService.initRnr(facilityId, programId, loggedInUserId(request)));
    } catch (DataException e) {
      return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> get(@RequestParam("facilityId") Integer facilityId,
                                              @RequestParam("programId") Integer programId) {
    return OpenLmisResponse.response(RNR, rnrService.get(facilityId, programId));
  }

  @RequestMapping(value = "/requisitions/{id}/save", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public void saveRnr(@RequestBody Rnr rnr, HttpServletRequest request) {
    rnr.setModifiedBy(loggedInUserId(request));
    rnrService.save(rnr);
  }

  @RequestMapping(value = "/requisitions/{id}/submit", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> submit(@RequestBody Rnr rnr, HttpServletRequest request) {
    rnr.setModifiedBy(loggedInUserId(request));
    try{
      return OpenLmisResponse.success(rnrService.submit(rnr));
    }catch(DataException e) {
      return OpenLmisResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions/lossAndAdjustments/reference-data", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public Map getReferenceData() {
    RnrReferenceData referenceData = new RnrReferenceData();
    return referenceData.addLossesAndAdjustmentsTypes(rnrService.getLossesAndAdjustmentsTypes()).get();
  }
}
