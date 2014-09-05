-- View: vw_user_facilities
DROP VIEW IF EXISTS vw_user_districts;
DROP VIEW IF EXISTS vw_user_facilities;

CREATE OR REPLACE VIEW vw_user_facilities AS 
 SELECT mv_user_facilities.facility_id,
    mv_user_facilities.district_id,
    mv_user_facilities.requisition_group_id,
    mv_user_facilities.user_id,
    mv_user_facilities.program_id
   FROM mv_user_facilities;

ALTER TABLE vw_user_facilities
  OWNER TO postgres;
