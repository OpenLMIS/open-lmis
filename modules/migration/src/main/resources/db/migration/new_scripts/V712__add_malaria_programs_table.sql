CREATE TABLE malaria_programs
(
  id serial NOT NULL,
  username character varying(50) NOT NULL,
  reporteddate TIMESTAMP NOT NULL,
  periodstartdate TIMESTAMP NOT NULL,
  periodenddate TIMESTAMP NOT NULL,
  CONSTRAINT malaria_programs_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE malaria_programs OWNER TO postgres;
