delete from programs_supported;
delete from facility;

insert into facility (code,name,description,gln,main_phone,fax,address1,address2,geographic_zone_id,type_id,catchment_population,latitude,longitude,altitude,operated_by_id,cold_storage_gross_capacity,cold_storage_net_capacity,supplies_others,is_sdp, has_electricity, is_online, has_electronic_scc, has_electronic_dar,active, go_live_date, go_down_date,is_satellite, comment, data_reportable) values
('F1756','Village Dispensary','IT department','G7645',9876234981,'fax','A','B',1,1,333,22.1,1.2,3.3,2,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/1887','TRUE','fc','TRUE'),
('F1757','Central Hospital','IT department','G7646',9876234981,'fax','A','B',1,2,333,22.3,1.2,3.3,3,9.9,6.6,'TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','TRUE','11/11/12','11/11/2012','TRUE','fc','TRUE');

insert into programs_supported(facility_id, program_id, active, modified_by) values
(1, 1, true, 'Admin123'),
(1, 2, true, 'Admin123'),
(2, 1, true, 'Admin123'),
(2, 2, true, 'Admin123');