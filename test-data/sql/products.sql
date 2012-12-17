delete from facility_approved_products;
delete from program_products;
delete from product;

insert into product
(code,  alternate_item_code,  manufacturer,     manufacturer_code,  manufacturer_barcode,   moh_barcode,  gtin,   type,         primary_name,   full_name,      generic_name,   alternate_name,   description,    strength,     form_id,  dosage_unit_id, dispensing_unit,  doses_per_dispensing_unit,  pack_size,  alternate_pack_size,  store_refrigerated,   store_room_temperature,   hazardous,  flammable,   controlled_substance,  light_sensitive,  approved_by_who,  contraceptive_cyp,  pack_length,  pack_width, pack_height,  pack_weight,  packs_per_carton, carton_length,  carton_width,   carton_height, cartons_per_pallet,  expected_shelf_life,  special_storage_instructions, special_transport_instructions, active, full_supply,  tracer,   pack_rounding_threshold,  round_to_zero,  archived) values
('P100',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                           1,        30,                   TRUE,                  TRUE,                    TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,                  2.2,            2,          2,            2,            2,                2,              2,              2,              2,                    2,                    'a',                          'a',                        TRUE,     TRUE,       TRUE,         1,                    FALSE,          TRUE),
('P101',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                           1,        30,                   TRUE,                  TRUE,                    TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,                  2.2,            2,          2,            2,            2,                2,              2,              2,              2,                    2,                    'a',                          'a',                        TRUE,     TRUE,       TRUE,         1,                    FALSE,          TRUE),
('P102',  'a',                'Glaxo and Smith',  'a',              'a',                    'a',          'a',    'antibiotic', 'antibiotic',   'TDF/FTC/EFV',  'TDF/FTC/EFV',  'TDF/FTC/EFV',    'TDF/FTC/EFV',  '300/200/600',  2,        1,            'Strip',           10,                           1,        30,                   TRUE,                  TRUE,                    TRUE,       TRUE,         TRUE,                 TRUE,             TRUE,               1,                  2.2,            2,          2,            2,            2,                2,              2,              2,              2,                    2,                    'a',                          'a',                        TRUE,     TRUE,       TRUE,         1,                    FALSE,          TRUE);

insert into program_products(programId, productId, dosesPerMonth, active) values
(1, 1, 30, true),
(1, 2, 30, true),
(1, 3, 30, true);

insert into facility_approved_products(facilityTypeId, productId, maxMonthsOfStock) values
(1, 1, 3),
(1, 2, 3),
(1, 3, 3);