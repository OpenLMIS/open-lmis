/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ConfigurationSettingKey;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ApproverService;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.email.service.EmailService;
import org.openlmis.rnr.domain.Rnr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class NotificationServices {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServices.class);

  @Value("${mail.base.url}")
  String baseURL;

  @Autowired
  private ConfigurationSettingService configService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private ApproverService approverService;

  @Autowired
  private RequisitionEmailServiceForSIMAM requisitionEmailServiceForSIMAM;

  @Autowired
  private StaticReferenceDataService staticReferenceDataService;

  public void notifyStatusChange(Rnr requisition) {


    List<User> users = null;
    // find out which email to send it to
    switch (requisition.getStatus()) {
      // this order has been submitted
      case SUBMITTED:
        // all that can fill for the facility
        users = approverService.getFacilityBasedAutorizers(requisition.getId());
        break;
      case AUTHORIZED:
        users = approverService.getNextApprovers(requisition.getId());
        break;
      case IN_APPROVAL:
        users = approverService.getNextApprovers(requisition.getId());
        break;
      case RELEASED:
      default:
        break;
    }

    if (users != null) {

      if (staticReferenceDataService.getBoolean("toggle.email.attachment.simam")) {
        //catch all the issues when creating file
        try {
          requisitionEmailServiceForSIMAM.sendRequisitionEmailWithAttachment(requisition, users);
        } catch (Throwable t) {
          LOGGER.error("There is a error when creating requisition email: " + t.getMessage());
        }
        return;
      }

      for (User user : users) {
        if (user.isMobileUser()) {
          continue;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        String emailMessage = configService.getByKey(ConfigurationSettingKey.EMAIL_TEMPLATE_APPROVAL).getValue();

        String approvalURL = String.format("%1$s/public/pages/logistics/rnr/index.html#/rnr-for-approval/%2$s/%3$s?supplyType=full-supply&page=1", baseURL, requisition.getId(), requisition.getProgram().getId());

        emailMessage = emailMessage.replaceAll("\\{facility_name\\}", requisition.getFacility().getName());
        emailMessage = emailMessage.replaceAll("\\{approver_name\\}", user.getFirstName() + " " + user.getLastName());
        emailMessage = emailMessage.replaceAll("\\{period\\}", requisition.getPeriod().getName());
        emailMessage = emailMessage.replaceAll("\\{link\\}", approvalURL);

        message.setText(emailMessage);
        message.setSubject(configService.getByKey(ConfigurationSettingKey.EMAIL_SUBJECT_APPROVAL).getValue());
        message.setTo(user.getEmail());

        try {
          emailService.queueMessage(message);
        } catch (Exception exp) {
          LOGGER.error("Notification was not sent due to the following exception ...", exp);
        }
      }
    }
  }

}
