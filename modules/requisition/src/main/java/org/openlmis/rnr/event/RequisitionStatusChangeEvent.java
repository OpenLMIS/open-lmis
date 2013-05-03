/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrFeedDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;


public class RequisitionStatusChangeEvent extends Event {

  public RequisitionStatusChangeEvent(Rnr requisition) throws URISyntaxException {
    this(UUID.randomUUID().toString(), "Requisition", DateTime.now(), "", RnrFeedDTO.populate(requisition).getSerializedContents(), null);
  }

  public RequisitionStatusChangeEvent(String uuid, String title, DateTime timeStamp, URI uri, String serializedContents, String category) {
    super(uuid, title, timeStamp, uri, serializedContents, category);
  }

  public RequisitionStatusChangeEvent(String uuid, String title, DateTime timeStamp, String uriString, String serializedContents, String category) throws URISyntaxException {
    super(uuid, title, timeStamp, uriString, serializedContents, category);
  }
}
