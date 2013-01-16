package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
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

import static org.openlmis.web.response.OpenLmisResponse.*;

@Controller
@NoArgsConstructor
public class RnrController extends BaseController {

  public static final String RNR = "rnr";
  public static final String RNR_SAVE_SUCCESS = "rnr.save.success";
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
      return response(RNR, rnrService.initRnr(facilityId, programId, null, loggedInUserId(request)));
    } catch (DataException e) {
      return error(e.getOpenLmisMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> get(@RequestParam("facilityId") Integer facilityId,
                                              @RequestParam("programId") Integer programId) {
    return response(RNR, rnrService.get(facilityId, programId));
  }

  @RequestMapping(value = "/requisitions/{id}/save", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> saveRnr(@RequestBody Rnr rnr,
                                                  @PathVariable("id") Integer id,
                                                  HttpServletRequest request) {
    try {
      rnr.setId(id);
      rnr.setModifiedBy(loggedInUserId(request));
      rnrService.save(rnr);
      return OpenLmisResponse.success(RNR_SAVE_SUCCESS);
    } catch (DataException e) {
      return OpenLmisResponse.error(e.getOpenLmisMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions/{id}/submit", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> submit(@RequestBody Rnr rnr,
                                                 @PathVariable("id") Integer id,
                                                 HttpServletRequest request) {
    try {
      rnr.setId(id);
      rnr.setModifiedBy(loggedInUserId(request));
      return success(rnrService.submit(rnr));
    } catch (DataException e) {
      //TODO: save R&R in a better way
      rnrService.save(rnr);
      return error(e.getOpenLmisMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions/lossAndAdjustments/reference-data", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public Map getReferenceData() {
    RnrReferenceData referenceData = new RnrReferenceData();
    return referenceData.addLossesAndAdjustmentsTypes(rnrService.getLossesAndAdjustmentsTypes()).get();
  }

  @RequestMapping(value = "/requisitions/{id}/authorize", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('', 'AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> authorize(@RequestBody Rnr rnr,
                                                    @PathVariable("id") Integer id,
                                                    HttpServletRequest request) {
    try {
      rnr.setId(id);
      rnr.setModifiedBy(loggedInUserId(request));
      OpenLmisMessage openLmisMessage = rnrService.authorize(rnr);
      return success(openLmisMessage);
    } catch (DataException e) {
      rnrService.save(rnr);
      //TODO save R&R in a better way
      return error(e.getOpenLmisMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
