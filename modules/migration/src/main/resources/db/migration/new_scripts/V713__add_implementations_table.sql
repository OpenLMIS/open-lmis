CREATE TABLE implementations
(
  id serial NOT NULL,
  executor character varying(50) NOT NULL,
  malariaprogramid integer NOT NULL,
  CONSTRAINT implementations_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE implementations OWNER TO postgres;
ALTER TABLE implementations ADD CONSTRAINT
  malaria_programs_id_fkey FOREIGN KEY (malariaprogramid)
  REFERENCES malaria_programs (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
