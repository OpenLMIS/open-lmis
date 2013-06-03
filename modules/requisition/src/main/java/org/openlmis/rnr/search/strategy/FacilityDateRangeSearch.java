/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
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

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

@NoArgsConstructor
public class FacilityDateRangeSearch implements RequisitionSearchStrategy {

  private RequisitionSearchCriteria criteria;
  private ProgramService programService;
  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;

  public FacilityDateRangeSearch(RequisitionSearchCriteria criteria, ProcessingScheduleService processingScheduleService, RequisitionRepository requisitionRepository, ProgramService programService) {
    this.criteria = criteria;
    this.programService = programService;
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  public List<Rnr> search() {
    Facility facility = new Facility(criteria.getFacilityId());
    List<Program> programs = programService.getProgramsForUserByFacilityAndRights(criteria.getFacilityId(),
        criteria.getUserId(), VIEW_REQUISITION);
    List<Rnr> requisitions = new ArrayList<>();
    for (Program program : programs) {
      List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsForDateRange(facility, program,
          criteria.getDateRangeStart(), criteria.getDateRangeEnd());
      requisitions.addAll(requisitionRepository.get(facility, program, periods));
    }
    return requisitions;
  }
}
