CREATE OR REPLACE VIEW vw_sync_time AS
  (SELECT
     max(stock_cards.modifieddate) AS last_sync_time,
     facilities.name               AS facility_name,
     facilities.code               AS facility_code,

     ZONE.name                     AS district_name,
     ZONE.code                     AS district_code,

     parent_zone.name              AS province_name,
     parent_zone.code              AS province_code
   FROM stock_cards
     JOIN facilities ON stock_cards.facilityid = facilities.id
     JOIN geographic_zones AS ZONE ON facilities.geographiczoneid = ZONE.id
     JOIN geographic_zones AS parent_zone ON ZONE.parentid = parent_zone.id
   GROUP BY facilities.code, facilities.name, district_code, district_name, province_code, province_name);