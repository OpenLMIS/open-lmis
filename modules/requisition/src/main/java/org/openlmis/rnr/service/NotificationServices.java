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
import org.openlmis.core.domain.User;
import org.openlmis.core.service.ApproverService;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.email.service.EmailService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class NotificationServices {


  @Autowired
  private ConfigurationSettingService configService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private ApproverService approverService;

  @Value("${mail.base.url}")
  String baseURL;



  public void notifyStatusChange(Rnr requisition)   {


        // read from the configuration the email template
        String emailTemplate = configService.getByKey("EMAIL_TEMPLATE_APPROVAL").getValue();

        List<User> users = null;
        // find out which email to send it to
       switch(requisition.getStatus() ){
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
           break;
       }

         if(users != null){
            // iterate through the emails and send the email.
            // replace the template with the message
            for(User user : users){

              SimpleMailMessage message = new SimpleMailMessage();
              String emailMessage = emailTemplate;

              // compse the link to the RnR
              String approvalURL = baseURL + "/public/pages/logistics/rnr/index.html#/rnr-for-approval/" + requisition.getId().toString()
                  + "/"
                  + requisition.getProgram().getId().toString()
                  +"?supplyType=full-supply&page=1" ;

              emailMessage = emailMessage.replaceAll("\\{facility_name\\}", requisition.getFacility().getName());
              emailMessage = emailMessage.replaceAll("\\{approver_name\\}", user.getFirstName() + " " + user.getLastName());
              emailMessage = emailMessage.replaceAll("\\{period\\}", requisition.getPeriod().getName());
              emailMessage = emailMessage.replaceAll("\\{link\\}", approvalURL);

              message.setText(emailMessage);
              message.setSubject(configService.getByKey("EMAIL_SUBJECT_APPROVAL").getValue());
              message.setTo(user.getEmail());

              try {
                emailService.queueMessage(message);
              }catch(Exception exp){
                //TODO: message is not sent ... try to log this error; may be preserve the message in the database to retry later 
              }
            }
         }



    }

}
