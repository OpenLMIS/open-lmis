package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.RnrRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;
import static org.openlmis.rnr.domain.RnrStatus.INITIATED;
import static org.openlmis.rnr.domain.RnrStatus.SUBMITTED;

@Service
@NoArgsConstructor
public class RnrService {

  public static final String RNR_AUTHORIZATION_ERROR = "rnr.authorization.error";
  public static final String RNR_SUBMISSION_ERROR = "rnr.submission.error";
  private RnrRepository rnrRepository;
  private RnrTemplateRepository rnrTemplateRepository;

  private FacilityApprovedProductService facilityApprovedProductService;
  private SupervisoryNodeService supervisoryNodeService;
  public static final String RNR_AUTHORIZED_SUCCESSFULLY = "rnr.authorized.success";
  public static final String RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR = "rnr.authorized.success.without.supervisor";

  @Autowired
  public RnrService(RnrRepository rnrRepository, RnrTemplateRepository rnrTemplateRepository, FacilityApprovedProductService facilityApprovedProductService, SupervisoryNodeService supervisoryNodeRepository) {
    this.rnrRepository = rnrRepository;
    this.rnrTemplateRepository = rnrTemplateRepository;
    this.facilityApprovedProductService = facilityApprovedProductService;
    this.supervisoryNodeService = supervisoryNodeRepository;
  }

  @Transactional
  public Rnr initRnr(Integer facilityId, Integer programId, Integer modifiedBy) {
    if (!rnrTemplateRepository.isRnrTemplateDefined(programId))
      throw new DataException("Please contact Admin to define R&R template for this program");
    Rnr requisition = new Rnr(facilityId, programId, modifiedBy);
    List<FacilityApprovedProduct> facilityApprovedProducts = facilityApprovedProductService.getByFacilityAndProgram(facilityId, programId);
    for (FacilityApprovedProduct programProduct : facilityApprovedProducts) {
      RnrLineItem requisitionLineItem = new RnrLineItem(requisition.getId(), programProduct, modifiedBy);
      requisition.add(requisitionLineItem);
    }
    rnrRepository.insert(requisition);
    return requisition;
  }

  public void save(Rnr rnr) {
    rnrRepository.update(rnr);
  }

  public Rnr get(Integer facilityId, Integer programId) {
    return rnrRepository.getRequisitionByFacilityAndProgram(facilityId, programId);
  }

  public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
    return rnrRepository.getLossesAndAdjustmentsTypes();
  }

  public String submit(Rnr rnr) {
    if(rnrRepository.getById(rnr.getId()).getStatus() != INITIATED) throw new DataException(new OpenLmisMessage(RNR_SUBMISSION_ERROR));
    rnr.validate(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId()));
    rnr.setStatus(SUBMITTED);
    rnrRepository.update(rnr);

    SupervisoryNode supervisoryNode = supervisoryNodeService.getFor(rnr.getFacilityId(), rnr.getProgramId());
    if (supervisoryNode == null) {
      return "There is no supervisory node to process the R&R further, Please contact the Administrator";
    }
    return "R&R submitted successfully!";
  }

  public OpenLmisMessage authorize(Rnr rnr) {
    if(rnrRepository.getById(rnr.getId()).getStatus() != SUBMITTED) throw new DataException(RNR_AUTHORIZATION_ERROR);

    rnr.validate(rnrTemplateRepository.isFormulaValidated(rnr.getProgramId()));
    rnr.setStatus(AUTHORIZED);
    rnrRepository.update(rnr);

    User approver = supervisoryNodeService.getApproverFor(rnr.getFacilityId(), rnr.getProgramId());

    if(approver == null)return new OpenLmisMessage(RNR_AUTHORIZED_SUCCESSFULLY_WITHOUT_SUPERVISOR);

    return new OpenLmisMessage(RNR_AUTHORIZED_SUCCESSFULLY);
  }
}

