-- Function: fn_get_user_default_settings(integer, integer)

DROP FUNCTION IF EXISTS fn_get_user_default_settings(integer, integer);

CREATE OR REPLACE FUNCTION fn_get_user_default_settings(IN in_programid integer, IN in_facilityid integer)
  RETURNS TABLE(programid integer, facilityid integer, scheduleid integer, periodid integer, geographiczoneid integer) AS
$BODY$
DECLARE

_query VARCHAR;
finalQuery            VARCHAR;
rowrec                 RECORD;
BEGIN

_query := 'SELECT
	programid, facilityid, scheduleid, periodid, geographiczoneid
FROM
	vw_expected_facilities
WHERE
	facilityid = ' || in_facilityid || ' 
AND programid = ' || in_programid || ' 
AND periodid IN (
	SELECT
		MAX (periodid) periodid
	FROM
		requisitions
	WHERE
		programid = ' || in_programid || ' 
	AND facilityid = '|| in_facilityid || '
)';

RETURN QUERY EXECUTE _query;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_get_user_default_settings(integer, integer)
  OWNER TO postgres;
