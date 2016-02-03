CREATE TABLE manufacturers
(
  id serial PRIMARY KEY,
  name character varying(1000) NOT NULL,
  website character varying(1000) NOT NULL,
  contactPerson character varying(200),
  primaryPhone character varying(20),
  email character varying(200),
  description character varying(2000),
  specialization character varying(2000),
  geographicCoverage character varying(2000),
  registrationDate date,
  createdBy integer,
  createdDate timestamp without time zone DEFAULT now(),
  modifiedBy integer,
  modifiedDate timestamp without time zone DEFAULT now()
);
ALTER TABLE manufacturers OWNER TO postgres;
