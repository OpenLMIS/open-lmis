-- Function: fn_get_notification_details(anyelement, integer)

-- DROP FUNCTION fn_get_notification_details(anyelement, integer);

CREATE OR REPLACE FUNCTION fn_get_notification_details(_tbl_name anyelement, id integer)
  RETURNS SETOF anyelement AS
$BODY$
BEGIN

RETURN QUERY EXECUTE 'SELECT * FROM ' || pg_typeof(_tbl_name) || ' where alertsummaryid = '||id;

END
$BODY$
  LANGUAGE plpgsql ;
ALTER FUNCTION fn_get_notification_details(anyelement, integer)
  OWNER TO postgres;
