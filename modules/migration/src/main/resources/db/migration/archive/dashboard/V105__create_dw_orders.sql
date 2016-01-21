-- Table: dw_orders

DROP TABLE IF EXISTS dw_orders;

CREATE TABLE dw_orders
(
  geographiczoneid integer NOT NULL,
  facilityid integer NOT NULL,
  facilitycode character varying(50) NOT NULL,
  periodid integer NOT NULL,
  rnrid integer NOT NULL,
  status character varying(20) NOT NULL,
  productid integer NOT NULL,
  productcode character varying(50) NOT NULL,
  quantityrequested integer,
  quantityapproved integer,
  quantityshipped integer,
  quantityreceived integer,
  createddate timestamp without time zone,
  approveddate timestamp without time zone,
  shippeddate timestamp without time zone,
  receiveddate timestamp without time zone
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dw_orders
  OWNER TO postgres;
COMMENT ON TABLE dw_orders
  IS 'stores data to calculate order fill rate and item fill rate

Definitions:
ORDER FILL RATE: Total number of products received / Total number of products approved Parameters: geograhic zone, facility, period
ITEM FILL RATE: Total qty received / Total qty approved. Parameter: geograhic zone, product, period

Joins: 
requisitions, facilitities, products, requision_line_items, shipment_line_items,requisition_status_changes,pod

Fields and source: 
geographic zone id - facilities table
facility id -- facilities table
period id -- requisitions tables
rnr id - requisitions
product id - requisition_line_items
quantity requested -- requisition_line_items
quantity approved -- requisition_line_items
quantity received -- requisition_line_items 
date requisition created -
date requisition approved - 
date requisition (order) shipped';
