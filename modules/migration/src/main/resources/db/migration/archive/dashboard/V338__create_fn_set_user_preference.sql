-- Function: fn_set_user_preference()

DROP FUNCTION IF EXISTS fn_set_user_preference(integer, character varying, character varying);

CREATE OR REPLACE FUNCTION fn_set_user_preference(in_userid integer, in_key character varying, in_value character varying)
  RETURNS character varying AS
$BODY$
DECLARE msg character varying(2000);
BEGIN
msg := 'Procedure completed successfully.';

if in_key = 'DEFAULT_PROGRAM' THEN
  delete from user_preferences where userpreferencekey = 'DEFAULT_PROGRAM' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PROGRAM', in_value, 2, now(), 2, now());
end if;

if in_key = 'DEFAULT_SCHEDULE' THEN
  delete from user_preferences where userpreferencekey = 'DEFAULT_SCHEDULE' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_SCHEDULE', in_value, 2, now(), 2, now());
end if;


if in_key = 'DEFAULT_PERIOD' THEN
  delete from user_preferences where userpreferencekey = 'DEFAULT_PERIOD' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PERIOD', in_value, 2, now(), 2, now());
end if;

if in_key = 'DEFAULT_GEOGRAPHIC_ZONE' THEN
  delete from user_preferences where userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_GEOGRAPHIC_ZONE', in_value, 2, now(), 2, now());
end if;

if in_key = 'DEFAULT_FACILITY' THEN
  delete from user_preferences where userpreferencekey = 'DEFAULT_FACILITY' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_FACILITY', in_value, 2, now(), 2, now());
end if;


if in_key = 'DEFAULT_PRODUCT' THEN
  delete from user_preferences where userpreferencekey = 'DEFAULT_PRODUCT' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PRODUCT', in_value, 2, now(), 2, now());
end if;


if in_key = 'DEFAULT_PRODUCTS' THEN
  delete from user_preferences where userpreferencekey = 'DEFAULT_PRODUCTS' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PRODUCTS', in_value, 2, now(), 2, now());
end if;


RETURN msg;
EXCEPTION WHEN OTHERS THEN
return SQLERRM;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_set_user_preference(integer, character varying, character varying)
  OWNER TO postgres;
