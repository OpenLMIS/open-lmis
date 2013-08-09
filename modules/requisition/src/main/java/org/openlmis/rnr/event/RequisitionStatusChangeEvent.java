/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.core.domain.Vendor;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrFeedDTO;

import java.net.URISyntaxException;
import java.util.UUID;


public class RequisitionStatusChangeEvent extends Event {

  public RequisitionStatusChangeEvent(Rnr requisition, Vendor vendor) throws URISyntaxException {
    super(UUID.randomUUID().toString(), "Requisition", DateTime.now(), "", RnrFeedDTO.populate(requisition, vendor).getSerializedContents(), "requisition");
  }

}
