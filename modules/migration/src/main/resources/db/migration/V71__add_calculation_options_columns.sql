ALTER TABLE master_rnr_columns 
ADD calculationOption varchar(200) default 'DEFAULT';

ALTER TABLE program_rnr_columns
ADD calculationOption VARCHAR(200) default 'DEFAULT';

UPDATE master_rnr_columns
set calculationOption = '[{"name":"Default", "id":"DEFAULT"},{"name":"Consumption x 2","id":"CONSUMPTION_X_2"}]'
where name = 'maxStockQuantity';
