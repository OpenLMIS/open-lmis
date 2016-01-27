/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RequisitionStatusFeedDTO;
import org.openlmis.rnr.service.NotificationServices;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * This class is responsible for generating a feed on the event of change of status of a rnr.
 */

public class RequisitionStatusChangeEvent extends Event {
  static final String FEED_TITLE = "Requisition Status";
  static final String FEED_CATEGORY = "requisition-status";

  NotificationServices notificationService;

  public RequisitionStatusChangeEvent(Rnr requisition, NotificationServices nServices) throws URISyntaxException {
    super(UUID.randomUUID().toString(), FEED_TITLE, DateTime.now(), "",
        new RequisitionStatusFeedDTO(requisition).getSerializedContents(), FEED_CATEGORY);

    if (requisition != null) {
      notificationService = nServices;
      notificationService.notifyStatusChange(requisition);
    }
  }

}
