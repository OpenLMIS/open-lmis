/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
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
  boolean isSearchable(Right right) {
    Facility facility = new Facility(criteria.getFacilityId());
    Program program = new Program(criteria.getProgramId());

    return requisitionPermissionService.hasPermission(criteria.getUserId(), facility, program, right);
  }

  @Override
  List<Rnr> findRequisitions() {
    Facility facility = new Facility(criteria.getFacilityId());
    Program program = new Program(criteria.getProgramId());
    Date dateRangeStart = criteria.getDateRangeStart();
    Date dateRangeEnd = criteria.getDateRangeEnd();

    List<ProcessingPeriod> periods = processingScheduleService.getAllPeriodsForDateRange(facility, program,
      dateRangeStart, dateRangeEnd);

    return requisitionRepository.getPostSubmitRequisitions(facility, program, periods);
  }
}
