ALTER TABLE facility_program_equipments
ALTER COLUMN serialnumber DROP NOT NULL;

ALTER TABLE facility_program_equipments
ALTER COLUMN yearOfInstallation DROP NOT NULL;

ALTER TABLE facility_program_equipments
ALTER COLUMN purchaseprice DROP NOT NULL;

ALTER TABLE facility_program_equipments
ALTER COLUMN replacementrecommended DROP NOT NULL;


ALTER TABLE facility_program_equipments
ALTER COLUMN datelastassessed DROP NOT NULL;

ALTER TABLE facility_program_equipments
ADD capacity INT NULL;

ALTER TABLE facility_program_equipments
ADD minTemperature INT NULL;

ALTER TABLE facility_program_equipments
ADD maxTemperature INT NULL;

ALTER TABLE facility_program_equipments
ADD dimension VARCHAR(60) NULL;

ALTER TABLE facility_program_equipments
ADD accessories VARCHAR(250) NULL;