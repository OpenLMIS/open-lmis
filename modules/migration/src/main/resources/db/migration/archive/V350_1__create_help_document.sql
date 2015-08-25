-- DROP TABLE elmis_help_document;
DROP TABLE IF EXISTS elmis_help_document cascade;
CREATE TABLE elmis_help_document
(
  id serial NOT NULL,
  document_type character varying(20),
  url character varying(100),
  created_date date,
  modified_date date,
  created_by integer,
  modified_by integer,
  CONSTRAINT elmis_help_document_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE elmis_help_document
  OWNER TO postgres;
