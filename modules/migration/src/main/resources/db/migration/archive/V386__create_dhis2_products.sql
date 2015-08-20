-- Table: dhis2_products

DROP TABLE IF EXISTS dhis2_products;

CREATE TABLE dhis2_products
(
  productname character varying,
  elmiscode character varying,
  othercode character varying
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dhis2_products
  OWNER TO openlmis;

