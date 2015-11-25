package org.openlmis.email.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.is;
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
		EmailMessage message = generateEmailMessage();

		Integer count = mapper.insertEmailMessage(message);
		assertThat(count, is(1));
		assertThat(message.getId() > 0, is(true));
	}

	@Test
	public void shouldInsertEmailAttachment() throws Exception {
		EmailAttachment attachment = generateEmailAttachment();

		Integer count = mapper.insertEmailAttachment(attachment);
		assertThat(count, is(1));
		assertThat(attachment.getId() > 0, is(true));
	}

	@Test
	public void shouldInsertEmailAttachmentRelation() throws Exception {
		EmailAttachment attachment1 = generateEmailAttachment();
		EmailAttachment attachment2 = generateEmailAttachment();
		mapper.insertEmailAttachment(attachment1);
		mapper.insertEmailAttachment(attachment2);

		EmailMessage message = generateEmailMessage();
		mapper.insertEmailMessage(message);

		mapper.insertEmailAttachmentsRelation(message.getId(), attachment1.getId());
		mapper.insertEmailAttachmentsRelation(message.getId(), attachment2.getId());

		List<EmailAttachment> attachmentList = mapper.queryEmailAttachmentsByEmailId(message.getId());
		assertThat(attachmentList.size(), is(2));
	}

	private EmailMessage generateEmailMessage() {
		EmailMessage message = new EmailMessage();
		message.setTo("test@dev.org");
		message.setText("The Test Message");
		message.setSubject("test");
		return message;
	}

	private EmailAttachment generateEmailAttachment() {
		EmailAttachment attachment = new EmailAttachment();
		attachment.setAttachmentName("test file");
		attachment.setAttachmentPath("/path");
		attachment.setAttachmentFileType("application/excel");
		return attachment;
	}

}