/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.commtrack.service;

import lombok.NoArgsConstructor;
import org.openlmis.commtrack.domain.CommtrackReport;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class CommtrackService {

  @Autowired
  RequisitionService requisitionService;

  public Integer submitRequisition(CommtrackReport report) {
    Rnr requisition = requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), report.getUserId());

    requisition.setFullSupplyLineItems(report.getProducts());

    requisitionService.submit(requisition);

    requisitionService.authorize(requisition);

    return requisition.getId();
  }

}
