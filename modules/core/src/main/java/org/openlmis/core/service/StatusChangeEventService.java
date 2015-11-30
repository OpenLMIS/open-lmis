/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.service;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.User;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exposes the services for handling event of change in statuses and then notifying users.
 */

@Service
public class StatusChangeEventService {

  private static Map<String, String> actionUrlMap = new HashMap<>();

  static {
    actionUrlMap.put("SUBMITTED", "public/pages/logistics/rnr/index.html#/create-rnr/{0}/{2}/{1}?supplyType=fullSupply&page=1");
    actionUrlMap.put("AUTHORIZED", "public/pages/logistics/rnr/index.html#/rnr-for-approval/{0}/{1}?supplyType=fullSupply&page=1");
    actionUrlMap.put("IN_APPROVAL", "public/pages/logistics/rnr/index.html#/rnr-for-approval/{0}/{1}?supplyType=fullSupply&page=1");
    actionUrlMap.put("APPROVED", "public/pages/logistics/rnr/index.html#/requisitions-for-convert-to-order?page=1");
    actionUrlMap.put("PACKED", "public/pages/logistics/fulfillment/index.html#/manage-pod-orders");
  }

  private MessageService messageService = MessageService.getRequestInstance();

  @Autowired
  private EmailService emailService;

  @Value("${mail.base.url}")
  private String baseUrl;

  public void notifyUsers(ArrayList<User> users, Long rnrId, Facility facility, Program program, ProcessingPeriod period, String status) {
    String newLine = System.getProperty("line.separator");
    String paragraphSeparator = newLine.concat(newLine);
    List<EmailMessage> mailMessages = new ArrayList<>();
    String actionUrl = baseUrl + actionUrlMap.get(status);
    for (User user : users) {
      EmailMessage mailMessage = new EmailMessage();
      mailMessage.setSubject(messageService.message("msg.email.notification.subject"));
      mailMessage.setTo(user.getEmail());
      mailMessage.setText(messageService.message("msg.email.notification.body", user.getUserName(), paragraphSeparator,
        facility.getName(), period.getName(), user.getUserName(), paragraphSeparator,
        messageService.message(actionUrl, rnrId, program.getId(), facility.getId()), paragraphSeparator));
      mailMessages.add(mailMessage);
    }

    emailService.processEmailsAsync(mailMessages);
  }
}
