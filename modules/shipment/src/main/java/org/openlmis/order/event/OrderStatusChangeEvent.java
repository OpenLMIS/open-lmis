/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.event;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderStatusFeedDTO;

import java.net.URISyntaxException;
import java.util.UUID;

/**
 * OrderStatusChangeEvent represents an order status change event.
 */

public class OrderStatusChangeEvent extends Event {
  static final String FEED_TITLE = "Requisition Status";
  static final String FEED_CATEGORY = "requisition-status";

  public OrderStatusChangeEvent(Order order) throws URISyntaxException {
    super(UUID.randomUUID().toString(), FEED_TITLE, DateTime.now(), "",
            new OrderStatusFeedDTO(order).getSerializedContents(), FEED_CATEGORY);
  }

}
