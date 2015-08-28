DO $$
BEGIN
  DROP TABLE IF EXISTS equipment_inventory_statuses;

  CREATE TABLE equipment_inventory_statuses
  (
    id serial NOT NULL,
    inventoryid integer NOT NULL,
    statusid integer NOT NULL,
    notfunctionalstatusid integer,
    effectivedatetime timestamp with time zone DEFAULT now(),
    CONSTRAINT equipment_inventory_status_history_pkey PRIMARY KEY (id),
    CONSTRAINT inventory_fkey FOREIGN KEY (inventoryid)
    REFERENCES equipment_inventories (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT status_fkey FOREIGN KEY (statusid)
    REFERENCES equipment_operational_status (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT not_functional_reason_fkey FOREIGN KEY (notfunctionalstatusid)
    REFERENCES equipment_operational_status (id) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
  )
  WITH (
  OIDS=FALSE
  );

  ALTER TABLE equipment_inventory_statuses
  OWNER TO postgres;

  DROP VIEW vw_equipment_list_by_donor;

  CREATE OR REPLACE VIEW vw_equipment_list_by_donor AS
    SELECT geographic_zones.name AS district,
      facilities.name AS facilityname,
      donors.shortname AS donor,
      equipment_inventories.sourceoffund,
      equipments.name AS equipment_name,
      equipments.model,
      equipment_inventories.yearofinstallation,
      CASE
        WHEN equipment_inventories.isactive = true THEN 'Yes'::text
        ELSE 'No'::text
      END AS isactive,
      CASE
        WHEN equipment_inventories.datedecommissioned IS NULL THEN '-'::text
        ELSE equipment_inventories.datedecommissioned::text
      END AS datedecommissioned,
      CASE
        WHEN equipment_inventories.replacementrecommended = false THEN 'No'::text
        ELSE 'Yes'::text
      END AS replacementrecommended,
      facilities.id AS facility_id,
      programs.id AS programid,
      equipments.id AS equipment_id,
      equipment_inventory_statuses.statusid AS status_id,
      equipment_types.id AS equipmenttype_id,
      facilities.geographiczoneid,
      facilities.typeid AS ftype_id,
      vw_districts.district_id,
      vw_districts.zone_id,
      vw_districts.region_id,
      vw_districts.parent,
      donors.id AS donorid
    FROM equipment_inventories
      JOIN equipments ON equipment_inventories.equipmentid = equipments.id
      JOIN programs ON equipment_inventories.programid = programs.id
      JOIN facilities ON facilities.id = equipment_inventories.facilityid
      JOIN facility_types ON facilities.typeid = facility_types.id
      JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
      JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
      LEFT JOIN donors ON donors.id = equipment_inventories.primarydonorid
      JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
      JOIN equipment_inventory_statuses ON equipment_inventory_statuses.inventoryid = equipment_inventories.id
    ORDER BY geographic_zones.name, facilities.name, equipments.model;

  ALTER TABLE vw_equipment_list_by_donor
    OWNER TO postgres;

  DROP VIEW vw_lab_equipment_status;

  CREATE OR REPLACE VIEW vw_lab_equipment_status AS
    SELECT programs.name AS program,
      facilities.code AS facility_code,
      facilities.name AS facility_name,
      facility_types.name AS facility_type,
      vw_districts.district_name AS disrict,
      vw_districts.zone_name AS zone,
      equipment_types.name AS equipment_type,
      equipments.model AS equipment_model,
      equipment_inventories.serialnumber AS serial_number,
      equipments.name AS equipment_name,
      equipment_operational_status.name AS equipment_status,
      facilities.latitude,
      facilities.longitude,
      facilities.id AS facility_id,
      programs.id AS programid,
      equipments.id AS equipment_id,
      equipment_operational_status.id AS status_id,
      facilities.geographiczoneid,
      facilities.typeid AS ftype_id,
      vw_districts.district_id,
      vw_districts.zone_id,
      vw_districts.region_id,
      vw_districts.parent,
      equipment_types.id AS equipmenttype_id
    FROM equipment_inventories
      JOIN facilities ON facilities.id = equipment_inventories.facilityid
      JOIN facility_types ON facility_types.id = facilities.typeid
      JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
      JOIN programs ON equipment_inventories.programid = programs.id
      JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
      JOIN equipments ON equipments.id = equipment_inventories.equipmentid
      JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
      JOIN equipment_inventory_statuses ON equipment_inventory_statuses.inventoryid = equipment_inventories.id
      JOIN equipment_operational_status ON equipment_operational_status.id = equipment_inventory_statuses.statusid
    ORDER BY facilities.name;

  ALTER TABLE vw_lab_equipment_status
    OWNER TO postgres;

  ALTER TABLE equipment_inventories
  DROP COLUMN operationalstatusid;

  ALTER TABLE equipment_inventories
  DROP COLUMN notfunctionalstatusid;

  ALTER TABLE equipment_status_line_items
  ADD COLUMN inventorystatusid integer NOT NULL;

  ALTER TABLE equipment_status_line_items
  ADD CONSTRAINT equipment_status_line_items_inventorystatusid_fkey FOREIGN KEY (inventorystatusid)
  REFERENCES equipment_inventory_statuses (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

  DROP VIEW vw_equipment_operational_status;

  CREATE OR REPLACE VIEW vw_equipment_operational_status AS
    SELECT geographic_zones.name AS district,
      facilities.name AS facility_name,
      equipments.name AS equipment_name,
      equipments.model,
      equipment_inventories.serialnumber,
      equipment_status_line_items.testcount AS test,
      equipment_status_line_items.totalcount AS total_test,
      equipment_status_line_items.daysoutofuse,
      equipment_operational_status.name AS operational_status,
      facility_types.id AS ft_id,
      programs.id AS pg_id,
      facilities.id AS f_id,
      processing_schedules.id AS ps_id,
      processing_periods.id AS pp_id,
      equipment_types.id AS eqpt_ty_id,
      vw_districts.zone_id,
      vw_districts.parent,
      vw_districts.region_id,
      vw_districts.district_id
    FROM equipment_inventories
      JOIN equipments ON equipment_inventories.equipmentid = equipments.id
      JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
      JOIN equipment_status_line_items ON equipment_types.code::text = equipment_status_line_items.code::text AND equipment_inventories.serialnumber::text = equipment_status_line_items.equipmentserial::text
      JOIN requisitions ON requisitions.id = equipment_status_line_items.rnrid
      JOIN programs ON equipment_inventories.programid = programs.id
      JOIN facilities ON facilities.id = equipment_inventories.facilityid
      JOIN facility_types ON facilities.typeid = facility_types.id
      JOIN equipment_inventory_statuses ON equipment_inventory_statuses.id = equipment_status_line_items.inventorystatusid
      JOIN equipment_operational_status ON equipment_operational_status.id = equipment_inventory_statuses.statusid
      JOIN processing_periods ON requisitions.periodid = processing_periods.id
      JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
      JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
      JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid
    ORDER BY geographic_zones.name, facilities.name, equipments.model, equipment_inventory_statuses.statusid;

  ALTER TABLE vw_equipment_operational_status
    OWNER TO postgres;

  ALTER TABLE equipment_status_line_items
    DROP CONSTRAINT equipment_status_line_items_operationalstatusid_fkey;

  ALTER TABLE equipment_status_line_items
    DROP COLUMN operationalstatusid;
END;
$$