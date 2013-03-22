/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.strategy;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;

import java.util.List;

@Data
@NoArgsConstructor
public class FacilityProgramDateRangeSearch implements RequisitionSearchStrategy {

  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;

  public FacilityProgramDateRangeSearch(ProcessingScheduleService processingScheduleService, RequisitionRepository requisitionRepository) {
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
  }

  @Override
  public List<Rnr> search(RequisitionSearchCriteria criteria) {
    Facility facility = new Facility(criteria.getFacilityId());
    Program program = new Program(criteria.getProgramId());
    List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsForDateRange(facility, program,
      criteria.getDateRangeStart(), criteria.getDateRangeEnd());

    return requisitionRepository.get(facility, program, periods);
  }
}
