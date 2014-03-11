/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.search.factory;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.search.strategy.*;
import org.openlmis.rnr.service.RequisitionPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for generating appropriate strategy, based on RequisitionSearchCriteria supplied, to search
 * for rnr in database.
 */

@NoArgsConstructor
@Component
public class RequisitionSearchStrategyFactory {

  @Autowired
  private ProcessingScheduleService scheduleService;

  @Autowired
  private RequisitionRepository repository;

  @Autowired
  private ProgramService programService;

  @Autowired
  private RequisitionPermissionService permissionService;


  public RequisitionSearchStrategy getSearchStrategy(RequisitionSearchCriteria criteria) {

    if (criteria.isEmergency()) {
      return new EmergencyRequisitionSearch(criteria, permissionService, repository);
    } else if (criteria.isWithoutLineItems()) {
      return new RequisitionOnlySearch(criteria, permissionService, repository);
    } else if (criteria.getProgramId() == null) {
      return new FacilityDateRangeSearch(criteria, permissionService, scheduleService, repository, programService);
    } else {
      return new FacilityProgramDateRangeSearch(criteria, permissionService, scheduleService, repository);
    }
  }
}
