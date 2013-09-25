DELETE FROM configuration_settings WHERE key = 'DEFAULT_ZERO';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype)
values('DEFAULT_ZERO','false','Enable RnR to fill zero by default','Fill RnR with 0 values when RnR is initated.','GENERAL','BOOLEAN')