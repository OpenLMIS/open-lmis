
DROP TABLE IF EXISTS vaccine_order_requisitions CASCADE;

CREATE TABLE vaccine_order_requisitions
(
  id serial NOT NULL,
  periodid integer NOT NULL,
  programid integer NOT NULL,
  status character varying(100) NOT NULL,
  supervisorynodeid integer,
  facilityid integer,
  orderdate character varying(100) NOT NULL,
  createdby integer,
  createddate timestamp with time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp with time zone DEFAULT now(),
  emergency boolean DEFAULT false,
  CONSTRAINT vaccine_order_requisitions_pkey PRIMARY KEY (id),
  CONSTRAINT facility_fkey FOREIGN KEY (facilityid)
      REFERENCES facilities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT vaccine_order_requisitions_periodid_fkey FOREIGN KEY (periodid)
      REFERENCES processing_periods (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT vaccine_order_requisitions_programid_fkey FOREIGN KEY (programid)
      REFERENCES programs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_order_requisitions
  OWNER TO postgres;


DROP TABLE IF EXISTS vaccine_order_requisition_status_changes;

CREATE TABLE vaccine_order_requisition_status_changes
(
  id serial NOT NULL,
  orderid integer NOT NULL,
  status character varying(50) NOT NULL,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT vaccine_order_requisition_status_changes_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_order_requisition_status_changes
  OWNER TO postgres;


DROP TABLE IF EXISTS vaccine_order_requisition_line_items;

CREATE TABLE vaccine_order_requisition_line_items
(
  id serial NOT NULL,
  orderid integer NOT NULL,
  productid integer NOT NULL,
  productname character varying(200) NOT NULL,
  maximumstock integer,
  reorderlevel integer,
  bufferstock integer,
  stockonhand integer,
  quantityrequested integer,
  ordereddate character varying(100) NOT NULL,
  overriddenisa integer,
  maxmonthsofstock integer,
  minmonthsofstock integer,
  eop integer,
  createdby integer,
  createddate timestamp with time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp with time zone DEFAULT now(),
  productcategory character varying(200),
  CONSTRAINT vaccine_order_requisition_line_items_pkey PRIMARY KEY (id),
  CONSTRAINT vaccine_order_requisition_line_items_productid_fkey FOREIGN KEY (productid)
      REFERENCES products (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_order_requisition_line_items
  OWNER TO postgres;


 DROP TABLE IF EXISTS vaccine_order_requisition_master_columns;

CREATE TABLE vaccine_order_requisition_master_columns
(
  id serial NOT NULL,
  name character varying(200) NOT NULL,
  description character varying(200) NOT NULL,
  label character varying(200) NOT NULL,
  indicator character varying(200) NOT NULL,
  displayorder integer NOT NULL,
  mandatory boolean NOT NULL,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT vaccine_order_requisition_master_columns_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE vaccine_order_requisition_master_columns
  OWNER TO postgres;



