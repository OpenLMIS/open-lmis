-- Function: fn_populate_user_facilites()
DROP TRIGGER IF EXISTS tg_role_assigments_insert_update_delete ON role_assignments;
DROP FUNCTION IF EXISTS fn_populate_user_facilites();

CREATE OR REPLACE FUNCTION fn_populate_user_facilites()
  RETURNS trigger AS
$BODY$
BEGIN
    DELETE FROM mv_user_facilities where user_id = NEW.userid; 
   
    INSERT INTO mv_user_facilities 
     SELECT DISTINCT f.id AS facility_id, f.geographiczoneid AS district_id, 
    rg.id AS requisition_group_id, ra.userid AS user_id, 
    ra.programid AS program_id,
    NOW()
   FROM facilities f
   JOIN requisition_group_members m ON m.facilityid = f.id
   JOIN requisition_groups rg ON rg.id = m.requisitiongroupid
   JOIN supervisory_nodes sn ON sn.id = rg.supervisorynodeid
   JOIN role_assignments ra ON ra.supervisorynodeid = sn.id OR ra.supervisorynodeid = sn.parentid WHERE ra.userid = NEW.userid; 

 
    DELETE FROM mv_user_geographic_zones where userid = NEW.userid; 
   
    INSERT INTO mv_user_geographic_zones 
SELECT DISTINCT ra.userid, ra.supervisorynodeid, gz.id AS geographiczoneid, 
    gz.levelid, ra.programid,
    NOW()
   FROM facilities f
   JOIN geographic_zones gz ON gz.id = f.geographiczoneid
   JOIN requisition_group_members m ON m.facilityid = f.id
   JOIN requisition_groups rg ON rg.id = m.requisitiongroupid
   JOIN supervisory_nodes sn ON sn.id = rg.supervisorynodeid
   JOIN role_assignments ra ON ra.supervisorynodeid = sn.id OR ra.supervisorynodeid = sn.parentid
   JOIN geographic_zones d ON d.id = f.geographiczoneid
  WHERE ra.supervisorynodeid IS NOT NULL AND rg.supervisorynodeid IS NOT NULL AND ra.userid = NEW.userid; 
   
    RETURN NEW;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_user_facilites()
  OWNER TO postgres;
  

CREATE TRIGGER tg_role_assigments_insert_update_delete
  AFTER INSERT OR UPDATE OR DELETE
  ON role_assignments
  FOR EACH ROW
  EXECUTE PROCEDURE fn_populate_user_facilites();
  
