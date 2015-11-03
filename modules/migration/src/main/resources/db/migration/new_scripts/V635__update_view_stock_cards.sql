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
    fap.maxmonthsofstock,
    fap.minmonthsofstock,
    fap.eop,
    ic.whoratio,
    ic.dosesperyear,
    ic.wastagefactor,
    ic.bufferpercentage,
    ic.minimumvalue,
    ic.maximumvalue,
    ic.adjustmentvalue
   FROM stock_cards sc
     LEFT JOIN program_products pp ON sc.productid = pp.productid
     LEFT JOIN facilities ON sc.facilityid = facilities.id
     LEFT JOIN isa_coefficients ic ON ic.id = pp.isacoefficientsid
     LEFT JOIN facility_approved_products fap ON facilities.typeid = fap.facilitytypeid AND fap.programproductid = pp.id;

ALTER TABLE vw_stock_cards
  OWNER TO postgres;
