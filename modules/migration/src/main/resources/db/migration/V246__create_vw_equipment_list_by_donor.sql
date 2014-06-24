DROP VIEW IF EXISTS  vw_equipment_list_by_donor;

CREATE OR REPLACE VIEW vw_equipment_list_by_donor AS
 SELECT geographic_zones.name AS district,
    facilities.name AS facilityname,
    donors.longname AS donor,
    facility_program_equipments.sourceoffund,
    equipments.name AS equipment_name,
    facility_program_equipments.model,
    facility_program_equipments.yearofinstallation,
        CASE
            WHEN facility_program_equipments.hasservicecontract = false THEN 'No'::text
            ELSE 'yes'::text
        END AS hasservicecontract,
        CASE
            WHEN facility_program_equipments.servicecontractenddate IS NULL THEN '-'::text
            ELSE facility_program_equipments.servicecontractenddate::text
        END AS servicecontractenddate,
        CASE
            WHEN facility_program_equipments.isactive = true THEN 'Yes'::text
            ELSE 'No'::text
        END AS isactive,
        CASE
            WHEN facility_program_equipments.datedecommissioned IS NULL THEN '-'::text
            ELSE facility_program_equipments.datedecommissioned::text
        END AS datedecommissioned,
        CASE
            WHEN facility_program_equipments.replacementrecommended = false THEN 'No'::text
            ELSE 'Yes'::text
        END AS replacementrecommended,
    facility_types.id AS ft_id,
    programs.id AS pg_id,
    facilities.id AS f_id,
    processing_schedules.id AS ps_id,
    processing_periods.id AS pp_id,
    requisition_group_members.requisitiongroupid AS rgm_id,
    equipment_types.id AS eqpt_ty_id
   FROM equipments
   JOIN facility_program_equipments ON facility_program_equipments.equipmentid = equipments.id
   JOIN programs ON facility_program_equipments.programid = programs.id
   JOIN facilities ON facilities.id = facility_program_equipments.facilityid
   JOIN facility_types ON facilities.typeid = facility_types.id
   JOIN equipment_status_line_items ON equipments.code::text = equipment_status_line_items.code::text
   JOIN equipment_operational_status ON equipment_operational_status.id = equipment_status_line_items.operationalstatusid
   JOIN requisitions ON requisitions.programid = programs.id AND equipment_status_line_items.rnrid = requisitions.id
   JOIN processing_periods ON requisitions.periodid = processing_periods.id
   JOIN processing_schedules ON processing_periods.scheduleid = processing_schedules.id
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN equipment_types ON equipment_types.id = equipments.equipmenttypeid
   JOIN requisition_group_members ON requisition_group_members.facilityid = facilities.id
   JOIN donors ON donors.id = facility_program_equipments.primarydonorid
  ORDER BY geographic_zones.name, facilities.name, equipment_status_line_items.operationalstatusid;

ALTER TABLE vw_equipment_list_by_donor
  OWNER TO postgres;
