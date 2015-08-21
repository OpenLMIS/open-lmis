
DELETE FROM role_rights where roleId =(SELECT id from roles where name = 'New dashboard POC') and rightname = 'ACCESS_NEW_DASHBOARD';
DELETE FROM Rights where name='ACCESS_NEW_DASHBOARD';
INSERT INTO Rights values('ACCESS_NEW_DASHBOARD', 'REPORT', 'Permission to access new dashboard', NOW(), 13, 'right.dashboard');
delete from role_assignments where roleid = (SELECT id from roles where name = 'New dashboard POC' );
delete from roles where name = 'New dashboard POC';
INSERT INTO roles( name, description, createdby, createddate, modifiedby, modifieddate)
    VALUES ( 'New dashboard POC', 'Permission to access new dashboard', null,NOW(), null, NOW());

INSERT INTO role_rights(roleId, rightname, createdby,createddate)
values((SELECT id from roles where name = 'New dashboard POC' ),'ACCESS_NEW_DASHBOARD', null, NOW());

DO
$do$
BEGIN
IF EXISTS (select id from users where username = 'nidris') THEN
  INSERT INTO role_assignments(userid,roleid)
	values((select id from users where username ='nidris'), (SELECT id from roles where name = 'New dashboard POC' ));
END IF;
END
$do$
