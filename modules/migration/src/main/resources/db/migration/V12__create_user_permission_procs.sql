-- View: vw_user_program_facilities

-- DROP VIEW vw_user_program_facilities;

CREATE OR REPLACE VIEW vw_user_program_facilities AS
 SELECT DISTINCT users.id AS user_id, role_assignments.roleid AS role_id,
    requisition_groups.code AS rg_code, requisition_groups.name AS rg_name,
    requisition_groups.id AS rg_id, users.username,
    role_assignments.supervisorynodeid, programs.id AS program_id,
    programs.code AS program_code, facilities.id AS facility_id,
    facilities.code AS facility_code
   FROM programs
   JOIN role_assignments ON programs.id = role_assignments.programid
   JOIN users ON role_assignments.userid = users.id
   JOIN requisition_group_program_schedules ON programs.id = requisition_group_program_schedules.programid
   JOIN requisition_groups ON requisition_groups.id = requisition_group_program_schedules.requisitiongroupid
   JOIN requisition_group_members ON requisition_groups.id = requisition_group_members.requisitiongroupid
   JOIN facilities ON facilities.id = requisition_group_members.facilityid;

ALTER TABLE vw_user_program_facilities
  OWNER TO postgres;
COMMENT ON VIEW vw_user_program_facilities
  IS 'This view combines information from users, user_assignments, programs, facilities. This is used in user related stored functions. If using directly, please use DISTINCT ON to get distrinct list';

-------------------------------------------------------------------------
-- View: vw_user_role_program_rg

-- DROP VIEW vw_user_role_program_rg;

CREATE OR REPLACE VIEW vw_user_role_program_rg AS
 SELECT DISTINCT users.id AS user_id, requisition_groups.code AS rg_code,
    requisition_groups.name AS rg_name, requisition_groups.id AS rg_id,
    users.username, role_assignments.supervisorynodeid, roles.id AS role_id,
    programs.id AS program_id, programs.code AS program_code
   FROM requisition_group_program_schedules
   JOIN programs ON requisition_group_program_schedules.scheduleid = programs.id
   JOIN requisition_groups ON requisition_group_program_schedules.programid = requisition_groups.id
   JOIN role_assignments ON programs.id = role_assignments.programid
   JOIN roles ON role_assignments.roleid = roles.id
   JOIN users ON role_assignments.userid = users.id;

ALTER TABLE vw_user_role_program_rg
  OWNER TO postgres;
COMMENT ON VIEW vw_user_role_program_rg
  IS 'This view combines information from user, role, role_assignment, program, requisition_group. This view is used in user related stored function. If using directly, make sure you use DISTINCT ON';

-----------------------------------------------------------------

-- Function: fn_tbl_user_attributes(integer, character varying, integer, text)
--DROP FUNCTION fn_tbl_user_attributes(integer, character varying, integer, text);

CREATE OR REPLACE FUNCTION fn_tbl_user_attributes(in_user_id integer DEFAULT NULL::integer, in_user_name character varying DEFAULT NULL::character varying, in_program_id integer DEFAULT NULL::integer, in_output text DEFAULT NULL::text)
  RETURNS text AS
$BODY$
DECLARE
/*
fn_tbl_user_attributes - This function returns user attributes like
requisition groups, programs, supervisornode and facilities one at a time.

Input:
 in_user_id - user id
 in_username - username
 in in_program_id - program id
 in_output - special code for output to return
           RGID - list of requisition group ids
           RGCODE - list of requistion group code (single quoted)
           SNODE - supervisor node ids
           FACCODE - list of facility code
           FACid - list of facility ids
 Output: comma-delimited list

Example Call-
 SELECT fn_tbl_user_attributes(16,null,1,'RGID');
 SELECT fn_tbl_user_attributes(null,'Elias',1,'RGID');
 SELECT fn_tbl_user_attributes(16,null,1,'SNODE');
 SELECT fn_tbl_user_attributes(16,null,1,'FACID');
 SELECT fn_tbl_user_attributes(16,null,1,'FACCODE');

TODO: Find a a way to pass parameters by name
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/

  -- user requisition groups
  rg_cursor CURSOR FOR
  SELECT distinct on (user_id,rg_id) user_id, rg_id, rg_code, role_id
  FROM vw_user_role_program_rg
  where (user_id = in_user_id or in_user_id is null)
  and (username = in_user_name or in_user_name is null)
  and (program_id = in_program_id or in_program_id is null);

  -- user facilities
  fac_cursor CURSOR FOR
  SELECT distinct on (user_id,facility_id) user_id, facility_id, facility_code, role_id
  FROM vw_user_program_facilities
  where (user_id = in_user_id or in_user_id is null)
  and (username = in_user_name or in_user_name is null)
  and (program_id = in_program_id or in_program_id is null);

  -- admin user
  user_cursor CURSOR FOR
  SELECT role_assignments.roleid
  FROM  users
  INNER JOIN role_assignments ON role_assignments.userid = users.id
  where (users.id = in_user_id or in_user_id is null)
    and (users.username = in_user_name or in_user_name is null)
    and role_assignments.roleid = 1;


rec RECORD;
delim character(1);
ret_val TEXT;

BEGIN
--
delim = '';
ret_val = '';

open user_cursor;
FETCH user_cursor INTO rec;
IF FOUND THEN
 ret_val = '*';
 RETURN ret_val;
end if;
close user_cursor;


-- check the output request
IF upper(in_output) = 'FACCODE' OR upper(in_output) = 'FACID' THEN
-- facility information requested
OPEN fac_cursor;
  LOOP
    FETCH fac_cursor INTO rec;
    EXIT WHEN NOT FOUND;

if upper(in_output) = 'FACID' THEN
 ret_val = ret_val || delim ||rec.facility_id;
elsif upper(in_output) = 'FACCODe' THEN
 ret_val = ret_val || delim ||chr(39)||rec.facility_code||chr(39);
else
 ret_val = '';
END IF;

delim = ',';

  END LOOP;
  CLOSE fac_cursor;

ELSIF upper(in_output) = 'RGID' OR upper(in_output) = 'RGCODE' OR upper(in_output) = 'SNODE' THEN

OPEN rg_cursor;
  LOOP
    FETCH rg_cursor INTO rec;
    EXIT WHEN NOT FOUND;

if upper(in_output) = 'RGID' THEN
 ret_val = ret_val || delim ||rec.rg_id;
elsif upper(in_output) = 'RGCODE' THEN
 ret_val = ret_val || delim ||chr(39)||rec.rg_code||chr(39);
elsif upper(in_output) = 'SNODE' THEN
ret_val = ret_val || delim ||rec.supervisorynodeid;
else
 ret_val = '';
END IF;

delim = ',';

  END LOOP;
  CLOSE rg_cursor;
END IF;


ret_val = coalesce(ret_val, 'none');

RETURN ret_val;
EXCEPTION WHEN OTHERS THEN RETURN SQLERRM;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_tbl_user_attributes(integer, character varying, integer, text)
  OWNER TO postgres;
