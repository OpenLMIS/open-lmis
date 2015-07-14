
DROP VIEW IF EXISTS vw_replacement_plan_summary;

CREATE OR REPLACE VIEW vw_replacement_plan_summary AS 
 SELECT (( SELECT date_part('year'::text, 'now'::text::date)::integer AS date_part)) - (( SELECT configuration_settings.value::integer AS value
           FROM configuration_settings
          WHERE configuration_settings.key::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text)) AS this_year,
    ( SELECT date_part('year'::text, 'now'::text::date)::integer AS date_part) AS replacementyearone,
    (( SELECT date_part('year'::text, 'now'::text::date)::integer + 1)) - (( SELECT configuration_settings.value::integer AS value
           FROM configuration_settings
          WHERE configuration_settings.key::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text)) AS second_year,
    ( SELECT date_part('year'::text, 'now'::text::date)::integer + 1) AS replacementyeartwo,
    (( SELECT date_part('year'::text, 'now'::text::date)::integer + 2)) - (( SELECT configuration_settings.value::integer AS value
           FROM configuration_settings
          WHERE configuration_settings.key::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text)) AS third_year,
    ( SELECT date_part('year'::text, 'now'::text::date)::integer + 2) AS replacementyearthree,
    (( SELECT date_part('year'::text, 'now'::text::date)::integer + 3)) - (( SELECT configuration_settings.value::integer AS value
           FROM configuration_settings
          WHERE configuration_settings.key::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text)) AS fourth_year,
    ( SELECT date_part('year'::text, 'now'::text::date)::integer + 3) AS replacementyearfour,
    (( SELECT date_part('year'::text, 'now'::text::date)::integer + 4)) - (( SELECT configuration_settings.value::integer AS value
           FROM configuration_settings
          WHERE configuration_settings.key::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text)) AS fifth_year,
    ( SELECT date_part('year'::text, 'now'::text::date)::integer + 4) AS replacementyearfive,
    facilities.code AS facilitycode,
    facilities.name AS facilityname,
    facility_types.id AS facilitytypeid,
    equipments.name AS equipment_name,
    equipment_inventories.yearofinstallation,
    COALESCE(equipment_inventories.purchaseprice, 0::numeric) AS purchaseprice,
    programs.id AS programid,
    facilities.id AS facilityid,
    equipments.id AS equipment_id,
    vw_districts.district_name AS district,
    vw_districts.region_name AS region
     FROM equipment_inventories
     JOIN equipments ON equipment_inventories.equipmentid = equipments.id
     JOIN equipment_inventory_statuses eis ON equipment_inventories.id = eis.inventoryId
     JOIN equipment_operational_status ON eis.statusID = equipment_operational_status.id or eis.notfunctionalstatusid = equipment_operational_status.id
     JOIN programs ON equipment_inventories.programid = programs.id
     JOIN facilities ON facilities.id = equipment_inventories.facilityid
     JOIN facility_types ON facilities.typeid = facility_types.id
     JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
  ORDER BY facilities.name;
