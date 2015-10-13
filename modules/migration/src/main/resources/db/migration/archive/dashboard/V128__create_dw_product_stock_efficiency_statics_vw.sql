DROP VIEW IF EXISTS dw_product_stock_efficiency_statics_vw;

CREATE OR REPLACE VIEW dw_product_stock_efficiency_statics_vw AS 
 
select geographicZoneid,programid,periodid,productid,primaryname,
    MAX(CASE WHEN stocking = 'A' THEN stockingStat END) AS adequatelyStocked,
    MAX(CASE WHEN stocking = 'O' THEN stockingStat END) AS overStocked,
    MAX(CASE WHEN stocking = 'S' THEN stockingStat END) AS stockedOut,
    MAX(CASE WHEN stocking = 'U' THEN stockingStat END) AS understocked
    from dw_product_facility_stock_info_vw    
group by geographicZoneid,programid,periodid,productid,primaryname
order by geographicZoneid,programid,periodid,productid,primaryname;
ALTER TABLE dw_product_stock_efficiency_statics_vw
  OWNER TO postgres;