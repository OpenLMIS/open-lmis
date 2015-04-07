package org.openlmis.email.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:test-applicationContext-db.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EmailNotificationMapperIT {

  @Autowired
  EmailNotificationMapper mapper;

  @Test
  public void shouldInsert() throws Exception {

    String to = "test@dev.org";
    String content =  "The Test Message";
    String subject = "The subject";
    Integer count = mapper.insert(to, content, subject);
    assertThat(count, is(1));
  }

}