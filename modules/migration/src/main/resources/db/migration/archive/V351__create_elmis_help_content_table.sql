-- Table: elmis_help

DROP  TABLE IF EXISTS elmis_help cascade;

CREATE TABLE elmis_help
(
  name character varying(500),
  modifiedby integer,
  htmlcontent character varying(2000),
  imagelink character varying(100),
  createddate date,
  id serial NOT NULL,
  createdby integer,
  modifieddate date,
  helptopicid integer,
  CONSTRAINT elmis_help_pkey PRIMARY KEY (id),
  CONSTRAINT elmis_help_helptopicid_fkey FOREIGN KEY (helptopicid)
      REFERENCES elmis_help_topic (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_user_help_modifier FOREIGN KEY (modifiedby)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE elmis_help
  OWNER TO postgres;
