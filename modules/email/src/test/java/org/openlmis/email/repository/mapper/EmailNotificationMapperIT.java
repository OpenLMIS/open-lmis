package org.openlmis.email.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


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
		String content = "The Test Message";
		String subject = "The subject";
		Integer count = mapper.insert(to, content, subject, false);
		assertThat(count, is(1));
	}

	@Test
	public void shouldReturnIdAfterInsertEmailMessage() throws Exception {
		EmailMessage message = new EmailMessage();
		message.setTo("test@dev.org");
		message.setText("The Test Message");
		message.setSubject("test");

		Integer count = mapper.insertEmailMessage(message);
		assertThat(count, is(1));
		assertThat(message.getId()>0, is(true));

	}

}