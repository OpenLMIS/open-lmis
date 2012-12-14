package org.openlmis.rnr.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
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
    private ProgramProductService programProductService;

    @Autowired
    public RnrService(RnrRepository rnrRepository, ProgramProductService programProductService) {
        this.rnrRepository = rnrRepository;
        this.programProductService = programProductService;
    }

    @Transactional
    public Rnr initRnr(Integer facilityId, String programCode, String modifiedBy) {
        Rnr requisition = rnrRepository.getRequisitionByFacilityAndProgram(facilityId, programCode);
        if (requisition.getId() == null) {
            requisition = new Rnr(facilityId, programCode, RnrStatus.INITIATED, modifiedBy);
            List<ProgramProduct> programProducts = programProductService.getByFacilityAndProgram(facilityId, programCode);
            for (ProgramProduct programProduct : programProducts) {
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
