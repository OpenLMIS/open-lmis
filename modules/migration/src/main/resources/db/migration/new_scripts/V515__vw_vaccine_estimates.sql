-- View: vw_vaccine_estimates

DROP VIEW IF EXISTS vw_vaccine_estimates;

CREATE OR REPLACE VIEW vw_vaccine_estimates AS 
 SELECT facility_demographic_estimates.year,
    facilities.id AS facility_id,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    geographic_zones.name AS geographic_zone_name,
    geographic_zones.catchmentpopulation AS population,
    demographic_estimate_categories.name AS category_name,
    facility_demographic_estimates.demographicestimateid AS demographic_estimate_id,
    facility_demographic_estimates.conversionfactor AS converstion_factory,
    facility_demographic_estimates.value
   FROM demographic_estimate_categories
     JOIN facility_demographic_estimates ON facility_demographic_estimates.demographicestimateid = demographic_estimate_categories.id
     JOIN facilities ON facility_demographic_estimates.facilityid = facilities.id
     JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id;

ALTER TABLE vw_vaccine_estimates
  OWNER TO postgres;