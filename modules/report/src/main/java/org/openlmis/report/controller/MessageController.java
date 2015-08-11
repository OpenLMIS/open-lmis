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
                //emailService.queueMessage(message);

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
