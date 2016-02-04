DROP VIEW IF EXISTS vw_stock_cards;

CREATE OR REPLACE VIEW vw_stock_cards AS
 SELECT DISTINCT sc.id,
    sc.facilityid,
    sc.productid,
    sc.totalquantityonhand,
    sc.effectivedate,
    sc.notes,
    sc.createdby,
    sc.createddate,
    sc.modifiedby,
    sc.modifieddate,
    pp.id AS program_product_id,
    pp.programid,
    fpp.overriddenisa,
    fap.maxmonthsofstock,
    fap.minmonthsofstock,
    fap.eop
   FROM stock_cards sc
     LEFT JOIN program_products pp ON sc.productid = pp.productid
     LEFT JOIN facility_program_products fpp ON sc.facilityid = fpp.facilityid AND pp.id = fpp.programproductid
     LEFT JOIN facilities ON sc.facilityid = facilities.id
     LEFT JOIN facility_approved_products fap ON facilities.typeid = fap.facilitytypeid AND fap.programproductid = pp.id;

ALTER TABLE vw_stock_cards
  OWNER TO postgres;
