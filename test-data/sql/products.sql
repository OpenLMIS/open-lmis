delete from facility_approved_product;
delete from program_product;
delete from product;
insert into product (code,alternate_item_code,manufacturer,manufacturer_code,manufacturer_barcode,moh_barcode,gtin,type,primary_name,full_name,generic_name,alternate_name,description,strength,form,dosage_unit,dispensing_unit,doses_per_dispensing_unit,doses_per_day,pack_size,alternate_pack_size,store_refrigerated,store_room_temperature,hazardous,flammable,controlled_substance,light_sensitive,approved_by_who,contraceptive_cyp,pack_length,pack_width,pack_height,pack_weight,packs_per_carton,carton_length,carton_width,carton_height,cartons_per_pallet,expected_shelf_life,special_storage_instructions,special_transport_instructions,active,full_supply,tracer,pack_rounding_threshold,round_to_zero,archived) values
('P100','a','Glaxo and Smith','a','a','a','a','antibiotic','antibiotic','TDF/FTC/EFV','TDF/FTC/EFV','TDF/FTC/EFV','TDF/FTC/EFV','300/200/600',2,1,'Strip',3,1,30,3,TRUE,TRUE,TRUE,TRUE,TRUE,TRUE,TRUE,2.2,2,2,2,2,2,2,2,2,2,2,'a','a',TRUE,TRUE,TRUE,1,FALSE,TRUE),
('P101','a','Glaxo and Smith','a','a','a','a','antibiotic','antibiotic','TDF/FTC/EFV','TDF/FTC/EFV','TDF/FTC/EFV','TDF/FTC/EFV','300/200/600',2,1,'Strip',3,1,30,3,TRUE,TRUE,TRUE,TRUE,TRUE,TRUE,TRUE,2.2,2,2,2,2,2,2,2,2,2,2,'a','a',TRUE,TRUE,TRUE,1,FALSE,TRUE),
('P102','a','Glaxo and Smith','a','a','a','a','antibiotic','antibiotic','TDF/FTC/EFV','TDF/FTC/EFV','TDF/FTC/EFV','TDF/FTC/EFV','300/200/600',2,1,'Strip',3,1,30,3,TRUE,TRUE,TRUE,TRUE,TRUE,TRUE,TRUE,2.2,2,2,2,2,2,2,2,2,2,2,'a','a',TRUE,TRUE,TRUE,1,FALSE,TRUE);
insert into program_product(program_code, product_code, active) values
('HIV', 'P100', true),
('HIV', 'P101', true),
('HIV', 'P102', true);
insert into facility_approved_product(facility_type_id, product_code) values
(1, 'P100'),
(1, 'P101'),
(1, 'P102');