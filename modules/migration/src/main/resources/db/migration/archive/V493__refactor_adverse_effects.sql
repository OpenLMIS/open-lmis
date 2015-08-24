ALTER TABLE vaccine_report_adverse_effect_line_items
  DROP COLUMN manufacturerId;

ALTER TABLE vaccine_report_adverse_effect_line_items
  DROP COLUMN investigation;

ALTER TABLE vaccine_report_adverse_effect_line_items
  ADD manufacturer VARCHAR (200) NULL;

ALTER TABLE vaccine_report_adverse_effect_line_items
  ADD isInvestigated BOOLEAN NOT NULL DEFAULT (FALSE);