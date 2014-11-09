package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.email.service.EmailService;
import org.openlmis.report.ReportManager;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.dto.MessageCollection;
import org.openlmis.report.model.dto.MessageDto;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.sms.domain.SMS;
import org.openlmis.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.activation.DataSource;

import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/messages")
public class MessageController extends BaseController{

  @Autowired
  private SMSService smsService;

  @Autowired
  private EmailService emailService;

    @Autowired
    public ReportManager reportManager;

    @Autowired
    private JavaMailSender mailSender;


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
        sms.setPhoneNumber(dto.getContact());
        sms.setDateSaved(new Date());
        sms.setDirection("O");
        smsService.sendAsync(sms);
      }else if( dto.getType().equals("email") ){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getContact());

        //TODO:  make this configurable or let the user write it.

        message.setSubject("Reporting rate notice");
        message.setText(dto.getMessage());
        emailService.send(message);
      }
    }

    return OpenLmisResponse.success("Success");
  }

    @RequestMapping(value = "/send/report", method = POST, headers = BaseController.ACCEPT_JSON)
    public ResponseEntity<OpenLmisResponse> sendWithReportAttachment(
            @RequestBody MessageCollection messageParams,
             HttpServletRequest request
    ) {

        Integer userId = loggedInUserId(request).intValue();

        /** extract message inputs from the payload **/
        String reportKey = messageParams.getReportKey();
        List<MessageDto> messages = messageParams.getMessages();
        String subject = messageParams.getSubject();
        String outputOption = messageParams.getOutputOption();
        Map<String, String[]> reportFilterParams = messageParams.getReportParams();

        /** Export report and process email attachment **/
        ByteArrayOutputStream byteArrayOutputStream = reportManager.exportReportBytesStream(userId, reportKey, reportFilterParams, outputOption);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        DataSource attachmentDataSource;

        switch (outputOption.toUpperCase()) {

            case "XLS":
                attachmentDataSource = new ByteArrayDataSource(bytes, "application/vnd.ms-excel");
                break;
            case "HTML":
                attachmentDataSource = new ByteArrayDataSource(bytes, "application/html");
                break;
            default:
                attachmentDataSource = new ByteArrayDataSource(bytes, "application/pdf");
                break;
        }

        for(MessageDto dto: messages) {

            javax.mail.internet.MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = null;

            try {
                helper = new MimeMessageHelper(message, true);
                helper.setTo(dto.getContact());
                helper.setSubject(subject);
                helper.setText(dto.getMessage());
                helper.addAttachment("Non Reporting facilities.pdf", attachmentDataSource);
                emailService.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        bytes = null;
        byteArrayOutputStream = null;
        attachmentDataSource = null;

        return OpenLmisResponse.success("Success");
    }
}
