package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FacilityVisitService {

  public static final String SYNCED_SUCCESSFULLY_STATUS = "Synced";
  public static final String ALREADY_SYNCED_STATUS = "AlreadySynced";
  public static final String SYNC_FAILED_STATUS = "Failed";
  @Autowired
  FacilityVisitRepository repository;

  public String save(FacilityVisit facilityVisit) {
    FacilityVisit existingFacilityVisit = repository.get(facilityVisit);
    if (existingFacilityVisit != null) {
      return ALREADY_SYNCED_STATUS;
    }
    try {
      repository.insert(facilityVisit);
    } catch (Exception exception) {
      return SYNC_FAILED_STATUS;
    }
    return SYNCED_SUCCESSFULLY_STATUS;
  }

}
