package org.openlmis.email.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.repository.mapper.EmailNotificationMapper;
import org.springframework.mail.SimpleMailMessage;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationRepositoryTest {

  @Mock
  EmailNotificationMapper mapper;

  @InjectMocks
  EmailNotificationRepository repository;

  @Test
  public void shouldQueueMessage() throws Exception {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo("test@gmail.com");
    message.setSubject("the subject");
    message.setText("The main message.");
    repository.queueMessage(message);
    verify(mapper).insert(anyString(), anyString(), anyString());
  }
}