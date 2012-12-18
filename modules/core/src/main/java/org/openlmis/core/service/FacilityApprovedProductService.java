package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.repository.FacilityApprovedProductRepository;
import org.openlmis.core.repository.ProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@NoArgsConstructor
public class FacilityApprovedProductService {

    private FacilityApprovedProductRepository repository;

    @Autowired
    public FacilityApprovedProductService(FacilityApprovedProductRepository repository) {
        this.repository = repository;
    }

    public List<FacilityApprovedProduct> getByFacilityAndProgram(Integer facilityId, String programCode) {
        return repository.getByFacilityAndProgram(facilityId, programCode);
    }

}
