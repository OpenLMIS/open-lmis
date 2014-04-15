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
import java.util.ArrayList;
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
    actionUrlMap.put(RnrStatus.SUBMITTED, "public/pages/logistics/rnr/index.html#/create-rnr/{0}/{2}/{1}?supplyType=fullSupply&page=1");
    actionUrlMap.put(RnrStatus.AUTHORIZED, "public/pages/logistics/rnr/index.html#/rnr-for-approval/{0}/{1}?supplyType=fullSupply&page=1");
    actionUrlMap.put(RnrStatus.IN_APPROVAL, "public/pages/logistics/rnr/index.html#/rnr-for-approval/{0}/{1}?supplyType=fullSupply&page=1");
    actionUrlMap.put(RnrStatus.APPROVED, "public/pages/logistics/rnr/index.html#/requisitions-for-convert-to-order?page=1");
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
    String newLine = System.getProperty("line.separator");
    String paragraphSeparator = newLine.concat(newLine);
    List<SimpleMailMessage> mailMessages = new ArrayList<>();
    String actionUrl = baseUrl + actionUrlMap.get(requisition.getStatus());
    for (User user : users) {
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setSubject(messageService.message("msg.email.notification.subject"));
      mailMessage.setTo(user.getEmail());
      mailMessage.setText(messageService.message("msg.email.notification.body", user.getUserName(), paragraphSeparator,
        requisition.getFacility().getName(), requisition.getPeriod().getName(), user.getUserName(), paragraphSeparator,
        messageService.message(actionUrl, requisition.getId(), requisition.getProgram().getId(), requisition.getFacility().getId()), paragraphSeparator));
      mailMessages.add(mailMessage);
    }
    emailService.processEmailsAsync(mailMessages);
  }
}
