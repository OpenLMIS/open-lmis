delete from configuration_settings where key = 'PROGRAM_VIEWABLE_MAX_LAST_PERIODS';
insert into configuration_settings(key,value,name,description,groupname,valuetype,isconfigurable)
values('PROGRAM_VIEWABLE_MAX_LAST_PERIODS',4,'Program Viewable max last periods','Used to limit maximum number of last periods to show when program is selected on dashboard page','Dashboard','TEXT',true);
