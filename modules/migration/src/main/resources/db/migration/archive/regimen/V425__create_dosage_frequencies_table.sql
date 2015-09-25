DROP TABLE IF EXISTS  dosage_frequencies;
CREATE TABLE dosage_frequencies
(
  id SERIAL   NOT NULL,
  name character varying(20),
  numericquantityperday numeric,
  CONSTRAINT dosage_frequency_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dosage_frequencies
  OWNER TO postgres;
