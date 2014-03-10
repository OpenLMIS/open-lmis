package org.openlmis.distribution.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.repository.FacilityVisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling FacilityVisit entity.
 */

@Service
public class FacilityVisitService {

  @Autowired
  FacilityVisitRepository repository;

  public FacilityVisit save(FacilityVisit facilityVisit) {
    facilityVisit.setApplicableVisitInfo();
    return repository.save(facilityVisit);
  }

  public FacilityVisit setSynced(FacilityVisit facilityVisit) {
    FacilityVisit existingVisit = repository.getById(facilityVisit.getId());
    if (existingVisit.getSynced()) {
      throw new DataException("error.facility.already.synced");
    }
    facilityVisit.setSynced(true);
    repository.update(facilityVisit);
    return facilityVisit;
  }

  public FacilityVisit getById(Long facilityVisitId) {
    return repository.getById(facilityVisitId);
  }

  public FacilityVisit getBy(Long facilityId, Long distributionId) {
    return repository.getBy(facilityId, distributionId);
  }

  public List<FacilityVisit> getUnSyncedFacilities(Long distributionId) {
    return repository.getUnSyncedFacilities(distributionId);
  }

  public Integer getUnsyncedFacilityCountForDistribution(Long distributionId) {
    return repository.getUnsyncedFacilityCountForDistribution(distributionId);
  }
}
