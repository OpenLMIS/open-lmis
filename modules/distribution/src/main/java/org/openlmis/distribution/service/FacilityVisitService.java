package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FacilityVisitService {

  @Autowired
  FacilityVisitRepository repository;

  public void save(FacilityVisit facilityVisit) {
    repository.insert(facilityVisit);
  }
}
