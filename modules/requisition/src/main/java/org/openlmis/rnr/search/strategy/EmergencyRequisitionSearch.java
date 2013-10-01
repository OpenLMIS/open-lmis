/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.search.strategy;

import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.RequisitionRepository;
import org.openlmis.rnr.search.criteria.RequisitionSearchCriteria;
import org.openlmis.rnr.service.RequisitionPermissionService;

import java.util.List;

public class EmergencyRequisitionSearch extends RequisitionOnlySearch {

  public EmergencyRequisitionSearch(RequisitionSearchCriteria criteria,
                                    RequisitionPermissionService requisitionPermissionService,
                                    RequisitionRepository requisitionRepository) {
    super(criteria, requisitionPermissionService, requisitionRepository);
  }

  @Override
  List<Rnr> findRequisitions() {
    return requisitionRepository.getInitiatedEmergencyRequisition(criteria.getFacilityId(), criteria.getProgramId());
  }
}
