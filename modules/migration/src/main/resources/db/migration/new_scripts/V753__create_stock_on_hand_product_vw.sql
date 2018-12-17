CREATE OR REPLACE VIEW stock_on_hand_product_vw AS
select f.code as facilitycode,
       f.name as facilityname,
       p.code as productcode,
       p.primaryname as productFullName,
       p.packsize,
       l.lotnumber,
       l.expirationdate,
       loh.quantityonhand,
       sc.effectivedate,
       loh.modifieddate
 from stock_cards sc
 left join facilities f on sc.facilityid = f.id
 left join products p on sc.productid = p.id
 left join lots_on_hand loh on sc.id = loh.stockcardid
 left join lots l on l.id = loh.lotid;