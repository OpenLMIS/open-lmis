/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.service;

import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.search.factory.RequisitionSearchStrategyFactory;
import org.openlmis.rnr.search.strategy.RequisitionSearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.BEGINNING_BALANCE;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Service
public class RequisitionService {

  public static final String RNR_AUTHORIZATION_ERROR = "error.rnr.authorization";
  public static final String RNR_SUBMISSION_ERROR = "error.rnr.submission";
  public static final String RNR_OPERATION_UNAUTHORIZED = "error.rnr.operation.unauthorized";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY = "msg.rnr.authorized.success";
  public static final String RNR_SUBMITTED_SUCCESSFULLY = "msg.rnr.submitted.success";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "msg.rnr.authorized.without.supervisor";
  public static final String RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "msg.rnr.approved.without.supervisor";
  public static final String NO_SUPERVISORY_NODE_CONTACT_ADMIN = "msg.rnr.submitted.without.supervisor";
  public static final String RNR_APPROVED_SUCCESSFULLY = "msg.rnr.approved.success";
  public static final String RNR_ALREADY_APPROVED = "rnr.already.approved";

  @Autowired
  private RequisitionRepository requisitionRepository;
  @Autowired
  private RnrTemplateService rnrTemplateService;
  @Autowired
  private FacilityApprovedProductService facilityApprovedProductService;
  @Autowired
  private SupervisoryNodeService supervisoryNodeService;
  @Autowired
  private RoleAssignmentService roleAssignmentService;
  @Autowired
  private ProgramService programService;
  @Autowired
  private ProcessingScheduleService processingScheduleService;
  @Autowired
  private FacilityService facilityService;
  @Autowired
  private SupplyLineService supplyLineService;
  @Autowired
  private RequisitionPermissionService requisitionPermissionService;
  @Autowired
  private UserService userService;
  @Autowired
  private RequisitionEventService requisitionEventService;
  @Autowired
  private RegimenService regimenService;
  @Autowired
  private RegimenColumnService regimenColumnService;

  private RequisitionSearchStrategyFactory requisitionSearchStrategyFactory;

  @Autowired
  public void setRequisitionSearchStrategyFactory(RequisitionSearchStrategyFactory requisitionSearchStrategyFactory) {
    this.requisitionSearchStrategyFactory = requisitionSearchStrategyFactory;
  }

  @Transactional
  public Rnr initiate(Long facilityId, Long programId, Long periodId, Long modifiedBy, Boolean emergency) {
    Long createdBy = modifiedBy;
    if (!requisitionPermissionService.hasPermission(modifiedBy, new Facility(facilityId), new Program(programId),
      CREATE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    ProgramRnrTemplate rnrTemplate = rnrTemplateService.fetchProgramTemplateForRequisition(programId);

    RegimenTemplate regimenTemplate = regimenColumnService.getRegimenTemplateByProgramId(programId);

    if (rnrTemplate.getColumns().size() == 0)
      throw new DataException("error.rnr.template.not.defined");

    if (!emergency) {
      validateIfRnrCanBeInitiatedFor(facilityId, programId, periodId);
    }

    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts;
    facilityTypeApprovedProducts = facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(
      facilityId, programId);

    List<Regimen> regimens = regimenService.getByProgram(programId);

    Rnr requisition = new Rnr(facilityId, programId, periodId, emergency, facilityTypeApprovedProducts, regimens, modifiedBy, createdBy);

    fillFieldsForInitiatedRequisitionAccordingToTemplate(requisition, rnrTemplate, regimenTemplate);

    insert(requisition);

    RequisitionSearchCriteria criteria = new RequisitionSearchCriteria(facilityId, programId, periodId, emergency);

    List<Rnr> rnrList = get(criteria);
    return (rnrList == null || rnrList.isEmpty()) ? null : rnrList.get(0);
  }

  @Transactional
  public void save(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());
    ProgramRnrTemplate rnrTemplate = rnrTemplateService.fetchProgramTemplate(savedRnr.getProgram().getId());
    RegimenTemplate regimenTemplate = regimenColumnService.getRegimenTemplateByProgramId(savedRnr.getProgram().getId());

    if (!requisitionPermissionService.hasPermissionToSave(rnr.getModifiedBy(), savedRnr))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);
    if (savedRnr.getStatus() == AUTHORIZED || savedRnr.getStatus() == IN_APPROVAL)
      savedRnr.copyApproverEditableFields(rnr, rnrTemplate);
    else
      savedRnr.copyCreatorEditableFields(rnr, rnrTemplate, regimenTemplate);

    requisitionRepository.update(savedRnr);
  }

