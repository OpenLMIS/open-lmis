package org.openlmis.email.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.email.builder.EmailMessageBuilder;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-email.xml")
public class EmailServiceTest {



  @Test
  public void shouldSendEmailMessage() {
    JavaMailSender mailSender = mock(JavaMailSender.class);
    EmailService service = new EmailService(mailSender);
    EmailMessage message = make(a(EmailMessageBuilder.defaultEmailMessage,
      with(EmailMessageBuilder.to, "balvindk@thoughtworks.com"), with(EmailMessageBuilder.from, "shibha@abc.com")));
    service.send(message);
    verify(mailSender).send(any(SimpleMailMessage.class));

  }
}
