ALTER TABLE programs_supported
ADD COLUMN reporttypeid integer;

ALTER TABLE programs_supported
ADD COLUMN reportstartdate timestamp without time zone;

ALTER TABLE programs_supported
ADD COLUMN reportactive boolean;

ALTER TABLE programs_supported
ADD CONSTRAINT programs_supported_reports_type_fk foreign key (reporttypeid)
REFERENCES reports_type(id);