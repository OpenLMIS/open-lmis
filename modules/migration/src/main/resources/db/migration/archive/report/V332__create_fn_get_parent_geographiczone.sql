-- Function: fn_dw_scheduler()

DROP FUNCTION IF EXISTS fn_get_parent_geographiczone(integer, integer);

-- Function: fn_get_parent_geographiczone(integer, integer)

CREATE OR REPLACE FUNCTION fn_get_parent_geographiczone(v_geographiczoneid integer, v_level integer)
  RETURNS character varying AS
$BODY$
DECLARE

v_highest_parent_id integer;
v_highest_parent_name geographic_zones.name%TYPE;

v_this_parent_id integer;
v_this_parent_name geographic_zones.name%TYPE;

v_current_parent_id integer;
v_current_parent_name geographic_zones.name%TYPE;

v_parent_geographizone_name geographic_zones.name%TYPE;


BEGIN

select id, name into v_highest_parent_id, v_highest_parent_name from geographic_zones where parentid is null; 
select parentid, name into v_this_parent_id, v_this_parent_name from geographic_zones where id = v_geographiczoneid;


IF (v_geographiczoneid = v_highest_parent_id) THEN
 v_parent_geographizone_name := v_highest_parent_name;
 RETURN v_parent_geographizone_name;
END IF; 


IF v_level = 0 THEN
 v_parent_geographizone_name = v_this_parent_name;
 RETURN v_parent_geographizone_name; 
END IF;


FOR i IN 1..v_level LOOP

select parentid,name into v_this_parent_id, v_parent_geographizone_name from geographic_zones where id = v_this_parent_id;

END LOOP;


v_parent_geographizone_name := coalesce(v_parent_geographizone_name, 'Unknown');

return v_parent_geographizone_name;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_parent_geographiczone(integer, integer)
  OWNER TO postgres;
