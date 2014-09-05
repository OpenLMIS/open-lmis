-- View: vw_user_districts

DROP VIEW IF EXISTS vw_user_districts;

CREATE OR REPLACE VIEW vw_user_districts AS 
 SELECT DISTINCT vw_user_facilities.user_id,
    vw_user_facilities.district_id,
    vw_user_facilities.program_id
   FROM vw_user_facilities;

ALTER TABLE vw_user_districts
  OWNER TO postgres;
