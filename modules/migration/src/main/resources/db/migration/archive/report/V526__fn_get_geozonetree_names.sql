
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


/* This function calls fn_get_geozonetree_names(id) for each leaf-geozone that exists.
   It returns an ordered array of geo-zone names, along with the id of each leaf geo-zone. */
DROP TYPE IF EXISTS geozonetree_names_type CASCADE;
CREATE TYPE geozonetree_names_type AS (hierarchy varchar[], leafid integer);
CREATE FUNCTION fn_get_geozonetree_names()
  RETURNS setof geozonetree_names_type AS
$BODY$
DECLARE
 idVal integer;
 zoneVal varchar[];
 gnt geozonetree_names_type;
BEGIN
	FOR idVal IN (SELECT id FROM geographic_zones WHERE levelid = (SELECT MAX(levelnumber) FROM geographic_levels)) LOOP
	    SELECT * INTO zoneVal FROM fn_get_geozonetree_names(idVal);
	    gnt.hierarchy = zoneVal;
	    gnt.leafid = idVal;
	    RETURN NEXT gnt;
	END LOOP;
END;
$BODY$
  LANGUAGE plpgsql;
ALTER FUNCTION fn_get_geozonetree_names()
  OWNER TO postgres;