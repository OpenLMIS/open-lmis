/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.MotorbikeProblems;
import org.openlmis.distribution.repository.mapper.FacilityVisitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for facility visit related database operations.
 */

@Repository
public class FacilityVisitRepository {

  @Autowired
  FacilityVisitMapper mapper;

  public FacilityVisit get(FacilityVisit facilityVisit) {
    return retrieveDetails(mapper.getBy(facilityVisit.getFacilityId(), facilityVisit.getDistributionId()));
  }

  public void update(FacilityVisit facilityVisit) {
    updateWithDetails(facilityVisit);
  }

  public FacilityVisit getById(Long facilityVisitId) {
    return retrieveDetails(mapper.getById(facilityVisitId));
  }

  public FacilityVisit getBy(Long facilityId, Long distributionId) {
    return retrieveDetails(mapper.getBy(facilityId, distributionId));
  }

  public List<FacilityVisit> getUnSyncedFacilities(Long distributionId) {
    return retrieveDetails(mapper.getUnSyncedFacilities(distributionId));
  }

  public Integer getUnsyncedFacilityCountForDistribution(Long distributionId) {
    return mapper.getUnsyncedFacilityCountForDistribution(distributionId);
  }

  public List<FacilityVisit> getByDistributionId(Long distributionId) {
    return retrieveDetails(mapper.getByDistributionId(distributionId));
  }

  public FacilityVisit save(FacilityVisit facilityVisit) {
    if (facilityVisit.getId() == null) {
      mapper.insert(facilityVisit);
    } else {
      updateWithDetails(facilityVisit);
    }
    return facilityVisit;
  }

  private List<FacilityVisit> retrieveDetails(List<FacilityVisit> visits) {
    for (FacilityVisit visit : visits) {
      retrieveDetails(visit);
    }

    return visits;
  }

  private FacilityVisit retrieveDetails(FacilityVisit visit) {
    visit.setMotorbikeProblems(mapper.getMotorbikeProblemsByFacilityVisitId(visit.getId()));

    return visit;
  }

  private void updateWithDetails(FacilityVisit visit) {
    MotorbikeProblems motorbikeProblems = visit.getMotorbikeProblems();

    if (motorbikeProblems != null) {
      MotorbikeProblems motorbikeProblemsDB = mapper.getMotorbikeProblemsByFacilityVisitId(visit.getId());

      if (motorbikeProblemsDB == null) {
        motorbikeProblems.setFacilityVisitId(visit.getId());
        motorbikeProblems.setCreatedBy(visit.getCreatedBy());
        motorbikeProblems.setModifiedBy(visit.getModifiedBy());

        mapper.insertMotorbikeProblems(motorbikeProblems);
      } else {
        motorbikeProblemsDB.setFacilityVisitId(motorbikeProblems.getFacilityVisitId());
        motorbikeProblemsDB.setLackOfFundingForFuel(motorbikeProblems.getLackOfFundingForFuel());
        motorbikeProblemsDB.setRepairsSchedulingProblem(motorbikeProblems.getRepairsSchedulingProblem());
        motorbikeProblemsDB.setLackOfFundingForRepairs(motorbikeProblems.getLackOfFundingForRepairs());
        motorbikeProblemsDB.setMissingParts(motorbikeProblems.getMissingParts());
        motorbikeProblemsDB.setOther(motorbikeProblems.getOther());
        motorbikeProblemsDB.setMotorbikeProblemOther(motorbikeProblems.getMotorbikeProblemOther());
        motorbikeProblemsDB.setModifiedBy(visit.getModifiedBy());

        mapper.updateMotorbikeProblems(motorbikeProblemsDB);
      }
    }

    mapper.update(visit);
  }

}
