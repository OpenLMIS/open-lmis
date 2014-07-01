
-- View: dw_product_facility_stock_info_vw

DROP VIEW IF EXISTS dw_product_facility_stock_info_vw;

CREATE OR REPLACE VIEW dw_product_facility_stock_info_vw AS 
select
0 as requisitiongroupid,
programid,
periodid,			
gz_id	as geographiczoneid,
location as geographiczonename,			
facility_id as facilityid,
facility as facilityname,
facilitycode,
productid,
product as primaryname,
amc,
stockinhand as soh,
mos,
left(status,1) as stocking
from vw_stock_status_2
ORDER BY geographiczoneid, programid, periodid, productid, primaryname, status; 

ALTER TABLE dw_product_facility_stock_info_vw
  OWNER TO postgres;

