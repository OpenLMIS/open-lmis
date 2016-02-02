-- View: vw_vaccine_target_population

DROP VIEW if exists vw_vaccine_target_population;

CREATE OR REPLACE VIEW vw_vaccine_target_population AS 
select
e.year,
facilityid facility_id,
f.geographiczoneid geographic_zone_id, 
c.id category_id,
c.name category_name, 
value target_value_annual,
round(value/12) target_value_monthly
from demographic_estimate_categories c
left join facility_demographic_estimates e on c.ID = e.demographicestimateid
join facilities f on e.facilityid = f.id;
