CREATE TABLE reports_type
(
  id serial NOT NULL,
  code character varying(50) NOT NULL,
  programid integer NOT NULL,
  name character varying(50) NOT NULL,
  description character varying(50),
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT reports_type_pkey PRIMARY KEY (id),
  CONSTRAINT reports_type_programid_fkey FOREIGN KEY (programid)
      REFERENCES programs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)