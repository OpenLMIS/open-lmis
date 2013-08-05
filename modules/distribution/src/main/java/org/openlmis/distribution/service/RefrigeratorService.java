package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.RefrigeratorReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefrigeratorService {

  @Autowired
  RefrigeratorReadingRepository refrigeratorReadingRepository;

  public RefrigeratorReading getByDistribution(Long refrigeratorId, Long distributionId) {
    return refrigeratorReadingRepository.getByDistribution(refrigeratorId, distributionId);
  }

}
