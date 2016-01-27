/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.search.strategy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.Date;
import java.util.List;

/**
 * This class is a strategy to search for requisitions based on facility, program and date range.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FacilityProgramDateRangeSearch extends RequisitionSearchStrategy {

  private RequisitionSearchCriteria criteria;
  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;
  private RequisitionPermissionService requisitionPermissionService;

  public FacilityProgramDateRangeSearch(RequisitionSearchCriteria criteria,
                                        RequisitionPermissionService requisitionPermissionService,
                                        ProcessingScheduleService processingScheduleService,
                                        RequisitionRepository requisitionRepository) {
    this.criteria = criteria;

    this.requisitionPermissionService = requisitionPermissionService;
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  boolean isSearchable(String rightName) {
    Facility facility = new Facility(criteria.getFacilityId());
    Program program = new Program(criteria.getProgramId());

    return requisitionPermissionService.hasPermission(criteria.getUserId(), facility, program, rightName);
  }

  @Override
  List<Rnr> findRequisitions() {
    Facility facility = new Facility(criteria.getFacilityId());
    Program program = new Program(criteria.getProgramId());
    Date dateRangeStart = criteria.getRangeStart();
    Date dateRangeEnd = criteria.getRangeEnd();

    List<ProcessingPeriod> periods = processingScheduleService.getUsedPeriodsForDateRange(facility, program,
      dateRangeStart, dateRangeEnd);

    return requisitionRepository.getPostSubmitRequisitions(facility, program, periods);
  }
}
