/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Comment;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.rnr.service.RnrTemplateService;
import org.openlmis.web.configurationReader.StaticReferenceDataReader;
import org.openlmis.web.model.RnrReferenceData;
import org.openlmis.web.response.OpenLmisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.openlmis.rnr.dto.RnrDTO.prepareForListApproval;
import static org.openlmis.rnr.dto.RnrDTO.prepareForView;
import static org.openlmis.web.response.OpenLmisResponse.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@NoArgsConstructor
public class RequisitionController extends BaseController {
  public static final String RNR = "rnr";
  public static final String RNR_SAVE_SUCCESS = "rnr.save.success";
  public static final String RNR_LIST = "rnr_list";
  public static final String RNR_TEMPLATE = "rnr_template";

  public static final String PERIODS = "periods";
  public static final String CURRENCY = "currency";

  public static final String COMMENTS = "comments";

  private RequisitionService requisitionService;
  private RnrTemplateService rnrTemplateService;
  private StaticReferenceDataReader staticReferenceDataReader;
  public static final String LOSSES_AND_ADJUSTMENT_TYPES = "lossesAndAdjustmentTypes";

  private static final Logger logger = LoggerFactory.getLogger(RequisitionController.class);

  @Autowired
  public RequisitionController(RequisitionService requisitionService, RnrTemplateService rnrTemplateService, StaticReferenceDataReader staticReferenceDataReader) {
    this.requisitionService = requisitionService;
    this.rnrTemplateService = rnrTemplateService;
    this.staticReferenceDataReader = staticReferenceDataReader;
  }

