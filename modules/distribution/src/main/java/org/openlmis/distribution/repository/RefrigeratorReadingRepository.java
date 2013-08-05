package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.mapper.RefrigeratorReadingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RefrigeratorReadingRepository {

  @Autowired
  RefrigeratorReadingMapper mapper;

  public RefrigeratorReading getByDistribution(Long refrigeratorId, Long distributionId) {
    return mapper.getByDistribution(refrigeratorId, distributionId);
  }
}
