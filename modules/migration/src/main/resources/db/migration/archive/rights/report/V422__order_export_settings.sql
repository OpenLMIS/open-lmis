
DELETE FROM configuration_settings WHERE key = 'LOCAL_ORDER_EXPORT_DIRECTORY';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype, displayOrder)
values('LOCAL_ORDER_EXPORT_DIRECTORY','./local-order-ftp-data','Local Directory To Export Order','Local Directory to Export Order','Order Export','TEXT', 4);

DELETE FROM configuration_settings WHERE key = 'USE_FTP_TO_SEND_ORDERS';
INSERT INTO configuration_settings(key, value, name, description, groupname, valuetype, displayOrder)
values('USE_FTP_TO_SEND_ORDERS','true','Use FTP to Send Orders','Use FTP To Send Orders','Order Export','BOOLEAN', 5);
