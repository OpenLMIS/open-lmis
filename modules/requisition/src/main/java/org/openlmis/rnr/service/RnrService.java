package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.*;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.RnrRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.openlmis.core.domain.Right.APPROVE_REQUISITION;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.rnr.domain.RnrStatus.*;

@Service
@NoArgsConstructor
public class RnrService {

  public static final String RNR_AUTHORIZATION_ERROR = "rnr.authorization.error";
  public static final String RNR_SUBMISSION_ERROR = "rnr.submission.error";
  public static final String RNR_OPERATION_UNAUTHORIZED = "rnr.operation.unauthorized";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY = "rnr.authorized.success";
  public static final String RNR_SUBMITTED_SUCCESSFULLY = "rnr.submitted.success";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "rnr.authorized.without.supervisor";
  public static final String NO_SUPERVISORY_NODE_CONTACT_THE_ADMINISTRATOR = "rnr.submitted.without.supervisor";

  private RnrRepository rnrRepository;

  private RnrTemplateRepository rnrTemplateRepository;
  private FacilityApprovedProductService facilityApprovedProductService;
  private SupervisoryNodeService supervisoryNodeService;
  private RoleRightsService roleRightsService;
  private FacilityService facilityService;
  private ProgramService programService;

  @Autowired
  public RnrService(RnrRepository rnrRepository, RnrTemplateRepository rnrTemplateRepository, FacilityApprovedProductService facilityApprovedProductService,
                    SupervisoryNodeService supervisoryNodeRepository, RoleRightsService roleRightsService, FacilityService facilityService, ProgramService programService) {
    this.rnrRepository = rnrRepository;
    this.rnrTemplateRepository = rnrTemplateRepository;
    this.facilityApprovedProductService = facilityApprovedProductService;
    this.supervisoryNodeService = supervisoryNodeRepository;
    this.roleRightsService = roleRightsService;
    this.facilityService = facilityService;
    this.programService = programService;
  }

  @Transactional
  public Rnr initRnr(Integer facilityId, Integer programId, Integer periodId, Integer modifiedBy) {
    if (!rnrTemplateRepository.isRnrTemplateDefined(programId))
      throw new DataException("Please contact Admin to define R&R template for this program");
    Rnr requisition = new Rnr(facilityId, programId, periodId, modifiedBy);
    List<FacilityApprovedProduct> facilityApprovedProducts = facilityApprovedProductService.getByFacilityAndProgram(facilityId, programId);
    for (FacilityApprovedProduct programProduct : facilityApprovedProducts) {
      RnrLineItem requisitionLineItem = new RnrLineItem(requisition.getId(), programProduct, modifiedBy);
      requisition.add(requisitionLineItem);
    }
    rnrRepository.insert(requisition);
    return requisition;
  }

  public void save(Rnr rnr) {
    if (!isUserAllowedToSave(rnr))
      throw new DataException(RNR_OPERATION_UNAUTHORIZED);

    rnrRepository.update(rnr);
  }

  private boolean isUserAllowedToSave(Rnr rnr) {
    return (rnr.getStatus() == INITIATED && roleRightsService.getRights(rnr.getModifiedBy()).contains(CREATE_REQUISITION)) ||
        (rnr.getStatus() == SUBMITTED && roleRightsService.getRights(rnr.getModifiedBy()).contains(AUTHORIZE_REQUISITION));
  }

  public Rnr get(Integer facilityId, Integer programId, Integer periodId) {
    return rnrRepository.getRequisition(facilityId, programId, periodId);
  }

  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return rnrRepository.getLossesAndAdjustmentsTypes();
  }

  public OpenLmisMessage submit(Rnr rnr) {
    if (rnrRepository.getById(rnr.getId()).getStatus() != INITIATED) {
      throw new DataException(new OpenLmisMessage(RNR_SUBMISSION_ERROR));
    }
    rnr.validate(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId()));
    rnr.calculate();
    rnr.setStatus(SUBMITTED);
    rnrRepository.update(rnr);

    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId());
    String msg = (supervisoryNode == null) ? NO_SUPERVISORY_NODE_CONTACT_THE_ADMINISTRATOR : RNR_SUBMITTED_SUCCESSFULLY;
    return new OpenLmisMessage(msg);
  }

  public OpenLmisMessage authorize(Rnr rnr) {
    if (rnrRepository.getById(rnr.getId()).getStatus() != SUBMITTED) throw new DataException(RNR_AUTHORIZATION_ERROR);

    rnr.validate(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId()));
    rnr.calculate();
    rnr.setStatus(AUTHORIZED);
    rnrRepository.update(rnr);

    User approver = supervisoryNodeService.getApproverFor(rnr.getFacilityId(), rnr.getProgramId());
    String msg = (approver == null) ? RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR : RNR_AUTHORIZED_SUCCESSFULLY;
    return new OpenLmisMessage(msg);
  }

  public List<Rnr> fetchUserSupervisedRnrForApproval(Integer userId) {
    List<Program> programs = programService.getActiveProgramsForUserWithRights(userId, APPROVE_REQUISITION);
    Set<Facility> facilities = new HashSet<>();
    for(Program program : programs){
      facilities.addAll(facilityService.getUserSupervisedFacilities(userId, program.getId(), APPROVE_REQUISITION));
    }
    return rnrRepository.getSubmittedRequisitionsForFacilitiesAndPrograms(new ArrayList<>(facilities), programs);
  }
}

