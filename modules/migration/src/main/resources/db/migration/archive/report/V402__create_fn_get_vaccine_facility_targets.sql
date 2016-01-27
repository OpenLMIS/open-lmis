DROP FUNCTION if exists fn_get_vaccine_facility_targets(integer, integer);

CREATE OR REPLACE FUNCTION fn_get_vaccine_facility_targets(IN in_facilityid integer, IN in_periodid integer)
  RETURNS TABLE(rownum integer, targetyear integer, facilityid integer, month_0 integer, month_12 integer, pregnant integer, outreach integer) AS
$BODY$
DECLARE
thisQuery VARCHAR;
finalQuery            VARCHAR;
qrow                 RECORD;
rec RECORD;

v_month_0 integer;
v_month_12 integer;
v_pregnant integer;
v_outreach integer;

v_month_0_this_period integer;
v_month_12_this_period integer;
v_pregnant_this_period integer;
v_outreach_this_period integer;

v_month_0_cumulative integer;
v_month_12_cumulative integer;
v_pregnant_cumulative integer;
v_outreach_cumulative integer;

v_year integer;
v_month integer;


BEGIN

v_month_0 = 0;
v_month_12 = 0;
v_pregnant = 0;
v_outreach = 0;

v_month_0_this_period = 0;
v_month_12_this_period = 0;
v_pregnant_this_period = 0;
v_outreach_this_period = 0;

v_month_0_cumulative = 0;
v_month_12_cumulative = 0;
v_pregnant_cumulative = 0;
v_outreach_cumulative = 0;

select  extract(year from enddate) thisyear, extract(month from enddate) thismonth  into v_year, v_month from processing_periods where id = in_periodid;

EXECUTE 'CREATE TEMP TABLE _targets (
 rownum integer, targetyear integer, facilityid integer, month_0 integer, month_12 integer, pregnant integer, outreach integer
) ON COMMIT DROP';

thisQuery :=
'SELECT
targetyear,
facilityid,
targetpopulation,
expectedbirths,
expectedpregnancies,
pregnantwomen,
survinginfants,
children01,
children12,
adolocentgirls

FROM
vaccine_facility_targets

WHERE
targetyear = '|| v_year ||'
AND facilityid = '|| in_facilityid;

FOR qrow IN EXECUTE thisQuery
LOOP


v_month_0 = COALESCE(qrow.children01,0)::integer;
v_month_12 = COALESCE(qrow.children12,0)::integer;
v_pregnant = COALESCE(qrow.pregnantwomen,0)::integer;
v_outreach = COALESCE(qrow.adolocentgirls,0)::integer;


v_month_0_this_period  =  v_month_0  / 12::integer;
v_month_12_this_period =  v_month_0  / 12::integer;
v_pregnant_this_period =  v_pregnant / 12::integer;
v_outreach_this_period =  v_outreach / 12::integer;

for rec in 1..v_month
LOOP
v_month_0_cumulative =  v_month_0_cumulative + COALESCE(v_month_0_this_period,0);
v_month_12_cumulative = v_month_0_cumulative + COALESCE(v_month_12_this_period,0);
v_pregnant_cumulative = v_month_0_cumulative + COALESCE(v_pregnant_this_period,0);
v_outreach_cumulative = v_month_0_cumulative + COALESCE(v_outreach_this_period,0);

END LOOP;



EXECUTE
'INSERT INTO _targets VALUES (' ||
COALESCE(1,0) || ',' ||
COALESCE(v_year,0) || ',' ||
COALESCE(in_facilityid,0) || ',' ||
COALESCE(v_month_0,0) || ',' ||
COALESCE(v_month_12,0) || ',' ||
COALESCE(v_pregnant,0) || ',' ||
COALESCE(v_outreach,0) || ')';


EXECUTE
'INSERT INTO _targets VALUES (' ||
COALESCE(2,0) || ',' ||
COALESCE(v_year,0) || ',' ||
COALESCE(in_facilityid,0) || ',' ||
COALESCE(v_month_0_this_period,0) || ',' ||
COALESCE(v_month_12_this_period,0) || ',' ||
COALESCE(v_pregnant_this_period,0) || ',' ||
COALESCE(v_outreach_this_period,0) || ')';


EXECUTE
'INSERT INTO _targets VALUES (' ||
COALESCE(3,0) || ',' ||
COALESCE(v_year,0) || ',' ||
COALESCE(in_facilityid,0) || ',' ||
COALESCE(v_month_0_cumulative,0) || ',' ||
COALESCE(v_month_12_cumulative,0) || ',' ||
COALESCE(v_pregnant_cumulative,0) || ',' ||
COALESCE(v_outreach_cumulative,0) || ')';


END LOOP;

finalQuery := 'SELECT rownum, targetyear, facilityid, month_0, month_12, pregnant, outreach FROM  _targets';
RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_get_vaccine_facility_targets(integer, integer)
  OWNER TO postgres;
