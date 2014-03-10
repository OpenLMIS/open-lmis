/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository;

import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.RefrigeratorProblem;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.mapper.DistributionRefrigeratorsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository class for refrigerator reading and refrigerator problems related database operations.
 */

@Repository
public class DistributionRefrigeratorsRepository {

  @Autowired
  private DistributionRefrigeratorsMapper mapper;


  public void saveReading(RefrigeratorReading reading) {
    mapper.insertReading(reading);

    RefrigeratorProblem problem = reading.getProblem();
    if (problem != null) {
      problem.setReadingId(reading.getId());
      mapper.insertProblem(problem);
    }
  }

  public DistributionRefrigerators getBy(Long facilityVisitId) {
    return new DistributionRefrigerators(mapper.getBy(facilityVisitId));
  }
}
