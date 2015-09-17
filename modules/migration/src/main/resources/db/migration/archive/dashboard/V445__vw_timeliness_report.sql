
DROP VIEW IF EXISTS vw_timeliness_report;

CREATE OR REPLACE VIEW vw_timeliness_report AS
 SELECT requisitions.programid,
    requisitions.periodid,
    rgps.scheduleid,
    facilities.geographiczoneid,
    facilities.name AS facilityname,
    facilities.code AS facilitycode,
    requisitions.createddate,
    pp.enddate,
    requisitions.status,
    requisitions.id AS rnrid,
    facilities.id AS facilityid,
    facility_types.id AS typeid,
    facility_types.name AS facilitytypename,
        CASE
            WHEN COALESCE(date_part('day'::text, requisitions.createddate - pp.enddate::date::timestamp without time zone), 0::double precision) <= COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE configuration_settings.key::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text))::integer, 0)::double precision THEN 'R'::text
            WHEN COALESCE(date_part('day'::text, requisitions.createddate - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE configuration_settings.key::text = 'UNSCHEDULED_REPORTING_CUT_OFF_DATE'::text))::integer, 0)::double precision THEN 'U'::text
            WHEN COALESCE(date_part('day'::text, requisitions.createddate - pp.enddate::date::timestamp without time zone), 0::double precision) > COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE configuration_settings.key::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text))::integer, 0)::double precision THEN 'L'::text
            ELSE 'N'::text
        END AS reportingstatus
   FROM requisitions
     JOIN facilities ON requisitions.facilityid = facilities.id
     JOIN requisition_group_members rgm ON rgm.facilityid = requisitions.facilityid
     JOIN facility_types ON facilities.typeid = facility_types.id
     JOIN programs_supported ps ON ps.programid = requisitions.programid AND requisitions.facilityid = ps.facilityid
     JOIN processing_periods pp ON pp.id = requisitions.periodid
     JOIN requisition_group_program_schedules rgps ON rgps.requisitiongroupid = rgm.requisitiongroupid AND rgps.programid = requisitions.programid AND pp.scheduleid = rgps.scheduleid
     JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
  WHERE (requisitions.status::text = ANY (ARRAY['IN_APPROVAL'::character varying::text, 'APPROVED'::character varying::text, 'RELEASED'::character varying::text])) AND facilities.active = true AND requisitions.emergency = false
  GROUP BY requisitions.status, requisitions.createddate, pp.enddate, requisitions.id, requisitions.programid, requisitions.periodid, rgps.scheduleid, facilities.geographiczoneid, facilities.name, facilities.code, facilities.id, facility_types.id, facility_types.name;

ALTER TABLE vw_timeliness_report
  OWNER TO postgres;
