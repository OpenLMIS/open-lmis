
DROP VIEW IF EXISTS dw_order_fill_rate_vw;
DROP VIEW IF EXISTS dw_product_facility_stock_info_vw;
DROP VIEW IF EXISTS dw_product_fill_rate_vw;
DROP VIEW IF EXISTS dw_product_lead_time_vw;
DROP VIEW IF EXISTS vw_dw_item_fill_rate;
DROP VIEW IF EXISTS vw_dw_order_fill_rate;
DROP VIEW IF EXISTS vw_order_fill_rate;

DROP Index IF EXISTS dw_orders_index;	
DROP Index IF EXISTS dw_orders_item_fill_rate;
DROP Index IF EXISTS dw_orders_lead_time;	
DROP Index IF EXISTS dw_orders_order_fill_rate;	
DROP Index IF EXISTS indx_dw_orders_productcode;	
DROP Index IF EXISTS indx_dw_orders_prog_period_rg_facility;	
DROP Index IF EXISTS indx_dw_orders_rnrid;	
DROP Index IF EXISTS indx_dw_orders_rnrid_periodid_productcode;	
DROP Index IF EXISTS indx_dw_orders_rnrid_productcode;	
DROP Index IF EXISTS indx_dw_orders_status;

 
-- 16. Table: dw_orders

DROP TABLE IF EXISTS dw_orders;

CREATE TABLE dw_orders
(
programid integer,
programname character varying(50),
scheduleid integer,
schedulename character varying(50), 
periodid integer NOT NULL,
processingperiodname character varying(250),
geographiczoneid integer NOT NULL,
geographiczonename character varying(250),
supervisorynodeid integer, 
requisitiongroupid integer,
requisitiongroupname character varying(50),
facilitytypeid integer,
facilitytypename character varying(50),
facilityid integer NOT NULL,
facilitycode character varying(50) NOT NULL,
facilityname character varying(50),
productcategoryid integer,
productcategoryname character varying(150),
productgroupid integer,
productgroupname character varying(250),
rnrid integer NOT NULL,
emergency boolean,
status character varying(20) NOT NULL,
createddate timestamp without time zone,
approveddate timestamp without time zone,
shippeddate timestamp without time zone,
receiveddate timestamp without time zone,
initiateddate timestamp without time zone,
submitteddate timestamp without time zone,
authorizeddate timestamp without time zone,
inapprovaldate timestamp without time zone,
releaseddate timestamp without time zone,
productid integer NOT NULL,
productcode character varying(50) NOT NULL,
productprimaryname character varying(150),
productfullname character varying(250),
quantityrequested integer,
quantityapproved integer,
quantityshipped integer,
quantityreceived integer, 
soh integer,
amc integer,
mos numeric(6,1),  
stockedoutinpast boolean,
suppliedinpast boolean,
mossuppliedinpast numeric(6,1),
stocking character(1),
reporting character(1)

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

-- Index: dw_orders_index

-- DROP INDEX dw_orders_index;

CREATE INDEX dw_orders_index
  ON dw_orders
  USING btree
  (stockedoutinpast, requisitiongroupid, programid, scheduleid, periodid, productid, facilityid);

-- Index: dw_orders_item_fill_rate

-- DROP INDEX dw_orders_item_fill_rate;

CREATE INDEX dw_orders_item_fill_rate
  ON dw_orders
  USING btree
  (programid, periodid, facilityname COLLATE pg_catalog."default", productid);

-- Index: dw_orders_lead_time

-- DROP INDEX dw_orders_lead_time;

CREATE INDEX dw_orders_lead_time
  ON dw_orders
  USING btree
  (programid, periodid, facilityname COLLATE pg_catalog."default", facilitycode COLLATE pg_catalog."default", facilityid);

-- Index: dw_orders_order_fill_rate

-- DROP INDEX dw_orders_order_fill_rate;

CREATE INDEX dw_orders_order_fill_rate
  ON dw_orders
  USING btree
  (programid, periodid, facilityid);

-- Index: indx_dw_orders_productcode

-- DROP INDEX indx_dw_orders_productcode;

CREATE INDEX indx_dw_orders_productcode
  ON dw_orders
  USING btree
  (productcode COLLATE pg_catalog."default");

-- Index: indx_dw_orders_prog_period_rg_facility

-- DROP INDEX indx_dw_orders_prog_period_rg_facility;

CREATE INDEX indx_dw_orders_prog_period_rg_facility
  ON dw_orders
  USING btree
  (programid, periodid, requisitiongroupid, facilityid);

-- Index: indx_dw_orders_rnrid

-- DROP INDEX indx_dw_orders_rnrid;

CREATE INDEX indx_dw_orders_rnrid
  ON dw_orders
  USING btree
  (rnrid);

-- Index: indx_dw_orders_rnrid_periodid_productcode

-- DROP INDEX indx_dw_orders_rnrid_periodid_productcode;

CREATE INDEX indx_dw_orders_rnrid_periodid_productcode
  ON dw_orders
  USING btree
  (rnrid, periodid, productcode COLLATE pg_catalog."default");

-- Index: indx_dw_orders_rnrid_productcode

-- DROP INDEX indx_dw_orders_rnrid_productcode;

CREATE INDEX indx_dw_orders_rnrid_productcode
  ON dw_orders
  USING btree
  (rnrid, productcode COLLATE pg_catalog."default");

-- Index: indx_dw_orders_status

-- DROP INDEX indx_dw_orders_status;

CREATE INDEX indx_dw_orders_status
  ON dw_orders
  USING btree
  (status COLLATE pg_catalog."default");
