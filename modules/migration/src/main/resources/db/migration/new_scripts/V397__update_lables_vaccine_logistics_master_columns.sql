update vaccine_logistics_master_columns set label = 'Issued / Used' where name = 'quantityIssued';
update vaccine_logistics_master_columns set label = 'Discarded Unopened' where name = 'quantityDiscardedUnopened';
update vaccine_logistics_master_columns set description = 'Issued / Used' where name = 'quantityIssued';
update vaccine_logistics_master_columns set description = 'Discarded Unopened' where name = 'quantityDiscardedUnopened';
update vaccine_program_logistics_columns set label = 'Issued / Used' where label ='Issued';
update vaccine_program_logistics_columns set label = 'Discarded Unopened' where label ='Discarded Un Opened';
