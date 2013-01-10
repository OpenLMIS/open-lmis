package org.openlmis.email.service;

import org.openlmis.email.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


  private MailSender mailSender;

  @Autowired
  public EmailService(MailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Value("${mail.sender.from}")
  private String from;

  public void send(EmailMessage emailMessage) {
    try {
      mailSender.send(copyToSimpleMailMessage(emailMessage));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private SimpleMailMessage copyToSimpleMailMessage(EmailMessage message) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
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
