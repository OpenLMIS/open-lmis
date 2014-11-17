DROP VIEW IF EXISTS vw_user_role_assignments;

CREATE OR REPLACE VIEW vw_user_role_assignments AS
 SELECT users.firstname, users.lastname, users.email, users.cellphone, users.officephone, supervisory_nodes.name AS supervisorynodename, programs.name AS programname, roles.name AS rolename, programs.id AS programid, supervisory_nodes.id AS supervisorynodeid, roles.id AS roleid
   FROM roles
   JOIN role_assignments ON roles.id = role_assignments.roleid
   JOIN programs ON programs.id = role_assignments.programid
   JOIN supervisory_nodes ON supervisory_nodes.id = role_assignments.supervisorynodeid
   JOIN users ON users.id = role_assignments.userid;

ALTER TABLE vw_user_role_assignments
  OWNER TO postgres;

