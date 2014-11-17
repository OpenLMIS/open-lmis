UPDATE master_rnr_columns
set calculationOption = '[{"name":"Default", "id":"DEFAULT"},{"name":"Accept in Packs","id":"ACCEPT_PACKS"}]'
where name = 'quantityApproved';