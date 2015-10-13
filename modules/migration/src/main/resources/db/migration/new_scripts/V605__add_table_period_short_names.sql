DROP TABLE IF EXISTS period_short_names;
CREATE TABLE period_short_names
(
  id serial PRIMARY KEY,
  periodid integer,
  name character varying(50) NOT NULL,
  createdby integer,
  createddate timestamp with time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp with time zone DEFAULT now(),
  CONSTRAINT period_short_names_periodId_fkey FOREIGN KEY (periodid)
      REFERENCES processing_periods (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE period_short_names
  OWNER TO postgres;
  
  