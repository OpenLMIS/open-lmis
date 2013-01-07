package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.repository.RnrRepository;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@NoArgsConstructor
public class RnrService {

    private RnrRepository rnrRepository;
    private RnrTemplateRepository rnrTemplateRepository;

    private FacilityApprovedProductService facilityApprovedProductService;

    @Autowired
    public RnrService(RnrRepository rnrRepository, RnrTemplateRepository rnrTemplateRepository, FacilityApprovedProductService facilityApprovedProductService) {
        this.rnrRepository = rnrRepository;
      this.rnrTemplateRepository = rnrTemplateRepository;
      this.facilityApprovedProductService = facilityApprovedProductService;
    }

    @Transactional
    public Rnr initRnr(Integer facilityId, Integer programId, Integer modifiedBy) {
      if(!rnrTemplateRepository.isRnrTemplateDefined(programId)) throw new DataException("Please contact Admin to define R&R template for this program");
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
    return  rnrRepository.getRequisitionByFacilityAndProgram(facilityId, programId);
  }

    public void removeLossAndAdjustment(Integer lossAndAdjustmentId) {
        rnrRepository.removeLossAndAdjustment(lossAndAdjustmentId);
    }

    public List<LossesAndAdjustmentsType> getLossesAndAdjustmentsTypes() {
        return rnrRepository.getLossesAndAdjustmentsTypes();
    }

  public String submit(Rnr rnr) {
    rnr.validate();
    return rnrRepository.submit(rnr);
  }
}

