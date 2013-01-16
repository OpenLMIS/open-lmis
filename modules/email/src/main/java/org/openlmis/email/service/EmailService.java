package org.openlmis.email.service;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.exception.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@NoArgsConstructor
public class EmailService {


  private MailSender mailSender;

  @Setter
  private SimpleMailMessage simpleMailMessage;

  @Autowired
  public EmailService(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Value("${mail.sender.from}")
  private String from;

  @Async
  public Future<Boolean> send(EmailMessage emailMessage) {

    mailSender.send(copyToSimpleMailMessage(emailMessage));
    return new AsyncResult(true);
  }

  private SimpleMailMessage copyToSimpleMailMessage(EmailMessage message) {
    if (message.getTo() == null || message.getTo().equals("")) throw new EmailException("Message 'To' not set");

    if (simpleMailMessage == null) {
      simpleMailMessage = new SimpleMailMessage();
    }
    simpleMailMessage.setSubject(message.getSubject());
    simpleMailMessage.setText(message.getText());
    simpleMailMessage.setFrom(message.getFrom() == null ? from : message.getFrom());
    simpleMailMessage.setTo(message.getTo());
    simpleMailMessage.setSentDate(message.getSentDate());
    simpleMailMessage.setReplyTo(message.getReplyTo());
    if (message.getCc() != null) {
      simpleMailMessage.setCc(message.getCc());
    }
    if (message.getBcc() != null) {
      simpleMailMessage.setBcc(message.getBcc());
    }
    return simpleMailMessage;
  }
}
