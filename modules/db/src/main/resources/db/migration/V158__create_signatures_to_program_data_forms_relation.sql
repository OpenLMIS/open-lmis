CREATE TABLE program_data_form_signatures (
  signatureId INTEGER REFERENCES signatures(id) NOT NULL,
  programDataFormId INTEGER REFERENCES program_data_forms(id) NOT NULL
);