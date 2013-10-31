UPDATE master_rnr_columns
set calculationOption = '[{"name":"Default (Users not allowed to override beginning balance) ", "id":"DEFAULT"},{"name":"Allow users to override beginning balance","id":"FORCE_DATA_ENTRY"}]'
where name = 'beginningBalance';