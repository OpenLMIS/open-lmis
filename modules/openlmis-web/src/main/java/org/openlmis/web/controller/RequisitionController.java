package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.web.model.RnrReferenceData;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.openlmis.web.response.OpenLmisResponse.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Controller
@NoArgsConstructor
public class RequisitionController extends BaseController {

  public static final String RNR = "rnr";
  public static final String RNR_SAVE_SUCCESS = "rnr.save.success";
  public static final String RNR_LIST = "rnr_list";
  private RequisitionService requisitionService;
  public static final String PERIODS = "periods";

  @Autowired
  public RequisitionController(RequisitionService requisitionService) {
    this.requisitionService = requisitionService;
  }

  @RequestMapping(value = "/requisitions", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> initiateRnr(@RequestParam("facilityId") Integer facilityId,
                                                      @RequestParam("programId") Integer programId,
                                                      @RequestParam("periodId") Integer periodId,
                                                      HttpServletRequest request) {
    try {
      return response(RNR, requisitionService.initiate(facilityId, programId, periodId, loggedInUserId(request)));
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> get(@RequestParam("facilityId") Integer facilityId,
                                              @RequestParam("programId") Integer programId,
                                              @RequestParam("periodId") Integer periodId) {
    return response(RNR, requisitionService.get(facilityId, programId, periodId));
  }

  @RequestMapping(value = "/requisitions/{id}/save", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> saveRnr(@RequestBody Rnr rnr,
                                                  @PathVariable("id") Integer id,
                                                  HttpServletRequest request) {
    try {
      rnr.setId(id);
      rnr.setModifiedBy(loggedInUserId(request));
      requisitionService.save(rnr);
      return OpenLmisResponse.success(RNR_SAVE_SUCCESS);
    } catch (DataException e) {
      return OpenLmisResponse.error(e, BAD_REQUEST);
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
      return success(requisitionService.submit(rnr));
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions/lossAndAdjustments/reference-data", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public Map getReferenceData() {
    RnrReferenceData referenceData = new RnrReferenceData();
    return referenceData.addLossesAndAdjustmentsTypes(requisitionService.getLossesAndAdjustmentsTypes()).get();
  }

  @RequestMapping(value = "/requisitions/{id}/authorize", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('', 'AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> authorize(@RequestBody Rnr rnr,
                                                    @PathVariable("id") Integer id,
                                                    HttpServletRequest request) {
    try {
      rnr.setId(id);
      rnr.setModifiedBy(loggedInUserId(request));
      OpenLmisMessage openLmisMessage = requisitionService.authorize(rnr);
      return success(openLmisMessage);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions-for-approval", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('', 'APPROVE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> listForApproval(HttpServletRequest request) {
    List<Rnr> requisitions = requisitionService.listForApproval(loggedInUserId(request));
    return response(RNR_LIST, RnrDTO.prepareForListApproval(requisitions));
  }

  @RequestMapping(value = "/logistics/facility/{facilityId}/program/{programId}/periods", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(@PathVariable("facilityId") Integer facilityId, @PathVariable("programId") Integer programId) {
    try {
      List<ProcessingPeriod> periodList = requisitionService.getAllPeriodsForInitiatingRequisition(facilityId, programId);
      Rnr currentRequisition = getRequisitionForCurrentPeriod(facilityId, programId, periodList);
      ResponseEntity<OpenLmisResponse> responseEntity = OpenLmisResponse.response(PERIODS, periodList);
      responseEntity.getBody().setData(RNR, currentRequisition);
      return  responseEntity;
    } catch (DataException e) {
      return error(e, CONFLICT);
    }
  }

  private Rnr getRequisitionForCurrentPeriod(Integer facilityId, Integer programId, List<ProcessingPeriod> periodList) {
    if(periodList == null || periodList.isEmpty()) return null;

    return requisitionService.get(facilityId, programId, periodList.get(0).getId());
  }
}
