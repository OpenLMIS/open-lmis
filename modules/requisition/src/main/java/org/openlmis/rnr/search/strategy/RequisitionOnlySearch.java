/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.List;

import static java.util.Arrays.asList;

public class RequisitionOnlySearch extends RequisitionSearchStrategy {
  RequisitionSearchCriteria criteria;

  RequisitionPermissionService requisitionPermissionService;
  RequisitionRepository requisitionRepository;

  public RequisitionOnlySearch(RequisitionSearchCriteria criteria,
                               RequisitionPermissionService requisitionPermissionService,
                               RequisitionRepository requisitionRepository) {
    this.criteria = criteria;
    this.requisitionPermissionService = requisitionPermissionService;
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
    Rnr requisition = requisitionRepository.getRequisitionWithoutLineItems(criteria.getFacilityId(),
      criteria.getProgramId(),
      criteria.getPeriodId());
    return (requisition == null) ? null : asList(requisition);

  }
}
