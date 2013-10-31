-- Function: fn_previous_cb(integer, character varying)
/*
2013-10-31 Muhammad Ahmed - modified to fix error in open balance highlight
2013-09-09 Muhammad Ahmed - created

*/
CREATE OR REPLACE FUNCTION fn_previous_cb(v_program_id integer, v_facility_id integer, v_period_id integer, v_productcode character varying)
  RETURNS integer AS
$BODY$
DECLARE
v_ret integer;
v_prev_id integer;
v_rnr_id integer;
BEGIN

select id into v_rnr_id from requisitions where periodid < v_period_id and facilityid = v_facility_id and programid = v_program_id order by periodid desc limit 1;
v_rnr_id = COALESCE(v_rnr_id,0);

if v_rnr_id > 0 then
 select stockinhand into v_ret from requisition_line_items where rnrid = v_rnr_id and productcode = v_productcode;
end if;

v_ret = COALESCE(v_ret,0);
return v_ret;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_previous_cb(integer, integer, integer, character varying)
  OWNER TO postgres;
