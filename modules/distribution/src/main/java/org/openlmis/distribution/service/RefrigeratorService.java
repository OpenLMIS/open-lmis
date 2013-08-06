package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.Refrigerator;
import org.openlmis.distribution.repository.RefrigeratorRepository;
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
