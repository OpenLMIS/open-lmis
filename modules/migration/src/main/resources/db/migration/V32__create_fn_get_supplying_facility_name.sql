--This function return supplying facility name given the supplying facility id which comes from requisiotions table of Stock Imabalance query
-- Function: fn_get_supplying_facility_name(integer)

-- DROP FUNCTION fn_get_supplying_facility_name(integer);

CREATE OR REPLACE FUNCTION fn_get_supplying_facility_name(v_supplying_facility_id integer)
  RETURNS character varying AS
$BODY$
DECLARE

 v_supplying_facility_name facilities.name%TYPE;
     
BEGIN
   select name into v_supplying_facility_name from facilities where id =  v_supplying_facility_id;
   v_supplying_facility_name = coalesce(v_supplying_facility_name, 'Unknown');
   return v_supplying_facility_name;       
 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_supplying_facility_name(integer)
  OWNER TO postgres;