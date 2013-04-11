/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.rnr.domain.*;
import org.openlmis.rnr.factory.RequisitionFactory;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.BEGINNING_BALANCE;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Service
@NoArgsConstructor
public class RequisitionService {

  public static final String RNR_AUTHORIZATION_ERROR = "rnr.authorization.error";
  public static final String RNR_SUBMISSION_ERROR = "rnr.submission.error";
  public static final String RNR_OPERATION_UNAUTHORIZED = "rnr.operation.unauthorized";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY = "rnr.authorized.success";
  public static final String RNR_SUBMITTED_SUCCESSFULLY = "rnr.submitted.success";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "rnr.authorized.without.supervisor";
  public static final String RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "rnr.approved.without.supervisor";
  public static final String NO_SUPERVISORY_NODE_CONTACT_ADMIN = "rnr.submitted.without.supervisor";
  public static final String RNR_PREVIOUS_NOT_FILLED_ERROR = "rnr.previous.not.filled.error";
  public static final String RNR_APPROVED_SUCCESSFULLY = "rnr.approved.success";
  public static final String RNR_TEMPLATE_NOT_INITIATED_ERROR = "rnr.template.not.defined.error";


  private RequisitionRepository requisitionRepository;
  private RnrTemplateRepository rnrTemplateRepository;
  private FacilityApprovedProductService facilityApprovedProductService;
  private SupervisoryNodeService supervisoryNodeService;
  private ProgramService programService;
  private ProcessingScheduleService processingScheduleService;
  private FacilityService facilityService;
  private SupplyLineService supplyLineService;
  private RequisitionFactory requisitionFactory;
  private RequisitionPermissionService requisitionPermissionService;
  private UserService userService;
  private RoleAssignmentService roleAssignmentService;

  @Autowired
  public RequisitionService(RequisitionRepository requisitionRepository, RnrTemplateRepository rnrTemplateRepository,
                            FacilityApprovedProductService facilityApprovedProductService, SupervisoryNodeService supervisoryNodeRepository,
                            RoleAssignmentService roleAssignmentService, ProgramService programService,
                            ProcessingScheduleService processingScheduleService, FacilityService facilityService, SupplyLineService supplyLineService,
                            RequisitionFactory requisitionFactory, RequisitionPermissionService requisitionPermissionService, UserService userService) {
    this.requisitionRepository = requisitionRepository;
    this.rnrTemplateRepository = rnrTemplateRepository;
    this.facilityApprovedProductService = facilityApprovedProductService;
    this.supervisoryNodeService = supervisoryNodeRepository;
    this.roleAssignmentService = roleAssignmentService;
    this.programService = programService;
    this.processingScheduleService = processingScheduleService;
    this.facilityService = facilityService;
    this.supplyLineService = supplyLineService;
    this.requisitionFactory = requisitionFactory;
    this.requisitionPermissionService = requisitionPermissionService;
    this.userService = userService;
  }

  @Transactional
  public Rnr initiate(Integer facilityId, Integer programId, Integer periodId, Integer modifiedBy) {
    if (!requisitionPermissionService.hasPermission(modifiedBy, new Facility(facilityId), new Program(programId), CREATE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    ProgramRnrTemplate rnrTemplate = new ProgramRnrTemplate(programId, rnrTemplateRepository.fetchColumnsForRequisition(programId));
    if (rnrTemplate.getRnrColumns().size() == 0) throw new DataException(RNR_TEMPLATE_NOT_INITIATED_ERROR);

    validateIfRnrCanBeInitiatedFor(facilityId, programId, periodId);

    List<FacilityApprovedProduct> facilityApprovedProducts = facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId);

    Rnr requisition = new Rnr(facilityId, programId, periodId, facilityApprovedProducts, modifiedBy);

    fillFieldsForInitiatedRequisitionAccordingToTemplate(requisition, rnrTemplate);

    requisitionRepository.insert(requisition);
    requisitionRepository.logStatusChange(requisition);
    return get(new Facility(facilityId), new Program(programId), new ProcessingPeriod(periodId));
  }

  private void fillFieldsForInitiatedRequisitionAccordingToTemplate(Rnr requisition, ProgramRnrTemplate template) {
    requisition.setBeginningBalances(getPreviousRequisition(requisition), template.columnsVisible(BEGINNING_BALANCE));
    requisition.setFieldsAccordingToTemplate(template);
  }

  public void save(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (!requisitionPermissionService.hasPermissionToSave(rnr.getModifiedBy(), savedRnr))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    savedRnr.copyEditableFields(rnr, rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(savedRnr.getProgram().getId()));

    requisitionRepository.update(savedRnr);
  }

  public Rnr get(Facility facility, Program program, ProcessingPeriod period) {
    Rnr requisition = requisitionRepository.getRequisition(facility, program, period);
    return fillSupportingInfo(requisition);
  }


  private Rnr fillSupportingInfo(Rnr requisition) {
    if (requisition == null) return null;

    fillFacilityPeriodProgram(requisition);
    fillPreviousRequisitionsForAmc(requisition);
    return requisition;
  }

  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return requisitionRepository.getLossesAndAdjustmentsTypes();
  }

