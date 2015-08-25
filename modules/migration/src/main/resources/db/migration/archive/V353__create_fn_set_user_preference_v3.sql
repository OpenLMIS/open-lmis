-- Function: fn_set_user_preference(integer, character varying, character varying)

DROP FUNCTION IF EXISTS fn_set_user_preference(integer, character varying, character varying);

CREATE OR REPLACE FUNCTION fn_set_user_preference(in_userid integer, in_key character varying, in_value character varying)
  RETURNS character varying AS
$BODY$
DECLARE msg character varying(2000);
check_id integer;
productids int[];
i integer;
valid_list integer;

BEGIN

msg := 'ERROR';

IF in_key != ANY(ARRAY['DEFAULT_PROGRAM','DEFAULT_SCHEDULE','DEFAULT_PERIOD','DEFAULT_GEOGRAPHIC_ZONE','DEFAULT_FACILITY','DEFAULT_PRODUCTS']) THEN
 msg := 'Invalid key';
END IF;


if in_key = 'DEFAULT_PROGRAM' THEN
 select id into check_id from programs where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_PROGRAM' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PROGRAM', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid program value. Aborting';  
 end if;
end if;

if in_key = 'DEFAULT_SCHEDULE' THEN
 select id into check_id from processing_schedules where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_SCHEDULE' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_SCHEDULE', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid schedule value. Aborting';  
 end if;
end if;


if in_key = 'DEFAULT_PERIOD' THEN
 select id into check_id from processing_periods where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_PERIOD' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PERIOD', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid period value. Aborting';  
 end if;
end if;

if in_key = 'DEFAULT_GEOGRAPHIC_ZONE' THEN
 select id into check_id from geographic_zones where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_GEOGRAPHIC_ZONE', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid geographic zone value. Aborting';  
 end if; 
end if;

if in_key = 'DEFAULT_FACILITY' THEN
 select id into check_id from facilities where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_FACILITY' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_FACILITY', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid facility value. Aborting';  
 end if;

end if;

valid_list = 1;
if in_key = 'DEFAULT_PRODUCTS' THEN
 productids = '{'||in_value||'}';
FOREACH i IN ARRAY productids
LOOP 
 select id into check_id from products where id = i::int;
 check_id = COALESCE(check_id,0);
 if check_id = 0 then
  valid_list = 0;
 end if; 
END LOOP;

 if valid_list > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_PRODUCTS' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PRODUCTS', in_value, 2, now(), 2, now());

  delete from user_preferences where userpreferencekey = 'DEFAULT_PRODUCT' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PRODUCT', productids[1], 2, now(), 2, now());

  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid product values. Aborting';  
 end if;
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
