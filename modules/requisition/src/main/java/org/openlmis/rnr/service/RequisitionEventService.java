/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.service;

import org.ict4h.atomfeed.server.service.EventService;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.email.service.EmailService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.event.RequisitionStatusChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exposes the services for handling event of change in rnr status.
 */

@Service
public class RequisitionEventService {

  private static Map<RnrStatus, String> actionUrlMap = new HashMap<>();

  static {
    actionUrlMap.put(RnrStatus.SUBMITTED, "/public/pages/rnr");
    actionUrlMap.put(RnrStatus.AUTHORIZED, "");
    actionUrlMap.put(RnrStatus.IN_APPROVAL, "");
    actionUrlMap.put(RnrStatus.APPROVED, "");
  }

  @Autowired
  private EventService eventService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private EmailService emailService;

  @Value("${mail.base.url}")
  private String baseUrl;

  public void notifyForStatusChange(Rnr requisition) {
    try {
      eventService.notify(new RequisitionStatusChangeEvent(requisition));
    } catch (URISyntaxException e) {
      throw new DataException("error.malformed.uri");
    }
  }

  public void notifyUsers(Rnr requisition, List<User> users) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setSubject(messageService.message("msg.email.notification.subject"));
    String actionUrl = baseUrl + actionUrlMap.get(requisition.getStatus());
    for (User user : users) {
      mailMessage.setTo(user.getEmail());
      mailMessage.setText(messageService.message("msg.email.notification.body", user.getUserName(),
        requisition.getFacility().getName(), requisition.getPeriod().getName(), user.getUserName(),
        actionUrl));
      emailService.send(mailMessage);
    }
  }
}
