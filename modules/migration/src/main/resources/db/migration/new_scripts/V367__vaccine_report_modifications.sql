ALTER TABLE vaccine_report_logistics_line_items
ADD productCategory VARCHAR (200) NULL;
DROP TABLE IF EXISTS vaccine_report_cold_chain_line_items;

CREATE TABLE vaccine_report_cold_chain_line_items
(
  id                  SERIAL PRIMARY KEY,
  reportId            INTEGER NOT NULL REFERENCES vaccine_reports(id),
  equipmentInventoryId INTEGER NOT NULL REFERENCES facility_program_equipments(id),
  minTemp             DECIMAL NULL,
  maxTemp             DECIMAL NULL,
  minEpisodeTemp      DECIMAL NULL,
  maxEpisodeTemp      DECIMAL NULL,
  remarks             VARCHAR (2000),

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);


DROP TABLE IF EXISTS vaccine_report_campaign_line_items;

CREATE TABLE vaccine_report_campaign_line_items
(
  id                  SERIAL PRIMARY KEY,
  reportId            INTEGER NOT NULL REFERENCES vaccine_reports(id),
  name                VARCHAR (200) NOT NULL,
  venue               VARCHAR (200) NULL,
  startDate           DATE NULL,
  endDate             DATE NULL,
  childrenVaccinated  INTEGER NULL,
  pregnantWomanVaccinated INTEGER NULL,
  otherObjectives     VARCHAR (2000) NULL,
  vaccinated          VARCHAR (200) NULL,
  remarks             VARCHAR (2000) NULL,

  createdBy         INTEGER,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);