package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FacilityVisitService {

  public static final String SYNCED_SUCCESSFULLY_STATUS = "Synced";
  public static final String ALREADY_SYNCED_STATUS = "AlreadySynced";
  @Autowired
  FacilityVisitRepository repository;

  //TODO return boolean
  //TODO why throw exception?
  public String save(FacilityVisit facilityVisit) {
    FacilityVisit existingFacilityVisit = repository.get(facilityVisit);
    if (existingFacilityVisit != null) {
      return ALREADY_SYNCED_STATUS;
    }
    try {
      repository.insert(facilityVisit);
    } catch (Exception exception) {
      throw exception;
    }
    return SYNCED_SUCCESSFULLY_STATUS;
  }

}
