  
DROP VIEW dw_product_facility_stock_info_vw;

CREATE OR REPLACE VIEW dw_product_facility_stock_info_vw AS 
 SELECT 0 AS requisitiongroupid,
    vw_stock_status_2.programid,
    vw_stock_status_2.periodid,
    vw_stock_status_2.gz_id AS geographiczoneid,
    vw_stock_status_2.location AS geographiczonename,
    vw_stock_status_2.facility_id AS facilityid,
    vw_stock_status_2.facility AS facilityname,
    vw_stock_status_2.facilitycode,
    vw_stock_status_2.productid,
    vw_stock_status_2.product AS primaryname,
    vw_stock_status_2.amc,
    vw_stock_status_2.stockinhand AS soh,
    vw_stock_status_2.mos,
    vw_stock_status_2.status,
    case vw_stock_status_2.status when 'SP' then 'A' when 'OS' then 'O' when 'US' then 'U' when 'SO' then 'S' end stocking
   FROM vw_stock_status_2
  ORDER BY vw_stock_status_2.gz_id, vw_stock_status_2.programid, vw_stock_status_2.periodid, vw_stock_status_2.productid, vw_stock_status_2.product, vw_stock_status_2.status;

ALTER TABLE dw_product_facility_stock_info_vw
  OWNER TO postgres;