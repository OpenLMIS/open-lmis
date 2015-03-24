CREATE TABLE dosage_frequencies
(
  id SERIAL   NOT NULL,
  name character varying(20),
  numeric_quantity_per_day numeric,
  CONSTRAINT pk_dosage_frequency PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dosage_frequencies
  OWNER TO postgres;
