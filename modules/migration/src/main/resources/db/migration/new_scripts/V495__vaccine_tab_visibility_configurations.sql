CREATE TABLE vaccine_ivd_tabs(
  tab              VARCHAR(200) UNIQUE PRIMARY KEY NOT NULL,
  name         VARCHAR(200) NOT NULL,

  createdBy           INT,
  createdDate         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy          INT,
  modifiedDate        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vaccine_ivd_tab_visibilities
(
  id                  SERIAL PRIMARY KEY,
  programId           INT NOT NULL REFERENCES programs(id),
  tab              VARCHAR(200) NOT NULL UNIQUE,
  name                varchar(200) NOT NULL,
  visible             BOOLEAN NOT NULL DEFAULT (true),

  createdBy           INT,
  createdDate         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy          INT,
  modifiedDate        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Master list of tabs
INSERT INTO vaccine_ivd_tabs
(tab, name)
    VALUES
      ('STOCK_STATUS_TAB', 'Stock Status'),
      ('COVERAGE_TAB', 'Coverage'),
      ('VITAMIN_SUPPLEMENTATION_TAB', 'Vitamin Supplementation'),
      ('DISEASE_TRACKING_TAB', 'Disease Tracking'),
      ('AEFI_REPORTING_TAB', 'AEFI Reporting'),
      ('COLD_CHAIN_TAB', 'Cold Chain'),
      ('CAMPAIGN_TAB', 'Campaign'),
      ('TARGET_TAB', 'Target');