  public OpenLmisMessage submit(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (!requisitionPermissionService.hasPermission(rnr.getModifiedBy(), savedRnr, CREATE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    List<RnrColumn> rnrColumns = rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(savedRnr.getProgram().getId());

    if (savedRnr.getStatus() != INITIATED) {
      throw new DataException(new OpenLmisMessage(RNR_SUBMISSION_ERROR));
    }

    savedRnr.copyUserEditableFields(rnr, rnrColumns);

    savedRnr.prepareFor(SUBMITTED, rnrColumns);

    update(savedRnr);
    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram());
    String msg = (supervisoryNode == null) ? NO_SUPERVISORY_NODE_CONTACT_ADMIN : RNR_SUBMITTED_SUCCESSFULLY;

    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage authorize(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());
    if (!requisitionPermissionService.hasPermission(rnr.getModifiedBy(), savedRnr, AUTHORIZE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    List<RnrColumn> rnrColumns = rnrTemplateRepository.fetchRnrTemplateColumnsOrMasterColumns(savedRnr.getProgram().getId());

    if (savedRnr.getStatus() != SUBMITTED) throw new DataException(RNR_AUTHORIZATION_ERROR);

    savedRnr.copyUserEditableFields(rnr, rnrColumns);

    savedRnr.prepareFor(AUTHORIZED, rnrColumns);

    savedRnr.setSupervisoryNodeId(supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram()).getId());

    update(savedRnr);

    User approver = supervisoryNodeService.getApproverFor(savedRnr.getFacility(), savedRnr.getProgram());
    String msg = (approver == null) ? RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR : RNR_AUTHORIZED_SUCCESSFULLY;
    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage approve(Rnr requisition) {
    Rnr savedRnr = getFullRequisitionById(requisition.getId());

    if (!requisitionPermissionService.hasPermission(requisition.getModifiedBy(), savedRnr, APPROVE_REQUISITION))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    if (!(savedRnr.getStatus() == AUTHORIZED || savedRnr.getStatus() == IN_APPROVAL)) {
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);
    }

    savedRnr.copyApproverEditableFields(requisition);

    savedRnr.calculateForApproval();
    final SupervisoryNode parent = supervisoryNodeService.getParent(savedRnr.getSupervisoryNodeId());
    if (parent == null) {
      return doFinalApproval(savedRnr);
    } else {
      return approveAndAssignToNextSupervisoryNode(savedRnr, parent);
    }
  }

  public Rnr getFullRequisitionById(Integer id) {
    Rnr savedRnr = requisitionRepository.getById(id);
    fillSupportingInfo(savedRnr);
    return savedRnr;
  }

  public List<Rnr> getApprovedRequisitions() {
    List<Rnr> requisitions = requisitionRepository.getApprovedRequisitions();
    fillFacilityPeriodProgram(requisitions.toArray(new Rnr[requisitions.size()]));
    fillSupplyingFacility(requisitions.toArray(new Rnr[requisitions.size()]));
    return requisitions;
  }

  private void fillSupplyingFacility(Rnr... requisitions) {
    for (Rnr requisition : requisitions) {
      if (requisition.getSupplyingFacility() != null) {
        Facility facility = facilityService.getById(requisition.getSupplyingFacility().getId());
        requisition.fillBasicInformationForSupplyingFacility(facility);
      }
    }
  }

  public List<ProcessingPeriod> getAllPeriodsForInitiatingRequisition(Integer facilityId, Integer programId) {
    Date programStartDate = programService.getProgramStartDate(facilityId, programId);
    Rnr lastRequisitionToEnterThePostSubmitFlow = requisitionRepository.getLastRequisitionToEnterThePostSubmitFlow(facilityId, programId);

    Integer periodIdOfLastRequisitionToEnterPostSubmitFlow = lastRequisitionToEnterThePostSubmitFlow == null ? null : lastRequisitionToEnterThePostSubmitFlow.getPeriod().getId();
    return processingScheduleService.getAllPeriodsAfterDateAndPeriod(facilityId, programId, programStartDate, periodIdOfLastRequisitionToEnterPostSubmitFlow);
  }


  public Rnr getRnrForApprovalById(Integer id, Integer userId) {
    Rnr savedRnr = getFullRequisitionById(id);

    if (!requisitionPermissionService.hasPermissionToApprove(userId, savedRnr))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    if (savedRnr.getStatus() == AUTHORIZED) {
      savedRnr.prepareForApproval();
      requisitionRepository.update(savedRnr);
    }

    return savedRnr;
  }

  private Rnr getPreviousRequisition(Rnr requisition) {
    ProcessingPeriod immediatePreviousPeriod = processingScheduleService.getImmediatePreviousPeriod(requisition.getPeriod());
    Rnr previousRequisition = null;
    if (immediatePreviousPeriod != null)
      previousRequisition = requisitionRepository.getRequisition(requisition.getFacility(), requisition.getProgram(), immediatePreviousPeriod);
    return previousRequisition;
  }

  private void validateIfRnrCanBeInitiatedFor(Integer facilityId, Integer programId, Integer periodId) {
    List<ProcessingPeriod> validPeriods = getAllPeriodsForInitiatingRequisition(facilityId, programId);
    if (validPeriods.size() == 0 || !validPeriods.get(0).getId().equals(periodId))
      throw new DataException(RNR_PREVIOUS_NOT_FILLED_ERROR);
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

  private void fillFacilityPeriodProgram(Rnr... requisitions) {
    for (Rnr requisition : requisitions) {
      Facility facility = facilityService.getById(requisition.getFacility().getId());
      ProcessingPeriod period = processingScheduleService.getPeriodById(requisition.getPeriod().getId());
      Program program = programService.getById(requisition.getProgram().getId());

      requisition.fillBasicInformation(facility, program, period);
    }
  }

  private Rnr getLastPeriodsRnr(Rnr requisition) {
    if (requisition == null) return null;

    ProcessingPeriod lastPeriod = processingScheduleService.getImmediatePreviousPeriod(requisition.getPeriod());
    if (lastPeriod == null) return null;

    return requisitionRepository.getRequisition(requisition.getFacility(), requisition.getProgram(), lastPeriod);
  }


  private OpenLmisMessage approveAndAssignToNextSupervisoryNode(Rnr requisition, SupervisoryNode parent) {
    final User nextApprover = supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, requisition.getProgram());
    requisition.setStatus(IN_APPROVAL);
    requisition.setSupervisoryNodeId(parent.getId());
    update(requisition);
    if (nextApprover == null) {
      return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY_WITHOUT_SUPERVISOR);
    }
    return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY);
  }

