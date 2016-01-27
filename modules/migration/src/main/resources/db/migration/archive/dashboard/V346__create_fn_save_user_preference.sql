-- Function: fn_save_user_preference(integer, integer, integer, character varying)

DROP FUNCTION IF EXISTS fn_save_user_preference(integer, integer, integer, character varying);

CREATE OR REPLACE FUNCTION fn_save_user_preference(in_userid integer, in_programid integer, in_facilityid integer, in_productid character varying)
  RETURNS character varying AS
$BODY$
DECLARE msg character varying(2000);
DECLARE msg2 character varying(2000);
v_scheduleid integer;
v_periodid integer;
v_zoneid integer;


BEGIN

msg := 'ERROR';

select  u.scheduleid, u.periodid, u.geographiczoneid into v_scheduleid, v_periodid, v_zoneid from fn_get_user_default_settings(in_userid,in_facilityid) u;
msg := fn_set_user_preference(in_userid, 'DEFAULT_PROGRAM', in_programid::text);
msg := fn_set_user_preference(in_userid, 'DEFAULT_SCHEDULE', v_scheduleid::text);
msg := fn_set_user_preference(in_userid, 'DEFAULT_PERIOD',   v_periodid::text);
msg := fn_set_user_preference(in_userid, 'DEFAULT_GEOGRAPHIC_ZONE',  v_zoneid::text);
msg := fn_set_user_preference(in_userid, 'DEFAULT_FACILITY',  in_facilityid::text);
msg := fn_set_user_preference(in_userid, 'DEFAULT_PRODUCTS',  in_productid::text);

RETURN msg;
EXCEPTION WHEN OTHERS THEN
return SQLERRM;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_save_user_preference(integer, integer, integer, character varying)
  OWNER TO postgres;
