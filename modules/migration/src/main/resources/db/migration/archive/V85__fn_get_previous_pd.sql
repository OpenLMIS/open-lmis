
DROP FUNCTION IF EXISTS fn_previous_cb(integer, integer, character varying) CASCADE;

CREATE OR REPLACE FUNCTION fn_previous_pd(v_rnr_id integer, v_period_id integer, v_productcode character varying)
  RETURNS integer AS
$BODY$
DECLARE
v_ret integer;
v_prev_id integer;
v_rnr_id integer;
BEGIN
select id into v_rnr_id from requisitions where periodid < v_period_id order by periodid desc limit 1;
v_rnr_id = COALESCE(v_rnr_id,0);
if v_rnr_id > 0 then
select quantityreceived into v_ret from requisition_line_items where rnrid = v_rnr_id and productcode = v_productcode;
end if;
v_ret = COALESCE(v_ret,0);
return v_ret;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_previous_pd(integer, integer, character varying)
  OWNER TO postgres;
