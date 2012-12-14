package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.ProgramProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@NoArgsConstructor
public class ProgramProductService {

    private ProgramProductRepository repository;

    @Autowired
    public ProgramProductService(ProgramProductRepository repository) {
        this.repository = repository;
    }

    public List<ProgramProduct> getByFacilityAndProgram(Integer facilityId, String programCode) {
        return repository.getByFacilityAndProgram(facilityId, programCode);
    }

}
