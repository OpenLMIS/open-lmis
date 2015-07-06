/* 
   This view is intended to return all information required by a Cold Chain Equipment Inventory Report.

   Dependency: fn_get_geozonetree_names.
*/

CREATE OR REPLACE VIEW vw_cold_chain_equipment AS 

SELECT
nested.id AS "equipmentId",
nested.manufacturer AS manufacturer, nested.model,
eet.name AS "energyTypeName", 
ecce.ccecode AS "equipmentColdChainEquipmentsCode", ecce.refrigerant, ecce.refrigeratorcapacity, ecce.freezercapacity,
eos.name AS "equipmentOperationalStatusName",
ei.yearofinstallation, (extract(year from current_date) - ei.yearofinstallation) AS "equipmentAge", (ei.yearofinstallation + 11) AS "yearOfReplacement",
facilities.id AS "facilityId", facilities.name AS "facilityName",
ft.name AS "facilityTypeName", 
facilities.address1 AS "facilityAddress1", facilities.address2 AS "facilityAddress2", facilities.haselectricity AS "facilityHasElectricity",
fo.text AS "facilityOperator",
gz.id AS geoZoneID, gz.name AS geoZoneName, geo_zone_tree.hierarchy AS "geozoneHierarchy"
 
FROM equipment_inventories ei JOIN
(
SELECT DISTINCT e.* FROM equipments e JOIN equipment_types et
ON(e.equipmenttypeid = et.id)
WHERE UPPER(et.code) = 'CCE1'  
) AS nested

ON nested.id = ei.equipmentid
LEFT JOIN equipment_cold_chain_equipments AS ecce
ON nested.id = ecce.equipmentid
LEFT JOIN equipment_energy_types AS eet
ON nested.energytypeid = eet.id 
LEFT JOIN facilities
ON ei.facilityid = facilities.id
LEFT JOIN facility_types AS ft
ON facilities.typeid = ft.id
LEFT JOIN equipment_inventory_statuses AS eis
ON ei.id = eis.inventoryid
LEFT JOIN equipment_operational_status AS eos
ON eis.statusid = eos.id OR eis.notfunctionalstatusid = eos.id
LEFT JOIN facility_operators as fo
ON facilities.operatedbyid = fo.id
LEFT JOIN geographic_zones gz
ON facilities.geographiczoneid = gz.id

--Using LATERAL is a clean approach, but unsupported in the version of postgres we're targeting
/*
LEFT OUTER JOIN LATERAL
(
	SELECT * FROM fn_get_geozonetree_names(facilities.geographiczoneid) AS "geozoneHierarchy"
) AS geo_zone_tree 
ON true; */
LEFT JOIN
(
	SELECT * FROM fn_get_geozonetree_names()
) AS geo_zone_tree
ON gz.id = geo_zone_tree.leafid;


ALTER TABLE vw_cold_chain_equipment
  OWNER TO postgres;
