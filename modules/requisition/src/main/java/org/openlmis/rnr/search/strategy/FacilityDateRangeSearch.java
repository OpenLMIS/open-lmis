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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.core.domain.RightName.VIEW_REQUISITION;

/**
 * This class is a strategy to search for requisitions based on facility and date range.
 */

@NoArgsConstructor
public class FacilityDateRangeSearch extends RequisitionSearchStrategy {

  private RequisitionSearchCriteria criteria;
  private RequisitionPermissionService requisitionPermissionService;
  private ProgramService programService;
  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;

  public FacilityDateRangeSearch(RequisitionSearchCriteria criteria,
                                 RequisitionPermissionService requisitionPermissionService,
                                 ProcessingScheduleService processingScheduleService,
                                 RequisitionRepository requisitionRepository,
                                 ProgramService programService) {

    this.criteria = criteria;
    this.requisitionPermissionService = requisitionPermissionService;
    this.programService = programService;
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  List<Rnr> findRequisitions() {
    Long facilityId = criteria.getFacilityId();
    Facility facility = new Facility(facilityId);
    Long userId = criteria.getUserId();
    List<Rnr> requisitions = new ArrayList<>();

    List<Program> programs = programService.getProgramsForUserByFacilityAndRights(facilityId, userId, VIEW_REQUISITION);

    for (Program program : programs) {
      if (requisitionPermissionService.hasPermission(userId, facility, program, VIEW_REQUISITION)) {

        List<ProcessingPeriod> periods = processingScheduleService.getUsedPeriodsForDateRange(facility, program,
          criteria.getRangeStart(), criteria.getRangeEnd());

        requisitions.addAll(requisitionRepository.getPostSubmitRequisitions(facility, program, periods));
      }
    }

    return requisitions;
  }
}