  @Transactional
  public Rnr submit(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (savedRnr.getStatus() != INITIATED)
      throw new DataException(new OpenLmisMessage(RNR_SUBMISSION_ERROR, savedRnr.getStatus().name()));

    if (!requisitionPermissionService.hasPermission(rnr.getModifiedBy(), savedRnr, CREATE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    savedRnr.setAuditFieldsForRequisition(rnr.getModifiedBy(), SUBMITTED);

    savedRnr.calculate(rnrTemplateService.fetchProgramTemplate(savedRnr.getProgram().getId()),
      requisitionRepository.getLossesAndAdjustmentsTypes());

    return update(savedRnr);
  }

  @Transactional
  public Rnr authorize(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (savedRnr.getStatus() != SUBMITTED)
      throw new DataException(new OpenLmisMessage(RNR_AUTHORIZATION_ERROR, savedRnr.getStatus().name()));

    if (!requisitionPermissionService.hasPermission(rnr.getModifiedBy(), savedRnr, AUTHORIZE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    savedRnr.setAuditFieldsForRequisition(rnr.getModifiedBy(), AUTHORIZED);
    savedRnr.setSupervisoryNodeId(supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram()).getId());

    savedRnr.calculate(rnrTemplateService.fetchProgramTemplate(savedRnr.getProgram().getId()),
      requisitionRepository.getLossesAndAdjustmentsTypes());
    savedRnr.setDefaultApprovedQuantity();

    return update(savedRnr);
  }


  @Transactional
  public Rnr approve(Rnr requisition) {
    Rnr savedRnr = getFullRequisitionById(requisition.getId());
    savedRnr.validateForApproval();

    if (!requisitionPermissionService.hasPermission(requisition.getModifiedBy(), savedRnr, APPROVE_REQUISITION))
      throw new DataException(RNR_ALREADY_APPROVED);

    if (savedRnr.getStatus() != AUTHORIZED && savedRnr.getStatus() != IN_APPROVAL) {
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);
    }

    savedRnr.calculateForApproval();
    final SupervisoryNode parent = supervisoryNodeService.getParent(savedRnr.getSupervisoryNodeId());

    if (parent == null) {
      savedRnr.prepareForFinalApproval();
    } else {
      savedRnr.approveAndAssignToNextSupervisoryNode(parent);
    }
    savedRnr.setModifiedBy(requisition.getModifiedBy());
    requisitionRepository.approve(savedRnr);
    logStatusChangeAndNotify(savedRnr);
    return savedRnr;
  }

  public void releaseRequisitionsAsOrder(List<Rnr> requisitions, Long userId) {
    if (!requisitionPermissionService.hasPermission(userId, CONVERT_TO_ORDER))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);
    for (Rnr requisition : requisitions) {
      Rnr loadedRequisition = requisitionRepository.getById(requisition.getId());
      loadedRequisition.convertToOrder(userId);
      update(loadedRequisition);
    }
  }

  public List<Rnr> get(RequisitionSearchCriteria criteria) {
    RequisitionSearchStrategy strategy = requisitionSearchStrategyFactory.getSearchStrategy(criteria);
    List<Rnr> requisitions = strategy.search();
    fillSupportingInfo(requisitions);
    return requisitions;
  }

  private void fillSupportingInfo(List<Rnr> requisitions) {
    for (Rnr rnr : requisitions) {
      fillSupportingInfo(rnr);
    }
  }

  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return requisitionRepository.getLossesAndAdjustmentsTypes();
  }

