package org.openlmis.core.service;

import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.repository.RefrigeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefrigeratorService {

  @Autowired
  RefrigeratorRepository repository;

  public List<Refrigerator> getRefrigeratorsForADeliveryZoneAndProgram(Long deliveryZoneId, Long programId) {
    return repository.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);
  }

}
