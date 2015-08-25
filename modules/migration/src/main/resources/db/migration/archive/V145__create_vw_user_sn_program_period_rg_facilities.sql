DROP VIEW IF EXISTS vw_user_supervisorynodes;

CREATE OR REPLACE VIEW vw_user_supervisorynodes AS 
 WITH RECURSIVE supervisorynodesrec AS (
                 SELECT DISTINCT ra.userid,
                    s.id,
                    s.parentid,
                    s.facilityid,
                    s.name,
                    s.code,
                    s.description,
                    s.createdby,
                    s.createddate,
                    s.modifiedby,
                    s.modifieddate
                   FROM supervisory_nodes s
              JOIN role_assignments ra ON s.id = ra.supervisorynodeid
        UNION
                 SELECT supervisorynodesrec_1.userid,
                    sn.id,
                    sn.parentid,
                    sn.facilityid,
                    sn.name,
                    sn.code,
                    sn.description,
                    sn.createdby,
                    sn.createddate,
                    sn.modifiedby,
                    sn.modifieddate
                   FROM supervisory_nodes sn
              JOIN supervisorynodesrec supervisorynodesrec_1 ON sn.parentid = supervisorynodesrec_1.id
        )
 SELECT supervisorynodesrec.userid,
    supervisorynodesrec.id,
    supervisorynodesrec.parentid,
    supervisorynodesrec.facilityid,
    supervisorynodesrec.name,
    supervisorynodesrec.code,
    supervisorynodesrec.description,
    supervisorynodesrec.createdby,
    supervisorynodesrec.createddate,
    supervisorynodesrec.modifiedby,
    supervisorynodesrec.modifieddate
   FROM supervisorynodesrec;

ALTER TABLE vw_user_supervisorynodes
  OWNER TO postgres;
