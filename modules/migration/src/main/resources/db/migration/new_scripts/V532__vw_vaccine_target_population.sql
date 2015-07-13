-- View: vw_vaccine_target_population

DROP VIEW IF EXISTS vw_vaccine_target_population;

CREATE OR REPLACE VIEW vw_vaccine_target_population AS 
 SELECT a.facility_id,
    a.value AS population,
    b.value AS pregnant_woman,
    c.value AS live_birth,
    d.value AS children_0_1,
    e.value AS children_1_2,
    f.value AS girls_9_12
   FROM vw_vaccine_estimates a
     JOIN vw_vaccine_estimates b ON a.facility_id = b.facility_id
     JOIN vw_vaccine_estimates c ON b.facility_id = c.facility_id
     JOIN vw_vaccine_estimates d ON c.facility_id = d.facility_id
     JOIN vw_vaccine_estimates e ON d.facility_id = e.facility_id
     JOIN vw_vaccine_estimates f ON e.facility_id = f.facility_id
  WHERE a.category_name::text = 'Population'::text AND b.category_name::text = 'Pregnant Woman'::text AND c.category_name::text = 'Live Birth'::text AND d.category_name::text = 'Children 0 - 1 Year'::text AND e.category_name::text = 'Children 1 - 2 Years'::text AND f.category_name::text = 'Girls 9 - 12 Years'::text;

ALTER TABLE vw_vaccine_target_population
  OWNER TO postgres;
