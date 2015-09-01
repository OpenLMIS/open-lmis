CREATE TABLE vaccine_discarding_reasons
(
  id SERIAL NOT NULL PRIMARY KEY,
  name  VARCHAR(100) NOT NULL,
  requiresExplanation BOOLEAN NOT NULL DEFAULT FALSE,
  displayOrder INTEGER NOT NULL,
  createdBy INTEGER NULL,
  createdDate DATE DEFAULT NOW(),
  modifiedBy INTEGER NULL,
  modifiedDate DATE DEFAULT NOW()
);

ALTER TABLE vaccine_report_logistics_line_items
ADD discardingReasonId INTEGER NULL REFERENCES vaccine_discarding_reasons(id);

ALTER TABLE vaccine_report_logistics_line_items
DROP reasonForDiscardingUnopened;

ALTER TABLE vaccine_report_logistics_line_items
ADD discardingReasonExplanation VARCHAR (500) NULL;


-- seed the reasons
INSERT INTO vaccine_discarding_reasons
(name, requiresExplanation, displayOrder)
VALUES
('Expired', false, 1),
('Broken', false, 2),
('Cold Chain Failure', false, 3),
('Other (Specify)', true, 4);