package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.ProgramRnrTemplate;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.openlmis.core.domain.Right.*;
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
  private RoleRightsService roleRightsService;
  private ProgramService programService;
  private ProcessingScheduleService processingScheduleService;
  private FacilityService facilityService;
  private SupplyLineService supplyLineService;

  @Autowired
  public RequisitionService(RequisitionRepository requisitionRepository, RnrTemplateRepository rnrTemplateRepository,
                            FacilityApprovedProductService facilityApprovedProductService, SupervisoryNodeService supervisoryNodeRepository,
                            RoleRightsService roleRightsService, ProgramService programService,
                            ProcessingScheduleService processingScheduleService, FacilityService facilityService, SupplyLineService supplyLineService) {
    this.requisitionRepository = requisitionRepository;
    this.rnrTemplateRepository = rnrTemplateRepository;
    this.facilityApprovedProductService = facilityApprovedProductService;
    this.supervisoryNodeService = supervisoryNodeRepository;
    this.roleRightsService = roleRightsService;
    this.programService = programService;
    this.processingScheduleService = processingScheduleService;
    this.facilityService = facilityService;
    this.supplyLineService = supplyLineService;
  }

  @Transactional
  public Rnr initiate(Integer facilityId, Integer programId, Integer periodId, Integer modifiedBy) {
    ProgramRnrTemplate rnrTemplate = new ProgramRnrTemplate(programId, rnrTemplateRepository.fetchRnrTemplateColumns(programId));
    if (rnrTemplate.getRnrColumns().size() == 0) throw new DataException(RNR_TEMPLATE_NOT_INITIATED_ERROR);

    validateIfRnrCanBeInitiatedFor(facilityId, programId, periodId);

    List<FacilityApprovedProduct> facilityApprovedProducts = facilityApprovedProductService.getFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId);

    Rnr requisition = new Rnr(facilityId, programId, periodId, facilityApprovedProducts, modifiedBy);

    fillBeginningBalanceFromPreviousRnrIfStockInHandVisible(requisition);

    requisitionRepository.insert(requisition);
    return get(new Facility(facilityId), new Program(programId), new ProcessingPeriod(periodId));
  }

  public void save(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (!isUserAllowedToSave(savedRnr))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    savedRnr.copyUserEditableFieldsForSaveSubmitOrAuthorize(rnr);
    requisitionRepository.update(savedRnr);
  }

  public Rnr get(Facility facility, Program program, ProcessingPeriod period) {
    Rnr requisition = requisitionRepository.getRequisition(facility, program, period);
    return fillSupportingInfo(requisition);
  }

  public Rnr getWithFullSupplyLineItems(Facility facility, Program program, ProcessingPeriod period) {
    Rnr requisition = requisitionRepository.getRequisitionWithFullSupplyLineItems(facility, program, period);
    return fillSupportingInfo(requisition);
  }

  public Rnr getWithNonFullSupplyLineItems(Facility facility, Program program, ProcessingPeriod period) {
    Rnr requisition = requisitionRepository.getRequisitionWithNonFullSupplyLineItems(facility, program, period);
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
    if (savedRnr.getStatus() != INITIATED) {
      throw new DataException(new OpenLmisMessage(RNR_SUBMISSION_ERROR));
    }

    savedRnr.copyUserEditableFieldsForSaveSubmitOrAuthorize(rnr);

    savedRnr.prepareForSubmit();
    savedRnr.validate(rnrTemplateRepository.fetchRnrTemplateColumns(savedRnr.getProgram().getId()));

    requisitionRepository.update(savedRnr);

    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram());
    String msg = (supervisoryNode == null) ? NO_SUPERVISORY_NODE_CONTACT_ADMIN : RNR_SUBMITTED_SUCCESSFULLY;

    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage authorize(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    if (savedRnr.getStatus() != SUBMITTED) throw new DataException(RNR_AUTHORIZATION_ERROR);
    savedRnr.copyUserEditableFieldsForSaveSubmitOrAuthorize(rnr);

    savedRnr.validate(rnrTemplateRepository.fetchRnrTemplateColumns(savedRnr.getProgram().getId()));

    savedRnr.prepareForAuthorize();

    savedRnr.setSupervisoryNodeId(supervisoryNodeService.getFor(savedRnr.getFacility(), savedRnr.getProgram()).getId());

    requisitionRepository.update(savedRnr);

    User approver = supervisoryNodeService.getApproverFor(savedRnr.getFacility(), savedRnr.getProgram());
    String msg = (approver == null) ? RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR : RNR_AUTHORIZED_SUCCESSFULLY;
    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage approve(Rnr rnr) {
    Rnr savedRnr = getFullRequisitionById(rnr.getId());

    savedRnr.copyApproverEditableFields(rnr);
    if (!(savedRnr.getStatus() == AUTHORIZED || savedRnr.getStatus() == IN_APPROVAL))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    savedRnr.calculate(new ArrayList<RnrColumn>());
    final SupervisoryNode parent = supervisoryNodeService.getParent(savedRnr.getSupervisoryNodeId());
    if (parent == null) {
      return doFinalApproval(savedRnr);
    } else {
      return approveAndAssignToNextSupervisoryNode(savedRnr, parent);
    }
  }

  private Rnr getFullRequisitionById(Integer id) {
    Rnr savedRnr = requisitionRepository.getById(id);
    fillFacilityPeriodProgram(savedRnr);
    fillPreviousRequisitionsForAmc(savedRnr);

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
    final Rnr rnr = requisitionRepository.getById(id);
    fillFacilityPeriodProgram(rnr);
    List<RoleAssignment> assignments = roleRightsService.getRoleAssignments(APPROVE_REQUISITION, userId);

    if (!userCanApprove(rnr, assignments)) throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    if (rnr.getStatus() == AUTHORIZED) {
      rnr.prepareForApproval();
      requisitionRepository.update(rnr);
    }

    return rnr;
  }

  private void fillBeginningBalanceFromPreviousRnrIfStockInHandVisible(Rnr requisition) {
      ProcessingPeriod immediatePreviousPeriod = processingScheduleService.getImmediatePreviousPeriod(requisition.getPeriod());
      Rnr previousRequisition = null;
      if (immediatePreviousPeriod != null)
        previousRequisition = requisitionRepository.getRequisition(requisition.getFacility(), requisition.getProgram(), immediatePreviousPeriod);

      requisition.setBeginningBalanceForEachLineItem(previousRequisition);
  }

  private void validateIfRnrCanBeInitiatedFor(Integer facilityId, Integer programId, Integer periodId) {
    List<ProcessingPeriod> validPeriods = getAllPeriodsForInitiatingRequisition(facilityId, programId);
    if (validPeriods.size() == 0 || !validPeriods.get(0).getId().equals(periodId))
      throw new DataException(RNR_PREVIOUS_NOT_FILLED_ERROR);
  }


  private boolean isUserAllowedToSave(Rnr rnr) {
    Set<Right> userRights = roleRightsService.getRights(rnr.getModifiedBy());
    return (rnr.getStatus() == INITIATED && userRights.contains(CREATE_REQUISITION)) ||
        (rnr.getStatus() == SUBMITTED && userRights.contains(AUTHORIZE_REQUISITION)) ||
        (rnr.getStatus() == AUTHORIZED && userRights.contains(APPROVE_REQUISITION)) ||
        (rnr.getStatus() == IN_APPROVAL && userRights.contains(APPROVE_REQUISITION));
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


  private OpenLmisMessage approveAndAssignToNextSupervisoryNode(Rnr rnr, SupervisoryNode parent) {
    final User nextApprover = supervisoryNodeService.getApproverForGivenSupervisoryNodeAndProgram(parent, rnr.getProgram());
    rnr.setStatus(IN_APPROVAL);
    rnr.setSupervisoryNodeId(parent.getId());
    requisitionRepository.update(rnr);
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
    requisitionRepository.update(rnr);
    return new OpenLmisMessage(RNR_APPROVED_SUCCESSFULLY);
  }

  public List<Rnr> listForApproval(Integer userId) {
    List<RoleAssignment> assignments = roleRightsService.getRoleAssignments(APPROVE_REQUISITION, userId);
    List<Rnr> requisitionsForApproval = new ArrayList<>();
    for (RoleAssignment assignment : assignments) {
      final List<Rnr> requisitions = requisitionRepository.getAuthorizedRequisitions(assignment);
      requisitionsForApproval.addAll(requisitions);
    }
    fillFacilityPeriodProgram(requisitionsForApproval.toArray(new Rnr[requisitionsForApproval.size()]));
    return requisitionsForApproval;
  }


  private boolean userCanApprove(final Rnr rnr, List<RoleAssignment> assignments) {
    return CollectionUtils.exists(assignments, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        final RoleAssignment o1 = (RoleAssignment) o;
        return (o1.getSupervisoryNode().getId() == rnr.getSupervisoryNodeId());
      }
    });
  }
}

