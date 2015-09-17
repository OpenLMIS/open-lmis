-- Table: elmis_help_topic

 DROP  TABLE IF EXISTS elmis_help_topic cascade;

CREATE TABLE elmis_help_topic
(
  level integer,
  name character varying(200),
  created_by integer,
  createddate date,
  modifiedby integer,
  modifieddate date,
  id serial NOT NULL,
  parent_help_topic_id integer,
  is_category boolean DEFAULT true,
  html_content character varying(50000),
  CONSTRAINT elmis_help_topic_pkey PRIMARY KEY (id),
  CONSTRAINT elmis_help_topic_parent_help_topic_id_fkey FOREIGN KEY (parent_help_topic_id)
      REFERENCES elmis_help_topic (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_foreign_users_modifier FOREIGN KEY (modifiedby)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_foreing_users_creator FOREIGN KEY (created_by)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE elmis_help_topic
  OWNER TO postgres;
