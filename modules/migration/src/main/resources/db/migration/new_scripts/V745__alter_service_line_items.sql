DO
$do$
BEGIN
DROP TABLE IF EXISTS service_line_items;
CREATE TABLE service_line_items
(
  id serial NOT NULL,
  rnrid integer NOT NULL,
  serviceid integer NOT NULL,
  programdatacolumnid integer,
  value integer NOT NULL DEFAULT 0,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT service_line_items_pkey PRIMARY KEY (id),
  CONSTRAINT service_line_items_programdatacolumnid_fkey FOREIGN KEY (programdatacolumnid)
      REFERENCES program_data_columns (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT service_line_items_service_fkey FOREIGN KEY (serviceid)
      REFERENCES services (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT service_line_items_rnr_fkey FOREIGN KEY (rnrid)
      REFERENCES requisitions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
END
$do$
