
--=== Table: vaccine_dilution --========
DROP TABLE IF EXISTS vaccine_dilution;
CREATE TABLE vaccine_dilution
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100) UNIQUE     NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uc_dilution_lower_name ON vaccine_dilution(LOWER(name));

COMMENT ON INDEX uc_dilution_lower_name IS 'Unique dilution required';
COMMENT ON TABLE vaccine_dilution IS 'dilution';
COMMENT ON COLUMN vaccine_dilution.id IS 'ID';
COMMENT ON COLUMN vaccine_dilution.name IS 'Diluation';
COMMENT ON COLUMN vaccine_dilution.createdBy IS 'Created by';
COMMENT ON COLUMN vaccine_dilution.createdDate IS 'Created on';
COMMENT ON COLUMN vaccine_dilution.modifiedBy IS 'Modified by';
COMMENT ON COLUMN vaccine_dilution.modifiedDate IS 'Modified on';

  -- Add reference data
INSERT INTO vaccine_dilution (id, name) VALUES (1, 'RUPF_Sdilution_5ml');
INSERT INTO vaccine_dilution (id, name) VALUES (2, 'Sdilution_2ml');
INSERT INTO vaccine_dilution (id, name) VALUES (3, 'Sdilution_5ml');
INSERT INTO vaccine_dilution (id, name) VALUES (4, 'Sdilution_10ml');
INSERT INTO vaccine_dilution (id, name) VALUES (5, 'Syringes');


--=== Table: vaccination_types --========
DROP TABLE IF EXISTS vaccination_types;
CREATE TABLE vaccination_types
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100) UNIQUE     NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_vaccination_types_lower_name ON vaccination_types(LOWER(name));

COMMENT ON INDEX uc_vaccination_types_lower_name  IS 'Unique vaccination type required';
COMMENT ON TABLE vaccination_types IS 'Vaccine storage types';
COMMENT ON COLUMN vaccination_types.id IS 'ID';
COMMENT ON COLUMN vaccination_types.name IS 'Vaccination type';
COMMENT ON COLUMN vaccination_types.createdBy IS 'Created by';
COMMENT ON COLUMN vaccination_types.createdDate IS 'Created on';
COMMENT ON COLUMN vaccination_types.modifiedBy IS 'Modified by';
COMMENT ON COLUMN vaccination_types.modifiedDate IS 'Modified on';

INSERT INTO vaccination_types (id, name) VALUES (1, 'Routine');
INSERT INTO vaccination_types (id, name) VALUES (2, 'Supplimentary');
INSERT INTO vaccination_types (id, name) VALUES (3, 'Other Intervention');


--=== Table: vaccine_administration_mode --========
DROP TABLE IF EXISTS vaccine_administration_mode;
CREATE TABLE vaccine_administration_mode
(
  id                SERIAL PRIMARY KEY                            ,
  name              VARCHAR (100) UNIQUE     NOT NULL,
  createdBy         INTEGER                                       ,
  createdDate       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  modifiedBy        INTEGER                                       ,
  modifiedDate      TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uc_administration_mode_lower_name ON vaccine_administration_mode(LOWER(name));

COMMENT ON INDEX uc_administration_mode_lower_name IS 'Unique administration mode required';
COMMENT ON TABLE vaccine_administration_mode IS 'administration_mode';
COMMENT ON COLUMN vaccine_administration_mode.id IS 'ID';
COMMENT ON COLUMN vaccine_administration_mode.name IS 'Administration mode';
COMMENT ON COLUMN vaccine_administration_mode.createdBy IS 'Created by';
COMMENT ON COLUMN vaccine_administration_mode.createdDate IS 'Created on';
COMMENT ON COLUMN vaccine_administration_mode.modifiedBy IS 'Modified by';
COMMENT ON COLUMN vaccine_administration_mode.modifiedDate IS 'Modified on';

-- reference data
INSERT INTO vaccine_administration_mode (id, name) VALUES (1, 'Oral Nasal Injection - ADS 0.05ml');
INSERT INTO vaccine_administration_mode (id, name) VALUES (2, 'Injection - ADS 0.10 ml');
INSERT INTO vaccine_administration_mode (id, name) VALUES (3, 'Injection - ADS 0.50 ml');
