-- DROP FUNCTION fn_get_stocked_out_notification_details(anyelement,integer, integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_get_stocked_out_notification_details(_tbl_name anyelement,userId integer, programId integer,periodId integer,zoneId integer,productId integer)
  RETURNS SETOF anyelement AS
$BODY$
BEGIN

RETURN QUERY EXECUTE 'SELECT * FROM ' || pg_typeof(_tbl_name) ||
 ' where programId = '||programId ||' and periodId= '||periodId||' and productId= '||productId||
 'and geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children('||userId||', '||zoneId||'))';

END
$BODY$
  LANGUAGE plpgsql ;
ALTER FUNCTION fn_get_stocked_out_notification_details(anyelement,integer, integer, integer, integer, integer)
  OWNER TO postgres;
