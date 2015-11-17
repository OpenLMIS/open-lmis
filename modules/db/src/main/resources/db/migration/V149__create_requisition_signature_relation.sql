CREATE TABLE requisition_signatures (
  signatureId INTEGER REFERENCES signatures(id) NOT NULL,
  rnrId INTEGER REFERENCES requisitions(id) NOT NULL
);