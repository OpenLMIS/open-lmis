CREATE TABLE USER_PASSWORD_TOKEN(
   userId                   INTEGER REFERENCES users(id),
   passwordToken            UUID,
   createdDate              TIMESTAMP,
   PRIMARY KEY (userId,passwordToken)
);