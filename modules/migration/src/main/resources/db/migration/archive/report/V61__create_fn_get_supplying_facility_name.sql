-- Function: fn_get_supplying_facility_name(integer)
/*
2013-09-05 - change incoming agrument from requisitions.supplylineid to requisitions.supervisorynodeid
??? Muhammad Ahmed - created
*/

DROP FUNCTION IF EXISTS fn_get_supplying_facility_name(integer) CASCADE;
CREATE OR REPLACE FUNCTION fn_get_supplying_facility_name(v_supervisorynode_id integer)
  RETURNS character varying AS
$BODY$
DECLARE

v_supplying_facility_id integer;
v_supplying_facility_name facilities.name%TYPE;
BEGIN
select supplyingfacilityid into v_supplying_facility_id from supply_lines where supervisorynodeid = v_supervisorynode_id;
select name into v_supplying_facility_name from facilities where id =  v_supplying_facility_id;
v_supplying_facility_name = coalesce(v_supplying_facility_name, 'Unknown');
return v_supplying_facility_name;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_supplying_facility_name(integer)
  OWNER TO postgres;
