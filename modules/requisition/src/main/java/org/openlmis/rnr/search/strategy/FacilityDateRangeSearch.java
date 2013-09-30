/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

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

        List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsForDateRange(facility, program,
          criteria.getDateRangeStart(), criteria.getDateRangeEnd());

        requisitions.addAll(requisitionRepository.getPostSubmitRequisitions(facility, program, periods));
      }
    }

    return requisitions;
  }
}
