-- Function: fn_get_geozonetree(integer)

-- DROP FUNCTION fn_get_geozonetree(integer);

CREATE OR REPLACE FUNCTION fn_get_geozonetree(IN in_facilityid integer)
  RETURNS TABLE(districtid integer, regionid integer, zoneid integer) AS
$BODY$
DECLARE

finalQuery            VARCHAR;

v_geographiczoneid integer;
v_districtid  integer;
v_regionid    integer;
v_zoneid      integer;

BEGIN

v_geographiczoneid = 0;
v_districtid = 0;
v_regionid = 0;
v_zoneid = 0;

select facilities.geographiczoneid into v_geographiczoneid from facilities where facilities.id = in_facilityid;

if coalesce(v_geographiczoneid, 0)  <> 0 THEN
 --select geographic_zones.id into v_districtid from geographic_zones where geographic_zones.id = v_geographiczoneid;
v_districtid = v_geographiczoneid;
end if;

if coalesce(v_districtid, 0)  <> 0 THEN
 select geographic_zones.parentid into v_regionid from geographic_zones where geographic_zones.id = v_districtid;
end if;

if coalesce(v_regionid, 0)  <> 0 THEN
 select geographic_zones.parentid into v_zoneid from geographic_zones where geographic_zones.id = v_regionid;
end if;


finalQuery := 'SELECT '|| v_districtid ||','||v_regionid||','||v_zoneid;

RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_get_geozonetree(integer)
  OWNER TO postgres;

