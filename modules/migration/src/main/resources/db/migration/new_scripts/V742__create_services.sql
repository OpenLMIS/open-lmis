CREATE TABLE services
(
  id serial NOT NULL,
  code character varying(50) NOT NULL,
  name character varying(50) NOT NULL,
  programid integer NOT NULL,
  active boolean NOT null,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT services_pkey PRIMARY KEY (id),
  CONSTRAINT services_programid_fkey FOREIGN KEY (programid)
      REFERENCES programs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
