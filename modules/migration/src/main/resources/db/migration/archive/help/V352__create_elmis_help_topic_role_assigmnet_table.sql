-- Table: elmis_help_topic_roles

DROP TABLE IF EXISTS elmis_help_topic_roles cascade;

CREATE TABLE elmis_help_topic_roles
(
  id serial NOT NULL,
  help_topic_id integer,
  role_id integer,
  is_asigned boolean DEFAULT true,
  was_previosly_assigned boolean DEFAULT true,
  created_by integer,
  createddate date,
  modifiedby integer,
  modifieddate date,
  CONSTRAINT elmis_help_topic_roles_pkey PRIMARY KEY (id),
  CONSTRAINT elmis_help_topic_roles_help_topic_id_fkey FOREIGN KEY (help_topic_id)
      REFERENCES elmis_help_topic (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT elmis_help_topic_roles_role_id_fkey FOREIGN KEY (role_id)
      REFERENCES roles (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE elmis_help_topic_roles
  OWNER TO postgres;

  -- Table: elmis_help_document


