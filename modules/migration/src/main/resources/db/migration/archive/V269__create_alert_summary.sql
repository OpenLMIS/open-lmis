
DROP TABLE IF EXISTS alert_summary;

CREATE TABLE alert_summary
(
  id serial NOT NULL,
  statics_value integer,
  description character varying(2000),
  geographiczoneid integer,
  alerttypeid character varying(50),
  programid integer,
  periodid integer,
  productid integer,
  CONSTRAINT alert_summary_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alert_summary
  OWNER TO postgres;