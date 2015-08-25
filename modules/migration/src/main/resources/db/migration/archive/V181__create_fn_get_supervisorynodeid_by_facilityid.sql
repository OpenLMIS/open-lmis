DROP FUNCTION IF EXISTS fn_get_supervisorynodeid_by_facilityid(integer);

CREATE OR REPLACE FUNCTION fn_get_supervisorynodeid_by_facilityid(v_facilityid integer)
  RETURNS integer AS
$BODY$
DECLARE
v_ret integer;
BEGIN

SELECT
requisition_groups.supervisorynodeid into v_ret
FROM
requisition_groups
INNER JOIN requisition_group_members ON requisition_groups.id = requisition_group_members.requisitiongroupid
where requisition_group_members.facilityid = v_facilityid LIMIT 1;


return v_ret;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_supervisorynodeid_by_facilityid(integer)
  OWNER TO postgres;
