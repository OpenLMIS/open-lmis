/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.factory;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.searchCriteria.RequisitionSearchCriteria;
import org.openlmis.rnr.strategy.FacilityDateRangeSearch;
import org.openlmis.rnr.strategy.FacilityProgramDateRangeSearch;
import org.openlmis.rnr.strategy.RequisitionSearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class RequisitionSearchStrategyFactory {

  private ProcessingScheduleService processingScheduleService;
  private RequisitionRepository requisitionRepository;
  private ProgramService programService;

  @Autowired
  public RequisitionSearchStrategyFactory(ProcessingScheduleService processingScheduleService, RequisitionRepository requisitionRepository, ProgramService programService) {
    this.processingScheduleService = processingScheduleService;
    this.requisitionRepository = requisitionRepository;
    this.programService = programService;
  }

  public RequisitionSearchStrategy getSearchStrategy(RequisitionSearchCriteria criteria) {
    if (criteria.getProgramId() == null) {
      return new FacilityDateRangeSearch(processingScheduleService, requisitionRepository, programService);
    }
    return new FacilityProgramDateRangeSearch(processingScheduleService, requisitionRepository);

  }
}
