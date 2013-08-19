package org.openlmis.rnr.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.service.ApproverService;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.email.service.EmailService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 8/13/13
 * Time: 10:53 AM
 */
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



  public void notifyStatusChange(Rnr requisition, Vendor vendor)   {


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

              emailService.send(message);

            }
         }



    }

}
