DROP TABLE IF EXISTS equipment_cold_chain_equipment_designations;
CREATE TABLE equipment_cold_chain_equipment_designations
(
  id SERIAL  NOT NULL,
  name character varying(200) NOT NULL,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT cce_designations_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE equipment_cold_chain_equipment_designations
  OWNER TO postgres;

DROP TABLE IF EXISTS equipment_cold_chain_equipment_energy_types;
CREATE TABLE equipment_cold_chain_equipment_energy_types
(
  id serial NOT NULL,
  name character varying(200) NOT NULL,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT cce_energy_types_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE equipment_cold_chain_equipment_energy_types
  OWNER TO postgres;

DROP TABLE IF EXISTS equipment_cold_chain_equipment_pqs_status;
CREATE TABLE equipment_cold_chain_equipment_pqs_status
(
  id serial NOT NULL,
  name character varying(200) NOT NULL,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT cce_psqstatus_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE equipment_cold_chain_equipment_pqs_status
  OWNER TO postgres;

DROP TABLE IF EXISTS equipment_cold_chain_equipments;
CREATE TABLE equipment_cold_chain_equipments
(
  id serial NOT NULL,
  equipmentid integer NOT NULL,
  designationid integer NOT NULL,
  brand character varying(200) NOT NULL,
  model character varying(200) NOT NULL,
  ccecode character varying(200),
  pqscode character varying(200),
  refrigeratorcapacity numeric(8,2),
  freezercapacity numeric(8,2),
  refrigerant character varying(200),
  temperaturezone character varying(200),
  maxtemperature integer,
  mintemperature integer,
  holdovertime character varying(200),
  energyconsumption character varying(200),
  energytypeid integer,
  dimension character varying(200),
  price numeric(18,2),
  pqsstatusid integer NOT NULL,
  donorid integer,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT equipment_cce_pkey PRIMARY KEY (id),
  CONSTRAINT equipment_cce_designation_fkey FOREIGN KEY (designationid)
      REFERENCES equipment_cold_chain_equipment_designations (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT equipment_cce_donor_fkey FOREIGN KEY (donorid)
      REFERENCES donors (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT equipment_cce_energy_type_fkey FOREIGN KEY (energytypeid)
      REFERENCES equipment_cold_chain_equipment_energy_types (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT equipment_cce_equipment_fkey FOREIGN KEY (equipmentid)
      REFERENCES equipments (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT equipment_cce_psq_status_fkey FOREIGN KEY (pqsstatusid)
      REFERENCES equipment_cold_chain_equipment_pqs_status (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE equipment_cold_chain_equipments
  OWNER TO postgres