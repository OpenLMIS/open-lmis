/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.service;

import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequisitionFeedEventService extends EventServiceImpl {

  public RequisitionFeedEventService() {
    super(null);
  }

  @Autowired
  public RequisitionFeedEventService(AllEventRecords allEventRecords) {
    super(allEventRecords);
  }
}
