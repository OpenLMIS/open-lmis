-- Function: fn_get_program_product_id(integer, integer)

DROP FUNCTION if exists fn_get_vaccine_coverage_denominator(integer, integer, integer, integer, integer);
CREATE OR REPLACE FUNCTION fn_get_vaccine_coverage_denominator(
 in_program integer, 
 in_facility integer, 
 in_year integer, 
 in_product integer, 
 in_dose integer)
 
 RETURNS integer AS
$BODY$
DECLARE
v_denominator integer;
v_year integer;
v_target_value integer;
BEGIN

select d.denominatorestimatecategoryid into v_denominator from vaccine_product_doses d 
where programid = in_program 
and productid = in_product 
and doseid = in_dose;
v_denominator = COALESCE(v_denominator,0);

select round(value/12) into v_target_value from facility_demographic_estimates 
 where year = in_year 
 and facilityid = in_facility
 and demographicestimateid = v_denominator;

v_target_value = COALESCE(v_target_value,0);

return v_target_value;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

