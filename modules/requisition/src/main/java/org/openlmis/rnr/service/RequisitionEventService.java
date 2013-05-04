/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.service;

import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.VendorService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.event.RequisitionStatusChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Service
public class RequisitionEventService {

  @Autowired
  EventService eventService;

  @Autowired
  VendorService vendorService;

  public void notifyForStatusChange(Rnr requisition) {
    Vendor vendor = vendorService.getByUserId(requisition.getModifiedBy());
    try {
      eventService.notify(new RequisitionStatusChangeEvent(requisition, vendor));
    } catch (URISyntaxException e) {
      throw new DataException("error.malformed.uri");
    }
  }
}
