
DROP VIEW IF EXISTS vw_regimen_district_distribution;

-- ----------------------------
-- View structure for "vw_regimen_district_distribution"
-- ----------------------------

CREATE OR REPLACE VIEW "vw_regimen_district_distribution" AS
 SELECT DISTINCT r.programid,
    ps.id AS scheduleid,
    pp.id AS periodid,
    regimens.categoryid,
    regimens.id AS regimenid,
    regimens.name AS regimen,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.facilityid,
    r.status,
    f.name AS facilityname,
    f.typeid AS facilitytypeid,
      gz.name AS district,
     gz.id AS districtid,
     zone.id AS regionid,
     c.id AS zoneid,
     c.parentid AS parentid
   FROM (((((((((regimen_line_items li
   JOIN requisitions r ON ((li.rnrid = r.id)))
   JOIN facilities f ON ((r.facilityid = f.id)))
   JOIN facility_types ft ON ((f.typeid = ft.id)))
   JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)))
   JOIN geographic_zones zone ON (( gz.parentid = zone.id)))
   JOIN geographic_zones c ON ((zone.parentid = c.id)))
   JOIN processing_periods pp ON ((r.periodid = pp.id)))
   JOIN processing_schedules ps ON ((pp.scheduleid = ps.id)))
   JOIN regimens ON ((r.programid = regimens.programid)));