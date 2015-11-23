CREATE TABLE email_attachment (
  id  SERIAL PRIMARY KEY,
  emailId INTEGER NOT NULL REFERENCES email_notifications(id),
  attachmentName VARCHAR(255) NOT NULL,
  attachmentPath VARCHAR(510) NOT NULL,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX i_email_attachment_emailId ON email_attachment(emailId);
