
delete from configuration_settings where key = 'DASHBOARD_SLIDES_TRANSITION_INTERVAL_MILLISECOND';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('DASHBOARD_SLIDES_TRANSITION_INTERVAL_MILLISECOND',20000,'Dashboard slide transition interval in millisecond','Dashboard slide transition interval in millisecond','Dashboard','TEXT',true);