  @RequestMapping(value = "/requisitions", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> initiateRnr(@RequestParam("facilityId") Long facilityId,
                                                      @RequestParam("programId") Long programId,
                                                      @RequestParam("periodId") Long periodId,
                                                      HttpServletRequest request) {
    try {
      return response(RNR, requisitionService.initiate(facilityId, programId, periodId, loggedInUserId(request)));
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> get(RequisitionSearchCriteria criteria, HttpServletRequest request) {
    criteria.setUserId(loggedInUserId(request));

    List<Rnr> rnrs = requisitionService.get(criteria);
    Rnr requisition = (rnrs == null || rnrs.isEmpty()) ? null : rnrs.get(0);
    return response(RNR, requisition);
  }

  @RequestMapping(value = "/requisitions-list", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getRequisitionsForView(RequisitionSearchCriteria criteria, HttpServletRequest request) {
    criteria.setUserId(loggedInUserId(request));
    return response(RNR_LIST, prepareForView(requisitionService.get(criteria)));
  }


  @RequestMapping(value = "/requisitions/{id}/save", method = PUT, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> saveRnr(@RequestBody Rnr rnr,
                                                  @PathVariable("id") Long id,
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

  @RequestMapping(value = "/requisitions/{id}/submit", method = PUT, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> submit(@PathVariable("id") Long id,
                                                 HttpServletRequest request) {
    try {
      Rnr rnr = new Rnr(id);
      rnr.setModifiedBy(loggedInUserId(request));
      Rnr submittedRnr = requisitionService.submit(rnr);

      return success(requisitionService.getSubmitMessageBasedOnSupervisoryNode(submittedRnr.getFacility(),
        submittedRnr.getProgram()));
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions/lossAndAdjustments/reference-data", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION')")
  public Map getReferenceData() {
    RnrReferenceData referenceData = new RnrReferenceData();
    return referenceData.addLossesAndAdjustmentsTypes(requisitionService.getLossesAndAdjustmentsTypes()).get();
  }

  @RequestMapping(value = "/requisitions/{id}/authorize", method = PUT, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> authorize(@PathVariable("id") Long id,
                                                    HttpServletRequest request) {
    try {
      Rnr rnr = new Rnr(id);
      rnr.setModifiedBy(loggedInUserId(request));
      Rnr authorizedRnr = requisitionService.authorize(rnr);
      return success(requisitionService.getAuthorizeMessageBasedOnSupervisoryNode(authorizedRnr.getFacility(),authorizedRnr.getProgram()));
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/requisitions/{id}/approve", method = PUT, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> approve(@PathVariable("id") Long id, HttpServletRequest request) {
    Rnr rnr = new Rnr(id);
    rnr.setModifiedBy(loggedInUserId(request));
    try {
      return success(requisitionService.approve(rnr));
    } catch (DataException dataException) {
      logger.warn("Error in approving requisition #{}", id, dataException);
      return error(dataException, BAD_REQUEST);
    }
  }


  @RequestMapping(value = "/requisitions-for-approval", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'APPROVE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> listForApproval(HttpServletRequest request) {
    List<Rnr> requisitions = requisitionService.listForApproval(loggedInUserId(request));
    return response(RNR_LIST, prepareForListApproval(requisitions));
  }

  @RequestMapping(value = "/requisitions-for-convert-to-order", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'CONVERT_TO_ORDER')")
  public ResponseEntity<OpenLmisResponse> listForConvertToOrder() {
    List<Rnr> approvedRequisitions = requisitionService.getApprovedRequisitions();
    return response(RNR_LIST, prepareForListApproval(approvedRequisitions));
  }

  @RequestMapping(value = "/logistics/facility/{facilityId}/program/{programId}/periods", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(
      @PathVariable("facilityId") Long facilityId,
      @PathVariable("programId") Long programId) {
    try {
      List<ProcessingPeriod> periodList = requisitionService.getAllPeriodsForInitiatingRequisition(facilityId, programId);
      Rnr currentRequisition = getRequisitionForCurrentPeriod(facilityId, programId, periodList);
      OpenLmisResponse response = new OpenLmisResponse(PERIODS, periodList);
      response.addData(RNR, currentRequisition);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (DataException e) {
      return error(e, CONFLICT);
    }
  }

  private Rnr getRequisitionForCurrentPeriod(Long facilityId, Long programId, List<ProcessingPeriod> periodList) {
    if (periodList == null || periodList.isEmpty()) return null;
    boolean withoutLineItems = true;
    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId,
        periodList.get(0).getId(), withoutLineItems);
    List<Rnr> rnrList = requisitionService.get(criteria);
    return (rnrList == null || rnrList.isEmpty()) ? null : rnrList.get(0);
  }

  @RequestMapping(value = "/requisitions/{id}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getById(@PathVariable Long id) {
    try {
      return response(RNR, requisitionService.getFullRequisitionById(id));
    } catch (DataException dataException) {
      return error(dataException, HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/requisitions/{id}/print", method = GET, headers = ACCEPT_PDF)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REQUISITION')")
  public ModelAndView printRequisition(@PathVariable Long id) {
    ModelAndView modelAndView = new ModelAndView("requisitionPDF");
    Rnr requisition = requisitionService.getFullRequisitionById(id);
    modelAndView.addObject(RNR, requisition);
    modelAndView.addObject(LOSSES_AND_ADJUSTMENT_TYPES, requisitionService.getLossesAndAdjustmentsTypes());
    modelAndView.addObject(RNR_TEMPLATE, rnrTemplateService.fetchColumnsForRequisition(requisition.getProgram().getId()));
    modelAndView.addObject(CURRENCY, staticReferenceDataReader.getCurrency());
    return modelAndView;
  }


  @RequestMapping(value = "/requisitions/{id}/comments", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'CREATE_REQUISITION, AUTHORIZE_REQUISITION, APPROVE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> insertComment(@RequestBody Comment comment, @PathVariable("id") Long id, HttpServletRequest request) {
    comment.setRnrId(id);
    User author = new User();
    author.setId(loggedInUserId(request));
    comment.setAuthor(author);
    requisitionService.insertComment(comment);
    return OpenLmisResponse.response(COMMENTS, requisitionService.getCommentsByRnrId(id));
  }

  @RequestMapping(value = "/requisitions/{id}/comments", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getCommentsForARnr(@PathVariable Long id) {
    return response(COMMENTS, requisitionService.getCommentsByRnrId(id));
  }
}
