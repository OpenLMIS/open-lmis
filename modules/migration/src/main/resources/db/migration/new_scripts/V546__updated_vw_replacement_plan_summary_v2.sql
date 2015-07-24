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
    ei.facilityid,
    ei.programid,
    f.typeid AS facilitytypeid,
    vwd.region_name AS region,
    vwd.district_name AS district,
    f.name AS facilityname,
    ft.code Facilitytypecode,
    ft.name AS facilitytypename,
    e.manufacturer AS brand,
    e.model,
    COALESCE(cce.refrigeratorcapacity, 0::numeric) AS capacity,
    eos.name AS working_status,
    COALESCE(brkd.break_down, 0::bigint) AS break_down,
        CASE
            WHEN eose.name::text = 'Obsolete'::text THEN 'O'::character varying
            WHEN eose.name::text = 'Waiting For Repair'::text THEN 'W'::character varying
            WHEN eose.name::text = 'Waiting For Spare Parts'::text THEN 'S'::character varying
            ELSE eose.name
        END AS status,
    e.name AS equipment_name,
    ei.yearofinstallation,
    COALESCE(ei.purchaseprice, 0::numeric) AS purchaseprice,
    eet.name AS sourceofenergy,
    ei.serialnumber,
    COALESCE((( SELECT date_part('year'::text, 'now'::text::date)::integer AS date_part)) - ei.yearofinstallation, 0) AS age
   FROM equipment_inventories ei
     JOIN equipment_inventory_statuses eis ON eis.id = (( SELECT eisb.id
           FROM equipment_inventory_statuses eisb
          WHERE eisb.inventoryid = ei.id
          ORDER BY eisb.createddate DESC
         LIMIT 1))
     JOIN equipment_operational_status eos ON eis.statusid = eos.id
     LEFT JOIN equipment_operational_status eose ON eis.notfunctionalstatusid = eose.id
     JOIN equipments e ON ei.equipmentid = e.id
     JOIN equipment_cold_chain_equipments cce ON cce.equipmentid = e.id
     JOIN equipment_types et ON e.equipmenttypeid = et.id
     JOIN facilities f ON f.id = ei.facilityid
     JOIN facility_types ft ON ft.id = f.typeid
     JOIN vw_districts vwd ON vwd.district_id = f.geographiczoneid
     LEFT JOIN ( SELECT eis_1.inventoryid AS id,
            COALESCE(count(eis_1.id), 0::bigint) AS break_down
           FROM equipment_inventory_statuses eis_1
             LEFT JOIN equipment_operational_status eos_1 ON eos_1.id = eis_1.statusid
          WHERE eos_1.name::text = 'Not Functional'::text
          GROUP BY eis_1.inventoryid) brkd ON brkd.id = ei.id
     LEFT JOIN equipment_energy_types eet ON e.energytypeid = eet.id
  WHERE et.iscoldchain IS TRUE;