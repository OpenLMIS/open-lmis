package org.openlmis.email.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.domain.OpenlmisEmailMessage;
import org.openlmis.email.repository.mapper.EmailNotificationMapper;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationRepositoryTest {

  @Mock
  EmailNotificationMapper mapper;

  @InjectMocks
  EmailNotificationRepository repository;

  @Test
  public void shouldQueueMessage() throws Exception {
    OpenlmisEmailMessage message = new OpenlmisEmailMessage();
    repository.queueMessage(message);
    verify(mapper).insert(message);
  }
}