  private OpenLmisMessage doFinalApproval(Rnr rnr) {
    rnr.setStatus(APPROVED);
    SupervisoryNode supervisoryNode = new SupervisoryNode();
    supervisoryNode.setId(rnr.getSupervisoryNodeId());
    SupplyLine supplyLine = supplyLineService.getSupplyLineBy(supervisoryNode, rnr.getProgram());
    if (supplyLine != null) {
      rnr.setSupplyingFacility(supplyLine.getSupplyingFacility());
    }
    rnr.setSupervisoryNodeId(null);
    update(rnr);
    return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY);
  }

  public List<Rnr> listForApproval(Integer userId) {
    List<RoleAssignment> assignments = roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId);
    List<Rnr> requisitionsForApproval = new ArrayList<>();
    for (RoleAssignment assignment : assignments) {
      final List<Rnr> requisitions = requisitionRepository.getAuthorizedRequisitions(assignment);
      requisitionsForApproval.addAll(requisitions);
    }
    fillFacilityPeriodProgram(requisitionsForApproval.toArray(new Rnr[requisitionsForApproval.size()]));
    return requisitionsForApproval;
  }

  @Transactional
  public void releaseRequisitionsAsOrder(List<Rnr> requisitions, Integer userId) {
    for (Rnr requisition : requisitions) {
      Rnr loadedRequisition = requisitionRepository.getById(requisition.getId());
      OrderBatch orderBatch = createOrderBatch(loadedRequisition, userId);
      loadedRequisition.convertToOrder(orderBatch, userId);
      update(loadedRequisition);
    }
  }

  private void update(Rnr requisition) {
    requisitionRepository.update(requisition);
    requisitionRepository.logStatusChange(requisition);
  }

  public List<Rnr> get(RequisitionSearchCriteria criteria) {
    RequisitionSearchStrategy strategy = requisitionFactory.getSearchStrategy(criteria);
    List<Rnr> requisitions = strategy.search(criteria);
    fillFacilityPeriodProgram(requisitions.toArray(new Rnr[requisitions.size()]));
    return requisitions;
  }

  public List<Rnr> getOrders() {
    List<Rnr> requisitions = requisitionRepository.getByStatus(RELEASED);
    fillFacilityPeriodProgram(requisitions.toArray(new Rnr[requisitions.size()]));
    fillSupplyingFacility(requisitions.toArray(new Rnr[requisitions.size()]));

    return requisitions;
  }

  private OrderBatch createOrderBatch(Rnr requisition, Integer userId) {
    OrderBatch orderBatch = new OrderBatch(requisition.getSupplyingFacility(), userId);
    requisitionRepository.createOrderBatch(orderBatch);
    return orderBatch;
  }

  public Integer getCategoryCount(Rnr requisition, boolean fullSupply) {
    return requisitionRepository.getCategoryCount(requisition, fullSupply);
  }

  public void insertComment(Comment comment) {
    requisitionRepository.insertComment(comment);
  }

  public List getCommentsByRnrId(Integer rnrId) {
    List<Comment> comments = requisitionRepository.getCommentsByRnrID(rnrId);
    for (Comment comment : comments) {
      User user = userService.getById(comment.getAuthor().getId());
      comment.setAuthor(user.basicInformation());
    }
    return comments;
  }
}

