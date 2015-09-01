
/*
INSERT INTO Rights values('ACCESS_NEW_DASHBOARD', 'REPORT', 'Permission to access new dashboard', NOW(), 13, 'right.dashboard');
INSERT INTO roles( name, description, createdby, createddate, modifiedby, modifieddate)
    VALUES ( 'New dashboard POC', 'Permission to access new dashboard', null,NOW(), null, NOW());

INSERT INTO role_rights(roleId, rightname, createdby,createddate)
values((SELECT id from roles where name = 'New dashboard POC' ),'ACCESS_NEW_DASHBeOARD', null, NOW());

INSERT INTO role_assignments(userid,roleid)
values((select id from users where username ='nidris'), (SELECT id from roles where name = 'New dashboard POC' ));*/