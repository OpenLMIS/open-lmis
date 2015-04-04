CREATE TABLE vaccine_vitamins
(
  id SERIAL NOT NULL PRIMARY KEY,
  code varchar(50) NOT NULL UNIQUE,
  name varchar(200) NOT NULL,
  description varchar(5000) NOT NULL,
  displayOrder INTEGER NOT NULL,

  createdBy INTEGER,
  createdDate DATE DEFAULT NOW(),
  modifiedBy INTEGER,
  modifiedDate DATE DEFAULT NOW()
);

CREATE TABLE vaccine_vitamin_supplementation_age_groups
(
  id SERIAL NOT NULL PRIMARY KEY,
  name varchar(200) NOT NULL,
  description varchar(500) NULL,
  displayOrder INTEGER NOT NULL,

  createdBy INTEGER,
  createdDate DATE DEFAULT NOW(),
  modifiedBy INTEGER,
  modifiedDate DATE DEFAULT NOW()
);

CREATE TABLE vaccine_report_vitamin_supplementation_line_items
(
  id SERIAL NOT NULL PRIMARY KEY,
  reportId INTEGER NOT NULL REFERENCES vaccine_reports(id),
  vaccineVitaminId INTEGER NOT NULL REFERENCES vaccine_vitamins (id),
  vitaminAgeGroupId INTEGER NOT NULL REFERENCES vaccine_vitamin_supplementation_age_groups(id),
  vitaminName varchar(100) NOT NULL,
  displayOrder INTEGER NOT NULL,
  maleValue INTEGER NULL,
  femaleValue INTEGER NULL,

  createdBy INTEGER,
  createdDate DATE DEFAULT NOW(),
  modifiedBy INTEGER,
  modifiedDate DATE DEFAULT NOW()
);

-- seed the vaccine vitamin
INSERT INTO vaccine_vitamins
(code, name, description, displayOrder)
VALUES
  ('VIT-A', 'Vitamin A', 'Vitamin A Supplements', 1);

INSERT INTO vaccine_vitamin_supplementation_age_groups
( name, description, displayOrder)
VALUES
  ('9 Months', '0 to 9 months of age', 1),
  ('18 Months', '9 - 18 months of age', 2);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
values ('VACCINE_TAB_VITAMIN_SUPPLEMENTATION_VISIBLE', 'Show Vitamin Supplementation Tab', 'Vaccine', '','true',  'BOOLEAN', 108, false);
