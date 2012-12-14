package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.repository.mapper.ProgramProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ProgramProductRepository {

  ProgramProductMapper mapper;

  @Autowired
  public ProgramProductRepository(ProgramProductMapper mapper) {
    this.mapper = mapper;
  }

  public List<ProgramProduct> getByFacilityAndProgram(Integer facilityId, String programCode) {
    return mapper.getFullSupplyProductsByFacilityAndProgram(facilityId, programCode);
  }

}
