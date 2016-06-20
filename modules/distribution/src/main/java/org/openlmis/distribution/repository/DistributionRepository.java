/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.distribution.repository;

import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.DistributionStatus;
import org.openlmis.distribution.repository.mapper.DistributionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository class for distribution related database operations.
 */

@Repository
public class DistributionRepository {

  @Autowired
  DistributionMapper mapper;

  public Distribution create(Distribution distribution) {
    distribution.setStatus(DistributionStatus.INITIATED);
    mapper.insert(distribution);
    return distribution;
  }

  public Distribution get(Distribution distribution) {
    return mapper.get(distribution);
  }

  public void updateDistributionStatus(Long distributionId, DistributionStatus status, Long modifiedBy) {
    mapper.updateDistributionStatus(distributionId, status, modifiedBy);

    if (status == DistributionStatus.SYNCED) {
      mapper.updateSyncDate(distributionId);
    }
  }


  public List<Long> getSyncedPeriodsForDeliveryZoneAndProgram(Long zoneId, Long programId) {
    return mapper.getSyncedPeriodsForDeliveryZoneAndProgram(zoneId, programId);
  }

  public Distribution getBy(Long distributionId) {
    return mapper.getBy(distributionId);
  }

  public Distribution getFullSyncedDistribution(Distribution distribution) {
    return mapper.getFullSyncedDistribution(distribution);
  }

  public List<Distribution> getFullSyncedDistributions() {
    return mapper.getFullSyncedDistributions();
  }

  public List<Distribution> getFullSyncedDistributions(Program program, DeliveryZone deliveryZone, ProcessingPeriod period) {
    if (null != deliveryZone && null != period) {
      return mapper.getFullSyncedDistributionsForProgramAndDeliveryZoneAndPeriod(program.getId(), deliveryZone.getId(), period.getId());
    }

    if (null != deliveryZone) {
      return mapper.getFullSyncedDistributionsForProgramAndDeliveryZone(program.getId(), deliveryZone.getId());
    }

    if (null != period) {
      return mapper.getFullSyncedDistributionsForProgramAndPeriod(program.getId(), period.getId());
    }

    return mapper.getFullSyncedDistributionsForProgram(program.getId());
  }
}
