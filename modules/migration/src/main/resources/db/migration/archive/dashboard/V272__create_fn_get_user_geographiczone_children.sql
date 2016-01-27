
DROP FUNCTION IF EXISTS fn_get_user_geographiczone_children(integer,integer);

CREATE OR REPLACE FUNCTION fn_get_user_geographiczone_children(in_userid integer, in_parentid integer)
RETURNS TABLE(geographiczoneid integer, levelid integer, parentid integer) AS

$BODY$
DECLARE

finalQuery            VARCHAR;
v_parents character varying;
v_current_parentid integer;
v_parentid integer;
v_result INTEGER = 0;

BEGIN

finalQuery :=
 'WITH  recursive  userGeographicZonesRec AS
          (SELECT *
          FROM geographic_zones 
          WHERE id = '||in_parentid||'
          UNION 
          SELECT gz.* 
          FROM geographic_zones gz 
          JOIN userGeographicZonesRec  ON gz.parentId = userGeographicZonesRec.id )          

          SELECT rec.id,rec.levelid,rec.parentid from userGeographicZonesRec rec
          INNER JOIN vw_user_geographic_zones uz on uz.geographiczoneid = rec.id 
         where userid = '||in_userid;

  RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
  ALTER FUNCTION fn_get_user_geographiczone_children(integer, integer) OWNER TO postgres;

