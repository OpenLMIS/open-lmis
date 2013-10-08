package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.mapper.FacilityVisitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FacilityVisitRepository {

  @Autowired
  FacilityVisitMapper mapper;

  public void insert(FacilityVisit facilityVisit) {
    mapper.insert(facilityVisit);
  }
}
