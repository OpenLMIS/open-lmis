package org.openlmis.distribution.repository;


import org.openlmis.distribution.domain.Refrigerator;
import org.openlmis.distribution.repository.mapper.RefrigeratorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RefrigeratorRepository {

  @Autowired
  RefrigeratorMapper mapper;

  public List<Refrigerator> getRefrigeratorsForADeliveryZoneAndProgram(Long deliveryZoneId, Long programId) {
    return mapper.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);
  }
}
