package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FacilityVisitService {

  @Autowired
  FacilityVisitRepository repository;

  public boolean save(FacilityVisit facilityVisit) {
    if (repository.get(facilityVisit) != null) {
      return false;
    }
    repository.insert(facilityVisit);
    return true;
  }

}
