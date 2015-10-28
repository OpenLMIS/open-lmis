DROP TABLE IF EXISTS vaccine_distributions CASCADE;
CREATE TABLE vaccine_distributions
(
  id serial NOT NULL,
  tofacilityid integer NOT NULL,
  fromfacilityid integer NOT NULL,
  vouchernumber character varying(100),
  distributiondate timestamp without time zone,
  periodid integer,
  orderid integer,
  status character varying(20),
  createdby integer,
  createddate timestamp without time zone,
  modifiedby integer,
  modifieddate timestamp without time zone,
  distributiontype character varying(40),
  CONSTRAINT vacc_inventory_distribution_pkey PRIMARY KEY (id),
  CONSTRAINT vacc_distributions_frofacility_fkey FOREIGN KEY (fromfacilityid)
      REFERENCES facilities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT vacc_distributions_period_fkey FOREIGN KEY (periodid)
      REFERENCES processing_periods (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT vacc_distributions_tofacility_fkey FOREIGN KEY (tofacilityid)
      REFERENCES facilities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distributions
  OWNER TO postgres;


DROP TABLE IF EXISTS vaccine_distribution_line_items CASCADE;
CREATE TABLE vaccine_distribution_line_items
(
  id serial NOT NULL,
  distributionid integer NOT NULL,
  productid integer NOT NULL,
  quantity integer DEFAULT 0,
  vvmstatus smallint,
  createdby integer,
  createddate timestamp without time zone,
  modifiedby integer,
  modifieddate timestamp without time zone,
  CONSTRAINT vacc_distribution_line_items_pkey PRIMARY KEY (id),
  CONSTRAINT vacc_distribution_line_items_distribution_fkey FOREIGN KEY (distributionid)
      REFERENCES vaccine_distributions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT vacc_distributions_line_items_product_fkey FOREIGN KEY (productid)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_line_items
  OWNER TO postgres;

DROP TABLE IF EXISTS vaccine_distribution_batches;
DROP TABLE IF EXISTS vaccine_distribution_line_item_lots;

CREATE TABLE vaccine_distribution_line_item_lots
(
  id serial NOT NULL,
  distributionlineitemid integer NOT NULL,
  lotid integer NOT NULL,
  quantity integer DEFAULT 0,
  vvmstatus smallint,
  createdby integer,
  createddate timestamp without time zone,
  modifiedby integer,
  modifieddate timestamp without time zone,
  CONSTRAINT vacc_distributin_line_item_losts_pkey PRIMARY KEY (id),
  CONSTRAINT vacc_distribution_lotid_fkey FOREIGN KEY (lotid)
      REFERENCES lots (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT vacc_distribution_lots_line_item_fkey FOREIGN KEY (distributionlineitemid)
      REFERENCES vaccine_distribution_line_items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_distribution_line_item_lots
  OWNER TO postgres;