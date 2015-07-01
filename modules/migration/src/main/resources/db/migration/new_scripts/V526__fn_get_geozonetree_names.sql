
-- Given a geo-zone ID, this function returns an ordered array containing the geo-zone''s name along with the names of all of its ancestor geo-zones.

CREATE OR REPLACE FUNCTION fn_get_geozonetree_names(IN in_geozone_id integer)
  RETURNS varchar[] AS
$BODY$
BEGIN

RETURN(
	SELECT ARRAY
	(
		WITH RECURSIVE all_geo_zones AS (
		  SELECT  id, parentid, name
		    FROM geographic_zones
		    WHERE id = in_geozone_id
		  UNION
		  SELECT gz.id, gz.parentid, gz.name
		    FROM geographic_zones gz
		    JOIN all_geo_zones agz
		      ON (agz.parentid = gz.id)
		)
		SELECT name FROM all_geo_zones
	) AS geo_zone_hierarchy
);

END;
$BODY$
  LANGUAGE plpgsql;
ALTER FUNCTION fn_get_geozonetree_names(IN in_geozone_id integer)
  OWNER TO postgres;

