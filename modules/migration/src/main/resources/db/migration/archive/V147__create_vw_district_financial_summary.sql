DROP VIEW IF EXISTS vw_district_financial_summary;

CREATE OR REPLACE VIEW vw_district_financial_summary AS
 SELECT processing_periods.id AS periodid, processing_periods.name AS period, processing_periods.startdate, processing_periods.enddate, processing_periods.scheduleid, processing_schedules.name AS schedule, facility_types.id AS facilitytypeid, facility_types.name AS facilitytype, facilities.code AS facilitycode, facilities.name AS facility, facilities.id AS facility_id, requisitions.id AS rnrid, requisitions.status, geographic_zones.id AS zoneid, geographic_zones.name AS region, p.name AS program, p.id AS programid, requisitions.fullsupplyitemssubmittedcost, rg.id AS rgroupid, rg.name AS rgroup, requisitions.nonfullsupplyitemssubmittedcost
   FROM requisitions
   JOIN supervisory_nodes sn ON sn.id = requisitions.supervisorynodeid
   JOIN facilities ON facilities.id = requisitions.facilityid
   JOIN facility_types ON facility_types.id = facilities.typeid
   JOIN processing_periods ON processing_periods.id = requisitions.periodid
   JOIN processing_schedules ON processing_schedules.id = processing_periods.scheduleid
   JOIN requisition_group_members ON requisition_group_members.facilityid = facilities.id
   JOIN requisition_groups rg ON rg.id = requisition_group_members.requisitiongroupid
   JOIN geographic_zones ON geographic_zones.id = facilities.geographiczoneid
   JOIN programs p ON p.id = requisitions.programid;

ALTER TABLE vw_district_financial_summary
  OWNER TO postgres;

