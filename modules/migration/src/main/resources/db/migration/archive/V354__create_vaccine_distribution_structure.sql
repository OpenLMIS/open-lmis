-------------------------------------------------
-- Table: vaccine_distribution_types
DROP TABLE IF EXISTS vaccine_distribution_types;
CREATE TABLE vaccine_distribution_types
(
  id integer NOT NULL,
  colde character varying(50),
  name character varying(250),
  nature character varying(2),
  CONSTRAINT vaccine_distribution_types_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_types
  OWNER TO postgres;  
--------------------------------------------------------
-- Table: vaccine_distribution_demographics
DROP TABLE IF EXISTS vaccine_distribution_demographics;
CREATE TABLE vaccine_distribution_demographics
(
  id serial NOT NULL,
  geographiczoneid integer,
  population integer,
  expected_births integer,
  expected_pregnancies integer,
  serving_infants integer,
  surviving_infants integer,
CONSTRAINT vaccine_distribution_demographics_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_demographics
  OWNER TO postgres;
-------------------------------------------------------------------
-- Table: vaccine_distribution_parameters
DROP TABLE IF EXISTS vaccine_distribution_parameters;
CREATE TABLE vaccine_distribution_parameters
(
  id serial NOT NULL,
  programid integer,
  productcode character varying(60),
  dosespertarget integer,
  targetpopulationpercent integer,
  expectedcoverage integer,
  presentation integer,
  wastagerate integer,
  administrationmodeid character varying,
  dilutionid character varying,
  supplyinterval integer,
  safetystock integer,
  leadtime integer,
  CONSTRAINT vaccine_distribution_parameters_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_parameters
  OWNER TO postgres;
-----------------------------------------------
-- Table: vaccine_distribution_batches
DROP TABLE IF EXISTS vaccine_distribution_batches;
CREATE TABLE  vaccine_distribution_batches
(
  id integer NOT NULL,
  batchid integer,
  expirydate timestamp without time zone,
  productiondate timestamp without time zone,
  manufacturerid integer,
  donorid integer,
  receiveddate date,
  productcode character varying(50),
  fromfacilityid integer,
  tofacilityid integer,
  distributiontype integer,
  vialperbox integer,
  box_length integer,
  box_width integer,
  box_height integer,
  unitcost integer,
  totalcost integer,
  purposeid integer,
  CONSTRAINT vaccine_distribution_batches_pkey PRIMARY KEY (id)  
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_batches
  OWNER TO postgres;
--------------------------------------------
-- Table: vaccine_distribution_line_items
DROP TABLE IF EXISTS vaccine_distribution_line_items;
CREATE TABLE vaccine_distribution_line_items
(
  id integer NOT NULL,
  distributionbatchid integer,
  quantityreceived double precision,
  vvmstage integer,
  confirmed integer,
  comments integer,
  CONSTRAINT vaccine_distribution_line_items_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_line_items
  OWNER TO postgres;