CREATE TABLE email_attachments (
  id  SERIAL PRIMARY KEY,
  attachmentName VARCHAR(255) NOT NULL,
  attachmentPath VARCHAR(510) NOT NULL,
  attachmentFileType VARCHAR(255) NOT NULL,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE email_attachments_relation (
  emailId INTEGER NOT NULL REFERENCES email_notifications(id),
  attachmentId INTEGER NOT NULL REFERENCES email_attachments(id)
);

CREATE INDEX i_email_attachment_emailId ON email_attachments_relation(emailId);