/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.service;

import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.exception.DataException;
import org.openlmis.order.domain.Order;
import org.openlmis.order.event.OrderStatusChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

/**
 * Exposes the services for handling events related to Order entity and notifying these events.
 */

@Service
public class OrderEventService {

  @Autowired
  EventService eventService;

  public void notifyForStatusChange(Order order) {
    try {
      eventService.notify(new OrderStatusChangeEvent(order));
    } catch (URISyntaxException e) {
      throw new DataException("error.malformed.uri");
    }
  }
}
