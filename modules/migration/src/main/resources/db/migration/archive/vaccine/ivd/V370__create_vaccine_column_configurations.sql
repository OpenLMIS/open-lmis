CREATE TABLE vaccine_logistics_master_columns
(
  id                SERIAL PRIMARY KEY,
  name              VARCHAR (200) NOT NULL,
  description       VARCHAR (200) NOT NULL,
  label             VARCHAR (200) NOT NULL,
  indicator         VARCHAR (20) NOT NULL,
  displayOrder      INT NOT NULL,
  mandatory         BOOLEAN NOT NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE vaccine_program_logistics_columns
(
  id                SERIAL PRIMARY KEY,
  programId         INT NOT NULL REFERENCES programs(id),
  masterColumnId    INT NOT NULL REFERENCES vaccine_logistics_master_columns(id),
  label             VARCHAR (200) NOT NULL,
  displayOrder      INT NOT NULL,
  visible           BOOLEAN NOT NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO vaccine_logistics_master_columns
    (name,description,label,indicator,displayOrder, mandatory)
    VALUES
    ('skipped', 'Skipped', 'Skipped', 'A',1, TRUE),
    ('productName', 'Product Name', 'Product Name', 'B',2, TRUE),
    ('openingBalance', 'Opening Balance', 'Opening Balance', 'C',3, FALSE),
    ('quantityReceived', 'Received', 'Received', 'D',4, FALSE),
    ('quantityIssued', 'Issued', 'Issued', 'E',5, FALSE),
    ('closingBalance', 'Closing Balance', 'Closing Balance', 'F',6, FALSE),
    ('quantityVvmAlerted', 'VVM Alerted', 'VVM Alerted', 'G',7, FALSE),
    ('quantityFreezed', 'Freezed', 'Freezed', 'H',8, FALSE),
    ('quantityExpired', 'Expired', 'Expired', 'I',9, FALSE),
    ('quantityOtherWasted', 'Other', 'Other', 'J',10, FALSE),
    ('quantityDiscardedUnopened', 'Discarded Un Opened', 'Discarded Un Opened', 'K',11, FALSE),
    ('quantityDiscardedOpened', 'Discarded Opened', 'Discarded Opened', 'L',12, FALSE);

ALTER TABLE vaccine_report_logistics_line_items
ADD closingBalance INT NULL;
