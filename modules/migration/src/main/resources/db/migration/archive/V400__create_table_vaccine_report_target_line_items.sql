-- Table: vaccine_report_target_line_items

DROP TABLE IF EXISTS vaccine_report_target_line_items;

CREATE TABLE vaccine_report_target_line_items
(
  id serial NOT NULL,
  reportid integer NOT NULL,
  month0 numeric,
  month12 numeric,
  pregnant numeric,
  outreach numeric,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT vaccine_report_target_line_items_pkey PRIMARY KEY (id),
  CONSTRAINT vaccine_report_target_line_items_reportid_fkey FOREIGN KEY (reportid)
      REFERENCES vaccine_reports (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_report_target_line_items
  OWNER TO openlmis;
