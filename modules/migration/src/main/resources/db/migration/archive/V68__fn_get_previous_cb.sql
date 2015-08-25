-- Function: fn_previous_cb(integer, character varying)
/*

2013-09-09 Muhammad Ahmed - created

*/
DROP FUNCTION IF EXISTS fn_previous_cb(integer, character varying) CASCADE;

CREATE OR REPLACE FUNCTION fn_previous_cb(v_rnr_id integer, v_productcode character varying)
  RETURNS integer AS
$BODY$
DECLARE
v_ret integer;
v_prev_id integer;

BEGIN

select stockinhand  into v_ret from requisition_line_items where id < v_rnr_id and productcode = v_productcode;
v_ret = COALESCE(v_ret,0);

return v_ret;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_previous_cb(integer, character varying)
  OWNER TO postgres;