  public OpenLmisMessage getSubmitMessageBasedOnSupervisoryNode(Facility facility, Program program) {
    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(facility, program);
    String msg = (supervisoryNode == null) ? NO_SUPERVISORY_NODE_CONTACT_ADMIN : RNR_SUBMITTED_SUCCESSFULLY;

    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage getAuthorizeMessageBasedOnSupervisoryNode(Facility facility, Program program) {
    User approver = supervisoryNodeService.getApproverFor(facility, program);
    String msg = (approver == null) ? RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR : RNR_AUTHORIZED_SUCCESSFULLY;
    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage getApproveMessageBasedOnParentNode(Rnr rnr) {
    SupervisoryNode parent = supervisoryNodeService.getParent(rnr.getSupervisoryNodeId());
    if (parent != null && supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, rnr.getProgram()) == null) {
      return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR);
    }
    return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY);
  }

  public Rnr getFullRequisitionById(Long id) {
    Rnr savedRnr = requisitionRepository.getById(id);
    fillSupportingInfo(savedRnr);
    fillSupplyingDepot(savedRnr);
    savedRnr.setSubmittedDate(getOperationDateFor(savedRnr.getId(), RnrStatus.SUBMITTED.toString()));
    return savedRnr;
  }

  public List<Rnr> getApprovedRequisitions() {
    List<Rnr> requisitions = requisitionRepository.getApprovedRequisitions();
    fillFacilityPeriodProgramWithAuditFields(requisitions);
    fillSupplyingFacility(requisitions.toArray(new Rnr[requisitions.size()]));
    return requisitions;
  }

  private void fillFieldsForInitiatedRequisitionAccordingToTemplate(Rnr requisition, ProgramRnrTemplate rnrTemplate, RegimenTemplate regimenTemplate) {
    requisition.setBeginningBalances(getPreviousRequisition(requisition), rnrTemplate.columnsVisible(BEGINNING_BALANCE));
    requisition.setFieldsAccordingToTemplate(rnrTemplate, regimenTemplate);
  }

  private Rnr fillSupportingInfo(Rnr requisition) {
    if (requisition == null) return null;

    fillFacilityPeriodProgramWithAuditFields(asList(requisition));
    fillPreviousRequisitionsForAmc(requisition);
    return requisition;
  }

  private void fillSupplyingFacility(Rnr... requisitions) {
    for (Rnr requisition : requisitions) {
      fillSupplyingDepot(requisition);
    }
  }

  private void fillSupplyingDepot(Rnr requisition) {
    if (requisition.getSupervisoryNodeId() != null && requisition.getStatus().equals(RnrStatus.APPROVED)) {
      SupplyLine supplyLine = supplyLineService.getSupplyLineBy(new SupervisoryNode(requisition.getSupervisoryNodeId()), requisition.getProgram());
      if (supplyLine != null)
        requisition.setSupplyingDepot(supplyLine.getSupplyingFacility());
    }
  }

  private List<ProcessingPeriod> getAllPeriodsForInitiatingRequisition(Long facilityId, Long programId) {
    return getAllPeriodsForInitiatingRequisition(new RequisitionSearchCriteria(facilityId, programId));
  }

  public List<ProcessingPeriod> getAllPeriodsForInitiatingRequisition(RequisitionSearchCriteria criteria) {
    Date programStartDate = programService.getProgramStartDate(criteria.getFacilityId(), criteria.getProgramId());

    Rnr lastRequisitionToEnterThePostSubmitFlow = requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(
      criteria.getFacilityId(), criteria.getProgramId());
    Long periodIdOfLastRequisitionToEnterPostSubmitFlow = lastRequisitionToEnterThePostSubmitFlow == null ?
      null : lastRequisitionToEnterThePostSubmitFlow.getPeriod().getId();

    return processingScheduleService.getAllPeriodsAfterDateAndPeriod(criteria.getFacilityId(), criteria.getProgramId(), programStartDate,
      periodIdOfLastRequisitionToEnterPostSubmitFlow);
  }

  private Rnr getPreviousRequisition(Rnr requisition) {
    ProcessingPeriod immediatePreviousPeriod = processingScheduleService.getImmediatePreviousPeriod(
      requisition.getPeriod());
    Rnr previousRequisition = null;
    if (immediatePreviousPeriod != null)
      previousRequisition = requisitionRepository.getRequisitionWithLineItems(requisition.getFacility(),
        requisition.getProgram(), immediatePreviousPeriod);
    return previousRequisition;
  }

  private void validateIfRnrCanBeInitiatedFor(Long facilityId, Long programId, Long periodId) {
    List<ProcessingPeriod> validPeriods = getAllPeriodsForInitiatingRequisition(facilityId, programId);
    if (validPeriods.size() == 0 || !validPeriods.get(0).getId().equals(periodId))
      throw new DataException("error.rnr.previous.not.filled");
  }

  private void fillPreviousRequisitionsForAmc(Rnr requisition) {
    if (requisition == null) return;

    Rnr lastPeriodsRnr = null;
    Rnr secondLastPeriodsRnr = null;

    if (requisition.getPeriod().getNumberOfMonths() <= 2) {
      lastPeriodsRnr = getLastPeriodsRnr(requisition);
      if (requisition.getPeriod().getNumberOfMonths() == 1)
        secondLastPeriodsRnr = getLastPeriodsRnr(lastPeriodsRnr);
    }
    requisition.fillLastTwoPeriodsNormalizedConsumptions(lastPeriodsRnr, secondLastPeriodsRnr);
  }

  private void fillFacilityPeriodProgramWithAuditFields(List<Rnr> requisitions) {
    for (Rnr requisition : requisitions) {
      Facility facility = facilityService.getById(requisition.getFacility().getId());
      ProcessingPeriod period = processingScheduleService.getPeriodById(requisition.getPeriod().getId());
      Program program = programService.getById(requisition.getProgram().getId());

      requisition.fillBasicInformation(facility, program, period);
      requisition.setSubmittedDate(getOperationDateFor(requisition.getId(), SUBMITTED.toString()));
    }
  }


  private Rnr getLastPeriodsRnr(Rnr requisition) {
    if (requisition == null) return null;

    ProcessingPeriod lastPeriod = processingScheduleService.getImmediatePreviousPeriod(requisition.getPeriod());
    if (lastPeriod == null) return null;

    return requisitionRepository.getRequisitionWithLineItems(requisition.getFacility(), requisition.getProgram(),
      lastPeriod);
  }

  public List<Rnr> listForApproval(Long userId) {
    List<RoleAssignment> assignments = roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId);
    List<Rnr> requisitionsForApproval = new ArrayList<>();
    for (RoleAssignment assignment : assignments) {
      final List<Rnr> requisitions = requisitionRepository.getAuthorizedRequisitions(assignment);
      requisitionsForApproval.addAll(requisitions);
    }
    fillFacilityPeriodProgramWithAuditFields(requisitionsForApproval);
    return requisitionsForApproval;
  }


  private Rnr update(Rnr requisition) {
    requisitionRepository.update(requisition);
    logStatusChangeAndNotify(requisition);
    return requisition;
  }

  private void logStatusChangeAndNotify(Rnr requisition) {
    requisitionRepository.logStatusChange(requisition);
    requisitionEventService.notifyForStatusChange(requisition);
  }

  private void insert(Rnr requisition) {
    requisitionRepository.insert(requisition);
    logStatusChangeAndNotify(requisition);
  }

  public Integer getCategoryCount(Rnr requisition, boolean fullSupply) {
    return requisitionRepository.getCategoryCount(requisition, fullSupply);
  }

  public void insertComment(Comment comment) {
    requisitionRepository.insertComment(comment);
  }

  public List getCommentsByRnrId(Long rnrId) {
    List<Comment> comments = requisitionRepository.getCommentsByRnrID(rnrId);
    for (Comment comment : comments) {
      User user = userService.getById(comment.getAuthor().getId());
      comment.setAuthor(user.basicInformation());
    }
    return comments;
  }

  public Rnr getLWById(Long rnrId) {
    return requisitionRepository.getLWById(rnrId);
  }

  public Date getOperationDateFor(Long rnrId, String status) {
    return requisitionRepository.getOperationDateFor(rnrId, status);
  }

  public ProcessingPeriod getCurrentPeriod(RequisitionSearchCriteria criteria) {
    Date programStartDate = programService.getProgramStartDate(criteria.getFacilityId(), criteria.getProgramId());
    return processingScheduleService.getCurrentPeriod(criteria.getFacilityId(), criteria.getProgramId(), programStartDate);
  }
}

