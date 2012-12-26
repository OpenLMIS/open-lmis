package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.repository.RnrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@NoArgsConstructor
public class RnrService {

    private RnrRepository rnrRepository;
    private FacilityApprovedProductService facilityApprovedProductService;

    @Autowired
    public RnrService(RnrRepository rnrRepository, FacilityApprovedProductService facilityApprovedProductService) {
        this.rnrRepository = rnrRepository;
        this.facilityApprovedProductService = facilityApprovedProductService;
    }

    @Transactional
    public Rnr initRnr(Integer facilityId, Integer programId, String modifiedBy) {
        Rnr requisition = rnrRepository.getRequisitionByFacilityAndProgram(facilityId, programId);
        if (requisition.getId() == null) {
            requisition = new Rnr(facilityId, programId, RnrStatus.INITIATED, modifiedBy);
            List<FacilityApprovedProduct> facilityApprovedProducts = facilityApprovedProductService.getByFacilityAndProgram(facilityId, programId);
            for (FacilityApprovedProduct programProduct : facilityApprovedProducts) {
                RnrLineItem requisitionLineItem = new RnrLineItem(requisition.getId(), programProduct, modifiedBy);
                requisition.add(requisitionLineItem);
            }
            rnrRepository.insert(requisition);
        }
        return requisition;
    }

    public void save(Rnr rnr) {
        rnrRepository.update(rnr);
    }
}
