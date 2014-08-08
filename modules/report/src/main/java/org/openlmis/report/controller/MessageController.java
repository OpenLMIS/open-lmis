package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.email.service.EmailService;
import org.openlmis.report.model.dto.MessageCollection;
import org.openlmis.report.model.dto.MessageDto;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.sms.domain.SMS;
import org.openlmis.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/messages")
public class MessageController extends BaseController{

  @Autowired
  private SMSService smsService;

  @Autowired
  private EmailService emailService;


  @RequestMapping(value = "/send", method = POST, headers = BaseController.ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> send(
     @RequestBody MessageCollection messages,
      HttpServletRequest request
  ) {
    Long userId = loggedInUserId(request);


    for(MessageDto dto: messages.getMessages()){

      if(dto.getType().equals("sms")){
        SMS sms = new SMS();
        sms.setMessage(dto.getMessage());
        sms.setPhoneNumber(dto.getAddress());
        sms.setDateSaved(new Date());
        sms.setDirection("O");
        smsService.sendAsync(sms);
      }else if( dto.getType().equals("email") ){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getAddress());

        //TODO:  make this configurable or let the user write it.

        message.setSubject("Reporting rate notice");
        message.setText(dto.getMessage());
        emailService.send(message);
      }
    }

    return OpenLmisResponse.success("Success");
  }

}
