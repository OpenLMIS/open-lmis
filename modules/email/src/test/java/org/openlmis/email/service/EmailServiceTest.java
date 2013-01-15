package org.openlmis.email.service;

import org.junit.Test;
import org.openlmis.email.builder.EmailMessageBuilder;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath*:applicationContext-email.xml")
public class EmailServiceTest {


  @Test
  public void shouldSendEmailMessage() {
    JavaMailSender mailSender = mock(JavaMailSender.class);
    SimpleMailMessage mailMessage = mock(SimpleMailMessage.class);
    EmailService service = new EmailService(mailSender);
   service.setSimpleMailMessage(mailMessage);
    EmailMessage message = make(a(EmailMessageBuilder.defaultEmailMessage,
      with(EmailMessageBuilder.to, "alert.open.lmis@gmail.com")));
    boolean status = false;
    try {
      status = service.send(message).get();
    } catch (Exception e) {
      e.printStackTrace();
    }
    assertTrue(status);
    verify(mailSender).send(any(SimpleMailMessage.class));
    verify(mailMessage).setTo(message.getTo());
    verify(mailMessage).setFrom(message.getFrom());
    verify(mailMessage).setSubject(message.getSubject());
    verify(mailMessage).setText(message.getText());
    verify(mailMessage).setSentDate(message.getSentDate());
    verify(mailMessage).setReplyTo(message.getReplyTo());
  }
}
